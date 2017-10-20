package org.analogweb;

/**
 * Formatter for {@link Renderable}.
 * 
 * @author snowgoose
 */
public interface ResponseFormatter extends MultiModule {

	ResponseEntity formatAndWriteInto(RequestContext request,
			ResponseContext response, String charset, Object source);
}
