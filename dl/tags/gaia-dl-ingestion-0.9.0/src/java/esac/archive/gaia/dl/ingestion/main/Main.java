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
package esac.archive.gaia.dl.ingestion.main;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.filefilter.IOFileFilter;

import uk.ac.starlink.table.TableBuilder;
import esac.archive.gaia.dl.ingestion.config.ConfigProperties;
import esac.archive.gaia.dl.ingestion.dump.Dumper;
import esac.archive.gaia.dl.ingestion.enumtypes.CatalogFormats;
import esac.archive.gaia.dl.ingestion.enumtypes.CreationTypes;
import esac.archive.gaia.dl.ingestion.enumtypes.OperationTypes;
import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.ingest.Ingestor;
import esac.archive.gaia.dl.ingestion.ingest.PostIngestion;
import esac.archive.gaia.dl.ingestion.ingest.extractors.ExtractSource;
import esac.archive.gaia.dl.ingestion.ingest.extractors.GbinExtractSource;
import esac.archive.gaia.dl.ingestion.ingest.extractors.TextExtractSource;
import esac.archive.gaia.dl.ingestion.objectconverter.IObjectConverter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import esac.archive.gaia.dl.ingestion.tapupload.Uploader;
import esac.archive.gaia.dl.ingestion.transform.GbinTransformCatalogueSource;
import esac.archive.gaia.dl.ingestion.transform.Transformer;
import esac.archive.gaia.dl.ingestion.transform.TransformerSource;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLDocumentationExport;
import esac.archive.gaia.dl.ingestion.xmlparser.XMLTable;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.util.props.PropertyLoader;

/**
 * 
 * @author jduran
 *
 */
public class Main {
	
    public static final String NONE	    						= "none";
    
    public static final String TYPE_OPERATION	   				= "type.operation";
    public static final String TYPE_FORMAT		    			= "type.format";
    
    public static final String GBIN_CLASSTOUSE					= "gbin.classToUse";
    public static final String GBIN_CONVERSOR					= "gbin.conversor";
    public static final String GBIN_FILTERCLASS		    		= "gbin.filterClass";
    public static final String GBIN_ZEROPOINTSSCHEMA    		= "gbin.zeroPointsSchema";
    public static final String GBIN_ZEROPOINTSTABLE	    		= "gbin.zeroPointsTable";
    public static final String GBIN_ZEROPOINTSCLASS	    		= "gbin.zeroPointsClass";
    public static final String GBIN_XMLFILE						= "gbin.pathToXMLFile";
    public static final String GBIN_DATAMODELTABLE				= "gbin.dataModelTableName";


    public static final String UPLOAD_SCHEMANAME     			= "upload.schemaName";
    //public static final String UPLOAD_TABLENAME     			= "upload.tableName";
    public static final String UPLOAD_RA		     			= "upload.raColumnName";
    public static final String UPLOAD_DEC		     			= "upload.decColumnName";
    public static final String UPLOAD_PUBLICSCHEMA				= "upload.public_schema";
    public static final String UPLOAD_PUBLICTABLE				= "upload.public_table";
    public static final String UPLOAD_ALLCOLUMNS				= "upload.tap_all_columns_table";
    public static final String UPLOAD_ALLTABLES					= "upload.tap_all_tables_table";
    public static final String UPLOAD_TAPALLSCHEMAS				= "upload.tap_all_schemas_table";
    public static final String UPLOAD_TAPSCHEMA					= "upload.tap_schema";
    
    public static final String INGEST_SCHEMANAME     			= "ingest.schemaName";
    public static final String INGEST_TABLENAME     			= "ingest.tableName";
    public static final String INGEST_PATHTOFILES   			= "ingest.pathToFiles";
    public static final String INGEST_FILEPATTERN   			= "ingest.filePattern";
    public static final String INGEST_CREATETABLE   			= "ingest.createTable";
    public static final String INGEST_STILTS		    		= "ingest.stiltsTableBuilderClass";
    public static final String INGEST_POST		    			= "ingest.post";
    
    public static final String POST_SCHEMANAME		    		= "post.schemaName";
    public static final String POST_AUTORADECINDEX		    	= "post.autoRaDecIndex";
    
