package org.analogweb.core;

import java.util.List;
import java.util.Map;

import org.analogweb.AttributesHandler;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.util.Maps;

public class DefaultReqestValueResolvers implements RequestValueResolvers {

    private static final Class<? extends RequestValueResolver> DEFAULT_RESOLVER_CLASS = ParameterScopeRequestAttributesResolver.class;
    private final Map<Class<? extends RequestValueResolver>, RequestValueResolver> resolverMap;

    public DefaultReqestValueResolvers(List<? extends RequestValueResolver> resolvers) {
        this.resolverMap = Maps.newConcurrentHashMap();
        for (RequestValueResolver resolver : resolvers) {
            if (resolver != null) {
                this.resolverMap.put(resolver.getClass(), resolver);
            }
        }
    }

    @Override
    public RequestValueResolver findDefaultRequestValueResolver() {
        return findRequestValueResolver(DEFAULT_RESOLVER_CLASS);
    }

    @Override
    public RequestValueResolver findRequestValueResolver(
            Class<? extends RequestValueResolver> resolverClass) {
        return this.resolverMap.get(resolverClass);
    }

    @Override
    public AttributesHandler findAttributesHandler(Class<? extends AttributesHandler> handlerClass) {
        RequestValueResolver resolver = findRequestValueResolver(handlerClass);
        if (resolver instanceof AttributesHandler) {
            return (AttributesHandler) resolver;
        }
        return null;
    }
}
