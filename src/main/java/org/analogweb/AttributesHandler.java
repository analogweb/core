package org.analogweb;

/**
 * 特定のスコープを持つ値の取得及び、更新を行います。<br/>
 * 「特定のスコープ」とは、例えば、リクエストパラメータ、
 * リクエストボディ、クッキー等を含みます。
 * @author snowgoose
 */
public interface AttributesHandler extends MultiModule {

    /**
     * このコンポーネントが値を操作可能なスコープ名を取得します。
     * @return スコープ名
     */
    String getScopeName();

    /**
     * スコープに関連付けられた属性値を取得します。
     * @param requestContext {@link RequestContext}
     * @param metadata {@link InvocationMetadata}
     * @param key 属性値を取得するキー
     * @param requiredType スコープから取得する対象の型
     * @return スコープに関連付けられた属性値
     */
    Object resolveAttributeValue(RequestContext requestContext,InvocationMetadata metadata, String key, Class<?> requiredType);

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
