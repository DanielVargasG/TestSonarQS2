package edn.cloud.sfactor.persistence.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlHistory;

public class EventListenerCtrlHistoDAO extends BasicDAO<EventListenerCtrlHistory>   
{
	public EventListenerCtrlHistoDAO(){
		super(EntityManagerProvider.getInstance());
	}
	
	/**
	 * return count of history by id ctrl
	 * @param Long id
	 * */
	public long getEventListenerCountHistoByidCrtl(Long id)
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery(""
					+ "SELECT COUNT(u) FROM EventListenerCtrlHistory u "					
					+ "WHERE u.idOriginalEvent = "+id);			
			
			long count = Long.parseLong(query.getSingleResult().toString());
			return count;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error getAllEventListerCtrl", e); 
		}	
	}
	
	
	/**
	 * delete all event listener ctrl 
	 * */
	public void deleteAllEventListerCtrlHisto()
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "DELETE FROM EventListenerCtrlHistory u WHERE u.refMassLoadId IS NULL ";
			final Query query = em.createQuery(sql);
						
			query.executeUpdate(); 
			tx.commit();
		}
		catch (NoResultException e) {
			
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteAllEventListerCtrl", e); 
		}		
	}	
	
	
	/**
	 * Paginator User Details
	 * @param filter
	 * @param Long fkMassiveLoad 
	 * @return List<String[]>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getStatusCount(FilterQueryDto filter,Long fkMassiveLoad)
	{		
		final EntityManager em = emProvider.get();
		
		try 
		{			
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			String sql = "Select COUNT(mu.ID),mu.STATUS,'histo' as source  "
						+"from EVENTLISTENER_CTRL_HISTO_N mu "						
						+"WHERE mu.STATUS IS NOT NULL ";
			
			if(filter.getStatus()!=null && !filter.getStatus().equals("")){
				sql = sql + " AND mu.STATUS IN ("+filter.getStatus()+")";
			}
			
			if(fkMassiveLoad!=null){
				sql = sql + " AND mu.REF_MASS_LOAD_ID = "+fkMassiveLoad;	
			}
			else{
				sql = sql + " AND mu.REF_MASS_LOAD_ID IS NULL ";
			}	
			
			String initDate, endDate = "";
			if(filter.getDate()!=null && filter.getDateFinish()!=null) {
				sql = sql + " AND (mu.CREATE_DATE >= ? AND mu.CREATE_DATE <= ?) ";
			}

			sql += " GROUP BY mu.STATUS ";
			final Query query = em.createNativeQuery(sql);
			
			if(filter.getDate()!=null && filter.getDateFinish()!=null) {
				query.setParameter(1,new java.sql.Date(UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), filter.getDate()).getTime()),TemporalType.TIMESTAMP);
				query.setParameter(2,new java.sql.Date(UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), filter.getDateFinish()).getTime()),TemporalType.TIMESTAMP);
			}
			
			List<Object[]> result = query.getResultList();			
						
			tx.commit();
			return result;
		}
		
		catch (Exception e) 
		{
			UtilLogger.getInstance().info(e.getMessage());
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	/**
	 * Get all Event Listener Control 
	 * @param FilterQueryDto filter
	 * @param Long refMassLoadId
	 * @return Collection<EventListenerCtrlHistory>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<EventListenerCtrlHistory> getAllEventListerCtrlHisto(FilterQueryDto filter,Long refMassLoadId) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			int end=0, start=0;
			String sql = "SELECT u FROM EventListenerCtrlHistory u ";
			
			if(refMassLoadId!=null)
				sql += " WHERE u.refMassLoadId = "+refMassLoadId+" ";
			else
				sql += " WHERE u.refMassLoadId IS NULL  ";
			
			if(filter.getStatus()!=null && !filter.getStatus().equals("")) {
				sql += " AND u.status IN ("+filter.getStatus()+") ";
			}
			
			if(filter.getUser()!=null && !filter.getUser().equals("")) {
				sql += " AND u.userIdPpd = :userValue ";
			}
			
			if(filter.getDate()!=null && filter.getDateFinish()!=null) {
				sql = sql + " AND (u.createOn >= :initDate  AND u.createOn <= :endDate) ";	
			}
			
			if(filter.getDate()!=null && !filter.getDate().equals("") && filter.getTypeDate()!=null && filter.getTypeDate().equals("start")) {
				sql = sql + " AND (u.startDatePpdOn = :startDate) ";	
			}
				
			sql += " ORDER BY u.createOn ASC, u.status DESC  ";
			
			final Query query = em.createQuery(sql);
			
			if(filter.getDate()!=null && filter.getDateFinish()!=null) {
				query.setParameter("initDate",new java.sql.Date(UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), filter.getDate()).getTime()),TemporalType.TIMESTAMP);
				query.setParameter("endDate",new java.sql.Date(UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), filter.getDateFinish()).getTime()),TemporalType.TIMESTAMP);
			}
			
			if(filter.getDate()!=null  && !filter.getDate().equals("") && filter.getTypeDate()!=null && filter.getTypeDate().equals("start")) {
				query.setParameter("startDate",new java.sql.Date(UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(), filter.getDate()).getTime()),TemporalType.DATE);				
			}
			
			if(filter.getUser()!=null && !filter.getUser().equals("")) {
				query.setParameter("userValue",filter.getUser());
			}
			
			if(filter != null ) 
			{
				if(filter.getPage()!=null && filter.getMaxResult()!=null && !filter.getMaxResult().equals("")) 
				{
					int number = Integer.parseInt(filter.getPage());
					int maxRow = Integer.parseInt(filter.getMaxResult());
					end = number * maxRow;
					start = end - maxRow ;
				}
			}
			
			List<EventListenerCtrlHistory> listReturn = query.getResultList();
			if(listReturn!=null && listReturn.size()>0 && listReturn.size()<= end && listReturn.size()>start) {
				end = listReturn.size();
				UtilLogger.getInstance().info("1start: "+start +"end: "+end);
				return listReturn.subList(start, end);
			}
			else if(listReturn!=null && listReturn.size()>0 && listReturn.size() >start) {
				UtilLogger.getInstance().info("2start: "+start +"end: "+end);
				return listReturn.subList(start, end);
			}
			
			
			return listReturn;
			
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error getAllEventListerCtrl", e); 
		}		
	}	
}
