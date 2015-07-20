package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class PathVariableValueResolverTest {

    private PathVariableValueResolver resolver;
    private InvocationMetadata metadata;
    private RequestContext context;

    @Before
    public void setUp() throws Exception {
        resolver = new PathVariableValueResolver();
        metadata = mock(InvocationMetadata.class);
        context = mock(RequestContext.class);
    }

    @Test
    public void testResolveAttributeValue() {
        RequestPath requestedPath = mock(RequestPath.class);
        when(context.getRequestPath()).thenReturn(requestedPath);
        when(requestedPath.getActualPath()).thenReturn("/mock/do/any/else");
        RequestPathMetadata definedPath = mock(RequestPathMetadata.class);
        when(metadata.getDefinedPath()).thenReturn(definedPath);
        when(definedPath.getActualPath()).thenReturn("/mock/do/{something}/else");
        when(definedPath.match(requestedPath)).thenReturn(true);
        String actual = (String) resolver.resolveValue(context, metadata, "something", null, null);
        assertThat(actual, is("any"));
        actual = (String) resolver.resolveValue(context, metadata, "anything", null, null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testResolveAttributeValueWithoutPlaceHolder() {
        RequestPath requestedPath = mock(RequestPath.class);
        when(context.getRequestPath()).thenReturn(requestedPath);
        when(requestedPath.getActualPath()).thenReturn("/mock/do/any/else");
        RequestPathMetadata definedPath = mock(RequestPathMetadata.class);
        when(metadata.getDefinedPath()).thenReturn(definedPath);
        when(definedPath.getActualPath()).thenReturn("/mock/do/any/else");
        when(definedPath.match(requestedPath)).thenReturn(true);
        String actual = (String) resolver.resolveValue(context, metadata, "something", null, null);
        assertThat(actual, is(nullValue()));
    }
}
