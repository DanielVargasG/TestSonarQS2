package edn.cloud.sfactor.business.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilFiles;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.GroupRule;
import edn.cloud.business.dto.GroupRuleValue;
import edn.cloud.business.dto.SFUserPhotoDto;
import edn.cloud.business.dto.integration.SFAdmin;
import edn.cloud.business.dto.integration.SFAdminList;
import edn.cloud.business.dto.integration.SFAdminListU;
import edn.cloud.business.dto.integration.SFGroupList;
import edn.cloud.business.dto.integration.SFPhotoDto;
import edn.cloud.business.dto.integration.SFRoleList;
import edn.cloud.business.dto.integration.SFUserDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.integration.UserInfo;
import edn.cloud.business.dto.integration.attach.SFAttachInfoN4Dto;
import edn.cloud.business.dto.integration.attach.SFAttachResponseN1Dto;
import edn.cloud.sfactor.business.connectivity.HttpConnectorSuccessFactor;
import edn.cloud.sfactor.business.connectivity.HttpConnectorSuccessFactorOnb;
import edn.cloud.sfactor.business.interfaces.SuccessFactor;
import edn.cloud.sfactor.persistence.dao.EmployeeDAO;
import edn.cloud.sfactor.persistence.dao.LoggerDAO;
import edn.cloud.sfactor.persistence.dao.LookupDAO;
import edn.cloud.sfactor.persistence.dao.QueryOdataDAO;
import edn.cloud.sfactor.persistence.dao.SignatureFileControlDAO;
import edn.cloud.sfactor.persistence.dao.SignatureFileControlDetailDAO;
import edn.cloud.sfactor.persistence.entities.Employee;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.LoggerAction;
import edn.cloud.sfactor.persistence.entities.LookupTable;
import edn.cloud.sfactor.persistence.entities.SignatureFileControl;
import edn.cloud.sfactor.persistence.entities.SignatureFileControlDetail;

public class SuccessFactorImpl implements SuccessFactor {
	private UtilLogger loggerSingle = UtilLogger.getInstance();
	private UtilMapping utilMapping = new UtilMapping();

	/**
	 * save logger control info
	 * 
	 * @param String
	 *            code
	 * @param String
	 *            message
	 * @param String
	 *            user
	 * @param String
	 *            status
	 * @return Long
	 */
	public Long saveLoggerControl(String code, String message, String user, String status) {
		LoggerDAO list = new LoggerDAO();
		LoggerAction li = new LoggerAction();
		li.setCode(code);
		li.setMessage(message);
		li.setUser(user);
		li.setStatus(status);
		if (li.getUser() != null) {
			list.saveNew(li);
		}
		return li.getId();
	}

