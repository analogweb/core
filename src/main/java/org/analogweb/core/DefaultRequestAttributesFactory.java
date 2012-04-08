package org.analogweb.core;

import java.util.Map;

import org.analogweb.AttributesHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestAttributesFactory;


/**
 * @author snowgoose
 */
public class DefaultRequestAttributesFactory implements RequestAttributesFactory {

    @Override
    public RequestAttributes createRequestAttributes(
            Map<String, AttributesHandler> resolvers,InvocationMetadata metadata) {
        return new DefaultRequestAttributes(resolvers,metadata);
    }

}
