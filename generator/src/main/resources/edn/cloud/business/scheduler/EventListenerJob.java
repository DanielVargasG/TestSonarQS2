package edn.cloud.business.scheduler;

import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorEventLFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.business.facade.document.SuccessFactorDocumentFacade;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.AdminParameters;


public class EventListenerJob implements Job {
	//private final UtilLogger logger = UtilLogger.getInstance();
	private SuccessFactorEventLFacade eventListenerFacade = new SuccessFactorEventLFacade();
	private SuccessFactorFacade successFactorFacade = new SuccessFactorFacade();
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();	

	/**
	 * execute job bussiness logic
	 * @param JobExecutionContext jobExecutionContext
	 */
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException 
	{
		//validate if current minute is par
		if(UtilDateTimeAdapter.isCurrentMinutePar())
		{
			//logger.info(EventListenerJob.class, "Start EvenListenerJob--------------------------------");
			EntityManagerProvider.getInstance().initEntityManagerProvider();
			AdminParameters parameter = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_CONTROLP_ENABLE_JOB_EVENT_LISTENER.getCode());
			
			if(parameter == null || parameter.getValue().toLowerCase().equals(UtilCodesEnum.CODE_TRUE.getCode())) {
			//control of duplicate jobs 
				Date date = new Date();		
				String codeTime = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_CODE.getCode(), date);
				
				Long idJobProcess = successFactorFacade.saveLoggerControl("200", "Start EventList ",UtilCodesEnum.CODE_JOB_EVENT_LIST.getCode(), "Started");
				Long idJobLog = successFactorAdmin.jobLogSave(idJobProcess,codeTime,UtilCodesEnum.CODE_JOB_EVENT_LIST.getCode());
		
				if(idJobLog!=null) 
				{
					eventListenerFacade.eventListenerActionProcessQuartz(idJobProcess);
					successFactorFacade.saveLoggerControl("200", "End EventList, idJobProcess: "+idJobProcess,UtilCodesEnum.CODE_JOB_EVENT_LIST.getCode(), "Completed");
				}
				else{
					successFactorFacade.saveLoggerControl("200", "End EventList Repeated: "+codeTime+" , idJobProcess: "+idJobProcess, UtilCodesEnum.CODE_JOB_EVENT_LIST.getCode(), "Canceled");
				}
						
				//logger.info(EventListenerJob.class, "End EvenListenerJob--------------------------------- "); 
			}
			else if(parameter.getValue().toLowerCase().equals(UtilCodesEnum.CODE_FALSE.getCode())) {
				successFactorFacade.saveLoggerControl("200", "Job Event Listener Disabled", UtilCodesEnum.CODE_JOB_EVENT_LIST.getCode(), "Disabled");
				//logger.info(EventListenerJob.class, "End EvenListenerJob--------------------------------- "); 
			}
		}
		
		SuccessFactorDocumentFacade SFDocumentFacade = new SuccessFactorDocumentFacade();
		SFDocumentFacade.documentUpdateArchiveByMaxTime();
	}	
}
