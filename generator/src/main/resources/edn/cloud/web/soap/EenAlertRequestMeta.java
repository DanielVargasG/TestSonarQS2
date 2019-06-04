package edn.cloud.web.soap;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "externalEventId", "type", "publishedAt", "publishedBy", "effective", "repost" })
@XmlRootElement(name = "EenAlertRequestMeta", namespace = "http://notification.event.successfactors.com")
public class EenAlertRequestMeta implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "externalEventId", namespace = "http://notification.event.successfactors.com")
	public String externalEventId;
	@XmlElement(name = "type", namespace = "http://notification.event.successfactors.com")
	public String type;
	@XmlElement(name = "publishedAt", namespace = "http://notification.event.successfactors.com")
	public String publishedAt;
	@XmlElement(name = "publishedBy", namespace = "http://notification.event.successfactors.com")
	public String publishedBy;
	@XmlElement(name = "effective", namespace = "http://notification.event.successfactors.com")
	public String effective;
	@XmlElement(name = "repost", namespace = "http://notification.event.successfactors.com")
	public Boolean repost;

}
