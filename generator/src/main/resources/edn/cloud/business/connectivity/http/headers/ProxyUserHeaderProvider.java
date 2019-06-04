package edn.cloud.business.connectivity.http.headers;

import com.sap.core.connectivity.api.authentication.AuthenticationHeader;

import edn.cloud.business.dto.odata.UserManager;

/**
 * Header provider that adds user mapping header, required when using mock API Endpoint.
 * @author i024072
 *
 */
@SuppressWarnings("nls")
public class ProxyUserHeaderProvider {

	private static final String PROXY_USER_MAPPING_HEADER_NAME = "X-Proxy-User-Mapping";  
	private static final String SF_MANAGED_EMPLOYEE_USER = "";
	private static final char USER_SEPARATOR = '|';

	public AuthenticationHeader createMappingHeader() {
		String headerValue = createMappingValue(SF_MANAGED_EMPLOYEE_USER);
		return new AuthenticationHeaderImpl(PROXY_USER_MAPPING_HEADER_NAME, headerValue);
	}

	private String createMappingValue(String sfUser) {
		StringBuilder valueBuilder = new StringBuilder();
		valueBuilder.append(UserManager.getUserId()).append(USER_SEPARATOR).append(sfUser);
		return valueBuilder.toString();
	}

}
