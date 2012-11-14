package org.analogweb.core.direction;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.analogweb.Direction;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.util.Maps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HttpStatusTest {

    private RequestContext requestContext;
    private Headers headers;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.requestContext = mock(RequestContext.class);
        this.headers = mock(Headers.class);
    }

    @Test
    public void testRender() throws Exception {
        when(requestContext.getResponseHeaders()).thenReturn(headers);
        HttpStatus.OK.render(requestContext);
        verify(requestContext).setResponseStatus(200);
    }

    @Test
    public void testRenderWithError() throws Exception {
        when(requestContext.getResponseHeaders()).thenReturn(headers);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(requestContext.getResponseBody()).thenReturn(out);
        HttpStatus.NOT_FOUND.byReasonOf("foo is not found.").render(requestContext);
        assertThat(new String(out.toByteArray()), is("foo is not found."));
        verify(requestContext).setResponseStatus(404);
    }

    @Test
    public void testRenderWithHeader() throws Exception {
        when(requestContext.getResponseHeaders()).thenReturn(headers);

        HttpStatus.FOUND.withHeader(Maps.newHashMap("Location", "http://foo.com/baa")).render(
                requestContext);

        verify(requestContext).setResponseStatus(302);
        verify(headers).putValue("Location", "http://foo.com/baa");
    }

    @Test
    public void testRenderWithPreRenderDirection() throws Exception {
        when(requestContext.getResponseHeaders()).thenReturn(headers);

        Direction direction = mock(Direction.class);

        HttpStatus.OK.with(direction).render(requestContext);

        verify(requestContext).setResponseStatus(200);
        verify(direction).render(requestContext);
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
