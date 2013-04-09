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
import org.analogweb.annotation.By;
import org.analogweb.annotation.On;
import org.analogweb.annotation.RequestFormats;
import org.analogweb.core.response.HttpStatus;
import org.analogweb.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

public class ConsumesMediaTypeVerifierTest {

    private ConsumesMediaTypeVerifier verifier;
    private InvocationArguments args;
    private InvocationMetadata metadata;
    private RequestContext context;
    private TypeMapperContext converters;
    private RequestValueResolvers handlers;

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
        when(context.getContentType()).thenReturn(requstType);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsAtom",
                new Class<?>[] { Object.class });
        Object actual = verifier.prepareInvoke(method, args, metadata, context, converters,
                handlers);
        assertThat(actual, is(ApplicationProcessor.NO_INTERRUPTION));
    }

    @Test
    public void testPrepareInvokeInvalidMediaType() {
        MediaType requstType = MediaTypes.APPLICATION_JSON_TYPE;
        when(context.getContentType()).thenReturn(requstType);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsAtom",
                new Class<?>[] { Object.class });
        Object actual = verifier.prepareInvoke(method, args, metadata, context, converters,
                handlers);
        assertThat((HttpStatus) actual, is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    public void testPrepareInvokeDefaultFormats() {
        MediaType requstType = MediaTypes.APPLICATION_XML_TYPE;
        when(context.getContentType()).thenReturn(requstType);
        SpecificMediaTypeRequestValueResolver handler = mock(SpecificMediaTypeRequestValueResolver.class);
        when(handlers.findRequestValueResolver(Xml.class)).thenReturn(handler);
        when(handler.supports(requstType)).thenReturn(true);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsSvg",
                new Class<?>[] { Object.class });
        Object actual = verifier.prepareInvoke(method, args, metadata, context, converters,
                handlers);
        assertThat(actual, is(ApplicationProcessor.NO_INTERRUPTION));
    }

    @Test
    public void testPrepareInvokeDefaultFormatsNotSupported() {
        MediaType requstType = MediaTypes.APPLICATION_JSON_TYPE;
        when(context.getContentType()).thenReturn(requstType);
        SpecificMediaTypeRequestValueResolver handler = mock(SpecificMediaTypeRequestValueResolver.class);
        when(handlers.findRequestValueResolver(Xml.class)).thenReturn(handler);
        when(handler.supports(requstType)).thenReturn(false);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsSvg",
                new Class<?>[] { Object.class });
        Object actual = verifier.prepareInvoke(method, args, metadata, context, converters,
                handlers);
        assertThat((HttpStatus) actual, is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    public void testPrepareInvokeDefaultFormatsNotFound() {
        MediaType requstType = MediaTypes.TEXT_XML_TYPE;
        when(context.getContentType()).thenReturn(requstType);
        when(handlers.findRequestValueResolver(Xml.class)).thenReturn(null);
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsSvg",
                new Class<?>[] { Object.class });
        Object actual = verifier.prepareInvoke(method, args, metadata, context, converters,
                handlers);
        assertThat((HttpStatus) actual, is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    public void testPrepareInvokeNotDefinedFormats() {
        Method method = ReflectionUtils.getMethodQuietly(SomeResource.class, "acceptsParameter",
                new Class<?>[] { String.class });
        Object actual = verifier.prepareInvoke(method, args, metadata, context, converters,
                handlers);
        assertThat((HttpStatus) actual, is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    public void testPrecidence() {
        assertThat(verifier.getPrecedence(), is(1));
    }

    private static final class SomeResource {

        @RequestFormats(MediaTypes.APPLICATION_ATOM_XML)
        @On
        public String acceptsAtom(@By(Xml.class) @As Object anXml) {
            return "fake!";
        }

        @RequestFormats
        @On
        public String acceptsSvg(@By(Xml.class) @As Object anXml) {
            return "fake!";
        }

        @RequestFormats
        @On
        public String acceptsParameter(@As("param") String param) {
            return "fake!";
        }
    }

    interface Xml extends RequestValueResolver {
    }
}
