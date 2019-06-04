package edn.cloud.business.dto.ppd.api;

public class PpdSignerTypeInfoDto {

	private String type;
	private String pdf_sign_field;
	private String technical_id;
	private int signing_order;
	private String email_address;
	private String first_name;
	private String last_name;
	private String status;
	private String language;
	private Boolean with_sms_notification = false;
	private Boolean with_sms_authentication = false;
	
	private PpdGeneratePdfSignField generate_pdf_sign_field;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPdf_sign_field() {
		return pdf_sign_field;
	}

	public void setPdf_sign_field(String pdf_sign_field) {
		this.pdf_sign_field = pdf_sign_field;
	}

	public int getSigning_order() {
		return signing_order;
	}

	public void setSigning_order(int signing_order) {
		this.signing_order = signing_order;
	}

	public String getTechnical_id() {
		return technical_id;
	}

	public void setTechnical_id(String technical_id) {
		this.technical_id = technical_id;
	}

	public String getEmail_address() {
		return email_address;
	}

	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public PpdGeneratePdfSignField getGenerate_pdf_sign_field() {
		return generate_pdf_sign_field;
	}

	public void setGenerate_pdf_sign_field(PpdGeneratePdfSignField generate_pdf_sign_field) {
		this.generate_pdf_sign_field = generate_pdf_sign_field;
	}

	public Boolean getWith_sms_notification() {
		return with_sms_notification;
	}

	public void setWith_sms_notification(Boolean with_sms_notification) {
		this.with_sms_notification = with_sms_notification;
	}

	public Boolean getWith_sms_authentication() {
		return with_sms_authentication;
	}

	public void setWith_sms_authentication(Boolean with_sms_authentication) {
		this.with_sms_authentication = with_sms_authentication;
	}
	
}
