package org.analogweb;

import java.io.IOException;

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
    void render(RequestContext context, ResponseContext response) throws IOException,
            WebApplicationException;
}
