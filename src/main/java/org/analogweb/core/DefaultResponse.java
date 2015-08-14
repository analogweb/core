package org.analogweb.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.analogweb.RequestContext;
import org.analogweb.Response;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseEntity;

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
        putEntity(new ByteArrayInputStream(entity.getBytes(charset)));
    }

    @Override
    public void putEntity(final InputStream entity) {
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
        ResponseEntity e = this.entity;
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