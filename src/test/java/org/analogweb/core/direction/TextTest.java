package org.analogweb.core.direction;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.analogweb.RequestContext;
import org.analogweb.mock.MockServletOutputStream;
import org.analogweb.util.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TextTest {

    private RequestContext context;
    private HttpServletResponse response;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testDefaultText() throws Exception {
        final String charset = Charset.defaultCharset().displayName();
        final String responseText = "this is test!";
        final Text actual = Text.with(responseText);

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        assertThat(actual.getContentType(), is("text/plain; charset=" + charset));
        assertThat(actual.getCharset(), is(Charset.defaultCharset().displayName()));

        actual.render(context);

        assertThat(out.toString(), is(responseText));
        assertThat(actual.toString(), is(responseText));

        verify(response).setContentType("text/plain; charset=" + charset);
    }

    @Test
    public void testDefaultTextEmptyCharset() throws Exception {
        final String charset = "";
        final String responseText = "this is test!";
        final Text actual = Text.with(responseText).withCharset(charset);

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        assertThat(actual.getContentType(), is("text/plain; charset="
                + Charset.defaultCharset().displayName()));
        assertThat(actual.getCharset(), is(Charset.defaultCharset().displayName()));

        actual.render(context);

        assertThat(out.toString(), is(responseText));

        verify(response).setContentType(
                "text/plain; charset=" + Charset.defaultCharset().displayName());
    }

    @Test
    public void testDefaultTextWithoutCharset() throws Exception {
        final String responseText = "this is test!";
        final Text actual = Text.with(responseText).withoutCharset();

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        assertThat(actual.getContentType(), is("text/plain"));
        assertThat(actual.getCharset(), is(StringUtils.EMPTY));

        actual.render(context);

        assertThat(out.toString(), is(responseText));

        verify(response).setContentType("text/plain");
    }

    @Test
    public void testXMLText() throws Exception {
        final String charset = Charset.defaultCharset().displayName();
        final String responseText = "<root/>";
        final Text actual = Text.with(responseText).as("text/xml");

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        assertThat(actual.getContentType(), is("text/xml; charset=" + charset));
        assertThat(actual.getCharset(), is(charset));

        actual.render(context);

        assertThat(out.toString(), is(responseText));

        verify(response).setContentType("text/xml; charset=" + charset);
    }

    @Test
    public void testJSONText() throws Exception {
        final String charset = "utf-8";
        final String responseText = "{\"foo\",\"baa\"}";
        final Text actual = Text.with(responseText).as("application/json").withCharset(charset);

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        assertThat(actual.getContentType(), is("application/json; charset=" + charset));

        actual.render(context);

        assertThat(out.toString(), is(responseText));

        verify(response).setContentType("application/json; charset=" + charset);
    }

    @Test
    public void testMultibyteText() throws Exception {
        final String responseText = "これはテストです";
        final Text actual = Text.with(responseText).withCharset("Shift-JIS");

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        assertThat(actual.getContentType(), is("text/plain; charset=Shift-JIS"));
        assertThat(actual.getCharset(), is("Shift-JIS"));

        actual.render(context);

        assertThat(out.toString("Shift-JIS"), is(responseText));
    }

    @Test
    public void testEmptyTextResponse() throws Exception {
        final String responseText = null;
        final Text actual = Text.with(responseText);

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        actual.render(context);

        assertThat(out.toString(), is(StringUtils.EMPTY));
    }

    @Test
    public void testTextResponseWithUnknownCharset() throws Exception {
        thrown.expect(UnsupportedEncodingException.class);
        final String responseText = "this is test!";
        final Text actual = Text.with(responseText).withCharset("*unknown*");

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        actual.render(context);
    }

}
