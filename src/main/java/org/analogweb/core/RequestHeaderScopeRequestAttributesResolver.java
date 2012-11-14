package org.analogweb.core;

import java.util.List;

import org.analogweb.Headers;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class RequestHeaderScopeRequestAttributesResolver extends AbstractAttributesHandler {

    private static final String NAME = "header";

    @Override
    public String getScopeName() {
        return NAME;
    }

    @Override
    public Object resolveAttributeValue(RequestContext requestContext,
            InvocationMetadata metadatan, String name, Class<?> requiredtype) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        Headers headers = requestContext.getRequestHeaders();
        List<String> headerValues = headers.getValues(name);
        if (headerValues == null || headerValues.isEmpty()) {
            return null;
        }
        return headerValues.toArray(new String[headerValues.size()]);
    }

}
