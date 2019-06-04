package edn.cloud.business.api.util;

public enum UtilCodesApiEnum 
{	
	//Code response http	
	CODE_ERROR_BAD_REQUEST("2207","BabRequest","Bad Request","Bad Request"),
	;
	
	//-------------------------------------------------------	
	private final String code ;
	private final String number;
	private final String label;
	private final String message;
	
	private UtilCodesApiEnum(String code) {
		this.code = code;
		this.label = "";		
		this.number = "";
		this.message = "";
	}

	private UtilCodesApiEnum(String number, String code, String label, String message) {
		this.code = code;
		this.label = label;
		this.number = number;
		this.message = message;
	}

	public String getCode() {
		return code;
	}
	
	public Integer getCodeInt() {
		return (Integer.parseInt(code));
	}	
	
	public Long getCodeLong() {
		return (Long.parseLong(code));
	}

	public String getLabel() {
		if(label!=null && !label.equals(""))
			return label;
		
		return code;
	}

	public String getNumber() {
		return number;
	}

	public String getMessage() {
		return message;
	}
	
	
}
