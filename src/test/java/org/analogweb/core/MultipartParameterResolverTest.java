package org.analogweb.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.analogweb.InvocationMetadata;
import org.analogweb.Multipart;
import org.analogweb.RequestContext;
import org.analogweb.util.IOUtils;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class MultipartParameterResolverTest {

    private MultipartParameterResolver resolver = new MultipartParameterResolver();
    private String content = "-----------------------------287032381131322" + "\r\n"
            + "Content-Disposition: form-data; name=\"datafile1\"; filename=\"r.gif\"" + "\r\n"
            + "Content-Type: image/gif" + "\r\n" + "" + "\r\n"
            + "GIF87a.............,...........D..;" + "\r\n"
            + "-----------------------------287032381131322" + "\r\n"
            + "Content-Disposition: form-data; name=\"datafile2\"; filename=\"g.gif\"" + "\r\n"
            + "Content-Type: image/gif" + "\r\n" + "" + "\r\n"
            + "GIF87a.............,...........D..;" + "\r\n"
            + "-----------------------------287032381131322" + "\r\n"
            + "Content-Disposition: form-data; name=\"datafile2\"; filename=\"f.gif\"" + "\r\n"
            + "Content-Type: image/gif" + "\r\n" + "" + "\r\n"
            + "GIF87a.............,...........D..;" + "\r\n"
            + "-----------------------------287032381131322" + "\r\n"
            + "Content-Disposition: form-data; name=\"field1\"" + "\r\n" + "" + "\r\n" + "$field2"
            + "\r\n" + "-----------------------------287032381131322" + "\r\n"
            + "Content-Disposition: form-data; name=\"field2\"" + "\r\n" + "" + "\r\n" + "$field3"
            + "\r\n" + "-----------------------------287032381131322" + "\r\n"
            + "Content-Disposition: form-data; name=\"field2\"" + "\r\n"
            + "Content-Type: text/plain;charset=utf-8" + "\r\n" + "" + "\r\n" + "ああああ" + "\r\n"
            + "-----------------------------287032381131322" + "\r\n"
            + "Content-Disposition: form-data; name=\"datafile3\"; filename=\"b.gif\"" + "\r\n"
            + "Content-Type: image/gif" + "\r\n" + "Content-Transfer-Encoding: binary" + "\r\n"
            + "" + "\r\n" + "GIF87a.............,...........D..;" + "\r\n"
            + "-----------------------------287032381131322--" + "\r\n";

    @Test
    public void testGetBinary() throws Exception {
        RequestContext request = mock(RequestContext.class);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        InputStream body = new ByteArrayInputStream(content.getBytes());
        when(request.getRequestBody()).thenReturn(body);
        when(request.getContentType())
                .thenReturn(
                        MediaTypes
                                .valueOf("multipart/form-data; boundary=---------------------------287032381131322"));
        File actual = (File) resolver
                .resolveValue(request, metadata, "datafile3", File.class, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(new FileInputStream(actual), out);
        assertThat(out.toByteArray().length, is(35));
    }

    @Test
    public void testGetField() throws Exception {
        RequestContext request = mock(RequestContext.class);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        InputStream body = new ByteArrayInputStream(content.getBytes());
        when(request.getRequestBody()).thenReturn(body);
        when(request.getContentType())
                .thenReturn(
                        MediaTypes
                                .valueOf("multipart/form-data; boundary=---------------------------287032381131322"));
        Multipart actual = (Multipart) resolver.resolveValue(request, metadata, "field1",
                Multipart.class, null);
        assertThat(actual.getName(), is("field1"));
        assertThat(actual.getResourceName(), is(nullValue()));
        assertThat(new String(actual.getBytes()), is("$field2"));
    }

    @Test
    public void testGetFieldAsString() throws Exception {
        RequestContext request = mock(RequestContext.class);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        InputStream body = new ByteArrayInputStream(content.getBytes());
        when(request.getRequestBody()).thenReturn(body);
        when(request.getContentType())
                .thenReturn(
                        MediaTypes
                                .valueOf("multipart/form-data; boundary=---------------------------287032381131322"));
        String actual = (String) resolver.resolveValue(request, metadata, "field1", String.class,
                null);
        assertThat(actual, is("$field2"));
    }

    @Test
    public void testGetMultiple() throws Exception {
        RequestContext request = mock(RequestContext.class);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        InputStream body = new ByteArrayInputStream(content.getBytes());
        when(request.getRequestBody()).thenReturn(body);
        when(request.getContentType())
                .thenReturn(
                        MediaTypes
                                .valueOf("multipart/form-data; boundary=---------------------------287032381131322"));
        Multipart[] actual = (Multipart[]) resolver.resolveValue(request, metadata, "datafile2",
                Multipart[].class, null);
        assertThat(actual[0].getName(), is("datafile2"));
        assertThat(actual[0].getResourceName(), is("g.gif"));
        assertThat(actual[0].getBytes().length, is(35));
        assertThat(actual[1].getName(), is("datafile2"));
        assertThat(actual[1].getResourceName(), is("f.gif"));
        assertThat(actual[1].getBytes().length, is(35));
    }

    @Test
    public void testGetMultipleField() throws Exception {
        RequestContext request = mock(RequestContext.class);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        InputStream body = new ByteArrayInputStream(content.getBytes());
        when(request.getRequestBody()).thenReturn(body);
        when(request.getContentType())
                .thenReturn(
                        MediaTypes
                                .valueOf("multipart/form-data; boundary=---------------------------287032381131322"));
        Multipart[] actual = (Multipart[]) resolver.resolveValue(request, metadata, "field2",
                Multipart[].class, null);
        assertThat(actual[0].getName(), is("field2"));
        assertThat(actual[0].getResourceName(), is(nullValue()));
        assertThat(new String(actual[0].getBytes()), is("$field3"));
        assertThat(actual[1].getName(), is("field2"));
        assertThat(actual[1].getResourceName(), is(nullValue()));
        assertThat(actual[1].getContentType(), is("text/plain;charset=utf-8"));
        assertThat(new String(actual[1].getBytes()), is("ああああ"));
    }

    @Test
    public void testGetMultipleFieldAsString() throws Exception {
        RequestContext request = mock(RequestContext.class);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        InputStream body = new ByteArrayInputStream(content.getBytes());
        when(request.getRequestBody()).thenReturn(body);
        when(request.getContentType())
                .thenReturn(
                        MediaTypes
                                .valueOf("multipart/form-data; boundary=---------------------------287032381131322"));
        String[] actual = (String[]) resolver.resolveValue(request, metadata, "field2",
                String[].class, null);
        assertThat(actual[0], is("$field3"));
        assertThat(actual[1], is("ああああ"));
    }
}
