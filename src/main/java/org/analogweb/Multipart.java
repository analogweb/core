package org.analogweb;

import java.io.InputStream;

/**
 * マルチパートリクエストによりアップロードされたファイルを示します。
 * @author snowgoose
 */
public interface Multipart {
    
    /**
     * このファイルをアップロードした時のパラメータ名を取得する。
     * @return パラメータ名
     */
    String getName();

    /**
     * このファイルをアップロードした時のリソース名を取得する。
     * @return リソース名
     */
    String getResourceName();

    /**
     * アップロードされたファイルを{@link InputStream}で取得する。
     * @return アップロードされたファイルの内容
     */
    InputStream getInputStream();

    /**
     * アップロードされたファイルをバイト列で取得する。
     * @return アップロードされたファイルの内容
     */
    byte[] getBytes();

    /**
     * アップロードされたファイルのコンテンツタイプ取得する。
     * @return アップロードされたファイルのコンテンツタイプ
     */
    String getContentType();

}
