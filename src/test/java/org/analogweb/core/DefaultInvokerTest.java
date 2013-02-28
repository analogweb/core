package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.analogweb.AttributesHandlers;
import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.Invoker;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.TypeMapperContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class DefaultInvokerTest {

	private InvocationMetadata metadata;
	private RequestContext request;
	private ResponseContext response;
	private List<InvocationProcessor> processors;
	private InvocationProcessor processor;
	private Invocation invocation;
	private AttributesHandlers handlers;
	private TypeMapperContext typeMapper;
	private InvocationArguments args;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		metadata = mock(InvocationMetadata.class);
		request = mock(RequestContext.class);
		response = mock(ResponseContext.class);
		processors = new ArrayList<InvocationProcessor>();
		processor = mock(InvocationProcessor.class);
		processors.add(processor);
		invocation = mock(Invocation.class);
		handlers = mock(AttributesHandlers.class);
		typeMapper = mock(TypeMapperContext.class);
		args = mock(InvocationArguments.class);
	}

	@Test
	public void testInvoke() {
		Invoker invoker = new DefaultInvoker(processors, typeMapper, handlers);

		Object result = new Object();
		when(invocation.invoke()).thenReturn(result);
		when(invocation.getInvocationArguments()).thenReturn(args);
		when(invocation.prepareInvoke(processors, handlers, typeMapper))
				.thenReturn(InvocationProcessor.NO_INTERRUPTION);
		doNothing().when(invocation).postInvoke(processors, result, handlers);
		doNothing().when(invocation).afterCompletion(processors, result);

		// delegate to Invocation#invoke only.
		Object actual = invoker.invoke(invocation, metadata, request, response);

		assertThat(actual, is(result));
		verify(invocation).prepareInvoke(processors, handlers, typeMapper);
		verify(invocation).invoke();
		verify(invocation).postInvoke(processors, result, handlers);
		verify(invocation).afterCompletion(processors, result);
	}

	@Test
	public void testInvokeWithException() {
		thrown.expect(InvocationFailureException.class);
		Invoker invoker = new DefaultInvoker(processors, typeMapper, handlers);

		Object result = new Object();
		when(invocation.invoke()).thenThrow(new IllegalArgumentException());
		when(invocation.getInvocationArguments()).thenReturn(args);
		when(args.asList()).thenReturn(Collections.emptyList());
		when(invocation.prepareInvoke(processors, handlers, typeMapper))
				.thenReturn(InvocationProcessor.NO_INTERRUPTION);
		when(
				invocation.onException(eq(processors),
						isA(IllegalArgumentException.class))).thenReturn(
				InvocationProcessor.NO_INTERRUPTION);
		doNothing().when(invocation).afterCompletion(processors, result);

		invoker.invoke(invocation, metadata, request, response);
	}

	@Test
	public void testInvokeWithInterruption() {
		Invoker invoker = new DefaultInvoker(processors, typeMapper, handlers);

		Object result = new Object();
		when(invocation.getInvocationArguments()).thenReturn(args);
		when(invocation.prepareInvoke(processors, handlers, typeMapper))
				.thenReturn(result);

		// delegate to Invocation#invoke only.
		Object actual = invoker.invoke(invocation, metadata, request, response);

		assertThat(actual, is(result));
		verify(invocation).prepareInvoke(processors, handlers, typeMapper);
	}

	@Test
	public void testInvokeOnExceptionInterruption() {
		Invoker invoker = new DefaultInvoker(processors, typeMapper, handlers);

		Object result = new Object();
		when(invocation.invoke()).thenThrow(new IllegalArgumentException());
		when(invocation.getInvocationArguments()).thenReturn(args);
		when(args.asList()).thenReturn(Collections.emptyList());
		when(invocation.prepareInvoke(processors, handlers, typeMapper))
				.thenReturn(InvocationProcessor.NO_INTERRUPTION);
		when(
				invocation.onException(eq(processors),
						isA(IllegalArgumentException.class)))
				.thenReturn(result);

		Object actual = invoker.invoke(invocation, metadata, request, response);

		assertThat(actual, is(result));
		verify(invocation).prepareInvoke(processors, handlers, typeMapper);
		verify(invocation).invoke();
		verify(invocation).onException(eq(processors),
				isA(IllegalArgumentException.class));
	}

}
