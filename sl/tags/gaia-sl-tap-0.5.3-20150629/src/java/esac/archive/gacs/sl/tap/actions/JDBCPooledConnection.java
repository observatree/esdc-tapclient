package esac.archive.gacs.sl.tap.actions;

/*
 * This file is part of TAPLibrary.
 * 
 * TAPLibrary is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * TAPLibrary is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with TAPLibrary.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2012 - UDS/Centre de Données astronomiques de Strasbourg (CDS)
 */

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import esac.archive.gacs.sl.services.publicgroup.PublicGroupItem;
//import esac.archive.gacs.sl.services.admin.JobsFilter.Comparison;
import esac.archive.gacs.sl.services.status.StatusManager;
import esac.archive.gacs.sl.services.status.types.StatusIngestion;
import esac.archive.gacs.sl.services.util.Utils;
import esac.archive.gacs.sl.tap.TapUtils;
import esavo.adql.query.ADQLQuery;
import esavo.tap.TAPException;
import esavo.tap.TAPSchemaInfo;
import esavo.tap.TAPService;
import esavo.tap.db.DBException;
import esavo.tap.metadata.TAPColumn;
import esavo.tap.metadata.TAPMetadataLoader;
import esavo.tap.metadata.TAPTable;
import esavo.tap.metadata.TAPTypes;
import esavo.tap.metadata.VotType;
import esavo.uws.UwsException;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.storage.QuotaException;
import esavo.uws.storage.UwsQuota;
import esavo.uws.storage.UwsQuotaSingleton;
import esavo.uws.utils.UwsUtils;



/**
 * 
 * @author jgonzale
 * @author rgutierrez
 * 
 */
public class JDBCPooledConnection implements JDBCPooledFunctions {
	
	public static final String SET_TIME_OUT_QUERY = "SET statement_timeout TO 1800000";
	public static final int RESULTS_FETCH_SIZE = 8192;
	
	private Connection dbConn = null;
	
	private Statement stmt = null;
	
	private PreparedStatement insertIntoTapSchemaSchemas = null;
	private PreparedStatement insertIntoTapSchemaTables = null;
	private PreparedStatement insertIntoTapSchemaColumns = null;
	private PreparedStatement deleteFromTapSchemaTables = null;
	private PreparedStatement deleteFromTapSchemaColumns = null;

	private TAPSchemaInfo tapSchemaInfo;

	public JDBCPooledConnection(TAPService service) throws TAPException {
		//this.appservice.getApplicationIdentifier(), service.getTapSchemaInfo()
		this.tapSchemaInfo = service.getTapSchemaInfo();
		
		String tapSchemaName = tapSchemaInfo.getTapSchemaName();
		String tapSchemasTableName = tapSchemaInfo.getTapSchemasTableName();
		String tapTablesTableName = tapSchemaInfo.getTapTablesTableName();
		String tapColumnsTableName = tapSchemaInfo.getTapColumnsTableName();

		try {

			this.dbConn = JDBCPoolSingleton.getInstance(service).getConnection();
			
			dbConn.createStatement().execute(SET_TIME_OUT_QUERY);
			
			insertIntoTapSchemaSchemas 	= dbConn.prepareStatement("INSERT INTO "+tapSchemaName+"."+tapSchemasTableName+" (schema_name, description, utype) values (?,?,?)");
			insertIntoTapSchemaTables 	= dbConn.prepareStatement("INSERT INTO "+tapSchemaName+"."+tapTablesTableName+" (schema_name, table_name, table_type, description, utype, size, flags) values (?,?,?,?,?,?,?)");
			insertIntoTapSchemaColumns 	= dbConn.prepareStatement("INSERT INTO "+tapSchemaName+"."+tapColumnsTableName+" (schema_name, table_name, column_name, description, unit, ucd, utype, datatype, size, principal, indexed, std, flags) values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
			deleteFromTapSchemaTables 	= dbConn.prepareStatement("DELETE FROM "+tapSchemaName+"."+tapTablesTableName+" WHERE schema_name=? AND table_name=?");
			deleteFromTapSchemaColumns 	= dbConn.prepareStatement("DELETE FROM "+tapSchemaName+"."+tapColumnsTableName+" WHERE schema_name=? AND table_name=?");
		} catch (SQLException se) {
			throw new TAPException(
					"Impossible to establish a connection to the database "+se.getMessage(), se);
		}
	}

	@Override
	public void startTransaction() throws DBException {
		try {
			//dbConn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			dbConn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new DBException("Impossible to start transaction, because: "
					+ e.getMessage());
		}
	}

	@Override
	public void cancelTransaction() throws DBException {
				
		try {
			if(stmt != null){
				stmt.cancel();
			}
			dbConn.rollback();
		} catch (SQLException e) {
			throw new DBException("Impossible to cancel transaction, because: "
					+ e.getMessage());
		}finally{
			try {
				dbConn.setAutoCommit(true);
			} catch (SQLException e) {
				throw new DBException("Impossible to reset autocommit to true, because: "
						+ e.getMessage());
			}
		}
	}

