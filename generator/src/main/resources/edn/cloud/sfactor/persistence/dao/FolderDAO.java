package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.Folder;

public class FolderDAO extends BasicDAO<Folder>{

	public FolderDAO() {
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
					+"FOLDERS "
					+"WHERE FK_PARENT_FOLDER = "+id);
			
			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteAllAssociatedWithSubFolders", e); //$NON-NLS-1$
		}
	} 
	
	/**
	 * get All subfolder from id folder parent
	 * @param Long folderParentId
	 * @return Collection<FolderDTO> 
	 * */
	@SuppressWarnings("unchecked")
	public Collection<Folder> getAllFolderByFolderParent(Long folderid) {
		final EntityManager em = emProvider.get();

		try {
			String sql = "SELECT t FROM Folder t WHERE t.folderId = " + folderid;
			final Query query = em.createQuery(sql);
			return query.getResultList();
		} catch (Exception e) {
			throw new IllegalStateException("Error getAllUsersByFolderId", e); //$NON-NLS-1$

		}
	}
	
	/**
	 * get all entities by Parent
	 * @param Long idParent 
	 * @return Collection<StructureBusiness>
	 */
	@SuppressWarnings("unchecked")
	public Collection<Folder> getByParent(Long idParent) 
	{
		final EntityManager em = emProvider.get();

		try 
		{
			String sql = "select f from Folder f ";
			
			if(idParent!=null){
				sql+= "WHERE f.parentFolder.id = "+idParent;
			}
			else{
				sql+= "WHERE f.parentFolder.id IS NULL ";
			}
			
			final Query query = em.createQuery(sql);

			return query.getResultList();
		} catch (Exception e) {
			throw new IllegalStateException("Error getByParent", e); //$NON-NLS-1$
		}
	}
}
