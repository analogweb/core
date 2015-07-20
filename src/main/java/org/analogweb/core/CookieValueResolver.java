package org.analogweb.core;

import java.lang.annotation.Annotation;

import org.analogweb.Cookies;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;

/**
 * @author snowgoose
 */
public class CookieValueResolver implements RequestValueResolver {

    @Override
    public Object resolveValue(RequestContext requestContext, InvocationMetadata metadatan,
            String key, Class<?> requiredType, Annotation[] annotations) {
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
}
