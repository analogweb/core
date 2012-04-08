package org.analogweb.core;

import javax.servlet.http.HttpServletRequest;


import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.util.Assertion;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class RequestScopeRequestAttributesResolver extends AbstractAttributesHandler {

    private static final Log log = Logs.getLog(RequestScopeRequestAttributesResolver.class);

    @Override
    public String getScopeName() {
        return "request";
    }

    @Override
    public Object resolveAttributeValue(RequestContext requestContext, InvocationMetadata metadatan, String name) {
        HttpServletRequest request = requestContext.getRequest();
        return request.getAttribute(name);
    }

    @Override
    public void putAttributeValue(RequestContext requestContext, String name, Object value) {
        Assertion.notNull(requestContext, RequestContext.class.getName());
        HttpServletRequest request = requestContext.getRequest();
        request.setAttribute(name, value);
        log.log(Markers.VARIABLE_ACCESS, "TV000001", getScopeName(), name, value);
    }

    @Override
    public void removeAttribute(RequestContext requestContext, String name) {
        HttpServletRequest request = requestContext.getRequest();
        request.removeAttribute(name);
        log.log(Markers.VARIABLE_ACCESS, "TV000002", getScopeName(), name);
    }

}
