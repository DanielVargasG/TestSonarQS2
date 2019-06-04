package edn.cloud.sfactor.business.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.GenErrorInfoDto;
import edn.cloud.business.dto.integration.SFAdmin;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Countries;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerDocHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerParam;
import edn.cloud.sfactor.persistence.entities.FieldsMappingMeta;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;
import edn.cloud.sfactor.persistence.entities.LookupTable;
import edn.cloud.sfactor.persistence.entities.ManagerRole;
import edn.cloud.sfactor.persistence.entities.ManagerRoleGroup;
import edn.cloud.sfactor.persistence.entities.Language;
import edn.cloud.sfactor.persistence.entities.RolesMappingPpd;
import edn.cloud.sfactor.persistence.entities.SfEntity;
import edn.cloud.sfactor.persistence.entities.SignsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.StructureBusiness;

public interface SuccessFactorAdmin {
	
	//-----------------------------------------
	//Methods admini paremeters
	
	/**
	 * get admin parameters by name code
	 * @param String nameCode
	 * @return AdminParameters
	 */
	public AdminParameters adminParamGetByNameCode(String nameCode);
	
	/**
	 * insert admin parameters
	 * @param AdminParameters param
	 * @return AdminParameters
	 */
	public AdminParameters adminParamInsert(AdminParameters param);
	
	/**
	 * update admin parameters
	 * @param AdminParameters param
	 * @return AdminParameters
	 */
	public AdminParameters adminParamUpdate(AdminParameters param);	
		
	/**
	 * delete all paramaters
	 */
	public boolean admParameterDeleteAll();	
	
	
	//-----------------------------------------
	//methods events
	
	/**
	 * get events by name
	 * @param String eventName
	 * @return EventListener
	 * */
	public EventListenerParam eventsGetByName(String eventName);
	
	/**
	 * get all eventListener
	 * @return ArrayList<EventListener>
	 * */
	public ArrayList<EventListenerParam> eventListenerGetAll();
	
	/**
	 * query event listener by id
	 * @param Long idEvent
	 * @return EventListener
	 * */
	public EventListenerParam eventListenerById(Long idEvent);

	
	/**
	 * Insert event listener
	 * @param EventListenerParam entity
	 * @return EventListener
	 * */
	public EventListenerParam eventListenerInsert(EventListenerParam entity);
	
	/**
	 * delete event listener ctrl 
	 * @param EventListenerCtrlProcess entity
	 */
	public void eventListenerCtrlDelete(EventListenerCtrlProcess entity);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Updates fielMapping to NULL in EventListenerDocProcess by idFieldMapping 
	 * @param Long idFieldMapping
	 * */
	public void eventListenerDocProUpdateFieldMapToNull(Long idFieldMapping);
	
	/**
	 * update status event ctrl
	 * @param Long idEventCtrl
	 * @param Long idMassLoad
	 * @param String status 
	 * */
	public void updateEventCtrlStatus(Long idEventCtrl,Long idMassLoad,String status);	
	
	/**
	 * update event listener
	 * @param EventListenerParam entity
	 * @return EventListener
	 * */
	public EventListenerParam eventListenerUpdate(EventListenerParam entity);	
	
	/**
	 * Delete event listener
	 * @param Long idEvent
	 * */
	public Boolean eventListenerDelete(Long idEvent);	
	
	
	
	/**
	 * Update event listener ctrl document to Process
	 * 
	 * @param EventListenerDocProcess entity
	 * @return EventListenerDocProcess
	 */
	public EventListenerDocProcess eventListenerDocProcessUpdate(EventListenerDocProcess entity);
	
	/**
	 * update id job process
	 * @param Long idEventCtrl
	 * @param Long idJobProcess
	 * 
	 * */
	public void eventListenerCtrlUpdateIdJobProcess(Long idEventCtrl,Long idJobProcess);	
	
	/**
	 * get counter by status in event listener history
	 * @param filter
	 * @param Long fkMassiveLoad 
	 * @return List<String[]>
	 * @return
	 */	
	public List<Object[]> eventListenerHistoGetStatusCount(FilterQueryDto filter,Long fkMassiveLoad);
	
	
	/**
	 * Get all Event Listener document Control
	 * @param Long idEventCtrlHistoProc
	 * @param FilterQueryDto filter
	 * @return Collection<EventListenerDocProcess>
	 * */
	public ArrayList<EventListenerDocProcess> eventListenerDocGetByFilter(Long idEventCtrl,FilterQueryDto filter);
	
