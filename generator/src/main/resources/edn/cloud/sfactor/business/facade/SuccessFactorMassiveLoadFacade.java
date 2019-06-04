package edn.cloud.sfactor.business.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.odata.UserManager;
import edn.cloud.sfactor.business.impl.EmailImpl;
import edn.cloud.sfactor.business.impl.SuccessFactorMassiveLoadImpl;
import edn.cloud.sfactor.business.interfaces.Email;
import edn.cloud.sfactor.business.interfaces.SuccessFactorMassiveLoad;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.MassiveLoadUser;

public class SuccessFactorMassiveLoadFacade {

	private SuccessFactorMassiveLoad sfMassiveLoad =  new SuccessFactorMassiveLoadImpl();
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	private SuccessFactorEventLFacade successFactorFacade = new SuccessFactorEventLFacade();
	
	/**
	 * 
	 * 
	 * 
	 * process call from EventListenerjob quartz
	 * @param Long idLoggerJob
	 */
	public void massiveLoadActionProcessQuartz(Long idLoggerJob)
	{
		this.massiveLoadFinish();//finish all massive load
		
		Date date = new Date();		
		// -----------------------------------
		// Load Admin parameters
		// get maximun number of records to process / employees
		Integer p_numMaxReg = 100;
		AdminParameters paramAdminCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_MASS_EMPL.getCode());
		if (paramAdminCode != null && paramAdminCode.getValue() != null) {
			p_numMaxReg = Integer.parseInt(paramAdminCode.getValue());
		}
		
		// status to load and process
		String statusToLoad = "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode() + "'," + "'" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode() + "'," + "'" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode()	+ "'," + "'" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode() + "'," + "'" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode() + "'," + "'" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode() + "'," + "'"
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode() + "','"
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING_VALIDATE.getCode() + "','"
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_USERPENDINGMAIL.getCode()+"','"
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode()+"' ";
		
		String statusMassiveLoad = "'" + UtilCodesEnum.CODE_STATUS_MASSIVEEMPLE_LOADED.getCode() + "','"
									   + UtilCodesEnum.CODE_STATUS_MASSIVEEMPLE_PENDING.getCode() + "'";
		
		// search register to proccess
		ArrayList<EventListenerCtrlProcess> eventListenerPendList = successFactorAdmin.eventsListenerCtrlProcGetAll(date, statusToLoad, p_numMaxReg, false,null,null,statusMassiveLoad);
		
