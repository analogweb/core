package org.analogweb.core;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class CookieScopeRequestAttributeResolver extends AbstractAttributesHandler {

    private static final Log log = Logs.getLog(CookieScopeRequestAttributeResolver.class);
    private static final String NAME = "cookie";

    @Override
    public String getScopeName() {
        return NAME;
    }

    @Override
    public Object resolveAttributeValue(RequestContext requestContext, InvocationMetadata metadatan, String name) {
        HttpServletRequest request = requestContext.getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public void putAttributeValue(RequestContext requestContext, String name, Object value) {
        HttpServletResponse response = requestContext.getResponse();
        Cookie cookie = new Cookie(name, value.toString());
        response.addCookie(cookie);
        log.log(Markers.VARIABLE_ACCESS, "TV000001", getScopeName(), name, value);
    }

    @Override
    public void removeAttribute(RequestContext requestContext, String name) {
        // nop.
        log.log(Markers.VARIABLE_ACCESS, "TV000003", getScopeName(), name);
    }

}
