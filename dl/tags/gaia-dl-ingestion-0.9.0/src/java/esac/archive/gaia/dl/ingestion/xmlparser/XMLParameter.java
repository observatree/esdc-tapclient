package esac.archive.gaia.dl.ingestion.xmlparser;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class XMLParameter {

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "unit")
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	@XmlAttribute(name = "type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlElement(name = "symbol")
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlElement(name = "detailed-description")
	public String getDetailedDescription() {
		return detailedDescription;
	}
	public void setDetailedDescription(String detailedDescription) {
		this.detailedDescription = detailedDescription;
	}
	@XmlElement(name = "ucd-list")
	public XMLUcdList getUcdList() {
		return ucdList;
	}
	public void setUcdList(XMLUcdList ucdList) {
		this.ucdList = ucdList;
	}
	@XmlElement(name = "utype")
	public String getUtype() {
		return utype;
	}
	public void setUtype(String utype) {
		this.utype = utype;
	}
	@XmlElement(name = "default-value")
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	@XmlElement(name = "hyperlink")
	public String getHyperlink() {
		return hyperlink;
	}
	public void setHyperlink(String hyperlink) {
		this.hyperlink = hyperlink;
	}
	
	@XmlTransient
	String name;
	@XmlTransient
	String unit;
	@XmlTransient
	String type;
	@XmlTransient
	String symbol;
	@XmlTransient
	String description;
	@XmlTransient
	String detailedDescription;
	@XmlTransient
	XMLUcdList ucdList;
	@XmlTransient
	String utype;
	@XmlTransient
	String defaultValue;
	@XmlTransient
	String hyperlink;
}
