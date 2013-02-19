package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.analogweb.AttributesHandlers;
import org.analogweb.ContainerAdaptor;
import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.On;
import org.analogweb.core.UnresolvableInvocationException;
import org.analogweb.junit.NoDescribeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class DefaultInvocationFactoryTest {

    private ContainerAdaptor provider;
    private InvocationMetadata metadata;
    private RequestContext context;
    private ResponseContext response;
    private TypeMapperContext converters;
    private List<InvocationProcessor> processors;
    private InvocationProcessor processor;
    private AttributesHandlers handlers;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        provider = mock(ContainerAdaptor.class);
        metadata = mock(InvocationMetadata.class);
        context = mock(RequestContext.class);
        response = mock(ResponseContext.class);
        converters = mock(TypeMapperContext.class);
        processors = new ArrayList<InvocationProcessor>();
        processor = mock(InvocationProcessor.class);
        processors.add(processor);
        handlers = mock(AttributesHandlers.class);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testCreateViaProvider() {
        StubResource instance = new StubResource();
        when(provider.getInstanceOfType(StubResource.class)).thenReturn(instance);
        when(metadata.getInvocationClass()).thenReturn((Class) StubResource.class);
        DefaultInvocationFactory factory = new DefaultInvocationFactory();
        Invocation invocation = factory.createInvocation(provider, metadata, context, response,
                converters, processors, handlers);
        assertThat((StubResource) invocation.getInvocationInstance(), is(sameInstance(instance)));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testCreateContainerProvidesNullInstance() {
        when(provider.getInstanceOfType(StubResource.class)).thenReturn(null);
        when(metadata.getInvocationClass()).thenReturn((Class) StubResource.class);
        DefaultInvocationFactory factory = new DefaultInvocationFactory();
        Invocation invocation = factory.createInvocation(provider, metadata, context, response,
                converters, processors, handlers);
        assertThat(invocation.getInvocationInstance(), is(not(nullValue())));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testCreateContainerWithSpecifiedConstractor() {
        when(provider.getInstanceOfType(StubResourceWithConstractor.class)).thenReturn(null);
        when(metadata.getInvocationClass()).thenReturn((Class) StubResourceWithConstractor.class);
        AnnotatedInvocationParameterValueResolver resolver = mock(AnnotatedInvocationParameterValueResolver.class);
        when(
                resolver.resolve(isA(Annotation[].class), eq(String.class), eq(context),
                        eq(metadata), eq(converters), eq(handlers))).thenReturn("Test!");
        DefaultInvocationFactory factory = new DefaultInvocationFactory(resolver);
        Invocation invocation = factory.createInvocation(provider, metadata, context, response,
                converters, processors, handlers);
        assertThat(((StubResourceWithConstractor) invocation.getInvocationInstance()).value,
                is("Test!"));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testCreateUnInstanticatable() {
        thrown.expect(new NoDescribeMatcher<UnresolvableInvocationException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof UnresolvableInvocationException) {
                    UnresolvableInvocationException ex = (UnresolvableInvocationException) arg0;
                    InvocationMetadata actual = ex.getSourceMetadata();
                    assertThat(actual, is(metadata));
                    return true;
                }
                return false;
            }
        });
        when(provider.getInstanceOfType(StubResourceUnInstanticatable.class)).thenReturn(null);
        when(metadata.getInvocationClass()).thenReturn((Class) StubResourceUnInstanticatable.class);
        DefaultInvocationFactory factory = new DefaultInvocationFactory();
        factory.createInvocation(provider, metadata, context, response, converters, processors,
                handlers);
    }

    @On
    public static class StubResource {
        @On
        public String doSomething(@As("foo") String foo) {
            return foo + " is anything!!";
        }
    }

    @On
    public static class StubResourceWithConstractor {

        private String value;

        public StubResourceWithConstractor(@As("baa") String value) {
            this.value = value;
        }

        @On
        public String doSomething(@As("foo") String foo) {
            return foo + " is anything!!";
        }
    }

    @On
    public static class StubResourceUnInstanticatable {

        private StubResourceUnInstanticatable() {
            // nop.
        }

        @On
        public String doSomething(@As("foo") String foo) {
            return foo + " is anything!!";
        }
    }

}
