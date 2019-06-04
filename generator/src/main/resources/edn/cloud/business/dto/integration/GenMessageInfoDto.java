package edn.cloud.business.dto.integration;

public class GenMessageInfoDto 
{
	private String code;	
	private SlugItem message;

	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public SlugItem getMessage() {
		return message;
	}

	public void setMessage(SlugItem message) {
		this.message = message;
	}
}
