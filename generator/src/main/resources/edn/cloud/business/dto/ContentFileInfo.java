package edn.cloud.business.dto;

import edn.cloud.business.dto.integration.SlugItem;

public class ContentFileInfo {

	private String fileName;
	private SlugItem ref1;
	private byte[] file;
	private String id;

	public ContentFileInfo() {
	}	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SlugItem getRef1() {
		return ref1;
	}

	public void setRef1(SlugItem ref1) {
		this.ref1 = ref1;
	}
}