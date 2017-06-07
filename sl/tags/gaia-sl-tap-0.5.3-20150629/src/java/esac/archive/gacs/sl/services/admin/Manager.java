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
package esac.archive.gacs.sl.services.admin;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.admin.handlers.EventsHandler;
import esac.archive.gacs.sl.services.admin.handlers.JobDetailsHandler;
import esac.archive.gacs.sl.services.admin.handlers.JobsListHandler;
import esac.archive.gacs.sl.services.admin.handlers.JobsRemoveProcedure;
import esac.archive.gacs.sl.services.admin.handlers.UserDetailsHandler;
import esac.archive.gacs.sl.services.admin.handlers.UserUpdateHandler;
import esac.archive.gacs.sl.services.admin.handlers.UsersListHandler;
import esac.archive.gacs.sl.services.util.Utils;
import esavo.tap.TAPException;
import esavo.tap.TAPService;
import esavo.uws.UwsException;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.security.UwsSecurity;
import esavo.uws.storage.UwsStorage;

public class Manager {
	public static final int MANAGER_DEFAULT_QUOTA_DB = 100000000; //MB
	public static final int MANAGER_DEFAULT_QUOTA_FILE = 1000000000; //MB

	public static final String PARAM_ACTION = "ACTION";
	public static final String PARAM_USER_ID = "USERID";
	public static final String PARAM_JOB_ID = "JOBID";
	public static final String PARAM_OWNER_ID = "OWNERID";
	public static final String PARAM_QUERY = "QUERY";
	public static final String PARAM_PHASE_ID = "PHASEID";
	public static final String PARAM_START_TIME_INIT = "START_TIME_INIT";
	public static final String PARAM_END_TIME_INIT = "END_TIME_INIT";
	public static final String PARAM_START_TIME_LIMIT = "START_TIME_LIMIT";
	public static final String PARAM_END_TIME_LIMIT = "END_TIME_LIMIT";
	public static final String PARAM_QUERY_RESULTS_OFFSET = "RESULTS_OFFSET";
	public static final String PARAM_QUERY_RESULTS_LIMIT = "RESULTS_LIMIT";
	public static final String PARAM_ROLES = "ROLES";
	public static final String PARAM_QUOTA_DB = "QUOTA_DB";
	public static final String PARAM_QUOTA_FILES = "QUOTA_FILES";
	
	private static final List<ManagerHandler> handlers = new ArrayList<ManagerHandler>();
	static{
		handlers.add(new UsersListHandler());
		handlers.add(new UserDetailsHandler());
		handlers.add(new JobsListHandler());
		handlers.add(new JobDetailsHandler());
		//handlers.add(new HandleJobHandler());
		handlers.add(new UserUpdateHandler());
		handlers.add(new JobsRemoveProcedure());
		handlers.add(new EventsHandler());
	}

	protected final TAPService service;

	//private UwsJobOwner owner = null;
	
	public Manager(TAPService serviceConnection) throws UwsException, TAPException {
		service = serviceConnection;
	}
	
	public void executeRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		owner = null;
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		UwsJobOwner user;
		try {
			user = security.getUser();
		} catch (UwsException e) {
			throw new ServletException("Cannot obtain current user: " + e.getMessage(), e);
		}
		
		try {
			// DENY ACCESS TO UNAUTHENTICATED/UNAUTHORIZED USERS
			Utils.checkAuthentication(user);
		} catch (InvalidParameterException ipe) {
			//Login required
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "User not authenticated", "Login required");
			response.flushBuffer();
			return;
		}
		
		UwsStorage uwsStorage = service.getFactory().getStorageManager();
		try{
			execute(request, response, uwsStorage, user);
		}catch(Exception e){
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", e);
			response.flushBuffer();
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void execute(HttpServletRequest request, HttpServletResponse response, UwsStorage uwsStorage, UwsJobOwner currentUser) throws Exception{
		if(currentUser == null){
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Invalid user", "User 'null' does not have enough privileges.");
			response.flushBuffer();
			return;
		}
		if(!currentUser.isAdmin()){
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Invalid user", "User '"+currentUser.getId()+"' does not have enough privileges.");
			response.flushBuffer();
			return;
		}

		//Execute action
		String action = (String)request.getParameter(PARAM_ACTION);
		
		Map<String,String> parameters = new HashMap<String, String>();
		Enumeration<String> e = request.getParameterNames();
		String paramName;
		while(e.hasMoreElements()){
			paramName = e.nextElement();
			parameters.put(paramName.toUpperCase(), request.getParameter(paramName));
		}

		
		for(ManagerHandler handler: handlers){
			if(handler.canHandle(action)){
				handler.handle(parameters, response, uwsStorage);
				return;
			}
		}
		
		ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Invalid action", "Action '"+action+"' is not known.\n<br/>" + getAvailableActions());
		response.flushBuffer();
	}
	
	private String getAvailableActions(){
		StringBuilder sb = new StringBuilder();
		sb.append("<p>Available actions:\n");
		sb.append("<ul>\n");
		for(ManagerHandler handler: handlers){
			sb.append("<li>").append(handler.getActionIdentifier()).append("</li>\n");
		}
		sb.append("</ul>\n");
		sb.append("</p>\n");
		return sb.toString();
	}

}
