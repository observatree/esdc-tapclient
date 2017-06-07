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
package esac.archive.gacs.sl.services.share;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import esavo.uws.jobs.UwsJob;
import esavo.uws.jobs.UwsJobErrorSummaryMeta;
import esavo.uws.jobs.UwsJobResultMeta;
import esavo.uws.jobs.parameters.UwsJobParameters;
import esavo.uws.jobs.utils.UwsJobDetails;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.share.UwsShareUser;

/**
 * Manager utilities
 * 
 * @author juan.carlos.segovia@sciops.esa.int
 */
public class ShareUtils {
	
	private static final String GENERIC_ERROR_MSG = "Share manager error";
	
	/**
	 * Writes a simple JSON message: <code>{ "id": "value" }</code>
	 * @param response communication handler.
	 * @param httpErrorCode http status code.
	 * @param id id keyword.
	 * @param explanation value.
	 * @throws IOException
	 */
	public static void writeMsg(HttpServletResponse response, int httpErrorCode, String id, String explanation) throws IOException{
		response.setStatus(httpErrorCode);
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		pw.println(Templates.getSimpleJsonMsg(id, explanation));
		pw.flush();
	}
	
	/**
	 * Writes an error message (HTML).
	 * @param response communication handler.
	 * @param httpErrorCode http status code.
	 * @param error error message.
	 * @param extraMsg extra message.
	 * @throws IOException
	 */
	public static void writeError(HttpServletResponse response, int httpErrorCode, String error, String extraMsg) throws IOException{
		response.setStatus(httpErrorCode);
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		pw.println(Templates.getErrorMessage(GENERIC_ERROR_MSG, error, extraMsg));
		pw.flush();
	}
	
	/**
	 * Writes an error (HTML) from an exception.
	 * @param response communication handler.
	 * @param httpErrorCode http status code.
	 * @param t exception (can be null).
	 * @throws IOException
	 */
	public static void writeError(HttpServletResponse response, int httpErrorCode, Throwable t) throws IOException {
		response.setStatus(httpErrorCode);
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		if(t == null){
			pw.println(Templates.getErrorMessage(GENERIC_ERROR_MSG, "Unknown error", null));
		}else{
			pw.println(Templates.getErrorMessage(GENERIC_ERROR_MSG, t.getMessage(), Templates.getPlainText(dumpStackTrace(t))));
		}
		pw.flush();
	}

	/**
	 * Writes an error (HTML) from an exception using the 'error' parameter also.
	 * @param response communication handler.
	 * @param httpErrorCode http status code.
	 * @param error error message.
	 * @param t excpetion (can be null).
	 * @throws IOException
	 */
	public static void writeError(HttpServletResponse response, int httpErrorCode, String error, Throwable t) throws IOException{
		response.setStatus(httpErrorCode);
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		if(t == null){
			pw.println(Templates.getErrorMessage(GENERIC_ERROR_MSG, error, null));
		}else{
			pw.println(Templates.getErrorMessage(GENERIC_ERROR_MSG, error + "<br/>" + t.getMessage(), Templates.getPlainText(dumpStackTrace(t))));
		}
		pw.flush();
	}

