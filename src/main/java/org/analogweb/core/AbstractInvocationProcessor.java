package org.analogweb.core;


import java.lang.reflect.Method;

import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.TypeMapperContext;
import org.analogweb.exception.InvocationFailureException;
import org.analogweb.util.ArrayUtils;


/**
 * @author snowgoose
 */
public abstract class AbstractInvocationProcessor implements InvocationProcessor {

    @Override
    public Invocation prepareInvoke(Method method, Invocation invocation,
            InvocationMetadata metadata, RequestContext context, RequestAttributes attributes,
            TypeMapperContext converters) {
        return invocation;
    }

    @Override
    public Object onInvoke(Method method, InvocationMetadata metadata, InvocationArguments args) {
        // nop.
        return null;
    }

    @Override
    public Object processException(Exception ex, RequestContext request, Invocation invocation,
            InvocationMetadata metadata) {
        if (ex instanceof InvocationFailureException) {
            throw (InvocationFailureException) ex;
        }
        throw new InvocationFailureException(ex, metadata, ArrayUtils.newArray());
    }

    @Override
    public Object postInvoke(Object invocationResult, Invocation invocation,
            InvocationMetadata metadata, RequestContext context, RequestAttributes attributes,
            ResultAttributes resultAttributes) {
        return invocationResult;
    }

    @Override
    public void afterCompletion(RequestContext request, Invocation invocation,
            InvocationMetadata metadata, Object invocationResult) {
    }

}
