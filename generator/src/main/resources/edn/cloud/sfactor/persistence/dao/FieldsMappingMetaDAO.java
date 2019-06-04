package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.FieldsMappingMeta;

public class FieldsMappingMetaDAO extends BasicDAO<FieldsMappingMeta> 
{

	public FieldsMappingMetaDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	/**
	 * delete Fields Metadata
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
			
			final Query query = em.createQuery("delete from FieldsMappingMeta u where u.id =:id ");
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
	 * delete all Fields Mapping Meta 
	 * @param Long idFieldMapp
	 * @param Long idTemplate
	 * @return boolean
	 * */
	public boolean deleteAllByFieldsMapping(Long idFieldMapp, Long idTemplate) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "";						
			if(idFieldMapp!=null)
				sql = "delete from FieldsMappingMeta u where u.fieldsMappingPpdId =:id ";
			
			if(idTemplate!=null)
				sql = "delete from FieldsMappingMeta u where u.templateId =:id ";
						
			final Query query = em.createQuery(sql);
			
			if(idFieldMapp!=null) {
				query.setParameter("id", idFieldMapp);
			}
			if(idTemplate!=null) {
				query.setParameter("id", idTemplate);
			}
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteAllByFieldsMapping", e); //$NON-NLS-1$	
		}		
	}
	
	/**
	 * Get all field mapping by filters
	 * @param Long idFielMapping
	 * @param Long idFieldTemplateLib
	 * */
	@SuppressWarnings("unchecked")
	public Collection<FieldsMappingMeta> getAllByIdFieldMap(Long idFielMapping,Long idFieldTemplateLib) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "select u from FieldsMappingMeta u where u.fieldsTemplateLibrary.id is not null  ";
			sql = sql+(idFielMapping!=null?" and u.fieldsMappingPpdId =:id ":"");
			sql = sql+(idFieldTemplateLib!=null?" and u.fieldsTemplateLibrary.id =:idlib ":"");
			
			final Query query = em.createQuery(sql);
			
			if(idFielMapping!=null)
				query.setParameter("id",idFielMapping);
			
			if(idFieldTemplateLib!=null)
				query.setParameter("idlib",idFieldTemplateLib);			
			
			return query.getResultList(); 
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return null;		
	}
	
	/**
	 * delete all Template MetaData 
	 * @param Long idFieldMapp
	 * @return boolean
	 * */
	public boolean deleteAllByTemplate(Long idTemplate) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from FieldsMappingMeta u where u.templateId =:id ");
			query.setParameter("id", idTemplate);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteAllByFieldsMapping", e); //$NON-NLS-1$	
		}		
	}
	
	
	public Collection<FieldsMappingMeta> getAllByIdTemplate(Long id) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery("select u from FieldsMappingMeta u where u.templateId =:id ");
			query.setParameter("id",id);
			
			return query.getResultList(); 
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return null;		
	}
}
