package org.analogweb.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.ResponseContext.ResponseWriter;

/**
 * @author snowgoose
 */
public class DefaultResponseWriter implements ResponseWriter {

    private ResponseEntity entity;

    @Override
    public void writeEntity(String entity) {
        writeEntity(entity, Charset.defaultCharset());
    }

    @Override
    public void writeEntity(String entity, Charset charset) {
        writeEntity(new ByteArrayInputStream(entity.getBytes(charset)));
    }

    @Override
    public void writeEntity(final InputStream entity) {
        writeEntity(new DefaultResponseEntity(entity));
    }

    @Override
    public void writeEntity(ResponseEntity entity) {
        this.entity = entity;
    }

    @Override
    public ResponseEntity getEntity() {
        return entity;
    }
}
