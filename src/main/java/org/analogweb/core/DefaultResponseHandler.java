package org.analogweb.core;

import java.io.IOException;

import org.analogweb.ExceptionHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.Renderable;
import org.analogweb.RenderableHolder;
import org.analogweb.RenderableResolver;
import org.analogweb.Response;
import org.analogweb.ResponseFormatter;
import org.analogweb.ResponseFormatterAware;
import org.analogweb.ResponseFormatterFinder;
import org.analogweb.ResponseHandler;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.WebApplicationException;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class DefaultResponseHandler implements ResponseHandler {

	private static final Log log = Logs.getLog(DefaultResponseHandler.class);

	@Override
	public Response handleResult(Object result, InvocationMetadata metadata,
			RenderableResolver renderableResolver, RequestContext context,
			ResponseContext response, ExceptionHandler exceptionHandler,
			ResponseFormatterFinder finder) throws IOException,
			WebApplicationException {
		Renderable resolved = renderableResolver.resolve(result, metadata,
				context, response);
		log.log(Markers.LIFECYCLE, "DL000008", result, result);
		ResponseFormatter resultFormatter = null;
		if (resolved instanceof RenderableHolder) {
			Renderable renderable = ((RenderableHolder) resolved)
					.getRenderable();
			if (renderable != null) {
				resultFormatter = finder.findResponseFormatter(renderable
						.getClass());
			}
		} else {
			resultFormatter = finder.findResponseFormatter(resolved.getClass());
		}
		if (resultFormatter != null) {
			log.log(Markers.LIFECYCLE, "DL000010", result, resultFormatter);
		} else {
			log.log(Markers.LIFECYCLE, "DL000011", result);
		}
		return handleResultInternal(resolved, metadata, renderableResolver,
				resultFormatter, context, response, exceptionHandler, finder);
	}

	protected Response handleResultInternal(Renderable result,
			InvocationMetadata metadata, RenderableResolver renderableResolver,
			ResponseFormatter resultFormatter, RequestContext context,
			ResponseContext response, ExceptionHandler exceptionHandler,
			ResponseFormatterFinder finder) throws IOException,
			WebApplicationException {
		try {
			if (result instanceof ResponseFormatterAware<?>) {
				((ResponseFormatterAware<?>) result).attach(resultFormatter);
			} else if (result instanceof RenderableHolder) {
				Renderable renderable = ((RenderableHolder) result)
						.getRenderable();
				if (renderable instanceof ResponseFormatterAware<?>) {
					((ResponseFormatterAware<?>) renderable)
							.attach(resultFormatter);
				}
			}
			return result.render(context, response);
		} catch (Exception e) {
			return handleResult(exceptionHandler.handleException(e), metadata,
					renderableResolver, context, response, exceptionHandler,
					finder);
		}
	}
}