	@Override
	public void endTransaction() throws DBException {
		try {
			dbConn.commit();
		} catch (SQLException e) {
			throw new DBException("Impossible to commit transaction, because: "
					+ e.getMessage());
		}finally{
			try {
				dbConn.setAutoCommit(true);
			} catch (SQLException e) {
				throw new DBException("Impossible to reset autocommit to true, because: "
						+ e.getMessage());
			}
		}
	}

	@Override
	public void close() throws DBException {
		try {
			//System.out.println("JDBCPooledConnection: Closing connection.");
			dbConn.close();
		} catch (SQLException e) {
			throw new DBException(
					"Impossible to close DB connection, because: "
							+ e.getMessage());
		}
	}

	@Override
	public ResultSet executeQuery(String sql, ADQLQuery query)
			throws DBException {
		try {
			stmt = dbConn.createStatement();
			if (!dbConn.getAutoCommit()) {
				stmt.setFetchSize(RESULTS_FETCH_SIZE);
			}
			return stmt.executeQuery(sql);
		} catch (SQLException se) {
			se.printStackTrace();
			throw new DBException("Can not execute the following SQL: \n" + sql
					+ "\n. Because: " + se.getMessage());
		}
	}

	/* ************************************** */
	/* METHODS USED ONLY IF UPLOAD IS ENABLED */
	/* ************************************** */

	@Override
	public void createSchema(String arg0) throws DBException {
		/// Test if schema exists
		try {
			ResultSet result = dbConn.createStatement().executeQuery("SELECT schema_name FROM information_schema.schemata WHERE schema_name = '"+arg0+"'");
			// If schema already exists, do nothing.
			if(result.next()) return;
		} catch (SQLException se) {
			throw new DBException("Can not execute the following SQL: \n" + "SELECT schema_name FROM information_schema.schemata WHERE schema_name = '"+arg0+"' "
					+ "\n. Because: " + se.getMessage());
		}

		try {
			dbConn.createStatement().execute("CREATE SCHEMA "+arg0);
		} catch (SQLException se) {
			throw new DBException("Can not execute the following SQL: \n" + "CREATE SCHEMA "+arg0
					+ "\n. Because: " + se.getMessage());
		}
		// TODO ONLY IF upload is enabled !
	}

