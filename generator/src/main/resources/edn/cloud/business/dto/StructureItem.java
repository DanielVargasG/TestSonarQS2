package edn.cloud.business.dto;

public class StructureItem {
	private String personIdExternal;
	private String userId;
	private String countryCode;
	private String jobStartDate;
	private String jobEndDate;
	private String employeeStatus;
	private String empEndDate;
	private String empStartDate;

	public String getPersonIdExternal() {
		return personIdExternal;
	}

	public void setPersonIdExternal(String personIdExternal) {
		this.personIdExternal = personIdExternal;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getJobEndDate() {
		return jobEndDate;
	}

	public void setJobEndDate(String jobEndDate) {
		this.jobEndDate = jobEndDate;
	}

	public String getJobStartDate() {
		return jobStartDate;
	}

	public void setJobStartDate(String jobStartDate) {
		this.jobStartDate = jobStartDate;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public String getEmpEndDate() {
		return empEndDate;
	}

	public void setEmpEndDate(String empEndDate) {
		this.empEndDate = empEndDate;
	}

	public String getEmpStartDate() {
		return empStartDate;
	}

	public void setEmpStartDate(String empStartDate) {
		this.empStartDate = empStartDate;
	}
}
