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
package esac.archive.gacs.sl.services.share.handlers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.share.Share;
import esac.archive.gacs.sl.services.share.ShareHandler;
import esac.archive.gacs.sl.services.share.ShareUtils;
import esavo.uws.UwsException;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.share.UwsShareManager;
import esavo.uws.share.UwsShareUser;

public class GetUsersHandler implements ShareHandler {

	public static final String ACTION = "get_users";

	@Override
	public boolean canHandle(String action) {
		return ACTION.equalsIgnoreCase(action);
	}

	@Override
	public void handle(Map<String, String> parameters, HttpServletResponse response, UwsShareManager shareManager) throws IOException  {
		String userPattern = parameters.get(Share.PARAM_USER_PATTERN);
		List<UwsShareUser> users;
		try {
//			if (userPattern == null) {
//				ShareUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", "User pattern not found.");
//			}
			users = shareManager.getUsers(userPattern, Share.MAX_USERS_RESULT);
			ShareUtils.writeUsers(response, UwsOutputResponseHandler.OK, users);
		} catch (UwsException e) {
			int code = e.getCode();
			if (code < 0) {
				code = UwsOutputResponseHandler.INTERNAL_SERVER_ERROR;
			}
			ShareUtils.writeError(response, code, "Error", e);
		}
		response.flushBuffer();
		return;
	}

	@Override
	public String getActionIdentifier() {
		return ACTION;
	}
}
