package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ERRORLOGS", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class ErrorLog implements IDBEntity {
	

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "FIELD")
	private String field;
	
	@Basic
	@Column(name = "MESSAGE")
	private String message; 

	@Basic
	@Column(name = "CODE")
	private String code;
	

	public ErrorLog() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


}
