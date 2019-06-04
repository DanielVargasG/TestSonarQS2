package edn.cloud.sfactor.persistence.entities;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "COUNTRIES", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class Countries implements IDBEntity {
	
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "CODE")
	private String code;
	
	@Basic
	@Column(name = "ACTIVE")
	private Boolean active=false;
	
	@Basic
	@Column(name = "TOPROCESS_USER")
	private Boolean toProcessUser=false;
	
	@Basic
	@Column(name = "PROCESSATTACH")
	private Boolean processAttach=true;
	
	@Basic
	@Column(name = "EMPLOYEEVAULT")
	private Boolean employeeVault=false;
	
	@Basic
	@Column(name = "PARAMETERS", length = 5000)
	private String parameters;	
	
	@Basic
	@Column(name = "TIME_ZONE_ID", length = 500)
	private String timeZoneId;	
	
	public Countries() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getToProcessUser() {
		return toProcessUser;
	}

	public void setToProcessUser(Boolean toProcessUser) {
		this.toProcessUser = toProcessUser;
	}

	public Boolean getProcessAttach() {
		return processAttach;
	}

	public void setProcessAttach(Boolean processAttach) {
		this.processAttach = processAttach;
	}

	public Boolean getEmployeeVault() {
		return employeeVault;
	}

	public void setEmployeeVault(Boolean employeeVault) {
		this.employeeVault = employeeVault;
	}
	
	public String getParameters() {
		if (parameters == null)
			parameters = "";

		return parameters;
	}

	public void setParameters(String parameters) {		
		if (parameters != null && parameters.length() > 5000) {
			parameters = parameters.substring(0, 4999);
		}
		
		this.parameters = parameters;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}
}
