/*******************************************************************************
 * Copyright (C) 2017 rgutierrez
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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.CaseFormat;

import esac.archive.gaia.dl.ingestion.db.JDBCPoolSingleton;
import esac.archive.gaia.dl.ingestion.main.AuxiliaryFunctions;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLIndex;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLIndexParameter;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLPrimaryKeyParameter;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLTable;

public class PostIngestion {
	private static final Logger logger = Logger.getLogger(PostIngestion.class.getName());
	private static ExecutorService taskExecutor = null;

	public static void createAlterIndexes(XMLTable t, String schema, boolean autoRaDecIndex, int threads) {
		List<String> indexes = createIndexes(t, autoRaDecIndex, schema);
		if (indexes != null) {
			try {
				
				//Create tasks for each index
				List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();

				int i = 0;
				for (String index : indexes) {
					JDBCPoolSingleton.addInstanceToPool();
					tasks.add(new PostIngestorThread(JDBCPoolSingleton.getInstanceInPool(i++), index));
				}
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
					logger.log(Level.INFO, "POST INGESTION SUCCEEDED");
				} else {
					logger.log(Level.SEVERE, "POST INGESTION FAILED");
				}

				
			} catch (PropertyVetoException | InterruptedException | ExecutionException e) {
				logger.log(Level.SEVERE, "Exception when commit alter table to include indexes: " + e.getMessage());
				System.exit(1);
			}finally{
				if(taskExecutor!=null){
					taskExecutor.shutdown();
				}
			}

		}
	}

	public static List<String> createIndexes(XMLTable t, boolean autoRaDecIndex, String schema) {

		List<String> indexes = new ArrayList<String>();
		String alter = "";
		if (t != null && t.getName() != null && t.getIndexesList() != null && t.getIndexesList().getIndexElements() != null) {
			
			String tableName = AuxiliaryFunctions.transformName(AuxiliaryFunctions.calculateTableName(t.getName()));
			
			if (tableName != null) {
				if (autoRaDecIndex) {
					alter = AuxiliaryFunctions.generateRaDecIndex(t, schema);
					if (alter != null) {
						indexes.add(alter);
					}
				}
				
				boolean raDecFound = false;
				List<String> radec = AuxiliaryFunctions.getRaDec(t);
				String ra = "";
				String dec = "";
				if (radec != null && radec.size() == 2) {
					ra = radec.get(0);
					dec = radec.get(1);
					raDecFound = true;
				}

				for (XMLIndex i : t.getIndexesList().getIndexElements()) {
					alter = "";
					if (i.getParameters() == null || i.getParameters().getParameters() == null) continue;
					String ids = calculateIndexColumnList(i.getParameters().getParameters());
					if (raDecFound && AuxiliaryFunctions.isRaDecIndex(t, i, ra, dec)) {
						if (!autoRaDecIndex) {
							alter = "create index on " + schema + "." + tableName + " using btree (q3c_ang2ipix(" + AuxiliaryFunctions.transformName(ids) + "))";
						}
					} else {
						alter = "create index on " + schema + "." + tableName + " using btree (" + AuxiliaryFunctions.transformName(ids) + ")";
					}
					indexes.add(alter);
				}
			} else {
				logger.log(Level.SEVERE, "Error preparing alter query. Either table name or pk name or parameters list are formed wrongly");
				System.exit(1);
			}
		}
		else {
			logger.log(Level.SEVERE, "Error preparing alter query. Indexes info in xml is formed wrongly");
			System.exit(1);
		}
		return indexes;

	}

	public static void createAlterPk(XMLTable t, String schema) {
		String alter = createPk(t, schema);
		if (alter != null) {
			Connection con = null;
			try {
				con = JDBCPoolSingleton.getInstance().getConnection();
				con.setAutoCommit(false);
			} catch (SQLException | PropertyVetoException e1) {
				logger.log(Level.SEVERE, "Exception when opening connection: " + e1.getMessage());
				System.exit(1);
			}
			Statement stmt = null;
			
			try {
				stmt = con.createStatement();
				stmt.execute(alter);
				con.commit();

			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Exception when commit alter table to include primary key: " + e.getMessage());
				System.exit(1);
			}

		}
	}

	public static String createPk(XMLTable t, String schema) {
		/**
		 * ALTER TABLE table_name ADD CONSTRAINT MyPrimaryKey PRIMARY KEY (column1, column2...);
		 */
		String alter = "";
		if (t != null && t.getName() != null && t.getPk() != null && t.getPk().getParameters() != null && t.getPk().getParameters().getParameters() != null) {
			if (t.getPk().getParameters().getParameters().size() > 0) {
				String tableName = AuxiliaryFunctions.transformName(AuxiliaryFunctions.calculateTableName(t.getName()));
				String pkName = AuxiliaryFunctions.transformName(t.getPk().getName());
				String columnList = AuxiliaryFunctions.transformName(calculateColumnList(t.getPk().getParameters().getParameters()));
				if (tableName != null && pkName != null && columnList != null) {
					alter = "alter table " + schema + "." + tableName + " add constraint " + pkName + " primary key (" + columnList + ")";
				} else {
					logger.log(Level.SEVERE, "Error preparing alter query. Either table name or pk name or parameters list are formed wrongly");
					System.exit(1);
				}
			}
		}
		else {
			logger.log(Level.SEVERE, "Error preparing alter query. PK info in xml is formed wrongly");
			System.exit(1);
		}
		return alter;
	}

	public static String calculateColumnList(List<XMLPrimaryKeyParameter> parameters) {
		String list = "";
		for (XMLPrimaryKeyParameter param : parameters) {
			if (param.getName() == null) continue;
			list += param.getName() + ",";
		}
		list = list.replaceAll(",$", "");
		return list;
	}
	
	public static String calculateIndexColumnList(List<XMLIndexParameter> parameters) {
		String list = "";
		for (XMLIndexParameter param : parameters) {
			if (param.getName() == null) continue;
			list += param.getName() + ",";
		}
		list = list.replaceAll(",$", "");
		return list;
	}

	public static String calculatePkName(String name) {
		String pk = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
		return pk;
	}


}
