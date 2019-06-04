package edn.cloud.business.connectivity.http;

import edn.cloud.business.dto.SimpleHttpResponse;

public interface HTTPResponseValidator {
	
   void validateHTTPResponse(SimpleHttpResponse httpResponse) throws InvalidResponseException;

}
