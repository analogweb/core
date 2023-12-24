package org.analogweb.core.response;

import java.util.Map;

import org.analogweb.Renderable;
import org.analogweb.ResponseEntity;

/**
 * @author snowgooseyk
 */
@SuppressWarnings("unchecked")
public class BuildAndRenderableResponse<T extends Renderable> extends DefaultRenderable {

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
