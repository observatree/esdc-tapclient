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
package esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import esac.archive.gaia.dl.ingestion.db.JDBCPoolSingleton;

public class ExtPhotZeroPointsTable implements IZeroPoints {

	public double getgMagZeroPoint() {
		return gMagZeroPoint;
	}
	public void setgMagZeroPoint(double gMagZeroPoint) {
		this.gMagZeroPoint = gMagZeroPoint;
	}
	public double getBpMagZeroPoint() {
		return bpMagZeroPoint;
	}
	public void setBpMagZeroPoint(double bpMagZeroPoint) {
		this.bpMagZeroPoint = bpMagZeroPoint;
	}
	public double getRpMagZeroPoint() {
		return rpMagZeroPoint;
	}
	public void setRpMagZeroPoint(double rpMagZeroPoint) {
		this.rpMagZeroPoint = rpMagZeroPoint;
	}
	public double getgMagZeroPointError() {
		return gMagZeroPointError;
	}
	public void setgMagZeroPointError(double gMagZeroPointError) {
		this.gMagZeroPointError = gMagZeroPointError;
	}
	public double getBpMagZeroPointError() {
		return bpMagZeroPointError;
	}
	public void setBpMagZeroPointError(double bpMagZeroPointError) {
		this.bpMagZeroPointError = bpMagZeroPointError;
	}
	public double getRpMagZeroPointError() {
		return rpMagZeroPointError;
	}
	public void setRpMagZeroPointError(double rpMagZeroPointError) {
		this.rpMagZeroPointError = rpMagZeroPointError;
	}
	private double gMagZeroPoint = 0;
	private double bpMagZeroPoint = 0;
	private double rpMagZeroPoint = 0;
	private double gMagZeroPointError = 0;
	private double bpMagZeroPointError = 0;
	private double rpMagZeroPointError = 0;
	private String schema = "";
	private String table = "";
	private double[] zeros = null;
	private boolean init = false; 
	
	@Override
	public void setSchema(String schema) {
		this.schema = schema;
	}
	@Override
	public void setTable(String table) {
		this.table = table;		
	}
	@Override
	public Double getZeroPoint(int index){
		if (zeros == null) return null;
		if (index >=6 || index < 0) return null;
		return zeros[index];
	}
	@Override
	public void setZeroPoints() throws Exception {
		Statement stmt = null;
		String query = "select * from " + schema + "." + table;
		ResultSet rs = null;

		try {
			Connection con = JDBCPoolSingleton.getInstance().getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				//One line table
				gMagZeroPoint = rs.getDouble(3);
				bpMagZeroPoint = rs.getDouble(1);
				rpMagZeroPoint = rs.getDouble(5);
				gMagZeroPointError = rs.getDouble(4);
				bpMagZeroPointError = rs.getDouble(2);
				rpMagZeroPointError = rs.getDouble(6);
				zeros = new double[]{0,0,0,0,0,0};
				zeros[0] = gMagZeroPoint;
				zeros[1] = gMagZeroPointError;
				zeros[2] = bpMagZeroPoint;
				zeros[3] = bpMagZeroPointError;
				zeros[4] = rpMagZeroPoint;
				zeros[5] = rpMagZeroPointError;
			}
		}finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();	
			init = true;
		}
	}
	@Override
	public boolean isRead() {
		return init;
	}
	

}
