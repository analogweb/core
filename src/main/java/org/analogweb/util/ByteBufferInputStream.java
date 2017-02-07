package org.analogweb.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author y2k2mt.
 */
public class ByteBufferInputStream extends InputStream {
    protected final ByteBuffer buffer;

    public ByteBufferInputStream(ByteBuffer buf) { buffer = buf; }

    @Override public int available() { return buffer.remaining(); }

    @Override
    public int read() throws IOException { return buffer.hasRemaining() ? (buffer.get() & 0xFF) : -1; }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        if (!buffer.hasRemaining()) return -1;
        len = Math.min(len, buffer.remaining());
        buffer.get(bytes, off, len);
        return len;
    }
}