package org.analogweb;

import java.util.List;

/**
 * @author snowgoose
 */
public interface InvokerFactory extends Module {

    /**
     * 新しい{@link Invoker}のインスタンスを生成します。
     * @param interceptors 生成される{@link Invocation}に適用される全ての{@link InvocationInterceptor}
     * @param processors 生成される{@link Invocation}に適用される全ての{@link InvocationProcessor}
     * @param converters {@link TypeMapperContext}
     * @param handlers {@link AttributesHandlers}
     * @return　生成された{@link Invoker}
     */
	Invoker createInvoker(List<InvocationInterceptor> interceptors,
			List<InvocationProcessor> processors, TypeMapperContext converters,
			AttributesHandlers handlers);

}
