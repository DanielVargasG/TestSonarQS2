package edn.cloud.web.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.internal.stubbing.answers.ThrowsException;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilFiles;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.UIConfig;
import edn.cloud.business.dto.integration.DocInfoDto;
import edn.cloud.business.dto.integration.FolderDTO;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.SFAdmin;
import edn.cloud.business.dto.integration.SFDocumentType;
import edn.cloud.business.dto.integration.SFGroup;
import edn.cloud.business.dto.integration.SFGroupList;
import edn.cloud.business.dto.integration.SFUserDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.integration.localizedLabel;
import edn.cloud.business.dto.odata.UserManager;
import edn.cloud.business.dto.ppd.api.PpdUserDto;
import edn.cloud.business.dto.sfactor.SFEventListenerListDto;
import edn.cloud.ppdoc.business.connectivity.PpdHttpConnectorV1;
import edn.cloud.ppdoc.business.facade.PpdApiUtilsFacade;
import edn.cloud.ppdoc.business.facade.PpdEmployeeApiFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorAttachFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorEventLFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorMassiveLoadFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorTemplateFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorWebServiceFacade;
import edn.cloud.sfactor.business.facade.document.SuccessFactorAuthoFacade;
import edn.cloud.sfactor.business.utils.DataBaseBuilder;
import edn.cloud.sfactor.business.utils.QueryBuilder;
import edn.cloud.sfactor.persistence.dao.EmployeeDAO;
import edn.cloud.sfactor.persistence.dao.FieldsMappingPpdDAO;
import edn.cloud.sfactor.persistence.dao.LoggerDAO;
import edn.cloud.sfactor.persistence.dao.RolesMappingGroupPpdDAO;
import edn.cloud.sfactor.persistence.dao.SfEntityDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.AuthorizationDetails;
import edn.cloud.sfactor.persistence.entities.Countries;
import edn.cloud.sfactor.persistence.entities.Employee;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerDocHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerParam;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;
import edn.cloud.sfactor.persistence.entities.FieldsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.Folder;
import edn.cloud.sfactor.persistence.entities.Language;
import edn.cloud.sfactor.persistence.entities.LoggerAction;
import edn.cloud.sfactor.persistence.entities.LookupTable;
import edn.cloud.sfactor.persistence.entities.ManagerRole;
import edn.cloud.sfactor.persistence.entities.MassiveLoadUser;
import edn.cloud.sfactor.persistence.entities.RoleMappingGroupPpd;
import edn.cloud.sfactor.persistence.entities.RolesMappingPpd;
import edn.cloud.sfactor.persistence.entities.SfEntity;
import edn.cloud.sfactor.persistence.entities.SignsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.StructureBusiness;
import edn.cloud.web.soap.EenAlertRequestMeta;
import edn.cloud.web.soap.EenUser;

@Path("/json/admin")
public class JsonAdminService {

	@Context
	HttpServletResponse response;

	private final UtilLogger logger = UtilLogger.getInstance();
	private SuccessFactorWebServiceFacade succesFactorWebServFacade = new SuccessFactorWebServiceFacade();
	private SuccessFactorAdminFacade succesFactorAdminFacade = new SuccessFactorAdminFacade();
	private SuccessFactorFacade succesFactorFacade = new SuccessFactorFacade();
	private SuccessFactorAttachFacade successFactorAttachFacade = new SuccessFactorAttachFacade();
	private PpdEmployeeApiFacade ppdEmployeeF = new PpdEmployeeApiFacade();
	private PpdApiUtilsFacade ppdApiUtilsFacade = new PpdApiUtilsFacade();
	private UtilMapping utilMapping = new UtilMapping();

	// -----------------------------------------
	// methods utils

	/**
	 * get documents type
	 * 
	 * @return SFDocumentType[]
	 */
	@GET
	@Path("/docsType")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ppdUtilGetDocumentType() {
		SFDocumentType[] listReturn = ppdApiUtilsFacade.wServiceGetDocumentType(null);
		ArrayList<SFDocumentType> listArrayReturn = new ArrayList<>(Arrays.asList(listReturn));

		if (listReturn != null) {
			SFDocumentType item = new SFDocumentType();
			item.setId(null);
			item.setLabel("N/A");
			listArrayReturn.add(item);

			return Response.status(200).entity(listArrayReturn).build();
		}

		return Response.status(200).entity(null).build();

	}

	/**
	 * get documents type
	 * 
	 * @return SFDocumentType[]
	 */
	@GET
	@Path("/docsTypeDocs/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ppdUtilGetDocumentTypeAndDocs(@PathParam("id") String id) {
		SFDocumentType[] listReturn = ppdApiUtilsFacade.wServiceGetDocumentType(null);

		try {
			FieldsMappingPpdDAO fieldDAO = new FieldsMappingPpdDAO();
			FieldsMappingPpd fi = fieldDAO.getFieldMappingByName("technical_id");

			Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();
			map.put("realuser", new ResultBuilderDto(fi.getNameDestination(), "default", ""));
			Map<String, ResultBuilderDto> mapRes = QueryBuilder.getInstance().convert(map, id, "");

			String tst = mapRes.get("realuser").getResult().replace(" ", "%20");
			PpdUserDto[] data2 = ppdEmployeeF.wServiceGetEmployee(tst);
			String currentId = data2[0].id;

			DocInfoDto[] data3 = ppdEmployeeF.wServiceEmployeeDocument(currentId);

			for (SFDocumentType SFDocument : listReturn) {

				ArrayList<DocInfoDto> list = new ArrayList<DocInfoDto>();

				if (Arrays.asList(SFDocument.getEmployee_access_permissions()).contains("preview")) {
					SFDocument.setVisible(true);
				} else {
					SFDocument.setVisible(false);
				}

				for (DocInfoDto docInfoDto : data3) {
					if (SFDocument.getId().equals(docInfoDto.document_type_id)) {
						docInfoDto.finalDoc = false;
						list.add(docInfoDto);
					}
				}
				DocInfoDto[] thePlayers = list.toArray(new DocInfoDto[list.size()]);
				if (thePlayers.length > 0) {
					SFDocument.setCountItems(thePlayers.length);
				}
				SFDocument.setEmployeeDocs(thePlayers);
			}

			ArrayList<SFDocumentType> listArrayReturn = new ArrayList<>(Arrays.asList(listReturn));

			SFUserDto sfUser = succesFactorFacade.userGetProfile(id, "9999-01-01");
			String langue = sfUser.callDefLG().substring(0, 2);

			ArrayList<SFDocumentType> finalL = new ArrayList<>();
			for (SFDocumentType tstLst : listArrayReturn) {
				if (tstLst.getCountItems() > 0) {

					for (localizedLabel lc : tstLst.getLocalized_labels()) {
						if (langue.equals(lc.getLanguage_code())) {
							tstLst.setLabel(lc.getValue());
						}
					}
					tstLst.setLocalized_labels(null);
					finalL.add(tstLst);
				}
			}

			return Response.ok(finalL).build();

		} catch (Exception ex) {

			logger.error("> error - Connection was not working with PPDoc");
			logger.error(ex.toString());
			return null;
		}

		// return Response.status(200).entity(listArrayReturn).build();
	}

	// -----------------------------------------
	// methods events

