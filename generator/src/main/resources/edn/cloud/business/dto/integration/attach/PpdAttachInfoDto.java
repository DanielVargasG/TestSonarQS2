package edn.cloud.business.dto.integration.attach;

import java.util.HashMap;

public class PpdAttachInfoDto
{
	private String external_unique_id;
	private String title;
	private String date;
	private String tags;
	private String employee_technical_id;
	private String document_type_code;
	private String api_uploader_name;
	private String[] organization_codes;
	private HashMap<String,String> document_type_metas;
	
	public String getExternal_unique_id() {
		return external_unique_id;
	}
	public void setExternal_unique_id(String external_unique_id) {
		this.external_unique_id = external_unique_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getEmployee_technical_id() {
		return employee_technical_id;
	}
	public void setEmployee_technical_id(String employee_technical_id) {
		this.employee_technical_id = employee_technical_id;
	}
	public String getDocument_type_code() {
		return document_type_code;
	}
	public void setDocument_type_code(String document_type_code) {
		this.document_type_code = document_type_code;
	}
	public String[] getOrganization_codes() {
		return organization_codes;
	}
	public void setOrganization_codes(String[] organization_codes) {
		this.organization_codes = organization_codes;
	}
	public String getApi_uploader_name() {
		return api_uploader_name;
	}
	public void setApi_uploader_name(String api_uploader_name) {
		this.api_uploader_name = api_uploader_name;
	}
	public HashMap<String, String> getDocument_type_metas() {
		return document_type_metas;
	}
	public void setDocument_type_metas(HashMap<String, String> document_type_metas) {
		this.document_type_metas = document_type_metas;
	}	
}
