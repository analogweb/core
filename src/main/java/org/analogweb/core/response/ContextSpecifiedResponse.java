package org.analogweb.core.response;

import java.io.IOException;

import org.analogweb.Renderable;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.Response;
import org.analogweb.WebApplicationException;

public abstract class ContextSpecifiedResponse<T extends RequestContext> implements Renderable {

    @Override
    @SuppressWarnings("unchecked")
    public Response render(RequestContext context, ResponseContext response) throws IOException,
            WebApplicationException {
        return renderInternal((T) context, response);
    }

    protected abstract Response renderInternal(T context, ResponseContext response)
            throws IOException, WebApplicationException;
}
