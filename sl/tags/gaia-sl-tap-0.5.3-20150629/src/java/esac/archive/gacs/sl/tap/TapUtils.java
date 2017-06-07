/*******************************************************************************
 * Copyright (C) 2017 rgutierrez
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
package esac.archive.gacs.sl.tap;

import java.util.ArrayList;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.votable.VOStarTable;
import esavo.tap.metadata.TAPColumn;
import esavo.tap.metadata.TAPTable;
import esavo.tap.metadata.TAPTypes;
import esavo.tap.metadata.VotType;

public class TapUtils {

	
	
	/** Get the list of all allowed coordinate systems. */
	public static ArrayList<String> getCoordinateSystems(){
		ArrayList<String> coordSys = new ArrayList<String>(2);
		coordSys.add("ICRS");
		coordSys.add("ICRS BARYCENTER");
		return coordSys;
	}
	
	public static String getDbType(TAPColumn tc){
		if(TAPTypes.checkVarBinaryRequired(tc.getDatatype(), tc.getArraySize())){
			//TODO function to obtain the array data type required
			return "bytea";
		}else{
			return TAPTypes.getDBType(tc.getDatatype());
		}
	}
	
	public static int getArraySize(ColumnInfo field){
		int arraysize = TAPTypes.NO_SIZE;
		if(!field.isArray())
			arraysize = 1;
		else if (field.isArray()&&field.getShape()[0]<=0)
			arraysize = TAPTypes.STAR_SIZE;
		else{
			try{
				arraysize = field.getShape()[0];
			}catch(Exception nfe){
				throw new IllegalArgumentException("Invalid array-size for the field \""+field.getName()+"\": \""+arraysize+"\"");
			}
		}
		return arraysize;
	}
	
	
	public static TAPColumn addColumnToTable(TAPTable tapTable, ColumnInfo field, String fieldNameLowerCase, int arraysize, int flags){
		return tapTable.addColumn(fieldNameLowerCase, 
				field.getDescription(), 
				field.getUnitString(), 
				field.getUCD(), 
				field.getUtype(), 
				new VotType((String)field.getAuxDatumValue(VOStarTable.DATATYPE_INFO, String.class), 
							arraysize, 
							(String)field.getAuxDatumValue(VOStarTable.XTYPE_INFO, String.class)), 
				false, 
				false, 
				false,
				flags);

	}


}
