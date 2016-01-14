package esac.archive.gaia.dl.ingestion.xmlparser;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class XMLIndexParameterList {
	@XmlTransient
	List<XMLIndexParameter> parameters;

	@XmlElement(name = "parameter")
	public List<XMLIndexParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<XMLIndexParameter> parameters) {
		this.parameters = parameters;
	}

}
