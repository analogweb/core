package org.analogweb.core.httpserver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.analogweb.RequestPath;
import org.analogweb.core.MediaTypes;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.Headers;

/**
 * @author snowgoose
 */
public class HttpExchangeRequestContextTest {

	private HttpExchangeRequestContext context;
	private RequestPath requestPath;
	private MockHttpExchange ex;

	@Before
	public void setUp() throws Exception {
		ex = new MockHttpExchange();
		requestPath = mock(RequestPath.class);
	}

	@Test
	public void testGetContentType() {
		context = new HttpExchangeRequestContext(ex, requestPath);
		Headers headers = new Headers();
		headers.put("Content-Type", Arrays.asList("text/xml"));
		ex.setRequestHeaders(headers);

		assertThat(context.getContentType().toString(),
				is(MediaTypes.TEXT_XML_TYPE.toString()));

		ex.setRequestHeaders(new Headers());
		assertThat(context.getContentType(), is(nullValue()));
	}

	@Test
	public void testGetCookies() {
		context = new HttpExchangeRequestContext(ex, requestPath);
		Headers headers = new Headers();
		headers.put("Cookie", Arrays.asList("foo=baa"));
		ex.setRequestHeaders(headers);

		assertThat(context.getCookies().getCookie("foo").getValue(), is("baa"));

		ex.setRequestHeaders(new Headers());

		assertThat(context.getCookies().getCookie("foo"), is(nullValue()));
	}

	@Test
	public void testGetRequestBody() throws Exception {
		context = new HttpExchangeRequestContext(ex, requestPath);
		InputStream expected = new ByteArrayInputStream(new byte[0]);
		ex.setRequestBody(expected);

		InputStream actual = context.getRequestBody();
		assertThat(actual, is(expected));
	}
}
