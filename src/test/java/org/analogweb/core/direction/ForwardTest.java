package org.analogweb.core.direction;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.ServletRequestContext;
import org.analogweb.exception.AssertionFailureException;
import org.analogweb.exception.MissingRequirmentsException;
import org.analogweb.junit.NoDescribeMatcher;
import org.analogweb.util.Maps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class ForwardTest {

    private ServletRequestContext context;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        context = mock(ServletRequestContext.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
    }

    @Test
    public void testRender() throws Exception {
        String path = "/foo/baa.rn";
        when(context.getServletRequest()).thenReturn(request);
        when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
        when(context.getServletResponse()).thenReturn(response);

        doNothing().when(dispatcher).forward(request, response);

        Forward.to(path).render(context);

        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testRenderWithNoContext() throws Exception {
        thrown.expect(AssertionFailureException.class);

        String path = "/foo/baa.rn";
        Forward.to(path).render(null);

    }

    @Test
    public void testRenderWithEmptyPath() throws Exception {
        thrown.expect(MissingRequirmentsException.class);

        Forward.to(null);

    }

    @Test
    public void testRenderPathNotStartWithSlash() throws Exception {
        thrown.expect(new NoDescribeMatcher<MissingRequirmentsException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof MissingRequirmentsException) {
                    MissingRequirmentsException mr = (MissingRequirmentsException) arg0;
                    assertThat(mr.getRequirment(), is("forward path"));
                    assertThat(mr.getUnresolvableInvocationResult().toString(), is("foo/baa.rn"));
                    return true;
                }
                return false;
            }
        });

        String path = "foo/baa.rn";
        Forward.to(path);

    }

    @Test
    public void testEquals() throws Exception {
        assertThat(Forward.to("/foo/bar"), is(Forward.to("/foo/bar")));
        assertThat(Forward.to("/foo/bar"), is(not(Forward.to("/foo/bar2"))));
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(Forward.to("/foo/bar").hashCode(), is(Forward.to("/foo/bar").hashCode()));
        assertThat(Forward.to("/foo/bar").hashCode(), is(not(Forward.to("/foo/bar2").hashCode())));
    }

    @Test
    public void testRenderWithContextObject() throws Exception {
        String path = "/foo/baz";
        when(context.getServletRequest()).thenReturn(request);
        when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
        when(context.getServletResponse()).thenReturn(response);

        doNothing().when(dispatcher).forward(request, response);

        Serializable extractToRequest = mock(Serializable.class);
        doNothing().when(request).setAttribute("serializeable", extractToRequest);

        Forward.to(path).with(extractToRequest).render(context);

        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testRenderWithContextNullObject() throws Exception {
        String path = "/foo/baz";
        when(context.getServletRequest()).thenReturn(request);
        when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
        when(context.getServletResponse()).thenReturn(response);

        doNothing().when(dispatcher).forward(request, response);

        Serializable extractToRequest = null;
        //        doNothing().when(request).setAttribute("serializeable", extractToRequest);

        Forward.to(path).with(extractToRequest).render(context);

        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testRenderWithContextMap() throws Exception {
        String path = "/foo/baz";
        when(context.getServletRequest()).thenReturn(request);
        when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
        when(context.getServletResponse()).thenReturn(response);

        doNothing().when(dispatcher).forward(request, response);

        Object foo = new Object();
        Map<String, Object> extractToRequest = Maps.newHashMap("foo", foo);
        doNothing().when(request).setAttribute("foo", foo);

        Forward.to(path).with(extractToRequest).render(context);

        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testRenderWithNulContextMap() throws Exception {
        String path = "/foo/baz";
        when(context.getServletRequest()).thenReturn(request);
        when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
        when(context.getServletResponse()).thenReturn(response);

        doNothing().when(dispatcher).forward(request, response);

        Map<String, Object> extractToRequest = null;
        //        doNothing().when(request).setAttribute("foo", foo);

        Forward.to(path).with(extractToRequest).render(context);

        verify(dispatcher).forward(request, response);
    }

}
