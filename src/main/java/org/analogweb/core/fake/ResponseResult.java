package org.analogweb.core.fake;

import java.io.OutputStream;
import java.util.Map;

/**
 * @author snowgooseyk
 */
public class ResponseResult {

	private int status;
	private OutputStream responseBody;
	private Map<String, String> responseHeader;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public OutputStream getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(OutputStream responseBody) {
		this.responseBody = responseBody;
	}

	public Map<String, String> getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(Map<String, String> responseHeader) {
		this.responseHeader = responseHeader;
	}

}
