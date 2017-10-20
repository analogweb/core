package org.analogweb;

/**
 * Entry-point {@link Invocation}.
 * 
 * @author snowgoose
 */
public interface Invoker extends Module {

	Object invoke(Invocation invocation, InvocationMetadata metadata,
			RequestContext request, ResponseContext response);
}
