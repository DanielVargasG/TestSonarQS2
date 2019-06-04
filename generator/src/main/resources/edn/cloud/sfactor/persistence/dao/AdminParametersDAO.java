package edn.cloud.sfactor.persistence.dao;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.AdminParameters;

public class AdminParametersDAO extends BasicDAO<AdminParameters> 
{

	public AdminParametersDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	/**
	 * get all parameters admin
	 * @return AdminParameters
	 * */
	@SuppressWarnings("unchecked")
	public AdminParameters getAdminParamGetByNameCode(String nameCode) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery("select u from AdminParameters u where u.nameCode = '"+nameCode+"' ");			
			Collection<AdminParameters> returnList= query.getResultList();
			if(returnList!=null && returnList.size()>0){
				ArrayList<AdminParameters> responseList = new ArrayList<AdminParameters>(returnList);
				return responseList.get(0);
			}
			
			return null;
			
		}
		catch (NoResultException n) {
			return null;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new IllegalStateException("Error getAllParametersAdmin", e); //$NON-NLS-1$	
		}		
	}	
	
	/**
	 * get all parameters admin
	 * @param Boolean isControlPanel
	 * @return Collection<AdminParameters>
	 * */
	public Collection<AdminParameters> getAllParametersAdmin(Boolean isControlPanel) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "select u from AdminParameters u ";
			
			if(isControlPanel!=null) 
			{
				if(!isControlPanel)
					sql += " where u.isControlPanel IS NULL or u.isControlPanel= :isControl ";
				else
					sql += " where u.isControlPanel= :isControl ";
			}
			
			final Query query = em.createQuery(sql);
			query.setParameter("isControl",isControlPanel);
			
			return query.getResultList(); 
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error getAllParametersAdmin", e); //$NON-NLS-1$	
		}		
	}

	/**
	 * delete entity by id
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteEntityById(Long id) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from AdminParameters u where u.id =:id ");
			query.setParameter("id", id);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteEntityById", e); //$NON-NLS-1$	
		}		
	}
	
	/**
	 * delete all admin parameter
	 * @return boolean
	 * */
	public boolean deleteAllAdmParam() 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from AdminParameters");
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteEntityById", e); //$NON-NLS-1$	
		}		
	}	
	
	
	/**
	 * delete all information from a table
	 * @param String nameTable
	 * @return boolean
	 * */
	public boolean deleteAllByTable(String nameTable) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from "+nameTable);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new IllegalStateException("Error deleteAllByTable", e); //$NON-NLS-1$	
		}		
	}	
	
}
