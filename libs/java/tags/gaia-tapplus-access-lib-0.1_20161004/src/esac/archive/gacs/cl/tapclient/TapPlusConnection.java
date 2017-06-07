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
package esac.archive.gacs.cl.tapclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import esac.archive.gacs.cl.tapclient.connection.ConnectionWrapper;
import esac.archive.gacs.cl.tapclient.interfaces.TAPPlusInterface;
import esac.archive.gacs.cl.tapclient.tapSchema.DbSchema;
import esac.archive.gacs.cl.web.client.util.TapUtils;

/**
 * Main class of this library. An object TapPlusConnection can be invoked to
 * perform the main methods available at a TAP+ server like login, query, upload, etc
 * It allows to hide from the user complexities like the use of sessions, https 
 * certificates, asynchronous queries, etc
 * 
 * @author jsalgado
 */
public class TapPlusConnection implements TAPPlusInterface {

		private ConnectionWrapper 	connection;
		private String 				tapBaseURL 		= "https://gea.esac.esa.int/tap-server/";
		private	String 				cookie 			= null;
		private String 				userName 		= "";
		
    /**
     * Basic constructor. It makes use of the default GACS TAP service
     */
	public TapPlusConnection() {
	}
	
	/**
	 * Constructor setting TAP service URL
	 * @param baseURL Location of the TAP service URL, e.g. https://gaia.esac.esa.int/tap-server/tap/
	 */
	public TapPlusConnection(String baseURL) {
		this();
		setTapBaseURL(baseURL);
	}
	

	/**
	 * Obtain the TAP Service URL
	 */
	@Override
	public String getTapBaseURL() {
		return tapBaseURL;
	}

	/**
	 * Set TAP base URL
	 * @param baseURL Location of the TAP service URL, e.g. https://gea.esac.esa.int/tap-server/
	 */
	@Override	
	public void setTapBaseURL(String baseURL) {
		tapBaseURL = baseURL;
	}	
	
	
	/**
	 * Perform Login action. Once invoked on this object, all the rest of
	 * operations will contain an authorized session
	 * 
	 * @param username LDAP user name
	 * @param password LDAP password
	 * @return List<String> with the response from the server
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	@Override
    public List<String> login(String username, String password) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		
		String loginURL 	= getTapBaseURL() + "login"; 		
		
		connection = new ConnectionWrapper(loginURL,"UTF-8");
		connection.setType("POST", "application/x-www-form-urlencoded");
		
		StringBuilder content = new StringBuilder();
	    content.append("username=").append(URLEncoder.encode(username, "UTF-8"));
	    content.append("&password=").append(URLEncoder.encode(password, "UTF-8"));

	    this.userName = username;
		connection.addPostParameters(content.toString());
		
		List<String> response = connection.getResponse();
        cookie = connection.getCookie();
        		
        return response;
    }
	
	/**
	 * Perform logout action and close the session with the TAP server
	 * It is recommended to include always this method invocation as a 
	 * final step of the workflow
	 * 
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	@Override
	public void logout() throws IOException, KeyManagementException, NoSuchAlgorithmException {
		
		String loginURL 	= getTapBaseURL() + "logout"; 		
		
		connection = new ConnectionWrapper(cookie, loginURL,"UTF-8");
		connection.setType("POST", "multipart/form-data");	
	}
	
	/**
	 * Execute a synchronous query
	 * @param query ADQL query to be executed
	 * @param outputFormat Output format of the response, e.g. votable
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	@Override
	public BufferedReader querySynchronous(String query, String outputFormat) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		
		String queryURL 	 = getTapBaseURL() + "tap/sync?"; 			    
		String parametersURL = "REQUEST=doQuery&LANG=ADQL&FORMAT=" + outputFormat + "&QUERY=" + query;
		queryURL = queryURL + parametersURL;
		
		connection = new ConnectionWrapper(cookie, queryURL, "UTF-8");
		connection.setType("GET", "multipart/form-data");
		
		return connection.finish();
	}	
	
	/**
	 * Execute an asynchronous query and returns the location of the UWS job information
	 * @param query ADQL query to be executed
	 * @param outputFormat Output format of the response, e.g. votable
	 * @return jobID of the job invoked
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	@Override
	public String queryAsynchronous(String query, String outputFormat) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		
		String queryURL 	 = getTapBaseURL() + "tap/async?"; 			    
		String parametersURL = "PHASE=run&REQUEST=doQuery&LANG=ADQL&FORMAT=" + outputFormat + "&QUERY=" + query;
		queryURL 				= queryURL + parametersURL;
		
		connection = new ConnectionWrapper(cookie, queryURL, "UTF-8");
		connection.setType("GET", "application/x-www-form-urlencoded");	
		
		List<String> response = connection.getResponse();
		String jobId = "";
		for (String line : response) {
			 
			 if(line.indexOf("<uws:jobId><![CDATA[") != -1) {
		         	int startResultsLink 	= line.indexOf("<uws:jobId><![CDATA[") + 20;
		         	int endResultsLink 		= line.substring(line.indexOf("<uws:jobId><![CDATA[") + 20).indexOf("]]></uws:jobId>");
		         	jobId = line.substring(startResultsLink, startResultsLink + endResultsLink);
		     }
        }
		 
		 
		return jobId;
	}
	
	/**
	 * Wait in a loop to finish an asynchronous job and provides result
	 * 
	 * @param jobId Job identifier to be monitored
	 * @return Results Location if the job has finished
	 * 
	 */
	@Override
	public String waitForJob(String jobId) throws KeyManagementException, NoSuchAlgorithmException, IOException, InterruptedException {
		
		String jobURL 				= getTapBaseURL() + "tap/async/" + jobId;
		
		boolean completed 			= false;
		boolean error 				= false;
		String  resultsLocation		= "";
		
		while(!completed && !error) {
			
			connection = new ConnectionWrapper(cookie, jobURL, "UTF-8");
			connection.setType("GET", "application/x-www-form-urlencoded");
			
			List<String> response = connection.getResponse();
	        for (String line : response) {
	        	
	            if(line.indexOf("<uws:phase>COMPLETED</uws:phase>") != -1) {
	            	completed = true;
	            } else  if(line.indexOf("<uws:phase>ERROR</uws:phase>") != -1) {
	            	completed = true;
	            }
	            	
	            if(line.indexOf("<uws:result id=\"result\"") != -1) {
	            	int startResultsLink 	= line.indexOf("xlink:href=\"") + 12;
	            	int endResultsLink 		= line.substring(line.indexOf("xlink:href=\"") + 12).indexOf("\"");

	            	resultsLocation = line.substring(startResultsLink, startResultsLink + endResultsLink);
	            }
	        }
	        if(!completed) Thread.sleep(2000);
		} 
		
		return (String) URLDecoder.decode(resultsLocation);
	}
	
