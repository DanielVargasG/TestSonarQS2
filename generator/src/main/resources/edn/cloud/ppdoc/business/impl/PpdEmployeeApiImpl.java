package edn.cloud.ppdoc.business.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.integration.DocInfoDto;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.attach.PpdAttachInfoDto;
import edn.cloud.business.dto.integration.attach.SFAttachResponseN1Dto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreUserInfoDto;
import edn.cloud.business.dto.ppd.api.PpdElectronicVaultOptionsDto;
import edn.cloud.business.dto.ppd.api.PpdUserDto;
import edn.cloud.business.dto.ppd.api.PpdtokenDto;
import edn.cloud.ppdoc.business.connectivity.PpdHttpConnector;
import edn.cloud.ppdoc.business.connectivity.PpdHttpConnectorV1;
import edn.cloud.ppdoc.business.interfaces.PpdEmployeeApi;
import edn.cloud.sfactor.business.utils.QueryBuilder;
import edn.cloud.sfactor.persistence.dao.FieldsMappingPpdDAO;
import edn.cloud.sfactor.persistence.dao.LoggerDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;
import edn.cloud.sfactor.persistence.entities.LoggerAction;

public class PpdEmployeeApiImpl implements PpdEmployeeApi
{
	private UtilLogger loggerSingle = UtilLogger.getInstance();
	private UtilMapping utilMapping = new UtilMapping();

	/**
	 * getTokens
	 */
	public String getToken() {
		try {
			String userJson = PpdHttpConnector.getInstance().executePOST("tokens");
			return userJson;
		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			return null;
		}
	}

	/**
	 * call web service people doc /api/v2/client/core_employees/
	 * @param PpdCoreEmployeeInfoDto post 
	 * @return PpdCoreEmployeeInfoDto
	 * */
	public PpdCoreEmployeeInfoDto wServiceCreateEmployee(PpdCoreEmployeeInfoDto post) {
		Gson gson = new Gson();
		String vJsonPost = gson.toJson(post);

		loggerSingle.info("CREATE EMPLOYEE FUNCTION");
		loggerSingle.info(vJsonPost);
		String responsePpd = "";
		try {
			String data = this.getToken();
			PpdtokenDto objToken = gson.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) 
			{
				responsePpd = PpdHttpConnector.getInstance().callPOST("employees",objToken.getAccess_token(),vJsonPost,null);
				PpdCoreEmployeeInfoDto responseDto = utilMapping.loadEmployeeUpdateJsom(responsePpd);
				responseDto.setObservations(responsePpd + " " + UtilMapping.toStringHtmlFormat(vJsonPost));
				return responseDto;
			}
		} catch (Exception e) 
		{
			//e.printStackTrace();
			PpdCoreEmployeeInfoDto employeeError = new PpdCoreEmployeeInfoDto();
			GenResponseInfoDto[] errorList = new GenResponseInfoDto[1];

			GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
			ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
			ppdErrorInfoDto.setField("Error send create employee ppd");
			ppdErrorInfoDto.setMessage(e.getMessage() + " "+ UtilMapping.toStringHtmlFormat(vJsonPost));
			errorList[0] = ppdErrorInfoDto;
			employeeError.setErrors(errorList);
			return employeeError;
		}

