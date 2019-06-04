package edn.cloud.sfactor.persistence.entities;

import java.lang.Long;
import java.lang.String;

import javax.persistence.*;

import edn.cloud.sfactor.persistence.entities.Template;

/**
 * Entity implementation class for Entity: SignatureGroupTemplate
 *
 */
@Entity
@Table(name = "TEMPLATE_GROUP_SIGNATURE", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class TemplateGroupSignature implements IDBEntity 
{
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TEMPLATE_ID")	
	private Template templateId;
	
	@Basic
	@Column(name = "NAME_GROUP")
	private String nameGroup;
	
	public TemplateGroupSignature() {
		super();
	}   
	
	public TemplateGroupSignature(Long id) {
		this.id = id;
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
	public String getNameGroup() {
		return nameGroup;
	}
	public void setNameGroup(String nameGroup) {
		this.nameGroup = nameGroup;
	}
	/*public ArrayList<TemplateSignature> getTemplateSignatureList() {
		return templateSignatureList;
	}
	public void setTemplateSignatureList(ArrayList<TemplateSignature> templateSignatureList) {
		this.templateSignatureList = templateSignatureList;
	}  */ 	
}
