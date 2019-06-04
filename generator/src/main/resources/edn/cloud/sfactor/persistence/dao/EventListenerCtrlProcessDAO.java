package edn.cloud.sfactor.persistence.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;

public class EventListenerCtrlProcessDAO extends BasicDAO<EventListenerCtrlProcess>   
{	
	private UtilLogger logger = UtilLogger.getInstance();
	
	public EventListenerCtrlProcessDAO(){
		super(EntityManagerProvider.getInstance());
	}		
	
	/**
	 * Get all Event Listener Control process by start date and status
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, can by null
	 * @param Integer maxReg maximum number of records to process, optional 
	 * @param Boolean isFutureDates
	 * @param Long fkMassiveLoad
	 * @param String statusMassiveToLoad
	 * @return Collection<EventListenerCtrlProcess>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<EventListenerCtrlProcess> getAllEventListerCtrlProcess(
												Date startDate,
												String statusToLoad,
												Integer maxReg,
												Boolean isFutureDates,
												FilterQueryDto filter,
												Long fkMassiveLoad,
												String statusMassiveToLoad)
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "SELECT u FROM EventListenerCtrlProcess u "
					+ "JOIN u.eventListenerParam ep "+
					((fkMassiveLoad!=null||statusMassiveToLoad!=null)?"JOIN u.fkMassiveLoad ml ":" ")
					+ "WHERE u.startDatePpdOn IS NOT null ";
			
			
			if(fkMassiveLoad!=null){
				sql = sql + " AND ml.id = "+fkMassiveLoad;	
			}
			else if(fkMassiveLoad==null&&statusMassiveToLoad==null)
			{
				sql = sql + " AND u.fkMassiveLoad IS NULL ";
			}
			
			if(statusMassiveToLoad!=null && !statusMassiveToLoad.equals("")){ sql = sql + " AND ml.status IN ("+statusMassiveToLoad+") ";	}
			if(statusToLoad!=null && !statusToLoad.equals("")){ sql = sql + " AND u.status IN ("+statusToLoad+") ";	}	
			
			//present
			if(startDate!=null && isFutureDates!=null && !isFutureDates)
			{
				sql = sql + " AND (u.startDatePpdOn <= :startDate "			
					+ " OR (ep.isDateinstant = TRUE AND (u.isProcessAgain = FALSE OR u.isProcessAgain IS NULL) ) OR "
					+ " u.status IN ('"+UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTEBYUSER.getCode()+"') ) ";
			}
			
			if(startDate!=null && isFutureDates!=null && isFutureDates)
			{
				sql = sql + " AND u.startDatePpdOn > :startDate "
						  + " AND (ep.isDateinstant = FALSE OR ep.isDateinstant IS NULL OR u.isProcessAgain = TRUE)";
			}
			
			sql += " ORDER BY u.startDatePpdOn ASC ";
			
			final Query query = em.createQuery(sql);
			
			//parameters			
			if(startDate!=null && isFutureDates!=null){query.setParameter("startDate",startDate);}
			if(maxReg!=null && maxReg>0){query.setMaxResults(maxReg);}
						
			return query.getResultList(); 
		}
		catch (NoResultException e) {
			return null;
		}
		catch (Exception e) 
		{
			logger.error(EventListenerDocProcessDAO.class,e.getMessage());
			throw new IllegalStateException("Error getAllEventListerCtrlProcess", e); 
		}		
	}
	
	/**
	 * update id job process
	 * @param Long idEventCtrl
	 * @param Long idJobProcess
	 * 
	 * */
	public void updateEventCtrlIdJobProcess(Long idEventCtrl,Long idJobProcess)
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "UPDATE EventListenerCtrlProcess u SET u.idJobProcess = :idJobProcess "
						+"WHERE u.id = :idEventCtrl ";		
					
			final Query query = em.createQuery(sql);
			
			query.setParameter("idJobProcess",idJobProcess);
			query.setParameter("idEventCtrl",idEventCtrl);
						
