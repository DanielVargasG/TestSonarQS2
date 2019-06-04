package edn.cloud.sfactor.persistence.dao;

public class QueryOdataDAO {
	public static final String MANAGED_EMPLOYEE_PHOTO = "Photo(photoType=1,userId='#')?$select=height,width,photo,userNav/userId,userNav/firstName,userNav/lastName,userNav/location,userNav/title,userNav/email,userNav/hireDate&$expand=userNav";
	public static final String MANAGED_EMPLOYEES_QUERY = "User?$select=userId,firstName,lastName,location,title,email,hireDate&$filter=hr/userId%20eq%20'#'";
	public static final String MANAGED_EMPLOYEESINACTIVE_QUERY = "User?$select=userId,firstName,lastName,location,title,email,hireDate&$filter=hr/userId%20eq%20'#'%20and%20status%20in%20%20'active','inactive'";
	public static final String MANAGED_SEARCH_QUERY = "User?$select=userId,firstName,lastName,location,title,email,hireDate&$filter=tolower(userId)%20like%20%27%25#%25%27%20or%20tolower(username)%20like%20%27%25#%25%27%20or%20tolower(firstName)%20like%20%27%25#%25%27%20or%20tolower(lastName)%20like%20%27%25#%25%27";
	public static final String MANAGED_SEARCHINACTIVE_QUERY = "User?$select=userId,firstName,lastName,location,title,email,hireDate&$filter=status%20in%20%20'active','inactive'%20and%20(tolower(userId)%20like%20%27%25#%25%27%20or%20tolower(username)%20like%20%27%25#%25%27%20or%20tolower(firstName)%20like%20%27%25#%25%27&%20or%20tolower(lastName)%20like%20%27%25#%25%27)";
	public static final String MANAGED_MEMPLOYEES_QUERY = "User?$select=userId,firstName,lastName,location,title,email,hireDate&$filter=manager/userId%20eq%20'#'";
	public static final String MANAGED_MEMPLOYEESINACTIVE_QUERY = "User?$select=userId,firstName,lastName,location,title,email,hireDate&$filter=manager/userId%20eq%20'#'%20and%20status%20in%20%20'active','inactive'";
	public static final String PROFILE_QUERY = "PerPerson('#')?$select=personIdExternal,dateOfBirth,personalInfoNav/lastName,personalInfoNav/firstName,employmentNav/userNav/defaultLocale,employmentNav/startDate,employmentNav/jobInfoNav/company,employmentNav/jobInfoNav/managerId,employmentNav/empJobRelationshipNav/relationshipType,employmentNav/empJobRelationshipNav/relUserId,employmentNav/empJobRelationshipNav/relUserNav/lastName,employmentNav/empJobRelationshipNav/relUserNav/firstName,employmentNav/empJobRelationshipNav/relUserNav/userId,emailNav/emailAddress&$expand=employmentNav/userNav,personalInfoNav,employmentNav,employmentNav/jobInfoNav,employmentNav/jobInfoNav/locationNav,employmentNav/jobInfoNav/companyNav,employmentNav/empJobRelationshipNav,employmentNav/empJobRelationshipNav/relUserNav,emailNav&$format=json&asOfDate=%";
	public static final String MANAGERCOUNT_QUERY = "User('#')/directReports/$count";
	public static final String HRCOUNT_QUERY = "User('#')/hrReports/$count";
	public static final String INFO_QUERY = "User('#')?$select=userId,firstName,lastName,location,businessPhone,division,title,empInfo/jobInfoNav/companyNav/name,empInfo/jobInfoNav/companyNav/externalCode,department,email,hireDate,manager/firstName,manager/lastName,manager/businessPhone,manager/userId&$expand=manager,empInfo/jobInfoNav/companyNav";
	public static final String INFO_QUERY2 = "User('#')?$select=userId,firstName,lastName,email,hireDate,username";

	// Query Roles & Permissions

	public static final String GROUP_QUERY = "DynamicGroup?$filter=groupName%20eq%20%27#%27";
	public static final String GROUP_USER_QUERY = "getUsersByDynamicGroup?groupId=#L";
	public static final String MANAGED_MANAGER_QUERY = "User?$select=userId,username,email&$filter=userId%20eq%20%27#%27%20and%20empInfo/endDate%20eq%20null";
	public static final String MANAGED_MANAGERLIST_QUERY = "User?$select=userId,username,email&$filter=userId%20in%20%27#%27%20and%20empInfo/endDate%20eq%20null";
	public static final String MANAGED_GROUPPARAMS_QUERY = "getExpandedDynamicGroupById?groupId=#L";
	public static final String GROUPS_BY_USER = "getDynamicGroupsByUser?userId=%27#%27&groupSubType=%27permission%27";

	// Query SuccessFactor No Content Recruiting Attach
	public static final String QUERY_ATTACH_RECRUI_FILES = "Candidate?$select=#1/fileName,#1/module,#1/documentEntityType,#1/attachmentId,&$expand=#1&$filter=(tolower(primaryEmail)%20eq%20%27#mail%27 or tolower(contactEmail)%20eq%20%27#mail%27)&$format=json";

