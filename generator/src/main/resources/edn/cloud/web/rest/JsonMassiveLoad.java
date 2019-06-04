package edn.cloud.web.rest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.JSONObject;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilFiles;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorEventLFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorMassiveLoadFacade;
import edn.cloud.sfactor.persistence.dao.MassiveLoadUserDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Countries;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerDocHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerParam;
import edn.cloud.sfactor.persistence.entities.MassiveLoadUser;

@Path("/json/admin")
public class JsonMassiveLoad 
{
	private final UtilLogger logger = UtilLogger.getInstance();	
	
	private final SuccessFactorMassiveLoadFacade sfMassiveload = new SuccessFactorMassiveLoadFacade();	
	
	private SuccessFactorAdminFacade succesFactorAdminFacade = new SuccessFactorAdminFacade();
	
	private SuccessFactorEventLFacade succesFactorEventLFacade = new SuccessFactorEventLFacade();

	/**
	 * Massive load of user
	 * @param MultipartFormDataInput input
	 * @param status of attachments module (true;false;true) = (ONB,REC,EMC)
	 * @return Response
	 * */
	@POST
	@Path("/massive/users/{nameLoad}/{attacmentModule}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFileMassiveLoad(MultipartFormDataInput input,@PathParam("nameLoad") String nameLoad,@PathParam("attacmentModule") String attacmentModule) {
		
		String fileName = "";
		byte[] bytes = null;
		InputStream inputStream = null;
		
		ArrayList<EventListenerCtrlProcess> listUserDet = new ArrayList<>() ;
		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("loadUser");

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

		EventListenerParam eventParam = succesFactorAdminFacade.eventsGetByName(UtilCodesEnum.CODE_STATUS_EVENTLIS_DEFAULT_MASSLOAD.getCode());
		AdminParameters paramAdmUserid = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_USERID.getCode());
		AdminParameters paramProcessUserNoEmail = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_PROCESS_USERNOMAIL.getCode());
		AdminParameters paramPatternEmail = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_FORMAT_MAILUSERTERM_PPD.getCode());
		List<Countries> listCountriesConf = succesFactorAdminFacade.countriesGetAll();		
		
		if(eventParam!=null && eventParam.getIsEnabled())
		{
			String csv = new String(bytes, StandardCharsets.UTF_8);
			if(csv!=null && !csv.equals(null) && !csv.equals("")) 
			{
				String [] lines = csv.split("\n");
				if(lines!=null && lines.length>0) 
				{
					for (String text : lines)
					{
						EventListenerCtrlProcess det = UtilMapping.getUserDet(text);
						if(det!=null)
						{
							det.setEventListenerParam(eventParam);
							listUserDet.add(det);
						}
					}
					
					if(!listUserDet.isEmpty()) 
					{
						if(listUserDet.size()>UtilCodesEnum.CODE_PARAM_ADM_MASSIVE_MAX_REG_PER_FILE.getCodeInt()) {
							return Response.status(1002).build(); 
						}
							
						MassiveLoadUser user = new MassiveLoadUser();
						user.setLoadReg(Long.valueOf(listUserDet.size()));
						user.setNameLoad(nameLoad);
						user.setStatus(UtilCodesEnum.CODE_STATUS_MASSLOAD_LOADING.getCode());
						user.setAttachTypes(attacmentModule!=null?attacmentModule:"");
						user.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() +"\n" 
								+"Process attachments: "+UtilMapping.getLabelsAttachMassiveLoad(user.getAttachTypes())
								+UtilCodesEnum.CODE_STRING_END.getCode());
						user = sfMassiveload.userLoadSave(user);
						
						if(user!=null)
						{
							for (EventListenerCtrlProcess userDet : listUserDet) 
							{
								userDet.setFkMassiveLoad(user);
								//in massive load the search of attachments is after
								userDet.setIsSearchAttachAfterProc(Boolean.TRUE);
								succesFactorEventLFacade.eventListenerCtrlCreate(userDet.getStartDatePpdOnString(),
																				 userDet.getUserIdPpd(),
																				 userDet.getUserIdPpd(),
																				 userDet.getSeqNumberPpd(),
																				 true,	
																				 false,
																				 listCountriesConf, 
																				 paramAdmUserid, 
																				 paramProcessUserNoEmail,
																				 paramPatternEmail,
																				 user,
																				 eventParam);
							}
							
							user.setStatus(UtilCodesEnum.CODE_STATUS_MASSIVEEMPLE_LOADED.getCode());
							sfMassiveload.massiveLoadUpdate(user);
							return Response.status(200).build();
						}				
					}else
					{
						return Response.status(200).build();
					}				
				}		
			}
		}
		else
			return Response.status(1001).build();
		
		return Response.status(500).build();
	}
	
	
	/**
	 * Get all 
	 * @return Response
	 * 
	 */
	@GET
	@Path("/massiveuserall")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllMassiveUser() {
		List<MassiveLoadUser> listUser = sfMassiveload.getUserAll();
		return Response.status(200).entity(sfMassiveload.updateCountTotalReg(listUser)).build();
	}
	
	/**
	 * Update user massive
	 * 
	 */
	@POST
	@Path("/massiveuserupdate/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllMassiveUserDet(@PathParam("id")Long id, String val) 
	{
		Gson token = new Gson();
		SlugItem infoUpdate = token.fromJson(val, SlugItem.class);
		MassiveLoadUser user = sfMassiveload.userLoadGetById(id);
		
		if (user != null && infoUpdate != null) 
		{
			Date date = new Date();
			String dateSeconds = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);
			user.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + infoUpdate.getLabel() + UtilCodesEnum.CODE_STRING_END.getCode() + user.getObservations());
			user.setLastUpdateOn(new Date());
			user.setStatus(infoUpdate.getValue().toString());			
			user = sfMassiveload.massiveLoadUpdate(user);
			
			if (infoUpdate.getFlag() || user.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode())) {
				succesFactorAdminFacade.updateEventCtrlStatus(null, id, user.getStatus());
			}
			
			return Response.status(200).entity(user).build();			
		}
		else {
			return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(UtilCodesEnum.CODE_ERROR_500.getCode()).build();
		}
	}
	
	
	/**
	 * Update userDet
	 * 
	 */
	@GET
	@Path("/massiveuserdetupdate/{id}/{status}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response userDetUpdateSave(@PathParam("id")Long id,@PathParam ("status")String status) 
	{	
		EventListenerCtrlProcess eventtoUpdate = succesFactorAdminFacade.eventsListenerCtrlProcGetByid(id);

		if (eventtoUpdate != null) 
		{
			Date date = new Date();
			String dateSeconds = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);

			eventtoUpdate.setStatus(status);
			eventtoUpdate.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + status + UtilCodesEnum.CODE_STRING_END.getCode() + eventtoUpdate.getObservations());
			eventtoUpdate.setLastUpdateOn(new Date());
			eventtoUpdate.setIdJobProcess(null);// reset id job process
			eventtoUpdate = succesFactorAdminFacade.eventListenerCtrlProcessUpdate(eventtoUpdate);

			if (eventtoUpdate != null) {
				if(eventtoUpdate.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode())) {
					succesFactorAdminFacade.eventListenerUpdateAllDocProc(eventtoUpdate.getId(),status,"");
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
	 * Get time current / future
	 * @param Boolean
	 * @return
	 */
	@GET
	@Path("/massiveusertime/{time}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUserTime(@PathParam("time") Boolean time) 
	{
		List <MassiveLoadUser> listReturn = (List<MassiveLoadUser>) sfMassiveload.getAllUserTime(new Date(), time);
		return Response.status(200).entity(sfMassiveload.updateCountTotalReg(listReturn)).build();
	}
	
	/**
	 * Paginator User Details
	 * @param 
	 * @return
	 */
	@GET
	@Path("/massiveuserdetpage/{iduser}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getuserListPage(@PathParam ("iduser") String idMassUser,@PathParam("page") String page) 
	{
		List <EventListenerCtrlProcess> listReturn = new ArrayList<>() ;
		
		if(!idMassUser.equals("undefined"))
		{
			if(idMassUser!=null && !idMassUser.equals(""))
			{
				// get Status to limit number of retries
				AdminParameters paramAdminRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STATUS_LIMIT_RETRIES.getCode());				
				// get max number of retries
				AdminParameters paramAdminMaxRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_RETRIES.getCode());
				
				FilterQueryDto filter = new FilterQueryDto();
					
				AdminParameters parameter = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_REG_PER_PAGE.getCode());
				if(parameter!=null && parameter.getValue()!=null)
					filter.setMaxResult(parameter.getValue());
				else
					filter.setMaxResult("50");
				
				filter.setPage(page);		
				listReturn = (List<EventListenerCtrlProcess>) succesFactorAdminFacade.getEventListenerCtrlByPage(filter,Long.valueOf(idMassUser));
				
				
				//add history register
				if(listReturn==null)
					listReturn = new ArrayList<>();
				
				listReturn.addAll(
				UtilMapping.loadEventListeCrtlFormHisto(
						succesFactorAdminFacade.eventsListenerCtrlHistoGetAll(filter,Long.valueOf(idMassUser)),
						paramAdminMaxRetriesCode!=null?paramAdminMaxRetriesCode.getValue():""));
				
			}
		}
		
		return Response.status(200).entity(listReturn).build();
	}
	
	/**
	 * Get number pages
	 * @param String idUser
	 */
	@GET
	@Path("/massiveusernumberpage/{iduser}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response numberPages(@PathParam ("iduser") String idUser) 
	{		
		Integer maxRegByPage = 50;
		AdminParameters parameter = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_REG_PER_PAGE.getCode());
		if(parameter!=null && parameter.getValue()!=null)
			maxRegByPage = Integer.valueOf(parameter.getValue());		
		
		double page = succesFactorAdminFacade.eventListenerCtrlProcNumberPages(maxRegByPage, idUser);
		JSONObject json = new JSONObject();
		json.append("page", page);
		
		return Response.status(200).entity(json).build();
	}
	
	
	/**
	 * 
	 * */
	@GET
	@Path("/massive/last_executions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response massiveGetLastExecutions() {
		ArrayList<AdminParameters> list = new ArrayList<>();

		AdminParameters paramEventLis = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_MASSIVE.getCode());
		AdminParameters paramEventLisAttach = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_EVENTLISTATTACH.getCode());

		if (paramEventLisAttach != null) {
			paramEventLisAttach.setDescription("Job Send Attachment");
			list.add(paramEventLisAttach);
		}

		if (paramEventLis != null) {
			paramEventLis.setDescription("Job Massive Load");
			list.add(paramEventLis);
		}

		return Response.status(200).entity(list).build();

	}
	
	/**
	 * get all event listener control attachment pending to process
	 * @return ArrayList<EventListenerDocProcess> listReturn
	 */
	@GET
	@Path("/massive/attach/{idCtrl}/{idCtrlHisto}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerCtrlAttachGetAll(@PathParam("idCtrl") Long idCtrl,@PathParam("idCtrlHisto") Long idCtrlHisto) 
	{
		// get Status to limit number of retries
		AdminParameters paramAdminRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STATUS_LIMIT_RETRIES.getCode());
		// get max number of retries
		AdminParameters paramAdminMaxRetriesCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_RETRIES.getCode());
		ArrayList<EventListenerDocProcess> list = succesFactorAdminFacade.eventsListenerDocProcGetAllByIdCtrl(idCtrl);

		if (paramAdminMaxRetriesCode != null && paramAdminRetriesCode != null) 
		{
			for (EventListenerDocProcess item : list) 
			{
				if (paramAdminRetriesCode != null && paramAdminRetriesCode.getValue() != null && item.getStatus() != null && paramAdminRetriesCode.getValue().contains(item.getStatus())) {
					item.setRetriesInfo(item.getRetries() + "/" + paramAdminMaxRetriesCode.getValue());
				} else {
					item.setRetriesInfo(item.getRetries() + "");
				}
			}
		}

		
		if(list==null) {
			list = new ArrayList<>();
		}
		
		//add hostory
		if(idCtrlHisto!=null && idCtrlHisto>0) 
		{
			ArrayList<EventListenerDocHistory> listAdd = succesFactorAdminFacade.eventsListenerDocHistoGetAll(idCtrlHisto,null);
			if(listAdd!=null && listAdd.size()>0)
				list.addAll(UtilMapping.loadEventListenerDocFormHisto(listAdd));			
		}
			
		return Response.status(200).entity(list).build();
	}
	
	/**
	 * Download file CSV
	 * @param Long idMassUser
	 * @param String status
	 * @return Response
	 */
	@GET
	@Path("/massiveuserdownload/{id}/{status}")	
	public Response eventsListenerDownload(@PathParam("id")Long idMassUser,@PathParam("status")String status) 
	{	
		String statusGroup = "";
		FilterQueryDto filter = new FilterQueryDto();
		filter.setPage(1+"");
		filter.setMaxResult(UtilCodesEnum.CODE_PARAM_ADM_MASSIVE_MAX_REG_PER_FILE.getCode());
		
		
			
		filter.setStatus(UtilMapping.getListStatusForGroup(status));		
		ResponseGenericDto response = sfMassiveload.downloadMassiveLoad(idMassUser, filter); 
	
		
		return Response.ok(response.getMessage()).header("Content-Disposition", "attachment; filename="+response.getField()+".csv" ).build();
		//return Response.status(200).entity(map).build();
	}	
	
	/**
	 * Download file CSV by massive load
	 * @param Long id
	 * @param String val
	 * @return Response
	 */
	@GET
	@Path("/massivedownload/{id}/{email}")	
	public Response massiveDownloadByEmail(@PathParam("id")Long idMassUser,@PathParam("email")String email) 
	{
		String statusGroup = "";
		FilterQueryDto filter = new FilterQueryDto();
		filter.setPage(1+"");
		filter.setMaxResult(UtilCodesEnum.CODE_PARAM_ADM_MASSIVE_MAX_REG_PER_FILE.getCode());
		
		HashMap<String, String> map = new HashMap<>();
		map.put("value","");
		
		AdminParameters paramEmail = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_NOTIFICATIONS.getCode());
		
		if(paramEmail!=null && email!=null && idMassUser!=null)
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
			ResponseGenericDto response = sfMassiveload.downloadMassiveLoad(idMassUser, filter); 
			return Response.ok(response.getMessage()).header("Content-Disposition", "attachment; filename="+response.getField()+".csv" ).build();
		}		
						
		
		return Response.status(200).entity(map).build();
	}
	
	/**
	 * get statistics of massive load
	 * @param Long id
	 * @param String val
	 * @return Response
	 */
	@GET
	@Path("/massivedownload/{id}/statistics")	
	public Response massiveStatisticsByIdMassive(@PathParam("id")Long idMassUser)
	{	
		class LocalStatisticsClass {
			private List<SlugItem> statisticsList;

			public List<SlugItem> getStatisticsList() {
				return statisticsList;
			}

			public void setStatisticsList(List<SlugItem> statisticsList) {
				this.statisticsList = statisticsList;
			}			
		}
		
		LocalStatisticsClass content = new LocalStatisticsClass();
		content.setStatisticsList(sfMassiveload.getStatisticsMassiveLoad(idMassUser));
		return Response.status(200).entity(content).build();
	}
}