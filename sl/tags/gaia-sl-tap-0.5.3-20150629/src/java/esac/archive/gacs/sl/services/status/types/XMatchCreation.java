package esac.archive.gacs.sl.services.status.types;

import esac.archive.gacs.sl.services.status.TaskType;

public class XMatchCreation extends AbstractStatusData{
	
	public static final TaskType TYPE = TaskType.XMATCH_CREATE;
	
	public XMatchCreation(String info){
		super(TYPE, info);
	}

}
