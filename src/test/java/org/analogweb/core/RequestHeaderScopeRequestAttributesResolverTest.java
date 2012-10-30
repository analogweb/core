package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

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
    private HttpServletRequest request;
    private InvocationMetadata metadata;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        resolver = new RequestHeaderScopeRequestAttributesResolver();
        requestContext = mock(RequestContext.class);
        request = mock(HttpServletRequest.class);
        metadata = mock(InvocationMetadata.class);
    }

    /**
     * Test method for
     * {@link org.analogweb.core.RequestHeaderScopeRequestAttributesResolver#getScopeName()}
     * .
     */
    @Test
    public void testGetName() {
        assertThat(resolver.getScopeName(), is("header"));
    }

    /**
     * Test method for
     * {@link org.analogweb.core.RequestHeaderScopeRequestAttributesResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * .
     */
    @Test
    public void testResolveAttributeValue() {
        when(requestContext.getRequest()).thenReturn(request);
        when(request.getHeaders("Content-Type")).thenReturn(new StringArrayEnumeration("text/plain"));

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "Content-Type");
        assertThat(((String[]) actual)[0], is("text/plain"));
        verify(request).getHeaders("Content-Type");
    }

    /**
     * Test method for
     * {@link org.analogweb.core.RequestHeaderScopeRequestAttributesResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * .
     */
    @Test
    public void testResolveAttributeValueNotAvairable() {
        when(requestContext.getRequest()).thenReturn(request);
        when(request.getHeaders("Foo-Type")).thenReturn(null);

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "Foo-Type");
        assertNull(actual);
        verify(request).getHeaders("Foo-Type");
    }

    /**
     * Test method for
     * {@link org.analogweb.core.RequestHeaderScopeRequestAttributesResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * .
     */
    @Test
    public void testResolveAttributeValueWithNullName() {

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, null);
        assertNull(actual);
    }
    
    static class StringArrayEnumeration implements Enumeration<String> {
        Iterator<String> strings;

        StringArrayEnumeration(String... strings) {
            this.strings = Arrays.asList(strings).iterator();
        }

        @Override
        public boolean hasMoreElements() {
            return strings.hasNext();
        }

        @Override
        public String nextElement() {
            return strings.next();
        }
    }

}
