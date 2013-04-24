package org.analogweb.core.response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.ResponseContext.ResponseWriter;
import org.analogweb.core.DefaultResponseEntity;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

public class DefaultResponseTest {

	private DefaultResponse response;
	private RequestContext requestContext;
	private ResponseContext responseContext;

	@Before
	public void setUp() throws Exception {
		requestContext = mock(RequestContext.class);
		responseContext = mock(ResponseContext.class);
	}

	@Test
	public void testRenderDefault() throws Exception {
		response = new DefaultResponse();

		response.render(requestContext, responseContext);

		assertThat(response.getHeaders(), is(emptyMap()));
		assertThat(response.getResponseEntity(), is(nullValue()));
		verify(responseContext)
				.setStatus(HttpStatus.NO_CONTENT.getStatusCode());
	}

	@Test
	public void testRenderEntityAndHeaders() throws Exception {
		response = new DefaultResponse();
		ResponseEntity entity = new DefaultResponseEntity("This Is TEST!");
		response.setResponseEntity(entity);
		ResponseWriter writer = mock(ResponseWriter.class);
		when(responseContext.getResponseWriter()).thenReturn(writer);

		response.addHeader("Content-Type", "text/plain");

		Headers responseHeaders = mock(Headers.class);
		when(responseContext.getResponseHeaders()).thenReturn(responseHeaders);

		response.render(requestContext, responseContext);

		assertThat(response.getHeaders().size(), is(1));
		assertThat(response.getHeaders().get("Content-Type"), is("text/plain"));
		assertThat(response.getResponseEntity(), is(entity));
		verify(responseHeaders).putValue("Content-Type", "text/plain");
		verify(writer).writeEntity(entity);
		verify(responseContext).setStatus(HttpStatus.OK.getStatusCode());
	}

	private Matcher<Object> emptyMap() {
		return new BaseMatcher<Object>() {
			@Override
			public boolean matches(Object item) {
				if (item instanceof Map && ((Map<?, ?>) item).isEmpty()) {
					return true;
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {

			}
		};
	}

}
