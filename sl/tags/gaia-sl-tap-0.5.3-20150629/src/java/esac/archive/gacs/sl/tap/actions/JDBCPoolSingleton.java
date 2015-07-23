package esac.archive.gacs.sl.tap.actions;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import esavo.tap.TAPService;
import esavo.uws.config.UwsConfiguration;

public class JDBCPoolSingleton {
	
	public static final String DB_SERVER_PROP 	= "esac.archive.gacs.sl.tap.actions.JDBCPoolSingleton.dbServer";
	public static final String DB_PORT_PROP 	= "esac.archive.gacs.sl.tap.actions.JDBCPoolSingleton.dbPort";
	public final static String DB_NAME_PROP 	= "esac.archive.gacs.sl.tap.actions.JDBCPoolSingleton.dbName";
	public final static String DB_OWNER_PROP 	= "esac.archive.gacs.sl.tap.actions.JDBCPoolSingleton.dbOwner";
	public final static String DB_PWD_PROP 	= "esac.archive.gacs.sl.tap.actions.JDBCPoolSingleton.dbPwd";

    //private static JDBCPoolSingleton INSTANCE = null;
	//private static Map<String, JDBCPoolSingleton> instances = new HashMap<String, JDBCPoolSingleton>();
	private static JDBCPoolSingleton instance;
    
    /**
     * This is used for test-harnesses
     * To set a connection, use {@link #setConnection(Connection)}
     */
    private static Connection dummyConnection = null;
    
    private DataSource datasource = null;
    private String appid = null;
    
    private JDBCPoolSingleton(TAPService service) {
    	appid = service.getFactory().getAppId();
    	if(dummyConnection != null){
    		//if dummyConnection is not null, we do not want to use a real database framework.
    		//No instance is created and 'dummyConnection' is returned when {@link getConnection()} is called.
    		return;
    	}
    	
    	UwsConfiguration configuration = service.getFactory().getConfiguration();
    	
    	String server = configuration.getProperty(DB_SERVER_PROP);
    	String port = configuration.getProperty(DB_PORT_PROP);
    	String dbname = configuration.getProperty(DB_NAME_PROP);
    	String owner = configuration.getProperty(DB_OWNER_PROP);
    	String pwd = configuration.getProperty(DB_PWD_PROP);
    	
    	String connectionUrl = "jdbc:postgresql://"+server+":"+port+"/"+dbname; 
    	
    	System.out.println("TAP JDBC app '"+appid+"': "+connectionUrl + "\n");

    	PoolProperties p = new PoolProperties();
        p.setUrl(connectionUrl);
        p.setDriverClassName("org.postgresql.Driver");
        p.setUsername(owner);
        p.setPassword(pwd);
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(90);
        p.setInitialSize(10);
        p.setMaxWait(60000);
        //p.setRemoveAbandonedTimeout(300); //5 min.
        p.setRemoveAbandonedTimeout(2400); //40 min.
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
          "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
          "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        datasource= new DataSource();
        datasource.setPoolProperties(p); 	
    	
    }

    // functionality methods
    
    public Connection getConnection() throws SQLException{
    	if(dummyConnection != null) {
    		return dummyConnection;
    	} else {
    		return this.datasource.getConnection();
    	}
    }
    
    
    
    // Singleton methods
    
//    private synchronized static JDBCPoolSingleton createInstance(TAPService service) {
//    	String appid = service.getFactory().getAppId();
//    	JDBCPoolSingleton pool = instances.get(appid);
//        if (pool == null) {
//        	pool = new JDBCPoolSingleton(service);
//        	instances.put(appid, pool);
//        }
//        return pool;
//    }
    
    private synchronized static JDBCPoolSingleton createInstance(TAPService service) {
        if (instance == null) {
        	instance = new JDBCPoolSingleton(service);
        }
        return instance;
    }
 
    public static JDBCPoolSingleton getInstance(TAPService service) {
        return createInstance(service);
    }
    
    
    /**
     * To be used by test-harnesses.<br/>
     * If connection is not null, this object will be returned when calling {@link #getInstance()}
     * @param connection
     */
    public static void setConnection(Connection connection){
    	dummyConnection = connection;
    }
    
}