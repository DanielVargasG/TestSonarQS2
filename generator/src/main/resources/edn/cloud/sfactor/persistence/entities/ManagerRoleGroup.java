package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "MANAGERROLEGROUP", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class ManagerRoleGroup implements IDBEntity{

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "MANAGER_ROLE_ID")
	private Long managerRoleId;

	@Basic
	@Column(name = "GROUP_ID")
	private String groupId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getManagerRoleId() {
		return managerRoleId;
	}

	public void setManagerRoleId(Long managerRoleId) {
		this.managerRoleId = managerRoleId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
}
