package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.analogweb.AttributesHandlers;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.On;
import org.analogweb.exception.InvocationFailureException;
import org.analogweb.util.ArrayUtils;
import org.analogweb.util.ReflectionUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class DefaultInvocationTest {

    private DefaultInvocation invocation;

    private InvocationMetadata metadata;
    private RequestContext context;
    private TypeMapperContext converters;
    private List<InvocationProcessor> processors;
    private InvocationProcessor processor;
    private AttributesHandlers handlers;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        metadata = mock(InvocationMetadata.class);
        context = mock(RequestContext.class);
        converters = mock(TypeMapperContext.class);
        processors = new ArrayList<InvocationProcessor>();
        processor = mock(InvocationProcessor.class);
        processors.add(processor);
        handlers = mock(AttributesHandlers.class);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvoke() {

        MockActions instance = new MockActions();
        final String methodName = "doSomething";
        final Class<?>[] argumentTypes = new Class<?>[] { String.class };
        final Method method = ReflectionUtils.getMethodQuietly(MockActions.class, methodName,
                argumentTypes);
        invocation = new DefaultInvocation(instance, metadata, context, converters, processors,
                handlers);
        invocation.putInvocationArgument(0, "foo");
        when(metadata.getInvocationClass()).thenReturn((Class) instance.getClass());
        when(metadata.getMethodName()).thenReturn(methodName);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(processor.prepareInvoke(method, invocation, metadata, context, converters, handlers))
                .thenReturn(InvocationProcessor.NO_INTERRUPTION);
        when(processor.onInvoke(method, invocation, metadata, invocation)).thenReturn(
                InvocationProcessor.NO_INTERRUPTION);
        Object actionResult = new Object();
        when(processor.postInvoke("foo is something!!", invocation, metadata, context, handlers))
                .thenReturn(actionResult);
        doNothing().when(processor).afterCompletion(context, invocation, metadata, actionResult);

        Object actual = invocation.invoke();
        assertThat(actual, is(actionResult));

        verify(processor)
                .prepareInvoke(method, invocation, metadata, context, converters, handlers);
        verify(processor).onInvoke(method, invocation, metadata, invocation);
        verify(processor).postInvoke("foo is something!!", invocation, metadata, context, handlers);
        verify(processor).afterCompletion(context, invocation, metadata, actionResult);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvokeWithResultOnInvoke() {

        MockActions instance = new MockActions();
        final String methodName = "doSomething";
        final Class<?>[] argumentTypes = new Class<?>[] { String.class };
        final Method method = ReflectionUtils.getMethodQuietly(MockActions.class, methodName,
                argumentTypes);
        invocation = new DefaultInvocation(instance, metadata, context, converters, processors,
                handlers);
        invocation.putInvocationArgument(0, "foo");

        when(metadata.getInvocationClass()).thenReturn((Class) instance.getClass());
        when(metadata.getMethodName()).thenReturn(methodName);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(processor.prepareInvoke(method, invocation, metadata, context, converters, handlers))
                .thenReturn(InvocationProcessor.NO_INTERRUPTION);
        Object result = new Object();
        when(processor.onInvoke(method, invocation, metadata, invocation)).thenReturn(result);

        Object actual = invocation.invoke();
        assertThat(actual, is(result));

        verify(processor)
                .prepareInvoke(method, invocation, metadata, context, converters, handlers);
        verify(processor).onInvoke(method, invocation, metadata, invocation);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvokeNoAccessableMethod() {

        MockActions instance = new MockActions();
        final String methodName = "doNothing";
        final Class<?>[] argumentTypes = new Class<?>[0];
        final Method method = ReflectionUtils.getMethodQuietly(MockActions.class, methodName,
                argumentTypes);
        invocation = new DefaultInvocation(instance, metadata, context, converters, processors,
                handlers);

        when(metadata.getInvocationClass()).thenReturn((Class) instance.getClass());
        when(metadata.getMethodName()).thenReturn(methodName);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(processor.prepareInvoke(method, invocation, metadata, context, converters, handlers))
                .thenReturn(InvocationProcessor.NO_INTERRUPTION);
        when(processor.onInvoke(method, invocation, metadata, invocation)).thenReturn(
                InvocationProcessor.NO_INTERRUPTION);

        invocation.invoke();

        verify(processor)
                .prepareInvoke(method, invocation, metadata, context, converters, handlers);
        verify(processor).processException(isA(InvocationFailureException.class), eq(context),
                eq(invocation), eq(metadata));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvokeWithMultiArgs() {

        MockActions instance = new MockActions();
        final String methodName = "doAnything";
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, String.class, Integer.class };
        final Method method = ReflectionUtils.getMethodQuietly(MockActions.class, methodName,
                argumentTypes);
        invocation = new DefaultInvocation(instance, metadata, context, converters, processors,
                handlers);
        invocation.putInvocationArgument(0, "foo");
        invocation.putInvocationArgument(2, 1);

        when(metadata.getInvocationClass()).thenReturn((Class) instance.getClass());
        when(metadata.getMethodName()).thenReturn(methodName);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(processor.prepareInvoke(method, invocation, metadata, context, converters, handlers))
                .thenReturn(InvocationProcessor.NO_INTERRUPTION);
        when(processor.onInvoke(method, invocation, metadata, invocation)).thenReturn(
                InvocationProcessor.NO_INTERRUPTION);
        Object actionResult = new Object();
        when(
                processor.postInvoke("No1 foo with null is anything!!", invocation, metadata,
                        context, handlers)).thenReturn(actionResult);
        doNothing().when(processor).afterCompletion(context, invocation, metadata, actionResult);

        Object actual = invocation.invoke();
        assertThat(actual, is(actionResult));

        verify(processor)
                .prepareInvoke(method, invocation, metadata, context, converters, handlers);
        verify(processor).postInvoke("No1 foo with null is anything!!", invocation, metadata,
                context, handlers);
        verify(processor).afterCompletion(context, invocation, metadata, actionResult);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvokePutArg() {

        MockActions instance = new MockActions();
        final String methodName = "doSomething";
        final Class<?>[] argumentTypes = new Class<?>[] { String.class };
        final Method method = ReflectionUtils.getMethodQuietly(MockActions.class, methodName,
                argumentTypes);
        invocation = new DefaultInvocation(instance, metadata, context, converters, processors,
                handlers);
        invocation.putInvocationArgument(0, "foo");
        invocation.putInvocationArgument(0, "baa");

        when(metadata.getInvocationClass()).thenReturn((Class) instance.getClass());
        when(metadata.getMethodName()).thenReturn(methodName);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(processor.prepareInvoke(method, invocation, metadata, context, converters, handlers))
                .thenReturn(InvocationProcessor.NO_INTERRUPTION);
        when(processor.onInvoke(method, invocation, metadata, invocation)).thenReturn(
                InvocationProcessor.NO_INTERRUPTION);
        Object actionResult = new Object();
        when(processor.postInvoke("baa is something!!", invocation, metadata, context, handlers))
                .thenReturn(actionResult);
        doNothing().when(processor).afterCompletion(context, invocation, metadata, actionResult);

        Object actual = invocation.invoke();
        assertThat(actual, is(actionResult));

        verify(processor)
                .prepareInvoke(method, invocation, metadata, context, converters, handlers);
        verify(processor).postInvoke("baa is something!!", invocation, metadata, context, handlers);
        verify(processor).afterCompletion(context, invocation, metadata, actionResult);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvokePutArgWithIllegalArgument() {

        MockActions instance = new MockActions();
        final String methodName = "doSomething";
        final Class<?>[] argumentTypes = new Class<?>[] { String.class };
        final Method method = ReflectionUtils.getMethodQuietly(MockActions.class, methodName,
                argumentTypes);
        invocation = new DefaultInvocation(instance, metadata, context, converters, processors,
                handlers);
        invocation.putInvocationArgument(0, 1L);

        when(metadata.getInvocationClass()).thenReturn((Class) instance.getClass());
        when(metadata.getMethodName()).thenReturn(methodName);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(processor.prepareInvoke(method, invocation, metadata, context, converters, handlers))
                .thenReturn(InvocationProcessor.NO_INTERRUPTION);
        when(processor.onInvoke(method, invocation, metadata, invocation)).thenReturn(
                InvocationProcessor.NO_INTERRUPTION);

        invocation.invoke();

        verify(processor)
                .prepareInvoke(method, invocation, metadata, context, converters, handlers);
        verify(processor).processException(isA(InvocationFailureException.class), eq(context),
                eq(invocation), eq(metadata));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvokeWithException() {

        thrown.expect(InvocationFailureException.class);
        // cause exception is action thrown.
        thrown.expect(rootExceptionIs(NullPointerException.class));

        InvocationProcessor processor2 = mock(InvocationProcessor.class);
        processors.add(processor2);

        MockActions instance = new MockActions();
        final String methodName = "doSomethingWithException";
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, Long.class };
        final Method method = ReflectionUtils.getMethodQuietly(MockActions.class, methodName,
                argumentTypes);
        invocation = new DefaultInvocation(instance, metadata, context, converters, processors,
                handlers);
        invocation.putInvocationArgument(0, "foo");

        when(metadata.getInvocationClass()).thenReturn((Class) instance.getClass());
        when(metadata.getMethodName()).thenReturn(methodName);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);

        when(processor.prepareInvoke(method, invocation, metadata, context, converters, handlers))
                .thenReturn(InvocationProcessor.NO_INTERRUPTION);
        when(processor2.prepareInvoke(method, invocation, metadata, context, converters, handlers))
                .thenReturn(InvocationProcessor.NO_INTERRUPTION);
        when(processor.onInvoke(method, invocation, metadata, invocation)).thenReturn(
                InvocationProcessor.NO_INTERRUPTION);
        when(processor2.onInvoke(method, invocation, metadata, invocation)).thenReturn(
                InvocationProcessor.NO_INTERRUPTION);

        invocation.putInvocationArgument(1, 100L);

        when(
                processor.processException(isA(InvocationFailureException.class), eq(context),
                        eq(invocation), eq(metadata))).thenReturn(
                InvocationProcessor.NO_INTERRUPTION);
        doThrow(
                new InvocationFailureException(new NullPointerException(), metadata, ArrayUtils
                        .newArray())).when(processor2).processException(
                isA(InvocationFailureException.class), eq(context), eq(invocation), eq(metadata));
        doNothing().when(processor2).afterCompletion(context, invocation, metadata, null);

        invocation.invoke();

        verify(processor)
                .prepareInvoke(method, invocation, metadata, context, converters, handlers);
        verify(processor).postInvoke("baa is something!!", invocation, metadata, context, handlers);
        verify(processor).afterCompletion(context, invocation, metadata, null);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvokeWithExceptionWithInterraption() {

        InvocationProcessor processor2 = mock(InvocationProcessor.class);
        processors.add(processor2);

        MockActions instance = new MockActions();
        final String methodName = "doSomethingWithException";
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, Long.class };
        final Method method = ReflectionUtils.getMethodQuietly(MockActions.class, methodName,
                argumentTypes);
        invocation = new DefaultInvocation(instance, metadata, context, converters, processors,
                handlers);

        when(metadata.getInvocationClass()).thenReturn((Class) instance.getClass());
        when(metadata.getMethodName()).thenReturn(methodName);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);

        when(processor.prepareInvoke(method, invocation, metadata, context, converters, handlers))
                .thenReturn(InvocationProcessor.NO_INTERRUPTION);
        invocation.putInvocationArgument(0, "foo");
        when(processor2.prepareInvoke(method, invocation, metadata, context, converters, handlers))
                .thenReturn(InvocationProcessor.NO_INTERRUPTION);
        invocation.putInvocationArgument(1, 100L);
        when(processor.onInvoke(method, invocation, metadata, invocation)).thenReturn(
                InvocationProcessor.NO_INTERRUPTION);
        when(processor2.onInvoke(method, invocation, metadata, invocation)).thenReturn(
                InvocationProcessor.NO_INTERRUPTION);

        Object invocationResult = new Object();
        when(
                processor.processException(isA(InvocationFailureException.class), eq(context),
                        eq(invocation), eq(metadata))).thenReturn(
                InvocationProcessor.NO_INTERRUPTION);
        when(
                processor2.processException(isA(InvocationFailureException.class), eq(context),
                        eq(invocation), eq(metadata))).thenReturn(invocationResult);

        Object actual = invocation.invoke();

        assertThat(actual, is(invocationResult));

        verify(processor)
                .prepareInvoke(method, invocation, metadata, context, converters, handlers);
    }

    private Matcher<InvocationFailureException> rootExceptionIs(final Class<? extends Throwable> t) {
        return new BaseMatcher<InvocationFailureException>() {

            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof Throwable) {
                    Throwable exp = (Throwable) arg0;
                    return exp.getCause().getClass().equals(t);
                }
                return false;
            }

            @Override
            public void describeTo(Description arg0) {
                arg0.appendText(t.toString());
            }
        };
    }

    @On
    public static class MockActions {
        @On
        private String doNothing() {
            return null;
        }

        @On
        public String doSomething(@As("foo") String foo) {
            return String.format("%s is something!!", foo);
        }

        @On
        public String doAnything(@As("foo") String foo, String baa, @As("baz") Integer baz) {
            return String.format("No%s %s with %s is anything!!", baz, foo, baa);
        }

        @On
        public String doSomethingWithException(@As("foo") String foo, @As("baa") Long baa) {
            throw new NullPointerException("oops!");
        }
    }

}
