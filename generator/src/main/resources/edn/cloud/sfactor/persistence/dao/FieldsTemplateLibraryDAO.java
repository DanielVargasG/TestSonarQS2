package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.FieldsTemplateLibrary;

public class FieldsTemplateLibraryDAO extends BasicDAO<FieldsTemplateLibrary> 
{

	public FieldsTemplateLibraryDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	/**
	 * get all field template library by name
	 * @param Boolean isAttach / optional
	 * @return Collection<FieldsTemplateLibrary>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<FieldsTemplateLibrary> getByName(String name) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery("select u from FieldsTemplateLibrary u where u.nameSource=:name ");
			query.setParameter("name",name);
			return query.getResultList(); 
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error getByName", e); //$NON-NLS-1$	
		}		
	}	
	
	
	/**
	 * get all field template library
	 * @param Boolean isAttach / optional
	 * @return Collection<FieldsTemplateLibrary>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<FieldsTemplateLibrary> getAllFields() 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery("select u from FieldsTemplateLibrary u order by u.nameSource asc ");			
			return query.getResultList(); 
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error getAllFields", e); //$NON-NLS-1$	
		}		
	}

	/**
	 * delete field templa
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteById(Long id) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from FieldsTemplateLibrary u where u.id =:id ");
			query.setParameter("id", id);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteById", e); //$NON-NLS-1$	
		}		
	}	
}