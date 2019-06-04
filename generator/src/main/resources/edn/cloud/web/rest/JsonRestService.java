package edn.cloud.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.integration.SFGroup;
import edn.cloud.business.dto.integration.SFGroupList;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.odata.UserManager;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.persistence.dao.LoggerDAO;
import edn.cloud.sfactor.persistence.entities.LoggerAction;
import edn.cloud.sfactor.persistence.entities.ManagerRole;
import edn.cloud.sfactor.persistence.entities.ManagerRoleGroup;

@Path("/json")
public class JsonRestService {

	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	private SuccessFactorFacade succesFactorFacade = new SuccessFactorFacade();
	public JsonRestService() {
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDefault() {
		return Response.status(200).entity("").build();
	}
	
	@GET
	@Path("/logger")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logger() 
	{
		LoggerDAO list2 = new LoggerDAO();
		List<LoggerAction> li2 = list2.getAllRevert(null);
		return Response.status(200).entity(li2).build();

	}
	
	@POST
	@Path("/loggerfilter")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logger(String val) 
	{		
		FilterQueryDto filters = new FilterQueryDto();
				
		if(val!=null && !val.equals("")){
			Gson gson = new Gson();
			filters = gson.fromJson(val,FilterQueryDto.class);
		}
		
		LoggerDAO list2 = new LoggerDAO();
		List<LoggerAction> li2 = list2.getAllRevert(filters);
		return Response.status(200).entity(li2).build();

	}
	
	@DELETE
	@Path("/loggerdelete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loggerDelete() 
	{
		LoggerDAO list2 = new LoggerDAO();
		try {
		 int val =	list2.deleteAllWithLimit(3000);
			
			return Response.status(200).entity(val).build();
		}catch (Exception e) {
			return Response.status(500).entity(e.toString()).build();
		}
		
	}	
	
	/**
	 * Get all Staatus of Attachment
	 * @return List items with status and description
	 * 
	 * */
	@GET
	@Path("/infostatusevenlist")
	@Produces(MediaType.APPLICATION_JSON)
	public Response infoStatusEvenList() 
	{
		ArrayList<SlugItem> items = new ArrayList<>();
        SlugItem item = new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode());
        item.setLabel("es un estado errro");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTESENDTOSIGN.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEDOCCREATE.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_SEARCHDOCS.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_USERPENDINGMAIL.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        return Response.status(200).entity(items).build();
	}
	
	/**
	 * Get all Status of Documents
	 * @return List items with status and description
	 * 
	 * */
	@GET
	@Path("/infostatusdocs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response infoStatusDocs() 
	{
		ArrayList<SlugItem> items = new ArrayList<>();
        SlugItem item = new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());
        item.setLabel("es un estado errro");
        items.add(item);
        

        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_VALIDATE_DOC.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_PENDING_SIGN_DOC.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_COMPLETE_DOC.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_PENDING.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_CANCELED.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_SIGNED.getCode());
        item.setLabel(" label ");
        items.add(item);
       
        /*item= new SlugItem();
        item.setId(UtilCodesEnum..getCode());
        item.setLabel(" label ");
        items.add(item);*/
        
        return Response.status(200).entity(items).build();
        
	}
	
	/**
	 * Get all Status of Documents
	 * @return List items with status and description
	 * 
	 * */
	@GET
	@Path("/infostatusattach")
	@Produces(MediaType.APPLICATION_JSON)
	public Response infoStatusAttach() 
	{
		ArrayList<SlugItem> items = new ArrayList<>();
        SlugItem item = new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode());
        item.setLabel("es un estado errro");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTESENDTOSIGN.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEDOCCREATE.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_SEARCHDOCS.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_USERPENDINGMAIL.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        item= new SlugItem();
        item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode());
        item.setLabel(" label ");
        items.add(item);
        
        return Response.status(200).entity(items).build();
	}
	
	@POST
	@Path("/loggerdelete/items")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loggerDeleteItems(String val) 
	{		
		try {
			UtilLogger.getInstance().info("*/************-*/" +val);
			JSONArray jarray = new JSONArray(val);
			JSONObject jObj = new JSONObject();
			LoggerDAO lDao = new LoggerDAO();
			
			for(int i=0; i<jarray.length(); i++) {
				jObj = new JSONObject(jarray.get(i).toString());
				
				if(jObj.getBoolean("check")) {
					lDao.delete(lDao.getById(jObj.getLong("id")));
				}
			}
			
			return Response.status(200).entity(null).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
		

	}
	
	@POST
	@Path("/managerrole/insert")
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertManagerRole(String val) {
		try {
			Gson gson = new Gson();
			ManagerRole mr = new ManagerRole();
			mr = gson.fromJson(val, mr.getClass());
			successFactorAdmin.insertManagerRole(mr);
			
			return Response.status(200).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("/managerrole/getall")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllManagerRole() {
		try {
			List <ManagerRole> list = successFactorAdmin.getAllManagerRole();
			
			return Response.status(200).entity(list).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@DELETE
	@Path("/managerrole/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteManagerRole(@PathParam("id") Long id) {
		try {
			
			successFactorAdmin.deleteManagerRole(id);
			
			return Response.status(200).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@POST
	@Path("/managerrole/update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateManagerRole(String val) {
		try {
			Gson gson = new Gson();
			ManagerRole mr = new ManagerRole();
			mr = gson.fromJson(val, mr.getClass());
			successFactorAdmin.updateManagerRole(mr);
			
			return Response.status(200).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("/managerroleuser/getall/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllManagerRole(@PathParam("id") Long id) {
		try {
			List <ManagerRoleGroup> list = successFactorAdmin.getAllManagerGroup(id);
			
			return Response.status(200).entity(list).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@POST
	@Path("/managergroup/insert")
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertManagerRoleGropu(String val) {
		try {
			Gson gson = new Gson();
			ManagerRoleGroup mr = new ManagerRoleGroup();
			mr = gson.fromJson(val, mr.getClass());
			mr = successFactorAdmin.insertManagerRoleGroup(mr);
			
			return Response.status(200).entity(mr).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@DELETE
	@Path("/managergroup/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteManagerRoleGroup(@PathParam("id") Long id) {
		try {
			
			successFactorAdmin.deleteManagerRoleGroup(id);
			return Response.status(200).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("/managerrole/getallUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllManagerRoleUser() {
		try {
			List <ManagerRole> list = successFactorAdmin.getAllManagerRole();
			List <ManagerRole> listReturn = new ArrayList<>();
			List <ManagerRoleGroup> listGroup = new ArrayList<>();
			String user = "";
			List <String> idGroupsList = new ArrayList<>();
			SFGroupList listGroups = succesFactorFacade.userGroupList(UserManager.getUserId());
			for (SFGroup string : listGroups.getD()) {
				idGroupsList.add(string.getGroupName());
			}
			UtilLogger.getInstance().info("/*///*/*/"+idGroupsList);
			for (ManagerRole managerRole : list) {
				listGroup=successFactorAdmin.getAllManagerGroup(managerRole.getId());
				
				for (ManagerRoleGroup mrgroup : listGroup) {
					
					for (String igroup : idGroupsList) {
						if(mrgroup.getGroupId().equals(igroup)) {
							listReturn.add(managerRole);
						}
					}
					
				}
			}
			return Response.status(200).entity(listReturn).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity(e.getMessage()).build();
		}
	}
}