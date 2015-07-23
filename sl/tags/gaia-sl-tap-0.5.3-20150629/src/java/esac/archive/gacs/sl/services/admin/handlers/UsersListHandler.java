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
