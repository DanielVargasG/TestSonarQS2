package edn.cloud.business.dto.integration;

public class DocTemplateInfo {

	private String sae_file_id;
	private String validity_start_date;
	private String validity_end_date;
	private String created_at;
	private String updated_at;	
	private Integer version;
	private Boolean enabled;

	public DocTemplateInfo() {

	}

	public String getSae_file_id() {
		return sae_file_id;
	}

	public void setSae_file_id(String sae_file_id) {
		this.sae_file_id = sae_file_id;
	}

	public String getValidity_start_date() {
		return validity_start_date;
	}

	public void setValidity_start_date(String validity_start_date) {
		this.validity_start_date = validity_start_date;
	}

	public String getValidity_end_date() {
		return validity_end_date;
	}

	public void setValidity_end_date(String validity_end_date) {
		this.validity_end_date = validity_end_date;
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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	

}
