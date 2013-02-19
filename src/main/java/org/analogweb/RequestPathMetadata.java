package org.analogweb;

/**
 * エントリポイントメソッドを起動可能なパスを保持します。
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
     * 指定された{@link RequestPath}がこのインスタンスと同義のパスを 表す場合は{@code true}を返します。
     * @param requestPath {@link RequestPath}
     * @return 指定した{@link RequestPath}がこのインスタンスと同義のパスを表す場合は{@code true}
     */
    boolean match(RequestPath requestPath);

    /**
     * 指定された{@link RequestPath}がこのインスタンスの定義を満たすことを検証します。<br/>
     * @param requestPath {@link RequestPathMetadata}
     * @return {@link RequestPath}がこのインスタンスの定義を満たす場合は{@code true}
     */
    boolean fulfill(RequestPath requestPath);

}
