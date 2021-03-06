package edn.cloud.ppdoc.business.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.GenerateInfoDto;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.SFUserDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.ppd.api.PpdDocGenFullWithErrors;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.business.dto.ppd.api.PpdSignatureInfoDto;
import edn.cloud.business.dto.ppd.api.PpdSigningDataDto;
import edn.cloud.business.dto.ppd.api.PpdSigningHeadDto;
import edn.cloud.business.dto.ppd.api.PpdTextOccurrencesDto;
import edn.cloud.business.dto.ppd.api.PpdtokenDto;
import edn.cloud.ppdoc.business.connectivity.PpdHttpConnector;
import edn.cloud.ppdoc.business.connectivity.PpdHttpConnectorV1;
import edn.cloud.ppdoc.business.interfaces.PpdApiUtils;
import edn.cloud.ppdoc.business.interfaces.PpdGenerator;
import edn.cloud.ppdoc.business.interfaces.PpdTemplate;
import edn.cloud.sfactor.persistence.entities.Document;
import edn.cloud.sfactor.persistence.entities.ErrorLog;
import edn.cloud.sfactor.persistence.entities.Generated;

public class PpdGeneratorImpl implements PpdGenerator {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private PpdtokenDto objToken;
	private UtilMapping utilMapping = new UtilMapping();
	private UtilLogger loggerSingle = UtilLogger.getInstance();
	private PpdTemplate ppTemplateGenerator = new PpdTemplateImpl();
	private PpdApiUtils ppdApiUtils = new PpdApiUtilsImpl();

	/**
	 * 
	 * @param ContentFileInfo file
	 * @param PpdSignatureInfoDto post
	 * @return PpdSigningHeadDto
	 */
	public PpdSigningHeadDto wServiceSetSignatureDocument(ContentFileInfo file, PpdSignatureInfoDto post) {
		PpdSigningHeadDto signingHeadResponse = new PpdSigningHeadDto();
		Gson gson = new Gson();
		String requestJson = gson.toJson(post);
		String response = null;

		try {
			response = this.callSetSignatureDocument("X-KEY", file, gson.toJson(post));
			if (response != null) {
				signingHeadResponse = gson.fromJson(response, PpdSigningHeadDto.class);
			}

			return signingHeadResponse;
		} catch (Exception e) {
			if (e.getMessage().contains(UtilCodesEnum.CODE_ERROR_409.getCode()))
				signingHeadResponse.setReason("There is already a signature process for this generation");
			else if(e.getMessage().contains(UtilCodesEnum.CODE_ERROR_400.getCode()))
				signingHeadResponse.setReason(e.getMessage());
			else
				signingHeadResponse.setReason(e.getMessage());
			signingHeadResponse.setStatus(UtilCodesEnum.CODE_ERROR.getCode());
			return signingHeadResponse;
		}
	}

