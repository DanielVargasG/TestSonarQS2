package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "LKTABLE", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class LookupTable implements IDBEntity {
	
	@Id
	@GeneratedValue
	@Column(name = "ID")    
	private Long id;
	
	@Basic
	@Column(name = "CODETABLE")
	private String codeTable;
	
	@Basic
	@Column(name = "VALUEIN")
	private String valueIn;
	
	@Basic
	@Column(name = "VALUEOUT")
	private String valueOut;
	
	public LookupTable() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodeTable() {
		return codeTable;
	}

	public void setCodeTable(String codeTable) {
		this.codeTable = codeTable;
	}

	public String getValueIn() {
		return valueIn;
	}

	public void setValueIn(String valueIn) {
		this.valueIn = valueIn;
	}

	public String getValueOut() {
		return valueOut;
	}

	public void setValueOut(String valueOut) {
		this.valueOut = valueOut;
	}

}
