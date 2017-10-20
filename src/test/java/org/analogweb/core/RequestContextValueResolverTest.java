package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.lang.annotation.Annotation;

import org.analogweb.Cookies;
import org.analogweb.Headers;
import org.analogweb.InvocationMetadata;
import org.analogweb.MediaType;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.junit.Before;
import org.junit.Test;

public class RequestContextValueResolverTest {

	private RequestContextValueResolver resolver = new RequestContextValueResolver();
	private RequestContext requestContext;
	private RequestPath path;
	private MediaType type;
	private Headers headers;
	private Cookies cookies;
	private InvocationMetadata metadata;

	@Before
	public void setUp() throws Exception {
		requestContext = mock(RequestContext.class);
		path = mock(RequestPath.class);
		when(requestContext.getRequestPath()).thenReturn(path);
		type = mock(MediaType.class);
		when(requestContext.getContentType()).thenReturn(type);
		headers = mock(Headers.class);
		when(requestContext.getRequestHeaders()).thenReturn(headers);
		cookies = mock(Cookies.class);
		when(requestContext.getCookies()).thenReturn(cookies);
		metadata = mock(InvocationMetadata.class);
	}

	@Test
	public void testResolved() {
		Object actual = resolver.resolveValue(requestContext, metadata, "",
				RequestPath.class, new Annotation[0]);
		assertThat((RequestPath) actual, is(path));
		actual = resolver.resolveValue(requestContext, metadata, "",
				MediaType.class, new Annotation[0]);
		assertThat((MediaType) actual, is(type));
		actual = resolver.resolveValue(requestContext, metadata, "",
				Headers.class, new Annotation[0]);
		assertThat((Headers) actual, is(headers));
		actual = resolver.resolveValue(requestContext, metadata, "",
				Cookies.class, new Annotation[0]);
		assertThat((Cookies) actual, is(cookies));
	}

	@Test
	public void testNotResolved() {
		Object actual = resolver.resolveValue(requestContext, metadata, "",
				String.class, new Annotation[0]);
		assertThat(actual, is(nullValue()));
		actual = resolver.resolveValue(requestContext, metadata, "",
				InputStream.class, new Annotation[0]);
		assertThat(actual, is(nullValue()));
		actual = resolver.resolveValue(requestContext, metadata, "", null,
				new Annotation[0]);
		assertThat(actual, is(nullValue()));
	}
}
