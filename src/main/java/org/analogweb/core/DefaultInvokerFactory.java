package org.analogweb.core;

import java.util.List;

import org.analogweb.InvocationInterceptor;
import org.analogweb.Invoker;
import org.analogweb.InvokerFactory;

/**
 * @author snowgoose
 */
public class DefaultInvokerFactory implements InvokerFactory {

	@Override
	public Invoker createInvoker(List<InvocationInterceptor> interceptors) {
		return new DefaultInvoker(interceptors);
	}
}
