package esac.archive.gacs.sl.services.statgraphs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Raul Gutierrez-Sanchez Copyright (c) 2014- European Space Agency
 */

public interface StatResourceManager {
	
	public boolean exists(String table, String type, String column);
	public long getResourceLength(String table, String type, String column) throws IOException;
	public String getResourceName(String table, String type, String column) throws IOException;
	public InputStream getResource(String table, String type, String column) throws FileNotFoundException;
	public InputStream getResourcesMetadata() throws FileNotFoundException;

}
