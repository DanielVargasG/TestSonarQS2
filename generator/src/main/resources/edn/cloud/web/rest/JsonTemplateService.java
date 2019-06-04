package edn.cloud.web.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilFiles;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.SignatureFieldDto;
import edn.cloud.business.dto.integration.FolderDTO;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.ppdoc.business.facade.PpdTemplateFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorTemplateFacade;
import edn.cloud.sfactor.business.facade.document.SuccessFactorAuthoFacade;
import edn.cloud.sfactor.persistence.dao.FavoriteDAO;
import edn.cloud.sfactor.persistence.dao.FolderDAO;
import edn.cloud.sfactor.persistence.dao.FolderPermissionDAO;
import edn.cloud.sfactor.persistence.dao.FolderTemplateDAO;
import edn.cloud.sfactor.persistence.dao.FolderUserDAO;
import edn.cloud.sfactor.persistence.entities.AuthorizationDetails;
import edn.cloud.sfactor.persistence.entities.AuthorizationDocument;
import edn.cloud.sfactor.persistence.entities.DocumentFields;
import edn.cloud.sfactor.persistence.entities.Favorite;
import edn.cloud.sfactor.persistence.entities.FieldsMappingMeta;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;
import edn.cloud.sfactor.persistence.entities.FieldsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.Folder;
import edn.cloud.sfactor.persistence.entities.FolderGroup;
import edn.cloud.sfactor.persistence.entities.FolderTemplate;
import edn.cloud.sfactor.persistence.entities.FolderUser;
import edn.cloud.sfactor.persistence.entities.Template;
import edn.cloud.sfactor.persistence.entities.TemplateFilters;
import edn.cloud.web.service.SessionCreateFilter;

@Path("/json")
public class JsonTemplateService {
	@Context
	HttpServletRequest request;
	@Context
	HttpServletResponse response;

	// -----------------------------------------------------------
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private UtilLogger loggerSingle = UtilLogger.getInstance();

	private PpdTemplateFacade ppdTemplateGeneratorFacade = new PpdTemplateFacade();
	private SuccessFactorFacade sucessFacade = new SuccessFactorFacade();
	private SuccessFactorTemplateFacade SFTemplateFacade = new SuccessFactorTemplateFacade();
	private SuccessFactorAuthoFacade SFAuthoFacade = new SuccessFactorAuthoFacade();
	private SuccessFactorAdminFacade SFAdminFacade = new SuccessFactorAdminFacade();

	private byte[] data3;

	// -----------------------------------------------------------
	@GET
	@Path("/templatesfields/{idTemplate}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFieldsTemplateList(@PathParam("idTemplate") Long idTemplate) {
		PpdDocGenRequestPayloadDto response = new PpdDocGenRequestPayloadDto();
		response = SFTemplateFacade.templateGetFieldsList(idTemplate);
		return Response.status(200).entity(response.getVariables()).build();
	}

	// -----------------------------------------------------------
	@GET
	@Path("/templates")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTemplateList() {
		List<TemplateInfoDto> doc = new ArrayList<TemplateInfoDto>();
		doc = SFTemplateFacade.templateGetListByGroupUser(getSFUser(), null);
		return Response.status(200).entity(doc).build();
	}

	// -----------------------------------------------------------
	@GET
	@Path("/templatesRec")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTemplateListRec() {
		List<TemplateInfoDto> doc = new ArrayList<TemplateInfoDto>();
		List<TemplateInfoDto> docOut = new ArrayList<TemplateInfoDto>();
		doc = SFTemplateFacade.templateGetListByGroupUser(getSFUser(), null);

		loggerSingle.gson(doc);

		for (TemplateInfoDto templateInfoDto : doc) {
			if (templateInfoDto.getModule().equals("REC")) {
				docOut.add(templateInfoDto);
			}
		}

		return Response.status(200).entity(docOut).build();
	}

