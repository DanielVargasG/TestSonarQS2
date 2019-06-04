package edn.cloud.business.dto.ppd.api;

import edn.cloud.business.dto.integration.GenResponseInfoDto;

public class PpdDocGenFullWithErrors {
	private String id;
	private String created_at;
	private String format;
	private String document_generation_template_id;
	private String document_generation_template_version;
	private String upload_id;
	private GenResponseInfoDto[] errors;
	
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
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getDocument_generation_template_id() {
		return document_generation_template_id;
	}
	public void setDocument_generation_template_id(String document_generation_template_id) {
		this.document_generation_template_id = document_generation_template_id;
	}
	public String getDocument_generation_template_version() {
		return document_generation_template_version;
	}
	public void setDocument_generation_template_version(String document_generation_template_version) {
		this.document_generation_template_version = document_generation_template_version;
	}
	public GenResponseInfoDto[] getErrors() {
		return errors;
	}
	public void setErrors(GenResponseInfoDto[] errors) {
		this.errors = errors;
	}
	public String getUpload_id() {
		return upload_id;
	}
	public void setUpload_id(String upload_id) {
		this.upload_id = upload_id;
	}
	
}
