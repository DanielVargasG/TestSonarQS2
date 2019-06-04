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
import edn.cloud.sfactor.persistence.entities.EventListenerDocHistory;

public class EventListenerDocHistoDAO extends BasicDAO<EventListenerDocHistory>   
{
	public EventListenerDocHistoDAO(){
		super(EntityManagerProvider.getInstance());
	}		
	
	/**
	 * Get all Event Listener document Control
	 * @param Long idEventCtrlHistoProc
	 * @param FilterQueryDto filter
	 * @return Collection<EventListenerDocHistory>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<EventListenerDocHistory> getAllEventListerDocHisto(Long idEventCtrlHistoProc,FilterQueryDto filter) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "SELECT u FROM EventListenerDocHistory u "
					+ "WHERE u.status IS NOT NULL ";
			
			if(idEventCtrlHistoProc!=null && idEventCtrlHistoProc>0)
				sql += " AND u.eventListenerCtrlHisto.id =:idEvent ";
			else if(idEventCtrlHistoProc!=null && idEventCtrlHistoProc<0)
				sql += " AND u.eventListenerCtrlHisto IS NULL ";
			
			if(filter!=null && filter.getStatus()!=null && !filter.getStatus().equals("")){
				sql = sql + " AND u.status IN ("+filter.getStatus()+") ";
			}
			
			String initDate, endDate = "";
			if(filter!=null && filter.getDate()!=null && filter.getDateFinish()!=null) {
				sql = sql + " AND (u.createOn >= :initDate AND u.createOn <= :endDate) ";
			}
			
			sql += " ORDER BY u.createOn DESC, u.status DESC  ";
			
			final Query query = em.createQuery(sql);
			
			if(filter!=null && filter.getDate()!=null && filter.getDateFinish()!=null) {
				query.setParameter("initDate",new java.sql.Date(UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), filter.getDate()).getTime()),TemporalType.TIMESTAMP);
				query.setParameter("endDate",new java.sql.Date(UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), filter.getDateFinish()).getTime()),TemporalType.TIMESTAMP);
			}
			
			if(idEventCtrlHistoProc!=null && idEventCtrlHistoProc>0) {
				query.setParameter("idEvent",idEventCtrlHistoProc);
			}
			
			return query.getResultList(); 
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error getAllEventListerDoc", e); 
		}		
	}	
	
	
	/**
	 * Get all document attachment
	 * @return Collection<EventListenerDocHistory>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<EventListenerDocHistory> getAllAttachmentHisto() 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery(""
					+ "SELECT u FROM EventListenerDocHistory u "
					+ "WHERE u.eventListenerCtrlHisto IS NULL "					
					+ "ORDER BY u.createOn DESC, u.status DESC  ");		
			
			return query.getResultList(); 
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error getAllAttachmentHisto", e); 
		}		
	}	
	
	/**
	 * delete all attachments  
	 * */
	public void deleteAllEventListeAttachments()
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "DELETE FROM EventListenerDocHistory u WHERE u.eventListenerCtrlHisto IS NOT NULL ";
			final Query query = em.createQuery(sql);
						
			query.executeUpdate(); 
			tx.commit();
		}
		catch (NoResultException e) {
			
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteAllAttachments", e); 
		}		
	}	
	
	/**
	 * delete all attachments 
	 * */
	public void deleteAllAttachments()
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "DELETE FROM EventListenerDocHistory u WHERE u.eventListenerCtrlHisto IS NULL ";
			final Query query = em.createQuery(sql);
						
			query.executeUpdate(); 
			tx.commit();
		}
		catch (NoResultException e) {
			
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteAllAttachments", e); 
		}		
	}
	
	/**
	 * get status count 
	 * @param filter 
	 * @return List<String[]>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getStatusCount(FilterQueryDto filter)
	{		
		final EntityManager em = emProvider.get();
		
		try 
		{			
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			String sql = "Select COUNT(mu.ID),mu.STATUS,'attach' as source "
						+"from EVENTLISTENER_DOC_HISTO_N mu "						
						+"WHERE mu.STATUS IS NOT NULL ";
			
			if(filter.getStatus()!=null && !filter.getStatus().equals("")){
				sql = sql + " AND mu.STATUS IN ("+filter.getStatus()+")";
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
}
