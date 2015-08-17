package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;

import org.analogweb.ApplicationProcessor;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolvers;
import org.analogweb.ResponseContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.junit.NoDescribeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class AbstractApplicationProcessorTest {

    private final AbstractApplicationProcessor processor = new AbstractApplicationProcessor() {
    };
    private InvocationMetadata metadata;
    private InvocationArguments args;
    private RequestContext context;
    private ResponseContext response;
    private TypeMapperContext converters;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        metadata = mock(InvocationMetadata.class);
        args = mock(InvocationArguments.class);
        context = mock(RequestContext.class);
        response = mock(ResponseContext.class);
        converters = mock(TypeMapperContext.class);
    }

    @Test
    public void testPrepareInvoke() {
        Object actual = processor.prepareInvoke(args, metadata, context, converters,
                null);
        assertSame(actual, ApplicationProcessor.NO_INTERRUPTION);
    }

    @Test
    public void testPostInvoke() {
        // do nothing.
        RequestValueResolvers handlers = mock(RequestValueResolvers.class);
        Object invocationResult = new Object();
        processor.postInvoke(invocationResult, args, metadata, context, handlers);
    }

    @Test
    public void testAfterCompletion() {
        // do nothing.
        processor.afterCompletion(context, response, null);
    }

    @Test
    public void testProcessExceptionWithActionInvocationFailureException() {
        final Throwable th = new Throwable();
        final Object[] messages = { "action invocation failure." };
        InvocationFailureException ex = new InvocationFailureException(th, metadata, messages);
        thrown.expect(is(sameInstance(ex)));
        thrown.expect(new NoDescribeMatcher<InvocationFailureException>() {

            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof InvocationFailureException) {
                    InvocationFailureException ex = (InvocationFailureException) arg0;
                    assertThat(ex.getArgs(), is(messages));
                    assertThat(ex.getMetadata(), is(metadata));
                    assertThat(ex.getCause(), is(th));
                    return true;
                }
                return false;
            }
        });
        processor.processException(ex, context, args, metadata);
    }

    @Test
    public void testProcessExceptionWithActionInvocationFailureExceptionCausedInvocationTargetException() {
        final NullPointerException cause = new NullPointerException();
        final InvocationTargetException th = new InvocationTargetException(cause);
        final Object[] messages = { "action invocation failure." };
        InvocationFailureException ex = new InvocationFailureException(th, metadata, messages);
        thrown.expect(is(sameInstance(ex)));
        thrown.expect(new NoDescribeMatcher<InvocationFailureException>() {

            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof InvocationFailureException) {
                    InvocationFailureException ex = (InvocationFailureException) arg0;
                    assertThat(ex.getArgs(), is(messages));
                    assertThat(ex.getMetadata(), is(metadata));
                    assertThat(ex.getCause(), is((Throwable) cause));
                    return true;
                }
                return false;
            }
        });
        processor.processException(ex, context, args, metadata);
    }

    @Test
    public void testProcessExceptionWithAnotherException() {
        Exception ex = new Exception();
        Object actual = processor.processException(ex, context, args, metadata);
        assertThat(actual, is(ApplicationProcessor.NO_INTERRUPTION));
    }
}
