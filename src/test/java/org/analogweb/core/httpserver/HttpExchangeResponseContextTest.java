package org.analogweb.core.httpserver;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import org.analogweb.RequestContext;
import org.analogweb.core.ApplicationRuntimeException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class HttpExchangeResponseContextTest {

	private HttpExchangeResponseContext context;
	private MockHttpExchange exc;
	private RequestContext requestContext;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		exc = new MockHttpExchange();
		context = new HttpExchangeResponseContext(exc);
		requestContext = mock(RequestContext.class);
	}

	@Test
	public void testCommmit() throws Exception {
		context.getResponseWriter().writeEntity("ThisIsTest");
		context.commmit(requestContext);
		assertThat(exc.getSendStatus(), is(HttpURLConnection.HTTP_OK));
		assertThat(exc.getResponseContentLength(), is(10L));
		assertThat(
				new String(
						((ByteArrayOutputStream) exc.getResponseBody())
								.toByteArray()), is("ThisIsTest"));
	}

	@Test
	public void testCommmitNoContent() throws Exception {
		context.getResponseWriter().writeEntity("ThisIsTest");
		context.setStatus(HttpURLConnection.HTTP_NO_CONTENT);
		context.commmit(requestContext);
		assertThat(exc.getSendStatus(), is(HttpURLConnection.HTTP_NO_CONTENT));
		assertThat(exc.getResponseContentLength(),
				is(HttpExchangeResponseContext.NO_CONTENT));
		assertThat(
				new String(
						((ByteArrayOutputStream) exc.getResponseBody())
								.toByteArray()), is("ThisIsTest"));
	}

	@Test
	public void testCommmitUnsupportedType() throws Exception {
		context.setStatus(HttpURLConnection.HTTP_UNSUPPORTED_TYPE);
		context.commmit(requestContext);
		assertThat(exc.getSendStatus(),
				is(HttpURLConnection.HTTP_UNSUPPORTED_TYPE));
		assertThat(exc.getResponseContentLength(),
				is(HttpExchangeResponseContext.NO_CONTENT));
	}

	@Test
	public void testCommmitIOException() throws Exception {
		thrown.expect(ApplicationRuntimeException.class);
		context.getResponseWriter().writeEntity("ThisIsTest");
		exc = new MockHttpExchange() {

			@Override
			public void sendResponseHeaders(int arg0, long arg1)
					throws IOException {
				throw new IOException();
			}
		};
		context = new HttpExchangeResponseContext(exc);
		context.commmit(requestContext);
	}
}
