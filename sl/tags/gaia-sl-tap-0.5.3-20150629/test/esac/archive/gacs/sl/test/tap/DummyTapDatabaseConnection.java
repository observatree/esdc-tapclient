package esac.archive.gacs.sl.test.tap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import uk.ac.starlink.table.StarTable;
import esac.archive.gacs.sl.services.publicgroup.PublicGroupItem;
import esac.archive.gacs.sl.tap.actions.JDBCPooledFunctions;
import esac.archive.gacs.sl.tap.actions.TapTableInfo;
import esavo.adql.query.ADQLQuery;
import esavo.tap.db.DBException;
import esavo.tap.metadata.TAPTable;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.test.database.DummyDatabaseConnection;

public class DummyTapDatabaseConnection implements JDBCPooledFunctions {
	
	private DummyDatabaseConnection dbConn;
	//private long taskid;
	//private boolean requestedIndexPk;
	//private boolean requestedUpdateTableSize;
	//private boolean requestedVacuum;
	
	public static final String ACTION_CMD_DELETE = "DELETE";
	public static final String ACTION_CMD_REQ_PK = "REQ_PK"; 
	public static final String ACTION_CMD_REQ_INDEX_RA_DEC = "REQ_INDEX_RA_DEC";
	public static final String ACTION_CMD_REMOVE_INDEX_RA_DEC = "REMOVE_INDEX_RA_DEC";
	public static final String ACTION_CMD_REQ_UPDATE_TABLE_SIZE = "REQ_UPDATE_TABLE_SIZE"; 
	public static final String ACTION_CMD_REQ_VACUUM = "REQ_VACUUM";
	public static final String ACTION_CMD_START_TRANSACTION = "START_TRANSACTION";
	public static final String ACTION_CMD_END_TRANSACTION = "END_TRANSACTION";
	public static final String ACTION_CMD_TRANSACTION_ROLLBACK = "TRANSACTION_ROLLBACK";
	public static final String ACTION_CMD_CLOSE_DB_CONNECTION = "CLOSE_DB_CONNECTION";
	public static final String ACTION_CMD_EXECUTE_QUERY = "EXECUTE_QUERY";
	public static final String ACTION_CMD_CREATE_SCHEMA = "CREATE_SCHEMA";
	public static final String ACTION_CMD_DROP_SCHEMA = "DROP_SCHEMA";
	public static final String ACTION_CMD_CREATE_TABLE = "CREATE_TABLE";
	public static final String ACTION_CMD_INSERT_ROW = "INSERT_ROW";
	public static final String ACTION_CMD_DROP_TABLE = "DROP_TABLE";
	public static final String ACTION_CMD_DROP_TABLE_FORCE = "DROP_TABLE_FORCE";
	public static final String ACTION_CMD_GET_ID = "GET_ID";
	public static final String ACTION_CMD_REGISTER_IN_TAP = "REGISTER_IN_TAP";
	public static final String ACTION_CMD_UNREGISTER_FROM_TAP = "UNREGISTER_FROM_TAP";
	public static final String ACTION_CMD_LOAD_TABLE_DATA = "LOAD_TABLE_DATA";
	//public static final String ACTION_CMD_GET_OLD_JOBS = "GET_OLD_JOBS";
	public static final String ACTION_CMD_INDEX_TABLE_COLUMN = "INDEX_TABLE_COLUMN";
	public static final String ACTION_CMD_UPDATE_USER_LOADED_TABLE = "UPDATE_USER_LOADED_TABLE";
	public static final String ACTION_CMD_UPDATE_RADEC_USER_LOADED_TABLE = "UPDATE_RADEC_USER_LOADED_TABLE";
	public static final String ACTION_CMD_FETCH_TAP_TABLE_INFO = "FETCH_TAP_TABLE_INFO";
	public static final String ACTION_CMD_REMOVE_INDEX_TABLE_COLUMN = "REMOVE_INDEX_TABLE_COLUMN";
	public static final String ACTION_CMD_LOAD_USER_DETAILS = "LOAD_USER_DETAILS";
	public static final String ACTION_CMD_CREATE_USER = "CREATE_USER";
	public static final String ACTION_CMD_RETRIEVE_USERS_BY_FILTER = "RETRIEVE_ALL_USERS";
	public static final String ACTION_CMD_LOAD_JOB_DETAILS = "LOAD_JOB_DETAILS";
	public static final String ACTION_CMD_RETRIEVE_JOBS_BY_FILTER = "RETRIEVE_JOBS_BY_FILTER";
	public static final String ACTION_CMD_UPDATE_USER_DETAILS = "UPDATE_USER_DETAILS";
	public static final String ACTION_CMD_ADD_ACCESS_TO_PUBLIC_GROUP_TABLE = "ADD_ACCESS_TO_PUBLIC_GROUP_TABLE";
	public static final String ACTION_CMD_REMOVE_ACCESS_TO_PUBLIC_GROUP_TABLE = "REMOVE_ACCESS_TO_PUBLIC_GROUP_TABLE";
	public static final String ACTION_CMD_LIST_PUBLIC_GROUP_TABLES = "LIST_PUBLIC_GROUP_TABLES";
	public static final String ACTION_CMD_PUBLIC_GROUP_SHARED_ITEMS = "GET_PUBLIC_GROUP_SHARED_ITEMS";
	
