package edn.cloud.sfactor.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.DBQueries;
import edn.cloud.sfactor.persistence.entities.SfEntity;

public class SfEntityDAO extends BasicDAO<SfEntity> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public SfEntityDAO() {
		super(EntityManagerProvider.getInstance());
	}

	public void reset() {
		final EntityManager em = emProvider.get();
		try {

			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query2 = em.createQuery("DELETE FROM SfNavProperty");
			query2.executeUpdate();

			final Query query3 = em.createQuery("DELETE FROM SfProperty");
			query3.executeUpdate();

			final Query query = em.createQuery("DELETE FROM SfEntity");
			query.executeUpdate();
			tx.commit();

			logger.warn("Reseted");
		} catch (NoResultException x) {
			logger.warn("Could not delete the content"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public SfEntity getByEntityName(String entityName) {
		final EntityManager em = emProvider.get();
		try {
			final TypedQuery<SfEntity> query = em.createNamedQuery(DBQueries.GET_ENTITY_BY_NAME, SfEntity.class);
			query.setParameter("name", entityName); //$NON-NLS-1$
			SfEntity gen = query.getSingleResult();
			return gen;
		} catch (NoResultException x) {
			logger.warn("Could not retrieve entity for {} from table {}.", entityName, "SfEntity"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User.", entityName), ex); //$NON-NLS-1$
		}

		return null;
	}

}