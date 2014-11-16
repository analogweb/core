package org.analogweb;

/**
 * Registry of {@link InvocationMetadata}.
 * @author snowgoose
 */
public interface RouteRegistry extends Disposable {

    /**
    * Find matched {@link InvocationMetadata}.<br/>
    * @param requestContext {@link RequestContext}
    * @return {@link InvocationMetadata}
     */
    InvocationMetadata findInvocationMetadata(RequestContext requestContext);

    /**
     * Register {@link InvocationMetadata}.
     * @param invocationMetadata {@link InvocationMetadata}
     */
    void register(InvocationMetadata invocationMetadata);
}
