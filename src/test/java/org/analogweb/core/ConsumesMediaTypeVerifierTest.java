package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.analogweb.ApplicationProcessor;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.MediaType;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.Resolver;
import org.analogweb.annotation.Route;
import org.analogweb.annotation.RequestFormats;
import org.analogweb.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConsumesMediaTypeVerifierTest {

    private ConsumesMediaTypeVerifier verifier;
    private InvocationArguments args;
    private InvocationMetadata metadata;
    private RequestContext context;
    private TypeMapperContext converters;
    private RequestValueResolvers handlers;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        verifier = new ConsumesMediaTypeVerifier();
        args = mock(InvocationArguments.class);
        metadata = mock(InvocationMetadata.class);
        context = mock(RequestContext.class);
        converters = mock(TypeMapperContext.class);
        handlers = mock(RequestValueResolvers.class);
    }

    @Test
    public void testPrepareInvoke() {
        MediaType requstType = MediaTypes.APPLICATION_ATOM_XML_TYPE;
        when(context.getRequestMethod()).thenReturn("POST");
        when(context.getContentType()).thenReturn(requstType);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsAtom",
                new Class<?>[] { Object.class });
        when(metadata.resolveMethod()).thenReturn(method);
        Object actual = verifier.prepareInvoke(args, metadata, context, converters,
                handlers);
        assertThat(actual, is(ApplicationProcessor.NO_INTERRUPTION));
    }

    @Test
    public void testPrepareInvokeWithGetMethod() {
        MediaType requstType = MediaTypes.APPLICATION_ATOM_XML_TYPE;
        when(context.getRequestMethod()).thenReturn("GET");
        when(context.getContentType()).thenReturn(requstType);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsAtom",
                new Class<?>[] { Object.class });
        when(metadata.resolveMethod()).thenReturn(method);
        Object actual = verifier.prepareInvoke(args, metadata, context, converters,
                handlers);
        assertThat(actual, is(ApplicationProcessor.NO_INTERRUPTION));
    }

    @Test
    public void testPrepareInvokeInvalidMediaType() {
        thrown.expect(UnsupportedMediaTypeException.class);
        MediaType requstType = MediaTypes.APPLICATION_JSON_TYPE;
        when(context.getRequestMethod()).thenReturn("POST");
        when(context.getContentType()).thenReturn(requstType);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsAtom",
                new Class<?>[] { Object.class });
        when(metadata.resolveMethod()).thenReturn(method);
        verifier.prepareInvoke(args, metadata, context, converters, handlers);
    }

    @Test
    public void testPrepareInvokeDefaultFormats() {
        MediaType requstType = MediaTypes.APPLICATION_XML_TYPE;
        when(context.getRequestMethod()).thenReturn("POST");
        when(context.getContentType()).thenReturn(requstType);
        SpecificMediaTypeRequestValueResolver handler = mock(SpecificMediaTypeRequestValueResolver.class);
        when(handlers.findRequestValueResolver(Xml.class)).thenReturn(handler);
        when(handler.supports(requstType)).thenReturn(true);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsSvg",
                new Class<?>[] { Object.class });
        when(metadata.resolveMethod()).thenReturn(method);
        Object actual = verifier.prepareInvoke(args, metadata, context, converters,
                handlers);
        assertThat(actual, is(ApplicationProcessor.NO_INTERRUPTION));
    }

    @Test
    public void testPrepareInvokeDefaultFormatsNotSupported() {
        thrown.expect(UnsupportedMediaTypeException.class);
        MediaType requstType = MediaTypes.APPLICATION_JSON_TYPE;
        when(context.getRequestMethod()).thenReturn("POST");
        when(context.getContentType()).thenReturn(requstType);
        SpecificMediaTypeRequestValueResolver handler = mock(SpecificMediaTypeRequestValueResolver.class);
        when(handlers.findRequestValueResolver(Xml.class)).thenReturn(handler);
        when(handler.supports(requstType)).thenReturn(false);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsSvg",
                new Class<?>[] { Object.class });
        when(metadata.resolveMethod()).thenReturn(method);
        verifier.prepareInvoke(args, metadata, context, converters, handlers);
    }

    @Test
    public void testPrepareInvokeNotDefinedFormats() {
        thrown.expect(UnsupportedMediaTypeException.class);
        when(context.getRequestMethod()).thenReturn("PUT");
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsParameter",
                new Class<?>[] { String.class });
        when(metadata.resolveMethod()).thenReturn(method);
        verifier.prepareInvoke(args, metadata, context, converters, handlers);
    }

    @Test
    public void testPrepareInvokeWithoutFormats() {
        thrown.expect(UnsupportedMediaTypeException.class);
        when(context.getRequestMethod()).thenReturn("POST");
        when(context.getContentType()).thenReturn(MediaTypes.APPLICATION_XML_TYPE);
        SpecificMediaTypeRequestValueResolver handler = mock(SpecificMediaTypeRequestValueResolver.class);
        when(handler.supports(MediaTypes.APPLICATION_XML_TYPE)).thenReturn(false);
        when(handlers.findRequestValueResolver(Xml.class)).thenReturn(handler);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsXml",
                new Class<?>[] { Object.class });
        when(metadata.resolveMethod()).thenReturn(method);
        verifier.prepareInvoke(args, metadata, context, converters, handlers);
    }

    @Test
    public void testPrepareInvokeFailureWithoutFormats() {
        when(context.getContentType()).thenReturn(MediaTypes.APPLICATION_XML_TYPE);
        when(context.getRequestMethod()).thenReturn("PUT");
        SpecificMediaTypeRequestValueResolver handler = mock(SpecificMediaTypeRequestValueResolver.class);
        when(handler.supports(MediaTypes.APPLICATION_XML_TYPE)).thenReturn(true);
        when(handlers.findRequestValueResolver(Xml.class)).thenReturn(handler);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsXml",
                new Class<?>[] { Object.class });
        when(metadata.resolveMethod()).thenReturn(method);
        verifier.prepareInvoke(args, metadata, context, converters, handlers);
    }

    @Test
    public void testPrecidence() {
        assertThat(verifier.getPrecedence(), is(1));
    }

    private static final class SomeResource {

        @RequestFormats(MediaTypes.APPLICATION_ATOM_XML)
        @Route
        public String acceptsAtom(@Resolver(Xml.class) @As Object anXml) {
            return "fake!";
        }

        @Route
        public String acceptsXml(@Resolver(Xml.class) @As Object anXml) {
            return "fake!";
        }

        @RequestFormats
        @Route
        public String acceptsSvg(@Resolver(Xml.class) @As Object anXml) {
            return "fake!";
        }

        @RequestFormats
        @Route
        public String acceptsParameter(@As("param") String param) {
            return "fake!";
        }
    }

    interface Xml extends RequestValueResolver {
    }
}
