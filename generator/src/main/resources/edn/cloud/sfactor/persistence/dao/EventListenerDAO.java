package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.EventListenerParam;

public class EventListenerDAO extends BasicDAO<EventListenerParam>
{
	public EventListenerDAO(){
		super(EntityManagerProvider.getInstance());
	}		
		
	
	/**
	 * Get Event Listener by name 
	 * @param String eventId
	 * @return EventListener
	 * */
	public EventListenerParam getByName(String eventId) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery("select u from EventListenerParam u where u.eventId = :eventId ");
			query.setParameter("eventId", eventId);
			
			EventListenerParam event = (EventListenerParam)query.getSingleResult();
			return event;
		}
		catch (NoResultException e) {
			return null;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error eventsGetByName", e); 
		}		
	}	
	
	/**
	 * Get all Event Listener 
	 * @return Collection<EventListener>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<EventListenerParam> getAllEventLister() 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			final Query query = em.createQuery("select u from EventListenerParam u  ");
			
			return query.getResultList(); 
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error getAllEventLister", e); 
		}		
	}
	
	/**
	 * delete event listener by id
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteEventListenerById(Long id) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from EventListenerParam u where u.id =:id ");
			query.setParameter("id", id);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteEventListenerById", e); 	
		}		
	}	
}
