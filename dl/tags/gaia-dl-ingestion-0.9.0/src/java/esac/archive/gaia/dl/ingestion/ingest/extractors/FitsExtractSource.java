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
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.starlink.fits.BintableStarTable;
import uk.ac.starlink.fits.FitsTableBuilder;
import uk.ac.starlink.table.ByteStore;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.table.TableBuilder;
import uk.ac.starlink.table.storage.ByteStoreStoragePolicy;
import uk.ac.starlink.util.DataSource;
import uk.ac.starlink.util.FileDataSource;

import com.google.common.base.CaseFormat;

import esac.archive.gaia.dl.ingestion.db.JDBCPoolSingleton;
import gaia.cu1.tools.exception.GaiaException;

/**
 * Extractor of DM objects obtained from a FITS file onto 
 * INSERT statements with the provided table name into the JDBC connection.
 * 
 * @author snieto - ESAC/ESA - Madrid, Spain Copyright (c) 2015
 *
 */

public class FitsExtractSource extends ExtractSource{

    private static final Logger logger = Logger.getLogger(FitsExtractSource.class.getName());
    
    /**
     * Goes through one FitsReader parsing objects
     * @param fitsFile
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
    public Integer process(boolean createTable, File fitsFile, String schema, String tableName, TableBuilder stiltsBuilder) 
            throws IllegalArgumentException, SecurityException, 
                IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, PropertyVetoException, GaiaException, ClassNotFoundException, IOException{

        // Parsing of the object set, insertion into the prepared statement and execution in batches
        int objectNumberCounter = 0;

        Connection con = JDBCPoolSingleton.getInstance().getConnection();
        
        BintableStarTable bst = getFitsTableBuilder(fitsFile);
        if (bst == null) {
            logger.log(Level.SEVERE, "Error at BintableStarTable!");
            System.exit(1);
        }

        try {

        // DB connection preparation
        
        con = JDBCPoolSingleton.getInstance().getConnection();
        PreparedStatement insertValues = null;
        
        // INSERT statement preparation
        logger.log(Level.INFO, "5");

        String tablename = schema + "." + tableName;
        
        String columns = "";
        String values = ""; 

        logger.log(Level.INFO, "2");

        // int i = 2 // to avoid insert serial primary key
        for (int i = 0; i < bst.getColumnCount(); i++) {
        	logger.log(Level.INFO, bst.getColumnInfo(i).getName());
            columns += CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, bst.getColumnInfo(i).getName()) + ",";
            values += "?,";
        }
        
        logger.log(Level.INFO, "3");

        //remove last comma
        columns = columns.replaceAll(",$", "");
        values = values.replaceAll(",$", "");

        String insertStatement = "INSERT INTO "+tablename+ " ("+columns+")" + " VALUES ("+values+");";

        logger.log(Level.INFO, "4");

        con.setAutoCommit(false);
        insertValues = con.prepareStatement(insertStatement);

        logger.log(Level.INFO, "5");

          
          long index = 0;
          long limit = bst.getRowCount();
          while (index < limit) {
              Object[] row = bst.getRow(index++);
              logger.log(Level.FINE, "Found "+row.toString()+" columns");
                            
              //index for placement in value list
              int methodIdx = 1;
              for(Object obj:row){
                  logger.log(Level.FINE, "idx "+methodIdx+" class "+obj);
                  logger.log(Level.INFO, "obj "+obj.getClass().getName() + " Value " + obj.toString());
                  
                  if (obj == null || obj.toString().equals("NaN")) {
                      //logger.log(Level.INFO, "VALOR NaN: "+ obj.toString());
                      insertValues.setNull(methodIdx, Types.NULL);
                      
                  } else if (obj instanceof Integer) {
                      //logger.log(Level.INFO, "VALOR 4: "+ obj.toString());
                      insertValues.setInt(methodIdx, (Integer) obj);
                      
                  } else if (obj instanceof Long) {
                      //logger.log(Level.INFO, "VALOR -5: "+ obj.toString());
                      insertValues.setLong(methodIdx, (Long) obj);
                      
                  } else if (obj instanceof Float) {
                      //logger.log(Level.INFO, "VALOR 6: "+ obj.toString());
                      insertValues.setFloat(methodIdx, (Float) obj);
                      
                  } else if (obj instanceof Double) {
                      //logger.log(Level.INFO, "VALOR 8: "+ obj.toString());
                      insertValues.setDouble(methodIdx, (Double) obj);
                      
                  } else if (obj instanceof String) {
                      //logger.log(Level.INFO, "VALOR 12: "+ obj.toString());
                      insertValues.setString(methodIdx, (String) obj);
                      
                  } else if (obj instanceof Character) {
                      //logger.log(Level.INFO, "VALOR 12: "+ obj.toString());
                      insertValues.setString(methodIdx, ((Character) obj).toString());
                      
                  } else {
                      logger.log(Level.INFO, "NO VALUE! "+ obj.toString());
                      throw new Exception();
                  }
                  
                  //logger.log(Level.INFO, "Insertion prepared statement "+insertValues.toString());
                  
                  methodIdx++;
              }
              
              insertValues.addBatch();
              objectNumberCounter ++;
              if(objectNumberCounter % 10000 == 0){
                  insertValues.executeBatch();
                  con.commit();
                  //logger.log(Level.INFO, "Executing batch update "+insertStatement);
              }
              
            }
      
          //insertion of the last batch (typically less than 10K)
          insertValues.executeBatch();
          logger.log(Level.INFO, "Executing batch update "+insertStatement);
          logger.log(Level.INFO, "Thread for "+fitsFile.getAbsolutePath()+" exiting having ingested "+objectNumberCounter+" sources");
          
        } catch(SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, e.getNextException().getMessage());
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Exception reading file "+ fitsFile + ": " + e);
        } finally {
            if (con != null) {
                con.commit();
                con.close();
            }
        }
          
        return objectNumberCounter;
    }
    

	/*
     * Retrieve a FitsTableBuilder to read the fits file given.
     */
    private static BintableStarTable getFitsTableBuilder (File fitsFile) {

        DataSource ds = getDataSource(fitsFile);
        StoragePolicy sp = getStoragePolicy();
        Boolean random = false;
        

        FitsTableBuilder ftb = new FitsTableBuilder();
        BintableStarTable bst = null;
        try {
            bst = (BintableStarTable) ftb.makeStarTable(ds, random, sp);

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return bst;
    }
    
    private static DataSource getDataSource (File fitsFile) {
        DataSource ds = null;
        try {
            ds = new FileDataSource(fitsFile);
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
