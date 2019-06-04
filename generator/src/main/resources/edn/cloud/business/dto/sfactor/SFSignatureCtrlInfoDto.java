package edn.cloud.business.dto.sfactor;

public class SFSignatureCtrlInfoDto 
{
	private String idDocument;
	private String idSignatureCtrl;
	private Boolean showLinkToSign;
	private String phaseCurrent;
	private String title;
	private String status;
	private String document_type;
	private String dateSignControl;
	private String signatoryLink;
	
	public String getIdSignatureCtrl() {
		return idSignatureCtrl;
	}
	public void setIdSignatureCtrl(String idSignatureCtrl) {
		this.idSignatureCtrl = idSignatureCtrl;
	}
	public Boolean getShowLinkToSign() {
		return showLinkToSign;
	}
	public void setShowLinkToSign(Boolean showLinkToSign) {
		this.showLinkToSign = showLinkToSign;
	}
	public String getPhaseCurrent() {
		return phaseCurrent;
	}
	public void setPhaseCurrent(String phaseCurrent) {
		this.phaseCurrent = phaseCurrent;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDocument_type() {
		return document_type;
	}
	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}
	public String getDateSignControl() {
		return dateSignControl;
	}
	public void setDateSignControl(String dateSignControl) {
		this.dateSignControl = dateSignControl;
	}
	public String getSignatoryLink() {
		return signatoryLink;
	}
	public void setSignatoryLink(String signatoryLink) {
		this.signatoryLink = signatoryLink;
	}
	public String getIdDocument() {
		return idDocument;
	}
	public void setIdDocument(String idDocument) {
		this.idDocument = idDocument;
	}
}
