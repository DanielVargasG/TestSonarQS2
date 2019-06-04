package edn.cloud.sfactor.persistence.entities;

import java.lang.Long;
import java.lang.String;
import javax.persistence.*;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilMapping;

/**
 * Entity implementation class for Entity: SignatureDocument
 *
 */
@Entity
@Table(name = "DOCUMENT_SIGNATURE", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class DocumentSignature implements IDBEntity 
{
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DOCUMENT_ID")	
	private Document documentId;
	
	@Basic
	@Column(name = "STATUS")
	private String status;
	
	@Basic
	@Column(name = "NAME_SOURCE")	
	private String nameSource;
	
	@Basic
	@Column(name = "NAME_DESTINATION")
	private String nameDestination;
	
	@Basic
	@Column(name = "ORDER_SING")
	private Integer order;
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="SIGN_TEM_LIB_ID")
	private SignsTemplateLibrary signsTemplateLibrary;

	public SignsTemplateLibrary getSignsTemplateLibrary() {
		return signsTemplateLibrary;
	}
	public void setSignsTemplateLibrary(SignsTemplateLibrary signsTemplateLibrary) {
		this.signsTemplateLibrary = signsTemplateLibrary;
	}
	public DocumentSignature() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getNameDestination() {
		return nameDestination;
	}
	public void setNameDestination(String nameDestination) {
		this.nameDestination = nameDestination;
	}
	public String getNameSource() {
		return nameSource;
	}
	public void setNameSource(String slug) {
		this.nameSource = slug;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Document getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Document documentId) {
		this.documentId = documentId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatusLabel() {
		if(status!=null && !status.equals(""))
			return UtilMapping.getLabelEnumByCode(status);
			
		return status;
	}
}
