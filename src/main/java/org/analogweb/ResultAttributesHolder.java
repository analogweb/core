package org.analogweb;

import java.util.Map;

/**
 * 実行結果として属性を保持していることを表します。
 * @author snowgoose
 */
public interface ResultAttributesHolder {

    /**
     * 属性の一覧を保持する{@link Map}を取得します。
     * @return 属性の一覧を保持する{@link Map}
     */
    Map<ScopedAttributeName, Object> getAttributes();

}
