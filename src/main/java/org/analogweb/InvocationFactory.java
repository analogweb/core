package org.analogweb;

/**
 * {@link Invocation}を生成するファクトリです。
 * 通常、{@link Invocation}はリクエスト毎にこのファクトリを
 * 通じてインスタンスが生成されます。
 * @author snowgoose
 */
public interface InvocationFactory extends Module {

    /**
     * 新しい{@link Invocation}のインスタンスを生成します。
     * @param instanceProvider リクエストに一致する実行対象のインスタンスプロバイダ
     * @param metadata {@link InvocationMetadata}
     * @param request {@link RequestContext}
     * @param response {@link ResponseContext}
     * @param typeMapperContext {@link TypeMapperContext}
     * @param handlers {@link AttributesHandlers}
     * @return　生成された{@link Invocation}
     */
    Invocation createInvocation(ContainerAdaptor instanceProvider, InvocationMetadata metadata,
            RequestContext request, ResponseContext response,TypeMapperContext typeMapperContext,
            AttributesHandlers handlers);

}
