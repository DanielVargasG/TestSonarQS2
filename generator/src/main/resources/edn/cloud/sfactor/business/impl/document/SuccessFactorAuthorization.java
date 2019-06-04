package edn.cloud.sfactor.business.impl.document;

import java.util.List;

import edn.cloud.sfactor.business.interfaces.document.SucessFactorAuthorization;
import edn.cloud.sfactor.persistence.dao.AuthorizationDetailDAO;
import edn.cloud.sfactor.persistence.dao.AuthorizationDocumentDAO;
import edn.cloud.sfactor.persistence.entities.AuthorizationDetails;
import edn.cloud.sfactor.persistence.entities.AuthorizationDocument;

public class SuccessFactorAuthorization implements SucessFactorAuthorization 
{
	private AuthorizationDetailDAO  authorizationDao = new AuthorizationDetailDAO();
	private AuthorizationDocumentDAO authorizationDocumentDAO = new AuthorizationDocumentDAO();

	/**
	 * Get AuthorizationDAO
	 * 
	 */
	public AuthorizationDetailDAO sfTemplateAuth () {
		AuthorizationDetailDAO authoDAO = new AuthorizationDetailDAO();
		return authoDAO;
	}
	
	/**
	 * get authorization document by id
	 * @param Long id
	 * @return AuthorizationDocument
	 * */
	public AuthorizationDocument authoGetById(Long id)
	{
		return authorizationDocumentDAO.getById(id);
	}
	

	/**
	 * get details of athorizations by id Autho Control
	 * @param Long idAutoCtrl
	 * @return List<AuthorizationDetails>
	 * */
	public List<AuthorizationDetails> authorizationDetailByIdControl(Long idAutoCtrl){
		return authorizationDao.getAuthorizationDetailByIdControl(idAutoCtrl);
	}
	
	/**
	 * get all details of athorizations
	 * @return List<AuthorizationDetails>
	 * */
	public List<AuthorizationDetails> authorizationDetailGetAll(){
		return authorizationDao.getAll();
	}	
	
	/**
	 * save New Authorization Document
	 * @param doc
	 * @return AuthorizationDocument
	 */
	public AuthorizationDocument saveNewAuthorizationDocument(AuthorizationDocument doc) {
		return authorizationDocumentDAO.saveNew(doc);
	}
	
	/**
	 * update AuthorizationDocument entity
	 * @param AuthorizationDocument entity
	 * @return AuthorizationDocument
	 */
	public AuthorizationDocument authorizationUpdate(AuthorizationDocument entity) {
		return authorizationDocumentDAO.save(entity);
	}	
	
	/**
	 * save New Authorization Details
	 * @param doc
	 * @return AuthorizationDocument
	 */
	public AuthorizationDetails saveNewAuthorizationDetails(AuthorizationDetails doc) {
		return authorizationDao.saveNew(doc);
	}
	
	/**
	 * Authorization Detail User Template
	 * @param Long idAuthoControl
	 * @param user
	 * @param id
	 * @return List<AuthorizationDetails>
	 */
	public List<AuthorizationDetails> getAuthorizationDetailUserTemplate(Long idAuthoControl,String user, Long id){
		return authorizationDao.getAuthorizationDetailUserTemplate(idAuthoControl,user, id);
	}
	
	/**
	 * Authorization Control User
	 * @param user
	 * @param String statusNotIn
	 * @return List<AuthorizationDetails>
	 */
	public List<AuthorizationDetails> getAuthorizationControlUser(String user,String statusNotIn){
		return authorizationDao.getAuthorizationControlUser(user,statusNotIn);
	}
	
	/**
	 * Get Authorization Details By Id
	 * @param id
	 * @return AuthorizationDetails
	 */
	public AuthorizationDetails getAuthorizationDetailsById(Long id) {
		return authorizationDao.getById(id);
	}
	
	/**
	 * Update Authorization Details
	 * @param entity
	 * @return
	 */
	public AuthorizationDetails updateAuthorizationDetails(AuthorizationDetails entity) {
		return authorizationDao.save(entity);
	}
	
	/**
	 * Get Status Authorization
	 * @param user
	 * @param idTmp
	 * @return AuthorizationDocument
	 */
	public List<AuthorizationDocument> getAuthorizationDocument(String user,Long idTmp){
		return authorizationDao.getAuthorizationDocument(user, idTmp);
	}
	
	/**
	 * get count Athorizations Details by User
	 * @param String user
	 * @param String statusInAuthoControl
	 * @param String statusInDoc
	 * @return Integer
	 * */
	public Integer getAuthorizationCountByUser(String user,String statusInAuthoControl,String statusInDoc) {
		return authorizationDao.getAuthorizationCountByUser(user, statusInAuthoControl,statusInDoc);
	}
	
	/**
	 * Delete AuthorizationDetails
	 * @param detail
	 */
	public void deleteAuthorizationDetails(AuthorizationDetails detail) {
		authorizationDao.delete(detail);
	}
	
	/**
	 * Get Info Authorization Document
	 * @param user
	 * @return List<AuthorizationDocument>
	 */
	public List<AuthorizationDocument> getInfoAuthorizationDocument (String user){
		return authorizationDao.getInfoAuthorizationDocument(user);
	}
}
