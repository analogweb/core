package org.analogweb;

/**
 * Interrupt application phases.
 *
 * @author snowgoose
 */
public interface ApplicationProcessor extends MultiModule, Precedence {

    /**
     * End of process without interruption.
     */
    Object NO_INTERRUPTION = new Object();

    /**
     * Interrupt before matching requested URI. Returns expect {@link #NO_INTERRUPTION}, finish process request with
     * return value.
     *
     * @param request
     *            {@link MutableRequestContext}
     * @param path
     *            {@link RequestPath}
     *
     * @return {@link #NO_INTERRUPTION} or {@link Renderable}
     */
    Object preMatching(MutableRequestContext request, RequestPath path);

    /**
     * Interrupt before invoking {@link Invocation}. Returns expect {@link #NO_INTERRUPTION}, finish process request
     * with return value.
     *
     * @param args
     *            {@link InvocationArguments}
     * @param metadata
     *            {@link InvocationMetadata}
     * @param context
     *            {@link RequestContext}
     * @param converters
     *            {@link TypeMapperContext}
     * @param resolvers
     *            {@link RequestValueResolvers}
     *
     * @return {@link #NO_INTERRUPTION} or {@link Renderable}
     */
    Object prepareInvoke(InvocationArguments args, InvocationMetadata metadata, RequestContext context,
            TypeMapperContext converters, RequestValueResolvers resolvers);

    /**
     * Interrupt after cause exception when execute {@link Application}. Returns expect {@link #NO_INTERRUPTION}, finish
     * process request with return value.
     *
     * @param ex
     *            Caused exception when execute {@link Application}.
     * @param request
     *            {@link RequestContext}
     * @param args
     *            {@link InvocationArguments}
     * @param metadata
     *            {@link InvocationMetadata}
     *
     * @return {@link #NO_INTERRUPTION} or {@link Renderable}
     */
    Object processException(Exception ex, RequestContext request, PreparedInvocationArguments args,
            InvocationMetadata metadata);

    /**
     * Interrupt after execute {@link Application}. Returns expect {@link #NO_INTERRUPTION}, finish process request with
     * return value.
     *
     * @param invocationResult
     *            {@link Application}の実行結果
     * @param args
     *            {@link InvocationArguments}
     * @param metadata
     *            {@link InvocationMetadata}
     * @param context
     *            {@link RequestContext}
     * @param resolvers
     *            {@link RequestValueResolvers}
     */
    void postInvoke(Object invocationResult, InvocationArguments args, InvocationMetadata metadata,
            RequestContext context, RequestValueResolvers resolvers);

    /**
     * Interrupt after execute {@link Application}(include exception occurred). When
     * {@link #processException(Exception, RequestContext, PreparedInvocationArguments, InvocationMetadata)} returns
     * value, this method has no effect.
     *
     * @param request
     *            {@link RequestContext}
     * @param response
     *            {@link ResponseContext}
     * @param e
     *            {@link Exception}
     */
    void afterCompletion(RequestContext request, ResponseContext response, Exception e);
}
