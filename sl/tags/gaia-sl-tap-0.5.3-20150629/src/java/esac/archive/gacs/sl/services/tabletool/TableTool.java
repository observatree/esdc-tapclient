/*******************************************************************************
 * Copyright (C) 2017 European Space Agency
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package esac.archive.gacs.sl.services.tabletool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import esac.archive.gacs.sl.services.status.StatusManager;
import esac.archive.gacs.sl.services.status.types.StatusTableEdit;
import esac.archive.gacs.sl.services.tabletool.handlers.EditTableHandler;
import esac.archive.gacs.sl.services.tabletool.handlers.RaDecHandler;
import esac.archive.gacs.sl.services.util.Utils;
import esac.archive.gacs.sl.tap.actions.JDBCPooledFunctions;
import esac.archive.gacs.sl.tap.actions.TapTableInfo;
import esavo.tap.TAPException;
import esavo.tap.TAPFactory;
import esavo.tap.TAPService;
import esavo.tap.db.DBException;
import esavo.tap.metadata.TAPMetadata;
import esavo.uws.UwsException;
import esavo.uws.event.UwsEventType;
import esavo.uws.output.UwsExceptionOutputFormat;
import esavo.uws.output.UwsOutputResponseHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.security.UwsSecurity;


/**
 * This class is able to change some table columns and to perform an index operation.<br/>
 * Format:
 * <pre><tt>
 * 
 * Complete edition: NUMTABLES required (TABLE_NAME ignored)
 * 
 * NUMTABLES=nt
 * TABLEnt=table
 * TABLEnt_NUMCOLS=nc
 * TABLEnt_COLn=column_name_nc
 * TABLEnt_COLn_UCD=column_nc UCD
 * TABLEnt_COLn_UTYPE=column_nc utype
 * TABLEnt_COLn_FLAGS=column_nc flags (it is expected a single string value, like 'ra'/'dec'/'mag'/'flux' ...)
 * TABLEnt_COLn_INDEXED=column_nc indexed yes/no (true/false)
 * 
 * 
 * Assign Ra/Dec: TABLE_NAME required (NUMTABLES ignored)
 * TABLE_NAME=schema.name
 * RA=ra_column
 * DEC=dec_column
 * 
 * If TABLE_NAME and NUMTABLES are present at the same time, an error is raised.
 * </tt></pre>
 * <p><b>NOTE</b>: In case table contains a schema name, the schema is removed, in order to use the authenticated user schema.
 * All user schemas are protected because the schema is set by the security context.
 * @param <R>
 */
public class TableTool {
	
	public static final String PARAM_TASKID = "TASKID";
	public static final String PARAM_ACTION = "ACTION";
	
//	public static final String PARAM_NUM_TABLES = "NUMTABLES";
//	public static final String PARAM_TASKID = "TASKID";
//	
//	public static final String PARAM_PREFIX_TABLE = "TABLE";
//	
//	public static final String PARAM_SUFFIX_NUM_COLS = "_NUMCOLS";
//	public static final String PARAM_SUFFIX_COLUMN = "_COL";
//	public static final String PARAM_COL_SUFFIX_UCD = "_UCD";
//	public static final String PARAM_COL_SUFFIX_UTYPE = "_UTYPE";
//	public static final String PARAM_COL_SUFFIX_INDEXED = "_INDEXED";
//	public static final String PARAM_COL_SUFFIX_FLAGS = "_FLAGS";
	
//	class IndexInfo{
//		boolean removeOldIndex = false;
//		boolean createNewIndex = false;
//		
//		public boolean isNewIndexRequested(){
//			return createNewIndex;
//		}
//		public boolean isOldIndexRemovalRequested(){
//			return removeOldIndex;
//		}
//		public void setRemoveOldIndex(boolean b){
//			removeOldIndex = b;
//		}
//		public void setCreateNewIndex(boolean b){
//			createNewIndex = b;
//		}
//	}
	
