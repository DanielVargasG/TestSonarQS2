package edn.cloud.sfactor.persistence.entities;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilMapping;

@Entity
@Table(name = "AUTHORIZATIONDEATILS", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class AuthorizationDetails implements IDBEntity{

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "STATUS")
	private String status;
	
	@Basic
	@Column(name = "USERID")
	private String userId;
	
	@Basic
	@Column(name = "EMAIL")
	private String email;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LASTUPDATE_DATE")
	private Date lastUpdateOn;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AUTHDOCUMENT_ID")
	private AuthorizationDocument authDocument;
	
	@Basic
	@Column(name = "ORDER_AUTHO")
	private int orderAutho;
	
	@Basic
	@Column(name = "IS_CURRENT_AUTHO")
	private Boolean isCurrentAutho;
	
	@Basic
	@Column(name = "Comment")
	private String comment;
	
	@Basic
	@Column(name = "ENABLE")
	private Boolean enable;
	
	@Basic
	@Column(name = "CURRENT_AUTHO")
	private String currentAutho;
		
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getLastUpdateOn() {
		return lastUpdateOn;
	}

	public void setLastUpdateOn(Date lastUpdateOn) {
		this.lastUpdateOn = lastUpdateOn;
	}

	public AuthorizationDocument getAuthDocument() {
		return authDocument;
	}

	public void setAuthDocument(AuthorizationDocument authDocument) {
		this.authDocument = authDocument;
	}
	
	public int getOrderAutho() {
		return orderAutho;
	}

	public void setOrderAutho(int orderAutho) {
		this.orderAutho = orderAutho;
	}

	public String getCreateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm", lastUpdateOn);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getEnable() 
	{
		if(enable!=null)
			return enable;
		
		return true;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	
	public String getStatusLabel() {
		if(status!=null && !status.equals(""))
			return UtilMapping.getLabelEnumByCode(status);
			
		return status;
	}

	public String getCurrentAutho() {
		return currentAutho;
	}

	public void setCurrentAutho(String currentAutho) {
		this.currentAutho = currentAutho;
	}

	public Boolean getIsCurrentAutho() {
		if(isCurrentAutho!=null)
			return isCurrentAutho;
		
		return false;
	}

	public void setIsCurrentAutho(Boolean isCurrentAutho) {
		this.isCurrentAutho = isCurrentAutho;
	}	
	
	
}
