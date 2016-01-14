package esac.archive.gaia.dl.ingestion.xmlparser;

import java.util.List;

import javax.xml.bind.annotation.*;

public class XMLUcdList {
	
	@XmlElement(name = "ucd")
	public List<XMLUcd> getUcdElements() {
		return ucdElements;
	}

	public void setUcdElements(List<XMLUcd> ucdElements) {
		this.ucdElements = ucdElements;
	}

	@XmlTransient
	List<XMLUcd> ucdElements;
}
