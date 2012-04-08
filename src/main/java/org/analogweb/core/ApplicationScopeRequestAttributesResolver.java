package org.analogweb.core;

import javax.servlet.ServletContext;


import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class ApplicationScopeRequestAttributesResolver extends AbstractAttributesHandler {

    private static final Log log = Logs.getLog(ApplicationScopeRequestAttributesResolver.class);
    private static final String NAME = "application";

    @Override
    public String getScopeName() {
        return NAME;
    }

    @Override
    public Object resolveAttributeValue(RequestContext requestContext, InvocationMetadata metadatan, String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        ServletContext servletContext = requestContext.getContext();
        return servletContext.getAttribute(name);
    }

    @Override
    public void putAttributeValue(RequestContext requestContext, String name, Object value) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        ServletContext servletContext = requestContext.getContext();
        servletContext.setAttribute(name, value);
        log.log(Markers.VARIABLE_ACCESS, "TV000001", getScopeName(), name, value);
    }

    @Override
    public void removeAttribute(RequestContext requestContext, String name) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        ServletContext servletContext = requestContext.getContext();
        servletContext.removeAttribute(name);
        log.log(Markers.VARIABLE_ACCESS, "TV000002", getScopeName(), name);
    }

}
