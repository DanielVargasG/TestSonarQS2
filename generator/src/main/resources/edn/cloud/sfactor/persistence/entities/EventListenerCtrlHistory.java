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
@Table(name = "EVENTLISTENER_CTRL_HISTO_N", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class EventListenerCtrlHistory implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "EVENT_LIS_PARAM_ID")
	private EventListenerParam eventListenerParam;
	
	@OneToMany(mappedBy = "eventListenerCtrlHisto", fetch= FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.REMOVE})  
	private List<EventListenerDocHistory> eventListenerDocsHisto = new ArrayList<>();

	@Basic
	@Column(name = "ORIGINAL_EVENT_ID")
	private Long idOriginalEvent;

	@Basic
	@Column(name = "STATUS")
	private String status;
	
	@Basic
	@Column(name = "REF_MASS_LOAD")
	private String refMassLoad;
	
	@Basic
	@Column(name = "REF_MASS_LOAD_ID")
	private Long refMassLoadId;		

	@Basic
	@Column(name = "RETRIES")
	private int retries;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE")
	private Date createOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LASTUPDATE_DATE")
	private Date lastUpdateOn;

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
	@Column(name = "SEQ_NUMBER_PPD")
	private String seqNumberPpd;

	@Basic
	@Column(name = "OBSERVATION", length = 5000)
	private String observations;
	
	//convert observations to list
	private List<PSFMsgDto> observationsList;

	@PrePersist
	protected void onCreate() {
		// this.generatedOn = new Date();
	}

	public EventListenerCtrlHistory() {

	}

	public EventListenerCtrlHistory(Long docId) {
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
	
	public String getStatusLabel() {
		if(status!=null && !status.equals(""))
			return UtilMapping.getLabelEnumByCode(status);
			
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
			observations = "";

		return observations;
	}

	public void setObservations(String observations) 
	{
		observations = escapeHtml(observations);
		if (observations != null && observations.length() > 5000) {
			observations = observations.substring(0, 4999);
		}
		
		this.observations = observations;
	}

	public String getCreateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", createOn);
	}

	public String getStartDatePpdOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", startDatePpdOn);
	}

	public String getLastUpdateOnString() {
		return UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS", lastUpdateOn);
	}

	public EventListenerParam getEventListenerParam() {
		return eventListenerParam;
	}

	public void setEventListenerParam(EventListenerParam eventListenerParam) {
		this.eventListenerParam = eventListenerParam;
	}

	public Long getIdOriginalEvent() {
		return idOriginalEvent;
	}

	public void setIdOriginalEvent(Long idOriginalEvent) {
		this.idOriginalEvent = idOriginalEvent;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public String getRefMassLoad() {
		return refMassLoad;
	}

	public void setRefMassLoad(String refMassLoad) {
		this.refMassLoad = refMassLoad;
	}

	public Long getRefMassLoadId() {
		return refMassLoadId;
	}

	public void setRefMassLoadId(Long refMassLoadId) {
		this.refMassLoadId = refMassLoadId;
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
	
	public List<PSFMsgDto> getObservationsList() 
	{
		if(this.observations!=null && !this.observations.equals("") && 
				!(this.observationsList!=null && this.observationsList.size()>0))
			this.observationsList = UtilMapping.loadPSFMsgDtoFromObservations(this.observations);
			
		return observationsList;
	}

	public void setObservationsList(List<PSFMsgDto> observationsList) {
		this.observationsList = observationsList;
	}	
}
