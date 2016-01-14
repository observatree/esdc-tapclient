package esac.archive.gaia.dl.ingestion.dump;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.starlink.table.StarTableWriter;


public class DumperThread implements Callable<Integer> {
	
    private static final Logger logger = Logger.getLogger(DumperThread.class.getName());
    
    private Class<?> classToDump;
    private boolean gbin;
	private String tableWriter;
	private String dumpOutputPath; 
	private String dumpFilePattern;
	private long dumpMaxSizeInRows;
	private String schemaName;
	private String tableName;
	private int index;

	/*gbin*/
    public DumperThread(
			Class<?> classToDump,
			String dumpOutputPath, 
			String dumpFilePattern,
			long dumpMaxSizeInRows,
			String schemaName,
			String tableName,
			int index){
    	this.gbin = true;
    	this.classToDump = classToDump;
		this.dumpOutputPath = dumpOutputPath;
		this.dumpFilePattern = dumpFilePattern;
		this.dumpMaxSizeInRows = dumpMaxSizeInRows;
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.index = index;
    }
    
    /*stilts*/
    public DumperThread(
			String tableWriter,
			String dumpOutputPath, 
			String dumpFilePattern,
			long dumpMaxSizeInRows,
			String schemaName,
			String tableName,
			int index){
    	this.gbin = false;
		this.tableWriter = tableWriter;
		this.dumpOutputPath = dumpOutputPath;
		this.dumpFilePattern = dumpFilePattern;
		this.dumpMaxSizeInRows = dumpMaxSizeInRows;
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.index = index;
    }
    
    @Override
    public Integer call() throws Exception {

    	logger.log(Level.INFO, "STARTING THREAD: index " + index);

        int processedSourcesNumber = 0;
        try {
        		            		
        		StiltsDumpWorker sdw = new StiltsDumpWorker(
        				tableWriter,
        				dumpOutputPath, 
        				dumpFilePattern,
        				dumpMaxSizeInRows,
        				schemaName,
        				tableName,
        				index);
        		
        		sdw.dump();

        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Exception dumping index " + index + " and size " + dumpMaxSizeInRows);
        	e.printStackTrace();
        }
         
        return processedSourcesNumber;
    }

}
