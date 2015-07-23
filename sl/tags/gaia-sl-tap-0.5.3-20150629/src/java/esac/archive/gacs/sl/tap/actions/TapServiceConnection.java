package esac.archive.gacs.sl.tap.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import esavo.tap.LimitUnit;
import esavo.tap.TAPException;
import esavo.tap.TAPFactory;
import esavo.tap.TAPSchemaInfo;
import esavo.tap.TAPService;
import esavo.tap.formatter.OutputFormat;
import esavo.tap.formatter.ResultSet2BinaryVotableFormatter;
import esavo.tap.formatter.ResultSet2SVFormatter;
import esavo.tap.formatter.ResultSet2JsonFormatter;
import esavo.tap.formatter.SVFormat;
import esavo.tap.metadata.TAPMetadata;
import esavo.tap.parameters.TAPParameters;
import esavo.tap.resource.TAP;
import esavo.uws.UwsException;
import esavo.uws.UwsManager;
import esavo.uws.config.UwsConfiguration;
import esavo.uws.config.UwsConfigurationManager;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.security.UwsSecurity;

public class TapServiceConnection implements TAPService {

	public static final String CAS_URL_BASE = "cas.server.url";

	public static final String TAP_VODATASERVICE_SCHEMA_EXTENSION = "esac.archive.gacs.sl.tap.actions.voDataServiceSchemaExtension";
	
	public static final String TAP_VOFUNCTIONS_SCHEMA = "esac.archive.gacs.sl.tap.actions.voFunctionsSchema";

	public static final String TAP_SCHEMAS_USE_VIEWS =  "esac.archive.gacs.sl.tap.actions.tapSchemaUseViews";
	
	public static final String APP_ID = "esac.archive.gacs.sl.tap.app.id";
	
	public static final String TAP_TEMP_LOCATION_PROP = "esac.archive.gacs.sl.tap.actions.tapTempLocation";
	//public static final String TAP_JOBS_MANAGER_ID = "esac.archive.gacs.sl.tap.actions.jobsManagerIdentifier";
	
	public static final String TAP_APP_PATH_PROP = "esac.archive.gacs.sl.tap.actions.tapAppPath";
	public static final String TAP_STAT_GRAPHS_DIRECTORY = "esac.archive.gacs.sl.tap.actions.statGraphsDirectory";
	public static final String TAP_ASYNC_THREADS = "esac.archive.gacs.sl.tap.TapServiceConfiguration.asyncThreadsNumber";
	
	public static final String UPLOAD_DIR_PROPERTY = "esac.archive.gacs.sl.fileupload.FileUploadServletConfig.uploadDir";

	public static final String MANAGER_DEFAULT_QUOTA_DB_PROP = "esac.archive.gacs.sl.tap.actions.Manager.quotaDb"; 
	public static final String MANAGER_DEFAULT_QUOTA_FILE_PROP = "esac.archive.gacs.sl.tap.actions.Manager.quotaFile";
	
	//public static final String TAP_UPLOAD_LIMIT_ROWS = "esac.archive.gacs.sl.tap.upload_limit_rows";
	//public static final String TAP_OUTPUT_LIMIT_ROWS = "esac.archive.gacs.sl.tap.output_limit_rows";
	//
	//public static final int DEFAULT_UPLOAD_LIMIT_ROWS = 10000;
	//public static final int DEFAULT_OUTPUT_LIMIT_ROWS = 100000;

	public static final String TAP_OUTPUT_LIMIT_ROWS = "esac.archive.gacs.sl.tap.output_limit_rows";
	public static final int DEFAULT_OUTPUT_LIMIT_ROWS = -1; //disabled
	
	public static final long DEFAULT_RETENTION_PERIOD = 259200000; //ms. 3 days (= 3*24*60*60*1000=259200000)
	public static final long DEFAULT_EXEC_DURATION_LIMIT = 0; //ms. 0=no limit
	
	public static final String PUBLIC_GROUP_ID_PROPERTY = "public.group.id";
	public static final String PUBLIC_GROUP_OWNER_ID_PROPERTY = "public.group.owner";
	
	//private static Map<String, TapServiceConnection> services = new HashMap<String, TapServiceConnection>();
	private static TapServiceConnection service;
	
	public static synchronized TapServiceConnection getInstance(String appid) throws UwsException, TAPException{
		//TapServiceConnection service = services.get(appid);
		if(service == null){
			service = new TapServiceConnection(appid);
			//services.put(appid, service);
		}
		return service;
	}
	
	private final String appid;

	private final 	ArrayList<String> 			coordSys;

	private final 	OutputFormat[] 	formats;
	//private final 	UserIdentifier 				identifier;

