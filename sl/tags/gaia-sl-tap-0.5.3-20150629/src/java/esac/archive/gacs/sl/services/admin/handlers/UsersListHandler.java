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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.admin.Manager;
import esac.archive.gacs.sl.services.admin.ManagerHandler;
import esac.archive.gacs.sl.services.admin.ManagerUtils;
import esavo.uws.UwsException;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.owner.utils.UwsJobsOwnersFilter;
import esavo.uws.storage.UwsQuotaSingleton;
import esavo.uws.storage.UwsStorage;

public class UsersListHandler implements ManagerHandler {
	
	public static final String ACTION = "users_list";

	@Override
	public boolean canHandle(String action) {
		return ACTION.equalsIgnoreCase(action);
	}

	@Override
	public void handle(Map<String, String> parameters, HttpServletResponse response, UwsStorage uwsStorage) throws IOException {
		try {
			UwsJobsOwnersFilter filter = createFilter(parameters);
			long offset = ManagerUtils.getLongFromParameter(parameters.get(Manager.PARAM_QUERY_RESULTS_OFFSET), -1);
			long limit = ManagerUtils.getLongFromParameter(parameters.get(Manager.PARAM_QUERY_RESULTS_LIMIT), -1);
			List<UwsJobOwner> users = uwsStorage.retrieveOwners(filter, offset, limit);
			if (users != null) {
				for (UwsJobOwner owner : users) {
					// Get current user quota:
					UwsQuotaSingleton.getInstance().updateOwnerQuotaParameters(owner);
				}
			}
			ManagerUtils.writeUserList(response, UwsOutputResponseHandler.OK, users);
		} catch (UwsException e) {
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", e);
		}
		response.flushBuffer();
		return;
	}

	private UwsJobsOwnersFilter createFilter(Map<String, String> parameters){
		UwsJobsOwnersFilter filter = new UwsJobsOwnersFilter();
		boolean filterFound = false;
		String value;
		
		value = parameters.get(Manager.PARAM_USER_ID);
		if(value != null && !"".equals(value)){
			filterFound = true;
			filter.setIdFilter(value);
		}

		if(filterFound){
			return filter;
		}else{
			return null;
		}
	}

	@Override
	public String getActionIdentifier() {
		return ACTION;
	}
}
