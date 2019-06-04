package edn.cloud.sfactor.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.RolesMappingPpd;

public class RolesMappingPpdDAO extends BasicDAO<RolesMappingPpd> 
{

	public RolesMappingPpdDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	
	

	/**
	 * delete mapping Field by Id
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteRoleById(Long id) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from RolesMappingPpd u where u.id =:id ");
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
	
}
