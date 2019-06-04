package edn.cloud.sfactor.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.integration.FolderDTO;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.sfactor.business.interfaces.SucessFactorTemplate;
import edn.cloud.sfactor.persistence.dao.AuthorizationDetailDAO;
import edn.cloud.sfactor.persistence.dao.AuthorizationDocumentDAO;
import edn.cloud.sfactor.persistence.dao.FieldsMappingMetaDAO;
import edn.cloud.sfactor.persistence.dao.FieldsTemplateDAO;
import edn.cloud.sfactor.persistence.dao.FieldsTemplateLibraryDAO;
import edn.cloud.sfactor.persistence.dao.FolderDAO;
import edn.cloud.sfactor.persistence.dao.TemplateDAO;
import edn.cloud.sfactor.persistence.dao.TemplateFiltersDAO;
import edn.cloud.sfactor.persistence.dao.TemplateGroupSignatureDAO;
import edn.cloud.sfactor.persistence.dao.TemplateSignatureDAO;
import edn.cloud.sfactor.persistence.entities.AuthorizationDetails;
import edn.cloud.sfactor.persistence.entities.AuthorizationDocument;
import edn.cloud.sfactor.persistence.entities.FieldsMappingMeta;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;
import edn.cloud.sfactor.persistence.entities.FieldsTemplate;
import edn.cloud.sfactor.persistence.entities.FieldsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.Folder;
import edn.cloud.sfactor.persistence.entities.Template;
import edn.cloud.sfactor.persistence.entities.TemplateFilters;
import edn.cloud.sfactor.persistence.entities.TemplateGroupSignature;
import edn.cloud.sfactor.persistence.entities.TemplateSignature;

public class SucessFactorTemplateImpl implements SucessFactorTemplate
{	
	private UtilLogger loggerSingle =  UtilLogger.getInstance();
		
	/**
	 * save template in succesfactor
	 * @param Template template
	 * @return id template
	 * */
	public Long saveTemplate(Template template)
	{
		TemplateDAO dao = new TemplateDAO();
		dao.saveNew(template);		
		return template.getId();
	}
	
	/**
	 * delete template in succesfactor
	 * @param Template template
	 * */
	public void deleteTemplate(Template template)
	{
		TemplateDAO dao = new TemplateDAO();
		dao.delete(template);		
		//return template.getId();
	}
	
	/**
	 * boolean template in succesfactor
	 * @param Template template
	 * @return id template
	 * */
	public Boolean updateTemplate(Template template)
	{
		TemplateDAO dao = new TemplateDAO();
		dao.save(template);		
		return true;
	}	
	
	/**
	 * update signature in succesfactor
	 * @param TemplateSignature signature
	 * @return boolean
	 * */
	public Boolean updateSignatureTemplate(TemplateSignature signature)
	{
		TemplateSignatureDAO dao = new TemplateSignatureDAO();
		dao.save(signature);		
		return true;
	}		
	
	/**
	 * Insert fields of template
	 * @param Template template
	 * @param PpdDocGenRequestPayloadDto variables
	 * */
	public boolean insertFieldsTemplate(Template template, ArrayList<FieldsTemplate> fields)
	{
		FieldsTemplateDAO daoTemplate = new FieldsTemplateDAO();
		
		for (FieldsTemplate field:fields)
		{
			field.setTemplateId(template);
			daoTemplate.saveNew(field);	
		}
		
		return true;
	}
			
	
	/**
	 * delete fields of template
	 * @param Template template
	 * @param PpdDocGenRequestPayloadDto variables
	 * */
	public boolean deleteFieldsTemplate(Template template)
	{
		FieldsTemplateDAO daoTemplate = new FieldsTemplateDAO();
		return daoTemplate.deleteFieldsByIdTemplate(template);
	}	
	
