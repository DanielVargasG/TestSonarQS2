package edn.cloud.ppdoc.business.facade;

import java.util.HashMap;

import org.json.JSONArray;

import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.integration.DocInfoDto;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.attach.SFAttachResponseN1Dto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.business.dto.ppd.api.PpdElectronicVaultOptionsDto;
import edn.cloud.business.dto.ppd.api.PpdUserDto;
import edn.cloud.ppdoc.business.impl.PpdEmployeeApiImpl;
import edn.cloud.ppdoc.business.interfaces.PpdEmployeeApi;
import edn.cloud.sfactor.persistence.entities.AdminParameters;

public class PpdEmployeeApiFacade 
{
	private PpdEmployeeApi ppdEmployeeApi = new PpdEmployeeApiImpl();	 
	private UtilLogger loggerSingle = UtilLogger.getInstance();
	/*** 
	 * Returns the details of an Employee, call api v2 PPd client/employees
	 * @param String externaId 
	 * @return PPDocUser[]
	 * */
	public PpdUserDto[] wServiceGetEmployee(String externaId){
		return ppdEmployeeApi.wServiceGetEmployee(externaId);		
	}
	
	/***
	 * Returns the details of an Employee, call api v1 PPd client/employees
	 * 
	 * @param String externaId
	 * @return PPDocUser[]
	 */
	public PpdUserDto wServiceGetEmployee_v1(String externaId){
		return ppdEmployeeApi.wServiceGetEmployee_v1(externaId);
	}	
	
	/**
	 * call service getEmployeeDocuments
	 * @param String  ID of the userId
	 * */
	public DocInfoDto[] wServiceEmployeeDocument(String userId){
		return ppdEmployeeApi.wServiceEmployeeDocument(userId);
	}	
	
	/**
	 * call service getEmployeeDocuments
	 *
	 * @param String idExternalDoc
	 * @return Boolean
	 */
	public Boolean wServiceEmployeeDocExistByExternalId(String idExternalDoc)
	{
		try
		{	
			String response = ppdEmployeeApi.wServiceEmployeeDocumentByExternalId(UtilMapping.toSlug(idExternalDoc));
			
			loggerSingle.info(response);
			
			if(response!=null && !response.trim().equals(""))
			{
				JSONArray jArr = new JSONArray(response);
				if(jArr!=null && jArr.length()>0)
				{
					return true;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return false;		
	}
	

	
	
	/**
	 * This method is used to upload a document to an employee folder on PeopleDoc. POST /api/v1/enterprise/documents/
	 * @param String userId 
	 * @param SFAttachResponseN1Dto infoAttach
	 * @param String documentType
	 * @param HashMap<String,String>
	 * @param AdminParameters paramFormatNameFile
	 * @return PpdGenErrorInfoDto 
	 * */
	public GenResponseInfoDto wServiceUploadEmployeeDocCompany(String userId,SFAttachResponseN1Dto infoAttach,String documentType, String[] orgas,
			HashMap<String,String> metadata,
			AdminParameters paramFormatNameFile)
	{
		return ppdEmployeeApi.wServiceUploadEmployeeDocCompany(userId, infoAttach, documentType, orgas, metadata, paramFormatNameFile);
	}
	
	
	/**
	 * This method is used to upload a document to an employee folder on PeopleDoc. POST /api/v1/enterprise/documents/
	 * @param String userId 
	 * @param SFAttachResponseN1Dto infoAttach
	 * @param String documentType
	 * @return PpdGenErrorInfoDto 
	 * */
	public GenResponseInfoDto wServiceCSVUploadManager(ContentFileInfo cfi)
	{
		return ppdEmployeeApi.wServiceCSVUploadManager(cfi);
	}
	
	/**
	 * 
	 * 
	 * Proccess event update user
	 * 
	 * @param PpdCreateEmployeeInfoDto employeeUpdate
	 * @return PpdEmployeeInfoDto
	 */
	public PpdCoreEmployeeInfoDto actionUpdateEmployee(PpdCoreEmployeeInfoDto employeeUpdate) 
	{
		if (employeeUpdate.getId()!=null && !employeeUpdate.getId().equals("") && employeeUpdate.getErrors() == null || (employeeUpdate.getErrors() != null && employeeUpdate.getErrors().length <= 0)) 
		{
			// call web service ppd to update employe
			return ppdEmployeeApi.wServiceUpdateEmployee(UtilMapping.emptyPddEmployeeCreateVCore(employeeUpdate));
		}

		return employeeUpdate;
	}	
	
	/**
	 * update / patch employee
	 * @param PpdCoreEmployeeInfoDto post
	 * @return PpdCoreEmployeeInfoDto post
	 * */
	public PpdCoreEmployeeInfoDto actionUpdatePatchEmployee(PpdCoreEmployeeInfoDto post) {
		return ppdEmployeeApi.wServiceUpdateEmployeePatch(post);
	}
	
	/**
	 * 
	 * 
	 * Proccess event create user
	 * 
	 * @param PpdCoreEmployeeInfoDto employeeUpdate
	 * @return PpdCoreEmployeeInfoDto
	 */
	public PpdCoreEmployeeInfoDto actionCreateEmployee(PpdCoreEmployeeInfoDto employeeUpdate) 
	{
		if (employeeUpdate.getErrors() == null || (employeeUpdate.getErrors() != null && employeeUpdate.getErrors().length <= 0)) 
		{
			// call web service ppd to update employe
			return ppdEmployeeApi.wServiceCreateEmployee(UtilMapping.emptyPddEmployeeCreateVCore(employeeUpdate));
		}

		return employeeUpdate;
	}
	
	/**
	 * call web service people doc /api/v2/electronic_vault_options
	 * For European based clients only, update the information related to the Electronic Vault of an employee
	 * @param String idEmployeePpd
	 * @param PpdElectronicVaultOptionsDto post 
	 * @return GenErrorInfoDto
	 * */
	public GenResponseInfoDto wServiceElectronicVaultOptionsByEmployee(String idEmployeePpd,PpdElectronicVaultOptionsDto post) {
		return ppdEmployeeApi.wServiceElectronicVaultOptionsByEmployee(idEmployeePpd, post);
	}
}
