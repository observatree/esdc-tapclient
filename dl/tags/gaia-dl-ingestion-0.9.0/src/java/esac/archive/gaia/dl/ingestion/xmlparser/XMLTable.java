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
