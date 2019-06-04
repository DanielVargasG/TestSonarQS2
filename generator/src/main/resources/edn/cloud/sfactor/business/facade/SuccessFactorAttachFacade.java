package edn.cloud.sfactor.business.facade;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.GenErrorInfoDto;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.integration.attach.SFAttachResponseN1Dto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.business.dto.psf.PSFStructureEmployeeDto;
import edn.cloud.ppdoc.business.facade.PpdApiUtilsFacade;
import edn.cloud.ppdoc.business.facade.PpdEmployeeApiFacade;
import edn.cloud.ppdoc.business.facade.PpdEmployeeUtilFacade;
import edn.cloud.sfactor.business.impl.SuccessFactorAttachmentImpl;
import edn.cloud.sfactor.business.impl.SuccessFactorImpl;
import edn.cloud.sfactor.business.interfaces.SuccessFactorAttachment;
import edn.cloud.sfactor.business.interfaces.SuccessFactor;
import edn.cloud.sfactor.business.utils.QueryBuilder;
import edn.cloud.sfactor.business.utils.StructureBuilder;
import edn.cloud.sfactor.persistence.dao.QueryOdataDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Countries;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerDocHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.FieldsMappingMeta;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;
import edn.cloud.sfactor.persistence.entities.Template;

public class SuccessFactorAttachFacade {
	private UtilLogger logger = UtilLogger.getInstance();
	PpdEmployeeUtilFacade ppdUserUtilF = new PpdEmployeeUtilFacade();
	private SuccessFactorTemplateFacade SFTemplateFacade = new SuccessFactorTemplateFacade();
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	private SuccesFactorUserFacade successFactorUserFacade = new SuccesFactorUserFacade();
	private PpdEmployeeApiFacade ppdEmployeeF = new PpdEmployeeApiFacade();
	private SuccessFactorFacade successFactorUtils = new SuccessFactorFacade();
	private SuccessFactor succesFactorI = new SuccessFactorImpl();
	private SuccessFactorAttachment succesFactorAttachI = new SuccessFactorAttachmentImpl();
	private SuccessFactorOnboardingFacade SFOnboardingFacade = new SuccessFactorOnboardingFacade();
	private PpdApiUtilsFacade ppdApiUtilsF = new PpdApiUtilsFacade();

	public SuccessFactorAttachFacade() {
		super();
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * process call from EventListenerAttachjob quartz
	 * 
	 * @param String idExternaEmployee **optional
	 * @param Date initDateEmplSearch **optional
	 * @param Date endDateEmplSearch **optional
	 * @param EventListenerCtrlProcess entity
	 */
	public ResponseGenericDto actionProcessAttachSearchQuartz(String idExternalEmplSearch,
												Date initDateEmplSearch,
												Date endDateEmplSearch,												
												EventListenerCtrlProcess eventListener) 
	{
		ResponseGenericDto response = new ResponseGenericDto();
		int countTotalEvents = 0;
		
		try 
		{
			Date date = new Date();			
			
			//validate/update link event listener ctrl process
			if(eventListener!=null && eventListener.getId()!=null){
				eventListener = successFactorAdmin.eventsListenerCtrlProcGetByid(eventListener.getId());
			}

			ArrayList<FieldsMappingPpd> fieldsUserList = successFactorAdmin.mappingPpdFieldsGetAll(true, UtilCodesEnum.CODE_NOTNULL.getCode(), Boolean.TRUE, null);
			// Time interval in search of changes of user attachments
			AdminParameters paramIntervalTime = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_TIME_INTERVAL_SEARCH_ATTCH.getCode());
			
			
			// date of last search changes of attachments in successfactor
			AdminParameters paramInitDate = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_TIME_INITDATE_SEARCH_ATTACH.getCode());
			
			// Number of minutes to add to the initial date of attachment search
			AdminParameters paramAddMinInitDate = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_TIME_INITDATE_SEARCH_ATTACH_ADD.getCode());

