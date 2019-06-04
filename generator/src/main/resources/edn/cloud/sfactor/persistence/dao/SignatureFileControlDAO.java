package edn.cloud.sfactor.persistence.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.SignatureFileControl;

public class SignatureFileControlDAO extends BasicDAO<SignatureFileControl> 
{

	public SignatureFileControlDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	/**
	 * update status signature file control  
	 * @param Long idSignatureCtrlFile
	 * @param String status
	 * @param String observations
	 * @return Boolean
	 * */
	public boolean updateStatus(Long idSignatureCtrlFile, String status, String observations)
	{
		final EntityManager em = emProvider.get();
		
		try {
			
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query1 = em.createQuery("update SignatureFileControl u "
					+ "set u.status = :status, u.lastUpdateOn = :dateUpdate "
					+ (observations!=null?",u.observations = :obs  ":" ")
					+ "where u.id = :id");
			
			query1.setParameter("status",status);
			query1.setParameter("dateUpdate",new Date());
			
			if(observations!=null) {
				query1.setParameter("obs",observations);
			}
			
			query1.setParameter("id",idSignatureCtrlFile);
			query1.executeUpdate(); 
			
			tx.commit();
			return true;
		} 
		catch (Exception e) {
			throw new IllegalStateException("Error updateStatus", e); //$NON-NLS-1$
		}
	}	
	
	/**
	 * find all signs file control by Document 
	 * @param Long idDoc **optional
	 * @param String status **optional
	 * @param Date filterDateJob
	 * @param Integer maxReg
	 * @return ArrayList<SignatureFileControl>
	 * */
	public ArrayList<SignatureFileControl> getAllSignFileCtrlByDoc(Long idDoc,String status, Date filterDateJob, Integer maxReg) 
	{
		final EntityManager em = emProvider.get();
		try 
		{
			String sql = "SELECT s FROM SignatureFileControl s "
						  +"inner join s.generated g  "
						  +"inner join g.document d "
						  +"WHERE s.status IS NOT NULL ";
			
			if(status!=null && !status.equals(""))
				sql+= " and s.status IN("+status+") ";
			
			if(idDoc!=null && idDoc>0)
				sql+= " and d.id =:idDoc ";
			
			if(filterDateJob!=null)
				sql+= " and (s.lastUpdateOn IS NULL OR s.lastUpdateOn <=:filterDate) ";
			
			final Query query = em.createQuery(sql);
			
			if(idDoc!=null && idDoc>0)
				query.setParameter("idDoc",idDoc);
			
			if(filterDateJob!=null)
				query.setParameter("filterDate",filterDateJob);
			
			if(maxReg!=null && maxReg>0){query.setMaxResults(maxReg);}
			
			Collection<SignatureFileControl> result = query.getResultList();			
			ArrayList<SignatureFileControl> retorno = new ArrayList<>(result);
			
			return  retorno;
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return null;
	}
	
	/**
	 * get count signature by status 
	 * @param String status
	 * @param String userId
	 * @return Integer
	 * */
	public Integer getCountSignFileCtrlByStatus(String userId, String status) 
	{
		final EntityManager em = emProvider.get();
		try 
		{					
			String sql ="SELECT COUNT(sc.ID) "
						+"FROM SIGNATURE_FILE_CTRL sc "
						+"INNER JOIN SIGNATURE_FILE_CTRL_DET det ON (det.SIGN_FILE_CTRL_ID = sc.ID) "
						+"WHERE sc.STATUS IN ("+status+") AND det.NAME_DESTINATION = '"+userId+"' ";
			
			final Query query = em.createNativeQuery(sql);
			
			Object obj  = query.getSingleResult();
			return Integer.parseInt(obj.toString());
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return 0;
	}
	
	/**
	 * get all signs file control by id Ppd control 
	 * @param String idPpdSignCtrl
	 * @return ArrayList<SignatureFileControl>
	 * */
	public ArrayList<SignatureFileControl> getAllSignFileCtrlByIdPpdCtrl(String idPpdSignCtrl) 
	{
		final EntityManager em = emProvider.get();
		try 
		{
			String sql = "SELECT s FROM SignatureFileControl s "						
						  +"WHERE s.idPpdSignCtrl =:idPpdSignCtrl ";
			
			final Query query = em.createQuery(sql);
			query.setParameter("idPpdSignCtrl",idPpdSignCtrl.toString());
			
			Collection<SignatureFileControl> result = query.getResultList();			
			ArrayList<SignatureFileControl> retorno = new ArrayList<>(result);
			
			return  retorno;
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return null;
	}	
	
	/**
	 * @param String userId
	 * @param UtilCodesEnum status
	 * @return ArrayList<SignatureFileControl>
	 * */
	@SuppressWarnings("unchecked")
	public ArrayList<SignatureFileControl> findAllDocumentsByUserStatus(String userId,UtilCodesEnum status) 
	{
		final EntityManager em = emProvider.get();
		try 
		{
			String sql ="SELECT sc.* "
						+"FROM SIGNATURE_FILE_CTRL sc "
						+"INNER JOIN SIGNATURE_FILE_CTRL_DET det ON (det.SIGN_FILE_CTRL_ID = sc.ID) "
						+"WHERE sc.STATUS IN ('"+status.getCode()+"') AND det.NAME_DESTINATION = '"+userId+"' "
						+"ORDER BY sc.GENERATED_DATE ASC ";
		
			final Query query = em.createNativeQuery(sql,SignatureFileControl.class);
			
			Collection<SignatureFileControl> result = query.getResultList();			
			ArrayList<SignatureFileControl> retorno = new ArrayList<>(result);
			
			return  retorno;
		}
		catch (NoResultException x) 
		{
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User."), ex); //$NON-NLS-1$
		}
		return null;	
	}
	

	/**
	 * delete all siganture file control generated/document Id
	 * @param Long idDoc
	 * @param listIdGenerate
	 * @return Boolean
	 * */
	public Boolean deleteAllByDocId(Long idDoc,String listIdGenerate)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			if(listIdGenerate!=null && !listIdGenerate.equals("")) 
			{
				EntityTransaction tx = em.getTransaction();
				tx.begin();
				
				Query query = em.createNativeQuery("delete from SIGNATURE_FILE_CTRL_DET where SIGN_FILE_CTRL_ID "
						+ "IN "
						+ "( "
						+ "	select sc.ID from SIGNATURE_FILE_CTRL sc WHERE GENERATED_ID IN ("+listIdGenerate+") "					
						+ ") ");
				query.executeUpdate();
				
				query = em.createNativeQuery("delete from SIGNATURE_FILE_CTRL where GENERATED_ID IN (select ID from GENERATED where DOCUMENT_ID ="+idDoc+" ) ");
				query.executeUpdate();
				
				tx.commit();
			}
			return true;
		}
		catch (Exception e) {
			throw new IllegalStateException("Error deleteAllByDocId", e); //$NON-NLS-1$	
		}
	}	
}
