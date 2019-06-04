package edn.cloud.sfactor.persistence.entities;

import java.lang.Long;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: FieldsTamplate
 *
 */
@Entity
@Table(name = "PARAM_ADMIN", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class AdminParameters implements IDBEntity 
{
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "NAME_CODE")	
	private String nameCode;
	
	
	@Basic
	@Column(name = "DESCRIPTION")	
	private String description;	
	
	@Basic
	@Column(name = "VALUE")
	private String value;
	
	@Basic
	@Column(name = "GROUP_ADMIN")
	private String group;
	
	@Basic
	@Column(name = "ISCONTROLPANEL")
	private Boolean isControlPanel=false;
	
	@Basic
	@Column(name = "LASTUPDATEDATE")
	private Date lastUpdateDate;
	
	@Basic
	@Column(name = "LASTUPDATEUSER")
	private String lastUpdateUser;

	public AdminParameters() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getNameCode() {
		return nameCode;
	}
	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public Boolean getIsControlPanel() {
		return isControlPanel;
	}
	public void setIsControlPanel(Boolean isControlPanel) {
		this.isControlPanel = isControlPanel;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getLastUpdateUser() {
		return lastUpdateUser;
	}
	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	} 
}