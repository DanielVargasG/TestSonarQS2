package edn.cloud.sfactor.business.facade;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.dto.ppd.api.PpdSigningDataDto;
import edn.cloud.business.dto.ppd.api.PpdSigningHeadDto;
import edn.cloud.ppdoc.business.facade.PpdGeneratorFacade;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.SignatureFileControl;

public class SuccessFactorSignatureFacade 
{
	
	private SuccessFactorFacade successFactorF = new SuccessFactorFacade();
	private PpdGeneratorFacade ppdGeneratorFacade = new PpdGeneratorFacade();
	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();

	public SuccessFactorSignatureFacade() {
		super();
	} 
	
	/**
	 * proccess call for signature status job
	 *  0 0 0,3,6 ? * * *
	 * */
	public void signatureProccessQuartz()
	{
		Timestamp timestamp = new Timestamp((new Date()).getTime());
		
		// get maximun number of records to process
		Integer p_numMaxReg = 10;
		AdminParameters paramAdminCode = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_QUARZT_MAX_REG_JOBSIGN.getCode());
		if (paramAdminCode != null && paramAdminCode.getValue() != null) {
			p_numMaxReg = Integer.parseInt(paramAdminCode.getValue());
		}
		
		//Time interval in search of changes of signature status
		AdminParameters paramIntervalTime = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_TIME_INTERVAL_REG_JOBSIGN.getCode());
		
		//validate interval time
		Long intervalTime = -4L;
		if(paramIntervalTime!=null && paramIntervalTime.getValue()!=null && !paramIntervalTime.getValue().equals(""))
		{
			try{
				intervalTime = Long.parseLong(paramIntervalTime.getValue())*-1;
			}catch (Exception e) {
				intervalTime = -4L; 
			}
		}		
		
		//calculate endDate
		Date endDate = UtilDateTimeAdapter.getDateAddMinutes(timestamp, intervalTime);		
		
		//get all signature file control in status pending
		List<SignatureFileControl> signatureFileControlList = successFactorF.signatureFileControlByDoc(
				null,
				"'"+UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_PENDING.getCode()+"'",endDate,p_numMaxReg);
		
		if(signatureFileControlList!=null)
		{
			//load the pending signatures, search each of them in ppd and update the status of the document and the signature
			ppdGeneratorFacade.actionGetSigningProcessList(null, signatureFileControlList);			
		}
	}
	
	
	/**
	 * update status document and status signature control according to the state in ppd
	 * @param Long idDoc
	 * */
	public void signatureUpdateStatusByStatusPpd(Long idDoc)
	{
		//get all signature file control in status pending
		List<SignatureFileControl> signatureFileControlList = successFactorF.signatureFileControlByDoc(idDoc,
				"'"+UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_PENDING.getCode()+"'",null,null);
		
		if(signatureFileControlList!=null)
		{
			//signature Control Files 
			for(SignatureFileControl signInfoCtrl:signatureFileControlList)
			{
				//load info status signature in People Doc, example generatedIdPpd d752724e-9689-470c-8e20-f18feaf5b7b0
				PpdSigningDataDto signatureStatusPpd = ppdGeneratorFacade.wServiceGetSigningProcess(signInfoCtrl.getGenerated().getGeneratedIdPpd());
				
				if(signatureStatusPpd.getData()!=null && signatureStatusPpd.getData().size()>0)
				{
					//get status signarutePpd
					for(PpdSigningHeadDto head:signatureStatusPpd.getData())
					{
						if(signInfoCtrl.getGenerated()!=null && signInfoCtrl.getGenerated().getDocument()!=null)
						{
							//update status document
							successFactorF.documentUpdateStatusByStatusSign(signInfoCtrl.getGenerated().getDocument().getId(),head.getStatus());
						}
						
						//update status signature File Control
						successFactorF.signatureFileControlUpdateStatus(signInfoCtrl.getId(),head.getStatus(),null);
					}
				}
			}
		}
	}
}
