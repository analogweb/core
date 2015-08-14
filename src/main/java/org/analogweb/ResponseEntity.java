package org.analogweb;

import java.io.IOException;
import java.io.OutputStream;

public interface ResponseEntity {

    void writeInto(OutputStream responseBody) throws IOException;

    long getContentLength();
}
