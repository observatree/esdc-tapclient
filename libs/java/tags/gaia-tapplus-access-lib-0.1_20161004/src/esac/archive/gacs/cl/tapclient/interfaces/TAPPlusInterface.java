/*******************************************************************************
 * Copyright (c) 2016 European Space Agency.
 ******************************************************************************/
package esac.archive.gacs.cl.tapclient.interfaces;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public interface TAPPlusInterface {
    
	/**
     * Get TAP base URL
     * @return
     */
	public String getTapBaseURL();

	/**
	 * Set TAP base URL
	 * @param bASE_URL
	 */
	public void setTapBaseURL(String bASE_URL);

	public List<String> 	login(String username, String password) 						throws IOException, KeyManagementException, NoSuchAlgorithmException;
	public void 			logout() 														throws IOException, KeyManagementException, NoSuchAlgorithmException;
	public BufferedReader 	querySynchronous(String query, String outputFormat) 			throws KeyManagementException, NoSuchAlgorithmException, IOException;
	public String 			queryAsynchronous(String query, String outputFormat) 			throws KeyManagementException, NoSuchAlgorithmException, IOException;
	public String 			waitForJob(String jobID) 										throws KeyManagementException, NoSuchAlgorithmException, IOException, InterruptedException;
	public boolean		 	deleteJob(String jobID)											throws KeyManagementException, NoSuchAlgorithmException, IOException;
	public List<String> 	uploadTable(String tableName, String tableFile) 				throws KeyManagementException, NoSuchAlgorithmException, IOException;
	public List<String> 	deleteTable(String tableName) 									throws IOException, KeyManagementException, NoSuchAlgorithmException;
	public String 			getJobStatus(String jobID) 										throws KeyManagementException, NoSuchAlgorithmException, IOException;
	public BufferedReader 	getResults(String resultsLocation)								throws KeyManagementException, NoSuchAlgorithmException, IOException;

	public List<String> 	getTables(String schema, boolean filterOut)						throws ParserConfigurationException, SAXException, IOException, KeyManagementException, NoSuchAlgorithmException;
	public List<String> 	getPublicTables() 												throws ParserConfigurationException, SAXException, IOException, KeyManagementException, NoSuchAlgorithmException;
	public List<String> 	getPrivateTables() 												throws ParserConfigurationException, SAXException, IOException, KeyManagementException, NoSuchAlgorithmException;

}
