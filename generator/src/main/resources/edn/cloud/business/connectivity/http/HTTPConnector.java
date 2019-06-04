package edn.cloud.business.connectivity.http;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.authentication.AuthenticationHeader;
import com.sap.core.connectivity.api.authentication.AuthenticationHeaderProvider;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

import edn.cloud.business.connectivity.http.headers.BasicAuthenticationHeaderProvider;
import edn.cloud.business.connectivity.http.headers.ProxyUserHeaderProvider;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.SimpleHttpResponse;

@SuppressWarnings("nls")
public class HTTPConnector {

	private static final String PATH_SUFFIX = "/";
	private static final String ACCEPT_HEADER = "Accept";
	private static final String GET_METHOD = "GET";
	private static final String PUT_METHOD = "PUT";
	private static final String PATCH_METHOD = "PATCH";
	private static final String POST_METHOD = "POST";
	private static final String DELETE_METHOD = "DELETE";
	private static final String DESTINATION_URL = "URL";
	private static final String attachmentName = "template";
	private static final String crlf = "\r\n";
	private static final String twoHyphens = "--";
	private static final String boundary = "*****";
	private static final String COOKIES_HEADER = "Set-Cookie";

	private static final Logger logger = LoggerFactory.getLogger(HTTPConnector.class);

	private final ProxyUserHeaderProvider proxyUserHeaderProvider = new ProxyUserHeaderProvider();
	private final String destinationName;
	private AuthenticationHeaderProvider localAuthenticationHeaderProvider;
	private ConnectivityConfiguration localConnectivityConfiguration;

	private List<String> cookiesHeader;
	private HTTPResponseValidator responseValidator;

	public HTTPConnector(String destinationName) {
		allowMethods("PATCH");

		this.destinationName = destinationName;
		this.responseValidator = new HTTPResponseValidatorImpl();
	}

