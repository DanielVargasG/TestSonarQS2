package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.FolderUser;

public class FolderUserDAO extends BasicDAO<FolderUser> {


	public FolderUserDAO() {
		super(EntityManagerProvider.getInstance());
	}

	@SuppressWarnings("unchecked")
	public Collection<FolderUser> getAllUsersByFolderId(Long folderid) {
		final EntityManager em = emProvider.get();

		try {
			String sql = "SELECT t FROM FolderUser t WHERE t.folderId = " + folderid;
			final Query query = em.createQuery(sql);
			return query.getResultList();
		} catch (Exception e) {
			throw new IllegalStateException("Error getAllUsersByFolderId", e); //$NON-NLS-1$

		}
	}

	@SuppressWarnings("unchecked")
	public Collection<FolderUser> getAllFolderByUserId(String userid, FilterQueryDto filter) {
		final EntityManager em = emProvider.get();
		HashMap<String, String> hm = new HashMap<>();
		String filterSql="";
		try {
			if(filter!=null) {
				hm = filter.getItem();
				
				if(!hm.get("idFolder").equals(null) && !hm.get("idFolder").equals("")) {
					filterSql += " and t.folderId = "+hm.get("idFolder");
				}
				
			}
			
			String sql = "SELECT t FROM FolderUser t WHERE t.userId = '" + userid + "'"+filterSql;
			final Query query = em.createQuery(sql);
			return query.getResultList();
		} catch (Exception e) {
			throw new IllegalStateException("Error getAllUsersByFolderId", e); //$NON-NLS-1$

		}
	}

	public boolean deleteAllUsersByFolderId(Long id) {
		final EntityManager em = emProvider.get();

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query = em.createQuery("delete from FolderUser u where u.folderId =:id ");
			query.setParameter("id", id);

			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteAllUsersByFolderId", e); //$NON-NLS-1$
		}
	}
}
