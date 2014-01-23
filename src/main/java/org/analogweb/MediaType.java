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

    /**
     * 指定された{@link MediaType}が、メディアタイプ
     * において互換性があるかを検証します。
     * @param other 互換性を検証する対象の{@link MediaType}
     * @return この{@link MediaType}に対する互換性がある場合は{@code true}
     */
    boolean isCompatible(MediaType other);
}
