package org.analogweb;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

public interface ReadableBuffer {

	ReadableBuffer read(byte[] dst, int index, int length) throws IOException;
	ReadableBuffer read(ByteBuffer buffer) throws IOException;
	String asString(Charset charset) throws IOException;
	InputStream asInputStream() throws IOException;
	ReadableByteChannel asChannel() throws IOException;
	long getLength();
}
