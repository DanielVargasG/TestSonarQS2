package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "SFPROPERTY", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class SfProperty implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "NAME", length=1024)
	private String name;
	
	@Basic
	@Column(name = "TYPE", length=1024)
	private String type;
	
	@Basic
	@Column(name = "LABEL", length=1024)
	private String label;
	
	public SfProperty() {

	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}