	private List<String> actions;
	private List<String> oldJobs;
	private TapTableInfo tapTableInfo;
//	private UserDetails userDetails;
//	private List<UserDetails> usersDetailsList;
//	private JobDetails jobDetails;
//	private List<JobDetails> jobsDetailsList;
	
	private boolean raiseException;
	private int raiseExceptionByCounter;
	
	private long dbSize;
	private long tableSize;
	
	public DummyTapDatabaseConnection(DummyDatabaseConnection dbconnection){
		dbConn = dbconnection;
		actions = new ArrayList<String>();
		clearFlags();
	}
	
	private String createLogEntry(String action, String msg){
		return action + ": " + msg;
	}
	
	public void setRaiseException(boolean raiseException){
		this.raiseException = raiseException;
	}
	
	/**
	 * 0 disabled. 1: next query will raise the exception. n: n-th query will raise the exception  
	 * @param raiseExceptionByCounter
	 */
	public void setRaiseExceptionByCounter(int raiseExceptionByCounter){
		this.raiseExceptionByCounter = raiseExceptionByCounter;
	}
	
	private void logAction(String action, String msg) throws DBException{
		if(raiseException){
			throw new DBException("Exception requested");
		}
		if(raiseExceptionByCounter > 0){
			raiseExceptionByCounter--;
			if(raiseExceptionByCounter == 0){
				throw new DBException("Exception requested");
			}
		}
		String logEntry = createLogEntry(action, msg);
		System.out.println("DB action: " + logEntry);
		actions.add(logEntry);
	}
	
	public boolean isActionLogged(String action, String msg){
		return actions.contains(createLogEntry(action, msg));
	}
	
	public String findSimilarAction(String action){
		StringBuilder sb = new StringBuilder();
		boolean firstTime = true;
		for(String a: actions){
			if(a.startsWith(action)){
				if(firstTime){
					firstTime = false;
				}else{
					sb.append("\n");
				}
				sb.append("\t'").append(a).append('\'');
			}
		}
		return sb.toString();
	}
	
	public String getRegisteredActions(){
		StringBuilder sb = new StringBuilder();
		boolean firstTime = true;
		for(String a: actions){
			if(firstTime){
				firstTime = false;
			}else{
				sb.append("\n");
			}
			sb.append("\t'").append(a).append('\'');
		}
		return sb.toString();
	}

//	@Override
//	public void setTaskId(long taskId) {
//		this.taskid = taskId;
//	}
//	
//	public long getTaskId(){
//		return taskid;
//	}
	
