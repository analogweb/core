package org.analogweb;

import java.util.List;
import java.util.Map;

/**
 * リクエストパラメータを管理します。
 * @author snowgoose
 */
public interface Parameters {
    
    /**
     * キーに一致するリクエストパラメータを取得します。
     * @param key リクエストパラメータのキー
     * @return キーに一致するリクエストパラメータ
     */
    List<String> getValues(String key);

    /**
     * リクエストパラメータのキーと値のペアを{@link Map}として取得します。
     * @return {@link Map}
     */
    Map<String, String[]> asMap();

}
