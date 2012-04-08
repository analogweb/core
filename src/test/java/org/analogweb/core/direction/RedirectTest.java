package org.analogweb.core.direction;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletResponse;


import org.analogweb.RequestContext;
import org.analogweb.core.direction.Redirect;
import org.analogweb.exception.AssertionFailureException;
import org.analogweb.exception.MissingRequirmentsException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class RedirectTest {

    private RequestContext context;
    private HttpServletResponse response;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testRender() throws Exception {
        when(context.getResponse()).thenReturn(response);
        when(response.encodeRedirectURL("/some/foo.rn")).thenReturn("/some/foo.rn");

        Redirect.to("/some/foo.rn").render(context);

        verify(context).getResponse();
        verify(response).sendRedirect("/some/foo.rn");
    }

    @Test
    public void testRenderHttp10Compatible() throws Exception {
        when(context.getResponse()).thenReturn(response);
        when(response.encodeRedirectURL("/some/foo.rn")).thenReturn("/some/foo.rn");

        Redirect.to("/some/foo.rn").resoposeCode(301).render(context);

        verify(context).getResponse();
        verify(response).setStatus(301);
        verify(response).setHeader("Location", "/some/foo.rn");
    }

    @Test
    public void testRenderWithNotReturnCode() throws Exception {
        when(context.getResponse()).thenReturn(response);
        when(response.encodeRedirectURL("/some/foo.rn")).thenReturn("/some/foo.rn");

        // not redirect response code.
        Redirect.to("/some/foo.rn").resoposeCode(404).render(context);

        verify(context).getResponse();
        verify(response).sendRedirect("/some/foo.rn");
    }

    @Test
    public void testRenderWithNullContext() throws Exception {

        thrown.expect(AssertionFailureException.class);

        Redirect.to("/some/foo.rn").render(null);

    }

    @Test
    public void testRenderWithParameter() throws Exception {
        when(context.getResponse()).thenReturn(response);
        when(response.encodeRedirectURL("/some/foo.rn?foo=baa&hoge=fuga")).thenReturn("/some/foo.rn?foo=baa&hoge=fuga");
        doNothing().when(response).sendRedirect("/some/foo.rn?foo=baa&hoge=fuga");

        Redirect.to("/some/foo.rn").addParameter("foo", "baa").addParameter("hoge", "fuga").render(context);

        verify(context).getResponse();
        verify(response).sendRedirect("/some/foo.rn?foo=baa&hoge=fuga");
    }

    @Test
    public void testRenderWithParametarizedURLAndParameter() throws Exception {
        when(context.getResponse()).thenReturn(response);
        doNothing().when(response).sendRedirect("/some/foo.rn?boo=baz&hoge=fuga&foo=baa");
        when(response.encodeRedirectURL("/some/foo.rn?boo=baz&foo=baa&hoge=fuga")).thenReturn(
                "/some/foo.rn?boo=baz&foo=baa&hoge=fuga");

        // sort parameter name by natural order.
        Redirect.to("/some/foo.rn?boo=baz").addParameter("hoge", "fuga").addParameter("foo", "baa")
                .encodeWith("ISO-8859-1").render(context);

        verify(context).getResponse();
        verify(response).sendRedirect("/some/foo.rn?boo=baz&foo=baa&hoge=fuga");

    }

    @Test
    public void testRenderWithEmptyPath() throws Exception {
        thrown.expect(MissingRequirmentsException.class);
        Redirect.to(null);
    }

    @Test
    public void testEquals() throws Exception {
        assertThat(Redirect.to("/foo/bar"), is(Redirect.to("/foo/bar")));
        assertThat(Redirect.to("/foo/bar"), is(not(Redirect.to("/foo/bar2"))));
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(Redirect.to("/foo/bar").hashCode(), is(Redirect.to("/foo/bar").hashCode()));
        assertThat(Redirect.to("/foo/bar").hashCode(), is(not(Redirect.to("/foo/bar2").hashCode())));
    }

}
