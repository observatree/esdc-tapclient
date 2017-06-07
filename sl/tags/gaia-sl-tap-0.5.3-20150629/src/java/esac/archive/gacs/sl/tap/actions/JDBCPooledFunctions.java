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
package esac.archive.gacs.sl.tap.actions;

import java.util.List;

import esac.archive.gacs.sl.services.publicgroup.PublicGroupItem;
import esavo.tap.db.DBConnection;
import esavo.tap.db.DBException;
import esavo.tap.metadata.TAPTable;
import esavo.uws.share.UwsShareManager;

public interface JDBCPooledFunctions extends DBConnection{

	public void updateTableSizeInTapSchema(TAPTable table) throws DBException;

	/**
	 * Returns the created PK name
	 * @param table
	 * @return
	 * @throws DBException
	 */
	public String createPkInTable(TAPTable table) throws DBException;
	
	public void createRaAndDecIndexes(String schemaName, String tableName, String raCol, String decCol, int raDecFlag) throws DBException;

	public void removeRaAndDecIndexes(String schemaName, String tableName, String raCol, String decCol, int raDecFlag) throws DBException;
	
	public void vacuumAnalyze(String schemaName, String tableName) throws DBException;
	
	public void createTableColumnIndex(String schemaName, String tableName, String tableColumnName) throws DBException;
	
	public void removeTableColumnIndex(String schemaName, String tableName, String tableColumnName) throws DBException;
	
	public void updateUserTableData(String schemaName, String tableName, String tableColumnName, String ucd, String uType, int flags, int indexed) throws DBException;
	
	public void updateUserTableRaDecData(String schemaName, String tableName, String raColumn, String decColumn, int flagsRa, int flagsDec) throws DBException;
	
	public TapTableInfo fetchTapTableInfo(String schemaName, String tableName) throws DBException;
	
	long getDbSize(String ownerid) throws DBException;
	long getTableSize(String schema, String table) throws DBException;
	
	//Public Group functions
	
	/**
	 * Adds access to the requested tables.<br/>
	 * No check about duplicated items is performed (an exception will be raised because of PK clashes).
	 * @param items
	 * @throws DBException
	 */
	public void addAccessToPublicGroupTables(List<PublicGroupItem> items) throws DBException;
	
	/**
	 * Removes access to the requested tables.
	 * @param items
	 * @throws DBException
	 */
	public void removeAccessToPublicGroupTables(List<PublicGroupItem> items) throws DBException;
	
	/**
	 * List all the public group tables the user can access to. If no user is provided, the entire
	 * table list is returned. (Table share_schema.accessible_public_group_tables).<br/>
	 * To obtain all public group tables, use {@link UwsShareManager#getGroupItems(String)}
	 * @param user
	 * @return
	 * @throws DBException
	 */
	public List<PublicGroupItem> getPublicGroupTables(String user) throws DBException;
	
	/**
	 * Returns the tables that are not in public_group group.
	 * @param items
	 * @param groupid
	 * @return
	 * @throws DBException
	 */
	public List<String> getPublicGroupSharedItems(List<PublicGroupItem> items, String groupid) throws DBException;
	
	
}
