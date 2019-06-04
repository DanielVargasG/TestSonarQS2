package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.Countries;

public class CountriesDAO extends BasicDAO<Countries>{

	public CountriesDAO() {
		super(EntityManagerProvider.getInstance());
	}
	
	/**
	 * Get Country for Code
	 * @param String Code
	 * @return Country
	 * 
	 * */
	
	public Collection<Countries> getCountryByCode(String code) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{			
			final Query query = em.createQuery("Select u from Countries u where u.code =:code ");
			query.setParameter("code", code);
			
			return query.getResultList(); 
		}
		catch (Exception e) 
		{	
			return null;
		}		
	}
	
	
}