	public SimpleHttpResponse executeMetadata() throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, "$metadata");

		HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();

		SimpleHttpResponse httpResponse = executeMetadata(urlConnection, destinationConfiguration.getAllProperties());
		return httpResponse;

	}

	private SimpleHttpResponse executeMetadata(HttpURLConnection connection, Map<String, String> destProp) throws IOException, InvalidResponseException {
		connection.setRequestMethod(GET_METHOD);
		connection.setRequestProperty("charset", "utf-8");

		connection.setRequestProperty("Content-Type", "application/xml");

		String content = "";
		Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> pair = entries.next();
			if (pair.getKey().equals("User")) {
				content += pair.getValue() + ":";
			}
			if (pair.getKey().equals("Password")) {
				content += pair.getValue();
			}
		}
		connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(content.getBytes("UTF-8")));

		int responseCode = connection.getResponseCode();

		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
		httpResponse.setContentType(connection.getContentType());

		httpResponse.setContent(IOUtils.toString(connection.getInputStream()));

		logResponse(httpResponse, "GET MTDT METHOD");
		// validateResponse(httpResponse);
		return httpResponse;
	}

	public Object hasKeyGetValue(String key) throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		Map<String, String> map = destinationConfiguration.getAllProperties();
		Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> pair = entries.next();
			if (pair.getKey().equals(key) || pair.getKey().equals("APP_KEY")) {
				return pair.getValue();
			}
		}
		return "";
	}

	public SimpleHttpResponse getTokenONB(String path) throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, path);
		HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();

		Map<String, String> destProp = destinationConfiguration.getAllProperties();

		String st = "{\"Username\":\"" + destProp.get("User") + "\",\"Password\":\"" + destProp.get("Password") + "\"}";
		String st2 = Base64.getEncoder().encodeToString(st.getBytes(StandardCharsets.UTF_8));
		connection.setRequestProperty("Authorization", "Basic " + st2);
		connection.setRequestMethod(GET_METHOD);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("charset", "utf-8");

		int responseCode = connection.getResponseCode();
		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
		httpResponse.setContentType(connection.getContentType());

		httpResponse.setContent(IOUtils.toString(connection.getInputStream()));

		logResponse(httpResponse, "GET ONB TOKEN METHOD");
		validateResponse(httpResponse);
		return httpResponse;
	}

	public SimpleHttpResponse getTokenONBV2() throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();

		Map<String, String> destProp = destinationConfiguration.getAllProperties();

		String st = "api.ashx/authenticate?AccountName=" + destProp.get("onb_acc") + "&UserName=" + destProp.get("User") + "&Password=" + destProp.get("Password");
		URL requestURL = getRequestURL(destinationConfiguration, st);
		HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();

		connection.setRequestMethod(GET_METHOD);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Accept", "application/json");

		Map<String, List<String>> headerFields = connection.getHeaderFields();
		cookiesHeader = headerFields.get(COOKIES_HEADER);

		// logger.info(cookiesHeader.toString());

		int responseCode = connection.getResponseCode();
		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
		httpResponse.setContentType(connection.getContentType());
		httpResponse.setCookies(cookiesHeader);

		httpResponse.setContent(IOUtils.toString(connection.getInputStream()));

		logResponse(httpResponse, "GET ONB TOKENV2 METHOD");
		validateResponse(httpResponse);
		return httpResponse;
	}

	public SimpleHttpResponse getAttachONB(String path, String token) throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, path);
		HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();

		connection.setRequestProperty("Authorization", token);
		connection.setRequestMethod(GET_METHOD);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("charset", "utf-8");

		int responseCode = connection.getResponseCode();
		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
		httpResponse.setContentType(connection.getContentType());

		httpResponse.setContent(IOUtils.toString(connection.getInputStream()));

		logResponse(httpResponse, "GET ONBV1 METHOD");
		validateResponse(httpResponse);
		return httpResponse;
	}

	public SimpleHttpResponse getAttachONBV2(String path, String token) throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, "api.ashx/doccenter/download");
		HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();

		connection.setRequestMethod(POST_METHOD);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Cookie", String.join("; ", cookiesHeader));
		// connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Cache-Control", "no-cache");
		connection.setDoOutput(true);

		String json = "{\"AdvancedFilters\" : [{\"Conditions\": [{\"Field\": \"dcXrefID\", \"Rule\": \"Equals\",\"Value\": \"" + token + "\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\", \"Value\": \"AMEA Photo\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"BostonScientific-ElectronicSignatureForm\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-de_DE\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-es_MX\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-fr_FR\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-it_IT\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-ja_JP\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-pt_BR\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-zh_CN\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ElectronicSignatureConsentForm\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ElectronicSignatureForm\"}" + "]}],\"Filters\": []}";

		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(json);

		writer.flush();

		int responseCode = connection.getResponseCode();
		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
		httpResponse.setContentType(connection.getContentType());

		Charset CP866 = Charset.forName("CP866");
		httpResponse.setContent(IOUtils.toString(connection.getInputStream(), CP866));

		logResponse(httpResponse, "GET ONBV2 METHOD");
		validateResponse(httpResponse);
		return httpResponse;
	}

	public SimpleHttpResponse getAttachONBV2Key(String path, String token, String key) throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, "api.ashx/doccenter/download");
		HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();

		connection.setRequestMethod(POST_METHOD);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Cookie", String.join("; ", cookiesHeader));
		// connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Cache-Control", "no-cache");
		connection.setDoOutput(true);

		String[] keys = key.split("@@");

		String json = "{\"AdvancedFilters\" : [{\"Conditions\": [{\"Field\": \"" + keys[1] + "\", \"Rule\": \"Equals\",\"Value\": \"" + keys[0] + "\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\", \"Value\": \"AMEA Photo\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"BostonScientific-ElectronicSignatureForm\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-de_DE\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-es_MX\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-fr_FR\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-it_IT\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-ja_JP\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-pt_BR\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ConsolidatedPersonalDataForm-zh_CN\"}," + "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ElectronicSignatureConsentForm\"},"
				+ "{\"Field\": \"dcDocumentCode\",\"Rule\": \"NotEqual\",\"Value\": \"ElectronicSignatureForm\"}" + "]}],\"Filters\": []}";

		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(json);

		writer.flush();

		int responseCode = connection.getResponseCode();
		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
		httpResponse.setContentType(connection.getContentType());

		Charset CP866 = Charset.forName("CP866");
		httpResponse.setContent(IOUtils.toString(connection.getInputStream(), CP866));

		logResponse(httpResponse, "GET ONBV2 METHOD");
		validateResponse(httpResponse);
		return httpResponse;
	}

	public SimpleHttpResponse executeGET(String path, String bearer, Boolean header, String json) throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, path);
		HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
		if (!header) {
			injectAuthenticationHeaders(urlConnection, destinationConfiguration);

		}
		SimpleHttpResponse httpResponse = executeGET(urlConnection, bearer, header, json, destinationConfiguration.getAllProperties());
		return httpResponse;
	}

	public SimpleHttpResponse executeHeader(String path, String bearer, Boolean header, String json) throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, path);
		HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
		if (!header) {
			injectAuthenticationHeaders(urlConnection, destinationConfiguration);

		}
		SimpleHttpResponse httpResponse = executeHead(urlConnection, bearer, header, json, destinationConfiguration.getAllProperties());
		return httpResponse;
	}

	public SimpleHttpResponse executePUT(String path, String token, String json, String bearer, ContentFileInfo input) throws InvalidResponseException, IOException {

		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, path);

		HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
		injectAuthenticationHeaders(urlConnection, destinationConfiguration);
		SimpleHttpResponse httpResponse;
		httpResponse = executePUT(urlConnection, token, json, bearer, destinationConfiguration.getAllProperties(), input);
		return httpResponse;
	}

	public SimpleHttpResponse executePATCH(String path, String token, String json, String bearer, ContentFileInfo input) throws InvalidResponseException, IOException {

		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, path);

		HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
		injectAuthenticationHeaders(urlConnection, destinationConfiguration);
		SimpleHttpResponse httpResponse;
		httpResponse = executePATCH(urlConnection, token, json, bearer, destinationConfiguration.getAllProperties(), input);
		return httpResponse;
	}

	private SimpleHttpResponse executePUT(HttpURLConnection connection, String token, String json, String bearer, Map<String, String> destProp, ContentFileInfo input) throws InvalidResponseException, IOException {
		connection.setRequestMethod(PUT_METHOD);

		Map<String, Object> params = new LinkedHashMap<>();

		if (input == null) {
			if (token.equals("")) {
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("charset", "utf-8");
				connection.setUseCaches(false);
				Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<String, String> pair = entries.next();
					if (!pair.getKey().equals("URL") && !pair.getKey().equals("Name") && !pair.getKey().equals("Password") && !pair.getKey().equals("User")) {
						params.put(pair.getKey(), pair.getValue());
					}
				}

			} else {
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("charset", "utf-8");
				if (bearer.equals("")) {
					Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<String, String> pair = entries.next();
						if (!pair.getKey().equals("URL") && !pair.getKey().equals("Name") && !pair.getKey().equals("Password") && !pair.getKey().equals("User")) {
							params.put(pair.getKey(), pair.getValue());
						}
						if (pair.getKey().equals("X-API-KEY")) {
							connection.setRequestProperty(pair.getKey(), pair.getValue());
						}
					}

				} else {
					connection.setRequestProperty("Authorization", "Bearer " + bearer);
				}
				connection.setUseCaches(false);

			}

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			String urlParameters = postData.toString();
			connection.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			if (json.equals("")) {

				writer.write(urlParameters);
			} else {
				writer.write(json);
			}
			writer.flush();
		} else {
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Bearer " + bearer);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Cache-Control", "no-cache");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			DataOutputStream request = new DataOutputStream(connection.getOutputStream());

			request.writeBytes(twoHyphens + boundary + crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + input.getFileName() + "\"" + crlf);
			request.writeBytes(crlf);
			request.write(input.getFile());
			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
			request.flush();
			request.close();

		}

		int responseCode = connection.getResponseCode();
		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
		httpResponse.setContentType(connection.getContentType());

		if (responseCode == 400 || responseCode == 422) {
			httpResponse = new SimpleHttpResponse(connection.getURL().toString(), 406, connection.getResponseMessage());
			httpResponse.setContentType(connection.getContentType());

			logResponse(httpResponse, "PUT METHOD");
			String error = "";
			if (connection.getErrorStream() != null) {
				error = IOUtils.toString(connection.getErrorStream());
			}
			httpResponse.setErrorMessage(error);
		} else {

			httpResponse.setContent(IOUtils.toString(connection.getInputStream()));
		}

		logResponse(httpResponse, "PUT METHOD");
		validateResponse(httpResponse);
		return httpResponse;
	}

	private SimpleHttpResponse executePATCH(HttpURLConnection connection, String token, String json, String bearer, Map<String, String> destProp, ContentFileInfo input) throws InvalidResponseException, IOException {
		connection.setRequestMethod(PATCH_METHOD);

		Map<String, Object> params = new LinkedHashMap<>();

		if (input == null) {
			if (token.equals("")) {
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("charset", "utf-8");
				connection.setUseCaches(false);
				Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<String, String> pair = entries.next();
					if (!pair.getKey().equals("URL") && !pair.getKey().equals("Name") && !pair.getKey().equals("Password") && !pair.getKey().equals("User")) {
						params.put(pair.getKey(), pair.getValue());
					}
				}

			} else {
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("charset", "utf-8");
				if (bearer.equals("")) {
					Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<String, String> pair = entries.next();
						if (!pair.getKey().equals("URL") && !pair.getKey().equals("Name") && !pair.getKey().equals("Password") && !pair.getKey().equals("User")) {
							params.put(pair.getKey(), pair.getValue());
						}
						if (pair.getKey().equals("X-API-KEY")) {
							connection.setRequestProperty(pair.getKey(), pair.getValue());
						}
					}

				} else {
					connection.setRequestProperty("Authorization", "Bearer " + bearer);
				}
				connection.setUseCaches(false);

			}

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			String urlParameters = postData.toString();
			connection.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			if (json.equals("")) {

				writer.write(urlParameters);
			} else {
				writer.write(json);
			}
			writer.flush();
		} else {
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Bearer " + bearer);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Cache-Control", "no-cache");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			DataOutputStream request = new DataOutputStream(connection.getOutputStream());

			request.writeBytes(twoHyphens + boundary + crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + input.getFileName() + "\"" + crlf);
			request.writeBytes(crlf);
			request.write(input.getFile());
			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
			request.flush();
			request.close();

		}

		int responseCode = connection.getResponseCode();
		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());

		httpResponse.setContentType(connection.getContentType());
		httpResponse.setContent(IOUtils.toString(connection.getInputStream()));
		logResponse(httpResponse, "PATCH METHOD");
		validateResponse(httpResponse);
		return httpResponse;
	}

	private static void allowMethods(String... methods) {
		try {
			Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

			methodsField.setAccessible(true);

			String[] oldMethods = (String[]) methodsField.get(null);
			Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
			methodsSet.addAll(Arrays.asList(methods));
			String[] newMethods = methodsSet.toArray(new String[0]);

			methodsField.set(null/* static field */, newMethods);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	private SimpleHttpResponse executeGET(HttpURLConnection connection, String bearer, Boolean header, String json, Map<String, String> destProp) throws IOException, InvalidResponseException {
		connection.setRequestMethod(GET_METHOD);

		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("charset", "UTF-8");

		if (bearer.equals("")) {
			Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, String> pair = entries.next();

				if (pair.getKey().equals("X-API-KEY")) {
					connection.setRequestProperty(pair.getKey(), pair.getValue());
				}
			}

		} else {
			connection.setRequestProperty("Authorization", "Bearer " + bearer);
		}

		int responseCode = connection.getResponseCode();

		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
		httpResponse.setContentType(connection.getContentType());

		if (header) {
			httpResponse.setInputStream(IOUtils.toByteArray(connection.getInputStream()));
		} else {
			httpResponse.setContent(IOUtils.toString(connection.getInputStream()));
		}

		logResponse(httpResponse, "GET METHOD");
		validateResponse(httpResponse);
		return httpResponse;
	}

	private SimpleHttpResponse executeHead(HttpURLConnection connection, String bearer, Boolean header, String json, Map<String, String> destProp) throws IOException, InvalidResponseException {
		connection.setRequestMethod("HEAD");

		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("charset", "UTF-8");

		if (bearer.equals("")) {
			Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, String> pair = entries.next();

				if (pair.getKey().equals("X-API-KEY")) {
					connection.setRequestProperty(pair.getKey(), pair.getValue());
				}
			}

		} else {
			connection.setRequestProperty("Authorization", "Bearer " + bearer);
		}

		int responseCode = connection.getResponseCode();

		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
		httpResponse.setContentType(connection.getContentType());
		httpResponse.setHeadersFields(connection.getHeaderFields());

		if (header) {
			httpResponse.setInputStream(IOUtils.toByteArray(connection.getInputStream()));
		} else {
			httpResponse.setContent(IOUtils.toString(connection.getInputStream()));
		}

		logResponse(httpResponse, "HEAD METHOD");
		validateResponse(httpResponse);
		return httpResponse;
	}

	public SimpleHttpResponse executePOST(String path, String token, String json, String bearer, ContentFileInfo input) throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, path);
		HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
		injectAuthenticationHeaders(urlConnection, destinationConfiguration);
		SimpleHttpResponse httpResponse;
		httpResponse = executePOST(urlConnection, token, json, bearer, destinationConfiguration.getAllProperties(), input);
		return httpResponse;
	}

	public SimpleHttpResponse executeFile(String path, ContentFileInfo fl, String json, String bearer) throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, path);
		HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
		injectAuthenticationHeaders(urlConnection, destinationConfiguration);
		SimpleHttpResponse httpResponse;
		httpResponse = executeFile(urlConnection, fl, json, destinationConfiguration.getAllProperties(), bearer);
		return httpResponse;
	}

	public String getUrl() throws IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, "");
		return requestURL.toString();
	}

	public SimpleHttpResponse executeDELETE(String path, String token, String json, String bearer) throws InvalidResponseException, IOException {
		DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
		URL requestURL = getRequestURL(destinationConfiguration, path);
		HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
		injectAuthenticationHeaders(urlConnection, destinationConfiguration);
		SimpleHttpResponse httpResponse;
		httpResponse = executeDELETE(urlConnection, token, json, bearer, destinationConfiguration.getAllProperties());
		return httpResponse;
	}

	private SimpleHttpResponse executePOST(HttpURLConnection connection, String token, String json, String bearer, Map<String, String> destProp, ContentFileInfo input) throws InvalidResponseException, IOException {
		connection.setRequestMethod(POST_METHOD);

		Map<String, Object> params = new LinkedHashMap<>();

		if (input == null) {
			if (token.equals("")) {
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("charset", "utf-8");
				connection.setUseCaches(false);
				Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<String, String> pair = entries.next();
					if (!pair.getKey().equals("URL") && !pair.getKey().equals("Name") && !pair.getKey().equals("Password") && !pair.getKey().equals("User")) {
						params.put(pair.getKey(), pair.getValue());
					}
				}

			} else {
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("charset", "utf-8");
				if (bearer.equals("")) {
					Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<String, String> pair = entries.next();
						if (!pair.getKey().equals("URL") && !pair.getKey().equals("Name") && !pair.getKey().equals("Password") && !pair.getKey().equals("User")) {
							params.put(pair.getKey(), pair.getValue());
						}
						if (pair.getKey().equals("X-API-KEY")) {
							connection.setRequestProperty(pair.getKey(), pair.getValue());
						}
					}

				} else {
					connection.setRequestProperty("Authorization", "Bearer " + bearer);
				}
				connection.setUseCaches(false);

			}

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			String urlParameters = postData.toString();
			connection.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			if (json.equals("")) {

				writer.write(urlParameters);
			} else {
				writer.write(json);
			}
			writer.flush();
		} else {
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Bearer " + bearer);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Cache-Control", "no-cache");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			DataOutputStream request = new DataOutputStream(connection.getOutputStream());

			request.writeBytes(twoHyphens + boundary + crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + input.getFileName() + "\"" + crlf);
			request.writeBytes(crlf);
			request.write(input.getFile());
			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
			request.flush();
			request.close();

		}

		int responseCode = connection.getResponseCode();
		SimpleHttpResponse httpResponse = null;
		if (responseCode == 400 || responseCode == 422) {
			httpResponse = new SimpleHttpResponse(connection.getURL().toString(), 406, connection.getResponseMessage());
			httpResponse.setContentType(connection.getContentType());

			logResponse(httpResponse, "POST METHOD");
			String error = "";
			if (connection.getErrorStream() != null) {
				error = IOUtils.toString(connection.getErrorStream());
			}
			httpResponse.setErrorMessage(error);
		} else {
			httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
			httpResponse.setContentType(connection.getContentType());

			logResponse(httpResponse, "POST METHOD");
			httpResponse.setContent(IOUtils.toString(connection.getInputStream()));
		}

		validateResponse(httpResponse);
		return httpResponse;
	}

	private SimpleHttpResponse executeFile(HttpURLConnection connection, ContentFileInfo fl, String json, Map<String, String> destProp, String bearer) throws InvalidResponseException, IOException {
		connection.setRequestMethod(POST_METHOD);
		connection.setUseCaches(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);

		if (bearer.equals("")) {

			Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, String> pair = entries.next();
				if (pair.getKey().equals("X-API-KEY")) {
					connection.setRequestProperty(pair.getKey(), pair.getValue());
				}
			}
		} else {
			connection.setRequestProperty("Authorization", "Bearer " + bearer);
		}
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		DataOutputStream request = new DataOutputStream(connection.getOutputStream());

		request.writeBytes(twoHyphens + boundary + crlf);
		request.writeBytes("Content-Disposition: form-data; name=\"data\"" + crlf);
		request.writeBytes("Content-Type: text/plain; charset=\"UTF-8\"" + crlf);
		request.writeBytes(crlf);
		request.writeBytes(json);
		request.writeBytes(crlf);
		request.flush();

		request.writeBytes(twoHyphens + boundary + crlf);
		request.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fl.getFileName() + "\"" + crlf);
		request.writeBytes(crlf);
		request.write(fl.getFile());
		request.writeBytes(crlf);
		request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
		request.flush();

		request.close();

		int responseCode = connection.getResponseCode();

		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
		logResponse(httpResponse, "FILE METHOD");
		httpResponse.setContentType(connection.getContentType());
		if (responseCode == 400) {

			logResponse(httpResponse, "FILE METHOD");

			String error = IOUtils.toString(connection.getErrorStream());
			if (connection.getErrorStream() != null) {
				error = error != null ? error + " error " + IOUtils.toString(connection.getErrorStream()) : IOUtils.toString(connection.getErrorStream());
			}
			httpResponse.setErrorMessage(error);

		} else {
			httpResponse.setContent(IOUtils.toString(connection.getInputStream()));
		}

		validateResponse(httpResponse);
		return httpResponse;
	}

	private SimpleHttpResponse executeDELETE(HttpURLConnection connection, String token, String json, String bearer, Map<String, String> destProp) throws InvalidResponseException, IOException {
		connection.setRequestMethod(DELETE_METHOD);

		Map<String, Object> params = new LinkedHashMap<>();
		if (token.equals("")) {
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setUseCaches(false);
			Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, String> pair = entries.next();
				if (!pair.getKey().equals("URL") && !pair.getKey().equals("Name") && !pair.getKey().equals("Password") && !pair.getKey().equals("User")) {
					params.put(pair.getKey(), pair.getValue());
				}
			}

		} else {
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("charset", "utf-8");
			if (bearer.equals("")) {
				Iterator<Map.Entry<String, String>> entries = destProp.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<String, String> pair = entries.next();
					if (pair.getKey().equals("X-API-KEY")) {
						connection.setRequestProperty(pair.getKey(), pair.getValue());
					}
				}

			} else {
				connection.setRequestProperty("Authorization", "Bearer " + bearer);
			}
			connection.setUseCaches(false);

		}

		StringBuilder postData = new StringBuilder();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			if (postData.length() != 0)
				postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}
		String urlParameters = postData.toString();
		connection.setDoOutput(true);

		if (!bearer.equals("")) {
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			if (json.equals("")) {
				writer.write(urlParameters);
			} else {
				writer.write(json);
			}
			writer.flush();
		}

		int responseCode = connection.getResponseCode();
		SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage() + " " + responseCode);
		httpResponse.setContentType(connection.getContentType());

		try {
			httpResponse.setContent(IOUtils.toString(connection.getInputStream()));
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		}

		logResponse(httpResponse, "DELETE METHOD");
		validateResponse(httpResponse);
		return httpResponse;
	}

	private void logResponse(SimpleHttpResponse httpResponse, String method) {
		logger.debug(method + " // " + String.valueOf(httpResponse.getResponseCode()) + " // " + httpResponse.getResponseMessage() + " // Response {}", httpResponse.getRequestPath());
		// logger.debug("Response content type from requesting {} is {}",
		// httpResponse.getRequestPath(), httpResponse.getContentType());
		// logger.debug("Response content from requesting {} is {}",
		// httpResponse.getRequestPath(), httpResponse.getContent());
	}

	private void validateResponse(SimpleHttpResponse httpResponse) throws InvalidResponseException {
		if (this.responseValidator != null) {
			this.responseValidator.validateHTTPResponse(httpResponse);
		}
	}

	private void injectAuthenticationHeaders(HttpURLConnection urlConnection, DestinationConfiguration destinationConfiguration) throws IOException {
		urlConnection.addRequestProperty(ACCEPT_HEADER, MediaType.APPLICATION_JSON);
		List<AuthenticationHeader> authenticationHeaders = getAuthenticationHeaders(destinationConfiguration);
		authenticationHeaders.add(this.proxyUserHeaderProvider.createMappingHeader());
		for (AuthenticationHeader authenticationHeader : authenticationHeaders) {
			urlConnection.addRequestProperty(authenticationHeader.getName(), authenticationHeader.getValue());
		}
	}

	private URL getRequestURL(DestinationConfiguration destinationConfiguration, String path) throws IOException {
		String requestBaseURL = destinationConfiguration.getProperty(DESTINATION_URL);
		if (StringUtils.isEmpty(requestBaseURL)) {
			String errorMessage = String.format("Request URL in Destination %s is not configured. Make sure to have the destination configured.", this.destinationName);
			throwConfigurationError(errorMessage);
		}
		if (!requestBaseURL.endsWith(PATH_SUFFIX)) {
			requestBaseURL += PATH_SUFFIX;
		}
		URL baseURL = new URL(requestBaseURL);
		URL fullURL = new URL(baseURL, path);
		return fullURL;
	}

	private void throwConfigurationError(String errorMessage) throws IOException {
		logger.error(errorMessage);
		throw new IOException(errorMessage);
	}

	private synchronized AuthenticationHeaderProvider lookupAuthenticationHeaderProvider() throws IOException {
		try {
			if (this.localAuthenticationHeaderProvider == null) {
				Context ctx = new InitialContext();
				this.localAuthenticationHeaderProvider = (AuthenticationHeaderProvider) ctx.lookup("java:comp/env/authenticationHeaderProvider");
			}
			return this.localAuthenticationHeaderProvider;
		} catch (NamingException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	private List<AuthenticationHeader> getAuthenticationHeaders(DestinationConfiguration destinationConfiguration) throws IOException {
		String authenticationType = destinationConfiguration.getProperty("Authentication");
		List<AuthenticationHeader> authenticationHeaders = new ArrayList<>();
		if ("OAuth2SAMLBearerAssertion".equals(authenticationType)) {
			AuthenticationHeaderProvider headerProvider = lookupAuthenticationHeaderProvider();
			authenticationHeaders.addAll(headerProvider.getOAuth2SAMLBearerAssertionHeaders(destinationConfiguration));
		} else if ("BasicAuthentication".equals(authenticationType)) {
			BasicAuthenticationHeaderProvider headerProvider = new BasicAuthenticationHeaderProvider();
			authenticationHeaders.add(headerProvider.getAuthenticationHeader(destinationConfiguration));
		}
		return authenticationHeaders;
	}

	private DestinationConfiguration lookupDestinationConfiguration() throws IOException {
		ConnectivityConfiguration configuration = lookupConnectivityConfiguration();

		DestinationConfiguration destinationConfiguration = configuration.getConfiguration(this.destinationName);

		if (destinationConfiguration == null) {
			String errorMessage = String.format("Destination %s is not found. Make sure to have the destination configured.", this.destinationName);
			logger.error(errorMessage);
			throw new IOException(errorMessage);
		}
		return destinationConfiguration;
	}

	private synchronized ConnectivityConfiguration lookupConnectivityConfiguration() throws IOException {
		try {
			if (this.localConnectivityConfiguration == null) {
				Context ctx = new InitialContext();
				this.localConnectivityConfiguration = (ConnectivityConfiguration) ctx.lookup("java:comp/env/connectivityConfiguration");
			}
			return this.localConnectivityConfiguration;
		} catch (NamingException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
}
