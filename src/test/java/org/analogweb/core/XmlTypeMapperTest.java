package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Test;

public class XmlTypeMapperTest {

    private XmlTypeMapper mapper;
    private RequestContext context;
    private Headers headers;

    @Before
    public void setUp() throws Exception {
        mapper = new XmlTypeMapper();
        context = mock(RequestContext.class);
        headers = mock(Headers.class);
    }

    @Test
    public void testMapToTypeByStream() {
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Content-Type")).thenReturn(Arrays.asList("text/xml"));
        byte[] xmlBytes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><hello><world>snowgoose</world></hello>"
                .getBytes();
        InputStream xmlBody = new ByteArrayInputStream(xmlBytes);
        Hello actual = (Hello) mapper.mapToType(context, xmlBody, Hello.class, new String[0]);
        assertThat(actual.getWorld(), is("snowgoose"));
    }

    @Test
    public void testMapToTypeByReader() {
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Content-Type")).thenReturn(Arrays.asList("application/xml"));
        StringReader xmlBody = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><hello><world>snowgoose</world></hello>");
        Hello actual = (Hello) mapper.mapToType(context, xmlBody, Hello.class, new String[0]);
        assertThat(actual.getWorld(), is("snowgoose"));
    }

    @Test
    public void testMapToTypeBytes() {
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Content-Type")).thenReturn(Arrays.asList("text/xml"));
        byte[] xmlBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><hello><world>snowgoose</world></hello>"
                .getBytes();
        Hello actual = (Hello) mapper.mapToType(context, xmlBody, Hello.class, new String[0]);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testMapToTypeIllegalClassType() {
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Content-Type")).thenReturn(Arrays.asList("text/xml"));
        byte[] xmlBytes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><hello><world>snowgoose</world></hello>"
                .getBytes();
        InputStream xmlBody = new ByteArrayInputStream(xmlBytes);
        Hello actual = (Hello) mapper.mapToType(context, xmlBody, UnHello.class, new String[0]);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testMapToTypeIllegalContentType() {
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Content-Type")).thenReturn(Arrays.asList("application/json"));
        byte[] xmlBytes = "{\"hello\":\"world\"}".getBytes();
        InputStream xmlBody = new ByteArrayInputStream(xmlBytes);
        Hello actual = (Hello) mapper.mapToType(context, xmlBody, UnHello.class, new String[0]);
        assertThat(actual, is(nullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapToTypeWithoutContentType() {
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Content-Type")).thenReturn(Collections.EMPTY_LIST);
        byte[] xmlBytes = "plain text".getBytes();
        InputStream xmlBody = new ByteArrayInputStream(xmlBytes);
        Hello actual = (Hello) mapper.mapToType(context, xmlBody, UnHello.class, new String[0]);
        assertThat(actual, is(nullValue()));
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
