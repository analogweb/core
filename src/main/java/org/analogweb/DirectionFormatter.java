package org.analogweb;

/**
 * レスポンスボディに、特定のフォーマットをレンダリング可能な{@link Direction}における、
 * フォーマット処理を定義します。
 * @author snowgoose
 */
public interface DirectionFormatter extends MultiModule {

    /**
     * 指定されたオブジェクトをフォーマットし、レスポンスに書き込みを行います。
     * @param writeTo 書き込みを行う{@link RequestContext}
     * @param charset 書き込み時に使用される文字コード
     * @param source フォーマットを行うオブジェクト
     */
    void formatAndWriteInto(RequestContext writeTo, String charset, Object source);

}
