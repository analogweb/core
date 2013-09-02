package org.analogweb.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.analogweb.ApplicationContextResolver;
import org.analogweb.util.Maps;

/**
 * Default implementation {@link ApplicationContextResolver}
 * @author snowgooseyk
 */
public class DefaultApplicationContextResolver implements ApplicationContextResolver {

    private final Map<String, ?> context;

    public static DefaultApplicationContextResolver context(String key, Object value) {
        return new DefaultApplicationContextResolver(Maps.newHashMap(key, value));
    }

    public static DefaultApplicationContextResolver context(Map<String, ?> context) {
        return new DefaultApplicationContextResolver(context);
    }

    public DefaultApplicationContextResolver(Map<String, ?> context) {
        this.context = new ConcurrentHashMap<String, Object>(context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> requiredType, String contextKey) {
        return (T) context.get(contextKey);
    }
}
