package edn.cloud.sfactor.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.DBQueries;
import edn.cloud.sfactor.persistence.entities.Employee;

public class EmployeeDAO extends BasicDAO<Employee> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public EmployeeDAO() {
		super(EntityManagerProvider.getInstance());
	}

	public Employee getByUserId(String userId) {
		final EntityManager em = emProvider.get();
		try {
			final TypedQuery<Employee> query = em.createNamedQuery(DBQueries.GET_USER_BY_USER_ID, Employee.class);
			query.setParameter("userId", userId); //$NON-NLS-1$
			Employee user = query.getSingleResult();
			return user;
		} catch (NoResultException x) {
			logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User.", userId), ex); //$NON-NLS-1$
		}

		return null;
	}

	public Employee findByEmail(String email) {
		final EntityManager em = emProvider.get();
		try {
			final TypedQuery<Employee> query = em.createNamedQuery(DBQueries.GET_USER_BY_EMAIL, Employee.class);
			query.setParameter("email", email); //$NON-NLS-1$
			Employee user = query.getSingleResult();
			return user;
		} catch (NoResultException x) {
			logger.warn("Could not retrieve user with emial {} from table {}. Maybe the user doesn't exist yet.", email, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one users with email %s from table User.", email), ex); //$NON-NLS-1$
		}

		return null;
	}
}
