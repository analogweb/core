package org.analogweb.core.direction;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.analogweb.Direction;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.ResponseContext.ResponseWriter;
import org.analogweb.core.DefaultResponseWriter;
import org.analogweb.WebApplicationException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class AcceptableTest {

    private RequestContext context;
    private ResponseContext response;
    private Headers headers;

    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
        response = mock(ResponseContext.class);
        headers = mock(Headers.class);
    }

    @Test
    public void testRenderAcceptableXML() throws Exception {

        final Member m = new Member("snowgoose", 34);
        final String actual = schenarioRender(" text/xml", m);
        assertThat(
                actual,
                is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><member><name>snowgoose</name><age>34</age></member>"));
    }

    @Test
    public void testRenderAcceptableXMLWithReplacedFormatter() throws Exception {

        final Member m = new Member("snowgoose", 34);
        Acceptable a = Acceptable.as(m);
        a.map(new Direction() {

            @Override
            public void render(RequestContext context, ResponseContext response)
                    throws IOException, WebApplicationException {
                response.getResponseWriter().writeEntity("write with XML");
            }
        }, "text/xml");
        final String actual = schenarioRender(" text/xml", m, a);
        assertThat(actual, is("write with XML"));
    }

    @Test
    public void testRenderAcceptableSecondXML() throws Exception {

        final Member m = new Member("snowgoose", 34);
        final String actual = schenarioRender(" text/x-dvi; q=0.8, application/xml, */*", m);
        assertThat(
                actual,
                is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><member><name>snowgoose</name><age>34</age></member>"));
    }

    @Test
    public void testRenderAcceptableXMLWithQuality() throws Exception {

        final Member m = new Member("snowgoose", 34);
        final String actual = schenarioRender(" text/x-dvi; q=0.8, text/xml; q=6, */*", m);
        assertThat(
                actual,
                is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><member><name>snowgoose</name><age>34</age></member>"));
    }

    @Test
    public void testRenderAcceptableJSON() throws Exception {

        final Member m = new Member("snowgoose", 34);
        final String actual = schenarioRender(" application/json, application/xml", m);
        assertThat(actual, is("{\"age\": 34,\"name\": \"snowgoose\"}"));
    }

    @Test
    public void testRenderAcceptableAny() throws Exception {

        final Member m = new Member("snowgoose", 34);
        //        final String accept = " text/x-dvi,image/png, */*";
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Accept")).thenReturn(
                Arrays.asList("text/x-dvi", "image/png", "*/*"));
        //        when(request.getHeader("Accept")).thenReturn(accept);
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);
        final Direction anyDirection = mock(Direction.class);
        Acceptable.as(m).mapToAny(anyDirection).render(context, response);
        verify(anyDirection).render(context, response);
    }

    @Test
    public void testRenderSwitAcceptableAny() throws Exception {

        final Member m = new Member("snowgoose", 34);
        final String actual = schenarioRender(" text/x-dvi,image/png, */*", m);
        // mapped json.
        assertThat(actual, is("{\"age\": 34,\"name\": \"snowgoose\"}"));
    }

    @Test
    public void testRenderSwitAcceptableAnyWithReplacedFormatter() throws Exception {

        final Member m = new Member("snowgoose", 34);
        Acceptable a = Acceptable.as(m);
        a.mapToAny(new Direction() {

            @Override
            public void render(RequestContext context, ResponseContext response)
                    throws IOException, WebApplicationException {
                response.getResponseWriter().writeEntity("write with ANY");
            }
        });
        final String actual = schenarioRender(" text/x-dvi,image/png, */*", m, a);
        // mapped any.
        assertThat(actual, is("write with ANY"));
    }

    @Test
    public void testRenderSwitchedAcceptable() throws Exception {

        final Member m = new Member("snowgoose", 34);
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Accept")).thenReturn(
                Arrays.asList("text/x-dvi", "image/png", "application/json"));
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);
        final Direction replaceDirection = mock(Direction.class);
        Acceptable.as(m).map(replaceDirection, "application/json").render(context, response);
        verify(replaceDirection).render(context, response);
    }

    @Test
    public void testRenderNotAcceptable() throws Exception {

        final Member m = new Member("snowgoose", 34);
        final String actual = schenarioRender(" text/x-dvi,image/png, text/*", m);
        assertThat(actual, is(""));
        verify(response).setStatus(406);
    }

    @Test
    public void testRenderNotAcceptable2() throws Exception {

        final Member m = new Member("snowgoose", 34);
        when(context.getRequestHeaders()).thenReturn(headers);
        when(headers.getValues("Accept")).thenReturn(
                Arrays.asList("text/x-dvi", "image/png", "*/*"));
        final Direction replaceDirection = mock(Direction.class);
        Acceptable.as(m).mapToAny(replaceDirection).render(context, response);
        verify(replaceDirection).render(context, response);
    }

    private String schenarioRender(final String accept, final Member m) throws Exception {
        return schenarioRender(accept, m, Acceptable.as(m));
    }

    private String schenarioRender(final String accept, final Member m, final Acceptable a)
            throws Exception {

        when(context.getRequestHeaders()).thenReturn(headers);
        Headers responseHeaders = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(responseHeaders);
        when(headers.getValues("Accept")).thenReturn(Arrays.asList(accept.split(",")));
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);

        a.render(context, response);
        ResponseEntity entity = writer.getEntity();
        if (entity != null) {
            entity.writeInto(out);
        }
        return new String(out.toByteArray());
    }

    @Test
    public void testComparator() {
        final List<String> accepts = Arrays.asList(" text/plain", " application/*;level=1",
                " application/json", " */*", " text/html;q=1", " text/*");
        Collections.sort(accepts, new Acceptable.AcceptHeaderComparator());
        assertThat(accepts.size(), is(6));
        assertThat(accepts.get(0), is(" text/html;q=1"));
        assertThat(accepts.get(1), is(" text/plain"));
        assertThat(accepts.get(2), is(" application/json"));
        assertThat(accepts.get(3), is(" application/*;level=1"));
        assertThat(accepts.get(4), is(" text/*"));
        assertThat(accepts.get(5), is(" */*"));
    }

    @XmlRootElement
    public static class Member {
        @XmlElement
        private String name;
        @XmlElement
        private int age;

        public Member() {
            super();
        }

        public Member(final String name, final int age) {
            super();
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

    }
}