	public void clearFlags(){
//		requestedIndexPk = false;
//		requestedUpdateTableSize = false;
//		requestedVacuum = false;
		actions.clear();
		raiseException = false;
		raiseExceptionByCounter = 0;
	}

//	public boolean isRequestedIndexPk(String table){
//		//return requestedIndexPk;
//		return isActionLogged(table, ACTION_CMD_REQ_PK);
//	}
//
//	public boolean isRequestedUpdateTableSize(String table){
//		//return requestedUpdateTableSize;
//		return isActionLogged(table, ACTION_CMD_REQ_UPDATE_TABLE_SIZE);
//	}
//
//	public boolean isRequestedVacuum(String table){
//		//return requestedVacuum;
//		return isActionLogged(table, ACTION_CMD_REQ_VACUUM);
//	}

	@Override
	public void updateTableSizeInTapSchema(TAPTable table) throws DBException {
		//requestedUpdateTableSize = true;
		logAction(ACTION_CMD_REQ_UPDATE_TABLE_SIZE, table.getFullName());
	}

//	@Override
//	public void indexAndPk(TAPTable table, String PARAM_RACOL,
//			String PARAM_DECCOL) throws DBException {
//		//requestedIndexPk = true;
//		logAction(ACTION_CMD_REQ_PK, table.getFullName());
//	}
	
	@Override
	public String createPkInTable(TAPTable table) throws DBException {
		logAction(ACTION_CMD_REQ_PK, table.getFullName());
		return "OK";
	}
	
	@Override
	public void createRaAndDecIndexes(String schemaName, String tableName, String raCol, String decCol, int raDecFlag) throws DBException {
		logAction(ACTION_CMD_REQ_INDEX_RA_DEC, schemaName + "." + tableName + " (ra: " + raCol + ",dec: "+decCol+")");
	}

	@Override
	public void removeRaAndDecIndexes(String schemaName, String tableName, String raCol, String decCol, int raDecFlag) throws DBException {
		logAction(ACTION_CMD_REMOVE_INDEX_RA_DEC, schemaName + "." + tableName + " (ra: " + raCol + ",dec: "+decCol+")");
	}
	@Override
	public void vacuumAnalyze(String schemaName, String tableName) throws DBException {
		logAction(ACTION_CMD_REQ_VACUUM, schemaName+"."+tableName);
	}
	
	
	//---------------------------------------------------------
	@Override
	public void startTransaction() throws DBException {
		try {
			dbConn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			logAction(ACTION_CMD_START_TRANSACTION,"");
		} catch (SQLException e) {
			throw new DBException("Impossible to start transaction, because: "+e.getMessage());
		}
	}

	@Override
	public void cancelTransaction() throws DBException {
		try{
			dbConn.rollback();
			logAction(ACTION_CMD_TRANSACTION_ROLLBACK,"");
		} catch (SQLException e) {
			throw new DBException("Impossible to cancel transaction, because: "+e.getMessage());
		}
	}

	@Override
	public void endTransaction() throws DBException {
		try{
			dbConn.commit();
			logAction(ACTION_CMD_END_TRANSACTION,"");
		} catch (SQLException e) {
			throw new DBException("Impossible to commit transaction, because: "+e.getMessage());
		}
	}

	@Override
	public void close() throws DBException {
		try{
			dbConn.close();
			logAction(ACTION_CMD_CLOSE_DB_CONNECTION,"");
		} catch (SQLException e) {
			throw new DBException("Impossible to close DB connection, because: "+e.getMessage());
		}
	}

	@Override
	public ResultSet executeQuery(String sql, ADQLQuery query) throws DBException {
		try{
			Statement stmt = dbConn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			logAction(ACTION_CMD_EXECUTE_QUERY, sql);
			return rs;
		}catch(SQLException se){
			throw new DBException("Can not execute the following SQL: \n"+sql+"\n. Because: "+se.getMessage());
		}
	}


	/* ************************************** */
	/* METHODS USED ONLY IF UPLOAD IS ENABLED */
	/* ************************************** */

