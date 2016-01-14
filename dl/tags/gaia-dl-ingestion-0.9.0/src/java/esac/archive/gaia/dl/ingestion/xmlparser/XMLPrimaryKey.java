package esac.archive.gaia.dl.ingestion.xmlparser;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class XMLPrimaryKey {

	@XmlTransient
	String name;

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String p) {
		this.name = p;
	}

	@XmlTransient
	XMLPrimaryKeyParametersList parameters;

	@XmlElement(name = "parameters")
	public XMLPrimaryKeyParametersList getParameters() {
		return parameters;
	}

	public void setParameters(XMLPrimaryKeyParametersList p) {
		this.parameters = p;
	}
}
