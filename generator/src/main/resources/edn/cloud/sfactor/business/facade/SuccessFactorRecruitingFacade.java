package edn.cloud.sfactor.business.facade;

import edn.cloud.business.dto.integration.SFRecList;
import edn.cloud.business.dto.integration.SFRecUnique;
import edn.cloud.sfactor.business.impl.SuccessFactorRecruitingImpl;

public class SuccessFactorRecruitingFacade 
{
	private SuccessFactorRecruitingImpl successFactorRec = new SuccessFactorRecruitingImpl();
	
	//-----------------------------------------
	//methods events 
	
	/**
	 * get events by name
	 * @param String eventName
	 * @return EventListener
	 * */
	public SFRecList searchUser(String userName)
	{
		return successFactorRec.getUser(userName);
	}
	
	public SFRecList searchUserAdv(String firstName, String lastName, String title)
	{
		return successFactorRec.getUserAdv(firstName, lastName, title);
	}
	
	
	public SFRecUnique getApplication(String recId)
	{
		return successFactorRec.getApplication(recId);
	}
		
}
