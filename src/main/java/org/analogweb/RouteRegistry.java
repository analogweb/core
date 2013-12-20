package org.analogweb;

/**
 * Registry of {@link InvocationMetadata}.
 * @author snowgoose
 */
public interface RouteRegistry extends Disposable {

    /**
    * Find matched {@link InvocationMetadata}.<br/>
    * @param requestPath {@link RequestPathMetadata}
    * @return {@link InvocationMetadata}
     */
    InvocationMetadata findInvocationMetadata(RequestPath requestPath);

    /**
     * Register {@link InvocationMetadata}.
     * @param invocationMetadata {@link InvocationMetadata}
     */
    void register(
            InvocationMetadata invocationMetadata);

}
