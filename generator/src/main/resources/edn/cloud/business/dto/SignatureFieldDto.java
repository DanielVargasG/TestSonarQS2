package edn.cloud.business.dto;

import edn.cloud.business.dto.integration.SlugItem;

public class SignatureFieldDto extends SlugItem 
{
	private Long idTemplate;
	private Long idSingGroupTemplate;
	private Long idSignDocument;
	private Integer order;
	private Boolean isSignDocument = false;
	private Long idSignTempLib;
	private String fullName;
	
	public SignatureFieldDto(String slug, String value) {
		super(slug, value);
	}
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Long getIdTemplate() {
		return idTemplate;
	}

	public void setIdTemplate(Long idTemplate) {
		this.idTemplate = idTemplate;
	}
	public Long getIdSignDocument() {
		return idSignDocument;
	}
	public void setIdSignDocument(Long idSingDocument) {
		this.idSignDocument = idSingDocument;
	}
	public Long getIdSingGroupTemplate() {
		return idSingGroupTemplate;
	}
	public void setIdSingGroupTemplate(Long idSingGroupTemplate) {
		this.idSingGroupTemplate = idSingGroupTemplate;
	}
	public Boolean getIsSignDocument() {
		return isSignDocument;
	}
	public void setIsSignDocument(Boolean isSignDocument) {
		this.isSignDocument = isSignDocument;
	}
	public Long getIdSignTempLib() {
		return idSignTempLib;
	}
	public void setIdSignTempLib(Long idSignTempLib) {
		this.idSignTempLib = idSignTempLib;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
