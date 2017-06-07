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
package esac.archive.gacs.sl.tap.adql;

import esavo.adql.db.DBColumn;
import esavo.adql.db.DBTable;
import esavo.adql.query.ADQLObject;
import esavo.adql.query.operand.ADQLColumn;
import esavo.adql.query.operand.function.geometry.PointFunction;
import esavo.adql.search.SimpleReplaceHandler;

/**
 * Replace all POINT('...', ra, dec) by coord.
 */
public class ReplacePointHandler extends SimpleReplaceHandler {

	@Override
	protected boolean match(ADQLObject obj) {
		if (obj instanceof PointFunction){
			PointFunction point = (PointFunction)obj;
			if (point.getCoord1() instanceof ADQLColumn && point.getCoord2() instanceof ADQLColumn){
				ADQLColumn coord1 = (ADQLColumn)point.getCoord1(), coord2 = (ADQLColumn)point.getCoord2();
				if (coord1.getDBLink() == null)
					return coord1.getColumnName().equalsIgnoreCase("ra") && coord2.getColumnName().equalsIgnoreCase("dec");
				else
					return coord1.getDBLink().getDBName().equalsIgnoreCase("ra") && coord2.getDBLink().getDBName().equalsIgnoreCase("dec");
			}
		}
		return false;
	}

	@Override
	protected ADQLObject getReplacer(ADQLObject obj) throws UnsupportedOperationException {
		PointFunction point = (PointFunction)obj;
		if (((ADQLColumn)point.getCoord1()).getDBLink() == null)
			return new ADQLColumn("coord");
		else{
			DBTable t = ((ADQLColumn)point.getCoord1()).getDBLink().getTable();
			DBColumn coordColumn = t.getColumn("coord", false);
			ADQLColumn col = new ADQLColumn("coord");
			col.setDBLink(coordColumn);
			return col;
		}
	}

}
