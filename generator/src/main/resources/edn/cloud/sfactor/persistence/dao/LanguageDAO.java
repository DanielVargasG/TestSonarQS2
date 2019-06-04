package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.Language;

public class LanguageDAO extends BasicDAO<Language>{

	public LanguageDAO() {
		super(EntityManagerProvider.getInstance());
	}
	
	
	public Collection<Language> getLanguageByCode(String code) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{			
			final Query query = em.createQuery("Select u from Language u where u.code =:code ");
			query.setParameter("code", code);
			
			return query.getResultList(); 
		}
		catch (Exception e) 
		{	
			return null;
		}		
	}
	
}