		return null;
	}
	
	
	/**
	 * update / patch employee
	 * @param PpdCoreEmployeeInfoDto post
	 * @return PpdCoreEmployeeInfoDto post
	 * */
	public PpdCoreEmployeeInfoDto wServiceUpdateEmployeePatch(PpdCoreEmployeeInfoDto post)
	{
		Gson gson = new Gson();
		String vJsonPost = gson.toJson(post);
		
		String responsePpd = "";
		
		try 
		{
			String data = this.getToken();
			PpdtokenDto objToken = gson.fromJson(data, PpdtokenDto.class);

			
			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) 
			{
				responsePpd = PpdHttpConnector.getInstance().callPATCH("employees/"+post.getId(),objToken.getAccess_token(),true,vJsonPost);
				PpdCoreEmployeeInfoDto responseDto = utilMapping.loadEmployeeUpdateJsom(responsePpd);
				responseDto.setObservations(responsePpd + " " + UtilMapping.toStringHtmlFormat(vJsonPost));
				return responseDto;
			}
		} catch (Exception e) {
			e.printStackTrace();
			PpdCoreEmployeeInfoDto employeeError = new PpdCoreEmployeeInfoDto();
			GenResponseInfoDto[] errorList = new GenResponseInfoDto[1];

			GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
			ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
			ppdErrorInfoDto.setField("Error send update/patch employee ppd");
			ppdErrorInfoDto.setMessage(e.getMessage() + " "+ UtilMapping.toStringHtmlFormat(vJsonPost));
			errorList[0] = ppdErrorInfoDto;
			employeeError.setErrors(errorList);
			return employeeError;
		}

		return null;
	}
	
	/**
	 * call web service people doc /api/v2/employees/ 
	 * @param PpdCoreEmployeeInfoDto post 
	 * @return PpdCoreEmployeeInfoDto
	 * */
	public PpdCoreEmployeeInfoDto wServiceUpdateEmployee(PpdCoreEmployeeInfoDto post){
		Gson gson = new Gson();
		String vJsonPost = gson.toJson(post);

		loggerSingle.info("UPDATE EMPLOYEE FUNCTION");
		loggerSingle.info(vJsonPost);
		
		String responsePpd = "";
		try {
			String data = this.getToken();
			PpdtokenDto objToken = gson.fromJson(data, PpdtokenDto.class);

			
			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) 
			{
				responsePpd = PpdHttpConnector.getInstance().callPUT("employees/"+post.getId(),objToken.getAccess_token(),true,vJsonPost);
				PpdCoreEmployeeInfoDto responseDto = utilMapping.loadEmployeeUpdateJsom(responsePpd);
				responseDto.setObservations(responsePpd + " " + UtilMapping.toStringHtmlFormat(vJsonPost));
				return responseDto;
			}
		} catch (Exception e) {
			e.printStackTrace();
			PpdCoreEmployeeInfoDto employeeError = new PpdCoreEmployeeInfoDto();
			GenResponseInfoDto[] errorList = new GenResponseInfoDto[1];

			GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
			ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
			ppdErrorInfoDto.setField("Error send update employee ppd");
			ppdErrorInfoDto.setMessage(e.getMessage() + " "+ UtilMapping.toStringHtmlFormat(vJsonPost));
			errorList[0] = ppdErrorInfoDto;
			employeeError.setErrors(errorList);
			return employeeError;
		}

		return null;
	}	

	/***
	 * Returns the details of an Employee, call api v2 PPd client/employees
	 * 
	 * @param String externaId
	 * @return PPDocUser[]
	 */
	public PpdUserDto[] wServiceGetEmployee(String externaId) {
		try {
			String data = this.getToken();
			Gson token = new Gson();

			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				String userJson = PpdHttpConnector.getInstance().callGET("/api/v2/client/employees?external_id=" + externaId, objToken.getAccess_token(), false, "");
				return utilMapping.loadUserJsom(userJson);
			}
		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			return null;
		}

		return null;
	}
	
	/***
	 * Returns the details of an Employee, call api v1 PPd client/employees
	 * 
	 * @param String externaId
	 * @return PPDocUser[]
	 */
	public PpdUserDto wServiceGetEmployee_v1(String externaId) 
	{
		try 
		{
			String data = this.getToken();
			Gson token = new Gson();

			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				String userJson = PpdHttpConnectorV1.getInstance().executeGET("/api/v1/employees/" + externaId, "", "");
				return utilMapping.loadUser_v1Jsom(userJson);
			}
		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			return null;
		}

		return null;
	}	

	/**
	 * This method is used to upload a document to an employee folder on PeopleDoc.
	 * 
	 * @param String userId
	 * @param SFAttachResponseN1Dto infoAttach
	 * @param String documentType
	 * @param HashMap<String,String> metadata
	 * @return PpdGenErrorInfoDto
	 */
	public GenResponseInfoDto wServiceUploadEmployeeDocCompany(String userId, 
															SFAttachResponseN1Dto infoAttach, 
															String documentType, 
															String[] orgas,
															HashMap<String,String> metadata,
															AdminParameters paramFormatNameFile) 
	{
		GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();

		// ---------------------------------------
		// build object to ppd
		PpdAttachInfoDto ppdAttachFile = new PpdAttachInfoDto();
		ppdAttachFile.setExternal_unique_id(infoAttach.getD().getAttachmentId());
		
		String title = UtilMapping.getFileNameToSendPpd(
				paramFormatNameFile!=null?paramFormatNameFile.getValue():"", 
				UtilMapping.getStripExtension(infoAttach.getD().getFileName()), 
				documentType, 
				userId);
		
		ppdAttachFile.setTitle(UtilMapping.toStringSimpleFormat(title,""));
		
		ppdAttachFile.setDate(UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd", new Date()));
		
		FieldsMappingPpdDAO fieldDAO = new FieldsMappingPpdDAO();
		FieldsMappingPpd fi = fieldDAO.getFieldMappingByName("technical_id");
		
		Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();
		map.put("realuser", new ResultBuilderDto(fi.getNameDestination(), "default", ""));
		Map<String, ResultBuilderDto> mapRes = QueryBuilder.getInstance().convert(map, userId, "");
		
		ppdAttachFile.setEmployee_technical_id(mapRes.get("realuser").getResult());
		ppdAttachFile.setDocument_type_code(documentType);
		ppdAttachFile.setOrganization_codes(orgas);
		
		if(metadata!=null)
			ppdAttachFile.setDocument_type_metas(metadata);

		Gson json = new Gson();
		String jsonStr = json.toJson(ppdAttachFile);
		String responseString ="";
		// ----------------------------------------

		loggerSingle.gson(ppdAttachFile);
		
		try {
			String data = this.getToken();
			PpdtokenDto objToken = json.fromJson(data, PpdtokenDto.class);

			
			// ---------------------------------------
			// content file
			ContentFileInfo file = new ContentFileInfo();
			ByteArrayInputStream bs = null;

			if (infoAttach.getD().getModule().equals("ONB")) {

				byte[] zipData = Base64.decodeBase64(infoAttach.getD().getFileContent());

				// String result = IOUtils.toString(zi);
				GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(zipData));

				bs = new ByteArrayInputStream(IOUtils.toByteArray(gis));

			} else {
				bs = new ByteArrayInputStream(Base64.decodeBase64(infoAttach.getD().getFileContent()));
			}

			byte[] bytesArray;
			try {
				bytesArray = read(bs);
				file.setFile(bytesArray);
			} catch (IOException e) {
				e.printStackTrace();
			}

			file.setFileName(UtilMapping.toStringSimpleFormat(infoAttach.getD().getFileName()," "));
			file.setId(infoAttach.getD().getAttachmentId());

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				responseString = this.callUploadCompanyDocument(objToken.getAccess_token(), file, jsonStr);
				ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
				ppdErrorInfoDto.setMessage(responseString + " :: " + jsonStr);
				return ppdErrorInfoDto;
			}
		} catch (Exception e) {
			loggerSingle.error(responseString);
			ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
			ppdErrorInfoDto.setMessage(e.getMessage() + " " + jsonStr);
			return ppdErrorInfoDto;
		}

		return ppdErrorInfoDto;
	}

	/**
	 * This method is used to upload a document to an employee folder on PeopleDoc.
	 * 
	 * @param String userId
	 * @param SFAttachResponseN1Dto infoAttach
	 * @param String documentType
	 * @return PpdGenErrorInfoDto
	 */
	public GenResponseInfoDto wServiceCSVUploadManager(ContentFileInfo cfi) {
		GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();

		// ----------------------------------------
		Gson json = new Gson();
		try {
			String data = this.getToken();
			PpdtokenDto objToken = json.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				String responseString = this.callCSVUploadManager(objToken.getAccess_token(), cfi);
				ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
				ppdErrorInfoDto.setMessage(responseString);
				return ppdErrorInfoDto;
			}
		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			this.logger(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode().toString(), "Errors : " + e.getMessage(),UtilCodesEnum.CODE_JOB_SIGNATURE.getCode(), "Error");
			ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
			ppdErrorInfoDto.setMessage(e.getMessage());
			return ppdErrorInfoDto;
		}

		return ppdErrorInfoDto;
	}

	/**
	 * call service getEmployeeDocuments
	 * 
	 * @param String bearer
	 * @param String json
	 */
	public DocInfoDto[] wServiceEmployeeDocument(String json) {
		try {
			String data = this.getToken();
			Gson token = new Gson();

			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				return this.callEmployeeDocuments(objToken.getAccess_token(), json);
			}
		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			return null;
		}

		return null;
	}
	
	/**
	 * call service getEmployeeDocuments
	 *
	 * @param String idExternalDoc
	 * @return String
	 */
	public String wServiceEmployeeDocumentByExternalId(String idExternalDoc) {
		try {
			String data = this.getToken();
			Gson token = new Gson();

			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				return this.callEmployeeDocumentsByExternalId(objToken.getAccess_token(),idExternalDoc);
			}
		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			return "";
		}

		return "";
	}	
	
	
	/**
	 * call web service people doc /api/v2/electronic_vault_options
	 * For European based clients only, update the information related to the Electronic Vault of an employee
	 * @param String idEmployeePpd
	 * @param PpdElectronicVaultOptionsDto post 
	 * @return GenErrorInfoDto
	 * */
	public GenResponseInfoDto wServiceElectronicVaultOptionsByEmployee(String idEmployeePpd,PpdElectronicVaultOptionsDto post) 
	{
		loggerSingle.info("ELECTRONIC VAULT");
		loggerSingle.info("ELECTRONIC VAULT");
		Gson gson = new Gson();
		GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
		String vJsonPost = gson.toJson(post);
		
		try {
			String data = this.getToken();
			PpdtokenDto objToken = gson.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) 
			{
				String responsePpd = PpdHttpConnector.getInstance().callPATCH("electronic_vault_options/"+idEmployeePpd,objToken.getAccess_token(),false, vJsonPost);
				ppdErrorInfoDto.setMessage(responsePpd + " " + UtilMapping.toStringHtmlFormat(vJsonPost));
				return ppdErrorInfoDto;
			}
		} catch (Exception e) 
		{
			PpdCoreEmployeeInfoDto employeeError = new PpdCoreEmployeeInfoDto();
			GenResponseInfoDto[] errorList = new GenResponseInfoDto[1];

			ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
			ppdErrorInfoDto.setField("Error electronic_vault_options ppd");
			ppdErrorInfoDto.setMessage(e.getMessage() + " "+ UtilMapping.toStringHtmlFormat(vJsonPost));
			errorList[0] = ppdErrorInfoDto;
			employeeError.setErrors(errorList);

			loggerSingle.error(e.getMessage());
			return ppdErrorInfoDto;
		}

		return null;
	}
	
	/**
	 * Get Core User of PeopleDoc
	 * @param String userId
	 * @return PpdCoreUserInfoDto
	 */
	public PpdCoreUserInfoDto wServiceCallCoreUser(String prefix, String userId) 
	{
		try 
		{
			PpdCoreUserInfoDto ppdUserCore = new PpdCoreUserInfoDto();
			String data = this.getToken();
			Gson gson = new Gson();

			PpdtokenDto objToken = gson.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				String userJson = PpdHttpConnector.getInstance().callGET("/api/v2/client/users?external_id=" + prefix.replace("@@", userId), objToken.getAccess_token(), false, "");
				
				if(userJson!=null && !userJson.equals(""))
				{
					JSONArray jarray = new JSONArray(userJson);
					if(jarray!=null && jarray.length()>0){
						ppdUserCore = gson.fromJson(jarray.get(0).toString(), PpdCoreUserInfoDto.class);
						return ppdUserCore;
					}
				}
			}
		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			return null;
		}

		return null;
	}	
	
	/**
	 * Update User of PeopleDoc
	 * @param String userId
	 * @param String json
	 * @return GenErrorInfoDto
	 */
	public ResponseGenericDto wServicePutCoreUser(String userId, String json) 
	{
		ResponseGenericDto response = new ResponseGenericDto();
		String vJson = "";
		
		try 
		{
			String data = this.getToken();
			Gson token = new Gson();

			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				vJson = PpdHttpConnector.getInstance().callPUT("/api/v2/client/users/" + userId, objToken.getAccess_token(), false, json);
				response.setCode(UtilCodesEnum.CODE_OK.getCode());
				response.setMessage(UtilMapping.toStringHtmlFormat(vJson));
				loggerSingle.info(vJson);
			}
		} catch (Exception e) 
		{						
			response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
			response.setField("Error wServicePutCoreUser ppd");
			response.setMessage(e.getMessage() + " "+ UtilMapping.toStringHtmlFormat(vJson));

			loggerSingle.error(e.getMessage());
			return response;
		}

		return response;
	}	
	
	/**
	 * Create (POST) User of PeopleDoc
	 * @param String json
	 * @return GenErrorInfoDto
	 */
	public ResponseGenericDto wServicePostCoreUser(String json) 
	{
		ResponseGenericDto response = new ResponseGenericDto();
		String vJson = "";

		try 
		{
			String data = this.getToken();
			Gson token = new Gson();

			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				vJson = PpdHttpConnector.getInstance().callPOST("/api/v2/client/users/", objToken.getAccess_token(), json, null);
			}
		} catch (Exception e) 
		{
			response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
			response.setField("Error wServicePostCoreUser ppd");
			response.setMessage(e.getMessage() + " "+ UtilMapping.toStringHtmlFormat(vJson));			
			loggerSingle.error(e.getMessage());
			return response;
		}

		return response;
	}	

	// ----------------------------------------------------------

	private void logger(String code, String message, String user, String status) {
		LoggerDAO list = new LoggerDAO();
		LoggerAction li = new LoggerAction();
		li.setCode(code);
		li.setMessage(message);
		li.setUser(user);
		li.setStatus(status);
		list.saveNew(li);
	}

	/**
	 * This method is used to upload a document to an employee folder on PeopleDoc.
	 * 
	 * @param String Token
	 * @param ContentFileInfo fl
	 * @param String json
	 */
	public String callUploadCompanyDocument(String Token, ContentFileInfo fl, String json) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnectorV1.getInstance().executeFile("/api/v1/enterprise/documents/", fl, json);
		return userJson;
	}

	/**
	 * This method is used to upload a document to an employee folder on PeopleDoc.
	 * 
	 * @param String Token
	 * @param ContentFileInfo fl
	 * @param String json
	 */
	public String callCSVUploadManager(String Token, ContentFileInfo fl) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnectorV1.getInstance().executeFile("/api/v1/import/managers/", fl, "");
		return userJson;
	}
	
	/**
	 * get all document
	 */
	private DocInfoDto[] callEmployeeDocuments(String bearer, String json) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnector.getInstance().callGET("/api/v2/client/employee_documents?per_page=100&employee_id=" + json, bearer, false, "");
		return utilMapping.loadDocumentsListInfoFromJsom(userJson);
	}
	
	/**
	 * get all document by id external id dococument
	 */
	private String callEmployeeDocumentsByExternalId(String bearer, String idExternalDoc) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnector.getInstance().callGET("/api/v2/client/employee_documents?external_reference="+idExternalDoc+"&per_page=1", bearer, false, "");
		return userJson;
	}	
	
	/**
	* 
	* */
	private byte[] read(ByteArrayInputStream bais) throws IOException {
		byte[] array = new byte[bais.available()];
		bais.read(array);
		return array;
	}
	

	public static boolean isZipped(final byte[] compressed) {
		return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	}	
}