	/**
	 * get counter by status in event listener
	 * 
	 * @param filter
	 * @param Long fkMassiveLoad 
	 * @return List<String[]>
	 * @return
	 */	
	public List<Object[]> eventListenerGetStatusCount(FilterQueryDto filter,Long fkMassiveLoad);
	
	//-----------------------------------------
	//methods mapping
	
	/**
	 * get all fields mapping ppd
	 * @param Boolean isAttach / optional
	 * @param String moduleAttach
	 * @param Boolean isActive
	 * @param Boolean loadNameMetadata
	 * @return ArrayList<FieldsMappingPpd>
	 * */
	public ArrayList<FieldsMappingPpd> mappingPpdFieldsGetAll(Boolean isAttach,String moduleAttach,Boolean isActive,Boolean loadNameMetadata);
	
	/**
	 * determines if the Field Mapping is used by another entity
	 * @param Long id Field Mapping	  
	 * @return GenErrorInfoDto
	 * */
	public GenErrorInfoDto mappingFieldIsUsedById(Long id);
	
	/**
	 * get all mapping fields by name
	 * @param String name
	 * @param Boolean loadNameMetadata
	 * @return ArrayList<FieldsMappingPpd>
	 * */
	public ArrayList<FieldsMappingPpd> mappingPpdFieldsByName(String name,Boolean loadNameMetadata);
	
	/**
	 * query field mapping ppd by if
	 * @param Long idField
	 * @param Boolean loadNameMetadata
	 * @return FieldsMappingPpd
	 * */
	public FieldsMappingPpd mappingPpdFieldById(Long idField,Boolean loadNameMetadata);

	
	/**
	 * Insert fields Mapping Ppd
	 * @param FieldsMappingPpd entity
	 * @return FieldsMappingPpd
	 * */
	public FieldsMappingPpd mappingPpdFieldsInsert(FieldsMappingPpd entity);
	
	/**
	 * delete field mapping ppd 
	 * @param FieldsMappingPpd variables
	 * @param Long idTemplate
	 * @return boolean
	 * */
	public boolean mappingPpdFieldDelete(Long id,Long idTemplate);
	
	/**
	 * update fields mapping ppd
	 * @param FieldsMappingPpd entity
	 * @return FieldsMappingPpd
	 * */
	public FieldsMappingPpd mappingPpdFieldUpdate(FieldsMappingPpd entity);
		
	/**
	 * delete all mapping ppd field metadata by id field mapping ppd 
	 * @param fieldMappingPpdId
	 * @param Long idTemplate
	 * */
	public void mappingPpdFieldMetaDelete(Long fieldMappingPpdId,Long idTemplate);
		
	/**
	 * Insert new mapping field metadata
	 * @param Long idFieldTemplate
	 * @param Long idTemplateLib
	 * @param Long idTemplate
	 * */
	public void mappingPpdFieldMetaInsert(Long idFieldTemplate,Long idTemplateLib,Long idTemplate);
	
	/**
	 * get FieldsMappingMeta by id Mapping Field ppd
	 * @param fieldMappingPpdId
	 * */
	public ArrayList<FieldsMappingMeta> mappingPpdFieldMetaGetByIdFieldMapp(Long fieldMappingPpdId);
	
	/**
	 * Get all field mapping by filters
	 * @param Long idFielMapping
	 * @param Long idFieldTemplateLib
	 * @return Collection<FieldsMappingMeta>
	 * */
	public Collection<FieldsMappingMeta> mappingPpdFieldMetaGetByRef(Long idFielMapping,Long idFieldTemplateLib);
	
	/**
	 * get mapping field by id
	 * @param Long idField
	 * @return FieldsMappingPpd
	 */
	public FieldsMappingPpd mappingPpdFieldGetId(Long idField);
	
	/**
	 * delete All Associated by id Massive Load
	 * @param Long idMassLoad
	 * @return boolean
	 * */
	public boolean eventListenerDocDeleteAllWithIdMassiveLoad(Long idMassLoad);	
	
