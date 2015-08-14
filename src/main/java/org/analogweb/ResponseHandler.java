package org.analogweb;

import java.io.IOException;

/**
 * Handle {@link Renderable}.<br/>
 * Usually,this handler executes only {@link Renderable#render(RequestContext, ResponseContext)}.
 * @author snowgoose
 */
public interface ResponseHandler extends Module {

    Response handleResult(Renderable result, ResponseFormatter resultFormatter,
            RequestContext context, ResponseContext response) throws IOException,
            WebApplicationException;
}
