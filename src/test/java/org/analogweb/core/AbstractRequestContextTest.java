package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.analogweb.Headers;
import org.analogweb.RequestPath;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgooseyk
 */
public class AbstractRequestContextTest {

    private AbstractRequestContext context;
    private RequestPath path;
    private Locale locale = Locale.JAPAN;
    private Headers headers;

    @Before
    public void setUp() throws Exception {
        path = mock(RequestPath.class);
        headers = mock(Headers.class);
        context = new StubRequestContext(path, locale, headers, new ByteArrayInputStream(
                "This is test.".getBytes()));
    }

    @Test
    public void testGetContentLength() {
        when(headers.getValues("Content-Length")).thenReturn(Arrays.asList("13"));
        long actual = context.getContentLength();
        assertThat(actual, is(13L));
        when(headers.getValues("Content-Length")).thenReturn(Arrays.asList(""));
        actual = context.getContentLength();
        assertThat(actual, is(0L));
        when(headers.getValues("Content-Length")).thenReturn(Collections.<String> emptyList());
        actual = context.getContentLength();
        assertThat(actual, is(0L));
    }

    @Test
    public void testGetCharacterEncoding() {
        when(headers.getValues("Content-Type")).thenReturn(
                Arrays.asList("text/html; charset=ISO-8859-4"));
        String actual = context.getCharacterEncoding();
        assertThat(actual, is("ISO-8859-4"));
        when(headers.getValues("Content-Type")).thenReturn(Arrays.asList("text/html"));
        actual = context.getCharacterEncoding();
        assertThat(actual, is("UTF-8"));
        when(headers.getValues("Content-Type")).thenReturn(Collections.<String> emptyList());
        actual = context.getCharacterEncoding();
        assertThat(actual, is("UTF-8"));
    }

    private final class StubRequestContext extends AbstractRequestContext {

        private Headers headers;
        private InputStream in;
        private String method;

        protected StubRequestContext(RequestPath requestPath, Locale defaultLocale,
                Headers headers, InputStream in) {
            this(requestPath, defaultLocale, headers, in, "GET");
        }

        protected StubRequestContext(RequestPath requestPath, Locale defaultLocale,
                Headers headers, InputStream in, String method) {
            super(requestPath, defaultLocale);
            this.headers = headers;
            this.in = in;
        }

        @Override
        public Headers getRequestHeaders() {
            return headers;
        }

        @Override
        public InputStream getRequestBody() throws IOException {
            return in;
        }

        @Override
        public String getRequestMethod() {
            return method;
        }
    }
}
