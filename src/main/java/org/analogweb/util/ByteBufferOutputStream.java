package org.analogweb.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author y2k2mt
 */
public class ByteBufferOutputStream extends OutputStream {
    protected final ByteBuffer buffer;

    public ByteBufferOutputStream(ByteBuffer buf) {
        this.buffer = buf;
    }

    @Override
    public void write(int b) throws IOException {
        this.buffer.put((byte) b);
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        this.buffer.put(bytes, off, len);
    }
}
