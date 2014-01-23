package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.analogweb.Cookies;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class CookieValueResolverTest {

    private CookieValueResolver resolver;
    private RequestContext requestContext;
    private Cookies cookies;
    private InvocationMetadata metadata;

    @Before
    public void setUp() throws Exception {
        resolver = new CookieValueResolver();
        requestContext = mock(RequestContext.class);
        cookies = mock(Cookies.class);
        metadata = mock(InvocationMetadata.class);
    }

    @Test
    public void testResolveAttributeValue() {
        Cookies.Cookie cookie1 = mock(Cookies.Cookie.class);
        Cookies.Cookie cookie2 = mock(Cookies.Cookie.class);
        when(cookie1.getValue()).thenReturn("baa");
        when(cookie2.getValue()).thenReturn("baz");
        when(requestContext.getCookies()).thenReturn(cookies);
        when(cookies.getCookie("foo")).thenReturn(cookie1);
        Object actual = resolver.resolveValue(requestContext, metadata, "foo", null);
        assertThat((String) actual, is("baa"));
    }

    @Test
    public void testResolveAttributeValueSpecifyTypes() {
        Cookies.Cookie cookie1 = mock(Cookies.Cookie.class);
        Cookies.Cookie cookie2 = mock(Cookies.Cookie.class);
        when(cookie1.getValue()).thenReturn("baa");
        when(cookie2.getValue()).thenReturn("baz");
        when(requestContext.getCookies()).thenReturn(cookies);
        when(cookies.getCookie("foo")).thenReturn(cookie1);
        Object actual = resolver
                .resolveValue(requestContext, metadata, "foo", Cookies.Cookie.class);
        assertThat(((Cookies.Cookie) actual).getValue(), is("baa"));
    }

    @Test
    public void testResolveAttributeValueNotMatchCookie() {
        when(requestContext.getCookies()).thenReturn(cookies);
        when(cookies.getCookie("baa")).thenReturn(null);
        Object actual = resolver.resolveValue(requestContext, metadata, "baa", null);
        assertNull(actual);
    }

    @Test
    public void testResolveAttributeValueCookiesNotAvairable() {
        when(requestContext.getCookies()).thenReturn(null);
        Object actual = resolver.resolveValue(requestContext, metadata, "foo", null);
        assertNull(actual);
    }
}
