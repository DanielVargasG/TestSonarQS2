package edn.cloud.sfactor.persistence.entities;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.lang.Long;
import java.lang.String;
import java.util.List;

import javax.persistence.*;

import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.integration.SlugItem;

/**
 * Entity implementation class for Entity: FieldsTemplateLibrary
 *
 */
@Entity
@Table(name = "FIELD_TEMPLATE_LIB", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class FieldsTemplateLibrary implements IDBEntity {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "NAME_SOURCE", unique = true)
	private String nameSource;

	@Basic
	@Column(name = "NAME_DESTINATION")
	private String nameDestination;

	@Basic
	@Column(name = "NAME_DESTIN", length = 5000)
	private String nameDestinationExt;

	@Basic
	@Column(name = "IS_TABLE")
	private Boolean isTableValue = false;

	@Basic
	@Column(name = "IS_CONSTANT")
	private Boolean isConstants = false;
	
	@Basic
	@Column(name = "ACTIONS_VAR", length = 5000)
	private String actions;	
	
	//list of actions from actions field
	private List<SlugItem> actionsList;

	public FieldsTemplateLibrary() {
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

	public Boolean getIsTableValue() {
		if (isTableValue != null)
			return isTableValue;
		else
			return false;
	}

	public void setIsTableValue(Boolean isTableValue) {
		this.isTableValue = isTableValue;
	}

	public Boolean getIsConstants() {
		if (isTableValue != null)
			return isConstants;
		else
			return false;
	}

	public void setIsConstants(Boolean isConstant) {
		this.isConstants = isConstant;
	}

	public String getNameDestinationExt() {
		return nameDestinationExt;
	}

	public void setNameDestinationExt(String nameDestinationExt) {
		this.nameDestinationExt = nameDestinationExt;
	}
	
	public List<SlugItem> getActionsList(String valueTarget) {
		if (this.actions != null && !this.actions.equals(""))
			return UtilMapping.loadActionsFieldMapping(this.actions, valueTarget, this.nameSource);

		return null;
	}

	public void setActionsList(List<SlugItem> actionsList) {
		this.actionsList = actionsList;
	}

	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}
	
	
}