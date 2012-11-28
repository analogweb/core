package org.analogweb.core;

import java.lang.reflect.Method;

import org.analogweb.AttributesHandlers;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.PreparedInvocationArguments;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.exception.InvocationFailureException;

/**
 * @author snowgoose
 */
public abstract class AbstractInvocationProcessor implements InvocationProcessor {

    @Override
    public Object prepareInvoke(Method method, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context, TypeMapperContext converters,
            AttributesHandlers handlers) {
        return NO_INTERRUPTION;
    }

    @Override
    public Object onInvoke(Method method, InvocationMetadata metadata, InvocationArguments args) {
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
    public Object postInvoke(Object invocationResult, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context, AttributesHandlers handlers) {
        return invocationResult;
    }

    @Override
    public void afterCompletion(RequestContext request, InvocationArguments args,
            InvocationMetadata metadata, Object invocationResult) {
    }

}
