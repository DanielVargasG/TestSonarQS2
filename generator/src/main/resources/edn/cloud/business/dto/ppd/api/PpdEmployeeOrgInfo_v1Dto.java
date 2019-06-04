package edn.cloud.business.dto.ppd.api;

/**
 * object integration calls Employees API People Doc
 * */
public class PpdEmployeeOrgInfo_v1Dto 
{
	//field v core_employee
	private String departure_date;
	
	private String organization_code;	
	private String registration_number;	
	private String active;
	
	public String getOrganization_code() {
		return organization_code;
	}
	public void setOrganization_code(String organization_code) {
		this.organization_code = organization_code;
	}
	public String getRegistration_number() {
		return registration_number;
	}
	public void setRegistration_number(String registration_number) {
		this.registration_number = registration_number;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getDeparture_date() {
		return departure_date;
	}
	public void setDeparture_date(String departure_date) {
		this.departure_date = departure_date;
	}
}