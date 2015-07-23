package esac.archive.gacs.sl.services.admin.handlers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.admin.Manager;
import esac.archive.gacs.sl.services.admin.ManagerHandler;
import esac.archive.gacs.sl.services.admin.ManagerUtils;
import esavo.uws.UwsException;
import esavo.uws.jobs.UwsJob;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.storage.UwsStorage;

public class JobDetailsHandler implements ManagerHandler {

	public static final String ACTION = "job_details";

	@Override
	public boolean canHandle(String action) {
		return ACTION.equalsIgnoreCase(action);
	}

	@Override
	public void handle(Map<String, String> parameters, HttpServletResponse response, UwsStorage uwsStorage) throws IOException  {
		String jobid = parameters.get(Manager.PARAM_JOB_ID);
		if(jobid == null){
			ManagerUtils.writeError(response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Error", "Job identifier not found.");
		}else{
			try {
				UwsJob job = uwsStorage.getJobMeta(jobid);
				ManagerUtils.writeJobDetails(response, UwsOutputResponseHandler.OK, job);
			} catch (UwsException e) {
				int code = e.getCode();
				if (code < 0){
					code = UwsOutputResponseHandler.INTERNAL_SERVER_ERROR;
				}
				ManagerUtils.writeError(response, code, "Error", e);
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
