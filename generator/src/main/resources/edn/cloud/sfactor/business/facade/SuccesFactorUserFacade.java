package edn.cloud.sfactor.business.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import edn.cloud.business.api.util.UtilCodesApiEnum;
import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.GenErrorInfoDto;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.integration.UserInfo;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeCtrInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeOrgInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreUserInfoDto;
import edn.cloud.business.dto.ppd.api.PpdProfilesInfoDto;
import edn.cloud.business.dto.ppd.api.PpdUserDto;
import edn.cloud.business.dto.ppd.api.PpdUserFieldFilters;
import edn.cloud.business.dto.ppd.api.PpdUserPerimeterBaseDto;
import edn.cloud.business.dto.psf.PSFStructureEmployeeDto;
import edn.cloud.ppdoc.business.facade.PpdApiUtilsFacade;
import edn.cloud.ppdoc.business.facade.PpdEmployeeApiFacade;
import edn.cloud.ppdoc.business.facade.PpdEmployeeUtilFacade;
import edn.cloud.ppdoc.business.impl.PpdEmployeeApiImpl;
import edn.cloud.ppdoc.business.interfaces.PpdEmployeeApi;
import edn.cloud.sfactor.business.impl.SuccessFactorImpl;
import edn.cloud.sfactor.business.interfaces.SuccessFactor;
import edn.cloud.sfactor.business.utils.QueryBuilder;
import edn.cloud.sfactor.business.utils.StructureBuilder;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Countries;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;

public class SuccesFactorUserFacade {
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	private PpdEmployeeApi ppdEmployeeUtils = new PpdEmployeeApiImpl();
	private SuccessFactor successFactor = new SuccessFactorImpl();
	private final UtilLogger logger = UtilLogger.getInstance();

	// -----------------------------------------------------------------------------------
	// Employe

	/**
	 * validate country user
	 * 
	 * @param String
	 *            userId
	 * @param String
	 *            effectiveDate
	 * @param List<Countries>
	 *            listCountriesConf
	 * @param String
	 *            concReturn
	 * @param Boolean
	 *            onlyStructureActive
	 * @return GenErrorInfoDto
	 */
	public GenErrorInfoDto employeeGetTimeZone(String userId, String effectiveDate, List<Countries> listCountriesConf, String concReturn, Boolean onlyStructureActive) {
		GenErrorInfoDto response = new GenErrorInfoDto();
		response.setFlagAux(false);// allowed process attachments
		AdminParameters paramAdmStructure = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STRUCTURE_KEY.getCode());

		StructureBuilder structureBuilder = new StructureBuilder();
		ArrayList<PpdCoreEmployeeCtrInfoDto> structure = structureBuilder.getCountry(userId, paramAdmStructure.getValue(), effectiveDate, false, concReturn);

		if (structure != null) {
			String countryResume = "";
			for (PpdCoreEmployeeCtrInfoDto org : structure) {
				if (org.getOrganization_code() != null) {
					countryResume += org.getCountry_id() + ",";
					if (listCountriesConf != null) {
						// validate if at least one structure allows processing attachments
						for (Countries countryConf : listCountriesConf) {
							if (countryConf.getActive() && countryConf.getToProcessUser() && org.getCountry_id().equals(countryConf.getCode())) {
								response.setCode(countryConf.getTimeZoneId());
							}
						}
					}
				}
			}

			response.setMessage("Rejected. Structure User without configuration: (" + countryResume + ")");
		} else {
			response.setMessage("Rejected. Structure not found for the employee or employee No-Show : " + userId);
		}

