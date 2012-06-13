package org.analogweb.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;


import org.analogweb.ContainerAdaptor;
import org.analogweb.Invocation;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.Invoker;
import org.analogweb.Modules;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.annotation.As;
import org.analogweb.annotation.On;
import org.analogweb.core.DefaultInvoker;
import org.analogweb.exception.AssertionFailureException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class DefaultInvokerTest {

    private InvocationMetadata metadata;
    private RequestAttributes attributes;
    private ResultAttributes resultAttributes;
    private RequestContext context;
    private List<InvocationProcessor> processors;
    private InvocationProcessor processor;
    private InvocationFactory factory;
    private Invocation invocation;
    private Modules modules;
    private ContainerAdaptor adaptor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        metadata = mock(InvocationMetadata.class);
        attributes = mock(RequestAttributes.class);
        resultAttributes = mock(ResultAttributes.class);
        context = mock(RequestContext.class);
        processors = new ArrayList<InvocationProcessor>();
        processor = mock(InvocationProcessor.class);
        processors.add(processor);
        factory = mock(InvocationFactory.class);
        invocation = mock(Invocation.class);
        modules = mock(Modules.class);
        adaptor = mock(ContainerAdaptor.class);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvoke() {

        final MockActions actionInstance = new MockActions();

        Invoker invoker = new DefaultInvoker();

        when(adaptor.getInstanceOfType(MockActions.class)).thenReturn(actionInstance);
        when(metadata.getInvocationClass()).thenReturn((Class)MockActions.class);
        when(
                factory.createInvocation(adaptor, metadata, attributes,
                        resultAttributes, context, null, processors)).thenReturn(invocation);

        Invocation invocation = mock(Invocation.class);
        // delegate to Invocation#invoke only.
        invoker.invoke(invocation, metadata, attributes, resultAttributes, context);

        verify(invocation).invoke();
    }

    @Test
    public void testInvokeWithNullInvocationMetadata() {

        thrown.expect(AssertionFailureException.class);

         Invoker invoker = new DefaultInvoker();

        when(modules.getInvocationFactory()).thenReturn(null);
        when(modules.getInvocationProcessors()).thenReturn(processors);

        Invocation invocation = mock(Invocation.class);
        // delegate to invocation only.
        invoker.invoke(invocation, null, attributes, resultAttributes, context);
    }

    @Test
    public void testInvokeWithNullInvocation() {

        thrown.expect(AssertionFailureException.class);

         Invoker invoker = new DefaultInvoker();

        when(modules.getInvocationFactory()).thenReturn(null);
        when(modules.getInvocationProcessors()).thenReturn(processors);

        // delegate to invocation only.
        invoker.invoke(null, metadata, attributes, resultAttributes, context);
    }

    @On
    public static class MockActions {
        @On
        public String doSomething(@As("foo") String foo) {
            return foo + " is anything!!";
        }
    }

}
