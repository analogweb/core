package org.analogweb.core;

import org.analogweb.ReadableBuffer;
import org.analogweb.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;

public class InStreamResponseEntity implements ResponseEntity<ReadableBuffer> {

    private ReadableBuffer entity;
    private long length;

    public InStreamResponseEntity(ReadableBuffer in) {
        this.entity = in;
        this.length = in.getLength();
    }

    @Override
    public ReadableBuffer entity() {
        return this.entity;
    }

    @Override
    public long getContentLength() {
        return this.length;
    }
}
