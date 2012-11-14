package org.analogweb.core.direction;

import java.io.IOException;

import javax.servlet.ServletException;

import org.analogweb.Direction;
import org.analogweb.RequestContext;

public abstract class ContextSpecifiedDirection<T extends RequestContext> implements Direction {

    @Override
    @SuppressWarnings("unchecked")
    public void render(RequestContext context) throws IOException, ServletException {
        renderInternal((T) context);
    }

    protected abstract void renderInternal(T context) throws IOException, ServletException;

}
