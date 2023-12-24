package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.analogweb.*;
import org.analogweb.util.Maps;
import org.junit.Before;
import org.junit.Test;

public class DefaultInvocationMetadataFinderTest {

    private DefaultInvocationMetadataFinder finder;
    private RequestContext context;

    @Before
    public void setUp() throws Exception {
        finder = new DefaultInvocationMetadataFinder();
        context = mock(RequestContext.class);
    }

    @Test
    public void testGetActionMethodMetadata() {
        RequestPath requestPath1 = mock(RequestPath.class);
        RequestPath requestPath2 = mock(RequestPath.class);
        RequestPath requestPath3 = mock(RequestPath.class);
        InvocationMetadata metadata1 = mock(InvocationMetadata.class);
        InvocationMetadata metadata2 = mock(InvocationMetadata.class);
        InvocationMetadata metadata3 = mock(InvocationMetadata.class);
        when(metadata1.getDefinedPath()).thenReturn(requestPath1);
        when(metadata2.getDefinedPath()).thenReturn(requestPath2);
        when(metadata3.getDefinedPath()).thenReturn(requestPath3);
        Map<RequestPathMetadata, InvocationMetadata> metadatas = Maps.newEmptyHashMap();
        metadatas.put(requestPath1, metadata1);
        metadatas.put(requestPath2, metadata2);
        metadatas.put(requestPath3, metadata3);
        metadatas = Collections.unmodifiableMap(metadatas);
        when(context.getRequestPath()).thenReturn(requestPath1);
        assertThat(finder.find(metadatas, context), is(metadata1));
        when(context.getRequestPath()).thenReturn(requestPath2);
        assertThat(finder.find(metadatas, context), is(metadata2));
        when(context.getRequestPath()).thenReturn(requestPath3);
        assertThat(finder.find(metadatas, context), is(metadata3));
    }

    @Test
    public void testGetActionMethodMetadataWithNoMatch() {
        RequestPath requestPath1 = mock(RequestPath.class);
        RequestPath requestPath2 = mock(RequestPath.class);
        InvocationMetadata metadata1 = mock(InvocationMetadata.class);
        when(metadata1.getDefinedPath()).thenReturn(requestPath1);
        Map<RequestPathMetadata, InvocationMetadata> metadatas = Maps.newEmptyHashMap();
        metadatas.put(requestPath1, metadata1);
        metadatas = Collections.unmodifiableMap(metadatas);
        when(requestPath1.match(requestPath1)).thenReturn(true);
        when(metadata1.getDefinedPath()).thenReturn(requestPath1);
        when(context.getRequestPath()).thenReturn(requestPath1);
        assertThat(finder.find(metadatas, context), is(metadata1));
        when(context.getRequestPath()).thenReturn(requestPath2);
        assertNull(finder.find(metadatas, context));
    }

    @Test
    public void testDisposedGetActionMethodMetadataWithNoMatch() {
        RequestPath requestPath1 = mock(RequestPath.class);
        RequestPath requestPath2 = mock(RequestPath.class);
        InvocationMetadata metadata1 = mock(InvocationMetadata.class);
        when(metadata1.getDefinedPath()).thenReturn(requestPath1);
        when(requestPath1.match(requestPath1)).thenReturn(true);
        Map<RequestPathMetadata, InvocationMetadata> metadatas = Maps.newEmptyHashMap();
        metadatas = Collections.unmodifiableMap(metadatas);
        when(context.getRequestPath()).thenReturn(requestPath1);
        assertNull(finder.find(metadatas, context));
        when(context.getRequestPath()).thenReturn(requestPath2);
        assertNull(finder.find(metadatas, context));
    }

    @Test
    public void testDuplicatePath() {
        InvocationMetadata meta1 = mock(InvocationMetadata.class);
        InvocationMetadata meta2 = mock(InvocationMetadata.class);
        RequestPathMetadata path1 = RequestPathDefinition.define("/", "/path", new String[] { "GET" });
        when(meta1.getDefinedPath()).thenReturn(path1);
        RequestPathMetadata path2 = RequestPathDefinition.define("/", "/path", new String[] { "POST" });
        when(meta2.getDefinedPath()).thenReturn(path2);
        Map<RequestPathMetadata, InvocationMetadata> metadatas = Maps.newEmptyHashMap();
        metadatas.put(path1, meta1);
        metadatas.put(path2, meta2);
        metadatas = Collections.unmodifiableMap(metadatas);
        RequestPath requestPath1 = new DefaultRequestPath(URI.create("/"), URI.create("/path"), "GET");
        when(meta2.getDefinedPath()).thenReturn(path2);
        when(context.getRequestPath()).thenReturn(requestPath1);
        InvocationMetadata actual = finder.find(metadatas, context);
        assertThat(((InvocationMetadataFinder.Cacheable) actual).getCachable(), is(meta1));
        RequestPath requestPath2 = new DefaultRequestPath(URI.create("/"), URI.create("/path"), "POST");
        when(context.getRequestPath()).thenReturn(requestPath2);
        actual = finder.find(metadatas, context);
        assertThat(((InvocationMetadataFinder.Cacheable) actual).getCachable(), is(meta2));
    }

    @Test(expected = RequestMethodUnsupportedException.class)
    public void testDuplicatePathMethodNotFound() {
        InvocationMetadata meta1 = mock(InvocationMetadata.class);
        InvocationMetadata meta2 = mock(InvocationMetadata.class);
        RequestPathMetadata path1 = RequestPathDefinition.define("/", "/path", new String[] { "GET" });
        when(meta1.getDefinedPath()).thenReturn(path1);
        RequestPathMetadata path2 = RequestPathDefinition.define("/", "/path", new String[] { "POST" });
        when(meta2.getDefinedPath()).thenReturn(path2);
        Map<RequestPathMetadata, InvocationMetadata> metadatas = Maps.newEmptyHashMap();
        metadatas.put(path1, meta1);
        metadatas.put(path2, meta2);
        metadatas = Collections.unmodifiableMap(metadatas);
        RequestPath requestPath = new DefaultRequestPath(URI.create("/"), URI.create("/path"), "PUT");
        when(context.getRequestPath()).thenReturn(requestPath);
        finder.find(metadatas, context);
    }
}