	@Override
	public void createSchema(String arg0) throws DBException {
		//System.out.println("TapDBConnection: Entering createSchema("+arg0+")");
		logAction(ACTION_CMD_CREATE_SCHEMA, arg0);
	}

	@Override
	public void dropSchema(String arg0) throws DBException {
		//System.out.println("TapDBConnection: Entering dropSchema("+arg0+")");
		logAction(ACTION_CMD_DROP_SCHEMA, arg0);
	}

	@Override
	public void createTable(TAPTable table) throws DBException {
		//System.out.println("TapDBConnection: Entering createTable("+table.getDBName()+")");
		logAction(ACTION_CMD_CREATE_TABLE, table.getFullName());
	}
	

//	@Override
//	public void insertRow(SavotTR row, TAPTable table) throws DBException {
//		//System.out.println("TapDBConnection: Entering insertRow()");
//		logAction(ACTION_CMD_INSERT_ROW, table.getFullName());
//	}

	@Override
	public void dropTable(TAPTable table) throws DBException {
		//System.out.println("TapDBConnection: Entering dropTable("+table.getDBName()+")");
		logAction(ACTION_CMD_DROP_TABLE, table.getFullName());
	}

	@Override
	public void dropTable(TAPTable table, boolean forceRemoval) throws DBException {
		//System.out.println("TapDBConnection: Entering dropTable("+table.getDBName()+")");
		logAction(ACTION_CMD_DROP_TABLE_FORCE, table.getFullName());
	}

