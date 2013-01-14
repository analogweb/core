package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.analogweb.AttributesHandlers;
import org.analogweb.ContainerAdaptor;
import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.On;
import org.analogweb.exception.UnresolvableInvocationException;
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
        converters = mock(TypeMapperContext.class);
        processors = new ArrayList<InvocationProcessor>();
        processor = mock(InvocationProcessor.class);
        processors.add(processor);
        handlers = mock(AttributesHandlers.class);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testCreate() {
        DefaultActionInvocationFactoryTestMockActions actionInstance = new DefaultActionInvocationFactoryTestMockActions();
        when(provider.getInstanceOfType(DefaultActionInvocationFactoryTestMockActions.class))
                .thenReturn(actionInstance);
        when(metadata.getInvocationClass()).thenReturn(
                (Class) DefaultActionInvocationFactoryTestMockActions.class);
        DefaultInvocationFactory factory = new DefaultInvocationFactory();
        Invocation invocation = factory.createInvocation(provider, metadata, context, converters,
                processors, handlers);
        assertSame(invocation.getInvocationInstance(), actionInstance);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testCreateWithNullInstance() {
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
        when(provider.getInstanceOfType(DefaultActionInvocationFactoryTestMockActions.class))
                .thenReturn(null);
        when(metadata.getInvocationClass()).thenReturn(
                (Class) DefaultActionInvocationFactoryTestMockActions.class);
        DefaultInvocationFactory factory = new DefaultInvocationFactory();
        factory.createInvocation(provider, metadata, context, converters, processors, handlers);
    }

    @On
    public static class DefaultActionInvocationFactoryTestMockActions {
        @On
        public String doSomething(@As("foo") String foo) {
            return foo + " is anything!!";
        }
    }

}