			// validate init date
			Date endDateParam = new Date();
			if (paramInitDate != null && paramInitDate.getValue() != null && !paramInitDate.getValue().equals("")) {
				endDateParam = UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), paramInitDate.getValue());
				endDateParam = endDateParam != null ? endDateParam : (new Date());
			}

			// validate interval time
			Long intervalTime = 1L;
			if (paramIntervalTime != null && paramIntervalTime.getValue() != null && !paramIntervalTime.getValue().equals("")) {
				try {
					intervalTime = Long.parseLong(paramIntervalTime.getValue());
				} catch (Exception e) {
					intervalTime = 1L;
				}
			}

			// validate add time to initial date
			Long addMinInitialDate = 0L;
			if (paramAddMinInitDate != null && paramAddMinInitDate.getValue() != null && !paramAddMinInitDate.getValue().equals("")) {
				try {
					addMinInitialDate = Long.parseLong(paramAddMinInitDate.getValue());
				} catch (Exception e) {
					addMinInitialDate = 0L;
				}
			}

			// add minutes to end date
			endDateParam = UtilDateTimeAdapter.getDateAddMinutes(new Timestamp(endDateParam.getTime()), addMinInitialDate);

			// calculate endDate
			Date initDate = UtilDateTimeAdapter.getDateAddMinutes(new Timestamp(endDateParam.getTime()), intervalTime * -1);
			Date endDateParamNew = UtilDateTimeAdapter.getDateAddMinutes(new Timestamp(endDateParam.getTime()), Math.abs(intervalTime));

			// ---------------------------------------------------
			// Udapte initital date next process
			String dateQueryString = "";
			if (idExternalEmplSearch == null) {
				if (paramInitDate != null && paramInitDate.getValue() != null && !paramInitDate.getValue().equals("")) {
					successFactorAdmin.adminParamInsertOrUpdate(UtilCodesEnum.CODE_PARAM_ADM_TIME_INITDATE_SEARCH_ATTACH.getCode(), UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), endDateParamNew), false);
				}

				// Udapte last execution
				dateQueryString = " Search Date From: " + UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), initDate) + " To: "
						+ UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), endDateParam) + "  ";
				successFactorAdmin.adminParamInsertOrUpdate(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_SEARCHATTACH.getCode(), dateQueryString + "Execution: " + UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), date),
						false);
			} else 
			{
				//update dates
				endDateParam = endDateEmplSearch;
				initDate = initDateEmplSearch;
				dateQueryString = " search for specific user " + idExternalEmplSearch;
			}
			// ---------------------------------------------------
			if (fieldsUserList != null) 
			{
				// load countries
				List<Countries> listCountriesConf = successFactorAdmin.countriesGetAll();
				String currentDate = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd", new Date());

				// load mappings parameters
				AdminParameters paramAdmStructure = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STRUCTURE_KEY.getCode());

				for (FieldsMappingPpd field : fieldsUserList) {
					// validate only fields attached
					if (field.getIsAttached()) {
						// ----------------------
						// Module Employee Center
						if (field.getTypeModule() != null && field.getTypeModule().equals(UtilCodesEnum.CODE_TYPE_MODULE_EMPLOYEE_CENTER.getCode()) && field.getNameDestination() != null && !field.getNameDestination().equals("")) 
						{
							ArrayList<EventListenerDocProcess> listFiles = succesFactorAttachI.searchModifiedAttachments(idExternalEmplSearch, field, initDate, endDateParam, 200);
							
							// validate country user vs country conf							
							listFiles = actionValidateFiltersAttachs(endDateParam,currentDate, listFiles, listCountriesConf);
							
							if (listFiles != null && listFiles.size() > 0) 
							{
								for (EventListenerDocProcess docItem : listFiles) {
									// only files no pending to process
									if (docItem.getAttachmentIdSF() != null && !docItem.getAttachmentIdSF().equals("")) {
										if (!docItem.getAttachmentIdSF().equals(UtilCodesEnum.CODE_TYPE_MODULE_ATTACH_UPDATE_USER.getCode()))
										{
											docItem.setFieldMapPpd(field);
											docItem.setCreateOn(new Date());
											docItem.setLastUpdateOn(new Date());
											docItem.setAttachmentFileName("Attach("+docItem.getAttachmentIdSF()+")");
											docItem.setStatus(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());
											
											docItem.setObservations(dateQueryString);											
											
											if(eventListener!=null)
												docItem.setEventListenerCtrlProc(eventListener);

											docItem = successFactorAdmin.eventListenerDocProcessInsert(docItem);
											countTotalEvents++;

										} else if (docItem.getAttachmentIdSF().equals(UtilCodesEnum.CODE_TYPE_MODULE_ATTACH_UPDATE_USER.getCode())) {
											docItem.setFieldMapPpd(field);
											docItem.setCreateOn(new Date());
											docItem.setLastUpdateOn(new Date());
											docItem.setAttachmentFileName("Update User ("+docItem.getUserIdPpd()+") in Ppd ");
											docItem.setStatus(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());
											docItem.setObservations(dateQueryString);											
											
											if(eventListener!=null)
												docItem.setEventListenerCtrlProc(eventListener);

											docItem = successFactorAdmin.eventListenerDocProcessInsert(docItem);
											countTotalEvents++;
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error :"+e.getMessage());
			response.setFlag(false);
		}
		
		response.setMessage("Total Events Attachment Created :"+countTotalEvents);
		response.setFlag(true);
		return response;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * process send attachments
	 */
	public void actionProcessAttachmentSyncSendQuartz() 
	{
		try 
		{
			Date date = new Date();
			String dateSeconds = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);

			// status to load and process documents attach
			String statusToLoad = "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode() + "'," + "'" 
									+ UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode() + "'," 
									+ "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode() + "',"
									+ "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode() + "'," 
									+ "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode() + "'," + "'" 
									+ UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode() + "'";

			// -----------------------------------
			// Load Admin parameters

			// get maximun number of records to process
			Integer p_numMaxReg = null;
			AdminParameters paramAdminCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_ATTACH.getCode());
			if (paramAdminCode != null && paramAdminCode.getValue() != null) {
				p_numMaxReg = Integer.parseInt(paramAdminCode.getValue());
			}

			// get wait time maximum to process register
			Integer p_timeMax = null;
			paramAdminCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_WAITIME.getCode());
			if (paramAdminCode != null && paramAdminCode.getValue() != null) {
				p_timeMax = Integer.parseInt(paramAdminCode.getValue());
			}

			// get max number of retries
			Integer p_maxRetries = null;
			AdminParameters paramAdminMaxRetriesCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_ATTACH.getCode());
			if (paramAdminMaxRetriesCode != null && paramAdminMaxRetriesCode.getValue() != null) {
				p_maxRetries = Integer.parseInt(paramAdminMaxRetriesCode.getValue());
			}

			// get structure
			AdminParameters paramAdmStructure = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STRUCTURE_KEY.getCode());

			// get userconstruction
			AdminParameters paramAdmUserid = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_USERID.getCode());

			// define format to filename
			AdminParameters paramFormatNameFile = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_PATTERN_FILENAME_SEND_PPD.getCode());

			// list mmapings fields
			ArrayList<FieldsMappingPpd> fieldsMapUserList = successFactorAdmin.mappingPpdFieldsGetAll(null, UtilCodesEnum.CODE_NA.getCode(), Boolean.TRUE, Boolean.FALSE);
			// -------------------------------------

			ArrayList<EventListenerDocProcess> eventListenerDocList = successFactorAdmin.getAllAttachmentProcess(date, statusToLoad, p_numMaxReg, null);

			// register quartz event
			successFactorAdmin.eventListenerRegisterEventQuarzt(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_EVENTLISTATTACH.getCode(),
					UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), date) + " " + "number of Records: " + (eventListenerDocList != null ? eventListenerDocList.size() : 0));

			if (eventListenerDocList != null) 
			{
				HashMap<String,String> keysAttachsFileMap = new HashMap<>();
				for (EventListenerDocProcess itemAttachment : eventListenerDocList) 
				{
					// update count of retries
					itemAttachment.setRetries(itemAttachment.getRetries() + 1);

					if (p_maxRetries != null && itemAttachment != null && itemAttachment.getRetries() >= p_maxRetries) {
						// change status of event
						itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode());
						itemAttachment.setLastUpdateOn(date);
						successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);
					} else 
					{
						// change status of attach
						itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode());
						itemAttachment.setLastUpdateOn(date);
						successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);

						// --------------------------------------------------------------------------
						// --------------------------------------------------------------------------
						// update user in Ppd
						if (itemAttachment.getAttachmentIdSF().equals(UtilCodesEnum.CODE_TYPE_MODULE_ATTACH_UPDATE_USER.getCode())) {
							this.actionProccessUpdateEmployee(itemAttachment, fieldsMapUserList, paramAdmStructure, paramAdmUserid);
						} else 
						{
							//validate that file dont exist in this cycle
							if(keysAttachsFileMap.get(itemAttachment.getAttachmentIdSF())!=null)
							{
								itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
								itemAttachment.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ": " + dateSeconds + " document will be processed by the event Attach: " + keysAttachsFileMap.get(itemAttachment.getAttachmentIdSF()).toString() + " "
																+ UtilCodesEnum.CODE_STRING_END.getCode() + itemAttachment.getObservations());
								itemAttachment.setLastUpdateOn(new Date());
								successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);
							}							
							else if (ppdEmployeeF.wServiceEmployeeDocExistByExternalId(itemAttachment.getAttachmentIdSF())) 
							{	
								keysAttachsFileMap.put(itemAttachment.getAttachmentIdSF(),itemAttachment.getId()+"");
								itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
								itemAttachment.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ": " + dateSeconds + " Document exist in Ppd by ExternaId: " + itemAttachment.getAttachmentIdSF() + " "
										+ UtilCodesEnum.CODE_STRING_END.getCode() + itemAttachment.getObservations());
								itemAttachment.setLastUpdateOn(new Date());
								successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);
							} 
							else 
							{
								//add attachment id for validation (repeated file)
								keysAttachsFileMap.put(itemAttachment.getAttachmentIdSF(),itemAttachment.getId()+"");
								
								SFAttachResponseN1Dto attachResponseDto = succesFactorI.getAttachContentFromAllModule(itemAttachment.getAttachmentIdSF());

								if (attachResponseDto != null && attachResponseDto.getD() != null && attachResponseDto.getD().getFileContent() != null) {
									// load document type from admin parameters
									FieldsMappingPpd fieldTypeDocument = itemAttachment.getFieldMapPpd();

									if (fieldTypeDocument != null && fieldTypeDocument.getParameters() != null && !fieldTypeDocument.getParameters().equals("")) {
										// --------------------------------------------------
										// Check organizations to compare with user
										StructureBuilder structureBuilder = new StructureBuilder();

										AdminParameters paramAdminCodeStruc = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STRUCTURE_KEY.getCode());

										String[] structure = null;
										if (paramAdminCodeStruc != null && paramAdminCodeStruc.getValue() != null) {
											try {
												structure = structureBuilder.getStructureAttach(itemAttachment.getUserIdPpd(), itemAttachment.getUserExtendPpd(), paramAdminCodeStruc.getValue(), "");
											} catch (Exception e) {
												structure = null;
											}
										} else {
											itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
											itemAttachment.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ": " + dateSeconds + "Admin Parameter: " + UtilCodesEnum.CODE_PARAM_ADM_STRUCTURE_KEY.getCode() + " is not configured "
													+ UtilCodesEnum.CODE_STRING_END.getCode() + itemAttachment.getObservations());
											itemAttachment.setLastUpdateOn(new Date());
											successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);
										}

										if (structure != null) {
											// build Metadata parameters
											HashMap<String, String> metadata = this.attachmentGetMetadataValues(itemAttachment.getUserIdPpd(), successFactorAdmin.mappingPpdFieldMetaGetByIdFieldMapp(fieldTypeDocument.getId()),
													itemAttachment.getStartDatePpdOnString());

											GenResponseInfoDto response = ppdEmployeeF.wServiceUploadEmployeeDocCompany(itemAttachment.getUserIdPpd(), attachResponseDto, fieldTypeDocument.getParameters(), structure, metadata,
													paramFormatNameFile);

											if (response != null && response.getCode() != null && response.getCode().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode())) {
												itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
												itemAttachment.setObservations(
														UtilCodesEnum.CODE_STRING_INIT.getCode() + ": " + dateSeconds + "\n " + response.getMessage() + UtilCodesEnum.CODE_STRING_END.getCode() + " " + itemAttachment.getObservations());
												itemAttachment.setLastUpdateOn(new Date());
												successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);

											} else {

												if (response.getMessage() != null && response.getMessage().contains(UtilCodesEnum.CODE_ERROR_409.getCode())) {
													itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
												} else {
													itemAttachment.setStatus(response.getCode());
												}

												itemAttachment.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + response.getMessage() + itemAttachment.getObservations());
												itemAttachment.setLastUpdateOn(new Date());
												successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);
											}
										} else {
											itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
											itemAttachment.setObservations(
													UtilCodesEnum.CODE_STRING_INIT.getCode() + ": " + dateSeconds + "Structure is null (get Structure Attach) " + UtilCodesEnum.CODE_STRING_END.getCode() + itemAttachment.getObservations());
											itemAttachment.setLastUpdateOn(new Date());
											successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);
										}
									} else {
										itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
										itemAttachment.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n Error: Type Document in field Mapping not exist "
												+ (fieldTypeDocument != null ? ", Mapping Source: " + fieldTypeDocument.getNameSource() + " Module: (" + fieldTypeDocument.getTypeModule() + ") " : " " + ". ")
												+ UtilCodesEnum.CODE_STRING_END.getCode() + itemAttachment.getObservations());
										itemAttachment.setLastUpdateOn(new Date());
										successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);
									}
								} else {

									if (attachResponseDto != null && attachResponseDto.getError() != null) {
										itemAttachment.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n Error " + attachResponseDto.getError().getCode() + " "
												+ attachResponseDto.getError().getMessage().getValue() + UtilCodesEnum.CODE_STRING_END.getCode() + " " + itemAttachment.getObservations());
									}

									itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode());
									itemAttachment.setLastUpdateOn(new Date());
									successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);
								}
							}
						}
					}
				}
			} else {
				logger.info("No Execute job / process attachment");
			}

			// update proccess pending to timeout
			// todo algo para con la tx
			successFactorUtils.saveLoggerControl("200", "End Loop principal", UtilCodesEnum.CODE_JOB_ATTACH.getCode(), "Process");
			this.actionDocUpdateEventsTimeOut(p_timeMax);
			this.attachmentFinishProccess();
			this.deleteAllLoggerByDate();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			successFactorUtils.saveLoggerControl("200", e.getMessage(), UtilCodesEnum.CODE_JOB_ATTACH.getCode(), "Process");
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
	 * process call from EventListenerAttachjob quartz
	 * 
	 * @param Long
	 *            idJobProcess
	 */
	public void actionProcessAttachEventListSendQuartz(Long idJobProcess) {
		try {
			Date date = new Date();
			String dateSeconds = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);

			// status to load and process documents attach
			String statusToLoad = "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode() + "',"
					+ "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode() + "'";

			// -----------------------------------
			// Load Admin parameters

			// get maximun number of records to process
			Integer p_numMaxReg = 100;
			AdminParameters paramAdminCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_ATTACH.getCode());
			if (paramAdminCode != null && paramAdminCode.getValue() != null) {
				p_numMaxReg = Integer.parseInt(paramAdminCode.getValue());
			}

			// get Status to limit number of retries
			AdminParameters paramAdminRetriesCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STATUS_LIMIT_RETRIES.getCode());

			// get max number of retries
			AdminParameters paramAdminMaxRetriesCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_RETRIES.getCode());

			// define format to filename
			AdminParameters paramFormatNameFile = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_PATTERN_FILENAME_SEND_PPD.getCode());

			// get wait time maximum to process register
			Integer p_timeMax = null;
			paramAdminCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_WAITIME.getCode());
			if (paramAdminCode != null && paramAdminCode.getValue() != null) {
				p_timeMax = Integer.parseInt(paramAdminCode.getValue());
			}

			// get structure param administration
			AdminParameters paramAdminCodeStruc = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STRUCTURE_KEY.getCode());

			// -------------------------------------

			ArrayList<EventListenerDocProcess> eventListenerDocList = successFactorAdmin.eventsListenerDocProcGetAll(null, date, statusToLoad, null, UtilCodesEnum.CODE_TYPE_MODULE_ONBOARDINGV2.getCode(), p_numMaxReg);

			// register quartz event
			successFactorAdmin.eventListenerRegisterEventQuarzt(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_EVENTLISTATTACH.getCode(),
			UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), date) + " " + "number of Attachments: " + (eventListenerDocList != null ? eventListenerDocList.size() : 0));

			if (eventListenerDocList != null && eventListenerDocList.size() > 0) 
			{				
				HashMap<String,String> keysAttachsFileMap = new HashMap<>();
				successFactorUtils.saveLoggerControl("200", "Step 1 No Registros :" + eventListenerDocList.size(), UtilCodesEnum.CODE_JOB_ATTACH_EVENT_LIST.getCode(), "Started");

				// get is automatic signature
				AdminParameters paramAdminSignAuto = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_SIGNAUTO_EVENTLIST.getCode());

				// get URL Callback signature
				AdminParameters paramAdminSignCallback = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_SIGNCALLBACK_URL.getCode());

				// ---------------------------------------
				for (EventListenerDocProcess itemEvent : eventListenerDocList) {
					// update id Job Process
					itemEvent.setIdJobProcess(idJobProcess);

					// update count of retries
					itemEvent.setRetries(itemEvent.getRetries() + actionGetRetries(paramAdminRetriesCode, itemEvent.getStatus()));

					// validate retries
					if (paramAdminRetriesCode != null && paramAdminRetriesCode.getValue() != null && itemEvent != null && itemEvent.getRetries() >= Integer.parseInt(paramAdminMaxRetriesCode.getValue())) {
						// change status of attach
						itemEvent.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode());
						itemEvent.setLastUpdateOn(date);
						successFactorAdmin.eventListenerDocProcessObsUpdate(itemEvent);
					} else {
						// change status of attach
						itemEvent.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode());
						itemEvent.setLastUpdateOn(date);
						successFactorAdmin.eventListenerDocProcessObsUpdate(itemEvent);

						// --------------------------------------------------------------------------
						// is template
						if (itemEvent.getTemplate() != null) {
							if (itemEvent.getTemplate().getEventListenerParam() != null && itemEvent.getTemplate().getEventListenerParam().getEventId().equals(itemEvent.getEventListenerCtrlProc().getEventListenerParam().getEventId())) {
								// validate version of template
								if (itemEvent.getTemplate().getLatesVersion() != null && itemEvent.getTemplate().getLatesVersion() > 0) {
									// validate template filters on user
									GenResponseInfoDto resultFilters = SFTemplateFacade.templateFilterValidateOnUser(itemEvent.getTemplate().getId(), itemEvent.getEventListenerCtrlProc().getUserIdPpd(),
											itemEvent.getEventListenerCtrlProc().getStartDatePpdOnString());

									if (resultFilters.getCode().equals(UtilCodesEnum.CODE_OK.getCode())) {
										edn.cloud.business.dto.GenErrorInfoDto resultErrorInfo = successFactorUtils.generatedDocumentAutomatic(itemEvent.getTemplate().getId(), itemEvent.getEventListenerCtrlProc().getUserIdPpd(),
												paramAdminSignAuto != null && paramAdminSignAuto.getValue() != null ? paramAdminSignAuto.getValue() : "",
												paramAdminSignCallback != null && paramAdminSignCallback.getValue() != null ? UtilCodesEnum.CODE_HTTPS.getCode() + paramAdminSignCallback.getValue() : "",
												itemEvent.getEventListenerCtrlProc().getStartDatePpdOnString(), UtilCodesEnum.CODE_SOURCE_EVENTLISTENER.getCode() + " id: " + itemEvent.getEventListenerCtrlProc().getId());

										// validate if document is created
										if (resultErrorInfo.getField() != null && !resultErrorInfo.getField().equals("")) {
											successFactorUtils.saveLoggerControl("200", "Step 3 creo el documento: " + resultErrorInfo.getField(), UtilCodesEnum.CODE_JOB_ATTACH_EVENT_LIST.getCode(), "Started");

											itemEvent.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEDOCCREATE.getCode());
											itemEvent.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (itemEvent.getIdJobProcess() != null ? " idJob: " + itemEvent.getIdJobProcess() + ". " : "")
													+ "EventListenerCtrl: " + itemEvent.getEventListenerCtrlProc().getId() + ", Document Create id: " + resultErrorInfo.getField() + ". " + resultErrorInfo.getMessage()
													+ itemEvent.getEventListenerCtrlProc().getEventListenerParam().getEventId() + UtilCodesEnum.CODE_STRING_END.getCode() + itemEvent.getObservations());

										} else {
											itemEvent.setStatus(resultErrorInfo.getCode());
											itemEvent.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (itemEvent.getIdJobProcess() != null ? " idJob: " + itemEvent.getIdJobProcess() + ". " : "")
													+ resultErrorInfo.getMessage() + itemEvent.getEventListenerCtrlProc().getEventListenerParam().getEventId() + UtilCodesEnum.CODE_STRING_END.getCode() + itemEvent.getObservations());
										}

										itemEvent.setLastUpdateOn(new Date());
										successFactorAdmin.eventListenerDocProcessObsUpdate(itemEvent);
									} else {
										itemEvent.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode());
										itemEvent.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (itemEvent.getIdJobProcess() != null ? " idJob: " + itemEvent.getIdJobProcess() + ". " : "")
												+ resultFilters.getMessage() + itemEvent.getEventListenerCtrlProc().getEventListenerParam().getEventId() + UtilCodesEnum.CODE_STRING_END.getCode() + itemEvent.getObservations());
										itemEvent.setLastUpdateOn(new Date());
										successFactorAdmin.eventListenerDocProcessObsUpdate(itemEvent);
									}
								} else {
									itemEvent.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
									itemEvent.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (itemEvent.getIdJobProcess() != null ? " idJob: " + itemEvent.getIdJobProcess() + ". " : "")
											+ " the associated template must have a version greater than zero. EventId: " + itemEvent.getEventListenerCtrlProc().getEventListenerParam().getEventId() + UtilCodesEnum.CODE_STRING_END.getCode()
											+ itemEvent.getObservations());
									itemEvent.setLastUpdateOn(new Date());
									successFactorAdmin.eventListenerDocProcessObsUpdate(itemEvent);
								}
							} else {
								itemEvent.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
								itemEvent.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (itemEvent.getIdJobProcess() != null ? " idJob: " + itemEvent.getIdJobProcess() + ". " : "")
										+ "Error: EventListener associated with the template is empty or different to  " + itemEvent.getEventListenerCtrlProc().getEventListenerParam().getEventId() + UtilCodesEnum.CODE_STRING_END.getCode()
										+ itemEvent.getObservations());
								itemEvent.setLastUpdateOn(new Date());
								successFactorAdmin.eventListenerDocProcessObsUpdate(itemEvent);
							}
						}
						// --------------------------------------------------------------------------
						else {
							// process all the ONBv2 files of the employee, this to download the zip file
							// once

							//validate that file dont exist in this cycle
							if(keysAttachsFileMap.get(itemEvent.getAttachmentIdSF())!=null)
							{	
								itemEvent.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
								itemEvent.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (itemEvent.getIdJobProcess() != null ? " idJob: " + itemEvent.getIdJobProcess() + ". " : "")
										+ " document will be processed by the event Attach: " + itemEvent.getId() + " " + itemEvent.getObservations());
								itemEvent.setLastUpdateOn(new Date());
								successFactorAdmin.eventListenerDocProcessObsUpdate(itemEvent);
							}
							else if (ppdEmployeeF.wServiceEmployeeDocExistByExternalId(itemEvent.getAttachmentIdSF())) {
								keysAttachsFileMap.put(itemEvent.getAttachmentIdSF(),itemEvent.getId()+"");
								itemEvent.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
								itemEvent.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (itemEvent.getIdJobProcess() != null ? " idJob: " + itemEvent.getIdJobProcess() + ". " : "")
										+ " Document exist in Ppd by ExternaId: " + itemEvent.getAttachmentIdSF() + " " + itemEvent.getObservations());
								itemEvent.setLastUpdateOn(new Date());
								successFactorAdmin.eventListenerDocProcessObsUpdate(itemEvent);
							} else 
							{
								//add attachment id for validation (repeated file)
								keysAttachsFileMap.put(itemEvent.getAttachmentIdSF(),itemEvent.getId()+"");
								
								GenResponseInfoDto response = this.sendAttachmentEmployeeCompanyToPPD(itemEvent, paramAdminCodeStruc, paramFormatNameFile);

								if (response != null && response.getCode() != null && response.getCode().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode())) {
									itemEvent.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
									itemEvent.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (itemEvent.getIdJobProcess() != null ? " idJob: " + itemEvent.getIdJobProcess() + ". " : "")
											+ response.getMessage() + UtilCodesEnum.CODE_STRING_END.getCode() + " " + itemEvent.getObservations());
									itemEvent.setLastUpdateOn(new Date());
									successFactorAdmin.eventListenerDocProcessObsUpdate(itemEvent);
								} else if (response != null && response.getCode() != null && response.getCode().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode())) {
									itemEvent.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
									itemEvent.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (itemEvent.getIdJobProcess() != null ? " idJob: " + itemEvent.getIdJobProcess() + ". " : "")
											+ response.getMessage() + itemEvent.getObservations());
									itemEvent.setLastUpdateOn(new Date());
									successFactorAdmin.eventListenerDocProcessObsUpdate(itemEvent);
								} else {
									if (response.getMessage() != null && response.getMessage().contains(UtilCodesEnum.CODE_ERROR_409.getCode())) {
										itemEvent.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
									} else {
										itemEvent.setStatus(response.getCode());
									}

									itemEvent.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (itemEvent.getIdJobProcess() != null ? " idJob: " + itemEvent.getIdJobProcess() + ". " : "")
											+ response.getMessage() + " " + UtilCodesEnum.CODE_STRING_END.getCode() + itemEvent.getObservations());
									itemEvent.setLastUpdateOn(new Date());
									successFactorAdmin.eventListenerDocProcessObsUpdate(itemEvent);
								}
							}
						}
					}
				}
			} else {
				logger.info("No Execute job Events to process attachment");
			}

			this.actionDocUpdateEventsTimeOut(p_timeMax);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			successFactorUtils.saveLoggerControl("200", e.getMessage(), UtilCodesEnum.CODE_JOB_ATTACH_EVENT_LIST.getCode(), "Process");
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
	 * process call from EventListenerAttachjob for OMB V2 quartz *
	 * 
	 * @param Long
	 *            idJobProcess
	 * @param Long
	 *            idEventListener
	 * @param AdminParameters
	 *            paramFormatNameFile
	 */
	public void actionProcessAttachEventListSendONBV2Quartz(Long idJobProcess, Long idEventListener, AdminParameters paramFormatNameFile, String userid) {
		try {
			Date date = new Date();
			GenResponseInfoDto response = new GenResponseInfoDto();
			Boolean isFindFile = false;
			String dateSeconds = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);

			// status to load and process documents attach
			String statusToLoad = "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode() + "',"
					+ "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode() + "'";

			// -----------------------------------
			// Load Admin parameters

			// get maximun number of records to process
			Integer p_numMaxReg = null;
			AdminParameters paramAdminCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_ATTACH.getCode());
			if (paramAdminCode != null && paramAdminCode.getValue() != null) {
				p_numMaxReg = Integer.parseInt(paramAdminCode.getValue());
			}

			// load file name structure on ONBv2 [0]=numbering separator
			// [1]=separator [2]= position of element [3] = begin position of namefile
			String[] patternNameOB = new String[] { "_", ".", "0", "2" };
			AdminParameters paramAdmin = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_PATTERN_FILENAME_ONBV2.getCode());
			if (paramAdmin != null && paramAdmin.getValue() != null) {
				patternNameOB = paramAdmin.getValue().split(";");
				if (patternNameOB == null || (patternNameOB != null && patternNameOB.length < 3))
					patternNameOB = new String[] { "_", ".", "0", "2" };
			}

			// get Status to limit number of retries
			AdminParameters paramAdminRetriesCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STATUS_LIMIT_RETRIES.getCode());

			// get max number of retries
			AdminParameters paramAdminMaxRetriesCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_MAX_RETRIES.getCode());

			// get wait time maximum to process register
			Integer p_timeMax = null;
			paramAdminCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_WAITIME.getCode());
			if (paramAdminCode != null && paramAdminCode.getValue() != null) {
				p_timeMax = Integer.parseInt(paramAdminCode.getValue());
			}

			AdminParameters paramONBKey = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_OTHER_KEY_ONB.getCode());

			// get structure param administration
			AdminParameters paramAdminCodeStruc = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STRUCTURE_KEY.getCode());

			// -------------------------------------
			ArrayList<EventListenerDocProcess> eventListenerDocList = successFactorAdmin.eventsListenerDocProcGetAll(idEventListener, null, statusToLoad, UtilCodesEnum.CODE_TYPE_MODULE_ONBOARDINGV2.getCode(), null, p_numMaxReg);

			if (eventListenerDocList != null && eventListenerDocList.size() > 0) {
				// register quartz event
				successFactorAdmin.eventListenerRegisterEventQuarzt(UtilCodesEnum.CODE_PARAM_ADM_LAST_EXE_EVENTLISTATTACH.getCode(),
						UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), date) + " " + "number of Attachments: " + (eventListenerDocList != null ? eventListenerDocList.size() : 0));

				successFactorUtils.saveLoggerControl("200", "Step 1 No Registros :" + eventListenerDocList.size(), UtilCodesEnum.CODE_JOB_ATTACH_EVENT_LIST.getCode(), "Started");

				// ---------------------------------------
				// Update status of all register asociated with eventlistener / user
				EventListenerDocProcess docItemMaster = new EventListenerDocProcess();
				for (EventListenerDocProcess docItem : eventListenerDocList) {
					if (docItemMaster.getId() == null)
						docItemMaster = docItem;

					// update count of retries
					docItem.setRetries(docItem.getRetries() + actionGetRetries(paramAdminRetriesCode, docItem.getStatus()));

					// update id Job Process
					docItem.setIdJobProcess(idJobProcess);

					docItem.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode());
					docItem.setLastUpdateOn(date);
					successFactorAdmin.eventListenerDocProcessObsUpdate(docItem);
				}

				// process all the ONBv2 files of the employee, this to download the zip file
				// once
				// load zip with files from ONBV2
				ArrayList<ContentFileInfo> filesZipInfo = new ArrayList<>();
				if (paramONBKey != null && paramONBKey.getValue() != null) {
					// CALL QUERY BUILDER

					String[] keys = paramONBKey.getValue().split("@@");
					Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();

					map.put("key", new ResultBuilderDto(keys[0], "default", ""));
					Map<String, ResultBuilderDto> mapRes = QueryBuilder.getInstance().convert(map, userid, "");

					String keyToShare = mapRes.get("key").getResult().replaceAll("-", "") + "@@" + keys[1];
					if (!mapRes.get("key").getResult().equals("")) {
						filesZipInfo = SFOnboardingFacade.getAttachNameFromOnboardingV2Key(docItemMaster.getFieldMapPpd().getNameDestination(), patternNameOB, userid, true, keyToShare);
					}
				} else {
					filesZipInfo = SFOnboardingFacade.getAttachNameFromOnboardingV2(docItemMaster.getFieldMapPpd().getNameDestination(), patternNameOB, docItemMaster.getUserIdOtherPlat(), true);
				}

				// ---------------------------------------
				for (EventListenerDocProcess docItem : eventListenerDocList) {
					// change status of attach
					docItem.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode());
					docItem.setLastUpdateOn(date);
					successFactorAdmin.eventListenerDocProcessObsUpdate(docItem);

					// validate retries
					if (paramAdminRetriesCode != null && paramAdminRetriesCode.getValue() != null && docItem != null && docItem.getRetries() > Integer.parseInt(paramAdminMaxRetriesCode.getValue())) {
						// change status of attach
						docItem.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode());
						docItem.setLastUpdateOn(date);
						successFactorAdmin.eventListenerDocProcessObsUpdate(docItem);
					} else {
						// --------------------------------------------------------------------------
						if (filesZipInfo != null && filesZipInfo.size() > 0) {
							try {
								// find document
								isFindFile = false;
								for (ContentFileInfo fileZip : filesZipInfo) {
									if (fileZip.getFileName().equals(docItem.getAttachmentFileName())) {
										isFindFile = true;
										if (ppdEmployeeF.wServiceEmployeeDocExistByExternalId(docItem.getAttachmentIdSF())) {
											docItem.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
											docItem.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (docItem.getIdJobProcess() != null ? " idJob: " + docItem.getIdJobProcess() + ". " : "")
													+ " Document exist in Ppd by ExternaId: " + docItem.getAttachmentIdSF() + " " + docItem.getObservations());
											docItem.setLastUpdateOn(new Date());
											successFactorAdmin.eventListenerDocProcessObsUpdate(docItem);
										} else {
											// ---------------------------------
											// load structure by user
											String[] structure;
											if (paramAdminCodeStruc != null && paramAdminCodeStruc.getValue() != null) {
												// Check organizations to compare with user
												StructureBuilder structureBuilder = new StructureBuilder();
												structure = structureBuilder.getStructureAttach(docItem.getEventListenerCtrlProc().getUserIdPpd(), docItem.getEventListenerCtrlProc().getUserExtendPpd(), paramAdminCodeStruc.getValue(),
														docItem.getEventListenerCtrlProc().getStartDatePpdOnString());
											} else {
												structure = null;
											}

											// -------------------------------------
											// load document type from admin parameters
											FieldsMappingPpd fieldTypeDocument = docItem.getFieldMapPpd();

											if (!(fieldTypeDocument != null && fieldTypeDocument.getParameters() != null && !fieldTypeDocument.getParameters().equals(""))) {
												response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
												response.setMessage("Error: Type Document in field Mapping not exist "
														+ (fieldTypeDocument != null ? ", Mapping Source: " + fieldTypeDocument.getNameSource() + " Module: (" + fieldTypeDocument.getTypeModule() + ") " : " " + ". ")
														+ UtilCodesEnum.CODE_STRING_END.getCode());
											} else {
												// ---------------------------------------
												// get document type
												if (docItem.getFieldMapPpdDest() == null)
													docItem.setFieldMapPpdDest(fieldTypeDocument);

												// ---------------------------------------
												// build Metadata parameters
												HashMap<String, String> metadata = this.attachmentGetMetadataValues(docItem.getEventListenerCtrlProc().getUserIdPpd(),
														successFactorAdmin.mappingPpdFieldMetaGetByIdFieldMapp(docItem.getFieldMapPpdDest().getId()), docItem.getEventListenerCtrlProc().getStartDatePpdOnString());

												response = SFOnboardingFacade.uploadDocumentEmployeeOnboardingV2(docItem.getEventListenerCtrlProc().getUserIdPpd(), docItem.getFieldMapPpdDest().getParameters(), fileZip, structure, metadata,
														paramFormatNameFile);
											}

											if (response != null && response.getCode() != null && response.getCode().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode())) {
												docItem.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
												docItem.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (docItem.getIdJobProcess() != null ? " idJob: " + docItem.getIdJobProcess() + ". " : "")
														+ response.getMessage() + UtilCodesEnum.CODE_STRING_END.getCode() + " " + docItem.getObservations());
												docItem.setLastUpdateOn(new Date());
												successFactorAdmin.eventListenerDocProcessObsUpdate(docItem);
											} else if (response != null && response.getCode() != null && response.getCode().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode())) {
												docItem.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
												docItem.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (docItem.getIdJobProcess() != null ? " idJob: " + docItem.getIdJobProcess() + ". " : "")
														+ response.getMessage() + docItem.getObservations());
												docItem.setLastUpdateOn(new Date());
												successFactorAdmin.eventListenerDocProcessObsUpdate(docItem);
											} else {
												if (response.getMessage() != null && response.getMessage().contains(UtilCodesEnum.CODE_ERROR_409.getCode())) {
													docItem.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode());
												} else {
													docItem.setStatus(response.getCode());
												}

												docItem.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (docItem.getIdJobProcess() != null ? " idJob: " + docItem.getIdJobProcess() + ". " : "")
														+ response.getMessage() + " " + UtilCodesEnum.CODE_STRING_END.getCode() + docItem.getObservations());
												docItem.setLastUpdateOn(new Date());
												successFactorAdmin.eventListenerDocProcessObsUpdate(docItem);
											}
										}
									}
								}
							} catch (Exception e) {
								docItem.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
								docItem.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (docItem.getIdJobProcess() != null ? " idJob: " + docItem.getIdJobProcess() + ". " : "")
										+ ("Error process File in zip. error: " + e.getMessage()) + " " + UtilCodesEnum.CODE_STRING_END.getCode() + docItem.getObservations());
								docItem.setLastUpdateOn(new Date());
								successFactorAdmin.eventListenerDocProcessObsUpdate(docItem);
								e.printStackTrace();
							}

							if (!isFindFile) {
								docItem.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
								docItem.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (docItem.getIdJobProcess() != null ? " idJob: " + docItem.getIdJobProcess() + ". " : "")
										+ ("File not found zip by name: " + docItem.getAttachmentFileName()) + " " + UtilCodesEnum.CODE_STRING_END.getCode() + docItem.getObservations());
								docItem.setLastUpdateOn(new Date());
								successFactorAdmin.eventListenerDocProcessObsUpdate(docItem);
							}

						} else {
							docItem.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
							docItem.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n" + (docItem.getIdJobProcess() != null ? " idJob: " + docItem.getIdJobProcess() + ". " : "")
									+ "Error not content file in zip (ONBv2) " + UtilCodesEnum.CODE_STRING_END.getCode() + docItem.getObservations());
							docItem.setLastUpdateOn(new Date());
							successFactorAdmin.eventListenerDocProcessObsUpdate(docItem);
						}
					}
				}
			} else {
				logger.info("No Execute job Events to process attachment");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			successFactorUtils.saveLoggerControl("200", e.getMessage(), UtilCodesEnum.CODE_JOB_ATTACH_EVENT_LIST.getCode(), "Process");
		}
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * save the templates to be processed for a user
	 * 
	 * @param EventListenerCtrlProcesseventProc
	 */
	public void attachmentGetSaveTemplate(EventListenerCtrlProcess eventProc, String userTarget) {
		ArrayList<Template> templatesListEventListener = SFTemplateFacade.templateListByEventList(eventProc.getEventListenerParam().getId());

		if (templatesListEventListener != null) {
			for (Template tmp : templatesListEventListener) {
				if (tmp.getLatesVersion() != null && tmp.getLatesVersion() > 0) {
					GenResponseInfoDto response = SFTemplateFacade.templateFilterValidateOnUser(tmp.getId(), userTarget, eventProc.getStartDatePpdOnString());
					if (response.getCode().equals(UtilCodesEnum.CODE_OK.getCode())) {
						successFactorUtils.saveLoggerControl("200", "Inicia inserta doc process: " + eventProc.getId() + "para id ctrl even " + eventProc.getId(), "JobAttachQ", "Process");
						EventListenerDocProcess docItem = new EventListenerDocProcess();
						docItem.setCreateOn(new Date());
						docItem.setEventListenerCtrlProc(eventProc);
						docItem.setTemplate(tmp);
						docItem.setAttachmentFileName("Template: " + tmp.getTitle());
						docItem.setStatus(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());
						docItem.setUserCountry(eventProc!=null&&eventProc.getUserCountry()!=null?eventProc.getUserCountry():"");

						docItem = successFactorAdmin.eventListenerDocProcessInsert(docItem);
						successFactorUtils.saveLoggerControl("200", "Inicia inserta doc process" + docItem.getId() + "para id ctrl even " + eventProc.getId(), "JobAttachQ", "Process");
					} else {
						successFactorUtils.saveLoggerControl("200", response.getMessage(), "JobAttachTempl", "Process");
					}
				}
			}
		}
	}

	/***
	 * 
	 * 
	 * 
	 * 
	 * save the documents to be processed for a user
	 * 
	 * @param EventListenerCtrlProcess eventProc
	 * @param Boolean proccessONB
	 * @param Boolean proccessREC
	 * @return ResponseGenericDto
	 */
	public ResponseGenericDto attachmentSearchModuleRC_ONB(EventListenerCtrlProcess eventProc, Boolean v2) {
		ResponseGenericDto response = new ResponseGenericDto();

		AdminParameters paramONBKey = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_OTHER_KEY_ONB.getCode());

		ArrayList<FieldsMappingPpd> fieldsUserList = successFactorAdmin.mappingPpdFieldsGetAll(true, UtilCodesEnum.CODE_NOTNULL.getCode(), Boolean.TRUE, Boolean.FALSE);
		ArrayList<FieldsMappingPpd> fieldsMappingONBV2 = successFactorAdmin.mappingPpdFieldsGetAll(true, UtilCodesEnum.CODE_TYPE_MODULE_ONBOARDINGV2.getCode(), Boolean.TRUE, Boolean.FALSE);

		// validate attachment in load massive
		Boolean proccessONB = false;
		Boolean proccessREC = false;
		if (eventProc.getFkMassiveLoad() != null && eventProc.getFkMassiveLoad().getAttachTypes() != null) {
			proccessONB = eventProc.getFkMassiveLoad().getAttachTypes().contains(UtilCodesEnum.CODE_TYPE_MODULE_ONBOARDING.getCode());
			proccessREC = eventProc.getFkMassiveLoad().getAttachTypes().contains(UtilCodesEnum.CODE_TYPE_MODULE_RECRUITMENT.getCode());
		} else if (eventProc.getFkMassiveLoad() == null) {
			proccessONB = true;
			proccessREC = true;
		}

		// search email of user in recruiting module
		if (fieldsUserList != null) {
			String infoUserRecruiting = successFactorUtils.getUserGetProfileRecruitingModule(eventProc.getUserIdPpd());
			String mailUserRecruiting = UtilMapping.loadUserInfoRecruitingForJson(infoUserRecruiting, QueryOdataDAO.QUERY_EMPLOYEE_PATH_MAIL_RECRUITING);

			Boolean isOnboarding = false;
			for (FieldsMappingPpd field : fieldsUserList) {
				// validate only fields attached
				if (field.getIsAttached()) {
					// ----------------------
					// Module Recrutamiento
					if (proccessREC && field.getTypeModule().equals(UtilCodesEnum.CODE_TYPE_MODULE_RECRUITMENT.getCode()) && eventProc.getEventListenerParam().getIsHire()) {
						if ((mailUserRecruiting != null && !mailUserRecruiting.equals("")) || v2 == true) {
							ArrayList<SlugItem> listFiles = succesFactorI.getAttachmentListRecruitingModule(field.getNameSource(), mailUserRecruiting, v2, eventProc.getUserIdPpd());

							if (listFiles != null && listFiles.size() > 0) {
								for (SlugItem item : listFiles) {
									EventListenerDocProcess docItem = new EventListenerDocProcess();
									docItem.setFieldMapPpd(field);
									docItem.setAttachmentFileName(item.getValue().toString());
									docItem.setAttachmentIdSF(item.getSlug());
									docItem.setCreateOn(new Date());
									docItem.setUserCountry(eventProc!=null&&eventProc.getUserCountry()!=null?eventProc.getUserCountry():"");
									docItem.setEventListenerCtrlProc(eventProc);
									docItem.setStatus(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());
									docItem = successFactorAdmin.eventListenerDocProcessInsert(docItem);
								}
							}
						}
					}
					// ----------------------
					// Module Onboarding v1
					else if (proccessONB && field.getTypeModule().equals(UtilCodesEnum.CODE_TYPE_MODULE_ONBOARDING.getCode()) && eventProc.getEventListenerParam().getIsHire()) {
						logger.info("you are here we need to add the document to the pool");
						isOnboarding = true;
					}
					// ----------------------
					// Module Onboarding v2
					else if (proccessONB && field.getTypeModule().equals(UtilCodesEnum.CODE_TYPE_MODULE_ONBOARDINGV2.getCode()) && eventProc.getEventListenerParam().getIsHire()) {
						if (!field.getNameSource().contains(UtilCodesEnum.CODE_SEPARATOR_OPERATION.getCode() + "" + UtilCodesEnum.CODE_OPERATION_REF.getCode())
								&& !field.getNameSource().contains(UtilCodesEnum.CODE_SEPARATOR_OPERATION.getCode() + "" + UtilCodesEnum.CODE_OPERATION_REF_DIS.getCode())) {
							// load file name structure on ONBv2 [0]=numbering separator 
							// [1]=separator [2]= position of element [3] = begin position of namefile
							
							
							logger.info("WE GO TO THIS SUPER PART");
							
							String[] patternNameOB = new String[] { "_", ".", "0", "2" };
							AdminParameters paramAdmin = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_PATTERN_FILENAME_ONBV2.getCode());
							if (paramAdmin != null && paramAdmin.getValue() != null) {
								patternNameOB = paramAdmin.getValue().split(";");
								if (patternNameOB == null || (patternNameOB != null && patternNameOB.length < 3))
									patternNameOB = new String[] { "_", ".", "0", "2" };
							}

							String kmsUser = SFOnboardingFacade.getUserInfoOnboardingModuleV2(eventProc.getUserIdPpd());
							if (kmsUser != null) {
								SFOnboardingFacade.saveEventListenerDocFromOnboardingV2(eventProc, field, kmsUser, patternNameOB, fieldsMappingONBV2);
							} else {
								if (paramONBKey != null && paramONBKey.getValue() != null) {
									// CALL QUERY BUILDER

									String[] keys = paramONBKey.getValue().split("@@");
									Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();

									map.put("key", new ResultBuilderDto(keys[0], "default", ""));
									Map<String, ResultBuilderDto> mapRes = QueryBuilder.getInstance().convert(map, eventProc.getUserIdPpd(), "");

									String keyToShare = mapRes.get("key").getResult().replaceAll("-", "") + "@@" + keys[1];
									if (!mapRes.get("key").getResult().equals("")) {
										SFOnboardingFacade.saveEventListenerDocFromOnboardingV2Key(eventProc, field, eventProc.getUserIdPpd(), patternNameOB, fieldsMappingONBV2, keyToShare);

									} else {
										response.setCode(UtilCodesEnum.CODE_ERROR.getCode());
										response.setMessage("not found kms user ONBv2 for user: " + eventProc.getUserIdPpd());
									}

								} else {
									response.setCode(UtilCodesEnum.CODE_ERROR.getCode());
									response.setMessage("not found kms user ONBv2 for user: " + eventProc.getUserIdPpd());
								}

							}
						}
					}
				}
			}

			if (isOnboarding) {
				JSONArray listOnbFiles = succesFactorI.getAttachmentListOnboardingModule(eventProc.getUserExtendPpd());

				if (listOnbFiles != null && listOnbFiles.length() > 0) {
					for (int i = 0; i < listOnbFiles.length(); i++) {
						JSONObject job = listOnbFiles.getJSONObject(i);

						logger.info("you have a file to add to your config :" + job.getString("AttachmentName"));

						FieldsMappingPpd field = null;
						FieldsMappingPpd def = null;
						for (FieldsMappingPpd fieldTmp : fieldsUserList) {
							if (job.getString("FormCode").equals(fieldTmp.getNameSource())) {
								field = fieldTmp;
							}
							if (fieldTmp.getNameSource().equals("Default")) {
								def = fieldTmp;
							}
						}

						if (field != null) {
							EventListenerDocProcess docItem = new EventListenerDocProcess();
							docItem.setFieldMapPpd(field);
							docItem.setAttachmentFileName(job.getString("AttachmentName"));
							docItem.setAttachmentIdSF(job.getString("AttachmentId"));
							docItem.setCreateOn(new Date());
							docItem.setUserCountry(eventProc!=null&&eventProc.getUserCountry()!=null?eventProc.getUserCountry():"");
							docItem.setEventListenerCtrlProc(eventProc);
							docItem.setStatus(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());
							

							docItem = successFactorAdmin.eventListenerDocProcessInsert(docItem);
						} else if (def != null) {
							EventListenerDocProcess docItem = new EventListenerDocProcess();
							docItem.setFieldMapPpd(def);
							docItem.setAttachmentFileName(job.getString("AttachmentName"));
							docItem.setAttachmentIdSF(job.getString("AttachmentId"));
							docItem.setCreateOn(new Date());
							docItem.setUserCountry(eventProc!=null&&eventProc.getUserCountry()!=null?eventProc.getUserCountry():"");
							docItem.setEventListenerCtrlProc(eventProc);
							docItem.setStatus(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());

							docItem = successFactorAdmin.eventListenerDocProcessInsert(docItem);
						} else {
							successFactorUtils.saveLoggerControl("404", "Onboarding", "Content by default not configured in Onboarding", "ERROR");
						}
					}
				}
			} else {
				successFactorUtils.saveLoggerControl("200", "Onboarding", "Module not configured", "Initialize");
			}

		}

		return response;
	}

	/**
	 * 
	 * 
	 * 
	 * finish process for attachments already process
	 */
	public void attachmentFinishProccess() {
		// status to load and process documents attach
		String statusToLoad = "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode() + "'," + "'"
				+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEDOCCREATE.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode() + "'";

		ArrayList<EventListenerDocProcess> eventListenerDocList = successFactorAdmin.getAllAttachmentProcess(null, statusToLoad, null, null);

		if (eventListenerDocList != null) {
			for (EventListenerDocProcess item : eventListenerDocList) {
				successFactorAdmin.eventListenerDocProcessDelete(item);
			}

			// save attachment in historial
			try {
				this.attachmentHistoCreate(eventListenerDocList);
			} catch (Exception e) {
				successFactorUtils.saveLoggerControl("400", "Create historial to event " + e.getMessage(), UtilCodesEnum.CODE_JOB_SIGNATURE.getCode(), "Error");
			}
		}

	}

	/**
	 * create historial to attachments
	 * 
	 * @param PpdCreateEmployeeInfoDto
	 *            employeeUpdate
	 * @param ArrayList<EventListenerDocProcess>
	 *            attachList
	 */
	public void attachmentHistoCreate(ArrayList<EventListenerDocProcess> attachList) {
		Date date = new Date();
		String dateSeconds = "Time Execution " + UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);

		// -------------------------------------------------
		// add event to history events
		if (attachList != null) {
			for (EventListenerDocProcess attach : attachList) {
				if (attach != null && attach.getId() != null) {
					EventListenerDocHistory attachHisto = new EventListenerDocHistory();
					attachHisto.setAttachmentFileName(attach.getAttachmentFileName());
					attachHisto.setAttachmentIdSF(attach.getAttachmentIdSF());
					attachHisto.setCreateOn(new Date());
					
					if(attach.getFieldMapPpdDest()!=null && attach.getFieldMapPpdDest().getId()!=null) {
						attachHisto.setIdFieldMapPpdDest(attach.getFieldMapPpdDest().getId());
					}
					
					if(attach.getFieldMapPpd()!=null && attach.getFieldMapPpd().getId()!=null) {
						attachHisto.setIdFieldMapPpd(attach.getFieldMapPpd().getId());
					}
					
					attachHisto.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + dateSeconds + " " + attach.getObservations() + UtilCodesEnum.CODE_STRING_END.getCode());
					attachHisto.setStatus(attach.getStatus());
					attachHisto.setRetries(attach.getRetries());

					attachHisto.setStartDatePpdOnAttach(attach.getStartDatePpdOnAttach());
					attachHisto.setUserExtendPpd(attach.getUserExtendPpd());
					attachHisto.setUserIdPpd(attach.getUserIdPpd());
					attachHisto.setUserCountry(attach.getUserCountry());
					

					// insert histo attachs processed
					successFactorAdmin.eventListenerDocHistoInsert(attachHisto);
				}
			}
		}
	}

	/**
	 * build metadata values for document
	 * 
	 * @param ArrayList<FieldsMappingMeta>
	 *            fields
	 * @param String
	 *            userTarget
	 * @param String
	 *            effectiveDate
	 * @return HashMap<String,String>
	 */
	public HashMap<String, String> attachmentGetMetadataValues(String userTarget, ArrayList<FieldsMappingMeta> fields, String effectiveDate) {
		HashMap<String, String> mapReturn = new HashMap<String, String>();
		if (fields != null && fields.size() > 0) {
			Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();

			for (FieldsMappingMeta itemFields : fields) {
				if (itemFields.getFieldsTemplateLibrary() != null && itemFields.getFieldsTemplateLibrary().getNameSource() != null) {
					String nameKey = itemFields.getFieldsTemplateLibrary().getNameSource();
					if (nameKey.contains(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()) && nameKey.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()).length > 1)
						nameKey = nameKey.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[0];

					if (itemFields.getFieldsTemplateLibrary() != null && itemFields.getFieldsTemplateLibrary().getIsConstants())
						mapReturn.put(nameKey, itemFields.getFieldsTemplateLibrary().getNameDestination());
					else
						map.put(nameKey, new ResultBuilderDto(itemFields.getFieldsTemplateLibrary().getNameDestination(), "default", ""));
				}
			}

			Map<String, ResultBuilderDto> dataMap = QueryBuilder.getInstance().convert(map, userTarget, effectiveDate != null ? effectiveDate : "");
			if (dataMap != null && dataMap.size() > 0) {
				for (Map.Entry<String, ResultBuilderDto> pair : dataMap.entrySet()) {
					if (pair.getValue().getResult() != null && !pair.getValue().getResult().equals(UtilCodesEnum.CODE_QUERYBUILDER_INVALID.getCode())) {
						mapReturn.put(pair.getKey(), pair.getValue().getResult());
					}
				}

				return mapReturn;
			}
		}

		if (mapReturn != null && mapReturn.size() > 0)
			return mapReturn;

		return null;
	}

	/**
	 * 
	 * 
	 * 
	 * Get all document attachment
	 * 
	 * @return Collection<EventListenerDocHistory>
	 */
	public ArrayList<EventListenerDocHistory> getAllAttachmentHisto() {
		return succesFactorAttachI.getAllAttachmentHisto();
	}

	/***
	 * 
	 * 
	 * delete all attachments History
	 */
	public void deleteAllAttachmentHisto() {
		succesFactorAttachI.deleteAllAttachmentHisto();
	}
	
	/**
	 * get status count 
	 * @param filter 
	 * @return List<String[]>
	 * @return
	 */
	public List<Object[]> attachmentHistoGetStatusCount(FilterQueryDto filter){
		return succesFactorAttachI.attachmentHistoGetStatusCount(filter);
	}

	/**
	 * removes all logger records with creation date less than the filter date
	 */
	private void deleteAllLoggerByDate() {
		try {
			// get date filter to delete loggers
			AdminParameters paramIntervalTime = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_MAX_DAYS_STORE_LOGG_RECORD.getCode());

			Long intervalTime = -1L;
			if (paramIntervalTime != null && paramIntervalTime.getValue() != null && !paramIntervalTime.getValue().equals("")) {
				try {
					intervalTime = Long.parseLong(paramIntervalTime.getValue());
				} catch (Exception e) {
					intervalTime = -1L;
				}
			}

			if (intervalTime > 0) {
				// add minutes to end date
				Date filterDate = UtilDateTimeAdapter.getDateAddMinutes(new Timestamp((new Date()).getTime()), intervalTime * -1);
				successFactorAdmin.loggerDeleteByDate(filterDate);
				successFactorAdmin.jobLogDeleteByDate(filterDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * validate country user and filter Attachments
	 * @param Date endDateQuery
	 * @param String effectiveDate
	 * @param ArrayList<EventListenerDocProcess> listFiles
	 * @param List<Countries> listCountriesConf
	 * @return ArrayList<EventListenerDocProcess>
	 */
	private ArrayList<EventListenerDocProcess> actionValidateFiltersAttachs(Date endDateQuery, String effectiveDate, ArrayList<EventListenerDocProcess> listFiles, List<Countries> listCountriesConf) 
	{
		ArrayList<EventListenerDocProcess> returnList = new ArrayList<>();
		if (listFiles != null && listFiles.size() > 0) 
		{
			for (EventListenerDocProcess docItem : listFiles) 
			{
				if (docItem.getUserIdPpd() != null && !docItem.getUserIdPpd().equals("")) 
				{
					String concReturn = UtilCodesEnum.CODE_FORMAT_STRUCTURE_USER.getCode();
					AdminParameters paramAdmUserid = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_USERID.getCode());
					if (paramAdmUserid != null) {
						concReturn = paramAdmUserid.getValue();
					}
					
					//validate Employee Country
					ArrayList<PSFStructureEmployeeDto> response = successFactorUserFacade.actionValidateStructureActive(docItem.getUserIdPpd(), effectiveDate, listCountriesConf, concReturn, false);
						
					// validates that the user has a configured structure and that some of them can process files
					if (response != null && response.size()>0 && response.get(0).getIsActiveStructureConfPSF() && response.get(0).getIsAllowAttachConfPSF() != null && response.get(0).getIsAllowAttachConfPSF()) 
					{
						//validate date entry of employee
						if(response.get(0).getEntryDateEmployee()!=null && !response.get(0).getEntryDateEmployee().equals(""))
						{	
							Date effectiveDateV = UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(), response.get(0).getEntryDateEmployee());
							
							if(endDateQuery!=null && effectiveDateV!=null && endDateQuery.compareTo(effectiveDateV)>=0){
								docItem.setUserCountry(UtilMapping.getCountryListFromList(response));
								returnList.add(docItem);
							}
						}
						else
						{						
							docItem.setUserCountry(UtilMapping.getCountryListFromList(response));
							returnList.add(docItem);
						}
					}					
				}
			}
		}

		return returnList;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Updates records documents that have exceeded the maximum wait time
	 * 
	 * @param String
	 *            statusToLoad
	 * @param String
	 *            statusDestination
	 * @param Integer
	 *            timeOutMinutes
	 */
	private void actionDocUpdateEventsTimeOut(Integer p_timeMax) {

		if (p_timeMax != null) {
			// update events that have exceeded the maximun wait time
			successFactorAdmin.eventListenerDocUpdatetToStatusTimeOut("'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode() + "'", UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode(), p_timeMax);
		}
	}

	/**
	 * 
	 * 
	 * 
	 * update employee in ppd
	 * 
	 * 
	 * @param EventListenerDocProcess
	 *            itemAttachment
	 * @param ArrayList<FieldsMappingPpd>
	 *            fieldsMapUserList
	 * @param AdminParameters
	 *            paramAdmStructure)
	 **/
	private void actionProccessUpdateEmployee(EventListenerDocProcess itemAttachment, ArrayList<FieldsMappingPpd> fieldsMapUserList, AdminParameters paramAdmStructure, AdminParameters paramAdmUserid) {
		Date date = new Date();
		String dateUpdateCreate = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(), new Date()) + "T" + UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_HOUR.getCode(), new Date());
		String dateSeconds = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);

		PpdCoreEmployeeInfoDto employeeUpdate = new PpdCoreEmployeeInfoDto();

		// get values for employee to update in people doc
		employeeUpdate = ppdUserUtilF.actionUserGetValueQueryBuilder(itemAttachment.getUserIdPpd(), fieldsMapUserList, paramAdmStructure, paramAdmUserid, false, "");
		
		employeeUpdate.setUpdated_at(dateUpdateCreate);
		PpdCoreEmployeeInfoDto[] coreEmplSearch = ppdApiUtilsF.wServiceSearchEmployee(itemAttachment.getUserIdPpd());
		if (coreEmplSearch != null && coreEmplSearch.length > 0) {
			employeeUpdate.setId(coreEmplSearch[0].getId());
			employeeUpdate.setCreated_at(coreEmplSearch[0].getCreated_at());
		}

		// update information employee in success factor
		employeeUpdate.setTerminated(false);
		employeeUpdate = ppdEmployeeF.actionUpdateEmployee(employeeUpdate);

		if (employeeUpdate != null && employeeUpdate.getErrors() != null && employeeUpdate.getErrors().length > 0) {
			String resumeErrors = "";
			for (int i = 0; i < employeeUpdate.getErrors().length; i++) {
				if (employeeUpdate.getErrors()[i] != null) {
					resumeErrors += "Error_"+(i+1)+": "+employeeUpdate.getErrors()[i].getCode() + " " + employeeUpdate.getErrors()[i].getField() + " " + employeeUpdate.getErrors()[i].getMessage() + ". ";
				}
			}

			itemAttachment.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + dateSeconds + "\n" + resumeErrors + " " + UtilCodesEnum.CODE_STRING_END.getCode() + " " + itemAttachment.getObservations());
			itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
			itemAttachment.setLastUpdateOn(new Date());
			successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);
		} else {
			if (employeeUpdate.getObservations() != null && !employeeUpdate.getObservations().equals("")) {
				itemAttachment.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ": " + dateSeconds + "\n " + employeeUpdate.getObservations() + UtilCodesEnum.CODE_STRING_END.getCode() + " " + itemAttachment.getObservations());
			}

			itemAttachment.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
			itemAttachment.setLastUpdateOn(new Date());
			successFactorAdmin.eventListenerDocProcessObsUpdate(itemAttachment);
		}
	}

	/**
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
	 * send to ppd attachment
	 * @param EventListenerDocProcess itemEvent
	 * @param AdminParameters paramAdminCodeStruc
	 * @param AdminParameters paramFormatNameFile
	 * @return GenErrorInfoDto
	 */
	private GenResponseInfoDto sendAttachmentEmployeeCompanyToPPD(EventListenerDocProcess itemEvent, AdminParameters paramAdminCodeStruc, AdminParameters paramFormatNameFile) 
	{
		GenResponseInfoDto response = new GenResponseInfoDto();
		Date date = new Date();
		String dateSeconds = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);
		SFAttachResponseN1Dto attachResponseDto = null;

		// load structure
		String[] structure;
		if (paramAdminCodeStruc != null && paramAdminCodeStruc.getValue() != null) 
		{
			// Check organizations to compare with user
			StructureBuilder structureBuilder = new StructureBuilder();
			structure = structureBuilder.getStructureAttach(itemEvent.getEventListenerCtrlProc().getUserIdPpd(), itemEvent.getEventListenerCtrlProc().getUserExtendPpd(), paramAdminCodeStruc.getValue(),
					itemEvent.getEventListenerCtrlProc().getStartDatePpdOnString());
		} else {
			structure = null;
		}

		// load document type from admin parameters
		FieldsMappingPpd fieldTypeDocument = itemEvent.getFieldMapPpd();

		//validate type Document
		if (!(fieldTypeDocument != null && fieldTypeDocument.getParameters() != null && !fieldTypeDocument.getParameters().equals(""))) {
			response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
			response.setMessage("Error: Type Document in field Mapping not exist "+ (fieldTypeDocument != null ? ", Mapping Source: " + fieldTypeDocument.getNameSource() + " Module: (" + fieldTypeDocument.getTypeModule() + ") " : " " + ". ") + UtilCodesEnum.CODE_STRING_END.getCode());
			return response;
		}

		// -----------------------------
		//load data attachment
		if (itemEvent.getFieldMapPpd().getTypeModule().equals(UtilCodesEnum.CODE_TYPE_MODULE_ONBOARDING.getCode())) {
			attachResponseDto = succesFactorI.getAttachContentFromOnboarding(itemEvent.getAttachmentIdSF());
		} else {
			attachResponseDto = succesFactorI.getAttachContentFromAllModule(itemEvent.getAttachmentIdSF());
		}

		if (attachResponseDto != null && attachResponseDto.getD() != null && attachResponseDto.getD().getFileContent() != null) 
		{
			// build Metadata parameters
			HashMap<String, String> metadata = this.attachmentGetMetadataValues(itemEvent.getEventListenerCtrlProc().getUserIdPpd(), successFactorAdmin.mappingPpdFieldMetaGetByIdFieldMapp(fieldTypeDocument.getId()),itemEvent.getEventListenerCtrlProc().getStartDatePpdOnString());

			// call webservice upload employee document company
			response = ppdEmployeeF.wServiceUploadEmployeeDocCompany(itemEvent.getEventListenerCtrlProc().getUserIdPpd(), attachResponseDto, fieldTypeDocument.getParameters(), structure, metadata, paramFormatNameFile);
			return response;
		}
		else 
		{
			if (attachResponseDto != null && attachResponseDto.getError() != null) 
			{
				response.setMessage("Error " + attachResponseDto.getError().getCode() + " " + attachResponseDto.getError().getMessage().getValue() + UtilCodesEnum.CODE_STRING_END.getCode());
			}

			response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode());
		}

		return response;
	}

	/**
	 * process batch of attachments
	 * 
	 * @param EventListenerDocProcess
	 *            itemEvent
	 * @param AdminParameters
	 *            paramAdminCodeStruc
	 * @param String
	 *            dateSeconds
	 * @param String
	 *            statusToLoad
	 */
	private void processBatchAttachONBV2(EventListenerDocProcess itemDocEvent, AdminParameters paramAdminCodeStruc, String dateSeconds, String statusToLoad) {
		Boolean isFindFile = false;
		Date date = new Date();
		GenResponseInfoDto response = new GenResponseInfoDto();
		ArrayList<EventListenerDocProcess> eventListenerDocList = successFactorAdmin.eventsListenerDocProcGetAll(itemDocEvent.getEventListenerCtrlProc().getId(), null, statusToLoad, UtilCodesEnum.CODE_TYPE_MODULE_ONBOARDINGV2.getCode(),
				null, null);

		if (eventListenerDocList != null && eventListenerDocList.size() > 0) {

		}
	}
}
