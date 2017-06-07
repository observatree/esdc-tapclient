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
package esac.archive.gacs.sl.services.share;

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

import esac.archive.gacs.sl.services.share.handlers.GetUsersHandler;
import esac.archive.gacs.sl.services.util.Utils;
import esavo.tap.TAPException;
import esavo.tap.TAPService;
import esavo.uws.UwsException;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.security.UwsSecurity;
import esavo.uws.share.UwsShareManager;

public class Share {
	public static final String PARAM_ACTION = "ACTION";
	
	public static final String PARAM_USER_PATTERN = "USER";
	
	public static final int MAX_USERS_RESULT = 1000;
	
	private static final List<ShareHandler> handlers = new ArrayList<ShareHandler>();
	static{
		handlers.add(new GetUsersHandler());
	}

	protected final TAPService service;

	//private UwsJobOwner owner = null;
	
	public Share(TAPService serviceConnection) throws UwsException, TAPException {
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
			ShareUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "User not authenticated", "Login required");
			response.flushBuffer();
			return;
		}
		
		//UwsStorage uwsStorage = service.getFactory().getStorageManager();
		UwsShareManager shareManager = service.getFactory().getShareManager();
		try{
			execute(request, response, shareManager, user);
		}catch(Exception e){
			ShareUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", e);
			response.flushBuffer();
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void execute(HttpServletRequest request, HttpServletResponse response, UwsShareManager shareManager, UwsJobOwner currentUser) throws Exception{
		if(currentUser == null){
			ShareUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Invalid user", "User 'null' does not have enough privileges.");
			response.flushBuffer();
			return;
		}

		//FIXME open to any user?
//		if(!currentUser.isAdmin()){
//			ShareUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Invalid user", "User '"+currentUser.getId()+"' does not have enough privileges.");
//			response.flushBuffer();
//			return;
//		}

		//Execute action
		String action = (String)request.getParameter(PARAM_ACTION);
		
		Map<String,String> parameters = new HashMap<String, String>();
		Enumeration<String> e = request.getParameterNames();
		String paramName;
		while(e.hasMoreElements()){
			paramName = e.nextElement();
			parameters.put(paramName, request.getParameter(paramName));
		}

		
		for(ShareHandler handler: handlers){
			if(handler.canHandle(action)){
				handler.handle(parameters, response, shareManager);
				return;
			}
		}
		
		ShareUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Invalid action", "Action '"+action+"' is not known.\n<br/>" + getAvailableActions());
		response.flushBuffer();
	}
	
	private String getAvailableActions(){
		StringBuilder sb = new StringBuilder();
		sb.append("<p>Available actions:\n");
		sb.append("<ul>\n");
		for(ShareHandler handler: handlers){
			sb.append("<li>").append(handler.getActionIdentifier()).append("</li>\n");
		}
		sb.append("</ul>\n");
		sb.append("</p>\n");
		return sb.toString();
	}

}
