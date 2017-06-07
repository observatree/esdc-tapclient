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
package esac.archive.gacs.sl.services.admin.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import esac.archive.gacs.sl.services.admin.Manager;
import esac.archive.gacs.sl.services.admin.Templates;
import esac.archive.gacs.sl.test.TestUtils;
import esac.archive.gacs.sl.test.http.DummyHttpResponse;
import esac.archive.gacs.sl.test.tap.DummyTapDatabaseConnection;
import esac.archive.gacs.sl.test.tap.DummyTapServiceConnection;
import esac.archive.gacs.sl.test.tap.DummyTapServiceFactory;
import esavo.tap.TAPException;
import esavo.uws.UwsException;
import esavo.uws.security.UwsSecurity;
import esavo.uws.storage.UwsStorage;
import esavo.uws.test.DatabaseUtils;
import esavo.uws.test.database.DummyData;
import esavo.uws.test.database.DummyDatabaseConnection;
import esavo.uws.test.uws.DummyUwsFactory.StorageType;

public class HandlersTest {
	
	private static final String TEST_APP_ID = "__TEST__" + HandlersTest.class.getName();
	
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
	public void testJobsListHandler() throws IOException{
		JobsListHandler h = new JobsListHandler();
		Assert.assertEquals("jobs_list", h.getActionIdentifier());
		Assert.assertFalse(h.canHandle(JobsListHandler.ACTION+"x"));
		Assert.assertTrue(h.canHandle(JobsListHandler.ACTION));
		
//		Map<String,String> parameters = new HashMap<String, String>();
//		DummyHttpResponse response = new DummyHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
		
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		Map<String,String> params = new HashMap<String, String>();
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		
		UwsStorage uwsStorage = service.getFactory().getStorageManager();
		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		String outputResponse;
		
//		UwsJobOwner user = new UwsJobOwner(TEST_APP_ID, UwsJobOwner.ROLE_USER);
//		user.setAuthUsername(TEST_APP_ID);
//		security.setUser(user);
		

		database.clearFlags();
		dbConn.clear();
		dbConn.clearExecutedQueries();
		
		String queryExecuted = "SELECT j.job_id, j.owner_id, j.phase_id, j.start_time, j.end_time, j.relative_path, p.string_representation FROM "
				+ "uws2_schema.jobs_meta AS j, uws2_schema.job_parameters p WHERE "
				+ "(j.job_id = p.job_id) AND (p.parameter_id = 'query') ORDER BY j.owner_id";

		DummyData data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), (String[][])null);
		dbConn.setDataForQuery(queryExecuted, data);


		//h.handle(parameters, response, dbConnection);
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		Assert.assertEquals("[\n]\n", outputResponse);
//		TestUtils.checkDbAction(database, 
//				DummyTapDatabaseConnection.ACTION_CMD_RETRIEVE_JOBS_BY_FILTER,
//				"offset: -1, limit: -1, filter: ");
		TestUtils.checkDbQuery(dbConn, queryExecuted);
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();
		
		//filters
		String jobid = "job1";
		String ownerid = "owner1";
		String phaseid = "phase1";
		String query = "query1";
		long startTimeInit = 1L;
		long startTimeLimit = 2L;
		long endTimeInit = 3L;
		long endTimeLimit = 4L;
		long offset = 100;
		long limit = 10;
		params.put(Manager.PARAM_JOB_ID, jobid);
		params.put(Manager.PARAM_OWNER_ID, ownerid);
		params.put(Manager.PARAM_PHASE_ID, phaseid);
		params.put(Manager.PARAM_QUERY, query);
		params.put(Manager.PARAM_START_TIME_INIT, ""+startTimeInit);
		params.put(Manager.PARAM_END_TIME_INIT, ""+endTimeInit);
		params.put(Manager.PARAM_START_TIME_LIMIT, ""+startTimeLimit);
		params.put(Manager.PARAM_END_TIME_LIMIT, ""+endTimeLimit);
		params.put(Manager.PARAM_QUERY_RESULTS_OFFSET, ""+offset);
		params.put(Manager.PARAM_QUERY_RESULTS_LIMIT, ""+limit);

		queryExecuted = "SELECT j.job_id, j.owner_id, j.phase_id, j.start_time, j.end_time, j.relative_path, p.string_representation FROM uws2_schema.jobs_meta AS j, uws2_schema.job_parameters p WHERE (j.job_id = p.job_id) AND (p.parameter_id = 'query') AND (j.job_id ILIKE '%job1%') AND (j.owner_id ILIKE '%owner1%') AND (j.phase_id = 'phase1') AND (p.string_representation ILIKE '%query1%') AND (j.start_time >= 1 AND j.start_time <= 2) AND (j.end_time >= 3 AND j.end_time <= 4) ORDER BY j.owner_id OFFSET 100 LIMIT 10";
		data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), (String[][])null);
		dbConn.setDataForQuery(queryExecuted, data);

		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		Assert.assertEquals("[\n]\n", outputResponse);
