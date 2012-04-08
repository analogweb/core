package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.core.SessionScopeRequestAttributesResolver;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class SessionScopeRequestAttributesResolverTest {

    private SessionScopeRequestAttributesResolver resolver;
    private RequestContext requestContext;
    private HttpServletRequest request;
    private HttpSession session;
    private InvocationMetadata metadata;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        resolver = new SessionScopeRequestAttributesResolver();
        requestContext = mock(RequestContext.class);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        metadata = mock(InvocationMetadata.class);
    }

    /**
     * Test method for
     * {@link org.analogweb.core.SessionScopeRequestAttributesResolver#getScopeName()}
     * .
     */
    @Test
    public void testGetName() {
        assertThat(resolver.getScopeName(), is("session"));
    }

    /**
     * Test method for
     * {@link org.analogweb.core.SessionScopeRequestAttributesResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * .
     */
    @Test
    public void testResolveAttributeValue() {
        when(requestContext.getRequest()).thenReturn(request);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("foo")).thenReturn("baa");

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo");
        assertThat((String) actual, is("baa"));

        verify(session).getAttribute("foo");
    }

    /**
     * Test method for
     * {@link org.analogweb.core.SessionScopeRequestAttributesResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * .
     */
    @Test
    public void testResolveAttributeValueAttributeNotAvairable() {
        when(requestContext.getRequest()).thenReturn(request);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("foo")).thenReturn(null);

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo");
        assertNull(actual);

        verify(session).getAttribute("foo");
    }

    /**
     * Test method for
     * {@link org.analogweb.core.SessionScopeRequestAttributesResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * .
     */
    @Test
    public void testResolveAttributeValueSessionNotAvairable() {
        when(requestContext.getRequest()).thenReturn(request);
        when(request.getSession(false)).thenReturn(null);

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo");
        assertNull(actual);
    }

    @Test
    public void testPutAttributeValue() {
        when(requestContext.getRequest()).thenReturn(request);
        when(request.getSession(true)).thenReturn(session);
        doNothing().when(session).setAttribute("foo", "baa");

        resolver.putAttributeValue(requestContext, "foo", "baa");
        verify(session).setAttribute("foo", "baa");
    }

    @Test
    public void testPutAttributeValueWhenNameIsNull() {
        resolver.putAttributeValue(requestContext, null, "baa");
    }

    @Test
    public void testRemoveAttribute() {
        when(requestContext.getRequest()).thenReturn(request);
        when(request.getSession(true)).thenReturn(session);
        doNothing().when(session).removeAttribute("baa");

        resolver.removeAttribute(requestContext, "baa");

        verify(session).removeAttribute("baa");
    }

    @Test
    public void testRemoveAttributeWithNullValue() {
        resolver.removeAttribute(requestContext, null);
    }

}
