package edn.cloud.business.dto.odata;

public class JobInfoObj 
{
	private String jobCode;
	private String jobTitle;
	private String standardHours;
	private String fte;
	private String location;
	private String company;
	private String managerId;
	private regularTempObj regularTempNav;
	private PositionObj positionNav;
	private LocationObj locationNav;
	private EmployeeTypeObj employeeTypeNav;
	private CompanyObj companyNav;

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getStandardHours() {
		return standardHours;
	}

	public void setStandardHours(String standardHours) {
		this.standardHours = standardHours;
	}

	public String getFte() {
		return fte;
	}

	public void setFte(String fte) {
		this.fte = fte;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public PositionObj getPositionNav() {
		return positionNav;
	}

	public void setPositionNav(PositionObj positionNav) {
		this.positionNav = positionNav;
	}

	public LocationObj getLocationNav() {
		return locationNav;
	}

	public void setLocationNav(LocationObj locationNav) {
		this.locationNav = locationNav;
	}

	public EmployeeTypeObj getEmployeeTypeNav() {
		return employeeTypeNav;
	}

	public void setEmployeeTypeNav(EmployeeTypeObj employeeTypeNav) {
		this.employeeTypeNav = employeeTypeNav;
	}

	public CompanyObj getCompanyNav() {
		return companyNav;
	}

	public void setCompanyNav(CompanyObj companyNav) {
		this.companyNav = companyNav;
	}

	public regularTempObj getRegularTempNav() {
		return regularTempNav;
	}

	public void setRegularTempNav(regularTempObj regularTemp) {
		this.regularTempNav = regularTemp;
	}

}
