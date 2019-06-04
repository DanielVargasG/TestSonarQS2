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

public class AttachSearchJob implements Job 
{
	//private final UtilLogger logger = UtilLogger.getInstance();
	private SuccessFactorAttachFacade attachFacade = new SuccessFactorAttachFacade();
	private SuccessFactorFacade successFactorFacade = new SuccessFactorFacade();
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();

	/**
	 * execute job bussiness logic
	 * 
	 * @param JobExecutionContext
	 *            jobExecutionContext
	 */
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException 
	{
		//logger.info(AttachSearchJob.class, "Start EvenListener Attach Search Job--------------------------------");
		EntityManagerProvider.getInstance().initEntityManagerProvider();
		AdminParameters parameter = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_CONTROLP_ENABLE_JOB_EMPLOYEE_SYNC.getCode());

		if (parameter == null || parameter.getValue().toLowerCase().equals(UtilCodesEnum.CODE_TRUE.getCode())) {
			// control of duplicate jobs
			Date date = new Date();
			String codeTime = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_CODE.getCode(), date);

			Long idJobProcess = successFactorFacade.saveLoggerControl("200", "Start AttachSearchJob for Listeners", "JobAttachSr", "Started");
			Long idJobLog = successFactorAdmin.jobLogSave(idJobProcess, codeTime, UtilCodesEnum.CODE_JOB_ATTACH_SEARCH.getCode());

			if (idJobLog != null) {
				attachFacade.actionProcessAttachSearchQuartz(null, null, null, null);
				successFactorFacade.saveLoggerControl("200", "End AttachSearchJob for Listeners", UtilCodesEnum.CODE_JOB_ATTACH_SEARCH.getCode(), "Completed");
			} else {
				successFactorFacade.saveLoggerControl("200", "End AttachSearchJob Repeated: " + codeTime + " , idJobProcess: " + idJobProcess, UtilCodesEnum.CODE_JOB_ATTACH_SEARCH.getCode(), "Canceled");
			}
		} else if (parameter.getValue().toLowerCase().equals(UtilCodesEnum.CODE_FALSE.getCode())) {
			successFactorFacade.saveLoggerControl("200", "Job AttachSearchJob for Listeners Disabled", UtilCodesEnum.CODE_JOB_ATTACH_SEARCH.getCode(), "Disabled");
		}
		//logger.info(AttachSearchJob.class, "End EvenListener Attach Search--------------------------------- ");
	}
	// }
}
