package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.DocumentFields;

public class DocumentFieldsDAO extends BasicDAO<DocumentFields> 
{

	public DocumentFieldsDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	/**
	 * delete document field
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteDocumentFieldById(Long id) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from DocumentFields u where u.id =:id ");
			query.setParameter("id", id);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteDocumentFieldById", e); //$NON-NLS-1$	
		}		
	}	
	
	
	/**
	 * delete all document field
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteAllByDocId(Long idDoc) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from DocumentFields u where u.documentId.id =:id ");
			query.setParameter("id", idDoc);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteAllByDocId", e); //$NON-NLS-1$	
		}		
	}
	
	@SuppressWarnings("unchecked")
	public Collection<DocumentFields> getAllByDocument(Long idDocument) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery("select u from DocumentFields u where u.documentId.id =:id ");
			query.setParameter("id", idDocument);
			
			return query.getResultList(); 
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
		
	}
}
