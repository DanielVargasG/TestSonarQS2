package edn.cloud.business.scheduler;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.GroupRule;
import edn.cloud.business.dto.GroupRuleValue;
import edn.cloud.business.dto.integration.SFAdmin;
import edn.cloud.business.dto.integration.SFAdminList;
import edn.cloud.business.dto.integration.SFRoleList;
import edn.cloud.ppdoc.business.facade.PpdEmployeeApiFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.business.facade.document.SuccessFactorDocumentFacade;
import edn.cloud.sfactor.business.impl.SuccessFactorImpl;
import edn.cloud.sfactor.business.interfaces.SuccessFactor;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.business.utils.StructureBuilder;
import edn.cloud.sfactor.persistence.dao.LoggerDAO;
import edn.cloud.sfactor.persistence.dao.LookupDAO;
import edn.cloud.sfactor.persistence.dao.RolesMappingGroupPpdDAO;
import edn.cloud.sfactor.persistence.dao.RolesMappingPpdDAO;
import edn.cloud.sfactor.persistence.dao.SFAdminDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.LoggerAction;
import edn.cloud.sfactor.persistence.entities.LookupTable;
import edn.cloud.sfactor.persistence.entities.RoleMappingGroupPpd;
import edn.cloud.sfactor.persistence.entities.RolesMappingPpd;
import edn.cloud.sfactor.persistence.entities.StructureBusiness;

public class EventListenerRolesJob implements Job {
	private PpdEmployeeApiFacade ppdEmployeeF = new PpdEmployeeApiFacade();
	private SuccessFactorFacade successFacade = new SuccessFactorFacade();
	private SuccessFactor successFactor = new SuccessFactorImpl();
	private SuccessFactorAdminFacade succesFactorAdminFacade = new SuccessFactorAdminFacade();
	private final UtilLogger loggerSingle = UtilLogger.getInstance();
	private AdminParameters paramAdminCode;

	private static final String NEW_LINE_SEPARATOR = "\n";

