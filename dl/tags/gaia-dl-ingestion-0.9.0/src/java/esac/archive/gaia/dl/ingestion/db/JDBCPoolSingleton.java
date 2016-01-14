package esac.archive.gaia.dl.ingestion.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import esac.archive.gaia.dl.ingestion.config.ConfigProperties;

public class JDBCPoolSingleton {
	
    private static JDBCPoolSingleton INSTANCE = null;
    private static List<JDBCPoolSingleton> INSTANCES = null;
    
    public static final String DB_DRIVER_PROP	= "db.driver";
    public static final String DB_DERBY_PROP	= "db.derby";
    public static final String DB_SERVER_PROP   = "db.server";
    public static final String DB_PORT_PROP     = "db.port";
    public final static String DB_NAME_PROP     = "db.name";
    public final static String DB_OWNER_PROP    = "db.owner";
    public final static String DB_PWD           = "db.pwd";
    
    ComboPooledDataSource cpds;
    
    private JDBCPoolSingleton() throws PropertyVetoException {
    	        
        String driver = ConfigProperties.getInstance().getProperty(DB_DRIVER_PROP);
        String derby = ConfigProperties.getInstance().getProperty(DB_DERBY_PROP);
        String server = ConfigProperties.getInstance().getProperty(DB_SERVER_PROP);
        String port = ConfigProperties.getInstance().getProperty(DB_PORT_PROP);
        String dbname = ConfigProperties.getInstance().getProperty(DB_NAME_PROP);
        String owner = ConfigProperties.getInstance().getProperty(DB_OWNER_PROP);
        String password = ConfigProperties.getInstance().getProperty(DB_PWD);
        
        String connectionUrl = "jdbc:"+derby+"://"+server+":"+port+"/"+dbname; 

        cpds = new ComboPooledDataSource();
    	cpds.setDriverClass(driver); //loads the jdbc driver
    	cpds.setJdbcUrl(connectionUrl);
    	cpds.setUser(owner);
    	cpds.setPassword(password);

    	// the settings below are optional -- c3p0 can work with defaults
    	cpds.setMinPoolSize(5);
    	cpds.setAcquireIncrement(5);
    	cpds.setMaxPoolSize(20);
    	
    }

    // functionality methods
    
    public synchronized Connection getConnection() throws SQLException{
    	return this.cpds.getConnection();
    }
    
    
    
    // Singleton methods
    
    private synchronized static void createInstance() throws PropertyVetoException {
        if (INSTANCE == null) { 
            INSTANCE = new JDBCPoolSingleton();
        }
    }
 
    public static JDBCPoolSingleton getInstance() throws PropertyVetoException {
        createInstance();
        return INSTANCE;
    }
    
    private synchronized static void createInstancesInPool() throws PropertyVetoException {
        if (INSTANCES == null) { 
            INSTANCES = new ArrayList<JDBCPoolSingleton>();
        }
        INSTANCES.add(new JDBCPoolSingleton());
    }
 
    public static void addInstanceToPool() throws PropertyVetoException {
    	createInstancesInPool();
    }
    
    public static JDBCPoolSingleton getInstanceInPool(int index) throws PropertyVetoException {
    	if (index < 0 || index >= INSTANCES.size())
    		return null;
        return INSTANCES.get(index);
    }
    
    
    
    
    
}