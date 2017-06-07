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
package esac.archive.gaia.dl.ingestion.ingest.extractors;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.CaseFormat;

import esac.archive.gaia.dl.ingestion.db.JDBCPoolSingleton;
import gaia.cu1.tools.exception.GaiaException;


/**
 * Extractor of Gaia DM objects obtained from a CSV file onto 
 * INSERT statements with the provided table name into the JDBC connection
 * 
 * @author jduran
 *
 */
public class CsvExtractSource extends ExtractSource{
	
	
	private static final Logger logger = Logger.getLogger(CsvExtractSource.class.getName());
	
	
	/**
	 * Goes through one CsvReader parsing objects
	 * @param csvFile
	 * @param dmObject
	 * @param conversor
	 * @param tableName
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SQLException
	 * @throws PropertyVetoException
	 * @throws GaiaException
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	@Override
	public Integer process(File csvFile, String schema, String tableName) 
			throws IllegalArgumentException, SecurityException, 
				IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, PropertyVetoException, GaiaException, ClassNotFoundException, IOException{

		// Parsing of the object set, insertion into the prepared statement and execution in batches
		int objectNumberCounter = 0;

		Connection con = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try {

		// DB connection preparation
		
		con = JDBCPoolSingleton.getInstance().getConnection();
		
		PreparedStatement insertValues = null;
		
		// INSERT statement preparation
		
		String tablename = schema + "." + tableName;
		
		String columns = "";
		String values = "";
		
		//String state = "SELECT c.column_name AS column_name, c.data_type, c.is_nullable AS nullable FROM information_schema.columns c LEFT JOIN information_schema.element_types e ON c.table_catalog = e.object_catalog AND c.table_schema = e.object_schema AND c.table_name = e.object_name AND ''TABLE'' = e.object_type WHERE UPPER(c.table_name) = upper( '" + tablename + "' ) ORDER BY c.ordinal_position";
		
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename + " limit 1");
		ResultSetMetaData metadata = rs.getMetaData();
		if (stmt != null) 
			stmt.close();

		for (int i = 1; i <= metadata.getColumnCount(); i++) {
			columns += CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, metadata.getColumnName(i)) + ",";
			values += "?,";
		}
		
		//remove last comma
		columns = columns.replaceAll(",$", "");
		values = values.replaceAll(",$", "");

		String insertStatement =
				"INSERT INTO "+tablename+ " ("+columns+")" 
						+ " VALUES ("+values+");";

		logger.log(Level.INFO, "Insertion prepared statement "+insertStatement);

		con.setAutoCommit(false);
		insertValues = con.prepareStatement(insertStatement);
		logger.log(Level.INFO, "prepareStatement");


	      fr = new FileReader(csvFile);
	      br = new BufferedReader(fr);
	 
	      String row;
	      while((row = br.readLine()) != null)
	      {
				logger.log(Level.INFO, "row ("+objectNumberCounter+") " + row);

	    	  	String[] s = row.split(",", -1);
		  				
				if (s.length != metadata.getColumnCount()) {
					logger.log(Level.SEVERE, "Expected columns mistaches with actual csv columns: s.length = " + s.length + " metadata.getColumnCount() = " +metadata.getColumnCount());
				}
				
				//List<Object> objects = ReturnedObjectsParser.getGaiaObjectValues(dmObject);
				
				//index for placement in value list
				int methodIdx = 1;
				for(Object obj:s){
					logger.log(Level.FINE, "idx "+methodIdx+" class "+obj);
					
					if (obj == null || obj.toString().equals("")) {
						insertValues.setNull(methodIdx, Types.NULL);
					}
					else if (metadata.getColumnType(methodIdx) == Types.BIGINT){
						insertValues.setLong(methodIdx, Long.parseLong(obj.toString()));
					}
					else if (metadata.getColumnType(methodIdx) == Types.FLOAT){
						insertValues.setDouble(methodIdx, Double.parseDouble(obj.toString()));
					}
					else if (metadata.getColumnType(methodIdx) == Types.REAL){
						insertValues.setDouble(methodIdx, Double.parseDouble(obj.toString()));
					}
					else if (metadata.getColumnType(methodIdx) == Types.DOUBLE){
						insertValues.setDouble(methodIdx, Double.parseDouble(obj.toString()));
					}
					else if (metadata.getColumnType(methodIdx) == Types.INTEGER){
						insertValues.setInt(methodIdx, Integer.parseInt(obj.toString()));
					}
					else if (metadata.getColumnType(methodIdx) == Types.TIMESTAMP){
						insertValues.setTimestamp(methodIdx, Timestamp.valueOf(obj.toString()));
					}
					else if (metadata.getColumnType(methodIdx) == Types.CHAR){
						insertValues.setString(methodIdx, obj.toString());
					}
					else if (metadata.getColumnType(methodIdx) == Types.VARCHAR){
						insertValues.setString(methodIdx, obj.toString());
					}
					else if (metadata.getColumnType(methodIdx) == Types.SMALLINT){
						insertValues.setShort(methodIdx, Short.parseShort(obj.toString()));
					}


					methodIdx++;
				}
				
				insertValues.addBatch();
				objectNumberCounter ++;
				if(objectNumberCounter % 10000 == 0){
					insertValues.executeBatch();
					con.commit();
					logger.log(Level.INFO, "Executing batch update "+insertStatement);
				}
				
		      }
		
			//insertion of the last batch (typically less than 10K)
			insertValues.executeBatch();
			logger.log(Level.INFO, "Executing batch update "+insertStatement);
			logger.log(Level.INFO, "Thread for "+csvFile.getAbsolutePath()+" exiting having ingested "+objectNumberCounter+" sources");
	    }
	    catch(SQLException e) {
			logger.log(Level.SEVERE, e.getMessage());
			logger.log(Level.SEVERE, e.getNextException().getMessage());
	    }
	    catch(Exception e) {
			logger.log(Level.SEVERE, "Exception reading file "+ csvFile + ": " + e);
	    }
		finally {
			if (con != null) {
				con.commit();
				con.close();
			}
			if (fr != null) {
			    fr.close();
			}
			if (br != null) {
			    br.close();
			}
		}
		return objectNumberCounter;
	}
}
