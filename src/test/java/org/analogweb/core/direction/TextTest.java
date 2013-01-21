package org.analogweb.core.direction;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.util.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TextTest {

    private RequestContext context;
    private ResponseContext response;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
        response = mock(ResponseContext.class);
    }

    @Test
    public void testDefaultText() throws Exception {
        final String charset = Charset.defaultCharset().displayName();
        final String responseText = "this is test!";
        final Text actual = Text.with(responseText);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(context.getResponseBody()).thenReturn(out);

        assertThat(actual.getContentType(), is("text/plain; charset=" + charset));
        assertThat(actual.getCharset(), is(Charset.defaultCharset().displayName()));

        Headers headers = mock(Headers.class);
        when(context.getResponseHeaders()).thenReturn(headers);

        actual.render(context,response);

        assertThat(new String(out.toByteArray()), is(responseText));
        assertThat(actual.toString(), is(responseText));

        verify(headers).putValue("Content-Type", "text/plain; charset=" + charset);
    }

    @Test
    public void testDefaultTextEmptyCharset() throws Exception {
        final String charset = "";
        final String responseText = "this is test!";
        final Text actual = Text.with(responseText).withCharset(charset);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(context.getResponseBody()).thenReturn(out);

        assertThat(actual.getContentType(), is("text/plain; charset="
                + Charset.defaultCharset().displayName()));
        assertThat(actual.getCharset(), is(Charset.defaultCharset().displayName()));

        Headers headers = mock(Headers.class);
        when(context.getResponseHeaders()).thenReturn(headers);

        actual.render(context,response);

        assertThat(new String(out.toByteArray()), is(responseText));

        verify(headers).putValue("Content-Type",
                "text/plain; charset=" + Charset.defaultCharset().displayName());
    }

    @Test
    public void testDefaultTextWithoutCharset() throws Exception {
        final String responseText = "this is test!";
        final Text actual = Text.with(responseText).withoutCharset();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(context.getResponseBody()).thenReturn(out);

        assertThat(actual.getContentType(), is("text/plain"));
        assertThat(actual.getCharset(), is(StringUtils.EMPTY));

        Headers headers = mock(Headers.class);
        when(context.getResponseHeaders()).thenReturn(headers);

        actual.render(context,response);

        assertThat(new String(out.toByteArray()), is(responseText));

        verify(headers).putValue("Content-Type", "text/plain");
    }

    @Test
    public void testXMLText() throws Exception {
        final String charset = Charset.defaultCharset().displayName();
        final String responseText = "<root/>";
        final Text actual = Text.with(responseText).typeAs("text/xml");

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(context.getResponseBody()).thenReturn(out);

        assertThat(actual.getContentType(), is("text/xml; charset=" + charset));
        assertThat(actual.getCharset(), is(charset));

        Headers headers = mock(Headers.class);
        when(context.getResponseHeaders()).thenReturn(headers);

        actual.render(context,response);

        assertThat(new String(out.toByteArray()), is(responseText));

        verify(headers).putValue("Content-Type", "text/xml; charset=" + charset);
    }

    @Test
    public void testJSONText() throws Exception {
        final String charset = "utf-8";
        final String responseText = "{\"foo\",\"baa\"}";
        final Text actual = Text.with(responseText).typeAs("application/json").withCharset(charset);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(context.getResponseBody()).thenReturn(out);

        assertThat(actual.getContentType(), is("application/json; charset=" + charset));

        Headers headers = mock(Headers.class);
        when(context.getResponseHeaders()).thenReturn(headers);

        actual.render(context,response);

        assertThat(new String(out.toByteArray()), is(responseText));

        verify(headers).putValue("Content-Type", "application/json; charset=" + charset);
    }

    @Test
    public void testMultibyteText() throws Exception {
        final String responseText = "これはテストです";
        final Text actual = Text.with(responseText).withCharset("Shift-JIS");

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(context.getResponseBody()).thenReturn(out);

        assertThat(actual.getContentType(), is("text/plain; charset=Shift-JIS"));
        assertThat(actual.getCharset(), is("Shift-JIS"));

        Headers headers = mock(Headers.class);
        when(context.getResponseHeaders()).thenReturn(headers);

        actual.render(context,response);

        assertThat(new String(out.toByteArray(), "Shift-JIS"), is(responseText));
    }

    @Test
    public void testEmptyTextResponse() throws Exception {
        final String responseText = null;
        final Text actual = Text.with(responseText);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(context.getResponseBody()).thenReturn(out);

        Headers headers = mock(Headers.class);
        when(context.getResponseHeaders()).thenReturn(headers);

        actual.render(context,response);

        assertThat(new String(out.toByteArray()), is(StringUtils.EMPTY));
    }

    @Test
    public void testTextResponseWithUnknownCharset() throws Exception {
        thrown.expect(UnsupportedEncodingException.class);
        final String responseText = "this is test!";
        final Text actual = Text.with(responseText).withCharset("*unknown*");

        Headers headers = mock(Headers.class);
        when(context.getResponseHeaders()).thenReturn(headers);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(context.getResponseBody()).thenReturn(out);

        actual.render(context,response);
    }

}
