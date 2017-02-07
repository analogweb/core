package org.analogweb;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

public interface ResponseEntity {

    void writeInto(WritableBuffer responseBody) throws IOException;

    long getContentLength();
}