		return response;
	}
	
	
	/**
	 * get list of country asociated with a employee
	 * @param String userId 
	 * @param ArrayList<PpdCoreEmployeeCtrInfoDto> structure,
	 * @param List<Countries> listCountriesConf 
	 * @param Boolean onlyStructureActive
	 * @return ArrayList<GenErrorInfoDto>
	 * */
	private ArrayList<PSFStructureEmployeeDto> actionApplyFiltersStructureOnEmployeeOrga(String userId,
															 ArrayList<PpdCoreEmployeeCtrInfoDto> structure,
															 List<Countries> listCountriesConf,
															 Boolean onlyStructureActive)
	{
		ArrayList<PSFStructureEmployeeDto> responseList = new ArrayList<>();
		StructureBuilder structureBuilder = new StructureBuilder();
		PSFStructureEmployeeDto response = new PSFStructureEmployeeDto();
		response.setIsActiveStructureConfPSF(false);// allowed process attachments

		if (structure != null) 
		{
			String countryResume = "";
			for (PpdCoreEmployeeCtrInfoDto org : structure) 
			{
				if (org.getOrganization_code() != null) {
					countryResume += org.getCountry_id() + ",";
					if (listCountriesConf != null) {
						// validate if at least one structure allows processing attachments
						for (Countries countryConf : listCountriesConf) {
							if (countryConf.getActive() && countryConf.getToProcessUser() && org.getCountry_id().equals(countryConf.getCode()) && countryConf.getProcessAttach() != null && countryConf.getProcessAttach()) {
								response.setIsAllowAttachConfPSF(true);
							}
						}

						for (Countries countryConf : listCountriesConf) {
							if (countryConf.getActive() && countryConf.getToProcessUser() && org.getCountry_id().equals(countryConf.getCode())) {
								response.setIsActiveStructureConfPSF(true);
								response.setParametersConfPSF((countryConf.getParameters() != null ? countryConf.getParameters() : ""));
								response.setIsActiveSF(org.getActive());
								response.setCodeStructure(org.getCountry_id());
								response.setEntryDateEmployee(org.getEntry_date()!=null?org.getEntry_date():"");

								if (onlyStructureActive) {
									if (org.getActive() != null && org.getActive().equals(UtilCodesEnum.CODE_TRUE.getCode())) {
										responseList.add(response);
										return responseList;
									}

									countryResume += " isActive in SF: false Only load structure active. ";
								} else {
									responseList.add(response);									
								}
							}
						}
					}
				}
			}
			
			if(responseList.size()>0)
				return responseList;

			response.setMessage("Rejected. Structure User without configuration, country: (" + countryResume + ")");
			
			
		} else {
			response.setMessage("Rejected. Structure not found for the employee or employee No-Show : " + userId);
		}
			
		response.setIsActiveStructureConfPSF(false);
		responseList.add(response);						
		return responseList;

	}
	
	/**
	 * validate country user
	 * 
	 * @param String
	 *            userId
	 * @param String
	 *            effectiveDate
	 * @param List<Countries>
	 *            listCountriesConf
	 * @param String
	 *            concReturn
	 * @param Boolean
	 *            onlyStructureActive
	 * @return ArrayList<GenErrorInfoDto>
	 */
	public ArrayList<PSFStructureEmployeeDto> actionValidateStructureActive(String userId, String effectiveDate, List<Countries> listCountriesConf, String concReturn, Boolean onlyStructureActive) 
	{
		ArrayList<PSFStructureEmployeeDto> responseList = new ArrayList<>();
		
		PSFStructureEmployeeDto response = new PSFStructureEmployeeDto();
		response.setIsActiveStructureConfPSF(false);// allowed process attachments
		AdminParameters paramAdmStructure = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STRUCTURE_KEY.getCode());
		
		if(paramAdmStructure!=null)
		{
			StructureBuilder structureBuilder = new StructureBuilder();
			ArrayList<PpdCoreEmployeeCtrInfoDto> structure = structureBuilder.getCountry(userId, paramAdmStructure.getValue(), effectiveDate, false, concReturn);
	
			responseList = this.actionApplyFiltersStructureOnEmployeeOrga(userId, structure, listCountriesConf, onlyStructureActive);
			
			if(responseList!=null && responseList.size()>0){	
				return responseList;
			}
			else
			{
				responseList = new ArrayList<>();
				response.setIsActiveStructureConfPSF(false);
				response.setIsAllowAttachConfPSF(false);
				response.setCodeStructure("");		
				response.setMessage("Employee Country List not exist");
				responseList.add(response);
			}
		}
		else
		{
			responseList = new ArrayList<>();
			response.setIsActiveStructureConfPSF(false);
			response.setIsAllowAttachConfPSF(false);
			response.setCodeStructure("");		
			response.setMessage("Employee Country List not exist");
			responseList.add(response);
		}
		
		return responseList;		
	}

	/**
	 * update Organization of employee
	 * 
	 * @param String userId
	 * @param String effectiveDate
	 * @param List<Countries> listCountriesConf

	 * @return GenErrorInfoDto
	 */
	public GenErrorInfoDto actionUpdateOrganitationToEmployee(String userId, String effectiveDate, List<Countries> listCountriesConf) 
	{
		GenErrorInfoDto response = new GenErrorInfoDto();
		response.setMessage("UpdateRegistrationReferences:false");
		
		try
		{	
			String concReturn = UtilCodesEnum.CODE_FORMAT_STRUCTURE_USER.getCode();
			AdminParameters paramAdmUserid = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_USERID.getCode());
			if (paramAdmUserid != null) {
				concReturn = paramAdmUserid.getValue();
			}
			
			ArrayList<PpdCoreEmployeeCtrInfoDto> employeeCtrInfoList = StructureBuilder.getInstance().getFullOrga(userId, "companyNav/country|territoryCode,territoryName|iso2", effectiveDate, true, concReturn);
			ArrayList<PpdCoreEmployeeOrgInfoDto> orgaListFinal = new ArrayList<>();
	
			if (listCountriesConf != null && employeeCtrInfoList != null && employeeCtrInfoList.size() > 0) {
				// validate if at least one structure allows processing attachments
				for (Countries countryConf : listCountriesConf) {
					for (int i = 0; i < employeeCtrInfoList.size(); i++) {
						if (countryConf.getActive() && countryConf.getToProcessUser() && employeeCtrInfoList.get(i).getCountry_id().equals(countryConf.getCode())) {
							PpdCoreEmployeeOrgInfoDto org = new PpdCoreEmployeeOrgInfoDto();
							org.setActive(employeeCtrInfoList.get(i).getActive());
							org.setDeparture_date(employeeCtrInfoList.get(i).getDeparture_date());
							org.setEmployee_number(employeeCtrInfoList.get(i).getEmployee_number());
							org.setOrganization_id(employeeCtrInfoList.get(i).getOrganization_id());
							orgaListFinal.add(org);
						}
					}
				}
			}
	
			PpdCoreEmployeeInfoDto employeeUpdate = new PpdCoreEmployeeInfoDto();
			if (orgaListFinal != null && orgaListFinal.size() > 0) {
				PpdEmployeeApiFacade ppdEmployeeF = new PpdEmployeeApiFacade();
				PpdApiUtilsFacade ppdApiUtilsF = new PpdApiUtilsFacade();
				String resumeErrors = "";
				employeeUpdate.setRegistration_references(orgaListFinal);
	
				PpdCoreEmployeeInfoDto[] coreEmplSearch = ppdApiUtilsF.wServiceSearchEmployee(userId);
				if (coreEmplSearch != null && coreEmplSearch.length > 0) {
					employeeUpdate.setId(coreEmplSearch[0].getId());
					logger.gson(employeeUpdate);
					employeeUpdate = ppdEmployeeF.actionUpdatePatchEmployee(employeeUpdate);
	
					if (employeeUpdate != null && employeeUpdate.getErrors() != null && employeeUpdate.getErrors().length > 0) {
						for (int i = 0; i < employeeUpdate.getErrors().length; i++) {
							if (employeeUpdate.getErrors()[i] != null) {
								resumeErrors += employeeUpdate.getErrors()[i].getCode() + " " + employeeUpdate.getErrors()[i].getField() + " " + employeeUpdate.getErrors()[i].getMessage() + ": ";
							}
						}
					}
	
					response.setMessage("UpdateRegistrationReferences:true, " + resumeErrors);
				} else {
					response.setMessage("UpdateRegistrationReferences:false:NoExistUserPpd");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			response.setMessage("UpdateRegistrationReferences:error, " + e.getMessage());
		}

		return response;
	}

	// -----------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------
	// User

	/**
	 * Event Update user in Ppd
	 * 
	 * @param String userIdPpd,
	 * @param Boolean isIndiContrToManager,
	 * @param Boolean isManagerToIndiContr,
	 * @param String paramManagerRole,
	 * @param String paramManagerOrga,
	 * @param String paramPrefixUserPpd,
	 * @param String fielMappingName
	 */
	public ResponseGenericDto userPpdUpdate(String userIdPpd, 
											Boolean isIndiContrToManager, 
											Boolean isManagerToIndiContr, 
											String paramManagerRole, 
											String paramManagerOrga,
											String paramPrefixUserPpd,
											String fielMappingName) 
	{
		ResponseGenericDto response = new ResponseGenericDto();

		try 
		{
			UserInfo user = successFactor.getUserInfoProfile(userIdPpd);
			
			if(user!=null)
			{
				PpdCoreUserInfoDto ppdUserCore = null;
				PpdEmployeeApiFacade ppdApi = new PpdEmployeeApiFacade();
				PpdApiUtilsFacade ppdUser = new PpdApiUtilsFacade();
				Gson gson = new Gson();
	
				//PPDocUserDto[] ppdUserDto = ppdApi.wServiceGetEmployee(userIdPpd);
	
				String jUser = "";
				//if (ppdUserDto != null && ppdUserDto.length != 0 && !ppdUserDto[0].equals(null) && !ppdUserDto[0].email.equals(null) && !ppdUserDto[0].email.equals("")) {
					ppdUserCore = ppdEmployeeUtils.wServiceCallCoreUser(paramPrefixUserPpd, userIdPpd);
				//}
	
				if (isIndiContrToManager) 
				{
					if (ppdUserCore != null) 
					{
						ppdUserCore.setProfiles(this.userChangeToManager(userIdPpd, ppdUserCore.getProfiles(), paramManagerRole, paramManagerOrga, fielMappingName));
						
						if(user!=null && user.getUsername()!=null && !user.getUsername().equals("")) {
							ppdUserCore.setSaml_token(user.getUsername());
						}
						
						String jsonUser = gson.toJson(ppdUserCore);
						response = ppdEmployeeUtils.wServicePutCoreUser(ppdUserCore.getId(), StringEscapeUtils.unescapeJava(jsonUser));
					} else 
					{
						ppdUserCore = this.createUserPpd(user,userIdPpd, paramManagerRole, paramManagerOrga,paramPrefixUserPpd, fielMappingName);
						
						if(user!=null && user.getUsername()!=null && !user.getUsername().equals("")) {
							ppdUserCore.setSaml_token(user.getUsername());
						}
						
						String jsonUser = gson.toJson(ppdUserCore);
						response = ppdEmployeeUtils.wServicePostCoreUser(StringEscapeUtils.unescapeJava(jsonUser));
					}
				} else if (isManagerToIndiContr) {
	
					if (ppdUserCore != null) 
					{
						ppdUserCore.setProfiles(this.changeToIndividual(ppdUserCore.getProfiles(), paramManagerRole));
						
						if(user!=null && user.getUsername()!=null && !user.getUsername().equals("")) {
							ppdUserCore.setSaml_token(user.getUsername());
						}
						
						String jsonUser = gson.toJson(ppdUserCore);
						response = ppdEmployeeUtils.wServicePutCoreUser(ppdUserCore.getId(), StringEscapeUtils.unescapeJava(jsonUser));
					}
				}
			}
			else
			{
				response.setCode(UtilCodesApiEnum.CODE_ERROR_BAD_REQUEST.getCode());
				response.setMessage(UtilCodesApiEnum.CODE_ERROR_BAD_REQUEST.getMessage()+": user information in SF ("+userIdPpd+") ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(UtilCodesEnum.CODE_ERROR.getCode());
			response.setMessage(e.getMessage());
		}

		return response;
	}

	/**
	 * Remove Manager Role
	 * 
	 * @param list
	 *            List<PpdProfilesInfoDto>
	 * @param parameter
	 *            AdminParameters
	 * @return List<PpdProfilesInfoDto>
	 */
	private List<PpdProfilesInfoDto> changeToIndividual(List<PpdProfilesInfoDto> list, String role) {

		List<PpdProfilesInfoDto> listReturn = new ArrayList<>();
		for (PpdProfilesInfoDto profile : list) {
			if (!profile.getRole_id().equals(role)) {
				listReturn.add(profile);
			}
		}

		return listReturn;
	}

	/**
	 * Create Core User Ppd
	 * 
	 * @param UserInfo user
	 * @param userId
	 * @param String role
	 * @param String orga
	 * @param String paramPrefixUserPpd
	 * @param String fielMappingName
	 * @return PpdCoreUserInfoDto
	 */
	private PpdCoreUserInfoDto createUserPpd(UserInfo user,String userId, String role, String orga, String paramPrefixUserPpd, String fielMappingName) 
	{		
		PpdCoreUserInfoDto ppdUser = new PpdCoreUserInfoDto();

		if (user != null) {

			// get email format
			SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
			AdminParameters paramAdminEmailFormatTermi = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_FORMAT_MAILUSERTERM_PPD.getCode());
			// transform email format
			String out = "";
			if (user.getEmail() == null || user.getEmail().contains("nomail") || user.getEmail().contains("sapecimplementation") || user.getEmail().contains("noemail") || user.getEmail().equals("")) {
				out = "noemail_" + userId + "@test.com";
				if (paramAdminEmailFormatTermi != null && paramAdminEmailFormatTermi.getValue() != null && !paramAdminEmailFormatTermi.getValue().equals("")) {
					out = paramAdminEmailFormatTermi.getValue().replace("??", userId);
				}
			}else {
				out = user.getEmail();
			}

			ppdUser.setEmail(out);
			ppdUser.setExternal_id(paramPrefixUserPpd.replace("@@", userId));
			ppdUser.setLastname(user.getLastName());
			ppdUser.setFirstname(user.getFirstName());

			PpdProfilesInfoDto ppdProfile = new PpdProfilesInfoDto();
			ppdProfile.setRole_id(role);

			PpdUserPerimeterBaseDto empPerimeter = new PpdUserPerimeterBaseDto();
			empPerimeter.setOperator(UtilCodesEnum.CODE_ROLE_MAPPING_OPERATOR_2.getCode());
			empPerimeter.setOrganization_id(orga);

			// add filters
			List<PpdUserFieldFilters> userFieldFilters = new ArrayList<PpdUserFieldFilters>();
			PpdUserFieldFilters ppdUserFieldFilter = new PpdUserFieldFilters();
			ppdUserFieldFilter.setCustom_field_id(fielMappingName);
			ppdUserFieldFilter.setOperator(UtilCodesEnum.CODE_ROLE_MAPPING_OPERATOR_1.getCode());
			ppdUserFieldFilter.setValue(userId);
			userFieldFilters.add(ppdUserFieldFilter);

			empPerimeter.setCustom_field_filters(userFieldFilters);
			ppdProfile.setEmployees_perimeter(empPerimeter);

			List<PpdProfilesInfoDto> newProfile = new ArrayList<>();
			newProfile.add(ppdProfile);
			ppdUser.setProfiles(newProfile);
		}

		logger.gson(ppdUser);
		return ppdUser;

	}

	/**
	 * Change Profile Manager
	 * 
	 * @param list
	 *            List<PpdProfilesInfoDto>
	 * @param String
	 *            role
	 * @param String
	 *            orga
	 * @param String
	 *            fielMappingName
	 * @return List<PpdProfilesInfoDto>
	 */
	private List<PpdProfilesInfoDto> userChangeToManager(String userId, List<PpdProfilesInfoDto> list, String role, String orga, String fielMappingName) {
		for (PpdProfilesInfoDto profile : list) {
			profile.setId(null);
			if (profile.getRole_id().equals(role)) {
				// validate filter
				if (profile.getEmployees_perimeter() != null) {
					if (profile.getEmployees_perimeter().getCustom_field_filters() != null) {
						for (PpdUserFieldFilters filter : profile.getEmployees_perimeter().getCustom_field_filters()) {
							if (filter.getCustom_field_id().equals(fielMappingName)) {
								return list;
							}
						}

						PpdUserFieldFilters ppdUserFieldFilter = new PpdUserFieldFilters();
						ppdUserFieldFilter.setCustom_field_id(fielMappingName);
						ppdUserFieldFilter.setOperator(UtilCodesEnum.CODE_ROLE_MAPPING_OPERATOR_1.getCode());
						ppdUserFieldFilter.setValue(userId);
						profile.getEmployees_perimeter().getCustom_field_filters().add(ppdUserFieldFilter);
						logger.gson(profile);
					} else {
						// add filters
						List<PpdUserFieldFilters> userFieldFilters = new ArrayList<PpdUserFieldFilters>();
						PpdUserFieldFilters ppdUserFieldFilter = new PpdUserFieldFilters();
						ppdUserFieldFilter.setCustom_field_id(fielMappingName);
						ppdUserFieldFilter.setOperator(UtilCodesEnum.CODE_ROLE_MAPPING_OPERATOR_1.getCode());
						ppdUserFieldFilter.setValue(userId);
						userFieldFilters.add(ppdUserFieldFilter);
						profile.getEmployees_perimeter().setCustom_field_filters(userFieldFilters);
						logger.gson(profile);
					}
				}

				return list;
			}
		}

		PpdProfilesInfoDto profile = new PpdProfilesInfoDto();
		profile.setRole_id(role);
		
		PpdUserPerimeterBaseDto empPerimeter = new PpdUserPerimeterBaseDto();
		empPerimeter.setOrganization_id(orga);
		empPerimeter.setOperator(UtilCodesEnum.CODE_ROLE_MAPPING_OPERATOR_2.getCode());

		// add filters
		List<PpdUserFieldFilters> userFieldFilters = new ArrayList<PpdUserFieldFilters>();
		PpdUserFieldFilters ppdUserFieldFilter = new PpdUserFieldFilters();
		ppdUserFieldFilter.setCustom_field_id(fielMappingName);
		ppdUserFieldFilter.setOperator(UtilCodesEnum.CODE_ROLE_MAPPING_OPERATOR_1.getCode());
		ppdUserFieldFilter.setValue(userId);
		userFieldFilters.add(ppdUserFieldFilter);
		empPerimeter.setCustom_field_filters(userFieldFilters);

		profile.setEmployees_perimeter(empPerimeter);
		list.add(profile);
		return list;
	}

	/**
	 * execute all actions related with users or employees
	 * 
	 * @param List<SlugItem> actionsList
	 * @param String orga
	 * @param String paramPrefixUserPpd
	 * @return GenErrorInfoDto
	 */
	public GenErrorInfoDto userEmployeeExecuteActions(List<SlugItem> actionsList, String orga, String paramPrefixUserPpd) {
		GenErrorInfoDto response = new GenErrorInfoDto();
		String observations = "";

		if (actionsList != null) {
			for (SlugItem actionItem : actionsList) {
				try {
					// actions update profile to user
					if (actionItem != null && actionItem.getId() != null && actionItem.getId().equals(UtilCodesEnum.CODE_ACTIONS_ACT1_UPDATE_PROFILEUSER.getCode())) {
						ResponseGenericDto responseUser = this.userPpdUpdate(actionItem.getValueToStr(), true, false, actionItem.getCode(), orga,paramPrefixUserPpd, actionItem.getLabel());

						if (responseUser != null && responseUser.getCode()!= null) {
							if (responseUser.getCode().equals(UtilCodesEnum.CODE_OK.getCode())) {
								observations += UtilCodesEnum.CODE_STRING_INIT.getCode() + " " + UtilCodesEnum.CODE_OK.getCode() + " action: " + actionItem.getId() + " user: " + actionItem.getValueToStr() + " profile: "
										+ actionItem.getCode() + " response: " + responseUser.getMessage() + " " + UtilCodesEnum.CODE_STRING_END.getCode();
							} else if (responseUser.getCode().equals(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORPPD.getCode())) {
								observations += UtilCodesEnum.CODE_STRING_INIT.getCode() + " " + UtilCodesEnum.CODE_ERROR.getCode() + " action: " + actionItem.getId() + " user: " + actionItem.getValueToStr() + " profile: "
										+ actionItem.getCode() + " response: " + responseUser.getMessage() + " " + UtilCodesEnum.CODE_STRING_END.getCode();
								;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					observations += UtilCodesEnum.CODE_ERROR.getCode() + " " + e.getMessage();
				}
			}
		}

		response.setMessage(observations);
		return response;
	}
	
	
	/**
	 * build user Dto Ppd userd to update or create employee in PPd
	 * @param String userId
	 * @param String startDate
	 * @return PpdCoreEmployeeInfoDto
	 * */
	public PpdCoreEmployeeInfoDto userPpdBuildDtoTest(String userId,String startDate) 
	{
		PpdCoreEmployeeInfoDto employeeUpdate = new PpdCoreEmployeeInfoDto();
		PpdEmployeeUtilFacade ppdUserUtilF = new PpdEmployeeUtilFacade();
		ArrayList<FieldsMappingPpd> fieldsUserList = successFactorAdmin.mappingPpdFieldsGetAll(null, UtilCodesEnum.CODE_NA.getCode(), Boolean.TRUE, Boolean.FALSE);
		AdminParameters paramAdmStructure = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_STRUCTURE_KEY.getCode());		
		AdminParameters paramAdmUserid = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_USERID.getCode());
		
		if(startDate==null)
			startDate = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(),new Date());
			
		employeeUpdate = ppdUserUtilF.actionUserGetValueQueryBuilder(userId, fieldsUserList, paramAdmStructure, paramAdmUserid, true,startDate);
		employeeUpdate = UtilMapping.emptyPddEmployeeCreateVCore(employeeUpdate);
		return employeeUpdate;
	}

}
