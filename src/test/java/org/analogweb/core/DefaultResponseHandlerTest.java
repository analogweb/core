package org.analogweb.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.analogweb.ExceptionHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.Renderable;
import org.analogweb.RenderableResolver;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseFormatterFinder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DefaultResponseHandlerTest {

    private DefaultResponseHandler handler;
    private RenderableResolver resolver;
    private InvocationMetadata metadata;
    private RequestContext context;
    private ResponseContext response;
    private ExceptionHandler exceptionHandler;
    private ResponseFormatterFinder finder;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        handler = new DefaultResponseHandler();
        resolver = mock(RenderableResolver.class);
        metadata = mock(InvocationMetadata.class);
        context = mock(RequestContext.class);
        response = mock(ResponseContext.class);
        exceptionHandler = mock(ExceptionHandler.class);
        finder = mock(ResponseFormatterFinder.class);
    }

    @Test
    public void testHandleResult() throws Exception {
        Renderable result = mock(Renderable.class);
        when(resolver.resolve(result, metadata, context, response)).thenReturn(result);
        handler.handleResult(result,metadata,resolver,context, response,exceptionHandler,finder);
        verify(result).render(context, response);
    }

}
