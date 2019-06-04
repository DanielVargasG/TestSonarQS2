package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.DocumentSignature;
import edn.cloud.sfactor.persistence.entities.TemplateGroupSignature;
import edn.cloud.sfactor.persistence.entities.TemplateSignature;

public class DocumentSignatureDAO extends BasicDAO<DocumentSignature> 
{

	public DocumentSignatureDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	/**
	 * delete signature document
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteDocumentSignatureById(Long id) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from DocumentSignature u where u.id =:id ");
			query.setParameter("id", id);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteDocumentSignatureById", e); //$NON-NLS-1$	
		}		
	}	
	
	
	/**
	 * delete all signature by document
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
			
			final Query query = em.createQuery("delete from DocumentSignature u where u.documentId.id =:id ");
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
	public Collection<DocumentSignature> getAllByDocument(Long idDocument) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery("select u from DocumentSignature u where u.documentId.id =:id ");
			query.setParameter("id", idDocument);
			
			return query.getResultList(); 
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	public Collection<TemplateSignature> getAllFields(TemplateGroupSignature group) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery("select u from TemplateSignature u where u.group =:groupId ");
			query.setParameter("groupId", group);
			
			return query.getResultList(); 
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for group sings %s from table SignatureTemplate."), ex); //$NON-NLS-1$
		}
		return null;
		
	}
}
