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
package esac.archive.gacs.sl.tap.actions;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;

import esac.archive.gacs.security.UserContextService;
import esac.archive.gacs.security.UserContextServiceImpl;
import esavo.uws.UwsException;
import esavo.uws.UwsManager;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.owner.UwsJobsOwnersManager;
import esavo.uws.security.UwsSecurity;
import esavo.uws.storage.UwsQuota;
import esavo.uws.storage.UwsStorage;
import esavo.uws.utils.UwsUtils;

public class TapSecurityManager implements UwsSecurity {
	
	//private TAPFactory factory;
	private String appid;
	
//	public TapSecurityManager(TAPFactory factory){
//		this.factory = factory;
//		this.appid = factory.getAppId();
//	}
	
	public TapSecurityManager(String appid){
		//Black magic to make sync/async threads work with springsecuirty 4.0 in command line, blah,blah,blah...
		//It is set in springsecurity xml config (beans...)
		//SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
		this.appid = appid;
	}
	
	@Override
	public UwsJobOwner getUser() throws UwsException {
//		UserContextService userContextService = new UserContextServiceImpl();
//		UwsJobOwner ownerTmp = createJobOwner(userContextService.getCurrentUser());
//		int roles = getRoles(userContextService.getCurrentSessionId(), ownerTmp.getId());
//		UwsJobOwner owner = new UwsJobOwner(ownerTmp.getId(), roles);
//		owner.setAuthUsername(ownerTmp.getAuthUsername());
//		owner.setPseudo(ownerTmp.getPseudo());
//		return owner;
		
		UserContextService userContextService = new UserContextServiceImpl();
		String authUsername = userContextService.getCurrentUser();
		String ownerid = getOwnerId(authUsername);
		String pseudo = getPseudo(authUsername);
		
		//UwsManager uwsManager = UwsManager.getInstance(appid);
		UwsManager uwsManager = UwsManager.getInstance();
		UwsJobsOwnersManager jobsOwnersManager = uwsManager.getFactory().getJobsOwnersManager(); 
		UwsJobOwner owner = jobsOwnersManager.loadOrCreateOwner(ownerid);
		
		UwsStorage storage = uwsManager.getFactory().getStorageManager();
		
		//System.out.println(UpdateQuotasSingleton.getInstance().toString());
		
		owner.setAuthUsername(authUsername);
		owner.setPseudo(pseudo);
		
		return owner;
	}
	
	/**
	 * If authUserName == null   : ownerId = "anonymous";
	 * If authUserName == ""     : ownerId = "anonymous";
	 * If authUserName == [value]: ownerId = [value];
	 * @param authUsername
	 * @return
	 */
	private String getOwnerId(String authUsername){
		if(authUsername != null && !"".equals(authUsername)){
			return authUsername;
		} else {
			return UwsUtils.ANONYMOUS_USER;
		}
	}
	
	/**
	 * If authUserName == null   : pseudo = "anonymous"
	 * If authUserName == ""     : pseudo = ""
	 * If authUserName == [value]: pseudo = [value]
	 * @param authUserName
	 * @return
	 */
	private String getPseudo(String authUserName){
		if(authUserName != null){
			return authUserName;
		} else {
			return UwsUtils.ANONYMOUS_USER;
		}
	}
	
//	/**
//	 * Creates a default job owner.<br/>
//	 * If authUserName == null   : ownerId = "anonymous"; pseudo = "anonymous"
//	 * If authUserName == ""     : ownerId = "anonymous"; pseudo = ""
//	 * If authUserName == [value]: ownerId = [value];     pseudo = [value]
//	 * @param authUsername
//	 * @return
//	 */
//	private UwsJobOwner createJobOwner(String authUsername) {
//		String ownerId;
//		if (authUsername == null || "".equals(authUsername)) {
//			ownerId = "anonymous";
//		} else {
//			ownerId = authUsername;
//		}
//		
//		String pseudo = ownerId;
//
//		if (authUsername != null) {
//			pseudo = authUsername;
//		}
//		UwsJobOwner jobOwner = new UwsJobOwner(ownerId, UwsJobOwner.ROLE_USER);
//		jobOwner.setPseudo(pseudo);
//		jobOwner.setAuthUsername(authUsername);
//		return jobOwner;
//	}


	@Override
	public void setUser(UwsJobOwner user) {
		//Nothing to do, setUser is done by spring security
	}
	
//	private int getRoles(String sessionid, String userid){
//		RolesCache rc = RolesCache.getInstance();
//		RolesCacheData rolesCacheData = rc.getData(sessionid, userid);
//		if (rolesCacheData != null) {
//			return rolesCacheData.getRoles();
//		} else {
//			JDBCPooledFunctions dbConn = null;
//			try {
//				dbConn = (JDBCPooledFunctions) factory.createDBConnection("TapServiceId");
//				UserDetails userDetails = dbConn.loadUserDetails(userid);
//				int roles = UwsJobOwner.ROLE_USER;
//				if (userDetails != null) {
//					roles = userDetails.getRoles();
//				}
//				rc.putRoles(sessionid, userid, roles);
//				return roles;
//			} catch (TAPException e) {
//				e.printStackTrace();
//				return UwsJobOwner.ROLE_USER;
//			} finally {
//				if (dbConn != null) {
//					try {
//						dbConn.close();
//					} catch (DBException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	}

	
	@Override
	public String toString(){
		return "TAP Security Manager for application '"+appid+"'";
	}


}