	@Override
	/**
	 * get User From SoucessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public SFAdminList getAdminList(String groupame) {
		try {
			String response = HttpConnectorSuccessFactor.getInstance().executeGET(getAdminQuery(groupame));
			return utilMapping.loadSFAdminsFromJsom(response);
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	@Override
	/**
	 * get User From SoucessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public SFAdmin getQueryAdminList(String listofusers) {
		try {
			String response = HttpConnectorSuccessFactor.getInstance().executeGET(getListAdminQuery(listofusers));
			// return null;
			SFAdminListU sflist = utilMapping.loadSFAdminUFromJsom(response);

			if (sflist.getD().getResults().size() > 0) {
				return sflist.getD().getResults().get(0);
			} else {

				return null;
			}

		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}
	
	@Override
	/**
	 * get User From SoucessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public List<SFAdmin> getQueryAdminList2(String listofusers) {
		try {
			String response = HttpConnectorSuccessFactor.getInstance().executeGET(getListAdminQuery2(listofusers));
			// return null;
			SFAdminListU sflist = utilMapping.loadSFAdminUFromJsom(response);

			if (sflist.getD().getResults().size() > 0) {
				return sflist.getD().getResults();
			} else {
				return null;
			}

		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	@Override
	/**
	 * get User From SoucessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public GroupRule queryGroupParamsList(String groupId) {
		try {
			String response = HttpConnectorSuccessFactor.getInstance().executeGET(getGroupParamQuery(groupId));
			//

			JSONObject jsonObj = new JSONObject(response);

			if (jsonObj.isNull("d")) {
				return null;
			} else {
				GroupRule groupParam = new GroupRule();
				JSONObject jsonObjD = jsonObj.getJSONObject("d");
				LookupDAO lkdao = new LookupDAO();
				if (!jsonObjD.isNull("dgExcludePools")) {
					JSONObject jsonObjExcl = jsonObjD.getJSONObject("dgExcludePools");
					JSONArray jsonObjExclArray = jsonObjExcl.getJSONArray("results");

					for (int i = 0; i < jsonObjExclArray.length(); i++) {
						JSONObject loop1 = jsonObjExclArray.getJSONObject(i);

						JSONArray loop1Array = loop1.getJSONObject("filters").getJSONArray("results");

						for (int j = 0; j < loop1Array.length(); j++) {
							JSONObject loop2 = loop1Array.getJSONObject(j);

							GroupRuleValue groupVal = new GroupRuleValue();
							groupVal.setKey(loop2.getJSONObject("field").getString("name").replaceAll("std_", ""));
							groupVal.setToken("eq");
							groupVal.setValue("");

							JSONArray loop3Array = loop2.getJSONObject("expressions").getJSONArray("results");

							for (int k = 0; k < loop3Array.length(); k++) {

								JSONObject loop4 = loop3Array.getJSONObject(k);
								JSONArray loop4Array = loop4.getJSONObject("values").getJSONArray("results");

								LookupTable lk4 = lkdao.getLookupByInput("@@", "mappingGroupValue@@" + groupVal.getKey());

								
								
								for (int l = 0; l < loop4Array.length(); l++) {
									JSONObject loop5 = loop4Array.getJSONObject(l);
									
									if (lk4.getValueOut().equals("CODE")) {
										String answer = loop5.getString("fieldValue").substring(loop5.getString("fieldValue").lastIndexOf("(") + 1, loop5.getString("fieldValue").lastIndexOf(")"));
										groupVal.setValue((groupVal.getValue().equals("") ? answer : groupVal.getValue() + ", " + answer));
									}else {
										groupVal.setValue((groupVal.getValue().equals("") ? loop5.getString("fieldValue") : groupVal.getValue() + ", " + loop5.getString("fieldValue")));
									}
									
									
								}

							}

							LookupTable lk3 = lkdao.getLookupByInput(groupVal.getValue(), "mappingGroupValue@@" + groupVal.getKey());

							if (!lk3.getValueOut().equals(""))
								groupVal.setValue(lk3.getValueOut());

							

							groupParam.setExclude(groupVal);

						}

					}

				}

				if (!jsonObjD.isNull("dgIncludePools")) {
					JSONObject jsonObjIncl = jsonObjD.getJSONObject("dgIncludePools");
					JSONArray jsonObjInclArray = jsonObjIncl.getJSONArray("results");

					for (int i = 0; i < jsonObjInclArray.length(); i++) {
						JSONObject loop1 = jsonObjInclArray.getJSONObject(i);

						JSONArray loop1Array = loop1.getJSONObject("filters").getJSONArray("results");

						for (int j = 0; j < loop1Array.length(); j++) {
							JSONObject loop2 = loop1Array.getJSONObject(j);

							GroupRuleValue groupVal = new GroupRuleValue();
							groupVal.setKey(loop2.getJSONObject("field").getString("name").replaceAll("std_", ""));
							groupVal.setToken("eq");
							groupVal.setValue("");

							JSONArray loop3Array = loop2.getJSONObject("expressions").getJSONArray("results");

							for (int k = 0; k < loop3Array.length(); k++) {

								JSONObject loop4 = loop3Array.getJSONObject(k);
								JSONArray loop4Array = loop4.getJSONObject("values").getJSONArray("results");
								
								LookupTable lk14 = lkdao.getLookupByInput("@@", "mappingGroupValue@@" + groupVal.getKey());

								
								
								for (int l = 0; l < loop4Array.length(); l++) {
									JSONObject loop5 = loop4Array.getJSONObject(l);
									
									if (lk14.getValueOut().equals("CODE")) {
										String answer = loop5.getString("fieldValue").substring(loop5.getString("fieldValue").lastIndexOf("(") + 1, loop5.getString("fieldValue").lastIndexOf(")"));
										//groupVal.setValue(answer);
										
										groupVal.setValue((groupVal.getValue().equals("") ? answer : groupVal.getValue() + ", " + answer));
									}else {
										groupVal.setValue((groupVal.getValue().equals("") ? loop5.getString("fieldValue") : groupVal.getValue() + ", " + loop5.getString("fieldValue")));
									}
									
									
								}

							}

							// loggerSingle.info("______________");
							// loggerSingle.info(groupVal.getValue());
							// loggerSingle.info("mappingGroupValue@@" + groupVal.getKey());

							LookupTable lk12 = lkdao.getLookupByInput(groupVal.getValue(), "mappingGroupValue@@" + groupVal.getKey());

							// loggerSingle.info(lk12.getValueOut());

							if (!lk12.getValueOut().equals("")) {
								groupVal.setValue(lk12.getValueOut());
							}

							

							groupParam.setInclude(groupVal);

						}

					}

				}
				return groupParam;
			}
			// return utilMapping.loadSFAdminFromJsom(response);
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	@Override
	/**
	 * get User From SoucessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public SFUserDto getUserProfile(String userName, String date) {
		try {
			String response = HttpConnectorSuccessFactor.getInstance().executeGET(getProfileQuery(userName, date));
			return utilMapping.loadSFUserProfileFromJsom(response);
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	/**
	 * get User From SoucessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public String getUserHrCount(String userName, String date) {
		try {
			String response = HttpConnectorSuccessFactor.getInstance().executeGET(getHrCountQuery(userName, date));
			return response;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return "0";
		}
	}

	/**
	 * get User From SoucessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public String getUserManagerCount(String userName, String date) {
		try {
			String response = HttpConnectorSuccessFactor.getInstance().executeGET(getManagerCountQuery(userName, date));
			return response;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return "0";
		}
	}

	/**
	 * get employee by user HR
	 * 
	 * @param String
	 *            userHR
	 * @return Employee
	 */
	public Employee getEmployeeByUserHR(String userHR) {
		EmployeeDAO userDAO = new EmployeeDAO();
		Employee employee = userDAO.getByUserId(userHR);

		return employee;
	}

