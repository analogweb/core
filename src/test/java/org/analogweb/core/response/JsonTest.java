package org.analogweb.core.response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.ResponseWriter;
import org.analogweb.core.DefaultResponseWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JsonTest {

    private RequestContext context;
    private ResponseContext response;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
        response = mock(ResponseContext.class);
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

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);

        assertThat(json.resolveContentType(), is("application/json; charset=" + charset));
        assertThat(json.getCharsetAsText(), is(charset));

        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);

        json.render(context, response);

        writer.getEntity().writeInto(out);
        String actual = new String(out.toByteArray(), charset);

        assertThat(actual, is("{\"age\": 33,\"birthDay\": " + birthDay.getTime()
                + ",\"name\": \"foo\"}"));
        verify(headers).putValue("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    public void testPlainJsonString() throws Exception {
        String charset = "UTF-8";

        Json json = Json.with("{\"value\": \"foo!\"}");

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);

        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);

        json.render(context, response);

        writer.getEntity().writeInto(out);
        String actual = new String(out.toByteArray(), charset);

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

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);

        assertThat(json.resolveContentType(), is("application/json; charset=" + charset));
        assertThat(json.getCharsetAsText(), is(charset));

        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);

        json.render(context, response);

        writer.getEntity().writeInto(out);
        String actual = new String(out.toByteArray(), charset);

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

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);

        assertThat(json.resolveContentType(), is("application/json; charset=" + charset));
        assertThat(json.getCharsetAsText(), is(charset));

        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);

        json.render(context, response);

        writer.getEntity().writeInto(out);
        String actual = new String(out.toByteArray(), charset);

        assertThat(actual, is("{[" + "{\"age\": 33,\"birthDay\": " + birthDay.getTime()
                + ",\"name\": \"foo\"}," + "{\"age\": 32,\"birthDay\": " + birthDay2.getTime()
                + ",\"name\": \"baa\"}" + "]}"));
    }

    @Test
    public void testManyList() throws Exception {

        Date birthDay = new SimpleDateFormat("yyyyMMdd").parse("20110420");
        Date birthDay2 = new SimpleDateFormat("yyyyMMdd").parse("20100712");
        Json json = Json.as(new ManyList());

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);

        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);

        json.render(context, response);

        writer.getEntity().writeInto(out);
        String actual = new String(out.toByteArray(), "UTF-8");

        assertThat(actual, is("{\"boo\": true,\"simples\": [" + "{\"age\": 33,\"birthDay\": "
                + birthDay.getTime() + ",\"name\": \"foo\"}," + "{\"age\": 32,\"birthDay\": "
                + birthDay2.getTime() + ",\"name\": \"baa\"}" + "]}"));
    }

    @Test
    public void testManyArray() throws Exception {

        Date birthDay = new SimpleDateFormat("yyyyMMdd").parse("20110420");
        Date birthDay2 = new SimpleDateFormat("yyyyMMdd").parse("20100712");
        Json jsons = Json.as(new ManyArray());

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);

        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);

        jsons.render(context, response);

        writer.getEntity().writeInto(out);
        String actual = new String(out.toByteArray(), "UTF-8");

        assertThat(actual, is("{\"id\": \"01\",\"simples\": [" + "{\"age\": 33,\"birthDay\": "
                + birthDay.getTime() + ",\"name\": \"foo\"}," + "{\"age\": 32,\"birthDay\": "
                + birthDay2.getTime() + ",\"name\": \"baa\"}" + "]}"));
    }

    @Test
    public void testContainsNull() throws Exception {
        String charset = "UTF-8";

        Simple bean = new Simple("foo", 33, null);
        Json json = Json.as(bean);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ResponseWriter writer = new DefaultResponseWriter();
        when(response.getResponseWriter()).thenReturn(writer);

        Headers headers = mock(Headers.class);
        when(response.getResponseHeaders()).thenReturn(headers);

        json.render(context, response);

        writer.getEntity().writeInto(out);
        String actual = new String(out.toByteArray(), charset);

        assertThat(actual, is("{\"age\": 33,\"birthDay\": null,\"name\": \"foo\"}"));
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
