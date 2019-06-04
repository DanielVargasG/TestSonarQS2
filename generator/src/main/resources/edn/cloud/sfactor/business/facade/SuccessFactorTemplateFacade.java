package edn.cloud.sfactor.business.facade;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.SignatureFieldDto;
import edn.cloud.business.dto.SignatureGroupDto;
import edn.cloud.business.dto.integration.FolderDTO;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.SFGroup;
import edn.cloud.business.dto.integration.SFGroupList;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.odata.UserManager;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.sfactor.business.impl.SuccessFactorImpl;
import edn.cloud.sfactor.business.impl.SucessFactorTemplateImpl;
import edn.cloud.sfactor.business.interfaces.SuccessFactor;
import edn.cloud.sfactor.business.interfaces.SucessFactorTemplate;
import edn.cloud.sfactor.business.utils.QueryBuilder;
import edn.cloud.sfactor.persistence.dao.FolderDAO;
import edn.cloud.sfactor.persistence.dao.FolderTemplateDAO;
import edn.cloud.sfactor.persistence.dao.FolderUserDAO;
import edn.cloud.sfactor.persistence.dao.TemplateDAO;
import edn.cloud.sfactor.persistence.entities.FieldsTemplate;
import edn.cloud.sfactor.persistence.entities.FieldsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.Folder;
import edn.cloud.sfactor.persistence.entities.FolderTemplate;
import edn.cloud.sfactor.persistence.entities.FolderUser;
import edn.cloud.sfactor.persistence.entities.SignsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.Template;
import edn.cloud.sfactor.persistence.entities.TemplateFilters;
import edn.cloud.sfactor.persistence.entities.TemplateGroupSignature;
import edn.cloud.sfactor.persistence.entities.TemplateSignature;

public class SuccessFactorTemplateFacade {
	private SucessFactorTemplate successFactorTemplateImpl = new SucessFactorTemplateImpl();
	private SuccessFactor successFactor = new SuccessFactorImpl();

	// Methods Template
	// ---------------------------------------------------------------
	// ---------------------------------------------------------------

	/**
	 * save template in succesfactor
	 * 
	 * @param Template
	 *            template
	 * @return id template
	 */
	public Long templateSave(TemplateInfoDto dto) {
		Template template = UtilMapping.templateToEntity(dto);
		return successFactorTemplateImpl.saveTemplate(template);
	}

	public Boolean templateUpdate(TemplateInfoDto dto) {
		Template template = UtilMapping.templateToEntity(dto);
		return successFactorTemplateImpl.updateTemplate(template);
	}

	/**
	 * delete template in succesfactor
	 * 
	 * @param TemplateInfoDto
	 *            template
	 */
	public void deleteTemplate(TemplateInfoDto template) {
		Template templateEnty = successFactorTemplateImpl.getTemplateById(template.getIdTemplate());
		templateEnty.setStatus(UtilCodesEnum.CODE_INACTIVE.getCode());
		successFactorTemplateImpl.updateTemplate(templateEnty);
	}

	/**
	 * update version of template
	 * 
	 * @param Long
	 *            id
	 * @param String
	 *            version
	 */
	public boolean templateUpdateVersion(Long id, String version) {
		Template template = successFactorTemplateImpl.getTemplateById(id);
		template.setLatesVersion(Integer.valueOf(version));
		return successFactorTemplateImpl.updateTemplate(template);
	}

	/**
	 * Delete fields joined to template
	 * 
	 * @param Template
	 *            template
	 * @param PpdDocGenRequestPayloadDto
	 *            variables
	 */
	public boolean templateDeleteFields(Long idTemplate) {
		Template template = successFactorTemplateImpl.getTemplateById(idTemplate);
		return successFactorTemplateImpl.deleteFieldsTemplate(template);
	}

	/**
	 * 
	 * @param Template
	 *            template
	 * @param PpdDocGenRequestPayloadDto
	 *            variables
	 */
	public boolean templateSaveFields(Long idTemplate, ArrayList<FieldsTemplate> fields) {
		Template template = new Template();
		template.setId(idTemplate);
		return successFactorTemplateImpl.insertFieldsTemplate(template, fields);
	}

