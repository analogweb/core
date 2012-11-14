package org.analogweb.core;

import org.analogweb.Cookies;
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
    public Object resolveAttributeValue(RequestContext requestContext,
            InvocationMetadata metadatan, String key, Class<?> requiredType) {
        Cookies cookies = requestContext.getCookies();
        if (cookies != null) {
            Cookies.Cookie cookie = cookies.getCookie(key);
            if (requiredType != null && requiredType.equals(Cookies.Cookie.class)) {
                return cookie;
            } else if (cookie != null) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public void putAttributeValue(RequestContext requestContext, String name, Object value) {
        Cookies cookies = requestContext.getCookies();
        cookies.putCookie(name,value.toString());
        log.log(Markers.VARIABLE_ACCESS, "TV000001", getScopeName(), name, value);
    }

    @Override
    public void removeAttribute(RequestContext requestContext, String name) {
        // nop.
        log.log(Markers.VARIABLE_ACCESS, "TV000003", getScopeName(), name);
    }

}
