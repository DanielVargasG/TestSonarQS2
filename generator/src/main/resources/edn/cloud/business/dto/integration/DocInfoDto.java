package edn.cloud.business.dto.integration;

import edn.cloud.business.api.util.UtilMapping;

public class DocInfoDto {

	//{"name":"Geneva Principal Office","parent_organization_id":"ARAGOCH","corporate_name":"","address1":"","address2":"","zip_code":"","city":"","country":"","contact_firstname":"","contact_lastname":"","contact_email":"","contact_phone_number":"","id":"GEN","created_at":"2017-08-27T16:24:30.420946+00:00","updated_at":"2017-12-15T20:05:59.188564+00:00"}
	private String id;
	private String title;
	private String description;
	private String locale;
	private String created_at;
	private String updated_at;
	private String format;
	private Integer latest_version;
	private Boolean enabled;
	private Boolean active = false;
	public String employee_id;
	public String document_type_id;
	public String name;
	public String date;
	public String origin;
	public Boolean finalDoc;
	public Boolean ntFinalDoc;

	public DocInfoDto() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Integer getLatest_version() {
		return latest_version;
	}

	public void setLatest_version(Integer latest_version) {
		this.latest_version = latest_version;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getHash() throws Exception {		
		return UtilMapping.getHashCodeDownloadDoc(getId());
	}
}
