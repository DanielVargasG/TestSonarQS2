package edn.cloud.business.dto.ppd.api;

public class PpdSignatureInfoDto 
{
	private String external_id;
	private String signature_type;
	private String document_type;
	private String title;
	private String reason;
	private String location;
	private String expiration_date;
	private String status;
	private String auto_send;
	private String callback_url;
	
	private PpdSignerTypeInfoDto[] signers;

	public String getExternal_id() {
		return external_id;
	}

	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}

	public String getSignature_type() {
		return signature_type;
	}

	public void setSignature_type(String signature_type) {
		this.signature_type = signature_type;
	}

	public String getDocument_type() {
		return document_type;
	}

	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) 
	{
		if(title!=null && !title.equals("") && title.length()>100){
			title = title.substring(0,98);
		}
		
		this.title = title;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getExpiration_date() {
		return expiration_date;
	}

	public void setExpiration_date(String expiration_date) {
		this.expiration_date = expiration_date;
	}

	public PpdSignerTypeInfoDto[] getSigners() {
		return signers;
	}

	public void setSigners(PpdSignerTypeInfoDto[] signers) {
		this.signers = signers;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAuto_send() {
		return auto_send;
	}

	public void setAuto_send(String auto_send) {
		this.auto_send = auto_send;
	}

	public String getCallback_url() {
		return callback_url;
	}

	public void setCallback_url(String callback_url) {
		this.callback_url = callback_url;
	}
}
