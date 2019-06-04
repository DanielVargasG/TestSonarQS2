package edn.cloud.sfactor.persistence.entities;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilMapping;

@Entity
@Table(name = "LOGGER", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class LoggerAction implements IDBEntity {
	

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "USERID")
	private String userid;
	
	@Basic
	@Column(name = "STATUS")
	private String status;
	
	@Basic
	@Column(name = "MESSAGE", length = 5000)
	private String message; 

	@Basic
	@Column(name = "CODE")
	private String code;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ADDED_ON")
	private Date addedOn;

	
	@PrePersist
	protected void onCreate() {
		this.addedOn = new Date();
	}
	
	public LoggerAction() {

	}

	public Date getAddedOn() {
		return addedOn;
	}
	
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		message = escapeHtml(message);
		if (message != null && message.length() > 5000) {
			message = message.substring(0, 4999);
		}

		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUser() {
		return userid;
	}

	public void setUser(String user) {
		this.userid = user;
	}
	
	public String getStatusLabel() {
		if(status!=null && !status.equals(""))
			return UtilMapping.getLabelEnumByCode(status);
			
		return status;
	}


}
