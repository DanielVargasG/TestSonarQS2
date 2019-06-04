package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ROLE_MAPPING_GROUP", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class RoleMappingGroupPpd implements IDBEntity {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "ROLE_MAPPING_ID")
	private Long roleMappingId;

	@Basic
	@Column(name = "GROUP_ID")
	private String groupId;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoleMappingId() {
		return roleMappingId;
	}

	public void setRoleMappingId(Long roleMappingId) {
		this.roleMappingId = roleMappingId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
