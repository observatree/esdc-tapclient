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
package esac.archive.gaia.dl.ingestion.xmlparser;

import javax.xml.bind.annotation.*;

public class XMLTable {

	@XmlAttribute(name = "parent-name")
	public String getParentName() {
		return parentName;
	}
	
	public void setParentName(String name) {
		this.parentName = name;
	}
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name = "parameters")
	public XMLParameters getParameterList() {
		return parameterList;
	}
	public void setParameterList(XMLParameters parameterList) {
		this.parameterList = parameterList;
	}
	
	@XmlElement(name = "primary-key")
	public XMLPrimaryKey getPk() {
		return pk;
	}

	public void setPk(XMLPrimaryKey pk) {
		this.pk = pk;
	}
	
	@XmlElement(name = "indexes")
	public XMLIndexesList getIndexesList() {
		return indexesList;
	}

	public void setIndexesList(XMLIndexesList indexesList) {
		this.indexesList = indexesList;
	}
	
	@XmlTransient
	String name;
	@XmlTransient
	String parentName;
	@XmlTransient
	String description;
	@XmlTransient
	XMLParameters parameterList;
	@XmlTransient
	XMLPrimaryKey pk;
	@XmlTransient
	XMLIndexesList indexesList;	
}
