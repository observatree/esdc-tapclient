package esac.archive.gacs.sl.services.status.types;

import esac.archive.gacs.sl.services.status.StatusData;
import esac.archive.gacs.sl.services.status.TaskType;

public class StatusDataFactory {
	
	public static StatusData createStatusData(TaskType type, String info){
		switch(type){
		case INGESTION:
			return new StatusIngestion(info);
		case PARSE:
			return new StatusParse(info);
		case UPLOAD:
			return new StatusUpload(info);
		case TABLE_EDIT:
			return new StatusTableEdit(info);
		case XMATCH_ANALYZE:
			return new XMatchAnalysis(info);
		case XMATCH_CREATE:
			return new XMatchCreation(info);
		case XMATCH_TAP_UPDATE:
			return new XMatchTapUpdate(info);
		default:
			throw new IllegalArgumentException("Cannot find suitable class for type: " + type.name());
		}
	}

}
