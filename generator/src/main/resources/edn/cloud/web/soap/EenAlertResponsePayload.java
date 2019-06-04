package edn.cloud.web.soap;

import java.io.Serializable;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "entityId", "errorCode", "errorMessage", "status", "statusDate", "statusDetails" })
@XmlRootElement(name = "EenAlertResponsePayload")
public class EenAlertResponsePayload implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "entityId", namespace = "http://notification.event.successfactors.com")
	private String entityId;
	@XmlElement(name = "errorCode", namespace = "http://notification.event.successfactors.com")
	private String errorCode;
	@XmlElement(name = "errorMessage", namespace = "http://notification.event.successfactors.com")
	private String errorMessage;
	@XmlElement(name = "status", namespace = "http://notification.event.successfactors.com")
	private Integer status;
	@XmlElement(name = "statusDate", namespace = "http://notification.event.successfactors.com")
	private Calendar statusDate;
	@XmlElement(name = "statusDetails", namespace = "http://notification.event.successfactors.com")
	private String statusDetails;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Calendar getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Calendar statusDate) {
		this.statusDate = statusDate;
	}

	public String getStatusDetails() {
		return statusDetails;
	}

	public void setStatusDetails(String statusDetails) {
		this.statusDetails = statusDetails;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
