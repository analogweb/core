package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.analogweb.ApplicationProcessor;
import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationInterceptor;
import org.analogweb.InvocationMetadata;
import org.analogweb.Invoker;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolvers;
import org.analogweb.ResponseContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.On;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class DefaultInvokerTest {

    private InvocationMetadata metadata;
    private RequestContext request;
    private ResponseContext response;
    private Invocation invocation;
    private RequestValueResolvers handlers;
    private TypeMapperContext typeMapper;
    private InvocationArguments args;
    private List<ApplicationProcessor> processors;
    private ApplicationProcessor processor;
    private List<InvocationInterceptor> interceptors;
    private InvocationInterceptor interceptor;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        metadata = mock(InvocationMetadata.class);
        request = mock(RequestContext.class);
        response = mock(ResponseContext.class);
        processors = new ArrayList<ApplicationProcessor>();
        processor = mock(ApplicationProcessor.class);
        processors.add(processor);
        invocation = mock(Invocation.class);
        handlers = mock(RequestValueResolvers.class);
        typeMapper = mock(TypeMapperContext.class);
        args = mock(InvocationArguments.class);
        processor = mock(ApplicationProcessor.class);
        processors = Arrays.asList(processor);
        interceptor = new AbstractInvocationInterceptor();
        interceptors = Arrays.asList(interceptor);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvoke() {
        Invoker invoker = new DefaultInvoker(interceptors);
        Object result = new Object();
        when(invocation.invoke()).thenReturn(result);
        when(invocation.getInvocationArguments()).thenReturn(args);
        List<Object> list = new ArrayList<Object>();
        list.add("foo");
        when(args.asList()).thenReturn(list);
        when(metadata.getInvocationClass()).thenReturn((Class) StubResource.class);
        when(metadata.getMethodName()).thenReturn("doSomething");
        when(metadata.getArgumentTypes()).thenReturn(new Class<?>[] { String.class });
        when(
                processor.prepareInvoke(isA(Method.class), eq(args), eq(metadata), eq(request),
                        eq(typeMapper), eq(handlers))).thenReturn(
                ApplicationProcessor.NO_INTERRUPTION);
        doNothing().when(processor).postInvoke("foo is something!!", args, metadata, request,
                handlers);
        // delegate to Invocation#invoke only.
        Object actual = invoker.invoke(invocation, metadata, request, response);
        assertThat(actual, is(result));
        verify(invocation).invoke();
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvokeWithException() {
        thrown.expect(IllegalArgumentException.class);
        Invoker invoker = new DefaultInvoker(interceptors);
        when(invocation.invoke()).thenThrow(new IllegalArgumentException());
        when(invocation.getInvocationArguments()).thenReturn(args);
        when(metadata.getInvocationClass()).thenReturn((Class) StubResource.class);
        when(metadata.getMethodName()).thenReturn("doSomething");
        when(metadata.getArgumentTypes()).thenReturn(new Class<?>[] { String.class });
        when(args.asList()).thenReturn(Collections.emptyList());
        when(
                processor.prepareInvoke(isA(Method.class), eq(args), eq(metadata), eq(request),
                        eq(typeMapper), eq(handlers))).thenReturn(
                ApplicationProcessor.NO_INTERRUPTION);
        when(
                processor.processException(isA(IllegalArgumentException.class), eq(request),
                        eq(args), eq(metadata))).thenReturn(ApplicationProcessor.NO_INTERRUPTION);
        invoker.invoke(invocation, metadata, request, response);
    }

    @On
    public static class StubResource {

        @On
        public String doSomething(@As("foo") String foo) {
            return String.format("%s is something!!", foo);
        }
    }
}
