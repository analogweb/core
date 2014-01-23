package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class IOUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCloseQuierly() throws IOException {
        Closeable closeable = mock(Closeable.class);
        IOUtils.closeQuietly(closeable);
        verify(closeable).close();
    }

    @Test
    public void testCloseQuierlyThrowsException() throws IOException {
        Closeable closeable = mock(Closeable.class);
        doThrow(new IOException()).when(closeable).close();
        IOUtils.closeQuietly(closeable);
    }

    @Test
    public void testCloseQuierlyWithoutResource() throws IOException {
        Closeable closeable = null;
        IOUtils.closeQuietly(closeable);
    }

    @Test
    public void testCopyQuietly() {
        InputStream in = new ByteArrayInputStream("this is test!".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertThat(IOUtils.copyQuietly(in, out), is(13));
        assertThat(new String(out.toByteArray()), is("this is test!"));
    }

    @Test
    public void testCopy() throws Exception {
        InputStream in = new ByteArrayInputStream("this is test!".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertThat(IOUtils.copy(in, out), is(13));
        assertThat(new String(out.toByteArray()), is("this is test!"));
    }

    @Test
    public void testCopyQuietlyOnException() throws Exception {
        InputStream in = new ByteArrayInputStream("this is test!".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream() {

            @Override
            public void flush() throws IOException {
                write(" with exception!".getBytes());
                throw new IOException();
            }
        };
        assertThat(IOUtils.copyQuietly(in, out), is(-1));
        assertThat(new String(out.toByteArray()), is("this is test! with exception!"));
    }

    @Test
    public void testCopyOnException() throws Exception {
        thrown.expect(IOException.class);
        InputStream in = new ByteArrayInputStream("this is test!".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream() {

            @Override
            public void flush() throws IOException {
                write(" with exception!".getBytes());
                throw new IOException("exception thrown!");
            }
        };
        IOUtils.copy(in, out);
    }

    @Test
    public void testToString() throws Exception {
        String expected = "this is test!";
        InputStream in = new ByteArrayInputStream(expected.getBytes());
        String actual = IOUtils.toString(new InputStreamReader(in));
        assertThat(actual, is(expected));
    }
}
