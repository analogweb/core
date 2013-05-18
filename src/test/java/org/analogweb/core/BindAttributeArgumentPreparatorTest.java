package org.analogweb.core;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.analogweb.ApplicationProcessor;
import org.analogweb.AttributesHandler;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapper;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.Resolver;
import org.analogweb.annotation.Formats;
import org.analogweb.annotation.Convert;
import org.analogweb.annotation.Route;
import org.analogweb.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link BindAttributeArgumentPreparator}に対するテストケースです。
 * @author snowgoose
 */
public class BindAttributeArgumentPreparatorTest {

    private BindAttributeArgumentPreparator preparator;
    private InvocationMetadata metadata;
    private InvocationArguments args;
    private RequestContext context;
    private TypeMapperContext typeMapper;
    private RequestValueResolvers handlers;

    @Before
    public void setUp() {
        preparator = new BindAttributeArgumentPreparator();
        metadata = mock(InvocationMetadata.class);
        args = mock(InvocationArguments.class);
        context = mock(RequestContext.class);
        typeMapper = mock(TypeMapperContext.class);
        handlers = mock(RequestValueResolvers.class);
    }

    @Test
    public void testPrepare() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, String.class };
        final Method method = ReflectionUtils.getMethodQuietly(StubResource.class, "doSomething",
                argumentTypes);

        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.findRequestValueResolver(null)).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveValue(context, metadata, "foo", argumentTypes[0])).thenReturn(
                "foo!");
        when(typeMapper.mapToType(TypeMapper.class, "foo!", String.class, new String[0]))
                .thenReturn("foo!");
        when(handler.resolveValue(context, metadata, "baa", argumentTypes[1])).thenReturn(
                "baa!");
        when(typeMapper.mapToType(TypeMapper.class, "baa!", String.class, new String[0]))
                .thenReturn("baa!");

        Object actual = preparator.prepareInvoke(method, args, metadata, context, typeMapper,
                handlers);
        assertSame(actual, ApplicationProcessor.NO_INTERRUPTION);

        verify(args).putInvocationArgument(0, "foo!");
        verify(args).putInvocationArgument(1, "baa!");
    }

    @Test
    public void testPrepareWithCoveredAnnotation() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class };
        final Method method = ReflectionUtils.getMethodQuietly(StubResource.class,
                "doWithCustomAnnotation", argumentTypes);

        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.findRequestValueResolver(Baa.class)).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveValue(context, metadata, "foo", argumentTypes[0])).thenReturn(
                "boo!");
        when(typeMapper.mapToType(SomeTypeMapper.class, "boo!", String.class, new String[0]))
                .thenReturn("booz!");

        Object actual = preparator.prepareInvoke(method, args, metadata, context, typeMapper,
                handlers);
        assertSame(actual, ApplicationProcessor.NO_INTERRUPTION);

        verify(args).putInvocationArgument(0, "booz!");
    }

    @Test
    public void testPrepareWithFormat() {
        final Class<?>[] argumentTypes = new Class<?>[] { BigDecimal.class };
        final Method method = ReflectionUtils.getMethodQuietly(StubResource.class, "doWithFormat",
                argumentTypes);

        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.findRequestValueResolver(null)).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveValue(context, metadata, "foo", argumentTypes[0])).thenReturn(
                "100,000");
        BigDecimal expected = new BigDecimal(100000L);
        when(
                typeMapper.mapToType(TypeMapper.class, "100,000", BigDecimal.class,
                        new String[] { "###,###" })).thenReturn(expected);

        Object actual = preparator.prepareInvoke(method, args, metadata, context, typeMapper,
                handlers);
        assertSame(actual, ApplicationProcessor.NO_INTERRUPTION);

        verify(args).putInvocationArgument(0, expected);
    }

    @Route
    private static final class StubResource {
        @Route
        public String doSomething(@As("foo") String foo, @As("baa") String baa) {
            return "do something!";
        }

        @Route
        public String doScope(@As("baz") @Resolver(Foo.class) String baz) {
            return "do scope!";
        }

        @Route
        public String doWithCustomAnnotation(@As("foo") @Covered String foo) {
            return "do nothing!";
        }

        @Route
        public String doWithFormat(@As("foo") @Formats("###,###") BigDecimal amount) {
            return "do nothing!";
        }

    }

    interface SomeTypeMapper extends TypeMapper {
    }
    
    interface Foo extends RequestValueResolver {
    }
    
    interface Baa extends RequestValueResolver {
    }

    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Resolver(Baa.class)
    @Convert(SomeTypeMapper.class)
    static @interface Covered {
    }

}
