package org.analogweb;

/**
 * Factory of {@link Invocation}.
 *
 * @author snowgoose
 */
public interface InvocationFactory extends Module {

    /**
     * Create new {@link Invocation}.
     *
     * @param instanceProvider
     *            {@link ContainerAdaptor}
     * @param metadata
     *            {@link InvocationMetadata}
     * @param request
     *            {@link RequestContext}
     * @param response
     *            {@link ResponseContext}
     * @param typeMapperContext
     *            {@link TypeMapperContext}
     * @param resolvers
     *            {@link RequestValueResolvers}
     *
     * @return Created {@link Invocation}
     */
    Invocation createInvocation(ContainerAdaptor instanceProvider, InvocationMetadata metadata, RequestContext request,
            ResponseContext response, TypeMapperContext typeMapperContext, RequestValueResolvers resolvers);
}
