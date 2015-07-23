package esac.archive.gacs.sl.services.statgraphs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import esac.archive.gacs.sl.tap.actions.TapServiceConnection;
import esavo.uws.config.UwsConfiguration;
import esavo.uws.config.UwsConfigurationManager;

/**
 * @author Raul Gutierrez-Sanchez Copyright (c) 2014- European Space Agency
 */

public class DefaultStatResourceManager implements StatResourceManager {

	String appId; 
	
	String graphsDirectory;
	
	DefaultStatResourceManager(String appId){
		this.appId = appId;

		UwsConfiguration configuration = UwsConfigurationManager.getConfiguration(appId);

		//this.graphsDirectory=EnvironmentManager.getProperty(appId, EnvironmentManager.TAP_STAT_GRAPHS_DIRECTORY);
		this.graphsDirectory = configuration.getProperty(TapServiceConnection.TAP_STAT_GRAPHS_DIRECTORY);
	}
	
	@Override
	public boolean exists(String table, String type, String column) {
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}
		File requestedFile = new File(graphsDirectory+"/"+table+column+"_"+type.toLowerCase()+".png");
		return requestedFile.canRead();
	}

	@Override
	public InputStream getResource(String table, String type, String column) throws FileNotFoundException {
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}

		FileInputStream stream = new FileInputStream(graphsDirectory+"/"+table+column+"_"+type.toLowerCase()+".png");
		return stream;
	}

	@Override
	public InputStream getResourcesMetadata() throws FileNotFoundException {
		FileInputStream stream = new FileInputStream(graphsDirectory+"/graphs.json");
		return stream;
	}

	@Override
	public long getResourceLength(String table, String type, String column) {
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}

		UwsConfiguration configuration = UwsConfigurationManager.getConfiguration(appId);
		//File graphsDirectory=new File(EnvironmentManager.getProperty(appId, EnvironmentManager.TAP_STAT_GRAPHS_DIRECTORY));
		File graphsDirectory = new File(configuration.getProperty(TapServiceConnection.TAP_STAT_GRAPHS_DIRECTORY));
		File requestedFile = new File(graphsDirectory.getAbsolutePath()+"/"+table+column+"_"+type.toLowerCase()+".png");
		return requestedFile.length();
	}

	@Override
	public String getResourceName(String table, String type, String column)
			throws IOException {
		if(table.equals("not_available") || column==null || column.trim().length()==0){
			column="";
		}else{
			column="-"+column.trim();
		}
		
		return table+column+"_"+type.toLowerCase()+".png";
	}

}
