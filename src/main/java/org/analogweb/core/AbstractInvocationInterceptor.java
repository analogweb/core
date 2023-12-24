package org.analogweb.core;

import org.analogweb.Invocation;
import org.analogweb.InvocationInterceptor;
import org.analogweb.InvocationMetadata;
import org.analogweb.Precedence;

/**
 * @author snowgoose
 */
public class AbstractInvocationInterceptor implements InvocationInterceptor {

    @Override
    public int getPrecedence() {
        return Precedence.LOWEST;
    }

    @Override
    public Object onInvoke(Invocation invocation, InvocationMetadata metadata) {
        return invocation.invoke();
    }
}
