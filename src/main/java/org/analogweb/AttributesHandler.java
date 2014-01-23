package org.analogweb;

/**
 * 特定のスコープを持つ値の取得及び、更新を行います。<br/>
 * 「特定のスコープ」とは、例えば、リクエストパラメータ、
 * リクエストボディ、クッキー等を含みます。
 * @author snowgoose
 */
public interface AttributesHandler extends RequestValueResolver {

    /**
     * スコープに関連付けられた属性値を設定します。
     * @param requestContext {@link RequestContext}
     * @param query 属性値を設定するクエリ
     * @param value 設定する属性の値
     */
    void putAttributeValue(RequestContext requestContext, String query, Object value);

    /**
     * スコープに関連付けられた属性を削除します。
     * @param requestContext {@link RequestContext}
     * @param query 属性値を削除するクエリ
     */
    void removeAttribute(RequestContext requestContext, String query);
}
