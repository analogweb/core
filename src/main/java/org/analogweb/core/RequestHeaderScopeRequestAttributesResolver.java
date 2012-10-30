package org.analogweb.core;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

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
    public Object resolveAttributeValue(RequestContext requestContext,
            InvocationMetadata metadatan, String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        HttpServletRequest request = requestContext.getRequest();
        @SuppressWarnings("unchecked")
        Enumeration<String> headers = request.getHeaders(name);
        if (headers == null || headers.hasMoreElements() == false) {
            return null;
        }
        List<String> list = Collections.list(headers);
        return list.toArray(new String[list.size()]);
    }

}