	/**
	 * Add a table to tap_schema. Create entries in tap_schema.schema, tap_schema.tables and tap_schema.columns.
	 * @param tapTable
	 * @throws DBException
	 */
	@Override
	public void registerInTapSchema(TAPTable tapTable) throws DBException {
		/// Test if schema exists
		try {
			// If schema is not registered, register it.
			ResultSet result = dbConn.createStatement().executeQuery(
					"SELECT schema_name FROM " + tapSchemaInfo.getTapSchemaName() + "." + tapSchemaInfo.getTapSchemasTableName() +
					" WHERE schema_name = '" + tapTable.getSchema().getADQLName() + "'");
			if(!result.next()){
				insertIntoTapSchemaSchemas.setString(1, tapTable.getSchema().getADQLName());
				
				if(tapTable.getSchema().getDescription()!=null)
					insertIntoTapSchemaSchemas.setString(2, tapTable.getSchema().getDescription());
				else
					insertIntoTapSchemaSchemas.setNull(2, java.sql.Types.VARCHAR);
				
				if(tapTable.getSchema().getUtype()!=null)
					insertIntoTapSchemaSchemas.setString(3, tapTable.getSchema().getUtype());
				else
					insertIntoTapSchemaSchemas.setNull(3, java.sql.Types.VARCHAR);
				
				insertIntoTapSchemaSchemas.executeUpdate();
			}
			
			// Unregister table from tap_schema
			// Delete table information from tap_schema
			unregisterFromTapSchema(tapTable);
			
			// Insert table in tap_schema
			insertIntoTapSchemaTables.setObject(1, tapTable.getSchema().getADQLName(), java.sql.Types.VARCHAR);
			insertIntoTapSchemaTables.setObject(2, tapTable.getADQLName(), java.sql.Types.VARCHAR);
			insertIntoTapSchemaTables.setObject(3, tapTable.getType(), java.sql.Types.VARCHAR);
			insertIntoTapSchemaTables.setObject(4, tapTable.getDescription(), java.sql.Types.VARCHAR);
			insertIntoTapSchemaTables.setObject(5, tapTable.getUtype(), java.sql.Types.VARCHAR);
			insertIntoTapSchemaTables.setObject(6, 0, java.sql.Types.INTEGER);
			insertIntoTapSchemaTables.setObject(7, tapTable.getFlags(), java.sql.Types.INTEGER);
			insertIntoTapSchemaTables.execute();

			// Insert columns information in tap_schema
			
			// Add OID column
			TAPColumn oidColumn = new TAPColumn(tapTable.getName()+"_oid", "Object Identifier", "", "", "");
			oidColumn.setDatatype(TAPTypes.INTEGER,TAPTypes.NO_SIZE);
			oidColumn.setIndexed(true);
			
			// Include OID column in original columns list
//			Iterator<TAPColumn> columnsInOrig = tapTable.getColumns();
//			@SuppressWarnings("unchecked")
//			List<TAPColumn> columns = IteratorUtils.toList(columnsInOrig);
//			columns.add(oidColumn);
//			Iterator<TAPColumn> columnsIt = columns.iterator();
			List<TAPColumn> columns = tapTable.getColumnsList();
			columns.add(oidColumn);
			Iterator<TAPColumn> columnsIt = columns.iterator();
			
			while(columnsIt.hasNext()){
				TAPColumn col = columnsIt.next();
				insertIntoTapSchemaColumns.setObject(1, tapTable.getSchema().getADQLName(), java.sql.Types.VARCHAR);
				insertIntoTapSchemaColumns.setObject(2, tapTable.getADQLName(), java.sql.Types.VARCHAR);
				insertIntoTapSchemaColumns.setObject(3, col.getADQLName(), java.sql.Types.VARCHAR);
				insertIntoTapSchemaColumns.setObject(4, col.getDescription(), java.sql.Types.VARCHAR);
				insertIntoTapSchemaColumns.setObject(5, col.getUnit(), java.sql.Types.VARCHAR);
				insertIntoTapSchemaColumns.setObject(6, col.getUcd(), java.sql.Types.VARCHAR);
				insertIntoTapSchemaColumns.setObject(7, col.getUtype(), java.sql.Types.VARCHAR);
				insertIntoTapSchemaColumns.setObject(8, col.getDatatype(), java.sql.Types.VARCHAR);
				Integer arraySize = TAPTypes.getColumnArraySize(col.getDatatype(), col.getArraySize());
				//insertIntoTapSchemaColumns.setObject(9, col.getArraySize(), java.sql.Types.INTEGER);
				insertIntoTapSchemaColumns.setObject(9, arraySize, java.sql.Types.INTEGER);
				
				Integer aux = (col.isPrincipal())?new Integer(1):new Integer(0);
				insertIntoTapSchemaColumns.setObject(10, aux, java.sql.Types.INTEGER);

				aux = (col.isIndexed())?new Integer(1):new Integer(0);
				insertIntoTapSchemaColumns.setObject(11, aux, java.sql.Types.INTEGER);
				
				aux = (col.isStd())?new Integer(1):new Integer(0);
				insertIntoTapSchemaColumns.setObject(12, aux, java.sql.Types.INTEGER);
				
				insertIntoTapSchemaColumns.setObject(13, col.getFlags(), java.sql.Types.INTEGER);
				
				insertIntoTapSchemaColumns.execute();
			}
			
			
			
		} catch (SQLException se) {
			throw new DBException("Can not register table in tap_schema: \n" 
					+ "\n. Because: " + se.getMessage());
		}

	}
	

	/**
	 * Delete a table from tap_schema. Delete entries from tap_schema.schema, tap_schema.tables and tap_schema.columns.
	 * @param tapTable
	 * @throws DBException
	 */
	@Override
	public void unregisterFromTapSchema(TAPTable tapTable) throws DBException {
		/// Test if schema exists
		try {
			// Delete table information from tap_schema 
			deleteFromTapSchemaColumns.setString(1, tapTable.getSchema().getDBName());
			deleteFromTapSchemaColumns.setString(2, tapTable.getDBName());

			deleteFromTapSchemaTables.setString(1, tapTable.getSchema().getDBName());
			deleteFromTapSchemaTables.setString(2, tapTable.getDBName());
			
			deleteFromTapSchemaColumns.executeUpdate();
			deleteFromTapSchemaTables.executeUpdate();
			
		} catch (SQLException se) {
			throw new DBException("Can not register table in tap_schema: \n" 
					+ "\n. Because: " + se.getMessage());
		}

	}

	@Override
	public void dropSchema(String arg0) throws DBException {
		try {
			dbConn.createStatement().execute("DROP SCHEMA IF EXISTS "+arg0+" CASCADE");
		} catch (SQLException se) {
			throw new DBException("Can not execute the following SQL: \n" + "DROP SCHEMA "+arg0
					+ "\n. Because: " + se.getMessage());
		}
	}

