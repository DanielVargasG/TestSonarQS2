package edn.cloud.business.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class UtilLogger 
{
	private static Logger logger = null;
	private static UtilLogger loggerSingle = null;
	
	public static UtilLogger getInstance()
	{
		if (loggerSingle ==null || logger == null)
		{
			loggerSingle = new UtilLogger();
			logger =LoggerFactory.getLogger(UtilLogger.class.getName());	
		}
		
		return loggerSingle;
	}
	
	public  void error(String message)
	{
		logger.error("MESSAGE>>>>> : "+message);
	}	
	
	public  void info(String message)
	{
		logger.info("MESSAGE>>>>> : "+message);
	}
	
	public  void gson(Object message)
	{
		Gson gson = new Gson();
		logger.info("MESSAGE>>>>> : "+gson.toJson(message));
	}
	
	public  void error(Object obj, String message)
	{
		logger.error("ERROR : "+obj.getClass().getName()+" > "+message);
	}
	
	public  void info(Object obj, String message)
	{
		logger.info("MESSAGE>>>>> : "+obj.getClass().getName()+" > "+message);
	}
	
	public  void trace(String param0, String param1, String param2 )
	{
		logger.trace("MESSAGE>>>>> : "+param0+" > ",param1,param2);
	}
	
	public  void warn(Object obj, String message)
	{
		logger.error("MESSAGE>>>>> : "+obj.getClass().getName()+" > "+message);
	}
	
	
}
