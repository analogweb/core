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
     * {@link Direction}を評価します。指定された{@link Direction}が{@link DirectionFormatterAware}
     * である場合は、パラメータの{@link DirectionFormatter}が適用されます。
     * @param result 評価する対象の{@link Direction}
     * @param resultFormatter 評価する対象の{@link Direction}をフォーマットする{@link DirectionFormatter}
     * @param context {@link RequestContext}
     * @param attributes {@link RequestAttributes}
     * @throws IOException {@link Direction}の評価時にI/Oエラーが発生した場合。
     * @throws ServletException {@link Direction}の評価時に任意の例外が発生した場合。
     */
    void handleResult(Direction result, DirectionFormatter resultFormatter, RequestContext context,
            RequestAttributes attributes) throws IOException, ServletException;

}
