package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;

import org.analogweb.AttributesHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapper;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.By;
import org.analogweb.annotation.Formats;
import org.analogweb.annotation.MapWith;
import org.analogweb.annotation.On;
import org.analogweb.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

public class ScopedParameterValueResolverTest {

    private ScopedParameterValueResolver resolver;
    private InvocationMetadata metadata;
    private RequestContext context;
    private TypeMapperContext typeMapper;
    private RequestValueResolvers handlers;

    @Before
    public void setUp() {
        resolver = new ScopedParameterValueResolver();
        metadata = mock(InvocationMetadata.class);
        context = mock(RequestContext.class);
        typeMapper = mock(TypeMapperContext.class);
        handlers = mock(RequestValueResolvers.class);
    }

    @Test
    public void testPrepare() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, Date.class };
        final Method method = ReflectionUtils.getMethodQuietly(StubResource.class, "doSomething",
                argumentTypes);

        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.findDefaultRequestValueResolver()).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveValue(context, metadata, "foo", argumentTypes[0])).thenReturn(
                "foo!");
        when(typeMapper.mapToType(TypeMapper.class, "foo!", String.class, new String[0]))
                .thenReturn("foo!");

        String actual = resolver.resolve(method.getParameterAnnotations()[0], String.class,
                context, metadata, typeMapper, handlers);

        assertThat(actual, is("foo!"));
    }

    @Test
    public void testPrepareDate() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, Date.class };
        final Method method = ReflectionUtils.getMethodQuietly(StubResource.class, "doSomething",
                argumentTypes);

        Date expected = new Date();
        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.findDefaultRequestValueResolver()).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveValue(context, metadata, "baa", argumentTypes[1])).thenReturn(
                expected);
        when(typeMapper.mapToType(TypeMapper.class, expected, Date.class, new String[0]))
                .thenReturn(expected);

        Date actual = resolver.resolve(method.getParameterAnnotations()[1], Date.class, context,
                metadata, typeMapper, handlers);

        assertThat(actual, is(expected));
    }

    @Test
    public void testPrepareNothing() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class };
        final Method method = ReflectionUtils.getMethodQuietly(StubResource.class, "doNothing",
                argumentTypes);

        Object actual = resolver.resolve(method.getParameterAnnotations()[0], Date.class, context,
                metadata, typeMapper, handlers);

        assertThat(actual, is(nullValue()));
    }
    
    interface Session extends AttributesHandler{
    }

    @Test
    public void testPrepareScopedValue() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class };
        final Method method = ReflectionUtils.getMethodQuietly(StubResource.class, "doScope",
                argumentTypes);

        BigDecimal expected = BigDecimal.ONE;
        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.findRequestValueResolver(Session.class)).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveValue(context, metadata, "baz", argumentTypes[0])).thenReturn(
                expected);
        when(typeMapper.mapToType(TypeMapper.class, expected, String.class, new String[0]))
                .thenReturn("1");

        String actual = resolver.resolve(method.getParameterAnnotations()[0], String.class,
                context, metadata, typeMapper, handlers);

        assertThat(actual, is("1"));
    }

    interface Boo extends RequestValueResolver {
    }

    @Test
    public void testPrepareWithCustomAnnotation() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class };
        final Method method = ReflectionUtils.getMethodQuietly(StubResource.class,
                "doCustomAnnotation", argumentTypes);

        BigDecimal expected = BigDecimal.ONE;
        RequestValueResolver handler = mock(RequestValueResolver.class);
        when(handlers.findRequestValueResolver(Boo.class)).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveValue(context, metadata, "foo", argumentTypes[0])).thenReturn(
                expected);
        when(typeMapper.mapToType(SomeTypeMapper.class, expected, String.class, new String[0]))
                .thenReturn("1");

        String actual = resolver.resolve(method.getParameterAnnotations()[0], String.class,
                context, metadata, typeMapper, handlers);

        assertThat(actual, is("1"));
    }

    @Test
    public void testPreparedoWithFormat() {
        final Class<?>[] argumentTypes = new Class<?>[] { BigDecimal.class };
        final Method method = ReflectionUtils.getMethodQuietly(StubResource.class, "doWithFormat",
                argumentTypes);

        BigDecimal expected = new BigDecimal(1000);
        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.findDefaultRequestValueResolver()).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveValue(context, metadata, "foo", argumentTypes[0])).thenReturn(
                "1,000");
        when(
                typeMapper.mapToType(TypeMapper.class, "1,000", BigDecimal.class,
                        new String[] { "###,###" })).thenReturn(expected);

        BigDecimal actual = resolver.resolve(method.getParameterAnnotations()[0], BigDecimal.class,
                context, metadata, typeMapper, handlers);

        assertThat(actual, is(expected));
    }

    @On
    private static final class StubResource {
        @On
        public String doSomething(@As("foo") String foo, @As("baa") Date baa) {
            return "do something!";
        }

        @On
        public String doNothing(String foo) {
            return "do nothing!";
        }

        @On
        public String doScope(@As("baz") @By(Session.class) String baz) {
            return "do scope!";
        }

        @On
        public String doCustomAnnotation(@As("foo") @Covered String foo) {
            return "do nothing!";
        }

        @On
        public String doWithFormat(@As("foo") @Formats("###,###") BigDecimal amount) {
            return "do nothing!";
        }

    }

    interface SomeTypeMapper extends TypeMapper {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @By(Boo.class)
    @MapWith(SomeTypeMapper.class)
    static @interface Covered {
    }

}
