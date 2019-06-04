package edn.cloud.web.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilFiles;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.PostInfo;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.ppd.api.PpdtokenDto;
import edn.cloud.ppdoc.business.facade.PpdApiUtilsFacade;
import edn.cloud.ppdoc.business.facade.PpdGeneratorFacade;
import edn.cloud.ppdoc.business.facade.PpdTemplateFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorSignatureFacade;
import edn.cloud.sfactor.business.utils.QueryBuilder;
import edn.cloud.sfactor.persistence.dao.DocumentDAO;
import edn.cloud.sfactor.persistence.dao.FieldsMappingPpdDAO;
import edn.cloud.sfactor.persistence.dao.GeneratedDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Document;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;
import edn.cloud.sfactor.persistence.entities.Generated;
import edn.cloud.sfactor.persistence.entities.SignatureFileControl;

@Path("/json")
public class JsonGeneratedService 
{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private PpdtokenDto objToken;
	private byte[] data3;
	
	private PpdGeneratorFacade ppdGeneratorFacade = new PpdGeneratorFacade();
	private PpdTemplateFacade ppdTemplateGeneratorFacade = new PpdTemplateFacade();
	private SuccessFactorFacade sucessFactorFacade = new SuccessFactorFacade();
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	private SuccessFactorSignatureFacade successFactorSingF = new SuccessFactorSignatureFacade();
	private PpdApiUtilsFacade ppdFacade = new PpdApiUtilsFacade();
	
