package esac.archive.gacs.sl.services.admin.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.admin.Manager;
import esac.archive.gacs.sl.services.admin.ManagerHandler;
import esac.archive.gacs.sl.services.admin.ManagerUtils;
import esavo.uws.UwsException;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.storage.UwsQuotaSingleton;
import esavo.uws.storage.UwsStorage;

public class UserDetailsHandler implements ManagerHandler {

	public static final String ACTION = "user_details";

	@Override
	public boolean canHandle(String action) {
		return ACTION.equalsIgnoreCase(action);
	}

	@Override
	public void handle(Map<String, String> parameters, HttpServletResponse response, UwsStorage uwsStorage) throws IOException {
		String userid = parameters.get(Manager.PARAM_USER_ID);
		if(userid == null){
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", "User identifier not found.");
		}else{
			try {
				//UwsJobOwner userDetails = dbConn.loadUserDetails(userid);
				UwsJobOwner userDetails = uwsStorage.getOwner(userid);
				List<UwsJobOwner> users = new ArrayList<UwsJobOwner>();

				// Get current user quota:
				UwsQuotaSingleton.getInstance().updateOwnerQuotaParameters(userDetails);
				
				users.add(userDetails);
				ManagerUtils.writeUserList(response, UwsOutputResponseHandler.OK, users);
			} catch (UwsException e) {
				ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", e);
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