	/**
	 * return list status of signatures
	 * 
	 * @param String idGenerated
	 * @param String status
	 * @return PpdSigningProcessesDataDto
	 */
	public PpdSigningDataDto wServiceGetSigningProcess(String idGenerated, String status) {
		PpdSigningDataDto responseDto = new PpdSigningDataDto();

		try {
			String data = ppdApiUtils.getToken();
			Gson gson = new Gson();

			PpdtokenDto objToken = gson.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				String response = this.callGetSignatureDocument(idGenerated, status, "", "", "");

				if (response != null) {

					responseDto = utilMapping.loadPpdSignatureInfoDto(response);
				}

				return responseDto;
			} else {
				logger.error("> error - Session was not opened");
				return null;
			}

		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			return responseDto;
		}
	}

	/**
	 * Get Generated File to ppd
	 * 
	 * @param String
	 *            identifierTemplate
	 * @return FileInfo
	 */
	public ContentFileInfo wServiceGetFileGenerated(String identifierTemplate) {
		ContentFileInfo file = null;
		byte[] response = null;

		try {
			String data = ppdApiUtils.getToken();
			Gson gson = new Gson();

			PpdtokenDto objToken = gson.fromJson(data, PpdtokenDto.class);

			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
				response = this.callGetFileGenerated(objToken.getAccess_token(), identifierTemplate);
				logger.info(response.toString());

				file = new ContentFileInfo();
				file.setFile(response);
				file.setFileName("temp.pdf");
				file.setId(identifierTemplate);

			} else {
				logger.error("> error - Session was not opened");
				return null;
			}
		} catch (Exception ex) {
			logger.error("> error - wServiceGetFileGenerated");
			logger.error(ex.toString());
			return null;
		}

		return file;
	}

	/**
	 * document generated
	 * 
	 * @param SFUserDto
	 *            sfUse2r
	 * @param Document
	 *            document
	 * @param PpdDocGenRequestPayloadDto
	 *            genVar
	 */
	public Generated wServiceGenerateDoc(SFUserDto sfUse2r, Document document, PpdDocGenRequestPayloadDto genVar) {
		GenerateInfoDto gen = new GenerateInfoDto();
		gen.setDocument(document);
		gen.setUser(sfUse2r);

		// get template people doc
		TemplateInfoDto ppdTemplanteDto = ppTemplateGenerator.getInfoTemplate(document.getTemplateId().getIdentifierPpd().toString());

		if (ppdTemplanteDto != null) {
			gen.setDocInfo(ppdTemplanteDto);

			// header
			genVar.setDocument_generation_template_id(gen.getDocument().getTemplateId().getIdentifierPpd());
			String outputformat = "PDF";
			
			logger.info(outputformat);
			
			if(gen.getDocument().getOutputFormat() == null || gen.getDocument().getOutputFormat().isEmpty()) {
				
			}else {
				outputformat = gen.getDocument().getOutputFormat();
			}
			logger.info("+++++++");
			logger.info(gen.getDocument().getOutputFormat());
			logger.info(gen.getDocument().getOutputFormat());
			logger.info(outputformat);
			
			genVar.setOutput_format(outputformat);

			// date
			Date today = Calendar.getInstance().getTime();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			genVar.addVariable(new SlugItem("today", df.format(today)));

			// estoy buscando de donde sacar el identificador
			try {
				String data = ppdApiUtils.getToken();
				Gson token = new Gson();
				Gson gson = new Gson();
				this.objToken = token.fromJson(data, PpdtokenDto.class);
				if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) {
					PpdDocGenFullWithErrors gene = new PpdDocGenFullWithErrors();
					gene = this.callPostGenerate(objToken.getAccess_token(), gson.toJson(genVar));
					

					Generated generatedResponse = new Generated();
					generatedResponse.setDocument(gen.getDocument());
					generatedResponse.setVersion(gene.getDocument_generation_template_version());
					generatedResponse.setUploadIdPpd(gene.getUpload_id());
					generatedResponse.setGeneratedIdPpd(gene.getId());					

					int errorsCount = 0;
					if (gene.getErrors() != null && gene.getErrors().length != 0) {
						for (GenResponseInfoDto b : gene.getErrors()) {
							if (!b.getCode().equals("unknown_slug")) {
								ErrorLog erro = new ErrorLog();
								erro.setCode(b.getCode());
								erro.setField(b.getField());
								erro.setMessage(b.getMessage());
								generatedResponse.addError(erro);
								errorsCount++;
							}
						}
					}

					if (errorsCount > 0) {
						generatedResponse.setState("error");
					} else {
						generatedResponse.setState("thumb-up");
					}

					generatedResponse.setNumberErrors(errorsCount);
					return generatedResponse;
				} else {
					logger.error("> error - Session was not opened");
					return null;
				}
			} catch (Exception ex) {

				logger.error("> error - Connection was not working with PPDoc");
				logger.error(ex.toString());
				return null;
			}
		}

		return null;
	}
	
	
	/**
	 * get information for signatures on document
	 * @param String idUploadedFile
	 * @param String keyWord
	 * @return List<PpdTextOccurrencesDto>
	 * */
	public List<PpdTextOccurrencesDto> wServiceUploadsTextOccurrences(String idUploadedFile, String keyWord)
	{
		List<PpdTextOccurrencesDto> responseDto = new ArrayList<>();
	
		try {
			String data = ppdApiUtils.getToken();
			Gson gson = new Gson();
	
			PpdtokenDto objToken = gson.fromJson(data, PpdtokenDto.class);
	
			if (objToken.getToken_type().equals(UtilCodesEnum.CODE_TOKEN_TYPE_BEARER.getCode())) 
			{
				String json = "{\"keyword\":\""+keyWord+"\"}";
				return this.callPostSignTextOccurences(objToken.getAccess_token(), idUploadedFile,json);	
	
			} else {
				logger.error("> error - Session was not opened");
				return null;
			}
	
		} catch (Exception e) {
			loggerSingle.error(e.getMessage());
			return responseDto;
		}
	}

	/**
	 * call service prev generated
	 * 
	 * @param String
	 *            bearer
	 * @param String
	 *            id
	 */
	public byte[] wServicePrevGenerated(String bearer, String id, String page) {
		try {
			return this.callGetPrevGenerated(bearer, id, page);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * call service FileGenerated
	 * 
	 * @param String
	 *            bearer
	 * @param String
	 *            id
	 */
	public byte[] wServiceFileGenerated(String bearer, String id) {
		try {
			return this.callGetFileGenerated(bearer, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * call service SetDocument
	 * 
	 * @param String
	 *            bearer
	 * @param String
	 *            id
	 */
	public String wServiceSetDocument(String Token, ContentFileInfo fl, String json) {
		try {
			return this.callSetDocument(Token, fl, json);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * call service CompanyDocument
	 * 
	 * @param String bearer
	 * @param String id
	 */
	public ResponseGenericDto wServiceSetCompanyDocument(String Token, ContentFileInfo fl, String json) 
	{
		ResponseGenericDto response = new ResponseGenericDto();
		try 
		{
			response.setMessage(this.callSetCompanyDocument(Token, fl, json));
			response.setCode(UtilCodesEnum.CODE_OK.getCode());
			return response;
		} catch (Exception e) 
		{
			response.setCode(UtilCodesEnum.CODE_ERROR.getCode());
			response.setMessage(e.getMessage());
			logger.error(e.getMessage());
			return response;
		}
	}

	/**
	 * call service delete
	 * 
	 * @param String
	 *            idSignature
	 * @return String response ppd
	 */
	public String wServiceDeletingSignature(String idSignature) {
		try {
			String data = ppdApiUtils.getToken();
			Gson gson = new Gson();

			PpdtokenDto objToken = gson.fromJson(data, PpdtokenDto.class);
			return this.callDeletingSignature(objToken.getAccess_token(), idSignature);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	// **********************************************

	private PpdDocGenFullWithErrors callPostGenerate(String bearer, String json) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnector.getInstance().callPOST("document_generations", bearer, json, null);
		return utilMapping.loadGeneratedMetadataFromJsom(userJson);
	}
	
	private List<PpdTextOccurrencesDto> callPostSignTextOccurences(String bearer, String idDocument, String json) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnector.getInstance().callPOST("uploads/"+idDocument+"/text_occurrences",bearer,json,null);
		return utilMapping.loadPpdTextOccurrencesDtoFromJsom(userJson);
	}	

	private byte[] callGetFileGenerated(String bearer, String id) throws IOException, InvalidResponseException {
		byte[] userJson = PpdHttpConnector.getInstance().callImage("document_generations/" + id + "/file", bearer, true);
		return userJson;
	}

	private byte[] callGetPrevGenerated(String bearer, String id, String page) throws IOException, InvalidResponseException {
		byte[] userJson = PpdHttpConnector.getInstance().callImage("document_generations/" + id + "/preview/" + page, bearer, true); 
		return userJson;
	}

	private String callSetSignatureDocument(String Token, ContentFileInfo fl, String json) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnectorV1.getInstance().executeFile("/api/v1/signatures/", fl, json);
		return userJson;
	}

	private String callGetSignatureDocument(String idGenerated, String status, String Token, String query, String json) throws IOException, InvalidResponseException {
		String userJson = "";
		if (status != null && !status.equals("")) {
			userJson = PpdHttpConnectorV1.getInstance().executeGET("/api/v1/signatures/?state=" + status + "&external_id=" + idGenerated, Token, json);
		} else {
			userJson = PpdHttpConnectorV1.getInstance().executeGET("/api/v1/signatures/?external_id=" + idGenerated, Token, json);
		}

		return userJson;
	}

	private String callSetDocument(String Token, ContentFileInfo fl, String json) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnectorV1.getInstance().executeFile("/api/v1/vault/documents/", fl, json);
		return userJson;
	}

	private String callSetCompanyDocument(String Token, ContentFileInfo fl, String json) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnectorV1.getInstance().executeFile("/api/v1/enterprise/documents/", fl, json);
		return userJson;
	}

	private String callDeletingSignature(String token, String idSignature) throws IOException, InvalidResponseException {
		String userJson = PpdHttpConnectorV1.getInstance().callDELETE("/api/v1/signatures/" + idSignature, token, "");
		return userJson;
	}
}
