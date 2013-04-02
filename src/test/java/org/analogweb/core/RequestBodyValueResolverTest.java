package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Test;

public class RequestBodyValueResolverTest {

    private RequestBodyValueResolver resolver;
    private RequestContext requestContext;
    private InvocationMetadata metadata;

    @Before
    public void setUp() throws Exception {
        resolver = new RequestBodyValueResolver();
        requestContext = mock(RequestContext.class);
        metadata = mock(InvocationMetadata.class);
    }

    @Test
    public void testResolveAttributeValue() throws Exception {
        InputStream expected = new ByteArrayInputStream(new byte[0]);
        when(requestContext.getRequestBody()).thenReturn(expected);
        InputStream actual = (InputStream) resolver
                .resolveValue(requestContext, metadata, "", null);
        assertThat(actual, is(expected));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResolveAttributeValueWithException() throws Exception {
        when(requestContext.getRequestBody()).thenThrow(IOException.class);
        InputStream actual = (InputStream) resolver
                .resolveValue(requestContext, metadata, "", null);
        assertThat(actual, is(nullValue()));
    }
}
