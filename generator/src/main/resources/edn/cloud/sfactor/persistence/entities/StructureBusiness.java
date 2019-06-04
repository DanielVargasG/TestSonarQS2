package edn.cloud.sfactor.persistence.entities;

import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_STRUCTURE_BY_NAME;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity implementation class for Entity: StructureBusiness
 *
 */
@Entity
@Table(name = "STRUCTURE_BUSINESS", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
@NamedQueries({ @NamedQuery(name = GET_STRUCTURE_BY_NAME, query = "select u from StructureBusiness u where u.structureName = :structName")})

public class StructureBusiness implements IDBEntity {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "STRUCTURE_NAME")
	private String structureName;
	
	@Basic
	@Column(name = "STRUCTURE_OBJECT")
	private String structureObject;

	@ManyToOne
	@JoinColumn(name = "FK_PARENT_STRUCTURE")
	public StructureBusiness parentStructure;

	public StructureBusiness() {
		super();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StructureBusiness getParentStructure() {
		return parentStructure;
	}

	public void setParentStructure(StructureBusiness parentStructure) {
		this.parentStructure = parentStructure;
	}

	public String getStructureName() {
		return structureName;
	}

	public void setStructureName(String structureName) {
		this.structureName = structureName;
	}
	
	public String getStructureObject() {
		return structureObject;
	}

	public void setStructureObject(String structureObject) {
		this.structureObject = structureObject;
	}
}
