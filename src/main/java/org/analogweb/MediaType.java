package org.analogweb;

import java.util.Map;

/**
 * メディアタイプを表します。
 * @author snowgoose
 */
public interface MediaType {

    /**
     * メディアタイプ名を取得します。
     * @return メディアタイプ名
     */
    String getType();

    /**
     * メディアサブタイプ名を取得します。
     * @return メディアサブタイプ名
     */
    String getSubType();

    /**
     * キーをパラメータ属性名とする、
     * 全てのパラメータを保持する{@link Map}取得します。
     * @return {@link Map} パラメータ
     */
    Map<String, String> getParameters();

}
