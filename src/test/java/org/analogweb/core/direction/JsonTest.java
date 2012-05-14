package org.analogweb.core.direction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.analogweb.DirectionFormatter;
import org.analogweb.RequestContext;
import org.analogweb.exception.FormatFailureException;
import org.analogweb.mock.MockServletOutputStream;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JsonTest {

    private RequestContext context;
    private HttpServletResponse response;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
        response = mock(HttpServletResponse.class);
    }

    @After
    public void tearDown() throws Exception {
        Json.flushCache();
    }

    @Test
    public void testSingleObject() throws Exception {
        String charset = "UTF-8";

        Date birthDay = new SimpleDateFormat("yyyyMMdd").parse("20110420");
        Simple bean = new Simple("foo", 33, birthDay);
        Json json = Json.as(bean);

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        assertThat(json.getContentType(), is("application/json; charset=" + charset));
        assertThat(json.getCharset(), is(charset));

        json.render(context);

        String actual = out.toString(charset);

        assertThat(actual, is("{\"age\": 33,\"birthDay\": " + birthDay.getTime()
                + ",\"name\": \"foo\"}"));
    }

    @Test
    public void testSingleObjectWithIOException() throws Exception {
        thrown.expect(formatFailureExceptionCauseOfIOException());
        String charset = "UTF-8";

        Date birthDay = new SimpleDateFormat("yyyyMMdd").parse("20110420");
        Simple bean = new Simple("foo", 33, birthDay);
        Json json = Json.as(bean);

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException();
            }
        };
        when(response.getOutputStream()).thenReturn(out);

        assertThat(json.getContentType(), is("application/json; charset=" + charset));
        assertThat(json.getCharset(), is(charset));

        json.render(context);
    }

    private Matcher<FormatFailureException> formatFailureExceptionCauseOfIOException() {
        return new BaseMatcher<FormatFailureException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof FormatFailureException) {
                    FormatFailureException ex = (FormatFailureException) arg0;
                    return IOException.class.isInstance(ex.getCause());
                }
                return false;
            }

            @Override
            public void describeTo(Description arg0) {
                // nop.

            }

        };
    }

    @Test
    public void testString() throws Exception {
        String charset = "UTF-8";

        Json json = Json.with("{\"value\": \"foo!\"}");

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        json.render(context);

        String actual = out.toString(charset);

        assertThat(actual, is("{\"value\": \"foo!\"}"));
    }

    @Test
    public void testList() throws Exception {
        String charset = "UTF-8";

        Date birthDay = new SimpleDateFormat("yyyyMMdd").parse("20110420");
        Date birthDay2 = new SimpleDateFormat("yyyyMMdd").parse("20110712");
        List<Simple> beans = Arrays.asList(new Simple("foo", 33, birthDay), new Simple("baa", 32,
                birthDay2));
        Json json = Json.as(beans);

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        assertThat(json.getContentType(), is("application/json; charset=" + charset));
        assertThat(json.getCharset(), is(charset));

        json.render(context);

        String actual = out.toString(charset);

        assertThat(actual, is("{[" + "{\"age\": 33,\"birthDay\": " + birthDay.getTime()
                + ",\"name\": \"foo\"}," + "{\"age\": 32,\"birthDay\": " + birthDay2.getTime()
                + ",\"name\": \"baa\"}" + "]}"));
    }

    @Test
    public void testArray() throws Exception {
        String charset = "UTF-8";

        Date birthDay = new SimpleDateFormat("yyyyMMdd").parse("20110420");
        Date birthDay2 = new SimpleDateFormat("yyyyMMdd").parse("20110712");
        Simple[] beans = new Simple[] { new Simple("foo", 33, birthDay),
                new Simple("baa", 32, birthDay2) };
        Json json = Json.as(beans);

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        assertThat(json.getContentType(), is("application/json; charset=" + charset));
        assertThat(json.getCharset(), is(charset));

        json.render(context);

        String actual = out.toString(charset);

        assertThat(actual, is("{[" + "{\"age\": 33,\"birthDay\": " + birthDay.getTime()
                + ",\"name\": \"foo\"}," + "{\"age\": 32,\"birthDay\": " + birthDay2.getTime()
                + ",\"name\": \"baa\"}" + "]}"));
    }

    @Test
    public void testManyList() throws Exception {

        Date birthDay = new SimpleDateFormat("yyyyMMdd").parse("20110420");
        Date birthDay2 = new SimpleDateFormat("yyyyMMdd").parse("20100712");
        Json json = Json.as(new ManyList());

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        json.render(context);

        String actual = out.toString("UTF-8");

        assertThat(actual, is("{\"boo\": true,\"simples\": [" + "{\"age\": 33,\"birthDay\": "
                + birthDay.getTime() + ",\"name\": \"foo\"}," + "{\"age\": 32,\"birthDay\": "
                + birthDay2.getTime() + ",\"name\": \"baa\"}" + "]}"));
    }

    @Test
    public void testManyArray() throws Exception {

        Date birthDay = new SimpleDateFormat("yyyyMMdd").parse("20110420");
        Date birthDay2 = new SimpleDateFormat("yyyyMMdd").parse("20100712");
        Json jsons = Json.as(new ManyArray());

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        jsons.render(context);

        String actual = out.toString("UTF-8");

        assertThat(actual, is("{\"id\": \"01\",\"simples\": [" + "{\"age\": 33,\"birthDay\": "
                + birthDay.getTime() + ",\"name\": \"foo\"}," + "{\"age\": 32,\"birthDay\": "
                + birthDay2.getTime() + ",\"name\": \"baa\"}" + "]}"));
    }

    @Test
    public void testContainsNull() throws Exception {
        String charset = "UTF-8";

        Simple bean = new Simple("foo", 33, null);
        Json json = Json.as(bean);

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        json.render(context);

        String actual = out.toString(charset);

        assertThat(actual, is("{\"age\": 33,\"birthDay\": null,\"name\": \"foo\"}"));
    }

    @Test
    public void testReplaceFormatter() throws Exception {
        DirectionFormatter formatter = mock(DirectionFormatter.class);

        Simple bean = new Simple("foo", 33, null);
        Json json = Json.as(bean).withCharset("Shift-JIS").attach(formatter);

        when(context.getResponse()).thenReturn(response);
        final MockServletOutputStream out = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(out);

        json.render(context);

        verify(formatter).formatAndWriteInto(context, "Shift-JIS", bean);
    }

    static class Simple implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private Integer age;
        private Date birthDay;

        Simple(String name, Integer age, Date birthDay) {
            this.name = name;
            this.age = age;
            this.birthDay = birthDay;
        }

        public String getName() {
            return this.name;
        }

        public Integer getAge() {
            return this.age;
        }

        public Date getBirthDay() {
            return this.birthDay;
        }
    }

    static class ManyList implements Serializable {
        private static final long serialVersionUID = 1L;
        private boolean boo;
        private List<Simple> simples;

        ManyList() {
            boo = true;
            simples = new ArrayList<Simple>();
            try {
                simples.add(new Simple("foo", 33, new SimpleDateFormat("yyyyMMdd")
                        .parse("20110420")));
                simples.add(new Simple("baa", 32, new SimpleDateFormat("yyyyMMdd")
                        .parse("20100712")));
            } catch (ParseException e) {
                // nop.
            }
        }

        public boolean getBoo() {
            return this.boo;
        }

        public List<Simple> getSimples() {
            return this.simples;
        }
    }

    static class ManyArray implements Serializable {
        private static final long serialVersionUID = 1L;
        private String id = "01";
        private Simple[] simples;

        ManyArray() {
            simples = new Simple[2];
            try {
                simples[0] = new Simple("foo", 33,
                        new SimpleDateFormat("yyyyMMdd").parse("20110420"));
                simples[1] = new Simple("baa", 32,
                        new SimpleDateFormat("yyyyMMdd").parse("20100712"));
            } catch (ParseException e) {
                // nop.
            }
        }

        public String getId() {
            return this.id;
        }

        public Simple[] getSimples() {
            return this.simples;
        }
    }
}
