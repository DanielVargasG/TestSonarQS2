package edn.cloud.ppdoc.business.facade;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.ppdoc.business.impl.PpdTemplateImpl;
import edn.cloud.ppdoc.business.interfaces.PpdTemplate;
import edn.cloud.sfactor.business.facade.SuccessFactorTemplateFacade;
import edn.cloud.sfactor.persistence.dao.FolderDAO;
import edn.cloud.sfactor.persistence.dao.FolderTemplateDAO;
import edn.cloud.sfactor.persistence.dao.FolderUserDAO;
import edn.cloud.sfactor.persistence.entities.Folder;
import edn.cloud.sfactor.persistence.entities.FolderTemplate;
import edn.cloud.sfactor.persistence.entities.FolderUser;

public class PpdTemplateFacade 
{
	private PpdTemplate ppdTemplateService = new PpdTemplateImpl();
	private SuccessFactorTemplateFacade SFTemplateFacade = new SuccessFactorTemplateFacade();	
	private UtilLogger logger = UtilLogger.getInstance();

	/**
	 * get info template
	 * 
	 * @param String
	 *            id
	 */
	public TemplateInfoDto getInfoTemplate(String id) {
		return ppdTemplateService.getInfoTemplate(id);
	}

	/**
	 * create new document in platform
	 * 
	 * @param String
	 *            id
	 * @param byte[]
	 *            bytes
	 * @param String
	 *            fileName
	 * @param String
	 *            version
	 */
	public GenResponseInfoDto createTemplateDoc(Long templateId, String id, byte[] bytes, String fileName) {
		ContentFileInfo file = new ContentFileInfo();
		file = new ContentFileInfo();
		file.setFile(bytes);
		file.setFileName(fileName);
		file.setId(id);

		// asociate document to template
		GenResponseInfoDto genError= ppdTemplateService.createTemplateDocument(id, file);
		if (genError.getFlag()) {
			// find last version
			GenResponseInfoDto genErrorReturn = new GenResponseInfoDto();
			TemplateInfoDto infoTemplate = ppdTemplateService.getInfoTemplate(id);

			if (infoTemplate != null) {
				PpdDocGenRequestPayloadDto response = ppdTemplateService.getFieldsTemplateByVersion(id, (infoTemplate.getLatest_version() + ""));

				// save fields template in sucessfactor
				if (response != null && response.getVariables() != null) {
					SFTemplateFacade.templateDeleteFields(templateId);
					SFTemplateFacade.templateSaveFields(templateId, UtilMapping.templateToEntity(response));
					// update version template
					SFTemplateFacade.templateUpdateVersion(templateId, (infoTemplate.getLatest_version() + ""));
				} else {
					logger.error(this.getClass().getName() + " Document no have fields");
					genErrorReturn.setMessage(this.getClass().getName() + " Document no have fields");
					genErrorReturn.setFlag(Boolean.FALSE);
					return genErrorReturn;
				}
			} else {
				logger.error(this.getClass().getName() + " Error get getFieldsTemplateByVersion");
				genErrorReturn.setMessage(this.getClass().getName() + " Error get getFieldsTemplateByVersion");
				genErrorReturn.setFlag(Boolean.FALSE);
				return genErrorReturn;
			}
			genErrorReturn.setFlag(Boolean.TRUE);
			return genErrorReturn;

		} else {
			
			logger.error(this.getClass().getName() + " PeopleDoc: error create document");
		}

		return genError;
	}

	/**
	 * If the id already does not exists yet, the folder will be created. If the id
	 * exists, update the title and description by defining metadata in json format.
	 * 
	 * @param String
	 *            val
	 */
	public Folder createFolderId(String val, String user) {

		Gson token = new Gson();
		Folder response = token.fromJson(val, Folder.class);
		FolderDAO fdao = new FolderDAO();

		if (!user.equals("")) {

			if(response.getParentFolder()!=null 
					&& response.getParentFolder().getId()!=null 
						&& response.getParentFolder().getId()==UtilCodesEnum.CODE_INVALID_OR_NONE.getCodeLong())
				response.setParentFolder(null);
			
			fdao.saveNew(response);

			//only firts level
			if(response.getParentFolder()==null) {
				FolderUserDAO fudao = new FolderUserDAO();
				FolderUser fu = new FolderUser();

				fu.setFolderId(response.getId());
				fu.setUserId(user);

				fudao.saveNew(fu);
			}
		}
		else 
		{			
			Folder entityCurrent = fdao.getById(response.getId(),null);
			entityCurrent.setTitle(response.getTitle());
			fdao.save(entityCurrent);
		}

		return response;
	}

