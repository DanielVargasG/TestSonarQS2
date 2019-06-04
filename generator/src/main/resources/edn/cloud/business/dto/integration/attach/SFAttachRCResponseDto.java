package edn.cloud.business.dto.integration.attach;

public class SFAttachRCResponseDto {
	private SFAttachRCInfoN4Dto resume;
	private SFAttachRCInfoN4Dto coverLetter;
	private SFAttachListInfoDto attachment1;
	private SFAttachListInfoDto attachment2;
	
	public SFAttachRCInfoN4Dto getResume() {
		return resume;
	}
	public void setResume(SFAttachRCInfoN4Dto resume) {
		this.resume = resume;
	}
	public SFAttachRCInfoN4Dto getCoverLetter() {
		return coverLetter;
	}
	public void setCoverLetter(SFAttachRCInfoN4Dto coverLetter) {
		this.coverLetter = coverLetter;
	}
	public SFAttachListInfoDto getAttachment1() {
		return attachment1;
	}
	public void setAttachment1(SFAttachListInfoDto attachment1) {
		this.attachment1 = attachment1;
	}
	public SFAttachListInfoDto getAttachment2() {
		return attachment2;
	}
	public void setAttachment2(SFAttachListInfoDto attachment2) {
		this.attachment2 = attachment2;
	}
}
