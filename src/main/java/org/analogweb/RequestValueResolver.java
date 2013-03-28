package org.analogweb;

public interface RequestValueResolver extends MultiModule {

    /**
     * リクエストされた値を取得します。
     * @param requestContext {@link RequestContext}
     * @param metadata {@link InvocationMetadata}
     * @param query 値を取得するクエリ
     * @param requiredType スコープから取得する対象の型
     * @return スコープに関連付けられた属性値
     */
    Object resolveValue(RequestContext requestContext,InvocationMetadata metadata, String key, Class<?> requiredType);

}
