/*******************************************************************************
 * Copyright (c) 2016 European Space Agency.
 ******************************************************************************/
package esac.archive.gacs.cl.tapclient.tapSchema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import esac.archive.gacs.cl.web.client.util.TapUtils;

public class DbSchema {
	
	private String name;
	private SortedSet<DbTable> tables;

	public DbSchema(){
		this.name="";
		this.tables=new TreeSet<DbTable>();
	}

	public DbSchema(String name){
		this.name=name;
		this.tables=new TreeSet<DbTable>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SortedSet<DbTable> getTables() {
		return tables;
	}
	public void setTables(SortedSet<DbTable> tables) {
		this.tables = tables;
	}
	
	public void addTable(DbTable table){
		tables.add(table);
	}
	
	public List<String> getTableNames() {
		
		List<String>			tableNames 	= new ArrayList<String>();
		Iterator<DbTable> 		itTables 	= tables.iterator();
		while(itTables.hasNext()) {
			DbTable table = (DbTable) itTables.next();
			tableNames.add(this.getName() + "." + table.getName());
		}
		
		return tableNames;
	}
  
}
