package org.analogweb.core;

import java.util.List;

import org.analogweb.Invocation;
import org.analogweb.InvocationInterceptor;
import org.analogweb.InvocationMetadata;
import org.analogweb.Invoker;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;

/**
 * @author snowgoose
 */
public class DefaultInvoker implements Invoker {

	private final List<InvocationInterceptor> interceptors;

	public DefaultInvoker(List<InvocationInterceptor> interceptors) {
		super();
		this.interceptors = interceptors;
	}

	@Override
	public Object invoke(Invocation invocation, InvocationMetadata metadata,
			RequestContext request, ResponseContext response) {
		return InvocationChain.create(invocation, metadata,
				getInvocationInterceptors()).invoke();
	}

	protected List<InvocationInterceptor> getInvocationInterceptors() {
		return this.interceptors;
	}
}
