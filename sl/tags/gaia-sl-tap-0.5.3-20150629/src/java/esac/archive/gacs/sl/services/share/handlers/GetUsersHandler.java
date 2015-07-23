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
