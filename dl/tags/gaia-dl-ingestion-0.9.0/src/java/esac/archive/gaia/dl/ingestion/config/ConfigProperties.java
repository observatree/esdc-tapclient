/*******************************************************************************
 * Copyright (C) 2017 European Space Agency
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package esac.archive.gaia.dl.ingestion.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigProperties {

	private static boolean initialized = false;
	private static Properties prop;
	private static String pathToConfigFile = "";
	private static final Logger logger = Logger.getLogger(ConfigProperties.class.getName());

	private static ConfigProperties instance = null;
	
	public static ConfigProperties getInstance() {
		if (!initialized) {
			logger.log(Level.SEVERE, "Error: Init() method must be called first.");
			System.exit(1);
		}

		if (instance == null) {
			instance = new ConfigProperties();
		}
		return instance;
	}
	
	public static void Init(String path) {
		logger.log(Level.INFO, "Creating properties file");
		prop = new Properties();
		pathToConfigFile = path;
		initialized = true;
	}
	
	private ConfigProperties(){
		try {
			logger.log(Level.INFO, "props " + prop);
			InputStream stream = getClass().getClassLoader().getResourceAsStream(pathToConfigFile);
			logger.log(Level.INFO, "Loading properties file '" + pathToConfigFile + "': " + stream);
			if (stream != null) {
				prop.load(stream);
			}
			else {
				throw new FileNotFoundException("property file " + pathToConfigFile + " not found in the classpath");
			}
			logger.log(Level.INFO, "Properties file '" + pathToConfigFile + "' loaded");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error loading properties file '" + pathToConfigFile + "'");
			e.printStackTrace();
		}

	}
	public String getProperty(String property) {
		String result = null;
		result = prop.getProperty(property);
		if (result == null || result.equals("")) {
			logger.log(Level.SEVERE, "Error reading properties file '" + pathToConfigFile + "'. There should be a parameter named " + property);
			System.exit(1);
		}
		return result;
	}

}
