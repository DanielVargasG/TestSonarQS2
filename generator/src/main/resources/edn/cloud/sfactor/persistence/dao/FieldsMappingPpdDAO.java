package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.dto.GenErrorInfoDto;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;

public class FieldsMappingPpdDAO extends BasicDAO<FieldsMappingPpd> 
{

	public FieldsMappingPpdDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	
	/**
	 * get all mapping fields by name
	 * @param String name
	 * @return Collection<FieldsMappingPpd>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<FieldsMappingPpd> getAllFieldsMappingByName(String name) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "select u from FieldsMappingPpd u ";
			sql = sql+" WHERE LOWER(u.nameSource) = '"+name.toLowerCase()+"' ";
			final Query query = em.createQuery(sql);
			
			
			return query.getResultList(); 
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error getAllFieldsMappingByName", e); //$NON-NLS-1$	
		}		
	}	
	
	/**
	 * get all mapping fields people doc
	 * @param Boolean isAttach / optional
	 * @param String moduleAttach
	 * @param Boolean isActive
	 * @return Collection<FieldsMappingPpd>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<FieldsMappingPpd> getAllFieldsMapping(Boolean isAttach,String moduleAttach,Boolean isActive) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "select u from FieldsMappingPpd u WHERE u.nameDestination IS NOT NULL ";
			
			if(isAttach!=null){sql = sql+" AND u.isAttached = "+isAttach.toString();}
			
			if(isActive!=null && isActive){
				sql = sql+" AND (u.isActive = "+isActive.toString()+" OR u.isActive IS NULL) ";
			}
			else if(isActive!=null){
				sql = sql+" AND u.isActive = "+isActive.toString();
			}
			
			if(moduleAttach!=null && moduleAttach.equals(UtilCodesEnum.CODE_NA.getCode())){
				sql = sql+" AND u.typeModule IS NULL OR u.typeModule='' ";
			}
			else if(moduleAttach!=null && moduleAttach.equals(UtilCodesEnum.CODE_NOTNULL.getCode())){
				sql = sql+" AND u.typeModule IS NOT NULL ";
			}
			else if(moduleAttach!=null){
				sql = sql+" AND u.typeModule IN ('"+moduleAttach+"') ";
			}
			
			final Query query = em.createQuery(sql);
			
			
			return query.getResultList(); 
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteFieldsMappingPpdById", e); //$NON-NLS-1$	
		}		
	}
	
	
	/**
	 * determines if the Field Mapping is used by another entity
	 * @param Long id Field Mapping	  
	 * @return GenErrorInfoDto
	 * */
	public GenErrorInfoDto mappingFieldIsUsedById(Long id)
	{
		GenErrorInfoDto response = new GenErrorInfoDto();
		response.setFlag(Boolean.FALSE);
		
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "select e from EventListenerDocProcess e ";
			sql = sql+" WHERE e.fieldMapPpd.id = "+id;
			final Query query = em.createQuery(sql);
			query.setMaxResults(1);
						
			Collection<EventListenerDocProcess> register = query.getResultList(); 
			
			if(register!=null && register.size()>0) {
				response.setFlag(Boolean.TRUE);
			}
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error isUsedFieldMappingPpdById", e); //$NON-NLS-1$	
		}		
		
		return response;
	}

	/**
	 * delete mapping Field by Id
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteFieldsMappingPpdById(Long id) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from FieldsMappingPpd u where u.id =:id ");
			query.setParameter("id", id);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteFieldsMappingPpdById", e); //$NON-NLS-1$	
		}		
	}
	
	/**
	 * get all mapping fields by name
	 * @param String name
	 * @return Collection<FieldsMappingPpd>
	 * */
	@SuppressWarnings("unchecked")
	public FieldsMappingPpd getFieldMappingByName(String name) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "select u from FieldsMappingPpd u ";
			sql = sql+" WHERE LOWER(u.nameSource) = '"+name.toLowerCase()+"' ";
			final Query query = em.createQuery(sql);
			
			
			return (FieldsMappingPpd) query.getSingleResult(); 
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error getAllFieldsMappingByName", e); //$NON-NLS-1$	
		}		
	}
	
}
