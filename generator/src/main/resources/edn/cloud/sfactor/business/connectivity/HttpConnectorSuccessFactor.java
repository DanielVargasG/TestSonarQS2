package edn.cloud.sfactor.business.connectivity;

import java.io.IOException;

import edn.cloud.business.connectivity.http.HTTPConnector;
import edn.cloud.business.connectivity.http.InvalidResponseException;

@SuppressWarnings("nls")
public class HttpConnectorSuccessFactor 
{
	private static final String ECAPI_DESTINATION_NAME = "sap_hcmcloud_core_odata";
    private static HttpConnectorSuccessFactor INSTANCE = null;
	private final HTTPConnector httpConnector;	

	public static synchronized HttpConnectorSuccessFactor getInstance() 
	{
		if (INSTANCE == null) 
		{
			INSTANCE = new HttpConnectorSuccessFactor();
		}
		
		return INSTANCE;
	}

	private HttpConnectorSuccessFactor() 
	{	    
		this.httpConnector = new HTTPConnector(ECAPI_DESTINATION_NAME);
	}
	
	public String executeGET(String query) throws InvalidResponseException, IOException {
	    return this.httpConnector.executeGET(query, "", false, null).getContent();
	}
	
	public Object hasKeyGetValue(String query) throws InvalidResponseException, IOException {
	    return this.httpConnector.hasKeyGetValue(query);
	}
	
	public String getResult(String query) throws InvalidResponseException, IOException 
	{
		String result = executeGET(query);
		return result;
	}
		 	
 	public String getMetadata() throws InvalidResponseException, IOException {
 		String result = executeMetadata();
 		return result;
 	}
 	
 	private String executeMetadata()  throws InvalidResponseException, IOException {
 		 return this.httpConnector.executeMetadata().getContent();
 	}	
}
