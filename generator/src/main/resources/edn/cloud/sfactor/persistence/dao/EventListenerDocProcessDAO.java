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
import edn.cloud.sfactor.persistence.entities.EventListenerDocHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;

public class EventListenerDocProcessDAO extends BasicDAO<EventListenerDocProcess>   
{	
	private UtilLogger logger = UtilLogger.getInstance();
	
	public EventListenerDocProcessDAO(){
		super(EntityManagerProvider.getInstance());
	}		
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Get all Event Listener Control Document process by start date and status
	 * @param Long idEventCtrlProc optional
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, optional
	 * @param String typeModule
	 * @param String typeModuleNot
	 * @param Integer maxReg, maximum number of records to process, optional
	 * @return Collection<EventListenerDocProcess>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<EventListenerDocProcess> getAllEventListerDocProcess(
																			Long idEventCtrlProc,
																			Date startDate,String statusToLoad,
																			String typeModule,
																			String typeModuleNot,
																			Integer maxReg) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "SELECT u FROM EventListenerDocProcess u "
						+"WHERE u.eventListenerCtrlProc.startDatePpdOn IS NOT null AND u.fieldMapPpd.typeModule IS NOT NULL ";
			
			if(statusToLoad!=null && !statusToLoad.equals("")){ sql = sql + " AND u.status IN ("+statusToLoad+") ";	}			
			
			if(startDate!=null)
			{
				sql = sql + " AND ( "
						  + "        (u.eventListenerCtrlProc.startDatePpdOn <= :startDate "
						  + "        AND u.eventListenerCtrlProc.status IN ('"+UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode()+"')) ";
				sql = sql + "       OR "
						  + "        u.eventListenerCtrlProc.status IN ('"+UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode()+"')"
						  + "     ) ";				
			}
			
			if(typeModule!=null){
				sql += " AND u.fieldMapPpd.typeModule IN ('"+typeModule+"') ";
			}
			
			if(typeModuleNot!=null){
				sql += " AND u.fieldMapPpd.typeModule NOT IN ('"+typeModuleNot+"') ";
			}
			
			if(idEventCtrlProc!=null){sql = sql + " AND u.eventListenerCtrlProc.id = :idEvent ";}
			
			sql += " ORDER BY u.createOn ASC, u.status DESC ";
			
			final Query query = em.createQuery(sql);
			
			//parameters			
			if(startDate!=null){query.setParameter("startDate",startDate);}
			if(idEventCtrlProc!=null){query.setParameter("idEvent",idEventCtrlProc);}
			if(maxReg!=null && maxReg>0){query.setMaxResults(maxReg);}
						
