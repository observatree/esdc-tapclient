package esac.archive.gacs.sl.services.statgraphs;


/**
 * @author Raul Gutierrez-Sanchez Copyright (c) 2014- European Space Agency
 */
public class StatResourceFactory {

	private static StatResourceManager statResourceManager = null;

	public static synchronized StatResourceManager getStatResourceManager(String appId) {
		if(statResourceManager!=null){
			return statResourceManager;
		}
		return new DefaultStatResourceManager(appId);
	}

	public static synchronized void setStatResourceManager(StatResourceManager statResourceManager) {
		StatResourceFactory.statResourceManager = statResourceManager;
	}
	
	
	
}