	/**
	 * delete All Associated by id Massive Load
	 * @param Long idMassLoad
	 * @return boolean
	 * */
	public boolean eventListenerCtrlDeleteAllWithIdMassiveLoad(Long idMassLoad);
	
	/**
	 * get Metadata associated with Metadata
	 * @param FieldsMappingPpd fielMapping
	 * @return List<FieldsMappingMeta>
	 * */
	public List<FieldsMappingMeta> mappingPPdFieldsGetMetada(FieldsMappingPpd fielMapping);
	
	//-----------------------------------------
	//methods ScructureBussiness 
	
	/**
	 * get all Structure Business
	 * @return ArrayList<StructureBusiness>
	 * */
	public ArrayList<StructureBusiness> structureBusinessGetAll();
	
	/**
	 * query event Structure Business
	 * @param Long idEvent
	 * @return StructureBusiness
	 * */
	public StructureBusiness structureBusinessById(Long idStructure);

	
	/**
	 * Insert Structure business
	 * @param EventListenerParam entity
	 * @param Long idParent
	 * @return EventListener	  
	 * */
	public StructureBusiness structureBusinessInsert(StructureBusiness entity, Long parentId);
	
	/**
	 * delete Structure Business
	 * @param EventListenerParam variables
	 * @return boolean
	 * */
	public boolean structureBusinessDelete(Long id);
	
	/**
	 * update Structure Business
	 * @param StructureBusiness entity
	 * @return StructureBusiness
	 * */
	public StructureBusiness structureBusinessUpdate(Long id, StructureBusiness entity);		

	//------------------------------------------------------------------------------------------------------------

	
	/**
	 * get all event listener ctrl general
	 * @param FilterQueryDto filter
	 * @param Integer maxReg
	 * @return ArrayList<EventListenerCtrl>
	 * */	
	public ArrayList<EventListenerCtrlHistory> eventsListenerCtrlHistoGetAll(FilterQueryDto filter,Long refMassLoadId);	
		
	
	/**
	 * Insert event listener ctrl
	 * @param EventListenerCtrlHistory entity
	 * @return EventListenerCtrl
	 * */
	public EventListenerCtrlHistory eventListenerCtrlHistoInsert(EventListenerCtrlHistory entity);
	
	/**
	 * get event listener ctrl by id
	 * 
	 * @param Long idEventListenerCtrlHisto
	 * @return EventListenerCtrlHistory
	 */
	public EventListenerCtrlHistory eventListenerCtrlHistoById(Long id);	
	
	/**
	 * return count of history by id ctrl
	 * @param Long id
	 * */
	public long getEventListenerCountHistoByidCrtl(Long id);
	
	/**
	 * get all event listener ctrl document general
	 * @param Long idEventCtlHisto
	 * @param FilterQueryDto filter
	 * @return ArrayList<EventListenerDocHistory>
	 * */	
	public ArrayList<EventListenerDocHistory> eventsListenerDocHistoGetAll(Long idEventCtlHisto,FilterQueryDto filter);
	
	/**
	 * get event listener ctrl document by id
	 * 
	 * @param Long idEventCtlHisto
	 * @return EventListenerDocHistory
	 */
	public EventListenerDocHistory eventsListenerDocHistoGetById(Long idEventCtlHisto);	
		
	
	/**
	 * Insert event listener ctrl document
	 * @param EventListenerDocHistory entity
	 * @return EventListenerDoc
	 * */
	public EventListenerDocHistory eventListenerDocHistoInsert(EventListenerDocHistory entity);
	
	/**
	 * Updates records ctrls that have exceeded the maximum wait time
	 * @param String statusToLoad
	 * @param String statusDestination
	 * @param Integer timeOutMinutes
	 * */
	public void eventListenerCtrlUpdateToStatusTimeOut(String statusToLoad, String statusDestination,Integer timeOutMinutes);	
	
	/**
	 * delete all events or specific event ctrl histo
	 * @param EventListenerCtrlHistory entity is null delete all history
	 * */
	public void eventsListenerCtrlHistoDelete(EventListenerCtrlHistory entity);	
	
	
	//------------------------------------------------------------------------------
	/**
	 * Get number pages
	 * @param Integer maxRegisterByPage
	 * @param String idMassiveLoad
	 * @return
	 */
	public double eventListenerCtrlProcNumberPages(Integer maxRegisterByPage, String idMassiveLoad) ;
	