	/**
	 * Returns an string with the stack trace.
	 * @param t
	 * @return an string with the stack trace.
	 */
	public static String dumpStackTrace (Throwable t){
		if(t == null){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(StackTraceElement ste: t.getStackTrace()){
			sb.append(ste).append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * Writes a list of users (text).
	 * <pre><tt>
	 * Each record: &lt;userid&gt;\n
	 * </tt></pre>
	 * @param response
	 * @param httpCode
	 * @param users
	 * @throws IOException
	 */
	public static void writeUsers(HttpServletResponse response, int httpCode, List<UwsShareUser> users) throws IOException {
		response.setStatus(httpCode);
		response.setContentType(UwsOutputResponseHandler.CONTENT_TYPE_TEXT_PLAIN);
		PrintWriter pw = response.getWriter();
		if(users == null){
			return;
		}
		for(UwsShareUser user: users){
			pw.println(user.getId() + ": " + user.getName());
		}
		pw.flush();
	}
	
	/**
	 * Writes a list of users (JSON): <code>[ record1, record2...]</code> <br/>
	 * Each record is composed of: {@link Templates#JSON_JOB_LIST_RECORD} 
	 * @param response communication handler
	 * @param httpCode http status code
	 * @param users users list (can be null)
	 * @throws IOException
	 */
	public static void writeUserList(HttpServletResponse response, int httpCode, List<UwsJobOwner> users) throws IOException {
		response.setStatus(httpCode);
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		pw.println("[");
		boolean firstTime = true;
		String row;
		if(users != null){
			for(UwsJobOwner user: users){
				if(firstTime){
					firstTime = false;
				}else{
					pw.println(",");
				}
				row = Templates.getJsonUserDetails(user);
				pw.print(row);
			}
		}
		pw.println("]");
		pw.flush();
	}
	
	/**
	 * Writes a list of jobs (JSON): <code>[ record1, record2...]</code> <br/>
	 * Each record is composed of: {@link Templates#JSON_JOB_LIST_RECORD}}
	 * @param response communication handler.
	 * @param httpCode http status code.
	 * @param jobs jobs lists (can be null).
	 * @throws IOException
	 */
	public static void writeJobsList(HttpServletResponse response, int httpCode, List<UwsJobDetails> jobs) throws IOException {
		response.setStatus(httpCode);
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		pw.println("[");
		boolean firstTime = true;
		String row;
		if(jobs != null){
			for(UwsJobDetails job: jobs){
				if(firstTime){
					firstTime = false;
				}else{
					pw.println(",");
				}
				row = Templates.getJsonJobDetails(job);
				pw.print(row);
			}
		}
		pw.println("]");
		pw.flush();
	}
	
	/**
	 * Writes a job details JSON: <code>{"meta": jobMetaData, "parameters": [ param1, param2...], "error_summary": jobErrorSummary, "results": [ result1, result2...]}</code>
	 * @param response communication handler.
	 * @param httpCode http status code.
	 * @param job job.
	 * @throws IOException
	 */
	public static void writeJobDetails(HttpServletResponse response, int httpCode, UwsJob job) throws IOException {
		response.setStatus(httpCode);
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		if (job == null) {
			pw.println("{}");
		} else {
			pw.println("{\"meta\": ");
			writeJobDetailsMain(job, pw);
			pw.println(",");
			pw.print("\"parameters\": [");
			writeJobDetailsParameters(job.getParameters(), pw);
			pw.println("],");
			pw.print("\"error_summary\": ");
			writeJobDetailsError(job.getErrorSummary(), pw);
			pw.println(",");
			pw.print("\"results\": [");
			writeJobDetailsResults(job.getResults(), pw);
			pw.println("]}");
		}
		pw.flush();
	}
	
	/**
	 * package for test-harnesses
	 * @param job
	 * @param pw
	 * @throws IOException
	 */
	static void writeJobDetailsMain(UwsJob job, PrintWriter pw) throws IOException {
		//String data = Templates.getJsonJobListDetails(job);
		String data = Templates.getJsonJobDetails(job);
		pw.print(data);
	}

	/**
	 * package for test-harnesses
	 * @param jobParameters
	 * @param pw
	 * @throws IOException
	 */
	static void writeJobDetailsParameters(UwsJobParameters jobParameters, PrintWriter pw) throws IOException {
		if(jobParameters != null){
			boolean firstTime = true;
			String data;
			for(String name: jobParameters.getParameterNames()){
				if(firstTime){
					firstTime = false;
				}else{
					pw.println(",");
				}
				data = Templates.getJsonJobParameters(jobParameters, name);
				pw.print(data);
			}
		}
	}

	/**
	 * package for test-harnesses.
	 * @param errorDetails
	 * @param pw
	 * @throws IOException
	 */
	static void writeJobDetailsError(UwsJobErrorSummaryMeta errorDetails, PrintWriter pw) throws IOException {
		String data = Templates.getJsonJobErrorSummary(errorDetails);
		pw.print(data);
	}

	/**
	 * package for test-harnesses.
	 * @param resultsDetails
	 * @param pw
	 * @throws IOException
	 */
	static void writeJobDetailsResults(List<UwsJobResultMeta> resultsDetails , PrintWriter pw) throws IOException {
		if(resultsDetails != null){
			boolean firstTime = true;
			String data;
			for(UwsJobResultMeta r: resultsDetails){
				if(firstTime){
					firstTime = false;
				}else{
					pw.println(",");
				}
				data = Templates.getJsonJobResult(r);
				pw.print(data);
			}
		}
	}

	/**
	 * Returns a long value from a parameter.
	 * @param parameterValue parameter value.
	 * @param defaultValue default value.
	 * @return a long value from a parameter.
	 */
	public static long getLongFromParameter(String parameterValue, long defaultValue){
		if(parameterValue == null){
			return defaultValue;
		}else{
			try{
				return Long.parseLong(parameterValue);
			}catch(NumberFormatException nfe){
				return defaultValue;
			}
		}
	}
	
	/**
	 * 
	 * @param response
	 * @param report
	 * @throws IOException
	 */
	public static void writeJobsRemovalReport(HttpServletResponse response, String report) throws IOException {
		response.setStatus(200);
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		if (report == null) {
			pw.println("{}");
		} else {
			String[] items = report.split("\n");
			pw.println("{\"report\": [");
			if(items != null){
				for(String i: items){
					pw.println("\""+i+"\"");
				}
			}
			pw.println("]}");
		}
		pw.flush();
	}
	
}