//		TestUtils.checkDbAction(database,
//				DummyTapDatabaseConnection.ACTION_CMD_RETRIEVE_JOBS_BY_FILTER, 
//				"offset: "+offset+", limit: "+limit+", filter: Filter: jobid: "+jobid+", ownerid: "+ownerid+", phaseid: "+phaseid+",\n"+
//				"Start time: "+startTimeInit+", End time: "+endTimeInit+",\n"+
//				"query: "+query+",\n"+
//				"Start time limit: "+startTimeLimit+", End time limit: " + endTimeLimit);
		TestUtils.checkDbQuery(dbConn, queryExecuted);
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();
		
		params.put(Manager.PARAM_JOB_ID, "");
		params.put(Manager.PARAM_OWNER_ID, "");
		params.put(Manager.PARAM_PHASE_ID, "");
		params.put(Manager.PARAM_QUERY, "");
		params.put(Manager.PARAM_START_TIME_INIT, "");
		params.put(Manager.PARAM_END_TIME_INIT, "");
		params.put(Manager.PARAM_START_TIME_LIMIT, "");
		params.put(Manager.PARAM_END_TIME_LIMIT, "");
		params.put(Manager.PARAM_QUERY_RESULTS_OFFSET, ""+offset);
		params.put(Manager.PARAM_QUERY_RESULTS_LIMIT, ""+limit);
		
		queryExecuted = "SELECT j.job_id, j.owner_id, j.phase_id, j.start_time, j.end_time, j.relative_path, p.string_representation FROM uws2_schema.jobs_meta AS j, uws2_schema.job_parameters p WHERE (j.job_id = p.job_id) AND (p.parameter_id = 'query') ORDER BY j.owner_id OFFSET 100 LIMIT 10";
		data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), (String[][])null);
		dbConn.setDataForQuery(queryExecuted, data);

		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		Assert.assertEquals("[\n]\n", outputResponse);
//		TestUtils.checkDbAction(database,
//				DummyTapDatabaseConnection.ACTION_CMD_RETRIEVE_JOBS_BY_FILTER, 
//				"offset: "+offset+", limit: "+limit+", filter: ");
		TestUtils.checkDbQuery(dbConn, queryExecuted);
		
		//Generate exception
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();

		params.put(Manager.PARAM_JOB_ID, "");
		params.put(Manager.PARAM_OWNER_ID, "");
		params.put(Manager.PARAM_PHASE_ID, "");
		params.put(Manager.PARAM_QUERY, "");
		params.put(Manager.PARAM_START_TIME_INIT, "");
		params.put(Manager.PARAM_END_TIME_INIT, "");
		params.put(Manager.PARAM_START_TIME_LIMIT, "");
		params.put(Manager.PARAM_END_TIME_LIMIT, "");
		params.put(Manager.PARAM_QUERY_RESULTS_OFFSET, ""+offset);
		params.put(Manager.PARAM_QUERY_RESULTS_LIMIT, ""+limit);
		
		queryExecuted = "SELECT j.job_id, j.owner_id, j.phase_id, j.start_time, j.end_time, j.relative_path, p.string_representation FROM uws2_schema.jobs_meta AS j, uws2_schema.job_parameters p WHERE (j.job_id = p.job_id) AND (p.parameter_id = 'query') ORDER BY j.owner_id OFFSET 100 LIMIT 10";
		data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), (String[][])null);
		dbConn.setDataForQuery(queryExecuted, data);

		dbConn.enableGenerateExceptionRequested(queryExecuted);
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: exception requested.");
		}
	}

	@Test
	public void testUsersListHandler() throws IOException{
		UsersListHandler h = new UsersListHandler();
		Assert.assertEquals("users_list", h.getActionIdentifier());
		Assert.assertFalse(h.canHandle(UsersListHandler.ACTION+"x"));
		Assert.assertTrue(h.canHandle(UsersListHandler.ACTION));
		
//		Map<String,String> parameters = new HashMap<String, String>();
//		DummyHttpResponse response = new DummyHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
		
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		Map<String,String> params = new HashMap<String, String>();
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		
		UwsStorage uwsStorage = service.getFactory().getStorageManager();
		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		String outputResponse;
		
		String queryExecuted = "SELECT owner_id FROM uws2_schema.owners ORDER BY owner_id";
		DummyData data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), (String[][])null);
		dbConn.setDataForQuery(queryExecuted, data);

		h.handle(params, response, uwsStorage);
		String result = response.getOutputAsString();
		Assert.assertEquals("[\n]\n", result);
