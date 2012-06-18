package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestPathMetadata;
import org.analogweb.RequestPath;
import org.analogweb.core.PathVariableScopeRequestAttributesResolver;
import org.analogweb.util.Maps;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class PathVariableScopeRequestAttributesResolverTest {

    private PathVariableScopeRequestAttributesResolver resolver;
    private InvocationMetadata metadata;
    private RequestContext context;
    private HttpServletRequest request;

    @Before
    public void setUp() throws Exception {
        resolver = new PathVariableScopeRequestAttributesResolver();
        metadata = mock(InvocationMetadata.class);
        context = mock(RequestContext.class);
        request = mock(HttpServletRequest.class);
    }

    @Test
    public void testResolveAttributeValue() {
        RequestPath requestedPath = mock(RequestPath.class);
        when(context.getRequest()).thenReturn(request);
        when(context.getRequestedPath()).thenReturn(requestedPath);
        when(requestedPath.getActualPath()).thenReturn("/mock/do/any/else");
        RequestPathMetadata definedPath = mock(RequestPathMetadata.class);
        when(metadata.getDefinedPath()).thenReturn(definedPath);
        when(definedPath.getActualPath()).thenReturn("/mock/do/{something}/else");
        when(definedPath.match(requestedPath)).thenReturn(true);

        String actual = (String) resolver.resolveAttributeValue(context, metadata, "something");
        assertThat(actual, is("any"));
        actual = (String) resolver.resolveAttributeValue(context, metadata, "anything");
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testResolveAttributeValueAlreadyCached() {
        when(context.getRequest()).thenReturn(request);
        when(request.getAttribute(PathVariableScopeRequestAttributesResolver.VALIABLES_CACHE_KEY))
                .thenReturn(Maps.newHashMap("something", "else"));

        String actual = (String) resolver.resolveAttributeValue(context, metadata, "something");
        assertThat(actual, is("else"));
    }

    @Test
    public void testResolveAttributeValueWithoutPlaceHolder() {
        RequestPath requestedPath = mock(RequestPath.class);
        when(context.getRequest()).thenReturn(request);
        when(context.getRequestedPath()).thenReturn(requestedPath);
        when(requestedPath.getActualPath()).thenReturn("/mock/do/any/else");
        RequestPathMetadata definedPath = mock(RequestPathMetadata.class);
        when(metadata.getDefinedPath()).thenReturn(definedPath);
        when(definedPath.getActualPath()).thenReturn("/mock/do/any/else");
        when(definedPath.match(requestedPath)).thenReturn(true);

        String actual = (String) resolver.resolveAttributeValue(context, metadata, "something");
        assertThat(actual, is(nullValue()));
        verify(request).setAttribute(
                eq(PathVariableScopeRequestAttributesResolver.VALIABLES_CACHE_KEY), isA(Map.class));
    }

}
