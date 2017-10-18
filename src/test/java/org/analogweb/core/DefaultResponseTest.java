package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.analogweb.ReadableBuffer;
import org.analogweb.ResponseEntity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DefaultResponseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testWriteStringEntity() throws IOException {
        DefaultResponseEntity expected = new DefaultResponseEntity("This Is Test Entity.");
        DefaultResponse writer = new DefaultResponse(expected);
        assertThat(new String((byte[]) writer.getEntity().entity()), is("This Is Test Entity."));
        assertThat(writer.getContentLength(), is(20L));
    }

    @Test
    public void testWriteStringEntityWithCharset() throws IOException {
        Charset charset = Charset.forName("UTF-8");
        DefaultResponseEntity expected = new DefaultResponseEntity("これはテストです。", charset);
        DefaultResponse writer = new DefaultResponse(expected);
        assertThat(new String(new String((byte[]) writer.getEntity().entity(), charset)), is("これはテストです。"));
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
        ResponseEntity<ReadableBuffer> writer
                = new ReadableBufferResponseEntity(DefaultReadableBuffer.readBuffer(entity));
        writer.entity().to(DefaultWritableBuffer.writeBuffer(new ByteArrayOutputStream()));
    }
}
