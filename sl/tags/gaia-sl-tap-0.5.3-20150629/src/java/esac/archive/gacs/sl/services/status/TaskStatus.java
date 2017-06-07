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

import org.json.JSONException;
import org.json.JSONObject;

import esavo.tap.TAPException;
import esavo.tap.TAPService;
import esavo.tap.log.TAPLog;
import esavo.uws.UwsException;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.security.UwsSecurity;

public class TaskStatus {
	
	/** Part of HTTP content type header. */
	protected final TAPService service;


	public static final String PARAM_TASKID = "TASKID";
	public static final String PARAM_TASKTYPE = "TASKTYPE";

	long taskId = 0;
	
	public TaskStatus(TAPService serviceConnection) {
		service = serviceConnection;
	}

//	public final TAPLog getLogger(){
//		return service.getLogger();
//	}
//
//	public final ServiceErrorWriter getErrorWriter() {
//		return errorWriter;
//	}
//
//	public final void setErrorWriter(ServiceErrorWriter errorWriter) {
//		if (errorWriter != null){
//			this.errorWriter = errorWriter;
//		}
//	}

	public void executeRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Reset variables
		taskId = 0;

		String taskIdStr = "";
		String taskTypeStr = "";
		
//		UwsSecurity security = service.getFactory().getSecurityManager();
//		UwsJobOwner user = security.getUser();
		
		try{
//			// Identify the user:
//			if (service.getUserIdentifier() != null){
//				owner = service.getUserIdentifier().extractUserId();
//			}
			
			// DENY ACCESS TO UNAUTHENTICATED/UNAUTHORIZED USERS
			//checkAuthentication();


			///////////////////////////////////////////
			/// CHECKS
			///////////////////////////////////////////
			check();
			
			// Get task id:
			taskIdStr = request.getParameter(PARAM_TASKID);
			if(taskIdStr==null || taskIdStr.trim().length()==0){
				taskIdStr=""; 
				throw new Exception("Task status request error: No task ID provided.");
			};
			taskTypeStr = request.getParameter(PARAM_TASKTYPE);
			
			//System.out.println("TaskStatus: "+taskIdStr);
			if(taskTypeStr==null || taskTypeStr.trim().length()==0) {
				throw new Exception("Task status request error: No task type provided.");
			}
			
			//Not necessary, valueOf will raise an IllegalArgumentException if the value cannot be parsed properly
//			if(TaskType.valueOf(taskTypeStr)==null) {
//				throw new Exception("Task status request error: Invalid task type: "+taskTypeStr);
//			}
			taskTypeStr=TaskType.valueOf(taskTypeStr).toString();
			
			try{
				taskId = Long.parseLong(request.getParameter(PARAM_TASKID));
				taskIdStr = ""+taskId;
			}catch(NumberFormatException e){
				throw new NumberFormatException("Task status request error: Incorrect task ID number format.");
			}
			
			StatusData data = StatusManager.getInstance().getStatus(taskId, TaskType.valueOf(taskTypeStr));
			
			String output = "";
			if(data!=null) {
				output=data.getData(); 
			}
		
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", taskIdStr);
			jsonObject.put("type", taskTypeStr);
			jsonObject.put("value", output);
			
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(jsonObject.toString());
			out.flush();
		}catch(Throwable t){
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("id", taskIdStr);
				jsonObject.put("type", taskTypeStr);
				jsonObject.put("failed", t.getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(jsonObject.toString());
			out.flush();
		}
	}


	/**
	 * Checks the validity of the upload or delete request.
	 * @throws IOException 
	 */
	private void check() throws Exception{
	}
	
//	/**
//	 * Checks corresponding to a table upload.
//	 * package: test-harness
//	 * @throws IOException
//	 */
//	void checkAuthentication(){
//		if (owner == null || owner.getAuthUsername()==null) {
//			throw new InvalidParameterException("Task status request error: user must be logged in to get a task status.");
//		}
//	}

}
