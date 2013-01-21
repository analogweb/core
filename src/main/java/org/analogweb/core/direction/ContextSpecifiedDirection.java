package org.analogweb.core.direction;

import java.io.IOException;

import org.analogweb.Direction;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.exception.WebApplicationException;

public abstract class ContextSpecifiedDirection<T extends RequestContext> implements Direction {

    @Override
    @SuppressWarnings("unchecked")
    public void render(RequestContext context,ResponseContext response) throws IOException, WebApplicationException {
        renderInternal((T) context,response);
    }

    protected abstract void renderInternal(T context,ResponseContext response) throws IOException, WebApplicationException;

}
