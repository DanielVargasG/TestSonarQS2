package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "SIGNTEMPLATELIBRARY", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class SignsTemplateLibrary implements IDBEntity  {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "NAMESOURCE")
	private String nameSource;
	
	@Basic
	@Column(name = "NAMEFULL")
	private String nameFull;	
	
	@Basic
	@Column(name = "NAMEDESTINATION")
	private String nameDestination;

	public SignsTemplateLibrary() {
	}
	public SignsTemplateLibrary(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNameSource() {
		return nameSource;
	}

	public void setNameSource(String nameSource) {
		this.nameSource = nameSource;
	}

	public String getNameDestination() {
		return nameDestination;
	}

	public void setNameDestination(String nameDestination) {
		this.nameDestination = nameDestination;
	}
	public String getNameFull() {
		return nameFull;
	}
	public void setNameFull(String nameFull) {
		this.nameFull = nameFull;
	}	
}