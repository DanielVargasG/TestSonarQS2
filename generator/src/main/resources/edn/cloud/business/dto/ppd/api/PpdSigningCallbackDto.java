package edn.cloud.business.dto.ppd.api;

import java.util.ArrayList;

public class PpdSigningCallbackDto 
{
	private String signature_id;
	private String external_id;
	private String status;
	private String document_download_url;
	private ArrayList<PpdSigningErrorDto> errors;
	private ArrayList<PpdSigningMessageDto> comments;
	
	public PpdSigningCallbackDto() {
		errors = new ArrayList<>();
		comments = new ArrayList<>();
	}
	
	public String getSignature_id() {
		return signature_id;
	}
	public void setSignature_id(String signature_id) {
		this.signature_id = signature_id;
	}
	public String getExternal_id() {
		return external_id;
	}
	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDocument_download_url() {
		return document_download_url;
	}
	public void setDocument_download_url(String document_download_url) {
		this.document_download_url = document_download_url;
	}
	public ArrayList<PpdSigningErrorDto> getErrors() {
		return errors;
	}
	public void setErrors(ArrayList<PpdSigningErrorDto> errors) {
		this.errors = errors;
	}
	public ArrayList<PpdSigningMessageDto> getComments() {
		return comments;
	}
	public void setComments(ArrayList<PpdSigningMessageDto> comments) {
		this.comments = comments;
	}	    
}
