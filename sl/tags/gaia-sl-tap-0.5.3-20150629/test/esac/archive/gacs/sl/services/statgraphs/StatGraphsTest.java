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
package esac.archive.gacs.sl.services.statgraphs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Test;

import esac.archive.gacs.sl.test.TestUtils;
import esac.archive.gacs.sl.test.data.ReadData;
import esac.archive.gacs.sl.test.http.DummyHttpRequest;
import esac.archive.gacs.sl.test.http.DummyHttpResponse;
import esac.archive.gacs.sl.test.stats.DummyStatResourceManager;

public class StatGraphsTest {

	@Test
	public void testExecuteRequest() throws ServletException, IOException, URISyntaxException {
		Map<String,String> params = new HashMap<String,String>();
		params.put("TABLE", "public.g10_mw");
		params.put("TYPE", "DENSITY");
		params.put("CHECK", "true");
		
		DummyHttpRequest request = TestUtils.createSimpleHttpStatGraphsGetRequest(params);
		
		DummyHttpResponse response = TestUtils.createSimpleHttpResponse();
		String appid = "appid_test";
		
		DummyStatResourceManager manager = new DummyStatResourceManager(appid);
		StatResourceFactory.setStatResourceManager(manager);

		StatGraphs statGraphs = new StatGraphs(appid);

		//CHECK
		statGraphs.executeRequest(request, response);
		assertEquals("TRUE",response.getOutputAsString());
		response.clearOutput();
		
		params.put("TABLE", "public.xxx");
		request = TestUtils.createSimpleHttpStatGraphsGetRequest(params);
		statGraphs.executeRequest(request, response);
		assertEquals("FALSE",response.getOutputAsString());
		response.clearOutput();

		//METADATA
		params = new HashMap<String,String>();
		params.put("STATMETADATA", "true");
		request = TestUtils.createSimpleHttpStatGraphsGetRequest(params);
		statGraphs.executeRequest(request, response);
		
		String jsonTest = ReadData.readDataTextAsResource(this.getClass(), TestUtils.STATGRAPHS_DIR+"graphs.json");
		assertEquals(jsonTest,response.getOutputAsString());
		response.clearOutput();
		
		//GET GRAPH
		params = new HashMap<String,String>();
		params.put("TABLE", "public.g10_mw");
		params.put("TYPE", "DENSITY");
		request = TestUtils.createSimpleHttpStatGraphsGetRequest(params);
		statGraphs.executeRequest(request, response);
		
		long lengthTest = ReadData.getContentLength(this.getClass(), TestUtils.STATGRAPHS_DIR+"public.g10_mw_density.png");
		assertEquals(lengthTest,response.getContentLength());
		response.clearOutput();
		
		params = new HashMap<String,String>();
		params.put("TABLE", "public.xxx");
		params.put("TYPE", "DENSITY");
		request = TestUtils.createSimpleHttpStatGraphsGetRequest(params);
		statGraphs.executeRequest(request, response);
		
		lengthTest = ReadData.getContentLength(this.getClass(), TestUtils.STATGRAPHS_DIR+"not_available_density.png");
		assertEquals(lengthTest,response.getContentLength());
		response.clearOutput();
		
	}

}
