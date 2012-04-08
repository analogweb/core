package org.analogweb;

/**
 * スコープに関連付けられた属性名を表します。
 * @author snowgoose
 */
public interface ScopedAttributeName {

    /**
     * スコープ名を取得します。
     * @return スコープ名
     */
    String getScope();

    /**
     * 関連付けられた属性名を取得します。
     * @return 属性名
     */
    String getName();

}
