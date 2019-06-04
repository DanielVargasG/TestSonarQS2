package edn.cloud.business.dto.ppd.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.SlugItem;

/**
 * object integration calls Employees API People Doc
 */
public class PpdEmployeeInfo_v1Dto {
	private String lastname;
	private String firstname;
	
	//no core version
	private String maiden_name;	
	//no core version
	private String technical_id;
	//no core version
	private String departure_date;
	//no core version
	private String dob;
	//no core version
	private String phone_number;
	//no core version
	private String disable_elec_distribution;
	//no core version
	private String disable_paper_distribution;
	//no core version
	private String gone;
	//no core version
	private String disable_vault;
	//no core version
	private Map<String, String> filters;	
	
	private String email;
	private String address1;
	private String address2;
	private String address3;
	private String zip_code;
	private String city;
	private String country;
	private String mobile_phone_number;	
	private String starting_date;
	private ArrayList<PpdCoreEmployeeOrgInfoDto> registration_references;

	// fields to manage response of web service ppd
	private GenResponseInfoDto[] errors;
	private String observations;
	
	private List<SlugItem> actions;

	/**
	 * @param filters
	 */
	public PpdEmployeeInfo_v1Dto() {
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getMaiden_name() {
		return maiden_name;
	}

	public void setMaiden_name(String maiden_name) {
		this.maiden_name = maiden_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTechnical_id() {
		return technical_id;
	}

	public void setTechnical_id(String technical_id) {
		this.technical_id = technical_id;
	}

	public String getDeparture_date() {
		return departure_date;
	}

	public void setDeparture_date(String departure_date) {
		this.departure_date = departure_date;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getZip_code() {
		return zip_code;
	}

	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getMobile_phone_number() {
		return mobile_phone_number;
	}

	public void setMobile_phone_number(String mobile_phone_number) {
		this.mobile_phone_number = mobile_phone_number;
	}

	public String getDisable_elec_distribution() {
		return disable_elec_distribution;
	}

	public void setDisable_elec_distribution(String disable_elec_distribution) {
		this.disable_elec_distribution = disable_elec_distribution;
	}

	public String getDisable_paper_distribution() {
		return disable_paper_distribution;
	}

	public void setDisable_paper_distribution(String disable_paper_distribution) {
		this.disable_paper_distribution = disable_paper_distribution;
	}

	public String getStarting_date() {
		return starting_date;
	}

	public void setStarting_date(String starting_date) {
		this.starting_date = starting_date;
	}

	public String getGone() {
		return gone;
	}

	public void setGone(String gone) {
		this.gone = gone;
	}

	public String getDisable_vault() {
		return disable_vault;
	}

	public void setDisable_vault(String disable_vault) {
		this.disable_vault = disable_vault;
	}

	public GenResponseInfoDto[] getErrors() {
		return errors;
	}

	public void setErrors(GenResponseInfoDto[] errors) {
		this.errors = errors;
	}

	public ArrayList<PpdCoreEmployeeOrgInfoDto> getRegistration_references() {
		return registration_references;
	}

	public void setRegistration_references(ArrayList<PpdCoreEmployeeOrgInfoDto> registration_references) {
		this.registration_references = registration_references;
	}	

	public Map<String, String> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}

	public String getObservations() {
		if (observations == null)
			observations = "";
		return observations;
	}

	public void setObservations(String observations) {
		if (observations != null && observations.length() > 1999)
			this.observations = observations.substring(0, 1998);
		else
			this.observations = observations;
	}

	public List<SlugItem> getActions() {
		return actions;
	}

	public void setActions(List<SlugItem> actions) {
		this.actions = actions;
	}
}