package esac.archive.gacs.sl.services.status.types;

import esac.archive.gacs.sl.services.status.StatusData;
import esac.archive.gacs.sl.services.status.TaskType;

public abstract class AbstractStatusData implements StatusData{
	private String statusData;
	private TaskType statusType;
	
	public AbstractStatusData(TaskType statusType){
		this(statusType, null);
	}
	
	public AbstractStatusData(TaskType statusType, String initialInfo){
		this.statusType = statusType;
		this.statusData = initialInfo;
	}

	@Override
	public void setData(String data) {
		this.statusData = data;
	}

	@Override
	public String getData() {
		return this.statusData;
	}
	
	@Override
	public TaskType getType(){
		return this.statusType;
	}
	
	@Override
	public String toString(){
		return "Status type: " + getType().name() + ", data: " + getData();
	}

}
