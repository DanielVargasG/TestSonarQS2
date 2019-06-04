package edn.cloud.business.dto.integration.attach;

public class SFAttachRCInfoN4Dto 
{
	private String attachmentId;
	private String module;
	private String fileName;
	private String documentEntityType;
		
	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}
	
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDocumentEntityType() {
		return documentEntityType;
	}

	public void setDocumentEntityType(String documentEntityType) {
		this.documentEntityType = documentEntityType;
	}
}
