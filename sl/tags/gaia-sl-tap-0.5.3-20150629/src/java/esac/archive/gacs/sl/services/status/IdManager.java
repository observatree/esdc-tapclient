package esac.archive.gacs.sl.services.status;

/*
 * This file is part of TAPLibrary.
 * 
 * TAPLibrary is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * TAPLibrary is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with TAPLibrary.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2012 - UDS/Centre de Données astronomiques de Strasbourg (CDS)
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidParameterException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import esavo.tap.TAPException;
import esavo.tap.TAPService;
import esavo.uws.UwsException;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.security.UwsSecurity;

public class IdManager {
	
	/** Part of HTTP content type header. */
	protected final TAPService service;

	//protected ServiceErrorWriter errorWriter;
	
	//private UwsJobOwner owner = null;
	
	public IdManager(TAPService serviceConnection) throws UwsException, TAPException {
		service = serviceConnection;
		//errorWriter = new DefaultTAPErrorWriter(service);
	}

//	public final TAPLog getLogger(){
//		return service.getLogger();
//	}

//	public final ServiceErrorWriter getErrorWriter() {
//		return errorWriter;
//	}

//	public final void setErrorWriter(ServiceErrorWriter errorWriter) {
//		if (errorWriter != null){
//			this.errorWriter = errorWriter;
//		}
//	}

	public void executeRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		UwsSecurity security = service.getFactory().getSecurityManager();
		UwsJobOwner user;
		try {
			user = security.getUser();
		} catch (UwsException e) {
			throw new ServletException("Cannot obtain current user: " + e.getMessage(), e);
		}

		
		// Reset variables
		//owner=null;

		try{
//			// Identify the user:
//			if (service.getUserIdentifier() != null)
//				owner = service.getUserIdentifier().extractUserId();
			
			// TODO DENY ACCESS TO UNAUTHENTICATED/UNAUTHORIZED USERS
			checkAuthentication(user);

			///////////////////////////////////////////
			/// CHECKS
			///////////////////////////////////////////
			try{
				check();
			}catch(Exception e){
				throw e;
			}
			
			// Obtain task id:
			StatusManager statusManager = StatusManager.getInstance();
			Long id = statusManager.createUserIdentifier(new UserInfo(user));
			
			String jsonOutput= "{ 'id': '"+id.longValue()+"' }";
			
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(jsonOutput);
			out.flush();
		}catch(Throwable t){
			String jsonOutput= "{ 'failed': '"+t.getMessage()+"' }";
			
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(jsonOutput);
			out.flush();
		}
	}


	/**
	 * Checks the validity of the upload or delete request.
	 * @throws IOException 
	 */
	private void check() throws IOException{
	}
	
	/**
	 * Checks corresponding to a table upload.
	 * @throws IOException
	 */
	private void checkAuthentication(UwsJobOwner owner){
		if (owner == null || owner.getAuthUsername()==null) {
			throw new InvalidParameterException("Task ID request error: user must be logged in to get a task ID.");
		}
	}

}
