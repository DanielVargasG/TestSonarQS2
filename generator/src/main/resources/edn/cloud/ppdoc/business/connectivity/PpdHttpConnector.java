package edn.cloud.ppdoc.business.connectivity;

import java.io.IOException;
import edn.cloud.business.connectivity.http.HTTPConnector;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.ContentFileInfo;
@SuppressWarnings("nls")
public class PpdHttpConnector {

	private static final String PPDAPI_DESTINATION_NAME = "ppd";	
	private static PpdHttpConnector INSTANCE = null;
	
	private final HTTPConnector httpConnector;	

	public static synchronized PpdHttpConnector getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PpdHttpConnector();
		}
		return INSTANCE;
	}

	private PpdHttpConnector() {
		this.httpConnector = new HTTPConnector(PPDAPI_DESTINATION_NAME);
	}
	
	public String executePOST(String query) throws InvalidResponseException, IOException {
		return this.httpConnector.executePOST(query, "", "", "", null).getContent();
	}

	public String callPOST(String query, String bearer, String json, ContentFileInfo input) throws InvalidResponseException, IOException {
		return this.httpConnector.executePOST(query, "bearer", json, bearer, input).getContent();
	}

	public String callDELETE(String query, String bearer, String json) throws InvalidResponseException, IOException {
		return this.httpConnector.executeDELETE(query, "bearer", json, bearer).getContent();
	}

	public String callGET(String query, String bearer, Boolean header, String json) throws InvalidResponseException, IOException {
		return this.httpConnector.executeGET(query, bearer, header, json).getContent();
	}
	
	public String callHeader(String query, String bearer, Boolean header, String json, String key) throws InvalidResponseException, IOException {
		return this.httpConnector.executeHeader(query, bearer, header, json).getHeadersField(key);
	}

	public byte[] callImage(String query, String bearer, Boolean header) throws InvalidResponseException, IOException {
		return this.httpConnector.executeGET(query, bearer, header, null).getInputStream();
	}
	
	public String callPUT(String query, String bearer, Boolean header, String json) throws InvalidResponseException, IOException {
		return this.httpConnector.executePUT(query, "bearer", json, bearer, null).getContent();
	}
	
	public String callPATCH(String query, String bearer, Boolean header, String json) throws InvalidResponseException, IOException {
		return this.httpConnector.executePATCH(query, "bearer", json, bearer, null).getContent();
	}
}
