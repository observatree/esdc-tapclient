package esac.archive.gacs.sl.services.status.types;

import esac.archive.gacs.sl.services.status.TaskType;

public class StatusTableEdit extends AbstractStatusData{
	
	public static final TaskType TYPE = TaskType.TABLE_EDIT;
	
	public StatusTableEdit(String info){
		super(TYPE, info);
	}

}