	/**
	 * get all event listener
	 * 
	 * @return ArrayList<EventListener> listReturn
	 */
	@GET
	@Path("/events")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventListenerGet() {
		SFEventListenerListDto listReturn = new SFEventListenerListDto();
		listReturn.setListEvents(succesFactorAdminFacade.eventsListenerGetAll());
		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * update event listener @PathParam("idEvent") Long idField
	 * 
	 * @param String
	 *            val - FieldsMappingPpd.class
	 * @return EventListener event
	 */
	@POST
	@Path("/eventListener/upload_event/{idEvent}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventListenerUpdate(@PathParam("idEvent") Long idEvent, String val) {
		logger.info("eventListenerUpdate " + val);

		Gson token = new Gson();
		EventListenerParam event = token.fromJson(val, EventListenerParam.class);
		event = succesFactorAdminFacade.eventListenerUpdate(event);

		if (event != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(event).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * delete event listener @PathParam("idEvent") Long idField
	 * 
	 * @return ArrayList<EventListener>
	 */
	@DELETE
	@Path("/eventListener/delete_event/{idEvent}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsFieldsGetAll(@PathParam("idEvent") Long idEvent) {
		if (succesFactorAdminFacade.eventListenerDelete(idEvent)) {
			ArrayList<EventListenerParam> listReturn = succesFactorAdminFacade.eventsListenerGetAll();
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * insert event listener
	 * 
	 * @param String
	 *            val EventListener.class
	 * @return EventListener event
	 */
	@POST
	@Path("/eventListener/insert_event/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventListenerInsert(String val) {
		logger.info("insertFieldTemplate " + val);

		Gson token = new Gson();
		EventListenerParam event = token.fromJson(val, EventListenerParam.class);
		event = succesFactorAdminFacade.eventListenerInsert(event);

		if (event != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(event).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	// -----------------------------------------
	// methods mapping

	/**
	 * get all mappig fields to emails
	 * 
	 * @return ArrayList<EventListener> listReturn
	 */
	@GET
	@Path("/mappingFieldPpd_mails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response mappingPpdFieldEmailsSigns() {
		ArrayList<FieldsMappingPpd> listReturn = new ArrayList<>();

		FieldsMappingPpd item = new FieldsMappingPpd();
		item.setNameSource("email");
		item.setNameDestination(UtilCodesEnum.CODE_MAPPING_MAIL.getCode());
		listReturn.add(item);

		item = new FieldsMappingPpd();
		item.setNameSource("email Personal");
		item.setNameDestination(UtilCodesEnum.CODE_MAPPING_MAIL_PER.getCode());
		listReturn.add(item);

		return Response.status(200).entity(listReturn).build();
	}

	@GET
	@Path("/mappingFieldPpd")
	@Produces(MediaType.APPLICATION_JSON)
	public Response mappingPpdField() {
		ArrayList<FieldsMappingPpd> listReturn = succesFactorAdminFacade.mappingPpdFieldsGetAll(null, null, null, Boolean.FALSE);
		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * update field mapping ppd
	 */
	@POST
	@Path("/mappingFieldPpd/upload_field/{idField}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response mappingPpdFieldUpdate(@PathParam("idField") Long idField, String val) {
		logger.info("updateFieldTemplate " + val);

		Gson token = new Gson();
		FieldsMappingPpd field = token.fromJson(val, FieldsMappingPpd.class);
		field = succesFactorAdminFacade.mappingPpdFieldUpdate(idField, field);

		if (field != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(field).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * update field mapping ppd
	 */
	@POST
	@Path("/mappingFieldPpd/upload_fieldmeta/{idField}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response mappingPpdFieldMetaUpdate(@PathParam("idField") Long idField, String val) {
		Gson token = new Gson();

		JSONObject json = new JSONObject(val);
		String metaData = json.get("metaData").toString();

		if (metaData != null) {
			String[] metaDataList = metaData.split(",");

			succesFactorAdminFacade.mappingPpdFieldMetaDelete(idField, null);
			if (metaDataList.length > 0) {
				for (String id : metaDataList) {
					succesFactorAdminFacade.mappingPpdFieldMetaInsert(idField, Long.parseLong(UtilMapping.toStringApplyFormat(id, UtilCodesEnum.CODE_PATRON_NUMBERS.getCode())), null);
				}
			}
		}

		FieldsMappingPpd field = succesFactorAdminFacade.mappingPpdFieldById(idField, Boolean.FALSE);

		if (field != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(field).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * delete field mapping ppd
	 * 
	 * @param Long
	 *            idField
	 * @return ArrayList<FieldsMappingPpd>
	 */
	@DELETE
	@Path("/mappingFieldPpd/delete_field/{idField}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response mappingPpdFieldDelete(@PathParam("idField") Long idField) {
		if (!succesFactorAdminFacade.mappingFieldIsUsedById(idField).getFlag()) {
			if (succesFactorAdminFacade.mappingPpdFieldDelete(idField, null)) {
				ArrayList<FieldsMappingPpd> listReturn = succesFactorAdminFacade.mappingPpdFieldsGetAll(null, null, null, Boolean.FALSE);
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();
			} else {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
			}
		} else {
			// return Response.status(200).entity("Delete Documentsss: ").build();
			return Response.status(UtilCodesEnum.CODE_ERROR_500.getCodeInt()).build();
		}
	}

	/**
	 * insert field mapping ppd
	 */
	@POST
	@Path("/mappingFieldPpd/insert_field/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response mappingPpdFieldInsert(String val) {
		logger.info("insertFieldTemplate " + val);

		Gson token = new Gson();
		FieldsMappingPpd field = token.fromJson(val, FieldsMappingPpd.class);
		field = succesFactorAdminFacade.mappingPpdFieldsInsert(field);

		if (field != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(field).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	@GET
	@Path("/mappingRolePpd/groups/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFoldersUsers(@PathParam("val") Long val) {

		RolesMappingGroupPpdDAO rodao = new RolesMappingGroupPpdDAO();
		Collection<RoleMappingGroupPpd> response = rodao.getAllGroupsByRoleId(val);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			logger.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new Folder()).build();
		}
	}

	@POST
	@Path("/mappingRolePpd/groups/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postFoldersUsers(@PathParam("val") Long val, String cont) {

		logger.info(cont);

		JSONObject json = new JSONObject(cont);

		RolesMappingGroupPpdDAO fudao = new RolesMappingGroupPpdDAO();

		RoleMappingGroupPpd obj = new RoleMappingGroupPpd();
		obj.setRoleMappingId(val);
		obj.setGroupId(json.getString("group"));

		fudao.saveNew(obj);

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(obj).build();
		} else {
			logger.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new RoleMappingGroupPpd()).build();
		}
	}

	@DELETE
	@Path("/mappingRolePpd/groups/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFoldersUsers(@PathParam("val") Long val) {

		RolesMappingGroupPpdDAO fudao = new RolesMappingGroupPpdDAO();

		RoleMappingGroupPpd fu = fudao.getById(val);
		fudao.delete(fu);
		Collection<RoleMappingGroupPpd> response = fudao.getAllGroupsByRoleId(fu.getRoleMappingId());

		if (response != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(response).build();
		} else {
			logger.error("Folder not created");
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(new RoleMappingGroupPpd()).build();
		}
	}

	// -----------------------------------------
	// structure business events

	/**
	 * get all Structure Business
	 * 
	 * @return ArrayList<StructureBusiness> listReturn
	 */
	@GET
	@Path("/structure")
	@Produces(MediaType.APPLICATION_JSON)
	public Response structureBusinessGet() {
		ArrayList<StructureBusiness> listReturn = succesFactorAdminFacade.structureBusinessGetAll();
		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * update Structure Business @PathParam("idStructure") Long idStructure
	 * 
	 * @param String
	 *            val - StructureBusiness.class
	 * @return StructureBusiness event
	 */
	@POST
	@Path("/structure/update/{idParent}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response structureBusinessUpdate(@PathParam("idParent") Long idParent, String val) {
		logger.info("eventListenerUpdate " + val);
		logger.info("eventListenerUpdate " + idParent);
		Gson token = new Gson();
		StructureBusiness entity = token.fromJson(val, StructureBusiness.class);
		entity = succesFactorAdminFacade.structureBusinessUpdate(idParent, entity);

		if (entity != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(entity).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * delete Structure Business @PathParam("idStructure") Long idStructure
	 * 
	 * @return ArrayList<idStructure>
	 */
	@DELETE
	@Path("/structure/delete/{idStructure}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response structureBusinessDelete(@PathParam("idStructure") Long idStructure) {
		if (succesFactorAdminFacade.structureBusinessDelete(idStructure)) {
			ArrayList<StructureBusiness> listReturn = succesFactorAdminFacade.structureBusinessGetAll();
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * Insert / Update Structure Business
	 * 
	 * @param String
	 *            val - StructureBusiness.class
	 * @return StructureBusiness event
	 */
	@POST
	@Path("/structure/insert_update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response structureBusinessInsertUpdate(String val) {
		try {
			JSONObject json = new JSONObject(val);
			Gson gson = new Gson();
			if (json.get("id").equals(null)) {
				StructureBusiness structure = gson.fromJson(val, StructureBusiness.class);
				structure = succesFactorAdminFacade.structureBusinessInsert(structure, null);
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(structure).build();
			} else {
				StructureBusiness entity = gson.fromJson(val, StructureBusiness.class);
				entity = succesFactorAdminFacade.structureBusinessUpdate(Long.parseLong(json.get("id").toString()), entity);
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(entity).build();
			}
		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}
	// methods eventlister control

	/**
	 * get status ctrl process attach
	 * 
	 * @return SlugItem[]
	 */
	@GET
	@Path("/statusCtrEventAttach")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerCtrlProcAttachStatus() {
		ArrayList<SlugItem> listArrayReturn = new ArrayList<SlugItem>();

		SlugItem item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_NOTPROCESS.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_NOTPROCESS.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
		listArrayReturn.add(item);

		return Response.status(200).entity(listArrayReturn).build();
	}

	/**
	 * get status ctrl process
	 * 
	 * @return SlugItem[]
	 */
	@GET
	@Path("/statusCtrEvent")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerCtrlProcStatus() {
		ArrayList<SlugItem> listArrayReturn = new ArrayList<SlugItem>();

		SlugItem item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_NOTPROCESS.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_NOTPROCESS.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode());
		listArrayReturn.add(item);

		return Response.status(200).entity(listArrayReturn).build();
	}

	/**
	 * get all control attachments pending to process
	 * 
	 * @return ArrayList<EventListenerDocProcess> listReturn
	 */
	@GET
	@Path("/attachments/process")
	@Produces(MediaType.APPLICATION_JSON)
	public Response attachmentsGetAll() {
		// get max number of retries
		AdminParameters paramAdminMaxRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_ATTACH.getCode());
		ArrayList<EventListenerDocProcess> list = succesFactorAdminFacade.getAllAttachmentProcess(null, null, null, null);

		if (paramAdminMaxRetriesCode != null) {
			for (EventListenerDocProcess item : list) {
				item.setRetriesInfo(item.getRetries() + "/" + paramAdminMaxRetriesCode.getValue());
			}
		}

		return Response.status(200).entity(list).build();
	}

	/**
	 * get all attachment history
	 * 
	 * @return ArrayList<EventListenerDocHistory> listReturn
	 */
	@GET
	@Path("/attachmentHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public Response attachmentHistoryGetAll() {
		ArrayList<EventListenerDocHistory> list = successFactorAttachFacade.getAllAttachmentHisto();
		return Response.status(200).entity(list).build();
	}

	/**
	 * delete all event listener ctrl history
	 */
	@GET
	@Path("/attachmentsHistoDeleteAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response attachmentHistoDeleteAll() {
		successFactorAttachFacade.deleteAllAttachmentHisto();

		ArrayList<EventListenerDocHistory> list = successFactorAttachFacade.getAllAttachmentHisto();
		return Response.status(200).entity(list).build();
	}

	/**
	 * 
	 * */
	@GET
	@Path("/attachments/last_executions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response attachmentGetLastExecutions() {
		ArrayList<AdminParameters> list = new ArrayList<>();

		AdminParameters paramAttachSearch = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_SEARCHATTACH.getCode());
		AdminParameters paramAttachSend = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_EVENTLISTATTACH.getCode());
		AdminParameters paramInitDateSearch = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_TIME_INITDATE_SEARCH_ATTACH.getCode());
		AdminParameters paramTimeInterval = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_TIME_INTERVAL_SEARCH_ATTCH.getCode());
		AdminParameters paramMaxRetries = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_ATTACH.getCode());

		if (paramAttachSearch != null) {
			paramAttachSearch.setDescription("Last Date Execution Job Search");
			list.add(paramAttachSearch);
		}

		if (paramAttachSend != null) {
			paramAttachSend.setDescription("Last Date Execution Job Send");
			list.add(paramAttachSend);
		}

		if (paramInitDateSearch != null) {
			paramInitDateSearch.setDescription("Initial Date of Search / Time Interval (minutes)");

			if (paramTimeInterval != null) {
				paramInitDateSearch.setValue(UtilDateTimeAdapter.getDateFormatFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), paramInitDateSearch.getValue()) + " / " + paramTimeInterval.getValue());
			}

			list.add(paramInitDateSearch);
		}

		if (paramMaxRetries != null) {
			paramMaxRetries.setDescription("Maximum Number of Retries");
			list.add(paramMaxRetries);
		} else {
			paramMaxRetries = new AdminParameters();
			paramMaxRetries.setDescription("maximum number of retries");
			paramMaxRetries.setValue("unlimited");
			list.add(paramMaxRetries);
		}

		return Response.status(200).entity(list).build();

	}

	/**
	 * get all event listener control attachment pending to process
	 * 
	 * @return ArrayList<EventListenerCtrlProcess> listReturn
	 */
	@GET
	@Path("/eventsCtrlAttach/process/{idCtrl}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerCtrlAttachGetAll(@PathParam("idCtrl") Long idCtrl) {
		// get Status to limit number of retries
		AdminParameters paramAdminRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STATUS_LIMIT_RETRIES.getCode());
		// get max number of retries
		AdminParameters paramAdminMaxRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_RETRIES.getCode());

		ArrayList<EventListenerDocProcess> list = succesFactorAdminFacade.eventsListenerDocProcGetAllByIdCtrl(idCtrl);

		if (paramAdminMaxRetriesCode != null && paramAdminRetriesCode != null) {
			for (EventListenerDocProcess item : list) {
				if (paramAdminRetriesCode != null && paramAdminRetriesCode.getValue() != null && item.getStatus() != null && paramAdminRetriesCode.getValue().contains(item.getStatus())) {
					item.setRetriesInfo(item.getRetries() + "/" + paramAdminMaxRetriesCode.getValue());
				} else {
					item.setRetriesInfo(item.getRetries() + "");
				}
			}
		}

		return Response.status(200).entity(list).build();
	}

	/**
	 * get all event listener control attachment history
	 * 
	 * @return ArrayList<EventListenerDocHistory> listReturn
	 */
	@GET
	@Path("/eventsCtrlHistoAttach/{idCtrlH}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerCtrlHistoAttachGetAll(@PathParam("idCtrlH") Long idCtrl) {
		ArrayList<EventListenerDocHistory> list = succesFactorAdminFacade.eventsListenerDocHistoGetAll(idCtrl,null);
		return Response.status(200).entity(list).build();
	}

	/**
	 * 
	 * */
	@GET
	@Path("/eventsCtrl/last_executions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerGetLastExecutions() {
		ArrayList<AdminParameters> list = new ArrayList<>();

		AdminParameters paramEventLis = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_EVENTLIST.getCode());
		AdminParameters paramEventLisAttach = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_EVENTLISTATTACH.getCode());

		if (paramEventLisAttach != null) {
			paramEventLisAttach.setDescription("Job Send Attachment");
			list.add(paramEventLisAttach);
		}

		if (paramEventLis != null) {
			paramEventLis.setDescription("Job Event Listener");
			list.add(paramEventLis);
		}

		return Response.status(200).entity(list).build();

	}
	
	
	/**
	 * Download file CSV for all events and sync
	 * @param Date
	 * @param String val
	 * @return Response
	 */
	@GET
	@Path("/eventsyncdownload/{date}/{email}")	
	public Response eventsSyncDownloadByEmail(@PathParam("date")String date,@PathParam("email")String email) 
	{
		String statusGroup = "";
		FilterQueryDto filter = new FilterQueryDto();
		filter.setPage(1+"");
		filter.setMaxResult(UtilCodesEnum.CODE_PARAM_ADM_MASSIVE_MAX_REG_PER_FILE.getCode());
		
		HashMap<String, String> map = new HashMap<>();
		map.put("value","");
		
		AdminParameters paramEmail = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_NOTIFICATIONS.getCode());
		
		if(paramEmail!=null && email!=null && date!=null)
		{
			String [] emails = paramEmail.getValue().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode());
			
			//load Info Email			
			for(String emailConf:emails)
			{
				if(emailConf!=null && !emailConf.equals(""))
				{
					if(emailConf.contains(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()))
					{
						//validate email
						if(emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[0].toString().equals(email)) 
						{
							statusGroup += emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1].toString()+",";
						}
					}
				}
			}
			
			
			filter.setStatus(UtilMapping.getListStatusForGroup(statusGroup));
			ResponseGenericDto response = succesFactorAdminFacade.downloadEventSyncLog(date,filter); 
			return Response.ok(response.getMessage()).header("Content-Disposition", "attachment; filename="+response.getField()+".csv" ).build();
		}		
						
		
		return Response.status(200).entity(map).build();
	}	