	@Override
	public void createTable(TAPTable table) throws DBException {
		
		// TODO ONLY IF upload is enabled !
				
		String sql = "CREATE TABLE "+table.getDBSchemaName()+"."+table.getDBName()+" ( ";
		String join = "";
		
		Iterator<TAPColumn> columns = table.getColumns();
		while(columns.hasNext()){
			TAPColumn col = (TAPColumn)columns.next();
			sql+= join+col.getDBName()+" ";
//			if(TAPTypes.checkVarBinaryRequired(col.getDatatype(), col.getArraySize())){
//				//TODO function to obtain the array data type required
//				sql += "bytea";
//			}else{
//				sql += TAPTypes.getDBType(col.getDatatype());
//			}
			sql += TapUtils.getDbType(col);
			join=", ";
		}
		
		//Add OID column
		sql+=join+" "+table.getDBName()+"_oid SERIAL";
		sql+=" ) ";
		
		try {
			dbConn.createStatement().execute(sql);
		} catch (SQLException se) {
			throw new DBException("Can not execute the following SQL: \n" + sql
					+ "\n. Because: " + se.getMessage());
		}
	}
	
	
	/**
	 * Obtains the database size used by the given owner (in bytes)
	 * @param ownerid
	 * @return
	 * @throws SQLException
	 */
	@Override
	public long getDbSize(String ownerid) throws DBException {
		
		try{
			PreparedStatement statement = dbConn.prepareStatement("SELECT tap_schema.db_user_usage(?)");
			statement.setString(1, ownerid);
			ResultSet rs = statement.executeQuery();
	
			if(rs.next()){
				long value = rs.getLong("db_user_usage");
				statement.close();
				return value;
			}else{
				statement.close();
				throw new DBException("Owner id '"+ownerid+"' not found");
			}
		}catch(SQLException e){
			throw new DBException(e);
		}

	}
	
	/**
	 * Obtains the database size used by the given table (in bytes)
	 * @param ownerid
	 * @return
	 * @throws SQLException
	 */
	@Override
	public long getTableSize(String schema, String table) throws DBException {
		
		try{
			PreparedStatement statement = dbConn.prepareStatement("SELECT tap_schema.db_table_usage(?,?)");
			statement.setString(1, schema);
			statement.setString(2, table);
			ResultSet rs = statement.executeQuery();
	
			if(rs.next()){
				long value = rs.getLong("db_table_usage");
				statement.close();
				return value;
			}else{
				statement.close();
				throw new DBException("Table '"+schema+"."+table+"' not found");
			}
		}catch(SQLException e){
			throw new DBException(e);
		}

	}
	
	@Override
	public void dropTable(TAPTable table) throws DBException {
		dropTable(table, false);
	}


	@Override
	public void dropTable(TAPTable table, boolean forceRemoval) throws DBException {
		String sql = "DROP TABLE "+table.getDBSchemaName()+"."+table.getDBName();
		if(forceRemoval){
			sql += " CASCADE";
		}
		try {
			dbConn.createStatement().execute(sql);
		} catch (SQLException se) {
			throw new DBException("Can not execute the following SQL: \n" + sql
					+ "\n. Because: " + se.getMessage());
		}
	}

	@Override
	public String getID() {
		return null;
	}

	@Override
	public int loadTableData(UwsJobOwner owner, TAPTable table, StarTable starTable) throws DBException, UwsException {
		return loadTableData(owner, table, starTable, -1);
	}

	@Override
	public int loadTableData(UwsJobOwner owner, TAPTable table, StarTable starTable, long taskid) throws DBException, UwsException {
		UwsQuotaSingleton quotaSingleton = UwsQuotaSingleton.getInstance(); 
		UwsQuota quota = quotaSingleton.createOrLoadQuota(owner);
		
		VotType type;
		int sqlType;
		Iterator<TAPColumn> columns;
		
		int totalRows = (int)starTable.getRowCount();
		int nbRows = 0;
		int percent = 0;
		
		String sql = "INSERT INTO "+table.getDBSchemaName()+"."+table.getDBName()+" VALUES ( ";
		
		columns = table.getColumns();
		boolean firstTime = true;
		TAPColumn c;
		while(columns.hasNext()){
			//type = columns.next().getVotType();
			c = columns.next();
			//sqlType = TAPTypes.getSQLType(type);
			sqlType = TAPTypes.getEffectiveSQLType(c);
			if(firstTime){
				firstTime = false;
			}else{
				sql += ", ";
			}
			sql += getSuitablePreparedStatementArg(sqlType);
		}
		
//		String join = "";
//		for(int i=0; i<table.getNbColumns(); i++){
//			sql+= join+" ? ";
//			join=" ,";
//		}
		sql+=" ) ";

		PreparedStatement insertRowStatement=null;
		try {
			insertRowStatement = dbConn.prepareStatement(sql);
		} catch (SQLException se) {
			throw new DBException("Can not execute the following SQL: \n" + sql
					+ "\n. Because: " + se.getMessage());
		}
		
		
		
		RowSequence rSeq;
		String tableDbName = table.getDBName();
		String schemaName = table.getDBSchemaName().toLowerCase();
		try {
			rSeq = starTable.getRowSequence();
			
			long prevTableSize = 0;
			//Object oTmp;
			while ( rSeq.next() ) {
				Object[] row = rSeq.getRow();
				insertRowStatement.clearParameters();
				
				columns = table.getColumns();
				int i=0;
				while(columns.hasNext()){
					c = columns.next();
					//type = columns.next().getVotType();
					
					//sqlType = TAPTypes.getSQLType(type);
					//sqlType = TAPTypes.getEffectiveSQLType(type);
					sqlType = TAPTypes.getEffectiveSQLType(c);
					
//					if(row[i]!=null){
//						insertSuitableValue(i, row[i], sqlType, insertRowStatement);
//						oTmp = getSuitableUploadObject(row[i], sqlType);
//						insertRowStatement.setObject(i+1, oTmp, sqlType);
//					}else{
//						insertRowStatement.setNull(i+1, sqlType);
//					}
					insertSuitableValue(i, row[i], sqlType, insertRowStatement);
					i++;
				}
				insertRowStatement.addBatch();
				
				if(nbRows%5000==0){
					insertRowStatement.executeBatch();
					insertRowStatement.clearBatch();
					
					// Check DB quota
					long newTableSize = getTableSize(schemaName, tableDbName);
					long deltaTableSize = newTableSize-prevTableSize;
					quota.addDbSize(deltaTableSize);
					prevTableSize = newTableSize;
					
					
					percent = (int)100*nbRows/totalRows;
					//System.out.println("Upload: "+percent+"% ingested (task="+taskid+")");
					
					StatusIngestion statusIngestion = new StatusIngestion(""+percent);
					if(taskid >= 0){
						try{
							StatusManager.getInstance().updateStatus(taskid, statusIngestion); 
						} catch (IllegalArgumentException iae){
							iae.printStackTrace();
							//throw new IOException("Error updating status: " + iae.getMessage(), iae);
						}
					}

				}
				nbRows++;
	        }
			insertRowStatement.executeBatch();
			insertRowStatement.clearBatch();
			
			// Check DB quota
			long newTableSize = getTableSize(schemaName, tableDbName);
			long deltaTableSize = newTableSize-prevTableSize;
			
			quota.addDbSize(deltaTableSize);
			
			prevTableSize = newTableSize;

			insertRowStatement.close();

			return nbRows;
		} catch (SQLException e) {
			restoreDbSize(quota, schemaName, tableDbName, e);
			throw new DBException("Error creating user table: "+e.getMessage(), e);
		} catch (QuotaException e) {
			restoreDbSize(quota, schemaName, tableDbName, e);
			throw new UwsException(e.getMessage(), e);
		} catch (IOException e) {
			restoreDbSize(quota, schemaName, tableDbName, e);
			throw new DBException("Error creating user table: "+e.getMessage(), e);
		}
	}
	
