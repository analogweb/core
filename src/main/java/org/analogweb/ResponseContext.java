package org.analogweb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

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
     * レスポンスボディに書き込むエンティティを設定します。
     * @author snowgoose
     */
    public static interface ResponseWriter {

        /**
         * レスポンスボディに書き込む対象のエンティティ
         * である{@link InputStream}を設定します。
         * @param entity {@link InputStream}
         */
        void writeEntity(InputStream entity);

        /**
         * レスポンスボディに書き込む対象のエンティティ
         * である{@link String}を設定します。<br/>
         * 文字コードは{@link Charset#defaultCharset()}
         * が使用されます。
         * @param entity {@link String}
         */
        void writeEntity(String entity);

        /**
         * レスポンスボディに書き込む対象のエンティティ
         * である{@link String}を設定します。<br/>
         * @param entity {@link String}
         * @param charset {@link Charset}
         */
        void writeEntity(String entity, Charset charset);

        /**
         * レスポンスボディに書き込む対象のエンティティ
         * である{@link ResponseEntity}を設定します。
         * @param entity {@link ResponseEntity}
         */
        void writeEntity(ResponseEntity entity);

        /**
         * レスポンスボディに書き込む対象のエンティティ
         * を取得します。
         * @return {@link ResponseEntity}
         */
        ResponseEntity getEntity();
    }

    /**
     * レスポンスボディにエンティティを書き込みます。
     * 任意の入力をレスポンスボディに反映するコールバック
     * として使用できます。
     * @author snowgoose
     */
    public static interface ResponseEntity {

        /**
         * レスポンスボディにエンティティを書き込みます。
         * @param responseBody {@link OutputStream}
         * @throws {@link IOException}
         */
        void writeInto(OutputStream responseBody) throws IOException;

        long getContentLength();
    }
}
