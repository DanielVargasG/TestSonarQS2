package edn.cloud.business.connectivity.http;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.common.util.StringUtils;
import org.json.JSONObject;

import edn.cloud.business.dto.SimpleHttpResponse;

@SuppressWarnings("nls")
public class HTTPResponseValidatorImpl implements HTTPResponseValidator {

	@Override
	public void validateHTTPResponse(SimpleHttpResponse httpResponse) throws InvalidResponseException {
		validateStatusCode(httpResponse);
		validateContentType(httpResponse);
	}

	private void validateStatusCode(SimpleHttpResponse httpResponse) throws InvalidResponseException {
		final int statusCode = httpResponse.getResponseCode();
		final String statusLine = httpResponse.getResponseMessage();
		if (statusCode == HttpServletResponse.SC_OK || statusCode == HttpServletResponse.SC_CREATED || statusCode == HttpServletResponse.SC_NO_CONTENT) {
			return;
		}

		String errMessage;
		switch (statusCode) {
		case HttpServletResponse.SC_NOT_FOUND:
			errMessage = String.format("Requesting path [%s] was not found.", httpResponse.getRequestPath());
			break;
		case HttpServletResponse.SC_UNAUTHORIZED:
			errMessage = String.format("Missing or incorrect credentials for path [%s].", httpResponse.getRequestPath());
			break;
		case HttpServletResponse.SC_FORBIDDEN:
			errMessage = String.format("Unauthorized request to path [%s].", httpResponse.getRequestPath());
			break;
		case HttpServletResponse.SC_BAD_REQUEST:
			JSONObject jos = new JSONObject(httpResponse.getErrorMessage());
			errMessage = "";
			
			try 
			{
				if(jos.isNull("message")==false) {			
					errMessage = String.format("Error with content : "+ jos.getString("message"));
				}
				else if(jos.isNull("data")==false){				
					errMessage = String.format("Error with content : "+ jos.getString("data"));
				}
			}
			catch (Exception e) {
				errMessage = jos.toString();
			}			
			
			break;
		case HttpServletResponse.SC_NOT_ACCEPTABLE:
			//JSONObject jos2 = new JSONObject(httpResponse.getErrorMessage());
			//JSONObject josR2 = jos2.getJSONObject("errors");
			errMessage = String.format("Error with content : [%s].", httpResponse.getErrorMessage().replace("\"", "'").replace("{", "(").replace("[", "(").replace("}", ")").replace("]", ")"));
			break;
		default:
			errMessage = String.format("Requesting path [%s] returns unexpected response.", httpResponse.getRequestPath());
		}
		errMessage += String.format(" Service returned [%d] [%s].", statusCode, statusLine);

		throw new InvalidResponseException(errMessage);
	}

	private void validateContentType(SimpleHttpResponse httpResponse) throws InvalidResponseException {
		if (StringUtils.isEmpty(httpResponse.getContentType())) {
			throw new InvalidResponseException(String.format("Response content type not found when requesting path [%s]", httpResponse.getRequestPath()));
		}
		if (!httpResponse.getContentType().contains(MediaType.APPLICATION_JSON) && !httpResponse.getContentType().contains("image/png") && !httpResponse.getContentType().contains("application/vnd.openxmlformats-officedocument.wordprocessingml.document") && !httpResponse.getContentType().contains("application/pdf")
				&& !httpResponse.getContentType().contains("text/html") && !httpResponse.getContentType().contains("text/plain") && !httpResponse.getContentType().contains("application/zip")
		// && !httpResponse.getContentType().contains("application/vnd.openxmlformats")
		) {
			throw new InvalidResponseException(String.format("Invalid response content type [%s] when requesting path [%s]", httpResponse.getContentType(), httpResponse.getRequestPath()));
		}
	}

}
