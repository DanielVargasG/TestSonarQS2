package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.TemplateGroupSignature;
import edn.cloud.sfactor.persistence.entities.TemplateSignature;
import edn.cloud.sfactor.persistence.entities.Template;

public class TemplateSignatureDAO extends BasicDAO<TemplateSignature> 
{

	public TemplateSignatureDAO() 
	{
		super(EntityManagerProvider.getInstance());
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

	/**
	 * delete signatures by templates
	 * */
	public boolean deleteSignatureByIdTemplate(Template template) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from TemplateSignature u where u.templateId =:templateId ");
			query.setParameter("templateId", template);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteSignatureByIdTemplate", e); //$NON-NLS-1$	
		}		
	}
	
}
