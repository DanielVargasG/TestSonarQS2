package edn.cloud.web.soap;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "entityKey" })
@XmlRootElement(name = "EntityKey")
public class EntityKey implements Serializable {

	private final static long serialVersionUID = 1L;

	@XmlElement(name = "entityKey", namespace = "http://notification.event.successfactors.com")
	private Param[] entityKey;

	public Param[] getEntityKey() {
		return entityKey;
	}

	public void setEntityKey(Param[] entityKey) {
		this.entityKey = entityKey;
	}

}
