package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "PARAM_EVENTLISTENERS", uniqueConstraints = { @UniqueConstraint(columnNames = { "EVENT_ID" }) })
@NamedQueries({ @NamedQuery(name = DBQueries.GET_EVENT_BY_ID, query = "select u from EventListenerParam u where u.eventId = :eventId") })
public class EventListenerParam implements IDBEntity {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "EVENT_ID")
	private String eventId;
	
	@Basic
	@Column(name = "IS_UPDATE")
	private Boolean isUpdate=true;	
	
	@Basic
	@Column(name = "IS_HIRE")
	private Boolean isHire=false;	
	
	@Basic
	@Column(name = "IS_TERMINATE")
	private Boolean isTerminate=false;	

	@Basic
	@Column(name = "IS_ENABLED")
	private Boolean isEnabled=true;
	
	@Basic
	@Column(name = "IS_PROCESS_AGAIN")
	private Boolean isProcessAgain=false;	

	@Basic
	@Column(name = "IS_DATEINSTANT")
	private Boolean isDateinstant=false;

	@Basic
	@Column(name = "IS_INDI_CONTR_MANAGER")
	private Boolean isIndiContrToManager=false;
	
	@Basic
	@Column(name = "IS_MANAGER_INDI_CONTR")
	private Boolean isManagerToIndiContr=false;	
	
	
	public EventListenerParam() {
	}
	
	public EventListenerParam(Long id) {
		this.id = id;
	}

	public EventListenerParam(String eventId) {
		this.eventId = eventId;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public Boolean getIsHire() {
		return isHire;
	}

	public void setIsHire(Boolean isHire) {
		this.isHire = isHire;
	}

	public Boolean getIsTerminate() {
		return isTerminate;
	}

	public void setIsTerminate(Boolean isTerminate) {
		this.isTerminate = isTerminate;
	}

	public Boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public Boolean getIsDateinstant() {
		return isDateinstant;
	}

	public void setIsDateinstant(Boolean isDateinstant) {
		this.isDateinstant = isDateinstant;
	}

	public Boolean getIsIndiContrToManager() {
		if(isIndiContrToManager!=null)
			return isIndiContrToManager;
		else
			return false;
	}

	public void setIsIndiContrToManager(Boolean isIndiContrToManager) {
		this.isIndiContrToManager = isIndiContrToManager;
	}

	public Boolean getIsManagerToIndiContr() {	
		if(isManagerToIndiContr!=null)
			return isManagerToIndiContr;
		else
			return false;
	}

	public void setIsManagerToIndiContr(Boolean isManagerToIndiContr) {
		this.isManagerToIndiContr = isManagerToIndiContr;
	}

	public Boolean getIsProcessAgain() {
		if(isProcessAgain!=null)
			return isProcessAgain;
		
		return false;
	}

	public void setIsProcessAgain(Boolean isProcessAgain) {
		this.isProcessAgain = isProcessAgain;
	}	
}