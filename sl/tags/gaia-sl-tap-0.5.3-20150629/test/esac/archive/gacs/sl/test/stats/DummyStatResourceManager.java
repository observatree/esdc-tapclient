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
package esac.archive.gacs.sl.test.stats;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import esac.archive.gacs.sl.services.statgraphs.StatResourceManager;
import esac.archive.gacs.sl.test.TestUtils;
import esac.archive.gacs.sl.test.data.ReadData;

/**
 * @author Raul Gutierrez-Sanchez Copyright (c) 2014- European Space Agency
 */

public class DummyStatResourceManager implements StatResourceManager {
	
	String appId; 
	
	public DummyStatResourceManager(String appId){
		this.appId = appId;
	}
	
	@Override
	public boolean exists(String table, String type, String column) {
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}

		InputStream ins = ReadData.findResource(this.getClass(),TestUtils.STATGRAPHS_DIR+"/"+table+column+"_"+type.toLowerCase()+".png");
		if(ins==null) return false;
		try{
			ins.read();
			return true;
		}catch(IOException e){
			return false;
		}
	}

	@Override
	public long getResourceLength(String table, String type, String column) throws IOException {
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}
		return ReadData.getContentLength(this.getClass(), TestUtils.STATGRAPHS_DIR+"/"+table+column+"_"+type.toLowerCase()+".png");
	}

	@Override
	public InputStream getResource(String table, String type, String column) throws FileNotFoundException {
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}
		return ReadData.findResource(this.getClass(),TestUtils.STATGRAPHS_DIR+"/"+table+column+"_"+type.toLowerCase()+".png");
	}

	@Override
	public InputStream getResourcesMetadata() throws FileNotFoundException {
		return ReadData.findResource(this.getClass(),TestUtils.STATGRAPHS_DIR+"/graphs.json");	
	}

	@Override
	public String getResourceName(String table, String type, String column){
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}
		return table+column+"_"+type.toLowerCase()+".png";
	}

	


}