	private void restoreDbSize(UwsQuota quota, String schemaName, String tableDbName, Exception e) throws UwsException {
		long newTableSize = 0;
		try {
			newTableSize = getTableSize(schemaName, tableDbName);
		} catch (DBException ex) {
			throw new UwsException(e.getMessage() + "\nWARNING: Cannot restore db quota: " + ex.getMessage(), e);
		}
		quota.reduceDbSize(newTableSize);
	}
	
	private String getSuitablePreparedStatementArg(int sqlType){
		if(sqlType == java.sql.Types.TIMESTAMP){
			return "CAST(? AS timestamp)";
		}else{
			return "?";
		}
	}

	private void insertSuitableValue(int index, Object value, int sqlType, PreparedStatement st) throws SQLException{
		if(value == null){
			st.setNull(index+1, sqlType);
			return;
		}
		if(sqlType == java.sql.Types.TIMESTAMP){
			//Object o = getTimeStamp((String)in); //problem with dates before 1970
			//st.setObject(index+1, o, sqlType);   //problem with dates before 1970
			//Use char instead.
			st.setString(index+1, value.toString());
			//st.setObject(index+1, value.toString(), java.sql.Types.CHAR);
		}else if(sqlType == java.sql.Types.VARBINARY){
			//STIL returns an array of short's when they contains bytes actually...
			byte[] b = getBytesFromObject(value);
			st.setBytes(index+1, b);
		}else{
			st.setObject(index+1, value, sqlType);
		}
	}
	
	/**
	 * PATCH: for working with STIL with arrays. 
	 * STILT always returns a short[] array when (datatype='unsignedByte' arraysize='*') is found
	 * @param obj
	 * @return
	 * @throws SQLException
	 */
	private byte[] getBytesFromObject(Object obj) throws SQLException{
		if(obj instanceof short[]){
			short[] sArray = (short[])obj;
			byte[] bArray = new byte[sArray.length];
			short s;
			for(int i = 0; i < bArray.length; i++){
				s = sArray[i];
				bArray[i] = (byte)s;
			}
			return bArray;
		}else{
			throw new SQLException("Invalid value class. Expected short[], found: " + obj.getClass().getName());
		}
	}

	
//	public static Object getSuitableUploadObject(Object in, int sqlType){
//		if(sqlType == java.sql.Types.TIMESTAMP){
//			//it is a String, transform into long/timestamp
//			return "'"+in.toString()+"'";
////			try {
////				return getTimeStamp((String)in);
////			} catch (ParseException e) {
////				e.printStackTrace();
////				return null;
////			}
//		}else{
//			return in;
//		}
//	}
	
	
	@Override
	public String createPkInTable(TAPTable table) throws DBException {
		String key = table.getDBName()+"_oid";
		String query1 = "ALTER TABLE "+table.getDBSchemaName()+"."+table.getDBName()+
		" ADD CONSTRAINT "+table.getDBName()+"_pk PRIMARY KEY ("+key+") ";
		String query2 = "UPDATE tap_schema.all_columns SET flags = " + Utils.TAP_COLUMN_TABLE_FLAG_PK + 
				" WHERE schema_name = '"+table.getDBSchemaName()+"' AND " +
				" table_name = '"+table.getDBName()+"' AND column_name = '"+key+"'";
		List<String> queries = new ArrayList<String>();
		queries.add(query1);
		queries.add(query2);
		for(String q: queries){
			try {
				dbConn.createStatement().execute(q);
			} catch (SQLException se) {
				throw new DBException("Can not execute the following SQL: \n" + q
					+ "\n. Because: " + se.getMessage());
			}
		}
		return key;
	}


