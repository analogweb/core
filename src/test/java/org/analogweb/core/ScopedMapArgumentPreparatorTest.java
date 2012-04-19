package org.analogweb.core;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.On;
import org.analogweb.annotation.To;
import org.analogweb.core.ScopedMapArgumentPreparator;
import org.analogweb.util.Maps;
import org.analogweb.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link ScopedMapArgumentPreparator}に対するテストケースです。
 * @author snowgoose
 */
public class ScopedMapArgumentPreparatorTest {

    private ScopedMapArgumentPreparator preparator;
    private Invocation invocation;
    private InvocationMetadata metadata;
    private RequestContext context;
    private RequestAttributes attributes;
    private TypeMapperContext typeMapper;
    private ResultAttributes resultAttributes;

    /**
     * テストの事前準備を行います。
     */
    @Before
    public void setUp() {
        preparator = new ScopedMapArgumentPreparator();
        invocation = mock(Invocation.class);
        metadata = mock(InvocationMetadata.class);
        context = mock(RequestContext.class);
        attributes = mock(RequestAttributes.class);
        typeMapper = mock(TypeMapperContext.class);
        resultAttributes = mock(ResultAttributes.class);
    }

    @Test
    public void testMapToFirstArgument() {
        Class<?>[] parameterTypes = new Class[] { Map.class, String.class };
        Method doSomething = ReflectionUtils.getMethodQuietly(MockAction.class,
                "doSomething", parameterTypes);
        Map<Integer, Object> arguments = Maps.newEmptyHashMap();
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);
        when(invocation.getPreparedArgs()).thenReturn(arguments);

        preparator
                .prepareInvoke(doSomething, invocation, metadata, context, attributes, typeMapper);

        assertThat(arguments.get(0), instanceOf(Map.class));

        Map<?, ?> newMap = (Map<?, ?>) arguments.get(0);
        assertTrue(newMap.isEmpty());
    }

    @Test
    public void testMapToFirstArgumentNotAssignableFromMap() {
        Class<?>[] parameterTypes = new Class[] { String.class, String.class };
        Method doSomething = ReflectionUtils.getMethodQuietly(MockAction.class,
                "doAnything", parameterTypes);
        Map<Integer, Object> arguments = Maps.newEmptyHashMap();
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);
        when(invocation.getPreparedArgs()).thenReturn(arguments);

        preparator
                .prepareInvoke(doSomething, invocation, metadata, context, attributes, typeMapper);

        assertNull(arguments.get(0));
    }

    @Test
    public void testMapToFirstArgumentNotEqualsMap() {
        Class<?>[] parameterTypes = new Class[] { HashMap.class, String.class };
        Method doSomething = ReflectionUtils.getMethodQuietly(MockAction.class,
                "doNothing", parameterTypes);
        Map<Integer, Object> arguments = Maps.newEmptyHashMap();
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);
        when(invocation.getPreparedArgs()).thenReturn(arguments);

        preparator
                .prepareInvoke(doSomething, invocation, metadata, context, attributes, typeMapper);

        assertNull(arguments.get(0));
    }

    @Test
    public void testExtractToSpecifiedScopeFirstArgument() {
        Map<Integer, Object> arguments = Maps.newEmptyHashMap();
        Map<String, Object> scopedMap = new ScopedMapArgumentPreparator.ContextExtractor<Object>(
                "session");
        BigDecimal amount = new BigDecimal("1000");
        scopedMap.put("amount", amount);
        arguments.put(0, scopedMap);

        when(invocation.getPreparedArgs()).thenReturn(arguments);

        Object invocationResult = new Object();

        preparator.postInvoke(invocationResult, invocation, metadata, context, attributes,
                resultAttributes);

        verify(resultAttributes).setValueOfQuery(context, "session", "amount", amount);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testExtractToScopeFirstArgument() {
        Class<?>[] parameterTypes = new Class[] { Map.class, String.class };
        Map<Integer, Object> arguments = Maps.newEmptyHashMap();
        Map<String, Object> scopedMap = new ScopedMapArgumentPreparator.ContextExtractor<Object>("");
        BigDecimal amount = new BigDecimal("1000");
        scopedMap.put("amount", amount);
        arguments.put(0, scopedMap);

        when(metadata.getInvocationClass()).thenReturn((Class) MockAction.class);
        when(metadata.getMethodName()).thenReturn("doSomething");
        when(metadata.getArgumentTypes()).thenReturn(parameterTypes);
        when(invocation.getPreparedArgs()).thenReturn(arguments);

        Object invocationResult = new Object();

        preparator.postInvoke(invocationResult, invocation, metadata, context, attributes,
                resultAttributes);

        verify(resultAttributes).setValueOfQuery(context, "request", "amount", amount);
    }

    @Test
    public void testExtractAndRemoveToScopeFirstArgument() {
        Map<Integer, Object> arguments = Maps.newEmptyHashMap();
        Map<String, Object> scopedMap = new ScopedMapArgumentPreparator.ContextExtractor<Object>(
                "session");
        scopedMap.remove("amount");
        arguments.put(0, "boobaa");
        arguments.put(1, scopedMap);

        when(invocation.getPreparedArgs()).thenReturn(arguments);

        Object invocationResult = new Object();

        preparator.postInvoke(invocationResult, invocation, metadata, context, attributes,
                resultAttributes);

        verify(resultAttributes).removeValueOfQuery(context, "session", "amount");
    }

    @On
    private static class MockAction {
        @SuppressWarnings("unused")
        @On
        public String doSomething(@To Map<String, ?> foo, @As("baa") String baa) {
            return "do something!";
        }

        @SuppressWarnings("unused")
        @On
        public String doSomethingElse(@To("session") Map<String, Object> session,
                @As("baa") String baa) {
            return "do something!";
        }

        @SuppressWarnings("unused")
        @On
        public String doNothing(@To HashMap<String, ?> foo, @As("baa") String baa) {
            return "do something!";
        }

        @SuppressWarnings("unused")
        @On
        public String doAnything(@To String notMap, @As("baa") String baa) {
            return "do something!";
        }
    }

}
