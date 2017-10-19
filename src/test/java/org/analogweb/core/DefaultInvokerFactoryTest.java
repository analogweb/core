package org.analogweb.core;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.analogweb.InvocationInterceptor;
import org.analogweb.Invoker;
import org.junit.Test;

public class DefaultInvokerFactoryTest {

	@Test
	public void test() {
		List<InvocationInterceptor> interceptors = new ArrayList<InvocationInterceptor>();
		DefaultInvokerFactory factory = new DefaultInvokerFactory();
		Invoker invoker = factory.createInvoker(interceptors);
		assertThat(invoker, is(instanceOf(DefaultInvoker.class)));
	}
}
