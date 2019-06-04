package edn.cloud.ppdoc.business.interfaces;

import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.integration.GenResponseInfoDto;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.sfactor.persistence.entities.Folder;

public interface PpdTemplate 
{
	/**
	 * Delete document
	 * @param idTemplatePpdNumber num
	 * */
	public String deleteDocument(String idTemplatePpd);

	/***
	 * If the id already does not exists yet, the template will be created. If the id exists, update the title and description by defining metadata in json format.
	 * @param String val 
	 * {
		  "title": "string",
		  "description": "string",
		  "locale": "string",
		  "format": "DOCX",
		  "enabled": true,
		  "validity_start_date": "2017-08-14T19:38:20.717+00:00",
		  "validity_end_date": "2017-08-14T19:38:20.717+00:00",
		  "active_version": 0,
		  "created_at": "2017-08-14T19:38:20.717+00:00",
		  "updated_at": "2017-08-14T19:38:20.717+00:00",
		  "id": "string",
		  "latest_version": 0
		}
	 * */
	public TemplateInfoDto  createTemplateId(String val);
	
	public Folder  createFolderId(String val);
	
	public TemplateInfoDto  updateTemplateId(String id, String val);
	
	
	/**
	 * List all placeholder fields in an existing template document for a given version.
	 * @param String id
	 * @param Integer version
	 * */
	public PpdDocGenRequestPayloadDto getFieldsTemplateByVersion(String id, String version) ;
	
	/**
	 *  Generate a document from a template in people doc platform
	 * */
	public GenResponseInfoDto createTemplateDocument(String id,ContentFileInfo file);	

	/**
	 * get info template
	 * @param String id
	 * */
	public TemplateInfoDto getInfoTemplate(String id) ;
	
	/**
	 * Get preview document
	 * @param idTemplatePpdNumber num
	 * @param Number num
	 * */
	public byte[] getPrevDocument(String idTemplatePpd, Number num, Number page);
	
	/**
	 * Get info document
	 * @param idTemplatePpd Number num
	 * @param Number num
	 * */
	public String getNumberPages(String id, Number num);
	
	public String getGeneratedNumberPages(String id, Number num);
	
}