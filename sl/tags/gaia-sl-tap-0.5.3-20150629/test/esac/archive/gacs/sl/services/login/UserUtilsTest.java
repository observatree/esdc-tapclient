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
package esac.archive.gacs.sl.services.login;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.Cookie;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import esac.archive.absi.interfaces.common.transferobjects.controls.UserTO;
import esac.archive.absi.modules.cl.aio.util.Crypt;
import esac.archive.absi.modules.cl.aio.util.EnvironmentDefs;
import esac.archive.gacs.common.constants.RetrievalConstants;
import esac.archive.gacs.sl.test.TestUtils;
import esac.archive.gacs.sl.test.http.DummyHttpRequest;
import esac.archive.gacs.sl.test.http.DummyHttpResponse;
import esavo.uws.UwsException;
import esavo.uws.UwsJobsListManager;
import esavo.uws.UwsManager;
import esavo.uws.config.UwsConfiguration;
import esavo.uws.config.UwsConfigurationManager;
import esavo.uws.jobs.parameters.UwsJobOwnerParameters;
import esavo.uws.owner.UwsDefaultJobsOwnersManager;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.owner.UwsJobsOwnersManager;
import esavo.uws.test.uws.DummyUwsExecutor;
import esavo.uws.test.uws.DummyUwsFactory;
import esavo.uws.test.uws.DummyUwsFactory.StorageType;
import esavo.uws.test.uws.DummyUwsScheduler;
import esavo.uws.test.uws.DummyUwsStorageManager;
import esavo.uws.utils.UwsUtils;

public class UserUtilsTest {
	
	public static final String TEST_ID = UserUtilsTest.class.getName();
	
	private static File fStorageDir;
	private static String appid = TEST_ID;
	private static UwsManager manager;
	private static DummyUwsFactory factory;
	private static UwsConfiguration configuration;
	private static DummyUwsStorageManager storage;
	private static UwsJobsListManager listManager;
	private static DummyUwsExecutor executor;
	private static DummyUwsScheduler scheduler;

	@BeforeClass
	public static void beforeClass(){
		fStorageDir = new File(".", TEST_ID);
		fStorageDir.mkdirs();
		configuration = UwsConfigurationManager.getConfiguration(appid);
		factory = new DummyUwsFactory(appid, fStorageDir, configuration, StorageType.fake); 
		manager = UwsManager.getInstance();
		storage = (DummyUwsStorageManager)factory.getStorageManager();
		executor = (DummyUwsExecutor)factory.getExecutor();
		scheduler = (DummyUwsScheduler)factory.getScheduler();
		listManager = UwsJobsListManager.getInstance(appid);
	}
	
	@AfterClass
	public static void afterClass(){
		TestUtils.removeDirectory(fStorageDir);
	}
	

	
	@Test
	public void testSendLogInRequiredResponse() throws IOException{
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		UserUtils.sendLogInRequiredResponse(response);
		
		String expected = "User must be logged in to perform this action\n";
		String msg = response.getOutputAsString();
		Assert.assertEquals("Expected login message", expected, msg);
	}
	
	
	@Test
	public void testCreateXmlFromUser() throws ParserConfigurationException, UwsException{
		//UwsJobOwner user = new UwsJobOwner(UwsUtils.ANONYMOUS_USER, UwsJobOwner.ROLE_ADMIN);
		//user.setAuthUsername("anonymous");
		UwsJobOwner user = UwsDefaultJobsOwnersManager.createDefaultOwner(UwsUtils.ANONYMOUS_USER, appid);
		user.setRoles(UwsJobOwner.ROLE_ADMIN);
		user.setAuthUsername("anonymous");
		UwsJobOwnerParameters parameters = new UwsJobOwnerParameters();
		
		parameters.setParameter(UwsJobsOwnersManager.OWNER_PARAMETER_DB_QUOTA, new Long(100));
		parameters.setParameter(UwsJobsOwnersManager.OWNER_PARAMETER_CURRENT_DB_SIZE, new Long(100));
		parameters.setParameter(UwsJobsOwnersManager.OWNER_PARAMETER_FILES_QUOTA, new Long(100));
		parameters.setParameter(UwsJobsOwnersManager.OWNER_PARAMETER_CURRENT_FILES_SIZE, new Long(100));
		user.setParameters(parameters);

		Document doc = UserUtils.createXmlFromUser(user);
		Assert.assertNotNull("Expected xml document", doc);
		
		checkXmlFromUser(doc, user);
	}
	
	private void checkXmlFromUser(Document doc, UwsJobOwner user){
		Element eRoot = doc.getDocumentElement();
		NodeList nl = eRoot.getChildNodes();
		
		int numParameters = 0;
		if(user.getParameters()!=null) {
			numParameters=user.getParameters().getNumParameters();
		}
		int extraArgs = 2; //userid and username
		Assert.assertEquals("Number of children", extraArgs+numParameters, nl.getLength());
		Element e;
		for(int i = 0; i < nl.getLength(); i++){
			e = (Element)nl.item(i);
			checkXmlFromUser(e, user);
		}
	}
	
	private void checkXmlFromUser(Element e, UwsJobOwner user){
		String id = e.getNodeName();
		String expected = getSuitableValueFromUser(user, id);
		String text = null;
		Text t = (Text)e.getChildNodes().item(0);
		if(t != null){
			text = t.getTextContent();
		}
		Assert.assertEquals("Testing xml node name: " + id, expected, text);
	}
	
	private String getSuitableValueFromUser(UwsJobOwner user, String id) {
		if (RetrievalConstants.XML_TAG_VALUE_USER_USERNAME.equals(id)) {
			return user.getAuthUsername();
		}
		if ("user_name_details".equals(id)){
			return user.getName();
		}
		if (UwsJobsOwnersManager.OWNER_PARAMETER_DB_QUOTA.equals(id)) {
			return user.getParameters().getLongParameter(UwsJobsOwnersManager.OWNER_PARAMETER_DB_QUOTA).toString();
		}
		if (UwsJobsOwnersManager.OWNER_PARAMETER_CURRENT_DB_SIZE.equals(id)) {
			return user.getParameters().getLongParameter(UwsJobsOwnersManager.OWNER_PARAMETER_CURRENT_DB_SIZE).toString();
		}
		if (UwsJobsOwnersManager.OWNER_PARAMETER_FILES_QUOTA.equals(id)) {
			return user.getParameters().getLongParameter(UwsJobsOwnersManager.OWNER_PARAMETER_FILES_QUOTA).toString();
		}
		if (UwsJobsOwnersManager.OWNER_PARAMETER_CURRENT_FILES_SIZE.equals(id)) {
			return user.getParameters().getLongParameter(UwsJobsOwnersManager.OWNER_PARAMETER_CURRENT_FILES_SIZE).toString();
		}
		throw new IllegalArgumentException(id + " not found");

	}
	
	@Test
	public void testSendResponseWithUserDetails() throws Exception {
		String username = "test";
		//UwsJobOwner user = new UwsJobOwner(username, UwsJobOwner.ROLE_USER);
		UwsJobOwner user = UwsDefaultJobsOwnersManager.createDefaultOwner(username, appid);
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		
		UserUtils.sendResponseWithUserDetails(user, response);
		
		String msg = response.getOutputAsString();
		
		InputSource is = new InputSource(new StringReader(msg));

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		doc.getDocumentElement().normalize();

		checkXmlFromUser(doc, user);
	}
	

}
