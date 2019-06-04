package edn.cloud.business.dto;

public class TemplateUpdateDto {
	private String description;
	private Boolean enabled;
	private String locale;
	private String title;
	private String validity_end_date;
	private String validity_start_date;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValidity_end_date() {
		return validity_end_date;
	}

	public void setValidity_end_date(String validity_end_date) {
		this.validity_end_date = validity_end_date;
	}

	public String getValidity_start_date() {
		return validity_start_date;
	}

	public void setValidity_start_date(String validity_start_date) {
		this.validity_start_date = validity_start_date;
	}

}
