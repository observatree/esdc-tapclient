package esac.archive.gacs.sl.services.status;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import esac.archive.gacs.sl.services.status.types.StatusDataFactory;
import esac.archive.gacs.sl.test.TestUtils;
import esac.archive.gacs.sl.test.http.DummyHttpRequest;
import esac.archive.gacs.sl.test.http.DummyHttpResponse;
import esac.archive.gacs.sl.test.tap.DummyTapServiceConnection;
import esavo.tap.TAPException;
import esavo.uws.UwsException;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.test.uws.DummyUwsFactory.StorageType;

public class TaskStatusTest {
	
	public static final String TEST_APP_ID = "__TEST__" + TaskStatusTest.class.getName();
	
	private static DummyTapServiceConnection service;
	
	@BeforeClass
	public static void beforeClass() throws UwsException, TAPException{
		service = new DummyTapServiceConnection(TEST_APP_ID, StorageType.database);
	}
	
	@AfterClass
	public static void afterClass(){
		service.clearStorage();
	}

	@Test
	public void test1() throws Exception{
		StatusManager.enableCheckThread(false);
		//DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
		//DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
		//String appid = "appid_test";
		//DummyTapServiceConnection<ResultSet> sc = new DummyTapServiceConnection<ResultSet>(appid, dbConnection);
		//TaskStatus ts = new TaskStatus<ResultSet>(sc);
		
		//boolean useDatabase = true;
		//DummyTapServiceConnection sc = new DummyTapServiceConnection(TEST_APP_ID, useDatabase);
		TaskStatus ts = new TaskStatus(service);


		Map<String,String> reqParams = new HashMap<String, String>();
		//reqParams
		DummyHttpRequest request = TestUtils.createSimpleHttpUploadGetRequest(reqParams);
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		ts.executeRequest(request, response);
		
		if(!TestUtils.findErrorInJSon(response.getOutputAsString())){
			Assert.fail("Expected error: no parameter " + TaskStatus.PARAM_TASKID);
		}
		

		//param task id provided (missing task type)
		reqParams.put(TaskStatus.PARAM_TASKID, "NotANumber");
		request = TestUtils.createSimpleHttpUploadGetRequest(reqParams);
		response.clearOutput();
		ts.executeRequest(request, response);
		
		if(!TestUtils.findErrorInJSon(response.getOutputAsString())){
			Assert.fail("Expected error: no parameter " + TaskStatus.PARAM_TASKTYPE);
		}
		
		//Invalid task type
		reqParams.put(TaskStatus.PARAM_TASKTYPE, "xxxInvalidType");
		request = TestUtils.createSimpleHttpUploadGetRequest(reqParams);
		response.clearOutput();
		ts.executeRequest(request, response);

		if(!TestUtils.findErrorInJSon(response.getOutputAsString())){
			Assert.fail("Expected error: invalid task type");
		}
		
		//Valid task type, taskid is not a number
		reqParams.put(TaskStatus.PARAM_TASKTYPE, TaskType.UPLOAD.name());
		request = TestUtils.createSimpleHttpUploadGetRequest(reqParams);
		response.clearOutput();
		ts.executeRequest(request, response);

		if(!TestUtils.findErrorInJSon(response.getOutputAsString())){
			Assert.fail("Expected error: invalid task id (expected a number)");
		}
		
		//Valid task type, task id is a number. StatusManager does not contain info about task id '1' => error
		long id = 1;
		reqParams.put(TaskStatus.PARAM_TASKID, ""+id);
		request = TestUtils.createSimpleHttpUploadGetRequest(reqParams);
		response.clearOutput();
		ts.executeRequest(request, response);
		
		if(!TestUtils.findErrorInJSon(response.getOutputAsString())){
			Assert.fail("Expected error: invalid task id (expected a number)");
		}

		//Update StatusManager with a valid task id
		StatusManager sm = StatusManager.getInstance();
		
		//DefaultJobOwner owner = TapUtils.createJobOwner("test");
		UwsJobOwner owner = new UwsJobOwner("anonymous", UwsJobOwner.ROLE_USER);
		UserInfo userInfo = new UserInfo(owner);
		id = sm.createUserIdentifier(userInfo);

		reqParams.put(TaskStatus.PARAM_TASKID, ""+id);
		request = TestUtils.createSimpleHttpUploadGetRequest(reqParams);
		response.clearOutput();
		ts.executeRequest(request, response);
		
		if(TestUtils.findErrorInJSon(response.getOutputAsString())){
			Assert.fail("Unexpected error");
		}
		
		if(!TestUtils.findValueInJSon(response.getOutputAsString(), "")){
			Assert.fail("Expected error: invalid task id (expected a number)");
		}
		
		//Put a status for the task
		String value = "50";
		StatusData sd = StatusDataFactory.createStatusData(TaskType.UPLOAD, value);
		sm.updateStatus(id, sd);

		response.clearOutput();
		ts.executeRequest(request, response);

		System.out.println(response.getOutputAsString());
		if(TestUtils.findErrorInJSon(response.getOutputAsString())){
			Assert.fail("Unexpected error");
		}
		
		if(!TestUtils.findValueInJSon(response.getOutputAsString(), value)){
			Assert.fail("Expected error: invalid task id (expected a number)");
		}

	}

}
