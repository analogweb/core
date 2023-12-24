package org.analogweb;

/**
 * Cookie based on <a href="http://www.ietf.org/rfc/rfc2109.txt">RFC 2109</a>.
 *
 * @author snowgoose
 */
public interface Cookies {

    /**
     * Get {@link Cookie}.
     *
     * @param name
     *            Key of {@link Cookie}
     *
     * @return {@link Cookie}
     */
    Cookie getCookie(String name);

    /**
     * Put {@link Cookie}.
     *
     * @param name
     *            Key of {@link Cookie}.
     * @param value
     *            Value of {@link Cookie}.
     */
    void putCookie(String name, Object value);

    /**
     * Put {@link Cookie}.
     *
     * @param cookie
     *            {@link Cookie}
     */
    void putCookie(Cookie cookie);

    /**
     * A Cookie.
     *
     * @author snowgoose
     */
    interface Cookie {

        String getName();

        String getValue();

        String getComment();

        String getPath();

        int getMaxAge();

        boolean isSecure();

        int getVersion();

        String getDomain();
    }
}
