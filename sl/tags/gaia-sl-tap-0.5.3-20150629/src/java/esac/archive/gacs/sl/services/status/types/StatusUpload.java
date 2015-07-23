package esac.archive.gacs.sl.services.status.types;

import esac.archive.gacs.sl.services.status.TaskType;

public class StatusUpload extends AbstractStatusData{
	
	public static final TaskType TYPE = TaskType.UPLOAD;
	
	public StatusUpload(String info){
		super(TYPE, info);
	}

}
