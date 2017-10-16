package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DefaultResponseTest {

    private DefaultResponse writer;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        this.writer = new DefaultResponse();
    }

    @Test
    public void testWriteStringEntity() throws IOException {
        DefaultResponseEntity expected = new DefaultResponseEntity("This Is Test Entity.");
        writer.putEntity(expected);
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        writer.getEntity().writeInto(DefaultWritableBuffer.writeBuffer(responseBody));
        assertThat(new String(responseBody.toByteArray()), is("This Is Test Entity."));
    }

    @Test
    public void testWriteStringEntityWithCharset() throws IOException {
        Charset charset = Charset.forName("UTF-8");
        DefaultResponseEntity expected = new DefaultResponseEntity("これはテストです。",charset);
        writer.putEntity(expected);
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        writer.getEntity().writeInto(DefaultWritableBuffer.writeBuffer(responseBody));
        assertThat(new String(responseBody.toByteArray(), charset), is("これはテストです。"));
    }

    @Test
    public void testWriteStringEntityFailed() throws IOException {
        thrown.expect(IOException.class);
        InputStream entity = new InputStream() {

            @Override
            public int read() throws IOException {
                throw new IOException();
            }
        };
        writer.putEntity(new DefaultResponseEntity(DefaultReadableBuffer.readBuffer(entity)));
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        writer.getEntity().writeInto(DefaultWritableBuffer.writeBuffer(responseBody));
    }
}
