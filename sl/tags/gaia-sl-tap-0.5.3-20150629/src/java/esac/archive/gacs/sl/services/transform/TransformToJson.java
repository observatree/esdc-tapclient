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
package esac.archive.gacs.sl.services.transform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLSession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import esac.archive.gacs.sl.services.transform.handlers.TransformHandler;
import esac.archive.gacs.sl.services.transform.handlers.TransformHandlerFactory;
import esac.archive.gacs.sl.services.transform.handlers.TransformHandlerFactory.TransformHandlerType;
import esac.archive.gacs.sl.services.util.Utils;
import esac.archive.gacs.sl.tap.actions.TapServiceConnection;
import esavo.tap.TAPFactory;
import esavo.uws.UwsException;
import esavo.uws.UwsManager;
import esavo.uws.jobs.UwsJob;
import esavo.uws.jobs.UwsJobResultMeta;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.security.UwsSecurity;
import esavo.uws.storage.UwsStorage;

/**
 * Converts query data into Json.<br/>
 * Query data formats are: Json, VOTable, CVS and CDF.
 * @author juan.carlos.segovia@sciops.esa.int
 *
 */
public class TransformToJson {
	
	private static final String URL_PARAMETER = "URL";
	private static final String RESULTS_OFFSET_PARAMETER = "RESULTS_OFFSET";
	private static final String PAGE_SIZE_PARAMETER = "PAGESIZE";
	private static final String DATA_FORMAT_PARAMETER = "FORMAT";
	private static final String ALL_STRINGS_PARAMETER = "ALL_STRINGS";
	
	private static Logger LOGGER = Logger.getLogger(TransformToJson.class);
	
	//TODO when pagination is enabled:
	//private static final int DEFAULT_PAGE_SIZE = 20;
	private static final int DEFAULT_PAGE_SIZE = -1; //no limits
	
	private File tmpDir;
	private TapServiceConnection service;
	
	public TransformToJson(TapServiceConnection service, File tmpdir){
		this.service = service;
		this.tmpDir = tmpdir;
	}
	
	public void executeRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String url = Utils.getParameter(URL_PARAMETER, request);
		
		//Authentication
		UwsSecurity security = service.getFactory().getSecurityManager();
		UwsJobOwner user;
		try {
			user = security.getUser();
		} catch (UwsException e1) {
			throw new ServletException("Cannot obtain current user: " + e1.getMessage(), e1);
		}

		//Get inputstream to results
		InputStream is;
		try {
			is = getResultInputStream(url, user);
		} catch (UwsException e) {
			throw new ServletException("Cannot obtain input stream to result data: " + e.getMessage(), e);
		}

		//dump data
		dumpData(request, response, is);
	}
	
	private InputStream getResultInputStream(String url, UwsJobOwner currentUser) throws UwsException{
		String[] resourcePath = url.split("/");
		String jobid = resourcePath[resourcePath.length-3];
		TAPFactory factory = service.getFactory();
		//UwsManager uwsManager = UwsManager.getInstance(factory.getAppId());
		UwsManager uwsManager = UwsManager.getInstance();
		UwsJob job = uwsManager.tryLoadJob(jobid, currentUser);
		List<UwsJobResultMeta> results = job.getResults();
		String resultId = resourcePath[resourcePath.length-1];
		for(UwsJobResultMeta result: results){
			if(resultId.equals(result.getId())){
				//found
				UwsStorage storage = factory.getStorageManager();
				InputStream source = storage.getJobResultDataInputSource(job, result.getId());
				return source;
			}
		}
		throw new UwsException(UwsOutputResponseHandler.BAD_REQUEST, "Result identifier '"+resultId+"' not found in job '"+jobid+"'");
	}
	
	private void dumpData(HttpServletRequest request, HttpServletResponse response, InputStream is) throws IOException{
		String format = Utils.getParameter(DATA_FORMAT_PARAMETER, request);
		long resultsOffset = Utils.getLongParameter(RESULTS_OFFSET_PARAMETER, request, "0");
		long pageSize = Utils.getLongParameter(PAGE_SIZE_PARAMETER, request, ""+DEFAULT_PAGE_SIZE);
		boolean allStrings = Utils.getBooleanParameter(ALL_STRINGS_PARAMETER, request, false);

		PrintStream out = new PrintStream(response.getOutputStream());
		try {
			TransformHandlerType type;
			if (format.equalsIgnoreCase(TransformHandlerType.VOTABLE.name())) {
				response.setContentType("text/plain");
				type = TransformHandlerType.VOTABLE;
			} else if (format.equalsIgnoreCase(TransformHandlerType.CDF.name())) {
				response.setContentType("text/plain");
				type = TransformHandlerType.CDF;
			} else if (format.equalsIgnoreCase(TransformHandlerType.CSV.name())) {
				response.setContentType("text/plain");
				type = TransformHandlerType.CSV;
			} else if (format.equalsIgnoreCase(TransformHandlerType.JSON.name())) {
				response.setContentType("text/plain");
				type = TransformHandlerType.JSON;
			} else {
				// ERROR unknown format.
				throw new IOException("Unknown format '" + format + "'");
			}
			TransformHandler handler = TransformHandlerFactory.createHandler(type, out, resultsOffset, pageSize, tmpDir, allStrings);
			handler.parse(is);
		} finally {
			closeAll(is, out);
		}
	}
	
	private void closeAll(InputStream is, PrintStream out){
		if (is != null) {
			try {
				is.close();
			} catch (Exception e) {
				LOGGER.log(Level.ERROR, "Exception closing InputStream", e);
			}
		}
		if(out != null){
			try {
				out.flush();
				out.close();
			} catch (Exception e) {
				LOGGER.log(Level.ERROR, "Exception closing PrintWriter", e);
			}
		}
	}

	
