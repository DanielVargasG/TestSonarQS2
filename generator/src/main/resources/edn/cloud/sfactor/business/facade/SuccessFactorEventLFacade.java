package edn.cloud.sfactor.business.facade;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.ser.StdSerializers.UtilDateSerializer;

import ch.qos.logback.classic.pattern.Util;
import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.GenErrorInfoDto;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.business.dto.ppd.api.PpdElectronicVaultOptionsDto;
import edn.cloud.business.dto.ppd.api.PpdEmployeeInfo_v1Dto;
import edn.cloud.business.dto.psf.PSFStructureEmployeeDto;
import edn.cloud.ppdoc.business.facade.PpdApiUtilsFacade;
import edn.cloud.ppdoc.business.facade.PpdEmployeeApiFacade;
import edn.cloud.ppdoc.business.facade.PpdEmployeeUtilFacade;
import edn.cloud.sfactor.business.impl.EmailImpl;
import edn.cloud.sfactor.business.interfaces.Email;
import edn.cloud.sfactor.persistence.dao.QueryOdataDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Countries;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerParam;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;
import edn.cloud.sfactor.persistence.entities.MassiveLoadUser;

public class SuccessFactorEventLFacade {
	private UtilLogger logger = UtilLogger.getInstance();
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	private SuccesFactorUserFacade successFactorUserFacade = new SuccesFactorUserFacade();
	private SuccessFactorFacade successFactorUtils = new SuccessFactorFacade();
	private SuccessFactorAttachFacade successFactorAttachFacade = new SuccessFactorAttachFacade();
	private PpdEmployeeApiFacade ppdEmployeeF = new PpdEmployeeApiFacade();
	private PpdApiUtilsFacade ppdApiUtilsF = new PpdApiUtilsFacade();

	public SuccessFactorEventLFacade() {
		super();
	}

	/**
	 * @param Long
	 *            idLoggerJob
	 * @param ArrayList<EventListenerCtrlProcess>
	 *            eventListenerPendList
	 */
	public void eventListenerActionProcessQuartz(Long idLoggerJob) {
		// Load Admin parameters
		// get maximun number of records to process		
		Integer p_numMaxReg = null;
		AdminParameters paramAdminCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_EVENT.getCode());
		if (paramAdminCode != null && paramAdminCode.getValue() != null) {
			p_numMaxReg = Integer.parseInt(paramAdminCode.getValue());
		}