	/**
	 * get Event Listener Control by page
	 * @param filter
	 * @param Long fkMassiveLoad
	 * @return Collection<EventListenerCtrlProcess>
	 */
	public Collection<EventListenerCtrlProcess> getEventListenerCtrlByPage(FilterQueryDto filter,Long fkMassiveLoad);
	
	
	/**
	 * Insert event listener ctrl document to Process
	 * @param EventListenerDocProcess entity
	 * @return EventListenerDocProcess
	 * */
	public EventListenerDocProcess eventListenerDocProcessInsert(EventListenerDocProcess entity);
	
	/**
	 * Updates records that have exceeded the maximum wait time
	 * @param String statusToLoad
	 * @param String statusDestination
	 * @param Integer timeOutMinutes
	 * */
	public void eventListenerDocUpdatetToStatusTimeOut(String statusToLoad, String statusDestination,Integer timeOutMinutes);	
	
		
	/**
	 * delete event listener ctrl document to Process
	 * @param EventListenerDocProcess entity 
	 * */
	public void eventListenerDocProcessDelete(EventListenerDocProcess entity);	
	
	/**
	 * delete All Associated by id EventListenerProcess
	 * @param Long id
	 * @return boolean
	 * */
	public boolean eventListenerDocDeleteByIdEventCtrl(Long id);	
	
	/**
	 * Get Event Listener Control process by id 
	 * @param Long idCtrl
	 * @return EventListenerCtrlProcess
	 */
	public EventListenerCtrlProcess eventsListenerCtrlProcGetByid(Long idCtrl);	
	
	
	/**
	 * Update event listener ctrl document to Process
	 * @param EventListenerDocProcess entity
	 * @return EventListenerDocProcess
	 * */
	public void eventListenerDocProcessObsUpdate(EventListenerDocProcess entity);	
	
	
	/**
	 * get event listener ctrl document
	 * @param Long id
	 * @return EventListenerDocProcess
	 * */
	public EventListenerDocProcess eventListenerDocProcessGetById(Long id);	
	
	/**
	 * Get all Event Listener Control Document process by start date and status
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, optional
	 * @param Long idEventCtrlProc optional
	 * @param String typeModule
	 * @param String typeModuleNot
	 * @param Integer maxReg, maximum number of records to process, optional 
	 * @return Collection<EventListenerDocProcess>
	 * */
	public ArrayList<EventListenerDocProcess> eventsListenerDocProcGetAll(Long idEventCtrlProc,Date startDate,String statusToLoad,String typeModule,String typeModuleNot,Integer maxReg);
	
	
	/**
	 * Update event listener ctrl to Process
	 * @param EventListenerCtrlProcess entity
	 * @return EventListenerCtrlProcess
	 * */
	public EventListenerCtrlProcess eventListenerCtrlProcessUpdate(EventListenerCtrlProcess entity);
	
	/**
	 * Insert event listener ctrl to Process
	 * @param EventListenerCtrlProcess entity
	 * @return EventListenerCtrlProcess
	 * */
	public EventListenerCtrlProcess eventListenerCtrlProcessInsert(EventListenerCtrlProcess entity);	
	
	/**
	 * Get all Event Listener Control process by start date and status
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, can by null
	 * @param Integer maxReg maximum number of records to process, optional 
	 * @param Boolean isFutureDates
	 * @param FilterQueryDto filter
	 * @param Load fkMassiveLoad
	 * @param String statusMassiveToLoad
	 * @return Collection<EventListenerCtrlProcess>
	 * */	
	public ArrayList<EventListenerCtrlProcess> eventsListenerCtrlProcGetAll(Date startDate,String statusToLoad,Integer maxReg,Boolean isFutureDates, FilterQueryDto filter,Long fkMassiveLoad,String statusMassiveToLoad);
	
	
	/**
	 * Get all Attachments tp process by start date and status
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, optional
	 * @param Integer maxReg, maximum number of records to process, optional
	 * @param FilterQueryDto filters
	 * @return ArrayList<EventListenerDocProcess>
	 * */
	public ArrayList<EventListenerDocProcess> getAllAttachmentProcess(Date startDate,String statusToLoad,Integer maxReg, FilterQueryDto filters);
	
	
	/**
	 * get status count 
	 * @param filter 
	 * @return List<String[]>
	 */
	public List<Object[]> eventListenerDocGetStatusCount(FilterQueryDto filter);
	
