package edn.cloud.sfactor.business.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.GenErrorInfoDto;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.integration.FolderDTO;
import edn.cloud.business.dto.integration.SFAdmin;
import edn.cloud.business.dto.integration.SFRoleList;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.odata.UserManager;
import edn.cloud.business.dto.ppd.api.PpdEmployeeInfo_v1Dto;
import edn.cloud.ppdoc.business.facade.PpdTemplateFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorTemplateFacade;
import edn.cloud.sfactor.business.impl.SuccessFactorAdminImpl;
import edn.cloud.sfactor.business.impl.SuccessFactorImpl;
import edn.cloud.sfactor.business.interfaces.SuccessFactorAdmin;
import edn.cloud.sfactor.business.interfaces.SuccessFactor;
import edn.cloud.sfactor.persistence.dao.FolderDAO;
import edn.cloud.sfactor.persistence.dao.FolderUserDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Countries;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerDocHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerParam;
import edn.cloud.sfactor.persistence.entities.FieldsMappingMeta;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;
import edn.cloud.sfactor.persistence.entities.FieldsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.Folder;
import edn.cloud.sfactor.persistence.entities.FolderUser;
import edn.cloud.sfactor.persistence.entities.Language;
import edn.cloud.sfactor.persistence.entities.LookupTable;
import edn.cloud.sfactor.persistence.entities.ManagerRole;
import edn.cloud.sfactor.persistence.entities.ManagerRoleGroup;
import edn.cloud.sfactor.persistence.entities.MassiveLoadUser;
import edn.cloud.sfactor.persistence.entities.RolesMappingPpd;
import edn.cloud.sfactor.persistence.entities.SfEntity;
import edn.cloud.sfactor.persistence.entities.SignsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.StructureBusiness;
import edn.cloud.sfactor.persistence.entities.Template;
import edn.cloud.sfactor.persistence.entities.TemplateFilters;

public class SuccessFactorAdminFacade {
	private UtilLogger logger = UtilLogger.getInstance();
	private SuccessFactorAdmin successFactorAdmin = new SuccessFactorAdminImpl();
	private SuccessFactor successFactor = new SuccessFactorImpl();
	private PpdTemplateFacade templateFacade = new PpdTemplateFacade();
	private PpdTemplateFacade ppdTemplateGeneratorFacade = new PpdTemplateFacade();	
	// -----------------------------------------

	/**
	 * get admin parameters by name code
	 * 
	 * @param String
	 *            nameCode
	 * @return AdminParameters
	 */
	public AdminParameters adminParamGetByNameCode(String nameCode) {
		return successFactorAdmin.adminParamGetByNameCode(nameCode);
	}

	/**
	 * insert admin parameters
	 * 
	 * @param AdminParameters
	 *            param
	 * @return AdminParameters
	 */
	public AdminParameters adminParamInsert(AdminParameters param) {
		return successFactorAdmin.adminParamInsert(param);
	}

	/**
	 * update admin parameters
	 * 
	 * @param AdminParameters
	 *            param
	 * @return AdminParameters
	 */
	public AdminParameters adminParamUpdate(AdminParameters param) {
		return successFactorAdmin.adminParamUpdate(param);
	}
	
	/**
	 * Insert or Update parameter 
	 * @param String nameParam
	 * @param String message
	 * @param Boolean isControlPanel
	 */
	public void adminParamInsertOrUpdate(String nameParam, String message,Boolean isControlPanel) {
		AdminParameters param = this.adminParamGetByNameCode(nameParam);
		if (param != null) {
			param.setValue(message);
			this.adminParamUpdate(param);
		} else {
			param = new AdminParameters();
			param.setValue(message);
			param.setDescription(message);
			param.setNameCode(nameParam);
			param.setIsControlPanel(isControlPanel);
			this.adminParamInsert(param);
		}
	}	

	// -----------------------------------------
	// methods events

	/**
	 * register event of quarzt
	 * 
	 * @param String
	 *            nameParam
	 * @param String
	 *            message
	 */
	public void eventListenerRegisterEventQuarzt(String nameParam, String message) {
		AdminParameters param = this.adminParamGetByNameCode(nameParam);
		if (param != null) {
			param.setValue(message);
			this.adminParamUpdate(param);
		} else {
			param = new AdminParameters();
			param.setDescription(message);
			param.setNameCode(nameParam);
			this.adminParamInsert(param);
		}
	}

	/**
	 * get events by name
	 * 
	 * @param String
	 *            eventName
	 * @return EventListener
	 */
	public EventListenerParam eventsGetByName(String eventName) {
		return successFactorAdmin.eventsGetByName(eventName);
	}

	/**
	 * get all event listeners
	 * 
	 * @return ArrayList<FieldsMappingPpd>
	 */
	public ArrayList<EventListenerParam> eventsListenerGetAll() {
		return successFactorAdmin.eventListenerGetAll();
	}

	/**
	 * Insert event listener
	 * 
	 * @param EventListenerParam
	 *            entity
	 * @return EventListener
	 */
	public EventListenerParam eventListenerInsert(EventListenerParam entity) {
		entity = successFactorAdmin.eventListenerInsert(entity);
		return entity;
	}

	/**
	 * delete event listener
	 * 
	 * @param Long
	 *            idEVent
	 * @return Boolean
	 */
	public Boolean eventListenerDelete(Long idEVent) {
		return successFactorAdmin.eventListenerDelete(idEVent);
	}

	/**
	 * delete event listener ctrl
	 * 
	 * @param EventListenerCtrlProcess
	 *            entity
	 */
	public void eventListenerCtrlDelete(EventListenerCtrlProcess entity) {
		successFactorAdmin.eventListenerCtrlDelete(entity);
	}

	/**
	 * update event listener
	 * 
	 * @param EventListenerParam
	 *            entity
	 * @return EventListener
	 */
	public EventListenerParam eventListenerUpdate(EventListenerParam entity) {
		if (successFactorAdmin.eventListenerUpdate(entity) != null) {
			return entity;
		}

		return null;
	}
	
	/**
	 * Paginator User Details
	 * @param filter
	 * @param Long fkMassiveLoad 
	 * @return List<String[]>
	 * @return
	 */	
	public List<Object[]> eventListenerGetStatusCount(FilterQueryDto filter,Long fkMassiveLoad){
		return successFactorAdmin.eventListenerGetStatusCount(filter, fkMassiveLoad);
	}
	
	/**
	 * Paginator User Details
	 * @param filter
	 * @param Long fkMassiveLoad 
	 * @return List<String[]>
	 * @return
	 */	
	public List<Object[]> eventListenerHistoGetStatusCount(FilterQueryDto filter,Long fkMassiveLoad){
		return successFactorAdmin.eventListenerHistoGetStatusCount(filter, fkMassiveLoad);
	}	
	
	
	
	/**
	 * 
	 * @param List<String[]> events
	 * @param List<String[]> histo
	 * @return List<String[]>
	 * */
	public List<Object[]> unionAllCountersEventListener(List<Object[]> events,List<Object[]> histo)
	{
		int totalRegister = 0;
		int totalHisto = 0;
		
		List<Object[]> responseList = new ArrayList<>();
		
		if(events!=null)
		{
			for(Object[] itemEvent:events)
			{
				for(Object[] responseItem:responseList)
				{
					if(!itemEvent[1].toString().equals("") 
							&& responseItem[1].toString().equals(itemEvent[1].toString()))
					{
						responseItem[0] = (Integer.valueOf(responseItem[0].toString())+
										  Integer.valueOf(itemEvent[0].toString()))+"";
						
						itemEvent[1] = "";						
					}
				}
				
				if(!itemEvent[1].toString().equals("")) {
					responseList.add(itemEvent);
					totalRegister += Integer.valueOf(itemEvent[0].toString());
				}
			}
		}
		
		if(histo!=null)
		{
			for(Object[] item:histo)
			{
				for(Object[] responseItem:responseList)
				{
					if(!item[1].toString().equals("") 
							&& responseItem[1].toString().equals(item[1].toString()))
					{
						responseItem[0] = (Integer.valueOf(responseItem[0].toString())+
										  Integer.valueOf(item[0].toString()))+"";
						
						item[1] = "";						
					}
				}
				
				if(!item[1].toString().equals("")) {
					responseList.add(item);
					totalHisto += Integer.valueOf(item[0].toString());
				}
			}
		}
		
		
		Object[] globalT = {Math.abs(totalHisto + totalRegister),"Total",""};		
		Object[] globalP  = {(totalRegister==0?0:Math.abs((totalRegister+totalHisto) - totalHisto)),"Pending Records",""};
		
		responseList.add(globalT);
		responseList.add(globalP);
		
		return responseList;
	}
	

	// -----------------------------------------
	// methods mapping
	
	/**
	 * query field mapping ppd by if
	 * 
	 * @param Long idField
	 * @param Boolean loadNameMetadata
	 * @return FieldsMappingPpd
	 */
	public FieldsMappingPpd mappingPpdFieldById(Long idField,Boolean loadNameMetadata){
		return successFactorAdmin.mappingPpdFieldById(idField,loadNameMetadata);
	}

	/**
	 * get all fields mapping ppd
	 * 
	 * @param Boolean isAttach / optional
	 * @param String moduleAttach
	 * @parma Boolean isActive
	 * @param Boolean loadNameMetadata
	 * @return ArrayList<FieldsMappingPpd>
	 */
	public ArrayList<FieldsMappingPpd> mappingPpdFieldsGetAll(Boolean isAttach,String moduleAttach,Boolean isActive,Boolean loadNameMetadata) {
		return successFactorAdmin.mappingPpdFieldsGetAll(isAttach,moduleAttach,isActive,loadNameMetadata);
	}
	
	/**
	 * determines if the Field Mapping is used by another entity
	 * @param Long id Field Mapping	  
	 * @return GenErrorInfoDto
	 * */
	public GenErrorInfoDto mappingFieldIsUsedById(Long id) {
		return successFactorAdmin.mappingFieldIsUsedById(id);
	}
	