	// Query SuccessFactor No Content Recruiting Attach
		public static final String QUERY_ATTACH_RECRUI_FILES_V2 = "Candidate?$select=#1/fileName,#1/module,#1/documentEntityType,#1/attachmentId,jobsApplied/app_confirmedEmployeeID,jobsApplied/employeeID,jobsApplied/appStatusSetItemId&$expand=#1&$filter=(employeeID eq '#mail' or jobsApplied/app_confirmedEmployeeID%20eq%20%27#mail%27)&$format=json";

	
	// Query succesFactor attach content
	public static final String QUERY_ATTACH_CONTENT_GENERAL = "Attachment(#L)?$format=json";

	// Query SuccessFactor Employee Center Attach
	public static final String QUERY_ATTACH_FILES_EC = "EmpJob(seqNumber=#1L,startDate=datetime'#2',userId='#3')?fromDate=01-01-1900&$select=attachmentId,attachmentFileName&$format=json";

	// Query SuccessFactors Recruiting (User + Job Application + Job Requisition);
	public static final String QUERY_APPLICATION_REC = "JobApplication?$select=applicationId,jobRequisition/jobReqLocale/jobTitle,firstName,lastName&$expand=offerLetter,candidate,jobOffer,jobRequisition,jobRequisition/jobReqLocale,jobRequisition/approver&$filter=tolower(jobRequisition/jobReqLocale/jobTitle)%20like%20%27%#%%27%20or%20tolower(lastName)%20like%20%27%#%%27%20or%20tolower(firstName)%20like%20%27%#%%27%20or%20tolower(firstName)%20like%20%27#%27%20or%20tolower(lastName)%20like%20%27#%27";

	// Query SuccessFactors Recruiting (User + Job Application + Job Requisition);
		public static final String QUERY_APPLICATION_RECADV = "JobApplication?$select=applicationId,jobRequisition/jobReqLocale/jobTitle,firstName,lastName&$expand=offerLetter,candidate,jobOffer,jobRequisition,jobRequisition/jobReqLocale,jobRequisition/approver&$filter=#";
	
	// https://api5.successfactors.eu/odata/v2/User('lbarber')?$select=empInfo/personNav/emailNav/emailAddress&$expand=empInfo/personNav/emailNav/emailTypeNav&$filter=empInfo/personNav/emailNav/emailTypeNav/externalCode%20eq%20%27P%27&$format=json
	public static final String QUERY_EMPLOYEE_PATH_MAIL_RECRUITING = "d.emailNav.results[0].emailAddress";

	public static final String QUERY_EMPLOYEE_RECRUITING = "PerPerson('#1')?$select=emailNav/emailAddress&$expand=emailNav/emailTypeNav&$filter=emailNav/emailTypeNav/externalCode%20eq%20%27P%27&$format=json";
	public static final String QUERY_EMPLOYEE_RECRUITING_V2 = "JobApplication?$filter=app_confirmedEmployeeID%20eq%20%27#1%27&$top=1&$format=json";

	public static final String QUERY_ONEAPPLICATION_REC = "JobApplication(#)?$select=applicationId,jobRequisition/jobReqLocale/jobTitle,firstName,lastName&$expand=offerLetter,candidate,jobOffer,jobRequisition,jobRequisition/jobReqLocale,jobRequisition/approver";
	// -----------------------------------
	// examples
	// https://api5.successfactors.eu/odata/v2/EmpJob(seqNumber=2L,startDate=datetime'2017-08-30T00:00:00',userId='ehernandez')?$select=attachmentId&$format=json
	// "https://api5.successfactors.eu/odata/v2/"+ "Candidate?$" + "select="+
	// "attachment1/fileName,attachment1/module,attachment1/documentEntityType,attachment1/attachmentId,"+
	// "attachment2/fileName,attachment2/module,attachment2/documentEntityType,attachment2/attachmentId,"+
	// "resume/fileName,resume/module,resume/documentEntityType,resume/attachmentId,"
	// +
	// "coverLetter/fileName,coverLetter/module,coverLetter/documentEntityType,coverLetter/attachmentId"+
	// "&$expand=attachment1,attachment2,resume,coverLetter" +
	// "&$filter=primaryEmail%20eq%20%27demo1@equipedenuit.fr%27&$format=json";*/
	// private static final String PROFILE_QUERY =
	// "PerPerson('#')?$expand=personalInfoNav,employmentNav,employmentNav/jobInfoNav,employmentNav/jobInfoNav/positionNav,employmentNav/jobInfoNav/employeeTypeNav/picklistLabels,employmentNav/jobInfoNav/employeeTypeNav,employmentNav/jobInfoNav/locationNav,employmentNav/jobInfoNav/companyNav,employmentNav/empJobRelationshipNav,employmentNav/empJobRelationshipNav/relUserNav,emailNav,homeAddressNavDEFLT/countryNav,homeAddressNavDEFLT/countryNav,homeAddressNavDEFLT&$format=json&asOfDate=%";

}
