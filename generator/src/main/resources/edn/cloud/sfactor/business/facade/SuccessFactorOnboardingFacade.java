package edn.cloud.sfactor.business.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.PostInfo;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.ppdoc.business.impl.PpdGeneratorImpl;
import edn.cloud.ppdoc.business.interfaces.PpdGenerator;
import edn.cloud.sfactor.business.impl.SuccessFactorAdminImpl;
import edn.cloud.sfactor.business.impl.SuccessFactorOnboardingImpl;
import edn.cloud.sfactor.business.interfaces.SuccessFactorAdmin;
import edn.cloud.sfactor.business.interfaces.SucessFactorOnboarding;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.EventListenerCtrlProcess;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;

public class SuccessFactorOnboardingFacade {
	private SucessFactorOnboarding successFactorOnboarding = new SuccessFactorOnboardingImpl();
	private SuccessFactorAdmin successFactorAdmin = new SuccessFactorAdminImpl();
	private PpdGenerator ppdGenerator = new PpdGeneratorImpl();

	// -----------------------------------------
	// methods

	/**
	 * get kms user of user in onboarding module version 2
	 * 
	 * @param String
	 *            userId
	 * @param String
	 *            kms
	 */
	public String getUserInfoOnboardingModuleV2(String userId) {
		return successFactorOnboarding.getUserInfoOnboardingModuleV2(userId);
	}

	/**
	 * get attach content or/and name of ONBV2
	 * 
	 * @param String
	 *            idDocumentTypeOnb
	 * @param String[]
	 *            patternNameOB [0]=begin position in all string [1]=separator [2]=
	 *            position of element
	 * @param String
	 *            kmsId
	 * @param Boolean
	 *            loadContent
	 * 
	 * @return ArrayList<ContentFileInfo>
	 */
	public ArrayList<ContentFileInfo> getAttachNameFromOnboardingV2(String idDocumentTypeOnb, String[] patternNameOB, String kmsId, Boolean loadContent) {
		return successFactorOnboarding.getAttachNameFromOnboardingV2(idDocumentTypeOnb, patternNameOB, kmsId, loadContent);
	}
	
	public ArrayList<ContentFileInfo> getAttachNameFromOnboardingV2Key(String idDocumentTypeOnb, String[] patternNameOB, String kmsId, Boolean loadContent, String key) {
		return successFactorOnboarding.getAttachNameFromOnboardingV2Key(idDocumentTypeOnb, patternNameOB, kmsId, loadContent, key);
	}

	/**
	 * @param EventListenerCtrlProcess
	 *            eventProc
	 * @param FieldsMappingPpd
	 *            fieldTmp
	 * @param String
	 *            kmsUser
	 * @param String[]
	 *            patternNameOB [0]=begin position in all string [1]=separator [2]=
	 *            position of element
	 * @param ArrayList<FieldsMappingPpd>
	 *            fieldsMappingONBV2
	 * @return ArrayList<EventListenerDocProcess>
	 */
	public ArrayList<EventListenerDocProcess> saveEventListenerDocFromOnboardingV2(EventListenerCtrlProcess eventProc, FieldsMappingPpd fieldTmp, String kmsUser, String[] patternNameOB, ArrayList<FieldsMappingPpd> fieldsMappingONBV2) {
		ArrayList<EventListenerDocProcess> returnList = new ArrayList<>();
		ArrayList<ContentFileInfo> attachFilesList = successFactorOnboarding.getAttachNameFromOnboardingV2(fieldTmp.getNameDestination(), patternNameOB, kmsUser, false);

		if (attachFilesList != null && attachFilesList.size() > 0) {
			for (ContentFileInfo file : attachFilesList) {
				EventListenerDocProcess docItem = new EventListenerDocProcess();
				docItem.setFieldMapPpd(fieldTmp);
				docItem.setUserIdOtherPlat(kmsUser);
				docItem.setAttachmentFileName(UtilMapping.toStringSimpleFormat(file.getFileName()," "));
				docItem.setAttachmentIdSF((file.getRef1().getId() != null && !file.getRef1().getId().equals("")) ? file.getRef1().getId() : file.getFileName());
				docItem.setCreateOn(new Date());

				docItem.setEventListenerCtrlProc(eventProc);
				docItem.setStatus(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());

				docItem.setFieldMapPpdDest(null);
				if (fieldsMappingONBV2 != null && fieldsMappingONBV2.size() > 0 && !file.getRef1().getCode().equals(UtilCodesEnum.CODE_DEFAULT.getCode())) {
					for (FieldsMappingPpd docType : fieldsMappingONBV2) {
						if (docItem != null) {
							if (docType.getNameSource().contains(UtilCodesEnum.CODE_SEPARATOR_OPERATION.getCode() + "" + UtilCodesEnum.CODE_OPERATION_REF_DIS.getCode()) && docType.getNameDestination().equals(file.getRef1().getCode())) {
								docItem = null;
							} else if (docType.getNameSource().contains(UtilCodesEnum.CODE_SEPARATOR_OPERATION.getCode() + "" + UtilCodesEnum.CODE_OPERATION_REF.getCode()) && docType.getNameDestination().equals(file.getRef1().getCode())) {
								docItem.setFieldMapPpdDest(docType);
							}
						}
					}
				}

				if (docItem != null) {
					docItem = successFactorAdmin.eventListenerDocProcessInsert(docItem);
					returnList.add(docItem);
				}
			}
		}

		return returnList;
	}

