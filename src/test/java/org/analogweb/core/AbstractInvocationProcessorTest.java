package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.TypeMapperContext;
import org.analogweb.exception.InvocationFailureException;
import org.analogweb.junit.NoDescribeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class AbstractInvocationProcessorTest {

    private final AbstractInvocationProcessor processor = new AbstractInvocationProcessor() {
    };
    private InvocationMetadata metadata;
    private InvocationArguments args;
    private RequestContext context;
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
        converters = mock(TypeMapperContext.class);
    }

    @Test
    public void testPrepareInvoke() {
        Object actual = processor.prepareInvoke((Method) null, args, metadata, context, converters,
                null);
        assertSame(actual, InvocationProcessor.NO_INTERRUPTION);
    }

    @Test
    public void testPostInvoke() {
        ResultAttributes resultAttributes = mock(ResultAttributes.class);
        Object invocationResult = new Object();
        Object actual = processor.postInvoke(invocationResult, args, metadata, context,
                resultAttributes);
        assertSame(actual, invocationResult);
    }

    @Test
    public void testAfterCompletion() {
        // do nothing.
        Object invocationResult = new Object();
        processor.afterCompletion(context, args, metadata, invocationResult);
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
        assertThat(actual, is(InvocationProcessor.NO_INTERRUPTION));
    }

}
