package esac.archive.gacs.sl.services.admin.handlers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.admin.ManagerHandler;
import esac.archive.gacs.sl.services.admin.ManagerUtils;
import esavo.uws.UwsManager;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.storage.UwsStorage;

public class JobsRemoveProcedure implements ManagerHandler {

	public static final String ACTION = "jobs_remove_procedure";

	@Override
	public boolean canHandle(String action) {
		return ACTION.equalsIgnoreCase(action);
	}

	@Override
	public void handle(Map<String, String> parameters, HttpServletResponse response, UwsStorage uwsStorage) throws IOException  {
		try {
			//UwsManager manager = UwsManager.getInstance(uwsStorage.getAppId());
			UwsManager manager = UwsManager.getInstance();
			String report = manager.checkJobsRemovalProcedure();
			ManagerUtils.writeJobsRemovalReport(response, report);
		} catch (IOException e) {
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", e);
		}
		response.flushBuffer();
		return;
	}

	@Override
	public String getActionIdentifier() {
		return ACTION;
	}
}
