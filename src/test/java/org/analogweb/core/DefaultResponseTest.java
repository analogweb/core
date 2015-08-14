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
        String expected = "This Is Test Entity.";
        writer.putEntity(expected);
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        writer.getEntity().writeInto(responseBody);
        assertThat(new String(responseBody.toByteArray()), is(expected));
    }

    @Test
    public void testWriteStringEntityWithCharset() throws IOException {
        String expected = "これはテストです。";
        Charset charset = Charset.forName("UTF-8");
        writer.putEntity(expected, charset);
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        writer.getEntity().writeInto(responseBody);
        assertThat(new String(responseBody.toByteArray(), charset), is(expected));
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
        writer.putEntity(entity);
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        writer.getEntity().writeInto(responseBody);
    }
}
