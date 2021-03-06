package edn.cloud.business.api.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import edn.cloud.business.api.util.UtilJson.JsonLoadingJob;
import edn.cloud.business.dto.GenErrorInfoDto;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.SFUserListDto;
import edn.cloud.business.dto.SFUserPhotoDto;
import edn.cloud.business.dto.SignatureFieldDto;
import edn.cloud.business.dto.SignatureGroupDto;
import edn.cloud.business.dto.integration.DocInfoDto;
import edn.cloud.business.dto.integration.DocTemplateInfo;
import edn.cloud.business.dto.integration.FolderDTO;
import edn.cloud.business.dto.integration.SFAdmin;
import edn.cloud.business.dto.integration.SFAdminList;
import edn.cloud.business.dto.integration.SFAdminListU;
import edn.cloud.business.dto.integration.SFDocumentType;
import edn.cloud.business.dto.integration.SFGroup;
import edn.cloud.business.dto.integration.SFGroupList;
import edn.cloud.business.dto.integration.SFPhotoDto;
import edn.cloud.business.dto.integration.SFRecList;
import edn.cloud.business.dto.integration.SFRecUnique;
import edn.cloud.business.dto.integration.SFRoleList;
import edn.cloud.business.dto.integration.SFUserDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.integration.UserInfo;
import edn.cloud.business.dto.integration.attach.SFAttachResponseN1Dto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeOrgInfoDto;
import edn.cloud.business.dto.ppd.api.PpdDocGenFullWithErrors;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.business.dto.ppd.api.PpdElectronicVaultOptionsDto;
import edn.cloud.business.dto.ppd.api.PpdFilterCoreEmployeeDto;
import edn.cloud.business.dto.ppd.api.PpdGeneratePdfSignField;
import edn.cloud.business.dto.ppd.api.PpdSigningDataDto;
import edn.cloud.business.dto.ppd.api.PpdTextOccurrencesDto;
import edn.cloud.business.dto.ppd.api.PpdUserDto;
import edn.cloud.business.dto.psf.PSFMsgDto;
import edn.cloud.business.dto.psf.PSFStructureEmployeeDto;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Countries;
import edn.cloud.sfactor.persistence.entities.DocumentSignature;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerDocHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerParam;
import edn.cloud.sfactor.persistence.entities.FieldsMappingMeta;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;
import edn.cloud.sfactor.persistence.entities.FieldsTemplate;
import edn.cloud.sfactor.persistence.entities.FieldsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.Language;
import edn.cloud.sfactor.persistence.entities.LookupTable;
import edn.cloud.sfactor.persistence.entities.ManagerRole;
import edn.cloud.sfactor.persistence.entities.RolesMappingPpd;
import edn.cloud.sfactor.persistence.entities.SignsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.StructureBusiness;
import edn.cloud.sfactor.persistence.entities.Template;
import edn.cloud.sfactor.persistence.entities.TemplateGroupSignature;
import edn.cloud.sfactor.persistence.entities.TemplateSignature;
import edn.cloud.web.soap.EenAlertRequestData;
import edn.cloud.web.soap.EenEvent;
import edn.cloud.web.soap.EenUser;
import edn.cloud.web.soap.Param;

public class UtilMapping {

	private final Gson defaultGson = UtilGsonFactory.getInstance().createDefaultGson();
	//private static UtilLogger logger = UtilLogger.getInstance();

	/**
	 * load array of string
	 * 
	 * @param String
	 *            separator
	 * @param String
	 *            value
	 * @return String[]
	 */
	public static String[] loadStringArrayFromString(String separator, String value) {
		if (value != null && separator != null && !value.equals("") && !separator.equals("")) {
			String[] response = value.split(separator);
			return response;
		}

		return null;
	}

	/**
	 * convert to entity from generate variables
	 * 
	 * @param PpdDocGenRequestPayloadDto
	 * @return ArrayList<FieldsTemplate>
	 */
	public static ArrayList<FieldsTemplate> templateToEntity(PpdDocGenRequestPayloadDto variables) {
		ArrayList<FieldsTemplate> list = new ArrayList<>();

		for (SlugItem field : variables.getVariables()) {
			FieldsTemplate fieldEntity = new FieldsTemplate();
			fieldEntity.setNameSource(field.getSlug());
			list.add(fieldEntity);
		}

		return list;
	}

	/**
	 * convert dto to DocumentSignature entity
	 * 
	 * @param SignatureFieldDto
	 *            dto
	 * @return DocumentSignature
	 */
	public static DocumentSignature dtoToDocumentSignature(SignatureFieldDto dto) {
		DocumentSignature sign = new DocumentSignature();
		if (dto.getIdSignDocument() != null) {
			sign.setId(dto.getIdSignDocument());
		}

		sign.setOrder(dto.getOrder());
		sign.setNameDestination(dto.getPath());
		sign.setNameSource(dto.getSlug());

		if (dto.getIdSignTempLib() != null && dto.getIdSignTempLib() >= 0) {
			sign.setSignsTemplateLibrary(new SignsTemplateLibrary(dto.getIdSignTempLib()));
		} else {
			sign.setSignsTemplateLibrary(null);
		}

		return sign;
	}

	/**
	 * convert to dto from entity
	 * 
	 * @param TemplateGroupSignature
	 *            group
	 * @param PpdDocGenRequestPayloadDto
	 * @param ArrayList<DocumentSignature>
	 * @return ArrayList<SignatureGroupDto>
	 */
	public static ArrayList<SignatureGroupDto> DocumentSignatureToDTO(TemplateGroupSignature group, ArrayList<DocumentSignature> listDocumentSign) {
		ArrayList<SignatureGroupDto> returnList = new ArrayList<>();

		SignatureGroupDto item = new SignatureGroupDto();
		item.setSignatures(new ArrayList<SignatureFieldDto>());
		item.setIdGroup(group.getId());

		if (listDocumentSign != null) {
			for (DocumentSignature docSign : listDocumentSign) {
				SignatureFieldDto singDto = new SignatureFieldDto(docSign.getNameSource(), docSign.getNameDestination());
				singDto.setIdSignDocument(docSign.getId());
				singDto.setId(docSign.getId().toString());
				singDto.setOrder(docSign.getOrder());
				singDto.setPath(docSign.getNameDestination());
				singDto.setValue(docSign.getNameDestination());
				singDto.setSlug(docSign.getNameSource());
				singDto.setFlag(Boolean.TRUE);

				if (docSign.getSignsTemplateLibrary() != null && docSign.getSignsTemplateLibrary().getId() >= 0) {
					singDto.setIdSignTempLib(docSign.getSignsTemplateLibrary().getId());
					singDto.setPath(docSign.getSignsTemplateLibrary().getNameDestination());
					singDto.setFullName(docSign.getSignsTemplateLibrary().getNameFull());
					singDto.setFlag(Boolean.FALSE);
				}

				item.getSignatures().add(singDto);
			}

			returnList.add(item);
		}

		return returnList;
	}

	/**
	 * convert to dto from entity
	 * 
	 * @param TemplateGroupSignature
	 *            group
	 * @param PpdDocGenRequestPayloadDto
	 * @return ArrayList<SignatureGroupDto>
	 */
	public static ArrayList<SignatureGroupDto> signatureGroupEntityToDTO(TemplateGroupSignature group, ArrayList<TemplateSignature> listSignature) {
		ArrayList<SignatureGroupDto> returnList = new ArrayList<>();

		SignatureGroupDto item = new SignatureGroupDto();
		item.setSignatures(new ArrayList<SignatureFieldDto>());
		item.setIdGroup(group.getId());

		for (TemplateSignature signature : listSignature) {
			SignatureFieldDto singDto = new SignatureFieldDto(signature.getNameSource(), signature.getNameDestination());
			singDto.setId(signature.getId().toString());
			singDto.setOrder(signature.getOrder());
			singDto.setPath(signature.getNameDestination());
			singDto.setSlug(signature.getNameSource());
			singDto.setFlag(signature.getIsConstants());
			singDto.setIdSingGroupTemplate(group.getId());
			singDto.setFullName(signature.getNameDestination());

			if (signature.getSignsTemplateLibrary() == null) {
				singDto.setIdSignTempLib(null);
			} else {
				singDto.setIdSignTempLib(signature.getSignsTemplateLibrary().getId());
				singDto.setPath(signature.getSignsTemplateLibrary().getNameDestination());
				singDto.setFullName(signature.getSignsTemplateLibrary().getNameFull());
			}

			item.getSignatures().add(singDto);
		}

		returnList.add(item);

		return returnList;
	}

	/**
	 * convert to dto from entity
	 * 
	 * @param Collection<FieldsTemplate>
	 *            listEntity
	 * @param Collection<FieldsTemplateLibrary>
	 * @return GenerateVariables
	 */
	public static PpdDocGenRequestPayloadDto templateFielsEntityToDTO(Collection<FieldsTemplate> listEntity, Collection<FieldsTemplateLibrary> fieldTempLib) {
		PpdDocGenRequestPayloadDto response = new PpdDocGenRequestPayloadDto();

		for (FieldsTemplate field : listEntity) {
			SlugItem fieldDto = new SlugItem(field.getNameSource(), "");
			fieldDto.setId(field.getId().toString());
			fieldDto.setPath(field.getNameDestination());
			fieldDto.setFlagAux(false);

			// update values with values of template field library
			if (fieldTempLib != null) {
				for (FieldsTemplateLibrary fieldLib : fieldTempLib) {
					if (fieldLib.getNameSource().equals(field.getNameSource())) {
						if (fieldLib.getActions() != null)
							fieldDto.setConvertValueTo(fieldLib.getActions());

						if (fieldLib.getIsTableValue()) {
							fieldDto.setPath(fieldLib.getNameDestinationExt());
						} else {
							fieldDto.setPath(fieldLib.getNameDestination());
						}
						fieldDto.setFlagAux(fieldLib.getIsTableValue());
						if (fieldLib.getIsConstants() != null && fieldLib.getIsConstants())
							fieldDto.setFlag(true);
					}
				}
			}

			response.addVariable(fieldDto);

		}

		return response;
	}

