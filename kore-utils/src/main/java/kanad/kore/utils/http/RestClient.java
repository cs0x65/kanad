package kanad.kore.utils.http;

public interface RestClient {
	/**
	 * 
	 * @return the HTTP status code received in the response.  
	 */
	public int getResponseCode();
	
	//TODO: sending back response as a String is a bad idea. It should be a byte[] rather.
	//Let the consumer of RestClient convert it to String, Image etc. based on relevant use case.
	/**
	 * 
	 * @param request the HTTP request to execute
	 * @return the response body received as a result of executing the given HTTP request.
	 */
	public String execute(Request request);
	
	//TODO: it would be good to have a execute variant which returns the InputStream of UrlConnection
	//which can be used by the consumer of RestClient to do appropriate work.
}