	@POST
	@Path("/generated/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateDoc(@PathParam("id") Long id) 
	{
		try
		{
			ArrayList<ResponseGenericDto> errorsSignCancel = sucessFactorFacade.signatureCancelProcess(id);
			
			if(errorsSignCancel!=null && errorsSignCancel.size()>0) 
			{
				Map<String, String> resp = new HashMap<String, String>();
				resp.put("response","No Generated Document. "+errorsSignCancel.get(0).getMessage());
				
				return Response.status(200).entity(resp).build();				
			}
			else 
			{
				if(sucessFactorFacade.generatedDocumentInPPD(id)!=null)
				{
					Map<String, String> resp = new HashMap<String, String>();
					resp.put("response", "Document Generated");
	
					return Response.status(200).entity(resp).build();
				}
				else
				{	
					return Response.status(200).entity("ERROR").build();
				}
			}
		
		} catch(Exception ex) 
		{
			ex.printStackTrace();	
			logger.error("> error - Connection was not working with PPDoc");
			logger.error(ex.toString());
			return Response.status(200).entity("ERROR").build();
		}
	}
	
	
	//------------------------------------------

	@GET
	@Path("/generated")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGene() {
		GeneratedDAO list2 = new GeneratedDAO();
		List<Generated> li2 = list2.getAll();
		return Response.status(200).entity(li2).build();
	}

	@GET
	@Path("/generated/{id}/{page}")
	@Produces("image/png")
	public Response getPrevGenerated(@PathParam("id") String id, @PathParam("page") String page) {
		try {
			String data = ppdFacade.getToken();
			Gson token = new Gson();
			this.objToken = token.fromJson(data, PpdtokenDto.class);
			if (objToken.getToken_type().equals("bearer")) {
				data3 = ppdGeneratorFacade.wServicePrevGenerated(objToken.getAccess_token(), id, page);
				return Response.ok(new ByteArrayInputStream(data3)).status(200).build();

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

	@POST
	@Path("/generated/{idGeneratedPpd}/{idDocument}/validated")
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateSendDocPpdEmpl(@PathParam("idGeneratedPpd") String idGeneratedPpd, @PathParam("idDocument") Long idDocument) {

		DocumentDAO list = new DocumentDAO();
		Document li = list.getById(idDocument);

		ContentFileInfo file = new ContentFileInfo();

		try {
			String data = ppdFacade.getToken();
			Gson token = new Gson();

			this.objToken = token.fromJson(data, PpdtokenDto.class);
			if (objToken.getToken_type().equals("bearer")) {
				data3 = ppdGeneratorFacade.wServiceFileGenerated(objToken.getAccess_token(), idGeneratedPpd);
				logger.info(data3.toString());

				file = new ContentFileInfo();
				file.setFile(data3);
				file.setFileName("temp.pdf");
				file.setId(idGeneratedPpd);
			} else {
				logger.error("> error - Session was not opened");
				return null;
			}
		} catch (Exception ex) {

			logger.error("> error - Connection was not working with PPDoc");
			logger.error(ex.toString());
			return null;
		}

		try {
			Gson gson = new Gson();
			PostInfo post = new PostInfo();
			post.setExternal_unique_id(li.getTemplateId().getTitle() + " - " + li.getTargetUser_firstName() + " " + li.getTargetUser_lastName() + " _ " + UUID.randomUUID().toString());

			post.setTitle(li.getTemplateId().getTitle() + " - " + li.getTargetUser_firstName() + " " + li.getTargetUser_lastName() + " _ " + UUID.randomUUID().toString());
			post.setDate(li.getEffectiveDate());


			FieldsMappingPpdDAO fieldDAO = new FieldsMappingPpdDAO();
			FieldsMappingPpd fi = fieldDAO.getFieldMappingByName("technical_id");

			Map<String, ResultBuilderDto> mapUser = new HashMap<String, ResultBuilderDto>();
			mapUser.put("realuser", new ResultBuilderDto(fi.getNameDestination(), "default", ""));
			Map<String, ResultBuilderDto> mapResUser = QueryBuilder.getInstance().convert(mapUser, li.getTargetUser(), "");


			post.setEmployee_technical_id(mapResUser.get("realuser").getResult());
			post.setDocument_type_code(li.getTemplateId().getDocumentType());


			String tst = ppdGeneratorFacade.wServiceSetDocument("X-KEY", file, gson.toJson(post));

			Map<String, String> resp = new HashMap<String, String>();
			resp.put("response", "ok");

			li.setStatus(UtilCodesEnum.CODE_STATUS_VALIDATE_DOC.getCode().toString());
			list.save(li);

			return Response.status(200).entity(resp).build();

		} catch (Exception ex) {
			logger.error(">> User not created in People Doc", ex);
			return null;
		}

		// return Response.status(200).entity(li).build();

	}
	
	@GET
	@Path("/generated/infopage/{id}/{number}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateInfoPage(@PathParam("id") String id, @PathParam("number") Integer num ) {
		String json = ppdTemplateGeneratorFacade.getGeneratedNumberPages(id, 1).replace("[", "");
		
		if(!json.equals("")) 
		{
			ArrayList<String>images= new ArrayList<>();
			int page= Integer.parseInt(json.replace("]", ""));
			
			for(int i=1; i<=page; i++) {
				String image="/generator/rst/json/generated/" + id + "/"+i;
				
				images.add(image);
			}
			return Response.status(200).entity(images).build();
		}
		else
		{
			return Response.status(200).entity(500).build();	
		}
	}

	@POST
	@Path("/generated/{id}/{key}/signature")
	@Produces(MediaType.APPLICATION_JSON)
	public Response signFileGenerated(@PathParam("id") Long idGenerated, @PathParam("key") Long idDoc) 
	{
		try
		{
			// get URL Callback signature
			AdminParameters paramAdminSignCallback = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_SIGNCALLBACK_URL.getCode());
			
			edn.cloud.business.dto.GenErrorInfoDto resutl = sucessFactorFacade.signatureSendToSing(idDoc, idGenerated,
					paramAdminSignCallback!=null&&paramAdminSignCallback.getValue()!=null?UtilCodesEnum.CODE_HTTPS.getCode()+paramAdminSignCallback.getValue():"");
			if(resutl.getFlag())
			{
				Map<String, String> resp = new HashMap<String, String>();
				resp.put("response", "Sent Signature");		
				return Response.status(200).entity(resp).build();						
			}
			else
			{
				Map<String, String> resp = new HashMap<String, String>();
				resp.put("response", "No Signature to Document. "+resutl.getMessage());		
				return Response.status(200).entity(resp).build();
			}		
		} catch(Exception ex) {
			
			logger.error("> error - signFileGenerated");
			logger.error(ex.toString());
			return Response.status(200).entity("ERROR").build();
		}
	}
	

	@POST
	@Path("/generated/{idGeneratedPpd}/{idDocument}/company")
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateSendDocPpdCompany(@PathParam("idGeneratedPpd") String idGeneratedPpd, @PathParam("idDocument") Long idDocument) {

		DocumentDAO list = new DocumentDAO();
		Document li = list.getById(idDocument);

		ContentFileInfo file = new ContentFileInfo();

		try {
			String data = ppdFacade.getToken();
			Gson token = new Gson();

			this.objToken = token.fromJson(data, PpdtokenDto.class);
			if (objToken.getToken_type().equals("bearer")) {
				data3 = ppdGeneratorFacade.wServiceFileGenerated(objToken.getAccess_token(), idGeneratedPpd);
				logger.info(data3.toString());

				file = new ContentFileInfo();
				file.setFile(data3);
				file.setFileName("temp.pdf");
				file.setId(idGeneratedPpd);

			} else {
				logger.error("> error - Session was not opened");
				return null;
			}
		} catch (Exception ex) {

			logger.error("> error - Connection was not working with PPDoc");
			logger.error(ex.toString());
			return null;
		}

		try {
			Gson gson = new Gson();
			PostInfo post = new PostInfo();
			post.setExternal_unique_id(li.getTemplateId().getTitle()+ " - " + li.getTargetUser_firstName() + " " + li.getTargetUser_lastName() + " _ " + UUID.randomUUID().toString());
			post.setTitle(li.getTemplateId().getTitle() + " - " + li.getTargetUser_firstName() + " " + li.getTargetUser_lastName() + " _ " + UUID.randomUUID().toString());
			post.setDate(li.getEffectiveDate());

			FieldsMappingPpdDAO fieldDAO = new FieldsMappingPpdDAO();
			FieldsMappingPpd fi = fieldDAO.getFieldMappingByName("technical_id");

			Map<String, ResultBuilderDto> mapUser = new HashMap<String, ResultBuilderDto>();
			mapUser.put("realuser", new ResultBuilderDto(fi.getNameDestination(), "default", ""));
			Map<String, ResultBuilderDto> mapResUser = QueryBuilder.getInstance().convert(mapUser, li.getTargetUser(), "");


			post.setEmployee_technical_id(mapResUser.get("realuser").getResult());
			//post.setDocument_type_code("test");
			post.setDocument_type_code(li.getTemplateId().getDocumentType());

			ResponseGenericDto tst = ppdGeneratorFacade.wServiceSetCompanyDocument("X-KEY", file, gson.toJson(post));
			Map<String, String> resp = new HashMap<String, String>();

			if(tst.getCode().equals(UtilCodesEnum.CODE_ERROR.getCode())) {
				resp.put("response", "error: "+tst.getMessage());
			}
			else 
			{
				resp.put("response", "ok");
			}

			return Response.status(200).entity(resp).build();

		} catch (Exception ex) {
			logger.error(">> User not created in People Doc", ex);
			return null;
		}
	}
	
	
	@GET
	@Path("/generated/{id}/file")
	@Produces("application/pdf")
	public Response getFileGenerated(@PathParam("id") String id) {
		try {
			String data = ppdFacade.getToken();
			Gson token = new Gson();
			this.objToken = token.fromJson(data, PpdtokenDto.class);
			if (objToken.getToken_type().equals("bearer")) {
				data3 = ppdGeneratorFacade.wServiceFileGenerated(objToken.getAccess_token(), id);
				logger.info(data3.toString());

				ResponseBuilder resp = Response.ok(new ByteArrayInputStream(data3));
				resp.header("Content-Disposition", "attachment; generated_" + id + ".pdf");

				return resp.build();

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
	
	@GET
	@Path("/generated/{id}/filedocx")
	@Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
	public Response getFileDocxGenerated(@PathParam("id") String id) {
		try {
			String data = ppdFacade.getToken();
			Gson token = new Gson();
			this.objToken = token.fromJson(data, PpdtokenDto.class);
			if (objToken.getToken_type().equals("bearer")) {
				data3 = ppdGeneratorFacade.wServiceFileGenerated(objToken.getAccess_token(), id);
				logger.info(data3.toString());
				byte[] doc = IOUtils.toByteArray(new ByteArrayInputStream(data3));
				ResponseBuilder resp = Response.ok(new ByteArrayInputStream(data3));
				resp.header("Content-Type: application/vnd.openxmlformats-officedocument.wordprocessingml.document", "filename=generated_" + id + "l.docx");
				//return data3;
				return resp.build();

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
	
	@POST
	@Path("/generated/addfile/{id}/{typecode}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response addFilePpd(MultipartFormDataInput input, @PathParam("id") String id, @PathParam("typecode") String typeCode ) {

		logger.info("Mensaje : " + id);

		String fileName = "";
		byte[] bytes = null;
		InputStream inputStream = null;

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("uploadFile");

		for (InputPart inputPart : inputParts) {
			try {
				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = UtilFiles.getFileName(header);

				inputStream = inputPart.getBody(InputStream.class, null);
				bytes = IOUtils.toByteArray(inputStream);
				//writeFile(bytes, "C:/file/" + fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			ContentFileInfo file = new ContentFileInfo();
			file = new ContentFileInfo();
			file.setFile(bytes);
			file.setFileName(fileName);
			file.setId(id + fileName);

			Gson gson = new Gson();
			PostInfo post = new PostInfo();
			post.setExternal_unique_id("Test_" + id + "_" + fileName);
			post.setTitle("Test_" + id + "_" + fileName);
			post.setDate(HourSystem());

			FieldsMappingPpdDAO fieldDAO = new FieldsMappingPpdDAO();
			FieldsMappingPpd fi = fieldDAO.getFieldMappingByName("technical_id");

			Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();
			map.put("realuser", new ResultBuilderDto(fi.getNameDestination(), "default", ""));
			Map<String, ResultBuilderDto> mapRes = QueryBuilder.getInstance().convert(map, id, "");


			post.setEmployee_technical_id(mapRes.get("realuser").getResult());
			post.setDocument_type_code(typeCode);

			logger.info("Json : "+gson.toJson(post));

			ResponseGenericDto resp = ppdGeneratorFacade.wServiceSetCompanyDocument("X-KEY", file, gson.toJson(post));
			if(resp.getCode().equals(UtilCodesEnum.CODE_ERROR.getCode())) {
				return Response.status(201).entity("fail: "+resp.getMessage()).build();
			}else {
				return Response.status(201).entity("ok").build();
			}	
		} catch (Exception ex) {
			logger.error(">> User not created in People Doc", ex);
			return null;
		}

	}
	
	/*private void writeFile(byte[] content, String filename) throws IOException {

		File file = new File(filename);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

	}*/
	
	public String HourSystem() {
		String hour="";
		
		Calendar calendar= Calendar.getInstance();
		
	
		hour= clock(calendar.get(Calendar.YEAR))+"-"+clock(calendar.get(Calendar.MONTH)+1)+"-"+clock(calendar.get(Calendar.DAY_OF_MONTH));
		
		return hour;
	}
	
	public String clock(int number) {
		String input;
		
		if(number<10) {
			input="0"+Integer.toString(number);
			return input;
		}else {
		
		return  Integer.toString(number);
		}
	}
	
	/**
	 * Get info warn of Document Generated
	 * @param String id
	 * @param Integer num 
	 * @return Response
	 * */
	@GET
	@Path("/generated/infowarn/{id}/{number}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentInfoWarn(@PathParam("id") String id, @PathParam("number") Integer num ) {
		String jsonPage = null;
		jsonPage=ppdTemplateGeneratorFacade.getGeneratedNumberPages(id, num);
		if(!jsonPage.equals("")) 
		{
			return Response.status(200).entity(200).build();
		}else{
			return Response.status(200).entity(500).build();	
		}
	}
	
	/**
	 * Get Process Sign
	 * @param long idDoc
	 * @return ArrayList<SignatureFileControl>
	 * 
	 * **/
	@GET
	@Path("/signatureprocess/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response signatureGetProcessByDoc(@PathParam("id") Long id)
	{
		//update status document and status signature control according to the state in ppd
		successFactorSingF.signatureUpdateStatusByStatusPpd(id);
		
		ArrayList<SignatureFileControl> arraySings = sucessFactorFacade.signatureFileControlByDoc(id,null,null,null);
		
		if(!arraySings.isEmpty()) 
		{
			for(SignatureFileControl item:arraySings) {
				item.setGenerated(null);				
			}
			
			
			return Response.status(200).entity(arraySings).build();
		}
		else {
			return Response.status(200).entity(200).build();
		}
	}
}
