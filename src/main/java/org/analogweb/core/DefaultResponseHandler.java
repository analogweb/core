package org.analogweb.core;

import java.io.IOException;

import org.analogweb.Renderable;
import org.analogweb.RenderableHolder;
import org.analogweb.Response;
import org.analogweb.ResponseFormatter;
import org.analogweb.ResponseFormatterAware;
import org.analogweb.ResponseHandler;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.WebApplicationException;

/**
 * @author snowgoose
 */
public class DefaultResponseHandler implements ResponseHandler {

    public Response handleResult(Renderable result, ResponseFormatter resultFormatter,
            RequestContext context, ResponseContext response) throws IOException,
            WebApplicationException {
        try {
            if (result instanceof ResponseFormatterAware<?>) {
                ((ResponseFormatterAware<?>) result).attach(resultFormatter);
            } else if (result instanceof RenderableHolder) {
                Renderable renderable = ((RenderableHolder) result).getRenderable();
                if (renderable instanceof ResponseFormatterAware<?>) {
                    ((ResponseFormatterAware<?>) renderable).attach(resultFormatter);
                }
            }
            return result.render(context, response);
        } catch (Exception e) {
            throw new ResponseEvaluationException(e, result);
        }
    }
}
