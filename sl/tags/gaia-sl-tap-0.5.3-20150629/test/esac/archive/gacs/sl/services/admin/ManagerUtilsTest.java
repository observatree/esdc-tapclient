package esac.archive.gacs.sl.services.admin;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import esac.archive.gacs.sl.services.util.Utils;
import esac.archive.gacs.sl.test.http.DummyHttpResponse;

public class ManagerUtilsTest {
	
	@Test
	public void testWriteMsg() throws IOException{
		DummyHttpResponse response = new DummyHttpResponse();
		int httpErrorCode = 500;
		String expectedContentType = "text/html";
		String id = "id";
		String explanation = "explanation";
		String expected = Templates.getSimpleJsonMsg(id, explanation) + '\n';
		ManagerUtils.writeMsg(response, httpErrorCode, id, explanation);
		String result = response.getOutputAsString();
		Assert.assertEquals("Basic msg", expected, result);
		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
	}
	
	@Test
	public void testWriteError() throws IOException {
		DummyHttpResponse response = new DummyHttpResponse();
		String expectedContentType = "text/html";
		int httpErrorCode = 500;
		String error = "Error";
		String extraMsg = "extraMsg";
		String expected = Templates.getErrorMessage("Manager error", error, extraMsg) + '\n';
		ManagerUtils.writeError(response, httpErrorCode, error, extraMsg);
		String result = response.getOutputAsString();
		Assert.assertEquals("Basic msg", expected, result);
		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
		
		response.clearOutput();
		
		ManagerUtils.writeError(response, httpErrorCode, null);
		expected = Templates.getErrorMessage("Manager error", "Unknown error", null) + '\n';
		result = response.getOutputAsString();
		Assert.assertEquals("Basic exception(null) msg", expected, result);
		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
		Assert.assertEquals("Content type", expectedContentType, response.getContentType());

		response.clearOutput();
		
		String message = "ExceptionMsg";
		Throwable t = new Throwable(message);
		expected = Templates.getErrorMessage("Manager error", t.getMessage(), Templates.getPlainText(Utils.dumpStackTrace(t))) + '\n';
		ManagerUtils.writeError(response, httpErrorCode, t);
		result = response.getOutputAsString();
		Assert.assertEquals("Basic exception msg", expected, result);
		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
		
		response.clearOutput();
		
		expected = Templates.getErrorMessage("Manager error", error, null) + '\n';
		ManagerUtils.writeError(response, httpErrorCode, error, (Throwable)null);
		result = response.getOutputAsString();
		Assert.assertEquals("Basic exception msg", expected, result);
		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
		
		response.clearOutput();

		expected = Templates.getErrorMessage("Manager error", error + "<br/>" + t.getMessage(), Templates.getPlainText(Utils.dumpStackTrace(t))) + '\n';
		ManagerUtils.writeError(response, httpErrorCode, error, t);
		result = response.getOutputAsString();
		Assert.assertEquals("Basic exception msg", expected, result);
		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
	}
	
	@Test
	public void testDumpStackTrace(){
		Assert.assertEquals("Null stack trace", "", Utils.dumpStackTrace(null));
		Throwable t = new Throwable();
		//check it works
		System.out.println(Utils.dumpStackTrace(t));
	}
	
