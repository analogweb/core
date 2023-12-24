package org.analogweb.core;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationMetadataFinder;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class DefaultRouteRegistryTest {

    private DefaultRouteRegistry registry;
    private RequestContext context;

    @Before
    public void setUp() throws Exception {
        registry = new DefaultRouteRegistry();
        context = mock(RequestContext.class);
    }

    @Test
    public void testGetMetadata() {
        RequestPath requestPath1 = mock(RequestPath.class);
        RequestPath requestPath2 = mock(RequestPath.class);
        RequestPath requestPath3 = mock(RequestPath.class);
        InvocationMetadata metadata1 = mock(InvocationMetadata.class);
        InvocationMetadata metadata2 = mock(InvocationMetadata.class);
        InvocationMetadata metadata3 = mock(InvocationMetadata.class);
        when(metadata1.getDefinedPath()).thenReturn(requestPath1);
        when(metadata2.getDefinedPath()).thenReturn(requestPath2);
        when(metadata3.getDefinedPath()).thenReturn(requestPath3);
        registry.register(metadata1);
        registry.register(metadata2);
        registry.register(metadata3);
        InvocationMetadataFinder finder1 = mock(InvocationMetadataFinder.class);
        when(finder1.find(anyMap(), eq(context))).thenReturn(null);
        InvocationMetadataFinder finder2 = mock(InvocationMetadataFinder.class);
        when(finder2.find(anyMap(), eq(context))).thenReturn(metadata2);
        InvocationMetadataFinder finder3 = mock(InvocationMetadataFinder.class);
        when(finder3.find(anyMap(), eq(context))).thenReturn(null);
        assertThat(registry.findInvocationMetadata(context, Arrays.asList(finder1, finder2, finder3)), is(metadata2));
    }

    @Test
    public void testGetNothing() {
        RequestPath requestPath1 = mock(RequestPath.class);
        RequestPath requestPath2 = mock(RequestPath.class);
        RequestPath requestPath3 = mock(RequestPath.class);
        InvocationMetadata metadata1 = mock(InvocationMetadata.class);
        InvocationMetadata metadata2 = mock(InvocationMetadata.class);
        InvocationMetadata metadata3 = mock(InvocationMetadata.class);
        when(metadata1.getDefinedPath()).thenReturn(requestPath1);
        when(metadata2.getDefinedPath()).thenReturn(requestPath2);
        when(metadata3.getDefinedPath()).thenReturn(requestPath3);
        registry.register(metadata1);
        registry.register(metadata2);
        registry.register(metadata3);
        InvocationMetadataFinder finder1 = mock(InvocationMetadataFinder.class);
        when(finder1.find(anyMap(), eq(context))).thenReturn(null);
        InvocationMetadataFinder finder2 = mock(InvocationMetadataFinder.class);
        when(finder2.find(anyMap(), eq(context))).thenReturn(null);
        InvocationMetadataFinder finder3 = mock(InvocationMetadataFinder.class);
        when(finder3.find(anyMap(), eq(context))).thenReturn(null);
        assertThat(registry.findInvocationMetadata(context, Arrays.asList(finder1, finder2, finder3)), is(nullValue()));
    }

    @Test
    public void testGetCacheable() {
        RequestPath requestPath1 = mock(RequestPath.class);
        RequestPath requestPath2 = mock(RequestPath.class);
        RequestPath requestPath3 = mock(RequestPath.class);
        InvocationMetadata metadata1 = mock(InvocationMetadata.class);
        InvocationMetadata metadata2 = mock(InvocationMetadata.class);
        InvocationMetadataFinder.Cacheable metadata3 = mock(InvocationMetadataFinder.Cacheable.class);
        when(metadata1.getDefinedPath()).thenReturn(requestPath1);
        when(metadata2.getDefinedPath()).thenReturn(requestPath2);
        when(metadata3.getDefinedPath()).thenReturn(requestPath3);
        registry.register(metadata1);
        registry.register(metadata2);
        registry.register(metadata3);
        when(context.getRequestPath()).thenReturn(requestPath3);
        InvocationMetadataFinder finder2 = mock(InvocationMetadataFinder.class);
        when(finder2.find(anyMap(), eq(context))).thenReturn(null);
        InvocationMetadataFinder finder3 = mock(InvocationMetadataFinder.class);
        when(finder3.find(anyMap(), eq(context))).thenReturn(metadata3);
        when(metadata3.getCachable()).thenReturn(metadata1);
        assertThat(registry.findInvocationMetadata(context, Arrays.asList(finder2, finder3)), is(metadata1));
    }
}
