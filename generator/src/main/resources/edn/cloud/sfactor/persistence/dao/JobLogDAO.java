package edn.cloud.sfactor.persistence.dao;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import edn.cloud.sfactor.persistence.entities.JobsLog;

public class JobLogDAO extends BasicDAO<JobsLog> 
{
	public JobLogDAO() 
	{
		super(edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider.getInstance());
	}
	
	/**
	 * removes all records with the same id Job
	 * @param Date filterDate
	 * @return boolean
	 * */
	public boolean deleteJobLogByIdJob(Long idJob) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete "
					+ "from JobsLog l "
					+ "where l.idJob = :idJob ");
			
			query.setParameter("idJob",idJob);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			return false; 	
		}		
	}	
	
	/**
	 * removes all records with creation date less than the filter date
	 * @param Date filterDate
	 * @return boolean
	 * */
	public boolean deleteJobLogByDate(Date filterDate) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete "
					+ "from JobsLog l "
					+ "where l.addedOn <= :filterDate ");
			
			query.setParameter("filterDate", filterDate);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			return false; 	
		}		
	}
}
