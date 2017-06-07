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
package esac.archive.gacs.sl.services.actions;

//Import from Java packages
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.statgraphs.StatGraphs;
import esavo.uws.config.UwsConfiguration;
import esavo.uws.config.UwsConfigurationManager;
import esavo.uws.utils.UwsUtils;




/**
 *  
 * @author Raul Gutierrez
 *
 */
public class StatGraphsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	private String appid;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
//		appid = EnvironmentManager.getAppId(config);
//		EnvironmentManager.initGenericServletEnvironment(appid, config);
//		EnvironmentProperties props = EnvironmentManager.getAppEnvironmentProperties(appid);
//		EnvironmentManager.initStatGraphsServletEnvironment(props, config);
		
		ServletContext context = getServletContext();
		appid = UwsUtils.getAppIdFromContext(context, config);
		if(appid == null){
			throw new IllegalArgumentException("Application identifier must be defined. Use configuration variable: '"+UwsConfiguration.CONFIG_APP_ID+"'");
		}

		UwsConfiguration configuration = UwsConfigurationManager.getConfiguration(appid);
		UwsUtils.updateConfiguration(configuration, context);
		UwsUtils.updateConfiguration(configuration, config);
	}

	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StatGraphs statGraphs = new StatGraphs(appid);
		statGraphs.executeRequest(request, response);
   }


	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}	
	

}

