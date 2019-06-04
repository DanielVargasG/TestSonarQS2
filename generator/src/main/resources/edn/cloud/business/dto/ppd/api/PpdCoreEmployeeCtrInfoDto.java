package edn.cloud.business.dto.ppd.api;

/**
 * object integration calls Employees API People Doc
 */
public class PpdCoreEmployeeCtrInfoDto extends PpdEmployeeOrgInfo_v1Dto {
	private String employee_number;
	private String organization_id;
	private String country_id;
	private String filter;
	private String entry_date;

	public String getEmployee_number() {
		return employee_number;
	}

	public void setEmployee_number(String employee_number) {
		this.employee_number = employee_number;
	}

	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}

	public String getCountry_id() {
		return country_id;
	}

	public void setCountry_id(String country_id) {
		this.country_id = country_id;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getEntry_date() {
		return entry_date;
	}

	public void setEntry_date(String entry_date) {
		this.entry_date = entry_date;
	}
	
}
