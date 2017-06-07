/*******************************************************************************
 * Copyright (C) 2017 rgutierrez
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