	private String skipStackTrace(String s){
		int init = s.indexOf("<code>");
		if(init < 0){
			return s;
		}
		int end = s.indexOf("</code>", init);
		if(end < 0){
			return s;
		}
		return s.substring(0, init) + s.substring(end+7);
	}
	
//	@Test
//	public void testWriteUserList() throws IOException{
//		DummyHttpResponse response = new DummyHttpResponse();
//		int httpErrorCode = 500;
//		String expectedContentType = "application/json";
//
//		String expected = "[\n]\n";
//		ManagerUtils.writeUserList(response, httpErrorCode, null);
//		String result = response.getOutputAsString();
//		Assert.assertEquals("Null user list", expected, result);
//		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
//		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
//		
//		response.clearOutput();
//		
//		List<UserDetails> userList = new ArrayList<UserDetails>();
//		ManagerUtils.writeUserList(response, httpErrorCode, userList);
//		result = response.getOutputAsString();
//		Assert.assertEquals("Empty user list", expected, result);
//		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
//		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
//		
//		response.clearOutput();
//
//		UserDetails ud1 = new UserDetails();
//		ud1.setId("id1");
//		UserDetails ud2 = new UserDetails();
//		ud2.setId("id2");
//		userList.add(ud1);
//		userList.add(ud2);
//		
//		expected = "[\n"+Templates.getJsonUserDetails(ud1)+",\n"+Templates.getJsonUserDetails(ud2)+"]\n";
//		ManagerUtils.writeUserList(response, httpErrorCode, userList);
//		result = response.getOutputAsString();
//		Assert.assertEquals(expected, result);
//		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
//		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
//	}
	
//	@Test
//	public void testWriteJobList() throws IOException {
//		DummyHttpResponse response = new DummyHttpResponse();
//		int httpErrorCode = 500;
//		String expectedContentType = "application/json";
//
//		String expected = "[\n]\n";
//		ManagerUtils.writeJobsList(response, httpErrorCode, null);
//		String result = response.getOutputAsString();
//		Assert.assertEquals("Null job list", expected, result);
//		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
//		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
//		
//		response.clearOutput();
//		
//		List<JobDetails> jobList = new ArrayList<JobDetails>();
//		ManagerUtils.writeJobsList(response, httpErrorCode, jobList);
//		result = response.getOutputAsString();
//		Assert.assertEquals("Empty job list", expected, result);
//		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
//		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
//		
//		response.clearOutput();
//
//		JobDetails job1 = new JobDetails();
//		job1.setJobid("id1");
//		JobDetails job2 = new JobDetails();
//		job2.setJobid("id2");
//		jobList.add(job1);
//		jobList.add(job2);
//		
//		expected = "[\n"+Templates.getJsonJobDetails(job1)+",\n"+Templates.getJsonJobDetails(job2)+"]\n";
//		ManagerUtils.writeJobsList(response, httpErrorCode, jobList);
//		result = response.getOutputAsString();
//		Assert.assertEquals(expected, result);
//		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
//		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
//	}
	
//	@Test
//	public void testWriteJobDetails() throws IOException {
//		DummyHttpResponse response = new DummyHttpResponse();
//		int httpErrorCode = 500;
//		String expectedContentType = "application/json";
//		
//		String expected = "{}\n";
//		ManagerUtils.writeJobDetails(response, httpErrorCode, null);
//		String result = response.getOutputAsString();
//		Assert.assertEquals("Null job details", expected, result);
//		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
//		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
//		
//		JobDetails job = new JobDetails();
//		job.setJobid("id1");
//
//		response.clearOutput();
//
//		expected =
//			"{\"meta\": \n" +
//			Templates.getJsonJobDetails(job) +
//			",\n" +
//			"\"parameters\": [" + 
//			"],\n" + 
//			"\"error_summary\": " + 
//			Templates.getJsonJobErrorSummary(job.getError()) + 
//			",\n" +
//			"\"results\": [" + 
//			"]}\n";
//		
//		ManagerUtils.writeJobDetails(response, httpErrorCode, job);
//		result = response.getOutputAsString();
//		Assert.assertEquals("(A)", expected, result);
//		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
//		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
//
//		response.clearOutput();
//
//		JobDetailsError error = new JobDetailsError();
//		error.setErrorSummaryMessage("msg");
//		error.setErrorSummaryType("type");
//		error.setErrorSummaryDetails("details");
//		job.setError(error);
//		
//		JobDetailsParameter parameter1 = new JobDetailsParameter();
//		parameter1.setId("paramid1");
//		parameter1.setDataType("dataType1");
//		parameter1.setParameterType("paramType1");
//		parameter1.setStringRepresentation("stringRep1");
//
//		JobDetailsParameter parameter2 = new JobDetailsParameter();
//		parameter2.setId("paramid2");
//		parameter2.setDataType("dataType2");
//		parameter2.setParameterType("paramType2");
//		parameter2.setStringRepresentation("stringRep2");
//		
//		List<JobDetailsParameter> parameters = new ArrayList<JobDetailsParameter>();
//		parameters.add(parameter1);
//		parameters.add(parameter2);
//		job.setParameters(parameters);
//		
//		JobDetailsResults r1 = new JobDetailsResults();
//		r1.setId("resultid1");
//		r1.setHref("rHref1");
//		r1.setMimeType("rMime1");
//		r1.setType("rType1");
//		r1.setRows(101L);
//		r1.setSize(501L);
//
//		JobDetailsResults r2 = new JobDetailsResults();
//		r2.setId("resultid2");
//		r2.setHref("rHref2");
//		r2.setMimeType("rMime2");
//		r2.setType("rType2");
//		r2.setRows(102L);
//		r2.setSize(502L);
//		
//		List<JobDetailsResults> results = new ArrayList<JobDetailsResults>();
//		results.add(r1);
//		results.add(r2);
//		job.setResults(results);
//
//		expected =
//				"{\"meta\": \n" +
//				Templates.getJsonJobDetails(job) +
//				",\n" +
//				"\"parameters\": [" + 
//				Templates.getJsonJobParameters(parameter1) + ",\n" +
//				Templates.getJsonJobParameters(parameter2) +
//				"],\n" + 
//				"\"error_summary\": " + 
//				Templates.getJsonJobErrorSummary(job.getError()) + 
//				",\n" +
//				"\"results\": [" + 
//				Templates.getJsonJobResult(r1) + ",\n" +
//				Templates.getJsonJobResult(r2) +
//				"]}\n";
//			
//		ManagerUtils.writeJobDetails(response, httpErrorCode, job);
//		result = response.getOutputAsString();
//		Assert.assertEquals("(A)", expected, result);
//		Assert.assertEquals("Http code", httpErrorCode, response.getStatus());
//		Assert.assertEquals("Content type", expectedContentType, response.getContentType());
//	}
	
//	@Test
//	public void testParameters(){
//		String property = "test_manager_utils_test";
//		//String prevValue = System.getProperty(property);
//		String wrongValue = "a";
//		long defaultValue = 555L;
//		long validValue = 7L;
//		
//		String appid = "appid_test";
//		
//		//System.clearProperty(property);
//		Assert.assertEquals("Property not found", defaultValue, ManagerUtils.getLongFromProperty(appid, property, defaultValue));
//		
//		//Wrong format
//		//System.setProperty(property, wrongValue);
//		EnvironmentManager.setPropertyIfNotNull(appid, property, wrongValue);
//		Assert.assertEquals("Wrong format", defaultValue, ManagerUtils.getLongFromProperty(appid, property, defaultValue));
//		
//		//Valid value
//		//System.setProperty(property, ""+validValue);
//		EnvironmentManager.setPropertyIfNotNull(appid, property, ""+validValue);
//		Assert.assertEquals("Valid value", validValue, ManagerUtils.getLongFromProperty(appid, property, defaultValue));
//		
////		if(prevValue != null){
////			System.setProperty(property, prevValue);
////		}else{
////			System.clearProperty(property);
////		}
//		
//		Assert.assertEquals("Null value (II)", defaultValue, ManagerUtils.getLongFromParameter(null, defaultValue));
//		Assert.assertEquals("Wrong format (II)", defaultValue, ManagerUtils.getLongFromParameter(wrongValue, defaultValue));
//		Assert.assertEquals("Valid value (II)", validValue, ManagerUtils.getLongFromParameter(""+validValue, defaultValue));
//	}
	

}
