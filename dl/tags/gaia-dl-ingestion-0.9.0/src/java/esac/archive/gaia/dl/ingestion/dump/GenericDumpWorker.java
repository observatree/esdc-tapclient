package esac.archive.gaia.dl.ingestion.dump;

public class GenericDumpWorker {
	
	protected static int numberFolder = 0;
	protected static int numberFile = 0;

	protected String dumpOutputPath; 
	protected String dumpFilePattern;
	protected long dumpMaxSizeInRows;
	protected String schemaName;
	protected String tableName;
	protected int index;

}
