package edn.cloud.ppdoc.business.facade;

import java.util.Map;
import edn.cloud.business.dto.integration.SFDocumentType;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.ppdoc.business.impl.PpdApiUtilsImpl;
import edn.cloud.ppdoc.business.interfaces.PpdApiUtils;

public class PpdApiUtilsFacade 
{
	private PpdApiUtils ppdApiUtils = new PpdApiUtilsImpl();	 
	
	/**
	 * getTokens
	 * */
	public String getToken()
	{
		return ppdApiUtils.getToken();
	}
	
	
	
	/**
 	 * call web service people doc /api/v2/organizations/
 	 * @param Boolean return
 	 * @param PpdCreateEmployeeInfoDto 
 	 * */
 	public Boolean wServiceUpdateOrga(String id, Map<String, String> orgaDto){		
 		return ppdApiUtils.wServiceUpdateOrga(id, orgaDto);
 	}
 	
 	
	/**
	 * Returns the Company Document Types defined on PeopleDoc
	 * @param Integer perPage
	 * @return SFDocumentType[]
	 * */
	public SFDocumentType[] wServiceGetDocumentType(Integer perPage){
		return ppdApiUtils.wServiceGetDocumentType(perPage);
	}
	
	/**
	 * Search Employees on PeopleDoc
	 * @param String externalId
	 * @return ArrayList<PpdCoreEmployeeInfoDto>
	 */
	public PpdCoreEmployeeInfoDto[] wServiceSearchEmployee(String externalId) {
		return ppdApiUtils.wServiceSearchEmployee(externalId);
	}	
}
