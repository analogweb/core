package org.analogweb;

/**
 * <a href="http://www.ietf.org/rfc/rfc2109.txt">RFC 2109</a>に基づく、
 * クッキーを管理します。
 * @author snowgoose
 */
public interface Cookies {

    /**
     * 指定したキーに一致する{@link Cookie}を返します。
     * @param name {@link Cookie}を特定するキー
     * @return {@link Cookie}
     */
    Cookie getCookie(String name);

    /**
     * 指定したキーに一致する{@link Cookie}を追加、又は更新します。
     * @param name {@link Cookie}を特定するキー
     * @param value 更新する{@link Cookie}の値
     */
    void putCookie(String name, Object value);

    /**
     * 指定した{@link Cookie}を追加、又は更新します。
     * @param cookie {@link Cookie}
     */
    void putCookie(Cookie cookie);

    /**
     * 単一のキーを保持するクッキーです。
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
