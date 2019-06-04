package edn.cloud.sfactor.persistence.entities;

import java.lang.Long;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: TypeDocumentVariables
 * 
 */
@Entity
@Table(name = "TYPE_DOCUMENT_VARIABLES")
public class TypeDocumentVariables implements IDBEntity{

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long Id;
	
	@Basic
	@Column(name="KEY_SOURCE_ORIGIN")
	private String keySourceOrigin;
	
	@Basic
	@Column(name="KEY_SOURCE_DESTINATION")	
	private String keySourceDestination;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOCUMENT_ID", referencedColumnName = "ID")
	private TypeDocument typeDocument;


	public TypeDocumentVariables() {
		super();
	}   
	public Long getId() {
		return this.Id;
	}

	public void setId(Long Id) {
		this.Id = Id;
	}
	public String getKeySourceOrigin() {
		return keySourceOrigin;
	}
	public void setKeySourceOrigin(String keySourceOrigin) {
		this.keySourceOrigin = keySourceOrigin;
	}
	public String getKeySourceDestination() {
		return keySourceDestination;
	}
	public void setKeySourceDestination(String keySourceDestination) {
		this.keySourceDestination = keySourceDestination;
	}   
	   
}