	private static final List<TableToolHandler> HANDLERS = new ArrayList<TableToolHandler>();
	static {
		HANDLERS.add(new EditTableHandler());
		HANDLERS.add(new RaDecHandler());
	}
	
	protected final TAPService service;

	public TableTool(TAPService serviceConnection) {
		service = serviceConnection;
	}

	public void executeRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		Map<String,String> parameters = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		Enumeration<String> e = request.getParameterNames();
		String paramName;
		while(e.hasMoreElements()){
			paramName = e.nextElement();
			parameters.put(paramName, request.getParameter(paramName));
		}
		
		UwsSecurity security = service.getFactory().getSecurityManager();
		UwsJobOwner user;
		try {
			user = security.getUser();
		} catch (UwsException e1) {
			throw new ServletException("Cannot obtain current user: " + e1.getMessage(), e1);
		}

		try {
			//DENY ACCESS TO UNAUTHENTICATED/UNAUTHORIZED USERS
			Utils.checkAuthentication(user);

			String action = parameters.get(PARAM_ACTION);
			if(action == null){
				service.getFactory().getOutputHandler().writeServerErrorResponse(
						response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "TableTool", null, PARAM_ACTION + " parameter not found.");
				return;
			}

			long taskIdentifier = -1;
			String taskid = parameters.get(PARAM_TASKID);
			if(taskid != null && !"".equals(taskid)){
				try{
					taskIdentifier = Long.parseLong(taskid);
				}catch(NumberFormatException nfe){
					service.getFactory().getOutputHandler().writeServerErrorResponse(
							response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "TableTool", null, "Cannot get task identifier");
					return;
				}
			}

			for(TableToolHandler handler: HANDLERS){
				if(handler.getAction().equalsIgnoreCase(action)){
					handler.handle(parameters, taskIdentifier, user, response, service);
					response.flushBuffer();
					return;
				}
			}
			
			//handler not found
			service.getFactory().getOutputHandler().writeServerErrorResponse(
					response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "TableTool", null, 
					"Cannot find a suitable handler for action '"+action+"'.\nAvailable actions are: " + getAvailableActions());
			response.flushBuffer();
			
//			//Check parameters
//			check(parameters);
//			
//			
//			int numTablesToProcess = -1;
//			String numCols = parameters.get(PARAM_NUM_TABLES);
//			try{
//				numTablesToProcess = Integer.parseInt(numCols);
//			}catch(NumberFormatException nfe){
//				service.getFactory().getOutputHandler().writeServerErrorResponse(
//						response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "TableTool", null, "Cannot get number of columns");
//				return;
//			}
//
//			execute(parameters, taskIdentifier, numTablesToProcess, user);
//
//			response.flushBuffer();
		}catch(Throwable t){
			//errorWriter.writeError(t, response, request, owner, "Updating tables."); 
			try {
				service.getFactory().getOutputHandler().writeServerErrorResponse(
						response, UwsOutputResponseHandler.INTERNAL_SERVER_ERROR, "Updating tables", t, UwsExceptionOutputFormat.HTML);
			} catch (UwsException e1) {
				throw new ServletException(e1);
			}
		}

	}
	
	private String getAvailableActions(){
		StringBuilder sb = new StringBuilder();
		boolean firstTime = true;
		for(TableToolHandler handler: HANDLERS){
			if(firstTime){
				firstTime = false;
			}else{
				sb.append(", ");
			}
			sb.append(handler.getAction());
		}
		return sb.toString();
	}
	
