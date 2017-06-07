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
package esac.archive.gacs.sl.services.upload;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import esac.archive.gacs.sl.test.TestUtils;
import esac.archive.gacs.sl.test.data.ReadData;
import esac.archive.gacs.sl.test.http.DummyHttpRequest;
import esac.archive.gacs.sl.test.http.DummyHttpResponse;
import esac.archive.gacs.sl.test.tap.DummyTapServiceConnection;
import esac.archive.gacs.sl.test.tap.DummyTapDatabaseConnection;
import esac.archive.gacs.sl.test.tap.DummyTapServiceFactory;
import esavo.tap.TAPException;
import esavo.tap.metadata.TAPMetadata;
import esavo.uws.UwsException;
import esavo.uws.config.UwsConfiguration;
import esavo.uws.jobs.parameters.UwsJobOwnerParameters;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.security.UwsSecurity;
import esavo.uws.test.DatabaseUtils;
import esavo.uws.test.database.DummyData;
import esavo.uws.test.database.DummyDatabaseConnection;
import esavo.uws.test.uws.DummyUwsFactory.StorageType;

public class UploadTest {
	
	private static final String TEST_APP_ID = "__TEST__" + UploadTest.class.getName();
	
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
	public void uploadTest() throws Exception {
		String file = TestUtils.DATA_DIR+"upload/test_user_table_upload_text_plain";
		String ownerAuthUserName = "test";
		String role = "0";
		String schema = TAPMetadata.getUserSchema(ownerAuthUserName);
		String tableName = "table1";
		String fullTableName = schema + "." + tableName;

//		DummyHttpRequest request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
//		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
//		DummyUWSDatabaseConnection uwsDbConnection = new DummyUWSDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
//		DummyServiceConnection<ResultSet> sc = new DummyServiceConnection<ResultSet>(dbConnection);
//		Upload<ResultSet> upload = new Upload<ResultSet>(sc);
//
//		DummyUserIdentifier userIdentifier = new DummyUserIdentifier(null); //set anonymous
//		sc.setUserIdentifier(userIdentifier);
		
		//Map<String, String> params = new HashMap<String, String>();;
		//String subcontext = "Manager";
		//String servletName = "Manager";
		DummyHttpRequest request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		security.setUser(null);
		
		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		UwsConfiguration config = service.getFactory().getConfiguration();
		config.setProperty("uws.upload.max_size", "10000");

		
//		userIdentifier.setAuthUserId(ownerAuthUserName);
//		dbConnection.clearFlags();
//		upload.executeRequest(request, response);
		
		UwsJobOwner user = new UwsJobOwner(ownerAuthUserName, UwsJobOwner.ROLE_USER);
		user.setAuthUsername(ownerAuthUserName);
		user.setPseudo(ownerAuthUserName);
		security.setUser(user);
		UwsJobOwnerParameters ownerParameters = TestUtils.createDefaultUserParameters();
		user.setParameters(ownerParameters);
		
		TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);
		
