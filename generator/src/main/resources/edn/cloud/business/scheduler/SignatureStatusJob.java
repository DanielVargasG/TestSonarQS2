package edn.cloud.business.scheduler;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorSignatureFacade;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;

public class SignatureStatusJob implements Job
{
	private final UtilLogger logger = UtilLogger.getInstance();
	private SuccessFactorSignatureFacade signatureFacade = new SuccessFactorSignatureFacade();
	private SuccessFactorFacade successFactorFacade = new SuccessFactorFacade();
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	
	/**
	 * execute job bussiness logic
	 * @param JobExecutionContext jobExecutionContext
	 * */
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException 
	{		
		//logger.info(SignatureStatusJob.class,"Start SignatureStatus Job--------------------------------");		
		EntityManagerProvider.getInstance().initEntityManagerProvider();
		
		//control of duplicate jobs 
		Date date = new Date();		
		String codeTime = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_CODE.getCode(), date);
		
		Long idJobProcess = successFactorFacade.saveLoggerControl("200", "Start SignatureStatus",UtilCodesEnum.CODE_JOB_SIGNATURE.getCode(), "Started");
		Long idJobLog = successFactorAdmin.jobLogSave(idJobProcess,codeTime,UtilCodesEnum.CODE_JOB_SIGNATURE.getCode());		
		
		if(idJobLog!=null) {
			signatureFacade.signatureProccessQuartz();
			successFactorFacade.saveLoggerControl("200", "End SignatureStatus",UtilCodesEnum.CODE_JOB_SIGNATURE.getCode(), "Completed");
		}
		else {
			successFactorFacade.saveLoggerControl("200", "End SignatureStatus Repeated: "+codeTime+" , idJobProcess: "+idJobProcess, UtilCodesEnum.CODE_JOB_SIGNATURE.getCode(), "Canceled");
		}
		
		//logger.info(SignatureStatusJob.class,"End SignatureStatus Job--------------------------------- ");
	}
}
