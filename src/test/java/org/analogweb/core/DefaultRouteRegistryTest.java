package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;
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

    @Test
    public void testDuplicatePath() {
        InvocationMetadata meta1 = mock(InvocationMetadata.class);
        InvocationMetadata meta2 = mock(InvocationMetadata.class);
        RequestPathMetadata path1 = RequestPathDefinition.define("/", "/path",
                new String[] { "GET" });
        when(meta1.getDefinedPath()).thenReturn(path1);
        RequestPathMetadata path2 = RequestPathDefinition.define("/", "/path",
                new String[] { "POST" });
        when(meta2.getDefinedPath()).thenReturn(path2);
        registry.register(meta1);
        registry.register(meta2);
        RequestPath requestPath1 = new DefaultRequestPath(URI.create("/"), URI.create("/path"),
                "GET");
        when(meta2.getDefinedPath()).thenReturn(path2);
        InvocationMetadata actual = registry.findInvocationMetadata(requestPath1);
        assertThat(actual, is(meta1));
        RequestPath requestPath2 = new DefaultRequestPath(URI.create("/"), URI.create("/path"),
                "POST");
        actual = registry.findInvocationMetadata(requestPath2);
        assertThat(actual, is(meta2));
    }
}
