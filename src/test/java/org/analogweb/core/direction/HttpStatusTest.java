package org.analogweb.core.direction;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;


import org.analogweb.Direction;
import org.analogweb.RequestContext;
import org.analogweb.core.direction.HttpStatus;
import org.analogweb.util.Maps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HttpStatusTest {

    private RequestContext requestContext;
    private HttpServletResponse response;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.requestContext = mock(RequestContext.class);
        this.response = mock(HttpServletResponse.class);
    }

    @Test
    public void testRender() throws Exception {
        when(requestContext.getResponse()).thenReturn(response);
        HttpStatus.OK.render(requestContext);
        verify(response).setStatus(200);
    }

    @Test
    public void testRenderWithError() throws Exception {
        when(requestContext.getResponse()).thenReturn(response);
        HttpStatus.NOT_FOUND.byReasonOf("foo is not found.").render(requestContext);
        verify(response).sendError(404, "foo is not found.");
    }

    @Test
    public void testRenderWithHeader() throws Exception {
        when(requestContext.getResponse()).thenReturn(response);

        Map<String, String> headers = Maps.newHashMap("Location", "http://foo.com/baa");

        HttpStatus.FOUND.withHeader(headers).render(requestContext);

        verify(response).setStatus(302);
        verify(response).addHeader("Location", "http://foo.com/baa");
    }

    @Test
    public void testRenderWithPreRenderDirection() throws Exception {
        when(requestContext.getResponse()).thenReturn(response);

        Direction direction = mock(Direction.class);

        HttpStatus.OK.with(direction).render(requestContext);

        verify(response).setStatus(200);
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
