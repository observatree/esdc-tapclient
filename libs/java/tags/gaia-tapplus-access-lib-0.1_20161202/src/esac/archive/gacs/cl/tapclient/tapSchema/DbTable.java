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
