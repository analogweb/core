package org.analogweb;

/**
 * レスポンスボディに、特定のフォーマットをレンダリング可能な{@link Direction}における、
 * フォーマット処理を定義します。
 * @author snowgoose
 */
public interface DirectionFormatter extends MultiModule {

    /**
     * 指定されたオブジェクトをフォーマットし、レスポンスに書き込みを行います。
     * @param context {@link RequestContext}
     * @param writeTo 書き込みを行う{@link ResponseContext}
     * @param charset 書き込み時に使用される文字コード
     * @param source フォーマットを行うオブジェクト
     */
    void formatAndWriteInto(RequestContext context, ResponseContext writeTo, String charset,
            Object source);

}
