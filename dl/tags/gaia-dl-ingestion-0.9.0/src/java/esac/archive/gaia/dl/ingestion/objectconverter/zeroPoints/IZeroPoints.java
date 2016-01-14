package esac.archive.gaia.dl.ingestion.objectconverter.zeroPoints;

public interface IZeroPoints {

	public void setSchema(String schema);
	public void setTable(String table);
	public Double getZeroPoint(int index);
	public void setZeroPoints() throws Exception;
	public boolean isRead();
}
