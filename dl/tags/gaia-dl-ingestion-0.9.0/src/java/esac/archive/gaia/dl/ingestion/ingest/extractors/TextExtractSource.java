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
import gaia.cu1.tools.exception.GaiaException;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

import java.sql.SQLException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.TableBuilder;

public class TextExtractSource  extends ExtractSource{
    private static final Logger logger = Logger.getLogger(TextExtractSource.class.getName());

	public static final int TMP_FILE_SIZE = 1000;
	
	protected List<ColumnInfo> metadata;

	public TextExtractSource(){

	};
	
	@Override
	public Integer process(
			boolean createTable,
			File file, 
			String schema,
			String tableName, 
			TableBuilder stiltsBuilder) 
					throws 
					IllegalArgumentException, 
					SecurityException, 
					IllegalAccessException, 
					InvocationTargetException, 
					NoSuchMethodException, 
					SQLException, 
					PropertyVetoException, 
					GaiaException, 
					ClassNotFoundException, 
					IOException
	{
        // Parsing of the object set, insertion into the prepared statement and execution in batches
        int objectNumberCounter = 0;

        File tmpDir = new File(".");
        logger.log(Level.INFO, "Created tmpdir");

        BufferedReader inputData = new BufferedReader(new FileReader(file));
        logger.log(Level.INFO, "Created inputdata");

        Connection con = JDBCPoolSingleton.getInstance().getConnection();
		boolean endReached = false;
		StarTable st = null;
		File tmpFile = null;
		boolean tableCreated = false;
		try {
			while(!endReached){
		        //logger.log(Level.INFO, "while(!endReached){");

				String line;
				String data = "";
		
				// Create a buffer of 1000 rows 
				int buff = 0;
				while(buff < TMP_FILE_SIZE){
			        logger.log(Level.INFO, "buff " + buff);

					line=inputData.readLine();
					if(line==null){
						endReached=true;
						break;
					}
					// First line of the file MUST be the header
					data+=line+"\n";
					buff++;
				}
						        
				// Create StarTable from the buffer
				InputStream is = new ByteArrayInputStream( (data).getBytes() );
		        logger.log(Level.INFO, "creado is");

				tmpFile = createTmpFile(is,tmpDir);
		        logger.log(Level.INFO, "creado tmpFile");

				st = getTableBuilder(tmpFile, stiltsBuilder);
		        logger.log(Level.INFO, "getTableBuilder");

		        if (st == null) {
		            logger.log(Level.INFO, "Error creating StarTable!");
		            System.exit(1);
		        }
		        if (createTable && !tableCreated) {
		  		  try {
		  		      createTable(con,schema,tableName,st);
		  		      tableCreated = true;
		  		      logger.log(Level.INFO, "created table");
		  		  } catch (Exception ex){
		  			  logger.log(Level.SEVERE, "Unable to create the table!");
		  			  System.exit(1);
		  		  }
		        }
				objectNumberCounter = updateTable(con, st, schema + "." + tableName);
	  			  logger.log(Level.INFO, "table updated");

				is.close();
				
				if(tmpFile!=null && tmpFile.exists()){
					tmpFile.delete();
				}
			}
		}catch(Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            System.exit(1);
		}
		finally {
			if (inputData != null)
				inputData.close();
		}

		return objectNumberCounter;
	}
		
	/**
 	 * Creates a temporary file where the input data is saved.<br/>
	 * This file will be used to create the StarTable.<br/>
	 * The file is removed when the parser ends.<br/>
	 * @param is
	 * @param tmpDir
	 * @return Temporary file
	 * @throws IOException
	 */
	protected File createTmpFile(InputStream is, File tmpDir) throws IOException{
		File fTmp = File.createTempFile("Parse", ".txt", tmpDir);
		
		PrintStream ps = null;
		BufferedReader reader = null;
		try{
			ps = new PrintStream(fTmp);
			
			//if(metadata!=null){
			//	ps.println(createHeader());
			//}
			
			reader = new BufferedReader(new InputStreamReader(is));
			String line;
			while((line=reader.readLine())!=null) {
				ps.println(line);
			}
			ps.flush();
			ps.close();
		}finally{
			if(ps != null){
				try{
					ps.close();
				}catch(Exception ioe){
				}
			}
			if(reader != null){
				try{
					reader.close();
				}catch(IOException ioe){
				}
			}
		}
		return fTmp;
	}
	
	//protected abstract String createHeader();

}