	@Override
	public void createRaAndDecIndexes(String schemaName, String tableName, String raCol, String decCol, int raDecFlag) throws DBException {
		String fullName = schemaName + "." + tableName;
		
		List<String> queries = new ArrayList<String>();
		
		String indexName = getRaDecIndexName(tableName);
		queries.add("CREATE INDEX " +  indexName + " ON "+ fullName + " USING btree (q3c_ang2ipix("+raCol+", "+decCol+"));");
		queries.add("ALTER TABLE " + fullName +	" ALTER COLUMN " + raCol + " SET NOT NULL");
		queries.add("ALTER TABLE " + fullName + " ALTER COLUMN " + decCol + " SET NOT NULL");		
		queries.add("ALTER TABLE " + fullName + " ADD CONSTRAINT "+tableName+"_ra_check_nan CHECK ( NOT "+raCol+" = 'NaN')");
		queries.add("ALTER TABLE " + fullName + " ADD CONSTRAINT "+tableName+"_dec_check_nan CHECK ( NOT "+decCol+" = 'NaN')");
		queries.add("UPDATE tap_schema.all_columns SET indexed = 1 WHERE "+
				"schema_name = '"+schemaName+"' AND table_name = '"+tableName+
				"' AND (column_name = '"+raCol+"' OR column_name = '"+decCol+"')");
		queries.add("UPDATE tap_schema.all_tables SET flags = flags | " + raDecFlag + " WHERE "+
				"schema_name = '"+schemaName+"' AND table_name = '"+tableName+"'");

		for(String q:queries){
			try {
				dbConn.createStatement().execute(q);
			} catch (SQLException se) {
				throw new DBException("Can not execute the following SQL: \n" + q
						+ "\n. Because: " + se.getMessage());
			}
		}
	}
	
	@Override
	public void removeRaAndDecIndexes(String schemaName, String tableName, String raCol, String decCol, int raDecFlag) throws DBException {
		String fullName = schemaName + "." + tableName;
		
		List<String> queries = new ArrayList<String>();
		
		String indexName = getRaDecIndexName(tableName);
	    queries.add("DROP INDEX IF EXISTS "+schemaName+"."+indexName); 
        queries.add("ALTER TABLE "+fullName+" DROP CONSTRAINT "+tableName+"_ra_check_nan"); 
        queries.add("ALTER TABLE "+fullName+" DROP CONSTRAINT "+tableName+"_dec_check_nan"); 
        queries.add("ALTER TABLE "+fullName+" ALTER COLUMN "+raCol+" DROP NOT NULL");
        queries.add("ALTER TABLE "+fullName+" ALTER COLUMN "+decCol+" DROP NOT NULL");
		
        queries.add("UPDATE tap_schema.all_tables SET flags = flags & (~" + raDecFlag + ") WHERE "+
				"schema_name = '"+schemaName+"' AND table_name = '"+tableName+"'");

		for(String q:queries){
			try {
				dbConn.createStatement().execute(q);
			} catch (SQLException se) {
				throw new DBException("Can not execute the following SQL: \n" + q
						+ "\n. Because: " + se.getMessage());
			}
		}
	}
	


