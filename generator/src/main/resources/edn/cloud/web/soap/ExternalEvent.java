package edn.cloud.web.soap;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "externalEventMeta", "events" })
@XmlRootElement(name = "ExternalEvent")
public class ExternalEvent implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "externalEventMeta")
	private EenAlertRequestMeta externalEventMeta;
	@XmlElement(name = "events")
    private EenAlertRequestData[] events;
	
	
	/*
	 * public ExternalEvent(EenAlertRequestMeta externalEventMeta,
	 * EenAlertRequestData[] events) { this.externalEventMeta =
	 * externalEventMeta; this.events = events; }
	 */

}