	public ArrayList<EventListenerDocProcess> saveEventListenerDocFromOnboardingV2Key(EventListenerCtrlProcess eventProc, FieldsMappingPpd fieldTmp, String kmsUser, String[] patternNameOB, ArrayList<FieldsMappingPpd> fieldsMappingONBV2,
			String key) {
		ArrayList<EventListenerDocProcess> returnList = new ArrayList<>();
		ArrayList<ContentFileInfo> attachFilesList = successFactorOnboarding.getAttachNameFromOnboardingV2Key(fieldTmp.getNameDestination(), patternNameOB, kmsUser, false, key);

		if (attachFilesList != null && attachFilesList.size() > 0) {
			for (ContentFileInfo file : attachFilesList) {
				EventListenerDocProcess docItem = new EventListenerDocProcess();
				docItem.setFieldMapPpd(fieldTmp);
				docItem.setUserIdOtherPlat(kmsUser);
				docItem.setAttachmentFileName(UtilMapping.toStringSimpleFormat(file.getFileName()," "));
				docItem.setAttachmentIdSF((file.getRef1().getId() != null && !file.getRef1().getId().equals("")) ? file.getRef1().getId() : UtilMapping.toStringSimpleFormat(file.getFileName()," "));
				docItem.setCreateOn(new Date());

				docItem.setEventListenerCtrlProc(eventProc);
				docItem.setStatus(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());

				docItem.setFieldMapPpdDest(null);
				if (fieldsMappingONBV2 != null && fieldsMappingONBV2.size() > 0 && !file.getRef1().getCode().equals(UtilCodesEnum.CODE_DEFAULT.getCode())) {
					for (FieldsMappingPpd docType : fieldsMappingONBV2) {
						if (docItem != null) {
							if (docType.getNameSource().contains(UtilCodesEnum.CODE_SEPARATOR_OPERATION.getCode() + "" + UtilCodesEnum.CODE_OPERATION_REF_DIS.getCode()) && docType.getNameDestination().equals(file.getRef1().getCode())) {
								docItem = null;
							} else if (docType.getNameSource().contains(UtilCodesEnum.CODE_SEPARATOR_OPERATION.getCode() + "" + UtilCodesEnum.CODE_OPERATION_REF.getCode()) && docType.getNameDestination().equals(file.getRef1().getCode())) {
								docItem.setFieldMapPpdDest(docType);
							}
						}
					}
				}

				if (docItem != null) {
					docItem = successFactorAdmin.eventListenerDocProcessInsert(docItem);
					returnList.add(docItem);
				}
			}
		}

		return returnList;
	}

	/**
	 * Upload document to employee mode onboarding v2
	 * 
	 * @param String
	 *            UserId
	 * @param String
	 *            documentType
	 * @param ContentFileInfo
	 *            filesInfo
	 * @param String[]
	 *            orga
	 * @param HashMap<String,String>
	 *            metadata
	 * @param AdminParameters
	 *            paramFormatNameFile
	 * @return GenErrorInfoDto
	 */
	public GenResponseInfoDto uploadDocumentEmployeeOnboardingV2(String userId, String documentType, ContentFileInfo file, String[] orga, HashMap<String, String> metadata, AdminParameters paramFormatNameFile) {
		GenResponseInfoDto response = new GenResponseInfoDto();

		try {
			// send to ppd
			Gson gson = new Gson();
			PostInfo post = new PostInfo();
			post.setDocument_type_metas(metadata);

			// Metadata
			/*
			 * HashMap<String,String> map = new HashMap<>();
			 * map.put("country","Argentina (ARG)");
			 * map.put("bsc-date",UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.
			 * CODE_FORMAT_DATE.getCode(),new Date())); post.setDocument_type_metas(map);
			 */

			if (file != null) 
			{
				String title = UtilMapping.getFileNameToSendPpd(paramFormatNameFile != null ? paramFormatNameFile.getValue() : "", file.getFileName(), documentType, userId);
				
				post.setExternal_unique_id((file.getRef1().getId() != null && !file.getRef1().getId().equals("")) ? file.getRef1().getId() : file.getFileName());
				post.setTitle(UtilMapping.toStringSimpleFormat(title,""));
				post.setDate(UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), new Date()));
				post.setEmployee_technical_id(userId);
				post.setDocument_type_code(documentType);
				post.setOrganization_codes(orga);

				ContentFileInfo file1 = new ContentFileInfo();
				file1 = new ContentFileInfo();
				file1.setFile(file.getFile());

				

				file1.setFileName(UtilMapping.toStringSimpleFormat(title,""));

				ResponseGenericDto responseGenDto = ppdGenerator.wServiceSetCompanyDocument("X-KEY", file1, gson.toJson(post));

				if (responseGenDto != null && !responseGenDto.getCode().equals(UtilCodesEnum.CODE_ERROR.getCode())) {
					response.setMessage(responseGenDto.getMessage() + " " + UtilMapping.toStringHtmlFormat(gson.toJson(post)));
					response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());
				} else {
					response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
					response.setMessage("Error response ppd upload company document: " + responseGenDto.getMessage() + " " + UtilMapping.toStringHtmlFormat(gson.toJson(post)));
				}
			}
		} catch (Exception e) {
			response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
			response.setMessage(e.getMessage());
			e.printStackTrace();
		}

		return response;
	}
}
