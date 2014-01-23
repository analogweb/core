package org.analogweb.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationInterceptor;
import org.analogweb.InvocationMetadata;
import org.analogweb.util.CollectionUtils;

/**
 * @author snowgoose
 */
class InvocationChain implements Invocation {

    private Invocation root;
    private InvocationMetadata metadata;
    private Iterator<InvocationInterceptor> interceptors;
    private boolean notArchiveTail = true;
    private Object result;

    public static InvocationChain create(Invocation root, InvocationMetadata metadata,
            List<InvocationInterceptor> interceptors) {
        return new InvocationChain(root, metadata, interceptors);
    }

    @SuppressWarnings("unchecked")
    private InvocationChain(Invocation root, InvocationMetadata metadata,
            List<InvocationInterceptor> interceptors) {
        super();
        this.root = root;
        this.metadata = metadata;
        if (CollectionUtils.isEmpty(interceptors)) {
            this.interceptors = Collections.EMPTY_LIST.iterator();
        } else {
            this.interceptors = interceptors.iterator();
        }
    }

    @Override
    public Object invoke() {
        if (interceptors.hasNext()) {
            InvocationInterceptor interceptor = interceptors.next();
            result = interceptor.onInvoke(this, metadata);
        } else {
            if (notArchiveTail) {
                result = new AbstractInvocationInterceptor() {

                    @Override
                    public Object onInvoke(Invocation invocation, InvocationMetadata metadata) {
                        return root.invoke();
                    }
                }.onInvoke(this, metadata);
                notArchiveTail = false;
            }
        }
        return result;
    }

    @Override
    public Object getInvocationInstance() {
        return root.getInvocationInstance();
    }

    @Override
    public InvocationArguments getInvocationArguments() {
        return root.getInvocationArguments();
    }
}
