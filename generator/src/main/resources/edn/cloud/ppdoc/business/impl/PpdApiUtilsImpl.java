package edn.cloud.ppdoc.business.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.integration.DocInfoDto;
import edn.cloud.business.dto.integration.SFDocumentType;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreUserInfoDto;
import edn.cloud.business.dto.ppd.api.PpdtokenDto;
import edn.cloud.ppdoc.business.connectivity.PpdHttpConnector;
import edn.cloud.ppdoc.business.connectivity.PpdHttpConnectorV1;
import edn.cloud.ppdoc.business.interfaces.PpdApiUtils;
import edn.cloud.sfactor.business.utils.QueryBuilder;
import edn.cloud.sfactor.persistence.dao.FieldsMappingPpdDAO;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;

public class PpdApiUtilsImpl implements PpdApiUtils {
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
	 * Returns the Company Document Types defined on PeopleDoc
	 * 
	 * @param Integer perPage
	 * @return SFDocumentType[]
	 */
	public SFDocumentType[] wServiceGetDocumentType(Integer perPage) {
		try {
			String data = this.getToken();
			Gson token = new Gson();

			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				if (perPage == null)
					perPage = 200;
				String userJson = PpdHttpConnector.getInstance().callGET("/api/v2/client/employee_document_types?per_page=" + perPage, objToken.getAccess_token(), false, "");
				return utilMapping.loadDocumentType(userJson);
			}
		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			return null;
		}

		return null;
	}

	public static boolean isZipped(final byte[] compressed) {
		return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	}

	/**
	 * call service update organization
	 * 
	 * @param String bearer
	 * @param String json
	 */
	public Boolean wServiceUpdateOrga(String id, Map<String, String> post) {
		Gson gson = new Gson();
		String JsonPost = gson.toJson(post);

		try {
			String data = this.getToken();
			Gson token = new Gson();

			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				this.setOrganization(objToken.getAccess_token(), id, JsonPost);
				return true;
			}
		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			return false;
		}

		return false;
	}
	
	/**
	 * Search Employees on PeopleDoc
	 * @param String externalId
	 * @return ArrayList<PpdCoreEmployeeInfoDto>
	 */
	public PpdCoreEmployeeInfoDto[] wServiceSearchEmployee(String externalId) 
	{
		try 
		{
			UtilMapping utilsMap = new UtilMapping();
			String data = this.getToken();
			Gson token = new Gson();
 			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);
 			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
 				
 				FieldsMappingPpdDAO fieldDAO = new FieldsMappingPpdDAO();
 				FieldsMappingPpd fi = fieldDAO.getFieldMappingByName("technical_id");

 				Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();
 				map.put("realuser", new ResultBuilderDto(fi.getNameDestination(), "default", ""));
 				Map<String, ResultBuilderDto> mapRes = QueryBuilder.getInstance().convert(map, externalId, "");

 				
 				
 				loggerSingle.info("/api/v2/client/employees?external_id=" + mapRes.get("realuser").getResult());
				String userJson = PpdHttpConnector.getInstance().callGET("/api/v2/client/employees?external_id=" + URLEncoder.encode(mapRes.get("realuser").getResult(), "UTF-8"), objToken.getAccess_token(), false, "");
				return utilsMap.loadPpdCoreEmployeeInfoDto(userJson);
			}
		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			return null;
		}
 		return null;
	}	

	public DocInfoDto setOrganization(String bearer, String id, String json) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnector.getInstance().callPUT("/api/v2/client/organizations/" + id, bearer, true, json);
		return utilMapping.loadDocumentsInfoFromJsom(userJson);
	}

	// ----------------------------------------------------------

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
}