	/**
	 * delete All data in attachment without eventlistener
	 * @return boolean
	 * */
	public boolean deleteAllEmployeeSync();	
	
	//-----------------------------------------
	//methods adm parameters
	
	/**
	 * query adm parameter by id	  
	 * @param Long id
	 * @return AdminParameters
	 */
	public AdminParameters admParameterById(Long id);
	
	/**
	 * get all parameters
	 * @param Boolean isAttach / optional
	 * @return ArrayList<AdminParameters>
	 */
	public java.util.List<AdminParameters> admParameterGetAll();
	
	/**
	 * get all parameters admin
	 * @param Boolean isControlPanel
	 * @return Collection<AdminParameters>
	 * */
	public Collection<AdminParameters> getAllParametersAdmin(Boolean isControlPanel);	
	
	/**
	 * Insert adm parameters
	 * 
	 * @param AdminParameters entity
	 * @return AdminParameters insert
	 */
	public AdminParameters admParameterInsert(AdminParameters entity);
	
	/**
	 * delete parameter adm	 
	 * @param AdminParameters id
	 */
	public boolean admParameterDelete(Long id);
	
	/**
	 * update parameter adm
	 * @param AdminParameters entity
	 */
	public AdminParameters admParameterUpdate(AdminParameters entity);
		
	
	/**
	 * get all parameters
	 * @param Boolean isAttach / optional
	 * @return ArrayList<AdminParameters>
	 */
	public List<RolesMappingPpd> roleGetAll();
	
	/**
	 * get all parameters
	 * @param Boolean isAttach / optional
	 * @return ArrayList<AdminParameters>
	 */
	public List<SFAdmin> roleGetUsers();
	
	/**
	 * Insert adm parameters
	 * 
	 * @param AdminParameters entity
	 * @return AdminParameters insert
	 */
	public RolesMappingPpd roleInsert(RolesMappingPpd entity);
	
	
	/**
	 * delete parameter adm	 
	 * @param AdminParameters id
	 */
	public boolean roleDelete(Long id);
	
	/**
	 * update parameter adm
	 * @param AdminParameters entity
	 */
	public RolesMappingPpd roleUpdate(RolesMappingPpd entity);
	
	
	public java.util.List<SfEntity> entityGetAll();
	
	public String entityGetOne(String id); 
	
	
	//-----------------------------------------
			//Methods Lookups
			
			/**
			 * Insert Lookup
			 * 
			 * @param Lookups entity
			 * @return Lookups insert
			 */
			public LookupTable lookupInsert(LookupTable entity);
				
			/**
			 * Get All Lookups
			 *
			 * @return List<Lookups>
			 */
			public java.util.List<LookupTable> lookupsGetAll();
			
			/**
			 * Delete Lookup
			 * @param Long id
			 * 
			 * */
			public void lookupDelete(Long id);
			
			/**
			 * Update Lookup
			 * @param Lookups entity
			 * @return 
			 * 
			 * */
			public LookupTable lookupUpdate(LookupTable entity);
			
			/**
			 * Get All Lookups by code
			 * @param String code
			 * @return List<Lookups>
			 */
			public java.util.List<LookupTable> lookupsGetByCode(String code);		
	
	
	//-----------------------------------------
		//Methods Countries
		
		/**
		 * Insert country
		 * 
		 * @param Countries entity
		 * @return Countries insert
		 */
		public Countries countryInsert(Countries entity);
			
		/**
		 * Get All Countries
		 *
		 * @return List<Countries>
		 */
		public java.util.List<Countries> countriesGetAll();
		
		/**
		 * Delete Country
		 * @param Long id
		 * 
		 * */
		public void countryDelete(Long id);
		
		/**
		 * Update Country
		 * @param Countries entity
		 * @return 
		 * 
		 * */
		public Countries countryUpdate(Countries entity);
		