	@Override
	public void updateTableSizeInTapSchema(TAPTable table) throws DBException {
		
		String sqlUpdate = "UPDATE "+tapSchemaInfo.getTapSchemaName()+"."+tapSchemaInfo.getTapTablesTableName()+" "+
		" SET size = ( SELECT count(*) FROM "+table.getDBSchemaName()+"."+table.getDBName()+
		") WHERE table_name ILIKE '"+table.getDBName()+"' AND schema_name ILIKE '"+table.getDBSchemaName()+"'";
		
		
		try {
			dbConn.createStatement().execute(sqlUpdate);
		} catch (SQLException se) {
			throw new DBException("Can not execute the following SQL: \n" + sqlUpdate
					+ "\n. Because: " + se.getMessage());
		}
	
	}
	
	
	@Override
	public void vacuumAnalyze(String schemaName, String tableName) throws DBException {
		
		String sqlVacuum = "VACUUM ANALYZE "+ schemaName+"."+tableName;

		try {
			dbConn.createStatement().execute(sqlVacuum);
		} catch (SQLException se) {
			throw new DBException("Can not execute the following SQL: \n" + sqlVacuum
					+ "\n. Because: " + se.getMessage());
		}
	}
	
//	@Override
//	public List<String> getOldJobs(String appid, String ownerid, long olderThan) throws DBException {
//		String query = "SELECT job_id FROM uws_schema.jobs WHERE owner_id = '"+ownerid+
//				"' AND start_time < " + olderThan + " AND job_id LIKE '%"+appid+"'";
//		List<String> jobs = new ArrayList<String>();
//		ResultSet result;
//		try {
//			result = dbConn.createStatement().executeQuery(query);
//			while (result.next()) {
//				jobs.add(result.getString(1));
//			}
//		} catch (SQLException e) {
//			throw new DBException("Cannot obtain old jobs for user " + ownerid, e);
//		}
//
//		return jobs;
//	}

	@Override
	public void createTableColumnIndex(String schemaName, String tableName, String tableColumnName) throws DBException {
		String fullName = schemaName + "." + tableName;
		String indexName = getColIndexName(tableName, tableColumnName);
		String query = "CREATE INDEX " + indexName + " ON "+ fullName + " ("+tableColumnName+")";
		try {
			dbConn.createStatement().execute(query);
		} catch (SQLException e) {
			throw new DBException("Can not execute the following SQL: \n" + query
					+ "\n. Because: " + e.getMessage());
		}
	}

	@Override
	public void removeTableColumnIndex(String schemaName, String tableName, String tableColumnName) throws DBException {
		//NOTE TAP_SCHEMA.all_columns index is not updated here. It must be updated using 'updateUserTableData'
		String indexName = getColIndexName(tableName, tableColumnName);
		String query = "DROP INDEX IF EXISTS " + schemaName + "." + indexName;
		try {
			dbConn.createStatement().execute(query);
		} catch (SQLException e) {
			throw new DBException("Can not execute the following SQL: \n" + query
					+ "\n. Because: " + e.getMessage());
		}
	}

	@Override
	public void updateUserTableData(String schemaName, String tableName,
			String tableColumnName, String ucd, String uType, int flags, int indexed) throws DBException {
		String query = "UPDATE tap_schema.all_columns SET ucd = ?, utype = ?, flags = ?, indexed = ? WHERE "+
			"schema_name = ? AND table_name = ? AND column_name = ?";
		try {
			PreparedStatement statement = dbConn.prepareStatement(query);
			statement.setObject(1, ucd, java.sql.Types.VARCHAR);
			statement.setObject(2, uType, java.sql.Types.VARCHAR);
			statement.setObject(3, flags, java.sql.Types.INTEGER);
			statement.setObject(4, indexed, java.sql.Types.INTEGER);
			statement.setString(5, schemaName);
			statement.setString(6, tableName);
			statement.setString(7, tableColumnName);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new DBException("Can not execute the following SQL: \n" + query
					+ "\n. Because: " + e.getMessage());
		}
	}
	
	@Override
	public void updateUserTableRaDecData(String schemaName, String tableName, String raColumn, String decColumn, int flagsRa, int flagsDec) throws DBException {
		String query = "UPDATE tap_schema.all_columns SET flags = ?, indexed = ? WHERE "+
				"schema_name = ? AND table_name = ? AND column_name = ?";
			try {
				PreparedStatement statement = dbConn.prepareStatement(query);
				statement.setObject(1, flagsRa, java.sql.Types.INTEGER);
				statement.setObject(2, 1, java.sql.Types.INTEGER);
				statement.setString(3, schemaName);
				statement.setString(4, tableName);
				statement.setString(5, raColumn);
				statement.executeUpdate();

				statement.setObject(1, flagsDec, java.sql.Types.INTEGER);
				statement.setObject(2, 1, java.sql.Types.INTEGER);
				statement.setString(3, schemaName);
				statement.setString(4, tableName);
				statement.setString(5, decColumn);
				statement.executeUpdate();
			} catch (SQLException e) {
				throw new DBException("Can not execute the following SQL: \n" + query
						+ "\n. Because: " + e.getMessage());
			}
	}

