package edn.cloud.sfactor.persistence.entities;

import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_USER_BY_EMAIL;
import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_USER_BY_USER_ID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edn.cloud.business.api.util.UtilMapping;

@Entity
@Table(name = "EMPLOYEES", uniqueConstraints = { @UniqueConstraint(columnNames = { "USER_ID" }) })
@NamedQueries({ @NamedQuery(name = GET_USER_BY_USER_ID, query = "select u from Employee u where u.userId = :userId"), @NamedQuery(name = GET_USER_BY_EMAIL, query = "select u from Employee u where u.email = :email") })
public class Employee implements IDBEntity {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "FIRST_NAME")
	private String firstName;

	@Basic
	@Column(name = "LAST_NAME")
	private String lastName;

	@Basic
	@Column(name = "USER_ID")
	private String userId;

	@Basic
	@Column(name = "TITLE")
	private String title;

	@Basic
	@Column(name = "LOCATION")
	private String location;
	
	@Basic
	@Column(name = "DEFT_LANGUAGE")
	private String defaultLanguage;

	@Basic
	@Column(name = "HIRE_DATE")
	private String hireDate;

	@Basic
	@Column(name = "COUNT_MNG")
	private String countMng;

	@Basic
	@Column(name = "COUNT_HR")
	private String countHr;

	@Basic
	private String email;

	public Employee() {

	}

	public Employee(String userId) {
		this.userId = userId;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return UtilMapping.decrypt(firstName);
	}

	public void setFirstName(String firstName) {
		this.firstName = UtilMapping.encrypt(firstName);
	}

	public String getLastName() {
		return UtilMapping.decrypt(lastName);
	}

	public void setLastName(String lastName) {
		this.lastName = UtilMapping.encrypt(lastName);
	}

	public String getFullName() {
		return UtilMapping.decrypt(firstName) + " " + UtilMapping.decrypt(lastName);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHireDate() {
		return hireDate;
	}

	public void setHireDate(String hireDate) {
		this.hireDate = hireDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCountMng() {
		return countMng;
	}

	public void setCountMng(String countMng) {
		this.countMng = countMng;
	}

	public String getCountHr() {
		return countHr;
	}

	public void setCountHr(String countHr) {
		this.countHr = countHr;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
}
