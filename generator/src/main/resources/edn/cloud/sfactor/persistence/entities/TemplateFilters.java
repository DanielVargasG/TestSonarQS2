package edn.cloud.sfactor.persistence.entities;

import java.lang.Long;
import java.lang.String;
import javax.persistence.*;


/**
 * Entity implementation class for Entity: Template Filters
 *
 */
@Entity
@Table(name = "TEMPLATE_FILTERS", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class TemplateFilters implements IDBEntity 
{
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TEMPLATE_ID")	
	private Template templateId;
	
	@Basic
	@Column(name = "PATH")	
	private String path;
	
	@Basic
	@Column(name = "VALUE")
	private String value;
	
	public TemplateFilters() {
		super();
	}   
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Template getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Template templateId) {
		this.templateId = templateId;
	}
}