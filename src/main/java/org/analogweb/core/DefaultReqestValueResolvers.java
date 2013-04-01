package org.analogweb.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.analogweb.AttributesHandler;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.util.Maps;

public class DefaultReqestValueResolvers implements RequestValueResolvers {

    private static final Class<? extends RequestValueResolver> DEFAULT_RESOLVER_CLASS = ParameterScopeRequestAttributesResolver.class;
    private final Map<Key, RequestValueResolver> resolverMap;

    public DefaultReqestValueResolvers(List<? extends RequestValueResolver> resolvers) {
        this.resolverMap = Maps.newConcurrentHashMap();
        for (RequestValueResolver resolver : resolvers) {
            if (resolver != null) {
                this.resolverMap.put(Key.valueOf(resolver.getClass()), resolver);
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
        return this.resolverMap.get(Key.valueOf(resolverClass));
    }

    @Override
    public AttributesHandler findAttributesHandler(Class<? extends AttributesHandler> handlerClass) {
        RequestValueResolver resolver = findRequestValueResolver(handlerClass);
        if (resolver instanceof AttributesHandler) {
            return (AttributesHandler) resolver;
        }
        return null;
    }

    static class Key implements Serializable {

        private static final long serialVersionUID = 1L;
        private int hashCode;
        private final Collection<String> names;

        private Key(Class<? extends RequestValueResolver> resolverClass) {
            this.names = new ArrayList<String>();
            calculateEquality(resolverClass);
        }

        private void calculateEquality(Class<? extends RequestValueResolver> resolverClass) {
            Class<?> target = resolverClass;
            this.hashCode = target.getCanonicalName().hashCode();
            this.names.add(target.getCanonicalName());
            while (RequestValueResolver.class.isAssignableFrom(target.getSuperclass())) {
                target = target.getSuperclass();
                this.hashCode = target.getCanonicalName().hashCode();
                this.names.add(target.getCanonicalName());
            }
        }

        static Key valueOf(Class<? extends RequestValueResolver> resolverClass) {
            return new Key(resolverClass);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Key) {
                Key otherKey = (Key) other;
                for (String otherName : otherKey.names) {
                    if (this.names.contains(otherName)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
