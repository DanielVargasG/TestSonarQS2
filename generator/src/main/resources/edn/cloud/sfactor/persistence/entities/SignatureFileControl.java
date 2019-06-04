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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilMapping;

@Entity
@Table(name = "SIGNATURE_FILE_CTRL", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class SignatureFileControl implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GENERATED_ID", referencedColumnName = "ID")
	private Generated generated;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "GENERATED_DATE")
	private Date generatedOn;
	
	@Basic
	@Column(name = "STATUS")
	private String status;
	
	@Basic
	@Column(name = "idPpdSignCtrl")
	private String idPpdSignCtrl;
	
	@Basic
	@Column(name = "IS_WITHCALLBACK")
	private Boolean isWithCallBack = false;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LASTUPDATE_DATE")
	private Date lastUpdateOn;	
	
	@PrePersist
	protected void onCreate() {
		//this.generatedOn = new Date();
	}
	
	@Basic
	@Column(name = "OBSERVATION", length = 5000)
	private String observations;

	public SignatureFileControl() {

	}

	public SignatureFileControl(Long docId) {
		this.id = docId;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getGeneratedDateOn() {
		return generatedOn;
	}
	
	public String getGeneratedOn() {
		return generatedOn.toString();
	}

	public Generated getGenerated() {
		return generated;
	}

	public void setGenerated(Generated generated) {
		this.generated = generated;
	}

	public void setGeneratedOn(Date generatedOn) {
		this.generatedOn = generatedOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIdPpdSignCtrl() {
		return idPpdSignCtrl;
	}

	public void setIdPpdSignCtrl(String idPpdSignCtrl) {
		this.idPpdSignCtrl = idPpdSignCtrl;
	}
	
	public String getObservations() {
		if (observations == null)
			observations = "";

		return observations;
	}

	public void setObservations(String observations) {
		if (observations != null && observations.length() > 5000) {
			observations = observations.substring(0, 4999);
		}

		this.observations = observations;
	}

	public Boolean getIsWithCallBack() {
		return isWithCallBack;
	}

	public void setIsWithCallBack(Boolean isWithCallBack) {
		this.isWithCallBack = isWithCallBack;
	}
	
	public Date getLastUpdateOn() {
		return lastUpdateOn;
	}

	public void setLastUpdateOn(Date lastUpdateOn) {
		this.lastUpdateOn = lastUpdateOn;
	}
	
	public String getLastUpdateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", lastUpdateOn);
	}
	
	public String getStatusLabel() {
		if(status!=null && !status.equals(""))
			return UtilMapping.getLabelEnumByCode(status);
			
		return status;
	}
}
