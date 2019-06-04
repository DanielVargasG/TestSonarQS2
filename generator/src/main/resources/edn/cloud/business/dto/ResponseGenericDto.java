package edn.cloud.business.dto;

public class ResponseGenericDto {
	private String field;
	private String msg;//version 1 api ppd
	private String message;
	private String code;
	private Boolean flag;
	
	/**
	 * 
	 */
	public ResponseGenericDto() {
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
}