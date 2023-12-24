package org.analogweb.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.analogweb.ApplicationContext;
import org.analogweb.util.Maps;

/**
 * Default implementation of {@link ApplicationContext}.
 *
 * @author snowgooseyk
 */
public class DefaultApplicationContext implements ApplicationContext {

    private final Map<String, ?> context;

    public static DefaultApplicationContext context(String key, Object value) {
        return new DefaultApplicationContext(Maps.newHashMap(key, value));
    }

    public static DefaultApplicationContext context(Map<String, ?> context) {
        return new DefaultApplicationContext(context);
    }

    public DefaultApplicationContext(Map<String, ?> context) {
        this.context = new ConcurrentHashMap<String, Object>(context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(Class<T> requiredType, String contextKey) {
        return (T) context.get(contextKey);
    }
}
