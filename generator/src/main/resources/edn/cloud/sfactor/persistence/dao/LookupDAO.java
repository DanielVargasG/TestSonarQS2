package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.LookupTable;

public class LookupDAO extends BasicDAO<LookupTable> {

	public LookupDAO() {
		super(EntityManagerProvider.getInstance());
	}

	/**
	 * Get Country for Code
	 * 
	 * @param String
	 *            Code
	 * @return Country
	 * 
	 */

	@SuppressWarnings("unchecked")
	public Collection<LookupTable> getLookupByCode(String code) {
		final EntityManager em = emProvider.get();

		try {
			final Query query = em.createQuery("Select u from LookupTable u where u.code =:code ");
			query.setParameter("code", code);

			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Collection<LookupTable> getLookupByTable(String table) {
		final EntityManager em = emProvider.get();

		try {
			final Query query = em.createQuery("Select u from LookupTable u where  u.codeTable=:table ");
			query.setParameter("table", table);

			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public LookupTable getLookupByInput(String code, String table) {
		final EntityManager em = emProvider.get();
		LookupTable gen = new LookupTable();
		gen.setCodeTable(table);
		gen.setValueIn(code);
		gen.setValueOut("");
		try {
			final Query query = em.createQuery("Select u from LookupTable u where u.valueIn =:code and u.codeTable=:table");
			query.setParameter("code", code);
			query.setParameter("table", table);
			Collection<LookupTable> clk = query.getResultList();

			if (clk.size() > 0) {
				gen = (LookupTable) query.getResultList().get(0);

			}
			return gen;
		} catch (Exception e) {
			return null;
		}
	}

}
