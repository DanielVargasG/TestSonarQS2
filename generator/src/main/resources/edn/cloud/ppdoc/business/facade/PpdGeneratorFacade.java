package edn.cloud.ppdoc.business.facade;

import java.util.List;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.integration.SFUserDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.business.dto.ppd.api.PpdSignatureInfoDto;
import edn.cloud.business.dto.ppd.api.PpdSigningDataDto;
import edn.cloud.business.dto.ppd.api.PpdSigningHeadDto;
import edn.cloud.business.dto.ppd.api.PpdSigningSingersDto;
import edn.cloud.business.dto.ppd.api.PpdTextOccurrencesDto;
import edn.cloud.business.dto.sfactor.SFSignatureCtrlInfoDto;
import edn.cloud.business.dto.sfactor.SFSigningControlHeadDto;
import edn.cloud.ppdoc.business.impl.PpdGeneratorImpl;
import edn.cloud.ppdoc.business.interfaces.PpdGenerator;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.business.impl.SuccessFactorImpl;
import edn.cloud.sfactor.business.interfaces.SuccessFactor;
import edn.cloud.sfactor.persistence.entities.Document;
import edn.cloud.sfactor.persistence.entities.Generated;
import edn.cloud.sfactor.persistence.entities.SignatureFileControl;
import edn.cloud.sfactor.persistence.entities.SignatureFileControlDetail;

public class PpdGeneratorFacade
{
	private PpdGenerator ppdDocGenerator = new PpdGeneratorImpl();
	private SuccessFactor successFactorI = new SuccessFactorImpl();
			
	
	/**
	 * Applies data type transformations on the values
	 * @param PpdDocGenRequestPayloadDto genRequest
	 * @return PpdDocGenRequestPayloadDto genRequest
	 * */
	public PpdDocGenRequestPayloadDto actionSetDataTypeOnVariable(PpdDocGenRequestPayloadDto genRequest)
	{
		if(genRequest!=null && genRequest.getVariables()!=null)
		{
			for(SlugItem item:genRequest.getVariables())
			{
				if(item.getValue()!=null && item.getConvertValueTo()!=null 
						&& item.getConvertValueTo().contains(UtilCodesEnum.CODE_ACTIONS_ACT2_APPLY_TYPE_DATA.getCode()))
				{
					String[] action = item.getConvertValueTo().split(UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
					if(action!=null && action.length>1)
					{
						if(action[0].toString().equals(UtilCodesEnum.CODE_ACTIONS_ACT2_APPLY_TYPE_DATA.getCode()))
						{
							//transform the data type
							if(action[1].toString().equals(UtilCodesEnum.CODE_TYPE_DATA_STRING.getCode()))
							{
								item.setValue(item.getValue().toString());
							}
							else if(action[1].toString().equals(UtilCodesEnum.CODE_TYPE_DATA_NUMBER.getCode()))
							{
								try {
									item.setValue(Long.parseLong(item.getValue().toString()));
								}catch (Exception e) {
									
								}
							}
							else if(action[1].toString().equals(UtilCodesEnum.CODE_TYPE_DATA_BOOLEAN.getCode()))
							{
								try {
									item.setValue(Boolean.parseBoolean(item.getValue().toString()));
								}catch (Exception e) {
									
								}
							}
						}
					}
				}
			}
		}
		
		return genRequest;
	}
	
	/** 
	 * get signatures status from ppd
	 * @param List<SignatureFileControl> signatureFileControlList
	 * @param idUserSession
	 * @return PpdSignatureInfoListDto
	 * */
	public SFSigningControlHeadDto actionGetSigningProcessList(String idUserSession, List<SignatureFileControl> signatureFileControlList)
	{		
		SuccessFactorFacade successFactorF = new SuccessFactorFacade();
			
		SFSigningControlHeadDto responseList = new SFSigningControlHeadDto();
		
		//get status form ppd
		for(SignatureFileControl item:signatureFileControlList)
		{
			//load info status signature in People Doc
			PpdSigningDataDto signatureStatus = ppdDocGenerator.wServiceGetSigningProcess(item.getGenerated().getGeneratedIdPpd(),"");
			if(signatureStatus.getData()!=null && signatureStatus.getData().size()>0)
			{				
				//calculate last signer pending, signature phase
				SFSignatureCtrlInfoDto signInfoCtrl = this.actionCalculateInfoSinging(idUserSession,item,signatureStatus);
				
				if(item.getGenerated()!=null && item.getGenerated().getDocument()!=null)
				{
					signInfoCtrl.setIdDocument(item.getGenerated().getDocument().getId().toString());
					
					//update status document
					successFactorF.documentUpdateStatusByStatusSign(item.getGenerated().getDocument().getId(),signInfoCtrl.getStatus());
				}
				
				successFactorI.signatureFileControlUpdateStatus(Long.parseLong(signInfoCtrl.getIdSignatureCtrl()),signInfoCtrl.getStatus(),null);
								
				if(signInfoCtrl.getStatus().equals(UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_PENDING.getCode())){
					responseList.getSignatureCtrlInfoList().add(signInfoCtrl);
				}
			}
		}
		
		return responseList;
	}
	
	/**
	 * calculate information from ppd information signing data
	 * @param String idUserSession
	 * @param SignatureFileControl responseList
	 * @param PpdSigningDataDto ppdSigninInfo
	 * @return SFSigningControlDto signatureCtrlInfoDto
	 * */
	private SFSignatureCtrlInfoDto actionCalculateInfoSinging(String idUserSession,SignatureFileControl signEntity,PpdSigningDataDto ppdSigninInfo)
	{
		SFSignatureCtrlInfoDto signatureCtrlInfoDto = new SFSignatureCtrlInfoDto();
		int countSingersOk = 0;
		for(PpdSigningHeadDto head:ppdSigninInfo.getData())
		{	
			signatureCtrlInfoDto.setTitle(head.getTitle());
			signatureCtrlInfoDto.setDocument_type(head.getDocument_type());
			signatureCtrlInfoDto.setStatus(head.getStatus());
			signatureCtrlInfoDto.setIdSignatureCtrl(signEntity.getId().toString());
			signatureCtrlInfoDto.setDateSignControl(UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(),signEntity.getGeneratedDateOn()));
			signatureCtrlInfoDto.setShowLinkToSign(Boolean.FALSE);
			
			countSingersOk = 0;
			for(PpdSigningSingersDto signers: head.getSigners())
			{	
				if(signers.getState().equals(UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_PENDING.getCode()))
				{
					//load detail of signature ctrl				
					List<SignatureFileControlDetail> signsCtrlDetail = successFactorI.signatureFileCtrlDetailGetByIdCtrl(signEntity.getId());
				
					if(signsCtrlDetail!=null)
					{
						for(SignatureFileControlDetail signDetail:signsCtrlDetail)
						{						
							if(idUserSession!=null &&idUserSession.equals(signDetail.getNameDestination()) && signDetail.getNameSource().equals(signers.getPdf_sign_field()))
							{
								signatureCtrlInfoDto.setShowLinkToSign(Boolean.TRUE);
								signatureCtrlInfoDto.setSignatoryLink(signers.getSignatory_link());
							}
						}
					}
				}
				
				if(signers.getState().equals(UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_SIGNED.getCode()))
				{
					countSingersOk++;
				}
			}
			
			signatureCtrlInfoDto.setPhaseCurrent(countSingersOk+"/"+head.getSigners().size());
		}
		
		return signatureCtrlInfoDto;
	}

	
	/** 
	 * document generated
	 * @param SFUserDto sfUse2r
	 * @param Document document 
	 * @param PpdDocGenRequestPayloadDto genVar
	 * @return Generated generateDoc
	 * */
	public Generated wServiceGenerateDoc(SFUserDto sfUse2r, Document document, PpdDocGenRequestPayloadDto genVar)
	{
		return ppdDocGenerator.wServiceGenerateDoc(sfUse2r, document, genVar);
	}

	
	/** 
	 * @param ContentFileInfo file
	 * @param PpdSignatureInfoDto post
	 * @return PpdSigningHeadDto
	 * */
	public PpdSigningHeadDto wServiceSetSignatureDocument(ContentFileInfo file, PpdSignatureInfoDto post)
	{
		PpdSigningHeadDto signingHeadDto = new PpdSigningHeadDto();
		
		return ppdDocGenerator.wServiceSetSignatureDocument(file, post); 
	}
	
