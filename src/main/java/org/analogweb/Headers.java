package org.analogweb;

import java.util.List;

/**
 * リクエスト及びレスポンスヘッダを管理します。
 * @author snowgoose
 */
public interface Headers {

    /**
     * 指定されたキーに対応するヘッダの値を取得します。
     * @param name ヘッダの値を取得する為のキー
     * @return キーに対応するヘッダの値
     */
    List<String> getValues(String name);

    /**
     * 全てのヘッダのキー名を取得します。
     * @return 全てのヘッダのキー名
     */
    List<String> getNames();

    /**
     * 指定したキーに対するヘッダの値を追加又は更新します。
     * @param name ヘッダの値に対するキー
     * @param value 追加又は更新するヘッダの値
     */
    void putValue(String name, String value);

    /**
     * 指定したキーがヘッダに存在する場合は{@code true}を返します。
     * @param name ヘッダの値に対するキー
     * @return キーがヘッダに存在する場合は{@code true}
     */
    boolean contains(String name);
}
