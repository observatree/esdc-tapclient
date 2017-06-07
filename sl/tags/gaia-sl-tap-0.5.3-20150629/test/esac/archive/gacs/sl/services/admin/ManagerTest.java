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
package esac.archive.gacs.sl.services.admin;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import esac.archive.gacs.sl.test.TestUtils;
import esac.archive.gacs.sl.test.http.DummyHttpRequest;
import esac.archive.gacs.sl.test.http.DummyHttpResponse;
import esac.archive.gacs.sl.test.tap.DummyTapServiceConnection;
import esac.archive.gacs.sl.test.tap.DummyTapDatabaseConnection;
import esac.archive.gacs.sl.test.tap.DummyTapServiceFactory;
import esavo.tap.TAPException;
import esavo.uws.UwsException;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.security.UwsSecurity;
import esavo.uws.test.database.DummyDatabaseConnection;
import esavo.uws.test.uws.DummyUwsFactory.StorageType;

public class ManagerTest {
	
	private static final String TEST_APP_ID = "__TEST__" + ManagerTest.class.getName();
	
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
	public void dummyTest(){
		
	}
	
	@Test
	public void testGetUserList() throws Exception {
//		String result;
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
//		String appid = "appid_test";
//		DummyTapServiceConnection<ResultSet> sc = new DummyTapServiceConnection<ResultSet>(appid, dbConnection);
//		
//		String subcontext = "Manager";
//		String servletName = "Manager";
//		Map<String,String> reqParams = new HashMap<String, String>();
//		
//		DummyHttpRequest request = TestUtils.createSimpleHttpGetRequest(subcontext, servletName, reqParams);
//		DummyHttpResponse response = new DummyHttpResponse();
		
		Map<String, String> params = null;
		String subcontext = "Manager";
		String servletName = "Manager";
		DummyHttpRequest request = TestUtils.createSimpleHttpGetRequest(subcontext, servletName, params);
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		params = new HashMap<String, String>();
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		
		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		UwsJobOwner user = new UwsJobOwner(TEST_APP_ID, UwsJobOwner.ROLE_USER);
		user.setAuthUsername(TEST_APP_ID);
		//security.setUser(user);
		

		database.clearFlags();
		response.clearOutput();
		dbConn.clearExecutedQueries();

		
		//Manager<ResultSet> manager = new Manager<ResultSet>(sc);
		Manager manager = new Manager(service);
		String outputResponse;
		
		//Anonymous user
		manager.executeRequest(request, response);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: anonymous user");
		}
		
		database.clearFlags();
		response.clearOutput();
		dbConn.clearExecutedQueries();

		//Authenticated user: normal user
		security.setUser(user);

//		DummyUserIdentifier userIdentifier = new DummyUserIdentifier(null); //set anonymous
//		sc.setUserIdentifier(userIdentifier);
//		
//		dbConnection.clearFlags();
//		response.clearOutput();

		manager.executeRequest(request, response);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: normal user");
		}

//		dbConnection.clearFlags();
//		response.clearOutput();
//
//		userIdentifier.setAuthUserId("not-allowed-user");
		
		database.clearFlags();
		response.clearOutput();
		dbConn.clearExecutedQueries();
		
		//Admin user
		user = new UwsJobOwner(TEST_APP_ID, UwsJobOwner.ROLE_ADMIN);
		user.setAuthUsername(TEST_APP_ID);
		security.setUser(user);

		manager.executeRequest(request, response);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: no action provided");
		}
		
		database.clearFlags();
		response.clearOutput();
		dbConn.clearExecutedQueries();

		
//		//check queries:
//		TestUtils.checkDbActions(dbConnection, 
//				new String[]{
//					DummyTapDatabaseConnection.ACTION_CMD_LOAD_USER_DETAILS,
//					DummyTapDatabaseConnection.ACTION_CMD_CREATE_USER}, 
//				new String[]{
//					"not-allowed-user",
//					"id: not-allowed-user, roles: 0, quota db: 100, current size db: 0, quota file: 1000, current size file: 0"});
//
//		dbConnection.clearFlags();
//		response.clearOutput();
//
//		//provide an admin user. There is no action => error 
//		UserDetails userDetails = new UserDetails();
//		userDetails.setId("userid");
//		userDetails.setRoles(UserDetails.ROLE_ADMIN);
//		dbConnection.setUserDetails(userDetails);
//		
//		userIdentifier.setAuthUserId("userid");
//
//		manager.executeRequest(request, response);
//		result = response.getOutputAsString();
//		if(!TestUtils.findErrorInHtml(result)){
//			Assert.fail("Expected error: no action provided");
//		}
//
//		TestUtils.checkDbAction(dbConnection, 
//				DummyTapDatabaseConnection.ACTION_CMD_LOAD_USER_DETAILS, 
//				"userid");
//		
//		dbConnection.clearFlags();
//		response.clearOutput();
//
//		//Provide action
//		reqParams.put(Manager.PARAM_ACTION, UsersListHandler.ACTION);
//		request = TestUtils.createSimpleHttpGetRequest(subcontext, servletName, reqParams);
//		manager.executeRequest(request, response);
//		result = response.getOutputAsString();
//		Assert.assertEquals("[\n]\n", result);
//		TestUtils.checkDbAction(dbConnection, 
//				DummyTapDatabaseConnection.ACTION_CMD_RETRIEVE_USERS_BY_FILTER, 
//				"offset: -1, limit: -1, filter: ");
//
//		dbConnection.clearFlags();
//		response.clearOutput();

		//invalid action
		params.put("ACTION", "xxx");
		request = TestUtils.createSimpleHttpGetRequest(subcontext, servletName, params);
		manager.executeRequest(request, response);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: invalid action");
		}
		
		database.clearFlags();
		response.clearOutput();
		dbConn.clearExecutedQueries();
		
		//valid action
		params.put("ACTION", "user_details");
		params.put("USERID", user.getId());
		request = TestUtils.createSimpleHttpGetRequest(subcontext, servletName, params);
		manager.executeRequest(request, response);
		outputResponse = response.getOutputAsString();

		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: requested DB exception");
		}

		database.clearFlags();
		response.clearOutput();
		dbConn.clearExecutedQueries();

//
//		//Check exception error
//		dbConnection.setRaiseException(true);
//		manager.executeRequest(request, response);
//		result = response.getOutputAsString();
//		if(!TestUtils.findErrorInHtml(result)){
//			Assert.fail("Expected error: requested DB exception");
//		}
//		
//		dbConnection.clearFlags();
//		response.clearOutput();
//		
//		dbConnection.setRaiseExceptionByCounter(1);
//		manager.executeRequest(request, response);
//		result = response.getOutputAsString();
//		if(!TestUtils.findErrorInHtml(result)){
//			Assert.fail("Expected error: requested DB exception");
//		}
		
	}
	
//	@Test
//	public void testOthers(){
//		JobDetails job = new JobDetails();
//		System.out.println(job.toString());
//		
//		UserDetails user = new UserDetails();
//		System.out.println(user.toString());
//		Assert.assertFalse("Default user is not adming", user.isAdmin());
//		user.setRoles(UserDetails.ROLE_ADMIN);
//		Assert.assertTrue(user.isAdmin());
//		
//		JobsFilter jobFilter = new JobsFilter();
//		System.out.println(jobFilter.toString());
//		
//		UsersFilter userFilter = new UsersFilter();
//		System.out.println(userFilter.toString());
//	}

}