	/**
	 * get team
	 * 
	 * @param String
	 *            hrSFUserName
	 */
	public List<SFUserPhotoDto> getTeam(String hrSFUserName, String onlyActive, String namesource) {
		try {

			String userListJson;
			if (onlyActive.equals("true")) {
				userListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getTm(hrSFUserName, namesource));

			} else {
				userListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getTmInactive(hrSFUserName, namesource));

			}
			return utilMapping.loadSFUserProfileListFromJsom(userListJson);
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	/**
	 * get search
	 * 
	 * @param String
	 *            hrSFUserName
	 */
	public List<SFUserPhotoDto> getSearch(String hrSFUserName, String onlyActive) {
		try {
			String userListJson;
			if (onlyActive.equals("true")) {
				userListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getSrch(hrSFUserName));
			} else {
				userListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getSrchInactive(hrSFUserName));
			}
			return utilMapping.loadSFUserProfileListFromJsom(userListJson);
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	/**
	 * get role group ID
	 * 
	 * @param String
	 *            hrSFUserName
	 */
	public SFRoleList getGroupId(String groupName) {
		try {
			String userListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getGroup(groupName));
			return utilMapping.loadSFUserRoleListFromJsom(userListJson);
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	public SFGroupList getGroupList(String userName) {
		try {
			String groupListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getGroupsByUserQuery(userName));
			return utilMapping.loadSFGroupListFromJsom(groupListJson);
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	/**
	 * get team
	 * 
	 * @param String
	 *            hrSFUserName
	 */
	public List<SFUserPhotoDto> getMTeam(String hrSFUserName, String onlyActive) {
		try {
			String userListJson;
			if (onlyActive.equals("true")) {
				userListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getMTm(hrSFUserName));

			} else {
				userListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getMTmInactive(hrSFUserName));

			}
			return utilMapping.loadSFUserProfileListFromJsom(userListJson);
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	/**
	 * get photo
	 * 
	 * @param SFPhotoDto
	 *            SFUserName
	 */
	public Object hasKeyGetValue(String key) {
		try {
			Object valueReturn = HttpConnectorSuccessFactor.getInstance().hasKeyGetValue(key);
			return valueReturn;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	/**
	 * get photo
	 * 
	 * @param SFPhotoDto
	 *            SFUserName
	 */
	public SFPhotoDto getImage(String SFUserName) {
		try {
			String userListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getImg(SFUserName));
			return utilMapping.loadSFUserImageFromJson(userListJson);
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	// ----------------------------------------------------
	// Methods attachment modules

	/**
	 * search all attachments from Employee central
	 * 
	 * @param String
	 *            seqNumber
	 * @param Date
	 *            dateTime
	 * @param String
	 *            idUserName
	 * @param return
	 *            SFAttachECResponseDto
	 */
	public SFAttachResponseN1Dto getAttachmentListEmployeeCentral(String seqNumber, Date dateTime, String idUserName) {
		try {
			String query = getQueryAttachEmployeeCentralModule(seqNumber, UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd", dateTime) + "T00:00:00", idUserName);
			String resultJson = HttpConnectorSuccessFactor.getInstance().executeGET(query);
			return utilMapping.loadSFAttachResponseDtoFromJsom(resultJson);
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	/**
	 * search all attachments from recruiting Module
	 * 
	 * @param String
	 *            keyTable
	 * @param String
	 *            mailUser
	 */
	public ArrayList<SlugItem> getAttachmentListRecruitingModule(String keyTable, String mailUser, Boolean v2, String idUser) {
		try {
			Gson json = new Gson();
			ArrayList<SlugItem> response = new ArrayList<>();
			mailUser = UtilMapping.toStringToUrlEncoder(mailUser);
			String query = "";
			if (v2) {
				query = getQueryAttachRecruitingModuleV2(keyTable, idUser);
			} else {
				query = getQueryAttachRecruitingModule(keyTable, mailUser);
			}

			String responseSearch = HttpConnectorSuccessFactor.getInstance().executeGET(query);

			if (responseSearch != null) {
				EventListenerDocProcess document = new EventListenerDocProcess();
				JSONObject nodeAllJson = new JSONObject(responseSearch);

				if (nodeAllJson.isNull("d") == false) {
					JSONObject nodeD = nodeAllJson.getJSONObject("d");

					if (nodeD.isNull("results") == false) {
						JSONArray nodeResultsJson = nodeD.getJSONArray("results");

						String[] splitNode = keyTable.split("/");
						int splitNodeLenght = splitNode.length;

						if (splitNodeLenght > 1) {
							loggerSingle.info("We have a subnode :" + splitNode[splitNodeLenght - 1]);

							if (nodeResultsJson != null) {
								for (int i = 0; i < nodeResultsJson.length(); i++) {
									if (nodeResultsJson.getJSONObject(i).isNull(splitNode[0]) == false) {
										JSONObject nodeKeyTableObjJson = nodeResultsJson.getJSONObject(i).getJSONObject(splitNode[0]);

										loggerSingle.gson(nodeKeyTableObjJson);

										JSONArray nodeResultsSubJson = nodeKeyTableObjJson.getJSONArray("results");

										if (nodeResultsSubJson != null) {
											loggerSingle.info("we have to do something");

											for (int r = 0; r < nodeResultsSubJson.length(); r++) {

												JSONObject nodeKeyTableSubObjJsonRet = nodeResultsSubJson.getJSONObject(r);
												if (!nodeKeyTableSubObjJsonRet.isNull(splitNode[splitNodeLenght - 1])) {
													JSONObject nodeKeyTableSubObjJson = nodeKeyTableSubObjJsonRet.getJSONObject(splitNode[splitNodeLenght - 1]);

													loggerSingle.gson(nodeKeyTableSubObjJsonRet);
													if (!v2 || (v2 && ((!nodeKeyTableSubObjJsonRet.isNull("app_confirmedEmployeeID") && nodeKeyTableSubObjJsonRet.getString("app_confirmedEmployeeID").equals(idUser)
															&& nodeKeyTableSubObjJsonRet.getString("appStatusSetItemId").equals("481"))
															|| (!nodeKeyTableSubObjJsonRet.isNull("employeeID") && nodeKeyTableSubObjJsonRet.getString("employeeID").equals(idUser)
																	&& nodeKeyTableSubObjJsonRet.getString("appStatusSetItemId").equals("481"))))) {
														if (nodeKeyTableSubObjJson.isNull("results") == false) {
															JSONArray nodeResultsSubSubJson = nodeKeyTableSubObjJson.getJSONArray("results");

															loggerSingle.info("files number :" + nodeResultsSubSubJson.length());

															for (int u = 0; u < nodeResultsSubSubJson.length(); u++) {
																JSONObject nodeKeyTableSubSubObjJson = nodeResultsSubSubJson.getJSONObject(u);
																if (nodeKeyTableSubSubObjJson != null) {
																	if (nodeKeyTableSubSubObjJson.isNull("attachmentId") == false) {
																		SlugItem item = new SlugItem(nodeKeyTableSubSubObjJson.getString("attachmentId"), nodeKeyTableSubSubObjJson.getString("fileName"));
																		response.add(item);
																	}
																}
															}
														} else {
															if (nodeKeyTableSubObjJson != null) {
																if (nodeKeyTableSubObjJson.isNull("attachmentId") == false) {
																	SlugItem item = new SlugItem(nodeKeyTableSubObjJson.getString("attachmentId"), nodeKeyTableSubObjJson.getString("fileName"));
																	response.add(item);
																}
															}
														}
													}
												}
											}
										}

									}
								}
							}

						} else {
							loggerSingle.info("We have a root : " + splitNode[splitNodeLenght - 1]);

							if (nodeResultsJson != null) {
								for (int i = 0; i < nodeResultsJson.length(); i++) {
									if (nodeResultsJson.getJSONObject(i).isNull(keyTable) == false) {
										JSONObject nodeKeyTableObjJson = nodeResultsJson.getJSONObject(i).getJSONObject(keyTable);

										if (nodeKeyTableObjJson.isNull("results") == false) {

											JSONArray nodeResultsSubSubJson = nodeKeyTableObjJson.getJSONArray("results");

											for (int u = 0; u < nodeResultsSubSubJson.length(); u++) {
												JSONObject nodeKeyTableSubSubObjJson = nodeResultsSubSubJson.getJSONObject(u);
												if (nodeKeyTableSubSubObjJson != null) {
													if (nodeKeyTableSubSubObjJson.isNull("attachmentId") == false) {
														SlugItem item = new SlugItem(nodeKeyTableSubSubObjJson.getString("attachmentId"), nodeKeyTableSubSubObjJson.getString("fileName"));
														response.add(item);
													}
												}
											}

										} else {
											if (nodeKeyTableObjJson != null) {
												if (nodeKeyTableObjJson.isNull("attachmentId") == false) {
													SlugItem item = new SlugItem(nodeKeyTableObjJson.getString("attachmentId"), nodeKeyTableObjJson.getString("fileName"));
													response.add(item);
												}
											}
										}
									}
								}
							}
						}

					}
				}
			}

			return response;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	/**
	 * search all attachments from recruiting Module
	 * 
	 * @param String
	 *            keyTable
	 * @param String
	 *            mailUser
	 */
	public JSONArray getAttachmentListOnboardingModule(String userId) {
		try {

			String st3 = HttpConnectorSuccessFactor.getInstance().executeGET("OnboardingCandidateInfo?$select=kmsUserId&$filter=userId%20eq%20%27" + userId + "%27");

			JSONObject candidateRetrieve = new JSONObject(st3);
			JSONArray usersAsCandidate = candidateRetrieve.getJSONObject("d").getJSONArray("results");
			JSONObject candidateFinal = (JSONObject) usersAsCandidate.get(0);

			try {
				String st = HttpConnectorSuccessFactorOnb.getInstance().getTokenONB("ODataAuthentication");
				try {
					JSONObject st2 = HttpConnectorSuccessFactorOnb.getInstance()
							.getContentONB("OnboardeeAttachment?$select=AttachmentId,AttachmentName,FormCode,&$filter=HrDataId%20eq%20guid%27" + candidateFinal.getString("kmsUserId") + "%27", st);

					if (st2.getJSONArray("value").length() > 0) {
						return st2.getJSONArray("value");
					} else {
						return null;
					}

				} catch (Exception e) {
					loggerSingle.info(e.getMessage());
					return null;
				}

			} catch (Exception e) {

				loggerSingle.info(e.getMessage());
				return null;
			}

		} catch (Exception e) {
			// TODO: handle exception
			loggerSingle.info(e.getMessage());
			return null;
		}
	}

	/**
	 * get attach content for all modules
	 * 
	 * @param String
	 *            idAttachment
	 * @return SFAttachResponseDto
	 */
	public SFAttachResponseN1Dto getAttachContentFromAllModule(String idAttachment) {
		SFAttachResponseN1Dto responseDto = new SFAttachResponseN1Dto();
		try {
			if (idAttachment != null) {
				String query = getQueryAttachContentGeneralModule(idAttachment);
				String resultJson = HttpConnectorSuccessFactor.getInstance().executeGET(query);

				responseDto = utilMapping.loadSFAttachResponseDtoFromJsom(resultJson);
			}

			return responseDto;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	/**
	 * get attach content for all modules
	 * 
	 * @param String
	 *            idAttachment
	 * @return SFAttachResponseDto
	 */
	public SFAttachResponseN1Dto getAttachContentFromOnboarding(String idAttachment) {
		SFAttachResponseN1Dto responseDto = new SFAttachResponseN1Dto();
		try {
			if (idAttachment != null) {

				String st = HttpConnectorSuccessFactorOnb.getInstance().getTokenONB("ODataAuthentication");

				String query = "OnboardeeAttachment('" + idAttachment + "')";
				JSONObject resultJson = HttpConnectorSuccessFactorOnb.getInstance().getContentONB(query, st);
				JSONObject resultReturn = resultJson.getJSONArray("value").getJSONObject(0);

				SFAttachInfoN4Dto file = new SFAttachInfoN4Dto();
				file.setAttachmentId(idAttachment);
				file.setFileContent(resultReturn.getString("AttachmentContent"));
				file.setAttachmentFileName(resultReturn.getString("AttachmentName"));
				file.setFileName(resultReturn.getString("AttachmentName"));
				file.setModule("ONB");
				responseDto.setD(file);
			}

			return responseDto;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	/**
	 * get User Info in recruiting module
	 * 
	 * @param String
	 *            isUser
	 * @return String response json
	 */
	public String getUserInfoInRecruitingModule(String idUser) {
		try {
			String query = getQueryUserRecruitingModule(idUser);

			String resultJson = HttpConnectorSuccessFactor.getInstance().executeGET(query);

			return resultJson;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	// --------------------------------------------------
	// Methods Signature Control

	/**
	 * find all signs details by Document
	 * 
	 * @param Long
	 *            idDoc
	 * @param String
	 *            statusSignCtrl **optional
	 * @return ArrayList<SignatureFileControlDetail>
	 */
	public ArrayList<SignatureFileControlDetail> signatureFileCtrlDetailByDoc(Long idDoc, String statusSignCtrl) {
		SignatureFileControlDetailDAO dao = new SignatureFileControlDetailDAO();
		return dao.getAllSignDetailByDoc(idDoc, statusSignCtrl);
	}

	/**
	 * get all signture file control detail by Id Ctrl
	 * 
	 * @param Long
	 *            idCtrl
	 * @return List<SignatureFileControlDetail>
	 */
	public List<SignatureFileControlDetail> signatureFileCtrlDetailGetByIdCtrl(Long idCtrl) {
		SignatureFileControlDetailDAO daoDetail = new SignatureFileControlDetailDAO();
		return daoDetail.getAllByCtrlId(idCtrl);
	}

	/**
	 * get list of documents sends to signs
	 * 
	 * @param String
	 *            userId
	 * @param UtilCodesEnum
	 *            status
	 */
	public List<SignatureFileControl> getSignatureFileControlList(String userId, UtilCodesEnum status) {
		ArrayList<SignatureFileControl> response = new ArrayList<>();
		SignatureFileControlDAO dao = new SignatureFileControlDAO();

		try {
			response = dao.findAllDocumentsByUserStatus(userId, status);
		} catch (Exception e) {
			e.printStackTrace();
			loggerSingle.error(this, "Error " + e.getMessage());
		}

		return response;
	}

	/**
	 * get all signs file control by id Ppd control
	 * 
	 * @param String
	 *            idPpdSignCtrl
	 * @return ArrayList<SignatureFileControl>
	 */
	public ArrayList<SignatureFileControl> getAllSignFileCtrlByIdPpdCtrl(String idPpdSignCtrl) {
		ArrayList<SignatureFileControl> response = new ArrayList<>();
		SignatureFileControlDAO dao = new SignatureFileControlDAO();

		try {
			response = dao.getAllSignFileCtrlByIdPpdCtrl(idPpdSignCtrl);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * get count signature by status
	 * 
	 * @param String
	 *            status
	 * @return Integer
	 */
	public Integer signatureFileControlCountByStatus(String userId, String status) {
		SignatureFileControlDAO dao = new SignatureFileControlDAO();
		return dao.getCountSignFileCtrlByStatus(userId, status);
	}

	/**
	 * save signature file control
	 * 
	 * @param SignatureFileControl
	 *            entity
	 * @return SignatureFileControl
	 */
	public SignatureFileControl saveSignatureFileControl(SignatureFileControl entity) {
		SignatureFileControlDAO dao = new SignatureFileControlDAO();

		try {
			entity = dao.saveNew(entity);
		} catch (Exception e) {
			loggerSingle.error(this, "Error " + e.getMessage());
		}

		return entity;
	}

	/**
	 * update signature file control
	 * 
	 * @param SignatureFileControl
	 *            entity
	 * @return SignatureFileControl
	 */
	public SignatureFileControl signatureUpdateFileControl(SignatureFileControl entity) {
		SignatureFileControlDAO dao = new SignatureFileControlDAO();

		try {
			entity = dao.save(entity);
		} catch (Exception e) {
			loggerSingle.error(this, "Error " + e.getMessage());
		}

		return entity;
	}

	/**
	 * update status signature file control
	 * 
	 * @param Long
	 *            idSignatureCtrlFile
	 * @param String
	 *            status
	 * @param String
	 *            Observations
	 * @return Boolean
	 */
	public Boolean signatureFileControlUpdateStatus(Long idSignatureCtrlFile, String status, String Observations) {
		SignatureFileControlDAO dao = new SignatureFileControlDAO();

		try {
			return dao.updateStatus(idSignatureCtrlFile, status, Observations);
		} catch (Exception e) {
			loggerSingle.error(this, "Error " + e.getMessage());
		}

		return false;
	}

	/**
	 * 
	 * @param Long
	 *            idDoc
	 * @param String
	 *            status
	 * @param Date
	 *            filterDateJob
	 * @param Integer
	 *            maxReg
	 * @return ArrayList<SignatureFileControl>
	 */
	public ArrayList<SignatureFileControl> signatureFileControlByDoc(Long idDoc, String status, Date filterDateJob, Integer maxReg) {
		SignatureFileControlDAO dao = new SignatureFileControlDAO();
		return dao.getAllSignFileCtrlByDoc(idDoc, status, filterDateJob, maxReg);
	}

	// -------------------------------------------------
	// -------------------------------------------------
	/**
	 * get info profile by username
	 * 
	 * @param String
	 *            userName
	 * @return UserInfo
	 */
	public UserInfo getUserInfoProfile(String userName) {
		try {
			String userJson = HttpConnectorSuccessFactor.getInstance().executeGET(getInfoQuery(userName));
			return utilMapping.loadUserInfoFromJson(userJson);
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	private String getTm(String hrSFUserName, String namesource) {
		String query = QueryOdataDAO.MANAGED_EMPLOYEES_QUERY.replace("#", UtilFiles.urlEncode(hrSFUserName));
		query = query.replace("hr", UtilFiles.urlEncode(namesource));
		return query;
	}

	private String getTmInactive(String hrSFUserName, String namesource) {
		String query = QueryOdataDAO.MANAGED_EMPLOYEESINACTIVE_QUERY.replace("#", UtilFiles.urlEncode(hrSFUserName));
		query = query.replace("hr", UtilFiles.urlEncode(namesource));
		return query;
	}

	private String getSrch(String hrSFUserName) {
		return QueryOdataDAO.MANAGED_SEARCH_QUERY.replace("#", UtilFiles.urlEncode(hrSFUserName.toLowerCase()));
	}

	private String getSrchInactive(String hrSFUserName) {
		return QueryOdataDAO.MANAGED_SEARCHINACTIVE_QUERY.replace("#", UtilFiles.urlEncode(hrSFUserName.toLowerCase()));
	}

	private String getGroup(String groupName) {
		return QueryOdataDAO.GROUP_QUERY.replace("#", UtilFiles.urlEncode(groupName));
	}

	private String getMTm(String hrSFUserName) {
		return QueryOdataDAO.MANAGED_MEMPLOYEES_QUERY.replace("#", UtilFiles.urlEncode(hrSFUserName));
	}

	private String getMTmInactive(String hrSFUserName) {
		return QueryOdataDAO.MANAGED_MEMPLOYEESINACTIVE_QUERY.replace("#", UtilFiles.urlEncode(hrSFUserName));
	}

	private String getImg(String SFUserName) {
		return QueryOdataDAO.MANAGED_EMPLOYEE_PHOTO.replace("#", UtilFiles.urlEncode(SFUserName));
	}

	private String getProfileQuery(String userName, String date) {
		String pr = QueryOdataDAO.PROFILE_QUERY.replace("#", UtilFiles.urlEncode(userName));
		return pr.replace("%", UtilFiles.urlEncode(date));
	}

	private String getManagerCountQuery(String userName, String date) {
		String pr = QueryOdataDAO.MANAGERCOUNT_QUERY.replace("#", UtilFiles.urlEncode(userName));
		return pr.replace("%", UtilFiles.urlEncode(date));
	}

	private String getHrCountQuery(String userName, String date) {
		String pr = QueryOdataDAO.HRCOUNT_QUERY.replace("#", UtilFiles.urlEncode(userName));
		return pr.replace("%", UtilFiles.urlEncode(date));
	}

	private String getAdminQuery(String roleName) {
		return QueryOdataDAO.GROUP_USER_QUERY.replace("#", UtilFiles.urlEncode(roleName));
	}

	private String getGroupsByUserQuery(String userName) {
		return QueryOdataDAO.GROUPS_BY_USER.replace("#", UtilFiles.urlEncode(userName));
	}

	private String getListAdminQuery(String listofusers) {
		return QueryOdataDAO.MANAGED_MANAGER_QUERY.replace("#", UtilFiles.urlEncode(listofusers));
	}
	
	private String getListAdminQuery2(String listofusers) {
		return QueryOdataDAO.MANAGED_MANAGERLIST_QUERY.replace("#", UtilFiles.urlEncode(listofusers));
	}

	private String getGroupParamQuery(String listofusers) {
		return QueryOdataDAO.MANAGED_GROUPPARAMS_QUERY.replace("#", UtilFiles.urlEncode(listofusers));
	}

	private String getInfoQuery(String userName) {
		return QueryOdataDAO.INFO_QUERY2.replace("#", UtilFiles.urlEncode(userName));
	}

	/**
	 * search all attachments from employee central
	 * 
	 * @param String
	 *            seqNumber
	 * @param String
	 *            dateTime 2017-08-30T00:00:00
	 * @param String
	 *            idUserName
	 */
	private String getQueryAttachEmployeeCentralModule(String seqNumber, String dateTime, String idUserName) {
		String query = QueryOdataDAO.QUERY_ATTACH_FILES_EC;
		query = query.replace("#1", seqNumber);
		query = query.replace("#2", dateTime);
		query = query.replace("#3", idUserName);

		return query;
	}

	/**
	 * get Info user for recruiting module
	 * 
	 * @param String
	 *            idUser
	 */
	private String getQueryUserRecruitingModule(String idUser) {
		String query = QueryOdataDAO.QUERY_EMPLOYEE_RECRUITING;
		query = query.replace("#1", idUser);

		return query;
	}

	/**
	 * build query to search all attachments from recruiting Module
	 * 
	 * @param String
	 *            key Table
	 * @param String
	 *            mailUser
	 */
	private String getQueryAttachRecruitingModule(String keyTable, String mailUser) {
		String query = QueryOdataDAO.QUERY_ATTACH_RECRUI_FILES;
		query = query.replace("#1", keyTable);
		query = query.replace("#mail", mailUser.toLowerCase());

		return query;
	}

	/**
	 * build query to search all attachments from recruiting Module
	 * 
	 * @param String
	 *            key Table
	 * @param String
	 *            mailUser
	 */
	private String getQueryAttachRecruitingModuleV2(String keyTable, String mailUser) {
		String query = QueryOdataDAO.QUERY_ATTACH_RECRUI_FILES_V2;
		query = query.replace("#1", keyTable);
		query = query.replace("#mail", mailUser);

		return query;
	}

	/**
	 * build query to search all attachments from recruiting Module
	 * 
	 * @param String
	 *            idAttach
	 */
	private String getQueryAttachContentGeneralModule(String idAttach) {
		String query = QueryOdataDAO.QUERY_ATTACH_CONTENT_GENERAL;
		query = query.replace("#", idAttach);

		return query;
	}
}
