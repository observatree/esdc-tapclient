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
package esac.archive.gacs.sl.services.statgraphs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import esavo.tap.TAPException;
import esavo.uws.UwsException;
import esavo.uws.owner.UwsJobOwner;

/**
 * @author Raul Gutierrez-Sanchez Copyright (c) 2014- European Space Agency
 */

public interface StatResourceManager {
	
	public boolean hasAccess(UwsJobOwner user, String table, String column) throws UwsException, TAPException;
	public boolean exists(UwsJobOwner user, String table, String type, String column) throws UwsException, TAPException;
	public long getResourceLength(UwsJobOwner user, String table, String type, String column) throws IOException, UwsException, TAPException;
	public String getResourceName(UwsJobOwner user, String table, String type, String column) throws IOException, UwsException, TAPException;
	public InputStream getResource(UwsJobOwner user, String table, String type, String column) throws FileNotFoundException,UwsException, TAPException;
	public InputStream getResourcesMetadata() throws FileNotFoundException;

}
