package org.analogweb.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.ResponseContext.ResponseWriter;
import org.analogweb.util.IOUtils;

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
        writeEntity(new ResponseEntity() {
            @Override
            public void writeInto(OutputStream responseBody) throws IOException {
                try {
                    IOUtils.copy(entity, responseBody);
                } finally {
                    IOUtils.closeQuietly(entity);
                }
            }
        });
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