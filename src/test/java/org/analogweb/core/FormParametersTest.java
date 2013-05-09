package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.analogweb.MediaType;
import org.junit.Test;

public class FormParametersTest {

    private FormParameters params;

    @Test
    public void testGetValues() throws Exception {
        URI requestURI = URI.create("http://localhost:80/a?b=c");
        InputStream body = new ByteArrayInputStream("foo=baa&hoge=fuga&hoge=hoge%21".getBytes());
        MediaType contentType = MediaTypes.APPLICATION_FORM_URLENCODED_TYPE;
        params = new FormParameters(requestURI, body, contentType);
        List<String> actual = params.getValues("foo");
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is("baa"));
        actual = params.getValues("hoge");
        assertThat(actual.size(), is(2));
        assertThat(actual.get(0), is("fuga"));
        assertThat(actual.get(1), is("hoge!"));
        // not avairable.
        actual = params.getValues("baz");
        assertThat(actual.isEmpty(), is(true));
        actual = params.getValues("b");
        assertThat(actual.isEmpty(), is(true));
    }

    @Test
    public void testAsMap() throws Exception {
        URI requestURI = URI.create("http://localhost:80/a");
        InputStream body = new ByteArrayInputStream(
                "%E5%A0%B4%E6%89%80=Tokyo&%E5%90%8D%E5%89%8D=Yukio".getBytes("UTF-8"));
        MediaType contentType = MediaTypes
                .valueOf("application/x-www-form-urlencoded; charset=UTF-8");
        params = new FormParameters(requestURI, body, contentType);
        Map<String, String[]> map = params.asMap();
        String[] actual = map.get("名前");
        assertThat(actual.length, is(1));
        assertThat(actual[0], is("Yukio"));
        actual = map.get("場所");
        assertThat(actual.length, is(1));
        assertThat(actual[0], is("Tokyo"));
        actual = map.get("foo");
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testAsMapOtherEncoding() throws Exception {
        URI requestURI = URI.create("http://localhost:80/a");
        InputStream body = new ByteArrayInputStream(
                "%E5%A0%B4%E6%89%80=Tokyo&%E5%90%8D%E5%89%8D=Yukio".getBytes("UTF-8"));
        MediaType contentType = MediaTypes
                .valueOf("application/x-www-form-urlencoded; charset=UTF-8");
        params = new FormParameters(requestURI, body, contentType);
        Map<String, String[]> map = params.asMap();
        String[] actual = map.get("名前");
        assertThat(actual.length, is(1));
        assertThat(actual[0], is("Yukio"));
        actual = map.get("場所");
        assertThat(actual.length, is(1));
        assertThat(actual[0], is("Tokyo"));
        actual = map.get("foo");
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testAsMapIsEmpty() {
        URI requestURI = URI.create("http://localhost:80/a");
        InputStream body = new ByteArrayInputStream("".getBytes());
        MediaType contentType = MediaTypes.valueOf("text/plain");
        params = new FormParameters(requestURI, body, contentType);
        Map<String, String[]> actual = params.asMap();
        assertThat(actual.isEmpty(), is(true));
    }
}
