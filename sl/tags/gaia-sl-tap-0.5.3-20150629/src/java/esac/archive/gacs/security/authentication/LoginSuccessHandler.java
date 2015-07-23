package esac.archive.gacs.security.authentication;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import esac.archive.gacs.common.constants.HttpConstants;
import esavo.uws.UwsManager;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.owner.UwsJobsOwnersManager;
import esavo.uws.storage.UwsQuotaSingleton;
import esavo.uws.storage.UwsStorage;
import esavo.uws.utils.UwsUtils;

public class  LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    
    @Override
    public void  onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {

		try {
			UwsManager uwsManager = UwsManager.getInstance();
			UwsJobsOwnersManager jobsOwnersManager = uwsManager.getFactory().getJobsOwnersManager();
			UwsStorage storage = uwsManager.getFactory().getStorageManager();
			String ownerId = "";
			
			if(authentication.getName() != null && !"".equals(authentication.getName())){
				ownerId = authentication.getName();
			} else {
				ownerId = UwsUtils.ANONYMOUS_USER;
			}
			
			UwsJobOwner owner = jobsOwnersManager.loadOrCreateOwner(ownerId);
			
			// Get user quota (force calculation of real current size)
			//UwsQuota quota = UwsQuotaSingleton.getInstance().createOrLoadQuota(owner,true);
			
			// Update user quotas and persist to DB
			UwsQuotaSingleton.getInstance().updateOwnerQuotaParameters(owner);
			storage.updateOwner(owner);
			
		} catch (Exception e) {
			throw new IOException("Error updating user quotas for user "+authentication.getName(),e);
		};
		
		String queryString = request.getQueryString();
		
		//String redirect = request.getParameter("redirect");
		if(queryString != null && queryString.contains("ticket")){
			super.onAuthenticationSuccess(request, response, authentication);
		}else{
			response.setStatus(HttpConstants.OK);
		}


    }
    
}