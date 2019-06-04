package edn.cloud.sfactor.persistence.entities;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.util.Date;
import java.util.List;

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
import edn.cloud.business.dto.psf.PSFMsgDto;

@Entity
@Table(name = "EVENTLISTENER_DOC_HISTO_N", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class EventListenerDocHistory implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "EVENT_LIS_CTRLHIST_ID")
	private EventListenerCtrlHistory eventListenerCtrlHisto;

	@Basic
	@Column(name = "USER_ID_PPD_A")
	private String userIdPpd;
	
	@Basic
	@Column(name = "USER_COUNTRY")
	private String userCountry;	
	
	@Basic
	@Column(name = "USER_EXTEND_PPD_A")
	private String userExtendPpd;		
	
	@Basic
	@Column(name = "ATTACH_ID_SF")
	private String attachmentIdSF;

	@Basic
	@Column(name = "ATTACH_FILENAME_SF")
	private String attachmentFileName;

	@Basic
	@Column(name = "STATUS")
	private String status;

	@Basic
	@Column(name = "RETRIES")
	private int retries;
	
	@Basic
	@Column(name = "ID_FIELD_MAPP")
	private Long idFieldMapPpd;
	
	@Basic
	@Column(name = "ID_FIELD_MAPP_DES")
	private Long idFieldMapPpdDest;	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE_PPD_ATTACH")
	private Date startDatePpdOnAttach;		

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE")
	private Date createOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LASTUPDATE_DATE")
	private Date lastUpdateOn;

	@Basic
	@Column(name = "OBSERVATION", length = 5000)
	private String observations;
	
	//convert observations to list
	private List<PSFMsgDto> observationsList;

	@PrePersist
	protected void onCreate() {
		// this.generatedOn = new Date();
	}

	public EventListenerDocHistory() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EventListenerCtrlHistory getEventListenerCtrlHisto() {
		return eventListenerCtrlHisto;
	}

	public void setEventListenerCtrlHisto(EventListenerCtrlHistory eventListenerCtrlHisto) {
		this.eventListenerCtrlHisto = eventListenerCtrlHisto;
	}

	public String getAttachmentIdSF() {
		return attachmentIdSF;
	}

	public void setAttachmentIdSF(String attachmentIdSF) {
		this.attachmentIdSF = attachmentIdSF;
	}

	public String getAttachmentFileName() {
		return attachmentFileName;
	}

	public void setAttachmentFileName(String attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
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

	public String getObservations() {
		if (observations == null)
			observations = "";

		return observations;
	}

	public void setObservations(String observations) {
		observations = escapeHtml(observations);
		if (observations != null && observations.length() > 5000) {
			observations = observations.substring(0, 4999);
		}

		this.observations = observations;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public String getUserIdPpd() {
		return userIdPpd;
	}

	public void setUserIdPpd(String userIdPpd) {
		this.userIdPpd = userIdPpd;
	}

	public String getUserExtendPpd() {
		return userExtendPpd;
	}

	public void setUserExtendPpd(String userExtendPpd) {
		this.userExtendPpd = userExtendPpd;
	}

	public Date getStartDatePpdOnAttach() {
		return startDatePpdOnAttach;
	}

	public void setStartDatePpdOnAttach(Date startDatePpdOnAttach) {
		this.startDatePpdOnAttach = startDatePpdOnAttach;
	}	
	
	public String getCreateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", createOn);
	}

	public String getStartDatePpdOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd", startDatePpdOnAttach);
	}

	public String getLastUpdateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", lastUpdateOn);
	}
	
	public String getStatusLabel() {
		if(status!=null && !status.equals(""))
			return UtilMapping.getLabelEnumByCode(status);
			
		return status;
	}

	public Long getIdFieldMapPpd() {
		return idFieldMapPpd;
	}

	public void setIdFieldMapPpd(Long idFieldMapPpd) {
		this.idFieldMapPpd = idFieldMapPpd;
	}

	public Long getIdFieldMapPpdDest() {
		return idFieldMapPpdDest;
	}

	public void setIdFieldMapPpdDest(Long idFieldMapPpdDest) {
		this.idFieldMapPpdDest = idFieldMapPpdDest;
	}

	public String getUserCountry() {
		return userCountry;
	}

	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}
	
	public String getUserCountryString() 
	{
		if(userCountry!=null) {
			return userCountry.replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode(),",");
		}			
			
		return "";
	}

	public void setObservationsList(List<PSFMsgDto> observationsList) {
		this.observationsList = observationsList;
	}
	
	public List<PSFMsgDto> getObservationsList() 
	{
		if(this.observations!=null && !this.observations.equals("") && 
				!(this.observationsList!=null && this.observationsList.size()>0))
			this.observationsList = UtilMapping.loadPSFMsgDtoFromObservations(this.observations);
			
		return observationsList;
	}
}
