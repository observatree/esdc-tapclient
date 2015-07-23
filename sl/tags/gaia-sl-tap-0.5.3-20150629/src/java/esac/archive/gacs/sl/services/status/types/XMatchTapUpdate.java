package esac.archive.gacs.sl.services.status.types;

import esac.archive.gacs.sl.services.status.TaskType;

public class XMatchTapUpdate extends AbstractStatusData{
	
	public static final TaskType TYPE = TaskType.XMATCH_TAP_UPDATE;
	
	public XMatchTapUpdate(String info){
		super(TYPE, info);
	}

}
