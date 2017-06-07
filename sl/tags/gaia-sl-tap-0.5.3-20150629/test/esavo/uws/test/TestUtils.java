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
package esavo.uws.test;

import java.io.File;
import java.text.MessageFormat;

import esavo.uws.jobs.UwsJob;
import esavo.uws.jobs.UwsJobPhase;
import esavo.uws.storage.jdbc.UwsJdbcStorageSingleton;
import esavo.uws.test.database.DummyData;
import esavo.uws.test.database.DummyDatabaseConnection;

/**
 * Utilities for test-harnesses.<br/>
 * In order to set sql data, you can see {@link DummyData}
 * 
 * @author juan.carlos.segovia@sciops.esa.int
 *
 */
public class TestUtils {
	public static final String DATA_DIR = "/esavo/uws/test/data/";
	
	
	private DummyDatabaseConnection dummyUWSDatabaseConnection;
	
	public TestUtils(){
		
	}
	
	public DummyDatabaseConnection getDummyUWSDatabaseConnection(){
		return dummyUWSDatabaseConnection;
	}

	public void createAndSetDefaultHandlers(){
		createAndSetUWSDatabaseHandler();
	}
	
	public void createAndSetUWSDatabaseHandler(){
		dummyUWSDatabaseConnection = new DummyDatabaseConnection();
		UwsJdbcStorageSingleton.setDummyConnection(dummyUWSDatabaseConnection);
	}
	
//	public static DummyUwsService createDummyService(){
//		DummyUwsService uwsService = new DummyUwsService();
//		String appid = "appid_test";
//		DummyUwsFactory uwsFactory = new DummyUwsFactory(appid);
//		uwsService.setFactory(uwsFactory);
//		DummyUwsLog uwsLogger = new DummyUwsLog();
//		uwsService.setLogger(uwsLogger);
//		return uwsService;
//	}
	
	public static void generateExceptionWhenSettingJobPhase(DummyDatabaseConnection connection, UwsJobPhase executionPhase, String jobid){
		String query = MessageFormat.format(DatabaseUtils.QUERY_UPDATE_JOB_EXEC_PHASE, new Object[]{
				"phase_id", "'"+executionPhase.name()+"'","'"+jobid+"'"
		});
		connection.enableGenerateExceptionRequested(query);
	}
	
	public static void waitForJobFinished(UwsJob job){
		while(!job.isPhaseFinished()){
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				throw new RuntimeException("Wait interrupted", e);
			}
		}
	}
	
	public static void removeDirectory(File f){
		if(f == null){
			return;
		}
		if(f.isDirectory()){
			File[] fContent = f.listFiles();
			if(fContent != null){
				for(File fTmp: fContent){
					removeDirectory(fTmp);
				}
			}
		}
		f.delete();
	}
	

	

}
