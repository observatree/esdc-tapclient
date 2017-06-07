/*******************************************************************************
 * Copyright (C) 2017 European Space Agency
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
