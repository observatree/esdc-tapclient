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

import esac.archive.gaia.dl.ingestion.db.JDBCPoolSingleton;
import esac.archive.gaia.dl.ingestion.filters.IFilter;
import esac.archive.gaia.dl.ingestion.objectconverter.IObjectConverter;
import esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints.IZeroPoints;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.exception.GaiaException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.starlink.table.ByteStore;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.table.TableBuilder;
import uk.ac.starlink.table.TableSequence;
import uk.ac.starlink.table.formats.CsvTableBuilder;
import uk.ac.starlink.table.storage.ByteStoreStoragePolicy;
import uk.ac.starlink.util.DataSource;
import uk.ac.starlink.util.FileDataSource;
import uk.ac.starlink.votable.VOStarTable;
import uk.ac.starlink.votable.VOTableBuilder;

import com.google.common.base.CaseFormat;

public class ExtractSource {
	
    private static final Logger logger = Logger.getLogger(ExtractSource.class.getName());

    //for csv
	public Integer process(
			File file, 
			String schema,
			String tableName) 
					throws IllegalArgumentException, SecurityException, 
	IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, PropertyVetoException, GaiaException, ClassNotFoundException, IOException
	{
        return 0;
	}
    //for gbin
	public Integer process(
			File file,
			String schema,
			String tableName, 
			Class<?> dmObject, 
			IObjectConverter<GaiaRoot, ?> conversor, IFilter filter, IZeroPoints zPoints) 
					throws IllegalArgumentException, SecurityException, 
	IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, PropertyVetoException, GaiaException, ClassNotFoundException, IOException
	{
        return 0;
	}

    //original (for stilts)
	public Integer process(
			boolean createTable,
			File file, 
			String schema,
			String tableName, 
			TableBuilder stiltsBuilder) 
					throws IllegalArgumentException, SecurityException, 
	IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, PropertyVetoException, GaiaException, ClassNotFoundException, IOException
	{
        // Parsing of the object set, insertion into the prepared statement and execution in batches
        int objectNumberCounter = 0;

        Connection con = JDBCPoolSingleton.getInstance().getConnection();

        StarTable st = getTableBuilder(file, stiltsBuilder);
        if (st == null) {
            logger.log(Level.INFO, "Error creating StarTable!");
            System.exit(1);
        }

        if (createTable) {
		  try {
		      createTable(con,schema,tableName,st);
              logger.log(Level.INFO, "Table created");

		  } catch (Exception ex){
			  logger.log(Level.SEVERE, "Unable to create the table!");
			  System.exit(1);
		  }
        }

        objectNumberCounter = updateTable(con, st, schema + "." + tableName);
        
        return objectNumberCounter;

	}

	protected int updateTable(Connection con, StarTable st, String table){
        int objectNumberCounter = 0;
        try {
            
        PreparedStatement insertValues = null;        
        
        String columns = "";
        String values = "";
                
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM "  + table + " limit 1");
        ResultSetMetaData metadata = rs.getMetaData();
        if (stmt != null) 
            stmt.close();

        // int i = 2 // to avoid insert serial primary key
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            columns += CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, metadata.getColumnName(i).replaceAll(" ", "")) + ",";
            values += "?,";
        }

        //remove last comma
        columns = columns.replaceAll(",$", "");
        values = values.replaceAll(",$", "");

        String insertStatement =
                "INSERT INTO "+table+ " ("+columns+")" 
                        + " VALUES ("+values+");";


        con.setAutoCommit(false);
        insertValues = con.prepareStatement(insertStatement);
                  
			  			
        	  RowSequence rowSeq = st.getRowSequence();//getRow(index);
                            
