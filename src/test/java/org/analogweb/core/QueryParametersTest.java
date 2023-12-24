package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class QueryParametersTest {

    private QueryParameters params;

    @Test
    public void testGetValues() {
        params = new QueryParameters(URI.create("http://localhost:80/a?foo=baa&hoge=fuga&hoge=hoge%21"));
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
    }

    @Test
    public void testAsMap() {
        params = new QueryParameters(URI.create("http://localhost:80/a?foo=1&baa=2&baz=33&foo=10"));
        Map<String, String[]> map = params.asMap();
        String[] actual = map.get("foo");
        assertThat(actual.length, is(2));
        assertThat(actual[0], is("1"));
        assertThat(actual[1], is("10"));
        actual = map.get("baa");
        assertThat(actual.length, is(1));
        assertThat(actual[0], is("2"));
        actual = map.get("baz");
        assertThat(actual.length, is(1));
        assertThat(actual[0], is("33"));
    }

    @Test
    public void testAsMapIsEmpty() {
        params = new QueryParameters(URI.create("http://localhost:80/a"));
        Map<String, String[]> actual = params.asMap();
        assertThat(actual.isEmpty(), is(true));
    }
}
