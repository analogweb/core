package org.analogweb.core;

import java.io.IOException;
import java.nio.charset.Charset;

import org.analogweb.ReadableBuffer;
import org.analogweb.ResponseEntity;
import org.analogweb.WritableBuffer;

/**
 * @author y2k2mt
 */
public class DefaultResponseEntity implements ResponseEntity {

    private final ReadableBuffer entity;
    private long length = Long.MIN_VALUE;

    public DefaultResponseEntity(String entity) {
        this(entity, Charset.defaultCharset());
    }

    public DefaultResponseEntity(String entity, Charset charset) {
        this(DefaultReadableBuffer.readBuffer(entity.getBytes(charset)));
    }

    public DefaultResponseEntity(ReadableBuffer entity) {
        this.entity = entity;
        this.length = entity.getLength();
    }

    protected ReadableBuffer getEntity(){
        return this.entity;
    }

    @Override
    public void writeInto(WritableBuffer responseBody) throws IOException {
        responseBody.from(getEntity());
    }

    @Override
    public long getContentLength() {
        if (this.length < 0) {
                length = -1;
        }
        return length;
    }
}
