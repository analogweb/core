package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.core.ParameterScopeRequestAttributesResolver;
import org.analogweb.util.Maps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class ParameterScopeRequestAttributesResolverTest {

    private ParameterScopeRequestAttributesResolver resolver;

    private RequestContext requestContext;
    private HttpServletRequest request;
    private InvocationMetadata metadata;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        resolver = new ParameterScopeRequestAttributesResolver();
        request = mock(HttpServletRequest.class);
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
     * {@link org.analogweb.core.ParameterScopeRequestAttributesResolver#resolveAttributeValue(org.analogweb.RequestContext, java.lang.String)}
     * 縺ｮ縺溘ａ縺ｮ繝�せ繝医�繝｡繧ｽ繝�ラ縲�
     */
    @Test
    public void testGetName() {
        // always return 'parameter'
        String actual = resolver.getScopeName();
        assertThat(actual, is("parameter"));
    }

    /**
     * 
     */
    @Test
    public void testResolveAttributeValue() {

        when(requestContext.getRequest()).thenReturn(request);
        when(request.getParameter("foo")).thenReturn("baa");

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo");
        assertTrue(actual instanceof String);
        assertThat(actual.toString(), is("baa"));
    }

    @Test
    public void testResolveAttributeValueWithNullName() {
        Object actual = resolver.resolveAttributeValue(requestContext, metadata, null);
        assertNull(actual);
    }

    /**
     * 
     */
    @Test
    public void testResolveAttributeValueOfParameterArray() {

        when(requestContext.getRequest()).thenReturn(request);
        when(request.getParameter("foo")).thenReturn(null);
        when(request.getParameterValues("foo")).thenReturn(new String[] { "baa", "baz" });

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo");
        assertTrue(actual instanceof String[]);
        String[] actualArray = (String[]) actual;
        assertThat(actualArray[0], is("baa"));
        assertThat(actualArray[1], is("baz"));
    }

    /**
     * 
     */
    @Test
    public void testResolveAttributeNoParameterValue() {

        when(requestContext.getRequest()).thenReturn(request);
        when(request.getParameter("foo")).thenReturn(null);
        when(request.getParameterValues("foo")).thenReturn(null);

        Object actual = resolver.resolveAttributeValue(requestContext, metadata, "foo");
        assertNull(actual);
    }

    @Test
    public void testResolveParameterMap() {
        Map<String, String> parameters = Maps.newHashMap("foo", "baa");
        when(requestContext.getRequest()).thenReturn(request);
        when(request.getParameterMap()).thenReturn(parameters);

        @SuppressWarnings("unchecked")
        Map<String, String> actual = (Map<String, String>) resolver.resolveAttributeValue(
                requestContext, metadata, ":map");
        assertThat(actual.get("foo"), is("baa"));
    }

}
