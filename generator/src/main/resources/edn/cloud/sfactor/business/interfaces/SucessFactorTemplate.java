package edn.cloud.sfactor.business.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edn.cloud.business.dto.integration.FolderDTO;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.sfactor.persistence.dao.AuthorizationDetailDAO;
import edn.cloud.sfactor.persistence.entities.AuthorizationDetails;
import edn.cloud.sfactor.persistence.entities.AuthorizationDocument;
import edn.cloud.sfactor.persistence.entities.FieldsTemplate;
import edn.cloud.sfactor.persistence.entities.FieldsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.TemplateGroupSignature;
import edn.cloud.sfactor.persistence.entities.TemplateSignature;
import edn.cloud.sfactor.persistence.entities.Template;
import edn.cloud.sfactor.persistence.entities.TemplateFilters;

public interface SucessFactorTemplate 
{	
	/**
	 * save template in succesfactor
	 * @param Template template
	 * @return id template
	 * */
	public Long saveTemplate(Template template);
	
	
	/**
	 * delete template in succesfactor
	 * @param Template template
	 * */
	public void deleteTemplate(Template template);
	
	/**
	 * Insert fields of template
	 * @param Template template
	 * @param GenerateVariables variables
	 * */
	public boolean insertFieldsTemplate(Template template, ArrayList<FieldsTemplate> fields);
	
	/**
	 * update fields of template
	 * @param Template template
	 * @param GenerateVariables variables
	 * */
	public boolean updateFieldsTemplate(ArrayList<FieldsTemplate> fields);
		
	/**
	 * return all list of templates
	 * */
	public Collection<Template> getListTemplates();
	
	/**
	 * @param String idGroupsList
	 * @param String selfGeneration
	 * return templates list filter by groups of user
	 * 
	 * @return List<Template>
	 **/
	public Collection<Template> templateGetListByGroupUser(String idGroupsList, String selfGeneration);
	
	/**
	 * find template by id
	 * @param Long id
	 * */
	public Template getTemplateById(Long id);	
	
	/**
	 * boolean template in succesfactor
	 * @param Template template
	 * @return id template
	 * */
	public Boolean updateTemplate(Template template);	
	
	/**
	 * return all list of field 
	 * @param Long idTemplate 
	 * @return GenerateVariables
	 **/
	public  Collection<FieldsTemplate> getFieldsTemplateList(Long idTemplate);
	
	
	/**
	 * find template by id
	 * @param Long id
	 * */
	public FieldsTemplate getFielTemplateById(Long id);
	
	/**
	 * Insert fields of template
	 * @param Template template
	 * @param GenerateVariables variables
	 * */
	public boolean deleteFieldsTemplate(Template template);	
	
	/**
	 * save sings of document 
	 * @param TemplateGroupSignature group
	 * @param InputStream inputStream 
	 * */
	public boolean saveSingsTemplateDocX(TemplateGroupSignature group,ArrayList<TemplateSignature> singList) ;
	
	/**
	 * update signature of template
	 * @param TemplateSignature variables
	 * */
	public boolean updateSignatureTemplate(ArrayList<TemplateSignature> signatureList);
	
	/**
	 * delete signature template
	 * @param Template template
	 * @param TemplateSignature signature
	 * */
	public boolean deleteSignatureTemplate(TemplateSignature signature);
	
	/**
	 * return all list of Signature 
	 * @param Long idGroupSignatureTemplate 
	 * @return SignatureTemplate
	 **/
	public Collection<TemplateSignature> getSignatureTemplateList(Long idGroupSignatureTemplate);	
	
	/**
	 * return all list of Group Signature 
	 * @param Long idTemplate 
	 * @return GenerateVariables
	 **/
	public ArrayList<TemplateGroupSignature> getSignatureGroupTemplateList(Long idTemplate);	
	
	/**
	 * Save Group Signature of template
	 * @param Template template
	 * @param GenerateVariables variables
	 * */
	public boolean saveGroupSingTemplate(Template template, ArrayList<TemplateGroupSignature> groupList);
	
	/**
	 * Save Group Signature of template
	 * @param Template template
	 * @param GenerateVariables variables
	 * */
	public TemplateGroupSignature saveGroupSingTemplate(Template template, TemplateGroupSignature group);	
	
	/**
	 * update signature in succesfactor
	 * @param TemplateSignature signature
	 * @return boolean
	 * */
	public Boolean updateSignatureTemplate(TemplateSignature signature);	
	
