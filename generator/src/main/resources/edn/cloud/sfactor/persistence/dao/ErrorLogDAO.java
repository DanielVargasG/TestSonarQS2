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

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.DBQueries;
import edn.cloud.sfactor.persistence.entities.ErrorLog;

public class ErrorLogDAO extends BasicDAO<ErrorLog> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public ErrorLogDAO() {
		super(EntityManagerProvider.getInstance());
	}
	
	public List<ErrorLog> getByDocId(Long genId) {
		final EntityManager em = emProvider.get();
		try {
			final TypedQuery<ErrorLog> query = em.createNamedQuery(DBQueries.GET_GEN_BY_KEYID, ErrorLog.class);
			query.setParameter("genId", genId); //$NON-NLS-1$
			List<ErrorLog> gen = query.getResultList();
			logger.info(gen.toString());
			return gen;
		} catch (NoResultException x) {
			logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", genId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User.", genId), ex); //$NON-NLS-1$
		}

		return null;
	}
	
	
	/**
	 * delete all error by generated/document Id
	 * @param Long idDoc
	 * @return Boolean
	 * */
	public Boolean deleteAllByDocId(Long idDoc)
	{
		final EntityManager em = emProvider.get();
		
		try{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createNativeQuery("delete from ERRORLOGS where ERR_ID IN (select ID from GENERATED where DOCUMENT_ID ="+idDoc+" ) ");
			query.executeUpdate();
			
			tx.commit();
			return true;
		}
		catch (Exception e) {
			throw new IllegalStateException("Error deleteAllByDocId", e); //$NON-NLS-1$	
		}
	}
}
