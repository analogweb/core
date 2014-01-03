package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.analogweb.ApplicationContext;
import org.analogweb.util.Maps;
import org.junit.Test;


public class DefaultApplicationContextResolverTest {

    @Test
    public void testResolve() {
        Object value = "baa";
        ApplicationContext context = DefaultApplicationContext.context("foo", value);
        assertThat(context.getAttribute(String.class, "foo"),is("baa"));
        assertThat(context.getAttribute(String.class, "baz"),is(nullValue()));
    }

    @Test
    public void testResolveMap() {
        Object value = "baa";
        Map<String, ?> map = Maps.newHashMap("foo", value);
        ApplicationContext context = DefaultApplicationContext.context(map);
        assertThat(context.getAttribute(String.class, "foo"),is("baa"));
        assertThat(context.getAttribute(String.class, "baz"),is(nullValue()));
    }

}
