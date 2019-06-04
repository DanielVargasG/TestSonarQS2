package edn.cloud.business.dto.integration.attach;

import edn.cloud.business.dto.integration.GenMessageInfoDto;

public class SFAttachResponseN1Dto 
{
	private SFAttachInfoN4Dto d;
	private GenMessageInfoDto error;
	
	
	public SFAttachInfoN4Dto getD() {
		return d;
	}
	public void setD(SFAttachInfoN4Dto d) {
		this.d = d;
	}
	public GenMessageInfoDto getError() {
		return error;
	}
	public void setError(GenMessageInfoDto error) {
		this.error = error;
	}	
}