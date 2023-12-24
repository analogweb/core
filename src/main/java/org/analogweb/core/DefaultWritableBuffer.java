package org.analogweb.core;

import org.analogweb.ReadableBuffer;
import org.analogweb.WritableBuffer;
import org.analogweb.util.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * @author y2k2mt
 */
public class DefaultWritableBuffer implements WritableBuffer {

    private WritableByteChannel channel;
    private long length;

    DefaultWritableBuffer(WritableByteChannel c, long length) {
        this.channel = c;
        this.length = length;
    }

    public static WritableBuffer writeBuffer(OutputStream out) {
        return writeBuffer(Channels.newChannel(out));
    }

    public static WritableBuffer writeBuffer(WritableByteChannel c) {
        return new DefaultWritableBuffer(c, 0);
    }

    public static WritableBuffer writeBuffer(WritableByteChannel c, long length) {
        return new DefaultWritableBuffer(c, length);
    }

    protected WritableByteChannel getChannel() {
        return this.channel;
    }

    @Override
    public WritableBuffer writeBytes(byte[] bytes) throws IOException {
        return writeBytes(bytes, 0, bytes.length);
    }

    @Override
    public WritableBuffer writeBytes(byte[] bytes, int index, int length) throws IOException {
        getChannel().write(ByteBuffer.wrap(bytes, index, length));
        if (this.length > -1) {
            this.length += length - index;
        }
        return this;
    }

    @Override
    public WritableBuffer writeBytes(ByteBuffer buffer) throws IOException {
        if (this.length > -1) {
            this.length += buffer.remaining();
        }
        getChannel().write(buffer);
        return this;
    }

    @Override
    public OutputStream asOutputStream() throws IOException {
        return Channels.newOutputStream(getChannel());
    }

    @Override
    public WritableByteChannel asChannel() throws IOException {
        return getChannel();
    }

    @Override
    public WritableBuffer from(ReadableBuffer readable) throws IOException {
        IOUtils.copy(readable.asChannel(), asChannel());
        return this;
    }

}
