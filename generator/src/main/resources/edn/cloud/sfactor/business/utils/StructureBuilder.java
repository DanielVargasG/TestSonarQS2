package edn.cloud.sfactor.business.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.wnameless.json.flattener.JsonFlattener;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.StructureInfo;
import edn.cloud.business.dto.StructureItem;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeCtrInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeOrgInfoDto;
import edn.cloud.ppdoc.business.facade.PpdApiUtilsFacade;
import edn.cloud.sfactor.business.connectivity.HttpConnectorSuccessFactor;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.persistence.dao.LoggerDAO;
import edn.cloud.sfactor.persistence.dao.LookupDAO;
import edn.cloud.sfactor.persistence.dao.StructureBusinessDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.LoggerAction;
import edn.cloud.sfactor.persistence.entities.LookupTable;
import edn.cloud.sfactor.persistence.entities.StructureBusiness;

public class StructureBuilder {

	private static StructureBuilder INSTANCE = null;
	private final UtilLogger logger = UtilLogger.getInstance();
	private PpdApiUtilsFacade ppDocUtilFacade = new PpdApiUtilsFacade();
	private SuccessFactorAdminFacade succesFactorAdminFacade = new SuccessFactorAdminFacade();
	private AdminParameters paramAdminCode;
	private AdminParameters paramCountryCode;
	private AdminParameters paramLowerCaseCode;
	private Map<String, Locale> localeMap;

