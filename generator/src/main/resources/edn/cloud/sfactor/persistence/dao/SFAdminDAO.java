package edn.cloud.sfactor.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edn.cloud.business.dto.integration.SFAdmin;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.DBQueries;

public class SFAdminDAO extends BasicDAO<SFAdmin> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public SFAdminDAO() {
		super(EntityManagerProvider.getInstance());
	}

	/**
	 * get mapping Field by Id
	 * 
	 * @param Long
	 *            id
	 * @return boolean
	 */
	public List<SFAdmin> getUserByRoleName(String id, Long appid) {
		final EntityManager em = emProvider.get();
		try {
			final TypedQuery<SFAdmin> query = em.createNamedQuery(DBQueries.GET_USERS_BY_ROLENAME, SFAdmin.class);
			query.setParameter("name", id); //$NON-NLS-1$
			query.setParameter("appid", appid);
			List<SFAdmin> gen = query.getResultList();
			return gen;
		} catch (NoResultException x) {
			logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", "User"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User."), ex); //$NON-NLS-1$
		}
	}

	/**
	 * delete mapping Field by Id
	 * 
	 * @param Long
	 *            id
	 * @return boolean
	 */
	public boolean deleteRoleById(String id) {
		final EntityManager em = emProvider.get();

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query = em.createQuery("delete from SFAdmin u where u.sfrolename =:id ");
			query.setParameter("id", id);

			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error SFAdmin", e); //$NON-NLS-1$
		}
	}

}
