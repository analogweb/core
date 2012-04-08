package org.analogweb;

/**
 * 特定のスコープを持つ{@link AttributesHandler}を通じて、属性値を設定するコンポーネント。
 * @author snowgoose
 */
public interface ResultAttributes extends Module {

    /**
     * 特定のスコープに属性値を設定します。
     * @param request {@link RequestContext}
     * @param placerName 対象となる{@link AttributesHandler}のスコープ名
     * @param attributeName 値を設定する属性のキー名
     * @param value 属性に設定する値
     */
    void setValueOfQuery(RequestContext request, String placerName, String attributeName,
            Object value);

    /**
     * 特定のスコープの属性を削除します。
     * @param request {@link RequestContext}
     * @param placerName 対象となる{@link AttributesHandler}のスコープ名
     * @param attributeName 値を設定する属性のキー名
     */
    void removeValueOfQuery(RequestContext request, String placerName, String attributeName);

}
