/*******************************************************************************
 * Copyright (c) 2016 European Space Agency.
 ******************************************************************************/
package esac.archive.gacs.cl.tapclient.tapSchema;

public class DbColumn {

	private int 	id;
	private String 	name;
	private boolean indexed = false;
	private String 	desc;
	private String 	unit;
	private String 	utype;
	private String 	ucd;
	private String 	dataType;

	public DbColumn(){
		this.id=-1;
		this.name="";
		this.desc="";
	}

	public DbColumn(int id, String name, String desc){
		this.id=id;
		this.name=name;
		this.desc=desc;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isIndexed() {
		return indexed;
	}
	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUtype() {
		return utype;
	}

	public void setUtype(String utype) {
		this.utype = utype;
	}

	public String getUcd() {
		return ucd;
	}

	public void setUcd(String ucd) {
		this.ucd = ucd;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