	/**
	 * If the id already does not exists yet, the template will be created. If the
	 * id exists, update the title and description by defining metadata in json
	 * format.
	 * 
	 * @param String
	 *            val
	 */
	public TemplateInfoDto createTemplateId(String val) {
		Gson token = new Gson();
		TemplateInfoDto tempObj = token.fromJson(val, TemplateInfoDto.class);
		TemplateInfoDto response = ppdTemplateService.createTemplateId(val);
		
		if (response != null) {
			response.setIdEventListener(tempObj.getIdEventListener());
			response.setDocumentType(tempObj.getDocumentType());
			response.setEsign(tempObj.getEsign());
			response.setModule(tempObj.getModule());
			response.setLocale(tempObj.getLocale());
			response.setEmailSign(tempObj.getEmailSign());
			response.setSelfGeneration(tempObj.getSelfGeneration());
			response.setFormatGenerated(tempObj.getFormatGenerated());
			response.setManagerConfirm(tempObj.getManagerConfirm());
			response.setSendDocAutho(tempObj.getSendDocAutho());
			response.setTypeSign(tempObj.getTypeSign());
			response.setIdTemplate(SFTemplateFacade.templateSave(response));			
			
			
			if (tempObj.getFolder() != null) {
				FolderTemplateDAO ftdao = new FolderTemplateDAO();
				FolderTemplate fd = new FolderTemplate();

				fd.setFolderId(tempObj.getFolder());
				fd.setTemplateId(response.getIdTemplate());

				ftdao.save(fd);
			}

		}
		
		return response;
	}

	/**
	 * If the id already does not exists yet, the template will be created. If the
	 * id exists, update the title and description by defining metadata in json
	 * format.
	 * 
	 * @param String
	 *            val
	 */
	public TemplateInfoDto updateTemplateId(String id, String val) {
		Gson token = new Gson();
		
		FolderTemplateDAO ftdao = new FolderTemplateDAO();
		TemplateInfoDto tempObj = token.fromJson(val, TemplateInfoDto.class);
		TemplateInfoDto response = ppdTemplateService.updateTemplateId(tempObj.getId(), val);

		if (response != null) {
			response.setIdEventListener(tempObj.getIdEventListener());
			response.setEsign(tempObj.getEsign());
			response.setModule(tempObj.getModule());
			response.setLocale(tempObj.getLocale());
			response.setDocumentType(tempObj.getDocumentType());
			response.setIdTemplate(tempObj.getIdTemplate());
			response.setEmailSign(tempObj.getEmailSign());
			response.setSelfGeneration(tempObj.getSelfGeneration());
			response.setFormatGenerated(tempObj.getFormatGenerated());
			response.setManagerConfirm(tempObj.getManagerConfirm());
			response.setSendDocAutho(tempObj.getSendDocAutho());
			response.setTypeSign(tempObj.getTypeSign());	
			response.setFolder((tempObj.getFolder()!=null&&tempObj.getFolder()==UtilCodesEnum.CODE_INVALID_OR_NONE.getCodeLong())? null : tempObj.getFolder());
			
			SFTemplateFacade.templateUpdate(response);
			
			if(tempObj.getFolder()!=null && tempObj.getFolder() != UtilCodesEnum.CODE_INVALID_OR_NONE.getCodeLong()) 
			{
				ftdao.deleteAllTempsByTempsId(response.getIdTemplate());		
				FolderTemplate fd = new FolderTemplate();

				fd.setFolderId(tempObj.getFolder());
				fd.setTemplateId(response.getIdTemplate());

				ftdao.save(fd);
			}
			else {
				ftdao.deleteAllTempsByTempsId(response.getIdTemplate());				
			}			
		}
		
		return response;
	}

	/**
	 * Insert or update fields associated with a template
	 * 
	 * @param String
	 *            id
	 * @param String
	 *            version
	 */
	public PpdDocGenRequestPayloadDto getFieldsTemplateByVersion(String id, String version) {
		return ppdTemplateService.getFieldsTemplateByVersion(id, version);
	}

	/**
	 * Get preview document
	 * 
	 * @param idTemplatePpdNumber
	 *            num
	 * @param Number
	 *            num
	 */
	public byte[] getPrevDocument(String idTemplatePpd, Number num, Number page) {
		return ppdTemplateService.getPrevDocument(idTemplatePpd, num, page);
	}

	/**
	 * Delete document
	 * 
	 * @param idTemplatePpdNumber
	 *            num
	 */
	public String deleteDocument(String idTemplatePpd) {
		return ppdTemplateService.deleteDocument(idTemplatePpd);
	}
	
	public String getNumberPages(String id, Number num) {
		return ppdTemplateService.getNumberPages(id, num);
	}
	
	public String getGeneratedNumberPages(String id, Number num) {
		return ppdTemplateService.getGeneratedNumberPages(id, num);
	}
}