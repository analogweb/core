package org.analogweb;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * {@link Direction}に任意の操作を付与可能なハンドラです。<br/>
 * 通常、このハンドラは{@link Direction#render(RequestContext)}を実行するのみです。
 * @author snowgoose
 */
public interface DirectionHandler extends Module {

    /**
     * {@link Direction}を評価します。
     * @param result 評価する対象の{@link Direction}
     * @param context {@link RequestContext}
     * @param attributes {@link RequestAttributes}
     * @throws IOException {@link Direction}の評価時にI/Oエラーが発生した場合。
     * @throws ServletException {@link Direction}の評価時に任意の例外が発生した場合。
     */
    void handleResult(Direction result, RequestContext context, RequestAttributes attributes)
            throws IOException, ServletException;

}
