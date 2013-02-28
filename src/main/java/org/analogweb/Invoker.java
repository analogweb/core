package org.analogweb;

/**
 * 指定されたメタデータから{@link Invocation}を特定し、実行します。
 * @author snowgoose
 */
public interface Invoker extends Module {

    /**
     * 指定されたメタデータからエントリポイントとなるメソッド(通常は{@link Invocation})を実行します。
     * @param instance エントリポイントとなるメソッドを保持するインスタンスを表す{@link Invocation}
     * @param metadata {@link InvocationMetadata}
     * @param request {@link RequestContext}
     * @param response {@link ResponseContext}
     * @return エントリポイントとなるメソッド(通常は{@link Invocation})の実行結果
     */
	Object invoke(Invocation instance, InvocationMetadata metadata,
			RequestContext request, ResponseContext response);

}
