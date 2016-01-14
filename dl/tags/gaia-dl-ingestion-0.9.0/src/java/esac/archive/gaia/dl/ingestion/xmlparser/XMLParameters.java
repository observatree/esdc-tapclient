package esac.archive.gaia.dl.ingestion.xmlparser;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class XMLParameters {

	@XmlElement(name = "parameter")
	public List<XMLParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<XMLParameter> parameters) {
		this.parameters = parameters;
	}

	@XmlTransient
	List<XMLParameter> parameters;
}
