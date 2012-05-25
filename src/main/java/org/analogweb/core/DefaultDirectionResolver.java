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
        if(type.equals(Integer.TYPE) || Number.class.isAssignableFrom(type)){
            return numberToDirection((Number)invocationResult);
        }
        if (String.class.isAssignableFrom(type)) {
            return stringToDirection((String) invocationResult);
        }
        if (Direction.class.isAssignableFrom(type)) {
            return (Direction) invocationResult;
        } else {
            throw new UnresolvableResultException(invocationResult);
        }
    }
    
    protected Direction numberToDirection(Number num){
        return HttpStatus.valueOf(num.intValue());
    }

    protected Direction stringToDirection(String str){
        return Text.with(str);
    }

}
