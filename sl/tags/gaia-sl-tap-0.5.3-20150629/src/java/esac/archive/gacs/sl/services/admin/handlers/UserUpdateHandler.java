package esac.archive.gacs.sl.services.admin.handlers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.admin.Manager;
import esac.archive.gacs.sl.services.admin.ManagerHandler;
import esac.archive.gacs.sl.services.admin.ManagerUtils;
import esavo.uws.UwsException;
import esavo.uws.jobs.parameters.UwsJobOwnerParameters;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.storage.UwsQuota;
import esavo.uws.storage.UwsQuotaSingleton;
import esavo.uws.storage.UwsStorage;

public class UserUpdateHandler implements ManagerHandler {

	public static final String ACTION = "user_update";

	@Override
	public boolean canHandle(String action) {
		return ACTION.equalsIgnoreCase(action);
	}

	@Override
	public void handle(Map<String, String> parameters, HttpServletResponse response, UwsStorage uwsStorage) throws IOException  {
		String userid = parameters.get(Manager.PARAM_USER_ID);
		if(userid == null){
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", "User identifier not found.");
		}else{
			UwsJobOwner owner;
            try {
                owner = uwsStorage.getOwner(userid);
            } catch (UwsException e1) {
                ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", e1);
                return;
            }
            UwsJobOwnerParameters ownerParameters = owner.getParameters();
            if(ownerParameters == null){
                ownerParameters = new UwsJobOwnerParameters();
                owner.setParameters(ownerParameters);
            }
            long newQuotaDB;
            try{
            	newQuotaDB = Long.parseLong(parameters.get(Manager.PARAM_QUOTA_DB));
            }catch(NumberFormatException nfe){
                ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error: wrong number format", "Invalid number: " + parameters.get(Manager.PARAM_QUOTA_DB));
                return;
            }

            long newQuotaFiles;
            try{
            	newQuotaFiles = Long.parseLong(parameters.get(Manager.PARAM_QUOTA_FILES));
            }catch(NumberFormatException nfe){
                ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error: wrong number format", "Invalid number: " + parameters.get(Manager.PARAM_QUOTA_FILES));
                return;
            }
            
            UwsQuota newQuota = null;
            try {
				newQuota = UwsQuotaSingleton.getInstance().createOrLoadQuota(owner,true);
			} catch (UwsException e1) {
				throw new IOException(e1);
			}
            
            newQuota.setDbQuota(newQuotaDB);
            newQuota.setFileQuota(newQuotaFiles);
            
            try {
				UwsQuotaSingleton.getInstance().updateOwnerQuotaParameters(owner);
			} catch (UwsException e1) {
				throw new IOException(e1);
			}

            int roles;
            try{
                roles = Integer.parseInt(parameters.get(Manager.PARAM_ROLES));
            }catch(NumberFormatException nfe){
                ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error: wrong number format", "Invalid number: " + parameters.get(Manager.PARAM_ROLES));
                return;
            }
            owner.setRoles(roles);
            try {
                //dbConn.updateUserDetails(userid, roles, quotaDB, quotaFiles);
                uwsStorage.updateOwner(owner);
                ManagerUtils.writeMsg(response, UwsOutputResponseHandler.OK, "id", "User " + userid + " updated.");
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