//		TestUtils.checkDbAction(dbConnection, 
//				DummyTapDatabaseConnection.ACTION_CMD_RETRIEVE_USERS_BY_FILTER,
//				"offset: -1, limit: -1, filter: ");
		TestUtils.checkDbQuery(dbConn, queryExecuted);
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();
		
		//filters
		String userid = "user1";
		long offset = 100;
		long limit = 10;
		params.put(Manager.PARAM_USER_ID, userid);
		params.put(Manager.PARAM_QUERY_RESULTS_OFFSET, ""+offset);
		params.put(Manager.PARAM_QUERY_RESULTS_LIMIT, ""+limit);
		
		queryExecuted = "SELECT owner_id FROM uws2_schema.owners WHERE owner_id ILIKE '%user1%' ORDER BY owner_id OFFSET 100 LIMIT 10";
		data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), (String[][])null);
		dbConn.setDataForQuery(queryExecuted, data);

		h.handle(params, response, uwsStorage);
		result = response.getOutputAsString();
		Assert.assertEquals("[\n]\n", result);
//		TestUtils.checkDbAction(dbConnection,
//				DummyTapDatabaseConnection.ACTION_CMD_RETRIEVE_USERS_BY_FILTER, 
//				"offset: "+offset+", limit: "+limit+", filter: Filter: id: "+userid+
//				", roles: 0, quota db: 0, current size db: 0, quota file: 0, current size file: 0");
		TestUtils.checkDbQuery(dbConn, queryExecuted);
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();
		
		params.put(Manager.PARAM_USER_ID, "");
		params.put(Manager.PARAM_QUERY_RESULTS_OFFSET, ""+offset);
		params.put(Manager.PARAM_QUERY_RESULTS_LIMIT, ""+limit);
		
		queryExecuted = "SELECT owner_id FROM uws2_schema.owners ORDER BY owner_id OFFSET 100 LIMIT 10";
		data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), (String[][])null);
		dbConn.setDataForQuery(queryExecuted, data);

		h.handle(params, response, uwsStorage);
		result = response.getOutputAsString();
		Assert.assertEquals("[\n]\n", result);
//		TestUtils.checkDbAction(dbConnection,
//				DummyTapDatabaseConnection.ACTION_CMD_RETRIEVE_USERS_BY_FILTER, 
//				"offset: "+offset+", limit: "+limit+", filter: ");
		TestUtils.checkDbQuery(dbConn, queryExecuted);		

		//Generate exception
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();
		
		dbConn.enableGenerateExceptionRequested(queryExecuted);
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: exception requested.");
		}
	}

	@Test
	public void testUserDetailsHandler() throws IOException{
		UserDetailsHandler h = new UserDetailsHandler();
		Assert.assertEquals("user_details", h.getActionIdentifier());
		Assert.assertFalse(h.canHandle(UserDetailsHandler.ACTION+"x"));
		Assert.assertTrue(h.canHandle(UserDetailsHandler.ACTION));
		
//		Map<String,String> parameters = new HashMap<String, String>();
//		DummyHttpResponse response = new DummyHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);

		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		Map<String,String> params = new HashMap<String, String>();
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		
		UwsStorage uwsStorage = service.getFactory().getStorageManager();
		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		String outputResponse;

		//No userid: error
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: no userid parameter.");
		}

		database.clearFlags();
		dbConn.clear();
		response.clearOutput();
		
		String userid = "user1";
		String role = "1";
		params.put(Manager.PARAM_USER_ID, userid);

		String queryExecuted = "SELECT auth_name, pseudo, roles FROM uws2_schema.owners WHERE owner_id = 'user1'";
		DummyData data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), (String[][])null);
		dbConn.setDataForQuery(queryExecuted, data);

		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: no user found.");
		}
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();

		data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), new String[][]{{userid,userid,role}});
		dbConn.setDataForQuery(queryExecuted, data);
		
		TestUtils.setUserData(dbConn, userid, role);
		
