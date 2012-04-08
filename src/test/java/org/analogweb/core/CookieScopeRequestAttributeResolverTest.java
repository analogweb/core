package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.core.CookieScopeRequestAttributeResolver;
import org.analogweb.util.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

/**
 * @author snowgoose
 */
public class CookieScopeRequestAttributeResolverTest {

    private CookieScopeRequestAttributeResolver resolver;
    private RequestContext requestContext;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private InvocationMetadata metadata;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        resolver = new CookieScopeRequestAttributeResolver();
        requestContext = mock(RequestContext.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        metadata = mock(InvocationMetadata.class);
    }

    /**
     * Test method for
     * {@link org.analogweb.core.CookieScopeRequestAttributeResolver#getScopeName()}
     * .
     */
    @Test
    public void testGetName() {
        assertThat(resolver.getScopeName(), is("cookie"));
    }

    /**
     * Test method for
     * {@link org.analogweb.core.CookieScopeRequestAttributeResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * .
     */
    @Test
    public void testResolveAttributeValue() {
        Cookie cookie = new Cookie("foo", "baa");
        Cookie[] cookies = ArrayUtils.newArray(cookie);
        when(requestContext.getRequest()).thenReturn(request);
        when(request.getCookies()).thenReturn(cookies);

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo");
        assertThat((String) actual, is("baa"));

        verify(request).getCookies();
    }

    /**
     * Test method for
     * {@link org.analogweb.core.CookieScopeRequestAttributeResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * .
     */
    @Test
    public void testResolveAttributeValueMultipleCookies() {
        Cookie cookie1 = new Cookie("foo", "baa");
        Cookie cookie2 = new Cookie("baz", "boo");
        Cookie[] cookies = ArrayUtils.newArray(cookie1, cookie2);

        when(requestContext.getRequest()).thenReturn(request);
        when(request.getCookies()).thenReturn(cookies);

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "baz");
        assertThat((String) actual, is("boo"));

        verify(request).getCookies();
    }

    @Test
    public void testResolveAttributeValueNotMatchCookie() {
        Cookie cookie1 = new Cookie("foo", "baa");
        Cookie cookie2 = new Cookie("baz", "boo");
        Cookie[] cookies = ArrayUtils.newArray(cookie1, cookie2);

        when(requestContext.getRequest()).thenReturn(request);
        when(request.getCookies()).thenReturn(cookies);

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "hoge");
        assertNull(actual);

        verify(request).getCookies();
    }

    /**
     * Test method for
     * {@link org.analogweb.core.CookieScopeRequestAttributeResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * .
     */
    @Test
    public void testResolveAttributeValueCookiesNotAvairable() {
        when(requestContext.getRequest()).thenReturn(request);
        when(request.getCookies()).thenReturn(null);

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo");
        assertNull(actual);

        verify(request).getCookies();
    }

    @Test
    public void testPutAttributeValue() {
        when(requestContext.getResponse()).thenReturn(response);
        doNothing().when(response).addCookie(new Cookie("foo", "baa"));

        resolver.putAttributeValue(requestContext, "foo", "baa");
        verify(response).addCookie(argThat(new ArgumentMatcher<Cookie>() {
            @Override
            public boolean matches(Object argument) {
                if (argument instanceof Cookie) {
                    Cookie cookie = (Cookie) argument;
                    return (cookie.getName().equals("foo") && cookie.getValue().equals("baa"));
                }
                return false;
            }
        }));
    }

    @Test
    public void testRemoveAttribute() {
        // do nothing.
        resolver.removeAttribute(requestContext, "baa");
    }

}
