package org.analogweb;

import java.lang.annotation.Annotation;

/**
 * Resolve variables from application states or passed by request or another.
 * @see org.analogweb.Application#processRequest(RequestPath, RequestContext, ResponseContext)
 * @see org.analogweb.annotation.Resolver
 * @author snowgooseyk
 */
public interface RequestValueResolver extends MultiModule {

    /**
     * Retrieve value from resolvable scope.
     * @param request {@link RequestContext}
     * @param metadata {@link InvocationMetadata}
     * @param name query for retrieval
     * @param requiredType required type
     * @param parameterAnnotations parameter field {@link Annotation}s.
     * @return resolved value from specified scope.
     */
    Object resolveValue(RequestContext request, InvocationMetadata metadata, String name,
            Class<?> requiredType, Annotation[] parameterAnnotations);
}
