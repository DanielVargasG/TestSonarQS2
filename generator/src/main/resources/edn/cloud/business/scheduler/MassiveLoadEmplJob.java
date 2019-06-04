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
import edn.cloud.sfactor.business.facade.SuccessFactorMassiveLoadFacade;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.AdminParameters;

public class MassiveLoadEmplJob implements Job
{
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	private SuccessFactorFacade successFactorFacade = new SuccessFactorFacade();
	private SuccessFactorMassiveLoadFacade massLoadFacade = new SuccessFactorMassiveLoadFacade();
	//private final UtilLogger logger = UtilLogger.getInstance();
	
	/**
	 * 
	 * */
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException 
	{
		//validate if current minute is par
		if(UtilDateTimeAdapter.isCurrentMinutePar())
		{
			//logger.info(MassiveLoadEmplJob.class, "Start MassiveLoadEmplJob--------------------------------");
			EntityManagerProvider.getInstance().initEntityManagerProvider();
			AdminParameters parameter = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_CONTROLP_ENABLE_JOB_LOAD_MASSIVE_EMPL.getCode());
			
			if(parameter == null || parameter.getValue().toLowerCase().equals(UtilCodesEnum.CODE_TRUE.getCode())) 
			{
				//control of duplicate jobs 
				Date date = new Date();		
				String codeTime = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_CODE.getCode(), date);
				
				Long idJobProcess = successFactorFacade.saveLoggerControl("200", "Start MassiveLoadEmplJob ",UtilCodesEnum.CODE_JOB_MASS_EMPL.getCode(), "Started");
				Long idJobLog = successFactorAdmin.jobLogSave(idJobProcess,codeTime,UtilCodesEnum.CODE_JOB_MASS_EMPL.getCode());
		
				if(idJobLog!=null) 
				{			
					massLoadFacade.massiveLoadActionProcessQuartz(idJobProcess);
					successFactorFacade.saveLoggerControl("200", "End MassiveLoadEmplJob, idJobProcess: "+idJobProcess,UtilCodesEnum.CODE_JOB_MASS_EMPL.getCode(), "Completed");
				}
				else{
					successFactorFacade.saveLoggerControl("200", "End MassiveLoadEmplJob Repeated: "+codeTime+" , idJobProcess: "+idJobProcess, UtilCodesEnum.CODE_JOB_MASS_EMPL.getCode(), "Canceled");
				}
						
				//logger.info(MassiveLoadEmplJob.class, "End EvenListenerJob--------------------------------- "); 
			}
			else if(parameter.getValue().toLowerCase().equals(UtilCodesEnum.CODE_FALSE.getCode())) {
				successFactorFacade.saveLoggerControl("200", "Job MassiveLoadEmplJob Disabled", UtilCodesEnum.CODE_JOB_EVENT_LIST.getCode(), "Disabled");
				//logger.info(MassiveLoadEmplJob.class, "End MassiveLoadEmplJob--------------------------------- "); 
			}
		}		
	}
	
}
