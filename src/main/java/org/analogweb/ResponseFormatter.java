package org.analogweb;

import java.io.OutputStream;

/**
 * レスポンスボディに、特定のフォーマットをレンダリング可能な{@link Direction}における、
 * フォーマット処理を定義します。
 * @author snowgoose
 */
public interface ResponseFormatter extends MultiModule {

    /**
     * 指定されたオブジェクトをフォーマットし、レスポンスに書き込みを行います。
     * @param context {@link RequestContext}
     * @param writeTo 書き込みを行う{@link OutputStream}
     * @param charset 書き込み時に使用される文字コード
     * @param source フォーマットを行うオブジェクト
     */
    void formatAndWriteInto(RequestContext context, OutputStream writeTo, String charset,
            Object source);

}
