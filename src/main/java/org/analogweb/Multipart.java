package org.analogweb;

import java.io.InputStream;

/**
 * マルチパートリクエストによりアップロードされたリソースを示します。
 * @author snowgoose
 */
public interface Multipart {

    /**
     * このリソースをアップロードした時のパラメータ名を取得する。
     * @return パラメータ名
     */
    String getName();

    /**
     * このリソースをアップロードした時のリソース名を取得する。
     * @return リソース名
     */
    String getResourceName();

    /**
     * アップロードされたリソースを{@link InputStream}で取得する。
     * @return アップロードされたリソースの内容
     */
    InputStream getInputStream();

    /**
     * アップロードされたリソースをバイト列で取得する。
     * @return アップロードされたリソースの内容
     */
    byte[] getBytes();

    /**
     * アップロードされたリソースのコンテンツタイプ取得する。
     * @return アップロードされたリソースのコンテンツタイプ
     */
    String getContentType();
}
