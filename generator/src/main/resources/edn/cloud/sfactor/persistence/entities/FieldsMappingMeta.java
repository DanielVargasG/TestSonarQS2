package edn.cloud.sfactor.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity implementation class for Entity: FieldsMappingMeta
 *
 */
@Entity
@Table(name = "FIELD_MAPP_PPD_META", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class FieldsMappingMeta implements IDBEntity 
{
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name="TEMPLATEID")
	private Long templateId;
	
	@Basic
	@Column(name="IDFIELDTEMPLATE")
	private Long fieldsMappingPpdId;
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="FIELD_TEMP_LIB_ID")	
	private FieldsTemplateLibrary fieldsTemplateLibrary;
		
	public FieldsMappingMeta() {
		super();
	}   
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public FieldsTemplateLibrary getFieldsTemplateLibrary() {
		return fieldsTemplateLibrary;
	}
	public void setFieldsTemplateLibrary(FieldsTemplateLibrary fieldsTemplateLibrary) {
		this.fieldsTemplateLibrary = fieldsTemplateLibrary;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long template) {
		this.templateId = template;
	}
	public Long getFieldsMappingPpdId() {
		return fieldsMappingPpdId;
	}
	public void setFieldsMappingPpdId(Long fieldsMappingPpdId) {
		this.fieldsMappingPpdId = fieldsMappingPpdId;
	}
	
}