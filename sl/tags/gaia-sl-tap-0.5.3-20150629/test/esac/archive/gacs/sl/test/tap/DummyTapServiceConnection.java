package esac.archive.gacs.sl.test.tap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import esac.archive.gacs.sl.tap.actions.TapServiceConnection;
import esavo.tap.LimitUnit;
import esavo.tap.TAPException;
import esavo.tap.TAPFactory;
import esavo.tap.TAPSchemaInfo;
import esavo.tap.TAPService;
import esavo.tap.formatter.OutputFormat;
import esavo.tap.formatter.ResultSet2BinaryVotableFormatter;
import esavo.tap.formatter.ResultSet2JsonFormatter;
import esavo.tap.formatter.ResultSet2SVFormatter;
import esavo.tap.formatter.SVFormat;
import esavo.tap.metadata.TAPMetadata;
import esavo.tap.resource.TAP;
import esavo.uws.UwsException;
import esavo.uws.UwsManager;
import esavo.uws.config.UwsConfiguration;
import esavo.uws.config.UwsConfigurationManager;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.test.uws.DummyUwsFactory.StorageType;

public class DummyTapServiceConnection implements TAPService{
	
	//public static final boolean USE_STORAGE_DATABASE = true;
	//public static final boolean USE_STORAGE_FILESYSTEM = false;
	
	private String appid;
	private DummyTapServiceFactory factory;
	private UwsManager uwsManager;
	private String voDataServiceSchemaExtension;
	private String voFunctionsSchema;
	private OutputFormat[] formats;
	private TAP tap;
	private List<String> coordSys;
	private TAPSchemaInfo tapSchemaInfo;
	private List<String> uwsJobsParametersToIgnore;
	
	public DummyTapServiceConnection(String appid, StorageType storageType) throws UwsException, TAPException{
		this.appid = appid;
		
		uwsJobsParametersToIgnore = new ArrayList<String>();
		uwsJobsParametersToIgnore.add("session");
		
		UwsConfiguration configuration = UwsConfigurationManager.getConfiguration(appid);
		//set the storage
		
		File f = new File(appid);
		if(f.exists()){
			clearStorage(f);
		}
		f.mkdirs();
		configuration.setProperty(UwsConfiguration.CONFIG_PROPERTY_STORAGE, f.getAbsolutePath());
		
		File storageDir = new File(configuration.getProperty(UwsConfiguration.CONFIG_PROPERTY_STORAGE));
		
		//create factory
		//factory = new TapServiceFactory(this, appid, storageDir, configuration);
		factory = new DummyTapServiceFactory(appid, storageDir, configuration, storageType);
		
		//initialize manager (this is done just once)
		//uwsManager = UwsManager.getManager(factory);
		uwsManager = factory.getUwsManager();
		
		//UwsConfiguration config = uwsManager.getFactory().getConfiguration();
		
		this.voDataServiceSchemaExtension = configuration.getProperty(TapServiceConnection.TAP_VODATASERVICE_SCHEMA_EXTENSION);
		this.voFunctionsSchema = configuration.getProperty(TapServiceConnection.TAP_VOFUNCTIONS_SCHEMA);
		this.tapSchemaInfo = createTapSchemaInfo(configuration);
		
		// List all available outputs (VOTable & CSV):
		formats = new OutputFormat[3];
		//formats[0] = new ResultSet2VotableFormatter(this);
		formats[0] = new ResultSet2BinaryVotableFormatter(this);
		formats[1] = new ResultSet2SVFormatter(this,SVFormat.COMMA_SEPARATOR);
		formats[2] = new ResultSet2JsonFormatter(this);
		
		// List all allowed coordinate systems:
		coordSys = new ArrayList<String>(2);
		coordSys.add("ICRS");
		coordSys.add("ICRS BARYCENTER");
		

		//String checkRemovalTime = configuration.getProperty(TapServiceConnection.TAP_JOBS_REMOVAL_CHECK_TIME);
		//String olderThanTime = configuration.getProperty(TapServiceConnection.TAP_JOBS_OLDER_THAN_TIME);

		//tapJobsDeletionManager = new TapJobsDeletionManager(appid, checkRemovalTime, olderThanTime, this);
		
		tap = new TAP(this);

	}
	
	public void clearStorage(){
		File storageDir = getFactory().getStorageDir();
		clearStorage(storageDir);
	}
	
	private void clearStorage(File f){
		if(f == null){
			return;
		}
		if(f.isDirectory()){
			for(File fTmp: f.listFiles()){
				clearStorage(fTmp);
			}
		}
		f.delete();
	}
	
	private TAPSchemaInfo createTapSchemaInfo(UwsConfiguration config){
		TAPSchemaInfo tapSchemaInfo = new TAPSchemaInfo();

		String tmpValue = config.getProperty(TapServiceConnection.TAP_SCHEMAS_USE_VIEWS);
		if(tmpValue != null && Boolean.parseBoolean(tmpValue)){
			tapSchemaInfo.setTapSchemasTableName("all_schemas");
			tapSchemaInfo.setTapTablesTableName("all_tables");
			tapSchemaInfo.setTapColumnsTableName("all_columns");
			tapSchemaInfo.setTapKeysTableName("all_keys");
			tapSchemaInfo.setTapFunctionsTableName("all_functions");
			tapSchemaInfo.setTapFunctionsArgumentsTableName("all_functions_arguments");
		}

		return tapSchemaInfo;
	}

	
	public TAP getTap(){
		return tap;
	}