	/**
	 * update fields of template
	 * @param Template template
	 * @param PpdDocGenRequestPayloadDto variables
	 * */
	public boolean updateFieldsTemplate(ArrayList<FieldsTemplate> fields)
	{
		FieldsTemplateDAO daoTemplate = new FieldsTemplateDAO();
		
		for (FieldsTemplate field:fields)
		{
			daoTemplate.save(field);	
		}
		
		return true;
	}	
	
	/**
	 * return all list of field 
	 * @param Long idTemplate 
	 * @return GenerateVariables
	 **/
	public Collection<FieldsTemplate> getFieldsTemplateList(Long idTemplate)
	{
		FieldsTemplateDAO daoTemplate = new FieldsTemplateDAO();
		
		Template template = new Template();
		template.setId(idTemplate);
		
		Collection<FieldsTemplate> responseList = daoTemplate.getAllFields(template);
		loggerSingle.info("num fields template "+idTemplate+":  "+responseList.size());
		return responseList;
	}
	
	/**
	 * return all list of field 
	 * @param Long idTemplate 
	 * @return FieldTemplatesDto
	 **/
	public PpdDocGenRequestPayloadDto getFieldsTemplateListWithLib(Long idTemplate)
	{
		FieldsTemplateDAO daoTemplate = new FieldsTemplateDAO();
		FieldsTemplateLibraryDAO daoTemplateLib = new FieldsTemplateLibraryDAO();
		
		Template template = new Template();
		template.setId(idTemplate);
		
		Collection<FieldsTemplate> responseList = daoTemplate.getAllFields(template);
		Collection<FieldsTemplateLibrary> responseTempLib = daoTemplateLib.getAllFields();
		
		PpdDocGenRequestPayloadDto respose = UtilMapping.templateFielsEntityToDTO(responseList,responseTempLib);
		
		return respose;
	}
	
	/**
	 * return all list of templates
	 * */
	public Collection<Template> getListTemplates()
	{		
		TemplateDAO dao = new TemplateDAO();
		return dao.getAll(UtilCodesEnum.CODE_ACTIVE.getCode(),null);		 
	}
	
	/**
	 * @param String idUser
	 * return templates list filter by groups of user
	 * 
	 * @return List<TemplateInfoDto>
	 **/
	public Collection<Template> templateGetListByGroupUser(String idGroupsList, String selfGeneration){
		TemplateDAO dao = new TemplateDAO();
		return dao.getListByGroupUser(idGroupsList,selfGeneration);
	}
	
	/**
	 * @param String idGroupsList
	 * return templates list of subfolders filter by groups of user
	 * 
	 * @return List<TemplateInfoDto>
	 **/
	public Collection<Template> getLisSubtByGroupUser(String idGroupsList){
		TemplateDAO dao = new TemplateDAO();
		return dao.getLisSubtByGroupUser(idGroupsList);
	}	
	
	/**
	 * find template by id
	 * @param Long id
	 * */
	public Template getTemplateById(Long id)
	{
		TemplateDAO dao = new TemplateDAO();
		Template template = dao.getById(id,UtilCodesEnum.CODE_ACTIVE.getCode());
		return template;
	}
	
	/**
	 * find template by id
	 * @param Long id
	 * */
	public FieldsTemplate getFielTemplateById(Long id)
	{
		FieldsTemplateDAO dao = new FieldsTemplateDAO();
		FieldsTemplate fieldTemplate = dao.getById(id);
		return fieldTemplate;
	}	

	
	//Methods Signature
	//---------------------------------------------------------------
	//---------------------------------------------------------------
	
	/**
	 * get all templates by event listener specific
	 * @param Long idEventListener
	 * @return ArrayList<Template>
	 * */
	public ArrayList<Template> getListTemplatesByEventList(Long idEventListener)
	{
		TemplateDAO dao = new TemplateDAO();
		Collection<Template> listCollection = dao.getAllTemplateByEventList(idEventListener);
		return (new ArrayList<Template>(listCollection));
	}
	
