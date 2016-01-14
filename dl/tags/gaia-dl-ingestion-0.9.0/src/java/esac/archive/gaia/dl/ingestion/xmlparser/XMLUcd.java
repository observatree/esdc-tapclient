package esac.archive.gaia.dl.ingestion.xmlparser;
import javax.xml.bind.annotation.*;

public class XMLUcd {
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	String name;
}
