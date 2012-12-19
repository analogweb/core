package org.analogweb.core;

import javax.servlet.ServletContext;

import org.analogweb.ApplicationContextResolver;

/**
 * {@link ServletContext}よりアプリケーションスコープのコンポーネントを
 * 解決する{@link ApplicationContextResolver}の実装です。
 * @author snowgoose
 */
public class ServletContextApplicationContextResolver implements ApplicationContextResolver {

    private final ServletContext context;

    public ServletContextApplicationContextResolver(ServletContext context) {
        this.context = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> requiredType, String contextKey) {
        Object value = getContext().getAttribute(contextKey);
        if (requiredType.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    protected final ServletContext getContext() {
        return this.context;
    }
}