	/**
	 * save sings of document 
	 * @param TemplateGroupSignature group
	 * @param InputStream inputStream 
	 * */
	public boolean saveSingsTemplateDocX(TemplateGroupSignature group,ArrayList<TemplateSignature> singList) 
	{
		try
		{
			TemplateSignatureDAO dao = new TemplateSignatureDAO();
			
			for (TemplateSignature item:singList)
			{
				//item.setSignatureGroupTemplateId(group);			
				dao.saveNew(item);			
			}
			
			return true;
		}
		catch (Exception e) {
			loggerSingle.error(e.getMessage());
		}
		
		return  false;
	}	
	
	/**
	 * update signature of template
	 * @param TemplateSignature variables
	 * */
	public boolean updateSignatureTemplate(ArrayList<TemplateSignature> signatureList)
	{
		TemplateSignatureDAO dao = new TemplateSignatureDAO();
		
		for (TemplateSignature item:signatureList)
		{
			dao.save(item);	
		}
		
		return true;
	}
	
	/**
	 * delete signature template
	 * @param Template template
	 * @param TemplateSignature signature
	 * */
	public boolean deleteSignatureTemplate(TemplateSignature signature)
	{
		TemplateSignatureDAO dao = new TemplateSignatureDAO();
		dao.delete(signature);
		return true;
	}
	
	/**
	 * return all list of Signature 
	 * @param Long idGroupSignatureTemplate 
	 * @return SignatureTemplate
	 **/
	public Collection<TemplateSignature> getSignatureTemplateList(Long idGroupSignatureTemplate)
	{
		TemplateSignatureDAO dao = new TemplateSignatureDAO();
		
		TemplateGroupSignature group = new TemplateGroupSignature();
		group.setId(idGroupSignatureTemplate);
		
		Collection<TemplateSignature> responseList = dao.getAllFields(group);
		loggerSingle.info("num signature group "+idGroupSignatureTemplate+":  "+responseList.size());
		return responseList;
	}		
	
	/**
	 * find signature+ by id
	 * @param Long idSignature
	 * */
	public TemplateSignature getSignatureById(Long idSignature)
	{
		TemplateSignatureDAO dao = new TemplateSignatureDAO();
		TemplateSignature sign = dao.getById(idSignature);
		return sign;
	}	
	
	
	//Methods Group Signature
	//---------------------------------------------------------------
	//---------------------------------------------------------------
	
	/**
	 * Save list Group Signature of template
	 * @param Template template
	 * @param PpdDocGenRequestPayloadDto variables
	 * */
	public boolean saveGroupSingTemplate(Template template, ArrayList<TemplateGroupSignature> groupList)
	{
		TemplateGroupSignatureDAO dao = new TemplateGroupSignatureDAO();
		
		for (TemplateGroupSignature item:groupList)
		{
			item.setTemplateId(template);
			dao.saveNew(item);	
		}
		
		return true;
	}
	
	/**
	 * Save Group Signature of template
	 * @param Template template
	 * @param PpdDocGenRequestPayloadDto variables
	 * */
	public TemplateGroupSignature saveGroupSingTemplate(Template template, TemplateGroupSignature group)
	{
		TemplateGroupSignatureDAO dao = new TemplateGroupSignatureDAO();
		group =  dao.saveNew(group);	
		return group; 
	}	
	
	/**
	 * return all list of Group Signature 
	 * @param Long idTemplate 
	 * @return GenerateVariables
	 **/
	public ArrayList<TemplateGroupSignature> getSignatureGroupTemplateList(Long idTemplate)
	{
		TemplateGroupSignatureDAO dao = new TemplateGroupSignatureDAO();
		
		Template template = new Template();
		template.setId(idTemplate);
		
		Collection<TemplateGroupSignature> signatureList = dao.getAllFields(template);
		ArrayList<TemplateGroupSignature> responseList = new ArrayList<TemplateGroupSignature>(signatureList);
		
		loggerSingle.info("num group signature template "+idTemplate+":  "+responseList.size());
		return responseList;
	}
	
	
	//Methods Template filters
	//---------------------------------------------------------------
	//---------------------------------------------------------------
	
