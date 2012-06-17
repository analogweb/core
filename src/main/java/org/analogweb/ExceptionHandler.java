package org.analogweb;

import javax.servlet.ServletException;

/**
 * リクエストを処理する過程における、全てのプロセスにおいて発生した例外をハンドルします。
 * @author snowgoose
 */
public interface ExceptionHandler extends Module {

    /**
     * 発生した例外をハンドルします。<br/>
     * nullではない結果を返した場合は、戻り値は{@link DirectionResolver}にて処理されます。
     * @param exception 発生した{@link Exception}
     * @throws ServletException フィルタに例外を送出する際、{@link ServletException}にラップされます。
     * @return 例外をハンドルした結果、示されるレスポンス({@link Direction}など)
     */
    Object handleException(Exception exception) throws ServletException;

}
