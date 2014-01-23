package org.analogweb.core.response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.analogweb.Renderable;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.core.DefaultResponseWriter;
import org.analogweb.util.Maps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HttpStatusTest {

    private RequestContext requestContext;
    private ResponseContext response;
    private Headers headers;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        requestContext = mock(RequestContext.class);
        response = mock(ResponseContext.class);
        headers = mock(Headers.class);
    }

    @Test
    public void testRender() throws Exception {
        when(response.getResponseHeaders()).thenReturn(headers);
        HttpStatus.OK.render(requestContext, response);
        verify(response).setStatus(200);
    }

    @Test
    public void testRenderWithError() throws Exception {
        when(response.getResponseHeaders()).thenReturn(headers);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DefaultResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);
        HttpStatus.NOT_FOUND.byReasonOf("foo is not found.").render(requestContext, response);
        writer.getEntity().writeInto(out);
        assertThat(new String(out.toByteArray()), is("foo is not found."));
        verify(response).setStatus(404);
    }

    @Test
    public void testRenderWithHeader() throws Exception {
        when(response.getResponseHeaders()).thenReturn(headers);
        HttpStatus.FOUND.withHeader(Maps.newHashMap("Location", "http://foo.com/baa")).render(
                requestContext, response);
        verify(response).setStatus(302);
        verify(headers).putValue("Location", "http://foo.com/baa");
    }

    @Test
    public void testRenderWithPreRenderResponse() throws Exception {
        when(response.getResponseHeaders()).thenReturn(headers);
        Renderable direction = mock(Renderable.class);
        HttpStatus.OK.with(direction).render(requestContext, response);
        verify(response).setStatus(200);
        verify(direction).render(requestContext, response);
    }

    @Test
    public void testValueOfNumber() {
        HttpStatus actual = HttpStatus.valueOf(200);
        assertThat(actual, is(HttpStatus.OK));
        actual = HttpStatus.valueOf(404);
        assertThat(actual, is(HttpStatus.NOT_FOUND));
        actual = HttpStatus.valueOf(301);
        assertThat(actual, is(HttpStatus.MOVED_PERMANENTLY));
        actual = HttpStatus.valueOf(500);
        assertThat(actual, is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testValueOfInvalidStatusNumber() {
        thrown.expect(IllegalArgumentException.class);
        HttpStatus.valueOf(9999);
    }
}