		// get wait time maximum to process register
		Integer p_timeMax = null;
		paramAdminCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_WAITIME.getCode());
		if (paramAdminCode != null && paramAdminCode.getValue() != null) {
			p_timeMax = Integer.parseInt(paramAdminCode.getValue());
		}

		Date date = new Date();

		// status to load and process
		String statusToLoad = "'" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode() + "'," + "'" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode() + "'," + "'" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode()	+ "'," + "'" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode() + "'," + "'" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode() + "'," + "'" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode() + "'," + "'"
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode() + "','" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING_VALIDATE.getCode() + "','" 
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_USERPENDINGMAIL.getCode() + "','"
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING_PROCCESS_AGAIN.getCode() + "','"				
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode() + "' ";

		// search register to proccess
		ArrayList<EventListenerCtrlProcess> eventListenerPendList = successFactorAdmin.eventsListenerCtrlProcGetAll(date, statusToLoad, p_numMaxReg, false, null, null, null);

		// register quartz event
		successFactorAdmin.eventListenerRegisterEventQuarzt(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_EVENTLIST.getCode(),
				UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), date) + " " + "number of events: " + (eventListenerPendList != null ? eventListenerPendList.size() : 0));

		if(eventListenerPendList!=null && eventListenerPendList.size()>0)
			this.processEventListener(idLoggerJob, eventListenerPendList);

		// update proccess pending to timeout
		// todo algo para con la tx
		this.actionCtrlUpdateEventsTimeOut(p_timeMax);
		
		sendEmailResumeEvent(true);
	}

	/**
	 * 
	 * 
	 * process call from EventListenerjob quartz
	 * 
	 * @param Long idLoggerJob
	 * @param ArrayList<EventListenerCtrlProcess> eventListenerPendList
	 * @param Long idMassiveLoad
	 */
	public void processEventListener(Long idLoggerJob, ArrayList<EventListenerCtrlProcess> eventListenerPendList) {
		Date date = new Date();
		String dateUpdateCreate = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(), new Date()) + "T" + UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_HOUR.getCode(), new Date());
		Boolean isCreateUser = Boolean.FALSE;

		// -----------------------------------
		// get Status to limit number of retries
		AdminParameters paramAdminRetriesCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STATUS_LIMIT_RETRIES.getCode());

		// get max number of retries
		AdminParameters paramAdminMaxRetriesCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_RETRIES.getCode());

		// get max number of retries
		AdminParameters paramAdminEmailFormatTermi = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_FORMAT_MAILUSERTERM_PPD.getCode());

		// get max number of retries
		AdminParameters paramAdminUpdateCreateUser = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_CALL_UPDATEUSER_AFTER_CREATE.getCode());

		// process user no email
		AdminParameters paramProcessUserNoEmail = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_PROCESS_USERNOMAIL.getCode());

		//Check if pattern exist
		AdminParameters paramPatternEmail = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_FORMAT_MAILUSERTERM_PPD.getCode());
		
		// manager role ppd
		AdminParameters paramManagerRole = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_MANAGER_ROLE_PPD.getCode());

		// manager organization ppd
		AdminParameters paramManagerOrga = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_MANAGER_ORGANIZATION_PPD.getCode());
		
		// prefix name to create user in ppd
		AdminParameters paramPrefixUserPpd = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_PREFIX_USER_CREATE_PPD.getCode());

		// define format to filename
		AdminParameters paramFormatNameFile = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_PATTERN_FILENAME_SEND_PPD.getCode());
		
		//show log ppd
		AdminParameters paramShowResponsePpd = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_LOG_SHOW_RESPONSE_PPD_EMPL.getCode());
		
		// -------------------------------------

		if (eventListenerPendList != null && eventListenerPendList.size() > 0) {
			PpdEmployeeUtilFacade ppdUserUtilF = new PpdEmployeeUtilFacade();

			ArrayList<FieldsMappingPpd> fieldsUserList = successFactorAdmin.mappingPpdFieldsGetAll(null, UtilCodesEnum.CODE_NA.getCode(), Boolean.TRUE, Boolean.FALSE);
			AdminParameters paramAdmStructure = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STRUCTURE_KEY.getCode());

			String concReturn = UtilCodesEnum.CODE_FORMAT_STRUCTURE_USER.getCode();
			AdminParameters paramAdmUserid = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_USERID.getCode());
			if (paramAdmUserid != null) {
				concReturn = paramAdmUserid.getValue();
			}

			// load countries or structure filters
			List<Countries> listCountriesConf = successFactorAdmin.countriesGetAll();

			// -----------------------------------------------------
			try 
			{
				for (EventListenerCtrlProcess event : eventListenerPendList) 
				{
					PpdCoreEmployeeInfoDto employeeUpdate = new PpdCoreEmployeeInfoDto();

					//update init event process
					event.setLastUpdateInit(new Date());
					
					// update id logger job in event ctrl
					event.setIdJobProcess(idLoggerJob);
					successFactorAdmin.eventListenerCtrlUpdateIdJobProcess(event.getId(), idLoggerJob);

					// update count of retries
					event.setRetries(event.getRetries() + actionGetRetries(paramAdminRetriesCode, event.getStatus()));

					// load all attachments associated to event listener without filters
					ArrayList<EventListenerDocProcess> listAttach = successFactorAdmin.eventsListenerDocProcGetAll(event.getId(), null, null, null, null, null);

					
					// -------------------------------------------------------------
					// -------------------------------------------------------------
					// Evaluate status

					// user pending for validate
					if (event != null && event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING_VALIDATE.getCode())) {
						event = this.validateEmployee(event.getUserIdPpd(), event, date, paramAdmUserid, listCountriesConf, paramProcessUserNoEmail,paramPatternEmail, event.getIsSearchAttachAfterProc());
						if (event != null && event.getId() != null && event.getId() > 0) {
							event.setLastUpdateOn(new Date());
							successFactorAdmin.eventListenerCtrlProcessUpdate(event);
						}
					}
					// user pending by mail
					else if (event != null && event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_USERPENDINGMAIL.getCode())) 
					{
						// evaluate hold time
						if (actionEvaluateIsTimeUserWhitouMail(event.getCreateOn())) 
						{
							if (!this.actionEvaluateUserWhitoutMail(event.getUserIdPpd())) {
								// load documents recruitment
								event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_SEARCHDOCS.getCode());
								event.setLastUpdateOn(date);
								successFactorAdmin.eventListenerCtrlProcessUpdate(event);

								successFactorAttachFacade.attachmentGetSaveTemplate(event, event.getUserIdPpd());
								
								if (paramProcessUserNoEmail != null && paramProcessUserNoEmail.getValue() != null && paramProcessUserNoEmail.getValue().equals(UtilCodesEnum.CODE_TRUE.getCode())) 
									successFactorAttachFacade.attachmentSearchModuleRC_ONB(event, true);
								else
									successFactorAttachFacade.attachmentSearchModuleRC_ONB(event, false);

								event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());
								event.setLastUpdateOn(date);
								successFactorAdmin.eventListenerCtrlProcessUpdate(event);
							} else {
								// terminate user
								event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode());
								event.setLastUpdateOn(date);
								event.setObservations("user without mail, exceeded the waiting time for mail generation");
								successFactorAdmin.eventListenerCtrlProcessUpdate(event);

								// finish process in ctrl event listener
								eventListFinishProcess(paramProcessUserNoEmail, employeeUpdate, listAttach, event);
							}
						}

					} else if (event != null && event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode())) {
						// finish process in ctrl event listener
						eventListFinishProcess(paramProcessUserNoEmail, employeeUpdate, listAttach, event);
					} else if (event != null && event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode())) {

						// finish process in ctrl event listener
						eventListFinishProcess(paramProcessUserNoEmail, employeeUpdate, listAttach, event);
					} else if (paramAdminMaxRetriesCode != null && paramAdminMaxRetriesCode.getValue() != null && event != null && event.getRetries() >= Integer.parseInt(paramAdminMaxRetriesCode.getValue())) {
						// change status of event
						event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode());
						event.setLastUpdateOn(date);
						successFactorAdmin.eventListenerCtrlProcessUpdate(event);

						// finish process in ctrl event listener
						eventListFinishProcess(paramProcessUserNoEmail, employeeUpdate, listAttach, event);
					}
					else if (event != null && event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode())) {
						// change status of event
						event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode());
						event.setLastUpdateOn(date);
						successFactorAdmin.eventListenerCtrlProcessUpdate(event);

						// process document of Onboarding v2
						successFactorAttachFacade.actionProcessAttachEventListSendONBV2Quartz(idLoggerJob, event.getId(), paramFormatNameFile, event.getUserIdPpd());

						// load all attachments in local associated to event listener without filters
						listAttach = successFactorAdmin.eventsListenerDocProcGetAll(event.getId(), null, null, null, null, null);

						// finish process in ctrl event listener
						eventListFinishProcess(paramProcessUserNoEmail, employeeUpdate, listAttach, event);
					} 
					else if (event != null && event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING_PROCCESS_AGAIN.getCode())) 
					{
						// change status of event
						event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode());
						event.setLastUpdateOn(date);
						successFactorAdmin.eventListenerCtrlProcessUpdate(event);
						
						//empty attachment
						if(!(listAttach!=null && listAttach.size()>0))
						{
							// process document of Onboarding v2
							successFactorAttachFacade.actionProcessAttachEventListSendONBV2Quartz(idLoggerJob, event.getId(), paramFormatNameFile, event.getUserIdPpd());
	
							// search document in Onboarding v1 and Recruiting
							if (paramProcessUserNoEmail != null && paramProcessUserNoEmail.getValue() != null && paramProcessUserNoEmail.getValue().equals(UtilCodesEnum.CODE_TRUE.getCode()))
								successFactorAttachFacade.attachmentSearchModuleRC_ONB(event, true);
							else
								successFactorAttachFacade.attachmentSearchModuleRC_ONB(event, false);
						}
						
						// load all attachments in local associated to event listener without filters
						listAttach = successFactorAdmin.eventsListenerDocProcGetAll(event.getId(), null, null, null, null, null);
						
						// finish process in ctrl event listener
						eventListFinishProcess(paramProcessUserNoEmail, employeeUpdate, listAttach, event);						
					}
					else 
					{
						// ---------------------------------------------------------------------
						// evaluate time zone id
						edn.cloud.business.dto.GenErrorInfoDto countryResponse = successFactorUserFacade.employeeGetTimeZone(event.getUserIdPpd(), UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd", date), listCountriesConf, concReturn, false);
						Date dateZone = UtilDateTimeAdapter.getCurrentDateFromZoneDateTime(((countryResponse.getCode() == null || countryResponse.getCode().equals("") || countryResponse.getCode().equals(" ")) ? "UTC" : countryResponse.getCode()));
						if ((dateZone != null && event.getStartDatePpdOn().compareTo(dateZone) <= 0) || (event.getEventListenerParam().getIsDateinstant() != null && event.getEventListenerParam().getIsDateinstant())) 
						{
							if (event != null && event.getEventListenerParam().getIsEnabled()) 
							{
								// init actions to execute
								List<SlugItem> actionsList = new ArrayList<SlugItem>();

								// search employee
								isCreateUser = true;
								PpdCoreEmployeeInfoDto[] coreEmplSearch = ppdApiUtilsF.wServiceSearchEmployee(event.getUserIdPpd());
								if (coreEmplSearch != null && coreEmplSearch.length > 0) {
									isCreateUser = false;
								}

								// change status of event
								event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode());
								event.setLastUpdateOn(date);
								successFactorAdmin.eventListenerCtrlProcessUpdate(event);

								// ----------------------------------------------------------------------
								// ONLY update user
								if (event.getEventListenerParam().getIsUpdate() && event.getEventListenerParam().getIsHire() == false && event.getEventListenerParam().getIsTerminate() == false) 
								{
									// get values for employee to update in people doc
									employeeUpdate = ppdUserUtilF.actionUserGetValueQueryBuilder(event.getUserIdPpd(), fieldsUserList, paramAdmStructure, paramAdmUserid, false, event.getStartDatePpdOnString());
									
									
									actionsList = employeeUpdate.getActions();

									employeeUpdate.setTerminated(false);
									employeeUpdate.setUpdated_at(dateUpdateCreate);
									employeeUpdate.setCreated_at(dateUpdateCreate);

									if (coreEmplSearch != null && coreEmplSearch.length > 0) {
										employeeUpdate.setId(coreEmplSearch[0].getId());
										employeeUpdate.setCreated_at(coreEmplSearch[0].getCreated_at());
									}

									if (!isCreateUser) {
										// update information employee in success factor
										employeeUpdate = ppdEmployeeF.actionUpdateEmployee(employeeUpdate);
									} else {
										// create employee in success factor
										employeeUpdate = ppdEmployeeF.actionCreateEmployee(employeeUpdate);

										if (employeeUpdate != null && employeeUpdate.getErrors() == null || (employeeUpdate.getErrors() != null && employeeUpdate.getErrors().length <= 0)) {
											String idEmplPPD = employeeUpdate.getId();

											// get values for employee to update in people doc
											employeeUpdate = ppdUserUtilF.actionUserGetValueQueryBuilder(event.getUserIdPpd(), fieldsUserList, paramAdmStructure, paramAdmUserid, true, event.getStartDatePpdOnString());
											employeeUpdate.setUpdated_at(dateUpdateCreate);
											employeeUpdate.setCreated_at(dateUpdateCreate);

											if (idEmplPPD != null) {
												employeeUpdate.setId(idEmplPPD);
											}

											// update information employee in success factor
											employeeUpdate = ppdEmployeeF.actionUpdateEmployee(employeeUpdate);
										}
									}
								}

								// ------------------------------------------------------------------------
								// hire user
								if (event.getEventListenerParam().getIsHire()) {
									// get values for employee to update in people doc
									employeeUpdate = ppdUserUtilF.actionUserGetValueQueryBuilder(event.getUserIdPpd(), fieldsUserList, paramAdmStructure, paramAdmUserid, true, event.getStartDatePpdOnString());
									employeeUpdate.setTerminated(false);
									employeeUpdate.setUpdated_at(dateUpdateCreate);
									employeeUpdate.setCreated_at(dateUpdateCreate);
									actionsList = employeeUpdate.getActions();
									
									if (coreEmplSearch != null && coreEmplSearch.length > 0) {
										employeeUpdate.setId(coreEmplSearch[0].getId());
										employeeUpdate.setCreated_at(coreEmplSearch[0].getCreated_at());
									}

									if (!isCreateUser) {
										// update information employee in success factor
										employeeUpdate = ppdEmployeeF.actionUpdateEmployee(employeeUpdate);
									} else {										
										// create employee in success factor
										employeeUpdate = ppdEmployeeF.actionCreateEmployee(employeeUpdate);

										if (employeeUpdate != null && employeeUpdate.getErrors() == null || (employeeUpdate.getErrors() != null && employeeUpdate.getErrors().length <= 0)) {
											String idEmplPPD = employeeUpdate.getId();

											// call update employee after create
											if (paramAdminUpdateCreateUser == null || (paramAdminUpdateCreateUser != null && paramAdminUpdateCreateUser.getValue() != null && paramAdminUpdateCreateUser.getValue().equals(""))
													|| (paramAdminUpdateCreateUser != null && paramAdminUpdateCreateUser.getValue().equals(UtilCodesEnum.CODE_TRUE.getCode()))) {
												// get values for employee to update in people doc
												employeeUpdate = ppdUserUtilF.actionUserGetValueQueryBuilder(event.getUserIdPpd(), fieldsUserList, paramAdmStructure, paramAdmUserid, true, event.getStartDatePpdOnString());
												employeeUpdate.setUpdated_at(dateUpdateCreate);
												employeeUpdate.setCreated_at(dateUpdateCreate);

												if (idEmplPPD != null) {
													employeeUpdate.setId(idEmplPPD);
												}
												
												// update information employee in success factor
												employeeUpdate = ppdEmployeeF.actionUpdateEmployee(employeeUpdate);
											}
										}
									}
								}

								// ------------------------------------------------------------------------
								// terminate user
								if (event.getEventListenerParam().getIsTerminate()) {
									// get values for employee to update in people doc
									employeeUpdate = ppdUserUtilF.actionUserGetValueQueryBuilder(event.getUserIdPpd(), fieldsUserList, paramAdmStructure, paramAdmUserid, true, event.getStartDatePpdOnString());
									employeeUpdate.setTerminated(true);
									employeeUpdate.setUpdated_at(dateUpdateCreate);
									employeeUpdate.setCreated_at(dateUpdateCreate);
									actionsList = employeeUpdate.getActions();

									if (coreEmplSearch != null && coreEmplSearch.length > 0) {
										employeeUpdate.setId(coreEmplSearch[0].getId());
										employeeUpdate.setCreated_at(coreEmplSearch[0].getCreated_at());
									}

									if (!isCreateUser) {
										// if parameter exist update mail for user
										if (paramAdminEmailFormatTermi != null && paramAdminEmailFormatTermi.getValue() != null && !paramAdminEmailFormatTermi.getValue().equals("")) {
											String newMail = UtilMapping.setStringInPattern(paramAdminEmailFormatTermi.getValue(), event.getUserIdPpd(), UtilCodesEnum.CODE_PATRON_MAILUDPATE.getCode());
											if (!newMail.equals(""))
												employeeUpdate.setEmail(newMail);
										}

										// update information employee in success factor
										employeeUpdate = ppdEmployeeF.actionUpdateEmployee(employeeUpdate);
									} else {
										// create employee in success factor
										employeeUpdate = ppdEmployeeF.actionCreateEmployee(employeeUpdate);

										if (employeeUpdate != null && employeeUpdate.getErrors() == null || (employeeUpdate.getErrors() != null && employeeUpdate.getErrors().length <= 0)) {
											String idEmplPPD = employeeUpdate.getId();

											// call update employee after create
											if (paramAdminUpdateCreateUser == null || (paramAdminUpdateCreateUser != null && paramAdminUpdateCreateUser.getValue() != null && paramAdminUpdateCreateUser.getValue().equals(""))
													|| (paramAdminUpdateCreateUser != null && paramAdminUpdateCreateUser.getValue().equals(UtilCodesEnum.CODE_TRUE.getCode()))) {
												// get values for employee to update in people doc
												employeeUpdate = ppdUserUtilF.actionUserGetValueQueryBuilder(event.getUserIdPpd(), fieldsUserList, paramAdmStructure, paramAdmUserid, true, event.getStartDatePpdOnString());
												employeeUpdate.setUpdated_at(dateUpdateCreate);
												employeeUpdate.setCreated_at(dateUpdateCreate);

												if (idEmplPPD != null) {
													employeeUpdate.setId(idEmplPPD);
												}

												// update information employee in success factor
												employeeUpdate = ppdEmployeeF.actionUpdateEmployee(employeeUpdate);
											}
										}
									}
								}

								// -----------------------------------------------------------
								// manager
								if (event.getEventListenerParam().getIsIndiContrToManager() || event.getEventListenerParam().getIsManagerToIndiContr()) {
									if (paramManagerRole != null && paramManagerRole.getValue() != null && !paramManagerRole.getValue().equals("")
											&& paramManagerRole.getValue().contains(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())
											&& paramManagerRole.getValue().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()).length > 1 && paramManagerOrga != null && paramManagerOrga.getValue() != null
											&& !paramManagerOrga.getValue().equals("")) 
									{	
										ResponseGenericDto responseUser = successFactorUserFacade.userPpdUpdate(event.getUserIdPpd(), 
												event.getEventListenerParam().getIsIndiContrToManager(),
												event.getEventListenerParam().getIsManagerToIndiContr(), 
												paramManagerRole.getValue().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1], 
												paramManagerOrga.getValue(),
												paramPrefixUserPpd!=null&&paramPrefixUserPpd.getValue()!=null?paramPrefixUserPpd.getValue():UtilCodesEnum.CODE_TYPE_DEFAULT_PREFIX_USER.getCode(),
												paramManagerRole.getValue().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[0]);
										
										if (responseUser != null) 
										{
											if (responseUser.getCode().equals(UtilCodesEnum.CODE_OK.getCode())) 
											{
												employeeUpdate.setObservations(responseUser.getMessage());
											}
											else if (responseUser.getCode().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode())) 
											{
												employeeUpdate = new PpdCoreEmployeeInfoDto();
												GenResponseInfoDto[] errorList = new GenResponseInfoDto[1];
												GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
												ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
												ppdErrorInfoDto.setField(responseUser.getMessage());
												errorList[0] = ppdErrorInfoDto;
												employeeUpdate.setErrors(errorList);
											}
										}
									} else {
										employeeUpdate = new PpdCoreEmployeeInfoDto();
										GenResponseInfoDto[] errorList = new GenResponseInfoDto[1];
										GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
										ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
										ppdErrorInfoDto.setField("Parameters Role, Orga PPd is not configured");
										errorList[0] = ppdErrorInfoDto;
										employeeUpdate.setErrors(errorList);
									}
								}
								logger.info("WE ARE HERE");
								// -----------------------------------------------------------
								// update electronic valut options
								GenResponseInfoDto responseElecVault = actionUpdateElectronicVault(event.getUserIdPpd(), event.getUserCountry());
								if (responseElecVault != null) {
									employeeUpdate.setObservations(employeeUpdate.getObservations() + " status update electronic vault : " + responseElecVault.getMessage());
								}

								// flag is new employee
								employeeUpdate.setObservations(" Employee is New : " + isCreateUser.toString() + ". " + employeeUpdate.getObservations());

								// execute actions asociated, update info user manager,hr
								if (employeeUpdate != null && employeeUpdate.getErrors() == null || (employeeUpdate.getErrors() != null && employeeUpdate.getErrors().length <= 0)) 
								{
									//validate if load response ppd
									if(!(paramShowResponsePpd!=null && paramShowResponsePpd.getValue()!=null && paramShowResponsePpd.getValue().equals(UtilCodesEnum.CODE_TRUE.getCode()) )) {
										employeeUpdate.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode()+" Action Completed In PPD / Employee ");
									}
																		
									employeeUpdate.setObservations( 
														this.actionExecuteActions(
																actionsList, 
																paramManagerOrga,
																paramPrefixUserPpd!=null&&paramPrefixUserPpd.getValue()!=null?paramPrefixUserPpd.getValue():UtilCodesEnum.CODE_TYPE_DEFAULT_PREFIX_USER.getCode()
																)
														+" "+employeeUpdate.getObservations()
													);
								}
								else
								{
									employeeUpdate.setObservations(" Evaluate Actions : false, pre-errors. " + employeeUpdate.getObservations());
								}

								// finish process in ctrl event listener
								eventListFinishProcess(paramProcessUserNoEmail, employeeUpdate, listAttach, event);
							} else {
								PpdEmployeeInfo_v1Dto employeeError = new PpdEmployeeInfo_v1Dto();
								GenResponseInfoDto[] errorList = new GenResponseInfoDto[1];

								GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
								ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
								ppdErrorInfoDto.setField("Event Listener is disabled");

								errorList[0] = ppdErrorInfoDto;
								employeeError.setErrors(errorList);

								// finish process in ctrl event listener
								eventListFinishProcess(paramProcessUserNoEmail, employeeError, null, event);
							}
						} else {
							if (dateZone != null) {
								event.setObservations("compare starDate vs TimeZone: " + event.getStartDatePpdOnString() + " vs " + dateZone.toString());
							} else {
								event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
								event.setObservations("Time Zone not found");
							}

							successFactorAdmin.eventListenerCtrlProcessUpdate(event);
						}
					}

					// update/reset id logger job in event ctrl
					successFactorAdmin.eventListenerCtrlUpdateIdJobProcess(event.getId(), null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				successFactorUtils.saveLoggerControl("400", "Error: " + e.getMessage(), "adminmt", "Error for/loop");
			}
		} else {
			logger.info("No Execute job Events to process");
		}
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Finish status ctrl event listener
	 * 
	 * @param AdminParameters
	 *            paramProcessUserNoEmail
	 * @param PpdEmployeeInfo_v1Dto
	 *            employeeUpdate
	 * @param ArrayList<EventListenerDocProcess>
	 *            attachList
	 * @param EventListenerCtrlProcess
	 *            event
	 */
	private boolean eventListFinishProcess(AdminParameters paramProcessUserNoEmail, PpdEmployeeInfo_v1Dto employeeUpdate, ArrayList<EventListenerDocProcess> attachList, EventListenerCtrlProcess event) {
		try 
		{
			Date date = new Date();			
			boolean attachWithError = false;
			String dateSeconds = "Time Execution " + UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);

			if (employeeUpdate != null && employeeUpdate.getErrors() == null || (employeeUpdate.getErrors() != null && employeeUpdate.getErrors().length <= 0)) 
			{
				// ------------------------------------------
				if (!event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode()) && !event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode())) {
					// evaluate exist attachments

					if(event.getFkMassiveLoad()!=null && event.getFkMassiveLoad().getAttachTypes()!=null 
							&& event.getFkMassiveLoad().getAttachTypes().equals(UtilCodesEnum.CODE_NA.getCode()) && !(attachList != null && attachList.size() > 0)) 
					{
						//attachment types is empty
						event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
					}
					else if (event.getIsSearchAttachAfterProc() && !(attachList != null && attachList.size() > 0)) {
						event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode());

						// search attachment in all platform
						ResponseGenericDto responseSearch = this.actionSearchAttachEventList(event.getUserIdPpd(), paramProcessUserNoEmail, event);

						if (responseSearch != null && responseSearch.getMessage() != null) {
							event.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + dateSeconds + "\n" + (event.getIdJobProcess() != null ? " idJob: " + event.getIdJobProcess() + ". " : "") + " search attachment after processed: "
									+ responseSearch.getMessage() + UtilCodesEnum.CODE_STRING_END.getCode() + " " + event.getObservations() + " ");
						} else {
							event.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + dateSeconds + "\n" + (event.getIdJobProcess() != null ? " idJob: " + event.getIdJobProcess() + ". " : "")
									+ " search attachment after processed: yes. " + UtilCodesEnum.CODE_STRING_END.getCode() + " " + event.getObservations() + " ");
						}

						event.setLastUpdateOn(new Date());
						event.setIsSearchAttachAfterProc(false);
						successFactorAdmin.eventListenerCtrlProcessUpdate(event);
						return false;
					} else if (attachList != null && attachList.size() > 0) 
					{
						int cont = 0;
						attachWithError = false;
						event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode());

						for (EventListenerDocProcess doc : attachList) 
						{
							if (!doc.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode()) && 
								!doc.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode())) 
							{
								if(doc.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode()) || 
								   doc.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode()) || 
								   doc.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTESENDTOSIGN.getCode()) || 
								   doc.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEDOCCREATE.getCode()))
									cont++;
								else 
								{
									if (employeeUpdate.getObservations() != null && !employeeUpdate.getObservations().equals("")) {
										event.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + dateSeconds + "\n" + (!employeeUpdate.getObservations().equals("") ? employeeUpdate.getObservations() : "")
												+ UtilCodesEnum.CODE_STRING_END.getCode() + " " + event.getObservations());
									}

									event.setLastUpdateOn(new Date());
									successFactorAdmin.eventListenerCtrlProcessUpdate(event);
									return false;
								}
							} else 
							{
								attachWithError = true;
								cont++;
							}
						}

						if (cont < attachList.size()) {
							event.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + dateSeconds + "\n" + 
									(event.getIdJobProcess() != null ? " idJob: " + event.getIdJobProcess() + ". " : "") + 
									employeeUpdate.getObservations()+
									UtilCodesEnum.CODE_STRING_END.getCode() + " " + 
									event.getObservations() + " ");

							event.setLastUpdateOn(new Date());
							successFactorAdmin.eventListenerCtrlProcessUpdate(event);
							return false;
						}
					}
				}
				
				//------------------------------------------------------
				//------------------------------------------------------
				
				//update status of event 
				if(attachWithError){
					event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMINATE_EMP_ERROR_ATTACH.getCode());
				}
				
				//delete attachment through JPA
				if(attachList!=null && attachList.size()>0) {
					for(EventListenerDocProcess docAttach : attachList){
						try{
							successFactorAdmin.eventListenerDocProcessDelete(docAttach);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				// delete attach
				successFactorAdmin.eventListenerDocDeleteByIdEventCtrl(event.getId());
				
				//delete event listener Ctrl, validate first if contains documents
				ArrayList<EventListenerDocProcess> listAttach = successFactorAdmin.eventListenerDocGetByFilter(event.getId(), null);				
				if(listAttach!=null && listAttach.size()>0)
				{
					event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode());
					event.setObservations(
							UtilCodesEnum.CODE_STRING_INIT.getCode() + dateSeconds + "\n" 
							+ "It tries to eliminate the event but it was not possible, it still has attachments"
							+ UtilCodesEnum.CODE_STRING_END.getCode() + " " + event.getObservations());
					
					event.setLastUpdateOn(new Date());
					successFactorAdmin.eventListenerCtrlProcessUpdate(event);
					successFactorUtils.saveLoggerControl("400", "It tries to eliminate the event but it was not possible, it still has attachments ", UtilCodesEnum.CODE_JOB_EVENT_LIST.getCode(), "Error");
				}
				else
				{
					// delete register for event to process
					successFactorAdmin.eventListenerCtrlDelete(event);	
				}

				
				// create historial
				try 
				{
					if (successFactorAdmin.getEventListenerCountHistoByidCrtl(event.getId()) <= 0) 
					{
						successFactorAdmin.eventListenerCtrlHistoCreate(employeeUpdate, attachList, event);
						
						//------------------------------------
						//send to processAgain
						if(!event.getIsProcessAgain() && event.getFkMassiveLoad()==null 
								&&  event.getEventListenerParam()!=null && event.getEventListenerParam().getIsProcessAgain()!=null 
									&& event.getEventListenerParam().getIsProcessAgain())
						{
							if(event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMINATE_EMP_ERROR_ATTACH.getCode())
									|| event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode())
										|| UtilMapping.getListStatusForGroup(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_SUCCESS.getCode()).contains(event.getStatus()))
							{
								// get Status to limit number of retries						
								Long addDay = 1L;						
								Timestamp initDate = new Timestamp((event.getStartDatePpdOn()).getTime());
								AdminParameters paramAdminAddDay = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_ADD_DAY_TIME_PROCESS_AGAIN.getCode());							 
								if(paramAdminAddDay!=null && paramAdminAddDay.getValue()!=null && !paramAdminAddDay.getValue().equals(""))
									addDay = Long.parseLong(paramAdminAddDay.getValue());
							
								event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING_PROCCESS_AGAIN.getCode());
								event.setCreateOn(new Date());
								event.setLastUpdateOn(new Date());
								event.setIsProcessAgain(true);
								event.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode()+"ProccessAgain From Id: "+event.getId()+" "+ UtilCodesEnum.CODE_STRING_END.getCode());
								
								for(int i=1; i<=addDay; i++)
								{
									Date dateAdd = UtilDateTimeAdapter.getDateAddMinutes(initDate,(1440L * i));
									event.setId(null);
									event.setStartDatePpdOn(dateAdd);													
									EventListenerCtrlProcess eventNew = successFactorAdmin.eventListenerCtrlProcessInsert(event);
								}
								
							}
						}
					}
				} catch (Exception e) {
					successFactorUtils.saveLoggerControl("400", "Create historial to event " + event.getId() + " " + e.getMessage(), UtilCodesEnum.CODE_JOB_SIGNATURE.getCode(), "Error");
				}

				return true;

			}
			//Error
			else {
				event.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
				event.setLastUpdateOn(date);

				if (employeeUpdate != null && employeeUpdate.getErrors() != null && employeeUpdate.getErrors().length > 0) {
					String resumeErrors = "";
					for (int i = 0; i < employeeUpdate.getErrors().length; i++) {
						if (employeeUpdate.getErrors()[i] != null) {
							resumeErrors += employeeUpdate.getErrors()[i].getCode() + " " + employeeUpdate.getErrors()[i].getField() + " " + employeeUpdate.getErrors()[i].getMessage() + ": ";
							if (employeeUpdate.getErrors()[i] != null && employeeUpdate.getErrors()[i].getCode() != null)
								event.setStatus(employeeUpdate.getErrors()[i].getCode());
						}
					}

					event.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + dateSeconds + "\n" + (event.getIdJobProcess() != null ? " idJob: " + event.getIdJobProcess() + ". " : "") + resumeErrors + " "
							+ UtilCodesEnum.CODE_STRING_END.getCode() + " " + event.getObservations());
				}

				event.setLastUpdateOn(new Date());
				successFactorAdmin.eventListenerCtrlProcessUpdate(event);

				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * create a new event listener control
	 * 
	 * @param String
	 *            startDate
	 * @param String
	 *            personIdExternal
	 * @param String
	 *            userId
	 * @param String
	 *            seqNum
	 * @param Boolean
	 *            searchFilesAfterProc
	 * @param Boolean
	 *            applyValidations,
	 * @param MassiveLoadUser
	 *            massiveLoadUser
	 * @param EventListenerParam
	 *            eventParam
	 * @return EventListenerCtrlProcess
	 */
	public EventListenerCtrlProcess eventListenerCtrlCreate(String startDate, String personIdExternal, String userId, 
			String seqNum, Boolean searchFilesAfterProc, 
			Boolean applyValidations, List<Countries> listCountriesConf,
			AdminParameters paramAdmUserid, AdminParameters paramProcessUserNoEmail, 
			AdminParameters paramPatternEmail, 
			MassiveLoadUser massiveLoadUser, EventListenerParam eventParam) 
	{
		EventListenerCtrlProcess entity = new EventListenerCtrlProcess();
		
		Date date = new Date();
		if(startDate != null) {
			date = UtilDateTimeAdapter.getDateFromString("yyyy-MM-dd", startDate);
		}
		String concReturn = UtilCodesEnum.CODE_FORMAT_STRUCTURE_USER.getCode();

		// ------------------------------------
		// set data event ctrl
		entity.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_SEARCHDOCS.getCode());
		entity.setUserIdPpd(personIdExternal);
		entity.setUserExtendPpd(userId);
		entity.setEventListenerParam(eventParam);
		entity.setSeqNumberPpd(seqNum);
		entity.setCreateOn(new Date());
		entity.setIdJobProcess(null);// reset id job process
		entity.setIsEmployeeValidate(true);

		entity.setIsSearchAttachAfterProc(searchFilesAfterProc);
		if (massiveLoadUser != null) {
			entity.setFkMassiveLoad(massiveLoadUser);
			entity.setIsEmployeeValidate(false);
		}

		if (date != null) {
			entity.setStartDatePpdOn(date);
		} else {
			entity.setObservations("Error format date " + startDate);
			entity.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
		}

		entity = successFactorAdmin.eventListenerCtrlProcessInsert(entity);
		if (!(entity != null && entity.getId() != null && entity.getId() > 0)) {
			entity.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
			entity.setObservations("Entity eventListenerCtrlProcess without id");
			return entity;
		}
		// ----------------------------------

		// validate country
		entity.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING_VALIDATE.getCode());
		if (applyValidations) {
			entity = this.validateEmployee(personIdExternal, entity, date, paramAdmUserid, listCountriesConf, paramProcessUserNoEmail, paramPatternEmail, searchFilesAfterProc);
		}

		if (entity != null && entity.getId() != null && entity.getId() > 0) {
			entity = successFactorAdmin.eventListenerCtrlProcessUpdate(entity);
		}
		return entity;
	}

	/**
	 * search attachments for user
	 * 
	 * @param String
	 *            personIdExternal
	 * @param AdminParameters
	 *            paramProcessUserNoEmail
	 * @parma EventListenerCtrlProcess entity
	 * @return ResponseGenericDto
	 */
	private ResponseGenericDto actionSearchAttachEventList(String personIdExternal, AdminParameters paramProcessUserNoEmail, EventListenerCtrlProcess entity) {
		ResponseGenericDto responseSearch = new ResponseGenericDto();
		
		logger.gson(personIdExternal);
		logger.gson(paramProcessUserNoEmail); 
		
		try {
			// email is required
			if (paramProcessUserNoEmail != null && paramProcessUserNoEmail.getValue() != null && paramProcessUserNoEmail.getValue().equals(UtilCodesEnum.CODE_TRUE.getCode())) 
			{
				responseSearch = successFactorAttachFacade.attachmentSearchModuleRC_ONB(entity, true);
			}
			else 
			{
				responseSearch = successFactorAttachFacade.attachmentSearchModuleRC_ONB(entity, false);
			}

			if (responseSearch != null && responseSearch.getCode() != null && responseSearch.getCode().equals(UtilCodesEnum.CODE_ERROR.getCode())) {
				responseSearch.setMessage(responseSearch.getMessage());
			}

			if((entity.getFkMassiveLoad() == null && entity.getEventListenerParam()!=null && entity.getEventListenerParam().getIsHire()!=null && entity.getEventListenerParam().getIsHire())
					|| (entity.getFkMassiveLoad() != null && entity.getFkMassiveLoad().getAttachTypes() != null 
							&& entity.getFkMassiveLoad().getAttachTypes().contains(UtilCodesEnum.CODE_TYPE_MODULE_EMPLOYEE_CENTER.getCode())) ) 
			{
				Date initDate = UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(),UtilCodesEnum.CODE_PARAM_ADM_SEARCH_ATTACH_INIT_DATE_MASSIVE.getCode());
				successFactorAttachFacade.actionProcessAttachSearchQuartz(personIdExternal,initDate,new Date(),entity);
			}

			if(entity.getFkMassiveLoad() == null || ((entity.getFkMassiveLoad() != null && entity.getFkMassiveLoad().getAttachTypes() != null 
					&& entity.getFkMassiveLoad().getAttachTypes().contains(UtilCodesEnum.CODE_TYPE_MODULE_TEMPLATE.getCode()))))
			{
				successFactorAttachFacade.attachmentGetSaveTemplate(entity, personIdExternal);
			}

		} catch (Exception e) {
			e.printStackTrace();
			responseSearch.setMessage("Error search attachments: " + e.getMessage());
		}

		return responseSearch;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Updates records that have exceeded the maximum wait time
	 * 
	 * @param String
	 *            statusToLoad
	 * @param String
	 *            statusDestination
	 * @param Integer
	 *            timeOutMinutes
	 */
	private void actionCtrlUpdateEventsTimeOut(Integer p_timeMax) {

		if (p_timeMax != null) {
			// update events that have exceeded the maximun wait time
			successFactorAdmin.eventListenerCtrlUpdatetToStatusTimeOut("'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_SEARCHDOCS.getCode() + "'",
					UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode(), p_timeMax);
		}
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * evaluate if user is whithout mail
	 * 
	 * @param String
	 *            userId
	 */
	public Boolean actionEvaluateUserWhitoutMail(String userId) {
		String infoUserRecruiting = successFactorUtils.getUserGetProfileRecruitingModule(userId);
		String mailUserRecruiting = UtilMapping.loadUserInfoRecruitingForJson(infoUserRecruiting, QueryOdataDAO.QUERY_EMPLOYEE_PATH_MAIL_RECRUITING);

		if (!(mailUserRecruiting != null && !mailUserRecruiting.equals(""))) {
			return true;
		}

		return false;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * evaluate
	 * 
	 * @param Date
	 *            initDate
	 * @return Boolean
	 */
	private Boolean actionEvaluateIsTimeUserWhitouMail(Date initDate) {
		Long p_timeMax = 10L;
		AdminParameters paramAdminCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_WAITTIME_USERNOMAIL.getCode());
		if (paramAdminCode != null && paramAdminCode.getValue() != null) {
			p_timeMax = Long.parseLong(paramAdminCode.getValue());
		}

		Date finalDate = UtilDateTimeAdapter.getDateAddMinutes(new Timestamp(initDate.getTime()), p_timeMax);
		if (finalDate.getTime() < (new Date()).getTime())
			return true;
		else
			return false;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * get num retries to add
	 * 
	 * @param String
	 *            statusEventDoc
	 * @param AdminParameters
	 *            paramAdminRetriesCode
	 * @return Intenger
	 */
	private Integer actionGetRetries(AdminParameters paramAdminRetriesCode, String statusLimitRetries) {
		if (paramAdminRetriesCode != null && paramAdminRetriesCode.getValue() != null && statusLimitRetries != null && paramAdminRetriesCode.getValue().contains(statusLimitRetries)) {
			return 1;
		} else if (!(paramAdminRetriesCode != null && paramAdminRetriesCode.getValue() != null))

		{
			if (!statusLimitRetries.equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode()) || !statusLimitRetries.equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode())
					|| !statusLimitRetries.equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode()) || !statusLimitRetries.equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode())
					|| !statusLimitRetries.equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode()) || !statusLimitRetries.equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode())) {
				return 1;
			}
		}

		return 0;
	}

	/**
	 * @param String  idEmplooyePpd
	 * @param String structureFilterEmployee
	 * @return GenErrorInfoDto
	 */
	private GenResponseInfoDto actionUpdateElectronicVault(String idEmployeePpd, String structureFilterCodeEmployee) 
	{
		GenResponseInfoDto response = new GenResponseInfoDto();

		try 
		{
			response.setFlag(false);			
			if(structureFilterCodeEmployee!=null && !structureFilterCodeEmployee.equals("")) 
			{
				String [] structureFiltersEmployee = structureFilterCodeEmployee.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
				if(structureFiltersEmployee!=null)
				{
					for(String structureFilterCode : structureFiltersEmployee)
					{
						List<Countries> structureFilter = successFactorAdmin.countriesGetByCode(structureFilterCodeEmployee);
						if(structureFilter!=null)
						{
							for(Countries structureFilterItem : structureFilter)
							{
								if (structureFilterItem.getParameters()!=null && !structureFilterItem.getParameters().equals("")) 
								{
									PpdElectronicVaultOptionsDto electronicVault = UtilMapping.loadPpdElectronicFromKey(idEmployeePpd, structureFilterItem.getCode());
									if (electronicVault != null) 
									{
										response = ppdEmployeeF.wServiceElectronicVaultOptionsByEmployee(idEmployeePpd, electronicVault);
									}
								} else 
								{
									response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode());
									response.setMessage("no configuration parameters were found for electronic vault, structure :" + structureFilterItem.getCode());
								}
							}
						}
					}
				}
			}			
		} catch (Exception e) {
			response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
			response.setMessage(e.getMessage());
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * @param String
	 *            personIdExternal
	 * @param EventListenerCtrlProcess
	 *            entity
	 * @param Date
	 *            date
	 * @param AdminParameters
	 *            paramAdmUserid
	 * @param List<Countries>
	 *            listCountriesConf
	 * @param AdminParameters
	 *            paramProcessUserNoEmail
	 * @param searchFilesAfterProc
	 */
	private EventListenerCtrlProcess validateEmployee(String personIdExternal, 
														EventListenerCtrlProcess entity, Date date, 
														AdminParameters paramAdmUserid, 
														List<Countries> listCountriesConf, 
														AdminParameters paramProcessUserNoEmail,
														AdminParameters paramPatternEmail, 
														Boolean searchFilesAfterProc) 
	{
		String concReturn = UtilCodesEnum.CODE_FORMAT_STRUCTURE_USER.getCode();

		if (paramAdmUserid != null) {
			concReturn = paramAdmUserid.getValue();
		}

		ArrayList<PSFStructureEmployeeDto> reponseMsgList = successFactorUserFacade.actionValidateStructureActive(personIdExternal, UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd", date), listCountriesConf, concReturn, false);
		if (reponseMsgList!=null && reponseMsgList.size()>0 && !reponseMsgList.get(0).getIsActiveStructureConfPSF()) 
		{
			successFactorAdmin.eventListenerCtrlDelete(entity);

			EventListenerCtrlHistory eventCtl = new EventListenerCtrlHistory();
			eventCtl.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
			eventCtl.setUserIdPpd(personIdExternal);
			eventCtl.setObservations(reponseMsgList.get(0).getMessage());
			eventCtl.setLastUpdateOn(new Date());
			
			// update list of countries
			eventCtl.setUserCountry(UtilMapping.getCountryListFromList(reponseMsgList));

			if (entity.getFkMassiveLoad() != null && entity.getFkMassiveLoad().getId() != null) {
				eventCtl.setRefMassLoadId(entity.getFkMassiveLoad().getId());
			}

			successFactorAdmin.eventListenerCtrlHistoInsert(eventCtl);

			entity.setId(null);
			entity.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
			entity.setObservations(eventCtl.getObservations());
			
			//update employee organitation in PPD
			if(!reponseMsgList.get(0).getIsActiveStructureConfPSF()){				
				GenErrorInfoDto responseUpdateOrga = successFactorUserFacade.actionUpdateOrganitationToEmployee(personIdExternal, UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd", date), listCountriesConf);
			}

			return entity;
		} else if(reponseMsgList!=null && reponseMsgList.size()>0) 
		{
			// update list of countries
			entity.setUserCountry(UtilMapping.getCountryListFromList(reponseMsgList));
			
			// save all documents associated with the user
			if (reponseMsgList.get(0).getIsAllowAttachConfPSF() != null && reponseMsgList.get(0).getIsAllowAttachConfPSF())// alloweb process attachments
			{
				// evaluate admin parameter proccess user no email
				Boolean userWhitoutEmail = this.actionEvaluateUserWhitoutMail(entity.getUserIdPpd());
				
				if(paramPatternEmail != null && paramPatternEmail.getValue() != null ) {
					userWhitoutEmail = false;
				}
				

				if (!userWhitoutEmail || (paramProcessUserNoEmail != null && paramProcessUserNoEmail.getValue() != null && paramProcessUserNoEmail.getValue().equals(UtilCodesEnum.CODE_TRUE.getCode()))) {
					ResponseGenericDto responseSearch = new ResponseGenericDto();

					if (!searchFilesAfterProc) {
						this.actionSearchAttachEventList(personIdExternal, paramProcessUserNoEmail, entity);
					}

					entity.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());
				} else {
					entity.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_USERPENDINGMAIL.getCode());
				}
			} else {
				entity.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());
				entity.setObservations("structure not active for processing attachments");
			}
		}

		return entity;
	}

	/**
	 * execute all actions asociated
	 * 
	 * @param List<SlugItem> actionsList
	 * @param AdminParameters paramManagerOrga
	 * @param String paramPrefixUserPpd
	 */
	private String actionExecuteActions(List<SlugItem> actionsList, AdminParameters paramManagerOrga, String paramPrefixUserPpd) {

		if (paramManagerOrga != null && paramManagerOrga.getValue() != null && !paramManagerOrga.getValue().equals("")) {
			edn.cloud.business.dto.GenErrorInfoDto response = successFactorUserFacade.userEmployeeExecuteActions(actionsList, paramManagerOrga.getValue(),paramPrefixUserPpd);

			if (response != null)
				return response.getMessage();
			else
				return "Evaluate actions: false, error userEmployeeExecuteActions";
		}

		return "Evaluate actions: false, error manager_orga_ppd ";
	}
	
	/**
	 * create event listener from event listener history
	 * @param Long idCtrlEventHisto
	 * @return EventListenerCtrlProcess 
	 * */
	public EventListenerCtrlProcess eventListenerCtrlCreateFromHisto(Long idCtrlEventHisto)
	{
		EventListenerCtrlHistory  history = successFactorAdmin.eventListenerCtrlHistoById(idCtrlEventHisto);
		
		AdminParameters paramAdmUserid = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_USERID.getCode());
		AdminParameters paramProcessUserNoEmail = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_PROCESS_USERNOMAIL.getCode());
		AdminParameters paramPatternEmail = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_FORMAT_MAILUSERTERM_PPD.getCode());
		List<Countries> listCountriesConf = successFactorAdmin.countriesGetAll();
		
		if(history!=null){
			EventListenerCtrlProcess  eventCtrl = this.eventListenerCtrlCreate(
										 history.getStartDatePpdOnString(),
										 history.getUserIdPpd(), 
										 history.getUserIdPpd(),  
										 history.getSeqNumberPpd(), 
										 false, 
										 true, 
										 listCountriesConf, 
										 paramAdmUserid, 
										 paramProcessUserNoEmail, 
										 paramPatternEmail, 
										 null,
										 history.getEventListenerParam());
			
			return eventCtrl;
		}
		
		return null;
	}
	
	
	/**
	 * send email to user
	 * @param Boolean validateHour	  
	 * @return ResponseGenericDto response
	 * */
	public ResponseGenericDto sendEmailResumeEvent(Boolean validateHour)
	{
		String urlDonwload = "";
		ResponseGenericDto response = new ResponseGenericDto();
		
		try
		{
			AdminParameters paramLastDateSend = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_LAST_DATE_EMAIL_NOTI_EVENT.getCode());
			AdminParameters paramEmailNotiHour = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_NOTIFICATIONS_HOUR.getCode());
			
			if(paramEmailNotiHour!=null && paramEmailNotiHour.getValue()!=null && !paramEmailNotiHour.getValue().equals(""))
			{				
				Date initDateHourInterval = UtilDateTimeAdapter.getDateAddMinutes((new Timestamp(new Date().getTime())), -1L);
				Date endDateHourInterval = UtilDateTimeAdapter.getDateAddMinutes((new Timestamp(new Date().getTime())), 1L);
				Date notiDateHour = UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(),
																		  UtilDateTimeAdapter.getDateNow()+" "+paramEmailNotiHour.getValue());
				
				if(validateHour && !(notiDateHour!=null && notiDateHour.getTime()>=initDateHourInterval.getTime() && notiDateHour.getTime()<=endDateHourInterval.getTime())) {
					return response;
				}
				
				FilterQueryDto filter = new FilterQueryDto();
				
				AdminParameters paramUrlDownload = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MASSIVE_DOWNLOAD_URL.getCode());
				AdminParameters paramEmail = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_NOTIFICATIONS.getCode());
				AdminParameters paramEmailSubject = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MASSIVE_EMAIL_SUBJECT.getCode());
				
				//-----------------------------------------
				//admin parameters event dates
				AdminParameters paramDateInitSearch = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_INIT_HOUR.getCode());
				if(paramDateInitSearch!=null && paramDateInitSearch.getValue()!=null)
					filter.setDate(UtilDateTimeAdapter.getDateNow()+" "+paramDateInitSearch.getValue());
				else
					filter.setDate(UtilDateTimeAdapter.getDateNow()+" "+" 00:00");
				
				AdminParameters paramDateEndSearch = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_END_HOUR.getCode());
				if(paramDateEndSearch!=null && paramDateEndSearch.getValue()!=null)
					filter.setDateFinish(UtilDateTimeAdapter.getDateNow()+" "+paramDateEndSearch.getValue());
				else
					filter.setDateFinish(UtilDateTimeAdapter.getDateNow()+" "+" 23:59");
								
				if(paramUrlDownload!=null && paramUrlDownload.getValue()!=null)
					urlDonwload = paramUrlDownload.getValue()+"/admin/eventsyncdownload/";
								
				if(paramEmail!=null && !paramEmail.getValue().equals(""))
				{
					Email emailImpl = new EmailImpl();
					String [] emails = paramEmail.getValue().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode());
					
					if(emails!=null)
					{
						//-------------------------------------------
						//events						
						List<Object[]> statusCounter = successFactorAdmin.eventListenerGetStatusCount(filter,null);
						List<Object[]> statusCounterHisto = successFactorAdmin.eventListenerHistoGetStatusCount(filter,null);
						statusCounter = successFactorAdmin.unionAllCountersEventListener(statusCounter,statusCounterHisto);
						
						//atachments
						List<Object[]> statusCounterDoc = successFactorAdmin.eventListenerDocGetStatusCount(filter);
						List<Object[]> statusCounterDocHisto =  successFactorAttachFacade.attachmentHistoGetStatusCount(filter);
						statusCounterDoc = successFactorAdmin.unionAllCountersEventListener(statusCounterDoc,statusCounterDocHisto);
						
						//build event body 
						String reportBody = 	UtilMapping.getHtmlTableCode("Events Report","", "tr_title")+
											 	UtilMapping.getHtmlTableCode("Date Report:", UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(),new Date()), "tr_info");
											 
						reportBody += 		 	UtilMapping.getHtmlTableCode("Statistics","", "tr_title")+
											 	UtilMapping.loadHtmlRow("Total",statusCounter)+
											 	UtilMapping.loadHtmlRow("Pending Records",statusCounter);
						
						//build attach body
						String reportAttachBody = 
												UtilMapping.getHtmlTableCode("","", "tr_separator")+
												UtilMapping.getHtmlTableCode("","", "tr_separator")+
												UtilMapping.getHtmlTableCode("","", "tr_separator")+																								
												UtilMapping.getHtmlTableCode("Synchronization Report","", "tr_title")+
												UtilMapping.getHtmlTableCode("Date Report:", UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(),new Date()), "tr_info");
								 
						reportAttachBody +=		UtilMapping.getHtmlTableCode("Statistics","", "tr_title")+
												UtilMapping.loadHtmlRow("Total",statusCounterDoc)+
												UtilMapping.loadHtmlRow("Pending Records",statusCounterDoc);
						
						//get title and body for events
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
							
						//get title and body for attachments
						String errorAttachBody = UtilMapping.loadHtmlRow(UtilMapping.getListStatusForGroup(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERROR.getCode()),statusCounterDoc);
						if(!errorAttachBody.equals(""))
							errorAttachBody = UtilMapping.getHtmlTableCode(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERROR.getLabel(),"","tr_title")+errorAttachBody;
											
						String errorAttachPpd = UtilMapping.loadHtmlRow(UtilMapping.getListStatusForGroup(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERRORPPD.getCode()),statusCounterDoc);
						if(!errorAttachPpd.equals(""))
							errorAttachPpd = UtilMapping.getHtmlTableCode(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERRORPPD.getLabel(),"","tr_title")+ errorAttachPpd;
						
						String errorAttachPending = UtilMapping.loadHtmlRow(UtilMapping.getListStatusForGroup(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_PENDING.getCode()),statusCounterDoc);
						if(!errorAttachPending.equals(""))
							errorAttachPending = UtilMapping.getHtmlTableCode(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_PENDING.getLabel(),"","tr_title")+ errorAttachPending;
						
						String errorAttachSuccess = UtilMapping.loadHtmlRow(UtilMapping.getListStatusForGroup(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_SUCCESS.getCode()),statusCounterDoc);
						if(!errorAttachSuccess.equals(""))
							errorAttachSuccess = UtilMapping.getHtmlTableCode(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_SUCCESS.getLabel(),"","tr_title") + errorAttachSuccess;
						
						
						String report,reportAttach = "";
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
										reportAttach = errorAttachBody+errorAttachPpd+errorAttachPending+errorAttachSuccess;
									}
									else if((!errorBody.equals("") || !errorAttachBody.equals(""))  
												&& emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1].toString()
													.equals(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERROR.getCode())){
										report = errorBody;
										reportAttach = errorAttachBody;
									}
									else if((!errorPpd.equals("")  || !errorAttachPpd.equals("")) 
											&& emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1].toString()
												.equals(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERRORPPD.getCode())){
										report = errorPpd;
										reportAttach = errorAttachPpd;
									}
									else if((!errorPending.equals("") || !errorAttachPending.equals(""))
											&& emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1].toString()
												.equals(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_PENDING.getCode())){
										report = errorPending;
										reportAttach = errorAttachPending;
									}
									else if((!errorSuccess.equals("")  || !errorAttachSuccess.equals("")) 
											&& emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1].toString()
												.equals(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_SUCCESS.getCode())){
										report = errorSuccess;
										reportAttach = errorAttachSuccess;
									}
									
									reportList.add(reportBody+report+reportAttachBody+reportAttach); 
									emailImpl.sendEmailResumeMassive(UtilCodesEnum.CODE_DEFAULT_FROM_NOTI_EMAIL.getCode(),
													emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[0].toString(),
													paramEmailSubject!=null&&!paramEmailSubject.getValue().equals("")?
													paramEmailSubject.getValue():UtilCodesEnum.CODE_MASSIVE_SYNC_EVENT_SUBJECT.getCode()+" ",
													!urlDonwload.equals("")?urlDonwload+UtilDateTimeAdapter.getDateNow()+"/"+emailConf.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[0].toString():"",
													reportList);
								}
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