	/**
	 * get template filter by id
	 * @param Long idTemplateFilter
	 * @return TemplateFilters
	 * */
	public TemplateFilters templateFiltersGetById(Long idTemplateFilter){
		TemplateFiltersDAO dao = new TemplateFiltersDAO();
		return dao.getById(idTemplateFilter);
	}
	
	/**
	 * get all templates filters by id template
	 * @param Template template
	 * @return ArrayList<TemplateFilters>
	 * */
	public ArrayList<TemplateFilters> templateFiltersGetAllById(Template template)
	{
		TemplateFiltersDAO dao = new TemplateFiltersDAO();
		Collection<TemplateFilters> listResponse = dao.getAllTemplateFiltersById(template);
		return (new ArrayList<TemplateFilters>(listResponse)); 
	}
	
	/**
	 * delete template filter by id template filters
	 * @param Long idTemplateFilters
	 * */
	public Boolean templateFiltersDelete(Long idTemplateFilters)
	{
		TemplateFiltersDAO dao = new TemplateFiltersDAO();
		dao.deleteTemplateFilterById(idTemplateFilters);
		return true;
	}
	
	/**
	 * insert template filters
	 * @param TemplateFilters entity
	 * @return TemplateFilters
	 * */
	public TemplateFilters templateFiltersInsert(TemplateFilters entity)
	{
		TemplateFiltersDAO dao = new TemplateFiltersDAO();
		return dao.saveNew(entity);
	}
	
	/**
	 * delete template filter by id template
	 * @param Long idTemplate
	 * @return boolean
	 * */
	public boolean deleteTemplateFilterByIdTemplate(Long idTemplate)
	{
		TemplateFiltersDAO dao = new TemplateFiltersDAO();
		return dao.deleteTemplateFilterByIdTemplate(idTemplate);
	}
	
	/**
	 * update template filters
	 * @param TemplateFilters entity
	 * @return TemplateFilters
	 * */
	public TemplateFilters templateFiltersUpdate(TemplateFilters entity)
	{
		TemplateFiltersDAO dao = new TemplateFiltersDAO();
		return dao.save(entity);
	}

	// -----------------------------------------
	// methods template fields library.

	/**
	 * query template field library by id
	 * 
	 * @param Long idField
	 * @return FieldsTemplateLibrary
	 */
	public FieldsTemplateLibrary templateFieldLibraryById(Long idField) {
		FieldsTemplateLibraryDAO dao = new FieldsTemplateLibraryDAO();
		return dao.getById(idField);
	}
	
	/**
	 * get all field template library by name
	 * @param Boolean isAttach / optional
	 * @return Collection<FieldsTemplateLibrary>
	 * */
	public ArrayList<FieldsTemplateLibrary> templateFieldLibraryByName(String name){
		FieldsTemplateLibraryDAO dao = new FieldsTemplateLibraryDAO();
		Collection<FieldsTemplateLibrary> collection = dao.getByName(name);
		ArrayList<FieldsTemplateLibrary> responseList = new ArrayList<FieldsTemplateLibrary>(collection);
		return responseList;
	} 

	/**
	 * get all template fields library
	 * @return ArrayList<FieldsTemplateLibrary>
	 */
	public ArrayList<FieldsTemplateLibrary> templateFieldLibraryGetAll() {
		FieldsTemplateLibraryDAO dao = new FieldsTemplateLibraryDAO();
		Collection<FieldsTemplateLibrary> collection = dao.getAllFields();
		ArrayList<FieldsTemplateLibrary> responseList = new ArrayList<FieldsTemplateLibrary>(collection);
		return responseList;
	}

	/**
	 * Insert template field library
	 * 
	 * @param FieldsMappingPpd entity
	 * @return FieldsMappingPpd entity with id
	 */
	public FieldsTemplateLibrary templateFieldLibraryInsert(FieldsTemplateLibrary entity) {
		FieldsTemplateLibraryDAO dao = new FieldsTemplateLibraryDAO();
		entity = dao.saveNew(entity);
		return entity;
	}

