package edn.cloud.web.soap;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "eventId", "entityType", "effectiveStartDate", "publishedAt", "publishedBy", "repost", "entityKeys", "params" })
@XmlRootElement(name = "EenAlertRequestData", namespace = "http://notification.event.successfactors.com")
public class EenAlertRequestData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	@XmlElement(name = "eventId", namespace = "http://notification.event.successfactors.com")
	private String eventId;
	@XmlElement(name = "entityType", namespace = "http://notification.event.successfactors.com")
	private String entityType;
	@XmlElement(name = "effectiveStartDate", namespace = "http://notification.event.successfactors.com")
	private String effectiveStartDate;
	@XmlElement(name = "publishedAt", namespace = "http://notification.event.successfactors.com")
	private Long publishedAt;
	@XmlElement(name = "publishedBy", namespace = "http://notification.event.successfactors.com")
	private String publishedBy;
	@XmlElement(name = "repost", namespace = "http://notification.event.successfactors.com")
	private Boolean repost;
	@XmlElement(name = "entityKeys", namespace = "http://notification.event.successfactors.com")
	private EntityKey entityKeys;
	@XmlElement(name = "params", namespace = "http://notification.event.successfactors.com")
	private EenParam params;
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public String getEffectiveStartDate() {
		return effectiveStartDate;
	}
	public void setEffectiveStartDate(String effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}
	public Long getPublishedAt() {
		return publishedAt;
	}
	public void setPublishedAt(Long publishedAt) {
		this.publishedAt = publishedAt;
	}
	public String getPublishedBy() {
		return publishedBy;
	}
	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
	}
	public Boolean getRepost() {
		return repost;
	}
	public void setRepost(Boolean repost) {
		this.repost = repost;
	}
	public EenParam getParams() {
		return params;
	}
	public void setParams(EenParam params) {
		this.params = params;
	}
	public EntityKey getEntityKeys() {
		return entityKeys;
	}
	public void setEntityKeys(EntityKey entityKeys) {
		this.entityKeys = entityKeys;
	}
	
	
}
