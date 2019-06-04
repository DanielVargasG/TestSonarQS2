package edn.cloud.business.dto.integration;

import java.util.ArrayList;
import java.util.List;

import edn.cloud.business.dto.SignatureGroupDto;
import edn.cloud.business.dto.ppd.api.PpdDocGenRequestPayloadDto;
import edn.cloud.sfactor.persistence.entities.TemplateFilters;

public class TemplateInfoDto extends TemplateInfoBasicDto {
	private String created_at;
	private String updated_at;	
	private Integer latest_version;	
	private Boolean active = false;
	public Boolean catSeeEdit = false; 
	public Boolean catSeeEnter = true; 
	public Boolean catSeeNothing = false;
	public Boolean isFileUpload = false;
	public Boolean isFileWithSign = false;
	private String nameEventListener;
	private PpdDocGenRequestPayloadDto generateVariables = new PpdDocGenRequestPayloadDto();
	private ArrayList<SignatureGroupDto> singatureGroup = new ArrayList<SignatureGroupDto>();
	private ArrayList<TemplateFilters> filters = new ArrayList<TemplateFilters>();
	private List<String> metadataList;
	private String sendDocAutho;
	
	public TemplateInfoDto() {
	}
	
	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	
	public Integer getLatest_version() {
		return latest_version;
	}

	public void setLatest_version(Integer latest_version) {
		this.latest_version = latest_version;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public PpdDocGenRequestPayloadDto getGenerateVariables() {
		return generateVariables;
	}

	public void setGenerateVariables(PpdDocGenRequestPayloadDto generateVariables) {
		this.generateVariables = generateVariables;
	}

	public ArrayList<SignatureGroupDto> getSingatureGroup() {
		return singatureGroup;
	}

	public void setSingatureGroup(ArrayList<SignatureGroupDto> singatureGroupList) {
		this.singatureGroup = singatureGroupList;
	}
	public ArrayList<TemplateFilters> getFilters() {
		return filters;
	}

	public void setFilters(ArrayList<TemplateFilters> filters) {
		this.filters = filters;
	}

	public String getNameEventListener() {
		return nameEventListener;
	}

	public void setNameEventListener(String nameEventListener) {
		this.nameEventListener = nameEventListener;
	}

	public List<String> getMetadataList() {
		return metadataList;
	}

	public void setMetadataList(List<String> metadataList) {
		this.metadataList = metadataList;
	}

	public String getSendDocAutho() {
		return sendDocAutho;
	}

	public void setSendDocAutho(String sendDocAutho) {
		this.sendDocAutho = sendDocAutho;
	}
	
	
}
