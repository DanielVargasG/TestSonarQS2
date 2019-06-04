package edn.cloud.business.scheduler;

import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorAttachFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.AdminParameters;

public class AttachSendJob implements Job
{
	//private final UtilLogger logger = UtilLogger.getInstance();
	private SuccessFactorAttachFacade attachFacade = new SuccessFactorAttachFacade();
	private SuccessFactorFacade successFactorFacade = new SuccessFactorFacade();
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	
	/**
	 * execute job bussiness logic
	 * @param JobExecutionContext jobExecutionContext
	 * */
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException 
	{
		//logger.info(AttachSendJob.class,"Start Attach Send Job--------------------------------");
		
		EntityManagerProvider.getInstance().initEntityManagerProvider();
		AdminParameters parameter = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_CONTROLP_ENABLE_JOB_EMPLOYEE_SYNC.getCode());
		
		if(parameter == null || parameter.getValue().toLowerCase().equals(UtilCodesEnum.CODE_TRUE.getCode())) 
		{
			//control of duplicate jobs 
			Date date = new Date();		
			String codeTime = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_CODE.getCode(), date);
			
			long idJobProcess = successFactorFacade.saveLoggerControl("200", "Start EventListAttachSendJob codeTime: "+codeTime,UtilCodesEnum.CODE_JOB_ATTACH_EVENT_LIST.getCode(), "Started");		
			Long idJobLog = successFactorAdmin.jobLogSave(idJobProcess,codeTime,UtilCodesEnum.CODE_JOB_ATTACH_EVENT_LIST.getCode());		
			
			if(idJobLog!=null) 
			{	
				attachFacade.actionProcessAttachEventListSendQuartz(idJobProcess);
				successFactorFacade.saveLoggerControl("200", "End EventListAttachSendJob, idJobProcess: "+idJobProcess, UtilCodesEnum.CODE_JOB_ATTACH_EVENT_LIST.getCode(), "Completed");				
			}
			else {
				successFactorFacade.saveLoggerControl("200", "End EventListAttachSendJob Repeated: "+codeTime+" , idJobProcess: "+idJobProcess, UtilCodesEnum.CODE_JOB_ATTACH_EVENT_LIST.getCode(), "Canceled");
			}
			
			//send attachment
			idJobProcess = successFactorFacade.saveLoggerControl("200", "Start AttachSendJob codeTime: "+codeTime,UtilCodesEnum.CODE_JOB_ATTACH.getCode(), "Started");
			idJobLog = successFactorAdmin.jobLogSave(idJobProcess,codeTime,UtilCodesEnum.CODE_JOB_ATTACH.getCode());		
			
			if(idJobLog!=null) {
				attachFacade.actionProcessAttachmentSyncSendQuartz();
				successFactorFacade.saveLoggerControl("200", "End AttachSendJob, idJobProcess: "+idJobProcess, UtilCodesEnum.CODE_JOB_ATTACH.getCode(), "Completed");	
			}else {
				successFactorFacade.saveLoggerControl("200", "End AttachSendJob Repeated: "+codeTime+" , idJobProcess: "+idJobProcess, UtilCodesEnum.CODE_JOB_ATTACH.getCode(), "Canceled");			
			}
		}
		else if(parameter.getValue().toLowerCase().equals(UtilCodesEnum.CODE_FALSE.getCode())) {
			successFactorFacade.saveLoggerControl("200", "Job EventListAttachSendJob Disabled", UtilCodesEnum.CODE_JOB_ATTACH_EVENT_LIST.getCode(), "Disabled");
		}
				
		//logger.info(AttachSendJob.class,"End Attach Send Job--------------------------------- ");
	}
}