//	public void executeRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
//		String url = Utils.getParameter(URL_PARAMETER, request);
//		String format = Utils.getParameter(DATA_FORMAT_PARAMETER, request);
//		long resultsOffset = Utils.getLongParameter(RESULTS_OFFSET_PARAMETER, request, "0");
//		long pageSize = Utils.getLongParameter(PAGE_SIZE_PARAMETER, request, ""+DEFAULT_PAGE_SIZE);
//		boolean allStrings = Utils.getBooleanParameter(ALL_STRINGS_PARAMETER, request, false);
//		
//		String cookie = request.getHeader("cookie");
//		LOGGER.info("Accessing to: " + url);
//		LOGGER.info("Cookie: " + cookie);
//		
//		HttpURLConnection urlConnection = openRequest(url, cookie);
//		InputStream is = urlConnection.getInputStream();
//		PrintStream out = new PrintStream(response.getOutputStream());
//		try {
//			TransformHandlerType type;
//			if (format.equalsIgnoreCase(TransformHandlerType.VOTABLE.name())) {
//				response.setContentType("text/plain");
//				type = TransformHandlerType.VOTABLE;
//			} else if (format.equalsIgnoreCase(TransformHandlerType.CDF.name())) {
//				response.setContentType("text/plain");
//				type = TransformHandlerType.CDF;
//			} else if (format.equalsIgnoreCase(TransformHandlerType.CSV.name())) {
//				response.setContentType("text/plain");
//				type = TransformHandlerType.CSV;
//			} else if (format.equalsIgnoreCase(TransformHandlerType.JSON.name())) {
//				response.setContentType("text/plain");
//				type = TransformHandlerType.JSON;
//			} else {
//				// ERROR unknown format.
//				throw new IOException("Unknown format '" + format + "'");
//			}
//			TransformHandler handler = TransformHandlerFactory.createHandler(type, out, resultsOffset, pageSize, tmpDir, allStrings);
//			handler.parse(is);
//		} finally {
//			closeAll(is, out, urlConnection);
//		}
//	}
	
//	private void closeAll(InputStream is, PrintStream out, HttpURLConnection urlConnection){
//		if (is != null) {
//			try {
//				is.close();
//			} catch (Exception e) {
//				LOGGER.log(Level.ERROR, "Exception closing InputStream", e);
//			}
//		}
//		if(urlConnection != null){
//			try{
//				urlConnection.disconnect();
//			}catch (Exception e){
//				LOGGER.log(Level.ERROR, "Exception closing PrintWriter", e);
//			}
//			urlConnection = null;
//		}
//		if(out != null){
//			try {
//				out.flush();
//				out.close();
//			} catch (Exception e) {
//				LOGGER.log(Level.ERROR, "Exception closing PrintWriter", e);
//			}
//		}
//	}
	
	
//	private HttpURLConnection openRequest(String request, String cookie) throws IOException{
//		URL url = new URL(request);
//		if(url.getProtocol().equalsIgnoreCase("https")){
//			//TODO uncomment this when https is ready
////			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
////			urlConnection.setRequestProperty("cookie", cookie);
////
////			urlConnection.setHostnameVerifier(new HostnameVerifier() {
////				@Override
////				public boolean verify(String hostname, SSLSession session) {
////					return true;
////				}
////			});
////			
////			return urlConnection;
//			URL newUrl = new URL("http", url.getHost(), 8080, url.getFile());
//			LOGGER.info("Accessing to new url: " + newUrl);
//			HttpURLConnection urlConnection = (HttpURLConnection) newUrl.openConnection();
//			urlConnection.setRequestProperty("cookie", cookie);
//			return urlConnection;
//		}else{
//			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//			urlConnection.setRequestProperty("cookie", cookie);
//			return urlConnection;
//		}
//		
//	}
	
	

}