	/**
	 * find signature+ by id
	 * @param Long idSignature
	 * */
	public TemplateSignature getSignatureById(Long idSignature);
	
	
	/**
	 * get all templates by event listener specific
	 * @param Long idEventListener
	 * @return ArrayList<Template>
	 * */
	public ArrayList<Template> getListTemplatesByEventList(Long idEventListener);
	
	/**
	 * update template filters
	 * @param TemplateFilters entity
	 * @return TemplateFilters
	 * */
	public TemplateFilters templateFiltersUpdate(TemplateFilters entity);
	
	/**
	 * insert template filters
	 * @param TemplateFilters entity
	 * @return TemplateFilters
	 * */
	public TemplateFilters templateFiltersInsert(TemplateFilters entity);
	
	/**
	 * delete template filter by id template filters
	 * @param Long idTemplateFilters
	 * @return Boolean
	 * */
	public Boolean templateFiltersDelete(Long idTemplateFilters);
	
	/**
	 * get all templates filters by id template
	 * @param Template template
	 * @return Collection<TemplateFilters>
	 * */
	public ArrayList<TemplateFilters> templateFiltersGetAllById(Template template);

	/**
	 * get templates filters by id 
	 * @param Long idTemplate
	 * @return TemplateFilters
	 * */
	public TemplateFilters templateFiltersGetById(Long idTemplate);
	
	/**
	 * delete template filter by id template
	 * @param Long idTemplate
	 * @return boolean
	 * */
	public boolean deleteTemplateFilterByIdTemplate(Long idTemplate);	
	
	/**
	 * query template field library by id
	 * 
	 * @param Long idField
	 * @return FieldsTemplateLibrary
	 */
	public FieldsTemplateLibrary templateFieldLibraryById(Long idField);
	/**
	 * get all template fields library
	 * @return ArrayList<FieldsTemplateLibrary>
	 */
	public ArrayList<FieldsTemplateLibrary> templateFieldLibraryGetAll();
	/**
	 * Insert template field library
	 * 
	 * @param FieldsMappingPpd entity
	 * @return FieldsMappingPpd entity with id
	 */
	public FieldsTemplateLibrary templateFieldLibraryInsert(FieldsTemplateLibrary entity);

	/**
	 * delete template field library 
	 * @param Long id
	 */
	public boolean templateFieldLibraryDelete(Long id);
	
	/**
	 * update fields mapping ppd
	 * @param FieldsMappingPpd entity
	 */
	public FieldsTemplateLibrary templateFieldLibraryUpdate(FieldsTemplateLibrary entity);	
	
	/**
	 * return all list of field 
	 * @param Long idTemplate 
	 * @return Java Web Tomcat 8 Server
	 **/
	public PpdDocGenRequestPayloadDto getFieldsTemplateListWithLib(Long idTemplate);
	
	/**
	 * get all entities by Parent
	 * @param Long idParent 
	 * @return Collection<StructureBusiness>
	 */	
	public ArrayList<FolderDTO> templateGetFoldersByParent(Long idParent);
	
	/**
	 * @param String idGroupsList
	 * return templates list of subfolders filter by groups of user
	 * 
	 * @return List<TemplateInfoDto>
	 **/
	public Collection<Template> getLisSubtByGroupUser(String idGroupsList);	
	
	/**
	 * get all field template library by name
	 * @param Boolean isAttach / optional
	 * @return ArrayList<FieldsTemplateLibrary>
	 * */
	public ArrayList<FieldsTemplateLibrary> templateFieldLibraryByName(String name);
	
	/**
	 * Insert metadata template
	 * @param Long idFieldTemplate
	 * @param Long idTemplateLib
	 * */
	public void templateMetadataInsert(Long idTemplate,Long idFieldLib); 
	
	/**
	 * Delete metadata template
	 * @param Long idFieldTemplate
	 * @param Long idTemplateLib
	 * */
	public void templateMetadataDelete(Long metadata,Long idTemplateLib);
	
	/**
	 * Delete all metadata template
	 * @param Long idFieldTemplate
	 * @param Long idTemplateLib
	 * */
	public void templateAllMetadataDelete(Long id);
	
	/**
	 * Get list Metadata Template
	 * @param id
	 * @param Boolean loadNameMetadata
	 * @return
	 */
	public List<String> getMetadataTemplate(Long id,Boolean loadNameMetadata);
}
