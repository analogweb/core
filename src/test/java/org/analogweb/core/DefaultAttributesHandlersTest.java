package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.analogweb.AttributesHandler;
import org.junit.Test;

public class DefaultAttributesHandlersTest {

    private DefaultAttributesHandlers handlers;

    @Test
    public void testGet() {
        AttributesHandler ha1 = mock(AttributesHandler.class);
        when(ha1.getScopeName()).thenReturn("foo");
        AttributesHandler ha2 = mock(AttributesHandler.class);
        when(ha2.getScopeName()).thenReturn("baa");

        handlers = new DefaultAttributesHandlers(Arrays.asList(ha1, ha2));
        AttributesHandler actual = handlers.get("foo");
        assertThat(actual, is(ha1));
    }

    @Test
    public void testGetDefault() {
        AttributesHandler ha1 = mock(AttributesHandler.class);
        when(ha1.getScopeName()).thenReturn("foo");
        AttributesHandler ha2 = mock(AttributesHandler.class);
        when(ha2.getScopeName()).thenReturn("parameter");

        handlers = new DefaultAttributesHandlers(Arrays.asList(ha1, ha2));
        AttributesHandler actual = handlers.get("hoge");
        assertThat(actual, is(ha2));
    }

    @Test
    public void testGetNothing() {
        AttributesHandler ha1 = mock(AttributesHandler.class);
        when(ha1.getScopeName()).thenReturn("foo");
        AttributesHandler ha2 = mock(AttributesHandler.class);
        when(ha2.getScopeName()).thenReturn("baa");

        handlers = new DefaultAttributesHandlers(Arrays.asList(ha1, ha2));
        AttributesHandler actual = handlers.get("baz");
        assertThat(actual, is(nullValue()));
    }

}
