package org.analogweb.core.direction;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.exception.FormatFailureException;
import org.analogweb.mock.MockServletOutputStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class XmlTest {

    private RequestContext context;
//    private HttpServletResponse response;
    private Headers headers;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
//        response = mock(HttpServletResponse.class);
        headers = mock(Headers.class);
    }

    @Test
    public void testRender() throws Exception {

        String charset = "UTF-8";

        when(context.getResponseHeaders()).thenReturn(headers);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(context.getResponseBody()).thenReturn(out);

        Xml xml = Xml.as(new Foo());
        xml.render(context);

        String actual = out.toString(charset);
        assertThat(
                actual,
                is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><foo><baa>baz!</baa></foo>"));
        verify(headers).putValue("Content-Type","application/xml; charset=UTF-8");
    }

    @Test
    public void testRenderWithInvalidType() throws Exception {

        thrown.expect(FormatFailureException.class);
        when(context.getResponseHeaders()).thenReturn(headers);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(context.getResponseBody()).thenReturn(out);

        // render miss mapped tppe.
        Xml xml = Xml.as(new Hoge());
        xml.render(context);
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
