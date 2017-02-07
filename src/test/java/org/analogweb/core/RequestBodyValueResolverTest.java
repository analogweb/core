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
import org.analogweb.ReadableBuffer;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RequestBodyValueResolverTest {

    private RequestBodyValueResolver resolver;
    private RequestContext requestContext;
    private InvocationMetadata metadata;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        resolver = new RequestBodyValueResolver();
        requestContext = mock(RequestContext.class);
        metadata = mock(InvocationMetadata.class);
    }

    @Test
    public void testResolveAttributeValue() throws Exception {
        ReadableBuffer expected = DefaultReadableBuffer.readBuffer(new byte[0]);
        when(requestContext.getRequestBody()).thenReturn(expected);
        InputStream actual = (InputStream) resolver.resolveValue(requestContext, metadata, "",
                InputStream.class, null);
        assertThat(actual, is(expected.asInputStream()));
    }

    @Test
    public void testResolveAttributeValueAsString() throws Exception {
        ReadableBuffer expected = DefaultReadableBuffer.readBuffer("abcde".getBytes("UTF-8"));
        when(requestContext.getRequestBody()).thenReturn(expected);
        String actual = (String) resolver.resolveValue(requestContext, metadata, "", String.class,
                null);
        assertThat(actual, is("abcde"));
    }

    @Test
    public void testResolveAttributeValueWithoutType() throws Exception {
        ReadableBuffer expected = DefaultReadableBuffer.readBuffer("abcde".getBytes("UTF-8"));
        when(requestContext.getRequestBody()).thenReturn(expected);
        Object actual = resolver.resolveValue(requestContext, metadata, "", null, null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testResolveAttributeValueUnknownType() throws Exception {
        thrown.expect(UnresolvableValueException.class);
        ReadableBuffer expected = DefaultReadableBuffer.readBuffer("abcde".getBytes("UTF-8"));
        when(requestContext.getRequestBody()).thenReturn(expected);
        resolver.resolveValue(requestContext, metadata, "", Integer.class, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResolveAttributeValueWithException() throws Exception {
        thrown.expect(ApplicationRuntimeException.class);
        when(requestContext.getRequestBody()).thenThrow(IOException.class);
        resolver.resolveValue(requestContext, metadata, "", InputStream.class, null);
    }
}
