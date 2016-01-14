package esac.archive.gaia.dl.ingestion.xmlparser;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class XMLPrimaryKeyParametersList {

	@XmlTransient
	List<XMLPrimaryKeyParameter> parameters;

	@XmlElement(name = "parameter")
	public List<XMLPrimaryKeyParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<XMLPrimaryKeyParameter> parameters) {
		this.parameters = parameters;
	}
}
