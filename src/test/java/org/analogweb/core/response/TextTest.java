package org.analogweb.core.response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.Response;
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
        assertThat(actual.resolveContentType(), is("text/plain; charset=" + charset));
        assertThat(actual.getCharsetAsText(), is(Charset.defaultCharset().displayName()));
        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);
        Response r = actual.render(context, response);
        r.getEntity().writeInto(out);
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
        assertThat(actual.resolveContentType(), is("text/plain; charset="
                + Charset.defaultCharset().displayName()));
        assertThat(actual.getCharsetAsText(), is(Charset.defaultCharset().displayName()));
        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);
        Response r = actual.render(context, response);
        r.getEntity().writeInto(out);
        assertThat(new String(out.toByteArray()), is(responseText));
        verify(headers).putValue("Content-Type",
                "text/plain; charset=" + Charset.defaultCharset().displayName());
    }

    @Test
    public void testDefaultTextWithoutCharset() throws Exception {
        final String responseText = "this is test!";
        final Text actual = Text.with(responseText).withoutCharset();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertThat(actual.resolveContentType(), is("text/plain"));
        assertThat(actual.getCharsetAsText(), is(StringUtils.EMPTY));
        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);
        Response r = actual.render(context, response);
        r.getEntity().writeInto(out);
        assertThat(new String(out.toByteArray()), is(responseText));
        verify(headers).putValue("Content-Type", "text/plain");
    }

    @Test
    public void testXMLText() throws Exception {
        final String charset = Charset.defaultCharset().displayName();
        final String responseText = "<root/>";
        final Text actual = Text.with(responseText).typeAs("text/xml");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertThat(actual.resolveContentType(), is("text/xml; charset=" + charset));
        assertThat(actual.getCharsetAsText(), is(charset));
        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);
        Response r = actual.render(context, response);
        r.getEntity().writeInto(out);
        assertThat(new String(out.toByteArray()), is(responseText));
        verify(headers).putValue("Content-Type", "text/xml; charset=" + charset);
    }

    @Test
    public void testJSONText() throws Exception {
        final String charset = "utf-8";
        final String responseText = "{\"foo\",\"baa\"}";
        final Text actual = Text.with(responseText).typeAs("application/json").withCharset(charset);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertThat(actual.resolveContentType(), is("application/json; charset=" + charset));
        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);
        Response r = actual.render(context, response);
        r.getEntity().writeInto(out);
        assertThat(new String(out.toByteArray()), is(responseText));
        verify(headers).putValue("Content-Type", "application/json; charset=" + charset);
    }

    @Test
    public void testMultibyteText() throws Exception {
        final String responseText = "これはテストです";
        final Text actual = Text.with(responseText).withCharset("Shift-JIS");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertThat(actual.resolveContentType(), is("text/plain; charset=Shift-JIS"));
        assertThat(actual.getCharsetAsText(), is("Shift-JIS"));
        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);
        Response r = actual.render(context, response);
        r.getEntity().writeInto(out);
        assertThat(new String(out.toByteArray(), "Shift-JIS"), is(responseText));
    }

    @Test
    public void testEmptyTextResponse() throws Exception {
        final String responseText = null;
        final Text actual = Text.with(responseText);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);
        Response r = actual.render(context, response);
        r.getEntity().writeInto(out);
        assertThat(new String(out.toByteArray()), is(StringUtils.EMPTY));
    }

    @Test
    public void testTextResponseWithUnknownCharset() throws Exception {
        thrown.expect(IllegalCharsetNameException.class);
        final String responseText = "this is test!";
        final Text actual = Text.with(responseText).withCharset("*unknown*");
        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);
        actual.render(context, response);
    }
}
