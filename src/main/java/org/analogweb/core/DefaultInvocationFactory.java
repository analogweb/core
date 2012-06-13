package org.analogweb.core;

import java.util.List;


import org.analogweb.ContainerAdaptor;
import org.analogweb.Invocation;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.TypeMapperContext;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;


/**
 * @author snowgoose
 */
public class DefaultInvocationFactory implements InvocationFactory {

    private static final Log log = Logs.getLog(DefaultInvocationFactory.class);

    public Invocation createInvocation(ContainerAdaptor instanceProvider, InvocationMetadata metadata,
            RequestAttributes attributes, ResultAttributes resultAttributes,
            RequestContext context, TypeMapperContext converters,
            List<InvocationProcessor> processors) {
        Object invocationInstance = instanceProvider
                .getInstanceOfType(metadata.getInvocationClass());
        log.log(Markers.LIFECYCLE, "DL000001", invocationInstance, instanceProvider);
        return new DefaultInvocation(invocationInstance, metadata, attributes, resultAttributes,
                context, converters, processors);
    }

}
