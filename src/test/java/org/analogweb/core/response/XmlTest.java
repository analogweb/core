package org.analogweb.core.response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.Response;
import org.analogweb.core.DefaultWritableBuffer;
import org.analogweb.core.FormatFailureException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class XmlTest {

    private RequestContext context;
    private ResponseContext response;
    private Headers headers;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
        response = mock(ResponseContext.class);
        headers = mock(Headers.class);
    }

    @Test
    public void testRender() throws Exception {
        String charset = "UTF-8";
        when(response.getResponseHeaders()).thenReturn(headers);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Xml xml = Xml.as(new Foo());
        Response r = xml.render(context, response);
        r.getEntity().writeInto(DefaultWritableBuffer.writeBuffer(out));
        String actual = new String(out.toByteArray(), charset);
        assertThat(
                actual,
                is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><foo><baa>baz!</baa></foo>"));
        verify(headers).putValue("Content-Type", "application/xml; charset=UTF-8");
    }

    @Test
    public void testRenderWithInvalidType() throws Exception {
        thrown.expect(FormatFailureException.class);
        when(response.getResponseHeaders()).thenReturn(headers);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        // render miss mapped type.
        Xml xml = Xml.as(new Hoge());
        Response r = xml.render(context, response);
        r.getEntity().writeInto(DefaultWritableBuffer.writeBuffer(out));
    }

    @XmlRootElement
    static class Foo {

        @XmlElement
        private String baa = "baz!";
    }

    // miss mapping.
    @XmlType
    static class Hoge {

        @XmlElement
        private String baa = "baz!";
    }
}
