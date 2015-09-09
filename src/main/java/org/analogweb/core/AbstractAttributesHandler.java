package org.analogweb.core;

import java.lang.annotation.Annotation;
import org.analogweb.AttributesHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;

/**
 * @author snowgoose
 */
public abstract class AbstractAttributesHandler implements AttributesHandler {

    @Override
    public Object resolveValue(RequestContext requestContext, InvocationMetadata metadata,
            String key, Class<?> requiredType, Annotation[] annotations) {
        // nop
        return null;
    }

    @Override
    public void putAttributeValue(RequestContext requestContext, String query, Object value) {
        // nop
    }

    @Override
    public void removeAttribute(RequestContext requestContext, String query) {
        // nop
    }
}
