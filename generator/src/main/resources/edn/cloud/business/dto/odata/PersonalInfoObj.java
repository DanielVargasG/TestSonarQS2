package edn.cloud.business.dto.odata;

public class PersonalInfoObj {

	private String lastName;
	private String firstName;
	private String nationality;
	private String gender;
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String _lastName) {
		this.lastName = _lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String _firstName) {
		this.firstName = _firstName;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
}
