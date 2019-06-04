package edn.cloud.business.dto;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


public class SimpleHttpResponse {

	private final String requestPath;
	private final int responseCode;
	private final String responseMessage;
	private String errorMessage;
	private String contentType;
	private String content;
	private List<String> cookies;
	private InputStream is;
	private byte[] inputStream;
	private Map<String,List<String>> headersFields;

	public SimpleHttpResponse(String requestPath, int responseCode, String responseMessage) {
		this.requestPath = requestPath;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public byte[] getInputStream() {
		return inputStream;
	}

	public void setInputStream(byte[] inputStream) {
		this.inputStream = inputStream;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Map<String,List<String>> getHeadersFields() {
		return headersFields;
	}
	
	public String getHeadersField(String key) {
		return this.getHeadersFields().get(key).toString();
	}

	public void setHeadersFields(Map<String, List<String>> map) {
		this.headersFields = map;
	}

	public List<String> getCookies() {
		return cookies;
	}

	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}

	public InputStream getIs() {
		return is;
	}

	public void setIs(InputStream is) {
		this.is = is;
	}

}
