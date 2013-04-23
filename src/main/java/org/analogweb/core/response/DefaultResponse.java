package org.analogweb.core.response;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.Response;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.ResponseContext.ResponseWriter;
import org.analogweb.WebApplicationException;
import org.analogweb.util.Maps;

/**
 * @author snowgoose
 */
public class DefaultResponse implements Response {
	
	private Map<String, String> header = Maps.newEmptyHashMap();
	private HttpStatus status = HttpStatus.OK;
	private ResponseEntity entity;
	
	@Override
	public void render(RequestContext request, ResponseContext response)
			throws IOException, WebApplicationException {
		ResponseEntity entity = getResponseEntity();
		if(entity == null){
			entity = extractResponseEntity(request,response);
			if(entity == null){
				setStatus(HttpStatus.NO_CONTENT);
			}
		}
		writeEntityToResponse(response.getResponseWriter(), entity);
		mergeHeaders(request,response,getHeaders(),entity);
		updateStatusToResponse(response, getStatus());
	}

	protected void writeEntityToResponse(ResponseWriter writer, ResponseEntity entity) {
		writer.writeEntity(entity);
	}

	protected void updateStatusToResponse(ResponseContext response, HttpStatus status) {
		response.setStatus(status.getStatusCode());
	}

	protected void mergeHeaders(RequestContext request,ResponseContext response,
			Map<String, String> headers, ResponseEntity entity) {
		Headers responseHeader = response.getResponseHeaders();
		for (Entry<String, String> entry : headers.entrySet()) {
			responseHeader.putValue(entry.getKey(), entry.getValue());
		}
	}

	protected final void setStatus(int status) {
		setStatus(HttpStatus.valueOf(status));
	}

	protected final void setStatus(HttpStatus status) {
		this.status = status;
	}
	
	protected final void addHeader(String attribute,String value){
		this.header.put(attribute, value);
	}
	
	protected final void addHeaders(Map<String,String> headers){
		this.header.putAll(headers);
	}

	protected final void setResponseEntity(ResponseEntity entity){
		this.entity = entity;
	}

	protected final Map<String, String> getHeaders() {
		return header;
	}

	protected final HttpStatus getStatus() {
		return status;
	}
	
	protected final ResponseEntity getResponseEntity(){
		return this.entity;
	}

	protected ResponseEntity extractResponseEntity(RequestContext request,ResponseContext response) {
		return null;
	}

}