		/**
		 * Get All Countries by code
		 * @param String code
		 * @return List<Countries>
		 */
		public java.util.List<Countries> countriesGetByCode(String code);		
		// -----------------------------------------
	    // Methods SIGN TEMPLATE LIB
		/**
		 * Get All SIGN TEMPLATE LIB
		 *
		 * @return List<SignsTemplateLibrary>
		 */
		public java.util.List<SignsTemplateLibrary> signTemplateLibGetAll();
		/**
		 * Insert SIGN TEMPLATE LIB
		 * 
		 * @param SignsTemplateLibrary entity
		 * @return SignsTemplateLibrary insert
		 */
		public SignsTemplateLibrary signsTemplateLibraryInsert(SignsTemplateLibrary entity);
		/**
		 * Delete SignsTemplateLibrary
		 * @param Long id
		 * */
		public void SignsTemplateLibraryDelete(Long id);
		/**
		 * Update SignsTemplateLibrary
		 * @param Countries entity 
		 * */
		public SignsTemplateLibrary SignsTemplateLibraryUpdate(SignsTemplateLibrary entity);
		
		//--------------------------------------
		//Methods Loggers
		
		/**
		 * removes all records with creation date less than the filter date
		 * @param Date filterDate
		 * @return boolean
		 * */
		public boolean loggerDeleteByDate(Date filterDate);
		
		/**
		 * removes all records
		 * @return boolean
		 * */
		public boolean loggerDeleteAll();		
		
		/**
		 * save Job Logger
		 * 
		 * @param Long idJob
		 * @param String code
		 * @param String user
		 * @return Long
		 */
		public Long jobLogSave(Long idJob,String code, String nameJob);
		
		/**
		 * removes all records with the same id Job
		 * @param Date filterDate
		 * @return boolean
		 * */
		public boolean deleteJobLogByIdJob(Long idJob);
		
		/**
		 * removes all records with creation date less than the filter date
		 * @param Date filterDate
		 * @return boolean
		 * */
		public boolean deleteJobLogByDate(Date filterDate);		
		
		/**
		 * delete all information from a table
		 * @param String nameTable
		 * @return boolean
		 * */
		public boolean controlPanelDeleteAllByTable(String nameTable);		
		
		// -----------------------------------------
	    // Methods Language.
			
		/**
		 * Insert Language
		 * 
		 * @param Language entity
		 * @return Language insert
		 */
		public Language languageInsert(Language entity);
		
		/**
		 * Get All Language
		 *
		 * @return List<Language>
		 */
		public java.util.List<Language> languageGetAll();
		
		/**
		 * Get All Language by code
		 * @param String code
		 * @return List<Language>
		 */
		public java.util.List<Language> languageGetByCode(String code) ;
		/**
		 * Delete Language
		 * @param Long id
		 * 
		 * */
		public void languageDelete(Long id);
		
		/**
		 * Update Language
		 * @param Language entity
		 * 
		 * */
		public Language languageUpdate(Language entity);
		
		//-----------------------------------------------------
		//Methods Control Panel.
		
		/**
		 * execute query in success Factor
		 * @param String query
		 * @return String response
		 * */
		public String executeQuerySF(String query);
		
		// Manager Role
		/**
		 * Inset Manager Role
		 * @param ManagerRole mr
		 * @return ManagerRole
		 */
		public ManagerRole insertManagerRole(ManagerRole mr);
		
		/**
		 * Get list all Manager Role
		 * @return List<ManagerRole>
		 */
		public List<ManagerRole> getAllManagerRole();
		
		/**
		 * Delete Manager Role
		 * @param Long id
		 */
		public void deleteManagerRole (Long id);
		
		/**
		 * Update Manager Role
		 * @param ManagerRole mr
		 * @return ManagerRole
		 */
		public ManagerRole updateManagerRole (ManagerRole mr);
		
		/**
		 * Get all user for Manager Role Group
		 * @param Long id
		 * @return List<ManagerRoleGroup>
		 */
		public List<ManagerRoleGroup> getAllManagerGroup(Long id);
		
		/**
		 * Insert Manager Role Group   
		 * @param ManagerRoleGroup mr
		 * @return
		 */
		public ManagerRoleGroup insertManagerRoleGroup(ManagerRoleGroup mr);
		
		/**
		 * Delete Manager Role Group
		 * 
		 * @param Long id
		 */
		public void deleteManagerRoleGroup (Long id);	
		
}