		try{
			Upload upload = new Upload(service);
			upload.executeRequest(request, response);
			
			Assert.assertTrue("IndexPk requested", database.isActionLogged(DummyTapDatabaseConnection.ACTION_CMD_REQ_PK, fullTableName));
			Assert.assertTrue("Table size requested", database.isActionLogged(DummyTapDatabaseConnection.ACTION_CMD_REQ_UPDATE_TABLE_SIZE, fullTableName));
			Assert.assertTrue("Vacuum requested", database.isActionLogged(DummyTapDatabaseConnection.ACTION_CMD_REQ_VACUUM, fullTableName));
			
			request.closeInputStream();
			response.closeOutputStream();
		}finally{
			File uploadDir = service.getFactory().getStorageManager().getStorageDir();
			if(uploadDir.exists()){
				TestUtils.clearDirectory(uploadDir);
			}
		}
	}

	
	@Test
	public void testUploadUserTable() throws Exception {
		String file = TestUtils.DATA_DIR+"upload/test_user_table_upload_text_plain";
		String ownerAuthUserName = "test";
		String role = "0";
		String schema = TAPMetadata.getUserSchema(ownerAuthUserName);
		String tableName = "table1";
		String fullTableName = schema + "." + tableName;
		UwsJobOwner user = null;

//		DummyHttpRequest request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
//		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
//		String appid = "appid_test";
//		DummyTapServiceConnection<ResultSet> sc = new DummyTapServiceConnection<ResultSet>(appid, dbConnection);
//		Upload<ResultSet> upload = new Upload<ResultSet>(sc);
		
		String responseOutput;
		DummyHttpRequest request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		security.setUser(null);

		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();

		UwsConfiguration config = service.getFactory().getConfiguration();
		config.setProperty("uws.upload.max_size", "10000");

		Upload upload = new Upload(service);

		//User not authenticated (null): no upload performed.
		try{
			upload.executeRequest(request, response);
			Assert.fail("Expected error: user not authenticated (null)");
		}catch(Exception e){
			//expected
		}
//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
//			Assert.fail("Expected error: user not authenticated (null)");
//		}
		
		try{
					
//			DummyUserIdentifier userIdentifier = new DummyUserIdentifier(null); //set anonymous
//			sc.setUserIdentifier(userIdentifier);
			
			ownerAuthUserName = "anonymous";
			user = new UwsJobOwner(ownerAuthUserName, UwsJobOwner.ROLE_USER);
			user.setAuthUsername(ownerAuthUserName);
			user.setPseudo(ownerAuthUserName);
			security.setUser(user);
			UwsJobOwnerParameters ownerParameters = TestUtils.createDefaultUserParameters();
			user.setParameters(ownerParameters);
			TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);

			database.clearFlags();
			response.clearOutput();
			
			//User not authenticated (anonymous): no upload performed.
			upload.executeRequest(request, response);
			responseOutput = response.getOutputAsString();
			if(!TestUtils.findErrorInHtml(responseOutput)){
				Assert.fail("Expected error: user not authenticated (anonymous)");
			}
	
			database.clearFlags();
			response.clearOutput();
		
	//			userIdentifier.setAuthUserId(ownerAuthUserName);
	//			dbConnection.clearFlags();
	//			upload.executeRequest(request, response);
	
			ownerAuthUserName = "test";
			role = "0";
			user = new UwsJobOwner(ownerAuthUserName, UwsJobOwner.ROLE_USER);
			user.setAuthUsername(ownerAuthUserName);
			user.setPseudo(ownerAuthUserName);
			security.setUser(user);
			user.setParameters(TestUtils.createDefaultUserParameters());
			TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);

			upload.executeRequest(request, response);
			
			Assert.assertTrue("IndexPk requested", database.isActionLogged(DummyTapDatabaseConnection.ACTION_CMD_REQ_PK, fullTableName));
			Assert.assertTrue("Table size requested", database.isActionLogged(DummyTapDatabaseConnection.ACTION_CMD_REQ_UPDATE_TABLE_SIZE, fullTableName));
			Assert.assertTrue("Vacuum requested", database.isActionLogged(DummyTapDatabaseConnection.ACTION_CMD_REQ_VACUUM, fullTableName));
			
			request.closeInputStream();
			response.closeOutputStream();
		}finally{
			File uploadDir = service.getFactory().getStorageManager().getStorageDir();
			if(uploadDir.exists()){
				TestUtils.clearDirectory(uploadDir);
			}
		}
	}
	
	@Test
	public void testUploadResults() throws Exception {
		String file = TestUtils.DATA_DIR+"upload/test_results_upload_text_plain";
		String ownerAuthUserName = "test";
		String role = "0";
		String schema = TAPMetadata.getUserSchema(ownerAuthUserName);
		String tableName = "table1";
		String fullTableName = schema + "." + tableName;
		UwsJobOwner user = null;

//		DummyHttpRequest request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
//		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
//		String appid = "appid_test";
//		DummyTapServiceConnection<ResultSet> sc = new DummyTapServiceConnection<ResultSet>(appid, dbConnection);
//		Upload<ResultSet> upload = new Upload<ResultSet>(sc);
		
		String responseOutput;
		DummyHttpRequest request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		security.setUser(null);

		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();

		UwsConfiguration config = service.getFactory().getConfiguration();
		config.setProperty("uws.upload.max_size", "10000");

		Upload upload = new Upload(service);

		//User not authenticated (null): no upload performed.
		try{
			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
			upload.executeRequest(request, response);
			Assert.fail("Expected error: user not authenticated (null)");
		}catch(Exception e){
			//expected
		}
//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
//			Assert.fail("Expected error: user not authenticated (null)");
//		}
		
		InputStream src = null;
		
		try{
					
//			DummyUserIdentifier userIdentifier = new DummyUserIdentifier(null); //set anonymous
//			sc.setUserIdentifier(userIdentifier);
			
			ownerAuthUserName = "anonymous";
			user = new UwsJobOwner(ownerAuthUserName, UwsJobOwner.ROLE_USER);
			user.setAuthUsername(ownerAuthUserName);
			user.setPseudo(ownerAuthUserName);
			security.setUser(user);
			UwsJobOwnerParameters ownerParameters = TestUtils.createDefaultUserParameters();
			user.setParameters(ownerParameters);
			TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);

			database.clearFlags();
			response.clearOutput();
			
			//User not authenticated (anonymous): no upload performed.
			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
			upload.executeRequest(request, response);
			responseOutput = response.getOutputAsString();
			if(!TestUtils.findErrorInHtml(responseOutput)){
				Assert.fail("Expected error: user not authenticated (anonymous)");
			}
	
			database.clearFlags();
			response.clearOutput();
		
	//			userIdentifier.setAuthUserId(ownerAuthUserName);
	//			dbConnection.clearFlags();
	//			upload.executeRequest(request, response);
	
			ownerAuthUserName = "test";
			role = "0";
			user = new UwsJobOwner(ownerAuthUserName, UwsJobOwner.ROLE_USER);
			user.setAuthUsername(ownerAuthUserName);
			user.setPseudo(ownerAuthUserName);
			security.setUser(user);
			user.setParameters(TestUtils.createDefaultUserParameters());
			TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);
			
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
			String query = "SELECT owner_id, session_id, phase_id, quote, start_time, end_time, destruction_time, "
					+ "execution_duration, relative_path, list_id, priority "
					+ "FROM uws2_schema.jobs_meta WHERE job_id = '12345'";
			DummyData data = DatabaseUtils.createDummyData(
					DatabaseUtils.getColumnNamesFromSelectQuery(query), 
					new String[][]{
				{ownerAuthUserName, sessionid, phaseid, quote, startTime, endTime, destructionTime, executionDuration, relativePath, listid, priority}
			});
			dbConn.setDataForQuery(query, data);
			TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);

			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
			upload.executeRequest(request, response);
			responseOutput = response.getOutputAsString();
			//No results
			if(!TestUtils.findErrorInHtml(responseOutput)){
				Assert.fail("Expected error: no results");
			}
			
			database.clearFlags();
			response.clearOutput();
			
			//TODO create results
			String queryResults = "SELECT result_id, type, mime_type, size, rows FROM uws2_schema.results_meta where job_id = '12345'";
			String resultid = "resultid";
			String resultType = "NONE";
			String mimeType = "";
			String size = "1";
			String rows = "1";
			data = DatabaseUtils.createDummyData(
					DatabaseUtils.getColumnNamesFromSelectQuery(queryResults), 
					new String[][]{
				{resultid, resultType, mimeType, size, rows}
			});
			dbConn.setDataForQuery(queryResults, data);
			
			File uploadDir = service.getFactory().getStorageManager().getStorageDir();
			File dst = new File(uploadDir,relativePath+"/"+resultid);
			src = ReadData.findResource(this.getClass(), TestUtils.DATA_DIR+"upload/test1");
			TestUtils.copyFile(src,dst);
			
			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
			upload.executeRequest(request, response);
			responseOutput = response.getOutputAsString();
			if(TestUtils.findErrorInHtml(responseOutput)){
				Assert.fail("Unexpected error: no results");
			}
			
			Assert.assertTrue("IndexPk requested", database.isActionLogged(DummyTapDatabaseConnection.ACTION_CMD_REQ_PK, fullTableName));
			Assert.assertTrue("Table size requested", database.isActionLogged(DummyTapDatabaseConnection.ACTION_CMD_REQ_UPDATE_TABLE_SIZE, fullTableName));
			Assert.assertTrue("Vacuum requested", database.isActionLogged(DummyTapDatabaseConnection.ACTION_CMD_REQ_VACUUM, fullTableName));
			
			request.closeInputStream();
			response.closeOutputStream();
		}finally{
			if(src != null){
				try{
					src.close();
				}catch(Exception e){
					
				}
			}
			File uploadDir = service.getFactory().getStorageManager().getStorageDir();
			if(uploadDir.exists()){
				TestUtils.clearDirectory(uploadDir);
			}
		}
	}
	
