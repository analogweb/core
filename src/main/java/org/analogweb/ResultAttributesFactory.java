package org.analogweb;

import java.util.Map;

/**
 * {@link ResultAttributes}を生成するファクトリです。
 * @author snowgoose
 */
public interface ResultAttributesFactory extends Module {

    /**
     * 新しい{@link ResultAttributes}のインスタンスを生成します。
     * @param placers スコープ名をキーとした{@link AttributesHandler}の{@link Map}
     * @return {@link ResultAttributes}
     */
    ResultAttributes createResultAttributes(Map<String, AttributesHandler> placers);

}
