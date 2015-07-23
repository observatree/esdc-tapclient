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
