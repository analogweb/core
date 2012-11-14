package org.analogweb.core;

import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.Invoker;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.util.Assertion;

/**
 * @author snowgoose
 */
public class DefaultInvoker implements Invoker {

    @Override
    public Object invoke(Invocation invocation, InvocationMetadata metadata,
            ResultAttributes resultAttributes, RequestContext context) {

        Assertion.notNull(metadata, InvocationMetadata.class.getSimpleName());
        Assertion.notNull(invocation, Invocation.class.getSimpleName());

        return invocation.invoke();
    }

}