	public static synchronized StructureBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new StructureBuilder();
		}
		return INSTANCE;
	}

	private void initCountryCodeMapping() {
		String[] countries = Locale.getISOCountries();
		localeMap = new HashMap<String, Locale>(countries.length);
		for (String country : countries) {
			Locale locale = new Locale("", country);
			localeMap.put(locale.getISO3Country().toUpperCase(), locale);
		}
	}

	private String iso3CountryCodeToIso2CountryCode(String iso3CountryCode) {

		if (iso3CountryCode != "") {
			return localeMap.get(iso3CountryCode).getCountry();
		} else {
			return "";
		}
	}

	private String iso2CountryCodeToIso3CountryCode(String iso2CountryCode) {
		if (iso2CountryCode != "") {
			Locale locale = new Locale("", iso2CountryCode);
			return locale.getISO3Country();
		} else {
			return "";
		}
	}

	public String[] getStructureAttach(String UserId, String UserIdExtended, String level, String effectiveDate) {
		ArrayList<String> scripts = new ArrayList<String>();

		initCountryCodeMapping();
		paramAdminCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_UPDATE_ORGA.getCode());
		paramCountryCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_COUNTRYID.getCode());
		paramLowerCaseCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_STRUCTURE_LOWERCASE.getCode());

		String countryCode = "";

		if (paramCountryCode != null) {
			countryCode = paramCountryCode.getValue();
		}

		StructureBusinessDAO strucDAO = new StructureBusinessDAO();
		StructureBusiness struc = strucDAO.getByEntityName(level);

		ArrayList<Map<String, String>> structureMap = new ArrayList<>();
		ArrayList<Map<String, String>> finalMap = recursiveStructure(struc, structureMap);
		Collections.reverse(finalMap);

		// ArrayList<Map<String, String>> SfOrga = getOrgaValue(UserId, finalMap, 0,
		// effectiveDate, countryCode);

		StructureInfo si = getOrgaValue(UserId, finalMap, effectiveDate, countryCode);
		ArrayList<Map<String, String>> SfOrgaOut = getOrgaResult(si, 0);
		ArrayList<Map<String, String>> SfOrga = new ArrayList<>();
		for (Map<String, String> map : SfOrgaOut) {
			if (map.get("structure_id").equals("")) {

			} else {
				SfOrga.add(map);
			}
		}

		if (SfOrga != null && SfOrga.size() > 0 && !SfOrga.get(SfOrga.size() - 1).get("userid").equals("")) {
			if (SfOrga.get(SfOrga.size() - 1).get("userid").equals(UserIdExtended)) {
				// NEW CODE
				String ORGID = "";
				if (paramLowerCaseCode != null) {
					String lowerCode = paramLowerCaseCode.getValue();
					if (lowerCode.equals("True")) {
						ORGID = (iso3CountryCodeToIso2CountryCode(SfOrga.get(SfOrga.size() - 1).get("structure_id")).toLowerCase());
					} else {
						ORGID = (SfOrga.get(SfOrga.size() - 1).get("structure_id"));
					}
				} else {
					ORGID = (SfOrga.get(SfOrga.size() - 1).get("structure_id"));
				}

				scripts.add(ORGID);
			}
		}

		// ArrayList<Map<String, String>> SfOrgaGlob = getOrgaValue(UserId, finalMap, 1,
		// effectiveDate, countryCode);

		ArrayList<Map<String, String>> SfOrgaGlobOut = getOrgaResult(si, 1);
		ArrayList<Map<String, String>> SfOrgaGlob = new ArrayList<>();
		for (Map<String, String> map : SfOrgaGlobOut) {
			if (map.get("structure_id").equals("")) {

			} else {

				SfOrgaGlob.add(map);
			}
		}

		if (SfOrgaGlob != null && SfOrgaGlob.size() > 0 && !SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("userid").equals("")) {
			if (SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("userid").equals(UserIdExtended)) {
				// scripts.add(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id"));

				// NEW CODE
				String ORGID = "";
				if (paramLowerCaseCode != null) {
					String lowerCode = paramLowerCaseCode.getValue();
					if (lowerCode.equals("True")) {
						ORGID = (iso3CountryCodeToIso2CountryCode(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id")).toLowerCase());
					} else {
						ORGID = (SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id"));
					}
				} else {
					ORGID = (SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id"));
				}

				scripts.add(ORGID);

			}
		}

		return scripts.stream().toArray(String[]::new);

		// return "ok";
	}

	public ArrayList<PpdCoreEmployeeOrgInfoDto> getStructure(String UserId, String level, String effectiveDate, Boolean isApiV2, String concReturn) {
		try {
			ArrayList<PpdCoreEmployeeOrgInfoDto> list = new ArrayList<>();

			initCountryCodeMapping();
			paramAdminCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_UPDATE_ORGA.getCode());
			paramCountryCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_COUNTRYID.getCode());
			paramLowerCaseCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_STRUCTURE_LOWERCASE.getCode());

			String countryCode = "";

			if (paramCountryCode != null) {
				countryCode = paramCountryCode.getValue();
			}
			StructureBusinessDAO strucDAO = new StructureBusinessDAO();
			StructureBusiness struc = strucDAO.getByEntityName(level);

			ArrayList<Map<String, String>> structureMap = new ArrayList<>();
			ArrayList<Map<String, String>> finalMap = recursiveStructure(struc, structureMap);
			Collections.reverse(finalMap);

			// ArrayList<Map<String, String>> SfOrga = getOrgaValue(UserId, finalMap, 0,
			// effectiveDate, countryCode);

			StructureInfo si = getOrgaValue(UserId, finalMap, effectiveDate, countryCode);
			ArrayList<Map<String, String>> SfOrgaOut = getOrgaResult(si, 0);
			ArrayList<Map<String, String>> SfOrga = new ArrayList<>();
			for (Map<String, String> map : SfOrgaOut) {
				if (map.get("structure_id").equals("")) {

				} else {
					SfOrga.add(map);
				}
			}

			if (SfOrga != null && SfOrga.size() > 0 && !SfOrga.get(SfOrga.size() - 1).get("structure_id").equals("")) {
				PpdCoreEmployeeOrgInfoDto orgInfo = new PpdCoreEmployeeOrgInfoDto();
				if (SfOrga.get(SfOrga.size() - 1).get("status").equals("A") || SfOrga.get(SfOrga.size() - 1).get("status").equals("P") || SfOrga.get(SfOrga.size() - 1).get("status").equals("U")) {
					orgInfo.setActive("true");
				} else {
					orgInfo.setActive("false");
				}

				if (isApiV2) {

					if (paramLowerCaseCode != null) {
						String lowerCode = paramLowerCaseCode.getValue();
						if (lowerCode.equals("True")) {
							orgInfo.setOrganization_id(iso3CountryCodeToIso2CountryCode(SfOrga.get(SfOrga.size() - 1).get("structure_id")).toLowerCase());
						} else {
							orgInfo.setOrganization_id(SfOrga.get(SfOrga.size() - 1).get("structure_id"));
						}
					} else {
						orgInfo.setOrganization_id(SfOrga.get(SfOrga.size() - 1).get("structure_id"));
					}

					if (concReturn.equals("STRUCTURE_USERID")) {
						orgInfo.setEmployee_number(SfOrga.get(SfOrga.size() - 1).get("structure_id") + "_" + UserId);
					}
					if (concReturn.equals("USERID")) {
						orgInfo.setEmployee_number(SfOrga.get(SfOrga.size() - 1).get("finalId"));
					}
					if (concReturn.equals("EXTERNALID")) {
						orgInfo.setEmployee_number(UserId);
					}
				} else {

					if (paramLowerCaseCode != null) {
						String lowerCode = paramLowerCaseCode.getValue();
						if (lowerCode.equals("True")) {
							orgInfo.setOrganization_code(iso3CountryCodeToIso2CountryCode(SfOrga.get(SfOrga.size() - 1).get("structure_id")).toLowerCase());
						} else {
							orgInfo.setOrganization_code(SfOrga.get(SfOrga.size() - 1).get("structure_id"));
						}
					} else {
						orgInfo.setOrganization_code(SfOrga.get(SfOrga.size() - 1).get("structure_id"));
					}

					if (concReturn.equals("STRUCTURE_USERID")) {
						orgInfo.setRegistration_number(SfOrga.get(SfOrga.size() - 1).get("structure_id") + "_" + UserId);
					}
					if (concReturn.equals("USERID")) {
						orgInfo.setRegistration_number(SfOrga.get(SfOrga.size() - 1).get("finalId"));
					}
					if (concReturn.equals("EXTERNALID")) {
						orgInfo.setRegistration_number(UserId);
					}

				}

				if (SfOrga.get(SfOrga.size() - 1).get("departure_date") != null
						&& (!SfOrga.get(SfOrga.size() - 1).get("status").equals("A") && !SfOrga.get(SfOrga.size() - 1).get("status").equals("P") && !SfOrga.get(SfOrga.size() - 1).get("status").equals("U"))) {
					orgInfo.setDeparture_date(SfOrga.get(SfOrga.size() - 1).get("departure_date"));
				}

				list.add(orgInfo);

				if (paramAdminCode != null) {
					if (paramAdminCode.getValue().equals("YES")) {
						updateOrgaLevel(SfOrga);
					}
				}

			}

			// ArrayList<Map<String, String>> SfOrgaGlob = getOrgaValue(UserId, finalMap, 1,
			// effectiveDate, countryCode);

			ArrayList<Map<String, String>> SfOrgaGlobOut = getOrgaResult(si, 1);
			ArrayList<Map<String, String>> SfOrgaGlob = new ArrayList<>();
			for (Map<String, String> map : SfOrgaGlobOut) {
				if (map.get("structure_id").equals("")) {

				} else {
					SfOrgaGlob.add(map);
				}
			}

			if (SfOrgaGlob.size() > 0 && !SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id").equals("")) {
				PpdCoreEmployeeOrgInfoDto orgInfo2 = new PpdCoreEmployeeOrgInfoDto();
				if (SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("status").equals("A") || SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("status").equals("P") || SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("status").equals("U")) {
					orgInfo2.setActive("true");
				} else {
					orgInfo2.setActive("false");
				}

				if (isApiV2) {

					if (paramLowerCaseCode != null) {
						String lowerCode = paramLowerCaseCode.getValue();
						if (lowerCode.equals("True")) {
							orgInfo2.setOrganization_id(iso3CountryCodeToIso2CountryCode(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id")).toLowerCase());
						} else {
							orgInfo2.setOrganization_id(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id"));
						}
					} else {
						orgInfo2.setOrganization_id(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id"));
					}

					if (concReturn.equals("STRUCTURE_USERID")) {
						orgInfo2.setEmployee_number(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id") + "_" + UserId);
					}
					if (concReturn.equals("USERID")) {
						orgInfo2.setEmployee_number(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("finalId"));
					}
					if (concReturn.equals("EXTERNALID")) {
						orgInfo2.setEmployee_number(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get(UserId));
					}
				} else {

					if (paramLowerCaseCode != null) {
						String lowerCode = paramLowerCaseCode.getValue();
						if (lowerCode.equals("True")) {
							orgInfo2.setOrganization_code(iso3CountryCodeToIso2CountryCode(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id")).toLowerCase());
						} else {
							orgInfo2.setOrganization_code(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id"));
						}
					} else {
						orgInfo2.setOrganization_code(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id"));
					}

					if (concReturn.equals("STRUCTURE_USERID")) {
						orgInfo2.setRegistration_number(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id") + "_" + UserId);
					}
					if (concReturn.equals("USERID")) {
						orgInfo2.setRegistration_number(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("finalId"));
					}
					if (concReturn.equals("EXTERNALID")) {
						orgInfo2.setRegistration_number(UserId);
					}
				}
				if (SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("departure_date") != null
						&& (!SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("status").equals("A") && !SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("status").equals("P") && !SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("status").equals("U"))) {
					orgInfo2.setDeparture_date(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("departure_date"));
				}
				list.add(orgInfo2);
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<PpdCoreEmployeeCtrInfoDto> getFullOrga(String UserId, String level, String effectiveDate, Boolean isApiV2, String concReturn) {

		paramLowerCaseCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_STRUCTURE_LOWERCASE.getCode());

		try {
			ArrayList<PpdCoreEmployeeCtrInfoDto> list = new ArrayList<>();
			initCountryCodeMapping();
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date = simpleDateFormat.parse(effectiveDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			StructureBusinessDAO strucDAO = new StructureBusinessDAO();
			StructureBusiness struc = strucDAO.getByEntityName(level);

			ArrayList<Map<String, String>> structureMap = new ArrayList<>();
			ArrayList<Map<String, String>> finalMap = recursiveStructure(struc, structureMap);
			Collections.reverse(finalMap);

			ArrayList<StructureItem> SfOrga = getFullOrgaValue(UserId, finalMap);

			Map<String, StructureItem> arf = new HashMap<String, StructureItem>();
			String key = "";

			for (StructureItem map : SfOrga) {

				String ORGID2 = iso3CountryCodeToIso2CountryCode(map.getCountryCode());

				if (paramLowerCaseCode != null) {
					String lowerCode = paramLowerCaseCode.getValue();
					if (lowerCode.equals("True")) {
						ORGID2 = ORGID2.toLowerCase();
					}
				}

				StructureItem arf2 = new StructureItem();
				if (concReturn.equals("STRUCTURE_USERID") || concReturn.equals("EXTERNALID")) {
					key = map.getCountryCode() + "_" + map.getPersonIdExternal();
					arf2.setUserId(ORGID2 + "_" + map.getPersonIdExternal());
				}
				if (concReturn.equals("USERID")) {
					key = map.getCountryCode() + "_" + map.getUserId();
					arf2.setUserId(map.getUserId());
				}
				if (concReturn.equals("EXTERNALID")) {
					arf2.setUserId(map.getPersonIdExternal());
				}

				if (cleanMilli(map.getJobStartDate()) < cal.getTimeInMillis()) {
					if (!arf.containsKey(key)) {

						if (cal.getTimeInMillis() > cleanMilli(map.getJobStartDate()) && cal.getTimeInMillis() < cleanMilli(map.getJobEndDate()) && map.getEmployeeStatus().equals("A")) {
							arf2.setEmployeeStatus("A");
							arf2.setCountryCode(map.getCountryCode());
							arf2.setEmpEndDate(null);
						} else {
							arf2.setEmployeeStatus("I");
							arf2.setEmpEndDate(cleanMilli(map.getJobEndDate()).toString());
							arf2.setCountryCode(map.getCountryCode());
						}

						arf.put(key, arf2);
					} else {

						if (cal.getTimeInMillis() > cleanMilli(map.getJobStartDate()) && cal.getTimeInMillis() < cleanMilli(map.getJobEndDate()) && map.getEmployeeStatus().equals("A")) {

							arf.get(key).setEmployeeStatus("A");
							arf.get(key).setCountryCode(map.getCountryCode());
							arf.get(key).setEmpEndDate(null);

						} else if (cal.getTimeInMillis() > cleanMilli(map.getJobStartDate()) && cal.getTimeInMillis() < cleanMilli(map.getJobEndDate()) && map.getEmployeeStatus().equals("I")) {

							arf.get(key).setEmployeeStatus("I");
							if (cleanMilli(map.getJobStartDate()) > cleanMilli(arf.get(key).getEmpEndDate())) {
								arf.get(key).setEmpEndDate(cleanMilli(map.getJobStartDate()).toString());
							}
							arf.get(key).setCountryCode(map.getCountryCode());

						} else {
							if (!arf.get(key).getEmployeeStatus().equals("A") && cleanMilli(map.getJobStartDate()) > cleanMilli(arf.get(key).getEmpEndDate())) {
								if (253402214400000L > cleanMilli(map.getJobEndDate())) {
									arf.get(key).setEmpEndDate(cleanMilli(map.getJobEndDate()).toString());
								} else {
									arf.get(key).setEmpEndDate(cleanMilli(map.getJobStartDate()).toString());
								}
							}
						}
					}
				}
			}

			for (Map.Entry<String, StructureItem> entry : arf.entrySet()) {

				PpdCoreEmployeeCtrInfoDto finalObj = new PpdCoreEmployeeCtrInfoDto();
				if (entry.getValue().getEmployeeStatus().equals("A")) {
					finalObj.setActive("true");
				} else {
					finalObj.setActive("false");
				}
				if (entry.getValue().getEmpEndDate() != null) {
					finalObj.setDeparture_date(JSONTarihConvert(entry.getValue().getEmpEndDate()));
				}

				String ORGID = "";
				Boolean isIso2 = false;
				String[] parts1 = level.split(Pattern.quote("|"));
				if (parts1.length > 2) {
					if (parts1[2].equals("iso2")) {
						isIso2 = true;
					}
				}
				ORGID = iso3CountryCodeToIso2CountryCode(entry.getValue().getCountryCode());

				if (paramLowerCaseCode != null) {
					String lowerCode = paramLowerCaseCode.getValue();
					if (lowerCode.equals("True")) {
						ORGID = ORGID.toLowerCase();
					}
				}

				if (isApiV2) {
					finalObj.setOrganization_id(ORGID);
					finalObj.setEmployee_number(entry.getValue().getUserId());
				} else {
					finalObj.setOrganization_code(entry.getValue().getCountryCode());
					finalObj.setRegistration_number(entry.getValue().getUserId());
				}
				finalObj.setCountry_id(entry.getValue().getCountryCode());

				list.add(finalObj);
			}

			return list;
		} catch (

		Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private String JSONTarihConvert(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));

		Long timeInMillis = Long.valueOf(date);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);
		c.setTimeZone(TimeZone.getTimeZone("UTC"));

		Date dt = c.getTime();

		return df.format(dt);
	}

	private Long cleanMilli(String date) {
		String datereip = date.replace("/Date(", "").replace(")/", "");
		Long timeInMillis = Long.valueOf(datereip);

		return timeInMillis;
	}

	public ArrayList<PpdCoreEmployeeCtrInfoDto> getCountry(String UserId, String level, String effectiveDate, Boolean isCreateUser, String concReturn) {
		try {
			ArrayList<PpdCoreEmployeeCtrInfoDto> list = new ArrayList<>();

			initCountryCodeMapping();
			paramAdminCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_UPDATE_ORGA.getCode());
			paramCountryCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_COUNTRYID.getCode());
			paramLowerCaseCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_STRUCTURE_LOWERCASE.getCode());

			String countryCode = "";

			if (paramCountryCode != null) {
				countryCode = paramCountryCode.getValue();
			}
			StructureBusinessDAO strucDAO = new StructureBusinessDAO();
			StructureBusiness struc = strucDAO.getByEntityName(level);

			ArrayList<Map<String, String>> structureMap = new ArrayList<>();
			ArrayList<Map<String, String>> finalMap = recursiveStructure(struc, structureMap);
			Collections.reverse(finalMap);

			StructureInfo si = getOrgaValue(UserId, finalMap, effectiveDate, countryCode);

			ArrayList<Map<String, String>> SfOrgaOut = getOrgaResult(si, 0);
			ArrayList<Map<String, String>> SfOrga = new ArrayList<>();
			for (Map<String, String> map : SfOrgaOut) {
				if (map.get("structure_id").equals("")) {

				} else {
					SfOrga.add(map);
				}
			}

			if (SfOrga != null && SfOrga.size() > 0 && SfOrga.get(SfOrga.size() - 1).get("structure_id") != null && !SfOrga.get(SfOrga.size() - 1).get("structure_id").equals("")) {

				PpdCoreEmployeeCtrInfoDto orgInfo = new PpdCoreEmployeeCtrInfoDto();
				if (SfOrga.get(SfOrga.size() - 1).get("status").equals("A") || SfOrga.get(SfOrga.size() - 1).get("status").equals("P") || SfOrga.get(SfOrga.size() - 1).get("status").equals("U")) {
					orgInfo.setActive("true");
				} else {
					orgInfo.setActive("false");
				}

				if (SfOrga.get(SfOrga.size() - 1).get("function").equals("ITERATIVELOOKUP")) {
					orgInfo.setFilter(SfOrga.get(SfOrga.size() - 1).get("filter"));
				}

				if (SfOrga.get(SfOrga.size() - 1).get("entry_date") != null && !SfOrga.get(SfOrga.size() - 1).get("entry_date").equals("")) {
					orgInfo.setEntry_date(SfOrga.get(SfOrga.size() - 1).get("entry_date"));
				}

				if (isCreateUser) {

					if (paramLowerCaseCode != null) {
						String lowerCode = paramLowerCaseCode.getValue();
						if (lowerCode.equals("True")) {
							orgInfo.setOrganization_id(iso3CountryCodeToIso2CountryCode(SfOrga.get(SfOrga.size() - 1).get("structure_id")).toLowerCase());
						} else {
							orgInfo.setOrganization_id(SfOrga.get(SfOrga.size() - 1).get("structure_id"));
						}
					} else {
						orgInfo.setOrganization_id(SfOrga.get(SfOrga.size() - 1).get("structure_id"));
					}

					if (concReturn.equals("STRUCTURE_USERID")) {
						orgInfo.setEmployee_number(SfOrga.get(SfOrga.size() - 1).get("structure_id") + "_" + UserId);
					}
					if (concReturn.equals("USERID")) {
						orgInfo.setEmployee_number(SfOrga.get(SfOrga.size() - 1).get("finalId"));
					}
					if (concReturn.equals("EXTERNALID")) {
						orgInfo.setEmployee_number(UserId);
					}
					if (paramLowerCaseCode != null) {
						String lowerCode = paramLowerCaseCode.getValue();
						if (lowerCode.equals("True")) {
							orgInfo.setCountry_id(iso3CountryCodeToIso2CountryCode(SfOrga.get(SfOrga.size() - 1).get("countryrid")).toLowerCase());
						} else {
							orgInfo.setCountry_id(SfOrga.get(SfOrga.size() - 1).get("countryrid"));
						}
					} else {
						orgInfo.setCountry_id(SfOrga.get(SfOrga.size() - 1).get("countryrid"));
					}
				} else {

					if (paramLowerCaseCode != null) {
						String lowerCode = paramLowerCaseCode.getValue();
						if (lowerCode.equals("True")) {
							orgInfo.setOrganization_code(iso3CountryCodeToIso2CountryCode(SfOrga.get(SfOrga.size() - 1).get("structure_id")).toLowerCase());
						} else {
							orgInfo.setOrganization_code(SfOrga.get(SfOrga.size() - 1).get("structure_id"));
						}
					} else {
						orgInfo.setOrganization_code(SfOrga.get(SfOrga.size() - 1).get("structure_id"));
					}

					if (concReturn.equals("STRUCTURE_USERID")) {
						orgInfo.setRegistration_number(SfOrga.get(SfOrga.size() - 1).get("structure_id") + "_" + UserId);
					}
					if (concReturn.equals("USERID")) {
						orgInfo.setRegistration_number(SfOrga.get(SfOrga.size() - 1).get("finalId"));
					}
					if (concReturn.equals("EXTERNALID")) {
						orgInfo.setEmployee_number(UserId);
					}

					if (paramLowerCaseCode != null) {
						String lowerCode = paramLowerCaseCode.getValue();
						if (lowerCode.equals("True")) {
							orgInfo.setCountry_id(iso3CountryCodeToIso2CountryCode(SfOrga.get(SfOrga.size() - 1).get("countryrid")).toLowerCase());
						} else {
							orgInfo.setCountry_id(SfOrga.get(SfOrga.size() - 1).get("countryrid"));
						}
					} else {
						orgInfo.setCountry_id(SfOrga.get(SfOrga.size() - 1).get("countryrid"));
					}
				}
				if (!SfOrga.get(SfOrga.size() - 1).get("entry_date").equals(SfOrga.get(SfOrga.size() - 1).get("departure_date"))) {
					list.add(orgInfo);
				}

			}

			// ArrayList<Map<String, String>> SfOrgaGlob = getOrgaValue(UserId, finalMap, 1,
			// effectiveDate, countryCode);

			ArrayList<Map<String, String>> SfOrgaGlobOut = getOrgaResult(si, 1);
			ArrayList<Map<String, String>> SfOrgaGlob = new ArrayList<>();
			for (Map<String, String> map : SfOrgaGlobOut) {
				if (map.get("structure_id").equals("")) {

				} else {
					SfOrgaGlob.add(map);
				}
			}

			if (SfOrgaGlob != null && SfOrgaGlob.size() > 0 && SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id") != null && !SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id").equals("")) {
				PpdCoreEmployeeCtrInfoDto orgInfo2 = new PpdCoreEmployeeCtrInfoDto();
				if (SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("status").equals("A") || SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("status").equals("P") || SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("status").equals("U")) {
					orgInfo2.setActive("true");
				} else {
					orgInfo2.setActive("false");
				}

				if (SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("function").equals("ITERATIVELOOKUP")) {
					orgInfo2.setFilter(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("filter"));
				}

				if (SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("entry_date") != null && !SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("entry_date").equals("")) {
					orgInfo2.setEntry_date(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("entry_date"));
				}

				if (isCreateUser) {

					if (paramLowerCaseCode != null) {
						String lowerCode = paramLowerCaseCode.getValue();
						if (lowerCode.equals("True")) {
							orgInfo2.setOrganization_id(iso3CountryCodeToIso2CountryCode(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id")).toLowerCase());
						} else {
							orgInfo2.setOrganization_id(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id"));

						}
					} else {
						orgInfo2.setOrganization_id(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id"));

					}

					if (concReturn.equals("STRUCTURE_USERID")) {
						orgInfo2.setEmployee_number(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id") + "_" + UserId);
					}
					if (concReturn.equals("USERID")) {
						orgInfo2.setEmployee_number(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("finalId"));
					}
					if (concReturn.equals("EXTERNALID")) {
						orgInfo2.setEmployee_number(UserId);
					}
				} else {

					if (paramLowerCaseCode != null) {
						String lowerCode = paramLowerCaseCode.getValue();
						if (lowerCode.equals("True")) {
							orgInfo2.setOrganization_code(iso3CountryCodeToIso2CountryCode(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id")).toLowerCase());
						} else {
							orgInfo2.setOrganization_code(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id"));
						}
					} else {
						orgInfo2.setOrganization_code(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id"));
					}

					if (concReturn.equals("STRUCTURE_USERID")) {
						orgInfo2.setRegistration_number(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("structure_id") + "_" + UserId);
					}
					if (concReturn.equals("USERID")) {
						orgInfo2.setRegistration_number(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("finalId"));
					}
					if (concReturn.equals("EXTERNALID")) {
						orgInfo2.setRegistration_number(UserId);
					}
				}

				if (paramLowerCaseCode != null) {
					String lowerCode = paramLowerCaseCode.getValue();
					if (lowerCode.equals("True")) {
						orgInfo2.setCountry_id(iso3CountryCodeToIso2CountryCode(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("countryrid")).toLowerCase());
					} else {
						orgInfo2.setCountry_id(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("countryrid"));
					}
				} else {
					orgInfo2.setCountry_id(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("countryrid"));
				}

				if (!SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("entry_date").equals(SfOrgaGlob.get(SfOrgaGlob.size() - 1).get("departure_date"))) {
					list.add(orgInfo2);
				}

				list.add(orgInfo2);
			}

			if (paramAdminCode != null && paramAdminCode.getValue().equals("YES")) {
				// updateOrgaLevel(finalMap);
			}

			LookupDAO lkdao = new LookupDAO();
			LookupTable lk = lkdao.getLookupByInput("filter", "filter_organization");

			if (lk.getValueOut().equals("")) {
			} else {
				String[] lstItm = lk.getValueOut().split(",");
				boolean isIN = false;
				ArrayList<PpdCoreEmployeeCtrInfoDto> toRem = new ArrayList<>();
				for (PpdCoreEmployeeCtrInfoDto valRem : list) {
					for (int i = 0; i < lstItm.length; i++) {
						if (lstItm[i].equals(valRem.getFilter())) {
							isIN = true;
						}
					}
					if (!isIN) {
						toRem.add(valRem);
					}
				}
				list.removeAll(toRem);
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getStructureUnik(String UserId, StructureBusiness struc, String effectiveDate) {

		paramCountryCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_COUNTRYID.getCode());

		String countryCode = "";

		if (paramCountryCode != null) {
			countryCode = paramCountryCode.getValue();
		}

		initCountryCodeMapping();

		ArrayList<Map<String, String>> structureMap = new ArrayList<>();
		ArrayList<Map<String, String>> finalMap = recursiveStructure(struc, structureMap);
		// ArrayList<Map<String, String>> SfOrga = getOrgaValue(UserId, finalMap, 0,
		// effectiveDate, countryCode)

		StructureInfo si = getOrgaValue(UserId, finalMap, effectiveDate, countryCode);
		
		ArrayList<Map<String, String>> SfOrgaOut = getOrgaResult(si, 0); 
		ArrayList<Map<String, String>> SfOrga = new ArrayList<>();
		for (Map<String, String> map : SfOrgaOut) {
			if (map.get("structure_id").equals("")) {

			} else {
				SfOrga.add(map);
			}
		}

		updateOrgaLevel(finalMap);

		return SfOrga.get(SfOrga.size() - 1).get("structure_id");

		// return "ok";
	}

	// Loop On Array structure
	private ArrayList<Map<String, String>> recursiveStructure(StructureBusiness struc, ArrayList<Map<String, String>> structureMap) {
		Map<String, String> myMap = new HashMap<>();

		if (struc != null) {

			String firstPart = struc.getStructureName();

			if (firstPart.equals("")) {
				return structureMap;
			}

			String[] parts1 = firstPart.split(Pattern.quote("|"));
			String[] parts2 = parts1[1].split(Pattern.quote(","));

			myMap.put("node", parts1[0]);
			myMap.put("keys1", parts2[0]);
			myMap.put("keys2", parts2[1]);
			if (parts1.length > 4)
				myMap.put("keys4", parts1[4]);
			if (parts1.length > 3)
				myMap.put("keys3", parts1[3]);
			if (parts1.length > 2) {
				myMap.put("function", parts1[2]);
			} else {
				myMap.put("function", "Dynamic");
			}
			if (struc.getParentStructure() != null) {
				String parentfirstPart = struc.getParentStructure().getStructureName();
				String[] parentparts1 = parentfirstPart.split(Pattern.quote("|"));
				String[] parentparts2 = parentparts1[1].split(Pattern.quote(","));
				myMap.put("parent", parentparts1[0]);
				myMap.put("parentkey", parentparts2[0]);
				if (parentparts1.length > 2) {
					myMap.put("parentfunction", parentparts1[2]);
				} else {
					myMap.put("parentfunction", "Dynamic");
				}
			}
			structureMap.add(myMap);
			if (struc.getParentStructure() != null)
				recursiveStructure(struc.getParentStructure(), structureMap);
		}
		return structureMap;
	}

	// Get Structure Dynamic from SF
	private ArrayList<StructureItem> getFullOrgaValue(String UserId, ArrayList<Map<String, String>> finalMap) {
		String variables = "employmentNav/userId,employmentNav/personIdExternal,employmentNav/startDate,employmentNav/endDate,employmentNav/jobInfoNav/startDate,employmentNav/jobInfoNav/endDate,employmentNav/jobInfoNav/countryOfCompany,employmentNav/jobInfoNav/emplStatusNav/externalCode,";
		String expand = "employmentNav/jobInfoNav/emplStatusNav,";
		String[] countVar = { "countryOfCompany" };

		String node = "employmentNav/jobInfoNav";
		for (Map<String, String> map : finalMap) {
			if (!map.get("function").equals("Static")) {
				variables += node + "/" + map.get("node") + "Nav/" + map.get("keys1") + "," + node + "/" + map.get("node") + "Nav/" + map.get("keys2") + ",";
				if (map.get("keys3") != null)
					variables += node + "/" + map.get("node") + "Nav/" + map.get("keys3") + ",";
				expand += node + "/" + map.get("node") + "Nav,";
			}
		}
		String level0 = "PerPerson('" + UserId + "')";

		ArrayList<StructureItem> items = new ArrayList<>();

		try {
			String SfFeedback = "";
			SfFeedback = HttpConnectorSuccessFactor.getInstance().getResult(level0 + "?$select=" + removeLastChar(variables) + "&$expand=" + removeLastChar(expand) + "&fromDate=1900-01-01");

			JSONObject jsonObj = new JSONObject(SfFeedback);

			if (!jsonObj.isNull("d")) {
				JSONObject resOdata = jsonObj.getJSONObject("d");
				JSONArray objResultsEmp = resOdata.getJSONObject("employmentNav").getJSONArray("results");

				for (int i = 0; i < objResultsEmp.length(); i++) {
					JSONObject obj = objResultsEmp.getJSONObject(i);
					JSONObject obj2 = obj.getJSONObject("jobInfoNav");
					JSONArray objResultsJob = obj2.getJSONArray("results");

					for (int j = 0; j < objResultsJob.length(); j++) {
						JSONObject objJob = objResultsJob.getJSONObject(j);

						StructureItem itm = new StructureItem();
						itm.setPersonIdExternal(obj.getString("personIdExternal"));
						itm.setUserId(obj.getString("userId"));
						itm.setCountryCode(objJob.getString("countryOfCompany"));
						itm.setJobStartDate((!objJob.isNull("startDate") ? objJob.getString("startDate") : ""));
						itm.setJobEndDate((!objJob.isNull("endDate") ? objJob.getString("endDate") : ""));
						itm.setEmployeeStatus(objJob.getJSONObject("emplStatusNav").getString("externalCode"));
						itm.setEmpEndDate((!obj.isNull("endDate") ? obj.getString("endDate") : ""));
						itm.setEmpStartDate((!obj.isNull("startDate") ? obj.getString("startDate") : ""));
						items.add(itm);
					}
				}

			}

		} catch (

		InvalidResponseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return items;

	}

	// Get Structure Dynamic from SF
	private StructureInfo getOrgaValue(String UserId, ArrayList<Map<String, String>> finalMap, String effectiveDate, String paramCountry) {
		String variables = "employmentNav/endDate,employmentNav/jobInfoNav/userId,employmentNav/startDate,employmentNav/jobInfoNav/countryOfCompany,employmentNav/jobInfoNav/emplStatusNav/externalCode,";
		String expand = "employmentNav/jobInfoNav/emplStatusNav,";
		String filters = "personIdExternal eq '" + UserId + "'";
		String[] countVar = { "countryOfCompany" };
		if (!paramCountry.equals("")) {
			variables += "employmentNav/jobInfoNav/" + paramCountry + ",";
			countVar = paramCountry.split(Pattern.quote("/"));
			String[] countVarOut = Arrays.copyOf(countVar, countVar.length - 1);
			if (countVarOut.length >= 1) {
				expand += "employmentNav/jobInfoNav/" + String.join("/", countVarOut) + ",";
			}
		}

		String node = "employmentNav/jobInfoNav";
		for (Map<String, String> map : finalMap) {
			if (!map.get("function").equals("Static") && !map.get("function").equals("StaticParent")) {
				variables += node + "/" + map.get("node") + "Nav/" + map.get("keys1") + "," + node + "/" + map.get("node") + "Nav/" + map.get("keys2") + ",";
				if (map.get("keys3") != null)
					variables += node + "/" + map.get("node") + "Nav/" + map.get("keys3") + ",";
				if (map.get("keys4") != null)
					variables += node + "/" + map.get("node") + "Nav/" + map.get("keys4") + ",";
				if (map.get("keys3") != null && map.get("keys3").split("/").length > 1)
					expand += node + "/" + map.get("node") + "Nav/" + map.get("keys3").split("/")[0] + ",";
				expand += node + "/" + map.get("node") + "Nav,";
			} else if (map.get("function").equals("StaticParent")) {
				variables += node + "/" + map.get("node") + "Nav/" + map.get("keys1") + "," + node + "/" + map.get("node") + "Nav/" + map.get("keys2") + ",";
				expand += node + "/" + map.get("node") + "Nav,";
			}
		}
		String level0 = "PerPerson";

		// filters += " and employmentNav/userNav/status in 'active','inactive'";

		try {
			String SfFeedback = "";
			if (effectiveDate.equals("")) {
				SfFeedback = HttpConnectorSuccessFactor.getInstance().getResult(level0 + "?$select=" + removeLastChar(variables) + "&$expand=" + removeLastChar(expand) + (filters != "" ? "&$filter=" + filters : ""));
			} else {
				SfFeedback = HttpConnectorSuccessFactor.getInstance()
						.getResult(level0 + "?$select=" + removeLastChar(variables) + "&$expand=" + removeLastChar(expand) + (filters != "" ? "&$filter=" + filters : "") + "&asOfDate=" + effectiveDate);
			}

			JSONObject jsonObjArray = new JSONObject(SfFeedback);
			JSONArray jsonObjFromArray = jsonObjArray.getJSONObject("d").getJSONArray("results");
			JSONObject jsonD = new JSONObject();
			jsonD.put("d", jsonObjFromArray.get(0));

			Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(jsonD.toString());

			StructureInfo si = new StructureInfo();
			si.setFlattenedJsonMap(flattenedJsonMap);
			si.setCountVar(countVar);
			si.setFinalMap(finalMap);

			return si;
		} catch (

		InvalidResponseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private ArrayList<Map<String, String>> getOrgaResult(StructureInfo si, int jobNumber) {

		Map<String, Object> flattenedJsonMap = si.getFlattenedJsonMap();
		ArrayList<Map<String, String>> finalMap = si.getFinalMap();
		String[] countVar = si.getCountVar();

		for (Map<String, String> map : finalMap) {

			if (map.get("function").equals("iso2")) {
				map.put("structure_id", iso3CountryCodeToIso2CountryCode(objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0]." + map.get("node").replace("/", ".") + "Nav." + map.get("keys1")))));
			} else if (map.get("function").equals("iso3")) {
				map.put("structure_id", iso2CountryCodeToIso3CountryCode(objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0]." + map.get("node").replace("/", ".") + "Nav." + map.get("keys1")))));

			} else {

				map.put("structure_id", objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0]." + map.get("node").replace("/", ".") + "Nav." + map.get("keys1"))));
			}

			map.put("route_name", objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0]." + map.get("node").replace("/", ".") + "Nav." + map.get("keys2"))));
			if (map.get("parent") != null) {

				if (map.get("parentfunction").equals("iso2")) {
					map.put("route_parent",
							iso3CountryCodeToIso2CountryCode(objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0]." + map.get("parent").replace("/", ".") + "Nav." + map.get("parentkey")))));
				} else if (map.get("parentfunction").equals("iso3")) {
					map.put("route_parent",
							iso2CountryCodeToIso3CountryCode(objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0]." + map.get("parent").replace("/", ".") + "Nav." + map.get("parentkey")))));
				} else {

					map.put("route_parent", objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0]." + map.get("parent").replace("/", ".") + "Nav." + map.get("parentkey"))));

					if (map.get("parentfunction").equals("Static")) {
						map.put("route_parent", map.get("parentkey"));
					}

				}

			} else if (map.get("keys3") != null) {
				if (map.get("function").equals("StaticParent")) {
					map.put("route_parent", map.get("keys3"));
				} else {
					map.put("route_parent", objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0]." + map.get("node").replace("/", ".") + "Nav." + map.get("keys3").replace("/", "."))));
				}
				if (map.get("keys4") != null) {
					map.put("filter", objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0]." + map.get("node").replace("/", ".") + "Nav." + map.get("keys4").replace("/", "."))));

				}
			}

			map.put("finalId", objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0].userId")));

			map.put("countryrid", objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0]." + String.join(".", countVar))));
			if (objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].endDate")).equals("")) {
			} else {
				map.put("departure_date", JSONTarihConvertUS(null, objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].endDate"))));
			}
			map.put("entry_date", JSONTarihConvertUS(null, objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].startDate"))));
			map.put("userid", objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0].userId")));
			map.put("status", objToStr(flattenedJsonMap.get("d.employmentNav.results[" + jobNumber + "].jobInfoNav.results[0].emplStatusNav.externalCode")));
			if (map.get("function").equals("Static")) {
				map.put("structure_id", map.get("keys1"));
				map.put("route_name", map.get("keys2"));
			}
		}

		return finalMap;

	}

	private String JSONTarihConvertUS(String formatResponse, String date) {
		if (date != null && !date.equals("")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			if (formatResponse != null)
				df = new SimpleDateFormat(formatResponse);

			String datereip = date.replace("/Date(", "").replace(")/", "");

			Long timeInMillis = Long.valueOf(datereip);

			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(timeInMillis);

			Date dt = c.getTime();
			return df.format(dt);
		} else {
			return "";
		}

	}

	// Update Structure in PeopleDoc
	private void updateOrgaLevel(ArrayList<Map<String, String>> map) {

		for (Map<String, String> map2 : map) {
			Map<String, String> organization = new HashMap<String, String>();
			organization.put("name", map2.get("route_name"));
			organization.put("id", map2.get("structure_id"));

			if (map2.get("route_parent") == null) {

			} else {
				organization.put("parent_organization_id", map2.get("route_parent"));
			}
			ppDocUtilFacade.wServiceUpdateOrga(map2.get("structure_id"), organization);
		}
	}

	public void updateLevelbyTime() {
		AdminParameters paramUpdateStruc = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_UPDATE_ORGA.getCode());

		if (paramUpdateStruc == null) {
			return;
		}

		String update = paramUpdateStruc.getValue();

		AdminParameters paramUpdateDelta = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_TIME_INITDATE_SEARCH_ATTACH_ADD.getCode());

		if (paramUpdateDelta != null) {
			String updateDelta = paramUpdateDelta.getValue();
		}

		if (update.equals("YES")) {

			String StructurA = "";
			String StructurB = "";
			String StructurC = "";
			StructureBusinessDAO strucDAO = new StructureBusinessDAO();
			List<StructureBusiness> struc = strucDAO.getAll();

			for (StructureBusiness structureBusiness : struc) {
				if (structureBusiness.getStructureObject() != null) {
					String[] list = structureBusiness.getStructureName().split("\\|");

					String added = "";
					if (list.length > 1) {
						String[] splitA = list[1].split("\\,");
						StructurA = splitA[0];
						StructurB = splitA[1];
						added += list[1];
					}

					if (list.length > 2 && list[2].equals("ITERATIVE")) {
						if (list.length > 3) {
							added += "," + list[3];
							StructurC = list[3];
						}
					}

					String SfFeedbackStruc = "";
					try {
						Date currentDate = new Date();
						Calendar cal = Calendar.getInstance();
						// remove next line if you're always using the current time.
						cal.setTime(currentDate);
						cal.add(Calendar.MINUTE, -5);
						String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(cal.getTime());
						SfFeedbackStruc = HttpConnectorSuccessFactor.getInstance()
								.getResult(structureBusiness.getStructureObject() + "?$select=" + added + "&$filter=lastModifiedDateTime gt datetimeoffset'" + timeStamp + ".000Z'&$format=json");

						JSONObject flattenedJsonMap = new JSONObject(SfFeedbackStruc);

						if (!flattenedJsonMap.isNull("d")) {
							JSONObject jd = flattenedJsonMap.getJSONObject("d");
							if (!jd.isNull("results")) {
								JSONArray jarr = jd.getJSONArray("results");

								this.logger("201", "EvenListener ORGA Job added " + jarr.length() + " organization to update", UtilCodesEnum.CODE_JOB_ORGAS.getCode(), "Process");

								for (int i = 0; i < jarr.length(); i++) {
									ArrayList<String> colonnes = new ArrayList<String>();
									JSONObject obj = jarr.getJSONObject(i);

									Map<String, String> organization = new HashMap<String, String>();
									organization.put("name", obj.getString(StructurB));
									organization.put("id", obj.getString(StructurA));
									if (obj.getString(StructurC) == null && obj.getString(StructurA) != "") {
									} else {
										organization.put("parent_organization_id", obj.getString(StructurC));
									}
									ppDocUtilFacade.wServiceUpdateOrga(obj.getString(StructurA), organization);
								}
							}
						}

					} catch (InvalidResponseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void logger(String code, String message, String user, String status) {
		LoggerDAO list = new LoggerDAO();
		LoggerAction li = new LoggerAction();
		li.setCode(code);
		li.setMessage(message);
		li.setUser(user);
		li.setStatus(status);
		list.saveNew(li);
	}

	private String objToStr(Object result) {
		if (result == null) {
			return "";
		} else {
			return result.toString();
		}

	}

	private static String removeLastChar(String str) {
		return str.substring(0, str.length() - 1);
	}

}