package org.analogweb.core;

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

import org.analogweb.AttributesHandler;
import org.analogweb.AttributesHandlers;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.On;
import org.analogweb.annotation.To;
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
    private AttributesHandlers handlers;
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
        handlers = mock(AttributesHandlers.class);
        handler = mock(AttributesHandler.class);
    }

    @Test
    public void testMapToFirstArgument() {
        Class<?>[] parameterTypes = new Class[] { Map.class, String.class };
        Method doSomething = ReflectionUtils.getMethodQuietly(MockAction.class, "doSomething",
                parameterTypes);
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);

        preparator.prepareInvoke(doSomething, args, metadata, context, typeMapper, handlers);

        verify(args).putInvocationArgument(eq(0), isA(ContextExtractor.class));
    }

    @Test
    public void testMapToFirstArgumentNotAssignableFromMap() {
        Class<?>[] parameterTypes = new Class[] { String.class, String.class };
        Method doSomething = ReflectionUtils.getMethodQuietly(MockAction.class, "doAnything",
                parameterTypes);
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);

        preparator.prepareInvoke(doSomething, args, metadata, context, typeMapper, handlers);
    }

    @Test
    public void testMapToFirstArgumentNotEqualsMap() {
        Class<?>[] parameterTypes = new Class[] { HashMap.class, String.class };
        Method doSomething = ReflectionUtils.getMethodQuietly(MockAction.class, "doNothing",
                parameterTypes);
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);

        preparator.prepareInvoke(doSomething, args, metadata, context, typeMapper, handlers);
    }

    @Test
    public void testExtractToSpecifiedScopeFirstArgument() {
        Map<String, Object> scopedMap = new ScopedMapArgumentPreparator.ContextExtractor<Object>(
                "session");
        BigDecimal amount = new BigDecimal("1000");
        scopedMap.put("amount", amount);

        when(handlers.get("session")).thenReturn(handler);

        ArrayList<Object> list = new ArrayList<Object>();
        list.add(scopedMap);
        when(args.asList()).thenReturn(list);

        Object invocationResult = new Object();

        preparator.postInvoke(invocationResult, args, metadata, context, handlers);

        verify(handler).putAttributeValue(context, "amount", amount);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testExtractToScopeFirstArgument() {

        when(metadata.getInvocationClass()).thenReturn((Class) MockAction.class);
        when(metadata.getMethodName()).thenReturn("doSomething");
        Class<?>[] parameterTypes = new Class[] { Map.class, String.class };
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);
        ArrayList<Object> list = new ArrayList<Object>();
        Map<String, Object> scopedMap = new ScopedMapArgumentPreparator.ContextExtractor<Object>("");
        BigDecimal amount = new BigDecimal("1000");
        scopedMap.put("amount", amount);
        list.add(scopedMap);
        when(args.asList()).thenReturn(list);
        when(handlers.get("request")).thenReturn(handler);

        Object invocationResult = new Object();

        preparator.postInvoke(invocationResult, args, metadata, context, handlers);

        verify(handler).putAttributeValue(context, "amount", amount);
    }

    @Test
    public void testExtractAndRemoveToScopeFirstArgument() {
        Map<String, Object> scopedMap = new ScopedMapArgumentPreparator.ContextExtractor<Object>(
                "session");
        scopedMap.remove("amount");

        when(handlers.get("session")).thenReturn(handler);

        ArrayList<Object> list = new ArrayList<Object>();
        list.add("boobaa");
        list.add(scopedMap);
        when(args.asList()).thenReturn(list);

        Object invocationResult = new Object();

        preparator.postInvoke(invocationResult, args, metadata, context, handlers);

        verify(handler).removeAttribute(context, "amount");
    }

    @On
    private static class MockAction {
        @On
        public String doSomething(@To Map<String, ?> foo, @As("baa") String baa) {
            return "do something!";
        }

        @On
        public String doSomethingElse(@To("session") Map<String, Object> session,
                @As("baa") String baa) {
            return "do something!";
        }

        @On
        public String doNothing(@To HashMap<String, ?> foo, @As("baa") String baa) {
            return "do something!";
        }

        @On
        public String doAnything(@To String notMap, @As("baa") String baa) {
            return "do something!";
        }
    }

}
