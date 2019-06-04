package edn.cloud.sfactor.business.connectivity;

import java.io.IOException;

import org.json.JSONObject;

import edn.cloud.business.connectivity.http.HTTPConnector;
import edn.cloud.business.connectivity.http.InvalidResponseException;

@SuppressWarnings("nls")
public class HttpConnectorSuccessFactorOnb {
	private static final String ONBAPI_DESTINATION_NAME = "sap_hcmcloud_onb_odata";
	private static HttpConnectorSuccessFactorOnb INSTANCE = null;
	private final HTTPConnector httpConnector;

	public static synchronized HttpConnectorSuccessFactorOnb getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new HttpConnectorSuccessFactorOnb();
		}

		return INSTANCE;
	}

	private HttpConnectorSuccessFactorOnb() {
		this.httpConnector = new HTTPConnector(ONBAPI_DESTINATION_NAME);
	}

	public String getTokenONB(String query) throws InvalidResponseException, IOException {

		String st = this.httpConnector.getTokenONB(query).getContent();

		JSONObject jsonObj = new JSONObject(st);
		return jsonObj.getString("Token");
	}

	public JSONObject getContentONB(String query, String token) throws InvalidResponseException, IOException {
		String st = this.httpConnector.getAttachONB(query, token).getContent();

		return new JSONObject(st);
	}

}
