package edn.cloud.sfactor.persistence.entities;

import java.lang.Long;
import java.lang.String;
import javax.persistence.*;


/**
 * Entity implementation class for Entity: FieldsTamplate
 *
 */
@Entity
@Table(name = "TEMPLATE_SIGNATURE", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
public class TemplateSignature implements IDBEntity 
{
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TEMPL_GROUP_SIGN_ID")	
	private TemplateGroupSignature group;

	@Basic
	@Column(name = "NAME_SOURCE")	
	private String nameSource;
	
	@Basic
	@Column(name = "NAME_DESTINATION")
	private String nameDestination;
	
	@Basic
	@Column(name = "IS_CONSTANT")
	private Boolean isConstants;
	
	@Basic
	@Column(name = "ORDER_SING")
	private Integer order;	
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="SIGN_TEM_LIB_ID")
	private SignsTemplateLibrary signsTemplateLibrary;

	public TemplateSignature() {
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
	public TemplateGroupSignature getGroup() {
		return group;
	}
	public void setGroup(TemplateGroupSignature group) {
		this.group = group;
	}
	public Boolean getIsConstants() {
		return isConstants;
	}
	public void setIsConstants(Boolean isConstants) {
		this.isConstants = isConstants;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public SignsTemplateLibrary getSignsTemplateLibrary() {
		return signsTemplateLibrary;
	}
	public void setSignsTemplateLibrary(SignsTemplateLibrary signsTemplateLibrary) {
		this.signsTemplateLibrary = signsTemplateLibrary;
	}
	
}
