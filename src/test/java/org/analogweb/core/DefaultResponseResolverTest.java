package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.analogweb.Renderable;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.core.response.HttpStatus;
import org.analogweb.core.response.Text;
import org.junit.Before;
import org.junit.Test;

public class DefaultResponseResolverTest extends DefaultResponseResolver {

    private DefaultResponseResolver resolver;
    private InvocationMetadata metadata;
    private RequestContext context;
    private ResponseContext response;

    @Before
    public void setUp() throws Exception {
        resolver = new DefaultResponseResolver();
        metadata = mock(InvocationMetadata.class);
        context = mock(RequestContext.class);
        response = mock(ResponseContext.class);
    }

    @Test
    public void testResolve() {
        Renderable invocationResult = mock(Renderable.class);
        Renderable actual = resolver.resolve(invocationResult, metadata, context, response);

        assertThat(actual, is(sameInstance(invocationResult)));
    }

    @Test
    public void testResolveWithNumber() {
        Integer invocationResult = 500;
        HttpStatus actual = (HttpStatus) resolver.resolve(invocationResult, metadata, context,
                response);

        assertThat(actual, is(sameInstance(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void testResolveWithPrimitiveNumber() {
        int invocationResult = 500;
        HttpStatus actual = (HttpStatus) resolver.resolve(invocationResult, metadata, context,
                response);

        assertThat(actual, is(sameInstance(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void testResolveWithText() {
        String invocationResult = "this is simple text.";
        Text actual = (Text) resolver.resolve(invocationResult, metadata, context, response);

        assertThat(actual.toString(), is(invocationResult));
    }

    @Test
    public void testResolveWithoutResult() {
        Renderable invocationResult = null;
        HttpStatus actual = (HttpStatus) resolver.resolve(invocationResult, metadata, context,
                response);

        assertThat(actual, is(sameInstance(HttpStatus.NO_CONTENT)));
    }
}
