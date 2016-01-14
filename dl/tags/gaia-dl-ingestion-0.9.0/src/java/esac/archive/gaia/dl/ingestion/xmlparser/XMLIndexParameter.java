package esac.archive.gaia.dl.ingestion.xmlparser;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class XMLIndexParameter {
	@XmlTransient
	String name;

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