			return query.getResultList(); 
		}
		catch (NoResultException e) {
			return null;
		}
		catch (Exception e) 
		{
			e.printStackTrace();			
			throw new IllegalStateException("Error getAllEventListerDocProcess", e); 
		}		
	}	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Get all Event Listener Control Document/Template process by start date and status
	 * @param Long idEventCtrlProc optional
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, optional
	 * @param String typeModule
	 * @param String typeModuleNot
	 * @param Integer maxReg, maximum number of records to process, optional
	 * @return Collection<EventListenerDocProcess>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<EventListenerDocProcess> getAllEventListerDocTemplateProcess(
																			Long idEventCtrlProc,
																			Date startDate,
																			String statusToLoad,																			
																			Integer maxReg) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "SELECT u FROM EventListenerDocProcess u "
						+"WHERE u.eventListenerCtrlProc.startDatePpdOn IS NOT null AND u.template IS NOT NULL ";
			
			if(statusToLoad!=null && !statusToLoad.equals("")){ sql = sql + " AND u.status IN ("+statusToLoad+") ";	}			
			
			if(startDate!=null)
			{
				sql = sql + " AND ( "
						  + "        (u.eventListenerCtrlProc.startDatePpdOn <= :startDate "
						  + "        AND u.eventListenerCtrlProc.status IN ('"+UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode()+"')) ";
				sql = sql + "       OR "
						  + "        u.eventListenerCtrlProc.status IN ('"+UtilCodesEnum.CODE_STATUS_EVENTLIS_TRANSFER_ATTACH.getCode()+"')"
						  + "     ) ";				
			}
			
			if(idEventCtrlProc!=null){sql = sql + " AND u.eventListenerCtrlProc.id = :idEvent ";}
			
			sql += " ORDER BY u.createOn ASC, u.status DESC ";
			
			final Query query = em.createQuery(sql);
			
			//parameters			
			if(startDate!=null){query.setParameter("startDate",startDate);}
			if(idEventCtrlProc!=null){query.setParameter("idEvent",idEventCtrlProc);}
			if(maxReg!=null && maxReg>0){query.setMaxResults(maxReg);}
						
			return query.getResultList(); 
		}
		catch (NoResultException e) {
			return null;
		}
		catch (Exception e) 
		{
			e.printStackTrace();			
			throw new IllegalStateException("Error getAllEventListerDocProcess", e); 
		}		
	}	
	

	
	/**
	 * 
	 * 
	 * 
	 * 
	 * Get all Attachments tp process by start date and status
	 * @param Date startDate, can by null
	 * @param String statusToLoad, list of status to load, optional
	 * @param Integer maxReg, maximum number of records to process, optional
	 * @param FilterQueryDto filters
	 * @return Collection<EventListenerDocProcess>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<EventListenerDocProcess> getAllAttachmentProcess(Date startDate,String statusToLoad,Integer maxReg, FilterQueryDto filters) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "SELECT u FROM EventListenerDocProcess u "
						+"WHERE u.startDatePpdOnAttach IS NOT null AND u.eventListenerCtrlProc IS NULL ";
			
			if(statusToLoad!=null && !statusToLoad.equals("")){ sql = sql + " AND u.status IN ("+statusToLoad+") ";	}			
			if(startDate!=null){sql = sql + " AND u.startDatePpdOnAttach <= :startDate ";}
					
			sql += " ORDER BY u.createOn ASC, u.status DESC ";
			
			final Query query = em.createQuery(sql);
			
			//parameters			
			if(startDate!=null){query.setParameter("startDate",startDate);}			
			if(maxReg!=null && maxReg>0){query.setMaxResults(maxReg);}
						
			return query.getResultList(); 
		}
		catch (NoResultException e) {
			return null;
		}
		catch (Exception e) 
		{
			logger.error(EventListenerDocProcessDAO.class,e.getMessage());
			throw new IllegalStateException("Error getAllEventListerDocProcess", e); 
		}		
	}	
	
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * search attachment pending to process
	 * @param String statusToLoad
	 * @param String attachmentIdSF
	 * @return Boolean
	 * */
	public Boolean isAttachmentPendingToProcess(String statusToLoad,String attachmentIdSF)
	{
		final EntityManager em = emProvider.get();
		
		try 
		{			
			String sql = "SELECT COUNT(u.id) FROM EventListenerDocProcess u "						
						+"WHERE u.attachmentIdSF = :attachmentIdSF  ";		
			
			if(statusToLoad!=null && !statusToLoad.equals("")){ sql = sql + " AND u.status IN ("+statusToLoad+") ";	}
					
			final Query query = em.createQuery(sql);
			query.setParameter("attachmentIdSF",attachmentIdSF);						
			if((long)query.getSingleResult()>0) {
				return true;				
			} 
		}
		catch (NoResultException e) {
			return false;
		}
		catch (Exception e) 
		{
			logger.error(EventListenerDocProcessDAO.class,e.getMessage());
		}
		
		return false;	
	}
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Updates id job process in eventlistener doc process
	 * @param Long idCtrlDoc
	 * @param Long idJobProcess
	 * */
	public void eventListenerDocProcessUpdateIdJob(Long idCtrlDoc,Long idJobProcess)
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "UPDATE EventListenerDocProcess u "
						+"SET u.idJobProcess = :idJobProcess "
						+"WHERE u.id = :idCtrlDoc  ";		
					
			final Query query = em.createQuery(sql);

			query.setParameter("idCtrlDoc",idCtrlDoc);
			query.setParameter("idJobProcess",idJobProcess);
						
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
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Updates observations of event attach
	 * @param EventListenerDocProcess entity
	 * */
	public void updateEventObservations(EventListenerDocProcess entity)
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "UPDATE EventListenerDocProcess u "
						+"SET status = :statusValue, "
						+ "u.observations = :obsValue, "
						+ "u.lastUpdateOn = :dateValue,"
						+ "u.idJobProcess = :idJobProcess "
						+"WHERE u.id = :idValue  ";		
					
			final Query query = em.createQuery(sql);

			query.setParameter("statusValue",entity.getStatus());
			query.setParameter("obsValue",entity.getObservations());
			query.setParameter("dateValue",entity.getLastUpdateOn());
			query.setParameter("idJobProcess",entity.getIdJobProcess());
			query.setParameter("idValue",entity.getId());
						
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
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Updates fielMapping to NULL in EventListenerDocProcess by idFieldMapping 
	 * @param Long idFieldMapping
	 * */
	public void updateFieldMappingToNull(Long idFieldMapping)
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "UPDATE EventListenerDocProcess u "
						+"SET "
						+"u.fieldMapPpd = NULL, "
						+"u.fieldMapPpdDest = NULL ";
			
			if(idFieldMapping!=null) {
				sql = 	" WHERE u.fieldMapPpdDest = :idValue OR u.fieldMapPpd = :idValue ";
			}
					
			final Query query = em.createQuery(sql);

			if(idFieldMapping!=null) {
				query.setParameter("idValue",idFieldMapping);
			}			
			
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
	 * Updates records that have exceeded the maximum wait time
	 * @param String statusToLoad
	 * @param String statusDestination
	 * @param Integer timeOutMinutes
	 * */
	public void updateEventToStatusTimeOut(String statusToLoad, String statusDestination,Integer timeOutMinutes)
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "UPDATE EventListenerDocProcess u SET status = :statusValue, idJobProcess = NULL "
						+"WHERE u.lastUpdateOn IS NOT NULL "
						+"AND u.lastUpdateOn < :timeOut "
						+"AND u.status IN ("+statusToLoad+") ";		
					
			final Query query = em.createQuery(sql);
			
			Date now = new Date();
			Date dateTimeOut = new Date(now.getTime() - (60000*timeOutMinutes));
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
		}		
	}
	
	
	/**
	 * delete All Associated by id EventListenerProcess
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteAllAssoWithIdEventListener(Long id)
	{
		final EntityManager em = emProvider.get();

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query = em.createNativeQuery(
					"DELETE FROM " 
					+"EVENTLISTENER_DOC_PROCS_N  "
					+"WHERE EVENT_LIS_CTRL_ID ="+id);
			
			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteAllAssoWithIdEventListener", e); //$NON-NLS-1$
		}
	}
	
	/**
	 * delete All Associated by id Id Massive load
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
					+"EVENTLISTENER_DOC_PROCS_N "					
					+"WHERE EVENT_LIS_CTRL_ID IN (SELECT b.ID "
					+"FROM EVENTLISTENER_CTRL_PROCS b "
					+"WHERE b.FK_MASSIVE_LOAD = ?) ");
			
			query.setParameter(1,idMassLoad);			
			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteAllWithIdMassiveLoad", e); //$NON-NLS-1$
		}
	}	
	
	/**
	 * delete All data in attachment without eventlistener
	 * @return boolean
	 * */
	public boolean deleteAllEmployeeSync()
	{
		final EntityManager em = emProvider.get();

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			final Query query = em.createNativeQuery(
					"DELETE FROM " 
					+"EVENTLISTENER_DOC_PROCS_N  "
					+"WHERE EVENT_LIS_CTRL_ID IS NULL ");
			
			query.executeUpdate();
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new IllegalStateException("Error deleteAllEmployeeSync", e); //$NON-NLS-1$
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
						+"from EVENTLISTENER_DOC_PROCS_N mu "						
						+"WHERE mu.STATUS IS NOT NULL ";
			
			if(filter.getStatus()!=null && !filter.getStatus().equals("")){
				sql = sql + " AND mu.STATUS IN ("+filter.getStatus()+")";
			}
			
			String initDate, endDate = "";
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
	 * Get all Event Listener document Control
	 * @param Long idEventCtrlHistoProc
	 * @param FilterQueryDto filter
	 * @return Collection<EventListenerDocProcess>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<EventListenerDocProcess> getAllEventListerDocProcess(Long idEventCtrl,FilterQueryDto filter) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "SELECT u FROM EventListenerDocProcess u "
					+ "WHERE u.status IS NOT NULL ";
			
			if(idEventCtrl!=null && idEventCtrl>0)
				sql += " AND u.eventListenerCtrlProc.id =:idEvent ";
			else if(idEventCtrl!=null && idEventCtrl<0)
				sql += " AND u.eventListenerCtrlProc IS NULL ";			
			
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
			
			if(idEventCtrl!=null  && idEventCtrl>0) {
				query.setParameter("idEvent",idEventCtrl);
			}
			
			return query.getResultList(); 
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error getAllEventListerDocProcess", e); 
		}		
	}	
	
	

}
