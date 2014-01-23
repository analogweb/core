package org.analogweb;

public interface RequestValueResolvers {

    RequestValueResolver findRequestValueResolver(
            Class<? extends RequestValueResolver> resolverClass);

    AttributesHandler findAttributesHandler(Class<? extends AttributesHandler> handlerClass);
}
