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
package esac.archive.gacs.sl.services.admin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import esavo.uws.storage.UwsStorage;

public interface ManagerHandler {
	
	/**
	 * Returns the action associated to this handler
	 * @return
	 */
	public String getActionIdentifier();
	
	/**
	 * Returns 'true' if the handler can handle the action.
	 * @param action
	 * @return
	 */
	public boolean canHandle(String action);
	
	/**
	 * Handles the action.
	 * @param parameters
	 * @param response
	 * @param dbConn
	 */
	public void handle(Map<String,String> parameters, HttpServletResponse response, UwsStorage uwsStorage) throws IOException ;

}
