package org.analogweb.core;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.AttributesHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestAttributesFactory;
import org.analogweb.RequestPath;
import org.analogweb.util.Maps;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class DefaultRequestContextTest {

    private DefaultRequestContext context;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;
    private InvocationMetadata metadata;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        servletContext = mock(ServletContext.class);
        metadata = mock(InvocationMetadata.class);
    }

    @Test
    public void testContext() {
        context = new DefaultRequestContext(request, response, servletContext);
        when(request.getRequestURI()).thenReturn("/baa/baz.rn");
        when(request.getContextPath()).thenReturn("/foo");
        when(request.getMethod()).thenReturn("GET");

        assertSame(context.getContext(), servletContext);
        assertSame(context.getRequest(), request);
        assertSame(context.getResponse(), response);
        assertTrue(context.getRequestPath() instanceof RequestPath);
    }

    @Test
    public void testResolveRequestAttributes() {
        context = new DefaultRequestContext(request, response, servletContext);
        RequestAttributesFactory factory = mock(RequestAttributesFactory.class);
        AttributesHandler resolver = mock(AttributesHandler.class);
        Map<String, AttributesHandler> resolversMap = Maps.newHashMap("scope", resolver);

        context.resolveRequestAttributes(factory, metadata, resolversMap);
    }

}
