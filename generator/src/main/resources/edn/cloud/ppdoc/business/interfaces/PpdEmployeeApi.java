package edn.cloud.ppdoc.business.interfaces;

import java.util.HashMap;

import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.integration.DocInfoDto;
import edn.cloud.business.dto.integration.UserInfo;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.attach.SFAttachResponseN1Dto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreUserInfoDto;
import edn.cloud.business.dto.ppd.api.PpdElectronicVaultOptionsDto;
import edn.cloud.business.dto.ppd.api.PpdUserDto;
import edn.cloud.sfactor.persistence.entities.AdminParameters;

public interface PpdEmployeeApi 
{	
	/**
	 * getTokens
	 * */
	public String getToken();	
	
	/*** 
	 * Returns the details of an Employee, call api v2 PPd client/employees
	 * @param String externaId 
	 * @return PPDocUser[]
	 * */
	public PpdUserDto[] wServiceGetEmployee(String externaId);
	
	/***
	 * Returns the details of an Employee, call api v1 PPd client/employees
	 * 
	 * @param String externaId
	 * @return PPDocUser[]
	 */
	public PpdUserDto wServiceGetEmployee_v1(String externaId);	
	
	
	/**
	 * call service getEmployeeDocuments
	 * @param String  ID of the Employee
	 * */
	public DocInfoDto[] wServiceEmployeeDocument(String idEmployee);
	
	/**
	 * call web service people doc /api/v2/client/core_employees/
	 * @param PpdCoreEmployeeInfoDto post 
	 * @return PpdCoreEmployeeInfoDto
	 * */
	public PpdCoreEmployeeInfoDto wServiceCreateEmployee(PpdCoreEmployeeInfoDto post);
	
	/**
	 * call web service people doc /api/v1/employees/ 
	 * @param PpdCoreEmployeeInfoDto post 
	 * @return PpdCoreEmployeeInfoDto
	 * */
	public PpdCoreEmployeeInfoDto wServiceUpdateEmployee(PpdCoreEmployeeInfoDto post);	
	
	
	/**
	 * update / patch employee
	 * @param PpdCoreEmployeeInfoDto post
	 * @return PpdCoreEmployeeInfoDto post
	 * */
	public PpdCoreEmployeeInfoDto wServiceUpdateEmployeePatch(PpdCoreEmployeeInfoDto post);
	
	
	/**
	 * This method is used to upload a document to an employee folder on PeopleDoc. POST /api/v1/enterprise/documents/
	 * @param String userId 
	 * @param SFAttachResponseN1Dto infoAttach
	 * @param String documentType
	 * @param HashMap<String,String> metadata
	 * @param AdminParameters paramFormatNameFile
	 * @return PpdGenErrorInfoDto 
	 * */
	public GenResponseInfoDto wServiceUploadEmployeeDocCompany(String userId,SFAttachResponseN1Dto infoAttach,String documentType, 
			String[] orgas,
			HashMap<String,String> metadata,
			AdminParameters paramFormatNameFile);
	
	/**
	 * This method is used to upload a document to an employee folder on PeopleDoc. POST /api/v1/enterprise/documents/
	 * @param String userId 
	 * @param SFAttachResponseN1Dto infoAttach
	 * @param String documentType
	 * @return PpdGenErrorInfoDto 
	 * */
	public GenResponseInfoDto wServiceCSVUploadManager(ContentFileInfo cfi);
	
	/**
	 * call service getEmployeeDocuments
	 *
	 * @param String idExternalDoc
	 * @return String
	 */
	public String wServiceEmployeeDocumentByExternalId(String idExternalDoc);	
	
	/**
	 * call web service people doc /api/v2/electronic_vault_options
	 * For European based clients only, update the information related to the Electronic Vault of an employee
	 * @param String idEmployeePpd
	 * @param PpdElectronicVaultOptionsDto post 
	 * @return GenErrorInfoDto
	 * */
	public GenResponseInfoDto wServiceElectronicVaultOptionsByEmployee(String idEmployeePpd,PpdElectronicVaultOptionsDto post);
	
 	/**
 	 * Get Core User of PeopleDoc
 	 * @param String userId
 	 * return PpdCoreUserInfoDto
 	 */	 	
	public PpdCoreUserInfoDto wServiceCallCoreUser(String Prefix, String userId);	
	
	/**
	 * Update User of PeopleDoc
	 * @param String userId
	 * @param String json
	 * @return ResponseGenericDto
	 */
	public ResponseGenericDto wServicePutCoreUser(String userId, String json);
	
	/**
	 * Create (POST) User of PeopleDoc
	 * @param String json
	 */
	public ResponseGenericDto wServicePostCoreUser(String json); 
}
