package org.analogweb.core.response;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.Renderable;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.Response;
import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.WebApplicationException;
import org.analogweb.core.DefaultResponseWriter;
import org.analogweb.util.Maps;

/**
 * @author snowgoose
 */
public class DefaultResponse implements Renderable {

    private Map<String, String> header = Maps.newEmptyHashMap();
    private HttpStatus status;
    private ResponseEntity entity;

    @Override
    public Response render(RequestContext request, ResponseContext responseContext)
            throws IOException, WebApplicationException {
        ResponseEntity entity = getResponseEntity();
        HttpStatus defaultStatus = HttpStatus.OK;
        if (entity == null) {
            entity = extractResponseEntity(request, responseContext);
            if (entity == null) {
                defaultStatus = HttpStatus.NO_CONTENT;
            }
        }
        Response response = createResponse();
        if (entity != null) {
            putEntityToResponse(response, entity);
        }
        mergeHeaders(request, responseContext, getHeaders(), entity);
        updateStatusToResponse(responseContext, getStatus() == null ? defaultStatus : getStatus());
        return response;
    }

    protected Response createResponse() {
        return new DefaultResponseWriter();
    }

    protected void putEntityToResponse(Response response, ResponseEntity entity) {
        response.putEntity(entity);
    }

    protected void updateStatusToResponse(ResponseContext response, HttpStatus status) {
        response.setStatus(status.getStatusCode());
    }

    protected void mergeHeaders(RequestContext request, ResponseContext response,
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

    protected final void addHeader(String attribute, String value) {
        this.header.put(attribute, value);
    }

    protected final void addHeaders(Map<String, String> headers) {
        this.header.putAll(headers);
    }

    protected final void setResponseEntity(ResponseEntity entity) {
        this.entity = entity;
    }

    protected final Map<String, String> getHeaders() {
        return header;
    }

    protected final HttpStatus getStatus() {
        return status;
    }

    protected final ResponseEntity getResponseEntity() {
        return this.entity;
    }

    protected ResponseEntity extractResponseEntity(RequestContext request, ResponseContext response) {
        return null;
    }
}
