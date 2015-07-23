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
