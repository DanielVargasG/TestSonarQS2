package edn.cloud.sfactor.persistence.dao;


import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.AuthorizationDetails;
import edn.cloud.sfactor.persistence.entities.AuthorizationDocument;

public class AuthorizationDetailDAO extends BasicDAO<AuthorizationDetails>{
	
	public AuthorizationDetailDAO() {
		super(EntityManagerProvider.getInstance());
	}
	
	@SuppressWarnings("unchecked")
	protected EntityManagerProvider emProvider2;
	
	private BasicDAO<AuthorizationDocument> authDocument = new BasicDAO<AuthorizationDocument>(emProvider);
	
	public BasicDAO<AuthorizationDocument> AuthDocument() {
		authDocument.emProvider = emProvider2.getInstance();
		return authDocument;
	}
	
	/**
	 * get details of athorizations by id Autho Control
	 * @param Long idAutoCtrl
	 * @return List<AuthorizationDetails>
	 * */
	public List<AuthorizationDetails> getAuthorizationDetailByIdControl(Long idAutoCtrl)
	{
		final EntityManager em = emProvider.get();
		try
		{
			String sql = 
					"SELECT audet " 
					+"FROM AuthorizationDetails audet " 
					+"WHERE audet.authDocument.id = "+idAutoCtrl+" ";
			
			final Query query = em.createQuery(sql);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error getAllTemplateForAuthorization ", e); //$NON-NLS-1$
			
		}
	}
	
	/**
	 * Get AuthorizationControl
	 * @param idTmp
	 * @param user
	 * @return List<AuthorizationControl>
	 */
	public List<AuthorizationDetails> getAuthorizationControl(Long idTmp, String user)
	{
		final EntityManager em = emProvider.get();
		try
		{
			String sql = 
					"SELECT audet " 
					+"FROM AuthorizationDetails audet " 
					+"WHERE audet.userId = '" + user + "' AND audet.authDocument.template.id = "+idTmp+" ";
					/*+"INNER JOIN AuthorizationDocument audoc ON (audoc.id = at.authDocument) "
					+"INNER JOIN AuthorizationDetails audet ON (at.authDetails = audet.id) " 
					+"WHERE audoc.template = "+idTmp+" AND audet.userId = '" + user + "' ";*/
					//at.
			/*String sql = "SELECT at " 
					+"FROM AuthorizationControl at, " 
					+"AuthorizationDocument audoc, "
					+"AuthorizationDetails audet " 
					+"WHERE audoc.template = "+idTmp+" AND audet.userId = '" + user + "' "
					+"AND audoc.id = at.authDocument.id "
					+"AND at.authDetails.id = audet.id ";*/
			//final Query query = em.createNativeQuery(sql, AuthorizationDetails.class);
			final Query query = em.createQuery(sql);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error getAllTemplateForAuthorization ", e); //$NON-NLS-1$
			
		}
	}

	/**
	 * @param Long idAuthoControl
	 * @param String user
	 * @param Long idTmp
	 * @return List<AuthorizationDetails>
	 * */
	public List<AuthorizationDetails> getAuthorizationDetailUserTemplate(Long idAuthoControl,String user, Long idTmp){
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = "SELECT audet " 
					+"FROM AuthorizationDetails audet " 
					+"WHERE audet.authDocument.userEmp = '" + user + "' "
					+"AND audet.authDocument.template.id = "+idTmp+" " +
					(idAuthoControl!=null?" AND audet.authDocument.id = "+idAuthoControl:"");

			final Query query = em.createQuery(sql);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error Authorization Detail User Template ", e); //$NON-NLS-1$
			
		}
	}
	
	/**
	 * get Athorizations Details by User
	 * @param String user
	 * @param String statusNotIn
	 * */
	public List<AuthorizationDetails> getAuthorizationControlUser(String user,String statusNotIn)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = 
					"SELECT audet " 
					+"FROM AuthorizationDetails audet "
					+"JOIN audet.authDocument autho " 
					+"WHERE audet.userId = '" + user + "' "
					+"AND autho.status IN ("+statusNotIn+") " ;
			
			final Query query = em.createQuery(sql);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error Authorization Control User ", e); //$NON-NLS-1$
			
		}
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
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = 
					"SELECT COUNT(audet) " 
					+"FROM AuthorizationDetails audet "
					+"JOIN audet.authDocument autho " 
					+"WHERE audet.userId = '" + user + "' "
					+"AND audet.isCurrentAutho = true "
					+"AND audet.status IN ("+statusInDoc+") "
					+"AND autho.status IN ("+statusInAuthoControl+") " ;
			
			final Query query = em.createQuery(sql);
			Object obj = query.getSingleResult();
			return Integer.parseInt(obj.toString());
		}
		catch (Exception e) {
			throw new IllegalStateException("Error getAuthorizationCountByUser ", e); //$NON-NLS-1$
			
		}
	}
	
	
	/**
	 * Get Status Authorization
	 * @param user
	 * @param idTmp
	 * @return AuthorizationDocument
	 */
	public List<AuthorizationDocument> getAuthorizationDocument (String user, Long idTmp) {
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = "SELECT audoc " 
					+"FROM AuthorizationDocument audoc " 
					+"WHERE audoc.userEmp = '" + user + "' "
					+"AND audoc.template.id = "+idTmp+" ";
			
			final Query query = em.createQuery(sql);			
			return query.getResultList();
		}
		catch (Exception e) {
			UtilLogger.getInstance().info(e.getMessage());
			return (null);
			
		}
	}
	
	/**
	 * Get Info Authorization Document
	 * @param user
	 * @return List<AuthorizationDocument>
	 */
	public List<AuthorizationDocument> getInfoAuthorizationDocument (String user) {
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = "SELECT audoc " 
					+"FROM AuthorizationDocument audoc " 
					+"WHERE audoc.userEmp = '" + user + "' ";
			final Query query = em.createQuery(sql);
			
			return query.getResultList();
		}
		catch (Exception e) {
			UtilLogger.getInstance().info(e.getMessage());
			return (null);
			
		}
	}
	
}
