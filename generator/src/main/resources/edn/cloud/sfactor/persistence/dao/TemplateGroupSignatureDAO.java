package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.TemplateGroupSignature;
import edn.cloud.sfactor.persistence.entities.Template;

public class TemplateGroupSignatureDAO extends BasicDAO<TemplateGroupSignature> 
{

	public TemplateGroupSignatureDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection<TemplateGroupSignature> getAllFields(Template template) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery("select u from TemplateGroupSignature u where u.templateId =:templateId ");
			query.setParameter("templateId", template);
			
			return query.getResultList(); 
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity."), ex); //$NON-NLS-1$
		}
		return null;		
	}
}
