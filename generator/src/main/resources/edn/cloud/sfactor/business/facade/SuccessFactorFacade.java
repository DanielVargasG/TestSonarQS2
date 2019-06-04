package edn.cloud.sfactor.business.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.GroupRule;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.SFUserPhotoDto;
import edn.cloud.business.dto.SignatureFieldDto;
import edn.cloud.business.dto.SignatureGroupDto;
import edn.cloud.business.dto.integration.SFAdmin;
import edn.cloud.business.dto.integration.SFAdminList;
import edn.cloud.business.dto.integration.SFGroupList;
import edn.cloud.business.dto.integration.SFPhotoDto;
import edn.cloud.business.dto.integration.SFRecUnique;
import edn.cloud.business.dto.integration.SFUserDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.business.dto.ppd.api.PpdSignatureInfoDto;
import edn.cloud.business.dto.ppd.api.PpdSignerTypeInfoDto;
import edn.cloud.business.dto.ppd.api.PpdSigningCallbackDto;
import edn.cloud.business.dto.ppd.api.PpdSigningErrorDto;
import edn.cloud.business.dto.ppd.api.PpdSigningHeadDto;
import edn.cloud.business.dto.ppd.api.PpdSigningMessageDto;
import edn.cloud.business.dto.ppd.api.PpdSigningSingersDto;
import edn.cloud.business.dto.ppd.api.PpdTextOccurrencesDto;
import edn.cloud.ppdoc.business.facade.PpdGeneratorFacade;
import edn.cloud.sfactor.business.facade.document.SuccessFactorDocumentFacade;
import edn.cloud.sfactor.business.impl.SuccessFactorGenerateImpl;
import edn.cloud.sfactor.business.impl.SuccessFactorImpl;
import edn.cloud.sfactor.business.impl.SucessFactorDocumentImpl;
import edn.cloud.sfactor.business.impl.SucessFactorTemplateImpl;
import edn.cloud.sfactor.business.interfaces.SuccessFactor;
import edn.cloud.sfactor.business.interfaces.SucessFactorDocument;
import edn.cloud.sfactor.business.interfaces.SucessFactorGenerator;
import edn.cloud.sfactor.business.interfaces.SucessFactorTemplate;
import edn.cloud.sfactor.business.utils.QueryBuilder;
import edn.cloud.sfactor.persistence.dao.EmployeeDAO;
import edn.cloud.sfactor.persistence.dao.FieldsMappingPpdDAO;
import edn.cloud.sfactor.persistence.dao.SignatureFileControlDetailDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;
import edn.cloud.sfactor.persistence.entities.Document;
import edn.cloud.sfactor.persistence.entities.DocumentFields;
import edn.cloud.sfactor.persistence.entities.Employee;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;
import edn.cloud.sfactor.persistence.entities.FieldsTemplateLibrary;
import edn.cloud.sfactor.persistence.entities.Generated;
import edn.cloud.sfactor.persistence.entities.SignatureFileControl;
import edn.cloud.sfactor.persistence.entities.SignatureFileControlDetail;
import edn.cloud.sfactor.persistence.entities.Template;
import edn.cloud.sfactor.persistence.entities.TemplateGroupSignature;

public class SuccessFactorFacade {
	private UtilLogger logger = UtilLogger.getInstance();
	private SucessFactorTemplate successFactorTemplate = new SucessFactorTemplateImpl();
	private SucessFactorDocument successFactorDocumentI = new SucessFactorDocumentImpl();
	private SuccessFactorAdminFacade succesFactorAdminFacade = new SuccessFactorAdminFacade();
	private SucessFactorGenerator successFactorGenerator = new SuccessFactorGenerateImpl();
	private PpdGeneratorFacade ppdGeneratorFacade = new PpdGeneratorFacade();
	private SuccessFactor successFactor = new SuccessFactorImpl();

	/**
	 * save logger control info
	 * 
	 * @param String
	 *            code
	 * @param String
	 *            message
	 * @param String
	 *            user
	 * @param String
	 *            status
	 */
	public Long saveLoggerControl(String code, String message, String user, String status) {
		return successFactor.saveLoggerControl(code, message, user, status);
	}

	/**
	 * get User From SuccessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public SFAdminList userAdminList(String groupName) {
		return successFactor.getAdminList(groupName);
	}

	/**
	 * get User From SuccessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public SFGroupList userGroupList(String groupName) {
		return successFactor.getGroupList(groupName);
	}

	/**
	 * get User From SuccessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public SFAdmin queryAdminList(String listOfUsers) {
		return successFactor.getQueryAdminList(listOfUsers);
	}
	
	/**
	 * get User From SuccessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public List<SFAdmin> queryAdminList2(String listOfUsers) {
		return successFactor.getQueryAdminList2(listOfUsers);
	}

	/**
	 * get Parameters from GROUPS From SuccessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public GroupRule queryGroupParamsList(String idFromGroup) {
		return successFactor.queryGroupParamsList(idFromGroup);
	}

	/**
	 * get User From SuccessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public SFUserDto userGetProfile(String userName, String date) {
		return successFactor.getUserProfile(userName, date);
	}

	/**
	 * get User From SuccessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public String userGetManagerCount(String userName, String date) {
		return successFactor.getUserManagerCount(userName, date);
	}

	/**
	 * get User From SuccessFactor
	 * 
	 * @param String
	 *            userName
	 * @param String
	 *            date
	 */
	public String userGetHrCount(String userName, String date) {
		return successFactor.getUserHrCount(userName, date);
	}

	/**
	 * get User Info in recruiting module
	 * 
	 * @param String
	 *            isUser
	 * @return String response json
	 */
	public String getUserGetProfileRecruitingModule(String idUser) {
		return successFactor.getUserInfoInRecruitingModule(idUser);
	}

	/**
	 * get team
	 * 
	 * @param String
	 *            hrSFUserName
	 */
	public List<SFUserPhotoDto> teamGet(String hrSFUserName, String onlyActive, String namesource) {
		return successFactor.getTeam(hrSFUserName, onlyActive, namesource);
	}

	/**
	 * get team
	 * 
	 * @param String
	 *            hrSFUserName
	 */
	public List<SFUserPhotoDto> teamMGet(String hrSFUserName, String onlyActive) {
		return successFactor.getMTeam(hrSFUserName, onlyActive);
	}

	/**
	 * get team
	 * 
	 * @param String
	 *            hrSFUserName
	 */
	public List<SFUserPhotoDto> search(String hrSFUserName, String onlyActive) {
		return successFactor.getSearch(hrSFUserName, onlyActive);
	}

	/**
	 * get key
	 * 
	 * @param String
	 *            key
	 */
	public Object hasKeyGetValue(String key) {
		return successFactor.hasKeyGetValue(key);
	}

	/**
	 * get photo
	 * 
	 * @param String
	 *            SFUserName
	 */
	public SFPhotoDto imgGet(String SFUserName) {
		return successFactor.getImage(SFUserName);
	}

	// Methods Template
	// ---------------------------------------------------------------
	// ---------------------------------------------------------------

	/**
	 * delete template in succesfactor
	 * 
	 * @param TemplateInfoDto
	 *            template
	 */
	public void deleteTemplate(TemplateInfoDto template) {
		Template templateEnty = successFactorTemplate.getTemplateById(template.getIdTemplate());
		templateEnty.setStatus(UtilCodesEnum.CODE_INACTIVE.getCode());
		successFactorTemplate.updateTemplate(templateEnty);
	}

	// Methods Template signature
	// ---------------------------------------------------------------
	// ---------------------------------------------------------------