	/**
	 * delete template field library 
	 * @param Long id
	 */
	public boolean templateFieldLibraryDelete(Long id) {
		FieldsTemplateLibraryDAO dao = new FieldsTemplateLibraryDAO();
		return dao.deleteById(id);
	}

	/**
	 * update fields mapping ppd
	 * @param FieldsMappingPpd entity
	 */
	public FieldsTemplateLibrary templateFieldLibraryUpdate(FieldsTemplateLibrary entity) {
		FieldsTemplateLibraryDAO dao = new FieldsTemplateLibraryDAO();
		return dao.save(entity);
	}

	//--------------------------------------------
	//methods template folders
	
	/**
	 * get all entities by Parent
	 * @param Long idParent 
	 * @return Collection<StructureBusiness>
	 */	
	public ArrayList<FolderDTO> templateGetFoldersByParent(Long idParent)
	{
		ArrayList<FolderDTO> returnList = new ArrayList<>();
		FolderDAO dao = new FolderDAO();
		Collection<Folder> folderSub = dao.getByParent(idParent);
		
		for(Folder folder:folderSub){
			FolderDTO folderDto = new FolderDTO();
			folderDto.setId(folder.getId());
			folderDto.setTitle(folder.getTitle());
			returnList.add(folderDto);
		}
		
		return returnList;
	}	
	
	//------------------------------------------------
	//methods template folders
	
	//methods Metadata
	
	/**
	 * Insert metadata template
	 * @param Long idFieldTemplate
	 * @param Long idTemplateLib
	 * */
	public void templateMetadataInsert(Long idTemplate,Long idFieldLib)
	{	
		FieldsTemplateLibrary lib = new FieldsTemplateLibrary();
		lib.setId(idFieldLib);
				
		FieldsMappingMeta metadata = new FieldsMappingMeta();
		metadata.setTemplateId(idTemplate);
		metadata.setFieldsTemplateLibrary(lib);
		
		FieldsMappingMetaDAO metaDao = new FieldsMappingMetaDAO();
		metaDao.saveNew(metadata);
	}
	
	/**
	 * Delete metadata template
	 * @param Long idFieldTemplate
	 * @param Long idTemplateLib
	 * */
	public void templateMetadataDelete(Long idFieldTemplate,Long idTemplateLib) {
		FieldsMappingMetaDAO dao = new FieldsMappingMetaDAO();
		dao.deleteAllByFieldsMapping(idFieldTemplate,idTemplateLib);
	}
	
	/**
	 * Delete metadata template
	 * @param Long idFieldTemplate
	 * @param Long idTemplateLib
	 * */
	public void templateAllMetadataDelete(Long id) {
		FieldsMappingMetaDAO dao = new FieldsMappingMetaDAO();
		dao.deleteAllByTemplate(id);
	}
	
	/**
	 * Get list Metadata Template
	 * @param id
	 * @param Boolean loadNameMetadata
	 * @return
	 */
	public List<String> getMetadataTemplate(Long id,Boolean loadNameMetadata){ 
		List<String> metadata = new ArrayList<>();
		FieldsMappingMetaDAO dao = new FieldsMappingMetaDAO();
		FieldsTemplateLibraryDAO fieldDao = new FieldsTemplateLibraryDAO();
		Collection<FieldsMappingMeta>fields = dao.getAllByIdTemplate(id);
		FieldsTemplateLibrary ftl = new FieldsTemplateLibrary();
		
		if(fields!=null && !fields.isEmpty()) {
			for (FieldsMappingMeta item : fields) 
			{
				if(loadNameMetadata)
					metadata.add(item.getFieldsTemplateLibrary().getNameSource().toString());
				else
					metadata.add(item.getFieldsTemplateLibrary().getId().toString());
			}
		}
		
		return metadata;
	}
}