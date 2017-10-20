package org.analogweb;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public interface WritableBuffer {

	WritableBuffer writeBytes(byte[] bytes) throws IOException;
	WritableBuffer writeBytes(byte[] bytes, int index, int length)
			throws IOException;
	WritableBuffer writeBytes(ByteBuffer buffer) throws IOException;
	OutputStream asOutputStream() throws IOException;
	WritableByteChannel asChannel() throws IOException;
	WritableBuffer from(ReadableBuffer readable) throws IOException;
}
