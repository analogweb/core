package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestAttributes;
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
public class AbstractActionInvocationProcessorTest {

    private final AbstractInvocationProcessor processor = new AbstractInvocationProcessor() {
    };
    private Invocation invocation;
    private InvocationMetadata metadata;
    private RequestContext context;
    private RequestAttributes attributes;
    private TypeMapperContext converters;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        invocation = mock(Invocation.class);
        metadata = mock(InvocationMetadata.class);
        context = mock(RequestContext.class);
        attributes = mock(RequestAttributes.class);
        converters = mock(TypeMapperContext.class);
    }

    @Test
    public void testPrepareInvoke() {
        Object actual = processor.prepareInvoke((Method) null, invocation, metadata, context,
                attributes, converters);
        assertSame(actual, InvocationProcessor.NO_INTERRUPTION);
    }

    @Test
    public void testPostInvoke() {
        ResultAttributes resultAttributes = mock(ResultAttributes.class);
        Object invocationResult = new Object();
        Object actual = processor.postInvoke(invocationResult, invocation, metadata, context,
                attributes, resultAttributes);
        assertSame(actual, invocationResult);
    }

    @Test
    public void testAfterCompletion() {
        // do nothing.
        Object invocationResult = new Object();
        processor.afterCompletion(context, invocation, metadata, invocationResult);
    }

    @Test
    public void testProcessExceptionWithActionInvocationFailureException() {
        final Throwable th = new Throwable();
        final Object[] args = { "action invocation failure." };
        InvocationFailureException ex = new InvocationFailureException(th, metadata, args);

        thrown.expect(is(sameInstance(ex)));
        thrown.expect(new NoDescribeMatcher<InvocationFailureException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof InvocationFailureException) {
                    InvocationFailureException ex = (InvocationFailureException) arg0;
                    assertThat(ex.getArgs(), is(args));
                    assertThat(ex.getMetadata(), is(metadata));
                    assertThat(ex.getCause(), is(th));
                    return true;
                }
                return false;
            }
        });

        processor.processException(ex, context, invocation, metadata);
    }

    @Test
    public void testProcessExceptionWithActionInvocationFailureExceptionCausedInvocationTargetException() {
        final NullPointerException cause = new NullPointerException();
        final InvocationTargetException th = new InvocationTargetException(cause);
        final Object[] args = { "action invocation failure." };
        InvocationFailureException ex = new InvocationFailureException(th, metadata, args);

        thrown.expect(is(sameInstance(ex)));
        thrown.expect(new NoDescribeMatcher<InvocationFailureException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof InvocationFailureException) {
                    InvocationFailureException ex = (InvocationFailureException) arg0;
                    assertThat(ex.getArgs(), is(args));
                    assertThat(ex.getMetadata(), is(metadata));
                    assertThat(ex.getCause(), is((Throwable) cause));
                    return true;
                }
                return false;
            }
        });

        processor.processException(ex, context, invocation, metadata);
    }

    @Test
    public void testProcessExceptionWithAnotherException() {
        thrown.expect(InvocationFailureException.class);

        Exception ex = new Exception();

        processor.processException(ex, context, invocation, metadata);
    }

}
