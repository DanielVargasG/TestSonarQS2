package edn.cloud.ppdoc.business.interfaces;

import java.util.Map;

import edn.cloud.business.dto.integration.SFDocumentType;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreUserInfoDto;

public interface PpdApiUtils 
{	 	
	/**
	 * getTokens
	 * */
	public String getToken();	
	
	/**
	 * call web service people doc /api/v2/oraganizations/
	 * @param String json type PpdEmployeeInfoDto 
	 * @param PpdCreateEmployeeInfoDto post 
	 * @return PpdEmployeeInfoDto
	 * */
	public Boolean wServiceUpdateOrga(String id, Map<String, String> orgaDto);		
	
	
	/**
	 * Returns the Company Document Types defined on PeopleDoc
	 * @param Integer perPage
	 * @return SFDocumentType[]
	 * */
	public SFDocumentType[] wServiceGetDocumentType(Integer perPage);
	
	/**
	 * Search Employees on PeopleDoc
	 * @param String externalId
	 * @return ArrayList<PpdCoreEmployeeInfoDto>
	 */
	public PpdCoreEmployeeInfoDto[] wServiceSearchEmployee(String externalId);
}
