package edn.cloud.business.dto.integration;

import java.io.Serializable;

public class SlugItem implements Serializable
{
	private String id;
	private String idAux;
	private String code;
	private String slug;
	private Object value;
	private String label;	
	private String path;
	private Boolean flag;
	private Boolean flagAux;
	private String convertValueTo;
	
	public SlugItem() {
		this.value = "";
	}
	public SlugItem(String slug, Object value) {
		this.slug = slug;
		this.value = value;
	}
		
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public Object getValue() {
		return value;
	}
	public String getValueToStr() 
	{
		if(value!=null)
		{
			return value.toString();
		}
		
		return null;
	}	
	public void setValue(Object value) {
		this.value = value;
	}		
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Boolean getFlag() {
		return flag;
	}
	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Boolean getFlagAux() {
		return flagAux;
	}
	public void setFlagAux(Boolean flagAux) {
		this.flagAux = flagAux;
	}
	@Override
	public String toString() {
		return "SlugItem [id=" + id + ", code=" + code + ", slug=" + slug + ", value=" + value + ", label=" + label
				+ ", path=" + path + ", flag=" + flag + ", flagAux=" + flagAux + "]";
	}
	public String getIdAux() {
		return idAux;
	}
	public void setIdAux(String idAux) {
		this.idAux = idAux;
	}
	public String getConvertValueTo() {
		return convertValueTo;
	}
	public void setConvertValueTo(String convertValueTo) {
		this.convertValueTo = convertValueTo;
	}	
}
