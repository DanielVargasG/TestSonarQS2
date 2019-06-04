package edn.cloud.sfactor.persistence.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.ManagerRoleGroup;

public class ManagerRoleGroupDAO extends BasicDAO<ManagerRoleGroup>{

	public ManagerRoleGroupDAO(){
		super(EntityManagerProvider.getInstance());
	}
	
	@SuppressWarnings("unchecked")
	public List<ManagerRoleGroup> getAllManagerGroup(Long id) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{			
			final Query query = em.createQuery("Select u from ManagerRoleGroup u where u.managerRoleId =:id ");
			query.setParameter("id", id);
			List<ManagerRoleGroup> list =new ArrayList<>(query.getResultList());
			return list; 
		}
		catch (Exception e) 
		{	
			e.printStackTrace();
			return null;
		}		
	}
}
