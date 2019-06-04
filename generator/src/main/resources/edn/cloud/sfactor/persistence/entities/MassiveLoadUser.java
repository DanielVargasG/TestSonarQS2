package edn.cloud.sfactor.persistence.entities;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilMapping;

@Entity
@Table(name = "MASSIVE_LOAD_USER", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class MassiveLoadUser implements IDBEntity{
	
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "nameLoad")
	private String nameLoad;	
	
	@Basic
	@Column(name = "STATUS")
	private String status;
	
	@Basic
	@Column(name = "LOAD_REG")
	private Long loadReg;	
	
	@Basic
	@Column(name = "TOTAL_REG")
	private Long totalReg;
	
	@Basic
	@Column(name = "ATTACH_TYPES")
	private String attachTypes;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_ON")
	private Date createOn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATE_ON")
	private Date lastUpdateOn;
	
	@Basic
	@Column(name = "CREATE_USER")
	private String createUser;
	
	@Basic
	@Column (name = "OBSERVATIONS")
	private String observations;

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

	public Date getCreateOn() {
		return createOn;
	}

	public void setCreateOn(Date createOn) {
		this.createOn = createOn;
	}

	public Date getLastUpdateOn() {
		return lastUpdateOn;
	}

	public void setLastUpdateOn(Date lastUpdateOn) {
		this.lastUpdateOn = lastUpdateOn;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
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
	
	public String getUserCreated() {
		return UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), createOn);
	}

	public String getNameLoad() {
		return nameLoad;
	}

	public void setNameLoad(String nameLoad) {
		this.nameLoad = nameLoad;
	}	
	
	public String getCreateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", createOn);
	}	

	public String getLastUpdateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", lastUpdateOn);
	}

	public Long getLoadReg() {
		return loadReg;
	}

	public void setLoadReg(Long loadReg) {
		this.loadReg = loadReg;
	}

	public Long getTotalReg() {
		return totalReg;
	}

	public void setTotalReg(Long totalReg) {
		this.totalReg = totalReg;
	}

	public String getAttachTypes() {
		return attachTypes;
	}

	public void setAttachTypes(String attachTypes) {
		this.attachTypes = attachTypes;
	}
	
	public String getStatusLabel() {
		if(status!=null && !status.equals(""))
			return UtilMapping.getLabelEnumByCode(status);
			
		return status;
	}
}