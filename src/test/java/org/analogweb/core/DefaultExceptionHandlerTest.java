package org.analogweb.core;

import static org.mockito.Mockito.when;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.analogweb.ExceptionMapper;
import org.analogweb.InvocationMetadata;
import org.analogweb.Modules;
import org.analogweb.RequestPathMetadata;
import org.analogweb.RequestValueResolver;
import org.analogweb.core.response.HttpStatus;
import org.analogweb.core.ApplicationRuntimeException;
import org.analogweb.core.RequestMethodUnsupportedException;
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
        Modules modules = mock(Modules.class);
        when(modules.getExceptionMappers()).thenReturn(
                Arrays.<ExceptionMapper> asList(new UnsupportedMediaTypeExceptionMapper(),
                        new InvalidRequestFormatExceptionMapper(),
                        new RequestMethodUnsupportedExceptionMapper()));
        handler.setModules(modules);
    }

    @Test
    public void testHandleThrowableWithApplicationRuntimeException() throws Exception {
        Object actual = handler.handleException(new SomeException());
        assertThat((HttpStatus) actual, is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testHandleThrowableWithRequestPathUnsupportedException() throws Exception {
        RequestPathMetadata metadata = mock(RequestPathMetadata.class);
        Object actual = handler.handleException(new RequestMethodUnsupportedException(metadata,
                Arrays.asList("GET"), "POST"));
        assertThat((HttpStatus) actual, is(HttpStatus.METHOD_NOT_ALLOWED));
    }

    @Test
    public void testHandleThrowableWithUnsupportedMediaTypeException() throws Exception {
        RequestPathMetadata metadata = mock(RequestPathMetadata.class);
        Object actual = handler.handleException(new UnsupportedMediaTypeException(metadata));
        assertThat((HttpStatus) actual, is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    public void testHandleRoutedThrowableWithUnsupportedMediaTypeException() throws Exception {
        RequestPathMetadata metadata = mock(RequestPathMetadata.class);
        InvocationMetadata invocationMetadata = mock(InvocationMetadata.class);
        Object actual = handler.handleException(new InvocationFailureException(
                new UnsupportedMediaTypeException(metadata), invocationMetadata, new String[0]));
        assertThat((HttpStatus) actual, is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    public void testHandleRoutedThrowableWithInvalidRequestFormatException() throws Exception {
        Object actual = handler.handleException(new InvalidRequestFormatException(
                RequestValueResolver.class));
        assertThat((HttpStatus) actual, is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testHandleRouteThrowableWithApplicationRuntimeException() throws Exception {
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        Object actual = handler.handleException(new InvocationFailureException(new SomeException(), metadata,
                new String[0]));
        assertThat((HttpStatus) actual, is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public static class SomeException extends ApplicationRuntimeException {

        private static final long serialVersionUID = 1L;
    }

}
