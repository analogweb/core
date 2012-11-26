package org.analogweb.core;

import java.util.List;

import org.analogweb.AttributesHandlers;
import org.analogweb.ContainerAdaptor;
import org.analogweb.Invocation;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.exception.UnresolvableInvocationException;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * 既定の{@link InvocationFactory}の実装です。
 * @author snowgoose
 */
public class DefaultInvocationFactory implements InvocationFactory {

    private static final Log log = Logs.getLog(DefaultInvocationFactory.class);

    public Invocation createInvocation(ContainerAdaptor instanceProvider,
            InvocationMetadata metadata, RequestContext context, TypeMapperContext converters,
            List<InvocationProcessor> processors, AttributesHandlers handlers) {
        Object invocationInstance = resolveInvocationInstance(instanceProvider, metadata, context);
        log.log(Markers.LIFECYCLE, "DL000001", invocationInstance, instanceProvider);
        return new DefaultInvocation(invocationInstance, metadata, context, converters, processors,
                handlers);
    }

    protected Object resolveInvocationInstance(ContainerAdaptor instanceProvider,
            InvocationMetadata metadata, RequestContext context)
            throws UnresolvableInvocationException {
        Object invocationInstance = instanceProvider.getInstanceOfType(metadata
                .getInvocationClass());
        if (invocationInstance == null) {
            throw new UnresolvableInvocationException(metadata);
        }
        return invocationInstance;
    }

}
