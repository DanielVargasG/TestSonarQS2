package edn.cloud.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.sfactor.persistence.dao.LoggerDAO;
import edn.cloud.sfactor.persistence.entities.LoggerAction;

@Path("/json/eventlist")
public class JsonAdmJobService {

	public JsonAdmJobService() {
	}

	
	/**
	 * get Select item events Listeners
	 * 
	 * @return StructureBusiness event
	 */
	@GET
	@Path("/selectevenlistener")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsListenerSelectItem() {
		ArrayList<SlugItem> listArrayReturn = new ArrayList<SlugItem>();
		
		//Event list --------------------------------------------------------------------------------
		SlugItem item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTESENDTOSIGN.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTESENDTOSIGN.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEDOCCREATE.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEDOCCREATE.getCode());
		listArrayReturn.add(item);
				
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_SEARCHDOCS.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_SEARCHDOCS.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_USERPENDINGMAIL.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_USERPENDINGMAIL.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
		listArrayReturn.add(item);

		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
		listArrayReturn.add(item);
		
		item = new SlugItem();
		item.setId(UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode());
		item.setLabel(UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode());
		listArrayReturn.add(item);
		
		return Response.status(200).entity(listArrayReturn).build();
	}
	
	@POST
	@Path("/eventlistfilter")
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
	
}