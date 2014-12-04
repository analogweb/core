package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestPathMetadata;
import org.analogweb.RequestValueResolver;
import org.analogweb.core.response.HttpStatus;
import org.analogweb.core.ApplicationRuntimeException;
import org.analogweb.core.RequestMethodUnsupportedException;
import org.analogweb.WebApplicationException;
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
	public void testHandleThrowableWithApplicationRuntimeException()
			throws Exception {
		thrown.expect(WebApplicationException.class);
		thrown.expect(rootCause(SomeException.class));
		handler.handleException(new SomeException());
	}

	@Test
	public void testHandleThrowableWithRequestPathUnsupportedException()
			throws Exception {
		RequestPathMetadata metadata = mock(RequestPathMetadata.class);
		Object actual = handler
				.handleException(new RequestMethodUnsupportedException(
						metadata, Arrays.asList("GET"), "POST"));
		assertThat((HttpStatus) actual, is(HttpStatus.METHOD_NOT_ALLOWED));
	}

	@Test
	public void testHandleThrowableWithUnsupportedMediaTypeException()
			throws Exception {
		RequestPathMetadata metadata = mock(RequestPathMetadata.class);
		Object actual = handler
				.handleException(new UnsupportedMediaTypeException(metadata));
		assertThat((HttpStatus) actual, is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
	}

	@Test
	public void testHandleRoutedThrowableWithUnsupportedMediaTypeException()
			throws Exception {
		RequestPathMetadata metadata = mock(RequestPathMetadata.class);
		InvocationMetadata invocationMetadata = mock(InvocationMetadata.class);
		Object actual = handler.handleException(new InvocationFailureException(
				new UnsupportedMediaTypeException(metadata),
				invocationMetadata, new String[0]));
		assertThat((HttpStatus) actual, is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
	}

        @Test
        public void testHandleRoutedThrowableWithInvalidRequestFormatException()
                        throws Exception {
                Object actual = handler.handleException(new InvalidRequestFormatException(RequestValueResolver.class));
                assertThat((HttpStatus) actual, is(HttpStatus.BAD_REQUEST));
        }

        @Test
	public void testHandleRouteThrowableWithApplicationRuntimeException()
			throws Exception {
		thrown.expect(WebApplicationException.class);
		thrown.expect(rootCause(InvocationFailureException.class));
		InvocationMetadata metadata = mock(InvocationMetadata.class);
		handler.handleException(new InvocationFailureException(
				new SomeException(), metadata, new String[0]));
	}

	public static class SomeException extends ApplicationRuntimeException {

		private static final long serialVersionUID = 1L;
	}

	private static Matcher<Throwable> rootCause(
			final Class<? extends Throwable> throwable) {
		return new BaseMatcher<Throwable>() {

			@Override
			public boolean matches(Object arg0) {
				if (arg0 instanceof WebApplicationException) {
					Throwable raised = ((WebApplicationException) arg0)
							.getCause();
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
