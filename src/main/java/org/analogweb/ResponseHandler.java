package org.analogweb;

import java.io.IOException;

/**
 * Handle {@link Renderable}.<br/>
 * Usually,this handler executes only
 * {@link Renderable#render(RequestContext, ResponseContext)}.
 * 
 * @author snowgoose
 */
public interface ResponseHandler extends Module {

	Response handleResult(Object result, InvocationMetadata metadata,
			RenderableResolver renderableResolver, RequestContext context,
			ResponseContext response, ExceptionHandler exceptionHandler,
			ResponseFormatterFinder finder) throws IOException,
			WebApplicationException;
}
