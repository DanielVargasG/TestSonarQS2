package edn.cloud.sfactor.persistence.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.IDBEntity;

public class BasicDAO<T extends IDBEntity> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected EntityManagerProvider emProvider;

	public BasicDAO(EntityManagerProvider emProvider) {
		this.emProvider = emProvider;
	}

	public List<T> getAll() {
		return this.getAll(null, null);
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll(String status, Integer limit) {
		final List<T> result = new ArrayList<>();
		final EntityManager em = emProvider.get();

		Query q = em.createQuery("select t from " + getTableName() + " t " + (status != null ? " where (t.status ='" + status + "' or t.status is null)" : ""), //$NON-NLS-3$ //$NON-NLS-4$
				this.getClass().getGenericSuperclass().getClass());

		if (limit != null)
			q.setMaxResults(limit);

		result.addAll((Collection<? extends T>) q.getResultList());
		return result;
	}

	public T save(T entity) {
		final EntityManager em = emProvider.get();
		em.getTransaction().begin();

		final T merge = em.merge(entity);
		try {
			em.getTransaction().commit();
		} catch (NullPointerException e) {
			// TODO: handle exception
		} catch (RollbackException d) {
			// TODO: handle exception
		}
		return merge;
	}

	public T saveNew(T entity) {
		final EntityManager em = emProvider.get();

		try {
			em.getTransaction().begin();
			em.persist(entity);
			em.getTransaction().commit();
		} catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (RollbackException d) {
			// TODO: handle exception
			d.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return entity;
	}

	public void delete(T entity) {
		final EntityManager em = emProvider.get();
		em.getTransaction().begin();
		em.remove(entity);
		em.getTransaction().commit();
	}

	public void deleteAll() {
		final List<T> all = getAll();
		final EntityManager em = emProvider.get();
		em.getTransaction().begin();

		for (T t : all) {
			final T managedObject = getById(t.getId(), em, null);
			em.remove(managedObject);
		}

		em.getTransaction().commit();
	}

	public int deleteAllWithLimit(Integer limit) {
		final List<T> all = getAll(null, limit);
		final EntityManager em = emProvider.get();
		em.getTransaction().begin();

		for (T t : all) {
			final T managedObject = getById(t.getId(), em, null);
			em.remove(managedObject);
		}

		em.getTransaction().commit();
		
		
		Query query= em.createQuery("select count(u) from " + getTableName() + " u");

		return ((Number) query.getSingleResult()).intValue();
	}
	
	public int getCount() {
		final EntityManager em = emProvider.get();
		
		Query query= em.createQuery("select count(u) from " + getTableName() + " u");

		return ((Number) query.getSingleResult()).intValue();
	}

	public T getById(long id) {
		final EntityManager em = emProvider.get();
		return getById(id, em, null);
	}

	public T getById(long id, String status) {
		final EntityManager em = emProvider.get();
		return getById(id, em, status);
	}

	@SuppressWarnings("unchecked")
	private T getById(long id, EntityManager em, String status) {
		T t = null;

		try {
			String sql = "select u from " + getTableName() + " u where u.id = :id " + (status != null ? " and (u.status ='" + status + "' or u.status is null)" : "");
			Query query = em.createQuery(sql); // $NON-NLS-1$ //$NON-NLS-2$
			query.setParameter("id", id); //$NON-NLS-1$
			t = (T) query.getSingleResult();
		} catch (NoResultException e) {
			logger.error("Could not retrieve entity {} from table {}.", id, getTableName()); //$NON-NLS-1$
		} catch (NonUniqueResultException e) {
			throw new IllegalStateException(String.format("More than one entity %s from table %s.", id, getTableName())); //$NON-NLS-1$
		}

		return t;
	}

	private Type getActualType() {
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) genericSuperclass;
		Type type = pt.getActualTypeArguments()[0];

		return type;
	}

	public String getTableName() {
		String actualType = getActualType().toString();
		return actualType.substring(actualType.lastIndexOf('.') + 1);
	}

}
