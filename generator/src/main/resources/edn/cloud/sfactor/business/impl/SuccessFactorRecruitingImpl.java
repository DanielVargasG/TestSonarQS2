package edn.cloud.sfactor.business.impl;

import java.io.IOException;

import edn.cloud.business.api.util.UtilFiles;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.integration.SFRecList;
import edn.cloud.business.dto.integration.SFRecUnique;
import edn.cloud.sfactor.business.connectivity.HttpConnectorSuccessFactor;
import edn.cloud.sfactor.business.interfaces.SucessFactorRecruiting;
import edn.cloud.sfactor.persistence.dao.QueryOdataDAO;

public class SuccessFactorRecruitingImpl implements SucessFactorRecruiting {
	private UtilLogger loggerSingle = UtilLogger.getInstance();
	private UtilMapping utilMapping = new UtilMapping();

	@Override
	/**
	 * get User From SoucessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public SFRecList getUser(String userName) {

		try {
			String userListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getSrch(userName));
			return utilMapping.loadSFApplicationFromJsom(userListJson);
			// return null;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}

		// return null;
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
	public SFRecList getUserAdv(String firstName, String lastName, String title) {

		try {
			String userListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getAdvSrch(firstName, lastName, title));
			return utilMapping.loadSFApplicationFromJsom(userListJson);
			// return null;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}

		// return null;
	}

	/**
	 * get Application From SoucessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public SFRecUnique getApplication(String appId) {

		try {
			String userListJson = HttpConnectorSuccessFactor.getInstance().executeGET(getJobApp(appId));
			return utilMapping.loadSFOneApplicationFromJsom(userListJson);
			// return null;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}

		// return null;
	}

	private String getJobApp(String appId) {
		return QueryOdataDAO.QUERY_ONEAPPLICATION_REC.replace("#", UtilFiles.urlEncode(appId));
	}

	private String getSrch(String userName) {

		return QueryOdataDAO.QUERY_APPLICATION_REC.replace("#", UtilFiles.urlEncode(userName.toLowerCase()));
	}

	private String getAdvSrch(String firstName, String lastName, String title) {

		String str = "";

		if (!firstName.equals("")) {
			str += "tolower(firstName)%20like%20%27%25" + firstName.toLowerCase() +"%25%27";
		}

		if (!lastName.equals("")) {
			if(str.equals("")) {
				str += "tolower(lastName)%20like%20%27%25" + lastName.toLowerCase() +"%25%27";
			}else {
				str += " and tolower(lastName)%20like%20%27%25" + lastName.toLowerCase() +"%25%27";
			}
		}

		if (!title.equals("")) {
			if(str.equals("")) {
				str += "tolower(jobRequisition/jobReqLocale/jobTitle)%20like%20%27%25" + title.toLowerCase() +"%25%27";
			}else {
				str += " and tolower(jobRequisition/jobReqLocale/jobTitle)%20like%20%27%25" + title.toLowerCase() +"%25%27";
			}
		}

		// tolower(jobRequisition/jobReqLocale/jobTitle)%20like%20%27%#%%27%20or%20tolower(lastName)%20like%20%27%#%%27%20or%20tolower(firstName)%20like%20%27%#%%27%20or%20tolower(firstName)%20like%20%27#%27%20or%20tolower(lastName)%20like%20%27#%27

		return QueryOdataDAO.QUERY_APPLICATION_RECADV.replace("#", str);
	}

}