	/**
	 * get all fields mapping ppd by Name
	 * 
	 * @param Boolean isAttach / optional
	 * @param Boolean loadNameMetadata
	 * @return ArrayList<FieldsMappingPpd>
	 */
	public ArrayList<FieldsMappingPpd> mappingPpdFieldsGetByName(String name,Boolean loadNameMetadata) {
		return successFactorAdmin.mappingPpdFieldsByName(name,loadNameMetadata);
	}	

	/**
	 * Insert fields Mapping Ppd
	 * 
	 * @param SlugItem item
	 * @return FieldsMappingPpd
	 */
	public FieldsMappingPpd mappingPpdFieldsInsert(FieldsMappingPpd field) {
		field = successFactorAdmin.mappingPpdFieldsInsert(field);
		return field;
	}

	/**
	 * delete field mapping ppd
	 * 
	 * @param FieldTemplatesDto variables
	 * @param Long idTemplate
	 */
	public boolean mappingPpdFieldDelete(Long id,Long idTemplate) {
		return successFactorAdmin.mappingPpdFieldDelete(id,idTemplate);
	}

	/**
	 * update fields mapping ppd
	 * 
	 * @param SlugItem item
	 * @param Long idField
	 */
	public FieldsMappingPpd mappingPpdFieldUpdate(Long idField, FieldsMappingPpd field) {
		if (successFactorAdmin.mappingPpdFieldUpdate(field) != null) {
			return field;
		}

		return null;
	}
	
	/**
	 * delete all mapping ppd field metadata by id field mapping ppd 
	 * @param fieldMappingPpdId
	 * @param Long idTemplate
	 * */
	public void mappingPpdFieldMetaDelete(Long fieldMappingPpdId,Long idTemplate) {
		successFactorAdmin.mappingPpdFieldMetaDelete(fieldMappingPpdId,idTemplate);
	}
		
	/**
	 * Insert new mapping field metadata
	 * @param Long idFieldTemplate
	 * @param Long idTemplateLib
	 * @param Long idTemplate
	 * */
	public void mappingPpdFieldMetaInsert(Long idFieldTemplate,Long idTemplateLib,Long idTemplate){
		successFactorAdmin.mappingPpdFieldMetaInsert(idFieldTemplate, idTemplateLib,idTemplate);
	}
	
	/**
	 * get FieldsMappingMeta by id Mapping Field ppd
	 * @param fieldMappingPpdId
	 * */
	public ArrayList<FieldsMappingMeta> mappingPpdFieldMetaGetByIdFieldMapp(Long fieldMappingPpdId){
		return successFactorAdmin.mappingPpdFieldMetaGetByIdFieldMapp(fieldMappingPpdId);
	}
	
	/**
	 * Get all field mapping by filters
	 * @param Long idFielMapping
	 * @param Long idFieldTemplateLib
	 * @return Collection<FieldsMappingMeta>
	 * */
	public Collection<FieldsMappingMeta> mappingPpdFieldMetaGetByRef(Long idFielMapping,Long idFieldTemplateLib){
		return successFactorAdmin.mappingPpdFieldMetaGetByRef(idFielMapping, idFieldTemplateLib);
	}
	
	// -----------------------------------------
	// methods Structure Business
	/**
	 * get all structure business
	 * 
	 * @return ArrayList<StructureBusiness>
	 */
	public ArrayList<StructureBusiness> structureBusinessGetAll() {
		return successFactorAdmin.structureBusinessGetAll();
	}

	/**
	 * Insert structure business
	 * 
	 * @param StructureBusiness
	 *            entity
	 * @return StructureBusiness
	 */
	public StructureBusiness structureBusinessInsert(StructureBusiness entity, Long parentId) {
		entity = successFactorAdmin.structureBusinessInsert(entity, parentId);
		return entity;
	}

	/**
	 * delete structure business
	 * 
	 * @param StructureBusiness
	 *            variables
	 * @return boolean
	 */
	public boolean structureBusinessDelete(Long id) {
		return successFactorAdmin.structureBusinessDelete(id);
	}

	/**
	 * update structure business
	 * 
	 * @param StructureBusiness
	 *            entity
	 * @return StructureBusiness
	 */
	public StructureBusiness structureBusinessUpdate(Long id, StructureBusiness entity) {
		if (successFactorAdmin.structureBusinessUpdate(id, entity) != null) {
			return entity;
		}

		return null;
	}

	// -----------------------------------------
	// methods event Listener control admin

	/**
	 * create historial to event listener control
	 * 
	 * @param PpdCreateEmployeeInfoDto employeeUpdate
	 * @param ArrayList<EventListenerDocProcess> attachList
	 * @param EventListenerCtrlProcess event
	 */
	public void eventListenerCtrlHistoCreate(PpdEmployeeInfo_v1Dto employeeUpdate, ArrayList<EventListenerDocProcess> attachList, EventListenerCtrlProcess event) 
	{
		Date date = new Date();
		String dateSeconds = "Time Execution " + UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);

		// -------------------------------------------------
		// add event to history events
		EventListenerCtrlHistory eventCtrlHisto = new EventListenerCtrlHistory();
		
