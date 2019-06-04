package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.RoleMappingGroupPpd;

public class RolesMappingGroupPpdDAO extends BasicDAO<RoleMappingGroupPpd> {


	public RolesMappingGroupPpdDAO() {
		super(EntityManagerProvider.getInstance());
	}

	@SuppressWarnings("unchecked")
	public Collection<RoleMappingGroupPpd> getAllGroupsByRoleId(Long roleid) {
		final EntityManager em = emProvider.get();

		try {
			String sql = "SELECT t FROM  " + getTableName() + " t WHERE t.roleMappingId = " + roleid;
			final Query query = em.createQuery(sql);
			return query.getResultList();
		} catch (Exception e) {
			throw new IllegalStateException("Error getAllGroupsByRoleId", e); //$NON-NLS-1$

		}
	}

	public boolean deleteAllUsersByFolderId(Long id) {
		final EntityManager em = emProvider.get();

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query = em.createQuery("delete from ROLE_MAPPING_GROUP u where u.getRoleMappingId =:id ");
			query.setParameter("id", id);

			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteAllGroupsByRoleId", e); //$NON-NLS-1$
		}
	}
}
