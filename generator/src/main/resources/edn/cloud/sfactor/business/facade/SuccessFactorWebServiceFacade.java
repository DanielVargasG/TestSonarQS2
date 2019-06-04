package edn.cloud.sfactor.business.facade;

import java.util.List;
import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Countries;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerParam;
import edn.cloud.web.soap.EenAlertRequestMeta;
import edn.cloud.web.soap.EenUser;

public class SuccessFactorWebServiceFacade {
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	private SuccessFactorEventLFacade successFactorEventLFacade = new SuccessFactorEventLFacade();	

	public SuccessFactorWebServiceFacade() {
		super();
	}

	/**
	 * Insert event listener control process
	 * 
	 * @param EenUser eanUser
	 * @param EenAlertRequestMeta alert
	 * @return EventListenerCtrlProcess
	 */
	public EventListenerCtrlProcess eventListenerActionProcessWebCall(EenUser eanUser, EenAlertRequestMeta alert) 
	{
		EventListenerCtrlProcess entity = new EventListenerCtrlProcess();
		
		AdminParameters paramAdmUserid = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_USERID.getCode());
		AdminParameters paramProcessUserNoEmail = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_PROCESS_USERNOMAIL.getCode());
		AdminParameters paramPatternEmail = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_FORMAT_MAILUSERTERM_PPD.getCode());
		List<Countries> listCountriesConf = successFactorAdmin.countriesGetAll();
		
		if(alert.type != null && !alert.type.equals(""))
		{
			EventListenerParam eventParam = successFactorAdmin.eventsGetByName(alert.type);
			
			if (eventParam != null && eventParam.getIsEnabled()) 
			{		
				entity = successFactorEventLFacade.eventListenerCtrlCreate(
																eanUser.getStartDate(), 
																eanUser.getPersonIdExternal(), 
																eanUser.getUserId(), 
																eanUser.getSeqNumber(), 
																false, 
																true,
																listCountriesConf, 
																paramAdmUserid,
																paramProcessUserNoEmail,
																paramPatternEmail,
																null,
																eventParam);
			} else {
				EventListenerCtrlHistory eventCtl = new EventListenerCtrlHistory();
				eventCtl.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
				eventCtl.setObservations("Error. Type EventListener is Null");
				successFactorAdmin.eventListenerCtrlHistoInsert(eventCtl);
	
				entity.setStatus(eventCtl.getStatus());
				entity.setObservations(eventCtl.getObservations());
			}
		} else {
			EventListenerCtrlHistory eventCtl = new EventListenerCtrlHistory();
			eventCtl.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
			eventCtl.setObservations("Error. EventListener " + alert.type + " without configuration or disabled.");
			successFactorAdmin.eventListenerCtrlHistoInsert(eventCtl);

			entity.setStatus(eventCtl.getStatus());
			entity.setObservations(eventCtl.getObservations());
		}

		return entity;
	}
}
