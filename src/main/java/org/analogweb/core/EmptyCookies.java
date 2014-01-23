package org.analogweb.core;

import org.analogweb.Cookies;

/**
 * @author snowgoose
 */
public class EmptyCookies implements Cookies {

    @Override
    public Cookie getCookie(String name) {
        // nop.
        return null;
    }

    @Override
    public void putCookie(Cookie cookie) {
        // nop.
    }

    @Override
    public void putCookie(String name, Object value) {
        // nop.
    }
}
