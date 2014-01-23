package org.analogweb;

import org.analogweb.ResponseContext.ResponseEntity;

/**
 * レスポンスボディに、特定のフォーマットをレンダリング可能な{@link Renderable}における、
 * フォーマット処理を定義します。
 * @author snowgoose
 */
public interface ResponseFormatter extends MultiModule {

    /**
     * 指定されたオブジェクトをフォーマットし、レスポンスに書き込みを行います。
     * @param request {@link RequestContext}
     * @param response 書き込みを行う{@link ResponseContext}
     * @param charset 書き込み時に使用される文字コード
     * @param source フォーマットを行うオブジェクト
     * @return {@link ResponseEntity}
     */
    ResponseEntity formatAndWriteInto(RequestContext request, ResponseContext response,
            String charset, Object source);
}
