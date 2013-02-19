package org.analogweb.core.httpserver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;

import org.analogweb.Application;
import org.analogweb.ApplicationContextResolver;
import org.analogweb.ApplicationProperties;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.ResponseContext;
import org.analogweb.core.InvocationFailureException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sun.net.httpserver.Headers;

/**
 * @author snowgoose
 */
public class AnalogHandlerTest {

    private AnalogHandler handler;

    private Application app;
    private ApplicationContextResolver resolver;
    private ApplicationProperties props;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        app = mock(Application.class);
        resolver = mock(ApplicationContextResolver.class);
        props = mock(ApplicationProperties.class);
        handler = new AnalogHandler(app, resolver, props);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRun() {
        doNothing().when(app).run(eq(resolver), eq(props), isA(Collection.class),
                isA(ClassLoader.class));

        handler.run();

        verify(app).run(eq(resolver), eq(props), isA(Collection.class), isA(ClassLoader.class));
    }

    @Test
    public void testShutdown() {

        doNothing().when(app).dispose();

        handler.shutdown();

        verify(app).dispose();
    }

    @Test
    public void testHandle() throws Exception {
        MockHttpExchange exc = new MockHttpExchange();
        exc.setRequestURI(URI.create("/context/foo/baa"));
        exc.setLocalAddress(InetSocketAddress.createUnresolved("0.0.0.0", 8080));
        exc.setRequestMethod("GET");
        MockHttpContext httpContext = new MockHttpContext();
        httpContext.setPath("/context");
        exc.setHttpContext(httpContext);
        when(
                app.processRequest(isA(RequestPath.class), isA(RequestContext.class),
                        isA(ResponseContext.class))).thenReturn(Application.PROCEEDED);

        handler.handle(exc);

        assertThat(exc.getSendStatus(), is(HttpURLConnection.HTTP_OK));
        assertThat(exc.getResponseContentLength(), is(0L));
    }

    @Test
    public void testHandlePathNotFound() throws Exception {
        MockHttpExchange exc = new MockHttpExchange();
        exc.setRequestURI(URI.create("/context/foo/baa"));
        exc.setLocalAddress(InetSocketAddress.createUnresolved("0.0.0.0", 8080));
        exc.setRequestMethod("GET");
        exc.setResponseHeaders(new Headers());
        MockHttpContext httpContext = new MockHttpContext();
        httpContext.setPath("/context");
        exc.setHttpContext(httpContext);
        when(
                app.processRequest(isA(RequestPath.class), isA(RequestContext.class),
                        isA(ResponseContext.class))).thenReturn(Application.NOT_FOUND);

        handler.handle(exc);

        assertThat(exc.getSendStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
        assertThat(exc.getResponseContentLength(), is(-1L));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleWithException() throws Exception {
        final MockHttpExchange exc = new MockHttpExchange();
        thrown.expect(new BaseMatcher<IOException>() {
            @Override
            public boolean matches(Object item) {
                if (item instanceof IOException) {
                    assertThat(exc.getSendStatus(), is(HttpURLConnection.HTTP_INTERNAL_ERROR));
                    assertThat(exc.getResponseContentLength(), is(-1L));
                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(exc);
            }

        });
        exc.setRequestURI(URI.create("/context/foo/baa"));
        exc.setLocalAddress(InetSocketAddress.createUnresolved("0.0.0.0", 8080));
        exc.setRequestMethod("GET");
        exc.setResponseHeaders(new Headers());
        MockHttpContext httpContext = new MockHttpContext();
        httpContext.setPath("/context");
        exc.setHttpContext(httpContext);
        when(
                app.processRequest(isA(RequestPath.class), isA(RequestContext.class),
                        isA(ResponseContext.class))).thenThrow(InvocationFailureException.class);

        handler.handle(exc);
    }

}
