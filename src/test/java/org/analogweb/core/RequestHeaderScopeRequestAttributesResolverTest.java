package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.analogweb.Headers;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class RequestHeaderScopeRequestAttributesResolverTest {

    private RequestHeaderScopeRequestAttributesResolver resolver;
    private RequestContext requestContext;
    private InvocationMetadata metadata;
    private Headers headers;

    @Before
    public void setUp() throws Exception {
        resolver = new RequestHeaderScopeRequestAttributesResolver();
        requestContext = mock(RequestContext.class);
        metadata = mock(InvocationMetadata.class);
        headers = mock(Headers.class);
    }

    @Test
    public void testResolveAttributeValue() {
        when(requestContext.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Content-Type")).thenReturn(Arrays.asList("text/plain"));

        Object actual = resolver.resolveValue(requestContext, metadata, "Content-Type", null);
        assertThat(((String[]) actual)[0], is("text/plain"));
    }

    @Test
    public void testResolveAttributeValueNotAvairable() {
        when(requestContext.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Foo-Type")).thenReturn(null);

        Object actual = resolver.resolveValue(requestContext, metadata, "Foo-Type", null);
        assertNull(actual);
    }

    @Test
    public void testResolveAttributeValueWithNullName() {
        Object actual = resolver.resolveValue(requestContext, metadata, null, null);
        assertNull(actual);
    }

}
