package edn.cloud.web.soap;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "EenAlertResponsePayload" })
@XmlRootElement(name = "ExternalEventResponse")
public class ExternalEventResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "responsePayload", namespace = "http://notification.event.successfactors.com")
	private EenAlertResponsePayload responsePayload;

}
