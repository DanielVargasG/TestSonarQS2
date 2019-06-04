package edn.cloud.sfactor.business.facade.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.SignatureFieldDto;
import edn.cloud.business.dto.SignatureGroupDto;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.integration.UserInfo;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorTemplateFacade;
import edn.cloud.sfactor.business.impl.EmailImpl;
import edn.cloud.sfactor.business.impl.SuccessFactorImpl;
import edn.cloud.sfactor.business.impl.document.SuccessFactorAuthorization;
import edn.cloud.sfactor.business.interfaces.Email;
import edn.cloud.sfactor.business.interfaces.SuccessFactor;
import edn.cloud.sfactor.business.interfaces.document.SucessFactorAuthorization;
import edn.cloud.sfactor.persistence.dao.AuthorizationDetailDAO;
import edn.cloud.sfactor.persistence.entities.AuthorizationDetails;
import edn.cloud.sfactor.persistence.entities.AuthorizationDocument;
import edn.cloud.sfactor.persistence.entities.Document;
import edn.cloud.sfactor.persistence.entities.Generated;

public class SuccessFactorAuthoFacade 
{
	private SucessFactorAuthorization successFactorAuthoImpl = new SuccessFactorAuthorization();
	
	/**
	 * Insert Authorization Manager
	 * @param id
	 * @param user
	 */
	public edn.cloud.business.dto.GenErrorInfoDto templateInsertAuth(Long id, String user) 
	{
		SuccessFactorFacade successFactorUtils = new SuccessFactorFacade();
		SuccessFactorFacade SFfacade = new SuccessFactorFacade();
		SuccessFactorTemplateFacade templateFacade = new SuccessFactorTemplateFacade();
		SuccessFactor successFactor = new SuccessFactorImpl();
		
		edn.cloud.business.dto.GenErrorInfoDto response = new edn.cloud.business.dto.GenErrorInfoDto();
		response.setFlag(false);//create register
		
		successFactorUtils.saveLoggerControl("400", "0. INICIO!!!", "debug_autho", "");
		
		try
		{
			UtilLogger log = new UtilLogger();
			AuthorizationDetailDAO authDao = new AuthorizationDetailDAO();
			TemplateInfoDto tmp = templateFacade.templateGetById(id);
			tmp.setSingatureGroup(templateFacade.templateGetSignatureGroupList(id));
			
			if(tmp.getSingatureGroup()!=null && tmp.getSingatureGroup().size()>0)
			{
				AuthorizationDocument authDoc = new AuthorizationDocument();		
				AuthorizationDetails autDet = new AuthorizationDetails();
				AuthorizationDetails autDetFirstAutho = null;
				authDoc.setUserEmp(user);
				authDoc.setCreateDate(new Date());
				authDoc.setStatus(UtilCodesEnum.CODE_MANAGER_PENDING.getCode());
				authDoc.setTemplate(UtilMapping.templateToEntity(tmp));
				authDoc.setEnable(Boolean.TRUE);
				
				successFactorUtils.saveLoggerControl("400", "1. parameter: user target :"+user, "debug_autho", "Error for/loop");
				SignatureGroupDto groupResponse = SFfacade.signatureGetValueQueryBuilder(
						tmp.getSingatureGroup().get(0),
						user,
						false,
						UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(),new Date()));
				
				if(groupResponse!=null && groupResponse.getSignatures()!=null)
				{
					AuthorizationDocument authDocNew = successFactorAuthoImpl.saveNewAuthorizationDocument(authDoc);				
					for (SignatureFieldDto  signDetail: groupResponse.getSignatures()) 
					{	
						if(signDetail.getValue()!=null && !signDetail.getValue().toString().equals(""))
						{
							UserInfo userSF = successFactor.getUserInfoProfile(signDetail.getValue().toString());
							
							autDet = new AuthorizationDetails();							
							autDet.setStatus(UtilCodesEnum.CODE_MANAGER_PENDING.getCode());
							autDet.setLastUpdateOn(new Date());
							autDet.setUserId(signDetail.getValue().toString());
							
							if(userSF!=null && userSF.getEmail()!=null && !userSF.getEmail().equals(""))
								autDet.setEmail(userSF.getEmail());
							else
								autDet.setEmail("");
							autDet.setComment("");
							autDet.setEnable(true);
							autDet.setCurrentAutho("");
							autDet.setOrderAutho(signDetail.getOrder());
							autDet.setAuthDocument(authDocNew);
							autDet.setIsCurrentAutho(false);
							response.setFlag(true);//create register
							
							successFactorAuthoImpl.saveNewAuthorizationDetails(autDet);
							
							if(signDetail.getOrder()==1)
							{
								autDet.setIsCurrentAutho(true);
								autDetFirstAutho = autDet;
							}
							
							if(autDet!=null && autDet.getId()!=null)
								successFactorUtils.saveLoggerControl("400", "3.2.10 id detalle "+autDet.getId(), "debug_autho", "Error for/loop");
							else
								successFactorUtils.saveLoggerControl("400", "3.2.11 null detalle ", "debug_autho", "Error for/loop");
								
						}
						else
						{
							successFactorUtils.saveLoggerControl("400", "3.3. NO value signature ", "debug_autho", "Error for/loop");
						}
					}
				}
				else
				{
					successFactorUtils.saveLoggerControl("400", "4. groupResponse.getSignatures() no data ", "debug_autho", "Error for/loop");
				}
				
				if(!response.getFlag())
				{//error
					response.setCode(UtilCodesEnum.CODE_ERROR_1001.getCode());
					if(authDoc!=null && authDoc.getId()!=null && authDoc.getId()>0) {
						successFactorUtils.saveLoggerControl("400", "6. intenta borrar ", "debug_autho", "Error for/loop");
						authDao.AuthDocument().delete(authDoc);
						successFactorUtils.saveLoggerControl("400", "7. borro ", "debug_autho", "Error for/loop");
					}
				}
				else 
				{
					response.setCode(UtilCodesEnum.CODE_OK.getCode());
					
					if(autDetFirstAutho!=null && autDetFirstAutho.getAuthDocument()!=null)
					{
						Email emailImpl = new EmailImpl();
						emailImpl.sendEmailAuthorization(UtilCodesEnum.CODE_DEFAULT_FROM_NOTI_EMAIL.getCode(),
								 autDetFirstAutho.getEmail()!=null&&!autDetFirstAutho.getEmail().equals("")?autDetFirstAutho.getEmail():UtilCodesEnum.CODE_DEFAULT_FROM_NOTI_EMAIL.getCode(), 
								 UtilCodesEnum.CODE_AUTHO_EMAIL_SUBJECT.getCode(), 
								 autDetFirstAutho.getAuthDocument().getTemplate().getTitle()+". Id Authorization: "+autDetFirstAutho.getAuthDocument().getId(), 
								 autDetFirstAutho.getAuthDocument().getUserEmp());
					}
				}
			}
			else
			{
				response.setCode(UtilCodesEnum.CODE_ERROR_1001.getCode());
			}
		}catch (Exception e) {
			e.printStackTrace();
			response.setCode(UtilCodesEnum.CODE_ERROR_1001.getCode());
		}
		
