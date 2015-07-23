package esac.archive.gacs.sl.services.status.types;

import esac.archive.gacs.sl.services.status.TaskType;

public class XMatchAnalysis extends AbstractStatusData{
	
	public static final TaskType TYPE = TaskType.XMATCH_ANALYZE;
	
	public XMatchAnalysis(String info){
		super(TYPE, info);
	}

}
