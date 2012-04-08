package org.analogweb;

import java.util.Collection;
import java.util.Map;

/**
 * マルチパートリクエストに含まれる全てのパラメータ（文字列、ファイル等のエンティティ)の集合を示します。<br/>
 * 通常、このインターフェースを値として取得する場合は、{@link #iterator()}({@link Iterable})を
 * 使用して、逐次（不可逆な）読み出しを行う事を想定しています。<br/>
 * キー等を指定してパラメータの値を取得しようとする場合、{@link #iterator()}を使用等して（あるいは既に）
 * 内部的に展開された値を参照するため、逐次読み出しを行う事によるメリットが失われる事に注意してください。
 * @author snowgoose
 */
public interface MultipartParameters extends Iterable<MultipartParameters.MultipartParameter>{
    
    /**
     * 指定したパラメータのキーに一致する文字列パラメータを取得します。
     * @param name パラメータのキー
     * @return キーに一致する文字列パラメータの値
     */
    String[] getParameter(String name);

    /**
     * リクエストに含まれるすべての文字列パラメータのキーを取得します。
     * @return すべての文字列パラメータのキー
     */
    Collection<String> getParameterNames();

    /**
     * リクエストに含まれるすべての文字列パラメータの{@link Map}を取得します。
     * @return すべての文字列パラメータの{@link Map}
     */
    Map<String,String[]> getParameterMap();

    /**
     * 指定したキーに一致するファイルパラメータを{@link MultipartFile}として取得します。
     * @param name パラメータのキー
     * @return キーに一致するファイルパラメータを表す{@link MultipartFile}
     */
    MultipartFile[] getFile(String name);

    /**
     * リクエストに含まれるすべてのファイルパラメータのキーを取得します。
     * @return すべてのファイルパラメータのキー
     */
    Collection<String> getFileParameterNames();

    /**
     * リクエストに含まれるすべてのファイルパラメータの{@link Map}を取得します。
     * @return すべてのファイルパラメータの{@link Map}
     */
    Map<String, MultipartFile[]> getFileMap();

    /**
     * マルチパートリクエストに含まれる単一のパラメータの値を表します。
     * @author snowgoose
     */
    interface MultipartParameter {
        
        /**
         * {@link #value}により返される値が{@link MultipartFile}
         * である場合は、{@code true}を返します。
         * @return パラメータが{@link MultipartFile}である場合は{@code true}
         */
        boolean isMultipartFile();

        /**
         * パラメータのキーを取得します。
         * @return パラメータのキー
         */
        String getParameterName();

        /**
         * パラメータの値を取得します。<br/>
         * パラメータが文字列である場合は{@link String}型で、ファイルである場合は
         * {@link MultipartFile}として取得することができます。<br/>
         * パラメータが{@link MultipartFile}であるかは、{@link #isMultipartFile()}
         * により判別することができます。
         * @see #isMultipartFile()
         * @return {@link String}または{@link MultipartFile}として取得したパラメータの値
         */
        <T> T value();
    }

}
