package org.analogweb.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.analogweb.ResponseContext.Response;
import org.analogweb.ResponseContext.ResponseEntity;

/**
 * @author snowgoose
 */
public class DefaultResponseWriter implements Response {

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
}
