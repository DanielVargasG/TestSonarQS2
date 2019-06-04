package edn.cloud.sfactor.persistence.entities;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

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
@Table(name = "AUTHORIZATIONDOCUMENT", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class AuthorizationDocument implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "STATUS")
	private String status;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TEMPLATE_ID")
	private Template template;
	
	@Basic
	@Column(name = "USEREMP")
	private String userEmp;
	
	@Basic
	@Column(name = "DOC_ID")
	private Long documentId;	
	
	@Basic
	@Column(name = "OBSERVATION_AUX", length = 5000)
	private String observations;	
	
	@Basic
	@Column(name = "ENABLE")
	private Boolean enable;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LASTUPDATE_DATE")
	private Date lastUpdateOn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE")
	private Date createDate;	

	
	public String currentStep;
	public String generatedIdPpd;
	public String statusDocumentId;
	
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

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public Date getCreateDate() {
		return createDate;
	}
	
	public String getCreateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", createDate);
	}	

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getUserEmp() {
		return userEmp;
	}

	public void setUserEmp(String userEmp) {
		this.userEmp = userEmp;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	
	public String getStatusLabel() {
		if(status!=null && !status.equals(""))
			return UtilMapping.getLabelEnumByCode(status);
			
		return status;
	}
	
	public String getObservations() {
		if (observations == null)
			return "";

		return observations;
	}

	public void setObservations(String observations) {
		observations = escapeHtml(observations);
		if (observations != null && observations.length() > 5000) {
			observations = observations.substring(0, 4999);
		}
		this.observations = observations;
	}
	
	public String getLastUpdateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", lastUpdateOn);
	}

	public Date getLastUpdateOn() {
		return lastUpdateOn;
	}

	public void setLastUpdateOn(Date lastUpdateOn) {
		this.lastUpdateOn = lastUpdateOn;
	}

	public String getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(String currentStep) {
		this.currentStep = currentStep;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getGeneratedIdPpd() {
		return generatedIdPpd;
	}

	public void setGeneratedIdPpd(String generatedIdPpd) {
		this.generatedIdPpd = generatedIdPpd;
	}

	public String getStatusDocumentId() {
		return statusDocumentId;
	}

	public void setStatusDocumentId(String statusDocumentId) {
		this.statusDocumentId = statusDocumentId;
	}	
}
