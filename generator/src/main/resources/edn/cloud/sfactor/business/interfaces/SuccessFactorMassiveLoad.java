package edn.cloud.sfactor.business.interfaces;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import edn.cloud.sfactor.persistence.entities.MassiveLoadUser;

public interface SuccessFactorMassiveLoad 
{
	
	/**
	 * @param Load MassiveLoadUser
	 * @return MassiveloadUser
	 */
	public MassiveLoadUser userLoadGetById(Long idMass);

	/**
	 * Load and Save/Update User
	 * @return MassiveloadUser
	 */
	public MassiveLoadUser userLoadSave(MassiveLoadUser userLoad);
	
	/**
	 * Update user
	 * @return MassiveloadUser
	 */
	public MassiveLoadUser userUpdateSave(MassiveLoadUser userLoad) ;
	
	/**
	 * delete user
	 * @return MassiveloadUser
	 */
	public void massiveLoadDelete(MassiveLoadUser userLoad);	
	
	/**
	 *Get all MassiveLoadUser
	 *@return List<MassiveLoadUser>
	 */
	public List<MassiveLoadUser> getUserAll();
	
	/**
	 * Get time current future
	 * @param startDate
	 * @param isFutureDates
	 * @return
	 */
	public Collection<MassiveLoadUser> getAllUserTime(Date startDate, Boolean isFutureDates);
	
	/**
	 * Get number of eventlistener by massiveload
	 * @param Integer maxRegisterByPage
	 * @param String idMassiveLoad
	 * @return
	 */
	public Long eventListCtrlCountByIdMassLoad(Long idMassiveLoad);
}
