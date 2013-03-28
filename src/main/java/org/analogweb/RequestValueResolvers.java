package org.analogweb;

public interface RequestValueResolvers {

	RequestValueResolver findRequestValueResolver(Class<? extends RequestValueResolver> resolverClass);
	RequestValueResolver findDefaultRequestValueResolver();
	AttributesHandler findAttributesHandler(Class<? extends AttributesHandler> handlerClass);

}
