package edn.cloud.sfactor.persistence.entities;

import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_SFUSER_BY_ID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "SFUSERS", uniqueConstraints = { @UniqueConstraint(columnNames = { "EVENT_ID" }) })
@NamedQueries({ @NamedQuery(name = GET_SFUSER_BY_ID, query = "select u from SfRecord u where u.eventId = :eventId")})
public class SfRecord implements IDBEntity {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "ENTITY_TYPE")
	private String entityType;

	@Basic
	@Column(name = "EFFECTIVE_STARTDATE")
	private String effectiveStartDate;
	
	@Basic
	@Column(name = "PUBLISHED_BY")
	private String publishedBy;

	@Basic
	@Column(name = "EVENT_ID")
	private String eventId;


	@Basic
	@Column(name = "USER_ID")
	private String userId;

	public SfRecord() {

	}

	public SfRecord(String eventId) {
		this.eventId = eventId;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getPublishedBy() {
		return publishedBy;
	}

	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
