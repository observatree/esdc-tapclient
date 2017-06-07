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
package esac.archive.gacs.sl.services.statgraphs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import esac.archive.gacs.sl.tap.actions.GacsTapService;
import esavo.sl.tap.actions.TapServiceConnection;
import esavo.tap.TAPService;
import esavo.tap.metadata.TAPMetadata;
import esavo.tap.metadata.TAPMetadataLoader;
import esavo.tap.metadata.TAPMetadataLoaderArgs;
import esavo.tap.metadata.TAPTable;
import esavo.uws.config.UwsConfiguration;
import esavo.uws.config.UwsConfigurationManager;
import esavo.uws.owner.UwsJobOwner;

/**
 * @author Raul Gutierrez-Sanchez Copyright (c) 2014- European Space Agency
 */

public class DefaultStatResourceManager implements StatResourceManager {
	
	private static final Logger LOG = Logger.getLogger(DefaultStatResourceManager.class.getName());
	
	String appId; 
	
	String graphsDirectory;
	
	DefaultStatResourceManager(String appId){
		this.appId = appId;

		UwsConfiguration configuration = UwsConfigurationManager.getConfiguration(appId);

		//this.graphsDirectory=EnvironmentManager.getProperty(appId, EnvironmentManager.TAP_STAT_GRAPHS_DIRECTORY);
		this.graphsDirectory = configuration.getProperty(GacsTapService.TAP_STAT_GRAPHS_DIRECTORY);
	}

	@Override
	public boolean hasAccess(UwsJobOwner user, String table, String column) {
		if(table.equals("not_available")){
			return true;
		}
		TAPMetadata tapMetadata;
		try {
			TAPService tapService = TapServiceConnection.getInstance(appId);
			
			TAPMetadataLoaderArgs args = new TAPMetadataLoaderArgs();
			args.setFullQualifiedTableNames(Arrays.asList(table));
			args.setIncludeAccessibleSharedItems(true);


			tapMetadata = TAPMetadataLoader.getSingleTable(tapService, user, args);
		} catch (Exception e) {
			//e.printStackTrace();
			LOG.info(e.getMessage());
			return false;
		}
		
		if(tapMetadata.hasTable(TAPMetadata.getSchemaFromTable(table), TAPMetadata.getTableNameOnly(table))){
			if(column==null){
				return true;
			}else{
				TAPTable tapTable = tapMetadata.getTable(TAPMetadata.getSchemaFromTable(table), TAPMetadata.getTableNameOnly(table));
				return tapTable.hasColumn(column);
			}
		}
		
		return false;
	}

	@Override
	public boolean exists(UwsJobOwner user, String table, String type, String column){
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}
		File requestedFile = new File(graphsDirectory+"/"+table+column+"."+type.toLowerCase()+".png");
		boolean exists = requestedFile.canRead();
		if(exists){
			return true;
		}
		LOG.info("Graph file not found: "+requestedFile.getAbsolutePath());
		return false;
	}

	@Override
	public InputStream getResource(UwsJobOwner user, String table, String type, String column) throws FileNotFoundException {
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}

		FileInputStream stream = new FileInputStream(graphsDirectory+"/"+table+column+"."+type.toLowerCase()+".png");
		return stream;
	}

	@Override
	public InputStream getResourcesMetadata() throws FileNotFoundException {
		FileInputStream stream = new FileInputStream(graphsDirectory+"/graphs.json");
		return stream;
	}

	@Override
	public long getResourceLength(UwsJobOwner user, String table, String type, String column) {
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}

		UwsConfiguration configuration = UwsConfigurationManager.getConfiguration(appId);
		//File graphsDirectory=new File(EnvironmentManager.getProperty(appId, EnvironmentManager.TAP_STAT_GRAPHS_DIRECTORY));
		File graphsDirectory = new File(configuration.getProperty(GacsTapService.TAP_STAT_GRAPHS_DIRECTORY));
		File requestedFile = new File(graphsDirectory.getAbsolutePath()+"/"+table+column+"."+type.toLowerCase()+".png");
		return requestedFile.length();
	}

	@Override
	public String getResourceName(UwsJobOwner user, String table, String type, String column)
			throws IOException {
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}
		
		return table+column+"_"+type.toLowerCase()+".png";
	}
}
