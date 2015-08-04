package org.analogweb.core.response;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.ResponseEntity;
import org.junit.Before;
import org.junit.Test;

public class ResponsesTest {

    private RequestContext request;
    private ResponseContext response;
    private Headers responseHeaders;

    @Before
    public void setUp() {
        request = mock(RequestContext.class);
        response = mock(ResponseContext.class);
        responseHeaders = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(responseHeaders);
    }

    @Test
    public void testOk() throws Exception {
        ResponseEntity entity = mock(ResponseEntity.class);
        Responses responses = Responses.ok(entity);
        responses.render(request, response);
        verify(response).setStatus(200);
    }

    @Test
    public void testOkWithEntityStream() throws Exception {
        InputStream entity = new ByteArrayInputStream(new byte[0]);
        Responses responses = Responses.ok(entity);
        responses.render(request, response);
        verify(response).setStatus(200);
    }

    @Test
    public void testOkWithEntityString() throws Exception {
        String entity = "foo";
        Charset cs = Charset.forName("UTF-8");
        Responses responses = Responses.ok(entity, cs);
        responses.render(request, response);
        verify(response).setStatus(200);
    }

    @Test
    public void testLocates() throws Exception {
        Responses responses = Responses.locates(URI.create("http://example.com/index.html"));
        responses.render(request, response);
        verify(responseHeaders).putValue("Location", "http://example.com/index.html");
        verify(response).setStatus(302);
    }
}
