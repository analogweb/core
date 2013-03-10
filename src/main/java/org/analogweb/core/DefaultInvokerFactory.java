package org.analogweb.core;

import java.util.List;

import org.analogweb.AttributesHandlers;
import org.analogweb.InvocationInterceptor;
import org.analogweb.InvocationProcessor;
import org.analogweb.Invoker;
import org.analogweb.InvokerFactory;
import org.analogweb.TypeMapperContext;

/**
 * @author snowgoose
 */
public class DefaultInvokerFactory implements InvokerFactory {

	@Override
	public Invoker createInvoker(List<InvocationInterceptor> interceptors,
			List<InvocationProcessor> processors,
			TypeMapperContext typeMapperContext, AttributesHandlers handlers) {
		return new DefaultInvoker(processors, interceptors, typeMapperContext,
				handlers);
	}

}
