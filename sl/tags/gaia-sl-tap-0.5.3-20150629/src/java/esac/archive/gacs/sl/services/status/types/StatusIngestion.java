package esac.archive.gacs.sl.services.status.types;

import esac.archive.gacs.sl.services.status.TaskType;

public class StatusIngestion extends AbstractStatusData{
	
	public static final TaskType TYPE = TaskType.INGESTION;
	
	public StatusIngestion(String info){
		super(TYPE, info);
	}

}
