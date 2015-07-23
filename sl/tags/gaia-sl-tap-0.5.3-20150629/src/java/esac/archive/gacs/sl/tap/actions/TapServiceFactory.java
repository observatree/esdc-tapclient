package esac.archive.gacs.sl.tap.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import esac.archive.gacs.sl.tap.adql.EsacADQLQueryFactory;
import esavo.adql.parser.ADQLQueryFactory;
import esavo.adql.translator.ADQLTranslator;
import esavo.adql.translator.Q3cPgSphereTranslator;
import esavo.tap.ADQLExecutor;
import esavo.tap.AbstractTAPFactory;
import esavo.tap.TAPException;
import esavo.tap.TAPService;
import esavo.tap.db.DBConnection;
import esavo.tap.log.DefaultTAPLog;
import esavo.tap.log.TAPLog;
import esavo.uws.UwsManager;
import esavo.uws.config.UwsConfiguration;
import esavo.uws.creator.UwsDefaultCreator;
import esavo.uws.output.UwsDefaultOutputHandler;
import esavo.uws.owner.UwsJobOwner;
import esavo.uws.scheduler.UwsDefaultScheduler;
import esavo.uws.share.UwsShareGroup;
import esavo.uws.storage.jdbc.UwsJdbcStorage;

public class TapServiceFactory extends AbstractTAPFactory {
	
	private DefaultTAPLog tapLog = new DefaultTAPLog(); 


	protected TapServiceFactory(TAPService service, String appid, File storageDir, UwsConfiguration configuration) throws NullPointerException {
		super(service, appid, storageDir, configuration);
		
		creator = new UwsDefaultCreator(appid);
		//securityManager = new TapSecurityManager(this);
		securityManager = new TapSecurityManager(appid);
		storageManager = new UwsJdbcStorage(appid, getStorageDir(), creator, configuration);
		executor = new ADQLExecutor(service, appid, tapLog);
		scheduler = new UwsDefaultScheduler(appid, executor);
		outputHandler = new UwsDefaultOutputHandler(appid);
		
		uwsManager = UwsManager.getManager(this);
	}

	@Override
	public ADQLTranslator createADQLTranslator() throws TAPException {
		//return new PgSphereTranslator();
		return new Q3cPgSphereTranslator();
	}

	@Override
	public DBConnection createDBConnection(String jobID)
			throws TAPException {
		//return new DemoTAP_DBConnection();
		//return new JDBCPooledConnection(service.getTapSchemaInfo());
		return new JDBCPooledConnection(service);
	}

	@Override
	public ADQLQueryFactory createQueryFactory(UwsJobOwner owner) throws TAPException{
		return new EsacADQLQueryFactory(owner);
	}

//	@Override
//	public String getApplicationIdentifier() {
//		return service.getApplicationIdentifier();
//	}
//
//	@Override
//	public String getApplicationProperty(String property) {
//		return service.getApplicationProperty(property);
//	}

	@Override
	public TAPLog getLogger() {
		return tapLog;
	}
	
	
	
	//TODO testing to deactivate query checker. 
	//Shall be removed to activate afterwards
	//@Override
	//public QueryChecker createQueryChecker(TAPSchema uploadSchema, String userid){
	//	return null;
	//}
	   
	
	@Override
	public List<UwsShareGroup> getAvailableGroups(String user) {
		String id = getConfiguration().getProperty(TapServiceConnection.PUBLIC_GROUP_ID_PROPERTY);
		String title = "Public Group";
		String description = "Puglic Group";
		String creator = getConfiguration().getProperty(TapServiceConnection.PUBLIC_GROUP_OWNER_ID_PROPERTY);
		UwsShareGroup group = new UwsShareGroup(id, title, description, creator);
		List<UwsShareGroup> groups = new ArrayList<UwsShareGroup>();
		groups.add(group);
		return groups;
	}





}
