package org.analogweb;

import java.io.InputStream;

/**
 * マルチパートリクエストによりアップロードされたファイルを示します。
 * @author snowgoose
 */
public interface MultipartFile {
    
    /**
     * このファイルをアップロードした時のパラメータ名を取得する。
     * @return パラメータ名
     */
    String getParameterName();
    /**
     * このファイルをアップロードした時のファイル名を取得する。
     * @return ファイル名
     */
    String getFileName();
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
