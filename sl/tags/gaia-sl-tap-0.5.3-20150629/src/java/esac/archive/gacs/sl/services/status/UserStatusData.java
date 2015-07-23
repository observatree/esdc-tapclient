package esac.archive.gacs.sl.services.status;

import java.util.HashMap;
import java.util.Map;

public class UserStatusData {
	
	private Map<TaskType, StatusData> statusMap;
	
	private long lastUpdate;
	private long taskid;
	private UserInfo userInfo;
	
	public UserStatusData(long taskid, UserInfo userInfo){
		this.taskid = taskid;
		this.userInfo = userInfo;
		statusMap = new HashMap<TaskType, StatusData>();
		updateTime();
	}
	
	public long getLastUpdate(){
		return this.lastUpdate;
	}
	
	public long getTaskId(){
		return this.taskid;
	}
	
	public UserInfo getUserInfo(){
		return this.userInfo;
	}
	
	/**
	 * THIS FUNCTION UPDATES ACCESS TIME
	 * @param data
	 */
	public synchronized void updateStatus(StatusData data){
		statusMap.put(data.getType(), data);
		updateTime();
	}
	
	/**
	 * THIS FUNCTION UPDATES ACCESS TIME
	 * @param type
	 * @return
	 */
	public synchronized StatusData getStatus(TaskType type){
		updateTime();
		return statusMap.get(type);
	}
	
	/**
	 * THIS FUNCTION UPDATES ACCESS TIME
	 * The task type is removed
	 * @param type
	 * @return
	 */
	public synchronized StatusData consumeStatus(TaskType type){
		updateTime();
		StatusData sd = statusMap.remove(type);
		return sd;
	}
	
	/**
	 * THIS FUNCTION UPDATES ACCESS TIME
	 * @param type
	 * @return
	 */
	public synchronized Map<TaskType, StatusData> getAllStatus(){
		updateTime();
		Map<TaskType, StatusData> map = new HashMap<TaskType, StatusData>(statusMap);
		return map;
	}
	
	/**
	 * THIS FUNCTION UPDATES ACCESS TIME
	 * @param type
	 * @return
	 */
	public synchronized Map<TaskType, StatusData> consumeAllStatus(){
		updateTime();
		Map<TaskType, StatusData> map = new HashMap<TaskType, StatusData>(statusMap);
		statusMap.clear();
		return map;
	}
	
	private synchronized void updateTime(){
		lastUpdate = System.currentTimeMillis();
	}
	
	@Override
	public String toString(){
		return "Task id: " + this.taskid + ", User info: " + this.userInfo + ", last update: " + this.lastUpdate;
	}

}
