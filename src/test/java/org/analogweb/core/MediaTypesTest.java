package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.analogweb.MediaType;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class MediaTypesTest {

    @Test
    public void testDefaultType() {
        MediaType actual = MediaTypes.WILDCARD_TYPE;
        assertThat(actual.getType(), is("*"));
        assertThat(actual.getSubType(), is("*"));
        assertThat(actual.getParameters(), is(emptyMap()));
        assertThat(actual.toString(), is("*/*"));
    }

    @Test
    public void testUsualType() {
        MediaType actual = MediaTypes.APPLICATION_JSON_TYPE;
        assertThat(actual.getType(), is("application"));
        assertThat(actual.getSubType(), is("json"));
        assertThat(actual.getParameters(), is(emptyMap()));
        assertThat(actual.toString(), is("application/json"));
    }

    @Test
    public void testValueOf() {
        MediaType actual = MediaTypes.valueOf("application/json");
        assertThat(actual.getType(), is("application"));
        assertThat(actual.getSubType(), is("json"));
        assertThat(actual.getParameters(), is(emptyMap()));
    }

    @Test
    public void testValueOfWithQuery() {
        MediaType actual = MediaTypes.valueOf("text/xml;s=1;q=0.9");
        assertThat(actual.getType(), is("text"));
        assertThat(actual.getSubType(), is("xml"));
        Map<String, String> parameters = actual.getParameters();
        assertThat(parameters.size(), is(2));
        assertThat(parameters.get("q"), is("0.9"));
        assertThat(parameters.get("s"), is("1"));
        assertThat(actual.toString(), is("text/xml;q=0.9;s=1"));

        actual = MediaTypes.valueOf("text/xml;t=;q=0.9");
        assertThat(actual.getType(), is("text"));
        assertThat(actual.getSubType(), is("xml"));
        parameters = actual.getParameters();
        assertThat(parameters.size(), is(2));
        assertThat(parameters.get("q"), is("0.9"));
        assertThat(parameters.get("t"), is(""));

        actual = MediaTypes.valueOf("text/xml;u;q=0.9");
        assertThat(actual.getType(), is("text"));
        assertThat(actual.getSubType(), is("xml"));
        parameters = actual.getParameters();
        assertThat(parameters.size(), is(1));
        assertThat(parameters.get("q"), is("0.9"));
    }

    @Test
    public void testValueOfWithInvalidType() {
        MediaType actual = MediaTypes.valueOf("aaa;s=1;q=0.9");
        assertThat(actual, is(MediaTypes.WILDCARD_TYPE));
    }

    @Test
    public void testValueOfWithoutValue() {
        MediaType actual = MediaTypes.valueOf(null);
        assertThat(actual, is(MediaTypes.WILDCARD_TYPE));
    }

    @Test
    public void testIsCompatible() {
        MediaType a = MediaTypes.APPLICATION_JSON_TYPE;
        MediaType b = MediaTypes.APPLICATION_JSON_TYPE;
        assertThat(a.isCompatible(b), is(true));

        a = MediaTypes.valueOf("text/xml; q=0.9");
        b = MediaTypes.valueOf("text/xml; q=0.8;a=1");
        assertThat(a.isCompatible(b), is(true));

        a = MediaTypes.valueOf("application/*");
        b = MediaTypes.valueOf("application/json");
        assertThat(a.isCompatible(b), is(true));

        a = MediaTypes.valueOf("application/json");
        b = MediaTypes.valueOf("application/*");
        assertThat(a.isCompatible(b), is(false));

        a = MediaTypes.APPLICATION_JSON_TYPE;
        b = MediaTypes.TEXT_XML_TYPE;
        assertThat(a.isCompatible(b), is(false));
    }

    private Matcher<Map<?, ?>> emptyMap() {
        return new BaseMatcher<Map<?, ?>>() {
            @Override
            public boolean matches(Object item) {
                if (item instanceof Map) {
                    Map<?, ?> actual = (Map<?, ?>) item;
                    return actual.isEmpty();
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                // nop
            }
        };
    }

}
