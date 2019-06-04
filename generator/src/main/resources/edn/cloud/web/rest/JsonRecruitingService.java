package edn.cloud.web.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.dto.integration.SFRecList;
import edn.cloud.sfactor.business.facade.SuccessFactorRecruitingFacade;

@Path("/json/recruiting")
public class JsonRecruitingService {
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private SuccessFactorRecruitingFacade succesFactorRecFacade = new SuccessFactorRecruitingFacade();

	// -----------------------------------------
	// methods events

	/**
	 * get all event listener
	 * 
	 * @return ArrayList<EventListener> listReturn
	 */
	@GET
	@Path("/search/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserRecruiting(@PathParam("id") String id) {
		SFRecList listReturn = succesFactorRecFacade.searchUser(id);
		return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();
	}
	
	
	/**
	 * get all event listener
	 * 
	 * @return ArrayList<EventListener> listReturn
	 */
	@POST
	@Path("/searchadv")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserRecruitingAdv(String val) {
		
		JSONObject jso = new JSONObject(val);
		
		SFRecList listReturn = succesFactorRecFacade.searchUserAdv(jso.getString("firstName"),jso.getString("lastName"),jso.getString("title"));
		return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity(listReturn).build();
	}

}
