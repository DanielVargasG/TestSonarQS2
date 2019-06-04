package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.DBQueries;
import edn.cloud.sfactor.persistence.entities.Favorite;

public class FavoriteDAO extends BasicDAO<Favorite> {
	public FavoriteDAO() {
		super(EntityManagerProvider.getInstance());
	}

	@SuppressWarnings("unchecked")
	public Collection<Favorite> findAllFav(String user) {
		final EntityManager em = emProvider.get();
		try {
			final Query query = em.createQuery("select u from Favorite u where u.userId = '" + user + "'");
			return query.getResultList();
		} catch (NoResultException x) {
			// logger.warn("Could not retrieve entity for userId {} from table
			// {}. Maybe the user doesn't exist yet.", userId, "User");
			// //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User."), ex); //$NON-NLS-1$
		}
		return null;

	}
	@SuppressWarnings("unchecked")
	public Collection<Favorite> findAllByTemp(String id) {
		final EntityManager em = emProvider.get();
		try {
			final Query query = em.createQuery("select u from Favorite u where u.templateId = '" + id + "'");
			return query.getResultList();
		} catch (NoResultException x) {
			// logger.warn("Could not retrieve entity for userId {} from table
			// {}. Maybe the user doesn't exist yet.", userId, "User");
			// //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User."), ex); //$NON-NLS-1$
		}
		return null;

	}
	
	public Favorite findByID(String id) {
		final EntityManager em = emProvider.get();
		try {
			final TypedQuery<Favorite> query = em.createNamedQuery(DBQueries.GET_FAV_BY_KEYID, Favorite.class);
			query.setParameter("keyId", id); //$NON-NLS-1$
			Favorite fav = query.getSingleResult();
			return fav;
		} catch (NoResultException x) {
			// logger.warn("Could not retrieve user with emial {} from table {}.
			// Maybe the user doesn't exist yet.", email, "User"); //$NON-NLS-1$
			// //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one fav with id %s from table Favorite.", id), ex); //$NON-NLS-1$
		}

		return null;
	}
}