package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity implementation class for Entity: DocumentFields
 *
 */
@Entity
@Table(name = "DOCUMENT_FIELDS", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class DocumentFields implements IDBEntity 
{
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DOCUMENT_ID")	
	private Document documentId;
	
	@Basic
	@Column(name = "NAME_DESTINATION")
	private String nameDestination;
	
	@Basic
	@Column(name = "NAME_SOURCE")	
	private String nameSource;
	
	@Basic
	@Column(name = "IS_CONSTANT")
	private Boolean isConstants = false;//test field, not include in user view	
		
	public DocumentFields() {
		super();
	}   
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Document getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Document documentId) {
		this.documentId = documentId;
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
	public Boolean getIsConstants() {
		return isConstants;
	}
	public void setIsConstants(Boolean isConstants) {
		this.isConstants = isConstants;
	}
}