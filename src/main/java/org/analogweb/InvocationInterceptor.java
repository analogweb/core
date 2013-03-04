package org.analogweb;

public interface InvocationInterceptor extends MultiModule,Precedence {

    Object onInvoke(Invocation invocation, InvocationMetadata metadata);

}
