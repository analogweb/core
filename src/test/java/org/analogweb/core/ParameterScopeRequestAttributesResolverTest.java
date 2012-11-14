package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.analogweb.InvocationMetadata;
import org.analogweb.Parameters;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class ParameterScopeRequestAttributesResolverTest {

    private ParameterScopeRequestAttributesResolver resolver;

    private RequestContext requestContext;
    private InvocationMetadata metadata;
    private Parameters params;

    @Before
    public void setUp() throws Exception {
        resolver = new ParameterScopeRequestAttributesResolver();
        requestContext = mock(RequestContext.class);
        metadata = mock(InvocationMetadata.class);
        params = mock(Parameters.class);
    }

    @Test
    public void testGetName() {
        // always return 'parameter'
        String actual = resolver.getScopeName();
        assertThat(actual, is("parameter"));
    }

    @Test
    public void testResolveAttributeValue() {
        when(requestContext.getParameters()).thenReturn(params);
        when(params.getValues("foo")).thenReturn(Arrays.asList("baa"));

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo",
                String.class);
        assertThat(actual.toString(), is("baa"));
    }

    @Test
    public void testResolveAttributeValueWithNullName() {
        Object actual = resolver.resolveAttributeValue(requestContext, metadata, null,
                String[].class);
        assertNull(actual);
    }

    @Test
    public void testResolveAttributeValueOfParameterArray() {
        when(requestContext.getParameters()).thenReturn(params);
        when(params.getValues("foo")).thenReturn(Arrays.asList("baa", "baz"));

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo",
                String[].class);
        assertTrue(actual instanceof String[]);
        String[] actualArray = (String[]) actual;
        assertThat(actualArray[0], is("baa"));
        assertThat(actualArray[1], is("baz"));
    }

    @Test
    public void testResolveAttributeNoParameterValue() {
        when(requestContext.getParameters()).thenReturn(params);
        when(params.getValues("foo")).thenReturn(null);

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo",
                String[].class);
        assertNull(actual);
    }

}
