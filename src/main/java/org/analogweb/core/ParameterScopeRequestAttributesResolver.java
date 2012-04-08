package org.analogweb.core;

import javax.servlet.http.HttpServletRequest;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class ParameterScopeRequestAttributesResolver extends AbstractAttributesHandler {

    private static final String NAME = "parameter";
    private static final String MAP_KEY = ":map";

    @Override
    public String getScopeName() {
        return NAME;
    }

    @Override
    public Object resolveAttributeValue(RequestContext requestContext, InvocationMetadata metadata,
            String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        HttpServletRequest request = requestContext.getRequest();
        if (MAP_KEY.equals(name)) {
            return request.getParameterMap();
        }
        String parameterValue = request.getParameter(name);
        if (StringUtils.isEmpty(parameterValue)) {
            return request.getParameterValues(name);
        } else {
            return parameterValue;
        }
    }

}
