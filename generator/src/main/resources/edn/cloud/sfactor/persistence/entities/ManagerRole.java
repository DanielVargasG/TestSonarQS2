package edn.cloud.sfactor.persistence.entities;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "MANAGERROLE", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class ManagerRole implements IDBEntity{
	
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "DESCRIPCION")
	private String description;

	@Basic
	@Column(name = "NAMESOURCE")
	private String namesource;
	
	@Basic
	@Column(name = "ICON")
	private String icon;
	
	@OneToMany(cascade = CascadeType.PERSIST)
	private List<ManagerRoleGroup> listGroup;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNamesource() {
		return namesource;
	}

	public void setNamesource(String namesource) {
		this.namesource = namesource;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<ManagerRoleGroup> getListGroup() {
		return listGroup;
	}

	public void setListGroup(List<ManagerRoleGroup> listGroup) {
		this.listGroup = listGroup;
	}
	
	
}