	// Methods Template Fields
	// ---------------------------------------------------------------
	// ---------------------------------------------------------------

	// Methods template fields library
	// ----------------------------------------------------------------
	// ----------------------------------------------------------------

	// Methods Document
	// ---------------------------------------------------------------
	// ---------------------------------------------------------------

	/**
	 * get document signature by id doc
	 * 
	 * @param Long
	 *            id
	 * @return ArrayList<DocumentSignature>
	 */
	public ArrayList<SignatureGroupDto> getDocumentSignatureByDoc(Long id) {
		TemplateGroupSignature group = new TemplateGroupSignature();
		return UtilMapping.DocumentSignatureToDTO(group, successFactorDocumentI.getDocumentSignatureByDoc(id));
	}

	/**
	 * find Document by id
	 * 
	 * @param Long
	 *            id
	 */
	public Document documentGetById(Long id) {
		return successFactorDocumentI.getDocumentById(id);
	}

	/**
	 * find and load all Document info by id
	 * 
	 * @param Long
	 *            id
	 */
	public Document documentGetCompleteById(Long id) {
		Document document = successFactorDocumentI.getDocumentById(id);
		// document.getTemplateId().setSignatureGroupList(successFactorTemplate.getSignatureGroupTemplateList(document.getTemplateId().getId()));
		return document;
	}

	/**
	 * delete all document and dependencies
	 * 
	 * @param Long
	 *            idDoc
	 * @return Boolean
	 */
	public Boolean documentDelete(Long idDoc) {
		return successFactorDocumentI.documentDelete(idDoc);
	}

	/**
	 * return document list filter by user template
	 * 
	 * @param String
	 *            idUser
	 * @param String
	 *            statusDocument / optional
	 * @param FilterQueryDto
	 *            dtoFilter
	 * @return ArrayList<Document>
	 **/
	public ArrayList<Document> documentGetListByUserTemplate(String idUser, String statusDocument, FilterQueryDto dtoFilter) {
		ArrayList<Document> returList = new ArrayList<>();
		Collection<Document> docSub = successFactorDocumentI.getListByUserTemplate(idUser, statusDocument, dtoFilter);

		if (docSub != null) {
			returList = new ArrayList<>(docSub);
		}
		return returList;
	}

	/**
	 * return document list filter by groups of user
	 * 
	 * @param String
	 *            idUserSession
	 * @param String
	 *            statusDocument / optional
	 * @return List<Document>
	 **/
	public ArrayList<Document> documentGetListByGroupUser(String idUserSession, String statusDocument) {
		ArrayList<Document> returList = new ArrayList<>();
		SFGroupList listGroups = this.userGroupList(idUserSession);

		if (listGroups != null && listGroups.getD().length > 0) {
			Collection<Document> docSub = successFactorDocumentI.getListSubByGroupUser(UtilMapping.getStringByComaFromGroupList(listGroups), statusDocument);
			Collection<Document> docs = successFactorDocumentI.getListByGroupUser(UtilMapping.getStringByComaFromGroupList(listGroups), statusDocument);

			if (docSub != null) {
				for (Document doc : docSub) {
					returList.add(doc);
				}
			}

			if (docs != null) {
				for (Document doc : docs) {
					returList.add(doc);
				}
			}
		}

		return returList;
	}

	/**
	 * 
	 * find all documents by status
	 * 
	 * @param String
	 *            idUser
	 * @param String
	 *            status
	 * @return Collection<Document>
	 **/
	public Collection<Document> findAllDocumentsByStatus(String idUser, String status) {
		return successFactorDocumentI.findAllDocumentsByStatus(idUser, status);
	}

	/**
	 * return number of documents by user template
	 * 
	 * @param String
	 *            idUser
	 * @param String
	 *            statusDocument / optional
	 * @return Integer
	 **/
	public Integer getCountDocumentByUserTemplate(String idUser, String statusDocument) {
		return successFactorDocumentI.getCountDocumentByUserTemplate(idUser, statusDocument);
	}

	/**
	 * delete all document without template
	 */
	public void documentDeleteAllWithoutTemplate() {
		successFactorDocumentI.documentDeleteAllWithoutTemplate();
	}

