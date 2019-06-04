package edn.cloud.sfactor.persistence.entities;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "EVENTLISTENER_CTRL_PROCS", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class EventListenerCtrlProcess implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "EVENT_LIS_PARAM_ID")
	private EventListenerParam eventListenerParam;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_MASSIVE_LOAD")
	private MassiveLoadUser fkMassiveLoad;	
		
	@OneToMany(mappedBy = "eventListenerCtrlProc", fetch= FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.REMOVE})  
	private List<EventListenerDocProcess> eventListenerDocs = new ArrayList<>();

	@Basic
	@Column(name = "STATUS")
	private String status;

	@Basic
	@Column(name = "RETRIES")
	private int retries = 0;
	
	@Basic
	@Column(name = "IS_SEARCH_ATTACH_AFTER")
	private Boolean isSearchAttachAfterProc=false;	
	
	@Basic
	@Column(name = "IS_EMPL_VALIDATE")
	private Boolean isEmployeeValidate=true;	
	
	@Basic
	@Column(name = "IS_PROCES_AGAIN")
	private Boolean isProcessAgain=false;	
	
	@Basic
	@Column(name = "ID_JOB_PROCESS")
	private Long idJobProcess;	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE")
	private Date createOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LASTUPDATE_DATE_INIT")
	private Date lastUpdateInit;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LASTUPDATE_DATE")
	private Date lastUpdateOn;

	@Basic
	@Column(name = "LASTUPDATE_USER")
	private String lastUpdateUser;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE_PPD")
	private Date startDatePpdOn;

	@Basic
	@Column(name = "USER_ID_PPD")
	private String userIdPpd;
	
	@Basic
	@Column(name = "USER_COUNTRY")
	private String userCountry;	
	
	@Basic
	@Column(name = "USER_EXTEND_PPD")
	private String userExtendPpd;

	@Basic
	@Column(name = "SEQ_NUMBER_PPD")
	private String seqNumberPpd;

	@Basic
	@Column(name = "OBSERVATION_AUX", length = 5000)
	private String observations;
	
	private String retriesInfo;
	private Boolean isEdit = true;
	private Long eventLCtrlHistoryId;
	
	//convert observations to list
	private List<PSFMsgDto> observationsList;

	@PrePersist
	protected void onCreate() {
		// this.generatedOn = new Date();
	}

	public EventListenerCtrlProcess() {
		this.observations = "";
	}

	public EventListenerCtrlProcess(Long docId) {
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
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd", startDatePpdOn);
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

	public Date getStartDatePpdOn() {
		return startDatePpdOn;
	}

	public void setStartDatePpdOn(Date startDatePpdOn) {
		this.startDatePpdOn = startDatePpdOn;
	}

	public String getUserIdPpd() {
		return userIdPpd;
	}

	public void setUserIdPpd(String userIdPpd) {
		this.userIdPpd = userIdPpd;
	}

	public String getSeqNumberPpd() {
		return seqNumberPpd;
	}

	public void setSeqNumberPpd(String seqNumberPpd) {
		this.seqNumberPpd = seqNumberPpd;
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

	public EventListenerParam getEventListenerParam() {
		return eventListenerParam;
	}

	public void setEventListenerParam(EventListenerParam eventListenerParam) {
		this.eventListenerParam = eventListenerParam;
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

	public String getRetriesInfo() {
		return retriesInfo;
	}

	public void setRetriesInfo(String retriesInfo) {
		this.retriesInfo = retriesInfo;
	}

	public String getUserExtendPpd() {
		return userExtendPpd;
	}

	public void setUserExtendPpd(String userExtendPpd) {
		this.userExtendPpd = userExtendPpd;
	}

	public Long getIdJobProcess() {
		return idJobProcess;
	}

	public void setIdJobProcess(Long idJobProccess) {
		this.idJobProcess = idJobProccess;
	}

	public MassiveLoadUser getFkMassiveLoad() {
		return fkMassiveLoad;
	}

	public void setFkMassiveLoad(MassiveLoadUser fkMassiveLoad) {
		this.fkMassiveLoad = fkMassiveLoad;
	}

	public Boolean getIsSearchAttachAfterProc() {
		if(isSearchAttachAfterProc!=null)
			return isSearchAttachAfterProc;
		else
			return false;
	}

	public void setIsSearchAttachAfterProc(Boolean isSearchAttachAfterProc) {
		this.isSearchAttachAfterProc = isSearchAttachAfterProc;
	}

	public Boolean getIsEmployeeValidate() {
		if(isEmployeeValidate!=null)
			return isEmployeeValidate;
		else
			return true;
	}

	public void setIsEmployeeValidate(Boolean isEmployeeValidate) {
		this.isEmployeeValidate = isEmployeeValidate;
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

	public Long getEventLCtrlHistoryId() {
		return eventLCtrlHistoryId;
	}

	public void setEventLCtrlHistoryId(Long eventLCtrlHistoryId) {
		this.eventLCtrlHistoryId = eventLCtrlHistoryId;
	}

	public Boolean getIsProcessAgain() 
	{
		if(isProcessAgain!=null)
			return isProcessAgain;
		else
			return false;
	}

	public void setIsProcessAgain(Boolean isProcessAgain) {
		this.isProcessAgain = isProcessAgain;
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

	public List<PSFMsgDto> getObservationsList() {
		return observationsList;
	}

	public void setObservationsList(List<PSFMsgDto> observationsList) {
		this.observationsList = observationsList;
	}

	public Date getLastUpdateInit() {
		return lastUpdateInit;
	}

	public void setLastUpdateInit(Date lastUpdateInit) {
		this.lastUpdateInit = lastUpdateInit;
	}
	
	public String getTimeProcessing()
	{
		if(lastUpdateInit!=null && lastUpdateOn!=null)
		{
			if(lastUpdateInit.compareTo(lastUpdateOn)<0)
			{
				try
				{
					return ((lastUpdateOn.getTime() - lastUpdateInit.getTime())*0.001) +"";
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		
		return "";
	}	
}
