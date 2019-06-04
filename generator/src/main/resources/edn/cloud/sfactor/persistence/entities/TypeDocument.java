package edn.cloud.sfactor.persistence.entities;

import java.lang.Long;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: TypeDocumentDAO
 *
 */
@Entity
@Table(name="TYPE_DOCUMENT")
public class TypeDocument implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "NAME")
	private String name;

	public TypeDocument() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
   
}
