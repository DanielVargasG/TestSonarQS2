package edn.cloud.sfactor.persistence.dao;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.Template;

public class TemplateDAO extends BasicDAO<Template> 
{
	public TemplateDAO() 
	{
		super(EntityManagerProvider.getInstance());
	}
	
	/**
	 * @param String idGroupsList
	 * @parma String selfGeneration
	 * @return List<TemplateInfoDto> templates list filter by groups of user
	 **/
	public Collection<Template> getListByGroupUser(String idGroupsList, String selfGeneration)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = 
					"SELECT t.* " 
					+"FROM TEMPLATE t " 
					+"INNER JOIN FOLDERS_TEMPLATES u ON (u.TEMPLATE_ID = t.TEMPLATE_ID) "
					+"INNER JOIN FOLDERS f ON (f.ID = u.FOLDER_ID)" 
					+"INNER JOIN FOLDERS_GROUP fg ON (fg.FOLDER_ID = f.ID) "					
					+"WHERE fg.GROUP_ID IN ("+idGroupsList+") AND t.STATUS = '" + UtilCodesEnum.CODE_ACTIVE.getCode() + "' "
					+(selfGeneration!=null?" AND t.SELF_GENERATION='"+selfGeneration+"' ":"")
					+"ORDER BY t.TITLE ASC ";
			
			final Query query = em.createNativeQuery(sql, Template.class);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error getAllTemplateByEventList", e); //$NON-NLS-1$
			
		}
	}
	
	/**
	 * @param String idGroupsList
	 * return templates list of subfolders filter by groups of user
	 * 
	 * @return List<TemplateInfoDto>
	 **/
	public Collection<Template> getLisSubtByGroupUser(String idGroupsList)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = 
					"SELECT t.* " 
					+"FROM TEMPLATE t " 
					+"INNER JOIN FOLDERS_TEMPLATES u ON (u.TEMPLATE_ID = t.TEMPLATE_ID) "
					+"INNER JOIN FOLDERS fs ON (fs.ID = u.FOLDER_ID)" 
					+"INNER JOIN FOLDERS f ON (f.ID = fs.FK_PARENT_FOLDER)" 
					+"INNER JOIN FOLDERS_GROUP fg ON (fg.FOLDER_ID = f.ID) "
					+"WHERE fg.GROUP_ID IN ("+idGroupsList+") AND t.STATUS = '" + UtilCodesEnum.CODE_ACTIVE.getCode() + "' "
					+"ORDER BY t.TITLE ASC ";
			
			final Query query = em.createNativeQuery(sql, Template.class);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error getAllTemplateByEventList", e); //$NON-NLS-1$
			
		}
	}	
	
	/**
	 * get all templates by event listener specific
	 * @param Long idEventListener
	 * @return Collection<Template>
	 * */
	@SuppressWarnings("unchecked")
	public Collection<Template> getAllTemplateByEventList(Long idEventListener)
	{
		final EntityManager em = emProvider.get();
		
		try
		{
			String sql = "SELECT t FROM Template t WHERE t.eventListenerParam.id = "+idEventListener+" and t.status='"+UtilCodesEnum.CODE_ACTIVE.getCode()+"' ";			
			final Query query = em.createQuery(sql);
			return query.getResultList();
		}
		catch (Exception e) {
			throw new IllegalStateException("Error getAllTemplateByEventList", e); //$NON-NLS-1$
			
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<Template> getByNull() 
	{
		final EntityManager em = emProvider.get();

		try {
			String sql = "SELECT t.* " 
						+ "FROM TEMPLATE t " 
						+ "LEFT JOIN FOLDERS_TEMPLATES u ON (u.TEMPLATE_ID = t.TEMPLATE_ID) " 
						+ "WHERE u.TEMPLATE_ID IS NULL AND t.STATUS = '" + UtilCodesEnum.CODE_ACTIVE.getCode() + "'";
			final Query query = em.createNativeQuery(sql, Template.class);
			return query.getResultList();
		} catch (Exception e) {
			throw new IllegalStateException("Error getByNull", e); //$NON-NLS-1$

		}
	}

}