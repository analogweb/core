package org.analogweb.core.response;

import java.io.IOException;

import org.analogweb.Response;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.WebApplicationException;

public abstract class ContextSpecifiedResponse<T extends RequestContext> implements Response {

    @Override
    @SuppressWarnings("unchecked")
    public void render(RequestContext context,ResponseContext response) throws IOException, WebApplicationException {
        renderInternal((T) context,response);
    }

    protected abstract void renderInternal(T context,ResponseContext response) throws IOException, WebApplicationException;

}