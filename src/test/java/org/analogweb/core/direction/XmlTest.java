package org.analogweb.core.direction;

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
import org.analogweb.ResponseContext.ResponseWriter;
import org.analogweb.core.DefaultResponseWriter;
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
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);

        Xml xml = Xml.as(new Foo());
        xml.render(context, response);

        writer.getEntity().writeInto(out);
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
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);

        // render miss mapped type.
        Xml xml = Xml.as(new Hoge());
        xml.render(context, response);

        writer.getEntity().writeInto(out);
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