	/**
	 * save document
	 * 
	 * @param Long
	 *            idTemplate
	 * @param String
	 *            DocId
	 * @param SFUserDto
	 *            sfuser
	 * @param String
	 *            idEventListener
	 * @return Document
	 */
	public Document documentSave(Long idTemplate, SFUserDto sfuser, String effectiveDate) {
		Template template = new Template();
		template = successFactorTemplate.getTemplateById(idTemplate);

		if (template != null) {
			String userHR = sfuser.callUserId();
			/*
			 * if (sfuser.callHr() != "") { userHR = sfuser.callHr(); }
			 */

			SFUserDto sfuserHR = new SFUserDto();
			sfuserHR = this.userGetProfile(userHR, UtilCodesEnum.DATE_SOURCE_USER_ACTIVE.getCode());

			if (sfuserHR != null) {
				Employee employee = new Employee();
				employee = successFactor.getEmployeeByUserHR(sfuserHR.callUserId());

				if (employee == null) {
					employee = new Employee();
					employee.setUserId(sfuserHR.callUserId());
					employee.setFirstName(sfuserHR.callFirstName());
					employee.setLastName(sfuserHR.callLastName());

					EmployeeDAO empDAO = new EmployeeDAO();
					empDAO.saveNew(employee);
				}

				Document doc = new Document();
				doc.setStatus(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode().toString());
				doc.setEffectiveDate(effectiveDate);
				doc.setCreateOn(new Date());
				doc.setOutputFormat(template.getFormatGenerated());
				doc.setTemplateId(template);
				doc.setTargetUser(sfuser.callUserId());
				doc.setTargetUser_firstName(sfuser.callFirstName());
				doc.setTargetUser_lastName(sfuser.callLastName());
				doc.setOwner(employee);
				doc.setOwnerId(employee.getId());
				doc = successFactorDocumentI.saveDocument(doc);

				return doc;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * save recruting document
	 * 
	 * @param Long
	 *            idTemplate
	 * @param String
	 *            DocId
	 * @param SFUserDto
	 *            sfuser
	 * @return Document
	 */
	public Document documentRecSave(Long idTemplate, SFRecUnique sfRecuser) {
		Template template = new Template();
		template = successFactorTemplate.getTemplateById(idTemplate);

		Document doc = new Document();
		doc.setStatus(UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode().toString());
		doc.setEffectiveDate(UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), new Date()));
		doc.setCreateOn(new Date());
		doc.setOutputFormat(UtilCodesEnum.CODE_PDF_DOC.getCode().toString());
		doc.setTemplateId(template);
		doc.setTargetUser(sfRecuser.getD().getApplicationId());
		doc.setTargetUser_firstName(sfRecuser.getD().getFirstName());
		doc.setTargetUser_lastName(sfRecuser.getD().getLastName());
		doc.setSource(template.getModule());
		doc = successFactorDocumentI.saveDocument(doc);

		return doc;
	}

	/**
	 * save new document Signature
	 * 
	 * @param SignatureFieldDto
	 *            signDto
	 * @return SignatureFieldDto
	 */
	public SignatureFieldDto documentSignatureInsert(Long idDocument, SignatureFieldDto signDto) {
		return successFactorDocumentI.documentSignatureInsert(idDocument, signDto);
	}

	/**
	 * reset document Signature
	 * 
	 * @param Long
	 *            idSignDocument
	 */
	public void documentSignatureReset(Long idSignDocument) {
		successFactorDocumentI.documentSignatureReset(idSignDocument);
	}

	/**
	 * update document Signature
	 * 
	 * @param Long
	 *            idDocument
	 * @param SignatureFieldDto
	 *            signDto
	 * @return SignatureFieldDto
	 */
	public SignatureFieldDto documentSignatureUpdate(Long idDocument, SignatureFieldDto signDto) {
		return successFactorDocumentI.documentSignatureUpdate(idDocument, signDto);
	}

	/**
	 * update status document by status signature control asociated
	 * 
	 * @param Long
	 *            idSignatureCtrl
	 * @param String
	 *            statusSignature
	 */
	public void documentUpdateStatusByStatusSign(Long idDocument, String statusSignature) {
		if (statusSignature != null) {
			if (UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_REJECTED.getCode().equals(statusSignature) || UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_EXPIRED.getCode().equals(statusSignature)
					|| UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_ERROR.getCode().equals(statusSignature) || UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_CANCELED.getCode().equals(statusSignature)) {
				successFactorDocumentI.updateStatusDocument(idDocument, UtilCodesEnum.CODE_STATUS_OTHEREASONCOMPLE_DOC.getCode());
			} else if (UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_SIGNED.getCode().equals(statusSignature) || UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_SENT.getCode().equals(statusSignature)
					|| UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_ARCHIVED.getCode().equals(statusSignature)) {
				successFactorDocumentI.updateStatusDocument(idDocument, UtilCodesEnum.CODE_STATUS_SUCCESFULLCOMPLE_DOC.getCode());
			} else if (UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_PENDING.getCode().equals(statusSignature)) {
				successFactorDocumentI.updateStatusDocument(idDocument, UtilCodesEnum.CODE_STATUS_PENDING_SIGN_DOC.getCode());
			}
		}
	}

	/**
	 * Replace signature of template by signature of documents
	 * 
	 * @param ArrayList<SignatureGroupDto>
	 *            signTemplate
	 * @param ArrayList<SignatureGroupDto>
	 *            signDocument
	 * @return ArrayList<SignatureGroupDto>
	 */
	public ArrayList<SignatureGroupDto> documentSignatureReplaceSignTemplate(ArrayList<SignatureGroupDto> signTemplate, ArrayList<SignatureGroupDto> signDocument) {
		if (signTemplate != null && signTemplate.size() > 0 && signDocument != null && signDocument.size() > 0) {
			for (SignatureGroupDto groupTemp : signTemplate) {
				for (SignatureFieldDto signTemp : groupTemp.getSignatures()) {
					for (SignatureGroupDto groupDoc : signDocument) {
						for (SignatureFieldDto signDoc : groupDoc.getSignatures()) {
							if (signTemp.getSlug().equals(signDoc.getSlug())) {
								signTemp.setOrder(signDoc.getOrder());
								signTemp.setPath(signDoc.getPath());
								signTemp.setValue(signDoc.getPath());
								signTemp.setFlag(Boolean.TRUE);
								signTemp.setIsSignDocument(Boolean.TRUE);
								signTemp.setFullName(signDoc.getFullName());

								if (signDoc.getIdSignTempLib() != null && signDoc.getIdSignTempLib() > 0) 
								{
									signTemp.setFlag(Boolean.FALSE);
									signTemp.setIdSignTempLib(signDoc.getIdSignTempLib());
								}

								signTemp.setIdSignDocument(Long.parseLong(signDoc.getId()));
							}
						}
					}
				}
			}
		}

		return signTemplate;
	}

	// ----------------------------------------------------------------
	// Methods document field

	/**
	 * Replace signature of template by signature of documents
	 * 
	 * @param ArrayList<DocumentFields>
	 *            docFieldsList
	 * @param PpdDocGenRequestPayloadDto
	 *            generateVariables
	 * @return ArrayList<SignatureGroupDto>
	 */
	public PpdDocGenRequestPayloadDto documentFieldReplaceTemplateField(ArrayList<DocumentFields> docFieldsList, PpdDocGenRequestPayloadDto generateVariables) {

		if (generateVariables != null && generateVariables.getVariables().size() > 0 && docFieldsList != null && docFieldsList.size() > 0) 
		{
			for (DocumentFields docField : docFieldsList) 
			{
				for (SlugItem templateField : generateVariables.getVariables()) 
				{
					if (docField.getNameSource().equals(templateField.getSlug())) 
					{
						templateField.setPath(docField.getNameDestination());
						templateField.setFlag(Boolean.TRUE);// only constants values
						templateField.setValue(docField.getNameDestination());
						templateField.setIdAux(docField.getId() + "");						
					}

				}
			}
		}

		return generateVariables;
	}

	/**
	 * get all documents fields by id document
	 * 
	 * @param Long
	 *            idDocumentField
	 * @return ArrayList<DocumentFields>
	 */
	public ArrayList<DocumentFields> documentFieldGetAllByIdDoc(Long idDocumentField) {
		return successFactorDocumentI.documentFieldGetAllByIdDoc(idDocumentField);
	}

	/**
	 * save new document field
	 * 
	 * @param Long
	 *            idDocument
	 * @param DocumentFields
	 *            docField
	 * @return DocumentFields
	 */
	public DocumentFields documentFieldInsert(Long idDocument, DocumentFields docField) {
		docField.setDocumentId(new Document(idDocument));
		return successFactorDocumentI.documentFieldInsert(docField);
	}

	/**
	 * reset document field
	 * 
	 * @param Long
	 *            idDocumentField
	 */
	public void documentFieldReset(Long idDocumentField) {
		successFactorDocumentI.documentFieldReset(idDocumentField);
	}

	/**
	 * update document field
	 * 
	 * @param Long
	 *            idDocument
	 * @param DocumentFields
	 *            docField
	 * @return DocumentFields
	 */
	public DocumentFields documentFieldUpdate(Long idDocument, DocumentFields docField) {
		docField.setDocumentId(new Document(idDocument));
		return successFactorDocumentI.documentFieldUpdate(docField);
	}

	// Methods Generated Automatic
	// ---------------------------------------------------------------
	// ---------------------------------------------------------------

	/**
	 * 
	 * @param Long
	 *            idTemplate
	 * @param String
	 *            idUser
	 * @param String
	 *            isAutoSignParamAdm
	 * @param String
	 *            urlCallBackSign
	 * @param String
	 *            effectiveDate
	 * @param String
	 *            sourceDocument
	 * @return GenErrorInfoDto
	 */
	public edn.cloud.business.dto.GenErrorInfoDto generatedDocumentAutomatic(Long idTemplate, String idUser, String isAutoSignParamAdm, String urlCallBackSign, String effectiveDate, String sourceDocument) {
		edn.cloud.business.dto.GenErrorInfoDto response = new edn.cloud.business.dto.GenErrorInfoDto();
		SuccessFactorDocumentFacade SFDocumentFacade = new SuccessFactorDocumentFacade();
		response.setField("");
		SFUserDto sfuser = new SFUserDto();

		try {
			// get Status to limit number of retries
			sfuser = userGetProfile(idUser, UtilCodesEnum.DATE_SOURCE_USER_ACTIVE.getCode());

			if (sfuser != null && idTemplate != null) {
				Document document = documentSave(idTemplate, sfuser, effectiveDate);

				if (document != null) {
					document.setSource(sourceDocument);
					response.setField(document.getId() + "");
					Generated generated = this.generatedDocumentInPPD(document.getId());
					if (generated != null) {
						if (document.getTemplateId() != null && (document.getTemplateId().getManagerConfirm().equals(UtilCodesEnum.CODE_MANAGER_TYPE_NONE.getCode())
								|| document.getTemplateId().getManagerConfirm().equals(UtilCodesEnum.CODE_MANAGER_TYPE_AUTHORIZATION.getCode()))) {
							response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());

							// send document to Ppd
							ResponseGenericDto responsePpd = new ResponseGenericDto();
							if (document.getTemplateId().getSendDocAutho().equals(UtilCodesEnum.CODE_TYPE_COMPANY_VAULT.getCode())) {
								responsePpd = SFDocumentFacade.sendDocumenToPpdEmployee(document, generated.getGeneratedIdPpd(), UtilCodesEnum.CODE_TYPE_COMPANY_VAULT.getCode());
							} else {
								responsePpd = SFDocumentFacade.sendDocumenToPpdEmployee(document, generated.getGeneratedIdPpd(), UtilCodesEnum.CODE_TYPE_EMPLOYEE_VAULT.getCode());
							}

							response.setMessage(responsePpd.getMessage());
							return response;
						}
						if (isAutoSignParamAdm.equals(UtilCodesEnum.CODE_TRUE.getCode())) {
							edn.cloud.business.dto.GenErrorInfoDto resultErrorInfo = this.signatureSendToSing(document.getId(), generated.getId(), urlCallBackSign);
							if (resultErrorInfo.getFlag()) {
								if (resultErrorInfo.getCode().equals(""))
									response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTE.getCode());

								response.setCode(resultErrorInfo.getCode());
								response.setMessage(resultErrorInfo.getMessage());
								return response;
							} else {
								response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
								response.setMessage("Error Sign Document in Ppd " + resultErrorInfo.getMessage() != null ? resultErrorInfo.getMessage() : "");
							}
						}
					} else {
						response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
						response.setMessage("Error generated Document in PPD");
					}
				} else {
					response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
					response.setMessage("Error generated Document in Succesfactor ");
				}
			} else {
				response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERRORFIELD.getCode());
				response.setMessage("Error get User Profile or Template null");
			}

