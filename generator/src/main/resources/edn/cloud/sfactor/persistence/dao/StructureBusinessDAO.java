package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.DBQueries;
import edn.cloud.sfactor.persistence.entities.StructureBusiness;

public class StructureBusinessDAO extends BasicDAO<StructureBusiness> {

	public StructureBusinessDAO() {
		super(EntityManagerProvider.getInstance());
	}

	/**
	 * get all entities
	 * 
	 * @return Collection<StructureBusiness>
	 */
	@SuppressWarnings("unchecked")
	public Collection<StructureBusiness> getAlls() {
		final EntityManager em = emProvider.get();

		try {
			final Query query = em.createQuery("select u from StructureBusiness u");

			return query.getResultList();
		} catch (Exception e) {
			throw new IllegalStateException("Error getAlls", e); //$NON-NLS-1$
		}
	}

	/**
	 * Delete all register 
	 * */
	public boolean deleteAllStructure()
	{
		final EntityManager em = emProvider.get();
		
		try {
			
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query1 = em.createQuery("update StructureBusiness u set parentStructure = null");
			query1.executeUpdate(); 
			
			final Query query = em.createQuery("delete from StructureBusiness u ");

			query.executeUpdate();
			tx.commit();
			return true;
		} 
		catch (Exception e) {
			throw new IllegalStateException("Error deleteEntityId", e); //$NON-NLS-1$
		}
	}
	
	/**
	 * delete entity
	 * 
	 * @param Long
	 *            id
	 * @return boolean
	 */
	public boolean deleteEntityId(Long id) {
		final EntityManager em = emProvider.get();

		try {

			final Query query2 = em.createQuery("select u from StructureBusiness u");
			@SuppressWarnings("unchecked")
			List<StructureBusiness> cl = query2.getResultList();
			StructureBusinessDAO stDao = new StructureBusinessDAO();
			for (StructureBusiness structureBusiness : cl) {
				if (structureBusiness.getParentStructure() != null && structureBusiness.getParentStructure().getId().equals(id)) {
					structureBusiness.setParentStructure(null);
					stDao.save(structureBusiness);
				}
			}
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query = em.createQuery("delete from StructureBusiness u where u.id =:id ");
			query.setParameter("id", id);

			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteEntityId", e); //$NON-NLS-1$
		}
	}

	/**
	 * Query entity
	 * 
	 * @param String
	 *            entityName
	 * @return StructureBusiness
	 */
	public StructureBusiness getByEntityName(String entityName) {
		final EntityManager em = emProvider.get();
		try {
			final TypedQuery<StructureBusiness> query = em.createNamedQuery(DBQueries.GET_STRUCTURE_BY_NAME, StructureBusiness.class);
			query.setParameter("structName", entityName); //$NON-NLS-1$
			StructureBusiness gen = query.getSingleResult();
			return gen;
		} catch (NoResultException x) {
			// logger.warn("Could not retrieve entity for {} from table {}.",
			// entityName, "SfEntity"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User.", entityName), ex); //$NON-NLS-1$
		}

		return null;
	}

}
