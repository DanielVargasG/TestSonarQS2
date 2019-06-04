package edn.cloud.web.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.GenerateInfoDto;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.SignatureFieldDto;
import edn.cloud.business.dto.SignatureGroupDto;
import edn.cloud.business.dto.integration.DocInfoDto;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.SFRecUnique;
import edn.cloud.business.dto.integration.SFUserDto;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.business.dto.ppd.api.PpdUserDto;
import edn.cloud.ppdoc.business.facade.PpdEmployeeApiFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorRecruitingFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorTemplateFacade;
import edn.cloud.sfactor.persistence.dao.DocumentDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Document;
import edn.cloud.sfactor.persistence.entities.DocumentFields;
import edn.cloud.sfactor.persistence.entities.Generated;
import edn.cloud.web.service.SessionCreateFilter;

@Path("/json")
public class JsonDocumentService 
{
	@Context
	HttpServletRequest request;
	
	private UtilLogger logger = UtilLogger.getInstance();
	private PpdEmployeeApiFacade ppdEmployeeF = new PpdEmployeeApiFacade();
	private SuccessFactorFacade successFacade = new SuccessFactorFacade();
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	private SuccessFactorRecruitingFacade successRecFacade = new SuccessFactorRecruitingFacade();

	// --------------------------------------------------
	@POST
	@Path("/documents")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentGetPending(String val) 
	{		
		Gson gson = new Gson();
		FilterQueryDto filters = gson.fromJson(val,FilterQueryDto.class);
		successFacade.documentDeleteAllWithoutTemplate();
		String idUserSession = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);
		
		ArrayList<Document> li = successFacade.documentGetListByUserTemplate(idUserSession,null,filters);
		ArrayList<Document> listReturn = new ArrayList<>();
		
		if(li!=null)
		{
			for(Document doc:li)
			{
				if(doc.getTemplateId()==null){
					successFacade.documentDelete(doc.getId());	
				}
				if(doc.getArchive() == null) {
					listReturn.add(doc);
				}
			}
		}
		
