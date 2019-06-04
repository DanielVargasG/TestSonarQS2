package edn.cloud.sfactor.persistence.entities;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.integration.SlugItem;

/**
 * Entity implementation class for Entity: FieldsTamplate
 *
 */
@Entity
@Table(name = "PARAM_FIELD_MAPPING", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class FieldsMappingPpd implements IDBEntity {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	/*
	 * @Basic
	 * 
	 * @Column(name = "GROUP") private String nameGroup;
	 */

	@Basic
	@Column(name = "DESCRIPTION")
	private String description;

	@Basic
	@Column(name = "NAME_SOURCE")
	private String nameSource;

	@Basic
	@Column(name = "NAME_DESTINATION", length = 5000)
	private String nameDestination;

	@Basic
	@Column(name = "TYPE_MODULE")
	private String typeModule;

	@Basic
	@Column(name = "IS_FILTER")
	private Boolean isFilter = false;

	@Basic
	@Column(name = "IS_ATTACHED")
	private Boolean isAttached = false;

	@Basic
	@Column(name = "PARAMETERS", length = 5000)
	private String parameters;

	@Basic
	@Column(name = "IS_CONSTANT")
	private Boolean isConstants = false;

	@Basic
	@Column(name = "IS_OBLIGATORY")
	private Boolean isObligatory = false;

	@Basic
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Basic
	@Column(name = "ACTIONS_VAR", length = 5000)
	private String actions;

	private List<String> metadataList;
	private List<SlugItem> actionsList;

	public FieldsMappingPpd() {
		super();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNameDestination() {
		return nameDestination;
	}

	public void setNameDestination(String nameDestination) {
		this.nameDestination = nameDestination;
	}

	public String getNameSource() {
		return nameSource;
	}

	public void setNameSource(String slug) {
		this.nameSource = slug;
	}

	public Boolean getIsFilter() {
		return isFilter;
	}

	public void setIsFilter(Boolean isFilter) {
		this.isFilter = isFilter;
	}

	public Boolean getIsAttached() {
		return isAttached;
	}

	public void setIsAttached(Boolean isAttached) {
		this.isAttached = isAttached;
	}

	public Boolean getIsConstants() {
		return isConstants;
	}

	public void setIsConstants(Boolean isConstants) {
		this.isConstants = isConstants;
	}

	public Boolean getIsObligatory() {
		return isObligatory;
	}

	public void setIsObligatory(Boolean isObligatory) {
		this.isObligatory = isObligatory;
	}

	public String getTypeModule() {
		return typeModule;
	}

	public void setTypeModule(String typeModule) {
		this.typeModule = typeModule;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsActive() {
		if (isActive == null)
			return Boolean.TRUE;

		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public List<String> getMetadataList() {
		return metadataList;
	}

	public void setMetadataList(List<String> metadataList) {
		this.metadataList = metadataList;
	}

	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public List<SlugItem> getActionsList(String valueTarget) {
		if (this.actions != null && !this.actions.equals(""))
			return UtilMapping.loadActionsFieldMapping(this.actions, valueTarget, this.nameSource);

		return null;
	}

	public void setActionsList(List<SlugItem> actionsList) {
		this.actionsList = actionsList;
	}
}