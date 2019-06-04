package edn.cloud.business.dto.integration.attach;

public class SFAttachInfoN4Dto 
{
	//atributes employee center
	private String attachmentId;
	private String attachmentFileName;
	private String attachmentFileType;
	private String attachment;

	//attributes recruiting
	private SFAttachRCResponseDto[] results;	
	
	//attributes get content attach
	private String fileContent;
	private String externalId;
	private String mimeType;
	private String fileName;
	private String moduleCategory;
	private String module;
	private String fileExtension;
			
	
	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getAttachmentFileName() {
		return attachmentFileName;
	}

	public void setAttachmentFileName(String attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}

	public String getAttachmentFileType() {
		return attachmentFileType;
	}

	public void setAttachmentFileType(String attachmentFileType) {
		this.attachmentFileType = attachmentFileType;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public SFAttachRCResponseDto[] getResults() {
		return results;
	}

	public void setResults(SFAttachRCResponseDto[] results) {
		this.results = results;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getModuleCategory() {
		return moduleCategory;
	}

	public void setModuleCategory(String moduleCategory) {
		this.moduleCategory = moduleCategory;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}	
}
