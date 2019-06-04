package edn.cloud.sfactor.business.connectivity;


import java.io.IOException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edn.cloud.business.connectivity.http.HTTPConnector;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.SimpleHttpResponse;

@SuppressWarnings("nls")
public class HttpConnectorSuccessFactorOnbV2 {
	private static final Logger logger = LoggerFactory.getLogger(HTTPConnector.class);
	private static final String ONBAPI_DESTINATION_NAME = "sap_hcmcloud_onbv2_odata";
	private static HttpConnectorSuccessFactorOnbV2 INSTANCE = null;
	private final HTTPConnector httpConnector;

	public static synchronized HttpConnectorSuccessFactorOnbV2 getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new HttpConnectorSuccessFactorOnbV2();
		}

		return INSTANCE;
	}

	private HttpConnectorSuccessFactorOnbV2() {
		this.httpConnector = new HTTPConnector(ONBAPI_DESTINATION_NAME);
	}

	/**
	 * 
	 * */
	public String getSessionONB() throws InvalidResponseException, IOException {

		String st = this.httpConnector.getTokenONBV2().getContent();

		JSONObject jsonObj = new JSONObject(st);

		return jsonObj.getString("Status");
	}
	
	/**
	 * 
	 * @param String idDocumentTypeOnb
	 * @param String kmsId
	 * @return SimpleHttpResponse
	 * */
	public SimpleHttpResponse getAttachONBV2(String idDocumentTypeOnb, String kmsId) {
		SimpleHttpResponse st = null;
		try {
			st = this.httpConnector.getAttachONBV2(idDocumentTypeOnb, kmsId);
		} catch (InvalidResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return st;
	}
	
	/**
	 * 
	 * @param String idDocumentTypeOnb
	 * @param String kmsId
	 * @return SimpleHttpResponse
	 * */
	public SimpleHttpResponse getAttachONBV2Key(String idDocumentTypeOnb, String kmsId, String key) {
		SimpleHttpResponse st = null;
		try {
			st = this.httpConnector.getAttachONBV2Key(idDocumentTypeOnb, kmsId, key);
		} catch (InvalidResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return st;
	}
}