//	/**
//	 * package-private for test-harnesses<br/>
//	 * Checks the required parameters.<br/>
//	 * Mandatory: PARAM_NUM_TABLES, PARAM_PREFIX_TABLEn, PARAM_NUM_COLUMNSn
//	 * @param parameters request parameters.
//	 * @throws IllegalArgumentException 
//	 */
//	void check(Map<String,String> parameters) throws IllegalArgumentException {
//		String numTables = parameters.get(PARAM_NUM_TABLES);
//		String tableName = parameters.get(PARAM_TABLE_NAME);
//		if(numTables == null && tableName == null){
//			throw new IllegalArgumentException("Error: missing the required action");
//		}
//	}
//	
//	
//	void checkTableEdit(Map<String,String> parameters) throws IllegalArgumentException{
//		// Common checks
//		// taskid is not mandatory.
//		
//		if(parameters.get(PARAM_NUM_TABLES) == null){
//			throw new IllegalArgumentException("Error: number of tables not provided.");
//		}
//		//check all tables exists:
//		int numTables;
//		try{
//			numTables = Integer.parseInt(parameters.get(PARAM_NUM_TABLES));
//		}catch(NumberFormatException nfe){
//			throw new IllegalArgumentException("Invalid number of tables: " + parameters.get(PARAM_NUM_TABLES));
//		}
//		String tableIndex;
//		String numColsKey;
//		String numCols;
//		for(int i = 0; i < numTables; i++){
//			//ensure param TABLEn exists
//			tableIndex = PARAM_PREFIX_TABLE+i;
//			if(!parameters.containsKey(tableIndex)){
//				throw new IllegalArgumentException("Table parameter " + tableIndex + " not found.");
//			}
//			//ensure TABLEn_NUM_COLUMNS exists
//			numColsKey = tableIndex + PARAM_SUFFIX_NUM_COLS;
//			numCols = parameters.get(numColsKey);
//			if(numCols == null){
//				throw new IllegalArgumentException("Table number of columns parameter " + numColsKey + 
//						" (table: "+parameters.get(tableIndex)+"): not found.");
//			}
//			//ensure TABLEn_NUM_COLUMNS is a valid number
//			try{
//				Integer.parseInt(numCols);
//			}catch(NumberFormatException nfe){
//				throw new IllegalArgumentException("Invalid number of columns parameter " + numColsKey + 
//						" (table: "+parameters.get(tableIndex)+"): not a number: " + numCols);
//			}
//		}
//	}
	
