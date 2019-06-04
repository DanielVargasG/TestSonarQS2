package edn.cloud.sfactor.business.interfaces;

import java.util.ArrayList;

import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.ResponseGenericDto;

public interface SucessFactorOnboarding 
{	
	/**
	 * get kms user of user in onboarding module version 2
	 * @param String userId
	 * @param String kms 
	 * */
	public String getUserInfoOnboardingModuleV2(String userId);
	
	/**
	 * get attach content for all modules
	 * 
	 * @param String idDocumentTypeOnb
	 * @param String[] patternNameOB [0]=begin position in all string [1]=separator [2]= position of element
	 * @param String kmsId
	 * @param Boolean loadContent
	 * 
	 * @return ArrayList<ContentFileInfo>
	 */
	public ArrayList<ContentFileInfo> getAttachNameFromOnboardingV2(String idDocumentTypeOnb,String[] patternNameOB, String kmsId, Boolean loadContent);
	
	public ArrayList<ContentFileInfo> getAttachNameFromOnboardingV2Key(String idDocumentTypeOnb,String[] patternNameOB, String kmsId, Boolean loadContent, String key);
	
	public ResponseGenericDto test(String idDocumentTypeOnb, String kmsId, Boolean loadContent);
}
	
	