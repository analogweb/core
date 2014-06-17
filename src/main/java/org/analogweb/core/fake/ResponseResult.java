package org.analogweb.core.fake;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;

import org.analogweb.util.Maps;

/**
 * @author snowgooseyk
 */
public class ResponseResult {

	private int status = 200;
	private OutputStream responseBody = new ByteArrayOutputStream();
	private Map<String, String> responseHeader = Maps.newEmptyHashMap();

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
