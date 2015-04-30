package org.analogweb.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.analogweb.InvocationMetadata;
import org.analogweb.Multipart;
import org.analogweb.RequestContext;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class MultipartParameterResolverTest {
    
    private MultipartParameterResolver resolver = new MultipartParameterResolver();

    private String content = 
            "-----------------------------287032381131322"+"\r\n"+
            "Content-Disposition: form-data; name=\"datafile1\"; filename=\"r.gif\""+"\r\n"+
            "Content-Type: image/gif"+"\r\n"+
            ""+"\r\n"+
            "GIF87a.............,...........D..;"+"\r\n"+
            "-----------------------------287032381131322"+"\r\n"+
            "Content-Disposition: form-data; name=\"datafile2\"; filename=\"g.gif\""+"\r\n"+
            "Content-Type: image/gif"+"\r\n"+
            ""+"\r\n"+
            "GIF87a.............,...........D..;"+"\r\n"+
            "-----------------------------287032381131322"+"\r\n"+
            "Content-Disposition: form-data; name=\"datafile3\"; filename=\"b.gif\""+"\r\n"+
            "Content-Type: image/gif"+"\r\n"+
            ""+"\r\n"+
            "GIF87a.............,...........D..;"+"\r\n"+
            "-----------------------------287032381131322--"+"\r\n";

    @Test
    public void test() throws Exception {
        RequestContext request = mock(RequestContext.class);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        InputStream body = new ByteArrayInputStream(content.getBytes());
        when(request.getRequestBody()).thenReturn(body);
        when(request.getContentType())
                .thenReturn(
                        MediaTypes
                                .valueOf("multipart/form-data; boundary=---------------------------287032381131322"));
        Multipart actual = (Multipart) resolver.resolveValue(request, metadata, "datafile1",
                Multipart.class, null);
        assertThat(actual.getName(), is("datafile1"));
        assertThat(actual.getResourceName(), is("r.gif"));
        assertThat(actual.getBytes().length, is(35));
    }
}
