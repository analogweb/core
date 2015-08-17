package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.analogweb.ApplicationProcessor;
import org.analogweb.AttributesHandler;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.Route;
import org.analogweb.annotation.Attributes;
import org.analogweb.core.ScopedMapArgumentPreparator.ContextExtractor;
import org.analogweb.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link ScopedMapArgumentPreparator}に対するテストケースです。
 * @author snowgoose
 */
public class ScopedMapArgumentPreparatorTest {

    private ScopedMapArgumentPreparator preparator;
    private InvocationMetadata metadata;
    private InvocationArguments args;
    private RequestContext context;
    private TypeMapperContext typeMapper;
    private RequestValueResolvers handlers;
    private AttributesHandler handler;

    /**
     * テストの事前準備を行います。
     */
    @Before
    public void setUp() {
        preparator = new ScopedMapArgumentPreparator();
        metadata = mock(InvocationMetadata.class);
        args = mock(InvocationArguments.class);
        context = mock(RequestContext.class);
        typeMapper = mock(TypeMapperContext.class);
        handlers = mock(RequestValueResolvers.class);
        handler = mock(AttributesHandler.class);
    }

    @Test
    public void testMapToFirstArgument() {
        Class<?>[] parameterTypes = new Class[] { Map.class, String.class };
        Method doSomething = ReflectionUtils.getMethodQuietly(MockAction.class, "doSomething",
                parameterTypes);
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);
        when(metadata.resolveMethod()).thenReturn(doSomething);
        preparator.prepareInvoke(args, metadata, context, typeMapper, handlers);
        verify(args).putInvocationArgument(eq(0), isA(ContextExtractor.class));
    }

    @Test
    public void testMapToFirstArgumentWithNullMethod() {
        Class<?>[] parameterTypes = new Class[] { Map.class, String.class };
        Method doSomething = null;
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);
        when(metadata.resolveMethod()).thenReturn(doSomething);
        Object actual = preparator.prepareInvoke(args, metadata, context, typeMapper, handlers);
        assertThat(actual, is(ApplicationProcessor.NO_INTERRUPTION));
    }

    @Test
    public void testMapWithNullArgument() {
        Class<?>[] parameterTypes = new Class[] { Map.class, String.class };
        Method doSomething = ReflectionUtils.getMethodQuietly(MockAction.class, "doSomething",
                parameterTypes);
        when(metadata.getArgumentTypes()).thenReturn(null);
        when(metadata.resolveMethod()).thenReturn(doSomething);
        Object actual = preparator.prepareInvoke(args, metadata, context, typeMapper, handlers);
        assertThat(actual, is(ApplicationProcessor.NO_INTERRUPTION));
    }

    @Test
    public void testMapToFirstArgumentNotAssignableFromMap() {
        Class<?>[] parameterTypes = new Class[] { String.class, String.class };
        Method doSomething = ReflectionUtils.getMethodQuietly(MockAction.class, "doAnything",
                parameterTypes);
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);
        when(metadata.resolveMethod()).thenReturn(doSomething);
        preparator.prepareInvoke(args, metadata, context, typeMapper, handlers);
    }

    @Test
    public void testMapToFirstArgumentNotEqualsMap() {
        Class<?>[] parameterTypes = new Class[] { HashMap.class, String.class };
        Method doSomething = ReflectionUtils.getMethodQuietly(MockAction.class, "doNothing",
                parameterTypes);
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);
        when(metadata.resolveMethod()).thenReturn(doSomething);
        preparator.prepareInvoke(args, metadata, context, typeMapper, handlers);
    }

    interface Session extends AttributesHandler {
    }

    @Test
    public void testExtractToSpecifiedScopeFirstArgument() {
        Map<String, Object> scopedMap = new ScopedMapArgumentPreparator.ContextExtractor<Object>(
                Session.class);
        BigDecimal amount = new BigDecimal("1000");
        scopedMap.put("amount", amount);
        when(handlers.findAttributesHandler(Session.class)).thenReturn(handler);
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(scopedMap);
        when(args.asList()).thenReturn(list);
        Object invocationResult = new Object();
        preparator.postInvoke(invocationResult, args, metadata, context, handlers);
        verify(handler).putAttributeValue(context, "amount", amount);
    }

    interface Request extends AttributesHandler {
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testExtractToScopeFirstArgument() {
        when(metadata.getInvocationClass()).thenReturn((Class) MockAction.class);
        when(metadata.getMethodName()).thenReturn("doSomething");
        Class<?>[] parameterTypes = new Class[] { Map.class, String.class };
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);
        ArrayList<Object> list = new ArrayList<Object>();
        Map<String, Object> scopedMap = new ScopedMapArgumentPreparator.ContextExtractor<Object>(
                Request.class);
        BigDecimal amount = new BigDecimal("1000");
        scopedMap.put("amount", amount);
        list.add(scopedMap);
        when(args.asList()).thenReturn(list);
        when(handlers.findAttributesHandler(Request.class)).thenReturn(handler);
        Object invocationResult = new Object();
        preparator.postInvoke(invocationResult, args, metadata, context, handlers);
        verify(handler).putAttributeValue(context, "amount", amount);
    }

    @Test
    public void testExtractAndRemoveToScopeFirstArgument() {
        Map<String, Object> scopedMap = new ScopedMapArgumentPreparator.ContextExtractor<Object>(
                Session.class);
        scopedMap.remove("amount");
        when(handlers.findAttributesHandler(Session.class)).thenReturn(handler);
        ArrayList<Object> list = new ArrayList<Object>();
        list.add("boobaa");
        list.add(scopedMap);
        when(args.asList()).thenReturn(list);
        Object invocationResult = new Object();
        preparator.postInvoke(invocationResult, args, metadata, context, handlers);
        verify(handler).removeAttribute(context, "amount");
    }

    @Route
    private static class MockAction {

        @Route
        public String doSomething(@Attributes(Request.class) Map<String, ?> foo,
                @As("baa") String baa) {
            return "do something!";
        }

        @Route
        public String doSomethingElse(@Attributes(Session.class) Map<String, Object> session,
                @As("baa") String baa) {
            return "do something!";
        }

        @Route
        public String doNothing(HashMap<String, ?> foo, @As("baa") String baa) {
            return "do something!";
        }

        @Route
        public String doAnything(@Attributes(AttributesHandler.class) String notMap,
                @As("baa") String baa) {
            return "do something!";
        }
    }
}
