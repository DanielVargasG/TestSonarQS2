package edn.cloud.ppdoc.business.impl;

import java.io.IOException;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.TemplateUpdateDto;
import edn.cloud.business.dto.integration.DocTemplateInfo;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.business.dto.ppd.api.PpdtokenDto;
import edn.cloud.ppdoc.business.connectivity.PpdHttpConnector;
import edn.cloud.ppdoc.business.interfaces.PpdApiUtils;
import edn.cloud.ppdoc.business.interfaces.PpdTemplate;
import edn.cloud.sfactor.persistence.entities.Folder;

public class PpdTemplateImpl implements PpdTemplate {
	private UtilLogger loggerSingle = UtilLogger.getInstance();
	private UtilMapping utilMapping = new UtilMapping();
	private PpdApiUtils ppdApiUtils = new PpdApiUtilsImpl();

	/**
	 * Generate a document from a template in people doc platform
	 * 
	 * @param String
	 *            id
	 * @param ContentFileInfo
	 *            file
	 */
	public GenResponseInfoDto createTemplateDocument(String id, ContentFileInfo file) {
		PpdtokenDto objToken = new PpdtokenDto();
		GenResponseInfoDto msg = new GenResponseInfoDto();
		try {
			String data = ppdApiUtils.getToken();
			Gson token = new Gson();
			objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode().toString())) {
				this.postDocument(objToken.getAccess_token(), id, file);
				msg.setFlag(Boolean.TRUE);
				return msg;
			} else {
				loggerSingle.error(PpdTemplateImpl.class.getName() + " createTemplateDocument: " + "Session was not opened");
				msg.setMessage(" createTemplateDocument: " + "Session was not opened");
				msg.setFlag(Boolean.FALSE);
				return msg;
			}
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(PpdTemplateImpl.class.getName() + " createTemplateDocument: " + "Error upload file in PPDoc");
			loggerSingle.error(PpdTemplateImpl.class.getName() + " createTemplateDocument: " + ex.toString());
			msg.setMessage(" createTemplateDocument: " + ex.getMessage());
			msg.setFlag(Boolean.FALSE);
			return msg;
		}
	}

	/***
	 * If the id already does not exists yet, the template will be created. If
	 * the id exists, update the title and description by defining metadata in
	 * json format.
	 * 
	 * @param String
	 *            val { "title": "string", "description": "string", "locale":
	 *            "string", "format": "DOCX", "enabled": true,
	 *            "validity_start_date": "2017-08-14T19:38:20.717+00:00",
	 *            "validity_end_date": "2017-08-14T19:38:20.717+00:00",
	 *            "active_version": 0, "created_at":
	 *            "2017-08-14T19:38:20.717+00:00", "updated_at":
	 *            "2017-08-14T19:38:20.717+00:00", "id": "string",
	 *            "latest_version": 0 }
	 */
	public TemplateInfoDto createTemplateId(String val) {
		TemplateInfoDto data2 = new TemplateInfoDto();

		try {
			String data = ppdApiUtils.getToken();
			Gson token = new Gson();
			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken != null && objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				data2 = this.postDocumentMetadata(objToken.getAccess_token(), val);
				return data2;
			} else {
				loggerSingle.error("> error - Session was not opened");
			}
		} catch (IOException | InvalidResponseException ex) {

			loggerSingle.error(PpdTemplateImpl.class.getName() + " : " + "> error - Connection was not working with PPDoc");
			loggerSingle.error(ex.toString());
			return null;
		}

		return null;
	}
	
	
	public Folder createFolderId(String val) {
		Folder data2 = new Folder();

		

		return null;
	}

	public TemplateInfoDto  updateTemplateId(String id, String val) 
	{
		TemplateInfoDto data2 = new TemplateInfoDto();
		
		try 
		{
			String data = ppdApiUtils.getToken();
			Gson token = new Gson();
			PpdtokenDto objToken =  token.fromJson(data, PpdtokenDto.class);
	
			if(objToken!=null && objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) 
			{
				
				TemplateUpdateDto rm = token.fromJson(val, TemplateUpdateDto.class);
				
				data2 = this.putDocumentMetadata(objToken.getAccess_token(), id, token.toJson(rm));
				return data2;
			}
			else 
			{
				loggerSingle.error("> error - Session was not opened");
			}
		} catch (IOException | InvalidResponseException ex) {
	
			loggerSingle.error(PpdTemplateImpl.class.getName()+" : "+"> error - Connection was not working with PPDoc");
			loggerSingle.error(ex.toString());
			return null;
		}				
		
		return null;
	}

	/**
	 * List all placeholder fields in an existing template document for a given
	 * version.
	 * 
	 * @param String
	 *            id
	 * @param String
	 *            version
	 */
	public PpdDocGenRequestPayloadDto getFieldsTemplateByVersion(String id, String version) {
		PpdDocGenRequestPayloadDto data2 = new PpdDocGenRequestPayloadDto();

		try {
			String data = ppdApiUtils.getToken();
			Gson token = new Gson();
			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				data2 = this.getFieldsTemplateByVersion(objToken.getAccess_token(), id, version);
				return data2;
			} else {
				loggerSingle.error("> error - Session was not opened");
			}
		} catch (Exception ex) {

			loggerSingle.error(PpdTemplateImpl.class.getName() + " : " + "> error - Connection was not working with PPDoc getFieldsTemplateByVersion");
			loggerSingle.error("getFieldsTemplateByVersion " + ex.toString());
			ex.printStackTrace();
			return null;
		}

		return null;
	}

	/**
	 * get info template
	 * 
	 * @param String
	 *            id
	 */
	public TemplateInfoDto getInfoTemplate(String id) {
		TemplateInfoDto response = new TemplateInfoDto();

		try {
			String data = ppdApiUtils.getToken();
			Gson token = new Gson();
			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				response = this.getTemplate(objToken.getAccess_token(), id);
				return response;
			} else {
				loggerSingle.error("> error - Session was not opened");
			}
		} catch (Exception ex) {

			loggerSingle.error(PpdTemplateImpl.class.getName() + " : " + "> error - Connection was not working with PPDoc getInfoTemplate");
			loggerSingle.error("getInfoTemplate " + ex.toString());
			ex.printStackTrace();
			return null;
		}

		return null;
	}

	/**
	 * Get preview document
	 * 
	 * @param idTemplatePpdNumber
	 *            num
	 * @param Number
	 *            num
	 */
	public byte[] getPrevDocument(String idTemplatePpd, Number num, Number page) {
		try {
			String data = ppdApiUtils.getToken();
			Gson token = new Gson();
			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);
			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				return this.getPrevDocument(objToken.getAccess_token(), idTemplatePpd, num,page);
			} else {
				loggerSingle.error("> error - Session was not opened");
			}
		} catch (IOException | InvalidResponseException ex) {

			loggerSingle.error(PpdTemplateImpl.class.getName() + " : " + "> error - Connection was not working with PPDoc");
			loggerSingle.error(ex.toString());
			return null;
		}

		return null;
	}

	/**
	 * Delete document
	 * 
	 * @param idTemplatePpdNumber
	 *            num
	 */
	public String deleteDocument(String idTemplatePpd) {
		try {
			String data = ppdApiUtils.getToken();
			Gson token = new Gson();
			PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				return this.deleteDocument(objToken.getAccess_token(), idTemplatePpd);
			} else {
				loggerSingle.error("> error - Session was not opened");
			}
		} catch (IOException | InvalidResponseException ex) {

			loggerSingle.error(PpdTemplateImpl.class.getName() + " : " + "> error -delete document in PPDoc");
			loggerSingle.error(ex.getMessage());
			return null;
		}

		return null;
	}
	
	public String getNumberPages(String id, Number num) {
		
		try {
		String data = ppdApiUtils.getToken();
		Gson token = new Gson();
		PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);
		
		String json =  PpdHttpConnector.getInstance().callHeader("document_generation_templates/" + id + "/versions/" + num + "/file", objToken.getAccess_token(), true, null, "Page-Count");
		loggerSingle.error(json);
		return json;
		
		}catch (IOException | InvalidResponseException ex) {

			loggerSingle.error(PpdTemplateImpl.class.getName() + " : " + "> error -Get Info Page document in PPDoc");
			loggerSingle.error(ex.getMessage());
			return "";
		}
	}
	
