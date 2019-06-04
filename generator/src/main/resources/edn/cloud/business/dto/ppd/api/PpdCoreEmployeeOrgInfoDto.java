package edn.cloud.business.dto.ppd.api;

/**
 * object integration calls Employees API People Doc
 */
public class PpdCoreEmployeeOrgInfoDto extends PpdEmployeeOrgInfo_v1Dto {
	private String employee_number;
	private String organization_id;

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

}
