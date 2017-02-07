package org.analogweb.core;

import java.nio.charset.Charset;

import org.analogweb.*;

/**
 * @author snowgoose
 */
public class DefaultResponse implements Response {

    private ResponseEntity entity;

    @Override
    public void putEntity(String entity) {
        putEntity(entity, Charset.defaultCharset());
    }

    @Override
    public void putEntity(String entity, Charset charset) {
        putEntity(DefaultReadableBuffer.readBuffer(entity.getBytes(charset)));
    }

    @Override
    public void putEntity(final ReadableBuffer entity) {
        putEntity(new DefaultResponseEntity(entity));
    }

    @Override
    public void putEntity(ResponseEntity entity) {
        this.entity = entity;
    }

    @Override
    public ResponseEntity getEntity() {
        return entity;
    }

    public long getContentLength() {
        ResponseEntity e = getEntity();
        if (e != null) {
            return e.getContentLength();
        }
        return 0L;
    }

    @Override
    public void commit(RequestContext request, ResponseContext response) {
        try{
            response.commit(request, this);
        } finally {
            response.ensure();
        }
    }
}