	@Override
	public TapTableInfo fetchTapTableInfo(String schemaName, String tableName) throws DBException {
		String query = "SELECT column_name, description, ucd, utype, datatype, unit, size, principal, std, indexed, flags FROM "+
			"tap_schema.all_columns WHERE schema_name = '"+schemaName+"' AND table_name='"+tableName+"'";
		
		ResultSet result;
		try {
			result = dbConn.createStatement().executeQuery(query);
			TapTableInfo t = null;
			String tableColumnName;
			while (result.next()) {
				if(t == null){
					t = TapTableInfo.createDefaultTapTableInfo(schemaName, tableName);
				}
				tableColumnName = result.getString(1);
				t.putColumn(tableColumnName, "description", result.getString(2));
				t.putColumn(tableColumnName, "ucd",         result.getString(3));
				t.putColumn(tableColumnName, "utype",       result.getString(4));
				t.putColumn(tableColumnName, "datatype",    result.getString(5));
				t.putColumn(tableColumnName, "unit",        result.getString(6));
				t.putColumn(tableColumnName, "size",        result.getInt(7));
				t.putColumn(tableColumnName, "principal",   result.getInt(8));
				t.putColumn(tableColumnName, "std",         result.getInt(9));
				t.putColumn(tableColumnName, "indexed",     result.getInt(10));
				t.putColumn(tableColumnName, "flags",       result.getInt(11));
			}
			return t;
		} catch (SQLException e) {
			throw new DBException("Cannot obtain table data for table: " + schemaName + "." + tableName, e);
		}
	}
	
	private String getRaDecIndexName(String tableName){
		return tableName + "_q3c";
	}
	
	private String getColIndexName(String tableName, String columnName){
		return tableName + "_" + columnName;
	}
	
	public static Timestamp getTimeStamp(String d) throws ParseException{
		Date date = UwsUtils.formatDate(d);
		return getTimeStamp(date);
	}
	
	public static Timestamp getTimeStamp(Date d){
		return new Timestamp(d.getTime());
	}

	@Override
	public void addAccessToPublicGroupTables(List<PublicGroupItem> items) throws DBException {
		try {
			startTransaction();
			PreparedStatement statement = dbConn.prepareStatement("INSERT INTO share_schema.accessible_public_group_tables "
					+ "(user_id, table_schema, table_name, table_owner) values (?,?,?,?)");
			for(PublicGroupItem item: items){
				statement.setString(1, item.getUser());
				statement.setString(2, item.getTableSchemaName());
				statement.setString(3, item.getTableName());
				statement.setString(4, item.getOwner());
				statement.executeUpdate();
			}
			endTransaction();
		} catch (SQLException e) {
			cancelTransaction();
			throw new DBException("Cannot add access to public group tables because: " + e.getMessage());
		} 
	}

	@Override
	public void removeAccessToPublicGroupTables(List<PublicGroupItem> items) throws DBException {
		try {
			startTransaction();
			PreparedStatement statement = dbConn.prepareStatement("DELETE FROM share_schema.accessible_public_group_tables "
					+ " WHERE user_id = ? AND table_schema = ? AND table_name = ? AND table_owner = ?");
			for(PublicGroupItem item: items){
				statement.setString(1, item.getUser());
				statement.setString(2, item.getTableSchemaName());
				statement.setString(3, item.getTableName());
				statement.setString(4, item.getOwner());
				statement.executeUpdate();
			}
			endTransaction();
		} catch (SQLException e) {
			cancelTransaction();
			throw new DBException("Cannot remove access from public group tables because: " + e.getMessage());
		} 
	}

	@Override
	public List<PublicGroupItem> getPublicGroupTables(String user) throws DBException {
		String query = "SELECT user_id, table_schema, table_name, table_owner FROM share_schema.accessible_public_group_tables";
		if(user != null && !"".equals(user)){
			query += " WHERE user_id = '"+user+"'";
		}
		try {
			ResultSet results = dbConn.createStatement().executeQuery(query);
			List<PublicGroupItem> items = new ArrayList<PublicGroupItem>();
			while(results.next()){
				PublicGroupItem item = new PublicGroupItem();
				item.setUser(results.getString("user_id"));
				item.setTableName(results.getString("table_name"));
				item.setTableSchemaName(results.getString("table_schema"));
				item.setOwner(results.getString("table_owner"));
				items.add(item);
			}
			return items;
		} catch (SQLException se) {
			throw new DBException("Can not execute the following SQL: \n" + query
					+ "\n. Because: " + se.getMessage());
		}
	}
	
	@Override
	public List<String> getPublicGroupSharedItems(List<PublicGroupItem> items, String groupid) throws DBException {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT s.resource_id, s.title FROM share_schema.share s, share_schema.share_groups g WHERE ");
		sb.append("s.resource_id = g.resource_id AND s.resource_type = g.resource_type AND s.resource_type = ");
		sb.append(TAPMetadataLoader.SHARED_RESOURCE_TYPE_TABLE);
		sb.append(" AND g.group_id = '").append(groupid).append("' AND s.title IN (");
		boolean firstTime = true;
		for(PublicGroupItem item: items){
			if(firstTime){
				firstTime = false;
			}else{
				sb.append(", ");
			}
			sb.append('\'').append(item.getTableSchemaName()).append('.').append(item.getTableName()).append('\'');
		}
		sb.append(")"); 
		List<String> publicTables = new ArrayList<String>();
		try {
			ResultSet results = dbConn.createStatement().executeQuery(sb.toString());
			while(results.next()){
				publicTables.add(results.getString("title"));
			}
			return publicTables;
		} catch (SQLException se) {
			throw new DBException("Can not obtain non public tables\n. Because: " + se.getMessage());
		}
	}

	
}
