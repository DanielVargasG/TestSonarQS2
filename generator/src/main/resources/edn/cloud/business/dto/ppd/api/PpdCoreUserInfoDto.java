package edn.cloud.business.dto.ppd.api;

import java.util.List;

public class PpdCoreUserInfoDto 
{		 
	private String id;
	private String external_id;
	private String firstname;
	private String lastname;
	private String middlename;
	private String language;
	private String timezone;
	private String mobile_phone_number;
	private String created_at;
	private String updated_at;
	private String email;
	public String saml_token;
	
	private List <PpdProfilesInfoDto> profiles;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExternal_id() {
		return external_id;
	}
	public void setExternal_id(String external_id) {
		this.external_id = external_id;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<PpdProfilesInfoDto> getProfiles() {
		return profiles;
	}
	public void setProfiles(List<PpdProfilesInfoDto> profiles) {
		this.profiles = profiles;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public String getMobile_phone_number() {
		return mobile_phone_number;
	}
	public void setMobile_phone_number(String mobile_phone_number) {
		this.mobile_phone_number = mobile_phone_number;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getSaml_token() {
		return saml_token;
	}
	public void setSaml_token(String saml_token) {
		this.saml_token = saml_token;
	}	
}