	private final	String						casServerUrlBase;
	private final   TAPSchemaInfo               tapSchemaInfo;
	private final   String						voDataServiceSchemaExtension;
	private final   String						voFunctionsSchema;
	//private TapJobsDeletionManager tapJobsDeletionManager = null;
	
	private List<String> uwsJobsParametersToIgnore;
	
	private UwsManager uwsManager;
	private TAPFactory factory;
	
	private TAP tap;

	private TapServiceConnection(String appid) throws UwsException, TAPException{
		this.appid = appid;
		
		uwsJobsParametersToIgnore = new ArrayList<String>();
		uwsJobsParametersToIgnore.add(TAPParameters.PARAM_SESSION);

		UwsConfiguration configuration = UwsConfigurationManager.getConfiguration(appid);
		File storageDir = new File(configuration.getProperty(UwsConfiguration.CONFIG_PROPERTY_STORAGE));
		
		//create factory
		factory = new TapServiceFactory(this, appid, storageDir, configuration);
		
		//initialize manager (this is done just once)
		//uwsManager = UwsManager.getManager(factory);
		uwsManager = factory.getUwsManager();
		
		//UwsConfiguration config = uwsManager.getFactory().getConfiguration();
		
		//this.defaultHostPort = configuration.getProperty(TAP_URL_BASE);
		this.casServerUrlBase=configuration.getProperty(CAS_URL_BASE);
		this.voDataServiceSchemaExtension = configuration.getProperty(TAP_VODATASERVICE_SCHEMA_EXTENSION);
		this.voFunctionsSchema = configuration.getProperty(TAP_VOFUNCTIONS_SCHEMA);
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
		
		//String checkRemovalTime = configuration.getProperty(TAP_JOBS_REMOVAL_CHECK_TIME);
		//String olderThanTime = configuration.getProperty(TAP_JOBS_OLDER_THAN_TIME);
		//tapJobsDeletionManager = new TapJobsDeletionManager(appid, checkRemovalTime, olderThanTime, this);
		
		tap = new TAP(this);
	}
	
	public TAP getTap(){
		return tap;
	}
	
	private TAPSchemaInfo createTapSchemaInfo(UwsConfiguration config){
		TAPSchemaInfo tapSchemaInfo = new TAPSchemaInfo();

		String tmpValue = config.getProperty(TAP_SCHEMAS_USE_VIEWS);
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
		long execDurationLimit = DEFAULT_EXEC_DURATION_LIMIT; //ms. 0 = for ever
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
		//String s = config.getProperty(TAP_JOBS_OLDER_THAN_TIME);
		String s = config.getProperty(UwsConfiguration.UWS_JOBS_DELTA_DESTRUCTION_TIME);
		long retentionPeriod = DEFAULT_RETENTION_PERIOD; //in ms.
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
		UwsSecurity security = service.getFactory().getSecurityManager();
		UwsJobOwner user;
		
		// If registered user do not limit results. Limits are controlled by user's quota.
		try {
			user = security.getUser();
			if(user!=null && !user.getId().equals("anonymous")){
				return new int[]{-1,-1};
			}
		} catch (UwsException e) {}
		
		UwsConfiguration config = UwsConfigurationManager.getConfiguration(appid);
		String s = config.getProperty(TAP_OUTPUT_LIMIT_ROWS);
		int limit = DEFAULT_OUTPUT_LIMIT_ROWS;
		if(s != null && !"".equals(s)){
			limit = Integer.parseInt(s);
		}
		return new int[]{limit,limit};
	}

	@Override
	public LimitUnit[] getOutputLimitType() {
		return new LimitUnit[]{LimitUnit.Row, LimitUnit.Row};
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

		// If registered user do not limit uploads. Limits are controlled by user's quota.
		UwsSecurity security = service.getFactory().getSecurityManager();
		UwsJobOwner user;
		try {
			user = security.getUser();
			if(user!=null && !user.getId().equals("anonymous")){
				return new int[]{-1,-1};
			}
		} catch (UwsException e) {}

		
		
		UwsConfiguration config = UwsConfigurationManager.getConfiguration(appid);
//		String s = config.getProperty(TAP_UPLOAD_LIMIT_ROWS);
//		int limit = DEFAULT_UPLOAD_LIMIT_ROWS;
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

	
	public String getCasServerUrlBase() {
		return casServerUrlBase;
	}

	@Override
	public TAPSchemaInfo getTapSchemaInfo(){
		return tapSchemaInfo;
	}

	@Override
	public String getVoDataServiceSchemaExtension() {
		return voDataServiceSchemaExtension;
	}
	
	public String getVoFunctionsSchema(){
		return voFunctionsSchema;
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
	public String getProjectTapName() {
		return getProperty(APP_ID);
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
