package edn.cloud.business.dto.ppd.api;

import java.util.ArrayList;
import java.util.Map;

/**
 * object integration calls Employees API People Doc
 */
public class PpdCoreEmployeeInfoDto extends PpdEmployeeInfo_v1Dto
{
	private String id;
	private String external_id;
	private String maidenname;
	private String middlename;
	private String language;
	private String timezone;
	private String saml_token;
	private String birth_date;
	private String state;
	private Boolean terminated ;
	private ArrayList<PpdFilterCoreEmployeeDto> custom_fields;
	private Map<String, String> options;
	private String created_at;
	private String updated_at;

	/**
	 * @param filters
	 */
	public PpdCoreEmployeeInfoDto() {
	}

	public String getExternal_id() {
		return external_id;
	}

	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}

	public String getMaidenname() {
		return maidenname;
	}

	public void setMaidenname(String maidenname) {
		this.maidenname = maidenname;
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

	public String getSaml_token() {
		return saml_token;
	}

	public void setSaml_token(String saml_token) {
		this.saml_token = saml_token;
	}

	public String getBirth_date() {
		return birth_date;
	}

	public void setBirth_date(String birth_date) {
		this.birth_date = birth_date;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public ArrayList<PpdFilterCoreEmployeeDto> getCustom_fields() {
		return custom_fields;
	}

	public void setCustom_fields(ArrayList<PpdFilterCoreEmployeeDto> custom_fields) {
		this.custom_fields = custom_fields;
	}

	public Boolean getTerminated() {
		return terminated;
	}

	public void setTerminated(Boolean terminated) {
		this.terminated = terminated;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Map<String, String> getOptions() {
		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;
	}	
}
