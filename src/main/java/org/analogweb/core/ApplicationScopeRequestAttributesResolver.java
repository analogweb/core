package org.analogweb.core;

import javax.servlet.ServletContext;

import org.analogweb.InvocationMetadata;
import org.analogweb.ServletRequestContext;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class ApplicationScopeRequestAttributesResolver extends ContextSpecifiedAttributesHandler<ServletRequestContext> {

    private static final Log log = Logs.getLog(ApplicationScopeRequestAttributesResolver.class);
    private static final String NAME = "application";

    @Override
    public String getScopeName() {
        return NAME;
    }

    @Override
    protected Object resolveAttributeValueOnContext(ServletRequestContext requestContext, InvocationMetadata metadata,
            String key, Class<?> requiredType) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        ServletContext servletContext = requestContext.getServletContext();
        return servletContext.getAttribute(key);
    }

    @Override
    protected void putAttributeValueOnContext(ServletRequestContext requestContext, String name, Object value) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        ServletContext servletContext = requestContext.getServletContext();
        servletContext.setAttribute(name, value);
        log.log(Markers.VARIABLE_ACCESS, "TV000001", getScopeName(), name, value);
    }

    @Override
    protected void removeAttributeOnContext(ServletRequestContext requestContext, String name) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        ServletContext servletContext = requestContext.getServletContext();
        servletContext.removeAttribute(name);
        log.log(Markers.VARIABLE_ACCESS, "TV000002", getScopeName(), name);
    }

}
