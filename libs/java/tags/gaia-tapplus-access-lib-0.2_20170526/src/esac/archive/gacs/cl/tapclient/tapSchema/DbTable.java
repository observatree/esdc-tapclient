/*******************************************************************************
 * Copyright (c) 2016 European Space Agency.
 ******************************************************************************/
package esac.archive.gacs.cl.tapclient.tapSchema;


import java.util.ArrayList;

public class DbTable implements Comparable<DbTable>{
	
	private int id;
	private String name;
	private ArrayList<DbColumn> columns;
	private String desc;

	
	public DbTable(){
		this.id=-1;
		this.name="";
		this.columns=new ArrayList<DbColumn>();
		this.desc="";
	}

	/**
	 * 
	 * @param id
	 * @param name
	 * @param desc
	 */
	public DbTable(int id, String name, String desc){
		this.id=id;
		this.name=name;
		this.columns=new ArrayList<DbColumn>();
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
	public ArrayList<DbColumn> getColumns() {
		return columns;
	}
	public void setColumns(ArrayList<DbColumn> columns) {
		this.columns = columns;
	}
  
	public void addColumn(DbColumn column){
		columns.add(column);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public int compareTo(DbTable o) {
		return this.name.compareTo(o.name);
	}
	
}
