package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.Document;

public class DocumentDAO extends BasicDAO<Document> 
{
	private UtilLogger logger = UtilLogger.getInstance();

	public DocumentDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Document> findAllDocuments() 
	{
		final EntityManager em = emProvider.get();
		try {
			final Query query = em.createQuery("select u from Document u where u.templateId IS NOT NULL ");
			return query.getResultList(); 
		} catch (NoResultException x) {
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User."), ex); //$NON-NLS-1$
		}
		return null;		
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Document> findAllDocumentsWithOutTemplate() 
	{
		final EntityManager em = emProvider.get();
		try {
			final Query query = em.createQuery("select u from Document u where u.templateId IS NULL ");
			return query.getResultList(); 
		} catch (NoResultException x) {
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User."), ex); //$NON-NLS-1$
		}
		return null;		
	}	
	
	/**
	 * delete all documents without template
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteAllWithoutTemplate() 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from Document u where templateId IS NOT NULL ");
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteDocumentByIdDoc", e); //$NON-NLS-1$	
		}		
	}
	
	
	/**
	 * delete document by id
	 * @param Long id
	 * @return boolean
	 * */
	public boolean deleteAllByDocId(Long idDoc) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			final Query query = em.createQuery("delete from Document u where u.id =:id ");
			query.setParameter("id", idDoc);
			
			query.executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) 
		{
			throw new IllegalStateException("Error deleteDocumentByIdDoc", e); //$NON-NLS-1$	
		}		
	}	
	
	
	/**
	 * 
	 * find all documents by status
	 * @param String userId
	 * @param String status
	 * @return Collection<Document>
	 * **/
	@SuppressWarnings("unchecked")
	public Collection<Document> findAllDocumentsByStatus(String userId,String status) 
	{
		final EntityManager em = emProvider.get();
		try 
		{
			String sql = "select u "
						+"from Document u "
						+"join u.owner o "
						+"where o.userId =:userId ";
			sql += (status!=null && !status.equals(""))?" and u.status IN ("+status+") ":"";
			
			final Query query = em.createQuery(sql);
			query.setParameter("userId",userId);
			return query.getResultList(); 
		} catch (NoResultException x) {
			//logger.warn("Could not retrieve entity for userId {} from table {}. Maybe the user doesn't exist yet.", userId, "User"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NonUniqueResultException ex) {
			throw new IllegalStateException(String.format("More than one entity for userId %s from table User."), ex); //$NON-NLS-1$
		}
		return null;	
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Document> getListByGroupUserww(Long idSignature)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = 
					"SELECT us.ID "
							+"FROM DOCUMENTS us "
							+"INNER JOIN GENERATED ug ON (ug.DOCUMENT_ID = us.ID) "
							+"INNER JOIN SIGNATURE_FILE_CTRL usig ON (usig.GENERATED_ID = ug.ID) "
							+"WHERE usig.ID = "+idSignature;
			
			final Query query = em.createNativeQuery(sql, Document.class);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error getAllTemplateByEventList", e); //$NON-NLS-1$
			
		}
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Updates status of document associated with signature control process
	 * @param Long idSignature
	 * @param String statusDocument
	 * */
	public void updateStatusDocumentByIdSign(Long idSignature, String statusDocument)
	{
		Collection<Document> tmp = getListByGroupUserww(idSignature);
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "UPDATE DOCUMENTS u "
						+"SET u.STATUS = '"+statusDocument+"' "
						+"WHERE u.ID IN( "
						+"SELECT us.ID "
						+"FROM DOCUMENTS us "
						+"INNER JOIN GENERATED ug ON (ug.DOCUMENT_ID = us.ID) "
						+"INNER JOIN SIGNATURE_FILE_CTRL usig ON (usig.GENERATED_ID = ug.ID) "
						+"WHERE usig.ID = "+idSignature
						+")";		
					
			final Query query = em.createNativeQuery(sql);						
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
	 * Updates status of document
	 * @param Long idDocument
	 * @param String statusDocument
	 * */
	public void updateStatusDocument(Long idDocument, String statusDocument)
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			
			String sql = "UPDATE Document u "
						+"SET status = :statusValue "
						+"WHERE u.id = :idValue  ";		
					
			final Query query = em.createQuery(sql);
			query.setParameter("statusValue",statusDocument);			
			query.setParameter("idValue",idDocument);
						
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
	 * return number of documents by user template
	 * @param String idGroupsList
	 * @param String statusDocument	 / optional 
	 * @return Integer
	 **/
	@SuppressWarnings("unchecked")
	public Integer getCountDocumentByUserTemplate(String idUser,String statusDocument)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = 
					"SELECT COUNT(d.ID) as cou " 
					+ "FROM DOCUMENTS d "
					+ "INNER JOIN TEMPLATE t ON (d.TEMPLATE_ID = t.TEMPLATE_ID) " 
					+ "INNER JOIN FOLDERS_TEMPLATES u ON (u.TEMPLATE_ID = t.TEMPLATE_ID) "
					+ "INNER JOIN FOLDERS_USERS fu ON (fu.FOLDER_ID = u.FOLDER_ID) "
					+ "WHERE fu.USER_ID = '"+idUser+"' "
					+ ((statusDocument!=null && statusDocument!="")?" AND d.STATUS IN ("+statusDocument+") ":" ")
					+ "and d.ARCHIVE Is null "
					+ "GROUP BY d.ID ";
			
			final Query query = em.createNativeQuery(sql);
			List<Object> obj  = query.getResultList();
			return obj.size();
		}
		catch (NoResultException e) {
			return 0;
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}	
	
	/**
	 * return document list filter by groups of user
	 * @param String idGroupsList
	 * @param String statusDocument	 / optional 
	 * @return List<Document>
	 **/
	@SuppressWarnings("unchecked")
	public Collection<Document> getListByGroupUser(String idGroupsList,String statusDocument)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = 
					"SELECT DISTINCT d.* " 
					+ "FROM DOCUMENTS d "
					+ "INNER JOIN TEMPLATE t ON (d.TEMPLATE_ID = t.TEMPLATE_ID) " 
					+ "INNER JOIN FOLDERS_TEMPLATES u ON (u.TEMPLATE_ID = t.TEMPLATE_ID) "
					+ "INNER JOIN FOLDERS f ON (f.ID = u.FOLDER_ID)" 
					+ "INNER JOIN FOLDERS_GROUP fg ON (fg.FOLDER_ID = f.ID) "
					+ "WHERE fg.GROUP_ID IN ("+idGroupsList+") "
					+ ((statusDocument!=null && statusDocument!="")?" AND d.STATUS IN ("+statusDocument+") ":" ");
			
			final Query query = em.createNativeQuery(sql, Document.class);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error getAllTemplateByEventList", e); //$NON-NLS-1$
			
		}
	}
	
	
	/**
	 * return document list filter by user template
	 * @param String idUser
	 * @param String statusDocument	 / optional
	 * @param FilterQueryDto dtoFilter 
	 * @return List<Document>
	 **/
	@SuppressWarnings("unchecked")
	public Collection<Document> getListByUserTemplate(String idUser,String statusDocument,FilterQueryDto dtoFilter)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String order = "", where = "";
			Boolean addGeneratorInner = false;
			String inner_generated = " INNER JOIN GENERATED ge ON (ge.DOCUMENT_ID = d.ID) ";
			int maxResult = 0;
			
						
			String sql = 
					"SELECT DISTINCT d.* " 
					+ "FROM DOCUMENTS d "
					+ "INNER JOIN TEMPLATE t ON (d.TEMPLATE_ID = t.TEMPLATE_ID) " 
					+ "INNER JOIN FOLDERS_TEMPLATES u ON (u.TEMPLATE_ID = t.TEMPLATE_ID) "
					+ "INNER JOIN FOLDERS_USERS fu ON (fu.FOLDER_ID = u.FOLDER_ID) "
					+ " @innergen@ "					
					+ "WHERE fu.USER_ID = '"+idUser+"' "
					+ ((statusDocument!=null && statusDocument!="")?" AND d.STATUS IN ("+statusDocument+") ":" ");
			
			order = "order by d.ID DESC ";
			
			if(dtoFilter!=null)
			{
				if(dtoFilter.getOrder()!=null && !dtoFilter.getOrder().equals(""))
				{
					if(dtoFilter.getOrder().equals("ASC_DATE")) {
						addGeneratorInner = true;
						order = " order by ge.GENERATED_ON ASC";
					}
					else if(dtoFilter.getOrder().equals("DESC_DATE")) {
						addGeneratorInner = true;
						order = " order by ge.GENERATED_ON DESC";
					}
				}
				
				if(dtoFilter.getUser()!=null && !dtoFilter.getUser().equals("")){
					where += " and LOWER(d.TARGET_USER) = '"+dtoFilter.getUser().toLowerCase()+"' ";
				}
				
				if(dtoFilter.getId()!=null && !dtoFilter.getId().equals("")){
					where += " and t.TEMPLATE_ID = "+dtoFilter.getId()+" ";
				}
				
				if(dtoFilter.getMaxResult()!=null && !dtoFilter.getMaxResult().equals("")){
					maxResult = Integer.parseInt(dtoFilter.getMaxResult());
				}
				
				if(dtoFilter.getDate()!=null && !dtoFilter.getDate().equals("")){
					where += " and d.CREATE_DATE <= '"+UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(),dtoFilter.getDate()+"  00:00")+"' ";
				}
				
				if(dtoFilter.getDateFinish()!=null && !dtoFilter.getDateFinish().equals("")){
					where += " and d.CREATE_DATE >= '"+UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(),dtoFilter.getDateFinish()+"  23:59")+"' ";
				}				
				
			}
			
			if(addGeneratorInner)
				sql = sql.replace("@innergen@",inner_generated);
			else
				sql = sql.replace("@innergen@"," ");
			
			sql = sql + where + order;
			final Query query = em.createNativeQuery(sql, Document.class);
			
			if(maxResult>0) {
				query.setMaxResults(maxResult);
			}
			
			return query.getResultList();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}	
	
	/**
	 * return document list associated with subfolders filter by groups of user
	 * @param String idGroupsList
	 * @param String statusDocument	 / optional 
	 * @return List<Document>
	 **/
	@SuppressWarnings("unchecked")
	public Collection<Document> getListSubByGroupUser(String idGroupsList,String statusDocument)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = 
					"SELECT DISTINCT d.* " 
					+ "FROM DOCUMENTS d "
					+ "INNER JOIN TEMPLATE t ON (d.TEMPLATE_ID = t.TEMPLATE_ID) " 
					+ "INNER JOIN FOLDERS_TEMPLATES u ON (u.TEMPLATE_ID = t.TEMPLATE_ID) "					
					+ "INNER JOIN FOLDERS fs ON (fs.ID = u.FOLDER_ID)" 
					+ "INNER JOIN FOLDERS f ON (f.ID = fs.FK_PARENT_FOLDER)"
					+ "INNER JOIN FOLDERS_GROUP fg ON (fg.FOLDER_ID = f.ID) "
					+ "WHERE fg.GROUP_ID IN ("+idGroupsList+") "
					+ ((statusDocument!=null && statusDocument!="")?" AND d.STATUS IN ("+statusDocument+") ":" ");
			
			final Query query = em.createNativeQuery(sql, Document.class);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error getAllTemplateByEventList", e); //$NON-NLS-1$
			
		}
	}
	/**
	 * Get document archived for date
	 * @param idUser
	 * @param statusDocument
	 * @String date1
	 * @String date2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<Document> getDocumentByUserArchive(String idUser,String statusDocument, String date1, String date2, String userTarget)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = 
					"SELECT d.* " 
					+ "FROM DOCUMENTS d "
					+ "INNER JOIN TEMPLATE t ON (d.TEMPLATE_ID = t.TEMPLATE_ID) " 
					+ "INNER JOIN FOLDERS_TEMPLATES u ON (u.TEMPLATE_ID = t.TEMPLATE_ID) "
					+ "INNER JOIN FOLDERS_USERS fu ON (fu.FOLDER_ID = u.FOLDER_ID) "
					+ "WHERE fu.USER_ID = '"+idUser+"' "
					+ ((statusDocument!=null && statusDocument!="")?" AND d.STATUS IN ("+statusDocument+") ":" ")
					+ "and d.ARCHIVE IS NOT NULL  "					
					;
			if(userTarget != null && !userTarget.equals("")) {
				sql += " and d.TARGET_USER = '"+userTarget+"'";
			}
			
			if(date1 != null && !date1.equals("") && date2 != null && !date2.equals("")) {
				sql += "and (d.CREATE_DATE >= ? "+
					   "and d.CREATE_DATE <= ?)";
			}
			
			final Query query = em.createNativeQuery(sql, Document.class);
			
			if(date1 != null && !date1.equals("") && date2 != null && !date2.equals("")) 
			{
				query.setParameter(1,new java.sql.Date(UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), date1+" 00:00").getTime()),TemporalType.TIMESTAMP);
				query.setParameter(2,new java.sql.Date(UtilDateTimeAdapter.getDateFromString(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), date2+" 23:59").getTime()),TemporalType.TIMESTAMP);
			}
			
			return query.getResultList();
		}
		catch (NoResultException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * update removes all records with creation date less than the filter date
	 * @param String archiveStatus
	 * @param Date filterDate
	 * @return boolean
	 * */
	public String documentUpdateArchiveByMaxTimeGetIds(String archiveStatus, Date filterDate) 
	{
		final EntityManager em = emProvider.get();
		
		try 
		{
			String sql = "SELECT us.ID FROM DOCUMENTS us WHERE us.ARCHIVE IS NULL AND us.CREATE_DATE <= ? ";
			String idDocs = "";
			Query query = em.createNativeQuery(sql, Document.class);
			query.setParameter(1,filterDate,TemporalType.TIMESTAMP);
			query.setMaxResults(50);
			
			Collection<Document> responseDocumentList = query.getResultList();
			if(responseDocumentList!=null && responseDocumentList.size()>0) 
			{
				for(Document doc :responseDocumentList) {
					idDocs +=doc.getId()+",";
				}
				
				idDocs = idDocs.substring(0,idDocs.length()-1);
			}
			
			return idDocs;
		}
		catch (Exception e) 
		{
			return ""; 	
		}		
	}	
	
	/**
	 * update removes all records with creation date less than the filter date
	 * @param String archiveStatus
	 * @param Date filterDate
	 * @param String idDocs
	 * @return boolean
	 * */
	public boolean documentUpdateArchiveByMaxTime(String archiveStatus, Date filterDate, String idDocs) 
	{
		if(idDocs!=null && !idDocs.equals(""))
		{
			final EntityManager em = emProvider.get();
			
			try 
			{
				EntityTransaction tx = em.getTransaction();
				tx.begin();
				
				String sql = "UPDATE Document u "
							+"SET archive = :archive "
							+"WHERE u.id IN("+idDocs+")  ";		
						
				final Query query = em.createQuery(sql);
				query.setParameter("archive",archiveStatus);
							
				query.executeUpdate(); 
				tx.commit();
			}
			catch (NoResultException e) {
				
			}
		}
		
		return true;
	}
}
