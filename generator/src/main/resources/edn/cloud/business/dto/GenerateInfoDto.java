package edn.cloud.business.dto;

import java.util.ArrayList;
import java.util.List;

import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.integration.SFRecUser;
import edn.cloud.business.dto.integration.SFUserDto;
import edn.cloud.business.dto.integration.TemplateInfoDto;
import edn.cloud.sfactor.persistence.entities.Document;
import edn.cloud.sfactor.persistence.entities.Generated;
import edn.cloud.sfactor.persistence.entities.SignatureFileControl;

public class GenerateInfoDto {

	private Document document;
	private SFUserDto user;
	private SFRecUser recuser;
	private TemplateInfoDto docInfo;
	private ArrayList<SignatureFileControl> singsList;
	private List<Generated> generated;	

	public GenerateInfoDto() {

	}

	public Document getDocument() {
		return document;
	}


	public void setDocument(Document document) {
		this.document = document;
	}


	public SFUserDto getUser() {
		return user;
	}


	public void setUser(SFUserDto user) {
		this.user = user;
	}


	public TemplateInfoDto getDocInfo() {
		return docInfo;
	}


	public void setDocInfo(TemplateInfoDto docInfo) {
		this.docInfo = docInfo;
	}


	public List<Generated> getGenerated() {
		return generated;
	}


	public void setGenerated(List<Generated> generated) {
		this.generated = generated;
	}


	public SFRecUser getRecuser() {
		return recuser;
	}


	public void setRecuser(SFRecUser recuser) {
		this.recuser = recuser;
	}


	public ArrayList<SignatureFileControl> getSingsList() {
		return singsList;
	}


	public void setSingsList(ArrayList<SignatureFileControl> singsList) {
		this.singsList = singsList;
	}
	
	public String getHashDownloadDoc() throws Exception 
	{
		if(generated!=null && generated.size()>0)
		{
			return UtilMapping.getHashCodeDownloadDoc(generated.get(generated.size()-1).getGeneratedIdPpd());
		}
		
		return "";
	}
}