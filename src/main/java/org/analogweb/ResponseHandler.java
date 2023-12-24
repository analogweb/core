package org.analogweb;

import java.io.IOException;

/**
 * Handle {@link Renderable}.
 * Usually,this handler executes only
 * {@link Renderable#render(RequestContext, ResponseContext)}.
 * 
 * @author y2k2mt
 */
public interface ResponseHandler extends Module {

	Response handleResult(Object result, InvocationMetadata metadata,
			RenderableResolver renderableResolver, RequestContext context,
			ResponseContext response, ExceptionHandler exceptionHandler,
			ResponseFormatterFinder finder) throws IOException,
			WebApplicationException;
}