//	@Test
//	public void testUploadUserTableMissingParameters() throws Exception {
//		uploadMissingParameters(TestUtils.DATA_DIR+"upload/test_user_table_upload_text_plain");
//	}
	
//	@Test
//	public void testUploadResultsMissingParameters() throws Exception {
//		uploadMissingParameters(TestUtils.DATA_DIR+"upload/test_results_upload_text_plain");
//	}
//
	
	@Test
	public void testUploadUserTableMissingParameters() throws Exception {
		String file = TestUtils.DATA_DIR+"upload/test_user_table_upload_text_plain";
		String ownerAuthUserName = "test";
		String schema = TAPMetadata.getUserSchema(ownerAuthUserName);
		String tableName = "table1";
		String fullTableName = schema + "." + tableName;
		String role = "0";

//		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
//		String appid = "appid_test";
//		DummyTapServiceConnection<ResultSet> sc = new DummyTapServiceConnection<ResultSet>(appid, dbConnection);
//		Upload<ResultSet> upload = new Upload<ResultSet>(sc);
//		DummyUserIdentifier userIdentifier = new DummyUserIdentifier(null); //set anonymous
//		sc.setUserIdentifier(userIdentifier);
//		userIdentifier.setAuthUserId(ownerAuthUserName);
		
		String responseOutput;
		DummyHttpRequest request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		security.setUser(null);

		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();

		UwsConfiguration config = service.getFactory().getConfiguration();
		config.setProperty("uws.upload.max_size", "10000");

		Upload upload = new Upload(service);

		

		//Missing table name
		//DummyHttpRequest request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "TABLE_NAME");
		request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "TABLE_NAME");
		try{
			upload.executeRequest(request, response);
			Assert.fail("Expected error: user not authenticated (null)");
		}catch(Exception e){
			
		}

		database.clearFlags();
		response.clearOutput();
		
		try{
			ownerAuthUserName = "test";
			role = "0";
			UwsJobOwner user = new UwsJobOwner(ownerAuthUserName, UwsJobOwner.ROLE_USER);
			user.setAuthUsername(ownerAuthUserName);
			user.setPseudo(ownerAuthUserName);
			security.setUser(user);
			user.setParameters(TestUtils.createDefaultUserParameters());
			TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);

			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "TABLE_NAME");
			upload.executeRequest(request, response);
			responseOutput = response.getOutputAsString();
			if(!TestUtils.findErrorInHtml(responseOutput)){
				Assert.fail("Unexepcted error: ra not found while dec is provided");
			}

			database.clearFlags();
			response.clearOutput();
	
			//new behavior: if you provide ra you must provide dec, or if you privide dec, you must provide ra
			//(because the index ra/dec needs both)
			//It is allowed to not provide ra nor dec, but if you provide one of them, you must provide the other too.
			
			//No ra
			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "RACOL");
			upload.executeRequest(request, response);
	
			//Ra no longer mandatory
	//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
	//			Assert.fail("Expected error: ra not found");
	//		}
			
			//Ra mandatory if you provide dec
			responseOutput = response.getOutputAsString();
			if(!TestUtils.findErrorInHtml(responseOutput)){
				Assert.fail("Unexepcted error: ra not found while dec is provided");
			}
	
			database.clearFlags();
			response.clearOutput();
			
			//No dec
			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "DECCOL");
			upload.executeRequest(request, response);
	
	//		Dec no longer mandatory
	//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
	//			Assert.fail("Expected error: dec not found");
	//		}
			
			//Dec mandatory if you provide ra
			responseOutput = response.getOutputAsString();
			if(!TestUtils.findErrorInHtml(responseOutput)){
				Assert.fail("Unexpected error: dec not found while ra is provided");
			}
	
			database.clearFlags();
			response.clearOutput();
			
	//		//No taskid
	//		request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "TASKID");
	//		upload.executeRequest(request, response);
	//
	//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
	//			Assert.fail("Expected error: no taskid found");
	//		}
	//
	//		dbConnection.clearFlags();
	//		response.clearOutput();
			
			//No file
			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "FILE");
			upload.executeRequest(request, response);
			responseOutput = response.getOutputAsString();
			if(!TestUtils.findErrorInHtml(responseOutput)){
				Assert.fail("Expected error: no file found");
			}
	
			database.clearFlags();
			response.clearOutput();
	
			//Normal use cases are already covered by other tests
	
			request.closeInputStream();
			response.closeOutputStream();
		}finally{
			File uploadDir = service.getFactory().getStorageManager().getStorageDir();
			if(uploadDir.exists()){
				TestUtils.clearDirectory(uploadDir);
			}
		}

	}

	@Test
	public void testUploadResultsMissingParameters() throws Exception {
		String file = TestUtils.DATA_DIR+"upload/test_results_upload_text_plain";
		String ownerAuthUserName = "test";
		String schema = TAPMetadata.getUserSchema(ownerAuthUserName);
		String tableName = "table1";
		String fullTableName = schema + "." + tableName;
		String role = "0";

//		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
//		String appid = "appid_test";
//		DummyTapServiceConnection<ResultSet> sc = new DummyTapServiceConnection<ResultSet>(appid, dbConnection);
//		Upload<ResultSet> upload = new Upload<ResultSet>(sc);
//		DummyUserIdentifier userIdentifier = new DummyUserIdentifier(null); //set anonymous
//		sc.setUserIdentifier(userIdentifier);
//		userIdentifier.setAuthUserId(ownerAuthUserName);
		
		String responseOutput;
		DummyHttpRequest request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file);
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		security.setUser(null);

		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		database.clearFlags();
		dbConn.clear();
		response.clearOutput();

		UwsConfiguration config = service.getFactory().getConfiguration();
		config.setProperty("uws.upload.max_size", "10000");

		Upload upload = new Upload(service);

		

		//Missing table name
		//DummyHttpRequest request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "TABLE_NAME");
		request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "TABLE_NAME");
		try{
			upload.executeRequest(request, response);
			Assert.fail("Expected error: user not authenticated (null)");
		}catch(Exception e){
			
		}

		database.clearFlags();
		response.clearOutput();

		InputStream src = null;
		
		try{
			ownerAuthUserName = "test";
			role = "0";
			UwsJobOwner user = new UwsJobOwner(ownerAuthUserName, UwsJobOwner.ROLE_USER);
			user.setAuthUsername(ownerAuthUserName);
			user.setPseudo(ownerAuthUserName);
			security.setUser(user);
			user.setParameters(TestUtils.createDefaultUserParameters());
			TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);

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
			String query = "SELECT owner_id, session_id, phase_id, quote, start_time, end_time, destruction_time, "
					+ "execution_duration, relative_path, list_id, priority "
					+ "FROM uws2_schema.jobs_meta WHERE job_id = '12345'";
			DummyData data = DatabaseUtils.createDummyData(
					DatabaseUtils.getColumnNamesFromSelectQuery(query), 
					new String[][]{
				{ownerAuthUserName, sessionid, phaseid, quote, startTime, endTime, destructionTime, executionDuration, relativePath, listid, priority}
			});
			dbConn.setDataForQuery(query, data);
			TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);
			
			String queryResults = "SELECT result_id, type, mime_type, size, rows FROM uws2_schema.results_meta where job_id = '12345'";
			String resultid = "resultid";
			String resultType = "NONE";
			String mimeType = "";
			String size = "1";
			String rows = "1";
			data = DatabaseUtils.createDummyData(
					DatabaseUtils.getColumnNamesFromSelectQuery(queryResults), 
					new String[][]{
				{resultid, resultType, mimeType, size, rows}
			});
			dbConn.setDataForQuery(queryResults, data);
			
			File uploadDir = service.getFactory().getStorageManager().getStorageDir();
			File dst = new File(uploadDir,relativePath+"/"+resultid);
			src = ReadData.findResource(this.getClass(), TestUtils.DATA_DIR+"upload/test1");
			TestUtils.copyFile(src,dst);

			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "TABLE_NAME");
			upload.executeRequest(request, response);
			responseOutput = response.getOutputAsString();
			if(!TestUtils.findErrorInHtml(responseOutput)){
				Assert.fail("Unexepcted error: ra not found while dec is provided");
			}

			database.clearFlags();
			response.clearOutput();
	
			//new behavior: if you provide ra you must provide dec, or if you privide dec, you must provide ra
			//(because the index ra/dec needs both)
			//It is allowed to not provide ra nor dec, but if you provide one of them, you must provide the other too.
			
			//No ra
			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "RACOL");
			upload.executeRequest(request, response);
	
			//Ra no longer mandatory
	//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
	//			Assert.fail("Expected error: ra not found");
	//		}
			
			//Ra mandatory if you provide dec
			responseOutput = response.getOutputAsString();
			if(!TestUtils.findErrorInHtml(responseOutput)){
				Assert.fail("Unexepcted error: ra not found while dec is provided");
			}
	
			database.clearFlags();
			response.clearOutput();
			
			//No dec
			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "DECCOL");
			upload.executeRequest(request, response);
	
	//		Dec no longer mandatory
	//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
	//			Assert.fail("Expected error: dec not found");
	//		}
			
			//Dec mandatory if you provide ra
			responseOutput = response.getOutputAsString();
			if(!TestUtils.findErrorInHtml(responseOutput)){
				Assert.fail("Unexpected error: dec not found while ra is provided");
			}
	
			database.clearFlags();
			response.clearOutput();
			
	//		//No taskid
	//		request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "TASKID");
	//		upload.executeRequest(request, response);
	//
	//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
	//			Assert.fail("Expected error: no taskid found");
	//		}
	//
	//		dbConnection.clearFlags();
	//		response.clearOutput();
			
