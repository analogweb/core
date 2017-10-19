package org.analogweb;

import java.util.Collection;

/**
 * Holder of RequestValueResolver.
 * 
 * @see RequestValueResolver
 * @author snowgooseyk
 */
public interface RequestValueResolvers {

	RequestValueResolver findDefaultRequestValueResolver();

	RequestValueResolver findRequestValueResolver(
			Class<? extends RequestValueResolver> resolverClass);

	AttributesHandler findAttributesHandler(
			Class<? extends AttributesHandler> handlerClass);

	Collection<RequestValueResolver> all();
}
