package edn.cloud.sfactor.business.impl;

import com.sendgrid.*;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.sfactor.business.interfaces.SuccessFactor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class EmailImpl implements edn.cloud.sfactor.business.interfaces.Email
{
	/**
	 * send email Authorization Document to employee 
	 * @param String emailFrom
	 * @param String emailToSend
	 * @param String subject
	 * @param String nameDocument
	 * @param String nameEmployee
	 * @return ResponseGenericDto response
	 * */
	public ResponseGenericDto sendEmailAuthorization(String emailFrom,String emailToSend,String subject,String nameDocument,String nameEmployee)
	{
		ResponseGenericDto responseGeneric = new ResponseGenericDto();
		SuccessFactor SFactorIml = new SuccessFactorImpl();
		
		try 
		{	
			Email from = new Email(emailFrom);			
			Email to = new Email(emailToSend);
			
			URL url = getClass().getResource("/edn/cloud/web/email/sendEmailAuthorization.html");
			String text = "";
			
			//---------------------------------------------------------------------------
			//read file
			BufferedReader reader = new BufferedReader(new FileReader (url.getPath()));
		    String line = null;
		    StringBuilder  stringBuilder = new StringBuilder();
		    String ls = System.getProperty("line.separator");

		    try 
		    {
		        while((line = reader.readLine()) != null) 
		        {   
		            if(line!=null && !line.equals("") && line.contains(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()+"document"+UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()))
		            {
		            	stringBuilder.append(line.replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()+"document"+UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode(),nameDocument));		            	
		            }
		            else if(line!=null && !line.equals("") && line.contains(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()+"employee"+UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()))
		            {
		            	stringBuilder.append(line.replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()+"employee"+UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode(),nameEmployee));
		            }	            
		            else
		            {
		            	stringBuilder.append(line);	
		            }
		            
		            stringBuilder.append(ls);
		        }

		        text = stringBuilder.toString();
		    } finally {
		        reader.close();
		    }
			
			
			Content content = new Content("text/html", text);
						
			Mail mail = new Mail(from, subject, to, content);
			SendGrid sg = new SendGrid(SFactorIml.hasKeyGetValue(UtilCodesEnum.CODE_CONNECT_KEY_EMAIL_SEND_GRID.getCode()).toString());
			
			Request request = new Request();	    
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
	    }
	    catch (Exception ex) 
	    {
	    	ex.printStackTrace();
	    }
		
		return responseGeneric;
	}
	
	/**
	 * send email to user
	 * @param String emailFrom
	 * @param String emailToSend
	 * @param String subject
	 * @param String urlDownload
	 * @param ArrayList<String> body
	 * @return ResponseGenericDto response
	 * */
	public ResponseGenericDto sendEmailResumeMassive(String emailFrom,String emailToSend,String subject,String urlDownload,ArrayList<String> body)
	{
		ResponseGenericDto responseGeneric = new ResponseGenericDto();
		SuccessFactor SFactorIml = new SuccessFactorImpl();
		
		try 
		{	
			Email from = new Email(emailFrom);			
			Email to = new Email(emailToSend);
			
			URL url = getClass().getResource("/edn/cloud/web/email/sendStatisticsMassive.html");
			String text = readStaticsFile(url.getPath(),urlDownload,body);
			Content content = new Content("text/html", text);
						
			Mail mail = new Mail(from, subject, to, content);
			SendGrid sg = new SendGrid(SFactorIml.hasKeyGetValue(UtilCodesEnum.CODE_CONNECT_KEY_EMAIL_SEND_GRID.getCode()).toString());
			
			Request request = new Request();	    
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
	    }
	    catch (Exception ex) 
	    {
	    	ex.printStackTrace();
	    }
		
		return responseGeneric;
	}
	
	/**
	 * load file 
	 * @param String file
	 * @param String urlDownload
	 * @param ArrayList<String> body
	 * @return String
	 * */
	private static String readStaticsFile(String file,String urlDownload,ArrayList<String> body) throws IOException 
	{
	    BufferedReader reader = new BufferedReader(new FileReader (file));
	    String line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String ls = System.getProperty("line.separator");

	    try 
	    {
	        while((line = reader.readLine()) != null) 
	        {   
	            if(line!=null && !line.equals("") 
	            		&& line.contains(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()
	            				+"body"+UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()))
	            {
	            	line.replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()+"body"+UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode(),"");
	            	if(body!=null) {
		            	for(String item:body) {
		            		stringBuilder.append(item);
		            	}
	            	}
	            }
	            else if(line!=null && !line.equals("") 
	            		&& line.contains(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()
	            				+"linkdonwload"+UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()))
	            {
	            	stringBuilder.append(line.replace(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()+"linkdonwload"+UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode(),urlDownload));
	            }	            
	            else
	            {
	            	stringBuilder.append(line);	
	            }
	            
	            stringBuilder.append(ls);
	        }

	        return stringBuilder.toString();
	    } finally {
	        reader.close();
	    }
	}
}