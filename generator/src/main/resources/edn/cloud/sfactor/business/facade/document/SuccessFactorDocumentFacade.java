package edn.cloud.sfactor.business.facade.document;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ext.Java7Support;
import com.google.gson.Gson;
import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.PostInfo;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.ppd.api.PpdtokenDto;
import edn.cloud.ppdoc.business.facade.PpdApiUtilsFacade;
import edn.cloud.ppdoc.business.facade.PpdGeneratorFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.impl.SucessFactorDocumentImpl;
import edn.cloud.sfactor.business.interfaces.SucessFactorDocument;
import edn.cloud.sfactor.business.utils.QueryBuilder;
import edn.cloud.sfactor.persistence.dao.FieldsMappingPpdDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Document;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;

public class SuccessFactorDocumentFacade 
{	
	private PpdApiUtilsFacade ppdFacade = new PpdApiUtilsFacade();
	private PpdGeneratorFacade ppdGeneratorFacade = new PpdGeneratorFacade();
	
	/**
	 * send document generated to ppd
	 * @param Document document
	 * @param String idDocument
	 * @param String typeSend
	 * @return ResponseGenericDto
	 * */
	public ResponseGenericDto sendDocumenToPpdEmployee(Document document, String idDocument, String typeSend)
	{
		ResponseGenericDto response = new ResponseGenericDto();
		PpdtokenDto objToken = new PpdtokenDto();
		ContentFileInfo file = new ContentFileInfo();
		byte[] fileByte;

		try 
		{
			String data = ppdFacade.getToken();
			Gson token = new Gson();

			objToken = token.fromJson(data, PpdtokenDto.class);
			if (objToken.getToken_type().equals("bearer")) 
			{
				fileByte = ppdGeneratorFacade.wServiceFileGenerated(objToken.getAccess_token(), idDocument);
				
				file = new ContentFileInfo();
				file.setFile(fileByte);
				file.setFileName("temp.pdf");
				file.setId(idDocument);
			} else 
			{
				response.setCode(UtilCodesEnum.CODE_ERROR.getCode());
				response.setMessage("Error Get File");
				return response;				
			}
		} catch (Exception ex) {
			response.setCode(UtilCodesEnum.CODE_ERROR.getCode());
			response.setMessage(ex.getMessage());
			return response;
		}

		try 
		{
			Gson gson = new Gson();
			PostInfo post = new PostInfo();
			post.setExternal_unique_id(document.getTemplateId().getTitle() + " - " + document.getTargetUser_firstName() + " " + document.getTargetUser_lastName() + " _ " + UUID.randomUUID().toString());

			post.setTitle(document.getTemplateId().getTitle() + " - " + document.getTargetUser_firstName() + " " + document.getTargetUser_lastName() + " _ " + UUID.randomUUID().toString());
			post.setDate(document.getEffectiveDate());


			FieldsMappingPpdDAO fieldDAO = new FieldsMappingPpdDAO();
			FieldsMappingPpd fi = fieldDAO.getFieldMappingByName("technical_id");

			Map<String, ResultBuilderDto> mapUser = new HashMap<String, ResultBuilderDto>();
			mapUser.put("realuser", new ResultBuilderDto(fi.getNameDestination(), "default", ""));
			Map<String, ResultBuilderDto> mapResUser = QueryBuilder.getInstance().convert(mapUser, document.getTargetUser(), "");


			post.setEmployee_technical_id(mapResUser.get("realuser").getResult());
			post.setDocument_type_code(document.getTemplateId().getDocumentType());


			//send to ppd
			if(typeSend.equals(UtilCodesEnum.CODE_TYPE_EMPLOYEE_VAULT.getCode()))
				ppdGeneratorFacade.wServiceSetDocument("X-KEY", file, gson.toJson(post));
			else if(!ppdGeneratorFacade.wServiceSetCompanyDocument("X-KEY", file, gson.toJson(post))
						.getCode().equals(UtilCodesEnum.CODE_OK.getCode())) {
				response.setCode(UtilCodesEnum.CODE_ERROR.getCode());
			}
						
			response.setCode(UtilCodesEnum.CODE_OK.getCode());
			response.setMessage("Send to Ppd file name: "+post.getTitle());
			return response;
			

		} catch (Exception ex) {
			response.setCode(UtilCodesEnum.CODE_ERROR.getCode());
			response.setMessage(ex.getMessage());
			return response;
		}
	}
	

	/**
	 * update all documents with creation date less than the admin parameter  CODE_PARAM_MAX_TIME_DOC_AUTO_ARCHIVE or 90 days
	 * @return void
	 * */
	public void documentUpdateArchiveByMaxTime()
	{
		try 
		{
			SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
			SucessFactorDocument successFactorDocumentI = new SucessFactorDocumentImpl();
			
			//get parameter max time doc to auto archive
			AdminParameters paramIntervalTime = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_MAX_TIME_DOC_AUTO_ARCHIVE.getCode());

			Long intervalTime = 90L;
			if (paramIntervalTime != null && paramIntervalTime.getValue() != null && !paramIntervalTime.getValue().equals("")) {
				try {
					intervalTime = Long.parseLong(paramIntervalTime.getValue());
				} catch (Exception e) {
					intervalTime = 90L;
				}
			}

			if (intervalTime >= 0) 
			{
				if(intervalTime==0)
					intervalTime = 1440L;
				else
					intervalTime = intervalTime * -1440;
				
				// add minutes to end date				
				Date filterDate = UtilDateTimeAdapter.getDateAddMinutes(new Timestamp((UtilDateTimeAdapter.getDateWithoutTime()).getTime()),intervalTime );				
				successFactorDocumentI.documentUpdateArchiveByMaxTime(UtilCodesEnum.CODE_DEFAULT.getCode(),filterDate);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
