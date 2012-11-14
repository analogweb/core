package org.analogweb;

import java.util.List;

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

}