	/**
	 * execute job bussiness logic
	 * 
	 * @param JobExecutionContext
	 *            jobExecutionContext
	 */
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		start();
		documentUpdateArchiveByMaxTime();
	}

	private void start() {
		EntityManagerProvider.getInstance().initEntityManagerProvider();

		this.logger("201", "EvenListener REMOVE Logger", UtilCodesEnum.CODE_JOB_ORGAS.getCode(), "Clean logger");

		LoggerDAO list2 = new LoggerDAO();
		try {
			int val = list2.getCount();

			if (val > 100000) {
				int val2 = list2.deleteAllWithLimit(1000);
				this.logger("201", "logger has still (records) : ", String.valueOf(val2), "Clean logger");
			} else {
				this.logger("201", "logger has still (records) : ", String.valueOf(val), "Clean logger");
			}

		} catch (Exception e) {
			this.logger("201", "logger", e.toString(), "Clean logger");
		}

		this.logger("201", "Start EvenListener ORGA Job", UtilCodesEnum.CODE_JOB_ORGAS.getCode(), "Started");

		StructureBuilder.getInstance().updateLevelbyTime();

		this.logger("201", "END EvenListener ORGA Job", UtilCodesEnum.CODE_JOB_ORGAS.getCode(), "Completed");

		// control of duplicate jobs
		Date date = new Date();
		String codeTime = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_CODE.getCode(), date);

		Long idJobProcess = successFacade.saveLoggerControl("200", "Start EvenListener Roles Job", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");
		Long idJobLog = succesFactorAdminFacade.jobLogSave(idJobProcess, codeTime, UtilCodesEnum.CODE_JOB_ROLES.getCode());

		if (idJobLog != null) {
			this.logger("201", "Start EvenListener Roles Job", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");

			SFAdminDAO sfDao = new SFAdminDAO();
			List<SFAdmin> sfList = sfDao.getAll();

			for (SFAdmin sfAdmin : sfList) {
				if (sfAdmin.getSfrolename() == null) {
					sfDao.delete(sfAdmin);
				}
			}

			// eventListenerFacade.eventListenerActionProcessAttachQuartz();

			paramAdminCode = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_CUSTOM_ADMIN_FIELDS.getCode());

			this.logger("201", "Grab : " + ((paramAdminCode != null) ? paramAdminCode.getNameCode() + " - " + paramAdminCode.getValue() : ""), UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");

			List<RolesMappingPpd> listReturn = succesFactorAdminFacade.roleGetAll();

			if (listReturn.size() > 0) {
				this.logger("201", "Roles count : " + listReturn.size(), UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");
			} else {
				this.logger("201", "No roles to process", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");
			}

			for (RolesMappingPpd rolesMappingPpd : listReturn) {
				this.logger("201", "Role update Start", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");
				executeRoleUpdate(rolesMappingPpd);
			}

			this.logger("201", "End EvenListener Roles Job", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Completed");
		} else {
			successFacade.saveLoggerControl("200", "End EvenListener Roles Repeated: " + codeTime + " , idJobProcess: " + idJobProcess, UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Canceled");
		}
	}

	public void force() {
		start();
	}

	private void executeRoleUpdate(RolesMappingPpd rolesMappingPpd) {
		// prefix name to create user in ppd
		AdminParameters paramPrefixUserPpd = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_PREFIX_USER_CREATE_PPD.getCode());

		SFAdminList admList = successFacade.userAdminList(rolesMappingPpd.getIdSf());

		if (admList != null) {
			if (admList.getD() != null) {
				rolesMappingPpd.setCountUsers(String.valueOf(admList.getD().size()));
				RolesMappingPpdDAO dao = new RolesMappingPpdDAO();
				dao.save(rolesMappingPpd);
				this.logger("201", "Role '" + rolesMappingPpd.getNameSf() + "' count : " + admList.getD().size(), UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");
			} else {
				this.logger("201", "Role '" + rolesMappingPpd.getNameSf() + "' count : 0", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");
			}
		} else {
			this.logger("201", "The role is incorrectly configurated for id : " + rolesMappingPpd.getIdSf(), UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");
			return;
		}

		RolesMappingGroupPpdDAO rodao = new RolesMappingGroupPpdDAO();
		Collection<RoleMappingGroupPpd> response = rodao.getAllGroupsByRoleId(rolesMappingPpd.getId());

		List<GroupRule> liGrRu = new ArrayList<GroupRule>();

		if (response.size() > 0) {
			for (RoleMappingGroupPpd roleMappingGroupPpd : response) {
				try {
					SFRoleList lst = successFactor.getGroupId(roleMappingGroupPpd.getGroupId());
					if (lst.getD().getResults().length > 0) {

						GroupRule groupParams = successFacade.queryGroupParamsList(lst.getD().getResults()[0].getGroupID());
						liGrRu.add(groupParams);
						loggerSingle.gson(groupParams);
					}
				} catch (Exception e) {
					this.logger("201", "GROUP List Fail  : " + e + " ", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Error");
				}

			}

			// Get group parameters
		}

		List<SFAdmin> toCompare = new ArrayList<>();

		String lstUsr = "";
		for (SFAdmin adm : admList.getD()) {
			lstUsr += "'" + adm.getUserId() + "',";
		}
		
		if(lstUsr.length() > 0) {
			lstUsr = lstUsr.replaceFirst("'", "");
			lstUsr = lstUsr.substring(0, lstUsr.length()-2);
		}

		List<SFAdmin> admLst = successFacade.queryAdminList2(lstUsr);

		for (SFAdmin adm : admList.getD()) {
			SFAdmin admTemp = new SFAdmin();
			try {
				for (SFAdmin sfAdmin : admLst) {
					if(sfAdmin.getUserId().equals(adm.getUserId())) {
						admTemp =  sfAdmin;
					}
				}
				//admTemp = successFacade.queryAdminList(adm.getUserId());
				//loggerSingle.gson(adm);
			} catch (Exception e) {
				loggerSingle.gson(e);
				this.logger("201", "User details : " + adm.getUserId() + " has no datas and can't be found ", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Error");
			}
			if (admTemp != null && admTemp.getUserId() != null && !admTemp.getUserId().equals("")) {
				adm.setUsername(admTemp.getUsername());
				adm.setEmail(admTemp.getEmail());

				if (admTemp.getEmail() == null || admTemp.getEmail().contains("noemail") || admTemp.getEmail().contains("nomail") || admTemp.getEmail().equals("")) {
					// get email format
					String out = "";
					SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
					AdminParameters paramAdminEmailFormatTermi = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_FORMAT_MAILUSERTERM_PPD.getCode());
					// transform email format
					out = "noemail_" + admTemp.getUserId() + "@test.com";
					if (paramAdminEmailFormatTermi != null && paramAdminEmailFormatTermi.getValue() != null && !paramAdminEmailFormatTermi.getValue().equals("")) {
						out = paramAdminEmailFormatTermi.getValue().replace("??", admTemp.getUserId());
					}
					adm.setEmail(out);
				}

				adm.setMessage("");
				adm.setOperator(rolesMappingPpd.getRoleOperator());
				adm.setOrga(rolesMappingPpd.getRoleOrga());
				adm.setSfrolename(rolesMappingPpd.getNameSf());
				adm.setAppsfroleid(rolesMappingPpd.getId());

				if (liGrRu.size() > 0 && rolesMappingPpd.getRoleOrga().equals("NaN")) {
					Gson gson = new Gson();
					adm.setMessage(gson.toJson(liGrRu));
				}

				if (liGrRu.size() > 0 && rolesMappingPpd.getRoleOrga().equals("NaN")) {
					LookupDAO lkdao = new LookupDAO();
					LookupTable lk = lkdao.getLookupByInput("organization", "mappingGroupOrga");

					for (GroupRule liGrRuru : liGrRu) {
						for (GroupRuleValue liGrRuruVal : liGrRuru.getInclude()) {
							if (liGrRuruVal.getKey().equals(lk.getValueOut())) {
								String[] ls = liGrRuruVal.getValue().split(",");

								adm.setOrga(ls[0]);

							}
						}
					}

				} else if (!rolesMappingPpd.getRoleOrgaDyna()) {

					StructureBusiness struc = new StructureBusiness();
					struc.setStructureName(rolesMappingPpd.getRoleOrga());

					if (rolesMappingPpd != null) {
						if (!rolesMappingPpd.getRoleOrga().equals("")) {
							String str = StructureBuilder.getInstance().getStructureUnik(admTemp.getUserId(), struc, "");

							adm.setOrga(str);
						}
					}
				}

				toCompare.add(adm);
			} else {
				this.logger("201", "User details : " + adm.getUserId() + " can't be found (inactive user) ", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Error");
			}

		}

		List<SFAdmin> toRm = this.removeLine(toCompare, rolesMappingPpd, liGrRu);
		List<SFAdmin> toAdd = this.addLine(toCompare, rolesMappingPpd, paramPrefixUserPpd != null && paramPrefixUserPpd.getValue() != null ? paramPrefixUserPpd.getValue() : UtilCodesEnum.CODE_TYPE_DEFAULT_PREFIX_USER.getCode(), liGrRu);

		this.logger("201", "Adding : " + toAdd.size() + "users", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");
		this.logger("201", "Removing : " + toRm.size() + "users", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");

		toAdd.addAll(toRm);
		if (toAdd.size() > 0) {
			try {
				byte[] dos = writeBinaryFile(toAdd);
				ContentFileInfo cfi = new ContentFileInfo();
				cfi.setFile(dos);
				cfi.setFileName("manager.csv");

				ppdEmployeeF.wServiceCSVUploadManager(cfi);

			} catch (IOException e) {

				// e.printStackTrace();
				this.logger("404", "Errors : " + e.getMessage(), UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Error");
			}
		} else {
			this.logger("201", "Nothing to add or remove", UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");
		}
	}

	private List<SFAdmin> addLine(List<SFAdmin> str, RolesMappingPpd rolesMappingPpd, String paramPrefixUserPpd, List<GroupRule> liGrRu) {
		List<SFAdmin> toAdd = new ArrayList<>();
		SFAdminDAO sfDao = new SFAdminDAO();
		LookupDAO lkdao = new LookupDAO();
		for (SFAdmin sfAdmin : str) {
			Boolean isPresent = false;
			List<SFAdmin> sfList = sfDao.getUserByRoleName(rolesMappingPpd.getNameSf(), rolesMappingPpd.getId());

			// Query to get all admins from role

			for (SFAdmin sfAdminSaved : sfList) {
				if (sfAdmin.toCompare().equals(sfAdminSaved.toCompare())) {
					isPresent = true;
				}
			}
			if (!isPresent)
				toAdd.add(sfAdmin);
		}

		this.logger("201", "Number of users to add to Groups : " + toAdd.size(), UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");

		String comma = "";

		if (paramAdminCode == null || paramAdminCode.getValue().equals("")) {

		} else {
			String[] parts = paramAdminCode.getValue().split(Pattern.quote(";"));

			if (liGrRu.size() > 0) {
				for (String s : parts) {
					comma += ";";
					for (GroupRule liGrRuru : liGrRu) {
						for (GroupRuleValue liGrRuruVal : liGrRuru.getInclude()) {
							LookupTable lk3 = lkdao.getLookupByInput(liGrRuruVal.getKey(), "mappingGroupKey");
							if (s.equals(lk3.getValueOut())) {
								comma += "=, " + liGrRuruVal.getValue();
							}
						}

						for (GroupRuleValue liGrRuruVal : liGrRuru.getExclude()) {
							LookupTable lk3 = lkdao.getLookupByInput(liGrRuruVal.getKey(), "mappingGroupKey");
							if (s.equals(lk3.getValueOut())) {
								comma += "<>, " + liGrRuruVal.getValue();
							}
						}
					}

				}
			} else {

				String[] partsResult = rolesMappingPpd.getRoleStaticContent().split(Pattern.quote(";"));

				for (String s : parts) {
					comma += ";";
					for (String res : partsResult) {
						if (res.startsWith(s)) {
							String sr = StringUtils.substringBetween(res, "'", "'");
							comma += sr;
						}
					}

				}
			}
		}

		for (SFAdmin sfAdmin : toAdd) {
			sfAdmin.setConcat(sfAdmin.getLastName() + ";" + sfAdmin.getFirstName() + ";" + paramPrefixUserPpd.replace("@@", sfAdmin.getUserId()) + ";" + sfAdmin.getEmail() + ";;" + rolesMappingPpd.getNamePpd() + ";;"
					+ rolesMappingPpd.getRoleType() + ";" + rolesMappingPpd.getRoleOperator() + ";" + sfAdmin.getOrga() + ";;" + sfAdmin.getUsername() + comma.replaceAll("@userid@", sfAdmin.getUserId()));
			sfAdmin.setPart1(sfAdmin.getLastName() + ";" + sfAdmin.getFirstName() + ";" + paramPrefixUserPpd.replace("@@", sfAdmin.getUserId()) + ";" + sfAdmin.getEmail() + ";;" + rolesMappingPpd.getNamePpd() + ";;"
					+ rolesMappingPpd.getRoleType() + ";" + rolesMappingPpd.getRoleOperator() + ";" + sfAdmin.getOrga() + ";");
			sfAdmin.setPart2(";" + sfAdmin.getUsername() + comma.replaceAll("@userid@", sfAdmin.getUserId()));
			sfDao.saveNew(sfAdmin);
		}

		return toAdd;
	}

	private List<SFAdmin> removeLine(List<SFAdmin> str, RolesMappingPpd rolesMappingPpd, List<GroupRule> liGrRu) {
		List<SFAdmin> toRm = new ArrayList<>();

		LookupDAO lkdao = new LookupDAO();
		SFAdminDAO sfDao = new SFAdminDAO();
		List<SFAdmin> sfList = sfDao.getUserByRoleName(rolesMappingPpd.getNameSf(), rolesMappingPpd.getId());
		// Query to get all admins from role

		for (SFAdmin sfAdmin : sfList) {
			Boolean isPresent = false;
			for (SFAdmin sfAdminSaved : str) {
				if (sfAdmin.getAppsfroleid().equals(sfAdminSaved.getAppsfroleid()) && sfAdmin.getUserId().equals(sfAdminSaved.getUserId())) {
					isPresent = true;
				}
			}
			if (!isPresent)
				toRm.add(sfAdmin);
		}

		for (SFAdmin sfAdmin : toRm) {
			sfDao.delete(sfAdmin);
		}

		this.logger("201", "Number of users to remove to Groups : " + Integer.toString(toRm.size()), UtilCodesEnum.CODE_JOB_ROLES.getCode(), "Started");

		String comma = "";

		if (paramAdminCode == null || paramAdminCode.getValue().equals("")) {

		} else {
			String[] parts = paramAdminCode.getValue().split(Pattern.quote(";"));

			if (liGrRu.size() > 0) {
				for (String s : parts) {
					comma += ";";
					for (GroupRule liGrRuru : liGrRu) {
						for (GroupRuleValue liGrRuruVal : liGrRuru.getInclude()) {
							LookupTable lk3 = lkdao.getLookupByInput(liGrRuruVal.getKey(), "mappingGroupKey");
							if (s.equals(lk3.getValueOut())) {
								comma += "=, " + liGrRuruVal.getValue();
							}
						}

						for (GroupRuleValue liGrRuruVal : liGrRuru.getExclude()) {
							LookupTable lk3 = lkdao.getLookupByInput(liGrRuruVal.getKey(), "mappingGroupKey");
							if (s.equals(lk3.getValueOut())) {
								comma += "<>, " + liGrRuruVal.getValue();
							}
						}
					}

				}
			} else {
				String[] partsResult = rolesMappingPpd.getRoleStaticContent().split(Pattern.quote(";"));

				for (String s : parts) {
					comma += ";";
					for (String res : partsResult) {
						if (res.startsWith(s)) {
							String sr = StringUtils.substringBetween(res, "'", "'");
							comma += sr;
						}
					}

				}
			}

		}

		for (SFAdmin sfAdmin : toRm) {
			sfAdmin.setConcat(sfAdmin.getPart1() + "X" + sfAdmin.getPart2());
		}

		return toRm;
	}

	private byte[] writeBinaryFile(List<SFAdmin> str) throws IOException, FileNotFoundException {
		byte[] data;
		// Open a binary file for output.

		// writer
		String writer = "";

		// headers
		writer += "last_name;first_name;technical_id;email_pro;phone_number;role_code;role_id;type;operator;organization_code;delete;saml_token";
		if (paramAdminCode == null || paramAdminCode.getValue().equals("")) {

		} else {
			writer += ";" + paramAdminCode.getValue();
		}

		writer += NEW_LINE_SEPARATOR;
		// data
		for (SFAdmin arr : str) {
			writer += arr.getConcat();
			writer += NEW_LINE_SEPARATOR;
		}
		loggerSingle.info(writer);
		ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
		// Write the array elements to the binary file.
		output.write(writer.getBytes("UTF-8"));
		data = output.toByteArray();

		// loggerSingle.gson(data);
		// Close the file.
		return data;
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
	
	
	/**
	 * update all documents with creation date less than the admin parameter  CODE_PARAM_MAX_TIME_DOC_AUTO_ARCHIVE or 90 days
	 * */
	private void documentUpdateArchiveByMaxTime(){
		SuccessFactorDocumentFacade SFDocumentFacade = new SuccessFactorDocumentFacade();
		SFDocumentFacade.documentUpdateArchiveByMaxTime();
	}
}
