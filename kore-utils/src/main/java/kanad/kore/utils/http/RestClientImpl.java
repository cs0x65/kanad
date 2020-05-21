package kanad.kore.utils.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

public class RestClientImpl implements RestClient {
	//-1 indicates the connection is yet to be established.
	private int responseCode = -1; 
	private URLConnection urlConnection;
	
	@Override
	public String execute(Request request) {
		URL url = null;
		StringBuffer sb = new StringBuffer();
		HttpURLConnection htcon = null;
		BufferedReader is = null;
		
		String urlPath = request.getUrlPath();
		Map<String, String> headersMap = request.getHeaders();
		String requestBody = request.getBody();
		try {
			LogManager.getLogger().info("URL Path Before constructing to URI: " + urlPath);
			
			urlPath = urlPath.replaceAll("http:", "");
			URI uri = new URI("http", urlPath, null);
			url = uri.toURL();
			
			LogManager.getLogger().info("URL path from URI :" + url);
			
			urlConnection = url.openConnection();
			
			urlConnection.setConnectTimeout(60000);
			urlConnection.setReadTimeout(60000);
			if(request.getContentType() != null){
				urlConnection.setRequestProperty("Content-Type", request.getContentType());
			}
			
			LogManager.getLogger().info("Request method : " + request.getMethod());
			LogManager.getLogger().info("Request Body : " + requestBody);
			
			// Set Request Method
			htcon = (HttpURLConnection) urlConnection;
			htcon.setRequestMethod(request.getMethod().name());
			
			// Add headers
			if(request.getHeaders() != null && !request.getHeaders().isEmpty()){
				Iterator<Map.Entry<String, String>> headersItr = headersMap.entrySet().iterator();
				while (headersItr.hasNext()) {
					Map.Entry<String, String> pair = headersItr.next();
					String key = pair.getKey();
					String value = pair.getValue();
					htcon.setRequestProperty(key, value);
				}
			}
			
			// Add request body data for post method
			if (request.getMethod() == HTTPMethod.POST || request.getMethod() == HTTPMethod.PUT) {
				htcon.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(htcon.getOutputStream());
				wr.writeBytes(requestBody);
				wr.flush();
				wr.close();
			}
			
	        responseCode =  htcon.getResponseCode();
	        
	        LogManager.getLogger().info("Response code  : " + responseCode);
	      
			if (responseCode == 200) {
				is = new BufferedReader(new InputStreamReader((htcon.getInputStream())));				
				String inpData = "";
				while ((inpData = is.readLine()) != null) {
					sb.append(inpData);		
				}
			} else if ( (responseCode == 401) || (responseCode == -1)) {
	        	LogManager.getLogger().info("Unauthorized access: "+responseCode);
	        } else if (responseCode == 500){
	        	
	        	BufferedReader es = new BufferedReader(new InputStreamReader((htcon.getErrorStream())));
	        	StringBuffer esb = new StringBuffer();
	        	String errorData = "";
	        	while((errorData = es.readLine()) != null) {
	        		esb.append(errorData);
	        	}
	        	
	        	LogManager.getLogger().error("Error Stream data : " + esb);
	        }
	        
			LogManager.getLogger().info("Response string : " + sb.toString());
			
			
		} catch (IOException e) {
			LogManager.getLogger().error("Exception !", e);
		} catch (URISyntaxException e) {
			LogManager.getLogger().error("Exception !", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LogManager.getLogger().error("Exception !", e);
				}
			}
			if (htcon != null) {
				htcon.disconnect();
			}
		}

		return sb.toString();

	}

	/* (non-Javadoc)
	 * @see RestClient#getStatusCode()
	 */
	@Override
	public int getResponseCode() {
		return responseCode;
	}
}
