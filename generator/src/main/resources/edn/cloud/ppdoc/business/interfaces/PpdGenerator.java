package edn.cloud.ppdoc.business.interfaces;

import java.util.List;

import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.ppd.api.PpdTextOccurrencesDto;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.integration.SFUserDto;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.business.dto.ppd.api.PpdSignatureInfoDto;
import edn.cloud.business.dto.ppd.api.PpdSigningDataDto;
import edn.cloud.business.dto.ppd.api.PpdSigningHeadDto;
import edn.cloud.sfactor.persistence.entities.Document;
import edn.cloud.sfactor.persistence.entities.Generated;

public interface PpdGenerator
{	
	/** 
	 * document generated
	 * @param SFUserDto sfUse2r
	 * @param Document document 
	 * @param PpdDocGenRequestPayloadDto genVar
	 * @return Generated generateDoc
	 * */
	public Generated wServiceGenerateDoc(SFUserDto sfUse2r, Document document,PpdDocGenRequestPayloadDto genVar);
	
	/**
	 * Get Generated File to ppd  
	 * @param String identifierTemplate
	 * @return FileInfo
	 * */
	public ContentFileInfo wServiceGetFileGenerated(String identifierTemplate);	
	
	/**
	 * 
	 * @param ContentFileInfo file
	 * @param PpdSignatureInfoDto post
	 * @return PpdSigningHeadDto
	 * */
	public PpdSigningHeadDto wServiceSetSignatureDocument(ContentFileInfo file, PpdSignatureInfoDto post);
	
	/**
	 * call service CompanyDocument
	 * @param String bearer
	 * @param String id
	 * @return ResponseGenericDto
	 * */
	public ResponseGenericDto wServiceSetCompanyDocument(String Token, ContentFileInfo fl, String json);
	
	/**
	 * call service SetDocument
	 * @param String bearer
	 * @param String id
	 * */
	public String wServiceSetDocument(String Token, ContentFileInfo fl, String json);
	
	/**
	 * call service FileGenerated
	 * @param String bearer
	 * @param String id
	 * */
	public byte[] wServiceFileGenerated(String bearer, String id) ;
	
	/**
	 * call service prev generated
	 * @param String bearer
	 * @param String id
	 * */
	public byte[] wServicePrevGenerated(String bearer, String id,String page) ;

		
	/**
	 * return list status of signatures
	 * @param String idGenerated 
	 * @param String status
	 * @return PpdSigningProcessesDataDto	
	 * */
	public PpdSigningDataDto wServiceGetSigningProcess(String idGenerated, String status);
	
	/**
	 * call service delete 
	 * @param String idSignPpd
	 * @return String response ppd
	 * */
	public String wServiceDeletingSignature(String idSignPpd);	
	
	/**
	 * get information for signatures on document
	 * @param String idUploadedFile
	 * @param String keyWord
	 * @return List<PpdTextOccurrencesDto>
	 * */
	public List<PpdTextOccurrencesDto> wServiceUploadsTextOccurrences(String idUploadedFile, String keyWord);
}
