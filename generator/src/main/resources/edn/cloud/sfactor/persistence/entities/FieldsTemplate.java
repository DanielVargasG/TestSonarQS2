package edn.cloud.sfactor.persistence.entities;

import java.lang.Long;
import java.lang.String;
import javax.persistence.*;

import edn.cloud.sfactor.persistence.entities.Template;

/**
 * Entity implementation class for Entity: FieldsTamplate
 *
 */
@Entity
@Table(name = "FIELD_TEMPLATE", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class FieldsTemplate implements IDBEntity 
{
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TEMPLATE_ID")	
	private Template templateId;
	
	@Basic
	@Column(name = "NAME_DESTINATION")
	private String nameDestination;
	
	@Basic
	@Column(name = "NAME_SOURCE")	
	private String nameSource;
	
	@Basic
	@Column(name = "IS_CONSTANT")
	private Boolean isConstants = false;//test field, not include in user view	

	public FieldsTemplate() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public Template getTemplateId() {
		return this.templateId;
	}
	
	public void setTemplateId(Template templateId) {
		this.templateId = templateId;
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
	public Boolean getIsConstants() {
		return isConstants;
	}
	public void setIsConstants(Boolean isConstants) {
		this.isConstants = isConstants;
	}      
	
}