		if(event!=null)
		{		
			eventCtrlHisto.setIdOriginalEvent(event.getId());
			eventCtrlHisto.setCreateOn(event.getCreateOn());
			eventCtrlHisto.setEventListenerParam(event.getEventListenerParam());
			eventCtrlHisto.setObservations(event.getObservations() + " " + employeeUpdate.getObservations());
			eventCtrlHisto.setSeqNumberPpd(event.getSeqNumberPpd());
			eventCtrlHisto.setStartDatePpdOn(event.getStartDatePpdOn());
			eventCtrlHisto.setUserIdPpd(event.getUserIdPpd());
			eventCtrlHisto.setUserCountry(event!=null&&event.getUserCountry()!=null?event.getUserCountry():"");
			
			if(event.getFkMassiveLoad()!=null && event.getFkMassiveLoad().getId()!=null) {
				eventCtrlHisto.setRefMassLoadId(event.getFkMassiveLoad().getId());
				eventCtrlHisto.setRefMassLoad("massiveLoad: "+event.getFkMassiveLoad().getId()
															 +". UserLoad: "+event.getFkMassiveLoad().getCreateUser()!=null?event.getFkMassiveLoad().getCreateUser():""
															 +". NameFile: "+event.getFkMassiveLoad().getNameLoad()!=null?event.getFkMassiveLoad().getNameLoad():"");
			}
	
			if (!event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode()) 
					&& !event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode())
						&& !event.getStatus().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMINATE_EMP_ERROR_ATTACH.getCode())) {
				eventCtrlHisto.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
			} else {
				eventCtrlHisto.setStatus(event.getStatus());
			}
	
			eventCtrlHisto.setRetries(event.getRetries());
			eventCtrlHisto.setLastUpdateOn(date);
	
			// add event to history
			eventCtrlHisto = this.eventListenerCtrlHistoInsert(eventCtrlHisto);
		}

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

					if(event!=null) {
						attachHisto.setEventListenerCtrlHisto(eventCtrlHisto);
					}
					
					// insert histo attachs processed
					successFactorAdmin.eventListenerDocHistoInsert(attachHisto);
				}
			}
		}
	}

	/**
	 * updates status of all documents associated with the event
	 * 
	 * @param Long
	 *            idEvent
	 * @param String
	 *            status
	 * @param String
	 *            observation
	 */
	public void eventListenerUpdateAllDocProc(Long idEvent, String status, String observation) {
		ArrayList<EventListenerDocProcess> listDocs = this.eventsListenerDocProcGetAllByIdCtrl(idEvent);

		for (EventListenerDocProcess item : listDocs) {
			item.setStatus(status);
			item.setLastUpdateOn(new Date());
			item.setObservations(UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + observation + UtilCodesEnum.CODE_STRING_END.getCode() + item.getObservations());

			successFactorAdmin.eventListenerDocProcessUpdate(item);
		}
	}

	/**
	 * Update event listener ctrl to Process
	 * 
	 * @param EventListenerCtrlProcess
	 *            entity
	 * @return EventListenerCtrlProcess
	 */
	public EventListenerCtrlProcess eventListenerCtrlProcessUpdate(EventListenerCtrlProcess entity) {
		return successFactorAdmin.eventListenerCtrlProcessUpdate(entity);
	}

	/**
	 * Get all Event Listener Control process by start date and status
	 * 
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, can by null
	 * @param Integer maxReg, maximum number of records to process, optional
	 * @param Boolean isFutureDates
	 * @param Long fkMassiveLoad
	 * @param String statusMassiveToLoad
	 * @return ArrayList<EventListenerCtrlProcess>
	 */
	public ArrayList<EventListenerCtrlProcess> eventsListenerCtrlProcGetAll(Date startDate, String statusToLoad, Integer maxReg,Boolean isFutureDates, FilterQueryDto filter,Long fkMassiveLoad, String statusMassiveToLoad) {
		return successFactorAdmin.eventsListenerCtrlProcGetAll(startDate, statusToLoad, maxReg,isFutureDates, filter, fkMassiveLoad,statusMassiveToLoad);
	}

	/**
	 * get all event listener ctrl
	 * @param FilterQueryDto filter
	 * @param Long refMassLoadId
	 * @return ArrayList<EventListenerCtrlHistory>
	 */
	public ArrayList<EventListenerCtrlHistory> eventsListenerCtrlHistoGetAll(FilterQueryDto filter,Long refMassLoadId) {
		return successFactorAdmin.eventsListenerCtrlHistoGetAll(filter,refMassLoadId);
	}

	/**
	 * delete all events or specific event ctrl histo
	 * 
	 * @param EventListenerCtrlHistory
	 *            entity is null delete all history
	 */
	public void eventsListenerCtrlHistoDelete(EventListenerCtrlHistory entity) {
		successFactorAdmin.eventsListenerCtrlHistoDelete(entity);
	}
	
	/**
	 * get event listener ctrl by id
	 * 
	 * @param Long idEventListenerCtrlHisto
	 * @return EventListenerCtrlHistory
	 */
	public EventListenerCtrlHistory eventListenerCtrlHistoById(Long id) {
		return successFactorAdmin.eventListenerCtrlHistoById(id);
	}

	/**
	 * Insert event listener ctrl
	 * 
	 * @param EventListenerCtrlHistory
	 *            entity
	 * @return EventListenerCtrl
	 */
	public EventListenerCtrlHistory eventListenerCtrlHistoInsert(EventListenerCtrlHistory entity) {
		return successFactorAdmin.eventListenerCtrlHistoInsert(entity);
	}

	/**
	 * return count of history by id ctrl
	 * 
	 * @param Long
	 *            id
	 */
	public long getEventListenerCountHistoByidCrtl(Long id) {
		return successFactorAdmin.getEventListenerCountHistoByidCrtl(id);
	}

	/**
	 * Insert event listener ctrl to Process
	 * 
	 * @param EventListenerCtrlProcess
	 *            entity
	 * @return EventListenerCtrlProcess
	 */
	public EventListenerCtrlProcess eventListenerCtrlProcessInsert(EventListenerCtrlProcess entity) {
		return successFactorAdmin.eventListenerCtrlProcessInsert(entity);
	}

	/**
	 * Get Event Listener Control process Attach by id EventListenerCtrl
	 * 
	 * @param Long idCtrl
	 * @return ArrayList<EventListenerDocProcess>
	 */
	public ArrayList<EventListenerDocProcess> eventsListenerDocProcGetAllByIdCtrl(Long idCtrl) {
		ArrayList<EventListenerDocProcess> li = successFactorAdmin.eventsListenerDocProcGetAll(idCtrl, null, null,null,null,null);
		return li;
	}

	/**
	 * get event listener control process
	 * 
	 * @param Long id
	 * @return EventListenerDocProcess
	 */
	public EventListenerDocProcess eventsListenerDocProcGetByid(Long id) {
		return successFactorAdmin.eventListenerDocProcessGetById(id);
	}

	/**
	 * get event listener control process
	 * 
	 * @param Long
	 *            idCtrl
	 */
	public EventListenerCtrlProcess eventsListenerCtrlProcGetByid(Long idCtrl) {
		return successFactorAdmin.eventsListenerCtrlProcGetByid(idCtrl);
	}

	/**
	 * Updates records that have exceeded the maximum wait time
	 * 
	 * @param String statusToLoad
	 * @param String statusDestination
	 * @param Integer timeOutMinutes
	 */
	public void eventListenerDocUpdatetToStatusTimeOut(String statusToLoad, String statusDestination, Integer timeOutMinutes) {
		successFactorAdmin.eventListenerDocUpdatetToStatusTimeOut(statusToLoad, statusDestination, timeOutMinutes);
	}
	
	
	

	// -----------------------------------------
	// methods event Listener control document admin

	/**
	 * delete event listener ctrl document to Process
	 * 
	 * @param EventListenerDocProcess
	 *            entity
	 */
	public void eventListenerDocProcessDelete(EventListenerDocProcess entity) {
		successFactorAdmin.eventListenerDocProcessDelete(entity);
	}
	
	/**
	 * delete All Associated by id EventListenerProcess
	 * @param Long id
	 * @return boolean
	 * */
	public void eventListenerDocDeleteByIdEventCtrl(Long id){
		successFactorAdmin.eventListenerDocDeleteByIdEventCtrl(id);
	}
	
	/**
	 * delete All Associated by id Massive Load
	 * @param Long idMassLoad
	 * @return boolean
	 * */
	public boolean eventListenerDocDeleteAllWithIdMassiveLoad(Long idMassLoad){
		return successFactorAdmin.eventListenerDocDeleteAllWithIdMassiveLoad(idMassLoad);
	}

	/**
	 * Update event listener ctrl document observations to Process
	 * 
	 * @param EventListenerDocProcess
	 *            entity
	 */
	public void eventListenerDocProcessObsUpdate(EventListenerDocProcess entity) {
		successFactorAdmin.eventListenerDocProcessObsUpdate(entity);
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Updates fielMapping to NULL in EventListenerDocProcess by idFieldMapping 
	 * @param Long idFieldMapping
	 * */
	public void eventListenerDocProUpdateFieldMapToNull(Long idFieldMapping) {
		successFactorAdmin.eventListenerDocProUpdateFieldMapToNull(idFieldMapping);
	}
	
	/**
	 * get status count 
	 * @param filter 
	 * @return List<String[]>
	 */
	public List<Object[]> eventListenerDocGetStatusCount(FilterQueryDto filter){
		return successFactorAdmin.eventListenerDocGetStatusCount(filter);
	}
	
	
	/**
	 * Get all Event Listener document Control
	 * @param Long idEventCtrlHistoProc
	 * @param FilterQueryDto filter
	 * @return ArrayList<EventListenerDocProcess>
	 * */
	public ArrayList<EventListenerDocProcess> eventListenerDocGetByFilter(Long idEventCtrl,FilterQueryDto filter) {
		return successFactorAdmin.eventListenerDocGetByFilter(idEventCtrl, filter);
	}

	/**
	 * Update event listener ctrl document to Process
	 * 
	 * @param EventListenerDocProcess
	 *            entity
	 * @return EventListenerDocProcess
	 */
	public EventListenerDocProcess eventListenerDocProcessUpdate(EventListenerDocProcess entity) {
		return successFactorAdmin.eventListenerDocProcessUpdate(entity);
	}

	/**
	 * Get all Event Listener Control Document process by start date and status
	 * 
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, optional
	 * @param String typeModuleNot,
	 * @param Long idEventCtrlProc optional
	 * @param Integer maxReg, maximum number of records to process, optional
	 * @return Collection<EventListenerDocProcess>
	 */
	public ArrayList<EventListenerDocProcess> eventsListenerDocProcGetAll(Long idEventCtrlProc, Date startDate, String statusToLoad,String typeModule,String typeModuleNot, Integer maxReg) {
		return successFactorAdmin.eventsListenerDocProcGetAll(idEventCtrlProc, startDate, statusToLoad,typeModule,typeModuleNot, maxReg);
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * Get all Attachments tp process by start date and status
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, optional
	 * @param Integer maxReg, maximum number of records to process, optional
	 * @return Collection<EventListenerDocProcess>
	 * */
	public ArrayList<EventListenerDocProcess> getAllAttachmentProcess(Date startDate,String statusToLoad,Integer maxReg, FilterQueryDto filters){
		return successFactorAdmin.getAllAttachmentProcess(startDate, statusToLoad, maxReg, filters);
	}

	/**
	 * get all event listener ctrl histo document
	 * 
	 * @param Long idEventCtlHisto
	 * @param FilterQueryDto filter
	 * @return ArrayList<EventListenerDocHistory>
	 */
	public ArrayList<EventListenerDocHistory> eventsListenerDocHistoGetAll(Long idEventCtlHisto,FilterQueryDto filter) {
		return successFactorAdmin.eventsListenerDocHistoGetAll(idEventCtlHisto,filter);
	}

	/**
	 * Insert event listener ctrl document
	 * 
	 * @param EventListenerDocHistory
	 *            entity
	 * @return EventListenerDoc
	 */
	public EventListenerDocHistory eventListenerDocHistoInsert(EventListenerDocHistory entity) {
		return successFactorAdmin.eventListenerDocHistoInsert(entity);
	}

	/**
	 * Insert event listener ctrl document to Process	  
	 * @param EventListenerDocProcess entity
	 * @return EventListenerDocProcess
	 */
	public EventListenerDocProcess eventListenerDocProcessInsert(EventListenerDocProcess entity) {
		return successFactorAdmin.eventListenerDocProcessInsert(entity);
	}
	
	
	/**
	 * load from eventlistenerDoc Histo and insert EventListenerDocProcess
	 * @param Long idHisto
	 * @return ResponseGenericDto
	 * */
	public ResponseGenericDto eventListenerDocProcessInsertFromHisto(Long idHisto) 
	{
		ResponseGenericDto response = new ResponseGenericDto();
		EventListenerDocHistory eventListDocHist = successFactorAdmin.eventsListenerDocHistoGetById(idHisto);
		
		if(eventListDocHist!=null)
		{
			EventListenerDocProcess entity = new EventListenerDocProcess();
			entity.setAttachmentFileName(eventListDocHist.getAttachmentFileName());
			entity.setAttachmentIdSF(eventListDocHist.getAttachmentIdSF());
			entity.setCreateOn(new Date());
			entity.setStatus(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());
			entity.setStartDatePpdOnAttach(eventListDocHist.getStartDatePpdOnAttach());
			entity.setUserIdPpd(eventListDocHist.getUserIdPpd());
			entity.setUserCountry(eventListDocHist.getUserCountry());
			entity.setUserExtendPpd(eventListDocHist.getUserExtendPpd());
			//entity.setUserIdOtherPlat(eventListDocHist.getUser);
			
			if(eventListDocHist.getIdFieldMapPpd()!=null)
			{
				FieldsMappingPpd fieldMapping = successFactorAdmin.mappingPpdFieldGetId(eventListDocHist.getIdFieldMapPpd());
				if(fieldMapping!=null) 
				{
					entity.setFieldMapPpd(fieldMapping);					
					
					if(eventListDocHist.getIdFieldMapPpdDest()!=null)
					{
						FieldsMappingPpd fieldMappingDesc = successFactorAdmin.mappingPpdFieldGetId(eventListDocHist.getIdFieldMapPpdDest());
						if(fieldMappingDesc!=null) {
							entity.setFieldMapPpdDest(fieldMappingDesc);	
						}
					}
				}
				else
				{
					response.setCode(UtilCodesEnum.CODE_HTTP_1501.getCode());
					response.setField(UtilCodesEnum.CODE_HTTP_1501.getLabel());
					response.setMessage("Field Mapping Ppd, to Id History :"+idHisto);					
					return response;
				}
			}
			else if(!entity.getAttachmentIdSF().equals(UtilCodesEnum.CODE_TYPE_MODULE_ATTACH_UPDATE_USER.getCode())) 
			{
				response.setCode(UtilCodesEnum.CODE_HTTP_1501.getCode());
				response.setField(UtilCodesEnum.CODE_HTTP_1501.getLabel());
				response.setMessage("Field Mapping Ppd, to Id History :"+idHisto);
				return response;
			}
			
			response.setCode(UtilCodesEnum.CODE_HTTP_200.getCode());
			response.setField(UtilCodesEnum.CODE_HTTP_200.getLabel());
			response.setMessage("new Id: "+successFactorAdmin.eventListenerDocProcessInsert(entity).getId()+" From Id History: "+idHisto);
		}
		
		return response;
	}

	/**
	 * Updates records that have exceeded the maximum wait time
	 * 
	 * @param String
	 *            statusToLoad
	 * @param String
	 *            statusDestination
	 * @param Integer
	 *            timeOutMinutes
	 */
	public void eventListenerCtrlUpdatetToStatusTimeOut(String statusToLoad, String statusDestination, Integer timeOutMinutes) {
		successFactorAdmin.eventListenerCtrlUpdateToStatusTimeOut(statusToLoad, statusDestination, timeOutMinutes);
	}
	
	/**
	 * update status event ctrl
	 * @param Long idEventCtrl
	 * @param Long idMassLoad
	 * @param String status 
	 * */
	public void updateEventCtrlStatus(Long idEventCtrl,Long idMassLoad,String status) {
		successFactorAdmin.updateEventCtrlStatus(idEventCtrl, idMassLoad, status);
	}	
	
	/**
	 * get Event Listener Control by page
	 * @param filter
	 * @param Long fkMassiveLoad
	 * @return Collection<EventListenerCtrlProcess>
	 */
	public Collection<EventListenerCtrlProcess> getEventListenerCtrlByPage(FilterQueryDto filter,Long fkMassiveLoad){
		return successFactorAdmin.getEventListenerCtrlByPage(filter,fkMassiveLoad);
	}
	
	/**
	 * Get number pages
	 * @param Integer maxRegisterByPage
	 * @param String idMassiveLoad
	 * @return
	 */
	public double eventListenerCtrlProcNumberPages(Integer maxRegisterByPage, String idMassiveLoad) {
		return successFactorAdmin.eventListenerCtrlProcNumberPages(maxRegisterByPage, idMassiveLoad);
	}
	
	/**
	 * delete All Associated by id Massive Load
	 * @param Long idMassLoad
	 * @return boolean
	 * */
	public boolean eventListenerCtrlDeleteAllWithIdMassiveLoad(Long idMassLoad) {
		return successFactorAdmin.eventListenerCtrlDeleteAllWithIdMassiveLoad(idMassLoad);
	}	
	
	
	/**
	 * update id job process
	 * @param Long idEventCtrl
	 * @param Long idJobProcess
	 * 
	 * */
	public void eventListenerCtrlUpdateIdJobProcess(Long idEventCtrl,Long idJobProcess) {
		successFactorAdmin.eventListenerCtrlUpdateIdJobProcess(idEventCtrl, idJobProcess);
	}
	
	/**
	 * delete All data in attachment without eventlistener
	 * @return boolean
	 * */
	public boolean deleteAllEmployeeSync() {
		return successFactorAdmin.deleteAllEmployeeSync();
	}	
	
	/**
	 * execute query in success Factor
	 * @param String query
	 * @return String response
	 * */
	public String executeQuerySF(String query){
		return successFactorAdmin.executeQuerySF(query);
	}
	
	
	/**
	 * Build cvs body of log event and sync
	 * @param FilterQueryDto filter
	 * @return ResponseGenericDto
	 * */
	public ResponseGenericDto downloadEventSyncLog(String date,FilterQueryDto filter)
	{		
		ResponseGenericDto response = new ResponseGenericDto();
		List<EventListenerCtrlProcess> listReturn = new ArrayList<>();
		
		//--------------------------------------------------------------------------------
		//admin parameters event dates
		AdminParameters paramDateInitSearch = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_INIT_HOUR.getCode());
		if(paramDateInitSearch!=null && paramDateInitSearch.getValue()!=null)
			filter.setDate(date+" "+paramDateInitSearch.getValue());
		else
			filter.setDate(date+" "+" 00:00");
		
		AdminParameters paramDateEndSearch = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_EMAIL_END_HOUR.getCode());
		if(paramDateEndSearch!=null && paramDateEndSearch.getValue()!=null)
			filter.setDateFinish(date+" "+paramDateEndSearch.getValue());
		else
			filter.setDateFinish(date+" "+" 23:59");
		
		//------------------------------------------
		String country = "";
		listReturn = (List<EventListenerCtrlProcess>) successFactorAdmin.getEventListenerCtrlByPage(filter,null);		
		response.setField("even_sync_report_"+date);
		
		//add history register
		if(listReturn==null)
			listReturn = new ArrayList<>();
		
		listReturn.addAll(
		UtilMapping.loadEventListeCrtlFormHisto(successFactorAdmin.eventsListenerCtrlHistoGetAll(filter,null),""));
		
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
						listAttach = this.eventsListenerDocProcGetAllByIdCtrl(event.getId());
					}
					
					csvAttach = "";
					observ = UtilMapping.toStringHtmlFormat(event.getObservations());
					country = event.getUserCountry()!=null?event.getUserCountry().replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()," "):"";
					observ = observ.replace(";","");
					observ = observ.replace("\n","");
					
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
							country = event.getUserCountry()!=null?event.getUserCountry().replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()," "):"";
							
							csvAttach += (event.getEventLCtrlHistoryId()!=null?"Event Listener History":"Event Listeners to process")
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"attach"
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+event.getId()		
										 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+event.getUserIdPpd()
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
		
		//load event sync
		csvAttach = "";
		ArrayList<EventListenerDocProcess> listAttach = 
				UtilMapping.loadEventListenerDocFormHisto(successFactorAdmin.eventsListenerDocHistoGetAll(-1L,filter));
		
		if(listAttach!=null)
		{
			for(EventListenerDocProcess item:listAttach)
			{
				observ = UtilMapping.toStringHtmlFormat(item.getObservations());
				observ = observ.replace(";","");
				observ = observ.replace("\n","");
				country = item.getUserCountry()!=null?item.getUserCountry().replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()," "):"";
				
				csvAttach += "Synchronization History"
							 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"attach"
							 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+item.getId()		
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
		
		csv += csvAttach;
		
		csvAttach = "";
		listAttach = this.eventListenerDocGetByFilter(-1L, filter);
		if(listAttach!=null)
		{
			for(EventListenerDocProcess item:listAttach)
			{
				observ = UtilMapping.toStringHtmlFormat(item.getObservations());
				observ = observ.replace(";","");
				observ = observ.replace("\n","");
				country = item.getUserCountry()!=null?item.getUserCountry().replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()," "):"";
				
				csvAttach += "Synchronization Process"
							 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+"attach"
							 +UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode()+item.getId()		
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
		
		csv += csvAttach;
		
		
		response.setMsg(csv);
		return response;
	}
	
	

	// -----------------------------------------
	// methods adm parameters

	/**
	 * query adm parameter by id
	 * 
	 * @param Long
	 *            id
	 * @return AdminParameters
	 */
	public AdminParameters admParameterById(Long id) {
		return successFactorAdmin.admParameterById(id);

	}

	/**
	 * get all parameters
	 * 
	 * @param Boolean
	 *            isAttach / optional
	 * @return ArrayList<AdminParameters>
	 */
	public java.util.List<AdminParameters> admParameterGetAll() {
		return successFactorAdmin.admParameterGetAll();
	}
	
	/**
	 * get all parameters admin
	 * @param Boolean isControlPanel
	 * @return List<AdminParameters>
	 * */
	public java.util.List<AdminParameters> admParameterGetAll(Boolean isControlPanel){
		return (new ArrayList<AdminParameters>(successFactorAdmin.getAllParametersAdmin(isControlPanel)));
	}

	/**
	 * Insert adm parameters
	 * 
	 * @param AdminParameters
	 *            entity
	 * @return AdminParameters insert
	 */
	public AdminParameters admParameterInsert(AdminParameters entity) {
		return successFactorAdmin.admParameterInsert(entity);
	}

	/**
	 * delete parameter adm
	 * 
	 * @param AdminParameters id
	 */
	public boolean admParameterDelete(Long id) {
		return successFactorAdmin.admParameterDelete(id);
	}
	
	/**
	 * delete all parameters
	 * @param AdminParameters id
	 */
	public boolean admParameterDeleteAll() 
	{
		try 
		{
			return successFactorAdmin.admParameterDeleteAll();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}	

	/**
	 * update parameter adm
	 * 
	 * @param AdminParameters
	 *            entity
	 */
	public AdminParameters admParameterUpdate(AdminParameters entity) {
		return successFactorAdmin.admParameterUpdate(entity);
	}

	/**
	 * get all parameters
	 * 
	 * @param Boolean
	 *            isAttach / optional
	 * @return ArrayList<AdminParameters>
	 */
	public java.util.List<RolesMappingPpd> roleGetAll() {
		return successFactorAdmin.roleGetAll();
	}

	/**
	 * get all parameters
	 * 
	 * @param Boolean
	 *            isAttach / optional
	 * @return ArrayList<AdminParameters>
	 */
	public java.util.List<SFAdmin> roleGetUsers() {
		return successFactorAdmin.roleGetUsers();
	}


	/**
	 * Insert role adm
	 * 
	 * @param AdminParameters
	 *            entity
	 */
	public RolesMappingPpd roleInsert(RolesMappingPpd entity) {

		SFRoleList lst = successFactor.getGroupId(entity.getNameSf());

		if (lst.getD().getResults().length > 0) {
			logger.info(lst.getD().getResults()[0].getGroupID().toString());
			entity.setIdSf(lst.getD().getResults()[0].getGroupID().toString());
			entity.setCountUsers(lst.getD().getResults()[0].getActiveMembershipCount().toString());
			return successFactorAdmin.roleInsert(entity);
		} else {
			return null;
		}

	}

	/**
	 * delete parameter adm
	 * 
	 * @param AdminParameters
	 *            id
	 */
	public boolean roleDelete(Long id) {
		return successFactorAdmin.roleDelete(id);
	}

	/**
	 * update parameter adm
	 * 
	 * @param AdminParameters
	 *            entity
	 */
	public RolesMappingPpd roleUpdate(RolesMappingPpd entity) {
		SFRoleList lst = successFactor.getGroupId(entity.getNameSf());

		if (lst.getD().getResults().length > 0) {
			logger.info(lst.getD().getResults()[0].getGroupID().toString());
			entity.setIdSf(lst.getD().getResults()[0].getGroupID().toString());
			entity.setCountUsers(lst.getD().getResults()[0].getActiveMembershipCount().toString());
			return successFactorAdmin.roleUpdate(entity);
		} else {
			return null;
		}
		// return successFactorAdmin.roleUpdate(entity);
	}
	
	
	/**
	 * get all sfentities
	 * 
	 * @param none
	 * @return ArrayList<SFEntity>
	 */
	public java.util.List<SfEntity> entityGetAll() {
		return successFactorAdmin.entityGetAll();
	}
	
	/**
	 * get all sfentities
	 * 
	 * @param none
	 * @return ArrayList<SFEntity>
	 */
	public String entityGetOne(String id) {
		return successFactorAdmin.entityGetOne(id);
	}
	
	

	
	/**
	 * Update Insert Admin Parameters
	 * 
	 * @param List<AdminParameters>
	 * @param String User
	 * @return none
	 * 
	 * **/
	public void updateInsertAdminParameters(List<AdminParameters> listNew, String User) {
		
		List<AdminParameters> listAdm = this.admParameterGetAll();
		
		for(int i=0; i<listNew.size(); i++) 
		{
			listNew.get(i).setId(null);
			for (AdminParameters adminParameters : listAdm) 
			{
				if(adminParameters.getNameCode()!=null && listNew.get(i).getNameCode()!=null && 
						adminParameters.getNameCode().equals(listNew.get(i).getNameCode()) &&
						(adminParameters.getIsControlPanel() == null || adminParameters.getIsControlPanel() == false)) 
				{
					listNew.get(i).setId(adminParameters.getId()); 
					// update
					adminParameters.setDescription(listNew.get(i).getDescription());
					adminParameters.setValue(listNew.get(i).getValue());
					adminParameters.setGroup(listNew.get(i).getGroup());
					adminParameters.setIsControlPanel(Boolean.FALSE);
					adminParameters.setLastUpdateDate(new Date());
					adminParameters.setLastUpdateUser(User);
					this.adminParamUpdate(adminParameters);
				}
			}
			
			if(listNew.get(i).getId()==null && listNew.get(i).getNameCode()!=null) {
				listNew.get(i).setIsControlPanel(Boolean.FALSE);
				listNew.get(i).setLastUpdateDate(new Date());
				listNew.get(i).setLastUpdateUser(User);
				this.adminParamInsert(listNew.get(i));
			}
		}
	}
	
	/**
	 * 
	 * @param List<FieldsMappingPpd>
	 * @return none
	 * 
	 * **/
	public void updateInsertFieldsMappingPpd(List<FieldsMappingPpd> listNew) {
		
		ArrayList<FieldsMappingPpd> listMpf= this.mappingPpdFieldsGetAll(null,null,null,false);
		
		for(int i=0; i<listNew.size(); i++) 
		{
			listNew.get(i).setId(null);
			for (FieldsMappingPpd fieldsMappingPpd : listMpf) {
				if(fieldsMappingPpd.getNameSource()!=null && listNew.get(i).getNameSource()!=null && 
						listNew.get(i).getNameDestination()!=null && fieldsMappingPpd.getNameSource().equals(listNew.get(i).getNameSource())) 
				{
					listNew.get(i).setId(fieldsMappingPpd.getId());
					fieldsMappingPpd.setDescription(listNew.get(i).getDescription());
					fieldsMappingPpd.setIsAttached(listNew.get(i).getIsAttached());
					fieldsMappingPpd.setIsConstants(listNew.get(i).getIsConstants());
					fieldsMappingPpd.setIsFilter(listNew.get(i).getIsFilter());
					fieldsMappingPpd.setIsObligatory(listNew.get(i).getIsObligatory());
					fieldsMappingPpd.setNameDestination(listNew.get(i).getNameDestination());
					fieldsMappingPpd.setParameters(listNew.get(i).getParameters());
					fieldsMappingPpd.setActions(listNew.get(i).getActions());
					fieldsMappingPpd.setTypeModule(listNew.get(i).getTypeModule());
					fieldsMappingPpd.setIsActive(listNew.get(i).getIsActive());
					FieldsMappingPpd field = this.mappingPpdFieldUpdate(fieldsMappingPpd.getId(), fieldsMappingPpd);
					
					this.mappingPpdFieldMetaDelete(field.getId(),null);
					List<FieldsTemplateLibrary> list = this.validationMetadata(listNew.get(i).getMetadataList());
							
					if(list != null && !list.isEmpty()) {
						for (FieldsTemplateLibrary item : list) {							
							this.mappingPpdFieldMetaInsert(field.getId(), item.getId(),null);
						}
					}
				}
			}
			
			if(listNew.get(i).getId()==null && listNew.get(i).getNameSource()!=null 
					&& listNew.get(i).getNameDestination()!=null) 
			{	
				FieldsMappingPpd field = this.mappingPpdFieldsInsert(listNew.get(i));
				this.mappingPpdFieldMetaDelete(field.getId(),null);
				
				List<FieldsTemplateLibrary> list = this.validationMetadata(listNew.get(i).getMetadataList());
				
				if(list != null && !list.isEmpty()) 
				{
					for (FieldsTemplateLibrary item : list){	
						this.mappingPpdFieldMetaInsert(field.getId(), item.getId(),null);
					}
				}				
			}
		}
	}
	
	/**
	 * 
	 * @param List<FieldsMappingPpd>
	 * @return none
	 * 
	 * **/
	public void updateInsertParametersEventListener(List<EventListenerParam> listNew) {
		
		ArrayList<EventListenerParam> listElp= this.eventsListenerGetAll();
		
		for(int i=0; i<listNew.size(); i++) 
		{
			listNew.get(i).setId(null);
			for (EventListenerParam eventListener : listElp) {
				if(eventListener.getEventId()!=null && listNew.get(i).getEventId()!=null && 
						eventListener.getEventId().equals(listNew.get(i).getEventId())) 
				{
					listNew.get(i).setId(eventListener.getId());
					
					this.eventListenerUpdate(eventListener);
				}
			}
			
			if(listNew.get(i).getId()==null && listNew.get(i).getEventId()!=null) {
				this.eventListenerInsert(listNew.get(i));
			}
		}
	}
	
	/**
	 * 
	 * @param List<Countries>
	 * @return none
	 * 
	 * **/
	public void updateInsertCountries(List<Countries> listNew) {
		
		List<Countries> listCountry= this.countriesGetAll();
		
		for(int i=0; i<listNew.size(); i++) 
		{
			listNew.get(i).setId(null);
			for (Countries country : listCountry) {
				if(country.getCode()!=null && listNew.get(i).getCode()!=null && 
						country.getCode().equals(listNew.get(i).getCode())) {
					listNew.get(i).setId(country.getId());
					
					this.countryUpdate(listNew.get(i));
				}
			}
			
			if(listNew.get(i).getId()==null && listNew.get(i).getCode()!=null) {
				this.countryInsert(listNew.get(i));
			}
		}
	}
	
	/**
	 * Update/insert Sign Template Library
	 * @param List<SignsTemplateLibrary>
	 */
	public void updateInsertParametersSignTemplateLibrary(List<SignsTemplateLibrary> listNew) {
		List<SignsTemplateLibrary> listSignTemLib= this.signTemplateLibGetAll();
		
		for(int i=0; i<listNew.size(); i++) 
		{
			Long id = listNew.get(i).getId();
			listNew.get(i).setId(null);
			for (SignsTemplateLibrary sign : listSignTemLib) {
				if(sign.getNameSource()!=null && listNew.get(i).getNameSource()!=null &&
						sign.getNameSource().equals(listNew.get(i).getNameSource())) {
					listNew.get(i).setId(sign.getId());
					
					this.SignsTemplateLibraryUpdate(listNew.get(i));
				}
			}
			
			if(listNew.get(i).getId()==null && listNew.get(i).getNameSource()!=null) {
				listNew.get(i).setId(id);
				this.signsTemplateLibraryInsert(listNew.get(i));
			}
		}
	}
	/**
	 * Update/insert Language from Json
	 * @param List<Language>
	 * 
	 * */
	public void updateInsertLanguages(List<Language> listNew) {
		List<Language> listLanguage = this.languageGetAll();
		
		for(int i=0; i<listNew.size(); i++) 
		{
			Long id = listNew.get(i).getId();
			listNew.get(i).setId(null);
			for (Language lang : listLanguage) {
				if(lang.getCode()!=null && lang.getDescription()!=null &&
						lang.getCode().equals(listNew.get(i).getCode())) {
					listNew.get(i).setId(lang.getId());
					this.languageUpdate(listNew.get(i));
				}
			}
			
			if(listNew.get(i).getId()==null && listNew.get(i).getCode()!=null && listNew.get(i).getDescription()!=null) {
				listNew.get(i).setId(id);
				this.languageInsert(listNew.get(i));
			}
		}
		
	}
	/**
	 * Update/insert Roles Mapping PpD from Json
	 * @param List<RolesMappingPpd>
	 * 
	 * */
	public int updateInsertRoles(List<RolesMappingPpd> listNew) {
		List<RolesMappingPpd> listRoles = this.roleGetAll();
		int j=0;
		for(int i=0; i<listNew.size(); i++) 
		{
			Long id = listNew.get(i).getId();
			listNew.get(i).setId(null);
			listNew.get(i).setRoleStaticContent(null);
			for (RolesMappingPpd rol : listRoles) {
				if(rol.getNamePpd()!=null && rol.getNameSf()!=null &&
						rol.getRoleOperator()!=null && rol.getRoleOrga()!=null &&
						rol.getRoleType()!=null && rol.getCountUsers()!=null &&
						rol.getIdSf().equals(listNew.get(i).getIdSf())) 
				{
					if(validationRolOperator(rol.getRoleOperator())&&validationOrganization(rol.getRoleType())) {
						listNew.get(i).setId(rol.getId());
						this.successFactorAdmin.roleUpdate(listNew.get(i));
						j++;
					}else {
						return -1;
					}
					
				}
			}
			
			if(listNew.get(i).getId()==null && (listNew.get(i).getNamePpd()!=null && listNew.get(i).getNameSf()!=null &&
					listNew.get(i).getRoleOperator()!=null && listNew.get(i).getRoleOrga()!=null &&
							listNew.get(i).getRoleType()!=null && listNew.get(i).getCountUsers()!=null)) 
			{
				if(validationRolOperator(listNew.get(i).getRoleOperator())&&validationOrganization(listNew.get(i).getRoleType())) {
					listNew.get(i).setId(id);
					this.successFactorAdmin.roleInsert(listNew.get(i));
					j++;
				}else {
					return -1;
				}
				
			}
		}
		return j;
	}
	
	/**
	 * Import insert/update Lookuptable
	 * @param listNew
	 * @return
	 */
	public int insertUpdateLookuptable(List<LookupTable> listNew) {
		List<LookupTable> listAll = this.lookupsGetAll();
		int index =0;
		try {
			
			for (LookupTable lookNew : listNew) {
				int step = 0;
				for (LookupTable lookup : listAll) {
					if(lookNew.getId()!=null && lookup.getId().equals(lookNew.getId())) {
						if(lookNew.getCodeTable()!=null && !lookNew.getCodeTable().equals("") &&
								lookNew.getValueIn()!=null && !lookNew.getValueIn().equals("") &&
								lookNew.getValueOut()!=null && !lookNew.getValueOut().equals("") ) {
							
									this.lookupUpdate(lookNew);
									step ++; index ++;
						}
						
					}
				}
				
				if(step == 0) {
					if(lookNew.getCodeTable()!=null && !lookNew.getCodeTable().equals("") &&
							lookNew.getValueIn()!=null && !lookNew.getValueIn().equals("") &&
							lookNew.getValueOut()!=null && !lookNew.getValueOut().equals("") ) {
						
						this.lookupInsert(lookNew);
						index ++;
					}
							
								
				}
				
			}
			return index;
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Import insert/update StructureBusiness
	 * @param listNew
	 * @return
	 */
	public int insertUpdateStructure(List<StructureBusiness> listNew) {
		List<StructureBusiness> listAll = this.structureBusinessGetAll();
		int index=0;
		try {
			
			for (StructureBusiness item : listNew) {
				if(item.getStructureName()!=null && !item.getStructureName().equals("") && item.getParentStructure()==null) {
					
					if(existStructure(item)) {
						item.setId(null);
						logger.info("*******////exist////********"+item.getStructureName());
						index++;
					}else {
						item.setId(null);
						this.structureBusinessInsert(item, null);
						index++;
					}
					logger.info("*******///////////********"+item.getStructureName());
				}
			}
			
			for (StructureBusiness newItem : listNew) {
				int step = 0;
				
				for (StructureBusiness item : listAll) {
					if(newItem.getStructureName()!=null && !newItem.getStructureName().equals("") && item.getStructureName().equals(newItem.getStructureName())) {
						
						StructureBusiness temp = null;
						if(newItem.getParentStructure()!=null && newItem.getParentStructure().getStructureName()!=null && !newItem.getParentStructure().getStructureName().equals("")) {
							
							if(existStructure(newItem.getParentStructure())) {
								temp = idStructure(newItem.getParentStructure());
								temp = this.structureBusinessUpdate(temp.getId(), temp);
							}else {
								newItem.setId(null);
								StructureBusiness sb = new StructureBusiness();
								sb.setStructureName(newItem.getParentStructure().getStructureName());
								temp =this.structureBusinessInsert(sb, null);
							}
						}
						newItem.setParentStructure(temp);
						newItem.setId(item.getId());
						this.structureBusinessUpdate(item.getId(),newItem);
						step ++; index++;
						
					}
				}
				
				if(step == 0) {
					if(newItem.getStructureName()!=null && !newItem.getStructureName().equals("") && !existStructure(newItem)) {
						StructureBusiness temp = null;
						if(newItem.getParentStructure()!=null && newItem.getParentStructure().getStructureName()!=null &&!newItem.getParentStructure().getStructureName().equals("")) {
							
							if(existStructure(newItem.getParentStructure())) {
								temp = idStructure(newItem.getParentStructure());
								temp = this.structureBusinessUpdate(temp.getId(), temp);
							}else {
								newItem.setId(null);
								StructureBusiness sb = new StructureBusiness();
								sb.setStructureName(newItem.getParentStructure().getStructureName());
								temp =this.structureBusinessInsert(sb, null);
							}
						}
						
						StructureBusiness sb = new StructureBusiness();
						sb.setStructureName(newItem.getStructureName());
						sb.setParentStructure(temp);
						logger.info("*******///////////********"+newItem.getStructureName());
						this.structureBusinessInsert(sb, null);
						index++;
						
					}
				}
				
			}
			
			return index;
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
	}
	
	// -----------------------------------------
	// Methods Country
	
	/**
	 * Insert country
	 * 
	 * @param Countries entity
	 * @return Countries insert
	 */
	public Countries countryInsert(Countries country) {
		return successFactorAdmin.countryInsert(country);
	}
	
	/**
	 * Get All Countries
	 *
	 * @return List<Countries>
	 */
	public java.util.List<Countries> countriesGetAll() {
		return successFactorAdmin.countriesGetAll();
	}
	
	/**
	 * Delete Country
	 * @param Long id
	 * 
	 * */
	public void countryDelete(Long id) {
		successFactorAdmin.countryDelete(id);
	}
	
	/**
	 * Update Country
	 * @param Countries entity
	 * 
	 * */
	public Countries countryUpdate(Countries entity) {
		return successFactorAdmin.countryUpdate(entity);
	}
	
	/**
	 * Get All Countries by code
	 * @param String code
	 * @return List<Countries>
	 */
	public java.util.List<Countries> countriesGetByCode(String code){
		return successFactorAdmin.countriesGetByCode(code);
	}
	
	// -----------------------------------------
    // Methods SIGN TEMPLATE LIB
	/**
	 * Get All SIGN TEMPLATE LIB
	 *
	 * @return List<SignsTemplateLibrary>
	 */
	public java.util.List<SignsTemplateLibrary> signTemplateLibGetAll() {
		return successFactorAdmin.signTemplateLibGetAll();
	}
	/**
	 * Insert SIGN TEMPLATE LIB
	 * 
	 * @param SignsTemplateLibrary entity
	 * @return SignsTemplateLibrary insert
	 */
	public SignsTemplateLibrary signsTemplateLibraryInsert(SignsTemplateLibrary entity) {
		return successFactorAdmin.signsTemplateLibraryInsert(entity);
	}
	/**
	 * Delete SignsTemplateLibrary
	 * @param Long id
	 * */
	public void SignsTemplateLibraryDelete(Long id) {
		successFactorAdmin.SignsTemplateLibraryDelete(id);
	}
	/**
	 * Update SignsTemplateLibrary
	 * @param Countries entity 
	 * */
	public SignsTemplateLibrary SignsTemplateLibraryUpdate(SignsTemplateLibrary entity) {
		return successFactorAdmin.SignsTemplateLibraryUpdate(entity);			
	}
	
	//--------------------------------------
	//Methods Loggers
	
	/**
	 * removes all records with creation date less than the filter date
	 * @param Date filterDate
	 * @return boolean
	 * */
	public boolean loggerDeleteByDate(Date filterDate){
		return successFactorAdmin.loggerDeleteByDate(filterDate);
	}
	
	/**
	 * removes all records
	 * @return boolean
	 * */
	public boolean loggerDeleteAll() {
		return successFactorAdmin.loggerDeleteAll();
	}
	
	/**
	 * save Job Logger
	 * 
	 * @param Long idJob
	 * @param String code
	 * @param String user
	 * @return Long
	 */
	public Long jobLogSave(Long idJob,String code, String nameJob){
		return successFactorAdmin.jobLogSave(idJob, code, nameJob);
	}
	
	/**
	 * removes all records with the same id Job
	 * @param Date filterDate
	 * @return boolean
	 * */
	public boolean jobLogDeleteByIdJob(Long idJob) {
		return successFactorAdmin.deleteJobLogByIdJob(idJob);
	}
	
	/**
	 * removes all records with creation date less than the filter date
	 * @param Date filterDate
	 * @return boolean
	 * */
	public boolean jobLogDeleteByDate(Date filterDate) {
		return successFactorAdmin.deleteJobLogByDate(filterDate);
	}
	
	
	/**
	 * delete all information from a table
	 * @param String nameTable
	 * @return boolean
	 * */
	public boolean controlPanelDeleteAllByTable(String nameTable) {
		return successFactorAdmin.controlPanelDeleteAllByTable(nameTable);
	}
	
	// -----------------------------------------
	// Methods Country
	
	/**
	 * Insert Language
	 * 
	 * @param Language entity
	 * @return Language insert
	 */
	public Language languageInsert(Language entity) {
		return successFactorAdmin.languageInsert(entity);
	}
	
	/**
	 * Get All Language
	 *
	 * @return List<Language>
	 */
	public java.util.List<Language> languageGetAll() {
		return successFactorAdmin.languageGetAll();
	}
	
	/**
	 * Get All Language by code
	 * @param String code
	 * @return List<Language>
	 */
	public java.util.List<Language> languageGetByCode(String code) {
		return successFactorAdmin.languageGetByCode(code);
	}
	/**
	 * Delete Language
	 * @param Long id
	 * 
	 * */
	public void languageDelete(Long id) {
		successFactorAdmin.languageDelete(id);
	}
	/**
	 * Update Language
	 * @param Language entity
	 * 
	 * */
	public Language languageUpdate(Language entity) {
		return successFactorAdmin.languageUpdate(entity);			
	}
	
		// -----------------------------------------
		// Methods Lookup
		
		/**
		 * Insert country
		 * 
		 * @param Lookups entity
		 * @return Lookups insert
		 */
		public LookupTable lookupInsert(LookupTable lookup) {
			return successFactorAdmin.lookupInsert(lookup);
		}
		
		/**
		 * Get All Lookups
		 *
		 * @return List<Lookups>
		 */
		public java.util.List<LookupTable> lookupsGetAll() {
			return successFactorAdmin.lookupsGetAll();
		}
		
		/**
		 * Delete Country
		 * @param Long id
		 * 
		 * */
		public void lookupDelete(Long id) {
			successFactorAdmin.lookupDelete(id);
		}
		
		/**
		 * Update Country
		 * @param Countries entity
		 * 
		 * */
		public LookupTable lookupUpdate(LookupTable entity) {
			return successFactorAdmin.lookupUpdate(entity);
		}
		
		/**
		 * Get All Countries by code
		 * @param String code
		 * @return List<Countries>
		 */
		public java.util.List<LookupTable> lookupsGetByCode(String code){
			return successFactorAdmin.lookupsGetByCode(code);
		}
		
		/**
	 * Validate Role Operator
	 * @param String text
	 * 
	 */
	 public boolean validationRolOperator(String text) {
		 if(text.equals(UtilCodesEnum.CODE_ROLE_MAPPING_OPERATOR_1.getCode())||
				 text.equals(UtilCodesEnum.CODE_ROLE_MAPPING_OPERATOR_2.getCode())||
				 text.equals(UtilCodesEnum.CODE_ROLE_MAPPING_OPERATOR_3.getCode())) 
		 {
			 return true;
		 }else {
			 return false;
		 }
	 }
	 /**
	  * Validation Role Organization
	  * @param String text
	  * 
	  */
	public boolean validationOrganization(String text) {
		if(text.equals(UtilCodesEnum.CODE_ROLE_MAPPING_ORGANIZATION.getCode())||
				text.equals(UtilCodesEnum.CODE_ROLE_MAPPING_GROUP_ORGANIZATION.getCode())||
				text.equals(UtilCodesEnum.CODE_ROLE_MAPPING_LIST_ORGANIZATION.getCode())) 
		{
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Validation MetaData
	 * @param data
	 * @return
	 */
	public List<FieldsTemplateLibrary> validationMetadata(List<String> data) 
	{
		List<FieldsTemplateLibrary> newData = new ArrayList<>();
		SuccessFactorTemplateFacade sfTemplate = new SuccessFactorTemplateFacade();
		if(data != null && !data.isEmpty()) 
		{
			for (String item : data) 
			{
				ArrayList<FieldsTemplateLibrary> fieldsT = sfTemplate.templateFieldLibraryByName(item);
				if(fieldsT != null && !fieldsT.isEmpty()) {
					for (FieldsTemplateLibrary field : fieldsT) {
						newData.add(field);
					}
				}
			}
		}
		return newData;
	}
	
	/**
	 * 
	 * */
	public void updateInsertTemplate(List<TemplateInfoDto>listNew, List<TemplateInfoDto>listTemplate) 
	{
		//Facade
		SuccessFactorTemplateFacade SFTempladeFacade = new SuccessFactorTemplateFacade();
		
		//load signature template library
		List<SignsTemplateLibrary> signTempLib = this.signTemplateLibGetAll();
		//load eventListernerParam
		ArrayList<EventListenerParam> eventsListenerParam = this.eventsListenerGetAll();
		
		Gson gson = new Gson();
		for(TemplateInfoDto tempNew:listNew) 
		{
			Long idTemplate = tempNew.getIdTemplate();
			tempNew.setIdTemplate(null);
			
			//update default values
			if(tempNew.getFormatGenerated()==null)
				tempNew.setFormatGenerated(UtilCodesEnum.CODE_PDF_DOC.getCode());
			if(tempNew.getEmailSign()==null)
				tempNew.setEmailSign(UtilCodesEnum.CODE_MAPPING_MAIL.getCode());
			if(tempNew.getSelfGeneration()==null)
				tempNew.setSelfGeneration("0");
			
			for (TemplateInfoDto temp : listTemplate) 
			{
				if(idTemplate.equals(temp.getIdTemplate()))
				{
					if(tempNew.getTitle() != null && 
					   tempNew.getDescription() != null &&						 
					   tempNew.getLocale() != null &&
					   tempNew.getEsign() != null && 
					   tempNew.getModule() != null &&						
					   tempNew.getDocumentType() != null &&
					   tempNew.getFormat() != null &&
					   tempNew.getEmailSign() != null &&
					   tempNew.getSelfGeneration() != null &&
					   tempNew.getFormatGenerated() != null)
					{
						tempNew.setId(temp.getId());
						tempNew.setIdTemplate(temp.getIdTemplate());	
						tempNew.setFolder(temp.getFolder());
						tempNew.setEnabled(temp.getEnabled());						
						
						tempNew.setIdEventListener(null);
						EventListenerParam eventList = UtilMapping.getEventsListenerParamByName(tempNew.getNameEventListener(), eventsListenerParam);
						if(eventList!=null) {
							tempNew.setNameEventListener(eventList.getEventId());
							tempNew.setIdEventListener(eventList.getId());
						}
						
						String json = gson.toJson(tempNew);
						this.templateFacade.updateTemplateId(String.valueOf(temp.getIdTemplate()), json);
												
						//save filters
						if(tempNew.getFilters()!=null && tempNew.getFilters().size()>0)
						{
							SFTempladeFacade.deleteTemplateFilterByIdTemplate(temp.getIdTemplate());
							Template template = new Template();
							template.setId(temp.getIdTemplate());
							
							for(TemplateFilters filter:tempNew.getFilters())
							{
								filter.setId(null);
								filter.setTemplateId(template);
								SFTempladeFacade.templateFiltersInsert(filter);								
							}
						}
						
						//save signatures
						if(tempNew.getSingatureGroup()!=null && tempNew.getSingatureGroup().size()>0) {	
							SFTempladeFacade.templateSaveSings(
									temp.getIdTemplate(), 
									UtilMapping.loadSignsForSignGroup(tempNew.getSingatureGroup(),signTempLib));
						}
						
						//save metadata
						this.mappingPpdFieldMetaDelete(null,temp.getIdTemplate());
						List<FieldsTemplateLibrary> list = this.validationMetadata(tempNew.getMetadataList());
								
						if(list != null && !list.isEmpty()) {
							for (FieldsTemplateLibrary item : list) {							
								this.mappingPpdFieldMetaInsert(null, item.getId(),temp.getIdTemplate());
							}
						}
					}
				}
			}
			
			if(tempNew.getIdTemplate()==null)
			{
				if(tempNew.getTitle() != null && 
				   tempNew.getDescription() != null &&						 
				   tempNew.getLocale() != null &&
				   tempNew.getEsign() != null && 
				   tempNew.getModule() != null &&						
				   tempNew.getDocumentType() != null &&
				   tempNew.getFormat() != null &&
				   tempNew.getEmailSign() != null &&
				   tempNew.getSelfGeneration() != null &&
				   tempNew.getFormatGenerated() != null) 
				{
					tempNew.setFolder(null);
					tempNew.setEnabled(Boolean.TRUE);
					tempNew.setLatest_version(0);					
					
					//load eventListener
					tempNew.setIdEventListener(null);
					EventListenerParam eventList = UtilMapping.getEventsListenerParamByName(tempNew.getNameEventListener(), eventsListenerParam);
					if(eventList!=null) {
						tempNew.setNameEventListener(eventList.getEventId());
						tempNew.setIdEventListener(eventList.getId());
					}	
					
					String json = gson.toJson(tempNew);
										
					TemplateInfoDto tempNewInsert = this.templateFacade.createTemplateId(json);
					if(tempNewInsert.getIdTemplate()!=null)
					{						
						//save filters
						if(tempNew.getFilters()!=null && tempNew.getFilters().size()>0)
						{
							Template template = new Template();
							template.setId(tempNewInsert.getIdTemplate());
							
							for(TemplateFilters filter:tempNew.getFilters())
							{
								filter.setId(null);
								filter.setTemplateId(template);
								SFTempladeFacade.templateFiltersInsert(filter);								
							}
						}
						
						//save signatures
						if(tempNew.getSingatureGroup()!=null && tempNew.getSingatureGroup().size()>0) {	
							SFTempladeFacade.templateSaveSings(
									tempNewInsert.getIdTemplate(), 
									UtilMapping.loadSignsForSignGroup(tempNew.getSingatureGroup(),signTempLib));
						}				
						
						//save metadata
						this.mappingPpdFieldMetaDelete(null,tempNewInsert.getIdTemplate());
						List<FieldsTemplateLibrary> list = this.validationMetadata(tempNew.getMetadataList());
								
						if(list != null && !list.isEmpty()) {
							for (FieldsTemplateLibrary item : list) {							
								this.mappingPpdFieldMetaInsert(null, item.getId(),tempNewInsert.getIdTemplate());
							}
						}
					}
				}
			}
		}
	}	
	
	public int updateInsertFolder(List<FolderDTO> listNew, List<FolderDTO> listFolder) {
		
		Gson gson = new Gson();
		int item = 0;
		List<FolderDTO> subFolder;
		Folder fd = new Folder();
		Folder parent = new Folder();
		parent.setId(new Long(-1));
		FolderDAO fdao = new FolderDAO();
		
		for (FolderDTO folderNew : listNew) {
			int p=0;
			
			for (FolderDTO folderItem : listFolder) {
				if(folderItem.getId()!=null && folderNew.getId()!=null && (folderItem.getId().equals(folderNew.getId()))){
					Collection<FolderUser> listUser=folderNew.getUsers();
					JSONObject json = new JSONObject();
					FolderUser folderuser = new FolderUser();
					json.put("id",folderNew.getId());
					json.put("title", folderNew.getTitle());
					logger.info("*****---",json.toString());
					ppdTemplateGeneratorFacade.createFolderId(json.toString(), "");
					p=1;
					
					if(folderNew.getNodesFolders()!=null&&!folderNew.getNodesFolders().isEmpty()) {
						subFolder = folderNew.getNodesFolders();
						Folder fld = new Folder();
						Folder parent2 = new Folder();
						for (FolderDTO sub : subFolder) {
							logger.info("----------childrem " +sub.getTitle());
							parent2.setId(folderNew.getId());
							parent2.setParentFolder(null);
							
							fld.setId(sub.getId());
							fld.setTitle(sub.getTitle());
							String val = gson.toJson(fld);
							logger.info("----------json_childrem " + val );
							if(this.listVerify(listFolder, sub.getId())) {
								fld.setParentFolder(null);
								ppdTemplateGeneratorFacade.createFolderId(val, "");
							}else {
								fld.setParentFolder(parent2);
								ppdTemplateGeneratorFacade.createFolderId(val, UserManager.getUserId());
							}
							
						}
					}
					
					if(listUser!=null && !listUser.isEmpty()) {
						for (FolderUser user : listUser) {
							FolderUserDAO fudao = new FolderUserDAO();
							
							folderuser = new FolderUser();
							folderuser.setFolderId(folderNew.getId());
							folderuser.setUserId(user.getUserId());
							logger.info("***---***"+gson.toJson(folderuser));
							if(!user.getUserId().equals(UserManager.getUserId())) {
								fudao.save(folderuser);
							}
						}
					}
				}
				
			}
			
			if(p==0) {
				JSONObject json = new JSONObject();
				JSONObject json2 = new JSONObject();
				JSONArray jarray = new JSONArray();
				Collection<FolderUser> listUser=folderNew.getUsers();
				FolderUser folderuser = new FolderUser();
				json.put("parentFolder", "");
				json.put("title", folderNew.getTitle());
				fd.setTitle(folderNew.getTitle());
				if(folderNew.getId()!=null) {
					fd.setId(folderNew.getId());
				}
				fd.setParentFolder(parent);
				logger.info("*****---",gson.toJson(fd));
				Folder newF = ppdTemplateGeneratorFacade.createFolderId(gson.toJson(fd), UserManager.getUserId());
				
				if(folderNew.getNodesFolders()!=null&&!folderNew.getNodesFolders().isEmpty()) {
					subFolder = folderNew.getNodesFolders();
					Folder fld = new Folder();
					Folder parent2 = new Folder();
					for (FolderDTO sub : subFolder) {
						logger.info("----------childrem " +sub.getTitle());
						parent2.setId(newF.getId());
						parent2.setParentFolder(null);
						fld.setParentFolder(parent2);
						if(sub.getId()!=null) {
							fld.setId(sub.getId());
						}
						
						fld.setTitle(sub.getTitle());
						String val = gson.toJson(fld);
						logger.info("----------json_childrem " + val );
						
						ppdTemplateGeneratorFacade.createFolderId(val, UserManager.getUserId());
						
					}
				}
				
				if(listUser!=null && !listUser.isEmpty()) {
					for (FolderUser user : listUser) {
						FolderUserDAO fudao = new FolderUserDAO();
						
						folderuser = new FolderUser();
						folderuser.setFolderId(newF.getId());
						folderuser.setUserId(user.getUserId());
						logger.info("***---***"+gson.toJson(folderuser));
						if(!user.getUserId().equals(UserManager.getUserId())) {
							fudao.save(folderuser);
						}
						
					}
				}
			}
			
			item ++;
		
		}
		
		return item;
	}	
	
	public Boolean listVerify(List<FolderDTO> listFolder, Long id) 
	{
		for (FolderDTO item : listFolder) {
			
			if(item.getNodesFolders()!=null && !item.getNodesFolders().isEmpty()) {
				for (FolderDTO sub : item.getNodesFolders()) {
					if(sub.getId().equals(id)) {
						return Boolean.TRUE;
					}
				}
			}
		}
		
		return Boolean.FALSE;
	}
	
	public Boolean existStructure(StructureBusiness sb) {
		List<StructureBusiness> listAll = this.structureBusinessGetAll();
		
		for (StructureBusiness item: listAll) {
			if(item.getStructureName()!=null && item.getStructureName().equals(sb.getStructureName())){
				return true;
			}
		}
		
		return false;
	}
	
	public StructureBusiness idStructure(StructureBusiness sb) {
		List<StructureBusiness> listAll = this.structureBusinessGetAll();
		
		for (StructureBusiness item: listAll) {
			if(item.getStructureName()!=null && item.getStructureName().equals(sb.getStructureName())){
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * Inset Manager Role
	 * @param ManagerRole mr
	 * @return ManagerRole
	 */
	public ManagerRole insertManagerRole(ManagerRole mr) {
		return successFactorAdmin.insertManagerRole(mr);
	}
	
	/**
	 * Get list all Manager Role
	 * @return List<ManagerRole>
	 */
	public List<ManagerRole> getAllManagerRole(){
		return successFactorAdmin.getAllManagerRole();
	}
	
	/**
	 * Delete Manager Role
	 * @param Long id
	 */
	public void deleteManagerRole (Long id) {
		successFactorAdmin.deleteManagerRole(id);
	}
	
	/**
	 * Update Manager Role
	 * @param ManagerRole mr
	 * @return ManagerRole
	 */
	public ManagerRole updateManagerRole (ManagerRole mr) {
		return successFactorAdmin.updateManagerRole(mr);
	}
	
	/**
	 * Get all user for Manager Role Group
	 * @param Long id
	 * @return List<ManagerRoleGroup>
	 */
	public List<ManagerRoleGroup> getAllManagerGroup(Long id){
		return successFactorAdmin.getAllManagerGroup(id);
	}
	
	/**
	 * Insert Manager Role Group   
	 * @param ManagerRoleGroup mr
	 * @return
	 */
	public ManagerRoleGroup insertManagerRoleGroup(ManagerRoleGroup mr) {
		return successFactorAdmin.insertManagerRoleGroup(mr);
	}
	
	/**
	 * Delete Manager Role Group
	 * 
	 * @param Long id
	 */
	public void deleteManagerRoleGroup (Long id) {
		successFactorAdmin.deleteManagerRoleGroup(id);
	}
	
	/**
	 * Export Manager role and Group
	 * @return List<ManagerRole>
	 */
	public List<ManagerRole> getAllExportManager() {
		try {
			 List<ManagerRole> list = this.getAllManagerRole();
			 
			 for (ManagerRole mr : list) {
				mr.setListGroup(this.getAllManagerGroup(mr.getId()));
			}
			return list;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public int insertUpdateManagerRole(List<ManagerRole> listNew) {
		List<ManagerRole> listAll = this.getAllManagerRole();
		int index =0;
		try {
			 
			 
			 for (ManagerRole mr : listNew) {
				 int step = 0;
				 
				
					for (ManagerRole role : listAll) {
						
						if(mr.getNamesource()!=null && !mr.getNamesource().equals("") && mr.getNamesource().equals(role.getNamesource())) {
							
							if(mr.getDescription()!=null && mr.getIcon() != null && !mr.getDescription().equals("") && !mr.getIcon().equals("")) {
								mr.setId(role.getId());
								ManagerRole newMr = this.updateManagerRole(mr);
								if(!mr.getListGroup().isEmpty()) {
									logger.info("******************ManagerRole Up"+newMr.getId());
									this.insertGroupManagerRole(mr.getListGroup(), newMr.getId());
								}
								step ++;
								index ++;
							}
							
						}
					}
					
					if(step == 0) {
						
						if(mr.getNamesource()!=null && mr.getDescription()!=null && mr.getIcon() != null && 
								!mr.getNamesource().equals("")&& !mr.getDescription().equals("") && !mr.getIcon().equals("")) 
						{
							mr.setId(null);
							ManagerRole mrole = new ManagerRole();
							mrole.setDescription(mr.getDescription());
							mrole.setNamesource(mr.getNamesource());
							mrole.setIcon(mr.getIcon());
							ManagerRole newMr = this.insertManagerRole(mrole);
							if(!mr.getListGroup().isEmpty()) {
								logger.info("******************ManagerRole Ins"+newMr.getId());
								this.insertGroupManagerRole(mr.getListGroup(), newMr.getId());
							}
							index ++;
							
						}
					}
			}
			return index;
			
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public void insertGroupManagerRole(List<ManagerRoleGroup> listNew, Long id) {
		
		for (ManagerRoleGroup mrT : listNew) {
			if(mrT.getGroupId()!=null && !mrT.getGroupId().equals("")) {
				ManagerRoleGroup mrg= new ManagerRoleGroup();
				mrg.setGroupId(mrT.getGroupId());
				mrg.setManagerRoleId(id);
				this.insertManagerRoleGroup(mrg);
				logger.info("/*/---////***"+mrT.getGroupId());
			}
		}
	}
}