package edn.cloud.business.dto.odata;

public class EmploymentObj {
	private String startDate;
	private String seniorityDate;
	private JobInfoList jobInfoNav;
	private EmpRelationShipList empJobRelationshipNav;
	private UserObj userNav;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getSeniorityDate() {
		return seniorityDate;
	}

	public void setSeniorityDate(String seniorityDate) {
		this.seniorityDate = seniorityDate;
	}

	public JobInfoList getJobInfoNav() {
		return jobInfoNav;
	}

	public void setJobInfoNav(JobInfoList jobInfoNav) {
		this.jobInfoNav = jobInfoNav;
	}

	public EmpRelationShipList getEmpJobRelationshipNav() {
		return empJobRelationshipNav;
	}

	public void setEmpJobRelationshipNav(EmpRelationShipList empJobRelationshipNav) {
		this.empJobRelationshipNav = empJobRelationshipNav;
	}

	public UserObj getUserNav() {
		return userNav;
	}

	public void setUserNav(UserObj userNav) {
		this.userNav = userNav;
	}
}
