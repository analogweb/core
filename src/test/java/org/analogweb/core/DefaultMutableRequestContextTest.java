package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultMutableRequestContextTest {

	private DefaultMutableRequestContext target;
	private RequestContext context;

	@Before
	public void setUp() throws Exception {
		context = mock(RequestContext.class);
	}

	@Test
	public void testNotOverridden() {
		when(context.getRequestMethod()).thenReturn("GET");
		RequestPath path = mock(RequestPath.class);
		when(context.getRequestPath()).thenReturn(path);
		target = new DefaultMutableRequestContext(context);
		RequestContext actual = target.unwrap();
		assertThat(actual.getRequestMethod(), is("GET"));
		assertThat(actual.getRequestPath(), is(path));
	}

	@Test
	public void testOverridden() {
		when(context.getRequestMethod()).thenReturn("GET");
		RequestPath path = mock(RequestPath.class);
		when(context.getRequestPath()).thenReturn(path);
		target = new DefaultMutableRequestContext(context);
		target.setRequestMethod("PUT");
		RequestPath overridePath = mock(RequestPath.class);
		target.setRequestPath(overridePath);
		RequestContext actual = target.unwrap();
		assertThat(actual.getRequestMethod(), is("PUT"));
		assertThat(actual.getRequestPath(), is(overridePath));
	}
}
