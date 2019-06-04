package edn.cloud.business.dto.ppd.api;

import java.util.ArrayList;

import edn.cloud.business.dto.integration.SlugItem;

public class PpdDocGenRequestPayloadDto {

	private Long idTemplate;
	private String document_generation_template_id;
	private String output_format;
	private ArrayList<SlugItem> variables;
	
	public PpdDocGenRequestPayloadDto()
	{
		this.variables = new ArrayList<SlugItem>();
	}	
		
	public Long getIdTemplate() {
		return idTemplate;
	}
	public void setIdTemplate(Long idTemplate) {
		this.idTemplate = idTemplate;
	}
	public String getDocument_generation_template_id() {
		return document_generation_template_id;
	}
	public void setDocument_generation_template_id(String identifierPpd) {
		this.document_generation_template_id = identifierPpd;
	}
	public String getOutput_format() {
		return output_format;
	}
	public void setOutput_format(String output_format) {
		this.output_format = output_format;
	}
	public ArrayList<SlugItem> getVariables() {
		return variables;
	}
	public void addVariable(SlugItem slug){
		this.variables.add(slug);
	}
}