		successFactorUtils.saveLoggerControl("400", "6. FIN!!!", "debug_autho", "");
		return response;
	}
	
	/**
	 * Get Authorization Details of User
	 * @param idTemporal
	 * @param user
	 * @return SlugItem
	 */
	public ResponseGenericDto authoValidateCreateNew(Long idTemporal, String user) 
	{	
		ResponseGenericDto response = new ResponseGenericDto();
		
		List<AuthorizationDocument> authoList = new ArrayList<>();
		authoList = successFactorAuthoImpl.sfTemplateAuth().getAuthorizationDocument(user, idTemporal);
		int index = 0;
		for(AuthorizationDocument aut : authoList) 
		{
			if(aut.getStatus().equals(UtilCodesEnum.CODE_MANAGER_PENDING.getCode()))
			{				
				index ++;
				response.setCode(UtilCodesEnum.CODE_FALSE.getCode());
				return response;
			}			
		}
		
		response.setCode(UtilCodesEnum.CODE_TRUE.getCode());
		return response;				
	}
	
	/**
	 * get count Athorizations Details by User
	 * @param String user
	 * @param String statusInAuthoControl
	 * @param String statusInDoc
	 * @return Integer
	 * */
	public Integer getAuthorizationCountByUser(String user,String statusInAuthoControl,String statusInDoc)
	{
		return successFactorAuthoImpl.getAuthorizationCountByUser(user, statusInAuthoControl,statusInDoc);
	}
	
	/**
	 * Get Authorization Details of Status
	 * @param Long idAuthoCtrl
	 * @return
	 */
	public void authoFinishProccess(Long idAuthoCtrl)
	{
		SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
		SuccessFactorFacade successFactorUtils = new SuccessFactorFacade();
		
		Date date = new Date();
		String dateSeconds = UtilDateTimeAdapter.getDateFormat("yyyy-MM-dd HH:mm:ss z", date);
				
		AuthorizationDocument authorizationDocument = new AuthorizationDocument();
		List<AuthorizationDetails>  details = successFactorAuthoImpl.authorizationDetailByIdControl(idAuthoCtrl);
		
		if(details!=null && details.size()>0)
		{
			int detAuth = 0;
			for(AuthorizationDetails det:details)
			{
				authorizationDocument = det.getAuthDocument();
				if(det.getStatus().equals(UtilCodesEnum.CODE_MANAGER_CANCELED.getCode()) || 
						det.getStatus().equals(UtilCodesEnum.CODE_MANAGER_REJECTED.getCode()))
				{
					authorizationDocument.setStatus(det.getStatus());
					successFactorAuthoImpl.authorizationUpdate(authorizationDocument);
					return;
				}
				
				if(det.getStatus().equals(UtilCodesEnum.CODE_MANAGER_AUTHORIZED.getCode())) {
					detAuth++;
				}
			}
			
			if(detAuth == details.size()) 
			{
				authorizationDocument.setStatus(UtilCodesEnum.CODE_MANAGER_AUTHORIZED.getCode());
				authorizationDocument.setLastUpdateOn(new Date());
				successFactorAuthoImpl.authorizationUpdate(authorizationDocument);
				
				//generate document
				edn.cloud.business.dto.GenErrorInfoDto resultErrorInfo = successFactorUtils.generatedDocumentAutomatic(
						authorizationDocument.getTemplate().getId(), 
						authorizationDocument.getUserEmp(),"","",
						UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(),new Date()),
						UtilCodesEnum.CODE_SOURCE_ATHORIZATION.getCode()+" id: "+authorizationDocument.getId());
				
				// validate if document is created
				if (resultErrorInfo.getField() != null && !resultErrorInfo.getField().equals("")) 
				{
					//save info of document
					authorizationDocument.setDocumentId(Long.valueOf(resultErrorInfo.getField()));
					authorizationDocument.setStatus(UtilCodesEnum.CODE_MANAGER_GENERATED.getCode());
					authorizationDocument.setObservations(
							UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n"+	
							"Document Id: "+resultErrorInfo.getField()+" "+	
							UtilCodesEnum.CODE_STRING_END.getCode() + authorizationDocument.getObservations());
				}
				else 
				{
					authorizationDocument.setObservations(
										UtilCodesEnum.CODE_STRING_INIT.getCode() + ":" + dateSeconds + "\n"+	
										resultErrorInfo.getMessage()+" "+	
										UtilCodesEnum.CODE_STRING_END.getCode() + authorizationDocument.getObservations());
				
					authorizationDocument.setStatus(UtilCodesEnum.CODE_MANAGER_ERROR_GENERATED.getCode());					
				}
				
				successFactorAuthoImpl.authorizationUpdate(authorizationDocument);
			}
			else
			{
								
				//send email next authorization
				Email emailImpl = new EmailImpl();
				ResponseGenericDto response = this.authoGetCurrentStep(authorizationDocument.getId(),
						   authorizationDocument.getTemplate().getId(), 
						   authorizationDocument.getUserEmp());

				for (AuthorizationDetails item : details) 
				{	
					if(item.getStatus().equals(UtilCodesEnum.CODE_MANAGER_PENDING.getCode()) 
							&& item.getOrderAutho()-1 == Integer.valueOf(response.getCode())) 
					{						
						
						if(item.getEnable()) 
						{
							item.setIsCurrentAutho(true);
							successFactorAuthoImpl.updateAuthorizationDetails(item);
							emailImpl.sendEmailAuthorization(UtilCodesEnum.CODE_DEFAULT_FROM_NOTI_EMAIL.getCode(),
														 item.getEmail()!=null&&!item.getEmail().equals("")?item.getEmail():UtilCodesEnum.CODE_DEFAULT_FROM_NOTI_EMAIL.getCode(), 
														 UtilCodesEnum.CODE_AUTHO_EMAIL_SUBJECT.getCode(), 
														 item.getAuthDocument().getTemplate().getTitle()+". Id Authorization: "+item.getAuthDocument().getId(), 
														 item.getAuthDocument().getUserEmp());
						}
								
					}
				}				
			}
		}
	}
	
	/**
	 * Get Authorization Details by user who authorizes
	 * @param user
	 * @return
	 */
	public List<AuthorizationDetails> authoDetailGetByUserAuthorizes(String user)
	{		
		List<AuthorizationDetails> listReturn = new ArrayList<>();
		
		List<AuthorizationDetails> details = successFactorAuthoImpl.sfTemplateAuth().getAuthorizationControlUser(
				user,"'"+UtilCodesEnum.CODE_MANAGER_PENDING.getCode()+"'");
		
		if(!details.isEmpty()) 
		{			
			for (AuthorizationDetails item : details) 
			{	
				ResponseGenericDto  response = this.authoGetCurrentStep(item.getAuthDocument().getId(),item.getAuthDocument().getTemplate().getId(), user);				

				if(item.getStatus().equals(UtilCodesEnum.CODE_MANAGER_PENDING.getCode()) 
						&& item.getOrderAutho()-1 == Integer.valueOf(response.getCode())) 
				{
					item.setCurrentAutho(response.getMessage());
					listReturn.add(item);
				}				
			}
		}
		
		return listReturn;
	}
	
	/**
	 * Update AuthorizationDetails
	 * @param id
	 * @param status
	 * @return Boolean
	 */
	public AuthorizationDetails updateAuthorizationDetails(Long id, String status) 
	{
		try {
			AuthorizationDetails details = successFactorAuthoImpl.getAuthorizationDetailsById(id);
			details.setStatus(status);
			details.setEnable(false);
			details.setIsCurrentAutho(false);
			
			details = successFactorAuthoImpl.updateAuthorizationDetails(details);
			return details;
		}catch (Exception e) {
			e.printStackTrace();
			UtilLogger.getInstance().info("Error Update AuthorizationDetails ");
			return null;
		}
	}
	
	/**
	 * Get Status Authorization
	 * @param user
	 * @param idTmp
	 * @return AuthorizationDocument
	 */
	public List<AuthorizationDocument> getStatusAuthorization (String user,Long idTmp) {
		return successFactorAuthoImpl.getAuthorizationDocument(user, idTmp);
	}
	
	/**
	 * get all details of athorizations
	 * @return List<AuthorizationDetails>
	 * */
	public List<AuthorizationDetails> authorizationDetailGetAll(){
		return successFactorAuthoImpl.authorizationDetailGetAll();
	}
	
	
	/**
	 * Get Authorization Step
	 * @param Long idAuthoControl
	 * @param id
	 * @param user
	 * @return ResponseGenericDto
	 */
	public ResponseGenericDto authoGetCurrentStep(Long idAuthoControl, Long id, String user) 
	{	
		ResponseGenericDto response = new ResponseGenericDto();
		
		List<AuthorizationDetails> listDetail = successFactorAuthoImpl.sfTemplateAuth().getAuthorizationDetailUserTemplate(idAuthoControl,user, id);
		
		if(listDetail!=null)
		{
			int step= 0;			
			for (AuthorizationDetails aut : listDetail) 
			{
				if(aut.getStatus().equals(UtilCodesEnum.CODE_MANAGER_AUTHORIZED.getCode())){				
					step ++;
				}
			}
			
			response.setCode(step+"");
			response.setMessage(step +" / "+listDetail.size());
		}
		
		return response;
	}
	
	/**
	 * Delete Authorization
	 * @param id
	 * @return
	 */
	public Boolean deleteAuthorization(Long id) {
		try {
			AuthorizationDetails detail = successFactorAuthoImpl.getAuthorizationDetailsById(id);
			successFactorAuthoImpl.deleteAuthorizationDetails(detail);
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}
	
	/**
	 * Update AuthorizationDocument
	 * @param id
	 * @param status
	 * @return Boolean
	 */
	public Boolean updateStatusAuthorizationDocument(String user, Long idTmp) {
		
		try {
			List <AuthorizationDocument> list = successFactorAuthoImpl.sfTemplateAuth().getAuthorizationDocument(user, idTmp);
			
			if(list != null && !list.isEmpty()) {
				AuthorizationDocument doc = list.get(0);
				doc.setStatus(UtilCodesEnum.CODE_MANAGER_AUTHORIZED.getCode());
				successFactorAuthoImpl.authorizationUpdate(doc);
			}
			
			return Boolean.TRUE;
		}catch (Exception e) {
			e.printStackTrace();
			UtilLogger.getInstance().info("Error Update AuthorizationDetails ");
			return Boolean.FALSE;
		}
		
	}
	

	/**
	 * Reboot Authorization
	 * @param user
	 * @param idTmp
	 * @param idDoc
	 * @return Boolean
	 */
	/*public Boolean getDocAuthorization(String user, Long idTmp,Long idDoc ) {
		
		try {
			SuccessFactorFacade sfDoc = new SuccessFactorFacade();
			List <AuthorizationDocument> list = successFactorAuthoImpl.getAuthorizationDocument(user, idTmp);
			List<AuthorizationDetails> listDetail = new ArrayList<>();
			listDetail = successFactorAuthoImpl.getAuthorizationDetailUserTemplate(null,user, idTmp);
			Document docGen = sfDoc.documentGetById(idDoc);
			if(list != null && !list.isEmpty()) {
				AuthorizationDocument doc = list.get(0);
				doc.setStatus(UtilCodesEnum.CODE_MANAGER_PENDING.getCode());
				doc.setDocumentId(docGen.getId());
				doc.setEnable(Boolean.FALSE);
				successFactorAuthoImpl.saveNewAuthorizationDocument(doc);
			}
			for (AuthorizationDetails auDet : listDetail) {
				auDet.setStatus(UtilCodesEnum.CODE_MANAGER_PENDING.getCode());
				auDet.setEnable(Boolean.FALSE);
				successFactorAuthoImpl.deleteAuthorizationDetails(auDet);
			}
			return Boolean.TRUE;
		}catch (Exception e) {
			e.printStackTrace();
			UtilLogger.getInstance().info(e.getMessage());
			return Boolean.FALSE;
		}
		
	}*/
	
	/**
	 * Get Info Authorization Document
	 * @param user
	 * @return List<AuthorizationDocument>
	 */
	public List<AuthorizationDocument> getInfoAuthorizationDocument (String user)
	{
		SuccessFactorFacade successFacade = new SuccessFactorFacade();
		List<AuthorizationDocument> list = successFactorAuthoImpl.getInfoAuthorizationDocument(user);
		List<AuthorizationDocument> listReturn = new ArrayList<>();
		
		for (AuthorizationDocument item : list) 
		{
			item.currentStep = "";
			if(item.getStatus().equals(UtilCodesEnum.CODE_MANAGER_PENDING.getCode())) {
				item.currentStep = this.authoGetCurrentStep(item.getId(),item.getTemplate().getId(), user).getMessage();
			}
			
			
			if(item.getStatus().equals(UtilCodesEnum.CODE_MANAGER_GENERATED.getCode()))
			{
				if(item.getDocumentId()!=null)
				{
					Document document = successFacade.documentGetById(item.getDocumentId());
					
					if(document!=null)
					{
						item.setGeneratedIdPpd("");
						
						item.setStatusDocumentId(document.getStatus());					
						List<Generated> objectArray = successFacade.generatedGetByIdDoc(item.getDocumentId());
						if(objectArray!=null && objectArray.size()>0)
							item.setGeneratedIdPpd(objectArray.get(objectArray.size()-1).getGeneratedIdPpd());
					}
					else
					{
						item.setStatusDocumentId(UtilCodesEnum.CODE_ERROR.getCode());
					}
				}
			}	
			
			listReturn.add(item);
		}
		return listReturn;
	}
	
	/**
	 * update Authorization
	 * @param AuthorizationDocument autho
	 * @return AuthorizationDocument
	 * */
	public AuthorizationDocument authorizationUpdate(AuthorizationDocument autho)
	{	
		successFactorAuthoImpl.authorizationUpdate(autho);
		return autho;
	}		
	
	/**
	 * get authorization document by id
	 * @param Long id
	 * @return AuthorizationDocument
	 * */
	public AuthorizationDocument authoGetById(Long id)
	{
		return successFactorAuthoImpl.authoGetById(id);
	}
}
