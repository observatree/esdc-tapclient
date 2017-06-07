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

import esac.archive.gacs.sl.services.status.StatusManager;
import esac.archive.gacs.sl.services.status.types.StatusTableEdit;
import esac.archive.gacs.sl.services.util.Utils;
import esac.archive.gacs.sl.tap.actions.JDBCPooledFunctions;
import esac.archive.gacs.sl.tap.actions.TapTableInfo;
import esavo.tap.db.DBException;

public class TableToolUtils {

	/**
	 * package-private for test-harnesses<br/>
	 * Updates status manager
	 * @param currentTableIndex
	 * @param numTables
	 * @param taskid
	 */
	public static void updateTaskStatus(int currentTableIndex, int numTables, long taskid){
		if(taskid < 0){
			//to task id to update
			return;
		}
		int percentDone = (int) Math.round(100.00 * currentTableIndex / numTables);
		StatusTableEdit status = new StatusTableEdit(""+percentDone);
		try{
			StatusManager.getInstance().updateStatus(taskid, status); 
		} catch (IllegalArgumentException iae){
			iae.printStackTrace();
		}
	}

	
	/**
	 * Creates a ra/dec index.<br/>
	 * Checks whether ra/dec are already indexed.<br/>
	 * The previous ra/dec index is removed (if it is found).<br/>
	 * @param dbConn database connection.
	 * @param tapTableInfo current database information.
	 * @param raColumn
	 * @param decColumn
	 * @return 'true' if the field has been updated.
	 * @throws DBException
	 */
	public static boolean indexRaDec(JDBCPooledFunctions dbConn, TapTableInfo tapTableInfo, String raColumn, String decColumn) throws DBException{
		if(raColumn == null){
			throw new IllegalArgumentException("Ra column not found");
		}
		if(decColumn == null){
			throw new IllegalArgumentException("Dec column not found");
		}

		//If it is already indexed, do not index again.
		//Remove old Ra/Dec if they are not the same columns.
		boolean alreadyIndexed = Utils.areAlreadyIndexedRaDec(tapTableInfo, raColumn, decColumn);
		if(alreadyIndexed){
			//Nothing to do, ra/dec are already indexed on the same columns.
			return false;
		}
		
		//Not the same columns.
		//Remove previous ra/dec if they exists
		removePrevRaDecIfExists(dbConn, tapTableInfo);
		
		//Create new indexes
		dbConn.createRaAndDecIndexes(tapTableInfo.getSchemaName(), tapTableInfo.getTableName(), raColumn, decColumn, Utils.TAP_TABLE_TYPE_RADEC); 
		return true;
	}

	/**
	 * Removes previous ra/dec index, if it is found.
	 * @param dbConn database connection
	 * @param tapTableInfo current database information.
	 * @return 'true' if the field has been updated.
	 * @throws DBException
	 */
	public static boolean removePrevRaDecIfExists(JDBCPooledFunctions dbConn, TapTableInfo tapTableInfo) throws DBException{
		String raColumn = null;
		String decColumn = null;
		int flags = 0;
		for(String tableColumnName: tapTableInfo.getTableColumnNames()){
			//tapTableInfo contains database current info: flags is an integer in database (it can be null)
			flags = Utils.getFlagsFromTapTable(tapTableInfo, tableColumnName);
			if((flags & Utils.TAP_COLUMN_TABLE_FLAG_RA) > 0){
				raColumn = tableColumnName;
				continue;
			}
			if((flags & Utils.TAP_COLUMN_TABLE_FLAG_DEC) > 0){
				decColumn = tableColumnName;
				continue;
			}
		}
		if(raColumn == null || decColumn == null){
			//wrong ra/dec specification, no index created.
			return false;
		}
		
		//we have the previous ra/dec indexed columns, remove them
		dbConn.removeRaAndDecIndexes(tapTableInfo.getSchemaName(), tapTableInfo.getTableName(), raColumn, decColumn, Utils.TAP_TABLE_TYPE_RADEC);
		return true;
	}
	
}
