package edn.cloud.web.soap;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "event"})
@XmlRootElement(name = "EenEvent", namespace = "http://notification.event.successfactors.com")
public class EenEvent implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "event", namespace = "http://notification.event.successfactors.com")
	private EenAlertRequestData[] event;

	public EenAlertRequestData[] getEvent() {
		return event;
	}

	public void setEvent(EenAlertRequestData[] event) {
		this.event = event;
	}
	
	
}