package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import org.analogweb.AttributesHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.core.DefaultRequestAttributes;
import org.analogweb.util.Maps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class DefaultRequestAttributesTest {

    private DefaultRequestAttributes attributes;
    private AttributesHandler resolver1;
    private AttributesHandler resolver2;
    private AttributesHandler resolver3;
    private RequestContext requestContext;
    private InvocationMetadata metadata;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        resolver1 = mock(AttributesHandler.class);
        resolver2 = mock(AttributesHandler.class);
        resolver3 = mock(AttributesHandler.class);
        requestContext = mock(RequestContext.class);
        metadata = mock(InvocationMetadata.class);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * 
     */
    @Test
    public void testGetValueOfQuery() {
        List<AttributesHandler> resolvers = Arrays.asList(resolver1, resolver2, resolver3);
        when(resolver1.getScopeName()).thenReturn("request");
        when(resolver2.getScopeName()).thenReturn("parameter");
        when(resolver3.getScopeName()).thenReturn("session");
        when(resolver1.resolveAttributeValue(requestContext, metadata, "foo")).thenReturn("1!");

        attributes = new DefaultRequestAttributes(resolvers, metadata);
        Object actual = attributes.getValueOfQuery(requestContext, "request", "foo");
        assertThat(actual.toString(), is("1!"));
    }

    /**
     * 
     */
    @Test
    public void testGetValueOfQueryMapConstractor() {
        Map<String, AttributesHandler> resolvers = Maps.newEmptyHashMap();
        resolvers.put("request", resolver1);
        resolvers.put("parameter", resolver2);
        resolvers.put("session", resolver3);

        when(resolver1.getScopeName()).thenReturn("request");
        when(resolver2.getScopeName()).thenReturn("parameter");
        when(resolver3.getScopeName()).thenReturn("session");
        when(resolver1.resolveAttributeValue(requestContext, metadata, "foo")).thenReturn("1!");

        attributes = new DefaultRequestAttributes(resolvers, metadata);
        Object actual = attributes.getValueOfQuery(requestContext, "request", "foo");
        assertThat(actual.toString(), is("1!"));
    }

    /**
     * 
     */
    @Test
    public void testGetValueOfQueryNoDistinctResolver() {
        List<AttributesHandler> resolvers = Arrays.asList(resolver1, resolver2, resolver3);
        when(resolver1.getScopeName()).thenReturn("request");
        when(resolver2.getScopeName()).thenReturn("parameter");
        when(resolver3.getScopeName()).thenReturn("session");
        when(resolver2.resolveAttributeValue(requestContext, metadata, "foo")).thenReturn("2!");

        attributes = new DefaultRequestAttributes(resolvers, metadata);
        Object actual = attributes.getValueOfQuery(requestContext, "", "foo");
        assertThat(actual.toString(), is("2!"));
    }

    @Test
    public void testGetValueOfQueryNullResolver() {
        List<AttributesHandler> resolvers = Arrays.asList(resolver1, resolver2, resolver3);
        when(resolver1.getScopeName()).thenReturn("request");
        when(resolver2.getScopeName()).thenReturn("parameter");
        when(resolver3.getScopeName()).thenReturn("session");
        when(resolver2.resolveAttributeValue(requestContext, metadata, "foo")).thenReturn("2!");

        attributes = new DefaultRequestAttributes(resolvers, metadata);
        Object actual = attributes.getValueOfQuery(requestContext, null, "foo");
        assertThat(actual.toString(), is("2!"));
    }

    @Test
    public void testGetValueOfQueryAbsoruteResolver() {
        AttributesHandler resolver4 = mock(AttributesHandler.class);
        List<AttributesHandler> resolvers = Arrays.asList(resolver1, resolver2, resolver3, resolver4);

        when(resolver1.getScopeName()).thenReturn("request");
        when(resolver2.getScopeName()).thenReturn("parameter");
        when(resolver3.getScopeName()).thenReturn("session");
        when(resolver4.getScopeName()).thenReturn("path");

        when(resolver2.resolveAttributeValue(requestContext, metadata, "foo")).thenReturn(null);
        when(resolver1.resolveAttributeValue(requestContext, metadata, "foo")).thenReturn(null);
        when(resolver4.resolveAttributeValue(requestContext, metadata, "foo")).thenReturn("1!");

        attributes = new DefaultRequestAttributes(resolvers, metadata);
        Object actual = attributes.getValueOfQuery(requestContext, null, "foo");
        assertThat(actual.toString(), is("1!"));
    }

    @Test
    public void testGetValueOfQueryAbsoruteResolver2() {
        AttributesHandler resolver4 = mock(AttributesHandler.class);
        List<AttributesHandler> resolvers = Arrays.asList(resolver1, resolver2, resolver3, resolver4);

        when(resolver1.getScopeName()).thenReturn("request");
        when(resolver2.getScopeName()).thenReturn("parameter");
        when(resolver3.getScopeName()).thenReturn("session");
        when(resolver4.getScopeName()).thenReturn("path");

        when(resolver2.resolveAttributeValue(requestContext, metadata, "foo")).thenReturn(null);
        when(resolver1.resolveAttributeValue(requestContext, metadata, "foo")).thenReturn("2!");
        when(resolver4.resolveAttributeValue(requestContext, metadata, "foo")).thenReturn("1!");

        attributes = new DefaultRequestAttributes(resolvers, metadata);
        Object actual = attributes.getValueOfQuery(requestContext, null, "foo");
        assertThat(actual.toString(), is("2!"));
    }

    @Test
    public void testGetValueOfQueryNoResolver() {
        List<AttributesHandler> resolvers = Arrays.asList(resolver1, resolver2, resolver3);
        when(resolver1.getScopeName()).thenReturn("request");
        when(resolver2.getScopeName()).thenReturn("parameter");
        when(resolver3.getScopeName()).thenReturn("session");

        attributes = new DefaultRequestAttributes(resolvers, metadata);
        Object actual = attributes.getValueOfQuery(requestContext, "no-resolver", "foo");
        assertNull(actual);
    }

}
