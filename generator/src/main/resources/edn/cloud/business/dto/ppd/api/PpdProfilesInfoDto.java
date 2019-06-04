package edn.cloud.business.dto.ppd.api;

public class PpdProfilesInfoDto 
{ 		       
    private String id;	 		        		
	private String role_id;
	private PpdUserPerimeterBaseDto employees_perimeter;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRole_id() {
		return role_id;
	}
	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}
	public PpdUserPerimeterBaseDto getEmployees_perimeter() {
		return employees_perimeter;
	}
	public void setEmployees_perimeter(PpdUserPerimeterBaseDto employees_perimeter) {
		this.employees_perimeter = employees_perimeter;
	}
}