package org.analogweb.core;

import javax.servlet.ServletException;


import org.analogweb.core.DefaultExceptionHandler;
import org.analogweb.exception.ApplicationRuntimeException;
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
        thrown.expect(ServletException.class);
        thrown.expect(rootCause(SomeException.class));
        handler.handleException(new SomeException());
    }

    public static class SomeException extends ApplicationRuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static Matcher<Throwable> rootCause(final Class<? extends Throwable> throwable) {
        return new BaseMatcher<Throwable>() {

            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof ServletException) {
                    Throwable raised = ((ServletException) arg0).getRootCause();
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
