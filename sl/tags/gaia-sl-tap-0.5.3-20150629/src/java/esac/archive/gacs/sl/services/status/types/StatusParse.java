package esac.archive.gacs.sl.services.status.types;

import esac.archive.gacs.sl.services.status.TaskType;

public class StatusParse extends AbstractStatusData{
	
	public static final TaskType TYPE = TaskType.PARSE;
	
	public StatusParse(String info){
		super(TYPE, info);
	}

}
