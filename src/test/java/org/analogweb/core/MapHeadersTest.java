package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class MapHeadersTest {

    private MapHeaders headers;

    @Test
    public void testGetNames() {
        HashMap<String, List<String>> source = new HashMap<String, List<String>>();
        source.put("Content-Type", Arrays.asList("text/html"));
        source.put("Locale", Arrays.asList("ja_JP"));
        headers = new MapHeaders(source);
        List<String> names = headers.getNames();
        assertThat(names.contains("Content-Type"), is(true));
        assertThat(names.contains("Locale"), is(true));
        assertThat(names.contains("Content-Disposition"), is(false));
    }

    @Test
    public void testContains() {
        HashMap<String, List<String>> source = new HashMap<String, List<String>>();
        source.put("Content-Type", Arrays.asList("text/html"));
        source.put("Locale", Arrays.asList("ja_JP"));
        headers = new MapHeaders(source);
        assertThat(headers.contains("Content-Type"), is(true));
        assertThat(headers.contains("Locale"), is(true));
        assertThat(headers.contains("Content-Disposition"), is(false));
    }

    @Test
    public void testGetValues() {
        HashMap<String, List<String>> source = new HashMap<String, List<String>>();
        source.put("Content-Type", Arrays.asList("text/html"));
        source.put("Locale", Arrays.asList("ja_JP"));
        headers = new MapHeaders(source);
        assertThat(headers.getValues("Content-Type").get(0), is("text/html"));
        assertThat(headers.getValues("Locale").get(0), is("ja_JP"));
    }

    @Test
    public void testPutValue() {
        HashMap<String, List<String>> source = new HashMap<String, List<String>>();
        List<String> accept = new ArrayList<String>();
        accept.add("text/html");
        source.put("Accept", accept);
        source.put("Locale", Arrays.asList("ja_JP"));
        headers = new MapHeaders(source);
        headers.putValue("Accept", "text/plain");
        headers.putValue("Cookie", "foo=baa");
        assertThat(headers.getValues("Accept").get(0), is("text/html"));
        assertThat(headers.getValues("Accept").get(1), is("text/plain"));
        assertThat(headers.getValues("Locale").get(0), is("ja_JP"));
        assertThat(headers.getValues("Cookie").get(0), is("foo=baa"));
        assertThat(headers.getValues("Hoge").isEmpty(), is(true));
    }
}
