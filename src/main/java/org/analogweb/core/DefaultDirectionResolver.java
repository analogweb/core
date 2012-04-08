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
        Class<?> type = invocationResult.getClass();
        if(type.equals(Integer.TYPE) || Integer.class.isAssignableFrom(type)){
            return HttpStatus.valueOf((Integer)invocationResult);
        }
        if (String.class.isAssignableFrom(type)) {
            return Text.with((String) invocationResult);
        }
        if (Direction.class.isAssignableFrom(type)) {
            return (Direction) invocationResult;
        } else {
            throw new UnresolvableResultException(invocationResult);
        }
    }

}