    public static final String TRANSFORM_OUTDIR	    			= "transform.outDir";
    public static final String TRANSFORM_INGESTING	    		= "transform.ingesting";
    public static final String TRANSFORM_PATH	    			= "transform.pathToFiles";
    public static final String TRANSFORM_FILEPATTERN   			= "transform.filePattern";
    
    public static final String DUMP_SCHEMANAME     				= "dump.schemaName";
    public static final String DUMP_TABLENAME     				= "dump.tableName";
    public static final String DUMP_OUTPUTPATH	    			= "dump.outputPath";
    public static final String DUMP_FILEPATTERN		    		= "dump.filePattern";
    public static final String DUMP_MAXSIZEPERFILEINROWS		= "dump.maxSizePerFileinRows";
    public static final String DUMP_STILTSTABLEWRITER			= "dump.stiltsTableWriter";
    //public static final String DUMP_GBINS_GAIAPROPERTIES		= "dump.gbins.gaiaProperties";
    
    public static final String THREADS_NUMBER	    			= "threads.number";
    public static final String LOGGING_LEVEL	    			= "logging.level";

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println("Error: One parameter is expected containing the location of the configuration file");
			System.exit(1);
		}
		ConfigProperties.Init(args[0]);
		String operation = ConfigProperties.getInstance().getProperty(TYPE_OPERATION);
		String format = ConfigProperties.getInstance().getProperty(TYPE_FORMAT);
		String stilts = ConfigProperties.getInstance().getProperty(INGEST_STILTS);
		
		String classToUse = ConfigProperties.getInstance().getProperty(GBIN_CLASSTOUSE);
		String conversorClass = ConfigProperties.getInstance().getProperty(GBIN_CONVERSOR);
		String filterClass = ConfigProperties.getInstance().getProperty(GBIN_FILTERCLASS);
		String zeroPointsSchema = ConfigProperties.getInstance().getProperty(GBIN_ZEROPOINTSSCHEMA);		
		String zeroPointsTable = ConfigProperties.getInstance().getProperty(GBIN_ZEROPOINTSTABLE);		
		String zeroPointsClass = ConfigProperties.getInstance().getProperty(GBIN_ZEROPOINTSCLASS);
		String gbin_dataModelTableName = ConfigProperties.getInstance().getProperty(GBIN_DATAMODELTABLE);

		String upload_schemaName = ConfigProperties.getInstance().getProperty(UPLOAD_SCHEMANAME);
		//String upload_tableName = ConfigProperties.getInstance().getProperty(UPLOAD_TABLENAME);
		String gbin_xmlFile = ConfigProperties.getInstance().getProperty(GBIN_XMLFILE);
		String upload_ra = ConfigProperties.getInstance().getProperty(UPLOAD_RA);
		String upload_dec = ConfigProperties.getInstance().getProperty(UPLOAD_DEC);
		boolean upload_publicSchema = ConfigProperties.getInstance().getProperty(UPLOAD_PUBLICSCHEMA).toUpperCase().equals("YES");
		boolean upload_publicTable = ConfigProperties.getInstance().getProperty(UPLOAD_PUBLICTABLE).toUpperCase().equals("YES");
		String upload_allColumns = ConfigProperties.getInstance().getProperty(UPLOAD_ALLCOLUMNS);
		String upload_allTables = ConfigProperties.getInstance().getProperty(UPLOAD_ALLTABLES);
		String upload_allSchemas = ConfigProperties.getInstance().getProperty(UPLOAD_TAPALLSCHEMAS);
		String upload_tap_schema = ConfigProperties.getInstance().getProperty(UPLOAD_TAPSCHEMA);
		
		String ingest_sourcePath = ConfigProperties.getInstance().getProperty(INGEST_PATHTOFILES);
		String ingest_schemaName = ConfigProperties.getInstance().getProperty(INGEST_SCHEMANAME);
		String ingest_tableName = ConfigProperties.getInstance().getProperty(INGEST_TABLENAME);
		boolean ingest_createTable = ConfigProperties.getInstance().getProperty(INGEST_CREATETABLE).toLowerCase().equals("yes");
		boolean ingest_post = ConfigProperties.getInstance().getProperty(INGEST_POST).toLowerCase().equals("yes");
		String ingest_filePattern = ConfigProperties.getInstance().getProperty(INGEST_FILEPATTERN);
		
		String post_schemaName = ConfigProperties.getInstance().getProperty(POST_SCHEMANAME);
		boolean post_autoRaDecIndex = ConfigProperties.getInstance().getProperty(POST_AUTORADECINDEX).toLowerCase().equals("yes");;

		String outDir = ConfigProperties.getInstance().getProperty(TRANSFORM_OUTDIR);		
		String transformPath = ConfigProperties.getInstance().getProperty(TRANSFORM_PATH);		
		String transformPattern = ConfigProperties.getInstance().getProperty(TRANSFORM_FILEPATTERN);		
		
		String dump_schemaName = ConfigProperties.getInstance().getProperty(DUMP_SCHEMANAME);
		String dump_tableName = ConfigProperties.getInstance().getProperty(DUMP_TABLENAME);
		String dumpOutputPath = ConfigProperties.getInstance().getProperty(DUMP_OUTPUTPATH);		
		String dumpFilePattern = ConfigProperties.getInstance().getProperty(DUMP_FILEPATTERN);		
		String stiltsTableWriter = ConfigProperties.getInstance().getProperty(DUMP_STILTSTABLEWRITER);		
		long dumpMaxSizeInRows = Long.parseLong(ConfigProperties.getInstance().getProperty(DUMP_MAXSIZEPERFILEINROWS));		
		String gaiaProperties = args[0];//ConfigProperties.getInstance().getProperty(DUMP_GBINS_GAIAPROPERTIES);		

		Integer threads = Integer.parseInt(ConfigProperties.getInstance().getProperty(THREADS_NUMBER));
		String logging = ConfigProperties.getInstance().getProperty(LOGGING_LEVEL);	

		PropertyLoader.load();
		PropertyLoader.load(gaiaProperties, true);

		AuxiliaryFunctions.loggerInitialization();
		AuxiliaryFunctions.loggerSetUp(logging);
		
		ArrayList<String> opTypes = new ArrayList<String>();
		for (int i = 0; i < OperationTypes.values().length; i++) {
			opTypes.add(OperationTypes.values()[i].toString());
		}
		ArrayList<String> catFormats = new ArrayList<String>();
		for (int i = 0; i < CatalogFormats.values().length; i++) {
			catFormats.add(CatalogFormats.values()[i].toString());
		}
		ArrayList<String> creationTypes = new ArrayList<String>();
		for (int i = 0; i < CreationTypes.values().length; i++) {
			creationTypes.add(CreationTypes.values()[i].toString());
		}

		if (!opTypes.contains(operation)) {
			logger.log(Level.SEVERE, "Error: Not valid operation type: " + operation);
			System.exit(1);
		}
				
		//Only used for Gbins
		IObjectConverter<GaiaRoot, ?> conversor = null;
		//Only used for Gbins
		Class<?> eClass = null;
		//Only used for Gbins
		TransformerSource transformer = null;
		TableBuilder stiltsBuilderObject = null;
		ExtractSource extract = null;
		IFilter gbinFilter = null;
		IZeroPoints zeroPoints = null;
		
		if (format.equals(CatalogFormats.gbin.toString())) {
			transformer = new GbinTransformCatalogueSource();
			if (!conversorClass.equals(NONE))
				conversor = AuxiliaryFunctions.createConversor(conversorClass);
			if (!classToUse.equals(NONE))
				eClass = AuxiliaryFunctions.createExtractedClass(classToUse);
			if (!filterClass.equals(NONE))
				gbinFilter = AuxiliaryFunctions.createFilterClass(filterClass);
			if (!zeroPointsSchema.equals(NONE) && !zeroPointsTable.equals(NONE) && !zeroPointsClass.equals(NONE)) {
				zeroPoints = AuxiliaryFunctions.createZeroPointsClass(zeroPointsClass);
				zeroPoints.setTable(zeroPointsTable);
				zeroPoints.setSchema(zeroPointsSchema);
			}
			extract = new GbinExtractSource();
		}
		else if (format.equals(CatalogFormats.txt.toString()) || format.equals(CatalogFormats.csv.toString())){ 
			if (!stilts.equals(NONE))
				stiltsBuilderObject = AuxiliaryFunctions.createStiltsBuilder(stilts);
			extract = new TextExtractSource();
		}
		else {
			if (!stilts.equals(NONE))
				stiltsBuilderObject = AuxiliaryFunctions.createStiltsBuilder(stilts);
			extract = new ExtractSource();
		}

		
		if (operation.equals(OperationTypes.ingest.toString())) {
			if (AuxiliaryFunctions.checkIngestParameters())
			{	
				IOFileFilter filter = AuxiliaryFunctions.getFileFilter(ingest_filePattern, format);

				if (format.equals(CatalogFormats.gbin.toString()))
				{
					Ingestor.ingest(
							gbin_xmlFile,
							ingest_createTable,
							filter, 
							eClass, 
							conversor,
							extract,
							gbinFilter,
							ingest_sourcePath, 
							ingest_schemaName,
							gbin_dataModelTableName,
							threads,
							zeroPoints,
							ingest_post,
							post_autoRaDecIndex);
				}
				else { 
					Ingestor.ingest(
							ingest_createTable,
							filter, 
							stiltsBuilderObject,
							extract,
							ingest_sourcePath, 
							ingest_schemaName,
							ingest_tableName, 
							threads);
				}
			}
			else {
				logger.log(Level.SEVERE, "Error in config parameters, check configuration.properties file");
				System.exit(1);
			}
		}
		else if (operation.equals(OperationTypes.dump.toString())) {
			if (AuxiliaryFunctions.checkDumpParameters()){
				if (format.equals(CatalogFormats.gbin.toString())) {
					Dumper.dump(
							gaiaProperties,
							eClass,
							dumpOutputPath, 
							dumpFilePattern,
							dumpMaxSizeInRows,
							dump_schemaName,
							dump_tableName,
							threads);

				}
				else {
					Dumper.dump(
							stiltsTableWriter,
							dumpOutputPath, 
							dumpFilePattern,
							dumpMaxSizeInRows,
							dump_schemaName,
							dump_tableName,
							threads);
				}
			}
		}
		else if (operation.equals(OperationTypes.transform.toString())) {
			if (AuxiliaryFunctions.checkTransformParameters()){
				if (format.equals(CatalogFormats.gbin.toString()))
				{
					IOFileFilter filter = AuxiliaryFunctions.getFileFilter(transformPattern, format);

					Transformer.transform(
							filter,
							conversor,
							transformer,
							gbinFilter,
							transformPath,
							outDir,
							threads,
							zeroPoints);
				}
			}
		}
		else if (operation.equals(OperationTypes.upload.toString())) {
			if (format.equals(CatalogFormats.gbin.toString())) {
				try {
					(new Uploader()).upload(
							upload_schemaName,
							upload_ra,
							upload_dec,
							upload_publicSchema,
							upload_publicTable,
							gbin_xmlFile, 
							gbin_dataModelTableName, 
							upload_tap_schema, 
							upload_allTables,
							upload_allSchemas,
							upload_allColumns,
							classToUse
							);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Error uploading table to tap: " + e.getMessage());
				}
			}
			else {
				
			}
		}
		else if (operation.equals(OperationTypes.post.toString())) {
			if (format.equals(CatalogFormats.gbin.toString())) {
				try {
					XMLDocumentationExport export = null;
					XMLTable table = null;
					try {
						export = AuxiliaryFunctions.readXMLInfo(gbin_xmlFile);
						for (XMLTable t : export.getTables()) {
							if (t.getName().equals(gbin_dataModelTableName)) {
								System.out.println(t.getName());
								table = t;
								break;
							}
						}
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Exception reading xml: ", e.getMessage());
						System.exit(1);
					}

					if (table == null) {
						logger.log(Level.SEVERE, "Error reading table name in xml");
						System.exit(1);
					}

					PostIngestion.createAlterPk(table, post_schemaName);
					PostIngestion.createAlterIndexes(table, post_schemaName, post_autoRaDecIndex, threads);
					
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Error while doing post ingestion table: " + e.getMessage());
				}
			}
			else {
				
			}
		}
	}
}