	/**
	 * save sings of template
	 * 
	 * @param Template
	 *            template
	 * @param ArrayList<SlugItem>
	 */
	public boolean templateSaveSings(Long idTemplate, ArrayList<SlugItem> slugItemsList) {
		ArrayList<TemplateSignature> signatureTemplateListInsert = new ArrayList<TemplateSignature>();

		// -----------------------------------------
		// capture group signature
		Collection<TemplateGroupSignature> groupsList = successFactorTemplateImpl.getSignatureGroupTemplateList(idTemplate);
		TemplateGroupSignature group = new TemplateGroupSignature();

		if (groupsList != null && groupsList.size() > 0) {
			group = groupsList.iterator().next();
		} else {
			Template template = new Template();
			template.setId(idTemplate);
			group.setTemplateId(template);
			group.setNameGroup(UtilCodesEnum.CODE_DEFAULT.getCode());

			group = successFactorTemplateImpl.saveGroupSingTemplate(template, group);

			ArrayList<TemplateGroupSignature> groupList = new ArrayList<>();
			groupList.add(group);
		}

		// -----------------------------------------
		// loop signature to add, insert or update
		ArrayList<TemplateSignature> listSignature = new ArrayList<TemplateSignature>(successFactorTemplateImpl.getSignatureTemplateList(group.getId()));

		int order = 1;
		int countSign = 0;

		// delete signatures not found
		if (listSignature != null && listSignature.size() > 0) {
			for (TemplateSignature oldSignature : listSignature) {
				countSign = 0;
				for (SlugItem newItem : slugItemsList) {
					if (oldSignature.getNameSource() != null && oldSignature.getNameSource().equals(newItem.getSlug())) {
						countSign++;
					}
				}

				if (countSign == 0) {

					successFactorTemplateImpl.deleteSignatureTemplate(oldSignature);
				}
			}
		}

		// load last signature
		listSignature = new ArrayList<TemplateSignature>(successFactorTemplateImpl.getSignatureTemplateList(group.getId()));

		// Insert new signature
		countSign = 0;
		for (SlugItem newItem : slugItemsList) {
			countSign = 0;
			if (listSignature != null && listSignature.size() > 0) {
				for (TemplateSignature oldSignature : listSignature) {
					// update
					if (oldSignature.getNameSource() != null && oldSignature.getNameSource().equals(newItem.getSlug())) {
						countSign++;

						// repeated
						if (countSign > 1) {
							successFactorTemplateImpl.deleteSignatureTemplate(oldSignature);
						}
					}
				}
			}

			// Insert
			if (countSign == 0) {
				TemplateSignature newSignature = new TemplateSignature();
				newSignature.setNameSource(newItem.getSlug());
				newSignature.setGroup(group);
				newSignature.setOrder(order);

				if (newItem.getValue() != null && !newItem.getValue().equals(""))
					newSignature.setNameDestination(newItem.getValue().toString());

				if (newItem.getFlag() != null)
					newSignature.setIsConstants(newItem.getFlag());
				else
					newSignature.setIsConstants(Boolean.FALSE);

				// id of template library
				if (newItem.getId() != null) {
					try {
						Long idSignTempLib = new Long(newItem.getId());
						newSignature.setSignsTemplateLibrary(new SignsTemplateLibrary(idSignTempLib));
						newSignature.setIsConstants(Boolean.FALSE);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				signatureTemplateListInsert.add(newSignature);
			}

			order = order + 1;
		}

		successFactorTemplateImpl.saveSingsTemplateDocX(group, signatureTemplateListInsert);

		return true;
	}

	/**
	 * 
	 * @param String
	 *            idUser
	 * @param String
	 *            selfGeneration return templates list filter by groups of user
	 * 
	 * @return List<TemplateInfoDto>
	 **/
	public List<TemplateInfoDto> templateGetListByGroupUser(String idUser, String selfGeneration) {
		ArrayList<TemplateInfoDto> responseDto = new ArrayList<>();
		SFGroupList listGroups = this.userGroupList(idUser);

		String idGroupsList = "";
		if (listGroups != null && listGroups.getD().length > 0) {
			for (SFGroup string : listGroups.getD()) {
				idGroupsList += "'" + string.getGroupName() + "',";
			}

			idGroupsList = idGroupsList.substring(0, idGroupsList.length() - 1);
			Collection<Template> listTemplate = successFactorTemplateImpl.templateGetListByGroupUser(idGroupsList, selfGeneration);

			// templates first level
			for (Template entity : listTemplate) {
				responseDto.add(UtilMapping.templateToDto(entity));
			}

			// templates second level
			listTemplate = successFactorTemplateImpl.getLisSubtByGroupUser(idGroupsList);
			for (Template entity : listTemplate) {
				responseDto.add(UtilMapping.templateToDto(entity));
			}

			for (int i = 0; i < responseDto.size(); i++) {
				for (int j = 1 + i; j < responseDto.size(); j++)
					if (responseDto.get(j).getId().equals(responseDto.get(i).getId())) {
						responseDto.remove(responseDto.get(j));
					}
			}
		}

		return responseDto;
	}

	/**
	 * @param Boolean
	 *            loadDetail
	 * @parma Boolean loadNameMetadata
	 * @return List<TemplateInfoDto>
	 **/
	public List<TemplateInfoDto> templateGetList(Boolean loadDetail, Boolean loadNameMetadata) {
		Collection<Template> listTemplate = successFactorTemplateImpl.getListTemplates();

		ArrayList<TemplateInfoDto> responseDto = new ArrayList<>();
		for (Template entity : listTemplate) {
			TemplateInfoDto template = UtilMapping.templateToDto(entity);

			if (loadDetail) {
				template.setMetadataList(this.getMetadataTemplate(entity.getId(), loadNameMetadata));
				template.setSingatureGroup(this.templateGetSignatureGroupList(Long.valueOf(entity.getId())));
				template.setFilters(this.templateFiltersGetAllById(Long.valueOf(entity.getId())));
			}

			responseDto.add(template);

		}

		return responseDto;
	}

	/**
	 * get all templates by event listener specific
	 * 
	 * @param Long
	 *            idEventListener
	 * @return ArrayList<Template>
	 */
	public ArrayList<Template> templateListByEventList(Long idEventListener) {
		return successFactorTemplateImpl.getListTemplatesByEventList(idEventListener);
	}

	/**
	 * find template by id
	 * 
	 * @param Long
	 *            id
	 * @return TemplateInfoDto
	 */
	public TemplateInfoDto templateGetById(Long id) {
		Template response = successFactorTemplateImpl.getTemplateById(id);
		return UtilMapping.templateToDto(response);
	}

	public List<Template> templateGetByNoFolder() {

		TemplateDAO dao = new TemplateDAO();
		Collection<Template> templateList = dao.getByNull();

		return Collections.list(Collections.enumeration(templateList));
	}

	// Methods Template signature
	// ---------------------------------------------------------------
	// ---------------------------------------------------------------

	/**
	 * update Signature
	 * 
	 * @Long idFieldTemplate
	 * @SlugItem sglugItem
	 */
	public SignatureFieldDto templateSignatureUpdate(Long idSignatureTemplate, SignatureFieldDto signature) {
		TemplateSignature sign = successFactorTemplateImpl.getSignatureById(idSignatureTemplate);
		sign.setNameDestination(signature.getPath());
		sign.setOrder(signature.getOrder());
		sign.setIsConstants(signature.getFlag());

		if (signature.getIdSignTempLib() == -1 || signature.getIdSignTempLib() == -2 || signature.getIdSignTempLib() == -3) {
			sign.setSignsTemplateLibrary(null);
		} else {
			sign.setSignsTemplateLibrary(new SignsTemplateLibrary(signature.getIdSignTempLib()));
		}

		if (successFactorTemplateImpl.updateSignatureTemplate(sign)) {
			signature.setIdTemplate(sign.getGroup().getTemplateId().getId());
			return signature;
		}

		return null;
	}

	// Methods Template Fields
	// ---------------------------------------------------------------
	// ---------------------------------------------------------------

	/**
	 * return all list of field
	 * 
	 * @param Long
	 *            idTemplate
	 * @return FieldTemplatesDto
	 **/
	public PpdDocGenRequestPayloadDto templateGetFieldsList(Long idTemplate) {
		return successFactorTemplateImpl.getFieldsTemplateListWithLib(idTemplate);
	}

	/**
	 * return all list of SignatureGroup
	 * 
	 * @param Long
	 *            idTemplate
	 * @return SignatureGroupDto
	 **/
	public ArrayList<SignatureGroupDto> templateGetSignatureGroupList(Long idTemplate) {
		ArrayList<TemplateGroupSignature> response = successFactorTemplateImpl.getSignatureGroupTemplateList(idTemplate);

		// set list signatures
		if (response != null && response.size() > 0) {
			ArrayList<TemplateSignature> list = new ArrayList<TemplateSignature>(successFactorTemplateImpl.getSignatureTemplateList(response.get(0).getId()));
			return UtilMapping.signatureGroupEntityToDTO(response.get(0), list);
		}

		return new ArrayList<SignatureGroupDto>();
	}

	/**
	 * update fields path
	 * 
	 * @Long idFieldTemplate
	 * @SlugItem sglugItem
	 */
	public SlugItem templateUpdateFields(Long idFieldTemplate, SlugItem sglugItem) {
		FieldsTemplate field = successFactorTemplateImpl.getFielTemplateById(idFieldTemplate);

		ArrayList<FieldsTemplate> fields = new ArrayList<FieldsTemplate>();
		field.setNameDestination(sglugItem.getPath());
		fields.add(field);

		if (successFactorTemplateImpl.updateFieldsTemplate(fields)) {
			return sglugItem;
		}

		return null;
	}

	/**
	 * fill values with successfactor information for field templates
	 * 
	 * @param PpdDocGenRequestPayloadDto
	 *            genVar
	 * @param String
	 *            userTarget
	 * @param Boolean
	 *            isShowError, if true load error info
	 * @param String
	 *            effectiveDate
	 * @param Boolean
	 *            isView
	 */
	public PpdDocGenRequestPayloadDto templateGetValueQueryBuilder(PpdDocGenRequestPayloadDto genVar, String userTarget, Boolean isShowError, String effectiveDate, Boolean isView) 
	{
		PpdDocGenRequestPayloadDto genVarFinal = new PpdDocGenRequestPayloadDto();
		Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();
		for (SlugItem item : genVar.getVariables()) 
		{
			if (item.getPath() != null && !item.getPath().equals("") && item.getFlag() != null && item.getFlag())// is constants
			{
				String nameKey = item.getSlug();
				if (item.getSlug().toString().contains(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()) && item.getSlug().toString().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()).length > 1)
					nameKey = item.getSlug().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[0];

				SlugItem itemFinal = new SlugItem(nameKey, item.getPath());
				itemFinal.setId(item.getId());
				itemFinal.setPath(item.getPath());
				itemFinal.setFlag(Boolean.TRUE);// indicate that it is constant
				itemFinal.setIdAux(item.getIdAux());// id of document id field
				itemFinal.setLabel(item.getLabel());
				itemFinal.setConvertValueTo(item.getConvertValueTo());
				genVarFinal.addVariable(itemFinal);
			}
			// Constant variables
			else if (item.getSlug() != null && (item.getSlug().equals(UtilCodesEnum.CODE_EFFECTIVE_DATE.getCode()) || item.getSlug().equals(UtilCodesEnum.CODE_TODAY_DATE.getCode()))) {
				Date date = null;
				if (item.getSlug().equals(UtilCodesEnum.CODE_EFFECTIVE_DATE.getCode()))
					date = UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(), effectiveDate);
				else
					date = Calendar.getInstance().getTime();

				SlugItem itemFinal = new SlugItem();
				if (item.getPath() != null && item.getPath().equals(UtilCodesEnum.CODE_EU_FORMAT.getCode()))
					itemFinal = new SlugItem(item.getSlug(), UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR_F1.getCode(), date));
				else
					itemFinal = new SlugItem(item.getSlug(), UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR_F2.getCode(), date));

				itemFinal.setId(item.getId());
				itemFinal.setFlag(Boolean.TRUE);
				itemFinal.setPath("");
				itemFinal.setConvertValueTo(item.getConvertValueTo());
				genVarFinal.addVariable(itemFinal);
			} else if (item.getPath() != null && !item.getPath().equals("")) 
			{	
				map.put(item.getSlug(), new ResultBuilderDto(item.getPath(), "default", ""));
			}
			else if (isShowError)// show error
			{
				SlugItem itemFinal = new SlugItem(item.getSlug(), "there is no path");
				itemFinal.setId(item.getId());// id variable from template
				itemFinal.setLabel(item.getLabel());// id variable from document (edit)
				itemFinal.setFlag(Boolean.FALSE);
				itemFinal.setPath("");
				itemFinal.setConvertValueTo(item.getConvertValueTo());
				genVarFinal.addVariable(itemFinal);
			}
		}

		// data builder successFactorTemplateImpl
		if (map.size() > 0) {
			Map<String, ResultBuilderDto> dataMap = QueryBuilder.getInstance().convert(map, userTarget, effectiveDate != null ? effectiveDate : "");

			if (dataMap != null && dataMap.size() > 0) {
				for (SlugItem item : genVar.getVariables()) {
					if (item.getPath() != null && !item.getPath().equals("") && dataMap.get(item.getSlug()) != null) 
					{
						ResultBuilderDto resultBuilder = (ResultBuilderDto) dataMap.get(item.getSlug());

						if (item.getFlagAux() != null && item.getFlagAux()) {// is table
							String nameTable[] = item.getSlug().split(UtilCodesEnum.CODE_SIGN_PRINCIPAL_CHAR.getCode() + ".");

							if (nameTable != null && nameTable.length > 0)
								item.setSlug(nameTable[0].toString());

							UtilMapping utilMapping = new UtilMapping();
							ArrayList<ArrayList<SlugItem>> tableArray = utilMapping.getArrayFromJsonTableTemplatePdd(resultBuilder.getResult());
							if (tableArray != null && tableArray.size() > 0)
								item.setValue(tableArray);
							else
								item.setValue("");
						} else {
							item.setValue(resultBuilder.getResult());
						}

						// -------------------------------------
						if (resultBuilder.getResult() != null && !resultBuilder.getResult().toString().equals(UtilCodesEnum.CODE_QUERYBUILDER_INVALID.getCode())) {
							String nameKey = item.getSlug();
							if (item.getSlug().toString().contains(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()) && item.getSlug().toString().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()).length > 1)
								nameKey = item.getSlug().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode())[1];

							SlugItem itemFinal = new SlugItem(nameKey, (isView ? item.getValue().toString() : item.getValue()));
							itemFinal.setId(item.getId());
							itemFinal.setFlag(Boolean.FALSE);
							itemFinal.setPath(item.getPath());
							itemFinal.setConvertValueTo(item.getConvertValueTo());
							genVarFinal.addVariable(itemFinal);
						} else if (isShowError)// show error
						{
							SlugItem itemFinal = new SlugItem(item.getSlug(), "value not found");
							itemFinal.setId(item.getId());
							itemFinal.setFlag(Boolean.FALSE);
							itemFinal.setPath(item.getPath());
							itemFinal.setConvertValueTo(item.getConvertValueTo());
							genVarFinal.addVariable(itemFinal);
						}
					}
				}
			}
		}

		return genVarFinal;
	}

	/**
	 * update template filters
	 * 
	 * @param TemplateFilters
	 *            entity
	 * @return TemplateFilters
	 */
	public TemplateFilters templateFiltersUpdate(TemplateFilters entity) {
		return successFactorTemplateImpl.templateFiltersUpdate(entity);
	}

	/**
	 * insert template filters
	 * 
	 * @param TemplateFilters
	 *            entity
	 * @return TemplateFilters
	 */
	public TemplateFilters templateFiltersInsert(TemplateFilters entity) {
		return successFactorTemplateImpl.templateFiltersInsert(entity);
	}

	/**
	 * delete template filter by id template
	 * 
	 * @param Long
	 *            idTemplate
	 * @return boolean
	 */
	public boolean deleteTemplateFilterByIdTemplate(Long idTemplate) {
		return successFactorTemplateImpl.deleteTemplateFilterByIdTemplate(idTemplate);
	}

	/**
	 * delete template filter by id template filters
	 * 
	 * @param Long
	 *            idTemplateFilters
	 * @return Boolean
	 */
	public Boolean templateFiltersDelete(Long idTemplateFilters) {
		return successFactorTemplateImpl.templateFiltersDelete(idTemplateFilters);
	}

	/**
	 * get all templates filters by id template
	 * 
	 * @param Long
	 *            idTemplate
	 * @return ArrayList<TemplateFilters>
	 */
	public ArrayList<TemplateFilters> templateFiltersGetAllById(Long idTemplate) {
		Template template = new Template();
		template.setId(idTemplate);
		return successFactorTemplateImpl.templateFiltersGetAllById(template);
	}

	/**
	 * get templates filters by id
	 * 
	 * @param Long
	 *            idTemplate
	 * @return TemplateFilters
	 */
	public TemplateFilters templateFiltersGetById(Long idTemplate) {
		return successFactorTemplateImpl.templateFiltersGetById(idTemplate);
	}

	/**
	 * validate templates filter on user
	 * 
	 * @param Template
	 *            template
	 * @param String
	 *            userTarget
	 * @param String
	 *            effectiveDate
	 * @return GenErrorInfoDto
	 */
	public GenResponseInfoDto templateFilterValidateOnUser(Long idTemplate, String userTarget, String effectiveDate) {
		GenResponseInfoDto result = new GenResponseInfoDto();

		ArrayList<TemplateFilters> filterList = this.templateFiltersGetAllById(idTemplate);

		if (filterList != null && filterList.size() > 0) {
			PpdDocGenRequestPayloadDto fieldDto = new PpdDocGenRequestPayloadDto();
			for (TemplateFilters filter : filterList) {
				SlugItem slugItem = new SlugItem();
				slugItem.setSlug(filter.getId() + "");
				slugItem.setPath(filter.getPath());
				fieldDto.addVariable(slugItem);
			}

			PpdDocGenRequestPayloadDto response = this.templateGetValueQueryBuilder(fieldDto, userTarget, true, effectiveDate, true);

			if (response != null) {
				for (TemplateFilters filter : filterList) {
					for (SlugItem item : response.getVariables()) {
						// match filter
						if ((filter.getId() + "").equals(item.getSlug())) {
							if (item.getValue() == null || (item.getValue() != null && item.getValue().equals(""))) {
								result.setCode(UtilCodesEnum.CODE_ERROR.getCode());
								result.setMessage("does not meet the filter: " + filter.getValue() + ", successFactor response : " + item.getValue() + ", template id: " + idTemplate + ", userTarget: " + userTarget);
								return result;
							} else if (filter.getValue().contains("!")) {
								if (item.getValue() != null && (filter.getValue().replaceAll("!", "")).contains(item.getValue().toString())) {
									result.setCode(UtilCodesEnum.CODE_ERROR.getCode());
									result.setMessage("does not meet the filter: " + filter.getValue() + ", successFactor response : " + item.getValue() + ", template id: " + idTemplate + ", userTarget: " + userTarget);
									return result;
								}
							} else if (item.getValue() != null && !filter.getValue().contains(item.getValue().toString())) {
								result.setCode(UtilCodesEnum.CODE_ERROR.getCode());
								result.setMessage("does not meet the filter: " + filter.getValue() + ", successFactor response : " + item.getValue() + ", template id: " + idTemplate + ", userTarget: " + userTarget);
								return result;
							}
						}
					}
				}

				result.setCode(UtilCodesEnum.CODE_OK.getCode());
				return result;
			} else {
				result.setCode(UtilCodesEnum.CODE_ERROR.getCode());
				result.setMessage("there is no information for the filters");
				return result;
			}
		} else {
			result.setCode(UtilCodesEnum.CODE_OK.getCode());
			return result;
		}

	}

	// Methods template fields library
	// ----------------------------------------------------------------
	// ----------------------------------------------------------------

	/**
	 * query template field library by id
	 * 
	 * @param Long
	 *            idField
	 * @return FieldsTemplateLibrary
	 */
	public FieldsTemplateLibrary templateFieldLibraryById(Long idField) {
		return successFactorTemplateImpl.templateFieldLibraryById(idField);
	}

	/**
	 * get all field template library by name
	 * 
	 * @param Boolean
	 *            isAttach / optional
	 * @return ArrayList<FieldsTemplateLibrary>
	 */
	public ArrayList<FieldsTemplateLibrary> templateFieldLibraryByName(String name) {
		return successFactorTemplateImpl.templateFieldLibraryByName(name);
	}

	/**
	 * get all template fields library
	 * 
	 * @return ArrayList<FieldsTemplateLibrary>
	 */
	public ArrayList<FieldsTemplateLibrary> templateFieldLibraryGetAll() {
		return successFactorTemplateImpl.templateFieldLibraryGetAll();
	}

	/**
	 * Insert template field library
	 * 
	 * @param FieldsMappingPpd
	 *            entity
	 * @return FieldsMappingPpd entity with id
	 */
	public FieldsTemplateLibrary templateFieldLibraryInsert(FieldsTemplateLibrary entity) {
		return successFactorTemplateImpl.templateFieldLibraryInsert(entity);
	}

	/**
	 * delete template field library
	 * 
	 * @param Long
	 *            id
	 */
	public boolean templateFieldLibraryDelete(Long id) {
		return successFactorTemplateImpl.templateFieldLibraryDelete(id);
	}

	/**
	 * update fields mapping ppd
	 * 
	 * @param FieldsMappingPpd
	 *            entity
	 */
	public FieldsTemplateLibrary templateFieldLibraryUpdate(FieldsTemplateLibrary entity) {
		return successFactorTemplateImpl.templateFieldLibraryUpdate(entity);
	}

	public void updateInsertFieldsTemplateLibrary(List<FieldsTemplateLibrary> listNew) {
		
		List<FieldsTemplateLibrary> listTmfl= this.templateFieldLibraryGetAll();
		
		for(int i=0; i<listNew.size(); i++) 
		{
			listNew.get(i).setId(null);
			for (FieldsTemplateLibrary fieldsTemplateLibrary : listTmfl) 
			{
				if(fieldsTemplateLibrary.getNameSource()!=null && listNew.get(i).getNameSource()!=null && 
						fieldsTemplateLibrary.getNameSource().equals(listNew.get(i).getNameSource())) 
				{
					listNew.get(i).setId(fieldsTemplateLibrary.getId());
					if (listNew.get(i).getIsTableValue()) {

						fieldsTemplateLibrary.setNameDestination(listNew.get(i).getNameDestinationExt());
					} else {

						fieldsTemplateLibrary.setNameDestination(listNew.get(i).getNameDestination());
					}
					
					this.templateFieldLibraryUpdate(fieldsTemplateLibrary);
				}
			}
			
			if(listNew.get(i).getId()==null && listNew.get(i).getNameSource()!=null ) {
				this.templateFieldLibraryInsert(listNew.get(i));
			}
		}
	}

	// ---------------------------------------------------------
	// ---------------------------------------------------------
	/**
	 * get all entities by Parent
	 * 
	 * @param Long
	 *            idParent
	 * @return Collection<StructureBusiness>
	 */
	public ArrayList<FolderDTO> templateGetFoldersByParent(Long idParent) {
		return successFactorTemplateImpl.templateGetFoldersByParent(idParent);
	}

	/**
	 * get User From SuccessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public SFGroupList userGroupList(String groupName) {
		return successFactor.getGroupList(groupName);
	}

	// -----------------------------------------------------------
	// -----------------------------------------------------------

	/**
	 * Get Structure Folders
	 * 
	 * @param String
	 *            userId
	 * @return List<FolderDTO>
	 */
	public List<FolderDTO> templateFoldersListAll(String userId) {
		FolderUserDAO fudao = new FolderUserDAO();
		FolderDAO fdao = new FolderDAO();

		List<FolderDTO> flist = new ArrayList<>();
		Collection<FolderUser> fu = fudao.getAllFolderByUserId(userId, null);

		for (FolderUser folderUser : fu) {
			Folder f = fdao.getById(folderUser.getFolderId());

			FolderDTO folder = new FolderDTO();
			folder.setId(folderUser.getFolderId());
			folder.setTitle(f.getTitle());
			folder.setLevelFolder(1);

			// -------------------------------
			// searchs sub folders 2 level
			folder.setNodesFolders(this.templateGetFoldersByParent(folderUser.getFolderId()));
			for (FolderDTO folderSubDto : folder.getNodesFolders()) {
				folderSubDto.setLevelFolder(2);
				flist.add(folderSubDto);
			}

			flist.add(folder);
		}

		FolderDTO folderNone = new FolderDTO();
		folderNone.setTitle("None");
		folderNone.catSeeEdit = false;
		folderNone.catSeeEnter = false;
		folderNone.catSeeNothing = true;
		flist.add(folderNone);

		return flist;

	}

	/**
	 * Get Structure Folders
	 * 
	 * @param String
	 *            userId
	 * @param FilterQueryDto
	 *            filter
	 * @return List<FolderDTO>
	 */
	public List<FolderDTO> templateFoldersStructure(String userId, FilterQueryDto filter) {
		FolderUserDAO fudao = new FolderUserDAO();
		FolderTemplateDAO ftdao = new FolderTemplateDAO();
		FolderDAO fdao = new FolderDAO();

		List<FolderDTO> flist = new ArrayList<>();
		Collection<FolderUser> fu = fudao.getAllFolderByUserId(userId, filter);

		for (FolderUser folderUser : fu) {
			Folder f = fdao.getById(folderUser.getFolderId());
			List<TemplateInfoDto> doc = new ArrayList<TemplateInfoDto>();

			FolderDTO folder = new FolderDTO();
			folder.setId(folderUser.getFolderId());
			folder.setTitle(f.getTitle());
			folder.setLevelFolder(1);

			// search templates
			Collection<FolderTemplate> colF = ftdao.getByFolderId(folderUser.getFolderId());
			for (FolderTemplate folderTemplate : colF) {
				TemplateInfoDto docU = this.templateGetById(folderTemplate.getTemplateId());
				docU.setGenerateVariables(null);
				if (filter != null) {
					if (!filter.getItem().get("idEvent").equals(null) && Long.parseLong(filter.getItem().get("idEvent")) == docU.getIdEventListener().longValue()) {
						doc.add(docU);
					}
					if (filter.getItem().get("idEvent").equals("-1") && !filter.getItem().get("idFolder").equals(null)) {
						doc.add(docU);
					}
				} else {
					doc.add(docU);
				}
			}

			folder.setNodes(doc);

			// -------------------------------
			// searchs sub folders 2 level
			folder.setNodesFolders(this.templateGetFoldersByParent(folderUser.getFolderId()));
			for (FolderDTO folderSubDto : folder.getNodesFolders()) {
				folderSubDto.setLevelFolder(2);
				List<TemplateInfoDto> docSub = new ArrayList<TemplateInfoDto>();
				Collection<FolderTemplate> colSubF = ftdao.getByFolderId(folderSubDto.getId());

				for (FolderTemplate folderTemplate : colSubF) {
					TemplateInfoDto docU = this.templateGetById(folderTemplate.getTemplateId());
					docU.setGenerateVariables(null);
					docSub.add(docU);
				}

				folderSubDto.setNodes(docSub);
			}

			flist.add(folder);
		}

		FolderDTO folderNone = new FolderDTO();
		folderNone.setTitle("None");
		folderNone.catSeeEdit = false;
		folderNone.catSeeEnter = false;
		folderNone.catSeeNothing = true;
		flist.add(folderNone);

		List<TemplateInfoDto> doc = new ArrayList<TemplateInfoDto>();
		List<Template> docU = this.templateGetByNoFolder();

		for (Template template : docU) {
			TemplateInfoDto docUB = new TemplateInfoDto();
			docUB.setTitle(template.getTitle());
			docUB.setIdTemplate(template.getId());
			docUB.setGenerateVariables(null);
			doc.add(docUB);
		}

		folderNone.setNodes(doc);

		return flist;
	}

	/**
	 * Get folder list for export
	 * 
	 * @return
	 */
	public List<FolderDTO> getFolderList() {
		String userId = UserManager.getUserId();
		List<FolderDTO> flist = this.templateFoldersStructure(userId, null);
		List<FolderDTO> listResult = new ArrayList<>();
		FolderUserDAO fudao = new FolderUserDAO();

		for (FolderDTO fld : flist) {
			UtilLogger.getInstance().info("------------" + fld.getId());
			if (fld.getId() != null) {
				fld.setUsers(fudao.getAllUsersByFolderId(fld.getId()));
				listResult.add(fld);
			}

		}

		return UtilMapping.removeItemFolder(listResult);
	}

	/**
	 * Delete metadata template
	 * 
	 * @param Long
	 *            idFieldTemplate
	 * @param Long
	 *            idTemplateLib
	 */
	public void templateMetadataDelete(Long idFieldTemplate, Long idTemplateLib) {
		successFactorTemplateImpl.templateMetadataDelete(idFieldTemplate, idTemplateLib);
	}

	/**
	 * Delete all metadata template
	 * 
	 * @param Long
	 *            idFieldTemplate
	 * @param Long
	 *            idTemplateLib
	 */
	public void templateAllMetadataDelete(Long id) {
		successFactorTemplateImpl.templateAllMetadataDelete(id);
	}

	/**
	 * Get list Metadata Template
	 * 
	 * @param id
	 * @param Boolean
	 *            loadNameMetadata
	 * @return
	 */
	public List<String> getMetadataTemplate(Long id, Boolean loadNameMetadata) {
		return successFactorTemplateImpl.getMetadataTemplate(id, loadNameMetadata);
	}

	/**
	 * Insert metadata template
	 * 
	 * @param Long
	 *            idFieldTemplate
	 * @param Long
	 *            idTemplateLib
	 */
	public void templateMetadataInsert(Long idTemplate, Long idFieldLib) {
		successFactorTemplateImpl.templateMetadataInsert(idTemplate, idFieldLib);
	}
}