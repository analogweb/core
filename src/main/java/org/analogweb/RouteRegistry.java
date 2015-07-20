package org.analogweb;

import java.util.List;

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
    InvocationMetadata findInvocationMetadata(RequestContext requestContext,
            List<InvocationMetadataFinder> finders);

    /**
     * Register {@link InvocationMetadata}.
     * @param invocationMetadata {@link InvocationMetadata}
     */
    void register(InvocationMetadata invocationMetadata);
}
