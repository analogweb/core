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
public class DefaultRequestPathMappingTest {

    private DefaultRequestPathMapping mapping;

    @Before
    public void setUp() throws Exception {
        mapping = new DefaultRequestPathMapping();
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

        mapping.mapInvocationMetadata(requestPath1, metadata1);
        mapping.mapInvocationMetadata(requestPath2, metadata2);
        mapping.mapInvocationMetadata(requestPath3, metadata3);

        assertThat(mapping.getActionMethodMetadata(requestPath1), is(metadata1));
        assertThat(mapping.getActionMethodMetadata(requestPath2), is(metadata2));
        assertThat(mapping.getActionMethodMetadata(requestPath3), is(metadata3));
    }

    @Test
    public void testGetActionMethodMetadataWithNoMatch() {
        RequestPath requestPath1 = mock(RequestPath.class);
        RequestPath requestPath2 = mock(RequestPath.class);

        InvocationMetadata metadata1 = mock(InvocationMetadata.class);
        mapping.mapInvocationMetadata(requestPath1, metadata1);

        when(requestPath1.match(requestPath1)).thenReturn(true);

        assertThat(mapping.getActionMethodMetadata(requestPath1), is(metadata1));
        assertNull(mapping.getActionMethodMetadata(requestPath2));
    }

    @Test
    public void testDisposedGetActionMethodMetadataWithNoMatch() {
        RequestPath requestPath1 = mock(RequestPath.class);
        RequestPath requestPath2 = mock(RequestPath.class);

        InvocationMetadata metadata1 = mock(InvocationMetadata.class);
        mapping.mapInvocationMetadata(requestPath1, metadata1);

        when(requestPath1.match(requestPath1)).thenReturn(true);

        mapping.dispose();
        assertNull(mapping.getActionMethodMetadata(requestPath1));
        assertNull(mapping.getActionMethodMetadata(requestPath2));
    }

}
