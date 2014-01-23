package org.analogweb;

/**
 * リクエストを処理する過程における、全てのプロセスにおいて発生した例外をハンドルします。
 * @author snowgoose
 */
public interface ExceptionHandler extends Module {

    /**
     * 発生した例外をハンドルします。<br/>
     * nullではない結果を返した場合は、戻り値は{@link ResponseResolver}にて処理されます。
     * @param exception 発生した{@link Exception}
     * @throws WebApplicationException アプリケーションに例外を送出する際にラップされます。
     * @return 例外をハンドルした結果、示されるレスポンス({@link Renderable}など)
     */
    Object handleException(Exception exception) throws WebApplicationException;
}
