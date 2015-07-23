package esac.archive.gacs.sl.services.share;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import esavo.uws.share.UwsShareManager;

public interface ShareHandler {
	
	/**
	 * Returns the action associated to this handler
	 * @return
	 */
	public String getActionIdentifier();
	
	/**
	 * Returns 'true' if the handler can handle the action.
	 * @param action
	 * @return
	 */
	public boolean canHandle(String action);
	
	/**
	 * Handles the action.
	 * @param parameters
	 * @param response
	 * @param dbConn
	 */
	public void handle(Map<String,String> parameters, HttpServletResponse response, UwsShareManager shareManager) throws IOException ;

}
