package org.analogweb.core;

import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.analogweb.RequestContext;
import org.analogweb.core.DefaultRequestContextFactory;
import org.junit.Before;
import org.junit.Test;

public class DefaultRequestContextFactoryTest {

    private DefaultRequestContextFactory factory;
    private ServletContext context;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        factory = new DefaultRequestContextFactory();
        context = mock(ServletContext.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testCreateRequestContext() {
        RequestContext actual = factory.createRequestContext(context, request, response);
        assertNotSame(actual, factory.createRequestContext(context, request, response));
    }

}
