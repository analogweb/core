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
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.analogweb.AttributesHandler;
import org.analogweb.AttributesHandlers;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapper;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.Formats;
import org.analogweb.annotation.MapWith;
import org.analogweb.annotation.On;
import org.analogweb.annotation.Scope;
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
    private AttributesHandlers handlers;

    @Before
    public void setUp() {
        preparator = new BindAttributeArgumentPreparator();
        metadata = mock(InvocationMetadata.class);
        args = mock(InvocationArguments.class);
        context = mock(RequestContext.class);
        typeMapper = mock(TypeMapperContext.class);
        handlers = mock(AttributesHandlers.class);
    }

    @Test
    public void testPrepare() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, String.class };
        final Method method = ReflectionUtils.getMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class, "doSomething",
                argumentTypes);

        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.get("")).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveAttributeValue(context, metadata, "foo", argumentTypes[0])).thenReturn(
                "foo!");
        when(typeMapper.mapToType(TypeMapper.class, "foo!", String.class, new String[0]))
                .thenReturn("foo!");
        when(handler.resolveAttributeValue(context, metadata, "baa", argumentTypes[1])).thenReturn(
                "baa!");
        when(typeMapper.mapToType(TypeMapper.class, "baa!", String.class, new String[0]))
                .thenReturn("baa!");

        Object actual = preparator.prepareInvoke(method, args, metadata, context, typeMapper,
                handlers);
        assertSame(actual, InvocationProcessor.NO_INTERRUPTION);

        verify(args).putInvocationArgument(0, "foo!");
        verify(args).putInvocationArgument(1, "baa!");
    }

    @Test
    public void testPrepareWithSrecialArgs() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, HttpServletRequest.class,
                String.class };
        final Method method = ReflectionUtils.getMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class, "doAnything",
                argumentTypes);

        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.get("")).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveAttributeValue(context, metadata, "foo", argumentTypes[0])).thenReturn(
                "foo!");
        when(typeMapper.mapToType(TypeMapper.class, "foo!", String.class, new String[0]))
                .thenReturn("foo!");
        when(handler.resolveAttributeValue(context, metadata, "baz", argumentTypes[2])).thenReturn(
                "baz!");
        when(typeMapper.mapToType(TypeMapper.class, "baz!", String.class, new String[0]))
                .thenReturn("baz!");

        Object actual = preparator.prepareInvoke(method, args, metadata, context, typeMapper,
                handlers);
        assertSame(actual, InvocationProcessor.NO_INTERRUPTION);

        verify(args).putInvocationArgument(0, "foo!");
        verify(args).putInvocationArgument(2, "baz!");
    }

    @Test
    public void testPrepareWithCoveredAnnotation() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class };
        final Method method = ReflectionUtils.getMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class,
                "doWithCustomAnnotation", argumentTypes);

        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.get("boo")).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveAttributeValue(context, metadata, "foo", argumentTypes[0])).thenReturn(
                "boo!");
        when(typeMapper.mapToType(SomeTypeMapper.class, "boo!", String.class, new String[0]))
                .thenReturn("booz!");

        Object actual = preparator.prepareInvoke(method, args, metadata, context, typeMapper,
                handlers);
        assertSame(actual, InvocationProcessor.NO_INTERRUPTION);

        verify(args).putInvocationArgument(0, "booz!");
    }

    @Test
    public void testPrepareWithNotAvairableValueInContext() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, HttpServletResponse.class,
                String.class, Integer.class };
        final Method method = ReflectionUtils.getMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class, "doWithoutArgs",
                argumentTypes);

        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.get("")).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveAttributeValue(context, metadata, "foo", argumentTypes[0])).thenReturn(
                "foo");
        when(typeMapper.mapToType(TypeMapper.class, "foo", String.class, new String[0]))
                .thenReturn("foo");
        // baa attribute ignored.
        when(handler.resolveAttributeValue(context, metadata, "baa", argumentTypes[1])).thenReturn(
                null);
        when(handler.resolveAttributeValue(context, metadata, "baz", argumentTypes[3])).thenReturn(
                "100");
        when(typeMapper.mapToType(TypeMapper.class, "100", Integer.class, new String[0]))
                .thenReturn(100);

        Object actual = preparator.prepareInvoke(method, args, metadata, context, typeMapper,
                handlers);
        assertSame(actual, InvocationProcessor.NO_INTERRUPTION);

        verify(args).putInvocationArgument(0, "foo");
        verify(args).putInvocationArgument(3, 100);
    }

    @Test
    public void testPrepareWithFormat() {
        final Class<?>[] argumentTypes = new Class<?>[] { BigDecimal.class };
        final Method method = ReflectionUtils.getMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class, "doWithFormat",
                argumentTypes);

        AttributesHandler handler = mock(AttributesHandler.class);
        when(handlers.get("")).thenReturn(handler);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(handler.resolveAttributeValue(context, metadata, "foo", argumentTypes[0])).thenReturn(
                "100,000");
        BigDecimal expected = new BigDecimal(100000L);
        when(
                typeMapper.mapToType(TypeMapper.class, "100,000", BigDecimal.class,
                        new String[] { "###,###" })).thenReturn(expected);

        Object actual = preparator.prepareInvoke(method, args, metadata, context, typeMapper,
                handlers);
        assertSame(actual, InvocationProcessor.NO_INTERRUPTION);

        verify(args).putInvocationArgument(0, expected);
    }

    @On
    private static final class DefaultActionInvocationArgumentPreparatorTestMockActions {
        @On
        public String doSomething(@As("foo") String foo, @As("baa") String baa) {
            return "do something!";
        }

        @On
        public String doAnything(@As("foo") String foo, HttpServletRequest request,
                @As("baz") String baz) {
            return "do anything!";
        }

        @On
        public String doNothing(String foo, ServletContext context, @As("baz") String baz) {
            return "do nothing!";
        }

        @On
        public String doTypeMap(String foo, HttpSession session,
                @As("baa") @MapWith(TypeMapper.class) Date baa, @As("baz") String baz) {
            return "do map!";
        }

        @On
        public String doScope(@As("baz") @Scope("session") String baz) {
            return "do scope!";
        }

        @On
        public String doWithoutArgs(@As("foo") String foo, HttpServletResponse response,
                @As("baa") String baa, @As("baz") Integer baz) {
            return "do nothing!";
        }

        @On
        public String doWithCustomAnnotation(@As("foo") @Covered String foo) {
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
    @Scope("boo")
    @MapWith(SomeTypeMapper.class)
    static @interface Covered {
    }

}
