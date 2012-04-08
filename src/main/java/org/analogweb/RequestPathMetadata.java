package org.analogweb;

import java.util.List;

/**
 * リクエストメソッドを起動可能なパスを保持します。
 * @author snowgoose
 */
public interface RequestPathMetadata {

    /**
     * リクエストパスを表す文字列を取得します。<br/>
     * アプリケーション内のコンテキストからのパスを表します。
     * @return リクエストパスを表す文字列
     */
    String getActualPath();

    /**
     * 指定された{@link RequestPathMetadata}がこのインスタンスと同義のパスを 表す場合は{@code true}を返します。
     * @param requestPath {@link RequestPathMetadata}
     * @return 指定した{@link RequestPathMetadata}がこのインスタンスと同義のパスを表す場合は{@code true}
     */
    boolean match(RequestPathMetadata requestPath);

    List<String> getRequestMethods();

}
