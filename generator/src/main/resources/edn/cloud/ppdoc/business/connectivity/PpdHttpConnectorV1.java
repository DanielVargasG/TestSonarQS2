package edn.cloud.ppdoc.business.connectivity;

import java.io.IOException;

import edn.cloud.business.connectivity.http.HTTPConnector;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.ContentFileInfo;

@SuppressWarnings("nls")
public class PpdHttpConnectorV1 {

	
	private static final String PPDAPI_DESTINATION_NAME2 = "ppd2";
	private static PpdHttpConnectorV1 INSTANCE = null;
	private final HTTPConnector httpConnector;
	
	
	public static synchronized PpdHttpConnectorV1 getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PpdHttpConnectorV1();
		}
		return INSTANCE;
	}
	
	private PpdHttpConnectorV1() {
		this.httpConnector = new HTTPConnector(PPDAPI_DESTINATION_NAME2);
	}

	public String executeFile(String query, ContentFileInfo fl, String json) throws InvalidResponseException, IOException {
	    return this.httpConnector.executeFile(query, fl, json, "").getContent();
	}
	
	public String executePOST(String query, String token, String json) throws InvalidResponseException, IOException {
	    return this.httpConnector.executePOST(query, token, json, "", null).getContent();
	}
	
	public String executeGET(String query, String token, String json) throws InvalidResponseException, IOException {
	    return this.httpConnector.executeGET(query, token, false, "").getContent();
	}
	
	public String callDELETE(String query, String token, String json) throws InvalidResponseException, IOException {
		return this.httpConnector.executeDELETE(query, "token", json, "").getContent(); 
	}
	
	public String getUrl() throws IOException {
		return this.httpConnector.getUrl(); 
	}
}