	/**
	 * get all event listener control pending to process
	 * 
	 * @return ArrayList<EventListenerCtrlProcess> listReturn
	 */
	@GET
	@Path("/eventsCtrl/process")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerCtrlProcGetAll() {
		// get Status to limit number of retries
		AdminParameters paramAdminRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STATUS_LIMIT_RETRIES.getCode());
		// get max number of retries
		AdminParameters paramAdminMaxRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_RETRIES.getCode());

		ArrayList<EventListenerCtrlProcess> listReturn = succesFactorAdminFacade.eventsListenerCtrlProcGetAll(new Date(), null, null, null, null, null, null);

		if (paramAdminMaxRetriesCode != null && paramAdminRetriesCode != null) {
			for (EventListenerCtrlProcess item : listReturn) {
				if (paramAdminRetriesCode != null && paramAdminRetriesCode.getValue() != null && item.getStatus() != null && paramAdminRetriesCode.getValue().contains(item.getStatus())) {
					item.setRetriesInfo(item.getRetries() + "/" + paramAdminMaxRetriesCode.getValue());
				} else {
					item.setRetriesInfo(item.getRetries() + "");
				}
			}
		}

		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * get all event listener control pending to process for time
	 * 
	 * @param String
	 *            time
	 * @return ArrayList<EventListenerCtrlProcess> listReturn
	 */
	@GET
	@Path("/eventsCtrl/processtime/{eventType}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerCtrlProcGetTime(@PathParam("eventType") String eventType) {
		// get Status to limit number of retries
		AdminParameters paramAdminRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STATUS_LIMIT_RETRIES.getCode());
		// get max number of retries
		AdminParameters paramAdminMaxRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_RETRIES.getCode());

		ArrayList<EventListenerCtrlProcess> listReturn = new ArrayList<>();
		if (eventType.equals("Current")) {
			listReturn = succesFactorAdminFacade.eventsListenerCtrlProcGetAll(new Date(), null, null, Boolean.FALSE, null, null, null);
		} else if (eventType.equals("Future")) {
			listReturn = succesFactorAdminFacade.eventsListenerCtrlProcGetAll(new Date(), null, null, Boolean.TRUE, null, null, null);
		}

		if (paramAdminMaxRetriesCode != null && paramAdminRetriesCode != null) {
			for (EventListenerCtrlProcess item : listReturn) 
			{
				item.setObservationsList(UtilMapping.loadPSFMsgDtoFromObservations(item.getObservations()));
				if (paramAdminRetriesCode != null && paramAdminRetriesCode.getValue() != null && item.getStatus() != null && paramAdminRetriesCode.getValue().contains(item.getStatus())) {
					item.setRetriesInfo(item.getRetries() + "/" + paramAdminMaxRetriesCode.getValue());
				} else {
					item.setRetriesInfo(item.getRetries() + "");
				}

			}
		}

		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * get all event listener control pending to process with future dates
	 * 
	 * @return ArrayList<EventListenerCtrlProcess> listReturn
	 */
	@GET
	@Path("/eventsCtrl/processnext")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerCtrlProcFutureDates() {
		// get Status to limit number of retries
		AdminParameters paramAdminRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STATUS_LIMIT_RETRIES.getCode());
		// get max number of retries
		AdminParameters paramAdminMaxRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_RETRIES.getCode());

		ArrayList<EventListenerCtrlProcess> listReturn = succesFactorAdminFacade.eventsListenerCtrlProcGetAll(new Date(), null, null, null, null, null, null);

		if (paramAdminMaxRetriesCode != null && paramAdminRetriesCode != null) {
			for (EventListenerCtrlProcess item : listReturn) {
				if (paramAdminRetriesCode != null && paramAdminRetriesCode.getValue() != null && item.getStatus() != null && paramAdminRetriesCode.getValue().contains(item.getStatus())) {
					item.setRetriesInfo(item.getRetries() + "/" + paramAdminMaxRetriesCode.getValue());
				} else {
					item.setRetriesInfo(item.getRetries() + "");
				}
			}
		}

		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * get all event listener control general
	 * @param String val
	 * @return ArrayList<EventListenerCtrlProcess> listReturn
	 */
	@POST
	@Path("/eventsCtrlHisto")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerCtrlHistoGetAll(String val) 
	{
		Integer maxReg = 100;
		AdminParameters paramAdminMaxRecord = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_REG_PER_PAGE.getCode());
		if (paramAdminMaxRecord != null && paramAdminMaxRecord.getValue() != null && !paramAdminMaxRecord.getValue().equals(""))
			maxReg = Integer.valueOf(paramAdminMaxRecord.getValue());

		FilterQueryDto filter = new FilterQueryDto();
		filter.setMaxResult(maxReg.toString());
		filter.setPage("1");
		
		if(val!=null&&!val.equals(""))
		{
			Gson gson = new Gson();
			filter = gson.fromJson(val,FilterQueryDto.class);
			
			if(!(filter.getPage()!=null && !filter.getPage().equals("")))
				filter.setPage("1");
			
			if(!(filter.getMaxResult()!=null && !filter.getMaxResult().equals("")))
				filter.setMaxResult(maxReg.toString());
		}		
		
		ArrayList<EventListenerCtrlHistory> listReturn = succesFactorAdminFacade.eventsListenerCtrlHistoGetAll(filter, null);
		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * delete all event listener ctrl history
	 */
	@GET
	@Path("/eventsCtrlHistoDelttAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventListenerCtrlDeleteAll() {
		succesFactorAdminFacade.eventsListenerCtrlHistoDelete(null);

		Integer maxReg = 100;
		AdminParameters paramAdminMaxRecord = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_REG_PER_PAGE.getCode());
		if (paramAdminMaxRecord != null && paramAdminMaxRecord.getValue() != null && !paramAdminMaxRecord.getValue().equals(""))
			maxReg = Integer.valueOf(paramAdminMaxRecord.getValue());

		FilterQueryDto filter = new FilterQueryDto();
		filter.setMaxResult(maxReg.toString());
		filter.setPage("1");
		ArrayList<EventListenerCtrlHistory> listReturn = succesFactorAdminFacade.eventsListenerCtrlHistoGetAll(filter, null);
		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * delete event listener ctrl history
	 */
	@POST
	@Path("/eventsCtrlHistoDelete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventListenerCtrlDelete(@PathParam("id") Long id) {
		EventListenerCtrlHistory history = new EventListenerCtrlHistory();
		history.setId(id);
		succesFactorAdminFacade.eventsListenerCtrlHistoDelete(history);

		Integer maxReg = 100;
		AdminParameters paramAdminMaxRecord = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_REG_PER_PAGE.getCode());
		if (paramAdminMaxRecord != null && paramAdminMaxRecord.getValue() != null && !paramAdminMaxRecord.getValue().equals(""))
			maxReg = Integer.valueOf(paramAdminMaxRecord.getValue());

		FilterQueryDto filter = new FilterQueryDto();
		filter.setMaxResult(maxReg.toString());
		filter.setPage("1");
		ArrayList<EventListenerCtrlHistory> listReturn = succesFactorAdminFacade.eventsListenerCtrlHistoGetAll(filter, null);
		return Response.status(200).entity(listReturn).build();
	}
	
	/**
	 * process again event listener
	 * @param String val
	 * */
	@POST
	@Path("/eventsCtrlHistoProcessAgain/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventListenerProcessAgain(@PathParam("type") String type,String val) 
	{
		ArrayList<ResponseGenericDto> responseList = new ArrayList<>();
		SuccessFactorEventLFacade eventLFacade = new SuccessFactorEventLFacade();		
		JSONArray jArray = new JSONArray(val);
		
		for (int i = 0; i < jArray.length(); i++) 
		{
			JSONObject obj = new JSONObject(jArray.get(i).toString());			
			if(obj.getBoolean("check")) 
			{
				Long id = obj.getLong("id");
				
				if(type.equals(UtilCodesEnum.CODE_TYPE_SYNC.getCode())) 
				{
					responseList.add(succesFactorAdminFacade.eventListenerDocProcessInsertFromHisto(id));					
				}
				else
				{
					ResponseGenericDto response = new ResponseGenericDto();
					EventListenerCtrlProcess  event = eventLFacade.eventListenerCtrlCreateFromHisto(id);
					if(event!=null){						
						response.setCode(UtilCodesEnum.CODE_HTTP_200.getCode());
						response.setField(UtilCodesEnum.CODE_HTTP_200.getLabel());
						response.setMessage("New Id: "+event.getId()+", From Id History: "+id);
						responseList.add(response);						
					}
					else
					{
						response.setCode(UtilCodesEnum.CODE_HTTP_1502.getCode());
						response.setField(UtilCodesEnum.CODE_HTTP_1502.getLabel());
						response.setMessage("Id Histo: "+id);
						responseList.add(response);
					}
				}
			}
		}
		
		return Response.status(UtilCodesEnum.CODE_HTTP_200.getCodeInt()).entity(responseList).build();			
	}
	

	/**
	 * update status event listener ctrl status
	 */
	@POST
	@Path("/eventsCtrl/uploadCtrlEvent/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventListenerCtrlUpdateStatus(@PathParam("id") Long id, String val) {
		Gson token = new Gson();
		SlugItem infoUpdate = token.fromJson(val, SlugItem.class);
		EventListenerCtrlProcess eventtoUpdate = succesFactorAdminFacade.eventsListenerCtrlProcGetByid(id);

		if (eventtoUpdate != null && infoUpdate != null) {
			Date date = new Date();
			String dateSeconds = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);

			eventtoUpdate.setStatus(infoUpdate.getValue().toString());
			eventtoUpdate.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + infoUpdate.getLabel() + UtilCodesEnum.CODE_STRING_END.getCode() + eventtoUpdate.getObservations());
			eventtoUpdate.setLastUpdateOn(new Date());
			eventtoUpdate.setIdJobProcess(null);// reset id job process
			eventtoUpdate = succesFactorAdminFacade.eventListenerCtrlProcessUpdate(eventtoUpdate);

			if (eventtoUpdate != null) {
				if (infoUpdate.getFlag() || eventtoUpdate.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode())) {
					succesFactorAdminFacade.eventListenerUpdateAllDocProc(eventtoUpdate.getId(), infoUpdate.getValue().toString(), infoUpdate.getLabel());
				}

				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(eventtoUpdate).build();
			} else {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
			}
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();

		}
	}

	/**
	 * update status event listener ctrl document status
	 */
	@POST
	@Path("/eventsCtrl/uploadCtrlDocEvent/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventListenerCtrlDocUpdateStatus(@PathParam("id") Long id, String val) {
		Gson token = new Gson();
		SlugItem infoUpdate = token.fromJson(val, SlugItem.class);
		EventListenerDocProcess eventDoctoUpdate = succesFactorAdminFacade.eventsListenerDocProcGetByid(id);

		if (eventDoctoUpdate != null && infoUpdate != null) {
			Date date = new Date();
			String dateSeconds = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);

			eventDoctoUpdate.setStatus(infoUpdate.getValue().toString());
			eventDoctoUpdate.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + infoUpdate.getLabel() + UtilCodesEnum.CODE_STRING_END.getCode() + eventDoctoUpdate.getObservations());
			eventDoctoUpdate.setLastUpdateOn(new Date());
			eventDoctoUpdate = succesFactorAdminFacade.eventListenerDocProcessUpdate(eventDoctoUpdate);

			if (eventDoctoUpdate != null) {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(eventDoctoUpdate).build();
			} else {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
			}
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();

		}
	}

	/**
	 * update status attachment
	 */
	@POST
	@Path("/attachment/uploadAttachment/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response attachmentUpdateStatus(@PathParam("id") Long id, String val) {
		Gson token = new Gson();
		SlugItem infoUpdate = token.fromJson(val, SlugItem.class);
		EventListenerDocProcess eventDoctoUpdate = succesFactorAdminFacade.eventsListenerDocProcGetByid(id);

		if (eventDoctoUpdate != null && infoUpdate != null) {
			Date date = new Date();
			String dateSeconds = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);

			eventDoctoUpdate.setStatus(infoUpdate.getValue().toString());
			eventDoctoUpdate.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + infoUpdate.getLabel() + UtilCodesEnum.CODE_STRING_END.getCode() + eventDoctoUpdate.getObservations());
			eventDoctoUpdate.setLastUpdateOn(new Date());
			eventDoctoUpdate = succesFactorAdminFacade.eventListenerDocProcessUpdate(eventDoctoUpdate);

			if (eventDoctoUpdate != null) {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(eventDoctoUpdate).build();
			} else {
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
			}
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();

		}
	}

	// --------------------------------------------------------------------------
	// methods data base

	@GET
	@Path("/initBD")
	@Produces(MediaType.APPLICATION_JSON)
	public Response installBD() {
		DataBaseBuilder.getInstance().reset();
		DataBaseBuilder.getInstance().init();

		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("status", "initializated");

		return Response.status(200).entity(map).build();
	}

	@GET
	@Path("/initData")
	@Produces(MediaType.APPLICATION_JSON)
	public Response installBDContent() {
		DataBaseBuilder.getInstance().initData();

		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("status", "initializated");

		return Response.status(200).entity(map).build();
	}

	@GET
	@Path("/resetBD")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetBD() {

		DataBaseBuilder.getInstance().reset();

		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("status", "reseted");

		return Response.status(200).entity(map).build();
	}

	@GET
	@Path("/getBD")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readBD() {
		SfEntityDAO list2 = new SfEntityDAO();
		List<SfEntity> li2 = list2.getAll();
		return Response.status(200).entity(li2).build();

	}

	@GET
	@Path("/getBD/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("id") String id) {
		SfEntityDAO list2 = new SfEntityDAO();
		SfEntity li2 = list2.getByEntityName(id);
		return Response.status(200).entity(li2).build();
	}

	@GET
	@Path("/logger")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logger() {
		LoggerDAO list2 = new LoggerDAO();
		List<LoggerAction> li2 = list2.getAllRevert(null);
		return Response.status(200).entity(li2).build();

	}

	@GET
	@Path("/uiConfig")
	@Produces(MediaType.APPLICATION_JSON)
	public Response config() {
		final UIConfig config = new UIConfig();
		
		config.userid  = UserManager.getUserId();
		
		if (UserManager.getIsUserAdmin()) {
			config.initAdminConfiguration();
		}
		if (UserManager.getIsUserSuperAdmin()) {
			config.initSuperAdminConfiguration();
		}
		if (!UserManager.getIsUserAdmin() && !UserManager.getIsUserSuperAdmin()) {
			config.initEmployeeConfiguration();
		}

		EmployeeDAO userDAO = getUserDAO();
		Employee userlogged = userDAO.getByUserId(UserManager.getUserId());

		config.setCountHr(Integer.valueOf((userlogged != null && userlogged.getCountHr() != null ? userlogged.getCountHr() : "0")));
		config.setCountMng(Integer.valueOf((userlogged != null && userlogged.getCountMng() != null ? userlogged.getCountMng() : "0")));

		SFGroupList listGroups = succesFactorFacade.userGroupList(UserManager.getUserId());

		AdminParameters paramRecCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_RECRUITING_VISIBILITY.getCode());
		AdminParameters paramTmpCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_TEMPLATES_VISIBILITY.getCode());
		AdminParameters paramAllCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_VIEWALL_VISIBILITY.getCode());
		AdminParameters paramTeamCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_TEAM_VISIBILITY.getCode());
		AdminParameters paramAdminCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADMIN_VISIBILITY.getCode());

		String[] paramRecCodeValues = null;
		if (paramRecCode != null) {
			paramRecCodeValues = paramRecCode.getValue().split(",");
		}

		String[] paramTeamCodeValues = null;
		if (paramTeamCode != null) {
			paramTeamCodeValues = paramTeamCode.getValue().split(",");
		}

		String[] paramTmpCodeValues = null;
		if (paramTmpCode != null) {
			paramTmpCodeValues = paramTmpCode.getValue().split(",");
		}

		String[] paramAllCodeValues = null;
		if (paramAllCode != null) {
			paramAllCodeValues = paramAllCode.getValue().split(",");
		}

		String[] paramAdminCodeValues = null;
		if (paramAdminCode != null) {
			paramAdminCodeValues = paramAdminCode.getValue().split(",");
		}

		if (succesFactorFacade.hasKeyGetValue(UtilCodesEnum.CODE_CONNECT_KEY_ARAGO.getCode()).toString()
				.equals(UtilCodesEnum.CODE_CONNECT_VALUE_ARAGO.getCode())) {
			config.setIsHasRightToSee(true);
			config.setHasTemplates(true);

			if (config.getCountHr() > 0 || config.getCountMng() > 0) {
				config.setSimpleUser(false);
				config.setMngHr(true);
				config.setSeesPerso(true);
				config.setSeesMngHr(true);
			} else {
				config.setIsHasRightToSee(true);
				config.setSeesPerso(true);
				config.setSeesMngHr(false);
			}

			if (listGroups != null) {
				if (listGroups.getD().length > 0) {
					for (SFGroup string : listGroups.getD()) {
						if (paramAdminCodeValues != null && Arrays.asList(paramAdminCodeValues).contains(string.getGroupName())) {
							config.initSuperAdminConfiguration();
						}
					}
				}
			}
			if (listGroups != null) {
				if (listGroups.getD().length > 0) {
					for (SFGroup string : listGroups.getD()) {
						if (paramRecCodeValues != null && Arrays.asList(paramRecCodeValues).contains(string.getGroupName())) {
							config.setSeesRecruiting(true);
						}
					}
				}

				if (listGroups.getD().length > 0) {
					for (SFGroup string : listGroups.getD()) {
						if (paramTeamCodeValues != null && Arrays.asList(paramTeamCodeValues).contains(string.getGroupName())) {
							config.setSeesTeamTemplate(true);
						}
					}
				}

				if (listGroups.getD().length > 0) {
					for (SFGroup string : listGroups.getD()) {
						if (paramTmpCodeValues != null && Arrays.asList(paramTmpCodeValues).contains(string.getGroupName())) {
							config.setSeesTemplates(true);
						}
					}

				}

				if (listGroups.getD().length > 0) {
					for (SFGroup string : listGroups.getD()) {
						if (paramAllCodeValues != null && Arrays.asList(paramAllCodeValues).contains(string.getGroupName())) {
							config.setSeesAllPop(true);
							config.setSeesMngHr(true);
						}
					}

				}
			}
		} else {
			config.setHasTemplates(false);
			config.setSeesAllPop(false);
			config.setSeesPerso(true);
			config.setSeesTemplates(false);
			config.setSeesRecruiting(false);
			config.setSimpleUser(true);
			// temporary added if need to be tested
			// config.setSeesTeamNoTemplate(true);

			if (listGroups != null) {
				if (listGroups != null) {
					if (listGroups.getD() != null && listGroups.getD().length > 0) {
						for (SFGroup string : listGroups.getD()) {
							if (paramAdminCodeValues != null && Arrays.asList(paramAdminCodeValues).contains(string.getGroupName())) {
								config.initSuperAdminConfiguration();
							}
						}
					}
				}

				if (listGroups.getD() != null && listGroups.getD().length > 0) {
					for (SFGroup string : listGroups.getD()) {
						if (paramTeamCodeValues != null && Arrays.asList(paramTeamCodeValues).contains(string.getGroupName())) {
							config.setSeesTeamNoTemplate(true);
						}
					}
				}

				if (listGroups.getD() != null && listGroups.getD().length > 0) {
					for (SFGroup string : listGroups.getD()) {
						if (paramAllCodeValues != null && Arrays.asList(paramAllCodeValues).contains(string.getGroupName())) {
							config.setSeesAllPop(true);
							config.setSeesMngHr(true);
							config.setSeesTeamNoTemplate(true);
						}
					}

				}
			} else {
				config.setSeesPerso(false);
				config.setSeesAllPop(true);
			}
		}

		if (userlogged != null && userlogged.getDefaultLanguage() != null && config != null && config.getDefaultLanguage() != null) {
			SFUserDto sfUser = succesFactorFacade.userGetProfile(userlogged.getUserId(), "9999-01-01");
			if (sfUser != null) {
				config.setDefaultLanguage(sfUser.callDefLG());
			} else {

				config.setDefaultLanguage(userlogged.getDefaultLanguage());
			}
		}
		try {
			config.setUrl(PpdHttpConnectorV1.getInstance().getUrl());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(200).entity(config).build();

	}

	private EmployeeDAO getUserDAO() {
		return new EmployeeDAO();
	}

	// --------------------------------------------------------
	// Methods parameters admin

	/**
	 * 
	 * Get info Admin Parameters list
	 * 
	 * @return ArrayList<SlugItem>
	 */
	public ArrayList<SlugItem> admParameterGetInfoList() {
		ArrayList<SlugItem> listArrayReturn = new ArrayList<SlugItem>();

		// structure
		SlugItem item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_STRUCTURE_KEY.getCode());
		item.setLabel("levels of the organizational structure");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_STRUCTURE.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_CUSTOM_ADMIN_FIELDS.getCode());
		item.setLabel("Custom fields from Manager View");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_STRUCTURE.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_UPDATE_ORGA.getCode());
		item.setLabel(" Administration parameters update organization on User Update");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_STRUCTURE.getCode());
		listArrayReturn.add(item);

		// Event listener
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_MAX_RETRIES.getCode());
		item.setLabel("maximum number of retries in failure states");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_STATUS_LIMIT_RETRIES.getCode());
		item.setLabel("Failure status of event listeners where the retry limit applies ");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_EVENT.getCode());
		item.setLabel("Maximum number of records to process per cycle");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_WAITIME.getCode());
		item.setLabel("maximum waiting time to reprocess an event in a timeout state or one that remains in processing, enter value in minutes.");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_EVENTLIST.getCode());
		item.setLabel("data of the last execution of the EventListener/User job");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_EVENTLISTATTACH.getCode());
		item.setLabel("data of the last execution of the EventListener/Attach job");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_WAITTIME_USERNOMAIL.getCode());
		item.setLabel("Wait time for mail generation to user without mail, enter value in minutes. ");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_PROCESS_USERNOMAIL.getCode());
		item.setLabel("process user without email, the email of the structure will be consulted in case it does not exist, (enter value true or false)");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_FORMAT_MAILUSERTERM_PPD.getCode());
		item.setLabel("Email format for user update with eventListener terminate, with this email the user will be updated in ppd. " + "the pattern " + UtilCodesEnum.CODE_PATRON_MAILUDPATE.getCode()
				+ " it will be updated with the employee's userid," + "example: ??noemail@company.com ");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_CALL_UPDATEUSER_AFTER_CREATE.getCode());
		item.setLabel("Perform update apiv1 after the creation of a userCore apiv2 in ppd, by default the update apv1 call is made, (enter value true or false)");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_MANAGER_ROLE_PPD.getCode());
		item.setLabel("role of manager defined in ppd, format:  manager name Field Ppd@@manager name Role PPd");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_MANAGER_ORGANIZATION_PPD.getCode());
		item.setLabel("organization to update profiles of a user in ppd");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);			
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_PREFIX_USER_CREATE_PPD.getCode());
		item.setLabel("prefix to create a user in ppd, this element will be put before the user's identifier");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);			
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_NOTIFICATIONS.getCode());
		item.setLabel("list of emails to send a summary of eventlistener, sync and massives load.\n" + 
					  "\n" + 
					  "format: email1 @@ type of notification; email2 @@ type of notification\n" + 
					  "\n" + 
					  "Types of Notification: All, Errors, ErrorPpd, Pending, Successful\n" + 
					  "\n" + 
					  "example: user_xyz @ email.com @@ Errors; user_abc_ @ email.com @@ All");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_NOTIFICATIONS_HOUR.getCode());
		item.setLabel("generation time and send to email with statistics of events, default time 23:50, format: HH:MM example 18:05 ");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_INIT_HOUR.getCode());
		item.setLabel("initial hour of event search. format HH:mm, by default current date 00:01");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);		
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_END_HOUR.getCode());
		item.setLabel("end hour of event search. format HH:mm, by default current date 23:59");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_ADD_DAY_TIME_PROCESS_AGAIN.getCode());
		item.setLabel("add days to the effective date of a finished event to be processed again in the future");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_EVENTLIST.getCode());
		listArrayReturn.add(item);
		
		// Attachment job

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_ATTACH.getCode());
		item.setLabel("Maximum number of records to process per cycle");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ATTACH_JOB.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_MAX_ATTACH.getCode());
		item.setLabel("maximum number of retries in failure states");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ATTACH_JOB.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_TIME_INTERVAL_SEARCH_ATTCH.getCode());
		item.setLabel("Time interval in minutes, for search of changes of user attachments, enter a positive value.");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ATTACH_JOB.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_TIME_INITDATE_SEARCH_ATTACH_ADD.getCode());
		item.setLabel("Number of minutes to add to the initial date of attachment search");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ATTACH_JOB.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_TIME_INITDATE_SEARCH_ATTACH.getCode());
		item.setLabel("search start date, only enter in case of attachments searches with past dates. " + "if this parameter is empty, the attachments job will search FROM: the current date minus "
				+ "the interval defined in the time_interval_search_attach parameter, TO: the current date. " + "enter value in date format: " + UtilCodesEnum.CODE_FORMAT_DATE.getCode());

		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ATTACH_JOB.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_SEARCHATTACH.getCode());
		item.setLabel("data of the last execution of the search jod of files modified by user");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ATTACH_JOB.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_PATTERN_FILENAME_ONBV2.getCode());
		item.setLabel("structure for the file name in ONBv2. string separated by semicolons (;), example: [0]=numbering separator; [1]=separator; [2]= position of document Type after the split; [3] = begin position of namefile in all string. default value: 2;.;0;2 for file name: 1_XYZ_A.pdf");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ATTACH_JOB.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_PATTERN_FILENAME_SEND_PPD.getCode());
		item.setLabel("File name format to be sent to ppd, example 1: FN@@DT@@PI, example 2: FN  [FN]=File Name, [DT]=Document Type, [PI]=Person ID");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ATTACH_JOB.getCode());
		listArrayReturn.add(item);		

		// Signatures
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_TIME_INTERVAL_REG_JOBSIGN.getCode());
		item.setLabel("Time interval in search of changes of signature status, enter the value in minutes.");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_SIGN.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_REG_JOBSIGN.getCode());
		item.setLabel("maximum number of records to be processed in a job cycle");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_SIGN.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_SIGNTECHNO.getCode());
		item.setLabel("Signature Technology used in PeopleDoc / Docusign");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_SIGN.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_SIGNTECHNO_OPENTRUST.getCode());
		item.setLabel("Signature Technology used in PeopleDoc / Opentrust");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_SIGN.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_SIGNEXPI.getCode());
		item.setLabel("Expiration date of the signature. Beyond this date, it will be impossible for all the remaining signatories to sign the document, enter the value in days from the generation date.");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_SIGN.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_SIGNCALLBACK_URL.getCode());
		item.setLabel("call of return or notification from peopledoc for the management of signatures and their states");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_SIGN.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_SIGNAUTO_EVENTLIST.getCode());
		item.setLabel("At the time of processing an eventlistener, automatically send document to sign to Ppd in case the document has signatures, enter true o false");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_SIGN.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_AUTO_SENDDOC_SIGNALL_PPD.getCode());
		item.setLabel("PeopleDoc sends the document automatically when they are all in the signatures in the document / WS send sign, enter true o false");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_SIGN.getCode());
		listArrayReturn.add(item);

		// visibility
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_RECRUITING_VISIBILITY.getCode());
		item.setLabel("visibility recruiting module");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ADM_JOB.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_TEMPLATES_VISIBILITY.getCode());
		item.setLabel("visibility templates module");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ADM_JOB.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_VIEWALL_VISIBILITY.getCode());
		item.setLabel("visibility menus");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ADM_JOB.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_TEAM_VISIBILITY.getCode());
		item.setLabel("visibility Teams");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ADM_JOB.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_MAX_DAYS_STORE_LOGG_RECORD.getCode());
		item.setLabel("maximum time to store a logger record. enter the value in minutes.");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ADM_JOB.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_OPTIONS_VERSION_SIGNATURE.getCode());
		item.setLabel("options for the signature version of a document, the options are separated by the semicolon character, format: "
				+ "label:key:signature_field_generation(true or false) ; label:key:signature_field_generation (true or false). " + "example: Sign v1:sv1:false;Sign v2:sv2:true");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ADM_JOB.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_MAX_TIME_DOC_AUTO_ARCHIVE.getCode());
		item.setLabel("maximum number of days after the creation of a document to send the document to the archive");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ADM_JOB.getCode());
		listArrayReturn.add(item);		

		// massive load
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_MASS_EMPL.getCode());
		item.setLabel("Maximum number of employees to process per cycle");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_MASSLOAD.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_MASSIVE_DOWNLOAD_URL.getCode());
		item.setLabel("url to download csv log for massive,event and sync, path: /generator/rst/json/admin/massiveuserdownload");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_MASSLOAD.getCode());
		listArrayReturn.add(item);
		
		// front
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_ADM_REG_PER_PAGE.getCode());
		item.setLabel("Number of records per page (pager)");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_FRONT.getCode());
		listArrayReturn.add(item);
		
		//logs
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_PARAM_LOG_SHOW_RESPONSE_PPD_EMPL.getCode());
		item.setLabel("Load the PPD response when creating an employee. values: true or false, by default false");
		item.setPath(UtilCodesEnum.CODE_PARAM_GRP_ADM_LOG.getCode());
		listArrayReturn.add(item);

		return listArrayReturn;
	}

	/**
	 * 
	 * Get info Admin Parameters
	 * 
	 * @return StructureBusiness AdminStructure
	 */
	@GET
	@Path("/infoadminparameters")
	@Produces(MediaType.APPLICATION_JSON)
	public Response admParameterGetInfoListWeb() {
		return Response.status(200).entity(this.admParameterGetInfoList()).build();
	}
	
	@GET
	@Path("/admParameterByName/{nameAdmParam}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response admParamaterGetAll(@PathParam("nameAdmParam") String nameAdmParam) 
	{
		AdminParameters admParam = succesFactorAdminFacade.adminParamGetByNameCode(nameAdmParam);
		
		if(admParam!=null)
			return Response.status(200).entity(admParam).build();
		else
			return Response.status(500).entity(null).build();
	}

	@GET
	@Path("/admParameter")
	@Produces(MediaType.APPLICATION_JSON)
	public Response admParamaterGetAll() {
		List<AdminParameters> listReturn = succesFactorAdminFacade.admParameterGetAll(false);
		ArrayList<SlugItem> listInfoAdmParam = this.admParameterGetInfoList();

		if (listReturn != null) {
			for (AdminParameters item : listReturn) {

				for (SlugItem itemInfo : listInfoAdmParam) {
					if (item.getNameCode().equals(itemInfo.getId())) {
						item.setDescription(itemInfo.getLabel());
						item.setGroup(itemInfo.getPath());

					}
				}

				if (item.getIsControlPanel() == null) {
					// logger.info("***_____________***");
				} else {
					if (item.getIsControlPanel() == true) {
						listReturn.remove(item);
					}
				}

			}

			for (SlugItem itemInfo : listInfoAdmParam) {
				int index = 0;
				for (AdminParameters item : listReturn) {
					if (item.getNameCode().equals(itemInfo.getId())) {
						index++;
					}
				}
				if (index == 0) {
					AdminParameters adm = new AdminParameters();
					adm.setGroup(UtilCodesEnum.CODE_PARAM_GRP_ADM_NOT_CONFIGURE.getCode());
					adm.setDescription(itemInfo.getLabel());
					adm.setNameCode(itemInfo.getId());
					adm.setValue(UtilCodesEnum.CODE_NA.getCode());
					listReturn.add(adm);
				}
			}
		}

		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * update admin Parameter
	 */
	@POST
	@Path("/admParameter/upload/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response admParameterUpdate(@PathParam("id") Long id, String val) {
		Gson token = new Gson();
		AdminParameters field = token.fromJson(val, AdminParameters.class);
		field.setLastUpdateDate(new Date());
		field = succesFactorAdminFacade.admParameterUpdate(field);

		if (field != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(field).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * delete admin Parameter
	 * 
	 * @param Long
	 *            id
	 * @return ArrayList<AdminParameters>
	 */
	/**
	 * delete admin Parameter
	 * 
	 * @param Long
	 *            id
	 * @return ArrayList<AdminParameters>
	 */
	@DELETE
	@Path("/admParameter/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response admParameterDelete(@PathParam("id") Long id) {
		if (succesFactorAdminFacade.admParameterDelete(id)) {
			List<AdminParameters> listReturn = succesFactorAdminFacade.admParameterGetAll();
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * Insert / Update admin Parameter
	 */
	@POST
	@Path("/admParameter/insert_upload/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response admParameterInsertUpdate(String val) {
		try {
			JSONObject json = new JSONObject(val);
			String idUser = UserManager.getUserId();
			Gson gson = new Gson();
			AdminParameters field = gson.fromJson(val, AdminParameters.class);
			field.setLastUpdateDate(new Date());
			field.setLastUpdateUser(idUser);
			if (json.get("id") == null) {
				field = succesFactorAdminFacade.admParameterInsert(field);
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(field).build();
			} else {
				field = succesFactorAdminFacade.admParameterUpdate(field);
				return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(field).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	// --------------------------------------------------------
	// Methods Roles SF to PeopleDoc

	/**
	 * insert event listener
	 * 
	 * @param @PathParam("idParent")Long
	 *            idParent,
	 * @param String
	 *            val StructureBusiness.class
	 * @return StructureBusiness event
	 */
	@GET
	@Path("/mappingRolePpd")
	@Produces(MediaType.APPLICATION_JSON)
	public Response roleview() {

		List<RolesMappingPpd> listReturn = succesFactorAdminFacade.roleGetAll();
		return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();

	}

	/**
	 * insert event listener
	 * 
	 * @param @PathParam("idParent")Long
	 *            idParent,
	 * @param String
	 *            val StructureBusiness.class
	 * @return StructureBusiness event
	 */
	@GET
	@Path("/adminsfs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response rolebyuser() {

		List<SFAdmin> listReturn = succesFactorAdminFacade.roleGetUsers();
		return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();

	}

	/**
	 * insert event listener
	 * 
	 * @param @PathParam("idParent")
	 *            Long idParent,
	 * @param String
	 *            val StructureBusiness.class
	 * @return StructureBusiness event
	 */
	@POST
	@Path("/mappingRolePpd/insert/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response roleInsert(String val) {
		logger.info("insertRoleTemplate " + val);

		Gson token = new Gson();
		RolesMappingPpd role = token.fromJson(val, RolesMappingPpd.class);
		role = succesFactorAdminFacade.roleInsert(role);

		if (role != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(role).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}

	}

	/**
	 * update admin Parameter
	 */
	@POST
	@Path("/mappingRolePpd/update/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response roleUpdate(@PathParam("id") Long id, String val) {
		Gson token = new Gson();
		RolesMappingPpd role = token.fromJson(val, RolesMappingPpd.class);
		role = succesFactorAdminFacade.roleUpdate(role);

		if (role != null) {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(role).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * delete admin Parameter
	 * 
	 * @param Long
	 *            id
	 * @return ArrayList<AdminParameters>
	 */
	@DELETE
	@Path("/mappingRolePpd/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response roleDelete(@PathParam("id") Long id) {
		if (succesFactorAdminFacade.roleDelete(id)) {
			List<RolesMappingPpd> listReturn = succesFactorAdminFacade.roleGetAll();
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();
		} else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}

	/**
	 * get all objects for sfEntity
	 * 
	 * @param @PathParam("idParent")Long
	 *            idParent,
	 * @param String
	 *            val StructureBusiness.class
	 * @return StructureBusiness event
	 */
	@GET
	@Path("/mapObjects")
	@Produces(MediaType.APPLICATION_JSON)
	public Response entityGetAll() {

		List<SfEntity> listReturn = succesFactorAdminFacade.entityGetAll();

		// List<SFAdmin> listReturn = succesFactorAdminFacade.roleGetUsers();
		return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();

	}

	/**
	 * get all objects for sfEntity
	 * 
	 * @param @PathParam("idParent")Long
	 *            idParent,
	 * @param String
	 *            val StructureBusiness.class
	 * @return StructureBusiness event
	 */
	@GET
	@Path("/mapObjects/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response entityGetOne(@PathParam("id") String id) {
		logger.info(id);

		String listReturn = succesFactorAdminFacade.entityGetOne(id);

		// List<SFAdmin> listReturn = succesFactorAdminFacade.roleGetUsers();
		return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();

	}

	/**
	 * delete all data
	 * 
	 * @param @PathParam("idTables") String idTables
	 * @param @PathParam("params") String params
	 * @return StructureBusiness Event
	 */
	@GET
	@Path("/controlPanelDelAll/{idTables}/{params}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response controlPanelDeleteAll(@PathParam("idTables") String idTables,@PathParam("params") String params) 
	{
		GenResponseInfoDto response = new GenResponseInfoDto();
		response.setMessage(UtilCodesEnum.CODE_SUCCESS_200.getCode());
		
		try 
		{
			if (UtilCodesEnum.CODE_ENTITY_EMPLOYEE_SYNC.getCode().equals(idTables)) {
				succesFactorAdminFacade.deleteAllEmployeeSync();
			}
			else if (UtilCodesEnum.CODE_ENTITY_LOGGERS.getCode().equals(idTables)) {
				succesFactorAdminFacade.controlPanelDeleteAllByTable("ErrorLog");
			}
			else if (UtilCodesEnum.CODE_ENTITY_FIELDSMAPPINGPPD.getCode().equals(idTables)) 
			{
				//update all event listener documento to null in fields of fieldMapping
				succesFactorAdminFacade.eventListenerDocProUpdateFieldMapToNull(null);
				succesFactorAdminFacade.controlPanelDeleteAllByTable("FieldsMappingPpd");
			}			
			else if (UtilCodesEnum.CODE_ENTITY_MASSIVE_LOAD.getCode().equals(idTables))
			{
				if(params!=null && !params.equals(""))
				{
					try 
					{
						if(succesFactorAdminFacade.eventListenerDocDeleteAllWithIdMassiveLoad(new Long(params)))
						//if(succesFactorAdminFacade.eventListenerCtrlDeleteAllWithIdMassiveLoad(new Long(params)))
						{
							if(succesFactorAdminFacade.eventListenerCtrlDeleteAllWithIdMassiveLoad(new Long(params)))
							{
								SuccessFactorMassiveLoadFacade SFMassiveLoadFacade = new SuccessFactorMassiveLoadFacade();
								
								MassiveLoadUser entityMassiveLoadUser = SFMassiveLoadFacade.userLoadGetById(new Long(params));
								if(entityMassiveLoadUser!=null) {
									SFMassiveLoadFacade.massiveLoadDelete(entityMassiveLoadUser);
								
									response.setCode(UtilCodesEnum.CODE_HTTP_200.getCode());
									response.setMessage(UtilCodesEnum.CODE_HTTP_200.getLabel());
								}
								else
								{
									response.setCode(UtilCodesEnum.CODE_ERROR_1001.getCode());
									response.setMessage("Error Massive Load does not Exist");
								}
							}
							else
							{
								response.setCode(UtilCodesEnum.CODE_ERROR_1001.getCode());
								response.setMessage("Error delete EventListeners");
							}
						}
						else
						{
							response.setCode(UtilCodesEnum.CODE_ERROR_1001.getCode());
							response.setMessage("Error delete Attachments");
						}
						
					}catch (Exception e) {
						new ThrowsException(e);
					}
				}
			}
		} catch (Exception e) 
		{
			response.setCode(UtilCodesEnum.CODE_ERROR_1001.getCode());
			response.setMessage(e.getMessage());
			e.printStackTrace();
		}

		return Response.status(200).entity(response).build();
	}
	
	
	/**
	 * send notification email 
	 * @param @PathParam("idEvent") String idEvent
	 */
	@GET
	@Path("/sendNotificationEventEmail/{idEvent}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendNotificationEventEmail(@PathParam("idEvent") String idEvent) {
		try 
		{
			SuccessFactorEventLFacade Fevent = new SuccessFactorEventLFacade();
			if (UtilCodesEnum.CODE_TYPE_SYNC.getCode().equals(idEvent)) 
			{
				
				Fevent.sendEmailResumeEvent(false);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(200).entity("").build();
	}
	
	

	/**
	 * generated a eventlister 
	 * @param String val
	 * @return Response
	 */
	@POST
	@Path("generateEventList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response controlPanelEventListGenerated(String val) {
		JSONObject json = new JSONObject(val);

		if (json.getString("eventId") != null && !json.getString("eventId").trim().equals("") && json.getString("userIdEventValue") != null && !json.getString("userIdEventValue").trim().equals("") && json.getString("dateEffEvent") != null
				&& !json.getString("dateEffEvent").trim().equals("") && json.getString("userSeq") != null && !json.getString("userSeq").trim().equals("")) {
			EenUser user = new EenUser();
			EenAlertRequestMeta externalEvent = new EenAlertRequestMeta();

			externalEvent.type = json.getString("eventId");
			user.setPersonIdExternal(json.getString("userIdEventValue"));
			user.setUserId(json.getString("userIdEventValue"));
			user.setStartDate(json.getString("dateEffEvent"));
			user.setSeqNumber(json.getString("userSeq"));
			EventListenerCtrlProcess result = succesFactorWebServFacade.eventListenerActionProcessWebCall(user, externalEvent);

			Map<String, String> resp = new HashMap<String, String>();

			if (result.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode())) {
				resp.put("response", UtilCodesEnum.CODE_ERROR_500.getCode() + " " + result.getObservations());
				return Response.status(200).entity(resp).build();

			} else {
				resp.put("response", "EventListenerCtrl Id " + result.getId());
				return Response.status(200).entity(resp).build();
			}
		}

		return Response.status(200).entity("OK").build();
	}
	
	/**
	 * generated a eventlister Attachment
	 * @param String val
	 * @return Response
	 */
	@POST
	@Path("generateEventAttach")
	@Produces(MediaType.APPLICATION_JSON)
	public Response controlPanelEventListAttachGenerated(String val) {
		JSONObject json = new JSONObject(val);

		if (json.getString("dateInit") != null && !json.getString("dateInit").trim().equals("") 
				&& json.getString("dateEnd") != null && !json.getString("dateEnd").trim().equals(""))
		{
			
			ResponseGenericDto response =  successFactorAttachFacade.actionProcessAttachSearchQuartz(
					json.getString("userIdAttachValue")!=null?json.getString("userIdAttachValue"):null,
					UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(), json.getString("dateInit") ),
					UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(), json.getString("dateEnd") ),
					null);
			
			Map<String, String> resp = new HashMap<String, String>();
			resp.put("response",response.getMessage());
			return Response.status(200).entity(resp).build();

		}

		return Response.status(200).entity("OK").build();
	}	

	/**
	 * Execure query in successFactor
	 * 
	 * @param String
	 *            val
	 * @return Response
	 */
	@POST
	@Path("executeQuery")
	@Produces(MediaType.APPLICATION_JSON)
	public Response controlPanelExecuteQuery(String val) {
		try {
			JSONObject json = new JSONObject(val);

			if (json.getString("query") != null && !json.getString("query").trim().equals("")) {
				String response = succesFactorAdminFacade.executeQuerySF(json.getString("query"));
				return Response.status(200).entity(response).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(200).entity("OK").build();
	}

	/**
	 * Get export tables
	 * 
	 * @param @PathParam("idTables")
	 *            String idTables
	 * @return StructureBusiness Event
	 */
	@GET
	@Path("/exportdata/{idTables}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExportFieldsData(@PathParam("idTables") String idTables) {
		SuccessFactorTemplateFacade SFTemplateFacade = new SuccessFactorTemplateFacade();

		if (UtilCodesEnum.CODE_ENTITY_FIELDSTEMPLATELIBRARY.getCode().equals(idTables)) {
			ArrayList<FieldsTemplateLibrary> listReturn = SFTemplateFacade.templateFieldLibraryGetAll();
			return Response.status(200).entity(listReturn).build();
		}

		else if (UtilCodesEnum.CODE_ENTITY_ADMINPARAMETERS.getCode().equals(idTables)) {
			List<AdminParameters> listReturn = succesFactorAdminFacade.admParameterGetAll();
			List<AdminParameters> listReturn2 = new ArrayList<AdminParameters>();
			for (AdminParameters adminParameter : listReturn) {
				if (adminParameter.getIsControlPanel() != null) {
					if (adminParameter.getIsControlPanel() == true) {
						//listReturn.remove(adminParameter);
					}else {
						adminParameter.setLastUpdateDate(null);
						listReturn2.add(adminParameter);
					}
					
				}
			}
			return Response.status(200).entity(listReturn2).build();
		}

		else if (UtilCodesEnum.CODE_ENTITY_FIELDSMAPPINGPPD.getCode().equals(idTables)) {
			ArrayList<FieldsMappingPpd> listReturn = succesFactorAdminFacade.mappingPpdFieldsGetAll(null, null, null, Boolean.TRUE);
			return Response.status(200).entity(listReturn).build();
		} else if (UtilCodesEnum.CODE_ENTITY_PARAMEVENTLISTENERS.getCode().equals(idTables)) {
			ArrayList<EventListenerParam> listReturn = succesFactorAdminFacade.eventsListenerGetAll();
			return Response.status(200).entity(listReturn).build();
		} else if (UtilCodesEnum.CODE_ENTITY_COUNTRY.getCode().equals(idTables)) {
			List<Countries> listReturn = succesFactorAdminFacade.countriesGetAll();
			return Response.status(200).entity(listReturn).build();
		} else if (UtilCodesEnum.CODE_ENTITY_SIGNTEMPLATELIBRARY.getCode().equals(idTables)) {
			List<SignsTemplateLibrary> listReturn = succesFactorAdminFacade.signTemplateLibGetAll();
			return Response.status(200).entity(listReturn).build();
		} else if (UtilCodesEnum.CODE_ENTITY_LANAGUAGE.getCode().equals(idTables)) {
			List<Language> listReturn = succesFactorAdminFacade.languageGetAll();
			return Response.status(200).entity(listReturn).build();
		} else if (UtilCodesEnum.CODE_ENTITY_ROLESMAPPINGPPD.getCode().equals(idTables)) {
			List<RolesMappingPpd> listReturn = succesFactorAdminFacade.roleGetAll();
			return Response.status(200).entity(listReturn).build();
		} else if (UtilCodesEnum.CODE_ENTITY_TEMPLATE.getCode().equals(idTables)) {
			List<TemplateInfoDto> listReturn = SFTemplateFacade.templateGetList(true, true);
			return Response.status(200).entity(listReturn).build();
		} else if (UtilCodesEnum.CODE_ENTITY_FOLDERS_TEMPLATE.getCode().equals(idTables)) {
			List<FolderDTO> listFld = SFTemplateFacade.getFolderList();
			Gson gson = new Gson();
			String json = UtilMapping.deleteItemJsonArray(gson.toJson(listFld), "nodes,levelFolder,catSeeEdit,catSeeEnter,catSeeNothing".split(","));
			JSONArray ja = new JSONArray(json);
			@SuppressWarnings("unchecked")
			List<FolderDTO> listReturn = gson.fromJson(json, List.class);
			return Response.status(200).entity(listReturn).build();
		} else if (UtilCodesEnum.CODE_ENTITY_LOOKUPTABLE.getCode().equals(idTables)) {
			List<LookupTable> listReturn = succesFactorAdminFacade.lookupsGetAll();
			return Response.status(200).entity(listReturn).build();
		} else if (UtilCodesEnum.CODE_ENTITY_BUSINESS_STRUCTURE.getCode().equals(idTables)) {
			ArrayList<StructureBusiness> listReturn = succesFactorAdminFacade.structureBusinessGetAll();
			return Response.status(200).entity(listReturn).build();
		} else if (UtilCodesEnum.CODE_ENTITY_MANAGER_ROLE.getCode().equals(idTables)) {
			List<ManagerRole> listReturn = succesFactorAdminFacade.getAllExportManager();
			return Response.status(200).entity(listReturn).build();
		} else if (UtilCodesEnum.CODE_ENTITY_TABLE_AUTHO.getCode().equals(idTables)) {
			SuccessFactorAuthoFacade facade = new SuccessFactorAuthoFacade();
			List<AuthorizationDetails> listReturn = facade.authorizationDetailGetAll();
			return Response.status(200).entity(listReturn).build();				
		} else {
			return Response.status(200).entity("{\"error\": \"parameter not found\"}").build();
		}
	}

	/**
	 * POST import tables
	 * 
	 * @param @PathParam("idTables")
	 *            String idTables
	 * @return StructureBusiness Event
	 */
	@POST
	@Path("/importdata/{idTables}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response getImportData(MultipartFormDataInput input, @PathParam("idTables") String idTables) {

		String fileName = "";
		byte[] bytes = null;
		InputStream inputStream = null;

		SuccessFactorTemplateFacade SFTemplateFacade = new SuccessFactorTemplateFacade();
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

		String json = new String(bytes, StandardCharsets.UTF_8);
		String idUser = UserManager.getUserId();

		try {
			if (UtilCodesEnum.CODE_ENTITY_FIELDSTEMPLATELIBRARY.getCode().equals(idTables)) {
				List<FieldsTemplateLibrary> listReturn = utilMapping.loadFieldsTemplateLibraryFromJsom(json);
				SFTemplateFacade.updateInsertFieldsTemplateLibrary(listReturn);
				if (listReturn.get(0).getNameSource() != null)
					return Response.status(200).entity("200").build();
				else
					return Response.status(500).entity(null).build();
			}

			else if (UtilCodesEnum.CODE_ENTITY_ADMINPARAMETERS.getCode().equals(idTables)) {
				List<AdminParameters> listReturn = utilMapping.loadAdminParametersFromJsom(json);
				succesFactorAdminFacade.admParameterDeleteAll();
				succesFactorAdminFacade.updateInsertAdminParameters(listReturn, idUser);
				if (listReturn.get(0).getNameCode() != null)
					return Response.status(200).entity("200").build();
				else
					return Response.status(500).entity(null).build();
			}

			else if (UtilCodesEnum.CODE_ENTITY_FIELDSMAPPINGPPD.getCode().equals(idTables)) {
				List<FieldsMappingPpd> listReturn = utilMapping.loadFieldsMappingPpdFromJsom(json);
				succesFactorAdminFacade.updateInsertFieldsMappingPpd(listReturn);
				if (listReturn.get(0).getNameSource() != null)
					return Response.status(200).entity("200").build();
				else
					return Response.status(500).entity(null).build();
			} else if (UtilCodesEnum.CODE_ENTITY_PARAMEVENTLISTENERS.getCode().equals(idTables)) {
				List<EventListenerParam> listReturn = utilMapping.loadParameterEventListenersFromJsom(json);
				succesFactorAdminFacade.updateInsertParametersEventListener(listReturn);
				if (listReturn.get(0).getEventId() != null)
					return Response.status(200).entity("200").build();
				else
					return Response.status(500).entity(null).build();
			} else if (UtilCodesEnum.CODE_ENTITY_COUNTRY.getCode().equals(idTables)) {
				List<Countries> listReturn = utilMapping.loadCountriesFromJsom(json);
				succesFactorAdminFacade.updateInsertCountries(listReturn);
				if (listReturn.get(0).getCode() != null)
					return Response.status(200).entity("200").build();
				else
					return Response.status(500).entity(null).build();
			} else if (UtilCodesEnum.CODE_ENTITY_SIGNTEMPLATELIBRARY.getCode().equals(idTables)) {
				List<SignsTemplateLibrary> listReturn = utilMapping.loadSignTemplateLibFromJsom(json);
				succesFactorAdminFacade.updateInsertParametersSignTemplateLibrary(listReturn);
				if (listReturn.get(0).getNameSource() != null)
					return Response.status(200).entity("200").build();
				else
					return Response.status(500).entity(null).build();
			} else if (UtilCodesEnum.CODE_ENTITY_LANAGUAGE.getCode().equals(idTables)) {
				List<Language> listReturn = utilMapping.loadLanguagesFromJson(json);
				succesFactorAdminFacade.updateInsertLanguages(listReturn);
				if (listReturn.get(0).getCode() != null)
					return Response.status(200).entity("200").build();
				else
					return Response.status(500).entity(null).build();
			} else if (UtilCodesEnum.CODE_ENTITY_ROLESMAPPINGPPD.getCode().equals(idTables)) {
				List<RolesMappingPpd> listReturn = utilMapping.loadRolesFromJson(json);
				int i = succesFactorAdminFacade.updateInsertRoles(listReturn);
				if (i == -1) {
					return Response.status(500).entity(null).build();
				} else {
					if (listReturn.get(0).getIdSf() != null)
						return Response.status(200).entity("200").build();
					else
						return Response.status(500).entity(null).build();
				}

			} else if (UtilCodesEnum.CODE_ENTITY_TEMPLATE.getCode().equals(idTables)) {
				List<TemplateInfoDto> listReturn = utilMapping.loadTemplateFromJson(json);
				logger.info(listReturn.get(0).getId());
				List<TemplateInfoDto> listTemplates = SFTemplateFacade.templateGetList(true, false);
				logger.info("Aqui estoy despues de extraer la lista");
				succesFactorAdminFacade.updateInsertTemplate(listReturn, listTemplates);
				logger.info("Realice el proceso de ingresar");
				if (listReturn.get(0).getIdTemplate() != null)
					return Response.status(200).entity("200").build();
				else
					return Response.status(200).entity("200").build();
			} else if (UtilCodesEnum.CODE_ENTITY_FOLDERS_TEMPLATE.getCode().equals(idTables)) {
				List<FolderDTO> listReturn = utilMapping.loadFolderFromJson(json);
				List<FolderDTO> flist = SFTemplateFacade.templateFoldersStructure(UserManager.getUserId(), null);
				logger.info("-----" + listReturn.get(0).getTitle());
				int item = succesFactorAdminFacade.updateInsertFolder(listReturn, flist);

				if (item != 0) {
					return Response.status(200).entity("200").build();
				} else {
					return Response.status(500).entity(null).build();
				}
			} else if (UtilCodesEnum.CODE_ENTITY_LOOKUPTABLE.getCode().equals(idTables)) {
				List<LookupTable> listReturn = utilMapping.loadLookuptableFromJson(json);
				int index = succesFactorAdminFacade.insertUpdateLookuptable(listReturn);

				if (index != 0) {
					return Response.status(200).entity("200").build();
				} else {
					return Response.status(500).entity(null).build();
				}

			} else if (UtilCodesEnum.CODE_ENTITY_BUSINESS_STRUCTURE.getCode().equals(idTables)) {
				List<StructureBusiness> listReturn = utilMapping.loadStructureBusinessFromJson(json);
				int index = succesFactorAdminFacade.insertUpdateStructure(listReturn);
				if (index != 0) {
					return Response.status(200).entity("200").build();
				} else {
					return Response.status(500).entity("error parent structure").build();
				}
			} else if (UtilCodesEnum.CODE_ENTITY_MANAGER_ROLE.getCode().equals(idTables)) {
				List<ManagerRole> listReturn = utilMapping.loadManagerRoleFromJson(json);
				int index = succesFactorAdminFacade.insertUpdateManagerRole(listReturn);
				if (index != 0) {
					return Response.status(200).entity("200").build();
				} else {
					return Response.status(500).entity("error structure manager ").build();
				}
			} else {
				return Response.status(500).entity("{\"error\": \"parameter not found\"}").build();
			}
		} catch (Exception e) {
			//
			logger.info("error -> " + e.getMessage());
			e.printStackTrace();
			return Response.status(500).entity(e.getMessage()).build();
		}

	}

	/**
	 * get all control attachments pending to process
	 * 
	 * @param val
	 *            StructureBusiness.class
	 * @return ArrayList<EventListenerDocProcess> listReturn
	 * 
	 */
	@POST
	@Path("/attachments/processattachfilters")
	@Produces(MediaType.APPLICATION_JSON)
	public Response attachmentsGetAllFilters(String val) {
		// get max number of retries

		Gson gson = new Gson();
		FilterQueryDto filters = gson.fromJson(val, FilterQueryDto.class);
		AdminParameters paramAdminMaxRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_ATTACH.getCode());
		ArrayList<EventListenerDocProcess> list = succesFactorAdminFacade.getAllAttachmentProcess(null, null, null, filters);

		if (paramAdminMaxRetriesCode != null) {
			for (EventListenerDocProcess item : list) {
				item.setRetriesInfo(item.getRetries() + "/" + paramAdminMaxRetriesCode.getValue());
			}
		}

		return Response.status(200).entity(list).build();
	}

	/**
	 * get all event listener control pending to process
	 * 
	 * @param val
	 *            StructureBusiness.class
	 * @return ArrayList<EventListenerCtrlProcess> listReturn
	 */
	@POST
	@Path("/eventsCtrl/processeventfilters")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerCtrlProcGetAllFilters(String val) {

		Gson gson = new Gson();
		FilterQueryDto filters = gson.fromJson(val, FilterQueryDto.class);
		// get Status to limit number of retries
		AdminParameters paramAdminRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STATUS_LIMIT_RETRIES.getCode());
		// get max number of retries
		AdminParameters paramAdminMaxRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_RETRIES.getCode());

		ArrayList<EventListenerCtrlProcess> listReturn = succesFactorAdminFacade.eventsListenerCtrlProcGetAll(new Date(), null, null, null, filters, null, null);

		if (paramAdminMaxRetriesCode != null && paramAdminRetriesCode != null) {
			for (EventListenerCtrlProcess item : listReturn) {
				if (paramAdminRetriesCode != null && paramAdminRetriesCode.getValue() != null && item.getStatus() != null && paramAdminRetriesCode.getValue().contains(item.getStatus())) {
					item.setRetriesInfo(item.getRetries() + "/" + paramAdminMaxRetriesCode.getValue());
				} else {
					item.setRetriesInfo(item.getRetries() + "");
				}
			}
		}

		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * Insert Country
	 * 
	 * @param val
	 *            StructureBusiness.class
	 * @return
	 */
	@POST
	@Path("/country/add_country")
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertCountry(String val) {

		Gson gson = new Gson();
		Countries country = gson.fromJson(val, Countries.class);
		Countries co = succesFactorAdminFacade.countryInsert(country);

		return Response.status(200).entity(co).build();
	}

	/**
	 * Get all Lookups
	 * 
	 * @return ArrayList<Lookup> listReturn
	 */
	@GET
	@Path("/lookup_all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLookupAll() {
		List<LookupTable> listReturn = succesFactorAdminFacade.lookupsGetAll();
		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * Delete Lookup @PathParam("idLookup") String idLookup
	 * 
	 * @return ArrayList<Lookups> listReturn
	 */
	@DELETE
	@Path("/lookups/delete_lookup/{idLookup}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLookupAll(@PathParam("idLookup") Long idLookup) {
		try {
			succesFactorAdminFacade.lookupDelete(idLookup);
			return Response.status(200).entity("200").build();
		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	/**
	 * Update Lookup
	 * 
	 * @param val
	 *            StructureBusiness.class
	 * @return
	 */
	@POST
	@Path("/lookup/insert_update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response InsertUpdateLookup(String val) {
		try {
			JSONObject json = new JSONObject(val);
			Gson gson = new Gson();
			if (json.get("id") == null) {
				LookupTable lookup = gson.fromJson(val, LookupTable.class);
				LookupTable co = succesFactorAdminFacade.lookupInsert(lookup);
				return Response.status(200).entity(co).build();
			} else {
				LookupTable lookup = gson.fromJson(val, LookupTable.class);
				LookupTable co = succesFactorAdminFacade.lookupUpdate(lookup);
				return Response.status(200).entity(co).build();
			}
		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	/**
	 * Get all Countries
	 * 
	 * @return ArrayList<Countries> listReturn
	 */
	@GET
	@Path("/country_all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCountriesAll() {
		List<Countries> listReturn = succesFactorAdminFacade.countriesGetAll();
		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * Delete country @PathParam("idCountry") String idCountry
	 * 
	 * @return ArrayList<Countries> listReturn
	 */
	@DELETE
	@Path("/countries/delete_country/{idCountry}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCountriesAll(@PathParam("idCountry") Long idCountry) {
		try {
			succesFactorAdminFacade.countryDelete(idCountry);
			return Response.status(200).entity("200").build();
		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	/**
	 * Update Country
	 * 
	 * @param val
	 *            StructureBusiness.class
	 * @return
	 */
	@POST
	@Path("/country/insert_update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response InsertUpdateCountry(String val) {
		try {
			JSONObject json = new JSONObject(val);
			Gson gson = new Gson();
			if (json.get("id") == null) {
				Countries country = gson.fromJson(val, Countries.class);
				Countries co = succesFactorAdminFacade.countryInsert(country);
				return Response.status(200).entity(co).build();
			} else {
				Countries country = gson.fromJson(val, Countries.class);
				Countries co = succesFactorAdminFacade.countryUpdate(country);
				return Response.status(200).entity(co).build();
			}
		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	/**
	 * Get all Signs Template Library
	 * 
	 * @return ArrayList<SignsTemplateLibrary> listReturn
	 */
	@GET
	@Path("/mappingsignall")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMappingSignAll() {
		List<SignsTemplateLibrary> listReturn = succesFactorAdminFacade.signTemplateLibGetAll();
		if (listReturn == null) {
			listReturn = new ArrayList<SignsTemplateLibrary>();
		}
		
		SignsTemplateLibrary item = new SignsTemplateLibrary();
		item.setId(-2L);
		item.setNameSource("None");
		listReturn.add(item);

		item = new SignsTemplateLibrary();
		item.setId(-1L);
		item.setNameSource("Other (Constant or Email whit Format)");
		listReturn.add(item);

		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * Update Country
	 * 
	 * @param val
	 *            StructureBusiness.class
	 * @return
	 */
	@POST
	@Path("/mappingsign/insertupdate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertUpdateCountry(String val) {
		try {
			JSONObject json = new JSONObject(val);
			Gson gson = new Gson();
			SignsTemplateLibrary mapSign = new SignsTemplateLibrary();
			mapSign = gson.fromJson(val, SignsTemplateLibrary.class);
			if (json.get("id").equals(null)) {
				mapSign = succesFactorAdminFacade.signsTemplateLibraryInsert(mapSign);
				return Response.status(200).entity(mapSign).build();
			} else {
				mapSign = succesFactorAdminFacade.SignsTemplateLibraryUpdate(mapSign);
				return Response.status(200).entity(mapSign).build();
			}
		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	/**
	 * Delete SignsTemplateLibrary @PathParam("idCountry") String idCountry
	 * 
	 * @return ArrayList<Countries> listReturn
	 */
	@DELETE
	@Path("/mappingsign/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response SignsTemplateLibraryDelete(@PathParam("id") Long id) {
		try {
			succesFactorAdminFacade.SignsTemplateLibraryDelete(id);
			return Response.status(200).entity("200").build();
		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	/**
	 * Get all Language
	 * 
	 * @return ArrayList<Language> listReturn
	 */
	@GET
	@Path("/language/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLanguageAll() {
		List<Language> listReturn = succesFactorAdminFacade.languageGetAll();
		return Response.status(200).entity(listReturn).build();
	}

	/**
	 * Insert_Update Language
	 * 
	 * @param val
	 *            StructureBusiness.class
	 * @return
	 */
	@POST
	@Path("/language/insert_update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response InsertUpdateLanguage(String val) {
		try {
			JSONObject json = new JSONObject(val);
			Gson gson = new Gson();
			if (json.get("id") == null) {
				Language lang = gson.fromJson(val, Language.class);
				Language co = succesFactorAdminFacade.languageInsert(lang);
				return Response.status(200).entity(co).build();
			} else {
				Language lang = gson.fromJson(val, Language.class);
				Language co = succesFactorAdminFacade.languageUpdate(lang);
				return Response.status(200).entity(co).build();
			}
		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	/**
	 * Delete Language @param("idLanguage") String idLanguage
	 */
	@DELETE
	@Path("/language/delete/{idLanguage}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLanguageDelete(@PathParam("idLanguage") Long idLanguage) {
		try {
			succesFactorAdminFacade.languageDelete(idLanguage);
			return Response.status(200).entity("200").build();
		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	/**
	 * Search Language @param("idCode") String idCode
	 */
	@GET
	@Path("/language/{idCode}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLanguageCode(@PathParam("idCode") String idCode) {
		try {
			if (idCode != null && !idCode.equals("")) {
				List<Language> lang = succesFactorAdminFacade.languageGetByCode(idCode);

				if (lang != null)
					return Response.status(200).entity(lang).build();
			}

			return Response.status(200).entity(new ArrayList<Language>()).build();
		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	/**
	 * Update Insert Control Panel Jobs
	 * 
	 * @param String
	 *            jobEvent
	 * @param String
	 *            jobEmployee
	 * @return Response
	 */
	@POST
	@Path("/controlpanel/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getControlPanelJob(String val) {

		JSONObject json = new JSONObject(val);
		String jobEvent = String.valueOf(json.getBoolean("JobEventListener"));
		String jobEmployee = String.valueOf(json.getBoolean("JobEmployee"));
		String jobLoad = String.valueOf(json.getBoolean("JobLoad"));
		try {
			succesFactorAdminFacade.adminParamInsertOrUpdate(UtilCodesEnum.CODE_CONTROLP_ENABLE_JOB_EVENT_LISTENER.getCode(), jobEvent, true);
			succesFactorAdminFacade.adminParamInsertOrUpdate(UtilCodesEnum.CODE_CONTROLP_ENABLE_JOB_EMPLOYEE_SYNC.getCode(), jobEmployee, true);
			succesFactorAdminFacade.adminParamInsertOrUpdate(UtilCodesEnum.CODE_CONTROLP_ENABLE_JOB_LOAD_MASSIVE_EMPL.getCode(), jobLoad, true);
			return Response.status(200).entity("200").build();
		} catch (Exception e) {
			return Response.status(500).entity(e.getMessage()).build();
		}
	}

	/**
	 * Search Description Job EventListener Active
	 * 
	 * @return Response
	 */
	@GET
	@Path("/controlpanel/jobevent")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getControlPanelJobEvent() {
		try {
			AdminParameters parameter = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_CONTROLP_ENABLE_JOB_EVENT_LISTENER.getCode());

			if (parameter == null) {
				return Response.status(200).entity(null).build();
			} else {
				return Response.status(200).entity(parameter.getValue()).build();
			}

		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	/**
	 * Search Description Job EventListener Active
	 * 
	 * @return Response
	 */
	@GET
	@Path("/controlpanel/jobemployee")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getControlPanelJobEmployee() {
		try {
			AdminParameters parameter = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_CONTROLP_ENABLE_JOB_EMPLOYEE_SYNC.getCode());

			if (parameter == null) {
				return Response.status(200).entity(null).build();
			} else {
				return Response.status(200).entity(parameter.getValue()).build();
			}
		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

	/**
	 * Search Description Job Load Massive Users
	 * 
	 * @return Response
	 */
	@GET
	@Path("/controlpanel/jobload")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getControlPanelJobLoad() {
		try {
			AdminParameters parameter = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_CONTROLP_ENABLE_JOB_LOAD_MASSIVE_EMPL.getCode());

			if (parameter == null) {
				return Response.status(200).entity(null).build();
			} else {
				return Response.status(200).entity(parameter.getValue()).build();
			}

		} catch (Exception e) {
			return Response.status(200).entity(e.getMessage()).build();
		}
	}

}
