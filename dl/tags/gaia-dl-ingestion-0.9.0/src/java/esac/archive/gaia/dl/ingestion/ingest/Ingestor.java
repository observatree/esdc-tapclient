/*******************************************************************************
 * Copyright (C) 2017 European Space Agency
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package esac.archive.gaia.dl.ingestion.ingest;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.google.common.base.CaseFormat;

import uk.ac.starlink.table.TableBuilder;
import esac.archive.gaia.dl.ingestion.db.JDBCPoolSingleton;
import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.ingest.extractors.ExtractSource;
import esac.archive.gaia.dl.ingestion.main.AuxiliaryFunctions;
import esac.archive.gaia.dl.ingestion.objectconverter.IObjectConverter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLDocumentationExport;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLIndex;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLParameter;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLPrimaryKeyParameter;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLTable;
import gaia.cu1.tools.dm.GaiaRoot;

public class Ingestor {

	private static final Logger logger = Logger.getLogger(Ingestor.class.getName());
	private static ExecutorService taskExecutor = null;

	//for gbin
	public static void ingest(
			String gbin_xmlfile,
			boolean createTable,
			IOFileFilter filter,
			Class<?> extractedClass, 
			IObjectConverter<GaiaRoot, ?> conversorClass, 
			ExtractSource extractor, 
			IFilter<GaiaRoot> fil,
			String sourcePath,
			String schema,
			String dataModelTableName,
			Integer threads,
			IZeroPoints zPoints,
			boolean doPostIngestion,
			boolean autoRaDecIndex){	

		XMLTable t = null;
		try{

			String tableName = AuxiliaryFunctions.calculateTableName(dataModelTableName);
			if (tableName == null) {
				logger.log(Level.SEVERE, "Error reading table name in xml");
				System.exit(1);
			}
			XMLDocumentationExport export = null;
			try {
				export = AuxiliaryFunctions.readXMLInfo(gbin_xmlfile);
				for (XMLTable tab : export.getTables()) {
					if (tab.getName().equals(dataModelTableName)) {
						System.out.println(tab.getName());
						t = tab;
						break;
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Exception reading xml: ", e.getMessage());
				System.exit(1);
			}

			if (t == null) {
				logger.log(Level.SEVERE, "Exception reading table: ", dataModelTableName);
				System.exit(1);
			}
			if (createTable) {
				createGbinTable(t, gbin_xmlfile, schema, tableName, dataModelTableName);
			}
			
			Collection<File> files = FileUtils.listFiles(new File(sourcePath), filter, TrueFileFilter.INSTANCE);

			//Create tasks for each file
			List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
			for (File file : files) {
				logger.log(Level.INFO, "Found "+ file.getAbsolutePath());
				tasks.add(new IngestorThread(file, schema, tableName, extractor, extractedClass, conversorClass, fil, zPoints));
			}
			logger.log(Level.INFO, "Identified "+tasks.size()+" files for a pool of "+threads+" threads");
			//Run tasks in a fixed thread pool
			taskExecutor = Executors.newFixedThreadPool(threads);
			// invokeAll() returns when all tasks are complete
			List<Future<Integer>> futures = taskExecutor.invokeAll(tasks);

			int flag = 0;
			for (Future<Integer> f : futures) {
				Integer res = f.get();
				logger.log(Level.INFO, "Thread finished and synchronized ingesting "+res+" sources");
				if (!f.isDone()){
					flag = 1;
					logger.log(Level.INFO, "Thread finished with no sources ingested");
				}
			}
			
			if (flag == 0){
				logger.log(Level.INFO, "INGESTION SUCCEEDED");
				if (t != null && doPostIngestion) {
					PostIngestion.createAlterPk(t, schema);
					PostIngestion.createAlterIndexes(t, schema, autoRaDecIndex, threads);
				}
			} else {
				logger.log(Level.SEVERE, "INGESTION FAILED");
			}

		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Exception in the execution pool", e);
		} catch (ExecutionException e) {
			logger.log(Level.SEVERE, "Exception in the execution pool", e);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in the execution pool", e);
		}finally{
			if(taskExecutor!=null){
				taskExecutor.shutdown();
			}
		}
	}

	private static void createGbinTable(XMLTable table, String xmlFile, String schema, String tableName, String dataModelTableName) {		

		if (table == null || table.getParameterList() == null || table.getParameterList().getParameters() == null) {
			logger.log(Level.SEVERE, "Error when reading xml file '" + xmlFile + "' to create table '" + tableName + "'");
			System.exit(1);
		}
		
		//begin work with table
		Connection con = null;
		try {
			con = JDBCPoolSingleton.getInstance().getConnection();
			con.setAutoCommit(false);
		} catch (SQLException | PropertyVetoException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		String columns = "";
		
		for (XMLParameter param : table.getParameterList().getParameters()) {
			String parsedType = AuxiliaryFunctions.parseType(param.getType());
			if (parsedType == null){
				logger.log(Level.SEVERE, "Type '" + param.getType() + "' of parameter '" + param.getName() + "' not valid");
				System.exit(1);
			}
			columns += CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, param.getName()) + " " + parsedType + ",";
		}
		
		//remove last comma
		columns = columns.replaceAll(",$", "");

		String schema_plus_table = schema + "." + tableName;
		String sql_create = "CREATE TABLE "+schema_plus_table+ "("+columns+")";
		Statement stmt = null;
		
		try {
			stmt = con.createStatement();
			stmt.execute(sql_create);
			con.commit();

		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Exception when commit create table: " + e.getMessage());
			System.exit(1);
		}
		
		return;
	}
	
	//for stilts
	public static void ingest(
			boolean createTable,
			IOFileFilter filter,
			TableBuilder stiltsBuilder,
			ExtractSource extractor, 
			String sourcePath,
			String schema,
			String tableName,
			Integer threads){	

		try{

			//Create tasks for each file
			List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
			for (File file : /*new File(sourcePath).list(filter)*/ FileUtils.listFiles(new File(sourcePath), filter, TrueFileFilter.INSTANCE)) {
				logger.log(Level.INFO, "Found "+ file.getAbsolutePath());
				tasks.add(new IngestorThread(createTable, file, schema, tableName, extractor, stiltsBuilder));
			}
			logger.log(Level.INFO, "Identified "+tasks.size()+" files for a pool of "+threads+" threads");
			//Run tasks in a fixed thread pool
			taskExecutor = Executors.newFixedThreadPool(threads);
			// invokeAll() returns when all tasks are complete
			List<Future<Integer>> futures = taskExecutor.invokeAll(tasks);

			int flag = 0;
			for (Future<Integer> f : futures) {
				Integer res = f.get();
				logger.log(Level.INFO, "Thread finished and synchronized ingesting "+res+" sources");
				if (!f.isDone()){
					flag = 1;
					logger.log(Level.INFO, "Thread finished with no sources ingested");
				}
			}
			
			if (flag == 0){
				logger.log(Level.INFO, "INGESTION SUCCEEDED");
			} else {
				logger.log(Level.SEVERE, "INGESTION FAILED");
			}

		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Exception in the execution pool", e);
		} catch (ExecutionException e) {
			logger.log(Level.SEVERE, "Exception in the execution pool", e);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in the execution pool", e);
		}finally{
			if(taskExecutor!=null){
				taskExecutor.shutdown();
			}
		}
	}

}
