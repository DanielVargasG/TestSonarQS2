package edn.cloud.sfactor.persistence.dao;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.SignatureFileControlDetail;

public class SignatureFileControlDetailDAO extends BasicDAO<SignatureFileControlDetail>{

	public SignatureFileControlDetailDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<SignatureFileControlDetail> getAllByCtrlId(Long idCtrl) 
	{
		final EntityManager em = emProvider.get();
		try 
		{
			final Query query = em.createQuery("select u from SignatureFileControlDetail u where  u.signatureFileCtrl.id = :idCtrl ");
			
			query.setParameter("idCtrl", idCtrl);
			
			Collection<SignatureFileControlDetail> result = query.getResultList();			
			ArrayList<SignatureFileControlDetail> retorno = new ArrayList<>(result);
			
			return  retorno;
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("Error to find all signature control detail."), ex); //$NON-NLS-1$
		}
		return null;	
	}
	
	/**
	 * find all signs details by Document 
	 * @param Long idDoc
	 * @param String statusSignCtrl **optional
	 * @return ArrayList<SignatureFileControlDetail>
	 * */
	public ArrayList<SignatureFileControlDetail> getAllSignDetailByDoc(Long idDoc,String statusSignCtrl) 
	{
		final EntityManager em = emProvider.get();
		try 
		{
			String sql = "SELECT u FROM SignatureFileControlDetail u "
						  +"join u.generated g  "
						  +"join g.document d "
						  +"WHERE d.id =:idDoc ";
			
			if(statusSignCtrl!=null && !statusSignCtrl.equals(""))
				sql+= " and u.status IN("+statusSignCtrl+") ";
			
			final Query query = em.createQuery(sql);
			query.setParameter("idDoc",idDoc);
			
			Collection<SignatureFileControlDetail> result = query.getResultList();			
			ArrayList<SignatureFileControlDetail> retorno = new ArrayList<>(result);
			
			return  retorno;
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return null;
	}
}
