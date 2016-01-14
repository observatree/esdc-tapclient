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
