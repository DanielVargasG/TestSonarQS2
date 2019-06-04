package edn.cloud.business.dto;

import java.util.ArrayList;

public class SignatureGroupDto 
{
	private Long idGroup;
	private ArrayList<SignatureFieldDto> signatures;
	
	public SignatureGroupDto()
	{
		this.signatures = new ArrayList<SignatureFieldDto>();
	}		
	
	public ArrayList<SignatureFieldDto> getSignatures() {
		return signatures;
	}
	public void setSignatures(ArrayList<SignatureFieldDto> signatures) {
		this.signatures = signatures;
	}
	public Long getIdGroup() {
		return idGroup;
	}

	public void setIdGroup(Long idGroup) {
		this.idGroup = idGroup;
	}
	
}
