package edn.cloud.sfactor.business.interfaces;

import java.util.ArrayList;

import edn.cloud.business.dto.ResponseGenericDto;

public interface Email 
{
	/**
	 * send email to user
	 * @param String emailFrom
	 * @param String emailToSend
	 * @param String subject
	 * @param String urlDownload
	 * @param ArrayList<String> body
	 * @return ResponseGenericDto response
	 * */
	public ResponseGenericDto sendEmailResumeMassive(String emailFrom,String emailToSend,String subject,String urlDownload,ArrayList<String> body);
	
	
	/**
	 * send email Authorization Document to employee 
	 * @param String emailFrom
	 * @param String emailToSend
	 * @param String subject
	 * @param String nameDocument
	 * @param String nameEmployee
	 * @return ResponseGenericDto response
	 * */
	public ResponseGenericDto sendEmailAuthorization(String emailFrom,String emailToSend,String subject,String nameDocument,String nameEmployee);
}
