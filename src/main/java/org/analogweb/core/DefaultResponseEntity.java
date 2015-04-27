package org.analogweb.core;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.util.CountingOutputStream;
import org.analogweb.util.IOUtils;

/**
 * @author snowgoose
 */
public class DefaultResponseEntity implements ResponseEntity {

    private final InputStream entity;
    private long length = Long.MIN_VALUE;

    public DefaultResponseEntity(String entity) {
        this(entity, Charset.defaultCharset());
    }

    public DefaultResponseEntity(String entity, Charset charset) {
        this(new ByteArrayInputStream(entity.getBytes(charset)));
    }

    public DefaultResponseEntity(InputStream entity) {
        this.entity = entity;
    }

    @Override
    public void writeInto(OutputStream responseBody) throws IOException {
        try {
            CountingOutputStream c = new CountingOutputStream(responseBody);
            IOUtils.copy(entity, c);
            this.length = c.getCount();
        } finally {
            IOUtils.closeQuietly(entity);
        }
    }

    @Override
    public long getContentLength() {
        if (this.length == Long.MIN_VALUE) {
            if (ByteArrayInputStream.class.isInstance(entity)
                    || FileInputStream.class.isInstance(entity)) {
                length = IOUtils.avairable(entity);
            } else {
                length = -1;
            }
        }
        return length;
    }
}
