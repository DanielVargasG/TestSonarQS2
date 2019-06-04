package edn.cloud.business.dto.integration;

import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_USERS_BY_ROLENAME;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edn.cloud.sfactor.persistence.entities.IDBEntity;

@Entity
@Table(name = "PARAM_ROLE_USER_PPD", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
@NamedQueries({ @NamedQuery(name = GET_USERS_BY_ROLENAME, query = "select u from SFAdmin u where u.sfrolename = :name and u.appsfroleid = :appid") })
public class SFAdmin implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "FIRSTNAME")
	private String firstName;

	@Basic
	@Column(name = "LASTNAME")
	private String lastName;

	@Basic
	@Column(name = "MIDDLENAME")
	private String middleName;

	@Basic
	@Column(name = "USERID")
	private String userId;

	@Basic
	@Column(name = "USERNAME")
	private String username;

	@Basic
	@Column(name = "EMAIL")
	private String email;

	@Basic
	@Column(name = "CONCAT")
	private String concat;

	@Basic
	@Column(name = "OPERATOR")
	private String operator;

	@Basic
	@Column(name = "ORGA")
	private String orga;

	@Basic
	@Column(name = "SFROLENAME")
	private String sfrolename;
	
	@Basic
	@Column(name = "APPSFROLEID")
	private Long appsfroleid;
	
	@Basic
	@Column(name = "MESSAGE", length = 5000)
	private String message; 
	
	@Basic
	@Column(name = "MESSAGEP1", length = 2000)
	private String part1;
	
	@Basic
	@Column(name = "MESSAGEP2", length = 2000)
	private String part2;

	public String getSfrolename() {
		return sfrolename;
	}

	public void setSfrolename(String sfrolename) {
		this.sfrolename = sfrolename;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConcat() {
		return concat;
	}

	public void setConcat(String concat) {
		this.concat = concat;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toCompare() {
		return getFirstName() + getLastName() + getMiddleName() + getUserId() + getEmail() + getUsername() + getOperator() + getOrga() + getSfrolename() + getAppsfroleid() + getMessage();
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOrga() {
		return orga;
	}

	public void setOrga(String orga) {
		this.orga = orga;
	}

	public Long getAppsfroleid() {
		return appsfroleid;
	}

	public void setAppsfroleid(Long appsfroleid) {
		this.appsfroleid = appsfroleid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPart1() {
		return part1;
	}

	public void setPart1(String part1) {
		this.part1 = part1;
	}

	public String getPart2() {
		return part2;
	}

	public void setPart2(String part2) {
		this.part2 = part2;
	}
}