			query.executeUpdate(); 
			tx.commit();
		}
		catch (NoResultException e) {
			
		}
		catch (Exception e) 
		{
			logger.error(EventListenerDocProcessDAO.class,e.getMessage());
		}		
	}
	
	/**
	 * update status event ctrl
	 * @param Long idEventCtrl
	 * @param Long idMassLoad
	 * @param String status 
	 * */
	public void updateEventCtrlStatus(Long idEventCtrl,Long idMassLoad,String status)
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "UPDATE EventListenerCtrlProcess u SET u.status = :status "
						+"WHERE ";		
					
			if(idEventCtrl!=null) {
				sql += " u.id = "+idEventCtrl;				
			}
			
			if(idMassLoad!=null) {
				sql += " u.fkMassiveLoad.id = "+idMassLoad;				
			}			
			
			final Query query = em.createQuery(sql);
			query.setParameter("status",status);
						
			query.executeUpdate(); 
			tx.commit();
		}
		catch (NoResultException e) {
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			logger.error(EventListenerDocProcessDAO.class,e.getMessage());
		}		
	}	
	
	/**
	 * Updates records that have exceeded the maximum wait time 
	 * @param String statusToLoad
	 * @param String statusDestination
	 * @param Integer timeOutMinutes
	 * */
	public void updateEventCtrlToStatusTimeOut(String statusToLoad, String statusDestination,Integer timeOutMinutes)
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "UPDATE EventListenerCtrlProcess u SET status = :statusValue, idJobProcess = NULL "
						+"WHERE u.lastUpdateOn IS NOT NULL "
						+"AND u.lastUpdateOn < :timeOut "
						+"AND u.status IN ("+statusToLoad+") ";		
					
			final Query query = em.createQuery(sql);
			
			Date now = new Date();
			Date dateTimeOut = new Date(now.getTime() - (60000*timeOutMinutes));
			logger.info("valor time out "+dateTimeOut.toString());
			query.setParameter("timeOut",dateTimeOut);
			query.setParameter("statusValue",statusDestination);
						
			query.executeUpdate(); 
			tx.commit();
		}
		catch (NoResultException e) {
			
		}
		catch (Exception e) 
		{
			logger.error(EventListenerDocProcessDAO.class,e.getMessage());
			//throw new IllegalStateException("Error updateEventToStatusTimeOut", e); 
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

			String sql = "Select COUNT(mu.ID),mu.STATUS,'event' as source "
						+"from EVENTLISTENER_CTRL_PROCS mu "						
						+"WHERE mu.STATUS IS NOT NULL ";
			
			if(filter.getStatus()!=null && !filter.getStatus().equals("")){
				sql = sql + " AND mu.STATUS IN ("+filter.getStatus()+")";
			}
			
			if(fkMassiveLoad!=null){
				sql = sql + " AND mu.FK_MASSIVE_LOAD = "+fkMassiveLoad;	
			}
			else{
				sql = sql + " AND mu.FK_MASSIVE_LOAD IS NULL ";
			}	
			
			if(filter.getDate()!=null && filter.getDateFinish()!=null) {
				sql = sql + " AND (mu.LASTUPDATE_DATE >= ? AND mu.LASTUPDATE_DATE <= ?) ";
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
	 * Paginator User Details
	 * @param filter
	 * @param Long fkMassiveLoad 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<EventListenerCtrlProcess> getEventListenerCtrlPage(FilterQueryDto filter,Long fkMassiveLoad)
	{		
		final EntityManager em = emProvider.get();
		
		try 
		{
			int end=0, start=0;
			String sql = "Select mu "
						+"from EventListenerCtrlProcess mu "
						+(fkMassiveLoad!=null?" JOIN mu.fkMassiveLoad ml ":" ")
						+"WHERE mu.status IS NOT NULL ";
			
			if(filter.getStatus()!=null && !filter.getStatus().equals("")){
				sql = sql + " AND mu.status IN ("+filter.getStatus()+")";
			}
			
			if(fkMassiveLoad!=null){
				sql = sql + " AND ml.id = "+fkMassiveLoad;	
			}
			else{
				sql = sql + " AND mu.fkMassiveLoad IS NULL ";
			}
			
			if(filter.getDate()!=null && filter.getDateFinish()!=null) {
				sql = sql + " AND (mu.lastUpdateOn >= :initDate  AND mu.lastUpdateOn <= :endDate) ";	
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
			
			sql += " ORDER BY mu.lastUpdateOn ASC ";
			final Query query = em.createQuery(sql);	
			
			if(filter.getDate()!=null && filter.getDateFinish()!=null) {
				query.setParameter("initDate",new java.sql.Date(UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), filter.getDate()).getTime()),TemporalType.TIMESTAMP);
				query.setParameter("endDate",new java.sql.Date(UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), filter.getDateFinish()).getTime()),TemporalType.TIMESTAMP);
			}
						
			List<EventListenerCtrlProcess> listReturn = query.getResultList();			
			if(listReturn!=null && listReturn.size()>0 && listReturn.size()<= end && listReturn.size()>start) {
				end = listReturn.size();
				UtilLogger.getInstance().info("1start: "+start +"end: "+end);
				return listReturn.subList(start, end);
			}
			else if(listReturn!=null && listReturn.size()>0 && listReturn.size() >start) {
				UtilLogger.getInstance().info("2start: "+start +"end: "+end);
				return listReturn.subList(start, end);
			}
			
			return null;
		}
		
		catch (Exception e) 
		{
			UtilLogger.getInstance().info(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get number pages
	 * @param Integer maxRegisterByPage
	 * @param String idMassiveLoad
	 * @return
	 */
	public double eventListenerCtrlProcNumberPages(Integer maxRegisterByPage, String idMassiveLoad) 
	{		
		final EntityManager em = emProvider.get();
		try 
		{
			
			String sql = "Select Count(mu) "
					+"from EventListenerCtrlProcess mu "
					+"JOIN mu.fkMassiveLoad fk "
					+"Where fk.id=:idValue ";
			
			final Query query = em.createQuery(sql);
			query.setParameter("idValue", Long.parseLong(idMassiveLoad));
			int size = (int) query.getSingleResult();
			double pages = Math.ceil(size/7);//Integer.parseInt(parameter.getValue())
			return pages;
			
		}
		catch (Exception e) 
		{
			UtilLogger.getInstance().info(e.getMessage());
			e.printStackTrace();
			return 0;
		}		
	}	
	
	/**
	 * delete All Associated by id Massive Load
	 * @param Long idMassLoad
	 * @return boolean
	 * */
	public boolean deleteAllWithIdMassiveLoad(Long idMassLoad)
	{
		final EntityManager em = emProvider.get();

		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query = em.createNativeQuery(
					"DELETE FROM " 
					+"EVENTLISTENER_CTRL_PROCS a "					
					+"WHERE a.FK_MASSIVE_LOAD = "+idMassLoad);
			
			
			//query.setParameter(1,idMassLoad);
			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteAllWithIdMassiveLoad", e); //$NON-NLS-1$
		}
	}	
	
}