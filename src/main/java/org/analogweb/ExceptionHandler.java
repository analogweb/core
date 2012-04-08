package org.analogweb;

import javax.servlet.ServletException;

/**
 * リクエストを処理する過程における、全てのプロセスにおいて発生した例外をハンドルします。
 * @author snowgoose
 */
public interface ExceptionHandler extends Module {

    /**
     * 発生した例外をハンドルします。
     * @param exception 発生した{@link Exception}
     * @throws ServletException フィルタに例外を送出する際、{@link ServletException}にラップされます。
     */
    void handleException(Exception exception) throws ServletException;

}