	/**
	 * convert to entity
	 * 
	 * @param TemplanteInfoDto
	 *            source
	 */
	public static Template templateToEntity(TemplateInfoDto source) {
		Template template = new Template();

		if (source != null) {
			template.setId(source.getIdTemplate());
			template.setIdentifierPpd(source.getId());
			template.setTitle(source.getTitle());
			template.setLocale(source.getLocale());
			template.setModule(source.getModule());
			template.setDocumentType(source.getDocumentType());
			template.setEsign(source.getEsign());
			template.setEmailSign(source.getEmailSign());
			template.setDescription(source.getDescription());
			template.setFormat(source.getFormat());
			template.setLatesVersion(source.getLatest_version());
			template.setSelfGeneration(source.getSelfGeneration());
			template.setFormatGenerated(source.getFormatGenerated());
			template.setManagerConfirm(source.getManagerConfirm());
			template.setSendDocAutho(source.getSendDocAutho());
			template.setTypeSign(source.getTypeSign());

			if (source.getIdEventListener() != null && source.getIdEventListener() > 0) {
				template.setEventListenerParam(new EventListenerParam(source.getIdEventListener()));
			}
		}

		return template;
	}

	/**
	 * convert to dto
	 * 
	 * @param Template
	 *            source
	 */
	public static TemplateInfoDto templateToDto(Template source) {
		TemplateInfoDto dto = new TemplateInfoDto();

		if (source != null) {
			dto.setIdTemplate(source.getId());
			dto.setId(source.getIdentifierPpd());
			dto.setTitle(source.getTitle());
			dto.setEsign(source.getEsign());
			dto.setModule(source.getModule());
			dto.setTypeSign(source.getTypeSign());

			if (source.getEventListenerParam() != null) {
				dto.setIdEventListener(source.getEventListenerParam().getId());
				dto.setNameEventListener(source.getEventListenerParam().getEventId());
			}
			dto.setDocumentType(source.getDocumentType());
			dto.setDescription(source.getDescription());
			dto.setFormat(source.getFormat());
			dto.setEmailSign(source.getEmailSign());
			dto.setLocale(source.getLocale());
			dto.setLatest_version(source.getLatesVersion());
			dto.setSelfGeneration(source.getSelfGeneration());
			dto.setFormatGenerated(source.getFormatGenerated());
			dto.setManagerConfirm(source.getManagerConfirm());
			dto.setSendDocAutho(source.getSendDocAutho());
		}

		return dto;
	}

	@SuppressWarnings("synthetic-access")
	public SFUserDto loadSFUserProfileFromJsom(String json) throws IOException {
		return load(json, new JsonLoadingJob<SFUserDto>() {
			@Override
			public SFUserDto loadFromJson(JsonReader reader) {
				return defaultGson.fromJson(reader, SFUserDto.class);
			}
		});
	}

