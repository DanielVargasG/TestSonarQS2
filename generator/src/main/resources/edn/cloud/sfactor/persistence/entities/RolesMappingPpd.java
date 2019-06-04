package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity implementation class for Entity: RoleTamplate
 *
 */
@Entity
@Table(name = "PARAMROLEMAPPINGPPD", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class RolesMappingPpd implements IDBEntity 
{
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "ROLE_NAME_SF")	
	private String nameSf;
	
	@Basic
	@Column(name = "ROLE_ID_SF")	
	private String idSf; 
	
	@Basic
	@Column(name = "ROLE_NAME_PPD")
	private String namePpd;	
	
	@Basic
	@Column(name = "ROLE_COUNT_SF")
	private String countUsers;
	
	@Basic
	@Column(name = "ROLE_TYPE")
	private String roleType;
	
	@Basic
	@Column(name = "ROLE_OPERATOR")
	private String roleOperator;
	
	@Basic
	@Column(name = "ROLE_ORGA")
	private String roleOrga;
	
	@Basic
	@Column(name = "ROLE_ORGA_DYNA")
	private Boolean roleOrgaDyna;
	
	@Basic
	@Column(name = "ROLE_USE_SF_GROUPS")
	private Boolean roleUseSfGroups;
	
	@Basic
	@Column(name = "ROLE_USE_SF_ROLES")
	private Boolean roleUseSfRoles;
	
	@Basic
	@Column(name = "ROLE_STTC_CONT", length = 1000)
	private String roleStaticContent;
	

	public RolesMappingPpd() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getNameSf() {
		return nameSf;
	}
	public void setNameSf(String nameSf) {
		this.nameSf = nameSf;
	}
	public String getNamePpd() {
		return namePpd;
	}
	public void setNamePpd(String namePpd) {
		this.namePpd = namePpd;
	}
	public String getIdSf() {
		return idSf;
	}
	public void setIdSf(String idSf) {
		this.idSf = idSf;
	}
	public String getCountUsers() {
		return countUsers;
	}
	public void setCountUsers(String countUsers) {
		this.countUsers = countUsers;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getRoleOperator() {
		return roleOperator;
	}
	public void setRoleOperator(String roleOperator) {
		this.roleOperator = roleOperator;
	}
	public String getRoleOrga() {
		return roleOrga;
	}
	public void setRoleOrga(String roleOrga) {
		this.roleOrga = roleOrga;
	}
	public Boolean getRoleOrgaDyna() {
		return roleOrgaDyna;
	}
	public void setRoleOrgaDyna(Boolean roleOrgaDyna) {
		this.roleOrgaDyna = roleOrgaDyna;
	}
	public String getRoleStaticContent() {
		return roleStaticContent;
	}
	public void setRoleStaticContent(String roleStaticContent) {
		this.roleStaticContent = roleStaticContent;
	}
	public Boolean getRoleUseSfGroups() {
		return roleUseSfGroups;
	}
	public void setRoleUseSfGroups(Boolean roleUseSfGroups) {
		this.roleUseSfGroups = roleUseSfGroups;
	}
	public Boolean getRoleUseSfRoles() {
		return roleUseSfRoles;
	}
	public void setRoleUseSfRoles(Boolean roleUseSfRoles) {
		this.roleUseSfRoles = roleUseSfRoles;
	}   
	
}