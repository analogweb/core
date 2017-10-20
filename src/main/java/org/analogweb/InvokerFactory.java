package org.analogweb;

import java.util.List;

/**
 * Factory of {@link Invoker}
 * 
 * @author snowgoose
 */
public interface InvokerFactory extends Module {

	Invoker createInvoker(List<InvocationInterceptor> interceptors);
}
