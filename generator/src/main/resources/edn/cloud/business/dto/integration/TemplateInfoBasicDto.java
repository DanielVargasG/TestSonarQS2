package edn.cloud.business.dto.integration;

public class TemplateInfoBasicDto 
{
	private Long idTemplate;// id date base
	private String id;	
	
	private String title;
	private String description;
	private String locale;
	private String esign;
	private String module;
	private String documentType;
	private Long idEventListener;	
	private String format;
	private String emailSign;
	private String selfGeneration;	
	private String formatGenerated;	
	private String managerConfirm;
	private String typeSign;
	
	private Long folder;
	private Boolean enabled;	
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFormatGenerated() {
		return formatGenerated;
	}
	public void setFormatGenerated(String formatGenerated) {
		this.formatGenerated = formatGenerated;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getEsign() {
		return esign;
	}
	public void setEsign(String esign) {
		this.esign = esign;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public Long getIdEventListener() {
		return idEventListener;
	}
	public void setIdEventListener(Long idEventListener) {
		this.idEventListener = idEventListener;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getEmailSign() {
		return emailSign;
	}
	public void setEmailSign(String emailSign) {
		this.emailSign = emailSign;
	}
	public String getSelfGeneration() {
		return selfGeneration;
	}
	public void setSelfGeneration(String selfGeneration) {
		this.selfGeneration = selfGeneration;
	}
	public Long getIdTemplate() {
		return idTemplate;
	}
	public void setIdTemplate(Long idTemplate) {
		this.idTemplate = idTemplate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public Long getFolder() {
		return folder;
	}
	public void setFolder(Long folder) {
		this.folder = folder;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public String getManagerConfirm() {
		return managerConfirm;
	}
	public void setManagerConfirm(String managerConfirm) {
		this.managerConfirm = managerConfirm;
	}
	public String getTypeSign() {
		return typeSign;
	}
	public void setTypeSign(String typeSign) {
		this.typeSign = typeSign;
	}
}
