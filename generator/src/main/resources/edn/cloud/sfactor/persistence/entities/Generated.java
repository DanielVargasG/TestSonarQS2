package edn.cloud.sfactor.persistence.entities;

import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_GENERATED_BY_ID;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "GENERATED", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
@NamedQueries({ @NamedQuery(name = GET_GENERATED_BY_ID, query = "select u from Generated u where u.documentId = :docId") })
public class Generated implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOCUMENT_ID", referencedColumnName = "ID")
	private Document document;	

	@Basic
	@Column(name = "VERSION")
	private String version;

	@Basic
	@Column(name = "GENERATED_ID_PPD")
	private String generatedIdPpd;

	@Basic
	@Column(name = "NUMBER_ERRORS")
	private int numberErrors;

	@Basic
	@Column(name = "STATE")
	private String state;
	
	@Basic
	@Column(name = "UPLOAD_ID_PPD", nullable = true)
	private String uploadIdPpd;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "GENERATED_ON")
	private Date generatedOn;

	@Column(name = "DOCUMENT_ID", insertable = false, updatable = false)
	private Long documentId;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="ERR_ID")
	private List<ErrorLog> errors;

	@PrePersist
	protected void onCreate() {
		this.generatedOn = new Date();
	}

	public Generated() {

	}

	public Generated(Long docId) {
		this.id = docId;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getGeneratedIdPpd() {
		return generatedIdPpd;
	}

	public void setGeneratedIdPpd(String generatedId) {
		this.generatedIdPpd = generatedId;
	}

	public String getGeneratedOn() {
		return generatedOn.toString();
	}

	public List<ErrorLog> getErrors() {
		if (this.errors == null) {
			this.errors = new ArrayList<>();
		}
		return errors;
	}

	public void setErrors(List<ErrorLog> errors) {
		this.errors = errors;
	}

	public void addError(ErrorLog error) {
		getErrors().add(error);
	}

	public int getNumberErrors() {
		return numberErrors;
	}

	public void setNumberErrors(int numberErrors) {
		this.numberErrors = numberErrors;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUploadIdPpd() {
		return uploadIdPpd;
	}

	public void setUploadIdPpd(String uploadIdPpd) {
		this.uploadIdPpd = uploadIdPpd;
	}
	
}
