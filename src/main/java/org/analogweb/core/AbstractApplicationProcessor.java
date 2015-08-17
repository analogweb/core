package org.analogweb.core;

import org.analogweb.ApplicationProcessor;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.MutableRequestContext;
import org.analogweb.Precedence;
import org.analogweb.PreparedInvocationArguments;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestValueResolvers;
import org.analogweb.ResponseContext;
import org.analogweb.TypeMapperContext;

/**
 * @author snowgoose
 */
public abstract class AbstractApplicationProcessor implements ApplicationProcessor {

    @Override
    public Object preMatching(MutableRequestContext request, RequestPath path) {
        return NO_INTERRUPTION;
    }

    @Override
    public Object prepareInvoke(InvocationArguments args,
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
    public void afterCompletion(RequestContext request, ResponseContext response, Exception e) {
        // nop.
    }

    @Override
    public int getPrecedence() {
        return Precedence.LOWEST;
    }
}
