package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.Cookies;
import org.analogweb.Headers;
import org.analogweb.Parameters;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class DefaultRequestContextTest {

    private DefaultRequestContext context;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        servletContext = mock(ServletContext.class);
    }

    @Test
    public void testGetCookies() {
        context = new DefaultRequestContext(request, response, servletContext);
        when(request.getRequestURI()).thenReturn("/baa/baz.rn");
        when(request.getContextPath()).thenReturn("/foo");
        when(request.getMethod()).thenReturn("GET");
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("foo", "baa") });

        Cookies.Cookie actual = context.getCookies().getCookie("foo");
        assertThat(actual.getValue(), is("baa"));
    }

    @Test
    public void testPutCookies() {
        context = new DefaultRequestContext(request, response, servletContext);
        when(request.getRequestURI()).thenReturn("/baa/baz.rn");
        when(request.getContextPath()).thenReturn("/foo");
        when(request.getMethod()).thenReturn("GET");
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("hoge", "fuga") });

        Cookies cookies = context.getCookies();
        cookies.putCookie("foo", "baa");
        Cookies.Cookie cookie = mock(Cookies.Cookie.class);
        when(cookie.getComment()).thenReturn("aComment");
        when(cookie.getDomain()).thenReturn("aDomain");
        when(cookie.getMaxAge()).thenReturn(3600);
        when(cookie.getName()).thenReturn("name");
        when(cookie.getPath()).thenReturn("/");
        when(cookie.getValue()).thenReturn("value");
        when(cookie.getVersion()).thenReturn(1);
        cookies.putCookie(cookie);

        verify(response, times(2)).addCookie(isA(Cookie.class));
    }

    @Test
    public void testGetParameters() {
        context = new DefaultRequestContext(request, response, servletContext);
        when(request.getParameterValues("foo")).thenReturn(new String[] { "baa" });

        Parameters actual = context.getParameters();
        assertThat(actual.getValues("foo").get(0), is("baa"));
    }

    @Test
    public void testRequestHeaders() {
        context = new DefaultRequestContext(request, response, servletContext);
        when(request.getHeaders("foo")).thenReturn(Collections.enumeration(Arrays.asList("baa")));

        Headers actual = context.getRequestHeaders();
        assertThat(actual.getValues("foo").get(0), is("baa"));
    }

    @Test
    public void testRequestBody() throws IOException {
        context = new DefaultRequestContext(request, response, servletContext);
        ServletInputStream expected = new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        when(request.getInputStream()).thenReturn(expected);

        InputStream actual = context.getRequestBody();
        assertThat((ServletInputStream) actual, is(expected));
    }

    @Test
    public void testGetResponseHeaders() {
        context = new DefaultRequestContext(request, response, servletContext);
        when(request.getHeaders("foo")).thenReturn(Collections.enumeration(Arrays.asList("baa")));

        Headers actual = context.getResponseHeaders();
        assertThat(actual.getValues("foo").get(0), is("baa"));

        actual.putValue("baa", "baz");
        verify(response).addHeader("baa", "baz");
    }

    @Test
    public void testGetResponseBody() throws IOException {
        context = new DefaultRequestContext(request, response, servletContext);

        ServletOutputStream expected = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        };
        when(response.getOutputStream()).thenReturn(expected);
        OutputStream actual = context.getResponseBody();
        assertThat((ServletOutputStream) actual, is(expected));
    }

    @Test
    public void testSetResponseCode() {
        context = new DefaultRequestContext(request, response, servletContext);

        context.setResponseStatus(404);
        verify(response).setStatus(404);
    }

}
