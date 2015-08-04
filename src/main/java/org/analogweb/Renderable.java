package org.analogweb;

import java.io.IOException;

import org.analogweb.ResponseContext.Response;

/**
 * A renderable response.
 * @author snowgoose
 */
public interface Renderable {

    /**
     * Render invocation result to response.
     * @param context {@link RequestContext}
     * @param response {@link ResponseContext}
     * @throws IOException I/O error.
     * @throws WebApplicationException other exception.
     */
    Response render(RequestContext context, ResponseContext response) throws IOException,
            WebApplicationException;
}
