package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.FieldsTemplate;
import edn.cloud.sfactor.persistence.entities.Template;

public class FieldsTemplateDAO extends BasicDAO<FieldsTemplate> 
{

	public FieldsTemplateDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection<FieldsTemplate> getAllFields(Template template) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery(
					"select f "
					+ "from FieldsTemplate f "
					+ "where f.templateId =:templateId ");
			
			query.setParameter("templateId", template);
			
			return query.getResultList(); 
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User."), ex); //$NON-NLS-1$
		}
		return null;
		
	}

	public boolean deleteFieldsByIdTemplate(Template template) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from FieldsTemplate u where u.templateId =:templateId ");
			query.setParameter("templateId", template);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteFieldsByIdTemplate", e); //$NON-NLS-1$	
		}		
	}
	
}
