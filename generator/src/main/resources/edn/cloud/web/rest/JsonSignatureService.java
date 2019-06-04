package edn.cloud.web.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.dto.ppd.api.PpdSigningCallbackDto;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;

@Path("/json/ppdocsign")
public class JsonSignatureService 
{
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private SuccessFactorFacade succesFactorFacade = new SuccessFactorFacade();
	
	@POST
	@Path("/callback")
	@Produces(MediaType.APPLICATION_JSON)
	public Response processCallbackPpdSigning(String val) 
	{
		succesFactorFacade.saveLoggerControl("200", "Start processCallbackPpdSigning", "callback", "Started");
		
		if(val!=null)
		{
			Gson gson = new Gson();		
			PpdSigningCallbackDto parameters = gson.fromJson(val, PpdSigningCallbackDto.class);
			succesFactorFacade.saveLoggerControl("200",parameters.getExternal_id()+" "+parameters.getStatus(), "callback", "Started");
			succesFactorFacade.signatureUdpateCtrlCallback(parameters);
		}
		
		succesFactorFacade.saveLoggerControl("200", "Start processCallbackPpdSigning", "callback", "End");
		
		return Response.status(UtilCodesEnum.CODE_SUCCESS_200.getCodeInt()).entity("{}").build();
	}

}