		// register quartz event
		successFactorAdmin.eventListenerRegisterEventQuarzt(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_MASSIVE.getCode(),
				UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), date) + " " + "number of employees: " + (eventListenerPendList != null ? eventListenerPendList.size() : 0));

		if(eventListenerPendList!=null && eventListenerPendList.size()>0)
			successFactorFacade.processEventListener(idLoggerJob, eventListenerPendList);
		
		this.massiveLoadFinish();//finish all massive load
		
	}
	
	
	/**
	 * finish process for massiveLoad
	 * */
	public void massiveLoadFinish()
	{
		List<MassiveLoadUser>  list = this.getUserAll();
		if(list!=null)
		{
			for(MassiveLoadUser massList:list) 
			{
				Long count = sfMassiveLoad.eventListCtrlCountByIdMassLoad(massList.getId());
				if(count!=null && count<=0)
				{	
					if(massList.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode()))
						this.massiveLoadDelete(massList);
					else if(!massList.getStatus().equals(UtilCodesEnum.CODE_STATUS_MASSIVEEMPLE_TERMIANTE.getCode()))
					{	
						massList.setStatus(UtilCodesEnum.CODE_STATUS_MASSIVEEMPLE_TERMIANTE.getCode());
						massList.setLastUpdateOn(new Date());
						this.massiveLoadUpdate(massList);
						this.sendEmailResumeMassiveLoad(massList.getId());
					}
				}
			}
		}		
	}
	
	/**
	 * Load and Save/Update User
	 * @param MassiveLoadUser user
	 * @return MassiveloadUser
	 */
	public MassiveLoadUser userLoadSave(MassiveLoadUser user) 
	{
		try
		{
			user.setCreateUser(UserManager.getUserId());
			
			if(!(user.getNameLoad()!=null && !user.getNameLoad().equals(UtilCodesEnum.CODE_INVALID_OR_NONE.getCode())))					
				user.setNameLoad("MassiveLoad_"+(new Date()).getTime());

			user.setCreateOn(new Date());				
			return sfMassiveLoad.userLoadSave(user);
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();			
		}			
		
		return null;
	}
	
	
	/**
	 * @param List<MassiveLoadUser>
	 * @return List<MassiveLoadUser>
	 * */
	public List<MassiveLoadUser> updateCountTotalReg(List<MassiveLoadUser> listReturn)
	{
		if(listReturn!=null)
		{
			for(MassiveLoadUser massList:listReturn) 
			{
				Long count = this.eventListCtrlCountByIdMassLoad(massList.getId());
				if(count!=null){
					massList.setTotalReg(count);
				}
			}
		}
		
		return listReturn;
	}

	
	/**
	 *Get all MassiveLoadUser
	 *@return List<MassiveLoadUser>
	 */
	public List<MassiveLoadUser> getUserAll(){
		return sfMassiveLoad.getUserAll();
	}
	
	/**
	 * Update user massive
	 * @param MassiveLoadUser
	 * 
	 */
	public MassiveLoadUser massiveLoadUpdate (MassiveLoadUser user) {
		return sfMassiveLoad.userUpdateSave(user);		
	}
	
	/**
	 * delete user massive
	 * @parma MassiveLoadUser user
	 * */
	public void massiveLoadDelete(MassiveLoadUser user){
		try {
			sfMassiveLoad.massiveLoadDelete(user);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get time current future
	 * @param startDate
	 * @param isFutureDates
	 * @return
	 */
	public Collection<MassiveLoadUser> getAllUserTime(Date startDate, Boolean isFutureDates){
		return sfMassiveLoad.getAllUserTime(startDate, isFutureDates);
	}	
	
	/**
	 * Get number of eventlistener by massiveload
	 * @param Integer maxRegisterByPage
	 * @param String idMassiveLoad
	 * @return
	 */
	public Long eventListCtrlCountByIdMassLoad(Long idMassiveLoad) {
		return sfMassiveLoad.eventListCtrlCountByIdMassLoad(idMassiveLoad);
	}
	
	/**
	 * @param Load MassiveLoadUser
	 * @return MassiveloadUser
	 */
	public MassiveLoadUser userLoadGetById(Long idMass) {
		return sfMassiveLoad.userLoadGetById(idMass);
	}
	
	/**
	 * Build cvs body of log massive load
	 * @param Long idMassiveLoad
	 * @param FilterQueryDto filter
	 * @return ResponseGenericDto
	 * */
	public ResponseGenericDto downloadMassiveLoad(Long idMassiveLoad,FilterQueryDto filter)
	{		
		ResponseGenericDto response = new ResponseGenericDto();
		List<EventListenerCtrlProcess> listReturn = new ArrayList<>();
		listReturn = (List<EventListenerCtrlProcess>) successFactorAdmin.getEventListenerCtrlByPage(filter,idMassiveLoad);
		
		MassiveLoadUser massiveLoadUser = sfMassiveLoad.userLoadGetById(idMassiveLoad);
		response.setField(massiveLoadUser.getNameLoad());
		
		//add history register
		if(listReturn==null)
			listReturn = new ArrayList<>();
		
		listReturn.addAll(
		UtilMapping.loadEventListeCrtlFormHisto(successFactorAdmin.eventsListenerCtrlHistoGetAll(filter,idMassiveLoad),""));
		
		String observ = "";		
		String csvAttach = "";
		String csv ="MONITOR_TYPE"
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"ELEMENT"
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"ID_EVENT"
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"USER"
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"USER_COUNTRY"
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"STATUS"
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"FILE_NAME"
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"EFECTIVE_DATE"
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"CREATE_DATE"
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"LAST_UPDATE"					
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"RETRIES"
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"ATTACHMENTS"					
					+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"OBSERVATIONS"
					+UtilCodesEnum.CODE_NEW_LINE_SEPARATOR.getCode();

		if(listReturn !=null && !listReturn.isEmpty())
		{
			for(EventListenerCtrlProcess event: listReturn) 
			{
				try
				{
					ArrayList<EventListenerDocProcess> listAttach = new ArrayList<>();
					if(event.getEventLCtrlHistoryId()!=null && event.getEventLCtrlHistoryId()>0) {
						listAttach = UtilMapping.loadEventListenerDocFormHisto(successFactorAdmin.eventsListenerDocHistoGetAll(event.getEventLCtrlHistoryId(),null));
					} 
					else{
						listAttach = successFactorAdmin.eventsListenerDocProcGetAllByIdCtrl(event.getId());
					}
					
					csvAttach = "";
					observ = UtilMapping.toStringHtmlFormat(event.getObservations());
					observ = observ.replace(";","");
					observ = observ.replace("\n","");
					
					String country = event.getUserCountry()!=null?event.getUserCountry().replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()," "):"";
					
					csv += 	(event.getEventLCtrlHistoryId()!=null?"Event Listener History":"Event Listeners to process")
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"employee"
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+event.getId()		
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+event.getUserIdPpd()
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+country
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+event.getStatusLabel()
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+""
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+event.getStartDatePpdOnString()
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+event.getCreateOnString()
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+event.getLastUpdateOnString()
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+event.getRetries()
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+(listAttach!=null?listAttach.size():0)							
							+UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+observ
							+UtilCodesEnum.CODE_NEW_LINE_SEPARATOR.getCode();	
					
					//load attachments
					if(listAttach!=null)
					{
						for(EventListenerDocProcess item:listAttach)
						{
							observ = UtilMapping.toStringHtmlFormat(item.getObservations());
							observ = observ.replace(";","");
							observ = observ.replace("\n",""); 
							
							country = item.getUserCountry()!=null?item.getUserCountry().replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()," "):"";
							csvAttach += (event.getEventLCtrlHistoryId()!=null?"Event Listeners to process":"Event Listener History")
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"attach"
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+event.getId()		
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+item.getUserIdPpd()
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+country
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+item.getStatusLabel()
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+item.getAttachmentFileName()
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+item.getStartDatePpdOnString()
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+item.getCreateOnString()
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+item.getLastUpdateOnString()
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+item.getRetries()
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()										 
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+observ
										 +UtilCodesEnum.CODE_NEW_LINE_SEPARATOR.getCode();
						}
					}
					
					//add attachments
					csv += csvAttach;
					
				}catch (Exception e) {
					e.printStackTrace();
				}					
			}
		}
		
		
		response.setMsg(csv);
		return response;
	}
	
	/**
	 *  @param Long idMassiveLoad
	 *  @return ResponseGenericDto
	 * */
	public List<SlugItem> getStatisticsMassiveLoad(Long idMassiveLoad)
	{
		List<SlugItem> responseList = new ArrayList<SlugItem>();
		List<Object[]> statusCounter = successFactorAdmin.eventListenerGetStatusCount(new FilterQueryDto(),idMassiveLoad);
		List<Object[]> statusCounterHisto = successFactorAdmin.eventListenerHistoGetStatusCount(new FilterQueryDto(),idMassiveLoad);
		statusCounter = successFactorAdmin.unionAllCountersEventListener(statusCounter,statusCounterHisto);
		
		for(Object[] item:statusCounter)
		{
			SlugItem newItem = new SlugItem();
			newItem.setValue(item[0].toString());
			newItem.setLabel(item[1].toString());
			responseList.add(newItem);
		}
		
		return responseList;
	}
	
	/**
	 * send email to user
	 * @param Long idMassiveLoad
	 * @param String emailToSend	  
	 * @return ResponseGenericDto response
	 * */
	public ResponseGenericDto sendEmailResumeMassiveLoad(Long idMassiveLoad)
	{
		String urlDonwload = "";
		ResponseGenericDto response = new ResponseGenericDto();
		
		try
		{
			AdminParameters paramEmail = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_NOTIFICATIONS.getCode());
			AdminParameters paramEmailSubject = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MASSIVE_EMAIL_SUBJECT.getCode());
			AdminParameters paramUrlDownload = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MASSIVE_DOWNLOAD_URL.getCode());
	
			if(paramUrlDownload!=null && paramUrlDownload.getValue()!=null)
				urlDonwload = paramUrlDownload.getValue()+"/admin/massivedownload/"+idMassiveLoad+"/";
			
			
			if(paramEmail!=null && !paramEmail.getValue().equals(""))
			{
				Email emailImpl = new EmailImpl();
				String [] emails = paramEmail.getValue().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode());
				
				if(emails!=null)
				{
					//load massive load
					MassiveLoadUser massiveLoadUser = sfMassiveLoad.userLoadGetById(idMassiveLoad);
					List<Object[]> statusCounter = successFactorAdmin.eventListenerGetStatusCount(new FilterQueryDto(),idMassiveLoad);
					List<Object[]> statusCounterHisto = successFactorAdmin.eventListenerHistoGetStatusCount(new FilterQueryDto(),idMassiveLoad);
					statusCounter = successFactorAdmin.unionAllCountersEventListener(statusCounter,statusCounterHisto);
					
					//load info massive 
					String massiveBody = UtilMapping.getHtmlTableCode("Massive Load Report","", "tr_title")+
										 UtilMapping.getHtmlTableCode("Date Report:", UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(),new Date()), "tr_info")+
										 UtilMapping.getHtmlTableCode("Id:",massiveLoadUser.getId().toString(), "tr_info")+
										 UtilMapping.getHtmlTableCode("Name:",massiveLoadUser.getNameLoad(), "tr_info")+
										 UtilMapping.getHtmlTableCode("Status",massiveLoadUser.getStatusLabel(), "tr_info")+
										 UtilMapping.getHtmlTableCode("Attachment",UtilMapping.getLabelsAttachMassiveLoad(massiveLoadUser.getAttachTypes()), "tr_info")+
										 
										 UtilMapping.getHtmlTableCode("Create Date",massiveLoadUser.getCreateOnString(), "tr_info")+
										 UtilMapping.getHtmlTableCode("LastUpdate",massiveLoadUser.getLastUpdateOnString(), "tr_info");
					
					massiveBody += 		 UtilMapping.getHtmlTableCode("Global Statistics","", "tr_title")+
										 UtilMapping.loadHtmlRow("Total",statusCounter)+
										 UtilMapping.loadHtmlRow("Pending Records",statusCounter);
					
					//get title and body
					String errorBody = UtilMapping.loadHtmlRow(UtilMapping.getListStatusForGroup(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERROR.getCode()),statusCounter);
					if(!errorBody.equals(""))
						errorBody = UtilMapping.getHtmlTableCode(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERROR.getLabel(),"","tr_title")+errorBody;
										
					String errorPpd = UtilMapping.loadHtmlRow(UtilMapping.getListStatusForGroup(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERRORPPD.getCode()),statusCounter);
					if(!errorPpd.equals(""))
						errorPpd = UtilMapping.getHtmlTableCode(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERRORPPD.getLabel(),"","tr_title")+ errorPpd;
					
					String errorPending = UtilMapping.loadHtmlRow(UtilMapping.getListStatusForGroup(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_PENDING.getCode()),statusCounter);
					if(!errorPending.equals(""))
						errorPending = UtilMapping.getHtmlTableCode(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_PENDING.getLabel(),"","tr_title")+ errorPending;
					
					String errorSuccess = UtilMapping.loadHtmlRow(UtilMapping.getListStatusForGroup(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_SUCCESS.getCode()),statusCounter);
					if(!errorSuccess.equals(""))
						errorSuccess = UtilMapping.getHtmlTableCode(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_SUCCESS.getLabel(),"","tr_title") + errorSuccess;
								
					String report = "";
					ArrayList<String> reportList = new ArrayList<>();
					
					for(String emailConf:emails)
					{
						if(emailConf!=null && !emailConf.equals(""))
						{
							if(emailConf.contains(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()))
							{
								reportList = new ArrayList<>();
								report = "";
								
								if(emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1].toString()
										.equals(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ALL.getCode())){
									report = errorBody+errorPpd+errorPending+errorSuccess;
								}
								else if(!errorBody.equals("") && emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1].toString()
										.equals(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERROR.getCode())){
									report = errorBody;
								}
								else if(!errorPpd.equals("") && emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1].toString()
										.equals(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERRORPPD.getCode())){
									report = errorPpd;
								}
								else if(!errorPending.equals("") && emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1].toString()
										.equals(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_PENDING.getCode())){
									report = errorPending;
								}
								else if(!errorSuccess.equals("") && emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1].toString()
										.equals(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_SUCCESS.getCode())){
									report = errorSuccess;
								}
								
								reportList.add(massiveBody+report); 
								emailImpl.sendEmailResumeMassive(UtilCodesEnum.CODE_DEFAULT_FROM_NOTI_EMAIL.getCode(),
												emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[0].toString(),
												paramEmailSubject!=null&&!paramEmailSubject.getValue().equals("")?
												paramEmailSubject.getValue():UtilCodesEnum.CODE_MASSIVE_EMAIL_SUBJECT.getCode()+" "+massiveLoadUser.getNameLoad(),
												!urlDonwload.equals("")?urlDonwload+emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[0].toString():"",
												reportList);
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
}
