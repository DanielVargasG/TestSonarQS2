package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.MassiveLoadUser;

public class MassiveLoadUserDAO extends BasicDAO<MassiveLoadUser> {
	
	public MassiveLoadUserDAO() {
		super(EntityManagerProvider.getInstance());
	}
	
	public MassiveLoadUser getByUser(String user) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{			
			final Query query = em.createQuery("Select * from MASSIVE_LOAD_USER u where u.CREATE_USER =:user ");
			query.setParameter("user", user);
			
			return (MassiveLoadUser) query.getSingleResult(); 
		}
		catch (Exception e) 
		{	
			return null;
		}		
	}
	
	/**
	 * Get time current future
	 * @param startDate
	 * @param isFutureDates
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<MassiveLoadUser> getAllUserTime(Date startDate, Boolean isFutureDates){
		final EntityManager em = emProvider.get();
		
		try 
		{			
			String sql = "Select m from MassiveLoadUser m Where m.createOn IS NOT Null";

			if(startDate!=null && isFutureDates!=null && !isFutureDates)
			{
				sql = sql + " AND (m.createOn <= :startDate ) ";
			}
			
			if(startDate!=null && isFutureDates!=null && isFutureDates)
			{
				sql = sql + " AND m.createOn > :startDate ";
			}
			
			sql+=" order by  m.createOn ASC ";
			
			final Query query = em.createQuery(sql);
			query.setParameter("startDate",startDate);
			return query.getResultList();
		}
		catch (Exception e) 
		{	
			UtilLogger.getInstance().error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Get number of eventlistener by massiveload
	 * @param Integer maxRegisterByPage
	 * @param String idMassiveLoad
	 * @return
	 */
	public Long eventListCtrlCountByIdMassLoad(Long idMassiveLoad) 
	{		
		final EntityManager em = emProvider.get();
		try 
		{
			
			String sql = "Select Count(mu) "
					+"from EventListenerCtrlProcess mu "
					+"JOIN mu.fkMassiveLoad fk "
					+"Where fk.id=:idValue ";
			
			final Query query = em.createQuery(sql);
			query.setParameter("idValue",idMassiveLoad);
			Long size = (Long) query.getSingleResult();
			return size;
			
		}
		catch (Exception e) 
		{
			UtilLogger.getInstance().info(e.getMessage());
			e.printStackTrace();
			return null;
		}		
	}
	
}


