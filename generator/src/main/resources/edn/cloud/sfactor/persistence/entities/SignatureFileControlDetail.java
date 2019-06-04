package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "SIGNATURE_FILE_CTRL_DET", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class SignatureFileControlDetail implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SIGN_FILE_CTRL_ID")
	private SignatureFileControl signatureFileCtrl;
	
	@Basic
	@Column(name = "ID_PPD")	
	private String idPpd;
	
	@Basic
	@Column(name = "NAME_SOURCE")	
	private String nameSource;
	
	@Basic
	@Column(name = "SING_LINK")	
	private String signatoryLink;	
	
	@Basic
	@Column(name = "NAME_DESTINATION")
	private String nameDestination;
	
	@Basic
	@Column(name = "ORDER_SING")
	private Integer order;

	@PrePersist
	protected void onCreate() {
		//this.generatedOn = new Date();
	}

	public SignatureFileControlDetail() {

	}

	public SignatureFileControlDetail(Long docId) {
		this.id = docId;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SignatureFileControl getSignatureFileCtrl() {
		return signatureFileCtrl;
	}

	public void setSignatureFileCtrl(SignatureFileControl signatureFileCtrl) {
		this.signatureFileCtrl = signatureFileCtrl;
	}

	public String getNameSource() {
		return nameSource;
	}

	public void setNameSource(String nameSource) {
		this.nameSource = nameSource;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getIdPpd() {
		return idPpd;
	}

	public void setIdPpd(String idPpd) {
		this.idPpd = idPpd;
	}

	public String getNameDestination() {
		return nameDestination;
	}

	public void setNameDestination(String nameDestination) {
		this.nameDestination = nameDestination;
	}

	public String getSignatoryLink() {
		return signatoryLink;
	}

	public void setSignatoryLink(String signatoryLink) {
		this.signatoryLink = signatoryLink;
	}	
}