package edn.cloud.sfactor.business.utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import edn.cloud.business.api.util.UtilBuilderDateFormat;
import edn.cloud.business.api.util.UtilBuilderFunctions;
import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.CompVale;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.sfactor.business.connectivity.HttpConnectorSuccessFactor;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.persistence.dao.LookupDAO;
import edn.cloud.sfactor.persistence.dao.SfEntityDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.LookupTable;
import edn.cloud.sfactor.persistence.entities.SfEntity;
import edn.cloud.sfactor.persistence.entities.SfNavProperty;
import edn.cloud.sfactor.persistence.entities.SfProperty;

public class QueryBuilder {

	private static QueryBuilder INSTANCE = null;
	private final UtilLogger logger = UtilLogger.getInstance();

	public static synchronized QueryBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new QueryBuilder();
			UtilBuilderFunctions.initCountryCodeMapping();
		}
		return INSTANCE;
	}

	public Map<String, ResultBuilderDto> convert(Map<String, ResultBuilderDto> map, String UserId, String effectiveDate) {

		Map<String, ResultBuilderDto> result = new HashMap<String, ResultBuilderDto>();

		if (map.size() > 0) {
			for (Map.Entry<String, ResultBuilderDto> entry : map.entrySet()) {
				String route = entry.getValue().getUrl();
				if (route.substring(0, 1).equals("/")) {
					route = route.substring(1);
				}
				String[] splitRoute = route.split("\\/");

				result.put(entry.getKey(), check(splitRoute, entry.getValue()));

			}

			return queryFromMap(result, UserId, effectiveDate);
		}

		return null;
	}

	private ResultBuilderDto check(String[] splitRoute, ResultBuilderDto rb) {
		if (splitRoute.length < 2) {
			rb.setStatus("Error");
			rb.setResult("Invalid");
			return rb;
		}
		String multiplicity = "1";
		String relation = splitRoute[0];
		for (int i = 0; i < splitRoute.length - 1; i++) {

			String[] splitNode = splitRoute[i].split("\\(");
			String result = StringUtils.substringBetween(splitRoute[i], "(", ")");

			rb.getNode().put(splitNode[0], multiplicity);
			ResultBuilderDto status = getLevel(splitRoute, relation, i, i + 1, splitRoute.length - 1, rb);

			String[] splitNodePlusOne = splitRoute[i + 1].split("\\(");
			multiplicity = realMultiplicity(relation, splitNodePlusOne[0]);
			relation = realRelation(relation, splitNodePlusOne[0]);
			if (!status.getType().equals("default")) {
				return status;
			}

		}

		return rb;
	}

	private ResultBuilderDto getLevel(String[] splitRoute, String relation, int level, int nextlevel, int lastlevel, ResultBuilderDto rb) {
		SfEntityDAO entDao = new SfEntityDAO();
		SfEntity ent = null;
		try {
			ent = entDao.getByEntityName(relation);

			if (ent == null) {
				rb.setStatus("Error");
				rb.setResult("Invalid");
				return rb;
			}
		} catch (NullPointerException e) {
			rb.setStatus("Error");
			rb.setResult("Invalid");
			return rb;
		}

		if (nextlevel == lastlevel) {

			for (SfProperty sfprop : ent.getProperties()) {

				String[] rteFunc = splitRoute[nextlevel].split("\\|");

				if (sfprop.getName().equals(rteFunc[0])) {
					String[] ret = sfprop.getType().split("\\.");
					rb.setType(ret[1]);
					return rb;
				}
			}

		} else {
			for (SfNavProperty sfnavprop : ent.getNavproperties()) {
				if (sfnavprop.getName().equals(splitRoute[nextlevel])) {
					return rb;
				}
			}

		}
		return rb;
	}

	private String realRelation(String entityName, String realName) {
		SfEntityDAO entDao = new SfEntityDAO();
		SfEntity ent = null;
		try {
			ent = entDao.getByEntityName(entityName);
			for (SfNavProperty sfnavprop : ent.getNavproperties()) {
				if (sfnavprop.getName().equals(realName)) {
					return sfnavprop.getType();
				}
			}
		} catch (NullPointerException e) {
			logger.warn(QueryBuilder.class, "no result for relationship");
		}

		return "";
	}

	private String realMultiplicity(String entityName, String realName) {
		SfEntityDAO entDao = new SfEntityDAO();
		SfEntity ent = null;
		try {
			ent = entDao.getByEntityName(entityName);
			for (SfNavProperty sfnavprop : ent.getNavproperties()) {
				if (sfnavprop.getName().equals(realName)) {
					return sfnavprop.getMultiplicity();
				}
			}

		} catch (NullPointerException e) {
			logger.warn(QueryBuilder.class, "no result for relationship");
		}

		return "";
	}

	private Map<String, ResultBuilderDto> queryFromMap(Map<String, ResultBuilderDto> map, String UserId, String effectiveDate) {
		Map<String, ResultBuilderDto> result = map;

		List<String> variables = new ArrayList<>();
		List<String> expend = new ArrayList<>();
		List<String> filters = new ArrayList<>();

		String level0 = "";
		for (Map.Entry<String, ResultBuilderDto> entry : map.entrySet()) {
			String[] rteFunc = entry.getValue().getUrl().toString().split("\\|");
			entry.getValue().setOrder("");
			if (rteFunc.length > 1) {
				entry.getValue().setUrl(rteFunc[0]);
				entry.getValue().setFunction1(rteFunc[1]);
				if (rteFunc.length > 2) {
					entry.getValue().setFunction2(rteFunc[2]);
				}
				if (rteFunc.length > 3) {
					entry.getValue().setFunction3(rteFunc[3]);
				}
				if (rteFunc.length > 4) {
					entry.getValue().setOrder(rteFunc[4]);
				}
			}
			String route = rteFunc[0];

			if (route.substring(0, 1).equals("/")) {
				route = route.substring(1);
			}
			if (!entry.getValue().getResult().equals("Invalid")) {

				// Split Route and ChangeNodes.

				String[] splitRoute = route.split("\\/");

				String[] valExp = new String[splitRoute.length - 2];
				String[] valVar = new String[splitRoute.length - 1];

				int inc = 0;
				String vari = "";
				String expr = "";

				for (String string : splitRoute) {

					if (inc > 0) {
						String[] splitNodePlusOne = string.split("\\(");
						if (inc < splitRoute.length - 1) {
							valExp[inc - 1] = splitNodePlusOne[0];
						}
						if (inc > 1) {
							vari += "/" + splitNodePlusOne[0];
						} else {
							vari += splitNodePlusOne[0];
						}

						if (splitNodePlusOne.length > 1 && !splitNodePlusOne[1].equals("last)")) {
							String[] splitNodeEqOne = splitNodePlusOne[1].split("\\=");
							variables.add(vari + "/" + splitNodeEqOne[0]);

							expend.add(vari);

						}

						valVar[inc - 1] = splitNodePlusOne[0];
					}
					inc++;
				}

				level0 = splitRoute[0];

				if (!String.join("/", valExp).equals("")) {
					expend.add(String.join("/", valExp));
				}
				variables.add(String.join("/", valVar));
				if (entry.getValue().getFunction1() != null && (entry.getValue().getFunction1().equals("CONCATSPACE") || entry.getValue().getFunction1().equals("CONCATRID") || entry.getValue().getFunction1().equals("CONCATID")
						|| entry.getValue().getFunction1().equals("CONCATIPHEN") || entry.getValue().getFunction1().equals("REPLACEEMPTY") || entry.getValue().getFunction1().equals("CONCATPHONE"))) {
					String[] b = Arrays.copyOf(valVar, valVar.length - 1);
					if (StringUtils.countMatches(entry.getValue().getFunction2(), ".") > 0) {
						String end2 = String.join("/", b) + "/" + entry.getValue().getFunction2().replaceAll("\\.", "/");
						String[] splitPath222 = end2.split("\\/");
						String[] c2 = Arrays.copyOf(splitPath222, splitPath222.length - 1);
						expend.add(String.join("/", c2));
					}
					variables.add(String.join("/", b) + "/" + entry.getValue().getFunction2().replaceAll("\\.", "/"));
				}

				if (entry.getValue().getFunction1() != null && entry.getValue().getFunction1().equals("LOOKUPADRESS")) {
					String[] b = Arrays.copyOf(valVar, valVar.length - 1);
					String[] u = entry.getValue().getFunction2().split(",");
					for (String string : u) {
						variables.add(String.join("/", b) + "/" + string);
					}

				}

			}
		}
		String lvl0ToCall = "";
		if (level0.equals("/PerPerson") || level0.equals("PerPerson")) {
			expend.add("employmentNav");
			expend.add("employmentNav/userNav");
			expend.add("employmentNav/jobInfoNav/emplStatusNav");
			variables.add("employmentNav/userId");
			variables.add("employmentNav/assignmentClass");
			variables.add("employmentNav/jobInfoNav/emplStatusNav/externalCode");
			filters.add("personIdExternal eq '" + UserId + "'");

			for (String string : expend) {
				if (StringUtils.containsIgnoreCase(string, "/managerUserNav")) {
					String[] mun = string.split("/managerUserNav");
					// filters.add("((" + mun[0] + "/managerUserNav/status in 'active','inactive')
					// or " + mun[0] + " eq null)");
				} else if (StringUtils.containsIgnoreCase(string, "/userNav")) {
					String[] un = string.split("/userNav");
					// filters.add(un[0] + "/userNav/status in 'active','inactive'");
				} else if (StringUtils.containsIgnoreCase(string, "/relUserNav")) {
					String[] run = string.split("/relUserNav");
					// filters.add("((" + run[0] + "/relUserNav/status in 'active','inactive') or "
					// + run[0] + " eq null)");
				}

			}

			lvl0ToCall = level0;
		} else {
			lvl0ToCall = level0 + "(" + UserId + ")";
		}

		variables = variables.stream().distinct().collect(Collectors.toList());
		expend = expend.stream().distinct().collect(Collectors.toList());
		filters = filters.stream().distinct().collect(Collectors.toList());
		try {
			String SfFeedback = "";
			if (effectiveDate.equals("")) {
				SfFeedback = HttpConnectorSuccessFactor.getInstance()
						.getResult(lvl0ToCall + "?$select=" + String.join(",", variables) + "&$expand=" + String.join(",", expend) + (filters.size() > 0 ? "&$filter=" + String.join(" and ", filters) : ""));
			} else if (effectiveDate.substring(0, 3).equals("ALL") || effectiveDate.substring(0, 4).equals("ALCO")) {
				String[] rev = effectiveDate.split("\\.");

				SfFeedback = HttpConnectorSuccessFactor.getInstance().getResult(
						level0 + "?&$filter=personIdExternal%20eq%20%27" + UserId + "%27&$select=" + String.join(",", variables) + "&$expand=" + String.join(",", expend) + "&fromDate=1900-01-01&$orderby=" + (rev.length > 1 ? rev[1] : ""));
			} else if (effectiveDate.substring(0, 4).equals("ALID")) {
				String[] rev = effectiveDate.split("\\.");

				SfFeedback = HttpConnectorSuccessFactor.getInstance()
						.getResult(level0 + "?&$filter=userId%20eq%20%27" + UserId + "%27&$select=" + String.join(",", variables) + "&$expand=" + String.join(",", expend) + "&fromDate=1900-01-01&$orderby=" + (rev.length > 1 ? rev[1] : ""));
			} else {
				SfFeedback = HttpConnectorSuccessFactor.getInstance()
						.getResult(lvl0ToCall + "?$select=" + String.join(",", variables) + "&$expand=" + String.join(",", expend) + (filters.size() > 0 ? "&$filter=" + String.join(" and ", filters) : "") + "&asOfDate=" + effectiveDate);
			}
			JSONObject jsonObj;
			if (level0.equals("/PerPerson") || level0.equals("PerPerson")) {
				JSONObject jsonObjArray = new JSONObject(SfFeedback);
				JSONArray jsonObjFromArray = jsonObjArray.getJSONObject("d").getJSONArray("results");
				JSONObject jsonD = new JSONObject();
				jsonD.put("d", jsonObjFromArray.get(0));
				jsonObj = jsonD;
			} else {
				jsonObj = new JSONObject(SfFeedback);
			}
			if (!effectiveDate.equals("") && (effectiveDate.substring(0, 3).equals("ALL") || effectiveDate.substring(0, 4).equals("ALID"))) {

				ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();

				JSONObject resOdata = jsonObj.getJSONObject("d");
				JSONArray objResults = resOdata.getJSONArray("results");

				for (int i = 0; i < objResults.length(); i++) {

					ArrayList<String> colonnes = new ArrayList<String>();
					JSONObject obj = objResults.getJSONObject(i);

					for (Map.Entry<String, ResultBuilderDto> entry : result.entrySet()) {

						String str = recursiveJson(obj, entry.getValue().getUrl(), entry.getValue().getNode());
						if (entry.getValue().getType().equals("DateTime"))
							str = UtilBuilderDateFormat.JSONTarihConvert(str);
						colonnes.add(str);
					}

					lines.add(colonnes);
				}
				ResultBuilderDto rb = new ResultBuilderDto("a", "b", "c");
				rb.setResultArray(lines);

				result.clear();
				result.put("out", rb);

			} else if (!effectiveDate.equals("") && effectiveDate.substring(0, 4).equals("ALCO")) {

				// logger.info(SfFeedback);

				JSONObject resOdata = jsonObj.getJSONObject("d");
				JSONObject objResult = (JSONObject) resOdata.getJSONObject("employmentNav").getJSONArray("results").get(0);

				int startYear = Integer.parseInt(UtilBuilderDateFormat.JSONTarihConvertYear(objResult.getString("startDate")));
				SimpleDateFormat df = new SimpleDateFormat("yyyy");
				Date dt = new Date();
				df.format(dt);
				int endYear = Integer.parseInt(df.format(dt));
				// NON RECURSIVE LOOP

				JSONArray resComNRec = objResult.getJSONObject("empPayCompNonRecurringNav").getJSONArray("results");
				LinkedHashMap<String, List<CompVale>> linkedHashMap = new LinkedHashMap<String, List<CompVale>>();
				for (int i = startYear; i < endYear; i++) {
					List<CompVale> lstrcv = new ArrayList<CompVale>();
					for (int a = 0; a < resComNRec.length(); a++) {
						JSONObject loopres = (JSONObject) resComNRec.get(a);
						if (Integer.parseInt(UtilBuilderDateFormat.JSONTarihConvertYear(loopres.getString("payDate"))) == i) {
							CompVale cp = new CompVale();
							cp.setCompCode(loopres.getString("payComponentCode"));
							cp.setCompCurrency(loopres.getString("currencyCode"));

							String datereip = loopres.getString("payDate").replace("/Date(", "").replace(")/", "");
							Long timeInMillis = Long.valueOf(datereip);
							Calendar c = Calendar.getInstance();
							c.setTimeInMillis(timeInMillis);
							Date dt2 = c.getTime();
							cp.setCompDate(dt2);

							cp.setCompValue(Double.parseDouble(loopres.getString("value")));

							cp.setCompFreq(1);

							lstrcv.add(cp);

						}
					}
					linkedHashMap.put(String.valueOf(i), lstrcv);
				}

				// COMP RECURING LOOP

				JSONArray resComNRec2 = objResult.getJSONObject("compInfoNav").getJSONArray("results");
				for (int i = startYear; i < endYear; i++) {
					for (int a = 0; a < resComNRec2.length(); a++) {
						JSONObject loopres = (JSONObject) resComNRec2.get(a);
						if (Integer.parseInt(UtilBuilderDateFormat.JSONTarihConvertYear(loopres.getString("startDate"))) == i) {
							boolean islast = true;

							for (int b = 0; b < resComNRec2.length(); b++) {
								JSONObject loopresb = (JSONObject) resComNRec2.get(b);
								if (Integer.parseInt(UtilBuilderDateFormat.JSONTarihConvertYear(loopresb.getString("startDate"))) == i
										&& UtilBuilderDateFormat.JSONTarihConvertDate(loopresb.getString("startDate")).after(UtilBuilderDateFormat.JSONTarihConvertDate(loopres.getString("startDate")))) {
									islast = false;
								}
								JSONObject loopresComp = (JSONObject) loopres.getJSONObject("empPayCompRecurringNav").getJSONArray("results").get(0);
								JSONObject loopresCompb = (JSONObject) loopresb.getJSONObject("empPayCompRecurringNav").getJSONArray("results").get(0);
								if (Integer.parseInt(UtilBuilderDateFormat.JSONTarihConvertYear(loopresb.getString("startDate"))) == i
										&& UtilBuilderDateFormat.JSONTarihConvertDate(loopresb.getString("startDate")).equals(UtilBuilderDateFormat.JSONTarihConvertDate(loopres.getString("startDate")))
										&& Integer.parseInt(loopresCompb.getString("seqNumber")) > Integer.parseInt(loopresComp.getString("seqNumber"))) {
									islast = false;
								}
							}
							if (islast) {
								JSONArray loopresCompR = loopres.getJSONObject("empPayCompRecurringNav").getJSONArray("results");

								for (int u = 0; u < loopresCompR.length(); u++) {
									JSONObject loopresComp = (JSONObject) loopresCompR.get(u);

									CompVale cp = new CompVale();
									cp.setCompCode(loopresComp.getString("payComponent"));
									cp.setCompCurrency(loopresComp.getString("currencyCode"));

									String datereip = loopres.getString("startDate").replace("/Date(", "").replace(")/", "");
									Long timeInMillis = Long.valueOf(datereip);
									Calendar c = Calendar.getInstance();
									c.setTimeInMillis(timeInMillis);
									Date dt2 = c.getTime();
									cp.setCompDate(dt2);

									cp.setCompValue(Double.parseDouble(loopresComp.getString("paycompvalue")));

									String datereipend = loopres.getString("endDate").replace("/Date(", "").replace(")/", "");
									Long timeInMillisend = Long.valueOf(datereipend);
									Calendar cend = Calendar.getInstance();
									cend.setTimeInMillis(timeInMillisend);
									Date dt3 = cend.getTime();

									String loopresComp2 = loopresComp.getJSONObject("frequencyNav").getString("annualizationFactor");

									cp.setCompFreq(Integer.parseInt(loopresComp2));

									linkedHashMap.get(String.valueOf(i)).add(cp);
									long diff = dt3.getTime() - dt2.getTime();
									int res = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) / 365;

									if (res > 0) {
										for (int j = 0; j < Math.min(res, endYear - i) - 1; j++) {
											linkedHashMap.get(String.valueOf((i + j + 1))).add(cp);
										}
									}

								}

							}
						}
					}
				}

				// logger.info(result.)

				ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();

				int inteCount = 0;
				for (int i = startYear; i < endYear; i++) {

					ArrayList<String> colonnes = new ArrayList<String>();

					LookupDAO lkdao2 = new LookupDAO();
					Collection<LookupTable> rm = lkdao2.getLookupByTable("table_salary");

					LookupTable rm2 = lkdao2.getLookupByInput("nbcol", "table_salary");

					for (int r = 1; r <= Integer.parseInt(rm2.getValueOut()); r++) {

						String str = "0";

						colonnes.add(str);
						for (LookupTable lookupTable : rm) {
							if (lookupTable.getValueIn().equals("column" + r)) {
								if (lookupTable.getValueOut().equals("@@year")) {
									str = String.valueOf(i);
									colonnes.set(r - 1, str);
								} else if (lookupTable.getValueOut().startsWith("@@previousCell")) {

									if (inteCount > 0) {
										String[] lst = lookupTable.getValueOut().split("@@previousCell\\|column");
										DecimalFormat dfr = new DecimalFormat("#.##");
										double valtmp = Double.parseDouble(lines.get(inteCount - 1).get(Integer.parseInt(lst[1]) - 1));
										double valtmp2 = Double.parseDouble(colonnes.get(Integer.parseInt(lst[1]) - 1));
										colonnes.set(r - 1, dfr.format(((valtmp2 - valtmp) / valtmp) * 100));
									} else {
										colonnes.set(r - 1, "-");
									}

								} else {
									List<CompVale> cvl = linkedHashMap.get(String.valueOf(i));

									for (CompVale compVale : cvl) {
										String[] lst = lookupTable.getValueOut().split("@@");
										if (lst[0].equals(compVale.getCompCode())) {
											DecimalFormat dfr = new DecimalFormat("#.##");
											double valtmp = Double.parseDouble(colonnes.get(r - 1));
											int freq = 1;
											if (lst.length >= 2 && lst[1].equals("multiply|compFreq")) {
												freq = compVale.getCompFreq();
											}
											double val = valtmp + (compVale.getCompValue() * freq);
											colonnes.set(r - 1, dfr.format(val));
										}
									}
								}
							}
						}

					}

					lines.add(colonnes);
					inteCount++;
				}

				ResultBuilderDto rb = new ResultBuilderDto("a", "b", "c");
				Collections.reverse(lines);
				rb.setResultArray(lines);

				result.clear();
				result.put("out", rb);

			} else {

				for (Map.Entry<String, ResultBuilderDto> entry : result.entrySet()) {

					String str = "";

					if ((level0.equals("/PerPerson") || level0.equals("PerPerson"))) {

						JSONObject resOdata = jsonObj.getJSONObject("d");
						JSONObject empJobs = resOdata.getJSONObject("employmentNav");
						JSONArray empJobResults = empJobs.getJSONArray("results");

						if (empJobResults.length() > 1) {

							Boolean isGA = false;
							Boolean isNotGA = false;
							String userToAddGA = "";
							String userToAdd = "";

							for (int i = 0; i < empJobResults.length(); i++) {
								JSONObject obj = empJobResults.getJSONObject(i);

								JSONArray empJobInfoStatus0 = obj.getJSONObject("jobInfoNav").getJSONArray("results");

								if (empJobInfoStatus0.length() > 0) {
									JSONObject empJobStatus0 = empJobInfoStatus0.getJSONObject(0).getJSONObject("emplStatusNav");

									if (obj.getString("assignmentClass").equals("GA") && empJobStatus0.getString("externalCode").equals("A")) {
										isGA = true;
										userToAddGA = obj.getString("userId");
									}
									if (!obj.getString("assignmentClass").equals("GA") && empJobStatus0.getString("externalCode").equals("A")) {
										isNotGA = true;
										userToAdd = obj.getString("userId");
									}
								}
							}

							if (isNotGA) {
								entry.getValue().setUrl(entry.getValue().getUrl().replaceAll("employmentNav", "employmentNav(userId='" + userToAdd + "')"));
							}
							if (isGA && entry.getValue().getFunction1() != null && entry.getValue().getFunction1().equals("withGBEmp")) {
								entry.getValue().setUrl(entry.getValue().getUrl().replaceAll("employmentNav", "employmentNav(userId='" + userToAddGA + "')"));
							}
						}
					}

					if (entry.getValue().getFunction1() != null && entry.getValue().getFunction1().equals("COUNT")) {

						// logger.info(entry.getValue().getUrl());

						str = recursiveJsonCount(jsonObj.getJSONObject("d"), entry.getValue().getUrl(), entry.getValue().getNode());

					} else {

						str = recursiveJson(jsonObj.getJSONObject("d"), entry.getValue().getUrl(), entry.getValue().getNode());
					}

					// Return result
					if (str != "null") {
						switch (entry.getValue().getType()) {
						case "DateTime":
							this.toDateTime(str, entry);
							entry.getValue().setStatus("Success");
							break;
						case "Double":
							NumberFormat nf = new DecimalFormat("#.####");

							if (str == "") {
								entry.getValue().setResult("");
							} else if (entry.getValue().getFunction1() != null) {
								str = this.toFunction(str, entry.getValue().getFunction1(), UserId, entry, jsonObj);
								entry.getValue().setResult(str);
							} else {
								entry.getValue().setResult(nf.format(Double.parseDouble(str)));
							}
							entry.getValue().setStatus("Success");
						case "":
							break;
						default:

							str = this.toFunction(str, entry.getValue().getFunction1(), UserId, entry, jsonObj);
							if (entry.getValue().getFunction1() != null && !entry.getValue().getFunction1().equals("TABLUSERID")) {
								str = this.toFunction(str, entry.getValue().getFunction2(), UserId, entry, jsonObj);
							}

							if (entry.getValue().getFunction1() != null && (entry.getValue().getFunction1().equals("today") || entry.getValue().getFunction1().equals("year") || entry.getValue().getFunction1().equals("usFormat")
									|| entry.getValue().getFunction1().equals("todayUS") || entry.getValue().getFunction1().equals("genericFormat"))) {
								this.toDateTime(str, entry);
							}

							entry.getValue().setStatus("Success");

							break;
						}
					} else {
						str = "";
						if (entry.getValue().getFunction1() != null
								&& (entry.getValue().getFunction1().equals("LOOKUPDEFAULT") || entry.getValue().getFunction1().equals("LOOKUPADRESS") || entry.getValue().getFunction1().equals("REPLACEEMPTY"))) {
							str = this.toFunction(str, entry.getValue().getFunction1(), UserId, entry, jsonObj);
							entry.getValue().setStatus("Success");
						} else {
							entry.getValue().setStatus("Error");
						}
						entry.getValue().setResult(str);
					}
				}
			}

		} catch (IOException | InvalidResponseException ex) {
			logger.error(QueryBuilder.class, "queryFromMap error " + ex.getMessage());
		}
		return result;
	}

	private String recursiveJson(JSONObject jsonObj, String url, Map<String, String> nodes) {
		if (url.substring(0, 1).equals("/")) {
			url = url.substring(1);
		}
		if (!url.replaceAll("\\s", "").equals("")) {
			String[] splitRoute = url.split("\\/");
			String[] yourArray = Arrays.copyOfRange(splitRoute, 1, splitRoute.length);
			String[] splitNodePlusOne = (yourArray != null && yourArray.length > 0) ? (yourArray[0].split("\\(")) : null;
			if (yourArray != null && yourArray.length > 1 && nodes.get(splitNodePlusOne[0]).equals("*")) {
				JSONObject rel = jsonObj.getJSONObject((splitNodePlusOne[0]));
				JSONArray ret = rel.getJSONArray("results");
				if (splitNodePlusOne.length > 1 && !splitNodePlusOne[1].equals("last)")) {
					String[] splitNodeEqOne = splitNodePlusOne[1].split("\\=");
					String result = StringUtils.substringBetween(splitNodeEqOne[1], "'", "'");
					for (int n = 0; n < ret.length(); n++) {
						JSONObject object = ret.getJSONObject(n);
						Object aObj = object.get(splitNodeEqOne[0]);
						if (String.valueOf(aObj).equals(result)) {
							return recursiveJson(object, String.join("/", yourArray), nodes);
						}
					}
					return "";
				} else if (splitNodePlusOne.length > 1 && splitNodePlusOne[1].equals("last)")) {
					if (ret.length() > 0) {
						JSONObject end = ret.getJSONObject(ret.length() - 1);
						return recursiveJson(end, String.join("/", yourArray), nodes);
					} else {
						return "";
					}
				} else {
					if (ret.length() > 0) {
						JSONObject end = ret.getJSONObject(0);
						return recursiveJson(end, String.join("/", yourArray), nodes);
					} else {
						return "";
					}
				}

			} else if (yourArray.length == 1) {
				String[] splitNodePlusOne2 = yourArray[0].split("\\|");
				return jsonObj.get(splitNodePlusOne2[0]).toString();
			} else if (splitNodePlusOne != null) {
				if (jsonObj.has(splitNodePlusOne[0]) && !jsonObj.isNull(splitNodePlusOne[0])) {
					JSONObject rel = jsonObj.getJSONObject((splitNodePlusOne[0]));
					return recursiveJson(rel, String.join("/", yourArray), nodes);
				} else {
					return "";
				}
			}
		} else {
			return "";
		}

		return "";
	}

	private String recursiveJsonCount(JSONObject jsonObj, String url, Map<String, String> nodes) {
		if (url.substring(0, 1).equals("/")) {
			url = url.substring(1);
		}
		if (!url.replaceAll("\\s", "").equals("")) {
			String[] splitRoute = url.split("\\/");
			String[] yourArray = Arrays.copyOfRange(splitRoute, 1, splitRoute.length);
			String[] splitNodePlusOne = (yourArray != null && yourArray.length > 0) ? (yourArray[0].split("\\(")) : null;
			if (yourArray != null && yourArray.length > 2 && nodes.get(splitNodePlusOne[0]).equals("*")) {
				JSONObject rel = jsonObj.getJSONObject((splitNodePlusOne[0]));
				JSONArray ret = rel.getJSONArray("results");
				if (splitNodePlusOne.length > 1 && !splitNodePlusOne[1].equals("last)")) {
					String[] splitNodeEqOne = splitNodePlusOne[1].split("\\=");
					String result = StringUtils.substringBetween(splitNodeEqOne[1], "'", "'");
					for (int n = 0; n < ret.length(); n++) {
						JSONObject object = ret.getJSONObject(n);
						Object aObj = object.get(splitNodeEqOne[0]);
						if (String.valueOf(aObj).equals(result)) {
							return recursiveJson(object, String.join("/", yourArray), nodes);
						}
					}
					return "";
				} else if (splitNodePlusOne.length > 1 && splitNodePlusOne[1].equals("last)")) {
					if (ret.length() > 0) {
						JSONObject end = ret.getJSONObject(ret.length() - 1);
						return recursiveJson(end, String.join("/", yourArray), nodes);
					} else {
						return "";
					}
				} else {
					if (ret.length() > 0) {
						JSONObject end = ret.getJSONObject(0);
						return recursiveJson(end, String.join("/", yourArray), nodes);
					} else {
						return "";
					}
				}

			} else if (yourArray.length == 2) {
				String[] splitNodePlusOne3 = yourArray[0].split("\\(");
				JSONObject objR = (JSONObject) jsonObj.get(splitNodePlusOne3[0]);
				JSONArray ret2 = objR.getJSONArray("results");
				int count = 0;

				for (int n = 0; n < ret2.length(); n++) {
					JSONObject object = ret2.getJSONObject(n);
					String[] splitNodeEqOne = splitNodePlusOne[1].split("\\=");
					String result = StringUtils.substringBetween(splitNodeEqOne[1], "'", "'");
					if (object.get(splitNodeEqOne[0]).equals(result)) {
						count++;
					}
				}

				return String.valueOf(count);
			} else if (splitNodePlusOne != null) {
				if (jsonObj.has(splitNodePlusOne[0]) && !jsonObj.isNull(splitNodePlusOne[0])) {
					JSONObject rel = jsonObj.getJSONObject((splitNodePlusOne[0]));
					return recursiveJson(rel, String.join("/", yourArray), nodes);
				} else {
					return "";
				}
			}
		} else {
			return "";
		}

		return "";
	}

	@SuppressWarnings("static-access")
	private String toFunction(String in, String function, String userid, Map.Entry<String, ResultBuilderDto> entry, JSONObject jsonObj) {
		String out = "";
		// logger.info("in :" + in);
		if (function != null) {
			switch (function) {
			case "COMPAREMAIL":
				if (in.contains("nomail") || in.contains("noemail") || in.equals("") || (entry.getValue().getFunction2() != null && in.contains(entry.getValue().getFunction2()))) {
					// get email format
					SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
					AdminParameters paramAdminEmailFormatTermi = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_FORMAT_MAILUSERTERM_PPD.getCode());
					// transform email format
					out = "noemail_" + userid + "@test.com";
					if (paramAdminEmailFormatTermi != null && paramAdminEmailFormatTermi.getValue() != null && !paramAdminEmailFormatTermi.getValue().equals("")) {
						out = paramAdminEmailFormatTermi.getValue().replace("??", userid);
					}
				} else {
					out = in;
				}
				break;
			case "LISTFAIL":
				String strTMP34 = in;
				strTMP34 = strTMP34.split(",")[0];
				out = strTMP34;
				break;
			case "LOOKUP":
				LookupDAO lkdao = new LookupDAO();
				LookupTable lk = lkdao.getLookupByInput(in, entry.getValue().getFunction2());
				out = lk.getValueOut();
				break;
			case "LOOKUPDEFAULT":
				LookupDAO lkdao2 = new LookupDAO();
				LookupTable lk2 = lkdao2.getLookupByInput(in, entry.getValue().getFunction2());
				out = lk2.getValueOut();

				if (out.equals("")) {
					out = entry.getValue().getFunction3();
				}

				break;
			case "LOOKUPADRESS":
				LookupDAO lkdaorrr = new LookupDAO();
				String[] splitPathCountry = entry.getValue().getUrl().split("\\/");
				String[] bCountry = Arrays.copyOf(splitPathCountry, splitPathCountry.length - 1);
				String endCountry = String.join("/", bCountry) + "/" + "country";
				String countryField = recursiveJson(jsonObj.getJSONObject("d"), endCountry, entry.getValue().getNode());
				LookupTable lkrrr = lkdaorrr.getLookupByInput(countryField + "@@" + entry.getKey(), "AddressCountry");

				String[] listFieldsCountry = lkrrr.getValueOut().split(",");

				out = "";
				for (String string : listFieldsCountry) {
					String endCountryL = String.join("/", bCountry) + "/" + string;
					String countryFieldL = recursiveJson(jsonObj.getJSONObject("d"), endCountryL, entry.getValue().getNode());
					if (string.equals(listFieldsCountry[listFieldsCountry.length - 1])) {
						if (!countryFieldL.equals("null"))
							out += countryFieldL;
					} else {
						if (!countryFieldL.equals("null"))
							out += countryFieldL + " ";
					}
				}

				break;
			case "EMPTYDEFAULT":
				if (in.equals("")) {
					out = entry.getValue().getFunction2();
				} else {
					out = in;
				}

				break;
			case "LOOKEMPTYUP":
				String strTMP00 = in;
				if (strTMP00.equals("")) {
					String end = entry.getValue().getFunction2();
					String strTMP21 = recursiveJson(jsonObj.getJSONObject("d"), end.replace(".", "/"), entry.getValue().getNode());
					if (strTMP21.equals("")) {
						out = "";
					} else {
						LookupDAO lkdao3 = new LookupDAO();
						LookupTable lk3 = lkdao3.getLookupByInput(strTMP21, entry.getValue().getFunction3());
						out = lk3.getValueOut();
					}
				} else {
					out = strTMP00;
				}
				break;
			case "CONCATSPACE":
				String strTMP1 = in;
				String[] splitPath2 = entry.getValue().getUrl().split("\\/");
				String[] b = Arrays.copyOf(splitPath2, splitPath2.length - 1);
				String end = String.join("/", b) + "/" + entry.getValue().getFunction2();
				String strTMP2 = recursiveJson(jsonObj.getJSONObject("d"), end, entry.getValue().getNode());
				if (strTMP1.equals("") && strTMP2.equals("")) {
				} else {
					out = strTMP1 + " " + strTMP2;
				}
				break;
			case "REPLACEEMPTY":
				String strTMPRE = in;
				String[] splitPath2RE = entry.getValue().getUrl().split("\\/");
				String[] bRE = Arrays.copyOf(splitPath2RE, splitPath2RE.length - 1);
				String endRE = String.join("/", bRE) + "/" + entry.getValue().getFunction2();
				String strTMP2RE = recursiveJson(jsonObj.getJSONObject("d"), endRE, entry.getValue().getNode());
				if (strTMP2RE.equals("") || strTMP2RE.equals("null")) {
					out = strTMPRE;
				} else {
					strTMP2RE = strTMP2RE.replace("(", "");
					strTMP2RE = strTMP2RE.replace(")", "");
					out = strTMP2RE;
				}
				break;
			case "CONCATID":
				String strTMP11 = in;
				String[] splitPath22 = entry.getValue().getUrl().split("\\/");
				String[] b2 = Arrays.copyOf(splitPath22, splitPath22.length - 1);
				String end2 = String.join("/", b2) + "/" + entry.getValue().getFunction2();
				String strTMP22 = recursiveJson(jsonObj.getJSONObject("d"), end2, entry.getValue().getNode());
				if (strTMP11.equals("") && strTMP22.equals("")) {
				} else {
					out = strTMP11 + " (" + strTMP22 + ")";
				}
				break;
			case "CONCATRID":
				String rstrTMP11 = in;
				String[] rsplitPath22 = entry.getValue().getUrl().split("\\/");
				String[] rb2 = Arrays.copyOf(rsplitPath22, rsplitPath22.length - 1);
				String rend2 = String.join("/", rb2) + "/" + entry.getValue().getFunction2().replaceAll("\\.", "/");

				if (StringUtils.countMatches(entry.getValue().getFunction2(), ".") > 0) {
					String[] rrsplitPath22 = entry.getValue().getFunction2().split("\\.");
					String[] rrb2 = Arrays.copyOf(rrsplitPath22, rrsplitPath22.length - 1);
					for (String string : rrb2) {
						if (string.equals("picklistLabels"))
							entry.getValue().getNode().put(string, "*");
					}
				}
				String rstrTMP22 = recursiveJson(jsonObj.getJSONObject("d"), rend2, entry.getValue().getNode());
				if (rstrTMP11.equals("") && rstrTMP22.equals("")) {
				} else {
					out = rstrTMP22 + " (" + rstrTMP11 + ")";
				}
				break;
			case "CONCATPHONE":

				String strTMPPhone = in;
				String[] splitPathPhone = entry.getValue().getUrl().split("\\/");
				String[] bPhone = Arrays.copyOf(splitPathPhone, splitPathPhone.length - 1);
				String endPhone = String.join("/", bPhone) + "/" + entry.getValue().getFunction2();
				String strTMPPhoneR = recursiveJson(jsonObj.getJSONObject("d"), endPhone, entry.getValue().getNode());

				if (strTMPPhone.equals("") && strTMPPhoneR.equals("")) {
				} else {
					out = strTMPPhone.replace("+", "00").trim() + strTMPPhoneR.replaceAll("-", "").trim();
				}
				break;
			case "CONCATIPHEN":
				String strTMP111 = in;
				String[] splitPath222 = entry.getValue().getUrl().split("\\/");
				String[] b22 = Arrays.copyOf(splitPath222, splitPath222.length - 1);
				String end22 = String.join("/", b22) + "/" + entry.getValue().getFunction2();
				String strTMP222 = recursiveJson(jsonObj.getJSONObject("d"), end22, entry.getValue().getNode());
				if (strTMP111.equals("") && strTMP222.equals("")) {
				} else {
					out = strTMP111 + " - " + strTMP222;
				}
				break;
			case "TRIM":
				String strTMP33 = in;
				strTMP33 = strTMP33.replaceAll("\\s", "");
				out = strTMP33;
				break;
			case "iso2":
				out = UtilBuilderFunctions.iso3CountryCodeToIso2CountryCode(in);
				break;
			case "iso2lowerCase":
				out = UtilBuilderFunctions.iso3CountryCodeToIso2CountryCodeLowerCase(in);
				break;
			case "x100":
				NumberFormat nf = new DecimalFormat("#.####");
				out = nf.format(Double.parseDouble((in != "" ? in : "0")) * 100);
				break;
			case "numNL":
				NumberFormat nf2 = new DecimalFormat("###,###.##");
				out = nf2.getInstance(Locale.GERMANY).format(Double.parseDouble((in != "" ? in : "0")));
				break;
			case "numNLUK":
				NumberFormat nf4 = new DecimalFormat("###,###.##");
				out = nf4.format(Double.parseDouble((in != "" ? in : "0")));
				break;
			case "numCH":
				NumberFormat nf3 = new DecimalFormat("#,###");
				out = nf3.format(Double.parseDouble((in != "" ? in : "0")));
				break;
			case "TABLPERONID":
				out = toTable(entry.getValue().getFunction3(), entry.getValue().getFunction2(), in, entry.getValue().getOrder(), "ALL");
				break;
			case "TABLUSERID":
				out = toTable(entry.getValue().getFunction3(), entry.getValue().getFunction2(), in, entry.getValue().getOrder(), "ALID");
				break;
			case "TABLCOMP":
				out = toTable(entry.getValue().getFunction3(), entry.getValue().getFunction2(), in, entry.getValue().getOrder(), "ALCO");
				break;
			default:
				out = in;
				break;
			}
		} else {
			out = in;
		}

		entry.getValue().setResult(out);
		// logger.gson("out :" + out);
		return out;
	}

	public static String replaceLast(String find, String replace, String string) {
		int lastIndex = string.lastIndexOf(find);

		if (lastIndex == -1) {
			return string;
		}

		String beginString = string.substring(0, lastIndex);
		String endString = string.substring(lastIndex + find.length());

		return beginString + replace + endString;
	}

	private String toTable(String function, String params, String userid, String order, String node) {

		LinkedHashMap<String, ResultBuilderDto> map = new LinkedHashMap<String, ResultBuilderDto>();

		String[] sp = params.split("\\,");
		int vr = 0;
		for (String st : sp) {
			map.put(String.valueOf(vr), new ResultBuilderDto(function + "/" + st.replace(".", "/"), "default", ""));
			vr++;
		}

		Map<String, ResultBuilderDto> mapRes = QueryBuilder.getInstance().convert(map, userid, node + "." + order);

		UtilMapping utilMap = new UtilMapping();
		ResultBuilderDto result = (ResultBuilderDto) mapRes.get("out");
		String response = utilMap.getTableTemplatePdd(result);

		return response;
	}

	private Map.Entry<String, ResultBuilderDto> toDateTime(String str, Map.Entry<String, ResultBuilderDto> entry) {
		if (objToStr(str).equals("")) {
			entry.getValue().setResult(str);
		} else {
			str = str.replace("+0000)/", ")/");
			String function1 = entry.getValue().getFunction1();
			String function2 = entry.getValue().getFunction2();
			if (function1 != null) {
				switch (function1) {
				case "Age":
					String datereip = str.replace("/Date(", "").replace(")/", "");
					Long timeInMillis = Long.valueOf(datereip);
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(timeInMillis);
					Date dt = c.getTime();
					Date date2 = new Date();
					long diff = date2.getTime() - dt.getTime();
					double test = (double) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) / 365;
					DecimalFormat test2 = new DecimalFormat("#.#");
					entry.getValue().setResult(test2.format(test));
					break;
				case "day":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertDay(str));
					break;
				case "month":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertMonth(str));
					break;
				case "year":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertYear(str));
					break;
				case "addyear":
					String yr = UtilBuilderDateFormat.JSONTarihConvertYear(str);
					int yrnum = Integer.parseInt(yr);
					int addnum = Integer.parseInt(function2);
					entry.getValue().setResult(String.valueOf((yrnum + addnum)));
					break;
				case "remyear":
					String yr2 = UtilBuilderDateFormat.JSONTarihConvertYear(str);
					int yrnum2 = Integer.parseInt(yr2);
					int remnum = Integer.parseInt(function2);
					entry.getValue().setResult(String.valueOf((yrnum2 - remnum)));
					break;
				case "today":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertToday());
					break;
				case "todayUS":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayUS());
					break;
				case "todayUK":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayUK());
					break;
				case "todayM":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayM());
					break;
				case "todayNL":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayNL());
					break;
				case "todayFR":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayFR());
					break;
				case "todayHU":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayHU());
					break;
				case "todayFullUS":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayFullUS());
					break;
				case "todayFullHU":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayFullHU());
					break;
				case "todayFullZH":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayFullZH());
					break;
				case "todayYear":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayYear());
					break;
				case "todayMonth":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayMonth());
					break;
				case "todayGeneric":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayGENERIC(function2));
					break;
				case "todayDay":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertTodayDay());
					break;
				case "usFormat":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertUS(null, str));
					break;
				case "ukFormat":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertUK(null, str));
					break;
				case "nlFormat":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertNL(null, str));
					break;
				case "frFormat":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertFR(null, str));
					break;
				case "huFormat":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertHU(null, str));
					break;
				case "zhFullFormat":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertFullZH(null, str));
					break;
				case "huFullFormat":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertFullHU(null, str));
					break;
				case "usFullFormat":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertFullUS(null, str));
					break;
				case "MFormat":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertM(null, str));
					break;
				case "genericFormat":
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertGENERIC(null, str, function2));
					break;
				case "dateFormat1":
					// format 1: YYYY-MM-DD
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertUS(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(), str));
					break;
				case "dateFormat2":
					// format 2: yyyy-MM-dd HH:mm
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvertUS(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), str));
					break;
				default:
					entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvert(str));
					break;
				}
			} else {
				entry.getValue().setResult(UtilBuilderDateFormat.JSONTarihConvert(str));
			}

		}
		return entry;

	}

	private String objToStr(Object result) {
		if (result == null) {
			return "";
		} else {
			return result.toString();

		}

	}

}