			return response;
		} catch (Exception e) {
			e.printStackTrace();
			this.saveLoggerControl("200", "Step 3 interno errror  generatedDocumentAutomatic ", UtilCodesEnum.CODE_JOB_ATTACH_EVENT_LIST.getCode(), "Started");
			response.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_ERROR.getCode());
			response.setMessage("Error fatal generatedDocumentAutomatic: " + e.getMessage());
			return response;
		}

	}

	// Methods Generated
	// ---------------------------------------------------------------
	// ---------------------------------------------------------------

	/**
	 * save generated document
	 * 
	 * @param Generated
	 *            generateDoc
	 */
	public Generated generatedDocumentSave(Generated generateDoc) {
		return successFactorGenerator.saveGeneratedDocument(generateDoc);
	}

	/**
	 * get list generated by id document
	 * 
	 * @param Long
	 *            idDoc
	 */
	public List<Generated> generatedGetByIdDoc(Long idDoc) {
		return successFactorGenerator.getGeneratedByIdDoc(idDoc);
	}

	/**
	 * get generated by id
	 * 
	 * @param Long
	 *            idGenerated
	 */
	public Generated generatedGetById(Long idGenerated) {
		return successFactorGenerator.getGeneratedById(idGenerated);
	}

	/**
	 * send document to generated 
	 * @param SLong idTemplate
	 * @param String effectiveDate
	 * @return Generated response
	 */
	public Generated generatedDocumentInPPD(Long idDocument) 
	{
		try 
		{
			SuccessFactorTemplateFacade SFTemplateFacade = new SuccessFactorTemplateFacade();
			Document document = this.documentGetById(idDocument);
			SFUserDto sfUse2r = this.userGetProfile(document.getTargetUser(), UtilCodesEnum.DATE_SOURCE_USER_ACTIVE.getCode());
			PpdDocGenRequestPayloadDto genVar = SFTemplateFacade.templateGetFieldsList(document.getTemplateId().getId());

			ArrayList<DocumentFields> documentFields = this.documentFieldGetAllByIdDoc(document.getId());
			PpdDocGenRequestPayloadDto generateVariablesDoc = this.documentFieldReplaceTemplateField(documentFields, genVar);

			PpdDocGenRequestPayloadDto genVarFinal = SFTemplateFacade.templateGetValueQueryBuilder(generateVariablesDoc, document.getTargetUser(), false, document.getEffectiveDate(), false);
			
			//update data types on values
			genVarFinal = ppdGeneratorFacade.actionSetDataTypeOnVariable(genVarFinal);			
			Generated generateDoc = ppdGeneratorFacade.wServiceGenerateDoc(sfUse2r, document, genVarFinal);

			if (generateDoc != null) {
				// save generated
				Generated generated = this.generatedDocumentSave(generateDoc);
				if (generated != null && generateDoc.getErrors().size() <= 0) {
					// save status document
					ArrayList<SignatureGroupDto> listSigns = SFTemplateFacade.templateGetSignatureGroupList(document.getTemplateId().getId());

					// update status for document
					if (document.getTemplateId().getManagerConfirm().equals(UtilCodesEnum.CODE_MANAGER_TYPE_NONE.getCode()) || document.getTemplateId().getManagerConfirm().equals(UtilCodesEnum.CODE_MANAGER_TYPE_AUTHORIZATION.getCode())) {
						successFactorDocumentI.updateStatusDocument(idDocument, UtilCodesEnum.CODE_STATUS_VALIDATE_DOC.getCode());
					} else if (listSigns != null && listSigns.size() > 0 && listSigns.get(0).getSignatures() != null && listSigns.get(0).getSignatures().size() > 0) {
						successFactorDocumentI.updateStatusDocument(idDocument, UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());
					} else {
						successFactorDocumentI.updateStatusDocument(idDocument, UtilCodesEnum.CODE_STATUS_VALIDATE_DOC.getCode());
					}
				} else {
					successFactorDocumentI.updateStatusDocument(idDocument, UtilCodesEnum.CODE_STATUS_PENDING_DOC.getCode());
				}

				return generated;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// Methods Signature Control
	// ---------------------------------------------------------------
	// ---------------------------------------------------------------

	/**
	 * fill values with successfactor information for signature templates
	 * 
	 * @param PpdDocGenRequestPayloadDto
	 *            genVar
	 * @param String
	 *            userTarget
	 * @param Boolean
	 *            isShowError, if true load error info
	 * @param String
	 *            effectiveDate
	 */
	public SignatureGroupDto signatureGetValueQueryBuilder(SignatureGroupDto group, String userTarget, Boolean isShowError, String effectiveDate) {
		SuccessFactorFacade successFactorUtils = new SuccessFactorFacade();
		successFactorUtils.saveLoggerControl("400", "1.1 signatureGetValueQueryBuilder: user target :" + userTarget + " effective date: " + effectiveDate, "debug_autho", "Error for/loop");

		Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();
		ArrayList<SignatureFieldDto> signatureFieldList = new ArrayList<>();

		logger.gson(group);

		try {
			for (SignatureFieldDto item : group.getSignatures()) {
				if (item.getPath() != null && !item.getPath().equals("") && item.getFlag() == false) {

					String keyFirstName = UtilMapping.loadKeyValueFromString(UtilCodesEnum.CODE_TYPE_SIGNATURE_FIRST_NAME.getCode(), item.getPath().toString(), UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
					String keyLastName = UtilMapping.loadKeyValueFromString(UtilCodesEnum.CODE_TYPE_SIGNATURE_LAST_NAME.getCode(), item.getPath().toString(), UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
					String keyEmail = UtilMapping.loadKeyValueFromString(UtilCodesEnum.CODE_TYPE_SIGNATURE_EMAIL.getCode(), item.getPath().toString(), UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
					// jj

					if (keyFirstName != null && !keyFirstName.equals("")) {
						logger.info(keyFirstName + " " + keyLastName + " " + keyEmail);
						map.put(item.getSlug() + "_fn", new ResultBuilderDto(keyFirstName, "default", ""));
						map.put(item.getSlug() + "_ln", new ResultBuilderDto(keyLastName, "default", ""));
						map.put(item.getSlug() + "_ml", new ResultBuilderDto(keyEmail, "default", ""));
					}

					map.put(item.getSlug(), new ResultBuilderDto(item.getPath(), "default", ""));
					if (item.getFullName() != null && !item.getFullName().equals("")) {
						map.put(item.getSlug() + "_fullName", new ResultBuilderDto(item.getFullName(), "default", ""));
					}
				} else if (item.getFlag() == true) {
					item.setValue(item.getPath());// update value constant
					item.setFullName(item.getPath());
					signatureFieldList.add(item);
				} else if (isShowError)// show error
				{
					item.setPath("");
					item.setValue("there is no path");
					signatureFieldList.add(item);
				}
			}

			// data builder successFactorTemplate
			successFactorUtils.saveLoggerControl("400", "1.2 signatureGetValueQueryBuilder: map size :" + map.size() + " effective date: " + effectiveDate, "debug_autho", "Error for/loop");
			if (map.size() > 0) {
				successFactorUtils.saveLoggerControl("400", "1.3", "debug_autho", "Error for/loop");
				Map<String, ResultBuilderDto> dataMap = QueryBuilder.getInstance().convert(map, userTarget, effectiveDate);
				successFactorUtils.saveLoggerControl("400", "1.4", "debug_autho", "Error for/loop");

				if (dataMap != null && dataMap.size() > 0) {
					successFactorUtils.saveLoggerControl("400", "1.5", "debug_autho", "");
					for (SignatureFieldDto item : group.getSignatures()) {
						successFactorUtils.saveLoggerControl("400", "1.6", "debug_autho", "");
						if (item.getPath() != null && !item.getPath().equals("") && item.getFlag() == false && dataMap.get(item.getSlug()) != null) {
							successFactorUtils.saveLoggerControl("400", "1.7", "debug_autho", "");
							ResultBuilderDto resultBuilder = (ResultBuilderDto) dataMap.get(item.getSlug());
							ResultBuilderDto resultBuilderFull = (ResultBuilderDto) dataMap.get(item.getSlug() + "_fullName");
							ResultBuilderDto resultBuilderFN = (ResultBuilderDto) dataMap.get(item.getSlug() + "_fn");
							ResultBuilderDto resultBuilderLN = (ResultBuilderDto) dataMap.get(item.getSlug() + "_ln");
							ResultBuilderDto resultBuilderML = (ResultBuilderDto) dataMap.get(item.getSlug() + "_ml");

							String keyFirstNameVerif = UtilMapping.loadKeyValueFromString(UtilCodesEnum.CODE_TYPE_SIGNATURE_FIRST_NAME.getCode(), item.getPath().toString(), UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());

							successFactorUtils.saveLoggerControl("400", "1.8", "debug_autho", "");
							if (resultBuilder.getResult() != null && !resultBuilder.getResult().equals(UtilCodesEnum.CODE_QUERYBUILDER_INVALID.getCode())) {
								successFactorUtils.saveLoggerControl("400", "1.9", "debug_autho", "");
								item.setValue(resultBuilder.getResult());
								// item.setFullName(resultBuilder.getResult());
								if (resultBuilderFull != null && resultBuilderFull.getResult() != null) {
									item.setFullName(resultBuilderFull.getResult());
								}
								if (resultBuilderFN != null && resultBuilderFN.getResult() != null) {
									item.setValue(UtilCodesEnum.CODE_TYPE_SIGNATURE_FIRST_NAME.getCode() + ":" + resultBuilderFN.getResult() + UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()
											+ UtilCodesEnum.CODE_TYPE_SIGNATURE_LAST_NAME.getCode() + ":" + resultBuilderLN.getResult() + UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode()
											+ UtilCodesEnum.CODE_TYPE_SIGNATURE_EMAIL.getCode() + ":" + resultBuilderML.getResult());
									item.setFlag(true);
								}
								successFactorUtils.saveLoggerControl("400", "1.10", "debug_autho", "");
								signatureFieldList.add(item);
							} else if (isShowError)// show error
							{
								successFactorUtils.saveLoggerControl("400", "1.11 ojo error", "debug_autho", "");
								item.setValue("Invalid value");
								signatureFieldList.add(item);
							}
						}
					}
				}
			}

			group.setSignatures(signatureFieldList);

			logger.gson(group);

			return group;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(SuccessFactorFacade.class, e.getMessage());
		}

		return null;

	}

	/**
	 * 
	 * @param Long
	 *            idDoc
	 * @param String
	 *            status
	 * @param Date
	 *            filterDateJob
	 * @param Integer
	 *            maxReg
	 * @return ArrayList<SignatureFileControl>
	 */
	public ArrayList<SignatureFileControl> signatureFileControlByDoc(Long idDoc, String status, Date filterDateJob, Integer maxReg) {
		return successFactor.signatureFileControlByDoc(idDoc, status, filterDateJob, maxReg);
	}

	/**
	 * Cancel Process to signature by document
	 * 
	 * @param Long
	 *            idDoc return ArrayList<ResponseGenericDto>;
	 */
	public ArrayList<ResponseGenericDto> signatureCancelProcess(Long idDoc) {
		ArrayList<ResponseGenericDto> errorList = new ArrayList<ResponseGenericDto>();

		ArrayList<SignatureFileControl> signFileCtrlList = successFactor.signatureFileControlByDoc(idDoc, "'" + UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_PENDING.getCode() + "'", null, null);

		errorList = new ArrayList<ResponseGenericDto>();

		for (SignatureFileControl signFileCtrl : signFileCtrlList) {
			if (signFileCtrl.getIdPpdSignCtrl() != null) {
				String responseWs = ppdGeneratorFacade.wServiceDeletingSignature(signFileCtrl.getIdPpdSignCtrl());

				if (responseWs != null && !(responseWs.contains(UtilCodesEnum.CODE_ERROR_404.getCode()) || responseWs.contains(UtilCodesEnum.CODE_SUCCESS_202.getCode()))) {
					ResponseGenericDto response = new ResponseGenericDto();
					response.setMessage(responseWs);
					response.setCode(UtilCodesEnum.CODE_ERROR.getCode());
					errorList.add(response);
				} else
					this.signatureFileControlUpdateStatus(signFileCtrl.getId(), UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_CANCELED.getCode(), null);
			}
		}

		return errorList;
	}

	/**
	 * get list of documents sends to signs
	 * 
	 * @param String
	 *            userId
	 * @param UtilCodesEnum
	 *            status
	 */
	public List<SignatureFileControl> signatureGetFileControlList(String userId, UtilCodesEnum status) {
		return successFactor.getSignatureFileControlList(userId, status);
	}

	/**
	 * get all signs file control by id Ppd control
	 * 
	 * @param Long
	 *            idPpdSignCtrl
	 * @return ArrayList<SignatureFileControl>
	 */
	public void signatureUdpateCtrlCallback(PpdSigningCallbackDto ppdSigningCallBDto) {
		if (ppdSigningCallBDto != null && ppdSigningCallBDto.getExternal_id() != null) {
			ArrayList<SignatureFileControl> arraySings = successFactor.getAllSignFileCtrlByIdPpdCtrl(ppdSigningCallBDto.getExternal_id());

			if (arraySings != null) {
				for (SignatureFileControl signFileCtrl : arraySings) {
					if (ppdSigningCallBDto.getStatus() != null && !ppdSigningCallBDto.getStatus().equals("")) {
						String commentsObs = "", errorObs = "", observations = "";

						// search comments
						if (ppdSigningCallBDto.getComments() != null && ppdSigningCallBDto.getComments().size() > 0) {
							for (PpdSigningMessageDto comments : ppdSigningCallBDto.getComments()) {
								commentsObs += "idSigner: " + comments.getSigner_id() + " comment: " + comments.getComment() + ". ";
							}

							commentsObs = UtilCodesEnum.CODE_STRING_INIT.getCode() + " " + commentsObs + " " + UtilCodesEnum.CODE_STRING_END.getCode();
						}

						// search errors
						if (ppdSigningCallBDto.getErrors() != null && ppdSigningCallBDto.getErrors().size() > 0) {
							for (PpdSigningErrorDto errors : ppdSigningCallBDto.getErrors()) {
								errorObs += "ErrorCode: " + errors.getCode() + " ErrorMessage: " + errors.getMessage() + ". ";
							}

							errorObs = UtilCodesEnum.CODE_STRING_INIT.getCode() + " " + errorObs + " " + UtilCodesEnum.CODE_STRING_END.getCode();
						}

						if (!commentsObs.equals("") || !errorObs.equals("")) {
							observations = UtilMapping.getStringSubString((commentsObs + errorObs + signFileCtrl.getObservations()), 4999);
						}

						// update status document
						this.documentUpdateStatusByStatusSign(signFileCtrl.getGenerated().getDocument().getId(), ppdSigningCallBDto.getStatus());

						// update process signature ctrl
						this.signatureFileControlUpdateStatus(signFileCtrl.getId(), ppdSigningCallBDto.getStatus(), observations);

					}
				}
			}
		}
	}

	/**
	 * update status signature file control
	 * 
	 * @param Long
	 *            idSignatureCtrlFile
	 * @param String
	 *            status
	 * @param String
	 *            observations
	 * @return Boolean
	 */
	public Boolean signatureFileControlUpdateStatus(Long idSignatureCtrlFile, String status, String observations) {
		return successFactor.signatureFileControlUpdateStatus(idSignatureCtrlFile, status, observations);
	}

	/**
	 * update signature file control
	 * 
	 * @param SignatureFileControl
	 *            entity
	 * @return SignatureFileControl
	 */
	public SignatureFileControl signatureUpdateFileControl(SignatureFileControl entity) {
		return successFactor.signatureUpdateFileControl(entity);
	}

	/**
	 * get count signature by status
	 * 
	 * @param String
	 *            status
	 * @param String
	 *            userId
	 * @return Integer
	 */
	public Integer signatureFileControlCountByStatus(String userId, String status) {
		return successFactor.signatureFileControlCountByStatus(userId, status);
	}

	/**
	 * save signature file control
	 * 
	 * @param Generated
	 *            generated
	 * @param isWithCallBack
	 * @return SignatureFileControl
	 */
	public SignatureFileControl signatureSaveFileControl(Generated generated, Boolean isWithCallBack) {
		try {
			SignatureFileControl entity = new SignatureFileControl();
			entity.setGenerated(generated);
			entity.setGeneratedOn(new Date());
			entity.setIsWithCallBack(isWithCallBack);
			entity.setStatus(UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_PENDING.getCode());

			return successFactor.saveSignatureFileControl(entity);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * save signature file detail
	 * 
	 * @param SignatureFileControl
	 *            signatureFileCtrl
	 * @param PpdSigningHeadDto
	 *            detailSign
	 * @param ArrayList<SignatureGroupDto>
	 */
	public void signatureSaveDetailFileControl(SignatureFileControl signatureFileCtrl, PpdSigningHeadDto ppdSignHeadDto, ArrayList<SignatureGroupDto> listSignatureGroup) {
		SignatureFileControlDetail sign;
		SignatureFileControlDetailDAO signFileCrlDet = new SignatureFileControlDetailDAO();

		if (ppdSignHeadDto != null && ppdSignHeadDto.getSigners() != null) {
			for (PpdSigningSingersDto signPpd : ppdSignHeadDto.getSigners()) {
				sign = new SignatureFileControlDetail();
				sign.setIdPpd(signPpd.getId());
				sign.setNameSource(signPpd.getPdf_sign_field());

				sign.setOrder(Integer.parseInt(signPpd.getSigning_order()));
				sign.setSignatureFileCtrl(signatureFileCtrl);
				sign.setSignatoryLink(signPpd.getSignatory_link());

				// search id of user signer
				for (SignatureGroupDto signatureGroup : listSignatureGroup) {
					for (SignatureFieldDto signature : signatureGroup.getSignatures()) {
						// match pdf sign identificator example: sg1, sg2 ..
						if (signature.getSlug().equals(signPpd.getPdf_sign_field())) {
							sign.setNameDestination(signature.getValue().toString());
						}
					}
				}

				signFileCrlDet.saveNew(sign);
			}
		}
	}

	/**
	 * send file to generated signature to ppd
	 * 
	 * @param String
	 *            idDocumentGenerated
	 * @param Long
	 *            idDoc
	 * @param urlCallBackSign
	 * @return GenErrorInfoDto
	 */
	public edn.cloud.business.dto.GenErrorInfoDto signatureSendToSing(Long idDoc, Long idGenerated, String urlCallBackSign) {
		SuccessFactorTemplateFacade SFTemplateFacade = new SuccessFactorTemplateFacade();
		edn.cloud.business.dto.GenErrorInfoDto infoReturn = new edn.cloud.business.dto.GenErrorInfoDto();
		infoReturn.setMessage("");
		infoReturn.setCode("");

		try {
			// get last File generated in Ppd
			Generated generated = this.generatedGetById(idGenerated);

			if (generated != null) {

				ContentFileInfo file = ppdGeneratorFacade.wServiceGetFileGenerated(generated.getGeneratedIdPpd());

				if (file != null) {
					// get document save in succesfactor
					Document document = this.documentGetCompleteById(idDoc);

					if (document != null) 
					{
						AdminParameters paramSignExec = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_SIGNTECHNO.getCode());		
						AdminParameters paramSignExpi = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_SIGNEXPI.getCode());
						AdminParameters paramSignAutoSend = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_AUTO_SENDDOC_SIGNALL_PPD.getCode());
						
						//validations
						if(document.getTemplateId()!=null && document.getTemplateId().getTypeSign()!=null && document.getTemplateId().getTypeSign().equals(UtilCodesEnum.CODE_TYPE_SIGNATURE_OPENTRUST.getCode())) {
							paramSignExec = succesFactorAdminFacade.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_ADM_SIGNTECHNO_OPENTRUST.getCode());
						}

						if(paramSignExec==null || (paramSignExec!=null && paramSignExec.getValue().equals("")) || generated.getUploadIdPpd()==null)
						{
							infoReturn.setFlag(false);
							infoReturn.setMessage("Error: Sign technology is not configured");
							return infoReturn;
						}
						
						// get signatures for template
						ArrayList<SignatureGroupDto> listSignatureGroup = SFTemplateFacade.templateGetSignatureGroupList(document.getTemplateId().getId());

						if(listSignatureGroup!=null && listSignatureGroup.size()>0 
								&& listSignatureGroup.get(0).getSignatures()!=null && listSignatureGroup.get(0).getSignatures().size()>0)
						{
							SignatureFileControl signatureFileControl = this.signatureSaveFileControl(generated, (urlCallBackSign != null && !urlCallBackSign.equals("") ? true : false));

							// load signatures modified
							ArrayList<SignatureGroupDto> documentSignByDocList = this.getDocumentSignatureByDoc(idDoc);

							// update signature for signature of documents
							listSignatureGroup = this.documentSignatureReplaceSignTemplate(listSignatureGroup, documentSignByDocList);

							if (listSignatureGroup != null && listSignatureGroup.size() > 0) {
								// validate signature and get values in succesfactor
								for (int i = 0; i < listSignatureGroup.size(); i++) {
									SignatureGroupDto groupResponse = this.signatureGetValueQueryBuilder(listSignatureGroup.get(i), document.getTargetUser(), false, document.getEffectiveDate());
									if (groupResponse != null) {
										listSignatureGroup.set(i, groupResponse);
									} else {
										infoReturn.setMessage("error, undefined or not found signatures");
										infoReturn.setFlag(false);
										return infoReturn;
									}
								}
								
								// build dto object to send Ppd
								PpdSignatureInfoDto signatureDto = this.signatureGetPpdSignatureInfo(generated.getUploadIdPpd(), document, listSignatureGroup,paramSignExec!=null?paramSignExec.getValue():"", paramSignExpi.getValue());								
								
								if (signatureDto.getStatus() != null && signatureDto.getStatus().equals(UtilCodesEnum.CODE_ERROR.getCode())) {
									infoReturn.setMessage(signatureDto.getReason());
									infoReturn.setFlag(false);
									return infoReturn;
								}

								if (signatureDto.getSigners().length > 0) {

									if (signatureFileControl != null && signatureFileControl.getId() != null && signatureFileControl.getId() > 0) {
										// update externalId Reference
										signatureDto.setExternal_id(generated.getGeneratedIdPpd());

										if (urlCallBackSign != null && !urlCallBackSign.equals("")) {
											signatureDto.setCallback_url(urlCallBackSign);
										}

										signatureDto.setTitle(signatureDto.getTitle() + " - Ref: " + signatureDto.getExternal_id());

										// update parameter send auto when finish proccess signature
										signatureDto.setAuto_send("false");
										if (paramSignAutoSend != null && !paramSignAutoSend.getValue().equals("") && (paramSignAutoSend.getValue().equals("true") || paramSignAutoSend.getValue().equals("false"))) {
											signatureDto.setAuto_send(paramSignAutoSend.getValue());
										}

										// send file to sing in Ppd
										PpdSigningHeadDto ppdSigninHeadDto = ppdGeneratorFacade.wServiceSetSignatureDocument(file, signatureDto);

										// validate error in ppd
										if (ppdSigninHeadDto != null && ppdSigninHeadDto.getStatus() != null && ppdSigninHeadDto.getStatus().equals(UtilCodesEnum.CODE_ERROR.getCode())) {
											infoReturn.setMessage(ppdSigninHeadDto.getReason());
											infoReturn.setFlag(false);

											signatureFileControl.setStatus(UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_REJECTEDBYPPD.getCode());
											signatureFileControl.setObservations(ppdSigninHeadDto.getReason());
											this.signatureUpdateFileControl(signatureFileControl);
											return infoReturn;
										}

										// save signature detail control file
										this.signatureSaveDetailFileControl(signatureFileControl, ppdSigninHeadDto, listSignatureGroup);

										// update id ppd signature in signature file control
										signatureFileControl.setIdPpdSignCtrl(ppdSigninHeadDto.getExternal_id());
										this.signatureUpdateFileControl(signatureFileControl);

										// update status for document
										successFactorDocumentI.updateStatusDocument(idDoc, UtilCodesEnum.CODE_STATUS_PENDING_SIGN_DOC.getCode());

										infoReturn.setMessage("");
										infoReturn.setCode(UtilCodesEnum.CODE_STATUS_EVENTLIS_TERMIANTESENDTOSIGN.getCode());
										infoReturn.setFlag(true);
										return infoReturn;

									}
								} else {
									infoReturn.setFlag(false);
									infoReturn.setMessage("there are no signatures configured");

									signatureFileControl.setStatus(UtilCodesEnum.CODE_STATUS_PPD_SIGNATURE_ERROR.getCode());
									signatureFileControl.setObservations(infoReturn.getMessage());
									this.signatureUpdateFileControl(signatureFileControl);

									return infoReturn;
								}
							} else {
								infoReturn.setFlag(true);
								return infoReturn;
							}
						} else {
							infoReturn.setFlag(true);
							return infoReturn;
						}
					}
				} else {
					logger.error("actionSendFileGeneratedToSing, File Null");

					infoReturn.setMessage("PDF file generated not available for download");
					infoReturn.setFlag(false);
					return infoReturn;
				}
			}

			infoReturn.setFlag(false);
			return infoReturn;
		} catch (Exception e) {

			logger.error(e.getMessage());

			infoReturn.setMessage(e.getMessage());
			infoReturn.setFlag(false);

			return infoReturn;
		}
	}

	/**
	 * Convert Document entity to PpdSignatureInfoDto
	 * 
	 * @param String
	 *            idDocumentGenerated
	 * @param ramdomId
	 * @param Document
	 *            document
	 * @return PpdSignatureInfoDto
	 */
	private PpdSignatureInfoDto signatureGetPpdSignatureInfo(
			String idUploadedFile,
			Document document,
			ArrayList<SignatureGroupDto> listSignatureGroup, 
			String signTech, 
			String expiration)
	{
		PpdSignatureInfoDto post = new PpdSignatureInfoDto();
		
		//validate Document Type
		if(document.getTemplateId().getDocumentType()==null || (document.getTemplateId().getDocumentType()!=null && document.getTemplateId().getDocumentType().equals(""))){
			post.setStatus(UtilCodesEnum.CODE_ERROR.getCode());
			post.setReason("Error: Document Type in Template is not configured\n");
			return post;
		}
		
		if(signTech==null || (signTech!=null && signTech.equals(""))){
			post.setStatus(UtilCodesEnum.CODE_ERROR.getCode());
			post.setReason("Error: Sign technology is not configured");
			return post;
		}
		
		SuccessFactorAdminFacade successFactorAdmFacade = new SuccessFactorAdminFacade();		
		FieldsMappingPpd mappingFieldMail = new FieldsMappingPpd();
		post.setSignature_type(signTech);
		post.setDocument_type(document.getTemplateId().getDocumentType());

		//load information about mail to send signature (employee)
		if(document.getTemplateId()!=null && document.getTemplateId().getEmailSign()!=null) 
		{
			ArrayList<FieldsMappingPpd> mailMapping = successFactorAdmFacade.mappingPpdFieldsGetByName(document.getTemplateId().getEmailSign(),Boolean.FALSE);

			if(mailMapping!=null && mailMapping.size()>0 && mailMapping.get(0).getNameDestination()!=null)
				mappingFieldMail = mailMapping.get(0);
		}	

		// Example: Employment contract Mathieu Martin - July 2016 - Ref: 457895
		post.setTitle(document.getTemplateId().getTitle() + " - " + document.getTargetUser_firstName() + " " + document.getTargetUser_lastName() + " - " + UtilDateTimeAdapter.getDateNow());

		post.setReason("Electronic document generation");
		post.setLocation("Successfactors Connector");

		post.setExpiration_date(UtilDateTimeAdapter.getDateNowDelta(expiration));
		post.setExternal_id(idUploadedFile);

		int cont = 0;
		for (SignatureGroupDto signatureGroup : listSignatureGroup) {
			for (SignatureFieldDto signature : signatureGroup.getSignatures()) {
				cont++;
			}
		}

		PpdSignerTypeInfoDto[] r = new PpdSignerTypeInfoDto[cont];
		cont = 0;

		// build dato to signers
		for (SignatureGroupDto signatureGroup : listSignatureGroup) {
			for (SignatureFieldDto signature : signatureGroup.getSignatures()) {
				PpdSignerTypeInfoDto sign = new PpdSignerTypeInfoDto();
				sign.setPdf_sign_field(signature.getSlug());
				sign.setSigning_order(signature.getOrder());
				sign.setTechnical_id(signature.getValue().toString());
				
				//set position of each occurrences
				if(document.getTemplateId().getTypeSign().equals(UtilCodesEnum.CODE_TYPE_SIGNATURE_OPENTRUST.getCode()))
				{
					//Get the position of each occurrences for a given keyword in a PDF document. 
					List<PpdTextOccurrencesDto> textOcurrencesDtoList = ppdGeneratorFacade.wServiceUploadsTextOccurrences(idUploadedFile,signature.getSlug());
					sign.setGenerate_pdf_sign_field(UtilMapping.loadPpdSignerTypeInfoDtoFromOccurrences(signature.getSlug(),textOcurrencesDtoList));
				}

				FieldsMappingPpdDAO fieldDAO = new FieldsMappingPpdDAO();
				FieldsMappingPpd fi = fieldDAO.getFieldMappingByName("technical_id");

				Map<String, ResultBuilderDto> mapRes = null;
				if (!document.getTemplateId().getModule().equals("REC")) {
					Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();
					map.put("realuser", new ResultBuilderDto(fi.getNameDestination(), "default", ""));
					mapRes = QueryBuilder.getInstance().convert(map, document.getTargetUser(), "");
				}

				// validate if signature is direct by email
				String keyFirstName = UtilMapping.loadKeyValueFromString(UtilCodesEnum.CODE_TYPE_SIGNATURE_FIRST_NAME.getCode(), signature.getValue().toString(), UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
				if (keyFirstName != null && !keyFirstName.equals("")) {
					String keyLastName = UtilMapping.loadKeyValueFromString(UtilCodesEnum.CODE_TYPE_SIGNATURE_LAST_NAME.getCode(), signature.getValue().toString(), UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());
					String keyEmail = UtilMapping.loadKeyValueFromString(UtilCodesEnum.CODE_TYPE_SIGNATURE_EMAIL.getCode(), signature.getValue().toString(), UtilCodesEnum.CODE_PARAM_SEPARATOR_VALUEKEY.getCode());

					if (keyLastName != null && !keyLastName.equals("") && keyEmail != null && !keyEmail.equals("")) {
						sign.setEmail_address(keyEmail);
						sign.setFirst_name(keyFirstName);
						sign.setLast_name(keyLastName);
						sign.setType(UtilCodesEnum.CODE_TYPE_SIGNATURE_EXTERNAL.getCode());
						sign.setLanguage(UtilCodesEnum.CODE_TYPE_SIGNATURE_LANGUAGE_EN.getCode());
					} else {
						post.setStatus(UtilCodesEnum.CODE_ERROR.getCode());
						post.setReason("Error: type Signature Email, wrong format");
						return post;
					}
				} else if (mapRes != null && mapRes.get("realuser").getResult().equals(signature.getValue())) { 
					sign.setTechnical_id(signature.getValue().toString());
					sign.setType(UtilCodesEnum.CODE_TYPE_SIGNATURE_EMPLOYEE.getCode());

					// send to specific mail
					if (mappingFieldMail != null && mappingFieldMail.getNameDestination() != null && !mappingFieldMail.getNameDestination().equals("")) {
						String email = signatureGetMailToSend(mappingFieldMail, signature.getValue().toString());
						if (email != null && !email.equals("")) {
							sign.setEmail_address(email);
						}
					}
				} else {
					SFUserDto sfUserDto = this.userGetProfile(signature.getValue().toString(), UtilCodesEnum.DATE_SOURCE_USER_ACTIVE.getCode());
					sign.setTechnical_id(signature.getValue().toString());

					if (sfUserDto != null && sfUserDto.getEmailNav() != null && sfUserDto.getEmailNav().getResults().length > 0) {
						sign.setEmail_address((sfUserDto.getEmailNav().getResults()[0]).getEmailAddress());
						sign.setFirst_name((sfUserDto.getPersonalInfoNav().getResults()[0]).getFirstName());
						sign.setLast_name((sfUserDto.getPersonalInfoNav().getResults()[0]).getLastName());
						sign.setType(UtilCodesEnum.CODE_TYPE_SIGNATURE_EXTERNAL.getCode());
					} else {
						post.setStatus(UtilCodesEnum.CODE_ERROR.getCode());
						post.setReason("Error: user " + signature.getValue() + " does not have mail (EmailNav) ");
						return post;
					}
				}

				r[cont] = sign;
				cont++;
			}
		}

		post.setSigners(r);

		logger.gson(post);

		return post;
	}

	/**
	 * get mail for userId from querySuccessFactor
	 * 
	 * @param String
	 *            querySuccessFacMail,String idUser
	 * @return String mail
	 */
	private String signatureGetMailToSend(FieldsMappingPpd mappingFieldMail, String idUser) {
		Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();
		map.put(mappingFieldMail.getNameSource(), new ResultBuilderDto(mappingFieldMail.getNameDestination(), "default", ""));
		Map<String, ResultBuilderDto> dataMap = QueryBuilder.getInstance().convert(map, idUser, "");

		if (dataMap != null && dataMap.size() > 0) {
			// find result from query builder
			if (dataMap.get(mappingFieldMail.getNameSource()) != null) {
				ResultBuilderDto resultBuilder = (ResultBuilderDto) dataMap.get(mappingFieldMail.getNameSource());

				if (resultBuilder.getResult() != null && !resultBuilder.getResult().equals("") && !resultBuilder.getResult().equals(UtilCodesEnum.CODE_QUERYBUILDER_INVALID.getCode())) {
					return resultBuilder.getResult();
				}
			}
		}

		return "";
	}

	public ArrayList<FieldsTemplateLibrary> exportTableGetAll(String idtable) {

		if (idtable.equals(UtilCodesEnum.CODE_ENTITY_FIELDSTEMPLATELIBRARY.getCode())) {

			return successFactorTemplate.templateFieldLibraryGetAll();

		} else {
			return null;
		}
	}

	/**
	 * Get document archived for date
	 * 
	 * @param idUser
	 * @param statusDocument
	 * @param String
	 *            date1
	 * @param String
	 *            date2
	 * @return
	 */
	public Collection<Document> getDocumentByUserArchive(String idUser, String statusDocument, String date1, String date2, String userTarget) {
		return successFactorDocumentI.getDocumentByUserArchive(idUser, statusDocument, date1, date2, userTarget);

	}

}
