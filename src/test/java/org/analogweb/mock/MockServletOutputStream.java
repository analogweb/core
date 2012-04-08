package org.analogweb.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;

/**
 * @author snowgoose
 */
public class MockServletOutputStream extends ServletOutputStream {

    private final ByteArrayOutputStream bytes;

    public MockServletOutputStream() {
        bytes = new ByteArrayOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
        bytes.write(b);
    }

    @Override
    public String toString() {
        return new String(bytes.toByteArray());
    }

    public String toString(String charset) throws UnsupportedEncodingException {
        return new String(bytes.toByteArray(), charset);
    }
}