//	void execute(Map<String,String> parameters, long taskId, int numTables, UwsJobOwner user) throws Exception {
//		String schemaName = TAPMetadata.getUserSchema(user);
//		
//		JDBCPooledFunctions dbConn = (JDBCPooledFunctions)service.getFactory().createDBConnection("TableToolConnection");
//
//		// Begin a DB transaction:
//		dbConn.startTransaction();
//
//		try{
//			Map<String, Boolean> allTables = new HashMap<String,Boolean>();
//			String paramTablePrefix;
//			String tableName;
//			int numColumnsToProcess;
//			boolean updated;
//			for(int i = 0; i < numTables; i++){
//				paramTablePrefix = PARAM_PREFIX_TABLE + i;
//				tableName = Utils.getTableNameOnly(parameters.get(paramTablePrefix));
//				String numCols = parameters.get(paramTablePrefix+PARAM_SUFFIX_NUM_COLS);
//				numColumnsToProcess = Integer.parseInt(numCols);
//				updated = executeTable(dbConn, parameters, taskId, numColumnsToProcess, schemaName, tableName, paramTablePrefix);
//				updateTaskStatus(i+1, numTables, taskId);
//				allTables.put(tableName, updated);
//			}
//			
//			// Commit modifications:
//			dbConn.endTransaction();
//			
//			boolean somethingUpdated = false;
//			// Vacuum
//			for(Entry<String, Boolean> e: allTables.entrySet()){
//				if(e.getValue()){
//					somethingUpdated = true;
//					dbConn.vacuumAnalyze(schemaName, e.getKey());
//				}
//			}
//			
//			if(somethingUpdated){
//				//generate event
//				service.getFactory().getEventsManager().setEventTime(user, TAPFactory.TABLE_UPDATED_EVENT);
//			}
//				
//		}catch(Exception e){
//			dbConn.cancelTransaction();	// ROLLBACK
//			throw e;
//		}finally{
//			try{
//				dbConn.close();
//			}catch(DBException ioe){
//			}
//		}
//	}
//	
//	/**
//	 * package-private for test-harnesses<br/>
//	 * Updates tap columns table.
//	 * @param parameters request parameters.
//	 * @param taskId task identifier (for progress information).
//	 * @param numColumnsToProcess number of columns to be processed in the current request (COLn, where n = number of columns).
//	 * @param paramTablePrefix table prefix
//	 * @return 'true' if the table has been updated.
//	 * @throws TAPException
//	 */
//	boolean executeTable(JDBCPooledFunctions dbConn, Map<String,String> parameters, long taskId, int numColumnsToProcess, String schemaName, String tableName, String paramTablePrefix) throws Exception {
//		TapTableInfo tapTableInfo = dbConn.fetchTapTableInfo(schemaName, tableName);
//		if(tapTableInfo == null){
//			throw new IllegalArgumentException("Table not found: " + schemaName + "." + tableName);
//		}
//		boolean updated = false;
//		IndexInfo indexInfo = new IndexInfo();
//		for(int i = 0; i < numColumnsToProcess; i++){
//			if(processField(i, dbConn, parameters, taskId, tapTableInfo, indexInfo, paramTablePrefix)){
//				updated = true;
//			}
//		}
//		
//		if(indexInfo.isNewIndexRequested()){
//			//A new ra/dec index creation will remove the previous ra/dec index if exists.
//			if(indexRaDec(dbConn, tapTableInfo, numColumnsToProcess, parameters, paramTablePrefix)){
//				updated = true;
//			}
//		}else{
//			//No new ra/dec index has been requested.
//			//Nevertheless, if an old ra/dec index removal was requested, it is necessary to remove them:
//			if(indexInfo.isOldIndexRemovalRequested()){
//				//old ra/dec index removal was requested.
//				if(removePrevRaDecIfExists(dbConn, tapTableInfo)){
//					updated = true;
//				}
//			}
//		}
//		return updated;
//	}
//	
//	
//	/**
//	 * Updates a tap column.<br/>
//	 * It checks whether the new values are different than the old ones.<br/>
//	 * It updates the table and creates indexes if required (old indexes are removed if required also).<br/>
//	 * In case that ra/dec index is requested, it is handled in a different procedure (returns 'true' in this case)<br/>
//	 * ra/dec index cannot be executed here because this method handles column by column. ra/dec index involves two columns.
//	 * @param index
//	 * @param dbConn database connection.
//	 * @param parameters request parameters.
//	 * @param taskId task identifier (for progress information).
//	 * @param tapTableInfo current database information.
//	 * @param indexInfo (output) it contains information about ra/dec indexes.
//	 * @param paramTablePrefix table prefix
//	 * @return 'true' if the field has been updated.
//	 * @throws DBException 
//	 */
//	private boolean processField(int index, JDBCPooledFunctions dbConn, Map<String,String> parameters, long taskId, TapTableInfo tapTableInfo, IndexInfo indexInfo, String paramTablePrefix) throws DBException{
//		String colIndex = paramTablePrefix + PARAM_SUFFIX_COLUMN + index;
//		String tableColumnName = parameters.get(colIndex);
//		String ucd = parameters.get(colIndex+PARAM_COL_SUFFIX_UCD);
//		String uType = parameters.get(colIndex+PARAM_COL_SUFFIX_UTYPE);
//		String sFlags = parameters.get(colIndex+PARAM_COL_SUFFIX_FLAGS);
//		String sIndexed = parameters.get(colIndex+PARAM_COL_SUFFIX_INDEXED);
//		
//		//check input parameters
//		if(tableColumnName == null){
//			throw new IllegalArgumentException("Cannot process column " + index + ": column name not found");
//		}
//		if(ucd == null){
//			throw new IllegalArgumentException("Cannot process column " + tableColumnName + ": ucd not found");
//		}
//		if(uType == null){
//			throw new IllegalArgumentException("Cannot process column " + tableColumnName + ": utype not found");
//		}
//		if(sFlags == null){
//			throw new IllegalArgumentException("Cannot process column " + tableColumnName + ": flags not found");
//		}
//		if(sIndexed == null){
//			throw new IllegalArgumentException("Cannot process column " + tableColumnName + ": indexed not found");
//		}
//		
//		//String flag is translated into integer
//		int flags = Utils.convertTapTableFlag(sFlags);
//		boolean bIndexedRequested = Utils.isTrueFromTapTableIndexed(sIndexed);
//		int iIndexRequested = bIndexedRequested ? 1:0;
//		String schemaName = tapTableInfo.getSchemaName();
//		String tableName = tapTableInfo.getTableName();
//		
//		//Test whether we must update the table.
//		
//		boolean updated = false;
//
//		//If any change in ucd, utype, flags or index is found, an update is required
//		if(Utils.requireUpdate(tapTableInfo, tableColumnName, ucd, uType, flags, iIndexRequested)){
//			dbConn.updateUserTableData(schemaName, tableName, tableColumnName, ucd, uType, flags, iIndexRequested);
//			updated = true;
//		}
//
//		//Index:
//		//In case the change is in index, if the index is removed, a remove request must be propagated to database.
//		//Ra/Dec is an special case.
//
//		if(bIndexedRequested){
//			//want to create an index
//			if(Utils.isRaOrDec(flags)){
//				//special case: do not create index here
//				indexInfo.setCreateNewIndex(true);
//			} else {
//				//Normal new index
//				//It can be already indexed: normal and/or RaDec
//				//if old index is not normal (i.e. Ra/Dec), the new normal index can be created (if not already created)
//				if(!Utils.isNormalIndexed(tapTableInfo, tableColumnName)){
//					//Remove index if exists
//					dbConn.removeTableColumnIndex(schemaName, tableName, tableColumnName);
//					//Create index
//					dbConn.createTableColumnIndex(schemaName, tableName, tableColumnName);
//					updated = true;
//				}
//				//new normal index was request. old ra/dec index is not requested
//				//If it was ra/dec indexed, remove
//				if(Utils.isRaDecIndexed(tapTableInfo, tableColumnName)){
//					indexInfo.setRemoveOldIndex(true);
//				}
//			}
//		} else {
//			//want to remove index
//			//If the column has a normal index => remove it
//			if(Utils.isNormalIndexed(tapTableInfo, tableColumnName)){
//				dbConn.removeTableColumnIndex(schemaName, tableName, tableColumnName);
//				updated = true;
//			}
//			//If the column has a RaDec index => mark for further removal
//			if(Utils.isRaDecIndexed(tapTableInfo, tableColumnName)){
//				indexInfo.setRemoveOldIndex(true);
//			}
//		}
//		
//		return updated;
//	}
//	
//	
//	
//	/**
//	 * Creates a ra/dec index.<br/>
//	 * Checks whether ra/dec are already indexed.<br/>
//	 * The previous ra/dec index is removed (if it is found).<br/>
//	 * It is expected that parameteres contains a 'flags' key that provides a single value: either 'ra' or 'dec'.
//	 * If parameters does not contain columns for 'ra' and/or 'dec', an exception is raised.<br/>
//	 * @param dbConn database connection.
//	 * @param tapTableInfo current database information.
//	 * @param numColumnsToProcess number of columns to be processed in the current request (COLn, where n = number of columns).
//	 * @param parameters request parameters.
//	 * @param paramTablePrefix table prefix
//	 * @return 'true' if the field has been updated.
//	 * @throws DBException
//	 */
//	boolean indexRaDec(JDBCPooledFunctions dbConn, TapTableInfo tapTableInfo, int numColumnsToProcess, Map<String,String> parameters, String paramTablePrefix) throws DBException{
//		//Locate Ra
//		//Locate Dec
//		String raColumn = null;
//		String decColumn = null;
//		String colIndex;
//		String flags;
//		for(int i = 0; i < numColumnsToProcess; i++){
//			colIndex = paramTablePrefix + PARAM_SUFFIX_COLUMN + i;
//			flags = parameters.get(colIndex + PARAM_COL_SUFFIX_FLAGS);
//			if(flags.equalsIgnoreCase(Utils.TAP_COLUMN_TABLE_FLAG_ID_RA)){
//				raColumn = parameters.get(colIndex);
//				continue;
//			}
//			if(flags.equalsIgnoreCase(Utils.TAP_COLUMN_TABLE_FLAG_ID_DEC)){
//				decColumn = parameters.get(colIndex);
//				continue;
//			}
//		}
//		
//		if(raColumn == null){
//			throw new IllegalArgumentException("Ra column not found");
//		}
//		if(decColumn == null){
//			throw new IllegalArgumentException("Dec column not found");
//		}
//
//		//If it is already indexed, do not index again.
//		//Remove old Ra/Dec if they are not the same columns.
//		boolean alreadyIndexed = Utils.areAlreadyIndexedRaDec(tapTableInfo, raColumn, decColumn);
//		if(alreadyIndexed){
//			//Nothing to do, ra/dec are already indexed on the same columns.
//			return false;
//		}
//		
//		//Not the same columns.
//		//Remove previous ra/dec if they exists
//		removePrevRaDecIfExists(dbConn, tapTableInfo);
//		
//		//Create new indexes
//		dbConn.createRaAndDecIndexes(tapTableInfo.getSchemaName(), tapTableInfo.getTableName(), raColumn, decColumn, Utils.TAP_TABLE_TYPE_RADEC); 
//		return true;
//	}
//	
//	/**
//	 * Removes previous ra/dec index, if it is found.
//	 * @param dbConn database connection
//	 * @param tapTableInfo current database information.
//	 * @return 'true' if the field has been updated.
//	 * @throws DBException
//	 */
//	private boolean removePrevRaDecIfExists(JDBCPooledFunctions dbConn, TapTableInfo tapTableInfo) throws DBException{
//		String raColumn = null;
//		String decColumn = null;
//		int flags = 0;
//		for(String tableColumnName: tapTableInfo.getTableColumnNames()){
//			//tapTableInfo contains database current info: flags is an integer in database (it can be null)
//			flags = Utils.getFlagsFromTapTable(tapTableInfo, tableColumnName);
//			if((flags & Utils.TAP_COLUMN_TABLE_FLAG_RA) > 0){
//				raColumn = tableColumnName;
//				continue;
//			}
//			if((flags & Utils.TAP_COLUMN_TABLE_FLAG_DEC) > 0){
//				decColumn = tableColumnName;
//				continue;
//			}
//		}
//		if(raColumn == null || decColumn == null){
//			//wrong ra/dec specification, no index created.
//			return false;
//		}
//		
//		//we have the previous ra/dec indexed columns, remove them
//		dbConn.removeRaAndDecIndexes(tapTableInfo.getSchemaName(), tapTableInfo.getTableName(), raColumn, decColumn, Utils.TAP_TABLE_TYPE_RADEC);
//		return true;
//	}
//	
//	/**
//	 * package-private for test-harnesses<br/>
//	 * Updates status manager
//	 * @param currentTableIndex
//	 * @param numTables
//	 * @param taskid
//	 */
//	void updateTaskStatus(int currentTableIndex, int numTables, long taskid){
//		if(taskid < 0){
//			//to task id to update
//			return;
//		}
//		int percentDone = (int) Math.round(100.00 * currentTableIndex / numTables);
//		StatusTableEdit status = new StatusTableEdit(""+percentDone);
//		try{
//			StatusManager.getInstance().updateStatus(taskid, status); 
//		} catch (IllegalArgumentException iae){
//			iae.printStackTrace();
//		}
//	}
	
}
