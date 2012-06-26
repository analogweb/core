package org.analogweb.core.direction;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.analogweb.Direction;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class AcceptableTest {

    private RequestContext context;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        when(context.getRequest()).thenReturn(request);
        when(context.getResponse()).thenReturn(response);
    }

    @Test
    public void testRenderAcceptableXML() throws Exception {

        final Member m = new Member("snowgoose", 34,
                new SimpleDateFormat("yyyy-MM-dd").parse("1978-04-20"));
        final String actual = schenarioRender(" text/xml", m);
        assertThat(
                actual,
                is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><member><name>snowgoose</name><age>34</age><birthDay>1978-04-20T00:00:00+09:00</birthDay></member>"));
    }

    @Test
    public void testRenderAcceptableSecondXML() throws Exception {

        final Member m = new Member("snowgoose", 34,
                new SimpleDateFormat("yyyy-MM-dd").parse("1978-04-20"));
        final String actual = schenarioRender(" text/x-dvi; q=0.8, application/xml, */*", m);
        assertThat(
                actual,
                is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><member><name>snowgoose</name><age>34</age><birthDay>1978-04-20T00:00:00+09:00</birthDay></member>"));
    }

    @Test
    public void testRenderAcceptableXMLWithQuality() throws Exception {

        final Member m = new Member("snowgoose", 34,
                new SimpleDateFormat("yyyy-MM-dd").parse("1978-04-20"));
        final String actual = schenarioRender(" text/x-dvi; q=0.8, text/xml; q=6, */*", m);
        assertThat(
                actual,
                is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><member><name>snowgoose</name><age>34</age><birthDay>1978-04-20T00:00:00+09:00</birthDay></member>"));
    }

    @Test
    public void testRenderAcceptableJSON() throws Exception {

        final Member m = new Member("snowgoose", 34,
                new SimpleDateFormat("yyyy-MM-dd").parse("1978-04-20"));
        final String actual = schenarioRender(" application/json, application/xml", m);
        assertThat(actual, is("{\"age\": 34,\"birthDay\": 261846000000,\"name\": \"snowgoose\"}"));
    }

    @Test
    public void testRenderAcceptableAny() throws Exception {

        final Member m = new Member("snowgoose", 34,
                new SimpleDateFormat("yyyy-MM-dd").parse("1978-04-20"));
        final String accept = " text/x-dvi,image/png, */*";
        when(request.getHeader("Accept")).thenReturn(accept);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(final int arg0) throws IOException {
                out.write(arg0);
            }
        });
        final Direction anyDirection = mock(Direction.class);
        Acceptable.as(m).mapToAny(anyDirection).render(context);
        verify(anyDirection).render(context);
    }

    @Test
    public void testRenderSwitAcceptableAny() throws Exception {

        final Member m = new Member("snowgoose", 34,
                new SimpleDateFormat("yyyy-MM-dd").parse("1978-04-20"));
        final String actual = schenarioRender(" text/x-dvi,image/png, */*", m);
        // mapped json.
        assertThat(actual, is("{\"age\": 34,\"birthDay\": 261846000000,\"name\": \"snowgoose\"}"));
    }

    @Test
    public void testRenderSwitchedAcceptable() throws Exception {

        final Member m = new Member("snowgoose", 34,
                new SimpleDateFormat("yyyy-MM-dd").parse("1978-04-20"));
        final String accept = " text/x-dvi,image/png, application/json";
        when(request.getHeader("Accept")).thenReturn(accept);
        final Direction replaceDirection = mock(Direction.class);
        Acceptable.as(m).map(replaceDirection, "application/json").render(context);
        verify(replaceDirection).render(context);
    }

    @Test
    public void testRenderNotAcceptable() throws Exception {

        final Member m = new Member("snowgoose", 34,
                new SimpleDateFormat("yyyy-MM-dd").parse("1978-04-20"));
        final String actual = schenarioRender(" text/x-dvi,image/png, text/*", m);
        assertThat(actual, is(""));
        verify(response).setStatus(406);
    }

    @Test
    public void testRenderNotAcceptable2() throws Exception {

        final Member m = new Member("snowgoose", 34,
                new SimpleDateFormat("yyyy-MM-dd").parse("1978-04-20"));
        final String accept = " text/x-dvi,image/png, text/javascript, */*";
        when(request.getHeader("Accept")).thenReturn(accept);
        final Direction replaceDirection = mock(Direction.class);
        Acceptable.as(m).mapToAny(replaceDirection).render(context);
        verify(replaceDirection).render(context);
    }

    private String schenarioRender(final String accept, final Member m) throws Exception {

        when(request.getHeader("Accept")).thenReturn(accept);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(final int arg0) throws IOException {
                out.write(arg0);
            }
        });

        Acceptable.as(m).render(context);
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
        @XmlElement
        private Date birthDay;

        public Member() {
            super();
        }

        public Member(final String name, final int age, final Date birthDay) {
            super();
            this.name = name;
            this.age = age;
            this.birthDay = birthDay;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public Date getBirthDay() {
            return birthDay;
        }

    }
}