package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;


import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.core.ApplicationScopeRequestAttributesResolver;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class ApplicationScopeRequestAttributesResolverTest {

    private ApplicationScopeRequestAttributesResolver resolver;
    private RequestContext requestContext;
    private ServletContext servletContext;
    private InvocationMetadata metadata;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        resolver = new ApplicationScopeRequestAttributesResolver();
        requestContext = mock(RequestContext.class);
        servletContext = mock(ServletContext.class);
        metadata = mock(InvocationMetadata.class);
    }

    /**
     * Test method for
     * {@link org.analogweb.core.ApplicationScopeRequestAttributesResolver#getScopeName()}
     * .
     */
    @Test
    public void testGetName() {
        assertThat(resolver.getScopeName(), is("application"));
    }

    /**
     * Test method for
     * {@link org.analogweb.core.ApplicationScopeRequestAttributesResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * .
     */
    @Test
    public void testResolveAttributeValue() {
        Object expected = new Object();
        when(requestContext.getContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("foo")).thenReturn(expected);

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo");
        assertThat(actual, is(expected));

        verify(servletContext).getAttribute("foo");
    }

    /**
     * Test method for
     * {@link org.analogweb.core.ApplicationScopeRequestAttributesResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * .
     */
    @Test
    public void testResolveAttributeValueWithNullName() {
        Object actual = resolver.resolveAttributeValue(requestContext, metadata, null);
        assertNull(actual);
    }

    @Test
    public void testPutAttributeValue() {
        when(requestContext.getContext()).thenReturn(servletContext);
        doNothing().when(servletContext).setAttribute("foo", "baa");

        resolver.putAttributeValue(requestContext, "foo", "baa");
        verify(servletContext).setAttribute("foo", "baa");
    }

    @Test
    public void testPutAttributeValueWithNullName() {
        resolver.putAttributeValue(requestContext, null, "baa");
    }

    @Test
    public void testRemoveAttributeValue() {
        when(requestContext.getContext()).thenReturn(servletContext);

        resolver.removeAttribute(requestContext, "foo");
        verify(servletContext).removeAttribute("foo");
    }

    @Test
    public void testRemoveAttributeValueWithNullName() {
        resolver.removeAttribute(requestContext, null);
    }

}
