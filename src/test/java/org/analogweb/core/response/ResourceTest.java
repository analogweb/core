package org.analogweb.core.response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.noMoreInteractions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.analogweb.*;
import org.analogweb.core.ApplicationRuntimeException;
import org.analogweb.core.AssertionFailureException;
import org.analogweb.core.DefaultReadableBuffer;
import org.analogweb.core.DefaultWritableBuffer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class ResourceTest {

    private RequestContext context;
    private ResponseContext response;
    private Headers headers;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
        response = mock(ResponseContext.class);
        headers = mock(Headers.class);
    }

    @Test
    public void testDefaultFileResource() throws Exception {
        File file = folder.newFile("text.log");
        writeStringTo(file);
        Resource resource = Resource.as(file).status(204);
        when(response.getResponseHeaders()).thenReturn(headers);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response r = resource.render(context, response);
        r.getEntity().writeInto(DefaultWritableBuffer.writeBuffer(out));
        assertThat(new String(out.toByteArray()), is("this is test log."));
        verify(headers).putValue("Content-Type", "application/octet-stream");
        verify(headers).putValue("Content-Disposition", "attachment; filename=text.log");
    }

    @Test
    public void testDistinctFileResource() throws Exception {
        File file = folder.newFile("text.log");
        writeStringTo(file);
        Resource resource = Resource.as(file).status(204).header("Content-Type", "text/plain");
        when(response.getResponseHeaders()).thenReturn(headers);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response r = resource.render(context, response);
        r.getEntity().writeInto(DefaultWritableBuffer.writeBuffer(out));
        assertThat(new String(out.toByteArray()), is("this is test log."));
        verify(headers).putValue("Content-Type", "text/plain");
        verify(headers).putValue("Content-Disposition", "attachment; filename=text.log");
    }

    @Test
    public void testNotAvairableFileResource() throws Exception {
        thrown.expect(ApplicationRuntimeException.class);
        Resource.as(new File("foo/baa"));
    }

    @Test
    public void testFileInlineResource() throws Exception {
        File file = folder.newFile("text.log");
        writeStringTo(file);
        Resource resource = Resource.as(file).inline();
        when(response.getResponseHeaders()).thenReturn(headers);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response r = resource.render(context, response);
        r.getEntity().writeInto(DefaultWritableBuffer.writeBuffer(out));
        assertThat(new String(out.toByteArray()), is("this is test log."));
        verify(headers).putValue("Content-Type", "application/octet-stream");
        verify(headers).putValue("Content-Disposition", "inline; filename=text.log");
    }

    @Test
    public void testNullFileResource() throws Exception {
        thrown.expect(AssertionFailureException.class);
        Resource.as((File) null);
    }

    @Test
    public void testDefaultFilePathResource() throws Exception {
        File file = folder.newFile("text.log");
        writeStringTo(file);
        Resource resource = Resource.asFilePath(file.getPath());
        when(response.getResponseHeaders()).thenReturn(headers);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response r = resource.render(context, response);
        r.getEntity().writeInto(DefaultWritableBuffer.writeBuffer(out));
        assertThat(new String(out.toByteArray()), is("this is test log."));
        verify(headers).putValue("Content-Type", "application/octet-stream");
        verify(headers).putValue("Content-Disposition", "attachment; filename=text.log");
    }

    @Test
    public void testNullFilePathResource() throws Exception {
        thrown.expect(AssertionFailureException.class);
        Resource.asFilePath((String) null);
    }

    @Test
    public void testDefaultStreamResource() throws Exception {
        File file = folder.newFile("text.log");
        writeStringTo(file);
        Resource resource = Resource.as(DefaultReadableBuffer.readBuffer(new FileInputStream(file)));
        when(response.getResponseHeaders()).thenReturn(headers);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response r = resource.render(context, response);
        r.getEntity().writeInto(DefaultWritableBuffer.writeBuffer(out));
        assertThat(new String(out.toByteArray()), is("this is test log."));
        verify(headers).putValue("Content-Type", "application/octet-stream");
        verify(headers).putValue("Content-Disposition", "attachment");
    }

    @Test
    public void testDefaultStreamResourceWithoutContentDisposition() throws Exception {
        File file = folder.newFile("text.log");
        writeStringTo(file);
        Resource resource = Resource.as(DefaultReadableBuffer.readBuffer(new FileInputStream(file))).withoutContentDisposition();
        when(response.getResponseHeaders()).thenReturn(headers);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response r = resource.render(context, response);
        r.getEntity().writeInto(DefaultWritableBuffer.writeBuffer(out));
        assertThat(new String(out.toByteArray()), is("this is test log."));
        verify(headers).putValue("Content-Type", "application/octet-stream");
        verify(headers,noMoreInteractions()).putValue("Content-Disposition", "attachment");
    }

    @Test
    public void testNullStreamResource() throws Exception {
        thrown.expect(AssertionFailureException.class);
        Resource.as((ReadableBuffer) null);
    }

    private void writeStringTo(File file) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write("this is test log.".getBytes());
        fileOut.flush();
        fileOut.close();
    }
}
