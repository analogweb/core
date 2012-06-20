package org.analogweb;

import java.util.Map;

/**
 * {@link RequestContext}からクエリに一致する属性の値を取得するコンポーネント。
 * @author snowgoose
 */
public interface RequestAttributes {

    /**
     * クエリに一致する値を{@link RequestContext}から取得します。
     * @param request {@link RequestContext}
     * @param resolverName クエリを発行する対象のスコープ
     * @param query 属性地を取得するクエリ
     * @return 指定されたクエリに一致する{@link RequestContext}内の属性値
     */
    Object getValueOfQuery(RequestContext request, String resolverName, String query);

    /**
     * スコープ名をキーとした、このインスタンスで取得が可能な全ての{@link AttributesHandler}
     * の{@link Map}を取得します。
     * @return 全ての{@link AttributesHandler}
     */
    Map<String, AttributesHandler> getAttributesHandlersMap();

}
