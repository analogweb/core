package org.analogweb.core;

import org.analogweb.ReadableBuffer;
import org.analogweb.WritableBuffer;
import org.analogweb.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * @author y2k2mt
 */
public class DefaultReadableBuffer implements ReadableBuffer{

    private final ReadableByteChannel channel;
    private long length;
    private InputStream stream;

    public static DefaultReadableBuffer readBuffer(byte[] content) {
        return new DefaultReadableBuffer(content,0,content.length);
    }

    public static DefaultReadableBuffer readBuffer(InputStream in) {
        return readBuffer(Channels.newChannel(in));
    }

    public static DefaultReadableBuffer readBuffer(ReadableByteChannel content) {
        return new DefaultReadableBuffer(content);
    }

    DefaultReadableBuffer(byte[] content, int offset, int length) {
        this(Channels.newChannel(new ByteArrayInputStream(content,offset,length)));
        this.length = length - offset;
    }

    DefaultReadableBuffer(ReadableByteChannel content) {
        this.channel = content;
    }

    protected ReadableByteChannel getChannel(){
        return this.channel;
    }

    @Override
    public ReadableBuffer read(byte[] dst, int index, int length) throws IOException {
        return read(ByteBuffer.wrap(dst,index,length));
    }

    @Override
    public ReadableBuffer read(ByteBuffer buffer) throws IOException {
        getChannel().read(buffer);
        return this;
    }

    @Override
    public String asString(Charset charset) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        readFully(buffer);
        buffer.flip();
        byte[] dst = new byte[buffer.remaining()];
        buffer.get(dst);
        return new String(dst,charset);
    }

    @Override
    public String toString() {
        try {
            return asString(Charset.defaultCharset());
        } catch (IOException e) {
            throw new ApplicationRuntimeException(e) {
            };
        }
    }

    private int readFully(ByteBuffer b) throws IOException {
        int total = 0;
        ReadableByteChannel ch = getChannel();
        while (true) {
            int got = ch.read(b);
            if (got < 0) {
                return (total == 0) ? -1 : total;
            }
            total += got;
            if (total == b.capacity() || b.position() == b.capacity()) {
                return total;
            }
        }
    }

    @Override
    public InputStream asInputStream() throws IOException {
        if(stream == null) {
            this.stream = Channels.newInputStream(getChannel());
        }
        return this.stream;
    }

    @Override
    public ReadableByteChannel asChannel() throws IOException{
        return getChannel();
    }

    @Override
    public ReadableBuffer to(WritableBuffer writable) throws IOException {
        IOUtils.copy(asChannel(),writable.asChannel());
        return this;
    }

    @Override
    public long getLength() {
        return this.length;
    }
}
