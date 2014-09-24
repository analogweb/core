package org.analogweb.core.httpserver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.analogweb.junit.ExceptionCauseMatchers.as;
import static org.analogweb.junit.ExceptionCauseMatchers.causedBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.analogweb.RequestPath;
import org.analogweb.core.ApplicationRuntimeException;
import org.analogweb.core.MediaTypes;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sun.net.httpserver.Headers;

/**
 * @author snowgoose
 */
public class HttpExchangeRequestContextTest {

    private HttpExchangeRequestContext context;
    private RequestPath requestPath;
    private MockHttpExchange ex;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        ex = new MockHttpExchange();
        requestPath = mock(RequestPath.class);
    }

    @Test
    public void testGetContentType() {
        context = new HttpExchangeRequestContext(ex, requestPath, Locale.getDefault());
        Headers headers = new Headers();
        headers.put("Content-Type", Arrays.asList("text/xml"));
        ex.setRequestHeaders(headers);
        assertThat(context.getContentType().toString(), is(MediaTypes.TEXT_XML_TYPE.toString()));
        ex.setRequestHeaders(new Headers());
        assertThat(context.getContentType(), is(nullValue()));
    }

    @Test
    public void testGetCookies() {
        context = new HttpExchangeRequestContext(ex, requestPath, Locale.getDefault());
        Headers headers = new Headers();
        headers.put("Cookie", Arrays.asList("foo=baa"));
        ex.setRequestHeaders(headers);
        assertThat(context.getCookies().getCookie("foo").getValue(), is("baa"));
        ex.setRequestHeaders(new Headers());
        assertThat(context.getCookies().getCookie("foo"), is(nullValue()));
    }

    @Test
    public void testGetRequestBody() throws Exception {
        context = new HttpExchangeRequestContext(ex, requestPath, Locale.getDefault());
        InputStream expected = new ByteArrayInputStream(new byte[0]);
        ex.setRequestBody(expected);
        InputStream actual = context.getRequestBody();
        assertThat(actual, is(expected));
    }

    @Test
    public void testGetLocale() {
        Headers headers = new Headers();
        headers.put("Accept-Language",
                Arrays.asList("en-ca,en;q=0.8,en-us;q=0.6,de-de;q=0.4,de;q=0.2,ja_JP"));
        ex.setRequestHeaders(headers);
        context = new HttpExchangeRequestContext(ex, requestPath, Locale.US);
        List<Locale> expected = Arrays.asList(Locale.CANADA, Locale.JAPAN, Locale.ENGLISH,
                Locale.US, Locale.GERMANY, Locale.GERMAN);
        assertThat(context.getLocales(), is(expected));
        assertThat(context.getLocale(), is(Locale.CANADA));
        // empty header.
        ex.setRequestHeaders(new Headers());
        context = new HttpExchangeRequestContext(ex, requestPath, null);
        assertThat(context.getLocales().isEmpty(), is(true));
        assertThat(context.getLocale(), is(nullValue()));
    }

    @Test
    public void testGetParameters() {
        when(requestPath.getRequestURI()).thenReturn(URI.create("http://foo?baa=baz"));
        context = new HttpExchangeRequestContext(ex, requestPath, Locale.getDefault());
        assertThat(context.getQueryParameters().getValues("baa").get(0), is("baz"));
    }

    @Test
    public void testGetMatrixParameters() {
        when(requestPath.getRequestURI()).thenReturn(URI.create("http://foo?a=b;baa=baz"));
        context = new HttpExchangeRequestContext(ex, requestPath, Locale.getDefault());
        assertThat(context.getMatrixParameters().getValues("baa").get(0), is("baz"));
    }

    @Test
    public void testGetFormParameters() {
        when(requestPath.getRequestURI()).thenReturn(URI.create("http://foo"));
        Headers headers = new Headers();
        headers.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
        ex.setRequestHeaders(headers);
        ex.setRequestBody(new ByteArrayInputStream("baa=baz".getBytes()));
        context = new HttpExchangeRequestContext(ex, requestPath, Locale.getDefault());
        assertThat(context.getFormParameters().getValues("baa").get(0), is("baz"));
    }

    @Test
    public void testGetFormParametersRaiseException() {
        thrown.expect(as(ApplicationRuntimeException.class, causedBy(IOException.class)));
        when(requestPath.getRequestURI()).thenReturn(URI.create("http://foo"));
        Headers headers = new Headers();
        headers.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
        ex.setRequestHeaders(headers);
        ex.setRequestBody(new InputStream() {

            @Override
            public int read() throws IOException {
                throw new IOException();
            }
        });
        context = new HttpExchangeRequestContext(ex, requestPath, Locale.getDefault());
        context.getFormParameters().getValues("baa");
    }

    @Test
    public void testGetHeaders() {
        when(requestPath.getRequestURI()).thenReturn(URI.create("http://foo"));
        Headers headers = new Headers();
        headers.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
        ex.setRequestHeaders(headers);
        ex.setRequestBody(new ByteArrayInputStream("baa=baz".getBytes()));
        context = new HttpExchangeRequestContext(ex, requestPath, Locale.getDefault());
        org.analogweb.Headers header = context.getRequestHeaders();
        assertThat(header.contains("Content-Type"), is(true));
        assertThat(header.getNames().get(0), is("Content-type"));
        assertThat(header.getValues("Content-Type").get(0), is("application/x-www-form-urlencoded"));
        header.putValue("foo", "baa");
        assertThat(header.contains("foo"), is(true));
    }

    @Test
    public void testGetRequestMethod() {
        context = new HttpExchangeRequestContext(ex, requestPath, Locale.getDefault());
        Headers headers = new Headers();
        headers.put("Content-Type", Arrays.asList("text/xml"));
        ex.setRequestHeaders(headers);
        ex.setRequestMethod("GET");
        assertThat(context.getRequestMethod(), is("GET"));
    }
}