//		String queryExecuted2 = "SELECT parameter_id, data_type, string_representation FROM uws2_schema.owner_parameters WHERE owner_id = 'user1'";
//		DummyData data2 = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted2), 
//				new String[][]{
//			{"db_quota","Long","1000"},
//			{"db_current_size","Long","100"},
//			{"files_quota","Long","2000"},
//			{"files_current_size","Long","200"}
//			});
//		dbConn.setDataForQuery(queryExecuted2, data2);
//		
//		String queryExecuted3 = "SELECT tap_schema.db_user_usage('user1')";
//		DummyData data3 = DatabaseUtils.createDummyData(
//				new String[]{"db_user_usage"},
//				new String[][]{
//			{"105"}
//			});
//		dbConn.setDataForQuery(queryExecuted3, data3);
//		
//		dbConn.setDataForQuery(
//				"UPDATE uws2_schema.owners SET auth_name = 'user1', pseudo = 'user1', roles = 1 WHERE owner_id = 'user1'", 
//				DatabaseUtils.createInsertOrUpdateData(1));
//		
//		dbConn.setDataForQuery(
//				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '100' WHERE parameter_id = 'db_current_size' AND owner_id = 'user1'", 
//				DatabaseUtils.createInsertOrUpdateData(1));
//		
//		dbConn.setDataForQuery(
//				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '2000' WHERE parameter_id = 'files_quota' AND owner_id = 'user1'",
//				DatabaseUtils.createInsertOrUpdateData(1));
//		
//		dbConn.setDataForQuery(
//				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '1000' WHERE parameter_id = 'db_quota' AND owner_id = 'user1'",
//				DatabaseUtils.createInsertOrUpdateData(1));
//
//		dbConn.setDataForQuery(
//				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '200' WHERE parameter_id = 'files_current_size' AND owner_id = 'user1'",
//				DatabaseUtils.createInsertOrUpdateData(1));


