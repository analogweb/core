package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletInputStream;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Test;

public class RequestBodyScopeAttributesResolverTest extends RequestBodyScopeAttributesResolver {

    private RequestBodyScopeAttributesResolver resolver;
    private RequestContext requestContext;
    private InvocationMetadata metadata;

    @Before
    public void setUp() throws Exception {
        resolver = new RequestBodyScopeAttributesResolver();
        requestContext = mock(RequestContext.class);
        metadata = mock(InvocationMetadata.class);
    }

    @Test
    public void testResolveAttributeValue() throws Exception {
        ServletInputStream expected = new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        when(requestContext.getRequestBody()).thenReturn(expected);
        ServletInputStream actual = (ServletInputStream) resolver.resolveAttributeValue(
                requestContext, metadata, "", null);
        assertThat(actual, is(expected));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResolveAttributeValueWithException() throws Exception {
        when(requestContext.getRequestBody()).thenThrow(IOException.class);
        ServletInputStream actual = (ServletInputStream) resolver.resolveAttributeValue(
                requestContext, metadata, "", null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testGetScopeName() {
        assertThat(resolver.getScopeName(), is("body"));
    }

}
