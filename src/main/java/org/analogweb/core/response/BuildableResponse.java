package org.analogweb.core.response;

import java.util.Map;

import org.analogweb.Renderable;
import org.analogweb.ResponseContext.ResponseEntity;

/**
 * @author snowgooseyk
 */
@SuppressWarnings("unchecked")
public abstract class BuildableResponse<T extends Renderable> extends DefaultResponse {

    public T status(int st) {
        super.setStatus(st);
        return (T) this;
    }

    public T status(HttpStatus status) {
        super.setStatus(status);
        return (T) this;
    }

    public T header(String attribute, String value) {
        super.addHeader(attribute, value);
        return (T) this;
    }

    public T header(Map<String, String> headers) {
        super.addHeaders(headers);
        return (T) this;
    }

    public T entity(ResponseEntity entity) {
        super.setResponseEntity(entity);
        return (T) this;
    }

}