		return Response.status(200).entity(listReturn).build();
	}
	
	/**
	 * Get Document Archive for date
	 * @return
	 */
	@POST
	@Path("/documents/archive")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentGetArchive(String val) 
	{		
		logger.info("*********---------**********"+val);
		Boolean isFilter = false;
		String idUserSession = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);
		JSONObject json = new JSONObject(val);
		ArrayList<Document> listReturn =  new ArrayList<>();
		
		if(json.getString("user")!=null && !json.getString("user").equals(""))
			isFilter = true;
			
		if(json.getString("date1")!=null && !json.getString("date1").equals("") && 
				json.getString("date2")!=null && !json.getString("date2").equals(""))
			isFilter = true;
		
		Collection<Document> list = null;
		if(isFilter){
			list = successFacade.getDocumentByUserArchive(idUserSession, null, json.getString("date1"), json.getString("date2"), json.getString("user"));
		}
		
		if(list!=null) {
			listReturn = new ArrayList<>(list);
		}
		return Response.status(200).entity(listReturn).build();
	}
	
	
	
	// --------------------------------------------------
	@GET
	@Path("/document/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentGetById(@PathParam("id") long idDoc)
	{
		return Response.status(200).entity(this.documentInfoGetById(idDoc,true)).build();
	}
	
	// ----------------------------------------------------
	/**
	 * update document sign field
	 * @param Long idDoc
	 * @param String val json (type SignatureFieldDto)
	 * @return Response
	 * */
	@POST
	@Path("/updateDocSing/{idDoc}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentDocSignUpdate(@PathParam("idDoc") Long idDoc, String val)
	{
		Gson gson = new Gson();
		SignatureFieldDto sign = gson.fromJson(val,SignatureFieldDto.class);
		sign.setFlag(Boolean.TRUE);
		
		if(sign.getIdSignTempLib()!=null && sign.getIdSignTempLib() == -2)
		{
			if(sign.getIdSignDocument()!=null && !sign.getIdSignDocument().equals("")){
				successFacade.documentSignatureReset(sign.getIdSignDocument());
			}
		}
		else 
		{
			if(sign.getIdSignDocument()!=null && !sign.getIdSignDocument().equals("")){
				sign = successFacade.documentSignatureUpdate(idDoc, sign);
			}
			else{
				sign = successFacade.documentSignatureInsert(idDoc, sign);
			}	
		}	
		
		return Response.status(200).entity(this.documentInfoGetById(idDoc,true)).build();
	}
	
	/**
	 * reset document signature for siganture template
	 * @param Long idDoc
	 * @param String val json (type SignatureFieldDto)
	 * @return Response
	 * */
	@POST
	@Path("/resetDocSing/{idDoc}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentDocSignReset(@PathParam("idDoc") Long idDoc, String val)
	{
		Gson gson = new Gson();
		SignatureFieldDto sign = gson.fromJson(val,SignatureFieldDto.class);
		sign.setFlag(Boolean.TRUE);
		
		if(sign.getIdSignDocument()!=null && !sign.getIdSignDocument().equals("")){
			successFacade.documentSignatureReset(sign.getIdSignDocument());
		}			
		
		return Response.status(200).entity(this.documentInfoGetById(idDoc,true)).build();
	}	
	
	// ----------------------------------------------------
	/**
	 * update document field
	 * @param Long idDoc
	 * @param String val json (type SignatureFieldDto)
	 * @return Response
	 * */
	@POST
	@Path("/updateDocField/{idDoc}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentDocFieldUpdate(@PathParam("idDoc") Long idDoc, String val)
	{
		Gson gson = new Gson();
		DocumentFields docField = gson.fromJson(val,DocumentFields.class);
		
		if(docField.getId()!=null){
			docField = successFacade.documentFieldUpdate(idDoc, docField);
		}
		else{
			docField = successFacade.documentFieldInsert(idDoc, docField);
		}	
		
		return Response.status(200).entity(this.documentInfoGetById(idDoc,true)).build();
	}
	
	/**
	 * reset document field for field library template or template field
	 * @param Long idDoc
	 * @param String val json (type DocumentFields)
	 * @return Response
	 * */
	@POST
	@Path("/resetDocField/{idDoc}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentDocFieldReset(@PathParam("idDoc") Long idDoc, String val)
	{
		Gson gson = new Gson();
		DocumentFields docField = gson.fromJson(val,DocumentFields.class);
		docField.setIsConstants(Boolean.TRUE);
		
		if(docField.getId()!=null){
			successFacade.documentFieldReset(docField.getId());
		}			
		
		return Response.status(200).entity(this.documentInfoGetById(idDoc,true)).build();
	}		

	// ----------------------------------------------------
	@POST
	@Path("/createDocument/{idtemplate}/{id}/{user}/{date}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentCreate(@PathParam("idtemplate") Long idTemplate, @PathParam("id") String id, @PathParam("user") String user, @PathParam("date") String date) {
		SFUserDto sfuser = new SFUserDto();
		sfuser = successFacade.userGetProfile(user, UtilCodesEnum.DATE_SOURCE_USER_ACTIVE.getCode());
		
		if (sfuser != null) {
			Document doc = successFacade.documentSave(idTemplate, sfuser, date);
			if (doc != null) {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(doc).build();
			}
		}

		return Response.status(UtilCodesEnum.CODE_ERROR_401.getCodeInt()).build();
	}

	// ----------------------------------------------------
	@POST
	@Path("/createRecDocument/{idtemplate}/{appId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createRecDocument(@PathParam("idtemplate") Long idTemplate, @PathParam("appId") String appId) {

		SFRecUnique sfRecUser = successRecFacade.getApplication(appId);

		if (sfRecUser != null) {
			Document doc = successFacade.documentRecSave(idTemplate, sfRecUser);
			if (doc != null) {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(doc).build();
			}
		}

		return Response.status(UtilCodesEnum.CODE_ERROR_401.getCodeInt()).build();
	}

	// ----------------------------------------------------
	@DELETE
	@Path("/deleteDocument/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentDelete(@PathParam("id") Long id) 
	{
		ArrayList<ResponseGenericDto> errorsSignCancel = successFacade.signatureCancelProcess(id);
		
		if(errorsSignCancel!=null && errorsSignCancel.size()>0) 
		{
			return Response.status(200).entity("Delete Document: "+errorsSignCancel.get(0).getMessage()).build();
		}
		else 
		{
			if(successFacade.documentDelete(id))
				return Response.status(200).entity("Document Removed").build();				
			else
				return Response.status(500).build();
		}
	}

	// -------------------------------------------------------
	@GET
	@Path("/employeedocuments/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPrevGenerated(@PathParam("id") String id) {
		try {
			PpdUserDto[] data2 = ppdEmployeeF.wServiceGetEmployee(id);
			String currentId = data2[0].id;

			DocInfoDto[] data3 = ppdEmployeeF.wServiceEmployeeDocument(currentId);
			return Response.ok(data3).build();

		} catch (Exception ex) {

			logger.error("> error - Connection was not working with PPDoc");
			logger.error(ex.toString());
			return null;
		}

	}
	
	/**
	 * get info document
	 * @param long idDoc
	 * @return GenerateInfoDto
	 * */
	private GenerateInfoDto documentInfoGetById(long idDoc, boolean loadValuesSuccessFactor)
	{
		GenerateInfoDto response = new GenerateInfoDto();
		SuccessFactorTemplateFacade SFTemplateFacade = new SuccessFactorTemplateFacade();
		List<Generated> objectArray = successFacade.generatedGetByIdDoc(idDoc);
	
		// Info document
		TemplateInfoDto templateDtoInfo = new TemplateInfoDto();
		Document document = successFacade.documentGetById(idDoc);
		
		if(document!=null && document.getTemplateId()!=null)
		{			
			response.setDocument(document);
	
			// load values to Fields templates
			PpdDocGenRequestPayloadDto generateVariables = SFTemplateFacade.templateGetFieldsList(document.getTemplateId().getId());
			ArrayList<DocumentFields> documentFields = successFacade.documentFieldGetAllByIdDoc(document.getId());
			generateVariables = successFacade.documentFieldReplaceTemplateField(documentFields, generateVariables);
			
			if(loadValuesSuccessFactor) 
			{
				PpdDocGenRequestPayloadDto genVarFinal = SFTemplateFacade.templateGetValueQueryBuilder(generateVariables, document.getTargetUser(), true,document.getEffectiveDate(),true);						
				templateDtoInfo.setGenerateVariables(genVarFinal);				
			}
			
			response.setDocInfo(templateDtoInfo);
		
			//load signatures modified
			ArrayList<SignatureGroupDto> documentSignByDocList = successFacade.getDocumentSignatureByDoc(idDoc);
			// load value of signature templates
			ArrayList<SignatureGroupDto> listSignatureGroup = SFTemplateFacade.templateGetSignatureGroupList(document.getTemplateId().getId());		
			
			//--------------------------------------------
			//update signature for signature of documents 
			listSignatureGroup = successFacade.documentSignatureReplaceSignTemplate(listSignatureGroup,documentSignByDocList);
			
			for (SignatureGroupDto group : listSignatureGroup) {
				group = successFacade.signatureGetValueQueryBuilder(group, document.getTargetUser(), true,document.getEffectiveDate());
			}
			
			templateDtoInfo.setSingatureGroup(listSignatureGroup);
			
			
			if (document.getTemplateId().getModule().equals("EMP") || document.getTemplateId().getModule().equals("EMC")) {
				SFUserDto sfUse2r = successFacade.userGetProfile(document.getTargetUser(), UtilCodesEnum.DATE_SOURCE_USER_ACTIVE.getCode());
				response.setUser(sfUse2r);
			}
			if (document.getTemplateId().getModule().equals("REC")) {
				SFRecUnique sfRecUser = successRecFacade.getApplication(document.getTargetUser());
		
				response.setRecuser(sfRecUser.getD());
			}
			response.setDocument(document);	
			response.setGenerated(objectArray);
		}
		
		return response;
	}
	/**
	 * 
	 * Get all Documents Pending Filters 
	 * 
	 * 
	 * **/
	@POST
	@Path("/documentspendigfilters")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentGetPendingFilters(String val) 
	{		
		successFacade.documentDeleteAllWithoutTemplate();
		String idUserSession = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);
		Gson gson = new Gson();
		FilterQueryDto filters = gson.fromJson(val,FilterQueryDto.class);
		ArrayList<Document> li = successFacade.documentGetListByUserTemplate(idUserSession, 
				"'"+UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode()+"',"+
				"'"+UtilCodesEnum.CODE_STATUS_PENDING_SIGN_DOC.getCode()+"'",filters);
		
		if(li!=null)
		{
			for(Document doc:li)
			{
				if(doc.getTemplateId()==null){
					successFacade.documentDelete(doc.getId());	
				}	
			}
		}
		
		return Response.status(200).entity(li).build();
	}
	
	/** 
	 * Delete documents items selected 
	 * 
	 * 
	 * **/
	@POST
	@Path("/documentsitemsdelete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentDeleteItems(String val) {
		
		JSONArray jArray = new JSONArray(val);
		
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject obj = new JSONObject(jArray.get(i).toString());
			try {
				if(obj.getBoolean("check")) {
					Long id = obj.getLong("id");
					ArrayList<ResponseGenericDto> errorsSignCancel = successFacade.signatureCancelProcess(id);
					
					if(errorsSignCancel!=null && errorsSignCancel.size()>0) 
					{
						return Response.status(200).entity("Delete Document: "+errorsSignCancel.get(0).getMessage()).build();
					}
					else 
					{
						if(successFacade.documentDelete(id))
							logger.info("delete document");				
						else
							return Response.status(500).build();
					}
				}
			} catch (Exception e) {
				return Response.status(500).build();
			}
			
		}
		
		return Response.status(200).entity(null).build();
	}
	
	/**
	 * Archive Documents
	 * @param val
	 * @return
	 */
	@POST
	@Path("/documentsitemsarchive")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentArchiveItems(String val) {
		
		try {
			JSONArray jArray = new JSONArray(val);
			
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject obj = new JSONObject(jArray.get(i).toString());
				
					if(obj.getBoolean("check")) {
						Long id = obj.getLong("id");
						DocumentDAO docDAO =  new DocumentDAO();
						Document doc = successFacade.documentGetById(id);
						doc.setArchive(UtilCodesEnum.CODE_DEFAULT.getCode());
						docDAO.save(doc);
					}
			} 
			return Response.status(200).build();
			
		}catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
			
	}
	/** 
	 * sign documents items selected
	 * @param String val
	 * **/
	@POST
	@Path("/documentsItemsSign")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentSignItems(String val) 
	{
		//resume erros
		ArrayList<GenResponseInfoDto> errorList = new ArrayList<>();
		
		// get URL Callback signature
		AdminParameters paramAdminSignCallback = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_SIGNCALLBACK_URL.getCode());

		
		JSONArray jArray = new JSONArray(val);		
		for (int i = 0; i < jArray.length(); i++) 
		{
			JSONObject obj = new JSONObject(jArray.get(i).toString());
			try 
			{
				if(obj.getBoolean("check")) 
				{
					Long idDoc = obj.getLong("id");
						
					if(idDoc!=null)//validate id documento
					{
						//errors
						GenResponseInfoDto error = new GenResponseInfoDto();
						
						//load document
						GenerateInfoDto documentDto = documentInfoGetById(idDoc,false);
						if(documentDto!=null)
						{
							//validate signatures
							if(documentDto.getDocInfo().getSingatureGroup()!=null && documentDto.getDocInfo().getSingatureGroup().size()>0 
									&& documentDto.getDocInfo().getSingatureGroup().get(0).getSignatures()!=null 
										&& documentDto.getDocInfo().getSingatureGroup().get(0).getSignatures().size()>0)
							{
								//load generated								
								Generated generated = null;	
								
								if(documentDto.getGenerated()!=null && documentDto.getGenerated().size()>0)									
									generated = documentDto.getGenerated().get(documentDto.getGenerated().size()-1);
								else
									generated = successFacade.generatedDocumentInPPD(idDoc);
								
								if (generated != null) 
								{
									if(generated.getDocument().getOutputFormat().equals(UtilCodesEnum.CODE_PDF_DOC.getCode())) 
									{
										//send to sign
										edn.cloud.business.dto.GenErrorInfoDto resultErrorInfo = successFacade.signatureSendToSing(
												idDoc, 
												generated.getId(),
												(paramAdminSignCallback!=null&&paramAdminSignCallback.getValue()!=null?UtilCodesEnum.CODE_HTTPS.getCode()+paramAdminSignCallback.getValue():""));
										
										
										if (resultErrorInfo.getFlag()) 
										{
											if (resultErrorInfo.getCode().equals(""))
												error.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
											
											error.setField(String.valueOf(idDoc));
											error.setCode(resultErrorInfo.getCode());
											error.setMessage("Id document:"+idDoc+", "+ resultErrorInfo.getMessage());
											errorList.add(error);
										} else 
										{	
											error.setField(String.valueOf(idDoc));
											error.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());											
											error.setMessage("Error send sign configuration "+resultErrorInfo.getMessage()!=null?resultErrorInfo.getMessage():"");
											errorList.add(error);
										}									
									}
									else {
										error.setField(String.valueOf(idDoc));
										error.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
										error.setMessage("No signature in format output DOCX");
										errorList.add(error);
									}									
								} 
								else
								{
									
									error.setField(String.valueOf(idDoc));
									error.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
									error.setMessage("Error generated Document in PPD");
									errorList.add(error);
								}
							}
							else
							{
								error.setField(String.valueOf(idDoc));
								error.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode());
								error.setMessage("Document without signatures");
								errorList.add(error);
							}
						}
					}
				}
			} 
			catch (Exception e) {
				return Response.status(500).build();
			}			
		}
		
		return Response.status(200).entity(errorList).build();		
	}	
	
	/**
	 * Create Document self-generation
	 * @param LLong idTemplate 
	 * @param String id
	 * @return Response 
	 * */
	@POST
	@Path("/createDocumentEmp/{idtemplate}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentCreateEmp(@PathParam("idtemplate") Long idTemplate, @PathParam("id") String id) {
		SFUserDto sfuser = new SFUserDto();
		String idUserSession = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);
		sfuser = successFacade.userGetProfile(idUserSession, UtilCodesEnum.DATE_SOURCE_USER_ACTIVE.getCode());
		
		if (sfuser != null) 
		{
			
			String effectiveDate = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(),new Date());
			Document doc = successFacade.documentSave(idTemplate, sfuser, effectiveDate);
			if (doc != null) {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(doc).build();
			}
		}

		return Response.status(UtilCodesEnum.CODE_ERROR_401.getCodeInt()).build();
	}
	
	/**
	 * Document no archive
	 * @param Long val
	 * @return
	 */
	@GET
	@Path("/documentsnoarchive/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentArchiveItems(@PathParam("id") Long id) {
		
		try {
			DocumentDAO docDAO =  new DocumentDAO();
			Document doc = successFacade.documentGetById(id);
			doc.setArchive(null);
			docDAO.save(doc);
			return Response.status(200).build();
			
		}catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
			
	}
	
	/**
	 * Create Document self-generation with signature
	 * @param LLong idTemplate 
	 * @param String id
	 * @return Response 
	 * */
	@POST
	@Path("/createSelfDocEmp/{idtemplate}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentSelfGenerated(@PathParam("idtemplate") Long idTemplate, @PathParam("id") String id) 
	{
		SFUserDto sfuser = new SFUserDto();
		String idUserSession = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);
		sfuser = successFacade.userGetProfile(idUserSession, UtilCodesEnum.DATE_SOURCE_USER_ACTIVE.getCode());
		
		if(sfuser != null) 
		{
			// get is automatic signature
			AdminParameters paramAdminSignAuto = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_SIGNAUTO_EVENTLIST.getCode());

			// get URL Callback signature
			AdminParameters paramAdminSignCallback = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_SIGNCALLBACK_URL.getCode());
			
			edn.cloud.business.dto.GenErrorInfoDto resultErrorInfo = successFacade.generatedDocumentAutomatic(
					idTemplate, 
					sfuser.getPersonIdExternal(),
					paramAdminSignAuto!=null&&paramAdminSignAuto.getValue()!=null?paramAdminSignAuto.getValue():"",
					paramAdminSignCallback!=null&&paramAdminSignCallback.getValue()!=null?paramAdminSignCallback.getValue():"",
					UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(),new Date()),
					(UtilCodesEnum.CODE_SOURCE_DOC_SELF.getCode()+", id Template: "+idTemplate));
					
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(resultErrorInfo).build();			
		}
		
		return Response.status(UtilCodesEnum.CODE_ERROR_401.getCodeInt()).build();
	}
}
