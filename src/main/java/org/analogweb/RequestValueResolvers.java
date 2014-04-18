package org.analogweb;

/**
 * Holder of RequestValueResolver.
 * @see RequestValueResolver
 * @author snowgooseyk
 */
public interface RequestValueResolvers {

    RequestValueResolver findDefaultRequestValueResolver();

    RequestValueResolver findRequestValueResolver(
            Class<? extends RequestValueResolver> resolverClass);

    AttributesHandler findAttributesHandler(Class<? extends AttributesHandler> handlerClass);
}
