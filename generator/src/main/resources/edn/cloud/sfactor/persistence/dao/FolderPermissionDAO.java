package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.FolderGroup;

public class FolderPermissionDAO extends BasicDAO<FolderGroup>
{
	public FolderPermissionDAO() {
		super(EntityManagerProvider.getInstance());
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection<FolderGroup> getAllUsersByFolderId(Long folderid)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = "SELECT t FROM FolderGroup t WHERE t.folderId = "+folderid;			
			final Query query = em.createQuery(sql);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error getAllUsersByFolderId", e); //$NON-NLS-1$
			
		}			
	}
	
	
	public boolean deleteAllUsersByFolderId(Long id) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from FolderGroup u where u.folderId =:id ");
			query.setParameter("id", id);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteAllUsersByFolderId", e); //$NON-NLS-1$	
		}		
	}
}
