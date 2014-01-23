package org.analogweb;

import java.io.IOException;

/**
 * Handle {@link Renderable}.<br/>
 * Usually,this handler executes only {@link Renderable#render(RequestContext, ResponseContext)}.
 * @author snowgoose
 */
public interface ResponseHandler extends Module {

    /**
     * {@link Renderable}を評価します。指定された{@link Renderable}が{@link ResponseFormatterAware}
     * である場合は、パラメータの{@link ResponseFormatter}が適用されます。
     * @param result 評価する対象の{@link Renderable}
     * @param resultFormatter 評価する対象の{@link Renderable}をフォーマットする{@link ResponseFormatter}
     * @param context {@link RequestContext}
     * @param response {@link ResponseContext}
     * @throws IOException {@link Renderable}の評価時にI/Oエラーが発生した場合。
     * @throws WebApplicationException {@link Renderable}の評価時に任意の例外が発生した場合。
     */
    void handleResult(Renderable result, ResponseFormatter resultFormatter, RequestContext context,
            ResponseContext response) throws IOException, WebApplicationException;
}
