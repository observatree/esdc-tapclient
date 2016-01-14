package esac.archive.gaia.dl.ingestion.xmlparser;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class XMLIndex {
	
	@XmlTransient
	String name;

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	XMLIndexParameterList parameters;

	@XmlElement(name = "parameters")
	public XMLIndexParameterList getParameters() {
		return parameters;
	}

	public void setParameters(XMLIndexParameterList parameters) {
		this.parameters = parameters;
	}

}
