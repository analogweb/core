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

import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestAttributes;
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
    private Invocation invocation;
    private InvocationMetadata metadata;
    private RequestAttributes attributes;
    private RequestContext context;
    private TypeMapperContext typeMapper;
    private HttpServletRequest request;
    private HttpSession session;
    private ServletContext servletContext;

    @Before
    public void setUp() {
        preparator = new BindAttributeArgumentPreparator();
        invocation = mock(Invocation.class);
        metadata = mock(InvocationMetadata.class);
        attributes = mock(RequestAttributes.class);
        context = mock(RequestContext.class);
        typeMapper = mock(TypeMapperContext.class);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        servletContext = mock(ServletContext.class);
    }

    @Test
    public void testPrepare() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, String.class };
        final Method method = ReflectionUtils.getDeclaredMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class, "doSomething",
                argumentTypes);

        when(context.getRequest()).thenReturn(request);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(attributes.getValueOfQuery(context, "", "foo")).thenReturn("foo!");
        when(
                typeMapper.mapToType(TypeMapper.class, context, attributes, "foo!", String.class,
                        new String[0])).thenReturn("foo!");
        when(attributes.getValueOfQuery(context, "", "baa")).thenReturn("baa!");
        when(
                typeMapper.mapToType(TypeMapper.class, context, attributes, "baa!", String.class,
                        new String[0])).thenReturn("baa!");

        Invocation actual = preparator.prepareInvoke(method, invocation, metadata, context,
                attributes, typeMapper);
        assertSame(actual, invocation);

        verify(invocation).putPreparedArg(0, "foo!");
        verify(invocation).putPreparedArg(1, "baa!");
    }

    @Test
    public void testPrepareWithSrecialArgs() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, HttpServletRequest.class,
                String.class };
        final Method method = ReflectionUtils.getDeclaredMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class, "doAnything",
                argumentTypes);

        when(context.getRequest()).thenReturn(request);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(attributes.getValueOfQuery(context, "", "foo")).thenReturn("foo!");
        when(
                typeMapper.mapToType(TypeMapper.class, context, attributes, "foo!", String.class,
                        new String[0])).thenReturn("foo!");
        when(attributes.getValueOfQuery(context, "", "baz")).thenReturn("baz!");
        when(
                typeMapper.mapToType(TypeMapper.class, context, attributes, "baz!", String.class,
                        new String[0])).thenReturn("baz!");

        Invocation actual = preparator.prepareInvoke(method, invocation, metadata, context,
                attributes, typeMapper);
        assertSame(invocation, actual);

        verify(invocation).putPreparedArg(0, "foo!");
        verify(invocation).putPreparedArg(2, "baz!");
    }

    @Test
    public void testPrepareWithSrecialArgsAndNoBindArg() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, ServletContext.class,
                String.class };
        final Method method = ReflectionUtils.getDeclaredMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class, "doNothing",
                argumentTypes);

        when(context.getRequest()).thenReturn(request);
        when(context.getContext()).thenReturn(servletContext);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(attributes.getValueOfQuery(context, "", "baz")).thenReturn("baz!");
        when(
                typeMapper.mapToType(TypeMapper.class, context, attributes, "baz!", String.class,
                        new String[0])).thenReturn("baz!");

        Invocation actual = preparator.prepareInvoke(method, invocation, metadata, context,
                attributes, typeMapper);
        assertSame(invocation, actual);

        verify(invocation).putPreparedArg(2, "baz!");
    }

    @Test
    public void testPrepareWithSrecialArgsAndTypeMapping() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, HttpSession.class,
                Date.class, String.class };
        final Method method = ReflectionUtils.getDeclaredMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class, "doTypeMap",
                argumentTypes);

        Date now = new Date();

        when(context.getRequest()).thenReturn(request);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(attributes.getValueOfQuery(context, "", "baz")).thenReturn("baz!");
        when(
                typeMapper.mapToType(TypeMapper.class, context, attributes, "baz!", String.class,
                        new String[0])).thenReturn("baz!");
        when(attributes.getValueOfQuery(context, "", "baa")).thenReturn("2010/11/11");
        when(
                typeMapper.mapToType(TypeMapper.class, context, attributes, "2010/11/11",
                        Date.class, new String[0])).thenReturn(now);

        Invocation actual = preparator.prepareInvoke(method, invocation, metadata, context,
                attributes, typeMapper);
        assertSame(invocation, actual);

        verify(invocation).putPreparedArg(2, now);
        verify(invocation).putPreparedArg(3, "baz!");
    }

    @Test
    public void testPrepareWithSrecialArgsAndScope() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class };
        final Method method = ReflectionUtils.getDeclaredMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class, "doScope",
                argumentTypes);

        when(context.getRequest()).thenReturn(request);
        when(request.getSession(true)).thenReturn(session);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(attributes.getValueOfQuery(context, "session", "baz")).thenReturn("baz!");
        when(
                typeMapper.mapToType(TypeMapper.class, context, attributes, "baz!", String.class,
                        new String[0])).thenReturn("baz!");

        Invocation actual = preparator.prepareInvoke(method, invocation, metadata, context,
                attributes, typeMapper);
        assertSame(invocation, actual);

        verify(invocation).putPreparedArg(0, "baz!");
    }

    @Test
    public void testPrepareWithCoveredAnnotation() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class };
        final Method method = ReflectionUtils.getDeclaredMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class,
                "doWithCustomAnnotation", argumentTypes);

        when(context.getRequest()).thenReturn(request);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(attributes.getValueOfQuery(context, "boo", "foo")).thenReturn("boo!");
        when(
                typeMapper.mapToType(SomeTypeMapper.class, context, attributes, "boo!",
                        String.class, new String[0])).thenReturn("booz!");

        Invocation actual = preparator.prepareInvoke(method, invocation, metadata, context,
                attributes, typeMapper);
        assertSame(invocation, actual);

        verify(invocation).putPreparedArg(0, "booz!");
    }

    @Test
    public void testPrepareWithNotAvairableValueInContext() {
        final Class<?>[] argumentTypes = new Class<?>[] { String.class, HttpServletResponse.class,
                String.class, Integer.class };
        final Method method = ReflectionUtils.getDeclaredMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class, "doWithoutArgs",
                argumentTypes);

        when(context.getRequest()).thenReturn(request);
        when(request.getSession(true)).thenReturn(session);
        when(context.getContext()).thenReturn(servletContext);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(attributes.getValueOfQuery(context, "", "foo")).thenReturn("foo");
        when(
                typeMapper.mapToType(TypeMapper.class, context, attributes, "foo", String.class,
                        new String[0])).thenReturn("foo");
        // baa attribute ignored.
        when(attributes.getValueOfQuery(context, "", "baa")).thenReturn(null);
        when(attributes.getValueOfQuery(context, "", "baz")).thenReturn("100");
        when(
                typeMapper.mapToType(TypeMapper.class, context, attributes, "100", Integer.class,
                        new String[0])).thenReturn(100);

        Invocation actual = preparator.prepareInvoke(method, invocation, metadata, context,
                attributes, typeMapper);
        assertSame(invocation, actual);

        verify(invocation).putPreparedArg(0, "foo");
        verify(invocation).putPreparedArg(3, 100);
    }

    @Test
    public void testPrepareWithFormat() {
        final Class<?>[] argumentTypes = new Class<?>[] { BigDecimal.class };
        final Method method = ReflectionUtils.getDeclaredMethodQuietly(
                DefaultActionInvocationArgumentPreparatorTestMockActions.class, "doWithFormat",
                argumentTypes);

        when(context.getRequest()).thenReturn(request);
        when(metadata.getArgumentTypes()).thenReturn(argumentTypes);
        when(attributes.getValueOfQuery(context, "", "foo")).thenReturn("100,000");
        BigDecimal expected = new BigDecimal(100000L);
        when(
                typeMapper.mapToType(TypeMapper.class, context, attributes, "100,000",
                        BigDecimal.class, new String[] { "###,###" })).thenReturn(expected);

        Invocation actual = preparator.prepareInvoke(method, invocation, metadata, context,
                attributes, typeMapper);
        assertSame(actual, invocation);

        verify(invocation).putPreparedArg(0, expected);
    }

    @On
    private static final class DefaultActionInvocationArgumentPreparatorTestMockActions {
        @SuppressWarnings("unused")
        @On
        public String doSomething(@As("foo") String foo, @As("baa") String baa) {
            return "do something!";
        }

        @SuppressWarnings("unused")
        @On
        public String doAnything(@As("foo") String foo, HttpServletRequest request,
                @As("baz") String baz) {
            return "do anything!";
        }

        @SuppressWarnings("unused")
        @On
        public String doNothing(String foo, ServletContext context, @As("baz") String baz) {
            return "do nothing!";
        }

        @SuppressWarnings("unused")
        @On
        public String doTypeMap(String foo, HttpSession session,
                @As("baa") @MapWith(TypeMapper.class) Date baa, @As("baz") String baz) {
            return "do map!";
        }

        @SuppressWarnings("unused")
        @On
        public String doScope(@As("baz") @Scope("session") String baz) {
            return "do scope!";
        }

        @SuppressWarnings("unused")
        @On
        public String doWithoutArgs(@As("foo") String foo, HttpServletResponse response,
                @As("baa") String baa, @As("baz") Integer baz) {
            return "do nothing!";
        }

        @SuppressWarnings("unused")
        @On
        public String doWithCustomAnnotation(@As("foo") @Covered String foo) {
            return "do nothing!";
        }

        @SuppressWarnings("unused")
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
