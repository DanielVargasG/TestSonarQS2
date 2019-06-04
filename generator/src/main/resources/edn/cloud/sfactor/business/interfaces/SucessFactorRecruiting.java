package edn.cloud.sfactor.business.interfaces;

import edn.cloud.business.dto.integration.SFRecList;
import edn.cloud.business.dto.integration.SFRecUnique;

public interface SucessFactorRecruiting 
{	
	/**
	 * get User From SoucessFactor
	 * @param String userName
	 * @param String date
	 * */
	public SFRecList getUser(String userName);
	
	
	/**
	 * get User From SoucessFactor
	 * @param String userName
	 * @param String date
	 * */
	public SFRecList getUserAdv(String firstName, String lastName, String title);
		
	
	/**
	 * get User From SoucessFactor
	 * @param String userName
	 * @param String date
	 * */
	
	public SFRecUnique getApplication(String appId);
}
	
	