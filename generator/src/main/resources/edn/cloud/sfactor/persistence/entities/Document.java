package edn.cloud.sfactor.persistence.entities;

import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_DOCS;
import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_DOCS_BY_USER_ID;
import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_DOC_BY_ID;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilMapping;

@Entity
@Table(name = "DOCUMENTS", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
@NamedQueries({ @NamedQuery(name = GET_DOC_BY_ID, query = "select u from Document u where u.id = :id"),
@NamedQuery(name = GET_DOCS_BY_USER_ID, query = "select u from Employee u where u.email = :email"),
@NamedQuery(name = GET_DOCS, query = "select u from Document u") }) 
public class Document implements IDBEntity {
	
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TEMPLATE_ID")
	private Template templateId;
	
	@Basic
	@Column(name = "EFFECTIVE_DATE")
	private String effectiveDate;

	@Basic
	@Column(name = "STATUS")
	private String status;
	
	@Basic
	@Column(name = "STATUS_SIGNATURE")
	private String statusSignature;		

	@Basic
	@Column(name = "OUTPUT_FORMAT")
	private String outputFormat;
	
	@Basic
	@Column(name = "TARGET_USER")
	private String targetUser;
	
	@Basic
	@Column(name = "TARGET_USER_FNAME")
	private String targetUser_firstName;
	
	@Basic
	@Column(name = "TARGET_USER_LNAME")
	private String targetUser_lastName;
	
	@Basic
	@Column(name = "SOURCE")
	private String source;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	private Employee owner;

	@Column(name = "USER_ID", insertable = false, updatable = false)
	private Long ownerId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE")
	private Date createOn;
	
	@Basic
	@Column(name = "ARCHIVE")
	private String archive;
	
	public Document() {
	}
	
	public Document(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Template getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Template templateId) {
		this.templateId = templateId;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}
	
	public Employee getOwner() {
		return owner;
	}

	public void setOwner(Employee owner) {
		this.owner = owner;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getTargetUser(){
		return UtilMapping.decrypt(targetUser);		
	}

	public void setTargetUser(String targetUser) {
		this.targetUser = UtilMapping.encrypt(targetUser);
	}

	public String getTargetUser_firstName() {
		return UtilMapping.decrypt(targetUser_firstName);
	}

	public void setTargetUser_firstName(String targetUser_firstName) {
		this.targetUser_firstName = UtilMapping.encrypt(targetUser_firstName);
	}

	public String getTargetUser_lastName() {
		return UtilMapping.decrypt(targetUser_lastName);
	}

	public void setTargetUser_lastName(String targetUser_lastName) {
		this.targetUser_lastName = targetUser_lastName;
	}

	public String getStatusSignature() {
		return statusSignature;
	}

	public void setStatusSignature(String statusSignature) {
		this.statusSignature = statusSignature;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getCreateOn() {
		return createOn;
	}

	public void setCreateOn(Date createOn) {
		this.createOn = createOn;
	}
	
	public String getCreateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", createOn);
	}

	public String getArchive() {
		return archive;
	}

	public void setArchive(String archive) {
		this.archive = archive;
	}
	
	public String getStatusLabel() {
		if(status!=null && !status.equals(""))
			return UtilMapping.getLabelEnumByCode(status);
			
		return status;
	}
}
