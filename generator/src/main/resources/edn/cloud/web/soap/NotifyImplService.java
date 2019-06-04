package edn.cloud.web.soap;

import java.rmi.RemoteException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.sfactor.business.facade.SuccessFactorWebServiceFacade;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;

@WebService(name = "NotifyImplService", targetNamespace = UtilSoap.CODE_NOTIF_SAP)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class NotifyImplService 
{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private SuccessFactorWebServiceFacade succesFactorWebServFacade = new SuccessFactorWebServiceFacade();

	// ---------------------------------------------------------------------
	// ----

	/*
	 * register information about event
	 * external @WebParam(name="externalEventMeta",targetNamespace=UtilSoap.
	 * CODE_NOTIF_SAP)EenAlertRequestMeta
	 * externalEventMeta @WebParam(name="events",targetNamespace=UtilSoap.
	 * CODE_NOTIF_SAP) EenEvent events)
	 */

	@WebMethod(operationName = "ExternalEvent")
	@WebResult(name = "ExternalEventResponse", targetNamespace = UtilSoap.CODE_NOTIF_SAP)
	public EenAlertResponsePayload externalEvent(
			@WebParam(name = "externalEventMeta", targetNamespace = UtilSoap.CODE_NOTIF_SAP) EenAlertRequestMeta externalEventMeta,
			@WebParam(name = "events", targetNamespace = UtilSoap.CODE_NOTIF_SAP) EenEvent events)
			throws RemoteException 
	{
		logger.info(externalEventMeta.type);

		EenUser user = UtilMapping.loadEenUserFetcher(events);
		EenAlertResponsePayload resp = new EenAlertResponsePayload();

		// Process Call
		EventListenerCtrlProcess eventListenerResponse = succesFactorWebServFacade.eventListenerActionProcessWebCall(user,externalEventMeta);
		
		if (eventListenerResponse.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode())) 
		{
			resp.setEntityId("AragoAPP");
			resp.setErrorCode(UtilCodesEnum.CODE_ERROR_500.getCode());
			resp.setErrorMessage(eventListenerResponse.getObservations());
			
		} else 
		{
			resp.setEntityId("AragoAPP");
			resp.setStatusDetails("EventListenerCtrl Id "+eventListenerResponse.getId());
		}

		return resp;
	}
}
