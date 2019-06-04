package edn.cloud.ppdoc.business.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeOrgInfoDto;
import edn.cloud.sfactor.business.facade.SuccessFactorEventLFacade;
import edn.cloud.sfactor.business.utils.QueryBuilder;
import edn.cloud.sfactor.business.utils.StructureBuilder;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;

public class PpdEmployeeUtilFacade 
{
	private UtilLogger logger = UtilLogger.getInstance();	
	
	/**
	 * 
	 * fill values with successfactor information for User Mapping Field
	 * 
	 * @param String idUser
	 * @param ArrayList<FieldsMappingPpd> fieldsUserList
	 * @param AdminParameters paramAdmStructure
	 * @param Boolean isShowErr
	 */
	public PpdCoreEmployeeInfoDto actionUserGetValueQueryBuilder(String idUser,
																ArrayList<FieldsMappingPpd> fieldsUserList,
																AdminParameters paramAdmStructure,
																AdminParameters paramAdmUserid,
																Boolean isShowError,																 
																String effectiveDate) 
	{
		int countError = 0;
		int countFilters = 0;
		PpdCoreEmployeeInfoDto response = new PpdCoreEmployeeInfoDto();
		HashMap<String, String> filtersList = new HashMap<String, String>();
		Boolean isError = false;

		Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();
		
		// ------------------------------------------------------
		// logic filter fiels
		for (FieldsMappingPpd field : fieldsUserList) {
			if (field.getIsFilter())
				countFilters++;
		}

		if (countFilters > 0) {
			filtersList = new HashMap<String, String>();
			countFilters = 0;
		}
		// ------------------------------------------------------

		try {
			GenResponseInfoDto[] errorList = new GenResponseInfoDto[fieldsUserList.size()];
			for (FieldsMappingPpd field : fieldsUserList) {
				if (field.getNameDestination() != null && !field.getNameDestination().equals("")) {
					if (field.getIsAttached() == false && field.getIsConstants() == false && field.getIsFilter() == false) {
						map.put(field.getNameSource(), new ResultBuilderDto(field.getNameDestination(), "default", ""));
					} else if (field.getIsConstants() && !field.getIsAttached()) {
						if (field.getNameDestination() != null && !field.getNameDestination().equals("")) 
						{
							response = UtilMapping.loadPpdCreateEmployeeFromKey(response, field.getNameSource(), field.getNameDestination());					
						} else if (field.getIsObligatory()) 
						{
							isError = true;
							GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
							ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
							ppdErrorInfoDto.setField(field.getNameSource());
							ppdErrorInfoDto.setMessage("Invalid Result,field is obligatory");
							errorList[countError] = ppdErrorInfoDto;
							countError++;
						}
					} 
					else if (field.getIsFilter()) 
					{
						if (!field.getIsConstants()) {
							map.put(field.getNameSource(), new ResultBuilderDto(field.getNameDestination(), "default", ""));
						}
						filtersList.put(field.getNameSource(), field.getNameDestination());
						countFilters++;
					}
				} else if (isShowError && !field.getIsAttached())// show error
				{
					isError = true;
					GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
					ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
					ppdErrorInfoDto.setField(field.getNameSource());
					ppdErrorInfoDto.setMessage("No Path");
					errorList[countError] = ppdErrorInfoDto;
					countError++;
				}
			}

			// data builder successFactorUser
			if (map.size() > 0) {
				Map<String, ResultBuilderDto> dataMap = QueryBuilder.getInstance().convert(map, idUser, effectiveDate);
				
				if (dataMap != null && dataMap.size() > 0) 
				{
					//define list of actions to do
					List<SlugItem> actionsListFinal = new ArrayList<SlugItem>();
					
					for (FieldsMappingPpd field : fieldsUserList) 
					{
						if (field.getNameDestination() != null && !field.getNameDestination().equals("")) 
						{
							// field query to success factor
							if (field.getIsAttached() == false && field.getIsConstants() == false && field.getIsFilter() == false) 
							{
								// find result from query builder
								if (dataMap.get(field.getNameSource()) != null) 
								{
									ResultBuilderDto resultBuilder = (ResultBuilderDto) dataMap.get(field.getNameSource());

									if (resultBuilder.getResult() != null && !resultBuilder.getResult().equals("") && !resultBuilder.getResult().equals(UtilCodesEnum.CODE_QUERYBUILDER_INVALID.getCode())) 
									{	
										response = UtilMapping.loadPpdCreateEmployeeFromKey(response, field.getNameSource(), resultBuilder.getResult());
										
										
										//add to the list of actions
										List<SlugItem> listActions = field.getActionsList(resultBuilder.getResult());
										if(listActions!=null)
											actionsListFinal.addAll(listActions);

										if (response.getRegistration_references() != null && response.getRegistration_references().size() > 0) {
											// ----------------------------------------------------------------
											// load info structure set Organization_id and employee_number
											if((response.getRegistration_references().get(0).getOrganization_code() == null || response.getRegistration_references().get(0).getOrganization_code().equals(""))
													&& (response.getRegistration_references().get(0).getOrganization_id() == null || response.getRegistration_references().get(0).getOrganization_id().equals("")))
											{
												
												if (paramAdmStructure != null && paramAdmStructure.getValue() != null) {
													// load structure
													StructureBuilder structureBuilder = new StructureBuilder();
													String concReturn = "STRUCTURE_USERID";
													if (paramAdmUserid != null) {
														concReturn = paramAdmUserid.getValue();
													}
													
													ArrayList<PpdCoreEmployeeOrgInfoDto> structure = structureBuilder.getStructure(idUser, paramAdmStructure.getValue(), effectiveDate,true,concReturn);

													if (structure != null) 
													{
														response.setRegistration_references(structure);
													}
													else {
														isError = true;
														GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
														ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
														ppdErrorInfoDto.setField(field.getNameSource());
														ppdErrorInfoDto.setMessage("Structure is null ");
														errorList[countError] = ppdErrorInfoDto;
														countError++;
													}
												} else {
													// fill no more calls
													response.getRegistration_references().get(0).setOrganization_code("Error");

													isError = true;
													GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
													ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
													ppdErrorInfoDto.setField(field.getNameSource());
													ppdErrorInfoDto.setMessage("Level Structura Admin parameter is null");
													errorList[countError] = ppdErrorInfoDto;
													countError++;
												}
											}
											// ------------------------------------------------------------
										} else {
											isError = true;
											GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
											ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
											ppdErrorInfoDto.setField(field.getNameSource());
											ppdErrorInfoDto.setMessage("organization structure in Mapping Field is null");
											errorList[countError] = ppdErrorInfoDto;
											countError++;
										}
									} else if (field.getIsObligatory()) {
										isError = true;
										GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
										ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
										ppdErrorInfoDto.setField(field.getNameSource());
										ppdErrorInfoDto.setMessage("Invalid Result,field is obligatory");
										errorList[countError] = ppdErrorInfoDto;
										countError++;
									}
								} else {
									isError = true;
									GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
									ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
									ppdErrorInfoDto.setField(field.getNameSource());
									ppdErrorInfoDto.setMessage("No Data Result");
									errorList[countError] = ppdErrorInfoDto;
									countError++;
								}
							} else if (field.getIsFilter() == true && field.getIsConstants() == false) {

								// find result from query builder
								if (dataMap.get(field.getNameSource()) != null) {
									ResultBuilderDto resultBuilder = (ResultBuilderDto) dataMap.get(field.getNameSource());

									if (resultBuilder.getResult() != null && !resultBuilder.getResult().equals(UtilCodesEnum.CODE_QUERYBUILDER_INVALID.getCode())) 
									{
										filtersList.put(field.getNameSource(), resultBuilder.getResult());
										
										List<SlugItem> listActions = field.getActionsList(resultBuilder.getResult());
										if(listActions!=null)
											actionsListFinal.addAll(listActions);

									}
									else {
										isError = true;
										GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
										ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
										ppdErrorInfoDto.setField(field.getNameSource());
										ppdErrorInfoDto.setMessage("Error in field/type filter, value not found");
										errorList[countError] = ppdErrorInfoDto;
										countError++;
									}
								}
							} else if (field.getIsFilter() == true && field.getIsConstants() == true) {

							}
						} else if (field.getIsObligatory()) {
							isError = true;
							GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
							ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
							ppdErrorInfoDto.setField(field.getNameSource());
							ppdErrorInfoDto.setMessage("field is obligatory");
							errorList[countError] = ppdErrorInfoDto;
							countError++;
						}
					}
					//set actions to do 
					response.setActions(actionsListFinal);
				} else {
					isError = true;
					GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
					ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
					ppdErrorInfoDto.setField("All Fields");
					ppdErrorInfoDto.setMessage("QueryBuilder not found data");
					errorList[countError] = ppdErrorInfoDto;
					countError++;
				}
			}

			// add error to object response
			if (isError) {
				response.setErrors(errorList);
			}

			// add filters to object response
			if (countFilters > 0) 
			{
				response.setCustom_fields(UtilMapping.loadPpdFilterCoreEmpToHashMap(filtersList));					
				//response.setFilters(filtersList);v1 api
			}

			return response;
		} catch (Exception e) 
		{
			e.printStackTrace();
			logger.error(SuccessFactorEventLFacade.class, e.getMessage());

			PpdCoreEmployeeInfoDto employeeError = new PpdCoreEmployeeInfoDto();
			GenResponseInfoDto[] errorList = new GenResponseInfoDto[1];

			GenResponseInfoDto ppdErrorInfoDto = new GenResponseInfoDto();
			ppdErrorInfoDto.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
			ppdErrorInfoDto.setField("Error process action User Get Value Query Builder ");
			ppdErrorInfoDto.setMessage(e.getMessage());
			errorList[0] = ppdErrorInfoDto;
			employeeError.setErrors(errorList);

			return employeeError;
		}
	}
}
