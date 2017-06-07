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
package esac.archive.gacs.sl.services.admin.handlers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.admin.Manager;
import esac.archive.gacs.sl.services.admin.ManagerHandler;
import esac.archive.gacs.sl.services.admin.ManagerUtils;
import esavo.uws.UwsException;
import esavo.uws.jobs.UwsJob;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.storage.UwsStorage;

public class JobDetailsHandler implements ManagerHandler {

	public static final String ACTION = "job_details";

	@Override
	public boolean canHandle(String action) {
		return ACTION.equalsIgnoreCase(action);
	}

	@Override
	public void handle(Map<String, String> parameters, HttpServletResponse response, UwsStorage uwsStorage) throws IOException  {
		String jobid = parameters.get(Manager.PARAM_JOB_ID);
		if(jobid == null){
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", "Job identifier not found.");
		}else{
			try {
				UwsJob job = uwsStorage.getJobMeta(jobid);
				ManagerUtils.writeJobDetails(response, UwsOutputResponseHandler.OK, job);
			} catch (UwsException e) {
				int code = e.getCode();
				if (code < 0){
					code = UwsOutputResponseHandler.INTERNAL_SERVER_ERROR;
				}
				ManagerUtils.writeError(response, code, "Error", e);
			}
		}
		response.flushBuffer();
		return;
	}

	@Override
	public String getActionIdentifier() {
		return ACTION;
	}
}