public String getGeneratedNumberPages(String id, Number num) {
		
		try {
		String data = ppdApiUtils.getToken();
		Gson token = new Gson();
		PpdtokenDto objToken = token.fromJson(data, PpdtokenDto.class);
		
		String json =  PpdHttpConnector.getInstance().callHeader("document_generations/" + id + "/file", objToken.getAccess_token(), true, null, "Page-Count");
		loggerSingle.error(json);
		return json;
		
		}catch (IOException | InvalidResponseException ex) {

			loggerSingle.error(PpdTemplateImpl.class.getName() + " : " + "> error -Get Info Page document in PPDoc");
			loggerSingle.error(ex.getMessage());
			return "";
		}
	}

	// ***************************************************

	public String deleteDocument(String bearer, String id) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnector.getInstance().callDELETE("document_generation_templates/" + id, bearer, "");
		return userJson;
	}

	public TemplateInfoDto postDocumentMetadata(String bearer, String json) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnector.getInstance().callPOST("document_generation_templates", bearer, json, null);
		return utilMapping.loadDocumentTemplateMetadataFromJsom(userJson);
		// return userJson;
	}

	public TemplateInfoDto putDocumentMetadata(String bearer, String id, String json) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnector.getInstance().callPATCH("document_generation_templates/" + id, bearer, null, json);
		return utilMapping.loadDocumentTemplateMetadataFromJsom(userJson);
		// return userJson;
	}

	public byte[] getPrevDocument(String bearer, String id, Number num, Number page) throws IOException, InvalidResponseException {
		byte[] userJson = PpdHttpConnector.getInstance().callImage("document_generation_templates/" + id + "/versions/" + num + "/preview/"+page, bearer, true);
		return userJson;
	}

	
	/**
	 * List all placeholder fields in an existing template document for a given
	 * version. /document_generation_templates/{id}/versions/{number}/fields
	 * 
	 * @param String
	 *            bearer
	 * @param String
	 *            id
	 * @param String
	 *            version
	 */
	public PpdDocGenRequestPayloadDto getFieldsTemplateByVersion(String bearer, String id, String version) throws IOException, InvalidResponseException {
		String callString = PpdHttpConnector.getInstance().callGET("document_generation_templates/" + id + "/versions/" + version + "/fields", bearer, false, null);
		return utilMapping.loadGenerateVariablesMetadataFromJson(callString);
	}

	public TemplateInfoDto[] getTemplates(String bearer) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnector.getInstance().callGET("document_generation_templates", bearer, false, null);
		return utilMapping.loadDocumentsTemplateFromJsom(userJson);
		// return userJson;
	}

	public TemplateInfoDto getTemplate(String bearer, String id) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnector.getInstance().callGET("document_generation_templates/" + id, bearer, false, null);
		return utilMapping.loadDocumentTemplateMetadataFromJsom(userJson);
		// return userJson;
	}

	public DocTemplateInfo postDocument(String bearer, String id, ContentFileInfo input) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnector.getInstance().callPOST("document_generation_templates/" + id + "/file", bearer, "", input);
		return utilMapping.loadDocumentTemplateFromJsom(userJson);
		// return userJson;
	}
}
