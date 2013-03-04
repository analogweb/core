package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.analogweb.AttributesHandlers;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.On;
import org.analogweb.util.ReflectionUtils;
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
	private TypeMapperContext converters;
	private List<InvocationProcessor> processors;
	private InvocationProcessor processor1;
	private InvocationProcessor processor2;
	private InvocationProcessor processor3;
	private AttributesHandlers handlers;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		metadata = mock(InvocationMetadata.class);
		context = mock(RequestContext.class);
		response = mock(ResponseContext.class);
		converters = mock(TypeMapperContext.class);
		processor1 = mock(InvocationProcessor.class);
		processor2 = mock(InvocationProcessor.class);
		processor3 = mock(InvocationProcessor.class);
		processors = Arrays.asList(processor1, processor2, processor3);
		handlers = mock(AttributesHandlers.class);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testInvoke() {
		StubResource instance = new StubResource();
		when(metadata.getInvocationClass()).thenReturn(
				(Class) instance.getClass());
		when(metadata.getMethodName()).thenReturn("doSomething");
		when(metadata.getArgumentTypes()).thenReturn(
				new Class<?>[] { String.class });

		invocation = new DefaultInvocation(instance, metadata, context,
				response);
		invocation.putInvocationArgument(0, "foo");

		Object actionResult = new Object();
		doNothing().when(processor1).postInvoke("foo is something!!", invocation,
				metadata, context, handlers);
		doNothing().when(processor1).afterCompletion(context, invocation,
				metadata, actionResult);

		Object actual = invocation.invoke();
		assertThat(actual.toString(), is("foo is something!!"));
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testPrepareInvoke() {

		StubResource instance = new StubResource();
		final String methodName = "doSomething";
		final Class<?>[] argumentTypes = new Class<?>[] { String.class };
		final Method method = ReflectionUtils.getMethodQuietly(
				StubResource.class, methodName, argumentTypes);
		when(metadata.getInvocationClass()).thenReturn(
				(Class) instance.getClass());
		when(metadata.getMethodName()).thenReturn("doSomething");
		when(metadata.getArgumentTypes()).thenReturn(
				new Class<?>[] { String.class });

		invocation = new DefaultInvocation(instance, metadata, context,
				response);
		invocation.putInvocationArgument(0, "foo");

		when(
				processor1.prepareInvoke(method, invocation, metadata, context,
						converters, handlers)).thenReturn(
				InvocationProcessor.NO_INTERRUPTION);
		when(
				processor2.prepareInvoke(method, invocation, metadata, context,
						converters, handlers)).thenReturn(
				InvocationProcessor.NO_INTERRUPTION);
		when(
				processor3.prepareInvoke(method, invocation, metadata, context,
						converters, handlers)).thenReturn(
				InvocationProcessor.NO_INTERRUPTION);

		Object actual = invocation.prepareInvoke(processors, handlers,
				converters);
		assertThat(actual, is(InvocationProcessor.NO_INTERRUPTION));

		verify(processor1).prepareInvoke(method, invocation, metadata, context,
				converters, handlers);
		verify(processor2).prepareInvoke(method, invocation, metadata, context,
				converters, handlers);
		verify(processor3).prepareInvoke(method, invocation, metadata, context,
				converters, handlers);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testPrepareInvokeWithInterruption() {

		StubResource instance = new StubResource();
		final String methodName = "doSomething";
		final Class<?>[] argumentTypes = new Class<?>[] { String.class };
		final Method method = ReflectionUtils.getMethodQuietly(
				StubResource.class, methodName, argumentTypes);
		when(metadata.getInvocationClass()).thenReturn(
				(Class) instance.getClass());
		when(metadata.getMethodName()).thenReturn("doSomething");
		when(metadata.getArgumentTypes()).thenReturn(
				new Class<?>[] { String.class });

		invocation = new DefaultInvocation(instance, metadata, context,
				response);
		invocation.putInvocationArgument(0, "foo");

		Object result = new Object();
		when(
				processor1.prepareInvoke(method, invocation, metadata, context,
						converters, handlers)).thenReturn(
				InvocationProcessor.NO_INTERRUPTION);
		when(
				processor2.prepareInvoke(method, invocation, metadata, context,
						converters, handlers)).thenReturn(result);
		when(
				processor3.prepareInvoke(method, invocation, metadata, context,
						converters, handlers)).thenReturn(
				InvocationProcessor.NO_INTERRUPTION);

		Object actual = invocation.prepareInvoke(processors, handlers,
				converters);
		assertThat(actual, is(result));

		verify(processor1).prepareInvoke(method, invocation, metadata, context,
				converters, handlers);
		verify(processor2).prepareInvoke(method, invocation, metadata, context,
				converters, handlers);
		verifyZeroInteractions(processor3);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testPostInvoke() {

		StubResource instance = new StubResource();
		when(metadata.getInvocationClass()).thenReturn(
				(Class) instance.getClass());
		when(metadata.getMethodName()).thenReturn("doSomething");
		when(metadata.getArgumentTypes()).thenReturn(
				new Class<?>[] { String.class });

		invocation = new DefaultInvocation(instance, metadata, context,
				response);
		invocation.putInvocationArgument(0, "foo");

		Object result = new Object();
		doNothing().when(
				processor1).postInvoke(result, invocation, metadata, context,
						handlers);
		doNothing().when(
				processor2).postInvoke(result, invocation, metadata, context,
						handlers);
		doNothing().when(
				processor3).postInvoke(result, invocation, metadata, context,
						handlers);

		invocation.postInvoke(processors, result, handlers);

		verify(processor1).postInvoke(result, invocation, metadata, context,
				handlers);
		verify(processor2).postInvoke(result, invocation, metadata, context,
				handlers);
		verify(processor3).postInvoke(result, invocation, metadata, context,
				handlers);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testOnException() {

		StubResource instance = new StubResource();
		when(metadata.getInvocationClass()).thenReturn(
				(Class) instance.getClass());
		when(metadata.getMethodName()).thenReturn("doSomething");
		when(metadata.getArgumentTypes()).thenReturn(
				new Class<?>[] { String.class });

		invocation = new DefaultInvocation(instance, metadata, context,
				response);
		invocation.putInvocationArgument(0, "foo");

		Exception ex = new Exception();
		when(processor1.processException(ex, context, invocation, metadata))
				.thenReturn(InvocationProcessor.NO_INTERRUPTION);
		when(processor2.processException(ex, context, invocation, metadata))
				.thenReturn(InvocationProcessor.NO_INTERRUPTION);
		when(processor3.processException(ex, context, invocation, metadata))
				.thenReturn(InvocationProcessor.NO_INTERRUPTION);

		invocation.onException(processors, ex);

		verify(processor1).processException(ex, context, invocation, metadata);
		verify(processor2).processException(ex, context, invocation, metadata);
		verify(processor3).processException(ex, context, invocation, metadata);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testOnExceptionWithInterruption() {

		StubResource instance = new StubResource();
		when(metadata.getInvocationClass()).thenReturn(
				(Class) instance.getClass());
		when(metadata.getMethodName()).thenReturn("doSomething");
		when(metadata.getArgumentTypes()).thenReturn(
				new Class<?>[] { String.class });

		invocation = new DefaultInvocation(instance, metadata, context,
				response);
		invocation.putInvocationArgument(0, "foo");

		Object result = new Object();
		Exception ex = new Exception();
		when(processor1.processException(ex, context, invocation, metadata))
				.thenReturn(InvocationProcessor.NO_INTERRUPTION);
		when(processor2.processException(ex, context, invocation, metadata))
				.thenReturn(result);

		Object actual = invocation.onException(processors, ex);

		assertThat(actual, is(result));

		verify(processor1).processException(ex, context, invocation, metadata);
		verify(processor2).processException(ex, context, invocation, metadata);
		verifyZeroInteractions(processor3);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testAfterCompletion() {

		StubResource instance = new StubResource();
		when(metadata.getInvocationClass()).thenReturn(
				(Class) instance.getClass());
		when(metadata.getMethodName()).thenReturn("doSomething");
		when(metadata.getArgumentTypes()).thenReturn(
				new Class<?>[] { String.class });

		invocation = new DefaultInvocation(instance, metadata, context,
				response);
		invocation.putInvocationArgument(0, "foo");

		Object result = new Object();
		doNothing().when(processor1).afterCompletion(context, invocation,
				metadata, result);
		doNothing().when(processor2).afterCompletion(context, invocation,
				metadata, result);
		doNothing().when(processor3).afterCompletion(context, invocation,
				metadata, result);

		invocation.afterCompletion(processors, result);

		verify(processor1).afterCompletion(context, invocation, metadata,
				result);
		verify(processor2).afterCompletion(context, invocation, metadata,
				result);
		verify(processor3).afterCompletion(context, invocation, metadata,
				result);
	}

	@On
	public static class StubResource {
		@On
		private String doNothing() {
			return null;
		}

		@On
		public String doSomething(@As("foo") String foo) {
			return String.format("%s is something!!", foo);
		}

		@On
		public String doAnything(@As("foo") String foo, String baa,
				@As("baz") Integer baz) {
			return String
					.format("No%s %s with %s is anything!!", baz, foo, baa);
		}

		@On
		public String doSomethingWithException(@As("foo") String foo,
				@As("baa") Long baa) {
			throw new NullPointerException("oops!");
		}
	}

}
