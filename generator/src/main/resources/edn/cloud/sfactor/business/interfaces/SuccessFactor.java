package edn.cloud.sfactor.business.interfaces;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.dto.GroupRule;
import edn.cloud.business.dto.SFUserPhotoDto;
import edn.cloud.business.dto.integration.SFAdmin;
import edn.cloud.business.dto.integration.SFAdminList;
import edn.cloud.business.dto.integration.SFGroupList;
import edn.cloud.business.dto.integration.SFPhotoDto;
import edn.cloud.business.dto.integration.SFRoleList;
import edn.cloud.business.dto.integration.SFUserDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.integration.UserInfo;
import edn.cloud.business.dto.integration.attach.SFAttachResponseN1Dto;
import edn.cloud.sfactor.persistence.entities.Employee;
import edn.cloud.sfactor.persistence.entities.SignatureFileControl;
import edn.cloud.sfactor.persistence.entities.SignatureFileControlDetail;

public interface SuccessFactor 
{	
	/**
	 * save logger control info
	 * @param String code
	 * @param String message
	 * @param String user
	 * @param String status
	 * @param Long
	 * */
	public Long saveLoggerControl(String code, String message, String user, String status);
	
	/**
	 * get User From SoucessFactor
	 * @param String userName
	 * @param String date
	 * */
	public SFAdminList getAdminList(String roleName);
	
	
	/**
	 * get User From SoucessFactor
	 * @param String userName
	 * @param String date
	 * */
	public SFAdmin getQueryAdminList(String ListOfUsers);
	
	
	/**
	 * get User From SoucessFactor
	 * @param String userName
	 * @param String date
	 * */
	public List<SFAdmin> getQueryAdminList2(String ListOfUsers);
	
	
	
	/**
	 * get Paremeters from groups From SoucessFactor
	 * @param String userName
	 * @param String date
	 * */
	public GroupRule queryGroupParamsList(String idFromGroup);
	
	/**
	 * get User From SoucessFactor
	 * @param String userName
	 * @param String date
	 * */
	public SFUserDto getUserProfile(String userName, String date);
	
	/**
	 * get User From SoucessFactor
	 * @param String userName
	 * @param String date
	 * */
	public String getUserManagerCount(String userName, String date);
	
	/**
	 * get User From SoucessFactor
	 * @param String userName
	 * @param String date
	 * */
	public String getUserHrCount(String userName, String date);
	
	
	/**
	 * 	get employee by user HR
	 *	@param String userHR
	 *	@return Employee
	 * */
	public Employee getEmployeeByUserHR(String userHR);	
	
	/**
	 * get team
	 * @param String hrSFUserName
	 * */
	public List<SFUserPhotoDto> getTeam(String hrSFUserName, String onlyActive, String namesource);
	
	/**
	 * get team
	 * @param String hrSFUserName
	 * */
	public List<SFUserPhotoDto> getMTeam(String hrSFUserName, String onlyActive);
	
	/**
	 * get team
	 * @param String hrSFUserName
	 * */
	public List<SFUserPhotoDto> getSearch(String hrSFUserName, String onlyActive);
	
	
	/**
	 * get team
	 * @param String hrSFUserName
	 * */
	public SFRoleList getGroupId(String rolename);
	
	/**
	 * get team
	 * @param String hrSFUserName
	 * */
	public SFGroupList getGroupList(String rolename);
	
	/**
	 * get photo
	 * @param String SFUserName
	 * */
	public SFPhotoDto getImage(String SFUserName);
	
	/**
	 * get list of documents sends to signs
	 * @param String userId
	 * @param UtilCodesEnum status
	 * */
	public List<SignatureFileControl> getSignatureFileControlList(String userId,UtilCodesEnum status);	
	
	/**
	 * save signature file control
	 * @param SignatureFileControl entity
	 * @return SignatureFileControl
	 * */
	public SignatureFileControl saveSignatureFileControl(SignatureFileControl entity);	
	
	/**
	 * search all attachments from Employee central
	 * @param String seqNumber
	 * @param Date dateTime
	 * @param String idUserName
	 * @param return SFAttachECResponseDto
	 * */
	public SFAttachResponseN1Dto getAttachmentListEmployeeCentral(String seqNumber,Date dateTime,String idUserName);	

	
	/**
	 * search all attachments from recruiting Module
	 * @param String keyTable
	 * @param String mailUser
	 * */
	public ArrayList<SlugItem> getAttachmentListRecruitingModule(String keyTable, String mailUser, Boolean v2, String idUser) ;
	
	/**
	 * search all attachments from recruiting Module
	 * @param String keyTable
	 * @param String mailUser
	 * */
	public JSONArray getAttachmentListOnboardingModule(String userId) ;
	
	/**
	 * get attach content for all modules	   
	 * @param String idAttachment
	 * @return SFAttachResponseDto 
	 * */
	public SFAttachResponseN1Dto getAttachContentFromAllModule(String idAttachment);
	
	/**
	 * get attach content for onBoarding	   
	 * @param String idAttachment
	 * @return SFAttachResponseDto 
	 * */
	public SFAttachResponseN1Dto getAttachContentFromOnboarding(String idAttachment);
	
	/**
	 * @param String key
	 * */
	public Object hasKeyGetValue(String key);	
	
	/**
	 * get User Info in recruiting module   
	 * @param String isUser
	 * @return String response json 
	 * */
	public String getUserInfoInRecruitingModule(String idUser);	
	
	/**
	 * update status signature file control  
	 * @param Long idSignatureCtrlFile
	 * @param String status
	 * @param String observations
	 * @return Boolean
	 * */
	public Boolean signatureFileControlUpdateStatus(Long idSignatureCtrlFile, String status, String observations);
	
	/**
	 * get all signture file control detail by Id Ctrl
	 * @param Long idCtrl
	 * @return List<SignatureFileControlDetail>
	 * */
	public List<SignatureFileControlDetail> signatureFileCtrlDetailGetByIdCtrl(Long idCtrl);
	
	/**
	 * 
	 * @param Long idDoc
	 * @param String status
	 * @param Date filterDateJob
	 * @param Integer maxReg
	 * @return ArrayList<SignatureFileControl>
	 * */
	public ArrayList<SignatureFileControl> signatureFileControlByDoc(Long idDoc,String status, Date filterDateJob, Integer maxReg);	
	
	/**
	 * find all signs details by Document 
	 * @param Long idDoc
	 * @param String statusSignCtrl **optional
	 * @return ArrayList<SignatureFileControlDetail>
	 * */
	public ArrayList<SignatureFileControlDetail> signatureFileCtrlDetailByDoc(Long idDoc,String statusSignCtrl) ;
	
	/**
	 * update signature file control
	 * 
	 * @param SignatureFileControl entity
	 * @return SignatureFileControl
	 */
	public SignatureFileControl signatureUpdateFileControl(SignatureFileControl entity);
	
	/**
	 * get all signs file control by id Ppd control 
	 * @param String idPpdSignCtrl
	 * @return ArrayList<SignatureFileControl>
	 * */
	public ArrayList<SignatureFileControl> getAllSignFileCtrlByIdPpdCtrl(String idPpdSignCtrl);
	
	/**
	 * get count signature by status
	 * @param String userId, 
	 * @param String status
	 * @return Integer
	 * */
	public Integer signatureFileControlCountByStatus(String userId,String status);
	
	/**
	 * get info profile by username
	 * @param String userName
	 * @return UserInfo
	 * */
	public UserInfo getUserInfoProfile(String userName);
}