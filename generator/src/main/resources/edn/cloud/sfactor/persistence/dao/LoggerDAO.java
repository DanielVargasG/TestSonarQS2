package edn.cloud.sfactor.persistence.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.sfactor.persistence.entities.LoggerAction;

public class LoggerDAO extends BasicDAO<LoggerAction> {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public LoggerDAO() {
		super(edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider.getInstance());
	}
	
	/*
	@param FilterQueryDto dtoFilter
	*/
	public List<LoggerAction> getAllRevert(FilterQueryDto dtoFilter) 
	{
		final EntityManager em = emProvider.get();
		String sql = "select t from LoggerAction t ";
		String order = " order by t.id desc ";
		String where = " where t.message is not null ";
		int maxResult = 200;
		
		if(dtoFilter!=null){
			if(dtoFilter.getOrder()!=null && !dtoFilter.getOrder().equals("")){
				if(dtoFilter.getOrder().equals("ASC_DATE"))
					order = " order by t.addedOn ASC";
				else if(dtoFilter.getOrder().equals("DESC_DATE"))
					order = " order by t.addedOn DESC";
			}
			
			if(dtoFilter.getUser()!=null && !dtoFilter.getUser().equals("")){
				where += " and LOWER(t.userid) = '"+dtoFilter.getUser().toLowerCase()+"' ";
			}
			
			if(dtoFilter.getDate()!=null && !dtoFilter.getDate().equals("")){
				where += " and t.addedOn >= :dateIni and t.addedOn <= :dateFin ";
			}
			
			if(dtoFilter.getMaxResult()!=null && !dtoFilter.getMaxResult().equals("")){
				maxResult = Integer.parseInt(dtoFilter.getMaxResult());
			}
		}
		
		Query q = em.createQuery(sql);
		q = em.createQuery((sql+where+order), LoggerAction.class);
		
		if(dtoFilter!=null && dtoFilter.getDate()!=null && !dtoFilter.getDate().equals("")){
			q.setParameter("dateIni",UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(),dtoFilter.getDate()+"  00:00"));
			q.setParameter("dateFin",UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(),dtoFilter.getDate()+"  23:59"));
		}
		
		@SuppressWarnings("unchecked")
		final List<LoggerAction> result = q.setMaxResults(maxResult).getResultList();
		return result;
	}
	
	
	/**
	 * removes all records with creation date less than the filter date
	 * @param Date filterDate
	 * @return boolean
	 * */
	public boolean deleteLoggerByDate(Date filterDate) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete "
					+ "from LoggerAction "
					+ "where addedOn <= :filterDate ");
			
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
	
	/**
	 * removes all records
	 * @return boolean
	 * */
	public boolean deleteAllLogger() 
	{
		final EntityManager em = emProvider.get();

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query = em.createNativeQuery("delete from logger");
			
			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteAllLogger", e); //$NON-NLS-1$
		}	
	}	
}
