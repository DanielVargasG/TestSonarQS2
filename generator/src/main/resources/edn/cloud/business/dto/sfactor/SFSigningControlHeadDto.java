package edn.cloud.business.dto.sfactor;

import java.util.ArrayList;
import java.util.List;

public class SFSigningControlHeadDto 
{
	List<SFSignatureCtrlInfoDto> signatureCtrlInfoList;

	public SFSigningControlHeadDto(){
		super();
		signatureCtrlInfoList = new ArrayList<SFSignatureCtrlInfoDto>();
	}
	
	public List<SFSignatureCtrlInfoDto> getSignatureCtrlInfoList() {
		return signatureCtrlInfoList;
	}
	
	public void setSignatureCtrlInfoList(List<SFSignatureCtrlInfoDto> signatureCtrlInfoList) {
		this.signatureCtrlInfoList = signatureCtrlInfoList;
	}
}
