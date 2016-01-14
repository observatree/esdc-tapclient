package esac.archive.gaia.dl.ingestion.xmlparser;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class XMLIndexesList {

	@XmlElement(name = "index")
	public List<XMLIndex> getIndexElements() {
		return indexElement;
	}

	public void setIndexElements(List<XMLIndex> index) {
		this.indexElement = index;
	}

	@XmlTransient
	List<XMLIndex> indexElement;
}
