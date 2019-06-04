package edn.cloud.business.dto.ppd.api;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * {"status": "pending", 
 * "requestor_description": "", 
 * "signers": [
 * 	{	"phone_number": "0033645764425", 
 * 		"pdf_sign_field": "sg7", 
 * 		"sms_authentication_sent": false, 
 * 		"signing_order": 1, 
 * 		"with_sms_authentication": null, 
 * 		"email_address": "mtuitel@aragoconsulting.ch", 
 * 		"registration_references": ["GEN_mtuitel"], 
 * 		"sms_notification_sent": false, 
 * 		"state": "pending", 
 * 		"with_sms_notification": null, 
 * 		"type": "employee", 
 * 		"id": 11131, 
 * 		"technical_id": "mtuitel"}]
 * , 
 * 		"reason": "Permanent Job Contract",
 * 		"document_type": "test", 
 * 		"id": 6297, 
 * 		"callback_url": "", 
 * 		"requestor_technical_id": "", 
 * 		"document_type_filters": {}, 
 * 		"expiration_date": "2017-12-01", 
 * 		"title": "doc - Emilio Hernandez - 7 10 2017 - Ref: bbb9de4b-c01b-41e8-bc1b-88d74dc8fac1", 
 * 		"signature_type": "docusign-arago", 
 * 		"location": "Paris", 
 * 		"document_type_metas": {}, 
 * 		"auto_send": false, 
 * 		"external_id": "bbb9de4b-c01b-41e8-bc1b-88d74dc8fac1"
 * 	}
 * */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PpdSigningHeadDto 
{
	private String status;
	private String requestor_description;
	private String reason;
	private String document_type; 
	private String id; 
	private String callback_url; 
	private String requestor_technical_id; 
	private String expiration_date; 
	private String title;
	private String signature_type;
	private String location;	
	private String auto_send;
	private String external_id;
	
	//private String document_type_metas;
	//private String document_type_filters;	
	
	
	private ArrayList<PpdSigningSingersDto> signers; 
			
	public PpdSigningHeadDto() {
		this.signers = new ArrayList<PpdSigningSingersDto>();
	}	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRequestor_description() {
		return requestor_description;
	}
	public void setRequestor_description(String requestor_description) {
		this.requestor_description = requestor_description;
	}	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getDocument_type() {
		return document_type;
	}
	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCallback_url() {
		return callback_url;
	}
	public void setCallback_url(String callback_url) {
		this.callback_url = callback_url;
	}
	public String getRequestor_technical_id() {
		return requestor_technical_id;
	}
	public void setRequestor_technical_id(String requestor_technical_id) {
		this.requestor_technical_id = requestor_technical_id;
	}
	public String getExpiration_date() {
		return expiration_date;
	}
	public void setExpiration_date(String expiration_date) {
		this.expiration_date = expiration_date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSignature_type() {
		return signature_type;
	}
	public void setSignature_type(String signature_type) {
		this.signature_type = signature_type;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAuto_send() {
		return auto_send;
	}
	public void setAuto_send(String auto_send) {
		this.auto_send = auto_send;
	}
	public String getExternal_id() {
		return external_id;
	}
	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}
	/**
	 * @return the signers
	 */
	public ArrayList<PpdSigningSingersDto> getSigners() {
		return signers;
	}
	/**
	 * @param signers the signers to set
	 */
	public void setSigners(ArrayList<PpdSigningSingersDto> signers) {
		this.signers = signers;
	}
}
