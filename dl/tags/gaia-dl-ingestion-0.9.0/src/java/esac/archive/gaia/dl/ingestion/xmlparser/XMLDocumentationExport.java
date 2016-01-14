package esac.archive.gaia.dl.ingestion.xmlparser;

import java.util.List;
import javax.xml.bind.annotation.*;

@XmlRootElement(name="documentation-export")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLDocumentationExport {

	@XmlElement(name = "table")
	public List<XMLTable> getTables() {
		return tables;
	}

	public void setTables(List<XMLTable> tables) {
		this.tables = tables;
	}

	@XmlTransient
	List<XMLTable> tables;
}
