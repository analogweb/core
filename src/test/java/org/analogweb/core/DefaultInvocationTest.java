package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.Route;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class DefaultInvocationTest {

	private DefaultInvocation invocation;
	private InvocationMetadata metadata;
	private RequestContext context;
	private ResponseContext response;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		metadata = mock(InvocationMetadata.class);
		context = mock(RequestContext.class);
		response = mock(ResponseContext.class);
	}

	@Test
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void testInvoke() {
		StubResource instance = new StubResource();
		when(metadata.getInvocationClass()).thenReturn(
				(Class) instance.getClass());
		when(metadata.getMethodName()).thenReturn("doSomething");
		when(metadata.getArgumentTypes()).thenReturn(
				new Class<?>[]{String.class});
		invocation = new DefaultInvocation(instance, metadata, context,
				response);
		invocation.putInvocationArgument(0, "foo");
		Object actual = invocation.invoke();
		assertThat(actual.toString(), is("foo is something!!"));
	}

	@Route
	public static class StubResource {

		@Route
		private String doNothing() {
			return null;
		}

		@Route
		public String doSomething(@As("foo") String foo) {
			return String.format("%s is something!!", foo);
		}

		@Route
		public String doAnything(@As("foo") String foo, String baa,
				@As("baz") Integer baz) {
			return String
					.format("No%s %s with %s is anything!!", baz, foo, baa);
		}

		@Route
		public String doSomethingWithException(@As("foo") String foo,
				@As("baa") Long baa) {
			throw new NullPointerException("oops!");
		}
	}
}
