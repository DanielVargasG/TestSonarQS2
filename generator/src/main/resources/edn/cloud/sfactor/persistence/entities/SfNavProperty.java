package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "SFNAVPROPERTY", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class SfNavProperty implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "NAME")
	private String name;

	@Basic
	@Column(name = "RELATIONSHIP")
	private String relationship;

	@Basic
	@Column(name = "LABEL")
	private String label;

	@Basic
	@Column(name = "TYPE")
	private String type;

	@Basic
	@Column(name = "MULTIPLICITY")
	private String multiplicity;

	public SfNavProperty() {

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

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}

}