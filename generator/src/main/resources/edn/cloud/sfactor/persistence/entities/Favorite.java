package edn.cloud.sfactor.persistence.entities;

import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_FAV_BY_KEYID;
import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_FAV_BY_USERID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "FAVORITES", uniqueConstraints = { @UniqueConstraint(columnNames = { "KEY_ID" }) })
@NamedQueries({ @NamedQuery(name = GET_FAV_BY_USERID, query = "select u from Favorite u where u.userId = :userId"), @NamedQuery(name = GET_FAV_BY_KEYID, query = "select u from Favorite u where u.keyId = :keyId")})
public class Favorite implements IDBEntity {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "TEMPLATE_ID")
	private String templateId;

	@Basic
	@Column(name = "KEY_ID")
	private String keyId;


	@Basic
	@Column(name = "USER_ID")
	private String userId;

	public Favorite() {

	}

	public Favorite(String keyId) {
		this.keyId = keyId;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

}
