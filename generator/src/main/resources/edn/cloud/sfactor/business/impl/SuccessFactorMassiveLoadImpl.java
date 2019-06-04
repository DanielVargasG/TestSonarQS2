package edn.cloud.sfactor.business.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import edn.cloud.sfactor.business.interfaces.SuccessFactorMassiveLoad;
import edn.cloud.sfactor.persistence.dao.MassiveLoadUserDAO;
import edn.cloud.sfactor.persistence.entities.MassiveLoadUser;

public class SuccessFactorMassiveLoadImpl implements SuccessFactorMassiveLoad
{
	private MassiveLoadUserDAO massiveUserDAO = new MassiveLoadUserDAO();	
	
	/**
	 * Load and Save/Update User
	 * @return MassiveloadUser
	 */
	public MassiveLoadUser userLoadSave(MassiveLoadUser userLoad) {
		
		MassiveLoadUserDAO massiveUserDAO = new MassiveLoadUserDAO();
		return massiveUserDAO.saveNew(userLoad);
	}
	
	/**
	 * @param Load MassiveLoadUser
	 * @return MassiveloadUser
	 */
	public MassiveLoadUser userLoadGetById(Long idMass) {
		
		MassiveLoadUserDAO massiveUserDAO = new MassiveLoadUserDAO();
		return massiveUserDAO.getById(idMass);
	}	
	
	
	
	/**
	 * Update user
	 * @return MassiveloadUser
	 */
	public MassiveLoadUser userUpdateSave(MassiveLoadUser userLoad) {		
		massiveUserDAO = new MassiveLoadUserDAO();
		return massiveUserDAO.save(userLoad);		
	}
	
	/**
	 * delete user
	 * @return MassiveloadUser
	 */
	public void massiveLoadDelete(MassiveLoadUser userLoad) {		
		massiveUserDAO = new MassiveLoadUserDAO();
		massiveUserDAO.delete(userLoad);		
	}	
	
	
	/**
	 *Get all MassiveLoadUser
	 *@return List<MassiveLoadUser>
	 */
	public List<MassiveLoadUser> getUserAll(){
		return massiveUserDAO.getAll();
	}	
	
	/**
	 * Get time current future
	 * @param startDate
	 * @param isFutureDates
	 * @return
	 */
	public Collection<MassiveLoadUser> getAllUserTime(Date startDate, Boolean isFutureDates){
		MassiveLoadUserDAO userDAO= new MassiveLoadUserDAO();
		return userDAO.getAllUserTime(startDate, isFutureDates);
	}	
	
	
	/**
	 * Get number of eventlistener by massiveload
	 * @param Integer maxRegisterByPage
	 * @param String idMassiveLoad
	 * @return
	 */
	public Long eventListCtrlCountByIdMassLoad(Long idMassiveLoad) {
		MassiveLoadUserDAO userDAO= new MassiveLoadUserDAO();
		return userDAO.eventListCtrlCountByIdMassLoad(idMassiveLoad);
	}
}