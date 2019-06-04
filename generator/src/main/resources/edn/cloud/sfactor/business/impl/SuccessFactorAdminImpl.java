package edn.cloud.sfactor.business.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.GenErrorInfoDto;
import edn.cloud.business.dto.integration.SFAdmin;
import edn.cloud.sfactor.business.connectivity.HttpConnectorSuccessFactor;
import edn.cloud.sfactor.business.interfaces.SuccessFactorAdmin;
import edn.cloud.sfactor.persistence.dao.AdminParametersDAO;
import edn.cloud.sfactor.persistence.dao.CountriesDAO;
import edn.cloud.sfactor.persistence.dao.EventListenerCtrlHistoDAO;
import edn.cloud.sfactor.persistence.dao.EventListenerCtrlProcessDAO;
import edn.cloud.sfactor.persistence.dao.EventListenerDAO;
import edn.cloud.sfactor.persistence.dao.EventListenerDocHistoDAO;
import edn.cloud.sfactor.persistence.dao.EventListenerDocProcessDAO;
import edn.cloud.sfactor.persistence.dao.FieldsMappingMetaDAO;
import edn.cloud.sfactor.persistence.dao.FieldsMappingPpdDAO;
import edn.cloud.sfactor.persistence.dao.JobLogDAO;
import edn.cloud.sfactor.persistence.dao.LanguageDAO;
import edn.cloud.sfactor.persistence.dao.LoggerDAO;
import edn.cloud.sfactor.persistence.dao.LookupDAO;
import edn.cloud.sfactor.persistence.dao.ManagerRoleDAO;
import edn.cloud.sfactor.persistence.dao.ManagerRoleGroupDAO;
import edn.cloud.sfactor.persistence.dao.RolesMappingPpdDAO;
import edn.cloud.sfactor.persistence.dao.SFAdminDAO;
import edn.cloud.sfactor.persistence.dao.SfEntityDAO;
import edn.cloud.sfactor.persistence.dao.SignsTemplateLibraryDAO;
import edn.cloud.sfactor.persistence.dao.StructureBusinessDAO;
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
import edn.cloud.sfactor.persistence.entities.JobsLog;
import edn.cloud.sfactor.persistence.entities.Language;
import edn.cloud.sfactor.persistence.entities.LookupTable;
import edn.cloud.sfactor.persistence.entities.ManagerRole;
import edn.cloud.sfactor.persistence.entities.ManagerRoleGroup;
import edn.cloud.sfactor.persistence.entities.RolesMappingPpd;
import edn.cloud.sfactor.persistence.entities.SfEntity;
import edn.cloud.sfactor.persistence.entities.SfNavProperty;
import edn.cloud.sfactor.persistence.entities.SfProperty;
import edn.cloud.sfactor.persistence.entities.SignsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.StructureBusiness;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class SuccessFactorAdminImpl implements SuccessFactorAdmin {
	private UtilLogger loggerSingle = UtilLogger.getInstance();

	// -----------------------------------------
	// methods admin parameters.

	/**
	 * get admin parameters by name code
	 * 
	 * @param String
	 *            nameCode
	 * @return AdminParameters
	 */
	public AdminParameters adminParamGetByNameCode(String nameCode) {
		AdminParametersDAO dao = new AdminParametersDAO();
		return dao.getAdminParamGetByNameCode(nameCode);
	}

	/**
	 * insert admin parameters
	 * 
	 * @param AdminParameters
	 *            param
	 * @return AdminParameters
	 */
	public AdminParameters adminParamInsert(AdminParameters param) {
		AdminParametersDAO dao = new AdminParametersDAO();
		return dao.saveNew(param);
	}

	/**
	 * update admin parameters
	 * 
	 * @param AdminParameters param
	 * @return AdminParameters
	 */
	public AdminParameters adminParamUpdate(AdminParameters param) {
		AdminParametersDAO dao = new AdminParametersDAO();
		return dao.save(param);
	}

	// -----------------------------------------
	// methods events

	/**
	 * get events by name
	 * 
	 * @param String
	 *            eventName
	 * @return EventListener
	 */
	public EventListenerParam eventsGetByName(String eventName) {
		EventListenerDAO dao = new EventListenerDAO();
		return dao.getByName(eventName);
	}

	/**
	 * get all eventListener
	 * 
	 * @return ArrayList<EventListener>
	 */
	public ArrayList<EventListenerParam> eventListenerGetAll() {
		EventListenerDAO list = new EventListenerDAO();
		Collection<EventListenerParam> li = list.getAll();
		ArrayList<EventListenerParam> responseList = new ArrayList<EventListenerParam>(li);

		return responseList;
	}

	/**
	 * query event listener by id
	 * 
	 * @param Long
	 *            idEvent
	 * @return EventListener
	 */
	public EventListenerParam eventListenerById(Long idEvent) {
		EventListenerDAO dao = new EventListenerDAO();
		return dao.getById(idEvent);
	}

	/**
	 * Insert event listener
	 * 
	 * @param EventListenerParam
	 *            entity
	 * @return EventListener
	 */
	public EventListenerParam eventListenerInsert(EventListenerParam entity) {
		EventListenerDAO dao = new EventListenerDAO();
		return dao.saveNew(entity);
	}

	/**
	 * Delete event listener
	 * 
	 * @param Long
	 *            idEvent
	 */
	public Boolean eventListenerDelete(Long idEvent) {
		EventListenerDAO dao = new EventListenerDAO();
		return dao.deleteEventListenerById(idEvent);
	}

	/**
	 * delete event listener ctrl
	 * 
	 * @param EventListenerCtrlProcess
	 *            entity
	 */
	public void eventListenerCtrlDelete(EventListenerCtrlProcess entity) {
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		dao.delete(entity);
	}

	/**
	 * update event listener
	 * 
	 * @param EventListenerParam
	 *            entity
	 * @return EventListener
	 */
	public EventListenerParam eventListenerUpdate(EventListenerParam entity) {
		EventListenerDAO dao = new EventListenerDAO();
		return dao.save(entity);
	}
	

	// -----------------------------------------
	// methods mapping.
	

	/**
	 * query field mapping ppd by id
	 * 
	 * @param Long idField
	 * @param Boolean loadNameMetadata
	 * @return FieldsMappingPpd
	 */
	public FieldsMappingPpd mappingPpdFieldById(Long idField,Boolean loadNameMetadata)
	{
		FieldsMappingPpdDAO dao = new FieldsMappingPpdDAO();
		FieldsMappingMetaDAO metaDao = new FieldsMappingMetaDAO();
		
		FieldsMappingPpd fieldMappingPpd =  dao.getById(idField);
		
		if(fieldMappingPpd!=null)
		{
			Collection<FieldsMappingMeta> metaList = metaDao.getAllByIdFieldMap(fieldMappingPpd.getId(),null);
			fieldMappingPpd.setMetadataList(new ArrayList<String>());
			
			if(metaList!=null)
			{
				for (Iterator iterator = metaList.iterator(); iterator.hasNext();) 
				{
					FieldsMappingMeta itemMeta = (FieldsMappingMeta)iterator.next();
					if(loadNameMetadata)
						fieldMappingPpd.getMetadataList().add(itemMeta.getFieldsTemplateLibrary().getNameSource());
					else
						fieldMappingPpd.getMetadataList().add(itemMeta.getFieldsTemplateLibrary().getId().toString());	
				}					
			}
		}
		
		return fieldMappingPpd;
	}

	/**
	 * get all fields mapping ppd
	 * 
	 * @param Boolean isAttach / optional
	 * @param String moduleAttach
	 * @param Boolean isActive
	 * @param Boolean loadNameMetadata
	 * @return ArrayList<FieldsMappingPpd>
	 */
	public ArrayList<FieldsMappingPpd> mappingPpdFieldsGetAll(Boolean isAttach,String moduleAttach,Boolean isActive,Boolean loadNameMetadata) {
		FieldsMappingPpdDAO dao = new FieldsMappingPpdDAO();
		FieldsMappingMetaDAO metaDao = new FieldsMappingMetaDAO();
		Collection<FieldsMappingPpd> collection = dao.getAllFieldsMapping(isAttach,moduleAttach,isActive);
		ArrayList<FieldsMappingPpd> responseList = new ArrayList<FieldsMappingPpd>(collection);
		
		if(responseList!=null && responseList.size()>0)
		{
			for(FieldsMappingPpd item:responseList)
			{
				if(loadNameMetadata!=null) {
					Collection<FieldsMappingMeta> metaList = metaDao.getAllByIdFieldMap(item.getId(),null);
					item.setMetadataList(new ArrayList<String>());
					
					if(metaList!=null)
					{
						for (Iterator iterator = metaList.iterator(); iterator.hasNext();) 
						{
							FieldsMappingMeta itemMeta = (FieldsMappingMeta)iterator.next();
							if(loadNameMetadata)
								item.getMetadataList().add(itemMeta.getFieldsTemplateLibrary().getNameSource());
							else
								item.getMetadataList().add(itemMeta.getFieldsTemplateLibrary().getId().toString());	
						}					
					}				
				}
			}
		}
		
		return responseList;
	}
	
	
	/**
	 * determines if the Field Mapping is used by another entity
	 * @param Long id Field Mapping	  
	 * @return GenErrorInfoDto
	 * */
	public GenErrorInfoDto mappingFieldIsUsedById(Long id) {
		FieldsMappingPpdDAO dao = new FieldsMappingPpdDAO();
		return dao.mappingFieldIsUsedById(id);
	}
	
	
	/**
	 * get all mapping fields by name
	 * @param String name
	 * @param Boolean loadNameMetadata
	 * @return ArrayList<FieldsMappingPpd>
	 * */
	public ArrayList<FieldsMappingPpd> mappingPpdFieldsByName(String name, Boolean loadNameMetadata)
	{
		FieldsMappingPpdDAO dao = new FieldsMappingPpdDAO();
		FieldsMappingMetaDAO metaDao = new FieldsMappingMetaDAO();
		
		ArrayList<FieldsMappingPpd> responseList = new ArrayList<FieldsMappingPpd>(dao.getAllFieldsMappingByName(name));
		
		if(responseList!=null && responseList.size()>0)
		{
			for(FieldsMappingPpd item:responseList)
			{
				Collection<FieldsMappingMeta> metaList = metaDao.getAllByIdFieldMap(item.getId(),null);
				item.setMetadataList(new ArrayList<String>());
				
				if(metaList!=null)
				{
					for (Iterator iterator = metaList.iterator(); iterator.hasNext();) 
					{
						FieldsMappingMeta itemMeta = (FieldsMappingMeta)iterator.next();
						
						if(loadNameMetadata)
							item.getMetadataList().add(itemMeta.getFieldsTemplateLibrary().getNameSource());
						else
							item.getMetadataList().add(itemMeta.getFieldsTemplateLibrary().getId().toString());
					}					
				}
			}
		}
		
		return responseList;
	}
	
	
	/**
	 * get Metadata associated with Metadata
	 * @param FieldsMappingPpd fielMapping
	 * @param String loadType
	 * @return List<FieldsMappingMeta>
	 * */
	public List<FieldsMappingMeta> mappingPPdFieldsGetMetada(FieldsMappingPpd fielMapping)
	{
		List<FieldsMappingMeta> response = new ArrayList<FieldsMappingMeta>();
		
		if(fielMapping!=null)
		{
			FieldsMappingMetaDAO metaDao = new FieldsMappingMetaDAO();
			Collection<FieldsMappingMeta> metaList = metaDao.getAllByIdFieldMap(fielMapping.getId(),null);
			
			if(metaList!=null)
			{
				for (Iterator iterator = metaList.iterator(); iterator.hasNext();){
					FieldsMappingMeta itemMeta = (FieldsMappingMeta)iterator.next();
					response.add(itemMeta);
				}
			}
		}
		
		return response;
	}

	/**
	 * Insert fields Mapping Ppd
	 * 
	 * @param FieldsMappingPpd
	 *            entity
	 * @return FieldsMappingPpd entity with id
	 */
	public FieldsMappingPpd mappingPpdFieldsInsert(FieldsMappingPpd entity) {
		FieldsMappingPpdDAO dao = new FieldsMappingPpdDAO();
		entity = dao.saveNew(entity);
		return entity;
	}

	/**
	 * delete field mapping ppd
	 * 
	 * @param FieldTemplatesDto variables
	 * @param Long idTemplate
	 */
	public boolean mappingPpdFieldDelete(Long id,Long idTemplate) {
		FieldsMappingPpdDAO dao = new FieldsMappingPpdDAO();
		FieldsMappingMetaDAO metaDao = new FieldsMappingMetaDAO();
		
		//firts delete metadata
		metaDao.deleteAllByFieldsMapping(id,idTemplate);		
		return dao.deleteFieldsMappingPpdById(id);
	}

	/**
	 * update fields mapping ppd
	 * 
	 * @param FieldsMappingPpd
	 *            entity
	 */
	public FieldsMappingPpd mappingPpdFieldUpdate(FieldsMappingPpd entity) {
		FieldsMappingPpdDAO dao = new FieldsMappingPpdDAO();
		return dao.save(entity);
	}
	
	/**
	 * get mapping field by id
	 * @param Long idField
	 * @return FieldsMappingPpd
	 */
	public FieldsMappingPpd mappingPpdFieldGetId(Long idField) {
		FieldsMappingPpdDAO dao = new FieldsMappingPpdDAO();
		return dao.getById(idField);
	}	
	
	/**
	 * delete all mapping ppd field metadata by id field mapping ppd 
	 * @param fieldMappingPpdId
	 * @param Long idTemplate
	 * */
	public void mappingPpdFieldMetaDelete(Long fieldMappingPpdId,Long idTemplate)
	{
		FieldsMappingMetaDAO metaDao = new FieldsMappingMetaDAO();		
		metaDao.deleteAllByFieldsMapping(fieldMappingPpdId,idTemplate);	
	}
	
	/**
	 * Get all field mapping by filters
	 * @param Long idFielMapping
	 * @param Long idFieldTemplateLib
	 * @return Collection<FieldsMappingMeta>
	 * */
	public Collection<FieldsMappingMeta> mappingPpdFieldMetaGetByRef(Long idFielMapping,Long idFieldTemplateLib)
	{
		FieldsMappingMetaDAO metaDao = new FieldsMappingMetaDAO();
		return metaDao.getAllByIdFieldMap(idFielMapping,idFieldTemplateLib);
	}
	
	/**
	 * get FieldsMappingMeta by id Mapping Field ppd
	 * @param fieldMappingPpdId
	 * */
	public ArrayList<FieldsMappingMeta> mappingPpdFieldMetaGetByIdFieldMapp(Long fieldMappingPpdId)
	{
		FieldsMappingMetaDAO metaDao = new FieldsMappingMetaDAO();
		Collection<FieldsMappingMeta> metaList = metaDao.getAllByIdFieldMap(fieldMappingPpdId,null);
		
		if(metaList!=null)
		{
			ArrayList<FieldsMappingMeta> returnList = new ArrayList<FieldsMappingMeta>(metaList);
			return returnList;
		}
		
		return new ArrayList<FieldsMappingMeta>();
	}	
	
	/**
	 * Insert new mapping field metadata
	 * @param Long idFieldTemplate
	 * @param Long idTemplateLib
	 * @param Long idTemplate
	 * */
	public void mappingPpdFieldMetaInsert(Long idFieldTemplate,Long idTemplateLib,Long idTemplate)
	{	
		FieldsTemplateLibrary lib = new FieldsTemplateLibrary();
		lib.setId(idTemplateLib);
		
		FieldsMappingMeta metadata = new FieldsMappingMeta();
		
		if(idFieldTemplate != null) {
			metadata.setFieldsMappingPpdId(idFieldTemplate);
		}
		if(idTemplateLib != null) {
			metadata.setTemplateId(idTemplate);
		}
		
		metadata.setFieldsTemplateLibrary(lib);
		
		FieldsMappingMetaDAO metaDao = new FieldsMappingMetaDAO();
		metaDao.saveNew(metadata);
	}

	// -----------------------------------------
	// methods ScructureBussiness

	/**
	 * get all Structure Business
	 * 
	 * @return ArrayList<StructureBusiness>
	 */
	public ArrayList<StructureBusiness> structureBusinessGetAll() {
		StructureBusinessDAO list = new StructureBusinessDAO();
		Collection<StructureBusiness> li = list.getAll();
		ArrayList<StructureBusiness> responseList = new ArrayList<StructureBusiness>(li);

		return responseList;
	}

	/**
	 * query event Structure Business
	 * 
	 * @param Long
	 *            idEvent
	 * @return StructureBusiness
	 */
	public StructureBusiness structureBusinessById(Long idStructure) {
		StructureBusinessDAO dao = new StructureBusinessDAO();
		return dao.getById(idStructure);
	}

	/**
	 * Insert Structure business
	 * 
	 * @param EventListenerParam
	 *            entity
	 * @param Long
	 *            idParent
	 * @return EventListener
	 */
	public StructureBusiness structureBusinessInsert(StructureBusiness entity, Long parentId) {
		StructureBusinessDAO dao = new StructureBusinessDAO();
		if (parentId != null) {
			StructureBusiness parent = dao.getById(parentId);
			entity.setParentStructure(parent);
		}
		return dao.saveNew(entity);
	}

	/**
	 * delete Structure Business
	 * 
	 * @param EventListenerParam
	 *            variables
	 * @return boolean
	 */
	public boolean structureBusinessDelete(Long id) {
		StructureBusinessDAO dao = new StructureBusinessDAO();
		return dao.deleteEntityId(id);
	}

	/**
	 * update Structure Business
	 * 
	 * @param StructureBusiness
	 *            entity
	 * @return StructureBusiness
	 */
	public StructureBusiness structureBusinessUpdate(Long id, StructureBusiness entity) {
		StructureBusinessDAO dao = new StructureBusinessDAO();
		Boolean status = false;
		if (entity.getParentStructure() != null) {
			StructureBusiness parent = dao.getById(entity.getParentStructure().getId());

			status = loopParent(parent, status, id);
		}
		if (status) {
			return dao.getById(id);
		} else {
			return dao.save(entity);
		}

	}

	private Boolean loopParent(StructureBusiness entity, Boolean status, Long id) {
		loggerSingle.info("+++" + entity.getId().toString());
		loggerSingle.info("---" + id.toString());

		if (entity.getId().equals(id)) {
			status = true;
			return status;
		} else {
			if (entity.getParentStructure() != null) {
				return loopParent(entity.getParentStructure(), status, id);
			} else {
				return status;
			}
		}

	}

	// ----------------------------------------------
	// methods event listener

	/**
	 * get all event listener ctrl document general
	 * 
	 * @param Long idEventCtlHisto
	 * @param FilterQueryDto filter
	 * @return ArrayList<EventListenerDocHistory>
	 */
	public ArrayList<EventListenerDocHistory> eventsListenerDocHistoGetAll(Long idEventCtlHisto,FilterQueryDto filter) {
		EventListenerDocHistoDAO dao = new EventListenerDocHistoDAO();
		Collection<EventListenerDocHistory> li = dao.getAllEventListerDocHisto(idEventCtlHisto,filter);
		ArrayList<EventListenerDocHistory> responseList = new ArrayList<EventListenerDocHistory>(li);
		return responseList;
	}
	
	/**
	 * get event listener ctrl document by id
	 * 
	 * @param Long idEventCtlHisto
	 * @return EventListenerDocHistory
	 */
	public EventListenerDocHistory eventsListenerDocHistoGetById(Long idEventCtlHisto) 
	{
		EventListenerDocHistoDAO dao = new EventListenerDocHistoDAO();
		return dao.getById(idEventCtlHisto);		
	}

	/**
	 * Insert event listener ctrl document
	 * 
	 * @param EventListenerDocHistory
	 *            entity
	 * @return EventListenerDoc
	 */
	public EventListenerDocHistory eventListenerDocHistoInsert(EventListenerDocHistory entity) {
		EventListenerDocHistoDAO dao = new EventListenerDocHistoDAO();
		return dao.saveNew(entity);
	}


	/**
	 * Get all Event Listener Control process by start date and status
	 * 
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, can by null
	 * @param Integer maxReg maximum number of records to process, optional
	 * @param Boolean isFutureDates
	 * @param Long fkMassiveLoad
	 * @param String statusMassiveToLoad
	 * @return Collection<EventListenerCtrlProcess>
	 */
	public ArrayList<EventListenerCtrlProcess> eventsListenerCtrlProcGetAll(Date startDate, String statusToLoad, Integer maxReg, Boolean isFutureDates, FilterQueryDto filter,Long fkMassiveLoad,String statusMassiveToLoad) {
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		Collection<EventListenerCtrlProcess> li = dao.getAllEventListerCtrlProcess(startDate, statusToLoad, maxReg, isFutureDates, filter,fkMassiveLoad,statusMassiveToLoad);
		ArrayList<EventListenerCtrlProcess> responseList = new ArrayList<EventListenerCtrlProcess>(li);

		return responseList;
	}

	/**
	 * Get Event Listener Control process by id
	 * 
	 * @param Long
	 *            idCtrl
	 * @return EventListenerCtrlProcess
	 */
	public EventListenerCtrlProcess eventsListenerCtrlProcGetByid(Long idCtrl) {
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		EventListenerCtrlProcess eventCtrl = dao.getById(idCtrl);
		return eventCtrl;
	}

	/**
	 * get all event listener ctrl history general
	 * @param FilterQueryDto filter
	 * @param Long refMassLoadId
	 * @return ArrayList<EventListenerCtrlHistory>
	 */
	public ArrayList<EventListenerCtrlHistory> eventsListenerCtrlHistoGetAll(FilterQueryDto filter,Long refMassLoadId) {
		EventListenerCtrlHistoDAO dao = new EventListenerCtrlHistoDAO();
		Collection<EventListenerCtrlHistory> li = dao.getAllEventListerCtrlHisto(filter,refMassLoadId);
		ArrayList<EventListenerCtrlHistory> responseList = new ArrayList<EventListenerCtrlHistory>(li);

		return responseList;
	}

	/**
	 * delete all events or specific event ctrl histo
	 * 
	 * @param EventListenerCtrlHistory
	 *            entity is null delete all history
	 */
	public void eventsListenerCtrlHistoDelete(EventListenerCtrlHistory entity) {
		EventListenerCtrlHistoDAO dao = new EventListenerCtrlHistoDAO();
		EventListenerDocHistoDAO docDao = new EventListenerDocHistoDAO();

		if (entity != null) {
			dao.delete(entity);
		} else {
			docDao.deleteAllEventListeAttachments();
			dao.deleteAllEventListerCtrlHisto();
		}
	}

	/**
	 * Insert event listener ctrl
	 * 
	 * @param EventListenerCtrlHistory entity
	 * @return EventListenerCtrl
	 */
	public EventListenerCtrlHistory eventListenerCtrlHistoInsert(EventListenerCtrlHistory entity) {
		EventListenerCtrlHistoDAO dao = new EventListenerCtrlHistoDAO();
		return dao.saveNew(entity);
	}
	
	/**
	 * get event listener ctrl by id
	 * 
	 * @param Long idEventListenerCtrlHisto
	 * @return EventListenerCtrlHistory
	 */
	public EventListenerCtrlHistory eventListenerCtrlHistoById(Long id) {
		EventListenerCtrlHistoDAO dao = new EventListenerCtrlHistoDAO();
		return dao.getById(id);
	}	
	
	/**
	 * get event listener ctrl by id
	 * 
	 * @param Long idEventListenerCtrlHisto
	 * @return EventListenerDoc
	 */
	public EventListenerDocHistory eventListenerDocHistoById(Long id) {
		EventListenerDocHistoDAO dao = new EventListenerDocHistoDAO();
		return dao.getById(id);
	}	

	/**
	 * return count of history by id ctrl
	 * 
	 * @param Long
	 *            id
	 */
	public long getEventListenerCountHistoByidCrtl(Long id) {
		EventListenerCtrlHistoDAO dao = new EventListenerCtrlHistoDAO();
		return dao.getEventListenerCountHistoByidCrtl(id);
	}
	
	/**
	 * get counter by status in event listener history
	 * @param filter
	 * @param Long fkMassiveLoad 
	 * @return List<String[]>
	 * @return
	 */	
	public List<Object[]> eventListenerHistoGetStatusCount(FilterQueryDto filter,Long fkMassiveLoad){
		EventListenerCtrlHistoDAO dao = new EventListenerCtrlHistoDAO();
		return dao.getStatusCount(filter, fkMassiveLoad);
	}	
	
	/**
	 * get counter by status in event listener
	 * @param filter
	 * @param Long fkMassiveLoad 
	 * @return List<String[]>
	 * @return
	 */	
	public List<Object[]> eventListenerGetStatusCount(FilterQueryDto filter,Long fkMassiveLoad){
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		return dao.getStatusCount(filter, fkMassiveLoad);
	}	

	/**
	 * Insert event listener ctrl to Process
	 * 
	 * @param EventListenerCtrlProcess
	 *            entity
	 * @return EventListenerCtrlProcess
	 */
	public EventListenerCtrlProcess eventListenerCtrlProcessInsert(EventListenerCtrlProcess entity) {
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		return dao.saveNew(entity);
	}

	/**
	 * Update event listener ctrl to Process
	 * 
	 * @param EventListenerCtrlProcess
	 *            entity
	 * @return EventListenerCtrlProcess
	 */
	public EventListenerCtrlProcess eventListenerCtrlProcessUpdate(EventListenerCtrlProcess entity) {
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		return dao.save(entity);
	}

	/**
	 * delete event listener ctrl to Process
	 * 
	 * @param EventListenerCtrlProcess
	 *            entity
	 */
	public void eventListenerCtrlProcessDelete(EventListenerCtrlProcess entity) {
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		dao.delete(entity);
	}

	/**
	 * Updates records ctrls that have exceeded the maximum wait time
	 * 
	 * @param String
	 *            statusToLoad
	 * @param String
	 *            statusDestination
	 * @param Integer
	 *            timeOutMinutes
	 */
	public void eventListenerCtrlUpdateToStatusTimeOut(String statusToLoad, String statusDestination, Integer timeOutMinutes) {
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		dao.updateEventCtrlToStatusTimeOut(statusToLoad, statusDestination, timeOutMinutes);
	}
	
	/**
	 * update status event ctrl
	 * @param Long idEventCtrl
	 * @param Long idMassLoad
	 * @param String status 
	 * */
	public void updateEventCtrlStatus(Long idEventCtrl,Long idMassLoad,String status) {
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		dao.updateEventCtrlStatus(idEventCtrl, idMassLoad, status);
	}	
	
	/**
	 * Get number pages
	 * @param Integer maxRegisterByPage
	 * @param String idMassiveLoad
	 * @return
	 */
	public double eventListenerCtrlProcNumberPages(Integer maxRegisterByPage, String idMassiveLoad)  {
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		return dao.eventListenerCtrlProcNumberPages(maxRegisterByPage, idMassiveLoad);
	}
	
	/**
	 * delete All Associated by id Massive Load
	 * @param Long idMassLoad
	 * @return boolean
	 * */
	public boolean eventListenerCtrlDeleteAllWithIdMassiveLoad(Long idMassLoad) {
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		return dao.deleteAllWithIdMassiveLoad(idMassLoad);
	}
	
	
	/**
	 * get Event Listener Control by page
	 * @param filter
	 * @param Long fkMassiveLoad
	 * @return Collection<EventListenerCtrlProcess>
	 */
	public Collection<EventListenerCtrlProcess> getEventListenerCtrlByPage(FilterQueryDto filter,Long fkMassiveLoad)
	{
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		return dao.getEventListenerCtrlPage(filter,fkMassiveLoad);
	}
	
	
	/**
	 * update id job process
	 * @param Long idEventCtrl
	 * @param Long idJobProcess
	 * 
	 * */
	public void eventListenerCtrlUpdateIdJobProcess(Long idEventCtrl,Long idJobProcess) {
		EventListenerCtrlProcessDAO dao = new EventListenerCtrlProcessDAO();
		dao.updateEventCtrlIdJobProcess(idEventCtrl, idJobProcess);
	}

	// ----------------------------------------------
	// methods event listener document

	/**
	 * Get all Event Listener Control Document process by start date and status
	 * 
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, optional
	 * @param String typeModule
	 * @param String typeModuleNot
	 * @param Long @param Integer maxReg, maximum number of records to process, optional
	 * @return Collection<EventListenerDocProcess>
	 */
	public ArrayList<EventListenerDocProcess> eventsListenerDocProcGetAll(Long idEventCtrlProc, Date startDate, String statusToLoad,String typeModule,String typeModuleNot, Integer maxReg) {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		
		Collection<EventListenerDocProcess> li = dao.getAllEventListerDocProcess(idEventCtrlProc, startDate, statusToLoad,typeModule,typeModuleNot, maxReg);
		
		if(typeModule==null) {
			Collection<EventListenerDocProcess> liTemplate = dao.getAllEventListerDocTemplateProcess(idEventCtrlProc, startDate, statusToLoad, maxReg);
			li.addAll(liTemplate);
		}		
				
		ArrayList<EventListenerDocProcess> responseList = new ArrayList<EventListenerDocProcess>(li);		
		return responseList;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * Get all Attachments tp process by start date and status
	 * 
	 * @param Date
	 *            startDate, can by null
	 * @param String
	 *            statusToLoad, list of status to load, optional
	 * @param Integer
	 *            maxReg, maximum number of records to process, optional
	 * @return Collection<EventListenerDocProcess>
	 */
	public ArrayList<EventListenerDocProcess> getAllAttachmentProcess(Date startDate, String statusToLoad, Integer maxReg, FilterQueryDto filter) {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		Collection<EventListenerDocProcess> li = dao.getAllAttachmentProcess(startDate, statusToLoad, maxReg, filter);
		ArrayList<EventListenerDocProcess> responseList = new ArrayList<EventListenerDocProcess>(li);

		return responseList;
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
	public void eventListenerDocUpdatetToStatusTimeOut(String statusToLoad, String statusDestination, Integer timeOutMinutes) {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		dao.updateEventToStatusTimeOut(statusToLoad, statusDestination, timeOutMinutes);
	}

	/**
	 * get all event listener ctrl document general
	 * 
	 * @param Long idEventCtlHisto
	 * @param FilterQueryDto filter
	 * @return ArrayList<EventListenerDocHistory>
	 */
	public ArrayList<EventListenerDocHistory> eventsListenerDocGetAll(Long idEventCtlHisto,FilterQueryDto filter) {
		EventListenerDocHistoDAO dao = new EventListenerDocHistoDAO();
		Collection<EventListenerDocHistory> li = dao.getAllEventListerDocHisto(idEventCtlHisto,filter);
		ArrayList<EventListenerDocHistory> responseList = new ArrayList<EventListenerDocHistory>(li);

		return responseList;
	}

	/**
	 * Insert event listener ctrl document
	 * 
	 * @param EventListenerDocHistory
	 *            entity
	 * @return EventListenerDoc
	 */
	public EventListenerDocHistory eventListenerDocInsert(EventListenerDocHistory entity) {
		EventListenerDocHistoDAO dao = new EventListenerDocHistoDAO();
		return dao.saveNew(entity);
	}

	/**
	 * Insert event listener ctrl document to Process
	 * 
	 * @param EventListenerDocProcess
	 *            entity
	 * @return EventListenerDocProcess
	 */
	public EventListenerDocProcess eventListenerDocProcessInsert(EventListenerDocProcess entity) {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		return dao.saveNew(entity);
	}

	/**
	 * update event listener ctrl document
	 * 
	 * @param EventListenerDocHistory
	 *            entity
	 * @return EventListenerDoc
	 */
	public EventListenerDocHistory eventListenerDocUpdate(EventListenerDocHistory entity) {
		EventListenerDocHistoDAO dao = new EventListenerDocHistoDAO();
		return dao.save(entity);
	}

	/**
	 * get event listener ctrl document
	 * 
	 * @param Long
	 *            id
	 * @return EventListenerDocProcess
	 */
	public EventListenerDocProcess eventListenerDocProcessGetById(Long id) {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		return dao.getById(id);
	}

	/**
	 * Update event listener ctrl document observations to Process
	 * 
	 * @param EventListenerDocProcess
	 *            entity
	 */
	public void eventListenerDocProcessObsUpdate(EventListenerDocProcess entity) {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		dao.updateEventObservations(entity);
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
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		dao.updateFieldMappingToNull(idFieldMapping);
	}

	/**
	 * Update event listener ctrl document to Process
	 * 
	 * @param EventListenerDocProcess
	 *            entity
	 * @return EventListenerDocProcess
	 */
	public EventListenerDocProcess eventListenerDocProcessUpdate(EventListenerDocProcess entity) {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		return dao.save(entity);
	}

	/**
	 * delete event listener ctrl document to Process
	 * 
	 * @param EventListenerDocProcess
	 *            entity
	 */
	public void eventListenerDocProcessDelete(EventListenerDocProcess entity) {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		dao.delete(entity);
	}
	
	/**
	 * delete All Associated by id EventListenerProcess
	 * @param Long id
	 * @return boolean
	 * */
	public boolean eventListenerDocDeleteByIdEventCtrl(Long id)
	{
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		return dao.deleteAllAssoWithIdEventListener(id);
	}
	
	/**
	 * delete All Associated by id Massive Load
	 * @param Long idMassLoad
	 * @return boolean
	 * */
	public boolean eventListenerDocDeleteAllWithIdMassiveLoad(Long idMassLoad){
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		return dao.deleteAllWithIdMassiveLoad(idMassLoad);
	}
	
	/**
	 * delete All data in attachment without eventlistener
	 * @return boolean
	 * */
	public boolean deleteAllEmployeeSync() {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		return dao.deleteAllEmployeeSync();
	}	
	
	/**
	 * get status count 
	 * @param filter 
	 * @return List<String[]>
	 */
	public List<Object[]> eventListenerDocGetStatusCount(FilterQueryDto filter){
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		return dao.getStatusCount(filter);
	}	
	
	/**
	 * Get all Event Listener document Control
	 * @param Long idEventCtrlHistoProc
	 * @param FilterQueryDto filter
	 * @return Collection<EventListenerDocProcess>
	 * */
	public ArrayList<EventListenerDocProcess> eventListenerDocGetByFilter(Long idEventCtrl,FilterQueryDto filter) {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		Collection<EventListenerDocProcess> li = dao.getAllEventListerDocProcess(idEventCtrl,filter); 
		ArrayList<EventListenerDocProcess> responseList = new ArrayList<EventListenerDocProcess>(li);
		return responseList;
	}

	// -----------------------------------------
	// methods admin parameters.

	/**
	 * query adm parameter by id
	 * 
	 * @param Long id
	 * @return AdminParameters
	 */
	public AdminParameters admParameterById(Long id) {
		AdminParametersDAO dao = new AdminParametersDAO();
		return dao.getById(id);
	}

	/**
	 * get all parameters
	 * 
	 * @param Boolean isAttach / optional
	 * @return ArrayList<AdminParameters>
	 */
	public java.util.List<AdminParameters> admParameterGetAll() {
		AdminParametersDAO dao = new AdminParametersDAO();
		List<AdminParameters> r = dao.getAll();
		
		for (AdminParameters adminParameters : r) {
			adminParameters.setLastUpdateDate(null);
		}
		
		return r;
	}
	
	/**
	 * get all parameters admin
	 * @param Boolean isControlPanel
	 * @return Collection<AdminParameters>
	 * */
	public Collection<AdminParameters> getAllParametersAdmin(Boolean isControlPanel){
		AdminParametersDAO dao = new AdminParametersDAO();
		return dao.getAllParametersAdmin(isControlPanel);
	}

	/**
	 * Insert adm parameters
	 * 
	 * @param AdminParameters
	 *            entity
	 * @return AdminParameters insert
	 */
	public AdminParameters admParameterInsert(AdminParameters entity) {
		AdminParametersDAO dao = new AdminParametersDAO();
		entity = dao.saveNew(entity);
		return entity;
	}

	/**
	 * delete parameter adm
	 * 
	 * @param AdminParameters
	 *            id
	 */
	public boolean admParameterDelete(Long id) {
		AdminParametersDAO dao = new AdminParametersDAO();
		return dao.deleteEntityById(id);
	}
	
	/**
	 * delete all paramaters
	 */
	public boolean admParameterDeleteAll() {
		AdminParametersDAO dao = new AdminParametersDAO();
		return dao.deleteAllAdmParam();
	}

	/**
	 * update parameter adm
	 * 
	 * @param AdminParameters
	 *            entity
	 */
	public AdminParameters admParameterUpdate(AdminParameters entity) {
		AdminParametersDAO dao = new AdminParametersDAO();
		return dao.save(entity);
	}

	/**
	 * get all parameters
	 * 
	 * @param Boolean
	 *            isAttach / optional
	 * @return ArrayList<AdminParameters>
	 */
	public java.util.List<RolesMappingPpd> roleGetAll() {
		RolesMappingPpdDAO dao = new RolesMappingPpdDAO();
		return dao.getAll();
	}

	/**
	 * get all parameters
	 * 
	 * @param Boolean
	 *            isAttach / optional
	 * @return ArrayList<AdminParameters>
	 */
	public java.util.List<SFAdmin> roleGetUsers() {
		SFAdminDAO dao = new SFAdminDAO();
		return dao.getAll();
	}

	/**
	 * Insert adm parameters
	 * 
	 * @param AdminParameters
	 *            entity
	 * @return AdminParameters insert
	 */
	public RolesMappingPpd roleInsert(RolesMappingPpd entity) {
		RolesMappingPpdDAO dao = new RolesMappingPpdDAO();
		entity = dao.saveNew(entity);
		return entity;
	}

	/**
	 * delete parameter adm
	 * 
	 * @param AdminParameters
	 *            id
	 */
	public boolean roleDelete(Long id) {

		RolesMappingPpdDAO daobis = new RolesMappingPpdDAO();
		RolesMappingPpd rol = daobis.getById(id);

		RolesMappingPpdDAO dao = new RolesMappingPpdDAO();

		SFAdminDAO sfDAO = new SFAdminDAO();
		sfDAO.deleteRoleById(rol.getNameSf());

		return dao.deleteRoleById(id);
	}

	/**
	 * update parameter adm
	 * 
	 * @param AdminParameters
	 *            entity
	 */
	public RolesMappingPpd roleUpdate(RolesMappingPpd entity) {
		RolesMappingPpdDAO dao = new RolesMappingPpdDAO();
		return dao.save(entity);
	}

	/**
	 * get all sfentities
	 * 
	 * @param none
	 * @return ArrayList<SFEntity>
	 */
	public java.util.List<SfEntity> entityGetAll() {

		SfEntityDAO dao = new SfEntityDAO();
		return dao.getAll();
	}

	/**
	 * get one sfentity
	 * 
	 * @param none
	 * @return SFEntity
	 */

	public String entityGetOne(String id) {
		SfEntityDAO dao = new SfEntityDAO();
		SfEntity sf = dao.getByEntityName(id);

		String str = "[";
		
		if(sf!=null)
		{			
			List<String> strlistObj = new ArrayList<String>();
			
			for (SfProperty proper : sf.getProperties()) 
			{
	
				str += "{\"url\" : \"/" + id + "/" + proper.getName() + "\", \"label\" : \"" + proper.getName() + "\" },";
			}
	
			for (SfNavProperty properNav : sf.getNavproperties()) {
	
				strlistObj.add(properNav.getRelationship());
	
				str += "{\"url\" : \"" + id + "/" + properNav.getName() + "\", \"type\" : \"" + properNav.getType() + "\", \"label\" : \"" + properNav.getName() + "\", \"relation\" : "
						+ recursiveSearch(strlistObj, properNav.getType(), "/" + id + "/" + properNav.getName() + "/", 0, properNav.getRelationship()) + "  },";
	
				// myMap1.put(properNav.getName(), "relation");
				//
	
			}
	
			if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
				str = str.substring(0, str.length() - 1);
			}		
		}
		
		str += "]";
		return str;

	}

	public String recursiveSearch(List<String> strlistObj, String nodeid, String path, int level, String rel) {
		SfEntityDAO dao = new SfEntityDAO();
		SfEntity sf = dao.getByEntityName(nodeid);

		level++;
		

		String str = "[";

		for (SfProperty proper : sf.getProperties()) {

			str += "{\"url\" : \"" + path + proper.getName() + "\", \"label\" : \"" + proper.getName() + "\" },";
		}

		for (SfNavProperty properNav : sf.getNavproperties()) {

			if ( !properNav.getType().equals("WfRequest") && level < 4) {

				
				str += "{\"url\" : \"" + path + properNav.getName() + "\", \"type\" : \"" + properNav.getType() + "\", \"label\" : \"" + properNav.getName() + "\", \"relation\" : "
						+ recursiveSearch(strlistObj, properNav.getType(), path + properNav.getName() + "/", level, properNav.getRelationship()) + " },";

				strlistObj.add(rel);
			}

		}

		if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
			str = str.substring(0, str.length() - 1);
		}

		str += "]";

		return str;
	}

	public boolean arrayContains(List<String> array, Object value) {
		for (String string : array) {
			if (string.equals(value)) {
				return true;
			}

		}
		return false;
	}
	
	// -----------------------------------------
    // Methods Lookups.
		
	/**
	 * Insert lookup
	 * 
	 * @param Lookups entity
	 * @return Lookups insert
	 */
	public LookupTable lookupInsert(LookupTable entity) {
		LookupDAO dao = new LookupDAO();
		dao.saveNew(entity);
		return entity;
	}
	
	/**
	 * Get All Lookups
	 *
	 * @return List<Lookups>
	 */
	public java.util.List<LookupTable> lookupsGetAll() {
		LookupDAO dao = new LookupDAO();
		return dao.getAll();
	}
	
	/**
	 * Get All Lookups by code
	 * @param String code
	 * @return List<Lookups>
	 */
	public java.util.List<LookupTable> lookupsGetByCode(String code) {
		LookupDAO dao = new LookupDAO();
		Collection<LookupTable> lle = dao.getLookupByCode(code);
		ArrayList<LookupTable> response = new ArrayList<LookupTable>(lle);
		return response;
		
	}		
	
	/**
	 * Delete Lookup
	 * @param Long id
	 * 
	 * */
	public void lookupDelete(Long id) {
		LookupDAO dao = new LookupDAO();
		LookupDAO co = new LookupDAO();			
		dao.delete(co.getById(id));
		
	}
	
	/**
	 * Update Lookup
	 * @param Lookups entity
	 * 
	 * */
	public LookupTable lookupUpdate(LookupTable entity) {
		LookupDAO dao = new LookupDAO();
		LookupDAO co = new LookupDAO();
		return dao.save(entity);			
	}
	
	
	// -----------------------------------------
    // Methods Countries.
		
	/**
	 * Insert country
	 * 
	 * @param Countries entity
	 * @return Countries insert
	 */
	public Countries countryInsert(Countries entity) {
		CountriesDAO dao = new CountriesDAO();
		dao.saveNew(entity);
		return entity;
	}
	
	/**
	 * Get All Countries
	 *
	 * @return List<Countries>
	 */
	public java.util.List<Countries> countriesGetAll() {
		CountriesDAO dao = new CountriesDAO();
		return dao.getAll();
	}
	
	/**
	 * Get All Countries by code
	 * @param String code
	 * @return List<Countries>
	 */
	public java.util.List<Countries> countriesGetByCode(String code) {
		CountriesDAO dao = new CountriesDAO();
		Collection<Countries> lle = dao.getCountryByCode(code);
		ArrayList<Countries> response = new ArrayList<Countries>(lle);
		return response;
		
	}		
	
	/**
	 * Delete Country
	 * @param Long id
	 * 
	 * */
	public void countryDelete(Long id) {
		CountriesDAO dao = new CountriesDAO();
		CountriesDAO co = new CountriesDAO();			
		dao.delete(co.getById(id));
		
	}
	
	/**
	 * Update Country
	 * @param Countries entity
	 * 
	 * */
	public Countries countryUpdate(Countries entity) {
		CountriesDAO dao = new CountriesDAO();
		CountriesDAO co = new CountriesDAO();
		return dao.save(entity);			
	}
	// -----------------------------------------
    // Methods SIGN TEMPLATE LIB
	/**
	 * Get All SIGN TEMPLATE LIB
	 *
	 * @return List<SignsTemplateLibrary>
	 */
	public java.util.List<SignsTemplateLibrary> signTemplateLibGetAll() {
		SignsTemplateLibraryDAO dao = new SignsTemplateLibraryDAO();
		return dao.getAll();
	}
	/**
	 * Insert SIGN TEMPLATE LIB
	 * 
	 * @param SignsTemplateLibrary entity
	 * @return SignsTemplateLibrary insert
	 */
	public SignsTemplateLibrary signsTemplateLibraryInsert(SignsTemplateLibrary entity) {
		SignsTemplateLibraryDAO dao = new SignsTemplateLibraryDAO();
		return dao.saveNew(entity);
	}
	/**
	 * Delete SignsTemplateLibrary
	 * @param Long id
	 * */
	public void SignsTemplateLibraryDelete(Long id) {
		SignsTemplateLibraryDAO dao = new SignsTemplateLibraryDAO();
		SignsTemplateLibraryDAO co = new SignsTemplateLibraryDAO();			
		dao.delete(co.getById(id));
	}
	/**
	 * Update SignsTemplateLibrary
	 * @param Countries entity 
	 * */
	public SignsTemplateLibrary SignsTemplateLibraryUpdate(SignsTemplateLibrary entity) {
		SignsTemplateLibraryDAO dao = new SignsTemplateLibraryDAO();
		return dao.save(entity);			
	}
	
	//--------------------------------------
	//Methods Loggers
	
	/**
	 * removes all records with creation date less than the filter date
	 * @param Date filterDate
	 * @return boolean
	 * */
	public boolean loggerDeleteByDate(Date filterDate) {
		LoggerDAO dao = new LoggerDAO();
		return dao.deleteLoggerByDate(filterDate);
	}
	
	/**
	 * removes all records
	 * @return boolean
	 * */
	public boolean loggerDeleteAll(){
		LoggerDAO dao = new LoggerDAO();
		return dao.deleteAllLogger();
	}	
	
	/**
	 * removes all records with creation date less than the filter date
	 * @param Date filterDate
	 * @return boolean
	 * */
	public boolean deleteJobLogByDate(Date filterDate) {
		JobLogDAO dao = new JobLogDAO();
		return dao.deleteJobLogByDate(filterDate);
	} 	
	
	/**
	 * removes all records with the same id Job
	 * @param Date filterDate
	 * @return boolean
	 * */
	public boolean deleteJobLogByIdJob(Long idJob) {
		JobLogDAO dao = new JobLogDAO();
		return dao.deleteJobLogByIdJob(idJob);
	}	
	
	/**
	 * save Job Logger
	 * 
	 * @param Long idJob
	 * @param String code
	 * @param String user
	 * @return Long
	 */
	public Long jobLogSave(Long idJob,String code, String nameJob)
	{
		JobLogDAO list = new JobLogDAO();
		
		try 
		{
			JobsLog li = new JobsLog();
			li.setIdJob(idJob);
			li.setCode(code);			
			li.setNameJob(nameJob);
			list.saveNew(li);
			return li.getId();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	//--------------------------------------------
	/**
	 * delete all information from a table
	 * @param String nameTable
	 * @return boolean
	 * */
	public boolean controlPanelDeleteAllByTable(String nameTable){
		AdminParametersDAO dao = new AdminParametersDAO();
		return dao.deleteAllByTable(nameTable);
	}
	
	// -----------------------------------------
    // Methods Language.
		
	/**
	 * Insert Language
	 * 
	 * @param Language entity
	 * @return Language insert
	 */
	public Language languageInsert(Language entity) {
		LanguageDAO dao = new LanguageDAO();
		dao.saveNew(entity);
		return entity;
	}
	
	/**
	 * Get All Language
	 *
	 * @return List<Language>
	 */
	public java.util.List<Language> languageGetAll() {
		LanguageDAO dao = new LanguageDAO();
		return dao.getAll();
	}
	
	/**
	 * Get All Language by code
	 * @param String code
	 * @return List<Language>
	 */
	public java.util.List<Language> languageGetByCode(String code) {
		LanguageDAO dao = new LanguageDAO();
		Collection<Language> lle = dao.getLanguageByCode(code);
		ArrayList<Language> response = new ArrayList<Language>(lle);
		return response;
		
	}		
	
	/**
	 * Delete Language
	 * @param Long id
	 * 
	 * */
	public void languageDelete(Long id) {
		LanguageDAO dao = new LanguageDAO();
		LanguageDAO co = new LanguageDAO();			
		dao.delete(co.getById(id));
		
	}
	
	/**
	 * Update Language
	 * @param Language entity
	 * 
	 * */
	public Language languageUpdate(Language entity) {
		LanguageDAO dao = new LanguageDAO();
		LanguageDAO co = new LanguageDAO();
		return dao.save(entity);			
	}
	
	
	/**
	 * execute query in success Factor
	 * @param String query
	 * @return String response
	 * */
	public String executeQuerySF(String query)
	{
		try 
		{
			return HttpConnectorSuccessFactor.getInstance().executeGET(query);
		} catch (InvalidResponseException | IOException e) 
		{
			return "error: "+e.getMessage();			
		}
	}
	
	/**
	 * Inset Manager Role
	 * @param ManagerRole mr
	 * @return ManagerRole
	 */
	public ManagerRole insertManagerRole(ManagerRole mr) {
		try {
			ManagerRoleDAO mrDao = new ManagerRoleDAO();
			return mrDao.saveNew(mr);
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Get list all Manager Role
	 * @return List<ManagerRole>
	 */
	public List<ManagerRole> getAllManagerRole(){
		try {
			ManagerRoleDAO mrDao = new ManagerRoleDAO();
			List<ManagerRole> list = new ArrayList<>();
			list=mrDao.getAll();
			return list;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Delete Manager Role
	 * @param Long id
	 */
	public void deleteManagerRole (Long id) {
		
		try {
			ManagerRoleDAO mrDao = new ManagerRoleDAO();
			mrDao.delete(mrDao.getById(id));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Update Manager Role
	 * @param ManagerRole mr
	 * @return ManagerRole
	 */
	public ManagerRole updateManagerRole (ManagerRole mr) {
		try {
			ManagerRoleDAO mrDao = new ManagerRoleDAO();
			return mrDao.save(mr);
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get all user for Manager Role Group
	 * @param Long id
	 * @return List<ManagerRoleGroup>
	 */
	public List<ManagerRoleGroup> getAllManagerGroup(Long id){
		try {
			ManagerRoleGroupDAO mrDao = new ManagerRoleGroupDAO();
			return mrDao.getAllManagerGroup(id);
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Insert Manager Role Group   
	 * @param ManagerRoleGroup mr
	 * @return
	 */
	public ManagerRoleGroup insertManagerRoleGroup(ManagerRoleGroup mr) {
		try {
			ManagerRoleGroupDAO mrDao = new ManagerRoleGroupDAO();
			return mrDao.saveNew(mr);
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Delete Manager Role Group
	 * 
	 * @param Long id
	 */
	public void deleteManagerRoleGroup (Long id) {
		try {
			ManagerRoleGroupDAO mrDao = new ManagerRoleGroupDAO();
			ManagerRoleGroup mr = mrDao.getById(id);
			mrDao.delete(mr);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
}