	@SuppressWarnings("synthetic-access")
	public SFAdminList loadSFAdminsFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, SFAdminList.class);
	}

	@SuppressWarnings("synthetic-access")
	public SFAdminListU loadSFAdminUFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, SFAdminListU.class);
	}

	@SuppressWarnings("synthetic-access")
	public SFAdmin loadSFAdminFromJsom(String json) throws IOException {

		return load(json, new JsonLoadingJob<SFAdmin>() {
			@Override
			public SFAdmin loadFromJson(JsonReader reader) {
				return defaultGson.fromJson(reader, SFAdmin.class);
			}
		});

	}

	@SuppressWarnings("synthetic-access")
	public SFRecList loadSFApplicationFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, SFRecList.class);

	}

	@SuppressWarnings("synthetic-access")
	public SFRecUnique loadSFOneApplicationFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, SFRecUnique.class);

	}

	@SuppressWarnings("synthetic-access")
	public TemplateInfoDto loadDocumentTemplateMetadataFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, TemplateInfoDto.class);
	}

	@SuppressWarnings("synthetic-access")
	public PpdDocGenFullWithErrors loadGeneratedMetadataFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, PpdDocGenFullWithErrors.class);
	}

	@SuppressWarnings("synthetic-access")
	public PpdSigningDataDto loadPpdSignatureInfoDto(String json) {
		return defaultGson.fromJson(json, PpdSigningDataDto.class);
	}

	@SuppressWarnings("synthetic-access")
	public PpdDocGenRequestPayloadDto loadGenerateVariablesMetadataFromJson(String json) throws IOException {
		return defaultGson.fromJson(json, PpdDocGenRequestPayloadDto.class);
	}

	@SuppressWarnings("synthetic-access")
	public DocTemplateInfo loadDocumentTemplateFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, DocTemplateInfo.class);
	}

	@SuppressWarnings("synthetic-access")
	public TemplateInfoDto[] loadDocumentsTemplateFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, TemplateInfoDto[].class);
	}

	@SuppressWarnings("synthetic-access")
	public DocInfoDto loadDocumentsInfoFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, DocInfoDto.class);
	}

	@SuppressWarnings("synthetic-access")
	public DocInfoDto[] loadDocumentsListInfoFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, DocInfoDto[].class);
	}

	@SuppressWarnings("synthetic-access")
	public PpdUserDto[] loadUserJsom(String json) throws IOException {
		return defaultGson.fromJson(json, PpdUserDto[].class);
	}

	@SuppressWarnings("synthetic-access")
	public PpdUserDto loadUser_v1Jsom(String json) throws IOException {
		return defaultGson.fromJson(json, PpdUserDto.class);
	}

	public SFDocumentType[] loadDocumentType(String json) throws IOException {
		return defaultGson.fromJson(json, SFDocumentType[].class);
	}

	@SuppressWarnings("synthetic-access")
	public PpdCoreEmployeeInfoDto loadEmployeeUpdateJsom(String json) throws IOException {
		return defaultGson.fromJson(json, PpdCoreEmployeeInfoDto.class);
	}

	@SuppressWarnings("synthetic-access")
	public UserInfo loadUserInfoFromJson(String json) throws IOException {
		return load(json, new JsonLoadingJob<UserInfo>() {
			@Override
			public UserInfo loadFromJson(JsonReader reader) {
				return defaultGson.fromJson(reader, UserInfo.class);
			}
		});
	}

	@SuppressWarnings("synthetic-access")
	public SFPhotoDto loadSFUserImageFromJson(String json) throws IOException {
		return load(json, new JsonLoadingJob<SFPhotoDto>() {
			@Override
			public SFPhotoDto loadFromJson(JsonReader reader) {
				return defaultGson.fromJson(reader, SFPhotoDto.class);
			}
		});
	}

	@SuppressWarnings("synthetic-access")
	public List<SFUserPhotoDto> loadSFUserProfileListFromJsom(String json) throws IOException {
		if (json == null) {
			return Collections.emptyList();
		}

		return load(json, new JsonLoadingJob<List<SFUserPhotoDto>>() {
			@Override
			public List<SFUserPhotoDto> loadFromJson(JsonReader reader) {
				edn.cloud.business.dto.SFUserListDto sfUserList = defaultGson.fromJson(reader, SFUserListDto.class);
				return sfUserList.getResults();
			}
		});
	}

	@SuppressWarnings("synthetic-access")
	public SFRoleList loadSFUserRoleListFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, SFRoleList.class);
	}

	@SuppressWarnings("synthetic-access")
	public SFGroupList loadSFGroupListFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, SFGroupList.class);
	}

	public <T> T load(String json, JsonLoadingJob<T> job) throws IOException {
		JsonReader reader = UtilJson.getInstance().createJsonReader(json);
		try {
			return job.loadFromJson(reader);
		} finally {
			UtilJson.getInstance().closeJsonReader(reader);
		}
	}

	/**
	 * empty fields not exist in Version Core employee
	 * 
	 * @param PpdCoreEmployeeInfoDto
	 *            employee
	 * @return PpdCoreEmployeeInfoDto
	 */
	public static PpdCoreEmployeeInfoDto emptyPddEmployeeCreateVCore(PpdCoreEmployeeInfoDto employee) {
		employee.setMaiden_name(null);
		employee.setTechnical_id(null);
		employee.setDeparture_date(null);
		employee.setDob(null);
		employee.setPhone_number(null);
		employee.setDisable_elec_distribution(null);
		employee.setDisable_paper_distribution(null);
		employee.setGone(null);
		employee.setDisable_vault(null);
		employee.setFilters(null);

		if (employee.getRegistration_references() != null) {
			for (PpdCoreEmployeeOrgInfoDto reg : employee.getRegistration_references()) {
				reg.setOrganization_code(null);
				reg.setRegistration_number(null);
			}
		}
		return employee;
	}

	/**
	 * empty fields not exist in Version 1 employee
	 * 
	 * @param PpdCoreEmployeeInfoDto
	 *            employee
	 * @return PpdCoreEmployeeInfoDto
	 */
	public static PpdCoreEmployeeInfoDto emptyPddEmployeeCreateV1(PpdCoreEmployeeInfoDto employee) {
		employee.setExternal_id(null);
		employee.setMaidenname(null);
		employee.setMiddlename(null);
		employee.setLanguage(null);
		employee.setTimezone(null);
		employee.setSaml_token(null);
		employee.setBirth_date(null);
		employee.setState(null);
		employee.setCustom_fields(null);

		if (employee.getRegistration_references() != null) {
			for (PpdCoreEmployeeOrgInfoDto reg : employee.getRegistration_references()) {
				reg.setEmployee_number(null);
				reg.setOrganization_id(null);
			}
		}

		return employee;
	}

	/**
	 * load PpdElectronicVaultOptionsDto from parameters in structure filters
	 * 
	 * @param String
	 *            idEmployeePpd
	 * @param String
	 *            parameters
	 * @return PpdElectronicVaultOptionsDto
	 */
	public static PpdElectronicVaultOptionsDto loadPpdElectronicFromKey(String idEmployeePpd, String parameters) {
		PpdElectronicVaultOptionsDto response = new PpdElectronicVaultOptionsDto();
		if (parameters != null) {
			String[] arrayValue = parameters.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode());
			if (arrayValue != null) {
				for (String parValue : arrayValue) {
					String[] item = parValue.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
					if (item != null && item.length > 1) {
						if (item[0].toString().equals("electronic_vault_subscription_enabled")) {
							response.setElectronic_vault_subscription_enabled(item[1].toString().equals(UtilCodesEnum.CODE_TRUE.getCode()) ? true : false);
						} else if (item[0].toString().equals("paper_documents_distribution_enabled")) {
							response.setPaper_documents_distribution_enabled(item[1].toString().equals(UtilCodesEnum.CODE_TRUE.getCode()) ? true : false);
						} else if (item[0].toString().equals("electronic_documents_distribution_enabled")) {
							response.setElectronic_documents_distribution_enabled(item[1].toString().equals(UtilCodesEnum.CODE_TRUE.getCode()) ? true : false);
						} else if (item[0].toString().equals("electronic_payslips_opted_out")) {
							response.setElectronic_payslips_opted_out(item[1].toString().equals(UtilCodesEnum.CODE_TRUE.getCode()) ? true : false);
						} else if (item[0].toString().equals("id")) {
							response.setId(item[1].toString());
						} else if (item[0].toString().equals("electronic_vault_id")) {
							response.setElectronic_vault_id(item[1].toString());
						} else if (item[0].toString().equals("electronic_vault_state")) {
							response.setElectronic_vault_state(item[1].toString());
						} else if (item[0].toString().equals("electronic_payslips_choice_updated_at")) {
							response.setElectronic_payslips_choice_updated_at(item[1].toString());
						} else if (item[0].toString().equals("electronic_payslips_choice_origin")) {
							response.setElectronic_payslips_choice_origin(item[1].toString());
						}
					}
				}
			}

			return response;
		}

		return null;
	}

	/**
	 * Load PpdCoreEmployeeInfoDto from key, value
	 * 
	 * @param PpdCoreEmployeeInfoDto
	 *            employeeCreate
	 * @param String
	 *            key
	 * @param String
	 *            value
	 * @return PpdCoreEmployeeInfoDto
	 */
	public static PpdCoreEmployeeInfoDto loadPpdCreateEmployeeFromKey(PpdCoreEmployeeInfoDto employee, String key, String value) {
		if (key != null && !key.equals("")) {
			if (key.toLowerCase().equals("lastname"))
				employee.setLastname(value);
			if (key.toLowerCase().equals("firstname"))
				employee.setFirstname(value);
			if (key.toLowerCase().equals("email"))
				employee.setEmail(value);

			if (key.toLowerCase().equals("organization_id") || key.equals("employee_number") || key.equals("active")) {
				PpdCoreEmployeeOrgInfoDto organizationDto = new PpdCoreEmployeeOrgInfoDto();
				if (employee.getRegistration_references() == null) {
					employee.setRegistration_references(new ArrayList<PpdCoreEmployeeOrgInfoDto>());
					employee.getRegistration_references().add(organizationDto);
				} else if (employee.getRegistration_references().size() > 0) {
					organizationDto = employee.getRegistration_references().get(0);
				}

				if (key.toLowerCase().equals("organization_id"))
					organizationDto.setOrganization_id(value);
				if (key.toLowerCase().equals("employee_number"))
					organizationDto.setEmployee_number(value);
				if (key.toLowerCase().equals("active"))
					organizationDto.setActive(value);
				if (key.toLowerCase().equals("departure_date"))
					organizationDto.setDeparture_date(value);
			}

			if (key.toLowerCase().equals("address1"))
				employee.setAddress1(value);
			if (key.toLowerCase().equals("address2"))
				employee.setAddress2(value);
			if (key.toLowerCase().equals("address3"))
				employee.setAddress3(value);
			if (key.toLowerCase().equals("zip_code"))
				employee.setZip_code(value);

			if (key.toLowerCase().equals("city"))
				employee.setCity(value);
			if (key.toLowerCase().equals("country"))
				employee.setCountry(value);
			if (key.toLowerCase().equals("phone_number"))
				employee.setPhone_number(value);
			if (key.toLowerCase().equals("mobile_phone_number"))
				employee.setMobile_phone_number(value);
			if (key.toLowerCase().equals("starting_date"))
				employee.setStarting_date(value);
			if (key.toLowerCase().equals("external_id"))
				employee.setExternal_id(value);
			if (key.toLowerCase().equals("maidenname"))
				employee.setMaidenname(value);
			if (key.toLowerCase().equals("middlename"))
				employee.setMiddlename(value);
			if (key.toLowerCase().equals("language"))
				employee.setLanguage(value);
			if (key.toLowerCase().equals("timezone"))
				employee.setTimezone(value);
			if (key.toLowerCase().equals("birth_date"))
				employee.setBirth_date(value);
			if (key.toLowerCase().equals("state"))
				employee.setState(value);
			if (key.toLowerCase().equals("saml_token"))
				employee.setSaml_token(value);

			if (key.toLowerCase().equals("options") && value != null && !value.equals("")) {
				HashMap<String, String> map = new HashMap<>();
				String[] opt = value.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode());
				if (opt != null) {
					for (String item : opt) {
						String[] mapValue = item.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
						if (mapValue != null && mapValue.length > 0) {
							map.put(mapValue[0], mapValue[1]);
						}
					}
					employee.setOptions(map);
				}
			}
		}
		return employee;
	}

	/**
	 * Load PpdEmployeeInfoDto from key, value
	 * 
	 * @param PpdCoreEmployeeInfoDto
	 *            employee
	 * @param String
	 *            key
	 * @param String
	 *            value
	 * @return PpdCoreEmployeeInfoDto
	 */
	public static PpdCoreEmployeeInfoDto loadPpdEmployeFromKey(PpdCoreEmployeeInfoDto employee, String key, String value) {
		if (key != null && !key.equals("")) {
			if (key.toLowerCase().equals("lastname"))
				employee.setLastname(value);
			if (key.toLowerCase().equals("firstname"))
				employee.setFirstname(value);
			if (key.toLowerCase().equals("maiden_name"))
				employee.setMaiden_name(value);
			if (key.toLowerCase().equals("email"))
				employee.setEmail(value);
			if (key.toLowerCase().equals("technical_id"))
				employee.setTechnical_id(value);

			if (key.toLowerCase().equals("organization_code") || key.equals("registration_number") || key.equals("active")) {
				PpdCoreEmployeeOrgInfoDto organizationDto = new PpdCoreEmployeeOrgInfoDto();
				if (employee.getRegistration_references() == null) {
					employee.setRegistration_references(new ArrayList<PpdCoreEmployeeOrgInfoDto>());
					employee.getRegistration_references().add(organizationDto);
				} else if (employee.getRegistration_references().size() > 0) {
					organizationDto = employee.getRegistration_references().get(0);
				}

				if (key.toLowerCase().equals("organization_code"))
					organizationDto.setOrganization_code(value);
				if (key.toLowerCase().equals("registration_number"))
					organizationDto.setRegistration_number(value);
				if (key.toLowerCase().equals("active"))
					organizationDto.setActive(value);
			}

			if (key.toLowerCase().equals("departure_date"))
				employee.setDeparture_date(value);
			if (key.toLowerCase().equals("dob"))
				employee.setDob(value);
			if (key.toLowerCase().equals("address1"))
				employee.setAddress1(value);
			if (key.toLowerCase().equals("address2"))
				employee.setAddress2(value);
			if (key.toLowerCase().equals("address3"))
				employee.setAddress3(value);
			if (key.toLowerCase().equals("zip_code"))
				employee.setZip_code(value);

			if (key.toLowerCase().equals("city"))
				employee.setCity(value);
			if (key.toLowerCase().equals("country"))
				employee.setCountry(value);
			if (key.toLowerCase().equals("phone_number"))
				employee.setPhone_number(value);
			if (key.toLowerCase().equals("mobile_phone_number"))
				employee.setMobile_phone_number(value);
			if (key.toLowerCase().equals("disable_elec_distribution"))
				employee.setDisable_elec_distribution(value);
			if (key.toLowerCase().equals("disable_paper_distribution"))
				employee.setDisable_paper_distribution(value);
			if (key.toLowerCase().equals("starting_date"))
				employee.setStarting_date(value);
			if (key.toLowerCase().equals("gone"))
				employee.setGone(value);
			if (key.toLowerCase().equals("disable_vault"))
				employee.setDisable_vault(value);
		}

		return employee;
	}

	/**
	 * load SFAttachResponseDto from response json
	 * 
	 * @param String
	 *            json
	 * @return SFAttachResponseDto
	 */
	@SuppressWarnings("synthetic-access")
	public SFAttachResponseN1Dto loadSFAttachResponseDtoFromJsom(String json) throws IOException {
		return defaultGson.fromJson(json, SFAttachResponseN1Dto.class);
	}

	/**
	 * @param EenEvent
	 *            events
	 * @return EenUser
	 */
	public static EenUser loadEenUserFetcher(EenEvent events) {
		EenUser user = new EenUser();

		for (EenAlertRequestData item : events.getEvent()) {
			for (Param entity : item.getEntityKeys().getEntityKey()) {
				if (entity.getName().toString().equals("userId")) {
					user.setUserId(entity.getValue());
				}
				if (entity.getName().toString().equals("startDate")) {
					user.setStartDate(entity.getValue());
				}
				if (entity.getName().toString().equals("seqNumber")) {
					user.setSeqNumber(entity.getValue());
				}
			}
			for (Param param : item.getParams().getParam()) {

				if (param.getName().toString().equals("personIdExternal")) {
					user.setPersonIdExternal(param.getValue());
				}
				if (param.getName().toString().equals("perPersonUuid")) {
					user.setPerPersonUuid(param.getValue());
				}
			}
		}

		return user;
	}

	/**
	 * returns the value associated with a key, with the following structure:
	 * key:value;key:value
	 * 
	 * @param String
	 *            key
	 * @param String
	 *            object
	 * @param String
	 *            separator
	 * @return String value
	 */
	public static String loadKeyValueFromString(String key, String object, String separator) {
		if (key != null && object != null && separator != null) {
			String[] array = object.split(separator);

			if (array != null && array.length > 0) {
				for (int i = 0; i < array.length; i++) {
					String[] arrayValue = array[i].split(":");

					if (arrayValue != null && arrayValue.length > 0) {
						if (arrayValue[0].toString().equals(key)) {
							return arrayValue[1].toString();
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Apply URL encoder
	 * 
	 * @param String
	 *            value
	 * @return value encoder
	 */
	public static String toStringToUrlEncoder(String value) {
		if (value != null) {
			try {
				value = StringUtils.stripAccents(value);
				value = URLEncoder.encode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return value;

	}

	/**
	 * Apply string format to view in html
	 * 
	 * @param String
	 *            value
	 * @return String
	 */
	public static String toStringHtmlFormat(String value) {
		if (value != null) {
			value = StringUtils.stripAccents(value);
			// value = value.replaceAll("\\p{L}", "");
			value = value.replaceAll(UtilCodesEnum.CODE_PATRON_HTMLFORMAT.getCode(), " ");
		}

		return value;
	}

	/***
	 * 
	 * */
	public static String toStringSimpleFormat(String value, String replaceValue) {
		if (value != null) {
			value = StringUtils.stripAccents(value);
			// value = value.replaceAll("\\p{L}", "");
			return value.replaceAll(UtilCodesEnum.CODE_PATRON_HTMLFORMAT.getCode(), replaceValue);
		}

		return value;
	}

	/**
	 * 
	 * */
	public static boolean isUnicodeValidValue(String value) {
		byte[] myBytes = null;

		try {
			myBytes = value.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return false;
		}

		return true;
	}

	/**
	 * Apply string format to view in html
	 * 
	 * @param String
	 *            value
	 * @param String
	 *            pattern
	 * @return String
	 */
	public static String toStringApplyFormat(String value, String pattern) {
		if (value != null) {
			value = value.replaceAll(pattern, "");
		}

		return value;
	}

	/**
	 * load attribute for User Info module recruiting form json,
	 * 
	 * @param String
	 *            json
	 * @param String
	 *            key info
	 * @return String
	 */
	public static String loadUserInfoRecruitingForJson(String json, String key) {
		String response = "";

		if (json != null) {
			Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(json.toString());
			if (flattenedJsonMap != null) {
				if (flattenedJsonMap.get(key) != null) {
					return flattenedJsonMap.get(key).toString();
				}
			}
		}

		return response;
	}

	/**
	 * return value from parameters structure
	 * 
	 * @param String
	 *            source
	 * @param String
	 *            key
	 */
	public static String loadParametersValueFromString(String source, String key) {
		if (source != null && !source.equals("")) {
			String[] arrayString = source.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.toString());
			if (arrayString != null && arrayString.length > 0) {
				for (int i = 0; i < arrayString.length; i++) {
					if (arrayString[i].toString().contains(key)) {
						String[] arrayValue = arrayString[i].toString().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.toString());
						if (arrayValue.length > 1 && arrayValue[1] != null)
							return arrayValue[1].toString();
					}
				}
			}
		}

		return "";
	}

	/**
	 * Load attachments employee center from Json query
	 * 
	 * @param String responseSearch
	 * @param FieldsMappingPpd fielMappingPpd
	 * @param List<FieldsMappingMeta> metadata
	 * @return ArrayList<EventListenerDocProcess>
	 */
	public static ArrayList<EventListenerDocProcess> loadAttachmentsEmployeeCentral(String responseSearch, 
																					FieldsMappingPpd fielMappingPpd,
																					List<FieldsMappingMeta> metadataAttach,
																					Date initDate, 
																					Date endDate) 
	{
		ArrayList<EventListenerDocProcess> resultList = new ArrayList<>();

		if (responseSearch != null) 
		{
			JSONObject nodeAllJson = new JSONObject(responseSearch);

			if (nodeAllJson.isNull("d") == false) 
			{
				JSONObject nodeD = nodeAllJson.getJSONObject("d");
				// logger.info("we are here 0");
				if (nodeD.isNull("results") == false) 
				{
					JSONArray nodeResultsJson = nodeD.getJSONArray("results");
					// logger.info("we are here 0.0.11");
					if (nodeResultsJson != null) 
					{
						for (int i = 0; i < nodeResultsJson.length(); i++) 
						{
							EventListenerDocProcess document = new EventListenerDocProcess();

							// logger.info("we are here 0.1");
							// -------------------------
							// search user data
							if (nodeResultsJson.getJSONObject(i).isNull("personIdExternal") == false) {
								document.setUserExtendPpd(nodeResultsJson.getJSONObject(i).getString("personIdExternal"));
								document.setUserIdPpd(nodeResultsJson.getJSONObject(i).getString("personIdExternal"));
							}

							if (nodeResultsJson.getJSONObject(i).isNull("userId") == false) {
								document.setUserIdPpd(nodeResultsJson.getJSONObject(i).getString("userId"));
							}
							
							// logger.info("we are here 1");
							// Do Iterative search of object
							String r2 = DoIterativeDate("", nodeResultsJson.getJSONObject(i), fielMappingPpd.getNameSource().split("/"), document.getUserExtendPpd(), document.getUserIdPpd(), "lastModifiedDateTime");
							String r3 = DoIterativeDate("", nodeResultsJson.getJSONObject(i), fielMappingPpd.getNameSource().split("/"), document.getUserExtendPpd(), document.getUserIdPpd(), "startDate");
							
							//process 
							if(metadataAttach!=null && metadataAttach.size()>0){
								for(FieldsMappingMeta metadataAttachItem:metadataAttach){
									String r4 = DoIterativeDate("", nodeResultsJson.getJSONObject(i), metadataAttachItem.getFieldsTemplateLibrary().getNameDestination().split("/"), document.getUserExtendPpd(), document.getUserIdPpd(),metadataAttachItem.getFieldsTemplateLibrary().getNameSource());
								}
							}
							
							
							Date dt = new Date();
							Date dt3 = new Date();

							if (!r2.equals("")) {
								String datereip = r2.replace("/Date(", "").replace(")/", "");
								datereip = datereip.replace("+0000", "");

								Long timeInMillis = Long.valueOf(datereip);

								Calendar c = Calendar.getInstance();
								c.setTimeInMillis(timeInMillis);
								// c.add(Calendar.HOUR_OF_DAY, 5);
								dt = c.getTime();

								/*
								 * logger.info("------START--------" + fielMappingPpd.getNameSource());
								 * logger.gson(initDate); logger.gson(endDate); logger.gson(dt);
								 * logger.info("------END--------");
								 */
							} else {
								/*
								 * logger.info("------START--------" + fielMappingPpd.getNameSource());
								 * logger.gson(initDate); logger.gson(endDate);
								 * logger.info("------END--------");
								 */
							}

							if (!r3.equals("")) {
								String datereip3 = r3.replace("/Date(", "").replace(")/", "");
								datereip3 = datereip3.replace("+0000", "");
								Long timeInMillis3 = Long.valueOf(datereip3);

								Calendar c3 = Calendar.getInstance();
								c3.setTimeInMillis(timeInMillis3);
								dt3 = c3.getTime();

								/*
								 * logger.info("------START--------" + fielMappingPpd.getNameSource());
								 * logger.gson(dt3); logger.info("------END--------");
								 */
							} else {

							}

							// -------------------------
							// if is true update user ppd for default
							if (fielMappingPpd.getIsFilter() != null && fielMappingPpd.getIsFilter()) {
								// logger.info("we are here and it is ok");
								EventListenerDocProcess documentUpdateUser = new EventListenerDocProcess();
								documentUpdateUser.setUserExtendPpd(document.getUserExtendPpd());
								documentUpdateUser.setUserIdPpd(document.getUserIdPpd());
								documentUpdateUser.setStartDatePpdOnAttach(new Date());
								documentUpdateUser.setAttachmentIdSF(UtilCodesEnum.CODE_TYPE_MODULE_ATTACH_UPDATE_USER.getCode());
								// logger.info("we are here and it is ok 2");
								if (!r2.equals("") && (initDate.before(dt) || initDate.equals(dt)) && endDate.after(dt)) {
									// logger.info("we are here and it is ok 3");
									resultList.add(documentUpdateUser);
								}
								if (!r3.equals("") && (initDate.before(dt3) || initDate.equals(dt3)) && endDate.after(dt3)) {
									// logger.info("we are here and it is ok 4");
									resultList.add(documentUpdateUser);
								}
								if (r2.equals("")) {
									resultList.add(documentUpdateUser);
								}
								// logger.info("we are here and it is ok 5");
							}

							if (!r2.equals("") && (initDate.before(dt) || initDate.equals(dt)) && endDate.after(dt)) {
								ArrayList<EventListenerDocProcess> r = DoIterative(resultList, nodeResultsJson.getJSONObject(i), fielMappingPpd.getNameSource().split("/"), document.getUserExtendPpd(), document.getUserIdPpd());
							}

							if (!r3.equals("") && (initDate.before(dt3) || initDate.equals(dt3)) && endDate.after(dt3)) {
								ArrayList<EventListenerDocProcess> r = DoIterative(resultList, nodeResultsJson.getJSONObject(i), fielMappingPpd.getNameSource().split("/"), document.getUserExtendPpd(), document.getUserIdPpd());
							}

							if (r2.equals("")) {
								ArrayList<EventListenerDocProcess> r = DoIterative(resultList, nodeResultsJson.getJSONObject(i), fielMappingPpd.getNameSource().split("/"), document.getUserExtendPpd(), document.getUserIdPpd());
							}

							// Do Iterative search of object

						}
					}
				}
			}
		}
		//logger.gson(resultList);
		// TO BE CHANGED ERRRRORRRRR
		return resultList;
		// return resultList;
	}

	private static ArrayList<EventListenerDocProcess> DoIterative(ArrayList<EventListenerDocProcess> resultList, JSONObject jbo, String[] route, String gid, String id) {
		//logger.info("we enter here");
		//logger.gson(jbo);
		//logger.gson(route);
		if (route == null || (route != null && route[0].equals(" ")) || (route != null && route[0].equals(""))) {
			if (!jbo.isNull("attachmentId") && jbo.getString("attachmentId") != null) {
				EventListenerDocProcess document = new EventListenerDocProcess();
				document.setUserExtendPpd(gid);
				document.setUserIdPpd(id);
				document.setAttachmentIdSF(jbo.getString("attachmentId"));

				if (jbo.isNull("startDate") == false) {
					try {
						document.setStartDatePpdOnAttach(UtilDateTimeAdapter.JSONTarihConvert(jbo.getString("startDate")));
					} catch (Exception e) {
						document.setStartDatePpdOnAttach(new Date());
					}
				} else {
					document.setStartDatePpdOnAttach(new Date());
				}
				document.setStartDatePpdOnAttach(new Date());

				resultList.add(document);
			} else {

			}
			return resultList;
		}

		int n = (route.length - 1 > 0 ? route.length - 1 : 0);
		String[] newArray = new String[(n > 0 ? n : 0)];
		if (n > 0) {
			System.arraycopy(route, 1, newArray, 0, n);
			// logger.gson(newArray);

			if (jbo.getJSONObject(route[0]).has("results")) {
				JSONObject jobO = jbo.getJSONObject(route[0]);
				// DO ITERATE AGAIN FOR
				JSONArray jarO = jobO.getJSONArray("results");
				ArrayList<EventListenerDocProcess> r = new ArrayList<>();
				for (int i = 0; i < jarO.length(); i++) {
					r.addAll(DoIterative(resultList, jarO.getJSONObject(i), newArray, gid, id));

				}
				return r;
			} else {
				// logger.info("go directly " + route[0] + " - " + gid);
				// DO ITERATE AGAIN DIRECT

				return DoIterative(resultList, jbo.getJSONObject(route[0]), newArray, gid, id);

			}
		} else {

			if (!jbo.isNull(route[0]) && jbo.getJSONObject(route[0]).has("results")) {
				JSONObject jobO = jbo.getJSONObject(route[0]);
				// DO ITERATE AGAIN FOR
				JSONArray jarO = jobO.getJSONArray("results");
				ArrayList<EventListenerDocProcess> r = new ArrayList<>();
				for (int i = 0; i < jarO.length(); i++) {
					r.addAll(DoIterative(resultList, jarO.getJSONObject(i), null, gid, id));
				}
				return r;
			} else {
				if (!jbo.isNull(route[0]) && !jbo.getJSONObject(route[0]).isNull("attachmentId")) {
					EventListenerDocProcess document = new EventListenerDocProcess();
					document.setUserExtendPpd(gid);
					document.setUserIdPpd(id);
					document.setAttachmentIdSF(jbo.getJSONObject(route[0]).getString("attachmentId"));

					if (jbo.getJSONObject(route[0]).isNull("startDate") == false) {
						try {
							document.setStartDatePpdOnAttach(UtilDateTimeAdapter.JSONTarihConvert(jbo.getJSONObject(route[0]).getString("startDate")));
						} catch (Exception e) {
							document.setStartDatePpdOnAttach(new Date());
						}
					} else {
						document.setStartDatePpdOnAttach(new Date());
					}

					resultList.add(document);

				}
				return resultList;
			}

		}
	}

	private static String DoIterativeDate(String resultListR, JSONObject jbo, String[] route, String gid, String id, String dateParam) {

		if (route == null) {
			if (!jbo.isNull(dateParam) && jbo.getString(dateParam) != null) {

				return resultListR = jbo.getString(dateParam);
			} else {
				return "";
			}

		}

		int n = (route.length - 1 > 0 ? route.length - 1 : 0);
		String[] newArray = new String[(n > 0 ? n : 0)];
		if (n > 0) {
			System.arraycopy(route, 1, newArray, 0, n);
			// logger.gson(newArray);

			if (jbo.getJSONObject(route[0]).has("results")) {
				JSONObject jobO = jbo.getJSONObject(route[0]);
				// DO ITERATE AGAIN FOR
				JSONArray jarO = jobO.getJSONArray("results");
				String r = "";
				for (int i = 0; i < jarO.length(); i++) {
					r = DoIterativeDate(resultListR, jarO.getJSONObject(i), newArray, gid, id, dateParam);

				}
				return r;
			} else {
				// logger.info("go directly " + route[0] + " - " + gid);
				// DO ITERATE AGAIN DIRECT

				return DoIterativeDate(resultListR, jbo.getJSONObject(route[0]), newArray, gid, id, dateParam);

			}
		} else {

			if (!jbo.isNull(route[0]) && jbo.getJSONObject(route[0]).has("results")) {
				JSONObject jobO = jbo.getJSONObject(route[0]);
				// DO ITERATE AGAIN FOR
				JSONArray jarO = jobO.getJSONArray("results");
				String r = "";
				for (int i = 0; i < jarO.length(); i++) {
					r = DoIterativeDate(resultListR, jarO.getJSONObject(i), null, gid, id, dateParam);
				}
				return r;
			} else {
				if (!jbo.isNull(route[0]) && !jbo.getJSONObject(route[0]).isNull(dateParam)) {

					resultListR = jbo.getJSONObject(route[0]).getString(dateParam);

				}
				return resultListR;
			}

		}

		// DoIterative(resultList, jbo);

		// return new ArrayList<>();
	}

	public List<AdminParameters> loadAdminParametersFromJsom(String json) {
		if (json == null) {
			return Collections.emptyList();
		}
		List<AdminParameters> listReturn = new ArrayList<AdminParameters>();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			AdminParameters admParameters = defaultGson.fromJson(jsonObject.toString(), AdminParameters.class);
			listReturn.add(admParameters);
		}
		return listReturn;
	}

	public List<FieldsMappingPpd> loadFieldsMappingPpdFromJsom(String json) throws IOException {
		if (json == null) {
			return Collections.emptyList();
		}

		List<FieldsMappingPpd> listReturn = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			FieldsMappingPpd fieldsMapping = defaultGson.fromJson(jsonObject.toString(), FieldsMappingPpd.class);
			listReturn.add(fieldsMapping);
		}
		return listReturn;
	}

	public List<FieldsTemplateLibrary> loadFieldsTemplateLibraryFromJsom(String json) throws IOException {
		if (json == null) {
			return Collections.emptyList();
		}

		List<FieldsTemplateLibrary> listReturn = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			FieldsTemplateLibrary fieldTemplate = defaultGson.fromJson(jsonObject.toString(), FieldsTemplateLibrary.class);
			listReturn.add(fieldTemplate);
		}
		return listReturn;
	}

	public List<EventListenerParam> loadParameterEventListenersFromJsom(String json) throws IOException {
		if (json == null) {
			return Collections.emptyList();
		}

		List<EventListenerParam> listReturn = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			EventListenerParam fieldTemplate = defaultGson.fromJson(jsonObject.toString(), EventListenerParam.class);
			listReturn.add(fieldTemplate);
		}
		return listReturn;
	}

	public List<Countries> loadCountriesFromJsom(String json) throws IOException {
		if (json == null) {
			return Collections.emptyList();
		}

		List<Countries> listReturn = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			Countries country = defaultGson.fromJson(jsonObject.toString(), Countries.class);
			listReturn.add(country);
		}
		return listReturn;
	}

	public List<SignsTemplateLibrary> loadSignTemplateLibFromJsom(String json) throws IOException {
		if (json == null) {
			return Collections.emptyList();
		}

		List<SignsTemplateLibrary> listReturn = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			SignsTemplateLibrary sign = defaultGson.fromJson(jsonObject.toString(), SignsTemplateLibrary.class);
			listReturn.add(sign);
		}
		return listReturn;
	}

	public List<Language> loadLanguagesFromJson(String json) {
		if (json == null) {
			return Collections.emptyList();
		}

		List<Language> listReturn = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			Language lang = defaultGson.fromJson(jsonObject.toString(), Language.class);
			listReturn.add(lang);
		}
		return listReturn;
	}

	public List<RolesMappingPpd> loadRolesFromJson(String json) {
		if (json == null) {
			return Collections.emptyList();
		}

		List<RolesMappingPpd> listReturn = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			RolesMappingPpd rol = defaultGson.fromJson(jsonObject.toString(), RolesMappingPpd.class);
			listReturn.add(rol);
		}
		return listReturn;
	}

	/**
	 * get String separate by coma from list of SF Group List
	 * 
	 * @param SFGroupList
	 *            listGroups
	 */
	public static String getStringByComaFromGroupList(SFGroupList listGroups) {
		String idGroupsList = "";
		if (listGroups != null && listGroups.getD().length > 0) {
			for (SFGroup string : listGroups.getD()) {
				idGroupsList += "'" + string.getGroupName() + "',";
			}

			idGroupsList = idGroupsList.substring(0, idGroupsList.length() - 1);
		}

		return idGroupsList;
	}

	/**
	 * get String subquery
	 * 
	 * @param String
	 *            info
	 * @param int
	 *            maxlength
	 */
	public static String getStringSubString(String info, int maxlength) {
		if (info != null && info.length() > maxlength) {
			return info.substring(0, maxlength);
		}

		return info;
	}

	/**
	 * encrypt data
	 * 
	 * @param strClearText
	 * @retrun data encrypt
	 */
	public static String encrypt(String strClearText) {
		return strClearText;
		/*
		 * String strData="";
		 * 
		 * if (strClearText != null && !strClearText.equals("")) { try { SecretKeySpec
		 * skeyspec = new
		 * SecretKeySpec(UtilCodesEnum.CODE_KEY_ENCRYPT_DATA.getCode().getBytes(),
		 * "AES"); Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		 * cipher.init(Cipher.ENCRYPT_MODE, skeyspec, new IvParameterSpec(new
		 * byte[16])); byte[] encrypted =
		 * cipher.doFinal(strClearText.getBytes(StandardCharsets.UTF_8)); strData =
		 * Base64.getEncoder().encodeToString(encrypted);
		 * 
		 * 
		 * } catch (Exception e) { e.printStackTrace(); return strClearText; } } return
		 * strData;
		 */
	}

	/**
	 * decrypt data
	 * 
	 * @param String
	 *            strEncrypted
	 * @return decryp data
	 */
	public static String decrypt(String strEncrypted) {
		return strEncrypted;
		/*
		 * String strData="";
		 * 
		 * if (strEncrypted != null && !strEncrypted.equals("")) { try { SecretKeySpec
		 * skeyspec = new
		 * SecretKeySpec(UtilCodesEnum.CODE_KEY_ENCRYPT_DATA.getCode().getBytes(),
		 * "AES"); Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		 * cipher.init(Cipher.DECRYPT_MODE, skeyspec, new IvParameterSpec(new
		 * byte[16])); byte[] textEncrypt= Base64.getDecoder().decode(strEncrypted);
		 * byte[] encrypted = cipher.doFinal(textEncrypt); strData = new
		 * String(encrypted, StandardCharsets.UTF_8);
		 * 
		 * 
		 * } catch (Exception e) { e.printStackTrace(); return strEncrypted; } } return
		 * strData;
		 */
	}

	/**
	 * build hash code to download file from ppd
	 * 
	 * @param String
	 *            idDocument in Ppd
	 */

	public static String getHashCodeDownloadDoc(String idDocument) throws Exception {
		long unixTimestamp = Instant.now().getEpochSecond();
		String password = UtilCodesEnum.CODE_PASSWS_DOWNLOAD_DOC.getCode() + idDocument + unixTimestamp;
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		sha256.update(password.getBytes("UTF-8"));
		byte[] digest = sha256.digest();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < digest.length; i++) {
			sb.append(String.format("%02x", digest[i]));
		}
		String hash = sb.toString();

		return "?timestamp=" + unixTimestamp + "&hash=" + hash + "&hash_method=sha256";
	}

	/**
	 * build string, replace new value for pattern un original value
	 * 
	 * @param String
	 *            originalValue
	 * @param String
	 *            newValue
	 * @param String
	 *            pattern
	 */
	public static String setStringInPattern(String originalValue, String newValue, String pattern) {
		if (pattern != null && !pattern.equals("")) {
			return originalValue.replace(pattern, newValue);
		}

		return "";
	}

	/**
	 * build array of PpdFilterCoreEmployeeDto
	 * 
	 * @param HashMap<String,
	 *            String> filtersList
	 * @return ArrayList<PpdFilterCoreEmployeeDto>
	 */
	public static ArrayList<PpdFilterCoreEmployeeDto> loadPpdFilterCoreEmpToHashMap(HashMap<String, String> filtersList) {
		ArrayList<PpdFilterCoreEmployeeDto> response = new ArrayList<>();

		for (Map.Entry<String, String> entry : filtersList.entrySet()) {
			PpdFilterCoreEmployeeDto item = new PpdFilterCoreEmployeeDto();
			item.setCode(entry.getKey());
			item.setValue(entry.getValue());
			response.add(item);
		}

		return response;
	}

	/**
	 * return string without extension
	 * 
	 * @param string
	 *            nameFile
	 * @return string
	 */
	public static String getStripExtension(String str) {
		// Handle null case specially.
		if (str == null)
			return null;

		// Get position of last '.'.
		int pos = str.lastIndexOf(".");

		// If there wasn't any '.' just return the string as is.
		if (pos == -1)
			return str;

		// Otherwise return the string, up to the dot.
		return str.substring(0, pos);
	}

	public static String deleteItemJsonArray(String json, String[] keys) {

		JSONArray array = new JSONArray(json);
		JSONArray array2 = new JSONArray();
		JSONObject obj = new JSONObject();
		for (int i = 0; i < array.length(); i++) {

			obj = array.getJSONObject(i);
			for (String key : keys) {
				obj.remove(key);
			}
			array2.put(obj);
		}
		return array2.toString();
	}

	public static String deleteItemJsonObject(String json, String[] keys) {

		JSONObject obj = new JSONObject(json);
		for (String key : keys) {
			obj.remove(key);
		}
		return obj.toString();
	}

	public static List<FolderDTO> removeItemFolder(List<FolderDTO> listFolder) {
		List<FolderDTO> listReturn = new ArrayList<>();
		List<FolderDTO> subfolder = new ArrayList<>();
		for (FolderDTO item : listFolder) {
			if (!item.getNodesFolders().isEmpty()) {
				subfolder = item.getNodesFolders();

				for (FolderDTO node : subfolder) {
					node.setNodes(null);
					node.setNodesFolders(null);
					node.catSeeEdit = null;
					node.catSeeEnter = null;
					node.catSeeNothing = null;
					node.setLevelFolder(null);
				}
			}
			listReturn.add(item);
		}

		return listReturn;
	}

	public List<TemplateInfoDto> loadTemplateFromJson(String json) {
		if (json == null) {
			return Collections.emptyList();
		}

		List<TemplateInfoDto> listReturn = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			TemplateInfoDto temp = defaultGson.fromJson(jsonObject.toString(), TemplateInfoDto.class);
			listReturn.add(temp);
		}

		return listReturn;

	}

	public List<FolderDTO> loadFolderFromJson(String json) {
		if (json == null) {
			return Collections.emptyList();
		}

		List<FolderDTO> listReturn = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			FolderDTO temp = defaultGson.fromJson(jsonObject.toString(), FolderDTO.class);
			listReturn.add(temp);
		}

		return listReturn;
	}

	public List<LookupTable> loadLookuptableFromJson(String json) {
		List<LookupTable> listReturn = new ArrayList<>();

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			LookupTable look = defaultGson.fromJson(jsonObject.toString(), LookupTable.class);
			listReturn.add(look);
		}

		return listReturn;

	}

	public List<StructureBusiness> loadStructureBusinessFromJson(String json) {
		List<StructureBusiness> listReturn = new ArrayList<>();

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			StructureBusiness sb = defaultGson.fromJson(jsonObject.toString(), StructureBusiness.class);
			listReturn.add(sb);
		}

		return listReturn;
	}

	public List<ManagerRole> loadManagerRoleFromJson(String json) {
		List<ManagerRole> listReturn = new ArrayList<>();

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			ManagerRole mr = defaultGson.fromJson(jsonObject.toString(), ManagerRole.class);
			listReturn.add(mr);
		}

		return listReturn;
	}

	/**
	 * return EventListenerParam by Name
	 * 
	 * @param String
	 *            name
	 * @param ArrayList<EventListenerParam>
	 *            list
	 */
	public static EventListenerParam getEventsListenerParamByName(String name, ArrayList<EventListenerParam> list) {
		if (list != null && list.size() > 0) {
			for (EventListenerParam item : list) {
				if (name != null && name.equals(item.getEventId()))
					return item;
			}
		}

		return null;
	}

	/**
	 * load signatures for signatures group
	 * 
	 * @param ArrayList<SignatureGroupDto>
	 *            signGroupList
	 * @param List<SignsTemplateLibrary>
	 *            signTempLib
	 * @return ArrayList<SlugItem>
	 */
	public static ArrayList<SlugItem> loadSignsForSignGroup(ArrayList<SignatureGroupDto> signGroupList, List<SignsTemplateLibrary> signTempLib) {
		ArrayList<SlugItem> returnList = new ArrayList<>();
		if (signGroupList != null && signGroupList.size() > 0) {
			for (SignatureGroupDto signGroup : signGroupList) {
				if (signGroup.getSignatures() != null && signGroup.getSignatures().size() > 0) {
					for (SignatureFieldDto sign : signGroup.getSignatures()) {
						SlugItem item = new SlugItem();
						item.setSlug(sign.getSlug());
						item.setValue(sign.getValue());
						item.setPath(sign.getPath());
						item.setFlag(sign.getFlag());

						if (sign.getIdSingGroupTemplate() != null && signTempLib != null && signTempLib.size() > 0) {
							for (SignsTemplateLibrary itemSignLib : signTempLib) {
								if (itemSignLib.getNameSource() != null && itemSignLib.getNameSource().equals(sign.getValue())) {
									item.setId(itemSignLib.getId().toString());
								}
							}
						}

						returnList.add(item);
					}
				}
			}
		}

		return returnList;
	}

	/**
	 * get string from pattern path
	 * 
	 * @param String base
	 * @param Integer beginPos
	 * @param String separator
	 * @param Integer posTarget
	 * @return String
	 */
	public static String loadStringFromPattern(String base, String separatorNumber, String separator, Integer posTarget) 
	{
		try 
		{
			if (base != null && !base.equals("") && separatorNumber != null && separator != null && posTarget != null) 
			{
				base = base.replaceFirst(separatorNumber, UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
				String[] responseWithOutNumber = base.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
				
				if(responseWithOutNumber!=null && responseWithOutNumber.length>1)
				{				
					if(separator!=null && !separator.equals(""))
					{
						String[] response = responseWithOutNumber[1].split("\\" + separator);
		
						if (response.length > 0 && response.length >= posTarget)
							return response[posTarget];
					}
					else
					{
						return responseWithOutNumber[1]+"";
					}
				}
			}
		}
		catch (Exception e) 
		{
			return null;
		}

		return null;
	}

	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
	private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

	public static String toSlug(String input) {
		String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		slug = EDGESDHASHES.matcher(slug).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Generated User Details MassiveLoadUserDet
	 * 
	 * @param String
	 *            line
	 * @return MassiveLoadUserDet
	 */
	public static EventListenerCtrlProcess getUserDet(String line) {
		if (line != null && !line.equals("")) {
			String[] item = line.split(";");

			if (item.length > 0 && item[0] != null && !item[0].equals("")) {
				EventListenerCtrlProcess userDet = new EventListenerCtrlProcess();
				userDet.setCreateOn(new Date());
				userDet.setUserIdPpd(item[0]);
				userDet.setStatus(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode());

				userDet.setStartDatePpdOn(new Date());
				if (item.length > 1 && item[1] != null && !item[1].equals("")) {
					Date date = UtilDateTimeAdapter.getDateFromString("yyyy-MM-dd", item[1].toString());
					if (date != null)
						userDet.setStartDatePpdOn(date);
				}

				userDet.setSeqNumberPpd(UtilCodesEnum.CODE_DEFAULT_SEQ_EVENTLIS.getCode());
				if (item.length > 2 && item[2] != null && !item[2].equals("")) {
					userDet.setSeqNumberPpd(item[2].toString());
				}

				return userDet;
			}
		}

		return null;
	}

	public PpdCoreEmployeeInfoDto[] loadPpdCoreEmployeeInfoDto(String json) throws IOException {
		return defaultGson.fromJson(json, PpdCoreEmployeeInfoDto[].class);
	}

	public static String getLabelsAttachMassiveLoad(String value) {
		if (value != null && !value.equals("")) {
			value = value.replace(UtilCodesEnum.CODE_TYPE_MODULE_ONBOARDING.getCode(), "Onboarding");
			value = value.replace(UtilCodesEnum.CODE_TYPE_MODULE_RECRUITMENT.getCode(), "Recruitment");
			value = value.replace(UtilCodesEnum.CODE_TYPE_MODULE_EMPLOYEE_CENTER.getCode(), "Employee Center");
		} else {
			value = UtilCodesEnum.CODE_NA.getCode();
		}

		return value;
	}

	/**
	 * convert ArrayList<ArrayList<String>> tb to String Json
	 * 
	 * @param ArrayList<ArrayList<String>>
	 *            tb
	 * @return String
	 */
	public String getJsonFormTableQueryBuilder(ArrayList<ArrayList<String>> tb) {
		if (tb != null && tb.size() > 0) {
			return defaultGson.toJson(tb);
		}

		return "";
	}

	public String getResultBuilderDtoToJson(ArrayList<ArrayList<SlugItem>> source) {
		return defaultGson.toJson(source);
	}

	public String getResultSlugToJson(SlugItem source) {
		return defaultGson.toJson(source);
	}

	/**
	 * 
	 * */
	public String getTableTemplatePdd(ResultBuilderDto result) {
		SlugItem response = new SlugItem();
		ArrayList<ArrayList<SlugItem>> linesFinal = new ArrayList<ArrayList<SlugItem>>();
		ArrayList<SlugItem> itemsTable = new ArrayList<>();

		if (result != null && result.getResultArray() != null && result.getResultArray().size() > 0) {
			for (ArrayList resultArray : result.getResultArray()) {
				itemsTable = new ArrayList<>();
				for (int i = 0; i < resultArray.size(); i++) {
					if (resultArray.get(i) != null) {
						SlugItem item = new SlugItem();
						item.setSlug("column" + (i + 1));
						item.setValue(resultArray.get(i).toString());
						itemsTable.add(item);
					}
				}

				linesFinal.add(itemsTable);
			}

			String tmp = this.getResultBuilderDtoToJson(linesFinal);
			tmp = tmp.replaceAll(UtilCodesEnum.CODE_NEW_LINE_SEPARATOR.getCode(), "");
			return tmp;
		}

		return "";
	}

	public ArrayList<ArrayList<SlugItem>> getArrayFromJsonTableTemplatePdd(String source) {
		ArrayList<ArrayList<SlugItem>> response = new ArrayList<>();

		try {
			if (source != null && !source.equals("")) {
				JSONArray jsonArray = new JSONArray(source);
				if (jsonArray != null && jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						ArrayList<SlugItem> columnArray = new ArrayList<SlugItem>();
						JSONArray itemArray = jsonArray.getJSONArray(i);
						if (itemArray != null && itemArray.length() > 0) {
							for (int j = 0; j < itemArray.length(); j++) {
								JSONObject obj = itemArray.getJSONObject(j);
								if (obj != null) {
									SlugItem slugItem = new SlugItem(obj.get("slug").toString(), obj.get("value").toString());
									columnArray.add(slugItem);
								}
							}

							response.add(columnArray);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * @param String
	 *            code
	 */
	public static String getLabelEnumByCode(String code) {
		if (code != null && !code.equals("")) {
			if (code.equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode()))
				return UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getLabel();
			else if (code.equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode()))
				return UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getLabel();
			else if (code.equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING_PROCCESS_AGAIN.getCode()))
				return UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING_PROCCESS_AGAIN.getLabel();
		}
		return code;
	}

	/**
	 * load eventlistener ctrl register from history
	 * 
	 * @param ArrayList<EventListenerCtrlHistory>
	 *            histo
	 * @param String
	 *            paramAdminRetriesCode
	 * @return ArrayList<EventListenerCtrlProcess>
	 */
	public static ArrayList<EventListenerCtrlProcess> loadEventListeCrtlFormHisto(ArrayList<EventListenerCtrlHistory> histo, String paramAdminRetriesCode) {
		ArrayList<EventListenerCtrlProcess> newList = new ArrayList<>();

		if (histo != null) {
			for (EventListenerCtrlHistory item : histo) {
				EventListenerCtrlProcess itemEventCtrl = new EventListenerCtrlProcess();
				itemEventCtrl.setId(item.getIdOriginalEvent());
				itemEventCtrl.setEventLCtrlHistoryId(item.getId());
				itemEventCtrl.setStatus(item.getStatus());
				itemEventCtrl.setUserIdPpd(item.getUserIdPpd());
				itemEventCtrl.setObservations(item.getObservations());
				itemEventCtrl.setCreateOn(item.getCreateOn());
				itemEventCtrl.setUserCountry(item.getUserCountry());
				itemEventCtrl.setLastUpdateOn(item.getLastUpdateOn());
				itemEventCtrl.setRetries(item.getRetries());
				itemEventCtrl.setStartDatePpdOn(item.getStartDatePpdOn());
				itemEventCtrl.setIsEdit(false);

				// update retriest
				if (paramAdminRetriesCode != null && !paramAdminRetriesCode.equals("")) {
					itemEventCtrl.setRetriesInfo(item.getRetries() > 0 ? item.getRetries() + "/" + paramAdminRetriesCode : "");
				}

				newList.add(itemEventCtrl);
			}
		}

		return newList;
	}

	/**
	 * load list of event listener docs from history
	 * 
	 * @param ArrayList<EventListenerDocHistory>
	 *            histo
	 * @return ArrayList<EventListenerDocProcess>
	 */
	public static ArrayList<EventListenerDocProcess> loadEventListenerDocFormHisto(ArrayList<EventListenerDocHistory> histo) {
		ArrayList<EventListenerDocProcess> newList = new ArrayList<>();

		if (histo != null) {
			for (EventListenerDocHistory itemHisto : histo) {
				EventListenerDocProcess item = new EventListenerDocProcess();
				item.setId(itemHisto.getId());

				EventListenerCtrlProcess event = new EventListenerCtrlProcess();
				if (itemHisto.getEventListenerCtrlHisto() != null) {
					event.setId(itemHisto.getEventListenerCtrlHisto().getIdOriginalEvent());
				}

				item.setEventListenerCtrlProc(event);

				item.setCreateOn(itemHisto.getCreateOn());
				item.setLastUpdateOn(itemHisto.getLastUpdateOn());
				item.setStatus(itemHisto.getStatus());
				item.setStartDatePpdOnAttach(itemHisto.getStartDatePpdOnAttach());
				item.setRetries(itemHisto.getRetries());
				item.setObservations(itemHisto.getObservations());
				item.setUserIdPpd(itemHisto.getUserIdPpd());
				item.setAttachmentFileName(itemHisto.getAttachmentFileName());
				item.setUserCountry(itemHisto.getUserCountry());
				item.setIsEdit(false);
				newList.add(item);
			}
		}

		return newList;
	}

	/**
	 * File name format to be sent to ppd, example 1: FN@@DT@@PI, example 2: FN
	 * [FN]=File Name, [DT]=Document Type, [PI]=Person ID
	 * 
	 * @param String
	 *            format
	 * @param String
	 *            fileName
	 * @param String
	 *            DocumentType
	 * @param String
	 *            idPerson
	 */
	public static String getFileNameToSendPpd(String format, String fileName, String documentType, String idPerson) {
		String result = fileName + documentType + idPerson;
		try {
			if (format != null && !format.equals("")) {
				result = format.replace(UtilCodesEnum.CODE_TYPE_FILE_NAME.getCode(), fileName);
				result = result.replace(UtilCodesEnum.CODE_TYPE_FILE_DT.getCode(), documentType);
				result = result.replace(UtilCodesEnum.CODE_TYPE_FILE_PI.getCode(), idPerson);
				result = result.replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode(), " ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * get Actions to execute in fieldMapping
	 * 
	 * @param String
	 *            actions
	 * @param valueTarget
	 * @return List<SlugItem>
	 */
	public static List<SlugItem> loadActionsFieldMapping(String actionsValue, String valueTarget, String nameSource) {
		List<SlugItem> response = new ArrayList<SlugItem>();
		if (actionsValue != null && !actionsValue.equals("")) {
			String[] actionsList = actionsValue.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_SEMICOLON.getCode());
			if (actionsList != null && actionsList.length > 0) {
				for (int i = 0; i < actionsList.length; i++) {
					if (actionsList[i] != null && !actionsList[i].toString().equals("")) {
						String[] actionItem = actionsList[i].toString().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
						if (actionItem != null && actionItem.length > 1 && actionItem[0] != null && actionItem[1] != null) {
							SlugItem item = new SlugItem();
							item.setId(actionItem[0].toString());
							item.setCode(actionItem[1].toString());
							item.setValue(valueTarget);
							item.setLabel(nameSource);

							response.add(item);
						}
					}
				}
			}

			return response;
		}

		return null;
	}

	/**
	 * get status associated with status group name
	 * 
	 * @param String
	 *            groupName
	 * @return status
	 */
	public static String getListStatusForGroup(String groupName) {
		String status = "";
		if (groupName != null && groupName.equals(UtilCodesEnum.CODE_NA.getCode())) {
			return ("'NA'");
		} else if (groupName != null && !groupName.equals("") && !groupName.equals(UtilCodesEnum.CODE_NA.getCode())) {
			if ((groupName.contains("Errors") && groupName.contains("ErrorPpd") && groupName.contains("Pending") && groupName.contains("Successful")) || groupName.contains(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ALL.getCode()))
				return "";

			if (groupName.contains(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERROR.getCode()))
				status += "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES.getCode() + "',"
						+ "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_DOC_NOEXIST.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode() + "'," + "'"
						+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMINATE_EMP_ERROR_ATTACH.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TIMEOUT.getCode() + "',";

			if (groupName.contains(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_ERRORPPD.getCode()))
				status += "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode() + "',";

			if (groupName.contains(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_PENDING.getCode()))
				status += "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING_VALIDATE.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_PROCESSING.getCode() + "',"
						+ "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_SEARCHDOCS.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_PENDING_PROCCESS_AGAIN.getCode() + "'," + "'"
						+ UtilCodesEnum.CODE_STATUS_EVENTLIS_USERPENDINGMAIL.getCode() + "',";

			if (groupName.contains(UtilCodesEnum.CODE_STATUS_GROUP_EVENTLIS_SUCCESS.getCode()))
				status += "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode() + "'," + "'"
						+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTESENDTOSIGN.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY.getCode() + "'," + "'"
						+ UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEDOCCREATE.getCode() + "'," + "'" + UtilCodesEnum.CODE_STATUS_EVENTLIS_NOTPROCESS.getCode() + "',";

			return (status + "''");
		}

		return "";
	}

	/**
	 * @param String
	 *            headFilter
	 * @param List<String[]>
	 *            valuesList
	 * @return html row code
	 */
	public static String loadHtmlRow(String headFilter, List<Object[]> valuesList) {
		String response = "";
		if (valuesList != null && headFilter != null) {
			String[] headFilterList = headFilter.split(UtilCodesEnum.CODE_PARAM_SEPARATOR_COLON.getCode());
			for (Object[] item : valuesList) {
				if (item != null) {
					for (String itemStatus : headFilterList) {
						if ((itemStatus.replace("'", "")).equals(item[1].toString())) {
							response += getHtmlTableCode(item[1].toString(), item[0].toString(), "tr_info");
						}
					}
				}
			}
		}

		return response;
	}

	/**
	 * return html code
	 * 
	 * @param String
	 *            label
	 * @param String
	 *            value
	 * @param String
	 *            type
	 * @return html Code
	 */
	public static String getHtmlTableCode(String label, String value, String type) {
		String htmlCode = "";

		if (type.equals("tr_title")) {
			htmlCode = "<tr>\r\n<td colspan=\"2\" valign=\"top\" style=\"background:#66669c;padding:.75pt .75pt .75pt .75pt\">\r\n <p class=\"MsoNormal\"><b><span style=\"font-size:12.0pt;font-family:&quot;Arial Unicode MS&quot;,sans-serif;color:white\">"
					+ label + "<u></u><u></u></span></b></p>\r\n</td>\r\n</tr>";
		} else if (type.equals("tr_info")) {
			htmlCode = "<tr>\r\n" + "    <td valign=\"top\" style=\"background:#f0f0f0;padding:.75pt .75pt .75pt .75pt\">\r\n" + "       <div>\r\n"
					+ "          <p><b><span style=\"font-size:10.0pt;font-family:&quot;Arial Unicode MS&quot;,sans-serif\">\r\n" + "				" + label + "\r\n" + "			<u></u><u></u></span></b></p>\r\n" + "       </div>\r\n"
					+ "    </td>												                                 \r\n" + "    <td valign=\"top\" style=\"background:#f0f0f0;padding:.75pt .75pt .75pt .75pt\">\r\n" + "       <div>\r\n"
					+ "          <p class=\"MsoNormal\"><span style=\"font-size:10.0pt;font-family:&quot;Arial Unicode MS&quot;,sans-serif\"> " + value + "<u></u><u></u></span></p>\r\n" + "       </div>\r\n" + "    </td>\r\n" + " </tr>	";
		} else if (type.equals("tr_separator")) {
			htmlCode = "<tr>\r\n<td colspan=\"2\" valign=\"top\" style=\"background:#FFFFFF;padding:.75pt .75pt .75pt .75pt\">\r\n <p class=\"MsoNormal\"><b><span style=\"font-size:12.0pt;font-family:&quot;Arial Unicode MS&quot;,sans-serif;color:white\">"
					+ label + "<u></u><u></u></span></b></p>\r\n</td>\r\n</tr>";
		}

		return htmlCode;
	}

	/**
	 * @api {get} UtilMapping getObservationFormatFromString
	 * @apiName getCountryListFromList
	 * @apiDescription Format String to Observation. [update yyyy mm dd by xyz]
	 *                 [create 2019 02 14 by josed]
	 * @apiGroup UtilMapping
	 *
	 * @apiParam {UtilCodesEnum} errorEnum message code
	 * @apiParam {String} addInfo message o body observation
	 * 
	 * @apiSuccess {String} observation observation with format
	 * @apiSuccessExample {String} Success-Response: wwww
	 * @apiError (Error) {String} string same value in parameter addInfo.
	 */
	public static String getCountryListFromList(ArrayList<PSFStructureEmployeeDto> countrysList) {
		String response = "";
		if (countrysList != null && countrysList.size() > 0) {
			for (PSFStructureEmployeeDto item : countrysList) {
				if (item.getIsActiveStructureConfPSF() && !response.contains(item.getCodeStructure())) {
					response += item.getCodeStructure() + UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode();
				}
			}
		}

		if (!response.equals(""))
			return response;

		return null;
	}

	/**
	 * @api {get} UtilMapping getObservationFormatFromString
	 * @apiName getObservationFormatFromString
	 * @apiDescription Format String to Observation. [update yyyy mm dd by xyz]
	 *                 [create 2019 02 14 by josed]
	 * @apiGroup UtilMapping
	 *
	 * @apiParam {UtilCodesEnum} errorEnum message code
	 * @apiParam {String} addInfo message o body observation
	 * 
	 * @apiSuccess {String} observation observation with format
	 * @apiSuccessExample {String} Success-Response: wwww
	 * @apiError (Error) {String} string same value in parameter addInfo.
	 */
	public static String getObservationFormatFromString(UtilCodesEnum errorEnum, String addInfo) {
		String obser = UtilCodesEnum.CODE_STRING_INIT.getCode() + " " + addInfo + " " + UtilCodesEnum.CODE_STRING_END.getCode();
		return "";
	}

	/**
	 * 
	 * @param Json
	 * @return List<PpdTextOccurrencesDto>
	 */
	public List<PpdTextOccurrencesDto> loadPpdTextOccurrencesDtoFromJsom(String json) throws IOException {
		if (json == null) {
			return Collections.emptyList();
		}

		List<PpdTextOccurrencesDto> listReturn = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray(json);

		for (int i = 0; i < jsonarray.length(); i++) {
			jsonObject = jsonarray.getJSONObject(i);
			PpdTextOccurrencesDto item = defaultGson.fromJson(jsonObject.toString(), PpdTextOccurrencesDto.class);
			listReturn.add(item);
		}
		return listReturn;
	}

	/**
	 * load PpdGeneratePdfSignField from occurrences
	 * 
	 * @param String
	 *            labelSign
	 * @param List<PpdTextOccurrencesDto>
	 *            occurrencesList
	 * @return PpdGeneratePdfSignField
	 */
	public static PpdGeneratePdfSignField loadPpdSignerTypeInfoDtoFromOccurrences(String labelSign, List<PpdTextOccurrencesDto> occurrencesList) {
		PpdGeneratePdfSignField response = new PpdGeneratePdfSignField();

		if (occurrencesList != null) {
			DecimalFormat decimalFormat = new DecimalFormat("#");

			for (PpdTextOccurrencesDto item : occurrencesList) {
				if (labelSign != null && item.getKeyword() != null && labelSign.equals(item.getKeyword())) {
					response.setPage(item.getPage_number());
					response.setLlx(new Integer(decimalFormat.format(item.getLeft())));
					response.setLly(new Integer(decimalFormat.format(item.getTop())) + 50);
					response.setUrx(new Integer(decimalFormat.format(item.getLeft())) + 70);
					response.setUry(new Integer(decimalFormat.format(item.getTop())) + 70);

					return response;
				}
			}
		}

		return null;
	}
	
	/**
	 * Load List of messages for eventListener
	 * @param String observations
	 * return List<PSFMsgDto> 
	 * */
	public static List<PSFMsgDto> loadPSFMsgDtoFromObservations(String observations)
	{
		List<PSFMsgDto> response = new ArrayList<>();
		
		if(observations!=null && !observations.equals(""))
		{
			String[] obs = observations.split(UtilCodesEnum.CODE_STRING_END.getCode());
			
			if(obs!=null)
			{
				for(String message:obs)
				{
					PSFMsgDto itemMsg = new PSFMsgDto();
					itemMsg.setMessage(message);
					response.add(itemMsg);
				}
			}
		}
		
		return response;
	}
}