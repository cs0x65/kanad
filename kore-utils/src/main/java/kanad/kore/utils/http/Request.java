package kanad.kore.utils.http;

import java.util.Map;

public class Request {
	
	private String urlPath;
	private Map<String, String> headers;
	private HTTPMethod method = HTTPMethod.GET;	// default is GET.
	private String  body;
	private String contentType = "application/json";	// default is json.
	
	public String getUrlPath() {
		return urlPath;
	}
	
	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public HTTPMethod getMethod() {
		return method;
	}
	
	public void setMethod(HTTPMethod method) {
		this.method = method;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	
	public static class RequestBuilder {
		
		private String urlPath;
		private Map<String, String> headers;
		private HTTPMethod method = HTTPMethod.GET;	// default is GET.
		private String  body;
		private String contentType = "application/json";	// default is json.
		
		
		public static RequestBuilder newInstance() {
			return new RequestBuilder();
		}
		/**
		 * @return the urlPath
		 */
		public String getUrlPath() {
			return urlPath;
		}
		/**
		 * @param urlPath the urlPath to set
		 */
		public RequestBuilder setUrlPath(String urlPath) {
			this.urlPath = urlPath;
			return this;
		}
		/**
		 * @return the headers
		 */
		public Map<String, String> getHeaders() {
			return headers;
		}
		/**
		 * @param headers the headers to set
		 */
		public RequestBuilder setHeaders(Map<String, String> headers) {
			this.headers = headers;
			return this;
		}
		/**
		 * @return the method
		 */
		public HTTPMethod getMethod() {
			return method;
		}
		/**
		 * @param method the method to set
		 */
		public RequestBuilder setMethod(HTTPMethod method) {
			this.method = method;
			return this;
		}
		/**
		 * @return the body
		 */
		public String getBody() {
			return body;
		}
		/**
		 * @param body the body to set
		 */
		public RequestBuilder setBody(String body) {
			this.body = body;
			return this;
		}
		/**
		 * @return the contentType
		 */
		public String getContentType() {
			return contentType;
		}
		/**
		 * @param contentType the contentType to set
		 */
		public RequestBuilder setContentType(String contentType) {
			this.contentType = contentType;
			return this;
		}
		
		public Request build() {
			Request request = new Request();
			request.setBody(body);
			request.setContentType(contentType);
			request.setHeaders(headers);
			request.setMethod(method);
			request.setUrlPath(urlPath);
			return request;
		}
		
	}
	
}
