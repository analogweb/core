package org.analogweb.core;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.ResultAttributesHolder;
import org.analogweb.ScopedAttributeName;
import org.analogweb.util.Maps;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class DefaultInvocationResultAttributesMapperTest {

    private DefaultInvocationResultAttributesMapper postProcessor;
    private InvocationMetadata metadata;
    private InvocationArguments args;
    private RequestContext context;
    private RequestAttributes attributes;
    private ResultAttributes resultAttributes;

    @Before
    public void setUp() throws Exception {
        postProcessor = new DefaultInvocationResultAttributesMapper();
        metadata = mock(InvocationMetadata.class);
        args = mock(InvocationArguments.class);
        context = mock(RequestContext.class);
        attributes = mock(RequestAttributes.class);
        resultAttributes = mock(ResultAttributes.class);
    }

    @Test
    public void testPostInvoke() {
        ResultAttributesHolder invocationResult = mock(ResultAttributesHolder.class);

        ScopedAttributeName attributeName = mock(ScopedAttributeName.class);
        when(attributeName.getName()).thenReturn("foo");
        when(attributeName.getScope()).thenReturn("request");

        Map<ScopedAttributeName, Object> value = Maps.newEmptyHashMap();
        value.put(attributeName, "baa");
        when(invocationResult.getAttributes()).thenReturn(value);

        doNothing().when(resultAttributes).setValueOfQuery(context, "request", "foo", "baa");

        Object actual = postProcessor.postInvoke(invocationResult, args, metadata, context,
                attributes, resultAttributes);
        assertSame(actual, invocationResult);
        verify(resultAttributes).setValueOfQuery(context, "request", "foo", "baa");
    }

    @Test
    public void testPostInvokeWithNoHolderResult() {

        String invocationResult = "success";
        Object actual = postProcessor.postInvoke(invocationResult, args, metadata, context,
                attributes, resultAttributes);
        assertSame(actual, invocationResult);
    }

    @Test
    public void testPostInvokeWithSpecifiedScope() {
        ResultAttributesHolder invocationResult = mock(ResultAttributesHolder.class);

        ScopedAttributeName attributeName = mock(ScopedAttributeName.class);
        when(attributeName.getName()).thenReturn("foo");
        when(attributeName.getScope()).thenReturn("session");

        Map<ScopedAttributeName, Object> value = Maps.newEmptyHashMap();
        value.put(attributeName, "baa");
        when(invocationResult.getAttributes()).thenReturn(value);

        doNothing().when(resultAttributes).setValueOfQuery(context, "session", "foo", "baa");

        Object actual = postProcessor.postInvoke(invocationResult, args, metadata, context,
                attributes, resultAttributes);
        assertSame(actual, invocationResult);
        verify(resultAttributes).setValueOfQuery(context, "session", "foo", "baa");
    }

    @Test
    public void testPostInvokeWithUnSpecifiedScope() {
        ResultAttributesHolder invocationResult = mock(ResultAttributesHolder.class);
        ScopedAttributeName attributeName = mock(ScopedAttributeName.class);
        when(attributeName.getName()).thenReturn("foo");
        when(attributeName.getScope()).thenReturn(null);

        Map<ScopedAttributeName, Object> value = Maps.newEmptyHashMap();
        value.put(attributeName, "baa");
        when(invocationResult.getAttributes()).thenReturn(value);

        doNothing().when(resultAttributes).setValueOfQuery(context, "request", "foo", "baa");

        Object actual = postProcessor.postInvoke(invocationResult, args, metadata, context,
                attributes, resultAttributes);
        assertSame(actual, invocationResult);
        verify(resultAttributes).setValueOfQuery(context, "request", "foo", "baa");
    }

}
