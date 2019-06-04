package edn.cloud.business.dto.ppd.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PpdSigningSingersDto 
{
	private String phone_number;
	private String pdf_sign_field; 
	private String sms_authentication_sent;
	private String signing_order; 
	private String with_sms_authentication; 
	private String email_address;
	private String signatory_link;
	//private String registration_references; 
	private String sms_notification_sent;
	private String state; 
	private String with_sms_notification;
	private String type; 
	private String id;
	private String technical_id;
			
	public PpdSigningSingersDto() {
	
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public String getPdf_sign_field() {
		return pdf_sign_field;
	}
	public void setPdf_sign_field(String pdf_sign_field) {
		this.pdf_sign_field = pdf_sign_field;
	}
	public String getSms_authentication_sent() {
		return sms_authentication_sent;
	}
	public void setSms_authentication_sent(String sms_authentication_sent) {
		this.sms_authentication_sent = sms_authentication_sent;
	}
	public String getSigning_order() {
		return signing_order;
	}
	public void setSigning_order(String signing_order) {
		this.signing_order = signing_order;
	}
	public String getWith_sms_authentication() {
		return with_sms_authentication;
	}
	public void setWith_sms_authentication(String with_sms_authentication) {
		this.with_sms_authentication = with_sms_authentication;
	}
	public String getEmail_address() {
		return email_address;
	}
	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}
	public String getSms_notification_sent() {
		return sms_notification_sent;
	}
	public void setSms_notification_sent(String sms_notification_sent) {
		this.sms_notification_sent = sms_notification_sent;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getWith_sms_notification() {
		return with_sms_notification;
	}
	public void setWith_sms_notification(String with_sms_notification) {
		this.with_sms_notification = with_sms_notification;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTechnical_id() {
		return technical_id;
	}
	public void setTechnical_id(String technical_id) {
		this.technical_id = technical_id;
	}
	public String getSignatory_link() {
		return signatory_link;
	}
	public void setSignatory_link(String signatory_link) {
		this.signatory_link = signatory_link;
	}
}
