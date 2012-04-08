package org.analogweb.core;

import javax.servlet.http.HttpServletRequest;


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
    public Object resolveAttributeValue(RequestContext requestContext, InvocationMetadata metadatan, String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        HttpServletRequest request = requestContext.getRequest();
        return request.getHeader(name);
    }

}
