package org.analogweb.core;

import org.analogweb.Renderable;
import org.analogweb.ResponseResolver;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.core.response.Acceptable;
import org.analogweb.core.response.HttpStatus;
import org.analogweb.core.response.Text;

/**
 * @author snowgoose
 */
public class DefaultResponseResolver implements ResponseResolver {

    @Override
    public Renderable resolve(Object invocationResult, InvocationMetadata metadata,
            RequestContext context, ResponseContext responseContext) {
        if (invocationResult == null) {
            return nullToResponse(metadata, context);
        }
        Class<?> type = invocationResult.getClass();
        if (Acceptable.class.isAssignableFrom(type)) {
            return ((Acceptable) invocationResult).selectAcceptableOne(context);
        }
        if (Renderable.class.isAssignableFrom(type)) {
            return (Renderable) invocationResult;
        }
        if (type.equals(Integer.TYPE) || Number.class.isAssignableFrom(type)) {
            return numberToResponse((Number) invocationResult);
        }
        if (String.class.isAssignableFrom(type)) {
            return stringToResponse((String) invocationResult, metadata, context);
        }
        return anyObjectToResponse(invocationResult, metadata, context);
    }

    protected Renderable nullToResponse(InvocationMetadata metadata, RequestContext context) {
        return HttpStatus.NO_CONTENT;
    }

    protected Renderable numberToResponse(Number num) {
        return HttpStatus.valueOf(num.intValue());
    }

    protected Renderable stringToResponse(String str, InvocationMetadata metadata,
            RequestContext context) {
        return Text.with(str);
    }

    protected Renderable anyObjectToResponse(Object invocationResult, InvocationMetadata metadata,
            RequestContext context) {
        throw new UnresolvableResultException(invocationResult);
    }
}
