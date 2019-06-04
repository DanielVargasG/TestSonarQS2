package edn.cloud.business.dto;

public class GenErrorInfoDto 
{
	private String id;
	private String field;
	private String msg;
	private String message;
	private String code;
	private Boolean flag;
	private Boolean flagAux;
	
	/**
	 * 
	 */
	public GenErrorInfoDto() {
		super();
		this.message ="";
		this.msg = "";
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getMessage() {
		if(this.msg!=null){
			return msg+" "+message;
		}
		return message+"";
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() 
	{
		if(this.message!=null){
			return msg+" "+message;
		}		
		return msg+"";
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Boolean getFlag() {
		return flag;
	}
	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Boolean getFlagAux() {
		return flagAux;
	}
	public void setFlagAux(Boolean flagAux) {
		this.flagAux = flagAux;
	}	
}