	@Override
	public String getID() {
		//System.out.println("TapDBConnection: Entering getID()");
		try {
			logAction(ACTION_CMD_GET_ID, "");
		} catch (DBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void registerInTapSchema(TAPTable arg0) throws DBException {
		logAction(ACTION_CMD_REGISTER_IN_TAP, arg0.getFullName());
	}

	@Override
	public void unregisterFromTapSchema(TAPTable arg0) throws DBException {
		logAction(ACTION_CMD_UNREGISTER_FROM_TAP, arg0.getFullName());
	}

	@Override
	public int loadTableData(UwsJobOwner owner, TAPTable table, StarTable starTable) throws DBException {
		return loadTableData(owner, table, starTable, -1);
	}

	@Override
	public int loadTableData(UwsJobOwner owner, TAPTable table, StarTable starTable, long taskid) throws DBException {
		logAction(ACTION_CMD_LOAD_TABLE_DATA, table.getFullName());
		return 0;
	}

//	@Override
//	public List<String> getOldJobs(String appid, String ownerid, long olderThan) throws DBException {
//		logAction(ACTION_CMD_GET_OLD_JOBS, appid + "-" + ownerid);
//		return oldJobs;
//	}
	
	public void setOldJobs(List<String> oldJobs){
		this.oldJobs = oldJobs;
	}

	@Override
	public void createTableColumnIndex(String schemaName, String tableName, String tableColumnName) throws DBException {
		logAction(ACTION_CMD_INDEX_TABLE_COLUMN,schemaName+"."+tableName+"."+tableColumnName);
	}

	@Override
	public void updateUserTableData(String schemaName, String tableName,
			String tableColumnName, String ucd, String uType, int flags, int indexed) throws DBException {
		logAction(ACTION_CMD_UPDATE_USER_LOADED_TABLE,schemaName+"."+tableName+"."+tableColumnName);
	}
	
	@Override
	public void updateUserTableRaDecData(String schemaName, String tableName,
			String raColumn, String decColumn, int flagsRa, int flagsDec)
			throws DBException {
		logAction(ACTION_CMD_UPDATE_RADEC_USER_LOADED_TABLE,
				schemaName+"."+tableName+" - "+raColumn + "/" + decColumn + " ("+flagsRa+"/"+flagsDec+")");
	}

	@Override
	public TapTableInfo fetchTapTableInfo(String schemaName, String tableName) throws DBException {
		logAction(ACTION_CMD_FETCH_TAP_TABLE_INFO,schemaName+"."+tableName);
		return tapTableInfo;
	}
	
	public void setTapTableInfo(TapTableInfo tapTableInfo){
		this.tapTableInfo = tapTableInfo;
	}

	@Override
	public void removeTableColumnIndex(String schemaName, String tableName, String tableColumnName) throws DBException {
		logAction(ACTION_CMD_REMOVE_INDEX_TABLE_COLUMN, schemaName+"."+tableName+"."+tableColumnName);
	}

	@Override
	public long getDbSize(String ownerid) throws DBException {
		return dbSize;
	}

	@Override
	public long getTableSize(String schema, String table) throws DBException {
		return tableSize;
	}
	
	public void setDbSize(long dbSize){
		this.dbSize = dbSize;
	}
	
	public void setTableSize(long tableSize){
		this.tableSize = tableSize;
	}

	@Override
	public void addAccessToPublicGroupTables(List<PublicGroupItem> items) throws DBException {
		logAction(ACTION_CMD_ADD_ACCESS_TO_PUBLIC_GROUP_TABLE, ""+items.size());
	}

	@Override
	public void removeAccessToPublicGroupTables(List<PublicGroupItem> items) throws DBException {
		logAction(ACTION_CMD_REMOVE_ACCESS_TO_PUBLIC_GROUP_TABLE, ""+items.size());
	}

	@Override
	public List<PublicGroupItem> getPublicGroupTables(String user) throws DBException {
		logAction(ACTION_CMD_LIST_PUBLIC_GROUP_TABLES, user);
		return null;
	}

	@Override
	public List<String> getPublicGroupSharedItems(List<PublicGroupItem> items, String groupid) throws DBException {
		logAction(ACTION_CMD_PUBLIC_GROUP_SHARED_ITEMS, groupid+" - "+items.size());
		return null;
	}


//	public void setUserDetails(UserDetails userDetails){
//		this.userDetails = userDetails;
//	}
//	
//	@Override
//	public UserDetails loadUserDetails(String userid) throws DBException {
//		logAction(ACTION_CMD_LOAD_USER_DETAILS, userid);
//		return userDetails;
//	}
//
//	@Override
//	public void createUser(UserDetails user) throws DBException {
//		logAction(ACTION_CMD_CREATE_USER, user.toString());
//	}
//	
//	public void setUsersDetailsList(List<UserDetails> usersDetailsList){
//		this.usersDetailsList = usersDetailsList;
//	}
//
//	@Override
//	public List<UserDetails> retrieveUsersByFilter(UsersFilter filter, long offset, long limit) throws DBException {
//		logAction(ACTION_CMD_RETRIEVE_USERS_BY_FILTER, "offset: " + offset + ", limit: " + limit + ", filter: " + (filter == null ? "":filter.toString()));
//		return usersDetailsList;
//	}
//	
//	public void setJobDetails(JobDetails jobDetails){
//		this.jobDetails = jobDetails;
//	}
//
//	@Override
//	public JobDetails loadJobDetails(String jobid) throws DBException {
//		logAction(ACTION_CMD_LOAD_JOB_DETAILS, jobid);
//		return jobDetails;
//	}
//	
//	public void setJobsDetailsList(List<JobDetails> jobsDetailsList){
//		this.jobsDetailsList = jobsDetailsList;
//	}
//
//	@Override
//	public List<JobDetails> retrieveJobsByFilter(JobsFilter filter, long offset, long limit) throws DBException {
//		logAction(ACTION_CMD_RETRIEVE_JOBS_BY_FILTER, "offset: " + offset + ", limit: " + limit + ", filter: " + (filter == null ? "":filter.toString()));
//		return jobsDetailsList;
//	}
//
//	@Override
//	public void updateUserDetails(String userid, int roles, long quotaDB, long quotaFiles) throws DBException {
//		logAction(ACTION_CMD_UPDATE_USER_DETAILS, userid + ","+roles+","+quotaDB+","+quotaFiles);
//	}

}
