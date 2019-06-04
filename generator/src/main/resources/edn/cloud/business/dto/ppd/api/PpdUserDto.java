package edn.cloud.business.dto.ppd.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PpdUserDto {

	public String firstname;
	public String lastname;
	public String technical_id;
	public String external_id;
	public String id;
	public String email;
	public String dob = "1985-01-12";
	public String address1;
	public String zip_code;
	public String city;
	public String country;
	public String gone;
	public String mobile_phone_number = "0033645764425";
	public List<PpdOrga> registration_references = new ArrayList<PpdOrga>();
	public Map<String, String> filters = new HashMap<String, String>();

	public PpdUserDto() {

	}

		public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getTechnical_id() {
		return technical_id;
	}

	public void setTechnical_id(String technical_id) {
		this.technical_id = technical_id;
	}

	public String getExternal_id() {
		return external_id;
	}

	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getGone() {
		return gone;
	}

	public void setGone(String gone) {
		this.gone = gone;
	}

	public String getMobile_phone_number() {
		return mobile_phone_number;
	}

	public void setMobile_phone_number(String mobile_phone_number) {
		this.mobile_phone_number = mobile_phone_number;
	}

	public List<PpdOrga> getRegistration_references() {
		return registration_references;
	}

	public void setRegistration_references(List<PpdOrga> registration_references) {
		this.registration_references = registration_references;
	}

	public Map<String, String> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}
}
