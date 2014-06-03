package org.analogweb.core;

import java.lang.annotation.Annotation;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;

public abstract class ContextSpecificAttributesHandler<T extends RequestContext> extends
        AbstractAttributesHandler {

    @Override
    @SuppressWarnings("unchecked")
    public final Object resolveValue(RequestContext requestContext, InvocationMetadata metadata,
            String key, Class<?> requiredType,Annotation[] annotations) {
        return this.resolveAttributeValueOnContext((T) requestContext, metadata, key, requiredType);
    }

    protected Object resolveAttributeValueOnContext(T requestContext, InvocationMetadata metadata,
            String key, Class<?> requiredType) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void putAttributeValue(RequestContext requestContext, String query, Object value) {
        this.putAttributeValueOnContext((T) requestContext, query, value);
    }

    protected void putAttributeValueOnContext(T requestContext, String query, Object value) {
        // nop.
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void removeAttribute(RequestContext requestContext, String query) {
        this.removeAttributeOnContext((T) requestContext, query);
    }

    protected void removeAttributeOnContext(T requestContext, String query) {
        // nop.
    }
}
