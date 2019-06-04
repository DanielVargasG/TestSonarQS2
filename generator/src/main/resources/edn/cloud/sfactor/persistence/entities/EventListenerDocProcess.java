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
@Table(name = "EVENTLISTENER_DOC_PROCS_N", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class EventListenerDocProcess implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "EVENT_LIS_CTRL_ID")
	private EventListenerCtrlProcess eventListenerCtrlProc;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FIELD_MAPPING_SOURCE")
	private FieldsMappingPpd fieldMapPpd;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FIELD_MAPPING_DESTI")
	private FieldsMappingPpd fieldMapPpdDest;	
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ID_TEMPLATE")
	private Template template;	
	
	@Basic
	@Column(name = "ID_JOB_PROCESS")
	private Long idJobProcess;
	
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
	@Column(name = "USER_ID_OTHER_PLAT")
	private String userIdOtherPlat;	
		
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
	@Column(name = "LASTUPDATE_USER")
	private String lastUpdateUser;

	@Basic
	@Column(name = "OBSERVATION", length = 5000)
	private String observations;
	
	private Boolean isEdit = true;
	private String retriesInfo;
	//convert observations to list
	private List<PSFMsgDto> observationsList;
	

	@PrePersist
	protected void onCreate() {
		// this.generatedOn = new Date();
	}

	public EventListenerDocProcess() {

	}

	public EventListenerDocProcess(Long docId) {
		this.id = docId;
	}

	@Override
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

	public String getCreateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", createOn);
	}

	public String getStartDatePpdOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd", startDatePpdOnAttach);
	}

	public String getLastUpdateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", lastUpdateOn);
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

	public String getObservations() 
	{
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

	public EventListenerCtrlProcess getEventListenerCtrlProc() {
		return eventListenerCtrlProc;
	}

	public void setEventListenerCtrlProc(EventListenerCtrlProcess eventListenerCtrlProc) {
		this.eventListenerCtrlProc = eventListenerCtrlProc;
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

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public FieldsMappingPpd getFieldMapPpd() {
		return fieldMapPpd;
	}

	public void setFieldMapPpd(FieldsMappingPpd fieldMapPpd) {
		this.fieldMapPpd = fieldMapPpd;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public String getRetriesInfo() {
		return retriesInfo;
	}

	public void setRetriesInfo(String retriesInfo) {
		this.retriesInfo = retriesInfo;
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

	public Long getIdJobProcess() {
		return idJobProcess;
	}

	public void setIdJobProcess(Long idJobProcess) {
		this.idJobProcess = idJobProcess;
	}

	public String getUserIdOtherPlat() {
		return userIdOtherPlat;
	}

	public void setUserIdOtherPlat(String userIdOtherPlat) {
		this.userIdOtherPlat = userIdOtherPlat;
	}

	public FieldsMappingPpd getFieldMapPpdDest() {
		return fieldMapPpdDest;
	}

	public void setFieldMapPpdDest(FieldsMappingPpd fieldMapPpdDest) {
		this.fieldMapPpdDest = fieldMapPpdDest;
	}
	
	public String getStatusLabel() {
		if(status!=null && !status.equals(""))
			return UtilMapping.getLabelEnumByCode(status);
			
		return status;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public String getUserCountry() {
		return userCountry;
	}
	
	public String getUserCountryString() 
	{
		if(userCountry!=null) {
			return userCountry.replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode(),",");
		}			
			
		return "";
	}

	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
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