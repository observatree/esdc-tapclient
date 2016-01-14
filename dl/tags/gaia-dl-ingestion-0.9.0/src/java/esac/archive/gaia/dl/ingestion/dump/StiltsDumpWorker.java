package esac.archive.gaia.dl.ingestion.dump;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectStreamConstants;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.starlink.table.StarTableWriter;
import uk.ac.starlink.table.StreamStarTableWriter;
import uk.ac.starlink.table.TableFormatException;
import uk.ac.starlink.table.formats.CsvTableWriter;
import uk.ac.starlink.table.jdbc.Connector;
import uk.ac.starlink.table.jdbc.JDBCStarTable;
import esac.archive.gaia.dl.ingestion.db.JDBCPoolSingleton;

public class StiltsDumpWorker extends GenericDumpWorker{
	private static final Logger logger = Logger.getLogger(GenericDumpWorker.class.getName());

	Connector connector = null;
	
	private String stiltsTableWriter;
			
	public StiltsDumpWorker(
			String stiltsTableWriter,
			String dumpOutputPath, 
			String dumpFilePattern,
			long dumpMaxSizeInRows,
			String schemaName,
			String tableName,
			int index) {
		this.stiltsTableWriter = stiltsTableWriter;
		this.dumpOutputPath = dumpOutputPath;
		this.dumpFilePattern = dumpFilePattern;
		this.dumpMaxSizeInRows = dumpMaxSizeInRows;
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.index = index;
		connector = new Connector() {
			
			@Override
			public Connection getConnection() throws SQLException {
				Connection con = null;
				try {
					con = JDBCPoolSingleton.getInstance().getConnection();
				} catch (PropertyVetoException e) {
				}
				return con;
			}
		};

	}
	
	public void dump() {
		               
		OutputStream os = null;
		try {
			
			String sql = "SELECT * FROM "  + schemaName + "." + tableName + " LIMIT " + dumpMaxSizeInRows + " OFFSET " + index;
			
			JDBCStarTable st = new JDBCStarTable(connector, sql);
			logger.log(Level.INFO, st.getName());
			
			File file = null;
			
			synchronized (this) {
				File f = new File(dumpOutputPath + File.separator + numberFolder);
				if (f.exists()) {
					int tam = f.listFiles().length;
					while (tam > 10000)
					{
						numberFolder++;
						f = new File(dumpOutputPath + File.separator + numberFolder);
						if (f.exists()) {
							tam = f.listFiles().length;
						}
						else {
							new File(dumpOutputPath + File.separator + numberFolder).mkdirs();
							tam = 0;
						}
					}
				}
				else {
					new File(dumpOutputPath + File.separator + numberFolder).mkdirs();
				}
				
				File fi = new File(dumpOutputPath + File.separator + numberFolder + File.separator + dumpFilePattern + "-" + numberFile);
				if (fi.exists()) {
					numberFile++;
				}
				file = new File(dumpOutputPath + File.separator + numberFolder + File.separator + dumpFilePattern + "-" + numberFile);
				
			}
			ObjectStreamConstants o = null;
			os = new FileOutputStream(file.getAbsolutePath());
			StarTableWriter stw = createStiltsTableWriter(stiltsTableWriter);
			
			logger.log(Level.INFO, stw.toString() + " " + st.getName() + " " + os.toString());

			stw.writeStarTable(st, os);

		} catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (TableFormatException e) {
            logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
		finally {
			if (os != null) {
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private static StarTableWriter createStiltsTableWriter(
			String stiltsTableWriter) {
		StarTableWriter tableWriter = new CsvTableWriter();
		// identify stilts class (if provided)
//		if(!stiltsTableWriter.equals("none")){
//		    try {
//		    	logger.log(Level.INFO, "Init " + stiltsTableWriter);
//		    	@SuppressWarnings("unchecked")
//				Class<? extends StarTableWriter> c = (Class<? extends StarTableWriter>) Class.forName(stiltsTableWriter);
//		    	logger.log(Level.INFO, "Created class");
//		    	Constructor<?> cc = c.getConstructor();
//		    	logger.log(Level.INFO, "Created constructor");
//		    	tableWriter = (StarTableWriter) cc.newInstance();
//		    	logger.log(Level.INFO, "Created instance");
//
//			} catch (InstantiationException | IllegalAccessException
//					| IllegalArgumentException | InvocationTargetException
//					| NoSuchMethodException | SecurityException
//					| ClassNotFoundException e) {
//				logger.log(Level.SEVERE, "Error creating Stilts Writer "+stiltsTableWriter+ ": " + e.getMessage());
//				System.exit(1);
//			}
//		}
		return tableWriter;
	}


}