              //index for placement in value list
              while(rowSeq.next()){
            	  Object[] objs = rowSeq.getRow();
                  int methodIdx = 1;
            	  for (Object obj: objs){
	                  logger.log(Level.FINE, "idx "+methodIdx+" class "+obj);
	                  
	                  if (obj == null || obj.toString().equals("NULL")) {
	                      insertValues.setNull(methodIdx, Types.NULL);
	                      
	                  } else if (obj instanceof Integer) {
	                      insertValues.setInt(methodIdx, (Integer) obj);
	                      
	                  } else if (obj instanceof Long) {
	                      insertValues.setLong(methodIdx, (Long) obj);
	                      
	                  } else if (obj instanceof Short) {
	                      insertValues.setShort(methodIdx, (Short) obj);
	                      
	                  } else if (obj instanceof Float) {
	                      insertValues.setDouble(methodIdx, ((Float) obj).doubleValue());
	                      
	                  } else if (obj instanceof Double) {
	                      insertValues.setDouble(methodIdx, (Double) obj);
	                      
	                  } else if (obj instanceof String) {
	                      insertValues.setString(methodIdx, (String) obj);
	                      
	                  } else if (obj instanceof Character) {
	                      insertValues.setString(methodIdx, (String) obj);
	                      
	                  } else if (obj instanceof Boolean) {
	                      insertValues.setBoolean(methodIdx, (Boolean) obj);
	                      
	                  } else {
	                      logger.log(Level.INFO, "NO VALUE! "+ obj.toString());
	                      System.exit(1);
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
          con.commit();
          logger.log(Level.INFO, "Executing batch update "+insertStatement);
          logger.log(Level.INFO, "Thread exiting having ingested "+objectNumberCounter+" sources");
          
        } catch(SQLException e) {
            logger.log(Level.SEVERE, "SQL Exception during updateTable: " + e.getMessage());
            logger.log(Level.SEVERE, "SQL Exception during updateTable: " + e.getNextException().getMessage());
            System.exit(1);
        } catch(Exception e) {
            logger.log(Level.SEVERE, "General exception during updateTable: " + e.getMessage());
            System.exit(1);
        } finally {

        }
          
        return objectNumberCounter;

	}
	
	protected void createTable(Connection con, String schemaName, String tableName, StarTable st) throws Exception {

    	ArrayList<String> columnNames = new ArrayList<String>();
    	ArrayList<Class> types = new ArrayList<Class>();
        for (int i = 0; i < st.getColumnCount(); i++) {
            columnNames.add(st.getColumnInfo(i).getName());
            types.add(st.getColumnInfo(i).getContentClass());
        }
        System.out.println(st.getColumnCount());
        System.out.println(columnNames.toString());
        System.out.println(types.toString());
        		
        String sql = "";
        for (int i = 0; i < columnNames.size(); i++) {
    		sql+= CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnNames.get(i).toLowerCase().replaceAll(" ", "")) + " !,";
        }
        sql = sql.replaceAll(",$", "");

    	String statement = "CREATE TABLE "+schemaName+"."+tableName + " (" + sql + ")";

        for (Class obj: types) {
            //logger.log(Level.INFO, "obj.getClass() = "+ obj.getName());
            //logger.log(Level.INFO, "Integer.class = "+ Integer.class.getName());

            if (obj.getName().equals(Boolean.class.getName())) {
            	statement = statement.replaceFirst("!", "boolean");
                
            } else if (obj.getName().equals(Integer.class.getName())) {
            	statement = statement.replaceFirst("!", "integer");
                        
            } else if (obj.getName().equals(Short.class.getName())) {
            	statement = statement.replaceFirst("!", "smallint");
                
            } else if (obj.getName().equals(Long.class.getName())) {
            	statement = statement.replaceFirst("!", "bigint");
                
            } else if (obj.getName().equals(Float.class.getName())) {
            	statement = statement.replaceFirst("!", "double precision");
                
            } else if (obj.getName().equals(Double.class.getName())) {
            	statement = statement.replaceFirst("!", "double precision");
                
            } else if (obj.getName().equals(String.class.getName())) {
            	statement = statement.replaceFirst("!", "character varying");
                
            } else if (obj.getName().equals(Character.class.getName())) {
            	statement = statement.replaceFirst("!", "character varying");
                
            } else {
                logger.log(Level.INFO, "NO VALUE! "+ obj);
                System.exit(1);
            }
        }
        
        statement = statement.replaceAll("-", "_");
        
        logger.log(Level.INFO, "statement = "+ statement);

    	try {
    		con.createStatement().executeUpdate(statement);

    	} catch (SQLException se) {
    		logger.log(Level.SEVERE, "Could not create table! = "+ tableName + "; " + se.getMessage());
    	} catch (Exception e) {
    		logger.log(Level.SEVERE, "Exception: Could not create table! = "+ tableName + "; " + e.getMessage());
    	} finally {

        }
	}


	/*
     * Retrieve a TableBuilder to read the file given.
     */
    protected static StarTable getTableBuilder (File file, TableBuilder stiltsBuilder) {
        DataSource ds = getDataSource(file);       
        StoragePolicy sp = getStoragePolicy();
        Boolean random = false;

        StarTable st = null;
        //TableSequence ts = null;
        try {
        	//VOTableBuilder vot = new VOTableBuilder();
            //ts = vot.makeStarTables(ds, sp);
            st = stiltsBuilder.makeStarTable(ds, random, sp);
            //ts.nextTable();
            //st = ts.nextTable();

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
	    }
        
        return st;
    }
    
    private static DataSource getDataSource (File file) {
        DataSource ds = null;
        try {
            ds = new FileDataSource(file);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return ds;
    }
    
    private static StoragePolicy getStoragePolicy () {
        
        StoragePolicy sp =  new ByteStoreStoragePolicy() {            
            @Override
            protected ByteStore attemptMakeByteStore() throws IOException {
                
                return null;
            }
        };
        
        return sp;
    }
}
