package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.Template;
import edn.cloud.sfactor.persistence.entities.TemplateFilters;

public class TemplateFiltersDAO extends BasicDAO<TemplateFilters> 
{
	public TemplateFiltersDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	/**
	 * get all templates filters by id template
	 * @param Template template
	 * @return Collection<TemplateFilters>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<TemplateFilters> getAllTemplateFiltersById(Template template)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = "SELECT t FROM TemplateFilters t WHERE t.templateId =:tempalteFilter";
			final Query query = em.createQuery(sql);
			query.setParameter("tempalteFilter", template);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error getAllTemplateFiltersById", e); //$NON-NLS-1$			
		}			
	}
	
	
	/**
	 * delete template filter
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteTemplateFilterById(Long id) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from TemplateFilters u where u.id =:id ");
			query.setParameter("id", id);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteTemplateFilterById", e); //$NON-NLS-1$	
		}		
	}
	
	/**
	 * delete template filter by id template
	 * @param Long idTemplate
	 * @return boolean
	 * */
	public boolean deleteTemplateFilterByIdTemplate(Long idTemplate) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from TemplateFilters u where u.templateId.id =:idTemplate ");
			query.setParameter("idTemplate", idTemplate);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteTemplateFilterByIdTemplate", e); //$NON-NLS-1$	
		}		
	}	
}