	/* ****************** */
	/* COORDINATE SYSTEMS */
	/* ****************** */
	@Override
	public Collection<String> getCoordinateSystems() {
		return coordSys;
	}


	/* ********************* */
	/* SERVICE CONFIGURATION */
	/* ********************* */
	@Override
	public int[] getExecutionDuration() {
		//return new int[]{1800, 1800};		// default = 1/2 hour , max = 1/2 hour
		UwsConfiguration config = UwsConfigurationManager.getConfiguration(appid);
		String s = config.getProperty(UwsConfiguration.CONFIG_EXEC_DURATION_LIMIT); //in ms.
		long execDurationLimit = TapServiceConnection.DEFAULT_EXEC_DURATION_LIMIT; //ms. 0 = for ever
		if(s != null && !"".equals(s)){
			execDurationLimit = Long.parseLong(s);
		}
		int r = (int) (execDurationLimit / 1000); //sec.
		return new int[]{r,r};
	}

	@Override
	public int[] getRetentionPeriod() {
		UwsConfiguration config = UwsConfigurationManager.getConfiguration(appid);
		//Retention period in ms.
		//String s = config.getProperty(TapServiceConnection.TAP_JOBS_OLDER_THAN_TIME);
		String s = config.getProperty(UwsConfiguration.UWS_JOBS_DELTA_DESTRUCTION_TIME);
		long retentionPeriod = TapServiceConnection.DEFAULT_RETENTION_PERIOD; //in ms.
		if(s != null && !"".equals(s)){
			retentionPeriod = Long.parseLong(s);
		}
		//Return in seconds
		int r = (int)(retentionPeriod / 1000);
		return new int[]{r, r};	// default = max = 7 days (7 * 24 * 3600, time in seconds)
		//return new int[]{60,60}; //test: 1 minute
	}
	
	@Override
	public int[] getOutputLimit() {
		UwsConfiguration config = UwsConfigurationManager.getConfiguration(appid);
		String s = config.getProperty(TapServiceConnection.TAP_OUTPUT_LIMIT_ROWS);
		int limit = TapServiceConnection.DEFAULT_OUTPUT_LIMIT_ROWS;
		if(s != null && !"".equals(s)){
			limit = Integer.parseInt(s);
		}
		return new int[]{limit,limit};
	}

	@Override
	public LimitUnit[] getOutputLimitType() {
		return new LimitUnit[]{LimitUnit.Row, LimitUnit.Row};
		//return new LimitUnit[]{LimitUnit.Byte, LimitUnit.Byte};
	}

	@Override
	public TAPFactory getFactory() {
		return factory;
	}
	
	@Override
	public UwsConfiguration getConfiguration(){
		return factory.getConfiguration();
	}

	
	@Override
	public int[] getUploadLimit() {
		UwsConfiguration config = UwsConfigurationManager.getConfiguration(appid);
//		String s = config.getProperty(TapServiceConnection.TAP_UPLOAD_LIMIT_ROWS);
//		int limit = TapServiceConnection.DEFAULT_UPLOAD_LIMIT_ROWS;
//		if(s != null && !"".equals(s)){
//			limit = Integer.parseInt(s);
//		}
		int limit = config.getIntProperty(UwsConfiguration.CONFIG_UPLOAD_MAX_SIZE);
		return new int[]{limit,limit};
	}

	@Override
	public LimitUnit[] getUploadLimitType() {
		//return new LimitUnit[]{LimitUnit.Row, LimitUnit.Row};
		return new LimitUnit[]{LimitUnit.Byte, LimitUnit.Byte};
	}

	@Override
	public Iterator<OutputFormat> getOutputFormats() {
		return Arrays.asList(formats).iterator();
	}

	@Override
	public TAPSchemaInfo getTapSchemaInfo(){
		return tapSchemaInfo;
	}

	@Override
	public String getVoDataServiceSchemaExtension() {
		return voDataServiceSchemaExtension;
	}

	public String getProperty(String propertyName){
		UwsConfiguration config = getFactory().getConfiguration();
		return config.getProperty(propertyName);
	}

	@Override
	public List<String> getUwsJobsToIgnoreParameters() {
		return uwsJobsParametersToIgnore;
	}

	@Override
	public String getVoFunctionsSchema() {
		return voFunctionsSchema;
	}

	@Override
	public String getProjectTapName() {
		return appid;
	}

	@Override
	public List<String> getAvailableSchemas(UwsJobOwner owner) {
		if(owner != null && owner.getAuthUsername() != null){
			String userSchemaName = TAPMetadata.getUserSchema(owner);
			List<String> schemas = new ArrayList<String>();
			schemas.add(userSchemaName);
			return schemas;
		}else{
			return null;
		}
	}
	
}