	/**
	 * Get Generated File to ppd  
	 * @param String identifierTemplate
	 * @return FileInfo
	 * */
	public ContentFileInfo wServiceGetFileGenerated(String identifierTemplate)
	{
		return ppdDocGenerator.wServiceGetFileGenerated(identifierTemplate);
	}	
	
	/**
	 * call service CompanyDocument
	 * @param String bearer
	 * @param String id
	 * */
	public ResponseGenericDto wServiceSetCompanyDocument(String Token, ContentFileInfo fl, String json){
		return ppdDocGenerator.wServiceSetCompanyDocument(Token, fl, json);
	}
	
	/**
	 * call service SetDocument
	 * @param String bearer
	 * @param String id
	 * */
	public String wServiceSetDocument(String Token, ContentFileInfo fl, String json){
		return ppdDocGenerator.wServiceSetDocument(Token, fl, json);
	}
	
	/**
	 * call service FileGenerated
	 * @param String bearer
	 * @param String id
	 * */
	public byte[] wServiceFileGenerated(String bearer, String id){
		return ppdDocGenerator.wServiceFileGenerated(bearer, id);
	}
	
	/**
	 * call service prev generated
	 * @param String bearer
	 * @param String id
	 * */
	public byte[] wServicePrevGenerated(String bearer, String id, String page){
		return ppdDocGenerator.wServicePrevGenerated(bearer, id, page);
	}
	
	/**
	 * call service delete 
	 * @param String idSignPpd
	 * @return String response ppd
	 * */
	public String wServiceDeletingSignature(String idSignPpd){
		return ppdDocGenerator.wServiceDeletingSignature(idSignPpd);
	}	
		
	
	/**
	 * Get Observation Signature
	 * @param String idGenerated
	 * @return PpdSigningDataDto
	 * */
	public PpdSigningDataDto wServiceGetSigningProcess(String idGenerated) {
		PpdSigningDataDto signatureStatus = ppdDocGenerator.wServiceGetSigningProcess(idGenerated,"");
		return signatureStatus;
	}
	

	/**
	 * get information for signatures on document
	 * @param String idUploadedFile
	 * @param String keyWord
	 * @return List<PpdTextOccurrencesDto>
	 * */
	public List<PpdTextOccurrencesDto> wServiceUploadsTextOccurrences(String idUploadedFile,String keyWord){
		return ppdDocGenerator.wServiceUploadsTextOccurrences(idUploadedFile, keyWord);
	}
} 