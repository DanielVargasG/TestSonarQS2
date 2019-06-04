package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.FolderTemplate;

public class FolderTemplateDAO extends BasicDAO<FolderTemplate> {

	public FolderTemplateDAO() {
		super(EntityManagerProvider.getInstance());
	}
	
	/**
	 * delete All Associated With SubFolders
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteAllAssoWithSubFolders(Long id){
		final EntityManager em = emProvider.get();

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query = em.createNativeQuery(
					"DELETE FROM " 
					+"FOLDERS_TEMPLATES "
					+"WHERE FOLDER_ID IN "
					+ "("
					+ "SELECT fs.ID FROM FOLDERS fs "					
					+" WHERE fs.FK_PARENT_FOLDER = "+id
					+ ") "
					);
			
			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteAllAssociatedWithSubFolders", e); //$NON-NLS-1$
		}
	} 	

	@SuppressWarnings("unchecked")
	public Collection<FolderTemplate> getByFolderId(Long folderid) {
		final EntityManager em = emProvider.get();

		try {
			String sql = "SELECT t FROM FolderTemplate t WHERE t.folderId = " + folderid;
			final Query query = em.createQuery(sql);
			return query.getResultList();
		} catch (Exception e) {
			throw new IllegalStateException("Error getAllUsersByFolderId", e); //$NON-NLS-1$

		}
	}

	public FolderTemplate getByTemplaterId(Long templateId) 
	{
		final EntityManager em = emProvider.get();

		try 
		{
			if(templateId!=null)
			{
				String sql = 
						"SELECT t FROM FolderTemplate t "
						+"WHERE t.templateId = " + templateId+" "
						+"ORDER BY t.id";
				final Query query = em.createQuery(sql);
				query.setMaxResults(1);
				FolderTemplate fd = (FolderTemplate) query.getSingleResult();
				
				return fd;
			}
			
			return null;
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			throw new IllegalStateException("Error getAllUsersByFolderId", e); //$NON-NLS-1$

		}
	}

	public boolean deleteAllUsersByFolderId(Long id) {
		final EntityManager em = emProvider.get();

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query = em.createQuery("delete from FolderTemplate u where u.folderId =:id ");
			query.setParameter("id", id);

			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteAllUsersByFolderId", e); //$NON-NLS-1$
		}
	}

	public boolean deleteAllTempsByTempsId(Long id) {
		final EntityManager em = emProvider.get();

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query = em.createQuery("delete from FolderTemplate u where u.templateId =:id ");
			query.setParameter("id", id);

			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteAllTempsByTempsId", e); //$NON-NLS-1$
		}
	}
}
