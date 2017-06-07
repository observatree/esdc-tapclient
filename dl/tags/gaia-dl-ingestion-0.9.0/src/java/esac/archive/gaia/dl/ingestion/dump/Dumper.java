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
package esac.archive.gaia.dl.ingestion.dump;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
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

import esac.archive.gaia.dl.ingestion.db.JDBCPoolSingleton;
import gaia.cu1.tools.exception.GaiaException;



public class Dumper {
	private static final Logger logger = Logger.getLogger(Dumper.class.getName());
	private static ExecutorService taskExecutor = null;

	public static void dump(
			String gaiaProperties,
			Class<?> classToDump,
			String dumpOutputPath, 
			String dumpFilePattern,
			long dumpMaxSizeInRows,
			String schemaName,
			String tableName,
			Integer threads){	
		
		GbinDumpWorker gdw = new GbinDumpWorker(gaiaProperties, classToDump,dumpOutputPath,dumpFilePattern,schemaName,tableName);
		try {
			gdw.dump();
		} catch (GaiaException | SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Exception: " + e.getMessage());
		}
		
	}
	public static void dump(
			String tableWriter,
			String dumpOutputPath, 
			String dumpFilePattern,
			long dumpMaxSizeInRows,
			String schemaName,
			String tableName,
			Integer threads){	
		try{
			
			long count = calculateCount(schemaName, tableName);
			//dumpMaxSizeInRows = count;//Math.round(count / 16) + 1;
			long numberOfSegments = Math.round((count / dumpMaxSizeInRows)) + 1;

			//Create tasks for each file
			List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
			int index = 0;
			for (long i = 0; i < numberOfSegments; i++) {
				logger.log(Level.INFO, "Segment  "+ i);
				
				tasks.add(new DumperThread(
						tableWriter,
						dumpOutputPath, 
						dumpFilePattern,
						dumpMaxSizeInRows,
						schemaName,
						tableName,
						index));
				index += dumpMaxSizeInRows;
				logger.log(Level.INFO, "Index "+i+"");
			}
			logger.log(Level.INFO, "Identified "+tasks.size()+" segments for a pool of "+threads+" threads");
			//Run tasks in a fixed thread pool
			taskExecutor = Executors.newFixedThreadPool(threads);
			// invokeAll() returns when all tasks are complete
			List<Future<Integer>> futures = taskExecutor.invokeAll(tasks);

			int flag = 0;
			for (Future<Integer> f : futures) {
				Integer res = f.get();
				if (!f.isDone()){
					flag = 1;
					logger.log(Level.INFO, "Thread finished with no sources dumped");
				}
			}
			
			if (flag == 0){
				logger.log(Level.INFO, "DUMP SUCCEEDED");
			} else {
				logger.log(Level.SEVERE, "DUMP FAILED");
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

	private static long calculateCount(String schemaName, String tableName) {
		long count = 0;
		String sql = "select count(*) from " + schemaName + "." + tableName;
		Connection  con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			con = JDBCPoolSingleton.getInstance().getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next())
				count = rs.getLong(1);
			
		} catch (SQLException | PropertyVetoException e) {
			logger.log(Level.SEVERE, "Exception calculating count of the table");
			e.printStackTrace();
		}
		return count;
	}
}
