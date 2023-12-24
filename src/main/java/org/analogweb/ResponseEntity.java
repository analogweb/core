package org.analogweb;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

public interface ResponseEntity<T> {

    T entity();

    long getContentLength();
}
