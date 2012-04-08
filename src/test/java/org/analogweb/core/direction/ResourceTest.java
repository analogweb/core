package org.analogweb.core.direction;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;


import org.analogweb.RequestContext;
import org.analogweb.core.direction.Resource;
import org.analogweb.exception.ApplicationRuntimeException;
import org.analogweb.exception.AssertionFailureException;
import org.analogweb.mock.MockServletOutputStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class ResourceTest {

    private RequestContext context;
    private HttpServletResponse response;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testDefaultFileResource() throws Exception {
        File file = folder.newFile("text.log");
        writeStringTo(file);
        Resource resource = Resource.as(file);

        when(context.getResponse()).thenReturn(response);
        MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        resource.render(context);

        assertThat(out.toString(), is("this is test log."));

        verify(response).setContentType("application/octet-stream");
        verify(response).setHeader("Content-Disposition", "attachment; filename=text.log");
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

        when(context.getResponse()).thenReturn(response);
        MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        resource.render(context);

        assertThat(out.toString(), is("this is test log."));

        verify(response).setContentType("application/octet-stream");
        verify(response).setHeader("Content-Disposition", "inline; filename=text.log");
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

        when(context.getResponse()).thenReturn(response);
        MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        resource.render(context);

        assertThat(out.toString(), is("this is test log."));

        verify(response).setContentType("application/octet-stream");
        verify(response).setHeader("Content-Disposition", "attachment; filename=text.log");
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
        Resource resource = Resource.as(new FileInputStream(file));

        when(context.getResponse()).thenReturn(response);
        MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        resource.render(context);

        assertThat(out.toString(), is("this is test log."));

        verify(response).setContentType("application/octet-stream");
        verify(response).setHeader("Content-Disposition", "attachment");
    }

    @Test
    public void testNullStreamResource() throws Exception {
        thrown.expect(AssertionFailureException.class);
        Resource.as((InputStream) null);
    }

    private void writeStringTo(File file) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write("this is test log.".getBytes());
        fileOut.flush();
        fileOut.close();
    }

}
