package org.analogweb.core;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.analogweb.ContainerAdaptor;
import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.On;
import org.analogweb.exception.UnresolvableInvocationException;
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
    private RequestAttributes attributes;
    private ResultAttributes resultAttributes;
    private RequestContext context;
    private TypeMapperContext converters;
    private List<InvocationProcessor> processors;
    private InvocationProcessor processor;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        provider = mock(ContainerAdaptor.class);
        metadata = mock(InvocationMetadata.class);
        attributes = mock(RequestAttributes.class);
        resultAttributes = mock(ResultAttributes.class);
        context = mock(RequestContext.class);
        converters = mock(TypeMapperContext.class);
        processors = new ArrayList<InvocationProcessor>();
        processor = mock(InvocationProcessor.class);
        processors.add(processor);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testCreate() {
        DefaultActionInvocationFactoryTestMockActions actionInstance = new DefaultActionInvocationFactoryTestMockActions();
        when(provider.getInstanceOfType(DefaultActionInvocationFactoryTestMockActions.class)).thenReturn(actionInstance);
        when(metadata.getInvocationClass()).thenReturn((Class)DefaultActionInvocationFactoryTestMockActions.class);
        DefaultInvocationFactory factory = new DefaultInvocationFactory();
        Invocation invocation = factory.createInvocation(provider, metadata,
                attributes, resultAttributes, context, converters, processors);
        assertSame(invocation.getInvocationInstance(), actionInstance);
        assertTrue(invocation.getPreparedArgs().isEmpty());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testCreateWithNullInstance() {
        thrown.expect(UnresolvableInvocationException.class);
        when(provider.getInstanceOfType(DefaultActionInvocationFactoryTestMockActions.class)).thenReturn(null);
        when(metadata.getInvocationClass()).thenReturn((Class)DefaultActionInvocationFactoryTestMockActions.class);
        DefaultInvocationFactory factory = new DefaultInvocationFactory();
        factory.createInvocation(provider, metadata,
                attributes, resultAttributes, context, converters, processors);
    }

    @On
    public static class DefaultActionInvocationFactoryTestMockActions {
        @On
        public String doSomething(@As("foo") String foo) {
            return foo + " is anything!!";
        }
    }

}
