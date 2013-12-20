package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestPath;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class DefaultRouteRegistryTest {

    private DefaultRouteRegistry registry;

    @Before
    public void setUp() throws Exception {
        registry = new DefaultRouteRegistry();
    }

    @Test
    public void testGetActionMethodMetadata() {
        RequestPath requestPath1 = mock(RequestPath.class);
        RequestPath requestPath2 = mock(RequestPath.class);
        RequestPath requestPath3 = mock(RequestPath.class);
        InvocationMetadata metadata1 = mock(InvocationMetadata.class);
        InvocationMetadata metadata2 = mock(InvocationMetadata.class);
        InvocationMetadata metadata3 = mock(InvocationMetadata.class);
        when(requestPath1.match(requestPath1)).thenReturn(true);
        when(requestPath2.match(requestPath2)).thenReturn(true);
        when(requestPath3.match(requestPath3)).thenReturn(true);
        when(metadata1.getDefinedPath()).thenReturn(requestPath1);
        when(metadata2.getDefinedPath()).thenReturn(requestPath2);
        when(metadata3.getDefinedPath()).thenReturn(requestPath3);
        registry.register(metadata1);
        registry.register(metadata2);
        registry.register(metadata3);
        assertThat(registry.findInvocationMetadata(requestPath1), is(metadata1));
        assertThat(registry.findInvocationMetadata(requestPath2), is(metadata2));
        assertThat(registry.findInvocationMetadata(requestPath3), is(metadata3));
    }

    @Test
    public void testGetActionMethodMetadataWithNoMatch() {
        RequestPath requestPath1 = mock(RequestPath.class);
        RequestPath requestPath2 = mock(RequestPath.class);
        InvocationMetadata metadata1 = mock(InvocationMetadata.class);
        when(metadata1.getDefinedPath()).thenReturn(requestPath1);
        registry.register(metadata1);
        when(requestPath1.match(requestPath1)).thenReturn(true);
        when(metadata1.getDefinedPath()).thenReturn(requestPath1);
        assertThat(registry.findInvocationMetadata(requestPath1), is(metadata1));
        assertNull(registry.findInvocationMetadata(requestPath2));
    }

    @Test
    public void testDisposedGetActionMethodMetadataWithNoMatch() {
        RequestPath requestPath1 = mock(RequestPath.class);
        RequestPath requestPath2 = mock(RequestPath.class);
        InvocationMetadata metadata1 = mock(InvocationMetadata.class);
        when(metadata1.getDefinedPath()).thenReturn(requestPath1);
        when(requestPath1.match(requestPath1)).thenReturn(true);
        registry.register(metadata1);
        registry.dispose();
        assertNull(registry.findInvocationMetadata(requestPath1));
        assertNull(registry.findInvocationMetadata(requestPath2));
    }
}
