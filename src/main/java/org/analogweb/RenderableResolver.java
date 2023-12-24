package org.analogweb;

/**
 * Resolve {@link Renderable} from result of entry-point invocation.
 *
 * @author snowgoose
 */
public interface RenderableResolver extends Module {

    Renderable resolve(Object invocationResult, InvocationMetadata metadata, RequestContext context,
            ResponseContext responseContext);
}
