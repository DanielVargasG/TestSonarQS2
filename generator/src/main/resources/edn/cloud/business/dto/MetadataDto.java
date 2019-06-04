package edn.cloud.business.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Metadata")
public class MetadataDto {

	String name;

	public MetadataDto(String name) {
		this.name = name;
	}
	
	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
