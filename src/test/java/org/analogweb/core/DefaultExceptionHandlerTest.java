package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.analogweb.RequestPathMetadata;
import org.analogweb.core.direction.HttpStatus;
import org.analogweb.exception.ApplicationRuntimeException;
import org.analogweb.exception.RequestMethodUnsupportedException;
import org.analogweb.exception.WebApplicationException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class DefaultExceptionHandlerTest {

    private DefaultExceptionHandler handler;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        handler = new DefaultExceptionHandler();
    }

    @Test
    public void testHandleThrowableWithApplicationRuntimeException() throws Exception {
        thrown.expect(WebApplicationException.class);
        thrown.expect(rootCause(SomeException.class));
        handler.handleException(new SomeException());
    }

    @Test
    public void testHandleThrowableWithRequestPathUnsupportedException() throws Exception {
        RequestPathMetadata metadata = mock(RequestPathMetadata.class);
        Object actual = handler.handleException(new RequestMethodUnsupportedException(metadata,
                Arrays.asList("GET"), "POST"));
        assertThat((HttpStatus) actual, is(HttpStatus.METHOD_NOT_ALLOWED));
    }

    public static class SomeException extends ApplicationRuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static Matcher<Throwable> rootCause(final Class<? extends Throwable> throwable) {
        return new BaseMatcher<Throwable>() {

            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof WebApplicationException) {
                    Throwable raised = ((WebApplicationException) arg0).getCause();
                    return throwable.equals(raised.getClass());
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description arg0) {
                // nop.
            }
        };
    }

}
