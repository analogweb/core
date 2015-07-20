package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Test;

public class XmlValueResolverTest {

    private XmlValueResolver mapper;
    private RequestContext context;
    private Headers headers;

    @Before
    public void setUp() throws Exception {
        mapper = new XmlValueResolver();
        context = mock(RequestContext.class);
        headers = mock(Headers.class);
    }

    @Test
    public void testMapToTypeByStream() throws Exception {
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Content-Type")).thenReturn(Arrays.asList("text/xml"));
        byte[] xmlBytes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><hello><world>snowgoose</world></hello>"
                .getBytes();
        InputStream xmlBody = new ByteArrayInputStream(xmlBytes);
        when(context.getRequestBody()).thenReturn(xmlBody);
        Hello actual = (Hello) mapper.resolveValue(context, null, null, Hello.class, null);
        assertThat(actual.getWorld(), is("snowgoose"));
    }

    @Test
    public void testMapToTypeIllegalClassType() throws Exception {
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Content-Type")).thenReturn(Arrays.asList("text/xml"));
        byte[] xmlBytes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><hello><world>snowgoose</world></hello>"
                .getBytes();
        InputStream xmlBody = new ByteArrayInputStream(xmlBytes);
        when(context.getRequestBody()).thenReturn(xmlBody);
        UnHello actual = (UnHello) mapper.resolveValue(context, null, null, UnHello.class, null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testMapToTypeIllegalContentType() throws Exception {
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Content-Type")).thenReturn(Arrays.asList("application/json"));
        byte[] xmlBytes = "{\"hello\":\"world\"}".getBytes();
        InputStream xmlBody = new ByteArrayInputStream(xmlBytes);
        when(context.getRequestBody()).thenReturn(xmlBody);
        UnHello actual = (UnHello) mapper.resolveValue(context, null, null, UnHello.class, null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapToTypeWithoutContentType() throws Exception {
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Content-Type")).thenReturn(Collections.EMPTY_LIST);
        byte[] xmlBytes = "plain text".getBytes();
        InputStream xmlBody = new ByteArrayInputStream(xmlBytes);
        when(context.getRequestBody()).thenReturn(xmlBody);
        UnHello actual = (UnHello) mapper.resolveValue(context, null, null, UnHello.class, null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testSupports() {
        assertThat(mapper.supports(MediaTypes.TEXT_XML_TYPE), is(true));
        assertThat(mapper.supports(MediaTypes.APPLICATION_XML_TYPE), is(true));
        assertThat(mapper.supports(MediaTypes.APPLICATION_SVG_XML_TYPE), is(true));
        assertThat(mapper.supports(MediaTypes.APPLICATION_ATOM_XML_TYPE), is(true));
        assertThat(mapper.supports(MediaTypes.APPLICATION_JSON_TYPE), is(false));
        assertThat(mapper.supports(MediaTypes.TEXT_PLAIN_TYPE), is(false));
    }

    @XmlRootElement
    static class Hello implements Serializable {

        private static final long serialVersionUID = 1L;
        @XmlElement
        private String world;

        public String getWorld() {
            return this.world;
        }
    }

    @XmlRootElement
    static class UnHello implements Serializable {

        private static final long serialVersionUID = 1L;
        @XmlElement
        private String world;

        public String getWorld() {
            return this.world;
        }
    }
}