//			//No file
//			request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "FILE");
//			upload.executeRequest(request, response);
//			responseOutput = response.getOutputAsString();
//			if(!TestUtils.findErrorInHtml(responseOutput)){
//				Assert.fail("Expected error: no file found");
//			}
//	
//			database.clearFlags();
//			response.clearOutput();
	
			//Normal use cases are already covered by other tests
	
			request.closeInputStream();
			response.closeOutputStream();
		}finally{
			if(src != null){
				try{
					src.close();
				}catch(Exception e){
				}
			}
			File uploadDir = service.getFactory().getStorageManager().getStorageDir();
			if(uploadDir.exists()){
				TestUtils.clearDirectory(uploadDir);
			}
		}


	}
	
//	private void uploadMissingParameters(String file) throws Exception {
//				
//		String ownerAuthUserName = "test";
//		String schema = TAPMetadata.getUserSchema(ownerAuthUserName);
//		String tableName = "table1";
//		String fullTableName = schema + "." + tableName;
//
//		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
//		String appid = "appid_test";
//		DummyTapServiceConnection<ResultSet> sc = new DummyTapServiceConnection<ResultSet>(appid, dbConnection);
//		Upload<ResultSet> upload = new Upload<ResultSet>(sc);
//		DummyUserIdentifier userIdentifier = new DummyUserIdentifier(null); //set anonymous
//		sc.setUserIdentifier(userIdentifier);
//		userIdentifier.setAuthUserId(ownerAuthUserName);
//		
//
//		//Missing table name
//		DummyHttpRequest request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "TABLE_NAME");
//		upload.executeRequest(request, response);
//
//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
//			Assert.fail("Expected error: user not authenticated (anonymous)");
//		}
//
//		dbConnection.clearFlags();
//		response.clearOutput();
//		
//		//new behavior: if you provide ra you must provide dec, or if you privide dec, you must provide ra
//		//(because the index ra/dec needs both)
//		//It is allowed to not provide ra nor dec, but if you provide one of them, you must provide the other too.
//		
//		//No ra
//		request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "RACOL");
//		upload.executeRequest(request, response);
//
//		//Ra no longer mandatory
////		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
////			Assert.fail("Expected error: ra not found");
////		}
//		
//		//Ra mandatory if you provide dec
//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
//			Assert.fail("Unexepcted error: ra not found while dec is provided");
//		}
//
//		dbConnection.clearFlags();
//		response.clearOutput();
//		
//		//No dec
//		request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "DECCOL");
//		upload.executeRequest(request, response);
//
////		Dec no longer mandatory
////		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
////			Assert.fail("Expected error: dec not found");
////		}
//		
//		//Dec mandatory if you provide ra
//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
//			Assert.fail("Unexpected error: dec not found while ra is provided");
//		}
//
//		dbConnection.clearFlags();
//		response.clearOutput();
//		
////		//No taskid
////		request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "TASKID");
////		upload.executeRequest(request, response);
////
////		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
////			Assert.fail("Expected error: no taskid found");
////		}
////
////		dbConnection.clearFlags();
////		response.clearOutput();
//		
//		//No file
//		request = TestUtils.createMultipartUploadHttpRequest(this.getClass(), file, "FILE");
//		upload.executeRequest(request, response);
//
//		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
//			Assert.fail("Expected error: no file found");
//		}
//
//		dbConnection.clearFlags();
//		response.clearOutput();
//
//		//Normal use cases are already covered by other tests
//
//		request.closeInputStream();
//		response.closeOutputStream();
//	}
	
	@Test
	public void testRemoval() throws Exception {
//		String ownerAuthUserName = "test";
//		String schema = TAPMetadata.getUserSchema(ownerAuthUserName);
//		String tableName = "table1";
//		String fullTableName = schema + "." + tableName;

//		Map<String,String> reqParams = new HashMap<String, String>();
//		reqParams.put(Upload.PARAM_TABLENAME, tableName);
//		reqParams.put(Upload.PARAM_DELETE, "DELETE");
//		DummyHttpRequest request = TestUtils.createSimpleHttpUploadGetRequest(reqParams);
//		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
//		String appid = "appid_test";
//		DummyTapServiceConnection<ResultSet> sc = new DummyTapServiceConnection<ResultSet>(appid, dbConnection);
//		Upload<ResultSet> upload = new Upload<ResultSet>(sc);
		
		
		Map<String, String> params = null;
		String subcontext = "Upload";
		String servletName = "Upload";
		DummyHttpRequest request = TestUtils.createSimpleHttpGetRequest(subcontext, servletName, params);
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		String tableName = "user_test.table1";
		
		params = new HashMap<String, String>();
		params.put(Upload.PARAM_TABLENAME, tableName);
		params.put(Upload.PARAM_DELETE, "DELETE");
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		security.setUser(null);
		
		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		UwsJobOwner user = new UwsJobOwner(TEST_APP_ID, UwsJobOwner.ROLE_USER);
		user.setAuthUsername(TEST_APP_ID);
		//security.setUser(user);
		

		database.clearFlags();
		response.clearOutput();
		dbConn.clearExecutedQueries();

		UwsConfiguration config = service.getFactory().getConfiguration();
		config.setProperty("uws.upload.max_size", "10000");

		Upload upload = new Upload(service);

		//User not authenticated (null): no upload performed.
		try{
			upload.executeRequest(request, response);
			Assert.fail("Expected error: user not authenticated (null)");
		}catch(Exception e){
			
		}
		
		database.clearFlags();
		response.clearOutput();
		
		String ownerAuthUserName = "anonymous";
		String role = "0";
		user = new UwsJobOwner(ownerAuthUserName, UwsJobOwner.ROLE_USER);
		user.setAuthUsername(ownerAuthUserName);
		user.setPseudo(ownerAuthUserName);
		security.setUser(user);
		UwsJobOwnerParameters ownerParameters = TestUtils.createDefaultUserParameters();
		user.setParameters(ownerParameters);
		TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);

		database.clearFlags();
		response.clearOutput();
		
		//User not authenticated (anonymous): no upload performed.
		request = TestUtils.createSimpleHttpGetRequest(subcontext, servletName, params);
		upload.executeRequest(request, response);
		String responseOutput = response.getOutputAsString();
		if(!TestUtils.findErrorInHtml(responseOutput)){
			Assert.fail("Expected error: user not authenticated (anonymous)");
		}

		database.clearFlags();
		response.clearOutput();
		
		
		ownerAuthUserName = "test";
		role = "0";
		user = new UwsJobOwner(ownerAuthUserName, UwsJobOwner.ROLE_USER);
		user.setAuthUsername(ownerAuthUserName);
		user.setPseudo(ownerAuthUserName);
		security.setUser(user);
		user.setParameters(TestUtils.createDefaultUserParameters());
		TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);

		upload.executeRequest(request, response);
		responseOutput = response.getOutputAsString();
		if(TestUtils.findErrorInHtml(responseOutput)){
			Assert.fail("Unexpected error");
		}

		Assert.assertTrue("Drop table", database.isActionLogged(DummyTapDatabaseConnection.ACTION_CMD_DROP_TABLE_FORCE, tableName));
		Assert.assertTrue("Unregister from tap", database.isActionLogged(DummyTapDatabaseConnection.ACTION_CMD_UNREGISTER_FROM_TAP, tableName));
		
		request.closeInputStream();
		response.closeOutputStream();
	}
	
	@Test
	public void testRemovalMissingParams() throws Exception {
//		String ownerAuthUserName = "test";
//		String schema = TAPMetadata.getUserSchema(ownerAuthUserName);
//		String tableName = "table1";
//		String fullTableName = schema + "." + tableName;
//
//		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
//		DummyDatabaseConnection uwsDbConnection = new DummyDatabaseConnection();
//		DummyTapDatabaseConnection dbConnection = new DummyTapDatabaseConnection(uwsDbConnection);
//		String appid = "appid_test";
//		DummyTapServiceConnection<ResultSet> sc = new DummyTapServiceConnection<ResultSet>(appid, dbConnection);
//		Upload<ResultSet> upload = new Upload<ResultSet>(sc);
//		DummyUserIdentifier userIdentifier = new DummyUserIdentifier(null); //set anonymous
//		sc.setUserIdentifier(userIdentifier);
//		userIdentifier.setAuthUserId(ownerAuthUserName);
//
//		Map<String,String> reqParams = new HashMap<String, String>();
//
//		dbConnection.clearFlags();
//		reqParams.clear();
//		
//		//No table
//		DummyHttpRequest request = TestUtils.createSimpleHttpUploadGetRequest(reqParams);
//		upload.executeRequest(request, response);
		
		
		Map<String, String> params = null;
		String subcontext = "Upload";
		String servletName = "Upload";
		DummyHttpRequest request = TestUtils.createSimpleHttpGetRequest(subcontext, servletName, params);
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		String tableName = "user_test.table1";
		
		params = new HashMap<String, String>();
		params.put(Upload.PARAM_DELETE, "DELETE");
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		security.setUser(null);
		
		DummyTapServiceFactory factory = (DummyTapServiceFactory)service.getFactory();
		DummyDatabaseConnection dbConn = factory.getDatabaseConnection();
		
		DummyTapDatabaseConnection database = factory.getDummyDatabaseConnection();
		
		UwsJobOwner user = new UwsJobOwner(TEST_APP_ID, UwsJobOwner.ROLE_USER);
		user.setAuthUsername(TEST_APP_ID);
		//security.setUser(user);
		
		database.clearFlags();
		response.clearOutput();
		dbConn.clearExecutedQueries();

		UwsConfiguration config = service.getFactory().getConfiguration();
		config.setProperty("uws.upload.max_size", "10000");

		Upload upload = new Upload(service);

		String ownerAuthUserName = "test";
		String role = "0";
		user = new UwsJobOwner(ownerAuthUserName, UwsJobOwner.ROLE_USER);
		user.setAuthUsername(ownerAuthUserName);
		user.setPseudo(ownerAuthUserName);
		security.setUser(user);
		user.setParameters(TestUtils.createDefaultUserParameters());
		TestUtils.setUserDataComplete(dbConn, ownerAuthUserName, role);

		//No table
		request = TestUtils.createSimpleHttpGetRequest(subcontext, servletName, params);
		upload.executeRequest(request, response);
		
		if(!TestUtils.findErrorInHtml(response.getOutputAsString())){
			Assert.fail("Expected error: user not authenticated (anonymous)");
		}

		//the normal use case is already tested in 'testRemoval'

		request.closeInputStream();
		response.closeOutputStream();
	}

}
