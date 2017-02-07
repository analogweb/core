package org.analogweb.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import org.analogweb.ReadableBuffer;
import org.analogweb.ResponseEntity;
import org.analogweb.WritableBuffer;

/**
 * @author snowgoose
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
        ReadableBuffer entity = getEntity();
        ByteBuffer buffer;
        if(entity.getLength() > 0) {
            buffer = ByteBuffer.allocate((int)entity.getLength());
        } else {
            buffer = ByteBuffer.allocate(8192);
        }
        int read;
        int readLength = 0;
        ReadableByteChannel readable = entity.asChannel();
        WritableByteChannel writable = responseBody.asChannel();
        while((read = readable.read(buffer)) > 0) {
            readLength += read;
            buffer.flip();
            writable.write(buffer);
            buffer.clear();
        }
        if(readLength > this.length) {
            this.length = readLength;
        }
    }

    @Override
    public long getContentLength() {
        if (this.length < 0) {
                length = -1;
        }
        return length;
    }
}
