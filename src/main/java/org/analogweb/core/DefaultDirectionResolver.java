package org.analogweb.core;

import org.analogweb.Direction;
import org.analogweb.DirectionResolver;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.core.direction.HttpStatus;
import org.analogweb.core.direction.Text;
import org.analogweb.exception.UnresolvableResultException;

/**
 * @author snowgoose
 */
public class DefaultDirectionResolver implements DirectionResolver {

    @Override
    public Direction resolve(Object invocationResult, InvocationMetadata metadata,
            RequestContext context) {
        if (invocationResult == null) {
            return nullToDirection(metadata, context);
        }
        Class<?> type = invocationResult.getClass();
        if (Direction.class.isAssignableFrom(type)) {
            return (Direction) invocationResult;
        }
        if (type.equals(Integer.TYPE) || Number.class.isAssignableFrom(type)) {
            return numberToDirection((Number) invocationResult);
        }
        if (String.class.isAssignableFrom(type)) {
            return stringToDirection((String) invocationResult, metadata, context);
        }
        return anyObjectToDirection(invocationResult, metadata, context);
    }

    protected Direction nullToDirection(InvocationMetadata metadata, RequestContext context) {
        return HttpStatus.NO_CONTENT;
    }

    protected Direction numberToDirection(Number num) {
        return HttpStatus.valueOf(num.intValue());
    }

    protected Direction stringToDirection(String str, InvocationMetadata metadata,
            RequestContext context) {
        return Text.with(str);
    }

    protected Direction anyObjectToDirection(Object invocationResult, InvocationMetadata metadata,
            RequestContext context) {
        throw new UnresolvableResultException(invocationResult);
    }

}