	/**
	 * Obtain the status of an asynchronous job
	 * @param jobId ID of the asynchronous job
	 * @return status of the job
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	@Override
	public String getJobStatus(String jobId) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		
		String jobURL 				= getTapBaseURL() + "tap/async/" + jobId;		
		connection = new ConnectionWrapper(cookie, jobURL, "UTF-8");
		connection.setType("GET", "application/x-www-form-urlencoded");
		
		List<String> response = connection.getResponse();
        for (String line : response) {
        	if(line.indexOf("<uws:phase>") != -1) {
        		int startStatus 	= line.indexOf("<uws:phase>") + 11;
        		int endStatus 		= line.substring(line.indexOf("<uws:phase>") + 11).indexOf("</uws:phase>");
        		return line.substring(startStatus, startStatus + endStatus);
        	}
        }
        
        return "";
	}
	
	/**
	 * Remove an asynchronous job
	 * @param jobId ID of the asynchronous job
	 * @return status of the job
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	@Override
	public boolean deleteJob(String jobId) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		
		String jobURL 				= getTapBaseURL() + "tap/async/" + jobId;		
		connection = new ConnectionWrapper(cookie, jobURL, "UTF-8");
		connection.setType("DELETE", "application/x-www-form-urlencoded");
		connection.followRedirects(false);
		
		int responseCode = connection.getResponseCode();

		connection.followRedirects(true);
		
		if(responseCode == 303){
			return true;
		}
        
        return false;
	}
	
	/**
	 * Displays results of asynchronous job
	 * 
	 * @param resultsLocation URL where a job result is located to download it	 
	 * @return A buffered reader with the response from the server
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	
	@Override
	public BufferedReader getResults(String resultsLocation) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		
		connection = new ConnectionWrapper(cookie, resultsLocation, "UTF-8");
		connection.setType("GET", "application/x-www-form-urlencoded");
		
		return connection.finish();
	}
 	
	/**
	 * Upload VOTable to the GACS user schema
	 * 
	 * @param tableName Name to be assigned to this table in the user schema
	 * @param raCol Column name where ra is located
	 * @param decCol Column name where dec is located
	 * @param tableFile	Location of the file to be uploaded (e.g. /home/<user>/example.xml) 
	 * @return Array list with the status report of the upload
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public List<String> uploadTable(String tableName, String raCol, String decCol, String tableFile) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		
		String uploadURL 	= getTapBaseURL() + "Upload"; 		

		connection = new ConnectionWrapper(cookie, uploadURL,"UTF-8");
		connection.setType("POST", "multipart/form-data");
		
		connection.addFormField("TABLE_NAME", 	tableName);
		connection.addFormField("RACOL", 		raCol);
		connection.addFormField("DECCOL", 		decCol);
		connection.addFilePart("FILE", new File(tableFile));
		        
        return connection.getResponse();
	}

	/**
	 * Upload VOTable to the GACS user schema
	 * 
	 * @param tableName Name to be assigned to this table in the user schema
	 * @param tableFile	Location of the file to be uploaded (e.g. /home/<user>/example.xml) 
	 * @return Array list with the status report of the upload
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public List<String> uploadTable(String tableName, String tableFile) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		
		String uploadURL 	= getTapBaseURL() + "Upload"; 		

		connection = new ConnectionWrapper(cookie, uploadURL,"UTF-8");
		connection.setType("POST", "multipart/form-data");
		
		connection.addFormField("TABLE_NAME", tableName);
		connection.addFilePart("FILE", new File(tableFile));
		return connection.getResponse();
	}
	
	
	/** 
	 * Delete table from the user schema
	 * 
	 * @param tableName Name of the table to be deleted
	 * @return Array list with the status report of the table removal
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public List<String> deleteTable(String tableName) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		
		String uploadURL 	= getTapBaseURL() + "Upload"; 		

		connection = new ConnectionWrapper(cookie, uploadURL,"UTF-8");		
		connection.setType("POST", "multipart/form-data");
		
		connection.addFormField("TABLE_NAME", tableName);
		connection.addFormField("DELETE", "true");
        
        return connection.getResponse();
	}	

	/**
	 * Get private tables
	 * 
	 * @param filterSchema Schema name to filter the output response
	 * @param filterOut If true, filter out tables from this schema. If  false, filter out tables out of this schema
	 * @return Array list with the table names
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public List<String> getTables(String filterSchema, boolean filterOut) throws ParserConfigurationException, SAXException, IOException, KeyManagementException, NoSuchAlgorithmException  {
		
		String schemalURL = getTapBaseURL() + "tap/tables"; 			
		
		connection = new ConnectionWrapper(cookie, schemalURL,"UTF-8");		
		connection.setType("POST", "multipart/form-data");
		
		InputStream inputStream = connection.getInputStream();
		
		HashMap<String, String> schemaMap = new HashMap<String, String>();
		schemaMap.put("user_" + userName, "user_" + userName);
		
		List<DbSchema> 	dbSchemas 	= TapUtils.getTableSchemas(inputStream, schemaMap);
		
		Iterator<DbSchema> 		itSchema = dbSchemas.iterator();
		while(itSchema.hasNext()) {
			DbSchema dbSchema = (DbSchema) itSchema.next();
			if(dbSchema.getName().equals(filterSchema) == !filterOut) {
				return dbSchema.getTableNames();
			}
			
		}
		
		return null;
	}
	 
	/**
	 * Get all tables
	 * 
	 * @return Array list with the table names
	 * @throws IOException
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public List<String> getPublicTables() throws IOException, ParserConfigurationException, SAXException, KeyManagementException, NoSuchAlgorithmException {
		return getTables("user_" + userName, true);
	}		
	
	/**
	 * Get private tables
	 * 
	 * @return Array list with the table names
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public List<String> getPrivateTables() throws ParserConfigurationException, SAXException, IOException, KeyManagementException, NoSuchAlgorithmException  {
		return getTables("user_" + userName, false);
	}
	

	/**
	 * Disconnect underlying connection
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		connection.disconnect();
	}


}
