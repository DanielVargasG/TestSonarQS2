package edn.cloud.sfactor.business.interfaces.document;

import java.util.List;

import edn.cloud.sfactor.persistence.dao.AuthorizationDetailDAO;
import edn.cloud.sfactor.persistence.entities.AuthorizationDetails;
import edn.cloud.sfactor.persistence.entities.AuthorizationDocument;

public interface SucessFactorAuthorization 
{
	/**
	 * Get AuthorizationDAO
	 * 
	 */
	public AuthorizationDetailDAO sfTemplateAuth ();
	
	/**
	 * get authorization document by id
	 * @param Long id
	 * @return AuthorizationDocument
	 * */
	public AuthorizationDocument authoGetById(Long id);
	
	/**
	 * save New Authorization Document
	 * @param doc
	 * @return AuthorizationDocument
	 */
	public AuthorizationDocument saveNewAuthorizationDocument(AuthorizationDocument doc);
	
	/**
	 * update AuthorizationDocument entity
	 * @param AuthorizationDocument entity
	 * @return AuthorizationDocument
	 */
	public AuthorizationDocument authorizationUpdate(AuthorizationDocument entity);	
	
	/**
	 * get all details of athorizations
	 * @return List<AuthorizationDetails>
	 * */
	public List<AuthorizationDetails> authorizationDetailGetAll();
	
	/**
	 * save New Authorization Details
	 * @param doc
	 * @return AuthorizationDocument
	 */
	public AuthorizationDetails saveNewAuthorizationDetails(AuthorizationDetails doc);
	
	/**
	 * Authorization Detail User Template
	 * @param user
	 * @param id
	 * @return List<AuthorizationDetails>
	 */
	public List<AuthorizationDetails> getAuthorizationDetailUserTemplate(Long idAuthoControl,String user, Long id);
	
	/**
	 * Authorization Control User
	 * @param String user
	 * @param String statusNotIn
	 * @return List<AuthorizationDetails>
	 */
	public List<AuthorizationDetails> getAuthorizationControlUser(String user,String statusNotIn);
	
	/**
	 * Get Authorization Details By Id
	 * @param id
	 * @return AuthorizationDetails
	 */
	public AuthorizationDetails getAuthorizationDetailsById(Long id);
	
	/**
	 * Update Authorization Details
	 * @param entity
	 * @return
	 */
	public AuthorizationDetails updateAuthorizationDetails(AuthorizationDetails entity);
	
	/**
	 * Get Status Authorization
	 * @param user
	 * @param idTmp
	 * @return AuthorizationDocument
	 */
	public List<AuthorizationDocument> getAuthorizationDocument(String user,Long idTmp);
	
	/**
	 * Delete AuthorizationDetails
	 * @param detail
	 */
	public void deleteAuthorizationDetails(AuthorizationDetails detail);
	
	/**
	 * Get Info Authorization Document
	 * @param user
	 * @return List<AuthorizationDocument>
	 */
	public List<AuthorizationDocument> getInfoAuthorizationDocument (String user);
	
	/**
	 * get details of athorizations by id Autho Control
	 * @param Long idAutoCtrl
	 * @return List<AuthorizationDetails>
	 * */
	public List<AuthorizationDetails> authorizationDetailByIdControl(Long idAutoCtrl);	
	
	/**
	 * get count Athorizations Details by User
	 * @param String user
	 * @param String statusInAuthoControl
	 * @param String statusInDoc
	 * @return Integer
	 * */
	public Integer getAuthorizationCountByUser(String user,String statusInAuthoControl,String statusInDoc);
}
