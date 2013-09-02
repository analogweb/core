package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.analogweb.ApplicationContextResolver;
import org.analogweb.util.Maps;
import org.junit.Test;


public class DefaultApplicationContextResolverTest {

    @Test
    public void testResolve() {
        Object value = "baa";
        ApplicationContextResolver context = DefaultApplicationContextResolver.context("foo", value);
        assertThat(context.resolve(String.class, "foo"),is("baa"));
        assertThat(context.resolve(String.class, "baz"),is(nullValue()));
    }

    @Test
    public void testResolveMap() {
        Object value = "baa";
        Map<String, ?> map = Maps.newHashMap("foo", value);
        ApplicationContextResolver context = DefaultApplicationContextResolver.context(map);
        assertThat(context.resolve(String.class, "foo"),is("baa"));
        assertThat(context.resolve(String.class, "baz"),is(nullValue()));
    }

}
