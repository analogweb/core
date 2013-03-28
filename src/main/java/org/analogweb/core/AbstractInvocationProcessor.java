package org.analogweb.core;

import java.lang.reflect.Method;

import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.PreparedInvocationArguments;
import org.analogweb.Precedence;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapperContext;

/**
 * @author snowgoose
 */
public abstract class AbstractInvocationProcessor implements InvocationProcessor {

    @Override
    public Object prepareInvoke(Method method, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context, TypeMapperContext converters,
            RequestValueResolvers resolvers) {
        return NO_INTERRUPTION;
    }

    @Override
    public Object processException(Exception ex, RequestContext request,
            PreparedInvocationArguments args, InvocationMetadata metadata) {
        if (ex instanceof InvocationFailureException) {
            throw (InvocationFailureException) ex;
        }
        return NO_INTERRUPTION;
    }

    @Override
    public void postInvoke(Object invocationResult, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context, RequestValueResolvers resolvers) {
    	// nop.
    }

    @Override
    public void afterCompletion(RequestContext request, InvocationArguments args,
            InvocationMetadata metadata, Object invocationResult) {
    	// nop.
    }

    @Override
    public int getPrecedence() {
        return Precedence.LOWEST;
    }

}
