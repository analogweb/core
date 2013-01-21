package org.analogweb;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author snowgoose
 */
public interface ResponseContext {

    /**
     * このコンテキストにレスポンスした内容を
     * HTTPレスポンスとしてコミットします。
     * @param context {@link RequestContext}
     */
    void commmit(RequestContext context);

    /**
     * HTTPレスポンスヘッダを保持する{@link Headers}を取得します。
     * @return {@link Headers}
     */
    Headers getResponseHeaders();

    /**
     * {@link ResponseWriter}を取得します。
     * @return {@link ResponseWriter}
     */
    ResponseWriter getResponseWriter();

    /**
     * レスポンスするHTTPステータスを指定します。
     * @param status レスポンスするHTTPステータス
     */
    void setStatus(int status);

    /**
     * レスポンスするHTTPレスポンスのコンテンツの長さを
     * 指定します。リクエストメソッド、レスポンスボディの
     * 内容により設定した値は無視される可能性があります。
     * @param length HTTPレスポンスのコンテンツの長さ
     */
    void setContentLength(long length);

    /**
     * レスポンスボディにエンティティを書き込みます。
     * @author snowgoose
     */
    public static interface ResponseWriter {
        void write(InputStream in);
        void write(String in);
        /**
         * レスポンスボディにエンティティを書き込みます。
         * @param responseBody {@link OutputStream}
         */
        void writeInto(OutputStream responseBody);
    }

}