	// -----------------------------------------------------------
	@GET
	@Path("/template/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTemplate(@PathParam("id") String id) {
		TemplateInfoDto response = new TemplateInfoDto();
		response = SFTemplateFacade.templateGetById(Long.valueOf(id));

		if (response != null) {

			FolderTemplateDAO fdao = new FolderTemplateDAO();
			FolderTemplate fd = fdao.getByTemplaterId(response.getIdTemplate());

			if (fd != null) {
				response.setFolder(fd.getFolderId());
			} else {
				response.setFolder(UtilCodesEnum.CODE_INVALID_OR_NONE.getCodeLong());
			}

			response.setGenerateVariables(SFTemplateFacade.templateGetFieldsList(Long.valueOf(id)));
			response.setSingatureGroup(SFTemplateFacade.templateGetSignatureGroupList(Long.valueOf(id)));
			response.setFilters(SFTemplateFacade.templateFiltersGetAllById(Long.valueOf(id)));
			response.setMetadataList(SFTemplateFacade.getMetadataTemplate(Long.valueOf(id), false));

			response.isFileUpload = false;
			response.isFileWithSign = false;
			if (response.getLatest_version() > 0)
				response.isFileUpload = true;

			if (response.getSingatureGroup() != null && response.getSingatureGroup().size() > 0 && response.getSingatureGroup().get(0).getSignatures().size() > 0)
				response.isFileWithSign = true;

			return Response.status(200).entity(response).build();
		} else {
			return Response.status(200).entity(response).build();
		}
	}