//		TestUtils.checkDbAction(dbConnection, 
//				DummyTapDatabaseConnection.ACTION_CMD_LOAD_USER_DETAILS,
//				userid);
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		Assert.assertEquals("[\n{\"id\": \"user1\", \"roles\": \"1\", \"quota_db\": \"1000\", \"curent_size_db\": \"105\", \"quota_files\": \"2000\", \"current_size_files\": \"0\"}]\n", outputResponse);
		TestUtils.checkDbQuery(dbConn, queryExecuted);
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();
		

		//DB Exception
		dbConn.enableGenerateExceptionRequested(queryExecuted);
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: exception requested.");
		}
	}
	
	

	@Test
	public void testUserUpdateHandler() throws IOException{
		UserUpdateHandler h = new UserUpdateHandler();
		Assert.assertEquals("user_update", h.getActionIdentifier());
		Assert.assertFalse(h.canHandle(UserUpdateHandler.ACTION+"x"));
		Assert.assertTrue(h.canHandle(UserUpdateHandler.ACTION));
		
//		Map<String,String> parameters = new HashMap<String, String>();
//		DummyHttpResponse response = new DummyHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);

		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		Map<String,String> params = new HashMap<String, String>();
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		
		UwsStorage uwsStorage = service.getFactory().getStorageManager();
		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		String outputResponse;

		//No userid: error
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: no userid parameter.");
		}

		database.clearFlags();
		dbConn.clear();
		response.clearOutput();
		
		String userid = "user1";
		params.put(Manager.PARAM_USER_ID, userid);
		
		String queryExecuted = "SELECT auth_name, pseudo, roles FROM uws2_schema.owners WHERE owner_id = 'user1'";
		DummyData data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), (String[][])null);
		dbConn.setDataForQuery(queryExecuted, data);

		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: no user found.");
		}
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();

		String role = "1";
		data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), new String[][]{{userid,userid,role}});
		dbConn.setDataForQuery(queryExecuted, data);
		
		TestUtils.setUserData(dbConn, userid, role);

		//No DB quota
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: no quota db.");
		}
		
		database.clearFlags();
		//dbConn.clear();
		response.clearOutput();
		
		long quotadb = 100;
		params.put(Manager.PARAM_QUOTA_DB, ""+quotadb);

		//No files quota
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: no quota files.");
		}
		
		database.clearFlags();
		//dbConn.clear();
		response.clearOutput();
		
		long quotaFiles = 500;
		params.put(Manager.PARAM_QUOTA_FILES, ""+quotaFiles);
		
		//No roles
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: no quota files.");
		}
		
		database.clearFlags();
		//dbConn.clear();
		response.clearOutput();
		
		int roles = 1;
		params.put(Manager.PARAM_ROLES, ""+roles);
		
		dbConn.setDataForQuery(
				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '105' WHERE parameter_id = 'db_current_size' AND owner_id = 'user1'",
				DatabaseUtils.createInsertOrUpdateData(1));
		
		dbConn.setDataForQuery(
				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '500' WHERE parameter_id = 'files_quota' AND owner_id = 'user1'", 
				DatabaseUtils.createInsertOrUpdateData(1));

		dbConn.setDataForQuery(
				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '0' WHERE parameter_id = 'files_current_size' AND owner_id = 'user1'", 
				DatabaseUtils.createInsertOrUpdateData(1));

		dbConn.setDataForQuery(
				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '100' WHERE parameter_id = 'db_quota' AND owner_id = 'user1'", 
				DatabaseUtils.createInsertOrUpdateData(1));

		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		//{ "id": "User user1 updated." }
		String expected = Templates.JSON_SIMPLE_MSG.
				replaceAll("\\{0\\}", "\"id\": \"User "+userid+" updated.\"").
				replaceAll("'","") + "\n";
		Assert.assertEquals(expected, outputResponse);
//		TestUtils.checkDbAction(dbConnection, 
//				DummyTapDatabaseConnection.ACTION_CMD_UPDATE_USER_DETAILS, 
//				userid+","+roles+","+quotadb+","+quotaFiles);
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();

		//DB Exception
		dbConn.enableGenerateExceptionRequested(queryExecuted);
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: exception requested.");
		}
	}

	@Test
	public void testJobDetailsHandler() throws IOException{
		JobDetailsHandler h = new JobDetailsHandler();
		Assert.assertEquals("job_details", h.getActionIdentifier());
		Assert.assertFalse(h.canHandle(JobDetailsHandler.ACTION+"x"));
		Assert.assertTrue(h.canHandle(JobDetailsHandler.ACTION));
		
//		Map<String,String> parameters = new HashMap<String, String>();
//		DummyHttpResponse response = new DummyHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);

		
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		Map<String,String> params = new HashMap<String, String>();
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		
		UwsStorage uwsStorage = service.getFactory().getStorageManager();
		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		String outputResponse;

		//No userid: error
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: no jobid parameter.");
		}

		database.clearFlags();
		dbConn.clear();
		response.clearOutput();
		
		String jobid = "job";
		params.put(Manager.PARAM_JOB_ID, jobid);
		
		String queryExecuted = "SELECT owner_id, session_id, phase_id, quote, start_time, end_time, destruction_time, execution_duration, relative_path, list_id, priority FROM uws2_schema.jobs_meta WHERE job_id = 'job'";
		DummyData data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), (String[][])null);
		dbConn.setDataForQuery(queryExecuted, data);

		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: no jobid found.");
		}
		
		database.clearFlags();
		//dbConn.clear();
		response.clearOutput();
		
		String ownerid = "user1";
		String sessionid = "session";
		String phaseid = "PENDING";
		String quote = "1";
		String startTime = "1432785600000";
		String endTime = "1432785700000";
		String destructionTime = "1432785800000";
		String executionDuration = "10";
		String relativePath = "path";
		String listid = "ASYNC";
		String priority = "1";
		
		data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted), new String[][]{
			{ownerid, sessionid, phaseid, quote, startTime, endTime, destructionTime, executionDuration, relativePath, listid, priority}
		});
		dbConn.setDataForQuery(queryExecuted, data);

		TestUtils.setUserDataComplete(dbConn, "user1", "1");
