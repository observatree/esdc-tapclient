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
package esac.archive.gacs.sl.services.actions;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.admin.Manager;
import esac.archive.gacs.sl.tap.actions.TapServiceConnection;
import esavo.tap.TAPException;
import esavo.uws.UwsException;
import esavo.uws.config.UwsConfiguration;
import esavo.uws.config.UwsConfigurationManager;
import esavo.uws.utils.UwsUtils;

public class ManagerServlet extends HttpServlet {

//	public static final String MANAGER_DEFAULT_QUOTA_DB_PROP = "esac.archive.gacs.sl.tap.actions.Manager.quotaDb"; 
//	public static final String MANAGER_DEFAULT_QUOTA_FILE_PROP = "esac.archive.gacs.sl.tap.actions.Manager.quotaFile";
	
	private static final long serialVersionUID = 1L;

	private String appid;
//	private TAPSchemaInfo tapSchemaInfo;
	
	private TapServiceConnection service;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
//		appid = EnvironmentManager.getAppId(config);
//		EnvironmentManager.initGenericServletEnvironment(appid, config);
//		
////		System.setProperty(MANAGER_DEFAULT_QUOTA_DB_PROP, config.getInitParameter(MANAGER_DEFAULT_QUOTA_DB_PROP));
////		System.setProperty(MANAGER_DEFAULT_QUOTA_FILE_PROP, config.getInitParameter(MANAGER_DEFAULT_QUOTA_FILE_PROP));
//		EnvironmentManager.setPropertyIfNotNull(appid, MANAGER_DEFAULT_QUOTA_DB_PROP, config.getInitParameter(MANAGER_DEFAULT_QUOTA_DB_PROP));
//		EnvironmentManager.setPropertyIfNotNull(appid, MANAGER_DEFAULT_QUOTA_FILE_PROP, config.getInitParameter(MANAGER_DEFAULT_QUOTA_FILE_PROP));
//
//		tapSchemaInfo = esac.archive.gacs.sl.services.util.Utils.getTapSchemaInfo(config);
		
		ServletContext context = getServletContext();
		appid = UwsUtils.getAppIdFromContext(context, config);
		if(appid == null){
			throw new IllegalArgumentException("Application identifier must be defined. Use configuration variable: '"+UwsConfiguration.CONFIG_APP_ID+"'");
		}

		UwsConfiguration configuration = UwsConfigurationManager.getConfiguration(appid);
		UwsUtils.updateConfiguration(configuration, context);
		UwsUtils.updateConfiguration(configuration, config);

		//Initialize
		try {
			service = TapServiceConnection.getInstance(appid);
		} catch (UwsException e) {
			throw new ServletException(e);
		} catch (TAPException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Manager manager = new Manager(service);
			// 2. Forward all requests to the TAP instance:
			manager.executeRequest(request, response);

		}catch(Throwable t){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t.getMessage());
		}
	}
}