	// -----------------------------------------------------------
	@PUT
	@Path("/template/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateUpdate(@PathParam("id") String id, String val) {
		TemplateInfoDto response = new TemplateInfoDto();
		response = ppdTemplateGeneratorFacade.updateTemplateId(id, val);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			loggerSingle.error("Template not generated");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new TemplateInfoDto()).build();
		}
	}

	// -----------------------------------------------------------
	@GET
	@Path("/template/{id}/{number}/{page}")
	@Produces("image/png")
	public byte[] getPrevTemplate(@PathParam("id") String id, @PathParam("number") Integer num, @PathParam("page") Integer page) {
		try {
			data3 = ppdTemplateGeneratorFacade.getPrevDocument(id, num, page);
			return data3;
		} catch (Exception ex) {

			logger.error("> error - Connection was not working with PPDoc");
			logger.error(ex.toString());
			return null;
		}

	}

	// ----------------------------------------------------------------------
	@DELETE
	@Path("/template/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTemplate(@PathParam("id") String id) {
		String data2 = "";
		try {

			TemplateInfoDto tmplDTO = SFTemplateFacade.templateGetById(Long.parseLong(id));

			sucessFacade.deleteTemplate(tmplDTO);

			FolderTemplateDAO ftao = new FolderTemplateDAO();
			ftao.deleteAllTempsByTempsId(tmplDTO.getIdTemplate());

			FavoriteDAO favDAO = new FavoriteDAO();
			Collection<Favorite> li = favDAO.findAllByTemp(id);

			for (Favorite favorite : li) {
				logger.info(favorite.toString());
				FavoriteDAO favDAOtmp = new FavoriteDAO();
				favDAOtmp.delete(favorite);
			}

			return Response.status(200).entity(data2).build();
		} catch (Exception ex) {

			logger.error("> error - Delete Template");
			logger.error(ex.getMessage());

			return Response.status(200).entity("it was not possible to eliminate the template, verify dependencies").build();
		}

	}

	// ----------------------------------------------------
	/**
	 * update file to template
	 **/
	@POST
	@Path("/upload/{idTemplate}/{id}/{version}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadTemplateDocument(MultipartFormDataInput input, @PathParam("idTemplate") Long templateId, @PathParam("id") String id, @PathParam("version") String version) {
		String fileName = "";
		byte[] bytes = null;
		InputStream inputStream = null;

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("template");

		for (InputPart inputPart : inputParts) {
			try {
				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = UtilFiles.getFileName(header);

				inputStream = inputPart.getBody(InputStream.class, null);
				bytes = IOUtils.toByteArray(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// create template document in platform people doc
		GenResponseInfoDto genErrorReturn = ppdTemplateGeneratorFacade.createTemplateDoc(templateId, id, bytes, fileName);
		if (genErrorReturn.getFlag()) {
			UtilFiles utilFiles = new UtilFiles();

			ArrayList<SlugItem> listSlugItems = new ArrayList<>();
			inputStream = new ByteArrayInputStream(bytes);
			listSlugItems = utilFiles.getItemsToDocX(inputStream, UtilCodesEnum.CODE_SIGN.getCode());

			if (SFTemplateFacade.templateSaveSings(templateId, listSlugItems)) {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_SUCCESS_200.getCode()).build();
			} else {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
			}
		} else {
			return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).entity(genErrorReturn.getMessage()).build();
		}
	}

	// ---------------------------------------------------------------------------
	/**
	 * update signature template
	 */
	@POST
	@Path("/templates/upload_signature/{idSignature}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSignatureTemplate(@PathParam("idSignature") Long idSignature, String val) {
		logger.info("updateSignatureTemplate " + val);

		Gson token = new Gson();
		SignatureFieldDto objParam = token.fromJson(val, SignatureFieldDto.class);
		objParam = SFTemplateFacade.templateSignatureUpdate(idSignature, objParam);

		if (objParam != null) {
			TemplateInfoDto response = SFTemplateFacade.templateGetById(objParam.getIdTemplate());

			response.setSingatureGroup(SFTemplateFacade.templateGetSignatureGroupList(objParam.getIdTemplate()));
			logger.info("valor de array despues de actualizar signature " + response.getSingatureGroup().size());
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	// ---------------------------------------------------------------------------
	/**
	 * update field template
	 */
	@POST
	@Path("/templates/upload_field/{idTemplate}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateFieldTemplate(@PathParam("idTemplate") Long templateId, String val) {
		logger.info("updateFieldTemplate " + val);

		Gson token = new Gson();
		SlugItem objParam = token.fromJson(val, SlugItem.class);
		objParam = SFTemplateFacade.templateUpdateFields(templateId, objParam);

		TemplateInfoDto response = SFTemplateFacade.templateGetById(templateId);
		if (objParam != null) {
			response.setGenerateVariables(SFTemplateFacade.templateGetFieldsList(templateId));
			logger.info("valor de array despues de actualizar " + response.getGenerateVariables().getVariables().size());
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	// ---------------------------------------------------------------------------
	/**
	 * Create template
	 */
	@POST
	@Path("/templates")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateCreate(String val) {

		String userid = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);

		TemplateInfoDto response = new TemplateInfoDto();
		response = ppdTemplateGeneratorFacade.createTemplateId(val);

		List<FolderDTO> flist = SFTemplateFacade.templateFoldersStructure(userid, null);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(flist).build();
		} else {
			loggerSingle.error("Template not generated");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new TemplateInfoDto()).build();
		}
	}

	// -----------------------------------------------------------------------------
	/**
	 * insert template filter
	 */
	@POST
	@Path("/templates/insert_filter/{idTemplate}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateFilterInsert(@PathParam("idTemplate") Long idTemplate, String val) {
		Gson token = new Gson();
		TemplateFilters filter = token.fromJson(val, TemplateFilters.class);

		Template template = new Template();
		template.setId(idTemplate);
		filter.setTemplateId(template);

		filter = SFTemplateFacade.templateFiltersInsert(filter);

		if (filter != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(filter).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * delete template filter
	 * 
	 * @param Long
	 *            idTemplate
	 * @param Long
	 *            idFilter
	 * @return ArrayList<FieldsMappingPpd>
	 */
	@DELETE
	@Path("/templates/delete_filter/{idTemplate}/{idFilter}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateFilterDelete(@PathParam("idTemplate") Long idTemplate, @PathParam("idFilter") Long idFilter) {
		if (SFTemplateFacade.templateFiltersDelete(idFilter)) {
			ArrayList<TemplateFilters> listReturn = SFTemplateFacade.templateFiltersGetAllById(idTemplate);
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * update template filter
	 */
	@POST
	@Path("/templates/update_filter/{idTemplate}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateFilterUpdate(@PathParam("idTemplate") Long idTemplate, String val) {
		Gson token = new Gson();
		TemplateFilters filter = token.fromJson(val, TemplateFilters.class);
		Template template = new Template();
		template.setId(idTemplate);
		filter.setTemplateId(template);

		filter = SFTemplateFacade.templateFiltersUpdate(filter);

		if (filter != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(filter).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	// -----------------------------------------
	// methods template field library

	@GET
	@Path("/templatefieldlib")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateFieldLib() {
		ArrayList<FieldsTemplateLibrary> listReturn = SFTemplateFacade.templateFieldLibraryGetAll();
		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * update template field library
	 * 
	 * @param Long
	 *            idField
	 * @param String
	 *            val
	 */
	@POST
	@Path("/templatefieldlib/upload_field/{idField}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateFieldUpdate(@PathParam("idField") Long idField, String val) 
	{
		Gson token = new Gson();
		FieldsTemplateLibrary field = token.fromJson(val, FieldsTemplateLibrary.class);
		field = SFTemplateFacade.templateFieldLibraryUpdate(field);

		if (field != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(field).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * delete template field library
	 * 
	 * @param Long
	 *            idField
	 * @return ArrayList<FieldsTemplateLibrary>
	 */
	@DELETE
	@Path("/templatefieldlib/delete_field/{idField}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateFieldDelete(@PathParam("idField") Long idField) {
		Collection<FieldsMappingMeta> mappingFieldMeta = SFAdminFacade.mappingPpdFieldMetaGetByRef(null, idField);
		if (mappingFieldMeta != null && mappingFieldMeta.size() > 0) {
			return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}

		if (SFTemplateFacade.templateFieldLibraryDelete(idField)) {
			ArrayList<FieldsTemplateLibrary> listReturn = SFTemplateFacade.templateFieldLibraryGetAll();
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * insert template field library
	 * 
	 * @param String
	 *            val
	 * @return Response
	 */
	@POST
	@Path("/templatefieldlib/insert_field/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateFieldInsert(String val) {

		Gson token = new Gson();
		FieldsTemplateLibrary field = token.fromJson(val, FieldsTemplateLibrary.class);
		field = SFTemplateFacade.templateFieldLibraryInsert(field);

		if (field != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(field).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	@GET
	@Path("/template/infopage/{id}/{number}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateInfoPage(@PathParam("id") String id, @PathParam("number") Integer num) {
		String jsonPage = null;
		jsonPage = ppdTemplateGeneratorFacade.getNumberPages(id, num);
		if (!jsonPage.equals("")) {
			String json = jsonPage.replace("[", "");
			ArrayList<String> images = new ArrayList<>();
			int page = Integer.parseInt(json.replace("]", ""));

			for (int i = 1; i <= page; i++) {
				String image = "/generator/rst/json/template/" + id + "/" + num + "/" + i;

				images.add(image);
			}
			return Response.status(200).entity(images).build();
		} else {
			return Response.status(200).entity(500).build();
		}
	}

	// ---------------------------------------------------------------------------
	/**
	 * Create folder
	 */
	@POST
	@Path("/folders")
	@Produces(MediaType.APPLICATION_JSON)
	public Response folderCreate(String val) {

		String userId = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);

		Folder response2 = new Folder();
		response2 = ppdTemplateGeneratorFacade.createFolderId(val, userId);
		List<FolderDTO> flist = SFTemplateFacade.templateFoldersStructure(userId, null);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(flist).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@POST
	@Path("/foldersE")
	@Produces(MediaType.APPLICATION_JSON)
	public Response folderEdit(String val) {

		String userid = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);

		Folder response2 = new Folder();
		response2 = ppdTemplateGeneratorFacade.createFolderId(val, "");

		List<FolderDTO> flist = SFTemplateFacade.templateFoldersStructure(userid, null);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(flist).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@GET
	@Path("/foldersLoggedInUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFoldersLogged(String val) {

		FolderDAO fdao = new FolderDAO();
		List<Folder> response = fdao.getAll();

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@GET
	@Path("/folders")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFolders(String val) {

		FolderDAO fdao = new FolderDAO();
		List<Folder> response = fdao.getAll();

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@DELETE
	@Path("/folders/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFolder(@PathParam("val") Long val) {

		String userid = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);

		FolderDAO fdao = new FolderDAO();
		FolderUserDAO fudao = new FolderUserDAO();
		FolderPermissionDAO fpdao = new FolderPermissionDAO();
		FolderTemplateDAO ftdao = new FolderTemplateDAO();

		Folder response2 = fdao.getById(val);

		// delete subfolders
		ftdao.deleteAllAssoWithSubFolders(response2.getId());
		fdao.deleteAllAssoWithSubFolders(response2.getId());

		// delete folders 1 level
		fdao.delete(response2);
		fudao.deleteAllUsersByFolderId(val);
		fpdao.deleteAllUsersByFolderId(val);
		ftdao.deleteAllUsersByFolderId(val);

		List<FolderDTO> flist = SFTemplateFacade.templateFoldersStructure(userid, null);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(flist).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@GET
	@Path("/folders/users/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFoldersUsers(@PathParam("val") Long val) {

		FolderUserDAO fudao = new FolderUserDAO();
		Collection<FolderUser> response = fudao.getAllUsersByFolderId(val);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@GET
	@Path("/folders/permissions/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFoldersPermissions(@PathParam("val") Long val) {

		FolderPermissionDAO fpdao = new FolderPermissionDAO();
		Collection<FolderGroup> response = fpdao.getAllUsersByFolderId(val);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@DELETE
	@Path("/folders/users/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFoldersUsers(@PathParam("val") Long val) {

		FolderUserDAO fudao = new FolderUserDAO();
		FolderUser fu = fudao.getById(val);

		fudao.delete(fu);

		Collection<FolderUser> response = fudao.getAllUsersByFolderId(fu.getFolderId());

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@DELETE
	@Path("/folders/permissions/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFoldersPerms(@PathParam("val") Long val) {

		FolderPermissionDAO fudao = new FolderPermissionDAO();
		FolderGroup fu = fudao.getById(val);

		fudao.delete(fu);

		Collection<FolderGroup> response = fudao.getAllUsersByFolderId(fu.getFolderId());

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@POST
	@Path("/folders/users/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postFoldersUsers(@PathParam("val") Long val, String cont) {

		logger.info(cont);

		JSONObject json = new JSONObject(cont);

		FolderUserDAO fudao = new FolderUserDAO();

		FolderUser folderuser = new FolderUser();
		folderuser.setFolderId(val);
		folderuser.setUserId(json.getString("user"));

		fudao.saveNew(folderuser);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(folderuser).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@POST
	@Path("/folders/permissions/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postFoldersPerms(@PathParam("val") Long val, String cont) {

		logger.info(cont);

		JSONObject json = new JSONObject(cont);

		FolderPermissionDAO fudao = new FolderPermissionDAO();

		FolderGroup foldergroup = new FolderGroup();
		foldergroup.setFolderId(val);
		foldergroup.setGroupId(json.getString("groupId"));

		fudao.saveNew(foldergroup);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(foldergroup).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@GET
	@Path("/foldersStructure")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFoldersFull() {
		String userId = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);
		List<FolderDTO> flist = SFTemplateFacade.templateFoldersStructure(userId, null);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(flist).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@GET
	@Path("/foldersList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFoldersList() {
		String userId = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);
		List<FolderDTO> flist = SFTemplateFacade.templateFoldersListAll(userId);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(flist).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@GET
	@Path("/foldersparent")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFoldersParents() {

		String userid = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);

		FolderUserDAO fudao = new FolderUserDAO();
		FolderDAO fdao = new FolderDAO();
		List<FolderDTO> flist = new ArrayList<>();
		Collection<FolderUser> fu = fudao.getAllFolderByUserId(userid, null);

		for (FolderUser folderUser : fu) {
			FolderDTO folder = new FolderDTO();
			folder.setId(folderUser.getFolderId());

			Folder f = fdao.getById(folderUser.getFolderId());
			folder.setTitle(f.getTitle());

			flist.add(folder);
		}

		FolderDTO folderNone = new FolderDTO();
		folderNone.setId(UtilCodesEnum.CODE_INVALID_OR_NONE.getCodeLong());
		folderNone.setTitle("None");
		folderNone.catSeeEdit = false;
		folderNone.catSeeEnter = false;
		folderNone.catSeeNothing = true;
		flist.add(folderNone);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(flist).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@GET
	@Path("/folderstest")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFoldersTest() {
		FolderTemplateDAO ftdao = new FolderTemplateDAO();
		Collection<FolderTemplate> response = ftdao.getAll();
		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			loggerSingle.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	/**
	 * @return String idUser
	 */
	private String getSFUser() {
		return (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);
	}

	/**
	 * Get info page of Template
	 * 
	 * @param String
	 *            id
	 * @param Integer
	 *            num
	 * @return Response
	 */
	@GET
	@Path("/template/infowarn/{id}/{number}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateWarnDoc(@PathParam("id") String id, @PathParam("number") Integer num) {
		String jsonPage = null;
		jsonPage = ppdTemplateGeneratorFacade.getNumberPages(id, num);
		if (!jsonPage.equals("")) {
			return Response.status(200).entity(200).build();
		} else {
			return Response.status(200).entity(500).build();
		}
	}

	/**
	 * Get info search Template
	 * 
	 * @param String
	 *            search
	 * @return Response
	 */
	@GET
	@Path("/searchtemplate/{search}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchTemplate(@PathParam("search") String search) {
		List<TemplateInfoDto> flist = SFTemplateFacade.templateGetList(false, false);
		ArrayList<TemplateInfoDto> listReturn = new ArrayList<>();

		for (TemplateInfoDto template : flist) {
			if (template.getDescription().toUpperCase().contains(search.toUpperCase())) {
				listReturn.add(template);
			} else if (template.getTitle().toUpperCase().contains(search.toUpperCase())) {
				listReturn.add(template);
			} else if (search.equals("all")) {
				listReturn.add(template);
			}
		}

		return Response.status(200).entity(listReturn).build();

	}

	/**
	 * Get info search Template for auto generation
	 * 
	 * @param String
	 *            search
	 * @return Response
	 */
	@GET
	@Path("/searchtselfemplate/{search}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchTemplateSelfEmployee(@PathParam("search") String search) {
		String userTarget = getSFUser();
		List<TemplateInfoDto> flist = SFTemplateFacade.templateGetListByGroupUser(getSFUser(), UtilCodesEnum.CODE_ACTIVE_NUMBER.getCode());
		ArrayList<TemplateInfoDto> listReturn = new ArrayList<>();

		for (TemplateInfoDto template : flist) {
			if (template.getLatest_version() != null && template.getLatest_version() > 0 && !(template.getManagerConfirm() != null && template.getManagerConfirm().equals(UtilCodesEnum.CODE_MANAGER_TYPE_SIGNATURE.getCode()))) {
				GenResponseInfoDto response = SFTemplateFacade.templateFilterValidateOnUser(template.getIdTemplate(), userTarget, UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), new Date()));
				if (response.getCode().equals(UtilCodesEnum.CODE_OK.getCode())) {
					if (template.getDescription().toUpperCase().contains(search.toUpperCase())) {
						listReturn.add(template);
					} else if (template.getTitle().toUpperCase().contains(search.toUpperCase())) {
						listReturn.add(template);
					} else if (search.equals("all")) {
						listReturn.add(template);
					}
				}
			}
		}

		return Response.status(200).entity(listReturn).build();

	}

	/**
	 * POST Filter Template
	 * 
	 * @param String
	 *            val
	 * @return Response
	 */
	@POST
	@Path("/filtertemplate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchTemplateFilter(String val) {
		logger.info("Filter Template ---------------> " + val);

		JSONObject json = new JSONObject(val);
		FilterQueryDto filter = new FilterQueryDto();
		HashMap<String, String> hm = new HashMap<>();
		hm.put("idFolder", json.getString("idFolder"));
		hm.put("idEvent", json.getString("idEvent"));
		filter.setItem(hm);
		String userId = (String) request.getSession().getAttribute(SessionCreateFilter.SF_USER_ID_ATTR_NAME);
		List<FolderDTO> flist = SFTemplateFacade.templateFoldersStructure(userId, filter);
		ArrayList<TemplateInfoDto> listReturn = new ArrayList<>();

		return Response.status(200).entity(flist).build();

	}

	/**
	 * Insert Metadata Template
	 * 
	 * @param idTmp
	 * @param val
	 * @return
	 */
	@POST
	@Path("/template/metadatainsert/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateInsertMetada(@PathParam("id") Long idTmp, String val) {

		try {
			JSONObject json = new JSONObject(val);
			String metaData = json.get("metaData").toString();

			if (metaData != null) {
				String[] metaDataList = metaData.split(",");

				SFTemplateFacade.templateAllMetadataDelete(idTmp);
				if (metaDataList.length > 0) {
					for (String id : metaDataList) {
						SFTemplateFacade.templateMetadataInsert(idTmp, Long.parseLong(UtilMapping.toStringApplyFormat(id, UtilCodesEnum.CODE_PATRON_NUMBERS.getCode())));
					}
				}
			}
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).build();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}

	}

	@GET
	@Path("/template/auth/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response templateAuth(@PathParam("id") Long idTmp) {
		String userTarget = getSFUser();
		try {
			edn.cloud.business.dto.GenErrorInfoDto response = SFAuthoFacade.templateInsertAuth(idTmp, userTarget);

			if (response != null && response.getFlag())
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).build();
			else if (response != null && response.getCode() != null)
				return Response.status((Integer.parseInt(response.getCode()))).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).build();
		}

		return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
	}

	@GET
	@Path("/template/getauth/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authoValidateCreateNew(@PathParam("id") Long idTmp) {
		String userTarget = getSFUser();
		try {

			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(SFAuthoFacade.authoValidateCreateNew(idTmp, userTarget)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * Get Authorization Details of Status
	 * 
	 * @return Response
	 */
	@GET
	@Path("/template/getauthuser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authoDetailGetByUserAutho() {
		String userTarget = getSFUser();
		try {

			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(SFAuthoFacade.authoDetailGetByUserAuthorizes(userTarget)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * Update AuthorizationDetails
	 * 
	 * @param id
	 * @param status
	 * @return Response
	 */
	@GET
	@Path("/template/updateauthuser/{id}/{status}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAuthorizationDetails(@PathParam("id") Long id, @PathParam("status") String status) {
		String userTarget = getSFUser();
		try {
			AuthorizationDetails authoDet = SFAuthoFacade.updateAuthorizationDetails(id, status);
			if (authoDet != null) {
				// load all detail of authorizations
				SFAuthoFacade.authoFinishProccess(authoDet.getAuthDocument().getId());
			}

			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(true).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * Get Status Authorization
	 * 
	 * @param idTmp
	 * @return Response
	 */
	@GET
	@Path("/template/updateauthuser/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatusAutho(@PathParam("id") Long id) {
		String userTarget = getSFUser();
		try {
			List<AuthorizationDocument> autDoc = SFAuthoFacade.getStatusAuthorization(userTarget, id);
			for (AuthorizationDocument autDet : autDoc) {
				if (autDet.getEnable() == Boolean.FALSE) {
					if (autDet.getStatus().equals(UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_PENDING.getCode())) {
						return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(autDoc).build();
					}
					// return
					// Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(autDet).build();
				}

			}
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(autDoc).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * Get Status Authorization
	 * 
	 * @param idTmp
	 * @return Response
	 */
	/*
	 * @GET
	 * 
	 * @Path("/template/stepsautho/{id}")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response
	 * getStepAuthorization(@PathParam("id") Long id){ String userTarget=
	 * getSFUser(); try { AuthorizationDetails autDetails =
	 * SFAuthoFacade.getAuthorizationStep(id, userTarget); return
	 * Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(
	 * autDetails).build(); } catch (Exception e) { e.printStackTrace(); return
	 * Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).entity(
	 * UtilCodesEnum.CODE_ERROR_500.getCode()).build(); } }
	 */

	/**
	 * Delete Status Authorization
	 * 
	 * @param idTmp
	 * @return Response
	 */
	/*
	 * @DELETE
	 * 
	 * @Path("/template/deleteauthuser/{id}")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response
	 * deleteStatusAutho(@PathParam("id") Long id){
	 * 
	 * Boolean rs = SFAuthoFacade.deleteAuthorization(id);
	 * 
	 * if(rs) { return
	 * Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).build(); }else {
	 * return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).build(); }
	 * }
	 */

	/**
	 * Get Status Authorization
	 * 
	 * @param idTmp
	 * @return Response
	 */
	@GET
	@Path("/template/statusauthodoc/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatusAuthorizationdoc(@PathParam("id") Long id) {
		String userTarget = getSFUser();
		try {
			Boolean rs = SFAuthoFacade.updateStatusAuthorizationDocument(userTarget, id);
			if (rs) {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).build();
			} else {
				return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).build();
		}
	}

	/**
	 * Get Authorization document
	 * 
	 * @param id
	 * @param idDoc
	 * @return
	 */
	@GET
	@Path("/template/getdocauthorization/{idTmp}/{idDoc}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocAuthorization(@PathParam("idTmp") Long id, @PathParam("idDoc") Long idDoc) {
		String userTarget = getSFUser();
		try {
			// Boolean rs = SFAuthoFacade.getDocAuthorization(userTarget, id, idDoc);

			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).build();
		}
	}

	/**
	 * Get Info Authorization Document
	 * 
	 * @param user
	 * @return List<AuthorizationDocument>
	 */
	@GET
	@Path("/template/getinfoauthorization")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInfoAuthorizationDocument() {
		String userTarget = getSFUser();

		List<AuthorizationDocument> listReturn = SFAuthoFacade.getInfoAuthorizationDocument(userTarget);

		if (!listReturn.isEmpty()) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).build();
		}
	}

	/**
	 * Delete Authorization Document
	 * 
	 * @param idDocument
	 */
	@GET
	@Path("/template/authocancel/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authoCancel(@PathParam("id") Long id) {
		try {
			String userTarget = getSFUser();

			AuthorizationDocument autho = SFAuthoFacade.authoGetById(id);
			autho.setLastUpdateOn(new Date());
			autho.setStatus(UtilCodesEnum.CODE_MANAGER_CANCELED.getCode());
			SFAuthoFacade.authorizationUpdate(autho);

			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(true).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).build();
		}
	}

	@GET
	@Path("/template/authogetvaridoc/{idAutho}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authoGetVariDoc(@PathParam("idAutho") Long idAutho) {
		AuthorizationDocument autho = SFAuthoFacade.authoGetById(idAutho);
		TemplateInfoDto templateDtoInfo = new TemplateInfoDto();

		if (autho != null) {

			// load values to Fields templates
			PpdDocGenRequestPayloadDto generateVariables = SFTemplateFacade.templateGetFieldsList(autho.getTemplate().getId());

			PpdDocGenRequestPayloadDto genVarFinal = SFTemplateFacade.templateGetValueQueryBuilder(generateVariables, autho.getUserEmp(), true, UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(), new Date()),
					true);

			templateDtoInfo.setGenerateVariables(genVarFinal);
		}

		return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(templateDtoInfo).build();

	}
}