//		String queryUser = "SELECT auth_name, pseudo, roles FROM uws2_schema.owners WHERE owner_id = 'user1'";
//		data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryUser), (String[][])null);
//		dbConn.setDataForQuery(queryUser, data);
//
//		data = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryUser), new String[][]{{"user1","user1","1"}});
//		dbConn.setDataForQuery(queryUser, data);
//		
//		TestUtils.setUserData(dbConn);

		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Unexpected error: job must be created.");
		}
		
		String expected = "{\"meta\": \n" + 
				"{\"job_id\": \"job\", \"owner_id\": \"user1\", \"phase_id\": \"PENDING\", \"start_time\": \"1432785600000\", \"end_time\": \"1432785700000\" , \"query\": \"\", \"relative_path\": \"path\"},\n"+
				"\"parameters\": [],\n"+
				"\"error_summary\": {\"message\": \"\", \"type\": \"\", \"details\": \"\"},\n"+
				"\"results\": []}\n";

		Assert.assertEquals(expected, outputResponse);
//		TestUtils.checkDbAction(dbConnection, 
//				DummyTapDatabaseConnection.ACTION_CMD_LOAD_JOB_DETAILS,
//				jobid);
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();

		//DB Exception
		dbConn.enableGenerateExceptionRequested(queryExecuted);
		h.handle(params, response, uwsStorage);
		outputResponse = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(outputResponse)){
			Assert.fail("Expected error: exception requested.");
		}
	}
	
//	private void setUserData(DummyDatabaseConnection dbConn){
//		String queryExecuted2 = "SELECT parameter_id, data_type, string_representation FROM uws2_schema.owner_parameters WHERE owner_id = 'user1'";
//		DummyData data2 = DatabaseUtils.createDummyData(DatabaseUtils.getColumnNamesFromSelectQuery(queryExecuted2), 
//				new String[][]{
//			{"db_quota","Long","1000"},
//			{"db_current_size","Long","100"},
//			{"files_quota","Long","2000"},
//			{"files_current_size","Long","200"}
//			});
//		dbConn.setDataForQuery(queryExecuted2, data2);
//		
//		String queryExecuted3 = "SELECT tap_schema.db_user_usage('user1')";
//		DummyData data3 = DatabaseUtils.createDummyData(
//				new String[]{"db_user_usage"},
//				new String[][]{
//			{"105"}
//			});
//		dbConn.setDataForQuery(queryExecuted3, data3);
//		
//		dbConn.setDataForQuery(
//				"UPDATE uws2_schema.owners SET auth_name = 'user1', pseudo = 'user1', roles = 1 WHERE owner_id = 'user1'", 
//				DatabaseUtils.createInsertOrUpdateData(1));
//		
//		dbConn.setDataForQuery(
//				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '100' WHERE parameter_id = 'db_current_size' AND owner_id = 'user1'", 
//				DatabaseUtils.createInsertOrUpdateData(1));
//		
//		dbConn.setDataForQuery(
//				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '2000' WHERE parameter_id = 'files_quota' AND owner_id = 'user1'",
//				DatabaseUtils.createInsertOrUpdateData(1));
//		
//		dbConn.setDataForQuery(
//				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '1000' WHERE parameter_id = 'db_quota' AND owner_id = 'user1'",
//				DatabaseUtils.createInsertOrUpdateData(1));
//
//		dbConn.setDataForQuery(
//				"UPDATE uws2_schema.owner_parameters SET data_type = 'Long', string_representation = '200' WHERE parameter_id = 'files_current_size' AND owner_id = 'user1'",
//				DatabaseUtils.createInsertOrUpdateData(1));
//
//	}
//

}
